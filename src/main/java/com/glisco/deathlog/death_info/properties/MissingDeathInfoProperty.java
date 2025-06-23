package com.glisco.deathlog.death_info.properties;

import com.glisco.deathlog.death_info.DeathInfoProperty;
import com.glisco.deathlog.death_info.DeathInfoPropertyType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;

public class MissingDeathInfoProperty implements DeathInfoProperty {

    private final Type type;
    private final ReadView view;

    public MissingDeathInfoProperty(Type type, ReadView view) {
        this.type = type;
        this.view = view;
    }

    @Override
    public DeathInfoPropertyType<?> getType() {
        return this.type;
    }

    @Override
    public Text formatted() {
        return Text.empty();
    }

    @Override
    public void writeNbt(WriteView view) {
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
        public MissingDeathInfoProperty readFromNbt(ReadView view) {
            return new MissingDeathInfoProperty(this, view);
        }
    }

}
