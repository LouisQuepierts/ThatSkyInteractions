package net.quepierts.thatskyinteractions;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class PlayerUtils {
    public static void costItems(Player player, Item item, int amount) {
        Inventory inventory = player.getInventory();

        int remain = amount;
        for (ItemStack stack : inventory.items) {
            if (!stack.is(item)) {
                continue;
            }

            int count = stack.getCount();
            if (count > remain) {
                stack.shrink(remain);
                break;
            } else {
                remain -= count;
                stack.shrink(count);
            }

            if (remain < 1) {
                break;
            }
        }
    }
}
