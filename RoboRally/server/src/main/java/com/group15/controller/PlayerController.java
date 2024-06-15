package com.group15.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group15.model.Game;
import com.group15.model.Player;
import com.group15.repository.GameRepository;
import com.group15.repository.PlayerRepository;

@RestController
@RequestMapping("/players")
public class PlayerController {

    PlayerRepository playerRepository;
    GameRepository gameRepository;

    public PlayerController(PlayerRepository playerRepository, GameRepository gameRepository) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
    }

    /**
     * Endpoint to create a new player and insert it into the database in 'Players' table
     * 
     * @author  Marcus Rémi Lemser Eychenne, s230985
     * 
     * @param playerName - the name of the player to be created
     * 
     * @return ResponseEntity<Long> - the generated id of the player created
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> createPlayer(@RequestBody String playerName) {
        Player player = new Player();
        player.setPlayerName(playerName);
        playerRepository.save(player);
        return ResponseEntity.ok(player.getPlayerId());
    }

    /**
     * Endpoint to update a player in the database in 'Players' table
     * 
     * @author Marcus Rémi Lemser Eychenne, s230985
     * 
     * @param player - the player to be updated
     * @param playerId - the id of the player to be updated
     * 
     * @return ResponseEntity<String> - a message indicating the success of the operation
     */
    @PutMapping(value = "/{playerId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updatePlayer(@RequestBody Player player, @PathVariable("playerId") Long playerId) {
        if (!playerRepository.existsById(playerId)) {
            return ResponseEntity.badRequest().build();
        }
        playerRepository.save(player);
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint to delete a player in the database in 'Players' table
     * 
     * @author Marcus Rémi Lemser Eychenne, s230985
     * 
     * @param playerId - the id of the player to be deleted
     * 
     * @return ResponseEntity<String> - a message indicating the success of the operation
     */

    @DeleteMapping(value = "/{playerId}")
    public ResponseEntity<String> deletePlayer(@PathVariable("playerId") Long playerId) {
        if (!playerRepository.existsById(playerId)) {
            return ResponseEntity.badRequest().build();
        }
        Long gameId = playerRepository.findById(playerId).orElse(null).getGameId();
        playerRepository.deleteById(playerId);

        //Update the number of players in the game
        if(gameId != null){
            Game game = gameRepository.findById(gameId).orElse(null);
            game.setNrOfPlayers(game.getNrOfPlayers() - 1);
            gameRepository.save(game);
        }
        return ResponseEntity.ok().build();
    }
}