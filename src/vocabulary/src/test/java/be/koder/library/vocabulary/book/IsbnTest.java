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
                "0123456789",
                "0123456789012"
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
                "012345678",
                "01234567890",
                "012345678901"
        })
        void exceptionThrown(final String str) {
            assertThrows(InvalidIsbnException.class, () -> Isbn.fromString(str));
        }
    }
}