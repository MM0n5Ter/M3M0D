package com.testmod.entity.operator;

import com.testmod.entity.operator.ai.FollowPlayerGoal;
import com.testmod.init.ModItems;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SampleGuardOperatorEntity extends OperatorBaseEntity {

    // AI Goals 实例
    private FollowPlayerGoal customFollowGoal; // 自定义的跟随玩家Goal
    private WaterAvoidingRandomStrollGoal randomStrollGoal;
    private LookAtPlayerGoal lookAtPlayerGoal; // 空闲时看玩家
    private MeleeAttackGoal meleeAttackGoal; // 攻击目标

    public SampleGuardOperatorEntity(EntityType<? extends OperatorBaseEntity> entityType, Level level) {
        super(entityType, level);
        this.setSpecialization(OperatorSpecialization.GUARD);
        this.setBlockCount(2);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return OperatorBaseEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 60.0D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D) // 攻击伤害将直接被MeleeAttackGoal使用
                .add(Attributes.ARMOR, 2.0D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.customFollowGoal = new FollowPlayerGoal(this, 1.1D, 5.0F, 2.0F);
        this.randomStrollGoal = new WaterAvoidingRandomStrollGoal(this, 0.1D) {
            @Override
            public boolean canUse() {
                return SampleGuardOperatorEntity.this.getCommandState() != CommandState.FOLLOW && super.canUse();
            }
        };
        this.lookAtPlayerGoal = new LookAtPlayerGoal(this, Player.class, 6.0F) {
            @Override
            public boolean canUse() {
                return SampleGuardOperatorEntity.this.getCommandState() == CommandState.IDLE && super.canUse();
            }
        };
        this.meleeAttackGoal = new MeleeAttackGoal(this, 1.5D, false);

        this.goalSelector.addGoal(1, this.meleeAttackGoal); // 近战攻击AI
        this.goalSelector.addGoal(2, this.customFollowGoal);  // 自定义跟随AI
        this.goalSelector.addGoal(5, this.randomStrollGoal);  // 待命时的随机巡逻
        this.goalSelector.addGoal(6, this.lookAtPlayerGoal); // 待命时看玩家
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this)); // 总是随机看看周围


        // 3. 添加索敌AI
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Monster.class, true));
    }

}
