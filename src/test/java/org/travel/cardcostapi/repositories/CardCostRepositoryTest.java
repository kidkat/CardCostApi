package org.travel.cardcostapi.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.travel.cardcostapi.models.CardCost;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author asafronov
 */
@DataJpaTest(properties = "spring.profiles.active=test")
class CardCostRepositoryTest {
    @Autowired
    private CardCostRepository cardCostRepository;

    @BeforeEach
    void setUp() {
        cardCostRepository.deleteAll();

        cardCostRepository.save(new CardCost("US", 5.0, 1L));
        cardCostRepository.save(new CardCost("GR", 15.0, 1L));
        cardCostRepository.save(new CardCost("FR", 10.0, 1L));
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
    void findAllCardCosts() {
        List<CardCost> costs = cardCostRepository.findAll();

        assertEquals(3, costs.size());
    }

    @Test
    void findByIdExists() {
        CardCost cardCost = cardCostRepository.save(new CardCost("JP", 20.0));
        Optional<CardCost> result = cardCostRepository.findById(cardCost.getId());

        assertTrue(result.isPresent());
        assertEquals("JP", result.get().getCountry());
        assertEquals(20.0, result.get().getCost());
    }

    @Test
    void findByIdNotExists() {
        Optional<CardCost> result = cardCostRepository.findById(9999L);

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
    void updateCartCost() {
        Optional<CardCost> clearingCostOptional = cardCostRepository.findByCountry("FR");
        assertTrue(clearingCostOptional.isPresent());

        CardCost cardCost = clearingCostOptional.get();
        cardCost.setCost(25.0);
        CardCost updatedCost = cardCostRepository.save(cardCost);

        assertEquals(25.0, updatedCost.getCost());
    }

    @Test
    void deleteCardCost() {
        Optional<CardCost> cardCost = cardCostRepository.findByCountry("GR");
        assertTrue(cardCost.isPresent());

        cardCostRepository.delete(cardCost.get());

        Optional<CardCost> result = cardCostRepository.findByCountry("GR");
        assertFalse(result.isPresent());
    }
}