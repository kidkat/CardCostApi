package org.travel.cardcostapi.repositories;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import org.travel.cardcostapi.models.CardCost;
import java.util.Optional;

@Repository
public interface CardCostRepository extends JpaRepository<CardCost, Long> {

    @Lock(LockModeType.OPTIMISTIC)
    Optional<CardCost> findById(Long cardCostId);
    Optional<CardCost> findByCountry(String country);
}
