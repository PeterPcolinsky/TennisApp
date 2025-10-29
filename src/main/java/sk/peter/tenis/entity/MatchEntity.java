package sk.peter.tenis.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "matches")
public class MatchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "player_a_id", nullable = false)
    private PlayerEntity playerA;

    @ManyToOne(optional = false)
    @JoinColumn(name = "player_b_id", nullable = false)
    private PlayerEntity playerB;

    @Column(nullable = false)
    private String result;       // napr. "6:4, 6:2"

    @Column(nullable = false)
    private LocalDate date;

    // --- Constructors ---
    public MatchEntity() {
    }

    public MatchEntity(PlayerEntity playerA, PlayerEntity playerB, String result, LocalDate date) {
        this.playerA = playerA;
        this.playerB = playerB;
        this.result = result;
        this.date = date;
    }

    // --- Getters & Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PlayerEntity getPlayerA() {
        return playerA;
    }

    public void setPlayerA(PlayerEntity playerA) {
        this.playerA = playerA;
    }

    public PlayerEntity getPlayerB() {
        return playerB;
    }

    public void setPlayerB(PlayerEntity playerB) {
        this.playerB = playerB;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}