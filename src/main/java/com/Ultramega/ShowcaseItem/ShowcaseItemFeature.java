package com.Ultramega.ShowcaseItem;

import com.Ultramega.ShowcaseItem.config.ShowcaseItemConfig;
import com.Ultramega.ShowcaseItem.message.ShareItemMessage;
import com.Ultramega.ShowcaseItem.message.ShowcaseItemNetwork;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = ShowcaseItem.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ShowcaseItemFeature {
	public static float alphaValue = 1F;

	private static long lastShadeTimestamp = -1;

	@OnlyIn(Dist.CLIENT)
	public static void renderItemForMessage(PoseStack poseStack, FormattedCharSequence sequence, float x, float y, int color) {
		if (!ShowcaseItemConfig.RENDER_ITEMS_IN_CHAT.get())
			return;

		Minecraft mc = Minecraft.getInstance();

		StringBuilder before = new StringBuilder();

		sequence.accept((counter_, style, character) -> {
			String sofar = before.toString();
			if (sofar.endsWith("    ")) {
				render(mc, poseStack, sofar.substring(0, sofar.length() - 3), x, y, style, color);
				return false;
			}
			before.append((char) character);
			return true;
		});
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void keyboardEvent(ScreenEvent.KeyPressed.Pre event) {
		Minecraft mc = Minecraft.getInstance();
		if(InputConstants.isKeyDown(mc.getWindow().getWindow(), ModKeyBindings.SHOWCASE_ITEM.getKey().getValue()) && keyModifierPressed(mc)) {
			keyPressed();
		}
	}

	public static void keyPressed() {
		Minecraft mc = Minecraft.getInstance();
		Screen screen = mc.screen;

		if(screen instanceof AbstractContainerScreen<?> gui) {
			List<? extends GuiEventListener> children = gui.children();
			for(GuiEventListener c : children)
				if(c instanceof EditBox tf) {
					if(tf.isFocused())
						return;
				}

			Slot slot = gui.getSlotUnderMouse();
			if(slot != null) {
				ItemStack stack = slot.getItem();

				if(!stack.isEmpty()) {
					if(mc.level != null && mc.level.getGameTime() - lastShadeTimestamp > 10) {
						lastShadeTimestamp = mc.level.getGameTime();
					} else return;

					ShareItemMessage message = new ShareItemMessage(slot.index, gui.getMenu().containerId);
					ShowcaseItemNetwork.sendToServer(message);
				}
			}
		}
	}

	public static void shareItem(ServerPlayer player, int slot, int containedId) {
		if(player.containerMenu.containerId == containedId) {
			var slots = player.containerMenu.slots;
			if (slot >= 0 && slots.size() > slot) {
				ItemStack stack = slots.get(slot).getItem();
				if (!stack.isEmpty()) {
					MutableComponent comp = Component.translatable("showcaseitem.misc.shared_item", player.getName());
					Component itemComp = stack.getDisplayName();

					comp.append(itemComp);
					player.server.getPlayerList().getPlayers().forEach(p -> p.sendSystemMessage(comp));
				}
			}
		}
	}

	public static MutableComponent createStackComponent(ItemStack stack, MutableComponent component) {
		if (!ShowcaseItemConfig.RENDER_ITEMS_IN_CHAT.get())
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
	private static void render(Minecraft mc, PoseStack pose, String before, float x, float y, Style style, int color) {
		float a = (color >> 24 & 255) / 255.0F;

		HoverEvent hoverEvent = style.getHoverEvent();
		if (hoverEvent != null && hoverEvent.getAction() == HoverEvent.Action.SHOW_ITEM) {
			HoverEvent.ItemStackInfo contents = hoverEvent.getValue(HoverEvent.Action.SHOW_ITEM);

			ItemStack stack = contents != null ? contents.getItemStack() : ItemStack.EMPTY;

			if (stack.isEmpty())
				stack = new ItemStack(Blocks.BARRIER); // for invalid icon

			int shift = mc.font.width(before);

			if (a > 0) {
				alphaValue = a;

				PoseStack poseStack = RenderSystem.getModelViewStack();

				poseStack.pushPose();

				poseStack.mulPoseMatrix(pose.last().pose());

				poseStack.translate(shift + x, y, 0);
				poseStack.scale(0.5f, 0.5f, 0.5f);
				mc.getItemRenderer().renderGuiItem(pose, stack, 0, 0);
				poseStack.popPose();

				RenderSystem.applyModelViewMatrix();

				alphaValue = 1F;
			}
		}
	}

	private static boolean keyModifierPressed(Minecraft mc) {
		int keyModifierInt = checkLeftKeyModifier();
		int keyModifierInt2 = checkRightKeyModifier();
		if(keyModifierInt != -1)
			return InputConstants.isKeyDown(mc.getWindow().getWindow(), keyModifierInt);
		else if(keyModifierInt2 != -1)
			return InputConstants.isKeyDown(mc.getWindow().getWindow(), keyModifierInt2);
		else
			return true;
	}

	private static int checkLeftKeyModifier() {
		KeyModifier keyModifier = ModKeyBindings.SHOWCASE_ITEM.getKeyModifier();
		int keyModifierInt = -1;
		if(keyModifier.equals(KeyModifier.CONTROL))
			keyModifierInt = 341;
		else if(keyModifier.equals(KeyModifier.ALT))
			keyModifierInt = 342;
		else if(keyModifier.equals(KeyModifier.SHIFT))
			keyModifierInt = 340;
		return keyModifierInt;
	}

	private static int checkRightKeyModifier() {
		KeyModifier keyModifier = ModKeyBindings.SHOWCASE_ITEM.getKeyModifier();
		int keyModifierInt = -1;
		if(keyModifier.equals(KeyModifier.CONTROL)) {
			keyModifierInt = 345;
		} else if(keyModifier.equals(KeyModifier.ALT)) {
			keyModifierInt = 346;
		} else if(keyModifier.equals(KeyModifier.SHIFT)) {
			keyModifierInt = 344;
		}
		return keyModifierInt;
	}
}
