package org.travel.cardcostapi.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.travel.cardcostapi.models.CardCost;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author asafronov
 */
@DataJpaTest
class CardCostRepositoryTest {
    @Autowired
    private CardCostRepository cardCostRepository;

    @BeforeEach
    void setUp() {
        // Clean up the repository before each test
        cardCostRepository.deleteAll();

        // Insert sample data
        cardCostRepository.save(new CardCost(1L, "US", 5.0, 1L));
        cardCostRepository.save(new CardCost(2L, "GR", 15.0, 1L));
        cardCostRepository.save(new CardCost(3L, "FR", 10.0, 1L));
    }

    @Test
    void findAllCardCosts() {
        List<CardCost> costs = cardCostRepository.findAll();

        assertEquals(3, costs.size());
    }

    @Test
    void findByCountryExists() {
        Optional<CardCost> result = cardCostRepository.findByCountry("US");

        assertTrue(result.isPresent());
        assertEquals("US", result.get().getCountry());
        assertEquals(5.0, result.get().getCost());
    }

    @Test
    void findByCountryNotExists() {
        Optional<CardCost> result = cardCostRepository.findByCountry("IN");

        assertFalse(result.isPresent());
    }

    @Test
    void saveCardCost() {
        CardCost newCardCost = new CardCost( "JP", 20.0);
        CardCost savedCost = cardCostRepository.save(newCardCost);

        assertNotNull(savedCost.getId());
        assertEquals("JP", savedCost.getCountry());
        assertEquals(20.0, savedCost.getCost());
    }

    @Test
    void deleteCardCost() {
        Optional<CardCost> cardCost = cardCostRepository.findByCountry("GR");
        assertTrue(cardCost.isPresent());

        cardCostRepository.delete(cardCost.get());

        Optional<CardCost> result = cardCostRepository.findByCountry("GR");
        assertFalse(result.isPresent());
    }

    @Test
    void updateCartCost() {
        Optional<CardCost> clearingCostOptional = cardCostRepository.findByCountry("FR");
        assertTrue(clearingCostOptional.isPresent());

        CardCost cardCost = clearingCostOptional.get();
        cardCost.setCost(25.0);
        CardCost updatedCost = cardCostRepository.save(cardCost);

        assertEquals(25.0, updatedCost.getCost());
    }


}