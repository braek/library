package be.koder.library.usecase.book;

import be.koder.library.api.book.AddBookPresenter;
import be.koder.library.domain.book.Book;
import be.koder.library.domain.book.BookAdded;
import be.koder.library.domain.book.BookSnapshot;
import be.koder.library.test.BookObjectMother;
import be.koder.library.test.MockAddBookPresenter;
import be.koder.library.test.MockBookRepository;
import be.koder.library.test.MockEventPublisher;
import be.koder.library.vocabulary.book.BookId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Given a use case to add books to the library")
class AddBookUseCaseTest {

    private final MockBookRepository bookRepository = new MockBookRepository();
    private final MockEventPublisher eventPublisher = new MockEventPublisher();
    private final AddBookUseCase addBookUseCase = new AddBookUseCase(bookRepository, bookRepository, eventPublisher);

    @Nested
    @DisplayName("when a book is added to the library")
    class TestWhenBookAdded implements AddBookPresenter {

        private final BookSnapshot book = BookObjectMother.INSTANCE.mobyDick;
        private BookId bookId;
        private BookSnapshot savedBook;

        @BeforeEach
        void setup() {
            addBookUseCase.execute(new AddBookCommand(book.isbn(), book.title(), book.author()), this);
            savedBook = bookRepository.getById(bookId).map(Book::takeSnapshot).orElseThrow();
        }

        @Test
        @DisplayName("it should throw an event")
        void eventThrown() {
            assertThat(eventPublisher.getLastPublishedEvent()).hasValueSatisfying(it -> {
                assertThat(it).isInstanceOf(BookAdded.class);
                var event = (BookAdded) it;
                assertThat(event.bookId()).isEqualTo(bookId);
            });
        }

        @Test
        @DisplayName("it should be saved")
        void bookSaved() {
            assertThat(savedBook.id()).isEqualTo(bookId);
            assertThat(savedBook.isbn()).isEqualTo(book.isbn());
            assertThat(savedBook.title()).isEqualTo(book.title());
            assertThat(savedBook.author()).isEqualTo(book.author());
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
            addBookUseCase.execute(new AddBookCommand(book.isbn(), book.title(), book.author()), new MockAddBookPresenter());
            addBookUseCase.execute(new AddBookCommand(book.isbn(), book.title(), book.author()), this);
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