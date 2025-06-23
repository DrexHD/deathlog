package com.glisco.deathlog.death_info.properties;

import com.glisco.deathlog.death_info.DeathInfoProperty;
import com.glisco.deathlog.death_info.DeathInfoPropertyType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;

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
    public Text formatted() {
        return Text.translatable(
                "deathlog.deathinfoproperty.location.value", location,
                multiplayer
                        ? Text.translatable("deathlog.deathinfoproperty.location.multiplayer")
                        : Text.translatable("deathlog.deathinfoproperty.location.singleplayer")
        );
    }

    @Override
    public void writeNbt(WriteView view) {
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
        public LocationProperty readFromNbt(ReadView view) {
            String location = view.getString("Location", "");
            boolean multiplayer = view.getBoolean("Multiplayer", false);
            return new LocationProperty(location, multiplayer);
        }
    }
}
