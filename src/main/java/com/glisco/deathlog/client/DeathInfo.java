package com.glisco.deathlog.client;

import com.glisco.deathlog.death_info.DeathInfoProperty;
import com.glisco.deathlog.death_info.DeathInfoPropertySerializer;
import com.glisco.deathlog.death_info.RestorableDeathInfoProperty;
import com.glisco.deathlog.death_info.properties.InventoryProperty;
import com.glisco.deathlog.util.CodecUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.core.NonNullList;

import java.util.*;
import java.util.function.Consumer;

public class DeathInfo {

    public static final String COORDINATES_KEY = "coordinates";
    public static final String DIMENSION_KEY = "dimension";
    public static final String LOCATION_KEY = "location";
    public static final String SCORE_KEY = "score";
    public static final String DEATH_MESSAGE_KEY = "death_message";
    public static final String TIME_OF_DEATH_KEY = "time_of_death";
    public static final String INVENTORY_KEY = "inventory";

    private final Map<String, DeathInfoProperty> properties;

    public DeathInfo() {
        this.properties = new LinkedHashMap<>();
    }

    public static DeathInfo readFromNbt(ValueInput view) {
        final DeathInfo deathInfo = new DeathInfo();

        List<String> keys = view.read(CodecUtil.KEY_CODEC).orElse(Collections.emptyList());
        keys.forEach(key -> {
            final var parsed = DeathInfoPropertySerializer.load(view.childOrEmpty(key));
            deathInfo.setProperty(parsed.getSecond(), parsed.getFirst());
        });
        return deathInfo;
    }

    public void writeNbt(ValueOutput view) {
        properties.forEach((key, property) -> DeathInfoPropertySerializer.save(property, key, view.child(key)));
    }

    public void restore(ServerPlayer player) {
        properties.values().stream().filter(property -> property instanceof RestorableDeathInfoProperty).forEach(property -> ((RestorableDeathInfoProperty) property).restore(player));
    }

    public void setProperty(String property, DeathInfoProperty value) {
        this.properties.put(property, value);
    }

    public Optional<DeathInfoProperty> getProperty(String property) {
        return Optional.ofNullable(properties.get(property));
    }

    public boolean isPartial() {
        return getProperty(INVENTORY_KEY).isEmpty();
    }

    public Component getListName() {
        DeathInfoProperty property = getProperty(TIME_OF_DEATH_KEY).orElse(null);
        return property == null ? Component.translatable("text.deathlog.info.time_missing") : property.formatted();
    }

    public Component getTitle() {
        DeathInfoProperty property = getProperty(DEATH_MESSAGE_KEY).orElse(null);
        return property == null ? Component.translatable("text.deathlog.info.death_message_missing") : property.formatted();
    }

    public List<Component> getLeftColumnText() {
        final var texts = new ArrayList<Component>();
        iterateDisplayProperties(property -> texts.add(property.getName()));
        return texts;
    }

    public List<Component> getRightColumnText() {
        final var texts = new ArrayList<Component>();
        iterateDisplayProperties(property -> texts.add(property.formatted()));
        return texts;
    }

    public String createSearchString() {
        final StringBuilder builder = new StringBuilder();
        properties.forEach((s, property) -> builder.append(property.toSearchableString()));
        return builder.toString().toLowerCase();
    }

    private void iterateDisplayProperties(Consumer<DeathInfoProperty> callback) {
        properties.forEach((s, property) -> {
            if (!property.getType().displayedInInfoView()) return;

            callback.accept(property);
        });
    }

    public NonNullList<ItemStack> getPlayerArmor() {
        var propertyOptional = getProperty(INVENTORY_KEY);
        if (propertyOptional.isEmpty()) return NonNullList.create();
        return ((InventoryProperty) propertyOptional.get()).getPlayerArmor();
    }

    public NonNullList<ItemStack> getPlayerItems() {
        var propertyOptional = getProperty(INVENTORY_KEY);
        if (propertyOptional.isEmpty()) return NonNullList.create();
        return ((InventoryProperty) propertyOptional.get()).getPlayerItems();
    }
}
