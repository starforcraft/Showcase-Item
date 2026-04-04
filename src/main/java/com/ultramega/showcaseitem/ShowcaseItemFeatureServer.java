package com.ultramega.showcaseitem;

import com.ultramega.showcaseitem.mixin.AccessorServerPlayer;

import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ShowcaseItemFeatureServer {
    private ShowcaseItemFeatureServer() {
    }

    public static void shareItem(final ServerPlayer player, final int slotIndex, final int containerId) {
        if (player.containerMenu.containerId != containerId) {
            return;
        }

        final NonNullList<Slot> slots = player.containerMenu.slots;
        if (slotIndex >= 0 && slots.size() > slotIndex) {
            final ItemStack stack;
            // Creative menu support
            if (player.containerMenu instanceof InventoryMenu) {
                stack = player.getInventory().getItem(slotIndex);
            } else {
                stack = slots.get(slotIndex).getItem();
            }
            if (!stack.isEmpty()) {
                final MutableComponent message = Component
                    .translatable("showcaseitem.misc.shared_item", player.getName())
                    .append(stack.getDisplayName());

                ((AccessorServerPlayer) player).showcaseitem$getServer().getPlayerList().getPlayers().forEach(p -> p.sendSystemMessage(message));
            }
        }
    }
}
