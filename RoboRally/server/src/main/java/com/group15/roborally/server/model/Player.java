package com.group15.roborally.server.model;

import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "players")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Player/*  implements Serializable */ {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long playerId;

    private long gameId;
    private String robotName;
    private String playerName;
    private int[] spawnPoint;
    private String spawnDirection;
    private int isReady;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "games_gameId", insertable = false, updatable = false)
    private Game game;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL)
    private List<Register> registers;

    /**
     * Compares two player objects.
     * @param player The player at another point. Must have same playerId.
     * @return Whether the player has had any variables changed.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public boolean hasChanged(Player player) {
        return  this.playerId != player.playerId ||
                ((this.robotName != null || player.robotName != null) && !Objects.equals(this.robotName, player.robotName)) ||
                ((this.playerName != null || player.playerName != null) && !Objects.equals(this.playerName, player.playerName)) ||
                 this.isReady != player.isReady;
    }
}