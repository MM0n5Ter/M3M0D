package com.testmod.init;

import com.testmod.ExampleMod;
import com.testmod.entity.item.DebugStick;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    // 创建一个 DeferredRegister 实例，用于注册物品。
    // 它与物品注册表 (ForgeRegistries.ITEMS) 和你的 mod ID 绑定。
    public static final DeferredRegister<Item> GENERAL_ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ExampleMod.MODID);

    // 注册你的 DebugStickItem
    public static final RegistryObject<Item> DEBUG_STICK = GENERAL_ITEMS.register("debug_stick",
            () -> new DebugStick(new Item.Properties().stacksTo(1))); // 基础属性，后面可以调整

    // 这个方法需要在你的主Mod类的构造函数中被调用，以注册DeferredRegister到事件总线
    public static void register(IEventBus eventBus) {
        GENERAL_ITEMS.register(eventBus);
    }
}
