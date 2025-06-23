package com.glisco.deathlog.death_info;

import com.glisco.deathlog.death_info.properties.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Pair;

import java.util.LinkedHashMap;
import java.util.Map;

public class DeathInfoPropertySerializer {

    private static final Map<String, DeathInfoPropertyType<?>> TYPES = new LinkedHashMap<>();

    static {
        TYPES.put("inventory", InventoryProperty.Type.INSTANCE);
        TYPES.put("coordinates", CoordinatesProperty.Type.INSTANCE);
        TYPES.put("location", LocationProperty.Type.INSTANCE);
        TYPES.put("score", ScoreProperty.Type.INSTANCE);
        TYPES.put("string", StringProperty.Type.INSTANCE);
    }

    public static void register(String id, DeathInfoPropertyType<?> type) {
        TYPES.put(id, type);
    }

    public static void save(DeathInfoProperty property, String identifier, WriteView view) {
        view.putString("Type", property.getType().getId());
        view.putString("Identifier", identifier);
        property.writeNbt(view);
    }

    public static Pair<DeathInfoProperty, String> load(ReadView view) {
        String type = view.getString("Type", "");
        String identifier = view.getString("Identifier", "");

        final var typeInstance = TYPES.containsKey(type) ? TYPES.get(type) : new MissingDeathInfoProperty.Type(identifier);
        return new Pair<>(typeInstance.readFromNbt(view), identifier);
    }

}
