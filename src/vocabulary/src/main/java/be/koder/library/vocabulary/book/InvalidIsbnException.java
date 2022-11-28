package be.koder.library.vocabulary.book;

public final class InvalidIsbnException extends RuntimeException {
    public InvalidIsbnException(String str) {
        super(String.format("This string is not a valid ISBN: %s", str));
    }
}