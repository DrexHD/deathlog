package com.glisco.deathlog.death_info;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;

public interface DeathInfoProperty {

    default Text getName() {
        return DeathInfoPropertyType.decorateName(getType().getName());
    }

    DeathInfoPropertyType<?> getType();

    Text formatted();

    void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapperLookup);

    String toSearchableString();
}
