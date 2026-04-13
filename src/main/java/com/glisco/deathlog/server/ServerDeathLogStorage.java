package com.glisco.deathlog.server;

import com.glisco.deathlog.client.DeathInfo;
import com.glisco.deathlog.death_info.SpecialPropertyProvider;
import com.glisco.deathlog.death_info.properties.*;
import com.glisco.deathlog.storage.BaseDeathLogStorage;
import com.glisco.deathlog.storage.DeathInfoCreatedCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ServerDeathLogStorage extends BaseDeathLogStorage {

    private final Map<UUID, List<DeathInfo>> deathInfos;
    private final Path deathLogDir;

    public ServerDeathLogStorage(HolderLookup.Provider wrapperLookup) {
        this.deathInfos = new HashMap<>();
        this.deathLogDir = FabricLoader.getInstance().getGameDir().resolve("deaths").toAbsolutePath();

        if (!Files.exists(deathLogDir) && !deathLogDir.toFile().mkdir()) {

            LOGGER.error("Failed to create DeathLog storage directory, further disk operations have been disabled");
            return;
        }

        try {
            Files.list(deathLogDir).forEach(path -> {

                if (!Files.exists(path)) return;
                if (path.endsWith(".dat")) return;


                try {
                    UUID uuid = UUID.fromString(FilenameUtils.getBaseName(path.toString()));
                    deathInfos.put(uuid, load(path.toFile(), wrapperLookup).join());
                } catch (IllegalArgumentException e) {
                    LOGGER.error("Failed to parse UUID from filename '{}'", FilenameUtils.removeExtension(path.toString()), e);
                }

            });
        } catch (IOException | IllegalArgumentException e) {
            LOGGER.error("Failed to load DeathLog database, further disk operations have been disabled", e);
        }
    }

    @Override
    public List<DeathInfo> getDeathInfoList(UUID profile) {
        return deathInfos.getOrDefault(profile, new ArrayList<>());
    }

    @Override
    public void delete(DeathInfo info, UUID profile, HolderLookup.Provider wrapperLookup) {
        deathInfos.get(profile).remove(info);
        save(deathLogDir.resolve(profile.toString() + ".dat").toFile(), deathInfos.get(profile), wrapperLookup);
    }

    @Override
    public void store(Component deathMessage, Player player, HolderLookup.Provider wrapperLookup) {
        final DeathInfo deathInfo = new DeathInfo();

        deathInfo.setProperty(DeathInfo.INVENTORY_KEY, new InventoryProperty(player.getInventory()));

        deathInfo.setProperty(DeathInfo.COORDINATES_KEY, new CoordinatesProperty(player.blockPosition()));
        deathInfo.setProperty(DeathInfo.DIMENSION_KEY, new StringProperty("deathlog.deathinfoproperty.dimension", player.level().dimension().identifier().toString()));
        deathInfo.setProperty(DeathInfo.LOCATION_KEY, new LocationProperty("Server", true));
        deathInfo.setProperty(DeathInfo.SCORE_KEY, new ScoreProperty(player.getScore(), player.experienceLevel, player.experienceProgress, player.totalExperience));
        deathInfo.setProperty(DeathInfo.DEATH_MESSAGE_KEY, new StringProperty("deathlog.deathinfoproperty.death_message", deathMessage.getString()));
        deathInfo.setProperty(DeathInfo.TIME_OF_DEATH_KEY, new StringProperty("deathlog.deathinfoproperty.time_of_death", new Date().toString()));

        SpecialPropertyProvider.apply(deathInfo, player);
        DeathInfoCreatedCallback.EVENT.invoker().event(deathInfo);

        deathInfos.computeIfAbsent(player.getUUID(), uuid -> new ArrayList<>()).add(deathInfo);
        save(deathLogDir.resolve(player.getUUID().toString() + ".dat").toFile(), deathInfos.get(player.getUUID()), wrapperLookup);
    }

    @Override
    public void restore(int index, @Nullable UUID profile, HolderLookup.Provider wrapperLookup) {
        //NO-OP
    }
}
