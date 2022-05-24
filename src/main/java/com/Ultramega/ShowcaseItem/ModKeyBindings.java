package com.Ultramega.ShowcaseItem;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;

public class ModKeyBindings {
    public static final KeyBinding SHOWCASE_ITEM = new KeyBinding(
            "key.showcaseitem.showcaseitem",
            KeyConflictContext.GUI,
            KeyModifier.SHIFT,
            InputMappings.Type.KEYSYM,
            GLFW.GLFW_KEY_X,
            "Shocase Item"
    );
}