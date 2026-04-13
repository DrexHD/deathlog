package com.glisco.deathlog.death_info;

import com.glisco.deathlog.death_info.properties.*;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.util.Tuple;

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

    public static void save(DeathInfoProperty property, String identifier, ValueOutput view) {
        view.putString("Type", property.getType().getId());
        view.putString("Identifier", identifier);
        property.writeNbt(view);
    }

    public static Tuple<DeathInfoProperty, String> load(ValueInput view) {
        String type = view.getStringOr("Type", "");
        String identifier = view.getStringOr("Identifier", "");

        final var typeInstance = TYPES.containsKey(type) ? TYPES.get(type) : new MissingDeathInfoProperty.Type(identifier);
        return new Tuple<>(typeInstance.readFromNbt(view), identifier);
    }

}
