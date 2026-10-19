package edu.persian.repository;

public class DataException extends RuntimeException {
    public DataException(Throwable cause) {
        super("Data could not be read or saved. Check the database folder and try again. · ڈیٹا محفوظ نہیں ہوا، دوبارہ کوشش کریں۔", cause);
    }
}
