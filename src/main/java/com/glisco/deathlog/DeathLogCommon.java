package com.glisco.deathlog;

import com.glisco.deathlog.death_info.DeathInfoPropertySerializer;
import com.glisco.deathlog.death_info.SpecialPropertyProvider;
import com.glisco.deathlog.death_info.properties.TrinketComponentProperty;
import com.glisco.deathlog.storage.DeathLogStorage;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeathLogCommon implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("deathlog");

    private static DeathLogStorage currentStorage = null;
    private static boolean usePermissions;

    @Override
    public void onInitialize() {
        if (FabricLoader.getInstance().isModLoaded("trinkets")) {
            SpecialPropertyProvider.register(TrinketComponentProperty::apply);
            DeathInfoPropertySerializer.register(TrinketComponentProperty.Type.INSTANCE.getId(), TrinketComponentProperty.Type.INSTANCE);
        }

        usePermissions = FabricLoader.getInstance().isModLoaded("fabric-permissions-api-v0");

    }

    public static boolean usePermissions() {
        return usePermissions;
    }

    public static void setStorage(DeathLogStorage storage) {
        if (DeathLogCommon.currentStorage != null) throw new IllegalStateException("Storage has already been set!");
        if (storage == null) throw new IllegalArgumentException("Storage cannot be null!");
        DeathLogCommon.currentStorage = storage;
    }

    public static DeathLogStorage getStorage() {
        return currentStorage;
    }
}
