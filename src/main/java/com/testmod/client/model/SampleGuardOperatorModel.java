package com.testmod.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.testmod.entity.operator.SampleGuardOperatorEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class SampleGuardOperatorModel<T extends SampleGuardOperatorEntity> extends EntityModel<T> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    // 新增：用于外部覆盖层的 ModelPart
    private final ModelPart headLayer;
    private final ModelPart bodyLayer;
    private final ModelPart rightArmLayer;
    private final ModelPart leftArmLayer;
    private final ModelPart rightLegLayer;
    private final ModelPart leftLegLayer;

    public SampleGuardOperatorModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");

        // 初始化外部覆盖层部件
        this.headLayer = root.getChild("head_layer"); // 名称需要与 createBodyLayer 中定义的一致
        this.bodyLayer = root.getChild("body_layer");
        this.rightArmLayer = root.getChild("right_arm_layer");
        this.leftArmLayer = root.getChild("left_arm_layer");
        this.rightLegLayer = root.getChild("right_leg_layer");
        this.leftLegLayer = root.getChild("left_leg_layer");
    }

    // 这个静态方法定义了模型的结构和各个部分的大小、位置
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // --- 基础部件 (与之前类似，但建议明确使用 CubeDeformation.NONE) ---
        partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0) // UV for base head
                        .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16) // UV for base body
                        .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, CubeDeformation.NONE),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16) // UV for base right arm
                        .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE),
                PartPose.offset(-5.0F, 2.0F, 0.0F));

        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 16).mirror() // UV for base left arm (mirrored)
                        .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE),
                PartPose.offset(5.0F, 2.0F, 0.0F));

        partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16) // UV for base right leg
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE),
                PartPose.offset(-1.9F, 12.0F, 0.0F));

        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).mirror() // UV for base left leg (mirrored)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE),
                PartPose.offset(1.9F, 12.0F, 0.0F));

        // --- 外部覆盖层部件 ---
        // 头部覆盖层 (帽子) - 通常比头部大0.5F
        // 标准玩家皮肤帽子区域通常在 (32,0) 开始
        partdefinition.addOrReplaceChild("head_layer", CubeListBuilder.create().texOffs(32, 0) // UV for hat layer
                        .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), // 稍微大一点
                PartPose.offset(0.0F, 0.0F, 0.0F));

        // 身体覆盖层 (夹克/外套) - 通常比身体大0.25F
        // 标准玩家皮肤夹克区域通常在 (16,32) 开始
        partdefinition.addOrReplaceChild("body_layer", CubeListBuilder.create().texOffs(16, 32) // UV for jacket layer
                        .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), // 稍微大一点
                PartPose.offset(0.0F, 0.0F, 0.0F));

        // 右臂覆盖层 (袖子)
        // 标准玩家皮肤右袖子区域通常在 (40,32) 或 (48,48) - 取决于slim/classic，这里用 (40,32) 做例子
        partdefinition.addOrReplaceChild("right_arm_layer", CubeListBuilder.create().texOffs(40, 32) // UV for right sleeve
                        .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
                PartPose.offset(-5.0F, 2.0F, 0.0F));

        // 左臂覆盖层 (袖子)
        // 标准玩家皮肤左袖子区域通常在 (48,48) 或 (56,16) - 这里用 (48,48) 做例子 (注意：如果左右对称，也可以镜像texOffs(40,32))
        partdefinition.addOrReplaceChild("left_arm_layer", CubeListBuilder.create().texOffs(48, 48).mirror() // UV for left sleeve (mirrored)
                        .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
                PartPose.offset(5.0F, 2.0F, 0.0F));

        // 右腿覆盖层 (裤子外层)
        // 标准玩家皮肤右裤腿覆盖层区域通常在 (0,32)
        partdefinition.addOrReplaceChild("right_leg_layer", CubeListBuilder.create().texOffs(0, 32) // UV for right pant layer
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
                PartPose.offset(-1.9F, 12.0F, 0.0F));

        // 左腿覆盖层 (裤子外层)
        // 标准玩家皮肤左裤腿覆盖层区域通常在 (0,48)
        partdefinition.addOrReplaceChild("left_leg_layer", CubeListBuilder.create().texOffs(0, 48).mirror() // UV for left pant layer (mirrored)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
                PartPose.offset(1.9F, 12.0F, 0.0F));

        // 确保纹理尺寸与你的纹理图一致，如果你的纹理图包含这些新层，它仍然是 64x64
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // 头部转动
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = headPitch * ((float)Math.PI / 180F);

        // 简单的行走动画
        this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
        this.rightArm.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
        this.leftArm.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;

        // 如果实体正在攻击，可以添加攻击动画
        if (entity.isAggressive()) { // 或者使用 AttackTime 属性
            // 示例：右臂向上挥动
            this.rightArm.xRot = -2.0F + 1.5F * Mth.triangleWave((float)entity.attackAnim * 0.1f, 0.6F);
            // this.rightArm.yRot = 0.0F;
        }

        // 同步外部层的姿态 (让它们跟随基础部件)
        this.headLayer.copyFrom(this.head);
        this.bodyLayer.copyFrom(this.body);
        this.rightArmLayer.copyFrom(this.rightArm);
        this.leftArmLayer.copyFrom(this.leftArm);
        this.rightLegLayer.copyFrom(this.rightLeg);
        this.leftLegLayer.copyFrom(this.leftLeg);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        // 直接渲染根模型部分，它包含了所有子部分
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
