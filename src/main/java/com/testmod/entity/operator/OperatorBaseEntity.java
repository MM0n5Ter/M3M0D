package com.testmod.entity.operator;

import com.testmod.ExampleMod;
import com.testmod.init.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public abstract class OperatorBaseEntity extends PathfinderMob {

    public enum CommandState {
        IDLE,   //待命
        FOLLOW  //跟随
    }

    // --- 同步数据访问器 ---
    private static final EntityDataAccessor<Integer> DATA_COMMAND_STATE_ID =
            SynchedEntityData.defineId(OperatorBaseEntity.class, EntityDataSerializers.INT); // 用整数存储枚举序数
    private static final EntityDataAccessor<Optional<UUID>> DATA_COMMANDER_UUID =
            SynchedEntityData.defineId(OperatorBaseEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    // --- 干员基础属性 ---
    protected OperatorSpecialization specialization;
    protected int blockCount;

    protected OperatorBaseEntity(EntityType<? extends OperatorBaseEntity> entityType, Level world) {
        super(entityType, world);
        this.specialization = OperatorSpecialization.NONE;
        this.blockCount = 1;
    }

    // --- 同步数据初始化 ---
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_COMMAND_STATE_ID, CommandState.IDLE.ordinal()); // 默认待命
        this.entityData.define(DATA_COMMANDER_UUID, Optional.empty());
    }

    // --- 属性注册 ---
    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D) // 默认最大生命值 (子类应具体设置)
                .add(Attributes.MOVEMENT_SPEED, 0.25D) // 默认移动速度
                .add(Attributes.FOLLOW_RANGE, 16.0D) // AI索敌
                .add(Attributes.ATTACK_DAMAGE, 1.0D); // 默认攻击力 (对于不攻击的干员，子类可以设为0或不依赖此属性)
    }

    // --- 基础AI目标 ---
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this)); // 确保干员不会淹死
        // 子类将在这里添加更多goal，例如 LookAtPlayerGoal, RandomLookAroundGoal, MeleeAttackGoal, RangedAttackGoal等
    }

    public CommandState getCommandState() {
        return CommandState.values()[this.entityData.get(DATA_COMMAND_STATE_ID)];
    }

    public void setCommandState(CommandState state) {
        this.entityData.set(DATA_COMMAND_STATE_ID, state.ordinal());
    }

    public Optional<UUID> getCommanderUUID() {
        return this.entityData.get(DATA_COMMANDER_UUID);
    }

    public void setCommanderUUID(@Nullable UUID commanderUUID) {
        this.entityData.set(DATA_COMMANDER_UUID, Optional.ofNullable(commanderUUID));
    }

    @Nullable
    public Player getCommander() {
        return getCommanderUUID()
                .map(uuid -> this.level().getPlayerByUUID(uuid)) // level() 是 Entity 类的方法
                .orElse(null);
    }

    // --- 核心指挥逻辑 ---
    public void toggleCommandState(Player commandingPlayer) {
        if (this.level().isClientSide()) {
            return; // 逻辑只在服务器端处理
        }

        CommandState currentState = getCommandState();
        Optional<UUID> currentCommanderOpt = getCommanderUUID();

        if (currentState == CommandState.FOLLOW && currentCommanderOpt.isPresent() && currentCommanderOpt.get().equals(commandingPlayer.getUUID())) {
            // 当前正在跟从此玩家 -> 切换到待命
            this.setCommandState(CommandState.IDLE);
            this.setCommanderUUID(null);
            if (commandingPlayer instanceof ServerPlayer sp) {
                sp.sendSystemMessage(Component.literal(this.getName().getString() + " 进入待命模式。"));
            }
            ExampleMod.LOGGER.info("Operator {} set to IDLE by {}", this.getName().getString(), commandingPlayer.getName().getString());
        } else {
            // 当前是待命，或者是跟随其他玩家 -> 切换到跟从此玩家
            this.setCommandState(CommandState.FOLLOW);
            this.setCommanderUUID(commandingPlayer.getUUID());
            if (commandingPlayer instanceof ServerPlayer sp) {
                sp.sendSystemMessage(Component.literal(this.getName().getString() + " 开始跟随您。"));
            }
            ExampleMod.LOGGER.info("Operator {} set to FOLLOW {} by {}", this.getName().getString(), commandingPlayer.getName().getString(), commandingPlayer.getName().getString());
        }
        // AI Goals 应该在其 canUse() 方法中检查这些状态，从而自行决定是否激活。
        // 一般不需要手动刷新AI，除非你有非常特殊的逻辑。
    }

    // --- 物品交互 ---
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        // 确保 ModItems.DEBUG_STICK 已经被正确初始化
        if (ModItems.DEBUG_STICK != null && itemStack.getItem() == ModItems.DEBUG_STICK.get()) {
            if (!this.level().isClientSide()) {
                this.toggleCommandState(player);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }
        return super.mobInteract(player, hand);
    }

    // --- NBT 数据持久化 ---
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("CommandState", this.getCommandState().ordinal());
        this.getCommanderUUID().ifPresent(uuid -> compound.putUUID("CommanderUUID", uuid));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setCommandState(CommandState.values()[compound.getInt("CommandState")]);
        if (compound.hasUUID("CommanderUUID")) {
            this.setCommanderUUID(compound.getUUID("CommanderUUID"));
        } else {
            this.setCommanderUUID(null);
        }
    }

    public OperatorSpecialization getSpecialization() {
        return specialization;
    }

    protected void setSpecialization(OperatorSpecialization specialization) {
        this.specialization = specialization;
    }

    public int getBlockCount() {
        return blockCount;
    }

    protected void setBlockCount(int blockCount) {
        this.blockCount = blockCount;
    }

}
