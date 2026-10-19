package edu.persian.service;

final class Rules {
    private Rules() { }
    static boolean blank(String value) {
        return value == null || value.codePoints().allMatch(c -> Character.isWhitespace(c) || Character.isSpaceChar(c));
    }
    static String required(String value, String field) {
        if (blank(value)) throw new ValidationException(field + " is required. · یہ خانہ پُر کریں۔");
        return value;
    }
    static void found(boolean found) {
        if (!found) throw new ValidationException("Record was not found. Reopen the chapter. · ریکارڈ نہیں ملا، باب دوبارہ کھولیں۔");
    }
}
