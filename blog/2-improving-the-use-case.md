# Improving the use case

1. Introducing domain primitive ISBN

In the previous article, we created a basic use case. This use case is still flawed, however. We need to add validation for example.

We need to introduce more **domain primitives,** starting with the ISBN for example.

So, we add this class to the **vocabulary** module.

```java
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
        if (isNull(sanitized) || !Pattern.compile("^\\d{10}|\\d{13}$").matcher(sanitized).matches()) {
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
```

Also introducing a new kind of exception for invalid ISBNs:

```java
package be.koder.library.vocabulary.book;

public final class InvalidIsbnException extends RuntimeException {
    public InvalidIsbnException(String str) {
        super(String.format("This string is not a valid ISBN: %s", str));
    }
}
```

We must also add **unit tests** for the ISBN type:

```java
package be.koder.library.vocabulary.book;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Given a class that represents ISBNs")
class IsbnTest {

    @Nested
    @DisplayName("when valid string provided")
    class TestWhenValidStringProvided {

        @DisplayName("it should be created successfully")
        @ParameterizedTest
        @ValueSource(strings = {
                "0123456789"
        })
        void testValidIsbn(final String str) {
            final var isbn = Isbn.fromString(str);
            assertThat(isbn.toString()).isEqualTo(str.trim());
        }
    }

    @Nested
    @DisplayName("when invalid string provided")
    class TestWhenInvalidStringProvided {

        @DisplayName("it should throw an exception")
        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {
                "abc",
                "batman@gothamcity.com",
                "https://www.koder.be",
                "123",
                "123456789"
        })
        void exceptionThrown(final String str) {
            assertThrows(InvalidIsbnException.class, () -> Isbn.fromString(str));
        }
    }
}
```