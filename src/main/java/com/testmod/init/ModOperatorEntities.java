package com.testmod.init;

import com.testmod.ExampleMod;
import com.testmod.entity.operator.SampleGuardOperatorEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModOperatorEntities {

    public static final DeferredRegister<EntityType<?>> OPERATOR_ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ExampleMod.MODID);

    public static final RegistryObject<EntityType<SampleGuardOperatorEntity>> SAMPLE_GUARD =
            OPERATOR_ENTITY_TYPES.register("sample_guard",
                    () -> EntityType.Builder.of(SampleGuardOperatorEntity::new, MobCategory.CREATURE)
                            .sized(0.6F, 1.8F)
                            .build("sample_guard"));

    public static void register(IEventBus eventBus) {
        OPERATOR_ENTITY_TYPES.register(eventBus);
    }
}
