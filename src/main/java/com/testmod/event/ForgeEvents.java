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