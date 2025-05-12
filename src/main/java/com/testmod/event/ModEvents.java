package com.testmod.event;

import com.testmod.init.ModBlocks;
import com.testmod.init.ModItems;
import com.testmod.init.ModOperatorEntities;
import com.testmod.init.ModOperatorItems;
import com.testmod.init.ModCreativeTabsDefinition;
import com.testmod.entity.operator.SampleGuardOperatorEntity;

import com.testmod.ExampleMod;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {
    private ModEvents() {}

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        ExampleMod.LOGGER.info("Registering entity attributes...");
        event.put(ModOperatorEntities.SAMPLE_GUARD.get(), SampleGuardOperatorEntity.createAttributes().build());
        ExampleMod.LOGGER.info("Attributes registered for Guard Operator.");
        // 注册其他实体属性...
    }

    @SubscribeEvent
    public static void addCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        ExampleMod.LOGGER.info("Adding items to creative tabs for tab: " + event.getTabKey().location());

        // 添加到原版 "建筑方块" 物品栏
        if (event.getTabKey() == ModCreativeTabsDefinition.MOD_TAB.getKey()) {
            event.accept(ModItems.DEBUG_STICK.get());
        }

        // 添加到原版 "刷怪蛋" 物品栏
        if (event.getTabKey() == ModCreativeTabsDefinition.MOD_TAB.getKey()) {
            event.accept(ModOperatorItems.OPERATOR_SPAWN_EGG.get());
        }

        // 如果你还想向其他原版标签页或自己的标签页添加更多东西，在这里继续添加
        // 例如，如果你在 ModCreativeTabsDefinition.EXAMPLE_TAB 的 displayItems 中没有完全定义所有内容
        // 你也可以在这里为它补充：
        // if (event.getTabKey() == ModCreativeTabsDefinition.EXAMPLE_TAB.getKey()) {
        //     event.accept(SomeOtherItem.get());
        // }
    }
}
