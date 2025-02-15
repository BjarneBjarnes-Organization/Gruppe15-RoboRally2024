package com.group15.roborally.client.model.upgrade_cards.permanent;

import com.group15.roborally.client.controller.GameController;
import com.group15.roborally.client.model.EventHandler;
import com.group15.roborally.client.model.Player;
import com.group15.roborally.client.model.damage.DamageType;
import com.group15.roborally.client.model.events.PlayerLaserHitListener;
import com.group15.roborally.client.model.upgrade_cards.UpgradeCardPermanent;

public class Card_BlueScreenOfDeath extends UpgradeCardPermanent {

    public Card_BlueScreenOfDeath() {
        super("Blue Screen of Death", 4, 0, 0, null);
    }

    @Override
    public void initialize(Player owner, GameController gameController) {
        super.initialize(owner, gameController);

        // Defining effects on events

        // OnDamageDealt
        eventListeners.add(EventHandler.subscribe((PlayerLaserHitListener) (damage, playerTakingDamage) -> {
            if (owner != playerTakingDamage) {
                if (damage.getAmount(DamageType.SPAM) > 0) {
                    printUsage();
                    // Modifying damage
                    damage.subtractAmount(DamageType.SPAM,1);
                    damage.addAmount(DamageType.WORM, 1);
                }
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
