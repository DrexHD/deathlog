package com.glisco.deathlog.death_info.properties;

import com.glisco.deathlog.death_info.DeathInfoProperty;
import com.glisco.deathlog.death_info.DeathInfoPropertyType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;

public class CoordinatesProperty implements DeathInfoProperty {

    private final BlockPos coordinates;

    public CoordinatesProperty(BlockPos coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public DeathInfoPropertyType<?> getType() {
        return Type.INSTANCE;
    }

    @Override
    public Component formatted() {
        return Component.literal("%d %d %d".formatted(coordinates.getX(), coordinates.getY(), coordinates.getZ()));
    }

    @Override
    public void writeNbt(ValueOutput view) {
        view.putLong("Coordinates", coordinates.asLong());
    }

    @Override
    public String toSearchableString() {
        return coordinates.getX() + " " + coordinates.getY() + " " + coordinates.getZ();
    }

    public static class Type extends DeathInfoPropertyType<CoordinatesProperty> {

        public static final Type INSTANCE = new Type();

        private Type() {
            super("deathlog.deathinfoproperty.coordinates", "coordinates");
        }

        @Override
        public boolean displayedInInfoView() {
            return true;
        }

        @Override
        public CoordinatesProperty readFromNbt(ValueInput view) {
            BlockPos location = BlockPos.of(view.getLongOr("Coordinates", 0));
            return new CoordinatesProperty(location);
        }
    }
}
