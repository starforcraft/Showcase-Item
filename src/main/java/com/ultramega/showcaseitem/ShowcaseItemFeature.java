package com.ultramega.showcaseitem;

import com.ultramega.showcaseitem.config.Config;
import com.ultramega.showcaseitem.network.ShareItemData;

import java.util.List;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.settings.KeyModifier;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = ShowcaseItem.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ShowcaseItemFeature {
    public static float alphaValue = 1F;

    private static long lastShadeTimestamp = -1;

    @OnlyIn(Dist.CLIENT)
    public static void renderItemForMessage(GuiGraphics guiGraphics, FormattedCharSequence sequence, float x, float y, int color) {
        if (!Config.renderItemsInChat)
            return;

        Minecraft mc = Minecraft.getInstance();

        StringBuilder before = new StringBuilder();

        int halfSpace = mc.font.width(" ") / 2;

        sequence.accept((counter_, style, character) -> {
            String sofar = before.toString();
            if (sofar.endsWith("  ")) {
                render(mc, guiGraphics, sofar.substring(0, sofar.length() - 2), character == ' ' ? 0 : -halfSpace, x, y, style, color);
                return false;
            }
            before.append((char) character);
            return true;
        });
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void keyboardEvent(ScreenEvent.KeyPressed.Pre event) {
        if (ModKeyBindings.SHOWCASE_ITEM.isUnbound()) return;

        Minecraft mc = Minecraft.getInstance();
        if (InputConstants.isKeyDown(mc.getWindow().getWindow(), ModKeyBindings.SHOWCASE_ITEM.getKey().getValue()) && keyModifierPressed(mc)) {
            keyPressed();
        }
    }

    public static void keyPressed() {
        Minecraft mc = Minecraft.getInstance();
        Screen screen = mc.screen;

        if (screen instanceof AbstractContainerScreen<?> gui) {
            List<? extends GuiEventListener> children = gui.children();
            for (GuiEventListener c : children)
                if (c instanceof EditBox tf) {
                    if (tf.isFocused())
                        return;
                }

            Slot slot = gui.getSlotUnderMouse();
            if (slot != null) {
                ItemStack stack = slot.getItem();

                if (!stack.isEmpty()) {
                    if (mc.level != null && mc.level.getGameTime() - lastShadeTimestamp > 10) {
                        lastShadeTimestamp = mc.level.getGameTime();
                    } else
                        return;

                    PacketDistributor.sendToServer(new ShareItemData(slot.getSlotIndex(), gui.getMenu().containerId));
                }
            }
        }
    }

    public static void shareItem(ServerPlayer player, int slotIndex, int containerId) {
        if (player.containerMenu.containerId != containerId) return;

        NonNullList<Slot> slots = player.containerMenu.slots;
        if (slotIndex >= 0 && slots.size() > slotIndex) {
            ItemStack stack;
            // Creative menu support
            if (player.containerMenu instanceof InventoryMenu) {
                stack = player.getInventory().getItem(slotIndex);
            } else {
                Slot slot = slots.get(slotIndex);
                stack = slot.getItem();
            }
            if (!stack.isEmpty()) {
                MutableComponent message = Component
                    .translatable("showcaseitem.misc.shared_item", player.getName())
                    .append(stack.getDisplayName());

                player.server.getPlayerList().getPlayers().forEach(p -> p.sendSystemMessage(message));
            }
        }
    }

    public static MutableComponent createStackComponent(ItemStack stack, MutableComponent component) {
        if (!Config.renderItemsInChat)
            return component;

        Style style = component.getStyle();
        if (stack.getCount() > 64) {
            ItemStack copyStack = stack.copy();
            copyStack.setCount(64);
            style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackInfo(copyStack)));
            component.withStyle(style);
        }

        MutableComponent out = Component.literal("   ");
        out.setStyle(style);
        return out.append(component);
    }

    @OnlyIn(Dist.CLIENT)
    private static void render(Minecraft mc, GuiGraphics graphics, String before, float extraShift, float x, float y, Style style, int color) {
        float a = (color >> 24 & 255) / 255.0F;

        HoverEvent hoverEvent = style.getHoverEvent();
        if (hoverEvent != null && hoverEvent.getAction() == HoverEvent.Action.SHOW_ITEM) {
            HoverEvent.ItemStackInfo contents = hoverEvent.getValue(HoverEvent.Action.SHOW_ITEM);

            ItemStack stack = contents != null ? contents.getItemStack() : ItemStack.EMPTY;

            if (stack.isEmpty())
                stack = new ItemStack(Blocks.BARRIER); // For invalid icon

            float shift = mc.font.width(before) + extraShift;

            // Fix y-shift if overflowingbars is installed
            if (ModList.get().isLoaded("overflowingbars")) {
                Player player = Minecraft.getInstance().player;
                if (player != null) {
                    y += player.getAbsorptionAmount() > 10.0F ? 10 : 0;
                    y += player.getArmorValue() > 0.5F ? 10 : 0;
                }
            }

            if (a > 0) {
                alphaValue = a;

                PoseStack pose = graphics.pose();
                pose.pushPose();

                pose.translate(shift + x + (mc.font.width("  ")) / 2.0f, y, 0);
                pose.scale(0.5f, 0.5f, 0.5f);

                graphics.renderItem(stack, 0, 0);

                pose.popPose();

                alphaValue = 1F;
            }
        }
    }

    private static boolean keyModifierPressed(Minecraft mc) {
        int keyModifierInt = checkLeftKeyModifier();
        int keyModifierInt2 = checkRightKeyModifier();

        if (keyModifierInt != -1)
            return InputConstants.isKeyDown(mc.getWindow().getWindow(), keyModifierInt);
        else if (keyModifierInt2 != -1)
            return InputConstants.isKeyDown(mc.getWindow().getWindow(), keyModifierInt2);

        return true;
    }

    private static int checkLeftKeyModifier() {
        KeyModifier keyModifier = ModKeyBindings.SHOWCASE_ITEM.getKeyModifier();
        int keyModifierInt = -1;
        if (keyModifier.equals(KeyModifier.CONTROL))
            keyModifierInt = 341;
        else if (keyModifier.equals(KeyModifier.ALT))
            keyModifierInt = 342;
        else if (keyModifier.equals(KeyModifier.SHIFT))
            keyModifierInt = 340;
        return keyModifierInt;
    }

    private static int checkRightKeyModifier() {
        KeyModifier keyModifier = ModKeyBindings.SHOWCASE_ITEM.getKeyModifier();
        int keyModifierInt = -1;
        if (keyModifier.equals(KeyModifier.CONTROL)) {
            keyModifierInt = 345;
        } else if (keyModifier.equals(KeyModifier.ALT)) {
            keyModifierInt = 346;
        } else if (keyModifier.equals(KeyModifier.SHIFT)) {
            keyModifierInt = 344;
        }
        return keyModifierInt;
    }
}
