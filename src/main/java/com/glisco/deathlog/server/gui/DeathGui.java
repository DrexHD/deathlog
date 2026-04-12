package com.glisco.deathlog.server.gui;

import com.glisco.deathlog.client.DeathInfo;
import com.glisco.deathlog.death_info.DeathInfoProperty;
import com.glisco.deathlog.server.ServerDeathLogStorage;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.core.NonNullList;

import java.util.List;

public class DeathGui extends SimpleGui {

    private DeathInfo deathInfo;
    private final ServerDeathLogStorage storage;
    private final NameAndId profile;
    private int index;

    public DeathGui(ServerPlayer player, ServerDeathLogStorage storage, NameAndId profile, int index) {
        super(MenuType.GENERIC_9x6, player, false);
        this.storage = storage;
        this.profile = profile;
        this.index = index;
        initializeSlots();
    }

    private void initializeSlots() {
        List<DeathInfo> deathInfos = storage.getDeathInfoList(profile.id());
        deathInfo = deathInfos.get(index);

        setTitle(deathInfo.getTitle());

        NonNullList<ItemStack> playerItems = deathInfo.getPlayerItems();
        for (int i = 0; i < playerItems.size(); i++) {
            setSlot(i, playerItems.get(i));
        }
        NonNullList<ItemStack> playerArmor = deathInfo.getPlayerArmor();
        for (int i = 0; i < playerArmor.size(); i++) {
            setSlot(41 + i, playerArmor.get(i));
        }

        boolean hasPreviousPage = index > 0;
        setSlot(48, new GuiElementBuilder(Items.PLAYER_HEAD)
            .setSkullOwner(hasPreviousPage ? GuiTextures.GUI_PREVIOUS_PAGE : GuiTextures.GUI_PREVIOUS_PAGE_BLOCKED)
            .setName(Component.literal("Previous page"))
            .setCallback(() -> {
                if (hasPreviousPage) {
                    index -= 1;
                    initializeSlots();
                }
            })
        );
        setSlot(49, new GuiElementBuilder(Items.BOOK)
            .setName(Component.literal((index + 1) + " / " + deathInfos.size()))
        );

        boolean hasNextPage = index < deathInfos.size() - 1;
        setSlot(50, new GuiElementBuilder(Items.PLAYER_HEAD)
            .setSkullOwner(hasNextPage ? GuiTextures.GUI_NEXT_PAGE : GuiTextures.GUI_NEXT_PAGE_BLOCKED)
            .setName(Component.literal("Next page"))
            .setCallback(() -> {
                if (hasNextPage) {
                    index += 1;
                    initializeSlots();
                }
            })
        );

        Component dimension = deathInfo.getProperty(DeathInfo.DIMENSION_KEY).map(DeathInfoProperty::formatted)
            .orElse(Component.literal("Unknown dimension..."));
        Component coordinates = deathInfo.getProperty(DeathInfo.COORDINATES_KEY).map(DeathInfoProperty::formatted)
            .orElse(Component.literal("Unknown coordinates..."));

        setSlot(51, new GuiElementBuilder(Items.EMERALD)
            .setName(Component.literal("Restore inventory"))
            .setCallback(() -> {
                player.level().getServer().getCommands().performPrefixedCommand(player.createCommandSourceStack(), "/deathlog restore %s %d".formatted(profile.name(), index));
            })
        );

        setSlot(52, new GuiElementBuilder(Items.ENDER_PEARL)
            .setName(Component.literal("Click to teleport!"))
            .setLore(List.of(dimension, coordinates))
            .setCallback(() -> {
                String dim = deathInfo.getProperty(DeathInfo.DIMENSION_KEY).map(DeathInfoProperty::toSearchableString)
                    .orElse("minecraft:overworld");
                String pos = deathInfo.getProperty(DeathInfo.COORDINATES_KEY).map(DeathInfoProperty::toSearchableString)
                    .orElse("0 0 0");
                player.level().getServer().getCommands().performPrefixedCommand(player.createCommandSourceStack(), "/execute in %s run tp @s %s".formatted(dim, pos));
            })
        );

        Component time = deathInfo.getProperty(DeathInfo.TIME_OF_DEATH_KEY).map(DeathInfoProperty::formatted)
            .orElse(Component.literal("Unknown time..."));
        setSlot(53, new GuiElementBuilder(Items.CLOCK).setName(time));
    }


}
