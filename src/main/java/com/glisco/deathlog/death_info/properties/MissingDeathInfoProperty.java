package com.glisco.deathlog.death_info.properties;

import com.glisco.deathlog.death_info.DeathInfoProperty;
import com.glisco.deathlog.death_info.DeathInfoPropertyType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.chat.Component;

public class MissingDeathInfoProperty implements DeathInfoProperty {

    private final Type type;
    private final ValueInput view;

    public MissingDeathInfoProperty(Type type, ValueInput view) {
        this.type = type;
        this.view = view;
    }

    @Override
    public DeathInfoPropertyType<?> getType() {
        return this.type;
    }

    @Override
    public Component formatted() {
        return Component.empty();
    }

    @Override
    public void writeNbt(ValueOutput view) {
        // TODO
//        view.put(NbtCompound.CODEC, this.view.read(NbtCompound.CODEC));
    }

    @Override
    public String toSearchableString() {
        return null;
    }

    public static class Type extends DeathInfoPropertyType<MissingDeathInfoProperty> {

        public Type(String id) {
            super(null, id);
        }

        @Override
        public boolean displayedInInfoView() {
            return false;
        }

        @Override
        public MissingDeathInfoProperty readFromNbt(ValueInput view) {
            return new MissingDeathInfoProperty(this, view);
        }
    }

}
