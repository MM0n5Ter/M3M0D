package com.testmod.entity.operator.ai;

import com.testmod.entity.operator.OperatorBaseEntity;
import com.testmod.entity.operator.SampleGuardOperatorEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.BlockPathTypes; // 用于设置水域寻路惩罚
import java.util.EnumSet;

public class FollowPlayerGoal extends Goal {
    private final OperatorBaseEntity operator;
    private Player commanderToFollow;
    private final double speedModifier;
    private final float stopDistanceSq;
    private final float maxDistanceToEngageSq; // 超过这个距离才开始寻路
    private int timeToRecalcPath;
    private float oldWaterCost;

    public FollowPlayerGoal(OperatorBaseEntity operator, double speedModifier, float stopDistance, float maxDistanceToEngage) {
        this.operator = operator;
        this.speedModifier = speedModifier;
        this.stopDistanceSq = stopDistance * stopDistance;
        this.maxDistanceToEngageSq = maxDistanceToEngage * maxDistanceToEngage;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.operator.getCommandState() != OperatorBaseEntity.CommandState.FOLLOW) {
            return false; // 没有被命令跟随
        }
        Player commander = this.operator.getCommander();
        if (commander == null || commander.isSpectator() || !commander.isAlive()) {
            return false; // 指挥官无效
        }
        // 如果与指挥官的距离小于停止距离，则不激活
        // 或者如果距离大于一个非常大的值（例如追踪范围的平方），也可能不激活，除非实体有传送能力
        if (this.operator.distanceToSqr(commander) < this.stopDistanceSq) {
            return false;
        }
        this.commanderToFollow = commander; // 确定要跟随的目标
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.commanderToFollow == null || !this.commanderToFollow.isAlive() || this.commanderToFollow.isSpectator()) {
            return false; // 指挥官失效
        }
        if (this.operator.getCommandState() != OperatorBaseEntity.CommandState.FOLLOW) {
            // 如果中途被命令停止跟随 (并且是指挥官是当前跟随目标的情况)
            // 这里的检查可以更严谨，比如检查UUID是否匹配
            return false;
        }
        if (this.operator.getNavigation().isDone()) {
            // 寻路完成，但如果还不够近，canUse会重新触发；如果够近了，这个会返回false
            return this.operator.distanceToSqr(this.commanderToFollow) > this.stopDistanceSq;
        }
        // 确保仍在停止距离之外
        return this.operator.distanceToSqr(this.commanderToFollow) > this.stopDistanceSq;
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.operator.getPathfindingMalus(BlockPathTypes.WATER);
        this.operator.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    }

    @Override
    public void stop() {
        this.commanderToFollow = null; // 清除指挥官引用
        this.operator.getNavigation().stop();
        this.operator.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
    }

    @Override
    public void tick() {
        if (this.commanderToFollow == null) return;

        this.operator.getLookControl().setLookAt(this.commanderToFollow, 10.0F, (float) this.operator.getMaxHeadXRot());
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            if (this.operator.distanceToSqr(this.commanderToFollow) >= this.maxDistanceToEngageSq) { // 只有当距离大于一定值才尝试寻路
                this.operator.getNavigation().moveTo(this.commanderToFollow, this.speedModifier);
            } else {
                this.operator.getNavigation().stop(); // 如果在最大接触距离内但在停止距离外，可能不需要主动寻路，或者进行更近的调整
            }
        }
    }
}
