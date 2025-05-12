package com.testmod.init;

import com.testmod.ExampleMod;
import com.testmod.init.ModItems;
import com.testmod.init.ModOperatorItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabsDefinition {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ExampleMod.MODID);

    public static final RegistryObject<CreativeModeTab> MOD_TAB = CREATIVE_MODE_TABS.register("mod_tab",
            () -> CreativeModeTab.builder()
                    .withTabsBefore(CreativeModeTabs.COMBAT) // 将其放置在战斗标签之前
                    .icon(() -> ModItems.DEBUG_STICK.get().getDefaultInstance()) // 使用 ModItems 中的 EXAMPLE_ITEM
                    .title(Component.translatable("mod_tab")) // 添加标题 (推荐本地化)
                    .displayItems((parameters, output) -> {
                        // output.accept(ModItems.DEBUG_STICK.get());
                        // output.accept(ModOperatorItems.OPERATOR_SPAWN_EGG.get());
                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
