package be.koder.library.vocabulary.book;

import be.koder.library.vocabulary.book.exception.InvalidTitleException;

import java.util.Objects;

import static java.util.Optional.ofNullable;

public final class Title {

    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 50;
    private final String value;

    public Title(final String str) {
        final var sanitized = ofNullable(str)
                .map(String::trim)
                .orElseThrow(() -> new NullPointerException("Cannot create Title from NULL"));
        if (sanitized.length() < MIN_LENGTH || sanitized.length() > MAX_LENGTH) {
            throw new InvalidTitleException(str);
        }
        this.value = sanitized;
    }

    public static Title fromString(final String str) {
        return new Title(str);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Title title = (Title) o;
        return Objects.equals(value, title.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}