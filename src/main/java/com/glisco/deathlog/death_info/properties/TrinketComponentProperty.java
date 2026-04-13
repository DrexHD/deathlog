//package com.glisco.deathlog.death_info.properties;
//
//import com.glisco.deathlog.client.DeathInfo;
//import com.glisco.deathlog.death_info.DeathInfoPropertyType;
//import com.glisco.deathlog.death_info.RestorableDeathInfoProperty;
//import dev.emi.trinkets.api.TrinketsApi;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.ContainerHelper;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.server.level.ServerPlayer;
//import net.minecraft.world.level.storage.ValueInput;
//import net.minecraft.world.level.storage.ValueOutput;
//import net.minecraft.network.chat.Component;
//import net.minecraft.core.NonNullList;
//
//public class TrinketComponentProperty implements RestorableDeathInfoProperty {
//
//    private final CompoundTag componentNbt;
//    private final NonNullList<ItemStack> trinkets;
//
//    public TrinketComponentProperty(CompoundTag componentNbt, NonNullList<ItemStack> trinkets) {
//        this.componentNbt = componentNbt;
//        this.trinkets = trinkets;
//    }
//
//    @Override
//    public DeathInfoPropertyType<?> getType() {
//        return Type.INSTANCE;
//    }
//
//    @Override
//    public Component formatted() {
//        return Component.translatable("deathlog.deathinfoproperty.trinket_component.value", trinkets.size());
//    }
//
//    @Override
//    public void writeNbt(ValueOutput view) {
//        view.store("ComponentData", CompoundTag.CODEC, componentNbt);
//        ContainerHelper.saveAllItems(view, trinkets);
//    }
//
//    @Override
//    public String toSearchableString() {
//        StringBuilder builder = new StringBuilder();
//        trinkets.forEach(stack -> builder.append(stack.getHoverName().getString()));
//        return builder.toString();
//    }
//
//    @Override
//    public void restore(ServerPlayer player) {
//        TrinketsApi.getTrinketComponent(player).get().readFromNbt(componentNbt, player.registryAccess());
//    }
//
//    public static void apply(DeathInfo info, Player player) {
//        final var trinketComponent = TrinketsApi.getTrinketComponent(player).get();
//        var list = trinketComponent.getAllEquipped().stream().map(pair -> pair.getB().copy()).toList();
//
//        var nbt = new CompoundTag();
//        trinketComponent.writeToNbt(nbt, player.registryAccess());
//
//        info.setProperty("trinket_component", new TrinketComponentProperty(nbt, NonNullList.of(ItemStack.EMPTY, list.toArray(new ItemStack[0]))));
//    }
//
//    public static class Type extends DeathInfoPropertyType<TrinketComponentProperty> {
//
//        public static final Type INSTANCE = new Type();
//
//        private Type() {
//            super("deathlog.deathinfoproperty.trinket_component", "trinket_component");
//        }
//
//        @Override
//        public boolean displayedInInfoView() {
//            return true;
//        }
//
//        @Override
//        public TrinketComponentProperty readFromNbt(ValueInput view) {
//            var componentNbt = view.read("ComponentData", CompoundTag.CODEC).orElse(new CompoundTag());
//
//
//            var trinketList = NonNullList.withSize(view.childrenList("Items").map(readViews -> readViews.stream().toList().size()).orElse(0), ItemStack.EMPTY);
//            ContainerHelper.loadAllItems(view, trinketList);
//
//            return new TrinketComponentProperty(componentNbt, trinketList);
//        }
//    }
//}
