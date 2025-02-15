package com.group15.roborally.server.controller;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.group15.roborally.server.model.Choice;
import com.group15.roborally.server.repository.ChoiceRepository;
import com.group15.roborally.server.repository.GameRepository;

@RestController
@RequestMapping("/choices")

public class ChoiceController {
    ChoiceRepository choiceRepository;
    GameRepository gameRepository;

    public ChoiceController(ChoiceRepository choiceRepository, GameRepository gameRepository) {
        this.choiceRepository = choiceRepository;
        this.gameRepository = gameRepository;
    }

    @PostMapping(value = "/{playerId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateChoice(@RequestBody List<Choice> choices){
        for(Choice choice : choices){
            System.out.println("Inserting choice: " + choice.getChoice());
            choiceRepository.save(choice);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Choice>> getChoices(@PathVariable("gameId") long gameId, @RequestParam("turn") int turn, @RequestParam("movement") int movement){
        int nrOfPlayers = gameRepository.findById(gameId).orElse(null).getNrOfPlayers();
        int nrOfPlayerInput = choiceRepository.countDistinctByGameIdAndTurnAndMovement(gameId, turn, movement);
        System.out.println("\n\n\nTurn: " + turn + "  | move: " + movement + "\nNr of players: " + nrOfPlayers + "\nNr of player input: " + nrOfPlayerInput);
        System.out.println("\n\n\n");
        if (nrOfPlayerInput != nrOfPlayers){
            return ResponseEntity.ok(null);
        }
        List<Choice> choices = choiceRepository.findAllByGameIdAndTurnAndMovement(gameId, turn, movement);
        if(choices.isEmpty()){
            return ResponseEntity.notFound().build();
        } else{
            return ResponseEntity.ok(choices);
        }

    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Choice>> getAllChoices(){
        return ResponseEntity.ok(choiceRepository.findAll());
    }
}
