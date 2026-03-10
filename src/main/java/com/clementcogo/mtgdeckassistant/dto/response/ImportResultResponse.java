package com.clementcogo.mtgdeckassistant.dto.response;

public class ImportResultResponse {
    Long deckId;
    int addedSlots;
    int updatedSlots;
    int ignoredLines;
    int invalidLines;
    int duplicateLines;

    public ImportResultResponse() {

    }

    public ImportResultResponse(Long deckId, int addedSlots, int updatedSlots, int ignoredLines, int invalidLines, int duplicateLines) {
        this.deckId = deckId;
        this.addedSlots = addedSlots;
        this.updatedSlots = updatedSlots;
        this.ignoredLines = ignoredLines;
        this.invalidLines = invalidLines;
        this.duplicateLines = duplicateLines;
    }

    public ImportResultResponse(Long deckId,int ignoredLines,int invalidLines) {
        this.deckId = deckId;
        this.ignoredLines = ignoredLines;
        this.invalidLines = invalidLines;
    }

    public Long getDeckId() {
        return deckId;
    }

    public void setDeckId(Long deckId) {
        this.deckId = deckId;
    }

    public int getAddedSlots() {
        return addedSlots;
    }

    public void setAddedSlots(int addedSlots) {
        this.addedSlots = addedSlots;
    }

    public void incrementAddedSlots() { addedSlots++;}

    public int getUpdatedSlots() {
        return updatedSlots;
    }

    public void setUpdatedSlots(int updatedSlots) {
        this.updatedSlots = updatedSlots;
    }

    public void incrementUpdatedSlots() { updatedSlots++;}

    public int getIgnoredLines() {
        return ignoredLines;
    }

    public void setIgnoredLines(int ignoredLines) {
        this.ignoredLines = ignoredLines;
    }

    public void incrementIgnoredLine() { ignoredLines++;}

    public int getInvalidLine() {
        return invalidLines;
    }

    public void setInvalidLine(int invalidLine) {
        this.invalidLines = invalidLine;
    }

    public void incrementInvalidLine() { invalidLines++;}

    public void incrementDuplicateLines() {duplicateLines++;}
}
