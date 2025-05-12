package com.testmod.entity.operator.ai;

import com.testmod.entity.operator.OperatorBaseEntity;
import net.minecraft.world.entity.LivingEntity;

public interface IOperatorAttackStrategy {

    /**
     * 执行攻击逻辑。
     * @param attacker 发动攻击的干员
     * @param target 攻击的目标 (可能为null，例如某些范围效果或非目标性治疗)
     */
    void executeAttack(OperatorBaseEntity attacker, @javax.annotation.Nullable LivingEntity target);

    /**
     * 获取此攻击策略的有效攻击范围。
     * @param attacker 发动攻击的干员
     * @return 攻击范围 (格)
     */
    float getAttackRange(OperatorBaseEntity attacker);

    /**
     * 获取此攻击策略的攻击间隔。
     * @param attacker 发动攻击的干员
     * @return 攻击间隔 (ticks)，20 ticks = 1 秒
     */
    int getAttackIntervalTicks(OperatorBaseEntity attacker);



}
