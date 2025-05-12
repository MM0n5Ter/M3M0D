package com.testmod.client.model;

import net.minecraft.client.model.geom.ModelLayerLocation;
import com.testmod.ExampleMod;
import net.minecraft.resources.ResourceLocation;

public class ModModelLayers {
    // 为 GuardOperator 定义一个模型层位置
    // "guard_operator" 是注册名，"main" 通常是默认的层部分名
    public static final ModelLayerLocation GUARD_OPERATOR_LAYER = new ModelLayerLocation(
            new ResourceLocation(ExampleMod.MODID, "guard_operator_model"), "main");

    // 如果你还有其他自定义模型，也在这里为它们定义ModelLayerLocation
    // public static final ModelLayerLocation OTHER_ENTITY_LAYER = ...
}