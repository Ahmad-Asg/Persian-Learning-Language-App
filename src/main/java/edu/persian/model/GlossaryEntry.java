package edu.persian.model;

public record GlossaryEntry(long id, long chapterId, String word, String meaning) {
    @Override public String toString() { return word + "  —  " + meaning; }
}
