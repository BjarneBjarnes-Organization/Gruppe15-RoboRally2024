package com.group15.roborally.client.model.upgrade_cards.temporary;

import com.group15.roborally.client.controller.GameController;
import com.group15.roborally.client.model.EventHandler;
import com.group15.roborally.client.model.Phase;
import com.group15.roborally.client.model.Player;
import com.group15.roborally.client.model.upgrade_cards.UpgradeCardTemporary;

public class Card_Reboot extends UpgradeCardTemporary {

    public Card_Reboot() {
        super("Reboot", 1, 0, 1, null, Phase.PLAYER_ACTIVATION, Phase.BOARD_ACTIVATION, Phase.PROGRAMMING);
    }

    @Override
    public void initialize(Player owner, GameController gameController) {
        super.initialize(owner, gameController);
    }

    @Override
    protected void onEnabled() {

    }

    @Override
    protected void onDisabled() {

    }

    @Override
    protected void onActivated() {
        EventHandler.event_PlayerReboot(owner, false, gameController);
        super.onActivated();
    }
}