package com.group15.roborally.client.model.upgrade_cards.permanent;

import com.group15.roborally.client.controller.GameController;
import com.group15.roborally.client.model.Player;
import com.group15.roborally.client.model.EventHandler;
import com.group15.roborally.client.model.damage.DamageType;
import com.group15.roborally.client.model.upgrade_cards.UpgradeCardPermanent;
import com.group15.roborally.client.model.events.PlayerLaserHitListener;

public class Card_DoubleBarrelLaser extends UpgradeCardPermanent {

    public Card_DoubleBarrelLaser() {
        super("Double Barrel Laser", 2, 0, 0, null);
    }

    @Override
    public void initialize(Player owner, GameController gameController) {
        super.initialize(owner, gameController);

        // Defining effects on events

        // OnDamageDealt
        eventListeners.add(EventHandler.subscribe((PlayerLaserHitListener) (damage, playerTakingDamage) -> {
            if (owner != playerTakingDamage) {
                printUsage();
                // Modifying damage
                damage.addAmount(DamageType.SPAM, 1);
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
    public void onActivated() {

    }
}
