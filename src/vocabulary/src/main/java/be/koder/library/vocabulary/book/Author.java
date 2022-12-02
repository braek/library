package be.koder.library.vocabulary.book;

import be.koder.library.vocabulary.book.exception.InvalidAuthorException;
import be.koder.library.vocabulary.book.exception.InvalidTitleException;

import java.util.Objects;

import static java.util.Optional.ofNullable;

public final class Author {

    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 50;
    private final String value;

    private Author(String str) {
        final var sanitized = ofNullable(str)
                .map(String::trim)
                .orElseThrow(() -> new NullPointerException("Cannot create Author from NULL"));
        if (sanitized.length() < MIN_LENGTH || sanitized.length() > MAX_LENGTH) {
            throw new InvalidAuthorException(str);
        }
        this.value = sanitized;
    }

    public static Author fromString(final String str) {
        return new Author(str);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Author author = (Author) o;
        return Objects.equals(value, author.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
