package com.glisco.deathlog.storage;

import com.glisco.deathlog.DeathLogCommon;
import com.glisco.deathlog.client.DeathInfo;
import com.google.common.collect.ImmutableList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class BaseDeathLogStorage implements DeathLogStorage {

    private static final int FORMAT_REVISION = 2;
    public static final Logger LOGGER = LogManager.getLogger();

    protected CompletableFuture<List<DeathInfo>> load(File file, HolderLookup.Provider wrapperLookup) {
        final var future = new CompletableFuture<List<DeathInfo>>();
        Util.ioPool().execute(() -> {

            CompoundTag deathNbt;

            if (file.exists()) {
                try {
                    deathNbt = NbtIo.read(file.toPath());

                    if (deathNbt.getIntOr("FormatRevision", 0) != FORMAT_REVISION) {

                        LOGGER.error("Incompatible DeathLog database format detected. Database not loaded and further disk operations disabled");

                        future.complete(null);
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    LOGGER.error("Failed to load DeathLog database, further disk operations have been disabled");

                    future.completeExceptionally(e);
                    return;
                }
            } else {
                deathNbt = new CompoundTag();
            }

            final var list = new ArrayList<DeathInfo>();
            try (var reporter = new ProblemReporter.ScopedCollector(() -> "deathlog:DeathInfos", DeathLogCommon.LOGGER)) {
                ValueInput readView = TagValueInput.create(reporter, wrapperLookup, deathNbt);
                ValueInput.ValueInputList deaths = readView.childrenListOrEmpty("Deaths");
                for (ValueInput death : deaths) {
                    list.add(DeathInfo.readFromNbt(death));
                }
            }
            future.complete(list);
        });

        return future;
    }

    protected void save(File file, List<DeathInfo> listIn, HolderLookup.Provider wrapperLookup) {
        final var list = ImmutableList.copyOf(listIn);
        Util.ioPool().execute(() -> {
            try (var reporter = new ProblemReporter.ScopedCollector(() -> "deathlog:DeathInfos", DeathLogCommon.LOGGER)) {
                TagValueOutput writeView = TagValueOutput.createWithContext(reporter, wrapperLookup);

                ValueOutput.ValueOutputList deaths = writeView.childrenList("Deaths");

                list.forEach(deathInfo -> deathInfo.writeNbt(deaths.addChild()));

                writeView.putInt("FormatRevision", FORMAT_REVISION);

                try {
                    NbtIo.write(writeView.buildResult(), file.toPath());
                } catch (IOException e) {
                    LOGGER.error("Failed to save DeathLog database", e);
                }
            }
        });
    }

}
