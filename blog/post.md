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