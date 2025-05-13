package com.testmod.entity.item;

import com.testmod.client.screen.TacticalScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CommandTablet extends Item {
    public CommandTablet(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        // 我们只关心主手右键打开GUI的情况
        if (hand == InteractionHand.MAIN_HAND) {
            if (level.isClientSide()) { // 打开GUI的操作只在客户端进行
                // 打开我们的 TacticalScreen
                Minecraft.getInstance().setScreen(new TacticalScreen());
            }
            // 返回SUCCESS，表示物品使用成功（打开了GUI或触发了动作）
            // consume() 会消耗一个物品（如果可消耗），对于不可消耗品，用 success()
            return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
        }

        // 如果是副手，或者其他情况，可以返回PASS让其他逻辑处理，或者执行副手逻辑（如果未来有的话）
        // 根据我们之前的讨论，副手X键开轮盘，副手右键是快速指令，所以这里的use方法主要处理主手
        return InteractionResultHolder.pass(itemStack);
    }
}
