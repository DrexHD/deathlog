package com.glisco.deathlog.mixin;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtTagSizeTracker;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static net.minecraft.nbt.NbtIo.readCompound;

@Mixin(NbtIo.class)
public abstract class NbtIoMixin {

    /**
     * @author Drex
     * @reason Fix <a href="https://bugs.mojang.com/browse/MC-266287">MC-266287</a>
     */
    @Overwrite
    @Nullable
    public static NbtCompound read(Path path) throws IOException {
        if (!Files.exists(path)) {
            return null;
        } else {
            InputStream inputStream = Files.newInputStream(path);

            NbtCompound var3;
            try {
                DataInputStream dataInputStream = new DataInputStream(inputStream);

                try {
                    var3 = readCompound(dataInputStream, NbtTagSizeTracker.ofUnlimitedBytes());
                } catch (Throwable var7) {
                    try {
                        dataInputStream.close();
                    } catch (Throwable var6) {
                        var7.addSuppressed(var6);
                    }

                    throw var7;
                }

                dataInputStream.close();
            } catch (Throwable var8) {
                try {
                    inputStream.close();
                } catch (Throwable var5) {
                    var8.addSuppressed(var5);
                }

                throw var8;
            }

            inputStream.close();

            return var3;
        }
    }

}
