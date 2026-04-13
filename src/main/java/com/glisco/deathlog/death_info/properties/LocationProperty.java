package com.glisco.deathlog.death_info.properties;

import com.glisco.deathlog.death_info.DeathInfoProperty;
import com.glisco.deathlog.death_info.DeathInfoPropertyType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.chat.Component;

public class LocationProperty implements DeathInfoProperty {

    private final String location;
    private final boolean multiplayer;

    public LocationProperty(String location, boolean multiplayer) {
        this.location = location;
        this.multiplayer = multiplayer;
    }

    @Override
    public DeathInfoPropertyType<?> getType() {
        return Type.INSTANCE;
    }

    @Override
    public Component formatted() {
        return Component.translatable(
                "deathlog.deathinfoproperty.location.value", location,
                multiplayer
                        ? Component.translatable("deathlog.deathinfoproperty.location.multiplayer")
                        : Component.translatable("deathlog.deathinfoproperty.location.singleplayer")
        );
    }

    @Override
    public void writeNbt(ValueOutput view) {
        view.putString("Location", location);
        view.putBoolean("Multiplayer", multiplayer);
    }

    @Override
    public String toSearchableString() {
        return location;
    }

    public static class Type extends DeathInfoPropertyType<LocationProperty> {

        public static final Type INSTANCE = new Type();

        private Type() {
            super("deathlog.deathinfoproperty.location", "location");
        }

        @Override
        public boolean displayedInInfoView() {
            return true;
        }

        @Override
        public LocationProperty readFromNbt(ValueInput view) {
            String location = view.getStringOr("Location", "");
            boolean multiplayer = view.getBooleanOr("Multiplayer", false);
            return new LocationProperty(location, multiplayer);
        }
    }
}
