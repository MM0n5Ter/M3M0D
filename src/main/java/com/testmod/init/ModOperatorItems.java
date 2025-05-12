package com.testmod.init;

import com.testmod.ExampleMod;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.testmod.init.ModOperatorEntities.SAMPLE_GUARD;

public class ModOperatorItems {
    public static final DeferredRegister<Item> OPERATOR_ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ExampleMod.MODID);

    public static final RegistryObject<Item> OPERATOR_SPAWN_EGG = OPERATOR_ITEMS.register("guard_operator_spawn_egg",
            () -> new ForgeSpawnEggItem(SAMPLE_GUARD,0xFFFFFF, 0xFFFFFF, // 近卫颜色
                    new Item.Properties()));

    public static void register(IEventBus eventBus) {
        OPERATOR_ITEMS.register(eventBus);
    }
}
