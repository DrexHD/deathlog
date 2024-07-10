package com.glisco.deathlog.death_info;

import com.glisco.deathlog.death_info.properties.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
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

    public static NbtCompound save(DeathInfoProperty property, String identifier, RegistryWrapper.WrapperLookup wrapperLookup) {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("Type", property.getType().getId());
        nbt.putString("Identifier", identifier);
        property.writeNbt(nbt, wrapperLookup);
        return nbt;
    }

    public static Pair<DeathInfoProperty, String> load(NbtCompound propertyNbt, RegistryWrapper.WrapperLookup wrapperLookup) {
        String type = propertyNbt.getString("Type");
        String identifier = propertyNbt.getString("Identifier");

        final var typeInstance = TYPES.containsKey(type) ? TYPES.get(type) : new MissingDeathInfoProperty.Type(identifier);
        return new Pair<>(typeInstance.readFromNbt(propertyNbt, wrapperLookup), identifier);
    }

}
