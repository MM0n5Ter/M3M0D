package com.testmod.event;

import com.testmod.ExampleMod;
import com.testmod.client.input.KeyBindings;
import com.testmod.client.screen.CommandWheelScreen;
import com.testmod.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class CommandWheelHandler  {
    private CommandWheelHandler () {}

    public static boolean isCommandWheelOpen = false;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;
            if (player == null) {
                return;
            }

            // 检查是否是“打开指令轮盘”键被按下
            if (KeyBindings.OPEN_COMMAND_WHEEL_KEY.consumeClick()) { // consumeClick() 会在按键按下时返回true一次
                // 检查玩家副手是否持有战术板
                if(player.getItemInHand(InteractionHand.OFF_HAND).getItem() == ModItems.COMMAND_TABLET.get()) {
                    isCommandWheelOpen = !isCommandWheelOpen;
                    if(isCommandWheelOpen) {
                        ExampleMod.LOGGER.info("COMMAND WHEEL OPENED");
                        mc.setScreen(new CommandWheelScreen(Component.literal("战术板")));
                    }
                    else {
                        // 如果当前已经是 CommandWheelScreen，再次按下 X 键的关闭逻辑由 CommandWheelScreen.keyPressed() 处理
                        // 所以这里不需要再调用 mc.screen.onClose()，避免重复处理或潜在的逻辑冲突。
                        // isCommandWheelOpen 的状态会在 CommandWheelScreen.onClose() 中被设为 false。
                    }
                }
            }

        }
    }
}
