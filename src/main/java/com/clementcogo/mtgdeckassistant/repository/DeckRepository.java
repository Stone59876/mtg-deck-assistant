package com.clementcogo.mtgdeckassistant.repository;

import com.clementcogo.mtgdeckassistant.entities.Deck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeckRepository extends JpaRepository<Deck, Long> {
}
