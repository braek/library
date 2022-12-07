package be.koder.library.test.book;

import be.koder.library.api.book.AddBook;
import be.koder.library.api.book.AddBookPresenter;
import be.koder.library.domain.book.BookSnapshot;
import be.koder.library.test.BookObjectMother;
import be.koder.library.test.MockAddBookPresenter;
import be.koder.library.test.MockBookRepository;
import be.koder.library.test.MockEventPublisher;
import be.koder.library.usecase.book.AddBookUseCase;
import be.koder.library.vocabulary.book.BookId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Given an API to add books to the library")
public class AddBookTest {

    private final MockBookRepository bookRepository = new MockBookRepository();
    private final AddBook addBook = new AddBookUseCase(bookRepository, bookRepository, new MockEventPublisher());

    @Nested
    @DisplayName("when a book is added to the library")
    class TestWhenBookAdded implements AddBookPresenter {

        private final BookSnapshot book = BookObjectMother.INSTANCE.mobyDick;
        private BookId bookId;

        @BeforeEach
        void setup() {
            addBook.addBook(book.isbn(), book.title(), book.author(), this);
        }

        @Test
        @DisplayName("it should provide feedback")
        void feedbackProvided() {
            assertNotNull(bookId);
        }

        @Override
        public void added(BookId bookId) {
            this.bookId = bookId;
        }

        @Override
        public void isbnAlreadyRegistered() {
            fail("Should not be called");
        }
    }

    @Nested
    @DisplayName("when a book is added to the library, but ISBN is already registered")
    class TestWhenBookAddedButIsbnAlreadyRegistered implements AddBookPresenter {

        private final BookSnapshot book = BookObjectMother.INSTANCE.theGreatGatsby;
        private boolean isbnAlreadyRegisteredCalled;

        @BeforeEach
        void setup() {
            addBook.addBook(book.isbn(), book.title(), book.author(), new MockAddBookPresenter());
            addBook.addBook(book.isbn(), book.title(), book.author(), this);
        }

        @Override
        public void added(BookId bookId) {
            fail("Should not be called");
        }

        @Test
        @DisplayName("it should provide feedback")
        void feedbackProvided() {
            assertTrue(isbnAlreadyRegisteredCalled);
        }

        @Override
        public void isbnAlreadyRegistered() {
            isbnAlreadyRegisteredCalled = true;
        }
    }
}