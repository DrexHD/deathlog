package com.glisco.deathlog.death_info;

import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;

public interface DeathInfoProperty {

    default Text getName() {
        return DeathInfoPropertyType.decorateName(getType().getName());
    }

    DeathInfoPropertyType<?> getType();

    Text formatted();

    void writeNbt(WriteView view);

    String toSearchableString();
}
