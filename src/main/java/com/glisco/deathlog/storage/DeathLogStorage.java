package com.glisco.deathlog.storage;

import com.glisco.deathlog.client.DeathInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public interface DeathLogStorage {

    List<DeathInfo> getDeathInfoList(@Nullable UUID profile);

    void delete(DeathInfo info, @Nullable UUID profile, RegistryWrapper.WrapperLookup wrapperLookup);

    void store(Text deathMessage, PlayerEntity player, RegistryWrapper.WrapperLookup wrapperLookup);

    void restore(int index, @Nullable UUID profile, RegistryWrapper.WrapperLookup wrapperLookup);

    boolean isErrored();

    String getErrorCondition();
}
