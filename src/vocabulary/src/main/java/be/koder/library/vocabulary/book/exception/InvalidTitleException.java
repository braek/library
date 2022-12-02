package be.koder.library.vocabulary.book.exception;

public final class InvalidTitleException extends RuntimeException {
    public InvalidTitleException(String str) {
        super(String.format("This string is not a valid title: %s", str));
    }
}