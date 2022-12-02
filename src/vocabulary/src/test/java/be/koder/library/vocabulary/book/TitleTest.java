package be.koder.library.vocabulary.book;

import be.koder.library.vocabulary.book.exception.InvalidTitleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Given a class that represents book titles")
public class TitleTest {

    @Nested
    @DisplayName("when valid string provided")
    class TestWhenValidStringProvided {

        @DisplayName("it should be created successfully")
        @ParameterizedTest
        @ValueSource(strings = {
                "Domain-Driven Design by Example",
                "The Big Friendly Giant"
        })
        void testValidTitle(final String str) {
            final var title = Title.fromString(str);
            assertThat(title.toString()).isEqualTo(str.trim());
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
            assertThrows(InvalidTitleException.class, () -> Title.fromString(str));
        }
    }

    @Nested
    @DisplayName("when NULL string provided")
    class TestWhenNullStringProvided {

        @ParameterizedTest
        @NullSource
        @DisplayName("it should throw an exception")
        void exceptionThrown(final String str) {
            assertThrows(NullPointerException.class, () -> Title.fromString(str));
        }
    }
}