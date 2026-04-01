package com.clementcogo.mtgdeckassistant.dto.response;

public class CommanderResponse {
    Long deckId;
    String commanderName;
    boolean isSet;

    public CommanderResponse(Long deckId) {
        this.deckId = deckId;
        this.isSet = false;
    }

    public CommanderResponse(Long deckId, String commanderName, boolean isSet) {
        this.deckId = deckId;
        this.commanderName = commanderName;
        this.isSet = isSet;
    }

    public Long getDeckId() {
        return deckId;
    }

    public void setDeckId(Long deckId) {
        this.deckId = deckId;
    }

    public String getCommanderName() {
        return commanderName;
    }

    public void setCommanderName(String commanderName) {
        this.commanderName = commanderName;
    }

    public boolean isSet() {
        return isSet;
    }

    public void setSet(boolean set) {
        isSet = set;
    }
}
