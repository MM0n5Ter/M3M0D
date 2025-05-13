package com.testmod.client.screen;

import com.testmod.ExampleMod;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class TacticalScreen extends Screen {

    public TacticalScreen() {
        super(Component.literal("tactical"));
    }

    @Override
    protected void init() {
        super.init();

        int screenWidth = this.width;
        int screenHeight = this.height;
        ExampleMod.LOGGER.info("TacticalScreen initialized. Size: {}x{}", screenWidth, screenHeight);

    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.onClose(); // 调用 Screen 的关闭方法
            return true;    // 表示事件已处理
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        super.onClose(); // 这会将 minecraft.screen 设为 null
        ExampleMod.LOGGER.info("TacticalScreen closed.");
    }

}
