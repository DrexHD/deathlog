package com.glisco.deathlog.death_info;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public abstract class DeathInfoPropertyType<P extends DeathInfoProperty> {

    private final String translationKey;
    private final String id;

    public DeathInfoPropertyType(String translationKey, String id) {
        this.translationKey = translationKey;
        this.id = id;
    }

    public MutableComponent getName() {
        return Component.translatable(translationKey);
    }

    public static Component decorateName(MutableComponent name) {
        return name.withStyle(ChatFormatting.BLUE);
    }

    public String getId() {
        return id;
    }

    public abstract boolean displayedInInfoView();

    public abstract P readFromNbt(ValueInput view);

}
