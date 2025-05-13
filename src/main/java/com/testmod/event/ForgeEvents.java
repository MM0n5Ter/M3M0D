package com.testmod.event;

import com.testmod.ExampleMod;
import com.testmod.client.input.KeyBindings;
import com.testmod.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE) // 注意这里是 FORGE bus
public class ForgeEvents {

    private ForgeEvents() {}

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        ExampleMod.LOGGER.info("HELLO from server starting (via ForgeEvents class)");
        // 其他服务器启动逻辑
    }
}

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
class ClientTickHandler  {
    private ClientTickHandler () {}

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
                    ClientEvents.isCommandWheelOpen = !ClientEvents.isCommandWheelOpen;
                    if(ClientEvents.isCommandWheelOpen) {
                        ExampleMod.LOGGER.info("COMMAND WHEEL OPENED");
                        //ToDo: open command wheel
                        mc.player.sendSystemMessage(Component.literal("COMMAND WHEEL OPENED"));
                    }
                    else {
                        ExampleMod.LOGGER.info("COMMAND WHEEL CLOSED");
                        mc.player.sendSystemMessage(Component.literal("COMMAND WHEEL CLOSED"));
                    }
                }
            }

            // 其他快捷键
        }
    }
}