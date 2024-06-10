package com.gruppe15.model.upgrade_cards.permanent;

import com.gruppe15.controller.GameController;
import com.gruppe15.model.*;
import com.gruppe15.model.boardelements.BE_Hole;
import com.gruppe15.model.events.PlayerEndOfActionListener;
import com.gruppe15.model.upgrade_cards.UpgradeCardPermanent;
import javafx.util.Pair;

public class Card_HoverUnit extends UpgradeCardPermanent {

    public Card_HoverUnit() {
        super("Hover Unit", 1, 0, 0, null);
    }

    @Override
    public void initialize(Player owner, GameController gameController) {
        super.initialize(owner, gameController);
        // Defining effects on events
        // OnDamageDealt
        eventListeners.add(EventHandler.subscribe((PlayerEndOfActionListener) (space, shouldReboot) -> {
            if (space != null) {
                if (space.getBoardElement() != null && space.getBoardElement() instanceof BE_Hole) {
                    Velocity playerVel = owner.getVelocity();
                    if ((Math.abs(playerVel.forward) + Math.abs(playerVel.right)) > 0) {
                        printUsage();
                        shouldReboot = false;
                    }
                }
            }
            return shouldReboot;
        }, owner));
    }

    @Override
    protected void onEnabled() {

    }

    @Override
    protected void onDisabled() {

    }

    @Override
    protected void onActivated() {

    }
}