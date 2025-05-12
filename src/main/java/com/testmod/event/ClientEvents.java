package com.testmod.event;

import com.testmod.ExampleMod;
import com.testmod.client.model.ModModelLayers;
import com.testmod.client.model.SampleGuardOperatorModel;
import com.testmod.client.model.SampleGuardOperatorRender; // 注意路径可能需要调整为你实际的Render类路径
import com.testmod.init.ModOperatorEntities;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;


@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {

    private ClientEvents() {} // 防止实例化

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ExampleMod.LOGGER.info("HELLO FROM CLIENT SETUP");
        ExampleMod.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        // 其他客户端设置代码，例如注册按键绑定等
        // event.enqueueWork(() -> {
        //    KeyBindings.register();
        //    MenuScreens.register(...);
        // });
    }

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        ExampleMod.LOGGER.info("Registering entity renderers...");
        event.registerEntityRenderer(ModOperatorEntities.SAMPLE_GUARD.get(), SampleGuardOperatorRender::new);
        ExampleMod.LOGGER.info("Registered entity renderer for Guard Operator.");
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        ExampleMod.LOGGER.info("Registering layer definitions...");
        event.registerLayerDefinition(ModModelLayers.GUARD_OPERATOR_LAYER, SampleGuardOperatorModel::createBodyLayer);
        ExampleMod.LOGGER.info("Registered layer definition for Guard Operator.");
    }
}
