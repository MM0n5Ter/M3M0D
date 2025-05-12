package com.testmod.client.model;

import com.testmod.ExampleMod;
import com.testmod.entity.operator.SampleGuardOperatorEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.testmod.client.model.ModModelLayers;

public class SampleGuardOperatorRender extends MobRenderer<SampleGuardOperatorEntity, SampleGuardOperatorModel<SampleGuardOperatorEntity>> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(ExampleMod.MODID, "textures/entity/chen.png");

    public SampleGuardOperatorRender(EntityRendererProvider.Context context) {
        super(context,
                new SampleGuardOperatorModel<>(context.bakeLayer(ModModelLayers.GUARD_OPERATOR_LAYER)), // 使用模型层
                0.5f); // 阴影大小 (0.5f是默认值)
    }

    /**
     * 返回此渲染器将用于给定实体的纹理位置。
     * @param entity 当前正在渲染的实体实例。
     * @return 纹理的 ResourceLocation。
     */
    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SampleGuardOperatorEntity entity) {
        return TEXTURE_LOCATION;
    }
}
