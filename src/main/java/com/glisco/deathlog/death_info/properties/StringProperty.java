package com.glisco.deathlog.death_info.properties;

import com.glisco.deathlog.death_info.DeathInfoProperty;
import com.glisco.deathlog.death_info.DeathInfoPropertyType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.chat.Component;

public class StringProperty implements DeathInfoProperty {

    private final String translationKey;
    private final String data;

    public StringProperty(String translationKey, String data) {
        this.translationKey = translationKey;
        this.data = data;
    }

    @Override
    public DeathInfoPropertyType<?> getType() {
        return Type.INSTANCE;
    }

    @Override
    public Component formatted() {
        return Component.literal(data);
    }

    @Override
    public void writeNbt(ValueOutput view) {
        view.putString("TranslationKey", translationKey);
        view.putString("Data", data);
    }

    @Override
    public String toSearchableString() {
        return data;
    }

    @Override
    public Component getName() {
        return DeathInfoPropertyType.decorateName(Component.translatable(translationKey));
    }

    public static class Type extends DeathInfoPropertyType<StringProperty> {

        public static final Type INSTANCE = new Type();

        private Type() {super("deathlog.deathinfoproperty.string", "string");}

        @Override
        public boolean displayedInInfoView() {
            return true;
        }

        @Override
        public StringProperty readFromNbt(ValueInput view) {
            String key = view.getStringOr("TranslationKey", "");
            String data = view.getStringOr("Data", "");

            return new StringProperty(key, data);
        }
    }
}
