package com.testmod.event;

import com.testmod.ExampleMod;
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