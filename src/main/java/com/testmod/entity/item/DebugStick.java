package com.testmod.entity.item;

import com.testmod.entity.operator.SampleGuardOperatorEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class DebugStick extends Item {
    public DebugStick(Properties props) {
        super(props);
    }
}
