package be.koder.library.test.book;

import be.koder.library.api.book.AddBook;
import be.koder.library.api.book.AddBookPresenter;
import be.koder.library.usecase.book.AddBookUseCase;
import be.koder.library.vocabulary.book.BookId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Given an API to add books to the library")
public class AddBookTest {

    private final AddBook addBook = new AddBookUseCase();

    @Nested
    @DisplayName("when a book is added to the library")
    class TestWhenBookAdded implements AddBookPresenter {

        private boolean addedCalled;
        private BookId addedBookId;

        @BeforeEach
        void setup() {
            addBook.addBook("0-7475-3269-9", "Harry Potter and the Philosopher's Stone", "J. K. Rowling", this);
        }

        @Test
        @DisplayName("it should provide feedback")
        void feedbackProvided() {
            assertTrue(addedCalled);
            assertNotNull(addedBookId);
        }

        @Override
        public void added(BookId id) {
            addedCalled = true;
            addedBookId = id;
        }
    }
}