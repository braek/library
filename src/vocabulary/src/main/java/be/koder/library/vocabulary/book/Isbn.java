package be.koder.library.vocabulary.book;

import java.util.Objects;
import java.util.regex.Pattern;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;

public final class Isbn {

    private final String value;

    private Isbn(final String str) {
        final String sanitized = ofNullable(str)
                .map(String::trim)
                .orElse(null);
        if (isNull(sanitized) || !Pattern.compile("^\\d{10}$").matcher(sanitized).matches()) {
            throw new InvalidIsbnException(str);
        }
        this.value = sanitized;
    }

    public static Isbn fromString(final String str) {
        return new Isbn(str);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Isbn isbn = (Isbn) o;
        return Objects.equals(value, isbn.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
