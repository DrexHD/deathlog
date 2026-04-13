package com.glisco.deathlog.death_info;

import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.chat.Component;

public interface DeathInfoProperty {

    default Component getName() {
        return DeathInfoPropertyType.decorateName(getType().getName());
    }

    DeathInfoPropertyType<?> getType();

    Component formatted();

    void writeNbt(ValueOutput view);

    String toSearchableString();
}
