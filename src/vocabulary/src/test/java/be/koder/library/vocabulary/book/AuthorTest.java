package be.koder.library.vocabulary.book;

import be.koder.library.vocabulary.book.exception.InvalidAuthorException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Given a class that represents book authors")
public class AuthorTest {

    @Nested
    @DisplayName("when valid string provided")
    class TestWhenValidStringProvided {

        @DisplayName("it should be created successfully")
        @ParameterizedTest
        @ValueSource(strings = {
                "Jane Doe",
                "John Doe"
        })
        void testValidAuthor(final String str) {
            final var author = Author.fromString(str);
            assertThat(author.toString()).isEqualTo(str.trim());
        }
    }

    @Nested
    @DisplayName("when invalid string provided")
    class TestWhenInvalidStringProvided {

        @DisplayName("it should throw an exception")
        @ParameterizedTest
        @ValueSource(strings = {
                "",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis eleifend interdum congue."
        })
        void exceptionThrown(final String str) {
            assertThrows(InvalidAuthorException.class, () -> Author.fromString(str));
        }
    }

    @Nested
    @DisplayName("when NULL string provided")
    class TestWhenNullStringProvided {

        @ParameterizedTest
        @NullSource
        @DisplayName("it should throw an exception")
        void exceptionThrown(final String str) {
            assertThrows(NullPointerException.class, () -> Author.fromString(str));
        }
    }
}