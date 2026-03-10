package com.clementcogo.mtgdeckassistant.repository;

import com.clementcogo.mtgdeckassistant.entities.DeckSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeckSlotRepository extends JpaRepository<DeckSlot, Long> {
    public Optional<DeckSlot> findByDeckIdAndCardName(Long deckId,String cardName);
}
