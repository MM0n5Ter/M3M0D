package com.testmod.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    //内部名
    public static final String KEY_CATEGORY_COMMAND = "key.category.testmod.command";
    //翻译名
    public static final String KEY_OPEN_COMMAND_WHEEL = "key.testmod.open_command_wheel";

    public static KeyMapping OPEN_COMMAND_WHEEL_KEY;

    public static void initializeKeyMappings() {
        OPEN_COMMAND_WHEEL_KEY = new KeyMapping(
                KEY_OPEN_COMMAND_WHEEL,
                KeyConflictContext.IN_GAME,     //游戏内冲突检测
                InputConstants.Type.KEYSYM,     //输入方式为键盘
                GLFW.GLFW_KEY_X,                //默认x
                KEY_CATEGORY_COMMAND
        );
    }

    // 在现代Forge (1.17+), 快捷键的注册通常在 FMLClientSetupEvent 期间通过 ClientRegistry.registerKeyBinding

}
