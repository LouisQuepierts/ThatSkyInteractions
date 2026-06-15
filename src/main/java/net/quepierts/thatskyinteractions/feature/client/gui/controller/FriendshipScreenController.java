package net.quepierts.thatskyinteractions.feature.client.gui.controller;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.quepierts.thatskyinteractions.feature.client.ClientPlayerFriendshipSystem;
import net.quepierts.thatskyinteractions.feature.client.gui.ScreenLoader;
import net.quepierts.thatskyinteractions.feature.client.gui.screen.ConfirmScreen;
import net.quepierts.thatskyinteractions.feature.friendship.CurrencyHelper;
import net.quepierts.thatskyinteractions.feature.friendship.FriendshipTreeData;
import net.quepierts.thatskyinteractions.feature.friendship.PlayerFriendshipSystem;
import net.quepierts.thatskyinteractions.feature.friendship.behaviour.FriendshipBehaviourFactory;
import net.quepierts.thatskyinteractions.feature.gui.ConfirmData;

public final class FriendshipScreenController extends ScreenController<FriendshipTreeData> {

    public FriendshipScreenController(final FriendshipTreeData model) {
        super(model);
    }

    private int clicked = -1;
    private int clicks  = 0;

    @SuppressWarnings("DataFlowIssue")
    public void onButtonClicked(final int index) {

        if (PlayerFriendshipSystem.isFriendshipConditional()) {

            final var model = this.getModel();
            final var structure = model.getStructure();

            final var state = model.getState(index);
            final var node = structure.get(index);

            switch (state) {
                case UNLOCKABLE: {

                    final var cost = node.getCost();
                    final var balance = CurrencyHelper.getBalance(Minecraft.getInstance().player, cost.currency());
                    if (balance < cost.amount()) {
                        return;
                    }

                    if (this.clicked != index) {
                        this.clicked = index;
                        this.clicks = 1;
                    } else {
                        this.clicks++;

                        if (this.clicks >= Math.min(3, cost.amount())) {
                            this.unlock(index);
                            this.clicked = -1;
                            this.clicks = 0;
                        }
                    }

                    break;
                }
                case UNLOCKED: {

                    this.interact(index);

                    break;
                }
            }
        } else {
            this.interact(index);
        }
    }

    @SuppressWarnings("DataFlowIssue")
    private void unlock(final int index) {
        final var model         = this.getModel();
        final var node          = model.getStructure().get(index);

        final var behaviour     = FriendshipBehaviourFactory.get(node);

        if (behaviour == null) {
            return; // normally, this should never happen
        }

        final var state         = model.getState(index);
        final var icon          = behaviour.getIcon(Minecraft.getInstance().player, node, state);

        final var confirm = new ConfirmData(
                icon,
                new Component[] { behaviour.getUnlockMessage(node) },
                () -> ClientPlayerFriendshipSystem.unlockFriendshipNode(model, index),
                null
        );

        ScreenLoader.open(ConfirmScreen.class, confirm);

    }

    private void interact(final int index) {
        ClientPlayerFriendshipSystem.interactFriendshipNode(this.getModel(), index);
    }
}
