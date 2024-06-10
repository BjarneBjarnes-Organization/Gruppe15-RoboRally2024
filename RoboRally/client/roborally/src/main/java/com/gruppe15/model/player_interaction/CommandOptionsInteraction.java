package com.gruppe15.model.player_interaction;

import com.gruppe15.controller.GameController;
import com.gruppe15.model.Command;
import com.gruppe15.model.Player;

import java.util.List;

public class CommandOptionsInteraction extends PlayerInteraction {
    private final List<Command> options;

    /**
     * @param player The player that has an interaction.
     * @param options The options the player can choose from.
     */
    public CommandOptionsInteraction(GameController gameController, Player player, List<Command> options) {
        super(gameController, player);
        this.options = options;
    }
    public List<Command> getOptions() {
        return options;
    }

    @Override
    public void initializeInteraction() {

    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("CommandOptionsInteraction - ");
        str.append("Player: \"").append(player.getName()).append("\"");
        str.append(", Options: \"").append(options.toString()).append("\"");
        str.append(".");
        return str.toString();
    }
}