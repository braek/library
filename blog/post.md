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

public class Book {

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

public final class BookId {

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