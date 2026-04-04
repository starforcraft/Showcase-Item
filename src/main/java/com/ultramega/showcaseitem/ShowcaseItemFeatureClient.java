package com.ultramega.showcaseitem;

import com.ultramega.showcaseitem.config.Config;
import com.ultramega.showcaseitem.interfaces.AlphaItemStackRenderState;
import com.ultramega.showcaseitem.mixin.client.AccessorGuiGraphicsExtractor;
import com.ultramega.showcaseitem.network.ShareItemData;

import java.util.List;
import javax.annotation.Nullable;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.client.renderer.state.gui.GuiItemRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.client.settings.KeyModifier;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;

@EventBusSubscriber(modid = ShowcaseItem.MODID, value = Dist.CLIENT)
public class ShowcaseItemFeatureClient {
    @Nullable
    public static GuiGraphicsExtractor graphics = null;

    private static long lastShadeTimestamp = -1;

    private ShowcaseItemFeatureClient() {
    }

    public static void renderItemForMessage(final FormattedCharSequence sequence, final float textTop, final float opacity) {
        if (!Config.renderItemsInChat || graphics == null) {
            return;
        }

        final Minecraft mc = Minecraft.getInstance();
        final StringBuilder before = new StringBuilder();
        final int halfSpace = mc.font.width(" ") / 2;

        sequence.accept((counter_, style, character) -> {
            final String sofar = before.toString();
            if (sofar.endsWith("  ")) {
                render(mc, graphics, sofar.substring(0, sofar.length() - 2), character == ' ' ? 0 : -halfSpace, 0, textTop, style, opacity);
                return false;
            }
            before.append((char) character);
            return true;
        });
    }

    @SubscribeEvent
    public static void keyboardEvent(final ScreenEvent.KeyPressed.Pre event) {
        if (ModKeyBindings.SHOWCASE_ITEM_KEY.isUnbound()) {
            return;
        }

        final Minecraft mc = Minecraft.getInstance();
        if (InputConstants.isKeyDown(mc.getWindow(), ModKeyBindings.SHOWCASE_ITEM_KEY.getKey().getValue()) && keyModifierPressed(mc)) {
            keyPressed();
        }
    }

    public static void keyPressed() {
        final Minecraft mc = Minecraft.getInstance();
        final Screen screen = mc.screen;

        if (screen instanceof AbstractContainerScreen<?> gui) {
            final List<? extends GuiEventListener> children = gui.children();
            for (final GuiEventListener c : children) {
                if (c instanceof EditBox tf) {
                    if (tf.isFocused()) {
                        return;
                    }
                }
            }

            final Slot slot = gui.getSlotUnderMouse();
            if (slot != null) {
                final ItemStack stack = slot.getItem();

                if (!stack.isEmpty()) {
                    if (mc.level != null && mc.level.getGameTime() - lastShadeTimestamp > 10) {
                        lastShadeTimestamp = mc.level.getGameTime();
                    } else {
                        return;
                    }

                    ClientPacketDistributor.sendToServer(new ShareItemData(slot.getSlotIndex(), gui.getMenu().containerId));
                }
            }
        }
    }

    public static MutableComponent createStackComponent(final ItemStack stack, final MutableComponent component) {
        if (!Config.renderItemsInChat) {
            return component;
        }

        Style style = component.getStyle();
        if (stack.getCount() > 64) {
            final ItemStackTemplate copyStack = ItemStackTemplate.fromNonEmptyStack(stack.copy()).withCount(64);
            style = style.withHoverEvent(new HoverEvent.ShowItem(copyStack));
            component.withStyle(style);
        }

        final MutableComponent out = Component.literal("   ");
        out.setStyle(style);
        return out.append(component);
    }

    private static void render(final Minecraft mc,
                               final GuiGraphicsExtractor graphics,
                               final String before,
                               final float extraShift,
                               final float x,
                               final float y,
                               final Style style,
                               final float opacity) {
        final HoverEvent hoverEvent = style.getHoverEvent();
        if (hoverEvent instanceof HoverEvent.ShowItem(ItemStackTemplate stack)) {
            if (stack.item().value() == Items.AIR || stack.count() <= 0) {
                stack = new ItemStackTemplate(Blocks.BARRIER.asItem()); // For invalid icon
            }

            float renderY = y;
            final float shift = mc.font.width(before) + extraShift;
            final Player player = mc.player;

            // Fix y-shift if overflowingbars is installed
            if (ModList.get().isLoaded("overflowingbars")) {
                if (player != null) {
                    renderY += player.getAbsorptionAmount() > 10.0F ? 10 : 0;
                    renderY += player.getArmorValue() > 0.5F ? 10 : 0;
                }
            }

            if (opacity > 0) {
                final Matrix3x2fStack pose = graphics.pose();

                pose.pushMatrix();
                pose.translate(shift + x + (mc.font.width("  ")) / 2.0f, renderY);
                pose.scale(0.5f, 0.5f);

                renderItem(graphics, player, mc.level, stack.create(), 0, 0, 0, opacity);

                graphics.pose().popMatrix();
            }
        }
    }

    /**
     * Copied and modified from {@link GuiGraphicsExtractor#item(LivingEntity, Level, ItemStack, int, int, int)}
     */
    public static void renderItem(final GuiGraphicsExtractor graphics,
                                  final @org.jspecify.annotations.Nullable LivingEntity owner,
                                  final @org.jspecify.annotations.Nullable Level level,
                                  final ItemStack itemStack,
                                  final int x,
                                  final int y,
                                  final int seed,
                                  final float alpha) {
        if (!itemStack.isEmpty()) {
            final TrackingItemStackRenderState itemStackRenderState = new TrackingItemStackRenderState();
            Minecraft.getInstance().getItemModelResolver().updateForTopItem(itemStackRenderState, itemStack, ItemDisplayContext.GUI, level, owner, seed);

            if (alpha < 1.0f && itemStackRenderState instanceof AlphaItemStackRenderState alphaItemStackRenderState) {
                itemStackRenderState.setAnimated();
                alphaItemStackRenderState.showcaseitem$setAlpha(alpha);
            }

            try {
                ((AccessorGuiGraphicsExtractor) graphics).showcaseitem$getGuiRenderState()
                    .addItem(new GuiItemRenderState(new Matrix3x2f(graphics.pose()), itemStackRenderState, x, y, graphics.peekScissorStack()));
            } catch (Throwable var11) {
                final CrashReport report = CrashReport.forThrowable(var11, "Rendering item");
                final CrashReportCategory category = report.addCategory("Item being rendered");
                category.setDetail("Item Type", () -> String.valueOf(itemStack.getItem()));
                category.setDetail("Item Components", () -> String.valueOf(itemStack.getComponents()));
                category.setDetail("Item Foil", () -> String.valueOf(itemStack.hasFoil()));
                throw new ReportedException(report);
            }
        }
    }

    // TODO: I don't like the code below at all
    private static boolean keyModifierPressed(final Minecraft mc) {
        final int keyModifierInt = checkLeftKeyModifier();
        final int keyModifierInt2 = checkRightKeyModifier();

        if (keyModifierInt != -1) {
            return InputConstants.isKeyDown(mc.getWindow(), keyModifierInt);
        } else if (keyModifierInt2 != -1) {
            return InputConstants.isKeyDown(mc.getWindow(), keyModifierInt2);
        }

        return true;
    }

    private static int checkLeftKeyModifier() {
        final KeyModifier keyModifier = ModKeyBindings.SHOWCASE_ITEM_KEY.getKeyModifier();
        int keyModifierInt = -1;
        if (keyModifier.equals(KeyModifier.CONTROL)) {
            keyModifierInt = 341;
        } else if (keyModifier.equals(KeyModifier.ALT)) {
            keyModifierInt = 342;
        } else if (keyModifier.equals(KeyModifier.SHIFT)) {
            keyModifierInt = 340;
        }
        return keyModifierInt;
    }

    private static int checkRightKeyModifier() {
        final KeyModifier keyModifier = ModKeyBindings.SHOWCASE_ITEM_KEY.getKeyModifier();
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
