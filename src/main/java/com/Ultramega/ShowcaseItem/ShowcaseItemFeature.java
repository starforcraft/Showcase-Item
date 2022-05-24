package com.Ultramega.ShowcaseItem;

import com.Ultramega.ShowcaseItem.config.ShowcaseItemConfig;
import com.Ultramega.ShowcaseItem.network.LinkItemMessage;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = ShowcaseItem.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ShowcaseItemFeature {
	private static int chatX, chatY;
	public static float alphaValue = 1F;

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void keyboardEvent(GuiScreenEvent.KeyboardKeyPressedEvent.Pre event) {
		Minecraft mc = Minecraft.getInstance();
		if (InputMappings.isKeyDown(mc.getWindow().getWindow(), ModKeyBindings.SHOWCASE_ITEM.getKey().getValue()) && keyModifierPressed(mc) && event.getGui() instanceof ContainerScreen) {
			ContainerScreen<?> gui = (ContainerScreen<?>) event.getGui();

			List<? extends IGuiEventListener> children = gui.children();
			for (IGuiEventListener c : children)
				if (c instanceof TextFieldWidget) {
					TextFieldWidget tf = (TextFieldWidget) c;
					if (tf.isFocused())
						return;
				}

			Slot slot = gui.getSlotUnderMouse();
			if (slot != null && slot.container != null) {
				ItemStack stack = slot.getItem();

				if (!stack.isEmpty() && !MinecraftForge.EVENT_BUS.post(new ClientChatEvent(stack.getDisplayName().getString()))) {
					ShowcaseItem.NETWORK_HANDLER.sendToServer(new LinkItemMessage(stack));
					event.setCanceled(true);
				}
			}
		}
	}

	private static boolean keyModifierPressed(Minecraft mc) {
		int keyModifierInt = checkLeftKeyModifier();
		int keyModifierInt2 = checkRightKeyModifier();
		if(keyModifierInt != 0)
			return InputMappings.isKeyDown(mc.getWindow().getWindow(), keyModifierInt);
		else if(keyModifierInt2 != 0)
			return InputMappings.isKeyDown(mc.getWindow().getWindow(), keyModifierInt2);
		else
			return false;
	}

	private static int checkLeftKeyModifier() {
		KeyModifier keyModifier = ModKeyBindings.SHOWCASE_ITEM.getKeyModifier();
		int keyModifierInt = 0;
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
		int keyModifierInt = 0;
		if(keyModifier.equals(KeyModifier.CONTROL)) {
			keyModifierInt = 345;
		} else if(keyModifier.equals(KeyModifier.ALT)) {
			keyModifierInt = 346;
		} else if(keyModifier.equals(KeyModifier.SHIFT)) {
			keyModifierInt = 344;
		}
		return keyModifierInt;
	}

	public static void linkItem(PlayerEntity player, ItemStack item) {
		if (!item.isEmpty() && player instanceof ServerPlayerEntity) {
			ITextComponent comp = item.getDisplayName();
			ITextComponent fullComp = new TranslationTextComponent("chat.type.text", player.getDisplayName(), comp);

			PlayerList players = ((ServerPlayerEntity) player).server.getPlayerList();

			ServerChatEvent event = new ServerChatEvent((ServerPlayerEntity) player, comp.getString(), fullComp);
			if (!MinecraftForge.EVENT_BUS.post(event)) {
				players.broadcastMessage(fullComp, ChatType.CHAT, player.getUUID());

				ServerPlayNetHandler handler = ((ServerPlayerEntity) player).connection;
				int threshold = handler.chatSpamTickCount;
				threshold += 20;

				if (threshold > 200 && !players.isOp(player.getGameProfile()))
					handler.onDisconnect(new TranslationTextComponent("disconnect.spam"));

				handler.chatSpamTickCount = threshold;
			}
		}
	}

	public static IFormattableTextComponent createStackComponent(ItemStack stack, IFormattableTextComponent component) {
		if (!ShowcaseItemConfig.RENDER_ITEMS_IN_CHAT.get())
			return component;

		Style style = component.getStyle();
		if (stack.getCount() > 64) {
			ItemStack copyStack = stack.copy();
			copyStack.setCount(64);
			style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemHover(copyStack)));
			component.withStyle(style);
		}

		IFormattableTextComponent out = new StringTextComponent("   ");
		out.setStyle(style);
		return out.append(component);
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void getChatPos(RenderGameOverlayEvent.Chat event) {
		chatX = event.getPosX();
		chatY = event.getPosY();
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void renderSymbols(RenderGameOverlayEvent.Post event) {
		if (!ShowcaseItemConfig.RENDER_ITEMS_IN_CHAT.get())
			return;

		Minecraft mc = Minecraft.getInstance();
		IngameGui gameGui = mc.gui;
		NewChatGui chatGui = gameGui.getChat();
		if (event.getType() == RenderGameOverlayEvent.ElementType.CHAT) {
			int updateCounter = gameGui.getGuiTicks();
			List<ChatLine<IReorderingProcessor>> lines = chatGui.trimmedMessages;
			int shift = chatGui.chatScrollbarPos;

			int idx = shift;

			while (idx < lines.size() && (idx - shift) < chatGui.getLinesPerPage()) {
				ChatLine<IReorderingProcessor> line = lines.get(idx);
				StringBuilder before = new StringBuilder();

				IReorderingProcessor lineProperties = line.getMessage();

				int captureIndex = idx;
				lineProperties.accept((counter_, style, character) -> {
					String sofar = before.toString();
					if (sofar.endsWith("    ")) {
						render(mc, chatGui, updateCounter, sofar.substring(0, sofar.length() - 3), line,
								captureIndex - shift, style);
						return false;
					}
					before.append((char) character);
					return true;
				});

				idx++;
			}
		}
	}

	@SuppressWarnings("deprecation")
	@OnlyIn(Dist.CLIENT)
	private static void render(Minecraft mc, NewChatGui chatGui, int updateCounter, String before, ChatLine<IReorderingProcessor> line, int lineHeight, Style style) {
		HoverEvent hoverEvent = style.getHoverEvent();

		if (hoverEvent != null && hoverEvent.getAction() == HoverEvent.Action.SHOW_ITEM) {
			HoverEvent.ItemHover contents = hoverEvent.getValue(HoverEvent.Action.SHOW_ITEM);

			ItemStack stack = contents != null ? contents.getItemStack() : ItemStack.EMPTY;

			if (stack.isEmpty())
				stack = new ItemStack(Blocks.BARRIER);

			int timeSinceCreation = updateCounter - line.getAddedTime();
			if (chatGui.isChatFocused())
				timeSinceCreation = 0;

			if (timeSinceCreation < 200) {
				float chatOpacity = (float) mc.options.chatOpacity * 0.9f + 0.1f;
				float fadeOut = MathHelper.clamp((1 - timeSinceCreation / 200f) * 10, 0, 1);
				float alpha = fadeOut * fadeOut * chatOpacity;

				int x = chatX + 3 + mc.font.width(before);
				int y = chatY - mc.font.lineHeight * lineHeight;

				if (alpha > 0) {
					alphaValue = alpha;

					RenderSystem.pushMatrix();
					RenderSystem.translatef(x - 2, y - 2, -2);
					RenderSystem.scalef(0.65f, 0.65f, 0.65f);
					mc.getItemRenderer().renderGuiItem(stack, 0, 0);
					RenderSystem.popMatrix();

					alphaValue = 1F;
				}
			}
		}
	}
}
