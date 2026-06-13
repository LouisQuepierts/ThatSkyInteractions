package net.quepierts.thatskyinteractions.feature.friendship;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.quepierts.thatskyinteractions.core.model.Currency;

@UtilityClass
public class CurrencyHelper {

    public static int getBalance(
            final @NonNull  Player      player,
            final @NonNull  Currency    currency
    ) {
        final var item = currency == Currency.WHITE_CANDLE ? Items.CANDLE : Items.RED_CANDLE;
        return player.getInventory().countItem(item);
    }

    public static void consume(
            final @NonNull  Player      player,
            final @NonNull  Currency    currency,
            final           int         amount
    ) {
        final var item      = currency == Currency.WHITE_CANDLE ? Items.CANDLE : Items.RED_CANDLE;
        final var inventory = player.getInventory();

        int remain          = amount;
        for (final var stack : inventory) {
            if (!stack.is(item)) {
                continue;
            }

            final var count = stack.getCount();
            if (count > remain) {
                stack.shrink(remain);
                break;
            } else {
                stack.shrink(count);
                remain -= count;
            }

            if (remain == 0) {
                break;
            }
        }
    }

    public static @NonNull Item getCurrencyItem(
            final @NonNull  Currency    currency
    ) {
        return currency == Currency.WHITE_CANDLE ? Items.CANDLE : Items.RED_CANDLE;
    }

}
