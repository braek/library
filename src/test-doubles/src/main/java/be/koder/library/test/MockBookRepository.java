package be.koder.library.test;

import be.koder.library.domain.book.Book;
import be.koder.library.domain.book.BookRepository;
import be.koder.library.domain.book.BookSnapshot;
import be.koder.library.domain.book.IsbnService;
import be.koder.library.vocabulary.book.BookId;
import be.koder.library.vocabulary.book.Isbn;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class MockBookRepository implements BookRepository, IsbnService {

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

    @Override
    public boolean exists(Isbn isbn) {
        return books.values().stream().anyMatch(it -> it.isbn().equals(isbn));
    }
}