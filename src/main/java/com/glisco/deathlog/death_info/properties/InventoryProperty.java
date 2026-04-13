package com.glisco.deathlog.death_info.properties;

import com.glisco.deathlog.death_info.DeathInfoPropertyType;
import com.glisco.deathlog.death_info.RestorableDeathInfoProperty;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.core.NonNullList;

public class InventoryProperty implements RestorableDeathInfoProperty {

    private final NonNullList<ItemStack> playerItems;
    private final NonNullList<ItemStack> playerArmor;

    public InventoryProperty(NonNullList<ItemStack> playerItems, NonNullList<ItemStack> playerArmor) {
        this.playerItems = playerItems;
        this.playerArmor = playerArmor;
    }

    public InventoryProperty(Inventory playerInventory) {
        this.playerItems = NonNullList.withSize(37, ItemStack.EMPTY);
        this.playerArmor = NonNullList.withSize(4, ItemStack.EMPTY);

        var player = playerInventory.player;

        for (EquipmentSlot value : EquipmentSlot.values()) {
            if (value.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                var stack = player.getItemBySlot(value);
                playerArmor.set(value.getIndex(), stack.copy());
            }
        }

        copy(playerInventory.getNonEquipmentItems(), playerItems);

        playerItems.set(Inventory.INVENTORY_SIZE, player.getOffhandItem().copy());
    }

    @Override
    public DeathInfoPropertyType<?> getType() {
        return Type.INSTANCE;
    }

    @Override
    public Component formatted() {
        return null;
    }

    @Override
    public void writeNbt(ValueOutput view) {
        ValueOutput.TypedOutputList<ItemStack> armor = view.list("Armor", ItemStack.OPTIONAL_CODEC);
        playerArmor.forEach(armor::add);
        ValueOutput.TypedOutputList<ItemStack> items = view.list("Items", ItemStack.OPTIONAL_CODEC);
        playerItems.forEach(items::add);
    }

    @Override
    public String toSearchableString() {
        StringBuilder builder = new StringBuilder();

        playerItems.forEach(stack -> builder.append(stack.getHoverName().getString()));
        playerArmor.forEach(stack -> builder.append(stack.getHoverName().getString()));

        return builder.toString();
    }

    @Override
    public void restore(ServerPlayer player) {
        final var inventory = player.getInventory();
        inventory.clearContent();


        for (EquipmentSlot value : EquipmentSlot.values()) {
            if (value.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                player.setItemSlot(value, playerArmor.get(value.getIndex()));
            }
        }

        copy(playerItems, inventory.getNonEquipmentItems(), Inventory.INVENTORY_SIZE);

        player.setItemSlot(EquipmentSlot.OFFHAND, playerItems.get(Inventory.INVENTORY_SIZE));
    }

    public NonNullList<ItemStack> getPlayerArmor() {
        return playerArmor;
    }

    public NonNullList<ItemStack> getPlayerItems() {
        return playerItems;
    }

    private static void copy(NonNullList<ItemStack> list, NonNullList<ItemStack> other) {
        copy(list, other, list.size());
    }

    private static void copy(NonNullList<ItemStack> list, NonNullList<ItemStack> other, int maxItems) {
        for (int i = 0; i < maxItems; i++) other.set(i, list.get(i).copy());
    }

    public static class Type extends DeathInfoPropertyType<InventoryProperty> {

        public static final Type INSTANCE = new Type();

        private Type() {
            super("deathlog.deathinfoproperty.inventory", "inventory");
        }

        @Override
        public boolean displayedInInfoView() {
            return false;
        }

        @Override
        public InventoryProperty readFromNbt(ValueInput view) {
            final var armorList = NonNullList.withSize(4, ItemStack.EMPTY);
            int i = 0;
            for (ItemStack armor : view.listOrEmpty("Armor", ItemStack.OPTIONAL_CODEC)) {
                armorList.set(i, armor);
                i++;
            }

            final var itemList = NonNullList.withSize(37, ItemStack.EMPTY);
            i = 0;
            for (ItemStack item : view.listOrEmpty("Items", ItemStack.OPTIONAL_CODEC)) {
                itemList.set(i, item);
                i++;
            }

            return new InventoryProperty(itemList, armorList);
        }
    }

}
