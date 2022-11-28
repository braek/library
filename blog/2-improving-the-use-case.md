# Improving the use case

## 1. Introducing domain primitive ISBN

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

Next, we can start to update the API that we defined:

```java
package be.koder.library.api.book;

import be.koder.library.vocabulary.book.Isbn;

public interface AddBook {
    void addBook(Isbn isbn, String title, String author, AddBookPresenter presenter);
}
```

And go deeper in the application by altering the **use case:**

```java
package be.koder.library.usecase.book;

import be.koder.library.api.book.AddBook;
import be.koder.library.api.book.AddBookPresenter;
import be.koder.library.domain.EventPublisher;
import be.koder.library.domain.book.Book;
import be.koder.library.domain.book.BookAdded;
import be.koder.library.domain.book.BookRepository;
import be.koder.library.usecase.UseCase;
import be.koder.library.vocabulary.book.Isbn;

public final class AddBookUseCase implements UseCase<AddBookCommand, AddBookPresenter>, AddBook {

    private final BookRepository bookRepository;
    private final EventPublisher eventPublisher;

    public AddBookUseCase(BookRepository bookRepository, EventPublisher eventPublisher) {
        this.bookRepository = bookRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void addBook(Isbn isbn, String title, String author, AddBookPresenter presenter) {
        execute(new AddBookCommand(isbn, title, author), presenter);
    }

    @Override
    public void execute(AddBookCommand command, AddBookPresenter presenter) {
        final var book = Book.createNew(command.isbn(), command.title(), command.author());
        bookRepository.save(book);
        final var bookId = book.takeSnapshot().id();
        eventPublisher.publish(new BookAdded(bookId));
        presenter.added(bookId);
    }
}
```

And the **use case command:**

```java
package be.koder.library.usecase.book;

import be.koder.library.usecase.Command;
import be.koder.library.vocabulary.book.Isbn;

public record AddBookCommand(Isbn isbn, String title, String author) implements Command {
}
```

Until we need to update the aggregate root:

```java
package be.koder.library.domain.book;

import be.koder.library.vocabulary.book.BookId;
import be.koder.library.vocabulary.book.Isbn;

public final class Book {

    private final BookId id;
    private final Isbn isbn;
    private final String title;
    private final String author;

    private Book(BookId id, Isbn isbn, String title, String author) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
    }

    public static Book fromSnapshot(BookSnapshot snapshot) {
        return new Book(snapshot.id(), snapshot.isbn(), snapshot.title(), snapshot.author());
    }

    public static Book createNew(Isbn isbn, String title, String author) {
        return new Book(BookId.createNew(), isbn, title, author);
    }

    public BookSnapshot takeSnapshot() {
        return new BookSnapshot(id, isbn, title, author);
    }
}
```

And the snapshot version of the aggregate root:

```java
package be.koder.library.domain.book;

import be.koder.library.vocabulary.book.BookId;
import be.koder.library.vocabulary.book.Isbn;

public record BookSnapshot(BookId id, Isbn isbn, String title, String author) {
}
```

And of course we also need to update the **object mother:**

```java
package be.koder.library.test;

import be.koder.library.domain.book.BookSnapshot;
import be.koder.library.vocabulary.book.BookId;
import be.koder.library.vocabulary.book.Isbn;

public enum BookObjectMother {

    INSTANCE;

    public final BookSnapshot harryPotterAndThePhilosophersStone = new BookSnapshot(
            BookId.createNew(),
            Isbn.fromString("0747532699"),
            "Harry Potter and the Philosopher's Stone",
            "J. K. Rowling"
    );
}
```

And the **acceptance test:**

```java
package be.koder.library.test.book;

import be.koder.library.api.book.AddBook;
import be.koder.library.api.book.AddBookPresenter;
import be.koder.library.domain.book.BookSnapshot;
import be.koder.library.test.BookObjectMother;
import be.koder.library.test.MockBookRepository;
import be.koder.library.test.MockEventPublisher;
import be.koder.library.usecase.book.AddBookUseCase;
import be.koder.library.vocabulary.book.BookId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Given an API to add books to the library")
public class AddBookTest {

    private final AddBook addBook = new AddBookUseCase(new MockBookRepository(), new MockEventPublisher());

    @Nested
    @DisplayName("when a book is added to the library")
    class TestWhenBookAdded implements AddBookPresenter {

        private final BookSnapshot book = BookObjectMother.INSTANCE.harryPotterAndThePhilosophersStone;
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
    }
}
```

And the **use case test:**

```java
package be.koder.library.usecase.book;

import be.koder.library.api.book.AddBookPresenter;
import be.koder.library.domain.book.Book;
import be.koder.library.domain.book.BookAdded;
import be.koder.library.domain.book.BookSnapshot;
import be.koder.library.test.BookObjectMother;
import be.koder.library.test.MockBookRepository;
import be.koder.library.test.MockEventPublisher;
import be.koder.library.vocabulary.book.BookId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Given a use case to add books to the library")
class AddBookUseCaseTest {

    private final MockBookRepository bookRepository = new MockBookRepository();
    private final MockEventPublisher eventPublisher = new MockEventPublisher();
    private final AddBookUseCase addBookUseCase = new AddBookUseCase(bookRepository, eventPublisher);

    @Nested
    @DisplayName("when a book is added to the library")
    class TestWhenBookAdded implements AddBookPresenter {

        private final BookSnapshot book = BookObjectMother.INSTANCE.harryPotterAndThePhilosophersStone;
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
    }
}
```