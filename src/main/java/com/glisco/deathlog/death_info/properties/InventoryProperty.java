package com.glisco.deathlog.death_info.properties;

import com.glisco.deathlog.death_info.DeathInfoPropertyType;
import com.glisco.deathlog.death_info.RestorableDeathInfoProperty;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;

public class InventoryProperty implements RestorableDeathInfoProperty {

    private final DefaultedList<ItemStack> playerItems;
    private final DefaultedList<ItemStack> playerArmor;

    public InventoryProperty(DefaultedList<ItemStack> playerItems, DefaultedList<ItemStack> playerArmor) {
        this.playerItems = playerItems;
        this.playerArmor = playerArmor;
    }

    public InventoryProperty(PlayerInventory playerInventory) {
        this.playerItems = DefaultedList.ofSize(37, ItemStack.EMPTY);
        this.playerArmor = DefaultedList.ofSize(4, ItemStack.EMPTY);

        var player = playerInventory.player;

        for (EquipmentSlot value : EquipmentSlot.values()) {
            if (value.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                var stack = player.getEquippedStack(value);
                playerArmor.set(value.getEntitySlotId(), stack.copy());
            }
        }

        copy(playerInventory.getMainStacks(), playerItems);

        playerItems.set(PlayerInventory.MAIN_SIZE, player.getOffHandStack().copy());
    }

    @Override
    public DeathInfoPropertyType<?> getType() {
        return Type.INSTANCE;
    }

    @Override
    public Text formatted() {
        return null;
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapperLookup) {
        final NbtList armorNbt = new NbtList();
        playerArmor.forEach(stack -> armorNbt.add(toNbtAllowEmpty(wrapperLookup, stack)));
        nbt.put("Armor", armorNbt);

        final NbtList inventoryNbt = new NbtList();
        playerItems.forEach(stack -> inventoryNbt.add(toNbtAllowEmpty(wrapperLookup, stack)));
        nbt.put("Items", inventoryNbt);
    }

    private static NbtElement toNbtAllowEmpty(RegistryWrapper.WrapperLookup registries, ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return new NbtCompound();
        }
        return itemStack.toNbt(registries);
    }

    @Override
    public String toSearchableString() {
        StringBuilder builder = new StringBuilder();

        playerItems.forEach(stack -> builder.append(stack.getName().getString()));
        playerArmor.forEach(stack -> builder.append(stack.getName().getString()));

        return builder.toString();
    }

    @Override
    public void restore(ServerPlayerEntity player) {
        final var inventory = player.getInventory();
        inventory.clear();


        for (EquipmentSlot value : EquipmentSlot.values()) {
            if (value.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                player.equipStack(value, playerArmor.get(value.getEntitySlotId()));
            }
        }

        copy(playerItems, inventory.getMainStacks(), PlayerInventory.MAIN_SIZE);

        player.equipStack(EquipmentSlot.OFFHAND, playerItems.get(PlayerInventory.MAIN_SIZE));
    }

    public DefaultedList<ItemStack> getPlayerArmor() {
        return playerArmor;
    }

    public DefaultedList<ItemStack> getPlayerItems() {
        return playerItems;
    }

    private static void copy(DefaultedList<ItemStack> list, DefaultedList<ItemStack> other) {
        copy(list, other, list.size());
    }

    private static void copy(DefaultedList<ItemStack> list, DefaultedList<ItemStack> other, int maxItems) {
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
        public InventoryProperty readFromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapperLookup) {

            final NbtList armorNbt = nbt.getListOrEmpty("Armor");
            final var armorList = DefaultedList.ofSize(4, ItemStack.EMPTY);
            for (int i = 0; i < armorNbt.size(); i++) {
                armorList.set(i, fromNbt(armorNbt.getCompoundOrEmpty(i), wrapperLookup));
            }

            final NbtList itemNbt = nbt.getListOrEmpty("Items");
            final var itemList = DefaultedList.ofSize(37, ItemStack.EMPTY);
            for (int i = 0; i < itemNbt.size(); i++) {
                itemList.set(i, fromNbt(itemNbt.getCompoundOrEmpty(i), wrapperLookup));
            }

            return new InventoryProperty(itemList, armorList);
        }
    }

    private static ItemStack fromNbt(NbtElement nbt, RegistryWrapper.WrapperLookup wrapperLookup) {
        return ItemStack.CODEC.parse(wrapperLookup.getOps(NbtOps.INSTANCE), nbt).result().orElse(ItemStack.EMPTY);
    }
}
