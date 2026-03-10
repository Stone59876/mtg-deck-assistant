package com.clementcogo.mtgdeckassistant.util;

import com.clementcogo.mtgdeckassistant.entities.DeckSlot;

import java.util.ArrayList;
import java.util.List;

public class DecklistParseResult {
    private List<DeckSlot> slots;
    private int ignoredLines;
    private int invalidLines;

    public DecklistParseResult(){
        this.slots = new ArrayList<>();
        this.ignoredLines = 0;
        this.invalidLines = 0;
    }

    public List<DeckSlot> getSlots() {
        return slots;
    }

    public void addSlot(DeckSlot deckslot){
        this.slots.add(deckslot);
    }

    public int getIgnoredLines() {
        return ignoredLines;
    }

    public void incrementIgnoredLines(){
        ignoredLines++;
    }

    public int getInvalidLines() {
        return invalidLines;
    }

    public void incrementInvalidLines(){
        invalidLines++;
    }
}
