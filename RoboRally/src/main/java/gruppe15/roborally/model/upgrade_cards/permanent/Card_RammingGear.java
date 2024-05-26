package gruppe15.roborally.model.upgrade_cards.permanent;

import gruppe15.roborally.controller.GameController;
import gruppe15.roborally.model.EventHandler;
import gruppe15.roborally.model.Player;
import gruppe15.roborally.model.damage.Damage;
import gruppe15.roborally.model.damage.DamageTypes;
import gruppe15.roborally.model.events.PlayerDamageListener;
import gruppe15.roborally.model.events.PlayerPushListener;
import gruppe15.roborally.model.upgrade_cards.UpgradeCardPermanent;

public class Card_RammingGear  extends UpgradeCardPermanent {

    public Card_RammingGear() {
        super("Ramming Gear", 2, 0, 0, null);
    }

    @Override
    public void initialize(Player owner, GameController gameController) {
        super.initialize(owner, gameController);

        // Defining effects on events

        // OnDamageDealt
        eventListeners.add(EventHandler.subscribe((PlayerPushListener) (playerPushing, playerToPush) -> {
            if (owner == playerPushing) {
                System.out.println("Player: \"" + owner.getName() + "\" used UpgradeCard: \"" + title + "\".");
                EventHandler.event_PlayerDamage(playerToPush, owner, new Damage(1, 0, 0, 0), owner.board.getBoardActionQueue());
            }
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