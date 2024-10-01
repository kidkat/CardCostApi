package org.travel.cardcostapi.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardCost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String country;
    @Column(nullable = false)
    private Double cost;
    @Version
    private Long version;

    public CardCost(String country, Double cost) {
        this.country = country;
        this.cost = cost;
    }

    public CardCost(long id, String country, double cost) {
        this.id = id;
        this.country = country;
        this.cost = cost;
    }

    public CardCost(String country, double cost, Long version) {
        this.country = country;
        this.cost = cost;
        this.version = version;
    }
}
