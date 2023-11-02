package com.glisco.deathlog.server.gui;

import com.glisco.deathlog.client.DeathInfo;
import com.glisco.deathlog.death_info.DeathInfoProperty;
import com.glisco.deathlog.server.ServerDeathLogStorage;
import com.mojang.authlib.GameProfile;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

public class DeathGui extends SimpleGui {

    private DeathInfo deathInfo;
    private final ServerDeathLogStorage storage;
    private final GameProfile profile;
    private int index;

    public DeathGui(ServerPlayerEntity player, ServerDeathLogStorage storage, GameProfile profile, int index) {
        super(ScreenHandlerType.GENERIC_9X6, player, false);
        this.storage = storage;
        this.profile = profile;
        this.index = index;
        initializeSlots();
    }

    private void initializeSlots() {
        List<DeathInfo> deathInfos = storage.getDeathInfoList(profile.getId());
        deathInfo = deathInfos.get(index);

        setTitle(deathInfo.getTitle());

        DefaultedList<ItemStack> playerItems = deathInfo.getPlayerItems();
        for (int i = 0; i < playerItems.size(); i++) {
            setSlot(i, playerItems.get(i));
        }
        DefaultedList<ItemStack> playerArmor = deathInfo.getPlayerArmor();
        for (int i = 0; i < playerArmor.size(); i++) {
            setSlot(41 + i, playerArmor.get(i));
        }

        boolean hasPreviousPage = index > 0;
        setSlot(48, new GuiElementBuilder(Items.PLAYER_HEAD)
            .setSkullOwner(hasPreviousPage ? GuiTextures.GUI_PREVIOUS_PAGE : GuiTextures.GUI_PREVIOUS_PAGE_BLOCKED)
            .setName(Text.literal("Previous page"))
            .setCallback(() -> {
                if (hasPreviousPage) {
                    index -= 1;
                    initializeSlots();
                }
            })
        );
        setSlot(49, new GuiElementBuilder(Items.BOOK)
            .setName(Text.literal((index + 1) + " / " + deathInfos.size()))
        );

        boolean hasNextPage = index < deathInfos.size() - 1;
        setSlot(50, new GuiElementBuilder(Items.PLAYER_HEAD)
            .setSkullOwner(hasNextPage ? GuiTextures.GUI_NEXT_PAGE : GuiTextures.GUI_NEXT_PAGE_BLOCKED)
            .setName(Text.literal("Next page"))
            .setCallback(() -> {
                if (hasNextPage) {
                    index += 1;
                    initializeSlots();
                }
            })
        );

        Text dimension = deathInfo.getProperty(DeathInfo.DIMENSION_KEY).map(DeathInfoProperty::formatted)
            .orElse(Text.literal("Unknown dimension..."));
        Text coordinates = deathInfo.getProperty(DeathInfo.COORDINATES_KEY).map(DeathInfoProperty::formatted)
            .orElse(Text.literal("Unknown coordinates..."));

        setSlot(51, new GuiElementBuilder(Items.EMERALD)
            .setName(Text.literal("Restore inventory"))
            .setCallback(() -> {
                player.server.getCommandManager().executeWithPrefix(player.getCommandSource(), "/deathlog restore %s %d".formatted(profile.getName(), index));
            })
        );

        setSlot(52, new GuiElementBuilder(Items.ENDER_PEARL)
            .setName(Text.literal("Click to teleport!"))
            .setLore(List.of(dimension, coordinates))
            .setCallback(() -> {
                String dim = deathInfo.getProperty(DeathInfo.DIMENSION_KEY).map(DeathInfoProperty::toSearchableString)
                    .orElse("minecraft:overworld");
                String pos = deathInfo.getProperty(DeathInfo.COORDINATES_KEY).map(DeathInfoProperty::toSearchableString)
                    .orElse("0 0 0");
                player.server.getCommandManager().executeWithPrefix(player.getCommandSource(), "/execute in %s run tp @s %s".formatted(dim, pos));
            })
        );

        Text time = deathInfo.getProperty(DeathInfo.TIME_OF_DEATH_KEY).map(DeathInfoProperty::formatted)
            .orElse(Text.literal("Unknown time..."));
        setSlot(53, new GuiElementBuilder(Items.CLOCK).setName(time));
    }


}
