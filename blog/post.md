# First use case in hexagonal architecture

## 1. Define the API

Base definition of the user action to add books to the library.

```java
package be.koder.library.api.book;

public interface AddBook {
    void addBook(String isbn, String title, String author, AddBookPresenter presenter);
}
```

The presenter defines the possible outcomes of the action.

Right now, we will focus only on the positive outcome, being that the book was added to the library.

```java
package be.koder.library.api.book;

public interface AddBookPresenter {
    void added();
}
```

## 2. Add the first acceptance test for the newly created API

Next up, we can add an acceptance test for this one.

```java
package be.koder.library.test.book;

import be.koder.library.api.AddBook;
import be.koder.library.usecase.AddBookUseCase;
import org.junit.jupiter.api.DisplayName;

@DisplayName("Given an API to add books to the library")
public class AddBookTest {

    private final AddBook addBook = new AddBookUseCase();
}
```

## 3. Implementing the API with a use case

And create the actual use case:

```java
package be.koder.library.usecase.book;

import be.koder.library.api.AddBook;
import be.koder.library.api.AddBookPresenter;

public final class AddBookUseCase implements AddBook {
    @Override
    public void addBook(String isbn, String title, String author, AddBookPresenter presenter) {

    }
}
```

We can create an interface for the use case now:

```java
package be.koder.library.usecase;

public interface UseCase<COMMAND extends Command, PRESENTER> {
    void execute(COMMAND command, PRESENTER presenter);
}
```

With a **marker interface** for commands:

```java
package be.koder.library.usecase;

public interface Command {
}
```

Now we can create the command to add books to the library.

```java
package be.koder.library.usecase.book;

public record AddBookCommand(String isbn, String title, String author) implements Command {
}
```

And also implement the **use case interface** in the ```AddBookUseCase.```

```java
package be.koder.library.usecase.book;

import be.koder.library.api.AddBook;
import be.koder.library.api.AddBookPresenter;
import be.koder.library.usecase.UseCase;

public final class AddBookUseCase implements UseCase<AddBookCommand, AddBookPresenter>, AddBook {

    @Override
    public void addBook(String isbn, String title, String author, AddBookPresenter presenter) {
        execute(new AddBookCommand(isbn, title, author), presenter);
    }

    @Override
    public void execute(AddBookCommand command, AddBookPresenter presenter) {

    }
}
```

## 4. Extend the acceptance test and implement the use case further

Now we can add are first real acceptance test:

```java
package be.koder.library.test.book;

import be.koder.library.api.book.AddBook;
import be.koder.library.api.book.AddBookPresenter;
import be.koder.library.usecase.book.AddBookUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Given an API to add books to the library")
public class AddBookTest {

    private final AddBook addBook = new AddBookUseCase();

    @Nested
    @DisplayName("when a book is added to the library")
    class TestWhenBookAdded implements AddBookPresenter {

        private boolean addedCalled;

        @BeforeEach
        void setup() {
            addBook.addBook("0-7475-3269-9", "Harry Potter and the Philosopher's Stone", "J. K. Rowling", this);
        }

        @Test
        @DisplayName("it should provide feedback")
        void feedbackProvided() {
            assertTrue(addedCalled);
        }

        @Override
        public void added() {
            addedCalled = true;
        }
    }
}
```

Implement the use case further:

```java
package be.koder.library.usecase.book;

import be.koder.library.api.book.AddBook;
import be.koder.library.api.book.AddBookPresenter;
import be.koder.library.domain.book.Book;
import be.koder.library.usecase.UseCase;

public final class AddBookUseCase implements UseCase<AddBookCommand, AddBookPresenter>, AddBook {

    @Override
    public void addBook(String isbn, String title, String author, AddBookPresenter presenter) {
        execute(new AddBookCommand(isbn, title, author), presenter);
    }

    @Override
    public void execute(AddBookCommand command, AddBookPresenter presenter) {
        var book = Book.createNew(command.isbn(), command.title(), command.author());
    }
}
```

We can create the **aggregate root** ```Book:```

```java
package be.koder.library.domain.book;

import be.koder.library.vocabulary.book.BookId;

public final class Book {

    private final BookId id;
    private final String isbn;
    private final String title;
    private final String author;

    private Book(BookId id, String isbn, String title, String author) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
    }

    public BookSnapshot takeSnapshot() {
        return new BookSnapshot(id, isbn, title, author);
    }

    public static Book createNew(String isbn, String title, String author) {
        return new Book(BookId.createNew(), isbn, title, author);
    }
}
```

And add a **domain primitive** for the identifier of a book:

```java
package be.koder.library.vocabulary.book;

import java.util.Objects;
import java.util.UUID;

public final final class BookId {

    private final UUID uuid;

    private BookId(UUID uuid) {
        this.uuid = uuid;
    }

    public static BookId createNew() {
        return new BookId(UUID.randomUUID());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookId bookId = (BookId) o;
        return Objects.equals(uuid, bookId.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public String toString() {
        return uuid.toString();
    }
}
```

There is also a **snapshot** created of the ```Book``` **aggregate root.** This is an immutable version of the **aggregate root.**

```java
package be.koder.library.domain.book;

import be.koder.library.vocabulary.book.BookId;

public record BookSnapshot(BookId id, String isbn, String title, String author) {
}
```

Next, we can implement the use case further by calling the presenter:

```java
package be.koder.library.usecase.book;

import be.koder.library.api.book.AddBook;
import be.koder.library.api.book.AddBookPresenter;
import be.koder.library.domain.book.Book;
import be.koder.library.usecase.UseCase;

public final class AddBookUseCase implements UseCase<AddBookCommand, AddBookPresenter>, AddBook {

    @Override
    public void addBook(String isbn, String title, String author, AddBookPresenter presenter) {
        execute(new AddBookCommand(isbn, title, author), presenter);
    }

    @Override
    public void execute(AddBookCommand command, AddBookPresenter presenter) {
        var book = Book.createNew(command.isbn(), command.title(), command.author());
        presenter.added(book.takeSnapshot().id());
    }
}
```

Also, the presenter is extended with extra feedback, now returning the ```BookId``` when a new book is added to the library:

```java
package be.koder.library.api.book;

import be.koder.library.vocabulary.book.BookId;

public interface AddBookPresenter {
    void added(BookId id);
}
```

Now the acceptance test will be green. And we can also add an extra assert on the ```BookId``` that is returned by the presenter:

```java
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

@DisplayName("Given an API to add books to the library")
public class AddBookTest {

    private final AddBook addBook = new AddBookUseCase();

    @Nested
    @DisplayName("when a book is added to the library")
    class TestWhenBookAdded implements AddBookPresenter {

        private BookId bookId;

        @BeforeEach
        void setup() {
            addBook.addBook("0-7475-3269-9", "Harry Potter and the Philosopher's Stone", "J. K. Rowling", this);
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

## 5. Saving the aggregate root and adding a use case test

Next, we need to add a **use case test** for the ```AddBookUseCase``` to test the **internal kitchen** of the domain. At first sight, this resembles a lot of the acceptance test.

```java
package be.koder.library.usecase.book;

import be.koder.library.api.book.AddBookPresenter;
import be.koder.library.vocabulary.book.BookId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Given a use case to add books to the library")
class AddBookUseCaseTest {

    private final AddBookUseCase addBookUseCase = new AddBookUseCase();

    @Nested
    @DisplayName("when a book is added to the library")
    class TestWhenBookAdded implements AddBookPresenter {

        private BookId bookId;

        @BeforeEach
        void setup() {
            addBookUseCase.execute(new AddBookCommand("0-7475-3269-9", "Harry Potter and the Philosopher's Stone", "J. K. Rowling"), this);
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

Next, we need a **domain repository** to save the **aggregate root**. First we will define an abstraction for this:

```java
package be.koder.library.domain;

import java.util.Optional;

public interface Repository<AGGREGATE_ID, AGGREGATE> {

    Optional<AGGREGATE> getById(AGGREGATE_ID id);

    void save(AGGREGATE aggregate);
}
```

For the **aggregate root** we can define a more concrete abstraction for this:

```java
package be.koder.library.domain.book;

import be.koder.library.domain.Repository;
import be.koder.library.vocabulary.book.BookId;

public interface BookRepository extends Repository<BookId, Book> {
}
```

We can also add the dependency in the ```AddBookUseCase:```

```java
public final class AddBookUseCase implements UseCase<AddBookCommand, AddBookPresenter>, AddBook {

    private final BookRepository bookRepository;

    public AddBookUseCase(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public void addBook(String isbn, String title, String author, AddBookPresenter presenter) {
        execute(new AddBookCommand(isbn, title, author), presenter);
    }

    @Override
    public void execute(AddBookCommand command, AddBookPresenter presenter) {
        var book = Book.createNew(command.isbn(), command.title(), command.author());
        presenter.added(book.takeSnapshot().id());
    }
}
```

For the tests, we need a mock implementation of the ```BookRepository``` that acts as an in-memory database:

```java
package be.koder.library.test;

import be.koder.library.domain.book.Book;
import be.koder.library.domain.book.BookRepository;
import be.koder.library.domain.book.BookSnapshot;
import be.koder.library.vocabulary.book.BookId;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class MockBookRepository implements BookRepository {

    private final Map<BookId, BookSnapshot> books = new HashMap<>();

    @Override
    public Optional<Book> getById(BookId bookId) {
        if (books.containsKey(bookId)) {
            return Optional.of(Book.fromSnapshot(books.get(bookId)));
        }
        return Optional.empty();
    }

    @Override
    public void save(Book book) {
        var snapshot = book.takeSnapshot();
        books.put(snapshot.id(), snapshot);
    }
}
```

We need a new method ```fromSnapshot``` on the ```Book``` aggregate, because now the thing with the snapshot also works in the other direction:

```java
package be.koder.library.domain.book;

import be.koder.library.vocabulary.book.BookId;

public final class Book {

    private final BookId id;
    private final String isbn;
    private final String title;
    private final String author;

    private Book(BookId id, String isbn, String title, String author) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
    }

    public static Book fromSnapshot(BookSnapshot snapshot) {
        return new Book(snapshot.id(), snapshot.isbn(), snapshot.title(), snapshot.author());
    }

    public BookSnapshot takeSnapshot() {
        return new BookSnapshot(id, isbn, title, author);
    }

    public static Book createNew(String isbn, String title, String author) {
        return new Book(BookId.createNew(), isbn, title, author);
    }
}
```

Next, add the dependency on the ```BookRepository``` in the acceptance test:

```java
package be.koder.library.test.book;

import be.koder.library.api.book.AddBook;
import be.koder.library.api.book.AddBookPresenter;
import be.koder.library.test.MockBookRepository;
import be.koder.library.usecase.book.AddBookUseCase;
import be.koder.library.vocabulary.book.BookId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Given an API to add books to the library")
public class AddBookTest {

    private final AddBook addBook = new AddBookUseCase(new MockBookRepository());

    @Nested
    @DisplayName("when a book is added to the library")
    class TestWhenBookAdded implements AddBookPresenter {

        private BookId bookId;

        @BeforeEach
        void setup() {
            addBook.addBook("0-7475-3269-9", "Harry Potter and the Philosopher's Stone", "J. K. Rowling", this);
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

And also add the same dependency in the **use case test.** In the use case test, we can also now test the save functionality:

```java
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

    private final MockBookRepository bookRepository = new MockBookRepository();
    private final AddBookUseCase addBookUseCase = new AddBookUseCase(bookRepository);

    @Nested
    @DisplayName("when a book is added to the library")
    class TestWhenBookAdded implements AddBookPresenter {

        private final String isbn = "0-7475-3269-9";
        private final String title = "Harry Potter and the Philosopher's Stone";
        private final String author = "J. K. Rowling";
        private BookId bookId;
        private BookSnapshot savedBook;

        @BeforeEach
        void setup() {
            addBookUseCase.execute(new AddBookCommand(isbn, title, author), this);
            savedBook = bookRepository.getById(bookId).map(Book::takeSnapshot).orElseThrow();
        }

        @Test
        @DisplayName("it should be saved")
        void bookSaved() {
            assertThat(savedBook.id()).isEqualTo(bookId);
            assertThat(savedBook.isbn()).isEqualTo(isbn);
            assertThat(savedBook.title()).isEqualTo(title);
            assertThat(savedBook.author()).isEqualTo(author);
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

This test will still be red, because the use case was not yet implemented. Now we can use the ```save``` method on the ```BookRepository``` in the use case to make the test green.

```java
package be.koder.library.usecase.book;

import be.koder.library.api.book.AddBook;
import be.koder.library.api.book.AddBookPresenter;
import be.koder.library.domain.book.Book;
import be.koder.library.domain.book.BookRepository;
import be.koder.library.usecase.UseCase;

public final class AddBookUseCase implements UseCase<AddBookCommand, AddBookPresenter>, AddBook {

    private final BookRepository bookRepository;

    public AddBookUseCase(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public void addBook(String isbn, String title, String author, AddBookPresenter presenter) {
        execute(new AddBookCommand(isbn, title, author), presenter);
    }

    @Override
    public void execute(AddBookCommand command, AddBookPresenter presenter) {
        var book = Book.createNew(command.isbn(), command.title(), command.author());
        bookRepository.save(book);
        presenter.added(book.takeSnapshot().id());
    }
}
```

## 6. Make the use case throw an event

Next, we need to make sure that the use case throws an event whenever a book is added to the library.

So we need to introduce the concept of an **event** and **event publisher** in the domain.

```java
package be.koder.library.domain;

public interface Event {
}
```

```java
package be.koder.library.domain;

public interface EventPublisher {
    void publish(Event event);
}
```

```java
package be.koder.library.domain.book;

import be.koder.library.domain.Event;
import be.koder.library.vocabulary.book.BookId;

public record BookAdded(BookId bookId) implements Event {
}
```

In the **test doubles** module, we must also add an implementation of the **event publisher** as an in-memory implementation.

```java
package be.koder.library.test;

import be.koder.library.domain.Event;
import be.koder.library.domain.EventPublisher;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class MockEventPublisher implements EventPublisher {

    private final List<Event> events = new ArrayList<>();

    @Override
    public void publish(Event event) {
        events.add(event);
    }

    public Optional<Event> getLastPublishedEvent() {
        if (events.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(events.get(events.size() - 1));
    }
}
```

Next, we need to add the **event publisher** as dependency in the use case:

```java
package be.koder.library.usecase.book;

import be.koder.library.api.book.AddBook;
import be.koder.library.api.book.AddBookPresenter;
import be.koder.library.domain.EventPublisher;
import be.koder.library.domain.book.Book;
import be.koder.library.domain.book.BookAdded;
import be.koder.library.domain.book.BookRepository;
import be.koder.library.usecase.UseCase;

public final class AddBookUseCase implements UseCase<AddBookCommand, AddBookPresenter>, AddBook {

    private final BookRepository bookRepository;
    private final EventPublisher eventPublisher;

    public AddBookUseCase(BookRepository bookRepository, EventPublisher eventPublisher) {
        this.bookRepository = bookRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void addBook(String isbn, String title, String author, AddBookPresenter presenter) {
        execute(new AddBookCommand(isbn, title, author), presenter);
    }

    @Override
    public void execute(AddBookCommand command, AddBookPresenter presenter) {
        var book = Book.createNew(command.isbn(), command.title(), command.author());
        bookRepository.save(book);
        presenter.added(book.takeSnapshot().id());
    }
}
```

Next, we can add an extra test in the use case test:

```java
package be.koder.library.usecase.book;

import be.koder.library.api.book.AddBookPresenter;
import be.koder.library.domain.book.Book;
import be.koder.library.domain.book.BookAdded;
import be.koder.library.domain.book.BookSnapshot;
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

        private final String isbn = "0-7475-3269-9";
        private final String title = "Harry Potter and the Philosopher's Stone";
        private final String author = "J. K. Rowling";
        private BookId bookId;
        private BookSnapshot savedBook;

        @BeforeEach
        void setup() {
            addBookUseCase.execute(new AddBookCommand(isbn, title, author), this);
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
            assertThat(savedBook.isbn()).isEqualTo(isbn);
            assertThat(savedBook.title()).isEqualTo(title);
            assertThat(savedBook.author()).isEqualTo(author);
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

This test will be orange however, so implement it in the use case to make it green:

```java
package be.koder.library.usecase.book;

import be.koder.library.api.book.AddBook;
import be.koder.library.api.book.AddBookPresenter;
import be.koder.library.domain.EventPublisher;
import be.koder.library.domain.book.Book;
import be.koder.library.domain.book.BookAdded;
import be.koder.library.domain.book.BookRepository;
import be.koder.library.usecase.UseCase;

public final class AddBookUseCase implements UseCase<AddBookCommand, AddBookPresenter>, AddBook {

    private final BookRepository bookRepository;
    private final EventPublisher eventPublisher;

    public AddBookUseCase(BookRepository bookRepository, EventPublisher eventPublisher) {
        this.bookRepository = bookRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void addBook(String isbn, String title, String author, AddBookPresenter presenter) {
        execute(new AddBookCommand(isbn, title, author), presenter);
    }

    @Override
    public void execute(AddBookCommand command, AddBookPresenter presenter) {
        var book = Book.createNew(command.isbn(), command.title(), command.author());
        bookRepository.save(book);
        final var snapshot = book.takeSnapshot();
        eventPublisher.publish(new BookAdded(snapshot.id()));
        presenter.added(snapshot.id());
    }
}
```