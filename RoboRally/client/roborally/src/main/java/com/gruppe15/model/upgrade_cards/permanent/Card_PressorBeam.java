package com.gruppe15.model.upgrade_cards.permanent;

import com.gruppe15.controller.GameController;
import com.gruppe15.model.EventHandler;
import com.gruppe15.model.Heading;
import com.gruppe15.model.Player;
import com.gruppe15.model.Space;
import com.gruppe15.model.events.PlayerLaserHitListener;
import com.gruppe15.model.upgrade_cards.UpgradeCardPermanent;

import java.util.ArrayList;
import java.util.List;

public class Card_PressorBeam extends UpgradeCardPermanent {

    public Card_PressorBeam() {
        super("Pressor Beam", 3, 0, 0, null);
    }

    @Override
    public void initialize(Player owner, GameController gameController) {
        super.initialize(owner, gameController);

        // Defining effects on events

        // OnDamageDealt
        eventListeners.add(EventHandler.subscribe((PlayerLaserHitListener) (damage, playerTakingDamage) -> {
            if (owner != playerTakingDamage) {
                // pushDirection calculated for compatibility with e.g. "REAR LASER" UpgradeCardPermanent.
                List<Player> playerToPush = new ArrayList<>();
                playerToPush.add(playerTakingDamage);
                Heading pushDirection;
                Space ownerSpace = owner.getSpace();
                Space targetSpace = playerTakingDamage.getSpace();
                if (ownerSpace.y > targetSpace.y) {
                    pushDirection = Heading.NORTH;
                } else if (ownerSpace.y < targetSpace.y) {
                    pushDirection = Heading.SOUTH;
                } else if (ownerSpace.x < targetSpace.x) {
                    pushDirection = Heading.EAST;
                } else {
                    pushDirection = Heading.WEST;
                }

                EventHandler.event_PlayerPush(owner.board.getSpaces(), owner, playerToPush, pushDirection);
            }
            return damage;
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