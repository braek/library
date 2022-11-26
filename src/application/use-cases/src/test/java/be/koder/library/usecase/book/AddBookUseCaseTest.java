package be.koder.library.usecase.book;

import be.koder.library.api.book.AddBookPresenter;
import be.koder.library.domain.book.Book;
import be.koder.library.domain.book.BookRepository;
import be.koder.library.domain.book.BookSnapshot;
import be.koder.library.test.MockBookRepository;
import be.koder.library.vocabulary.book.BookId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Given a use case to add books to the library")
class AddBookUseCaseTest {

    private final BookRepository bookRepository = new MockBookRepository();
    private final AddBookUseCase addBookUseCase = new AddBookUseCase(bookRepository);

    @Nested
    @DisplayName("when a book is added to the library")
    class TestWhenBookAdded implements AddBookPresenter {

        private final String isbn = "0-7475-3269-9";
        private final String title = "Harry Potter and the Philosopher's Stone";
        private final String author = "J. K. Rowling";
        private BookId bookId;
        private BookSnapshot book;

        @BeforeEach
        void setup() {
            addBookUseCase.execute(new AddBookCommand(isbn, title, author), this);
            book = bookRepository.getById(bookId).map(Book::takeSnapshot).orElseThrow();
        }

        @Test
        @DisplayName("it should be saved")
        void bookSaved() {
            assertThat(book.id()).isEqualTo(bookId);
            assertThat(book.isbn()).isEqualTo(isbn);
            assertThat(book.title()).isEqualTo(title);
            assertThat(book.author()).isEqualTo(author);
        }

        @Test
        @DisplayName("it should provide feedback")
        void feedbackProvided() {
            assertNotNull(bookId);
        }

        @Override
        public void added(BookId id) {
            bookId = id;
        }
    }
}