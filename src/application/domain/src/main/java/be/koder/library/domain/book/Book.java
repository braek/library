package be.koder.library.domain.book;

import be.koder.library.vocabulary.book.Author;
import be.koder.library.vocabulary.book.BookId;
import be.koder.library.vocabulary.book.Isbn;
import be.koder.library.vocabulary.book.Title;

public final class Book {

    private final BookId id;
    private final Isbn isbn;
    private final Title title;
    private final Author author;

    private Book(BookId id, Isbn isbn, Title title, Author author) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
    }

    public static Book fromSnapshot(BookSnapshot snapshot) {
        return new Book(snapshot.id(), snapshot.isbn(), snapshot.title(), snapshot.author());
    }

    public static Book createNew(Isbn isbn, Title title, Author author) {
        return new Book(BookId.createNew(), isbn, title, author);
    }

    public BookSnapshot takeSnapshot() {
        return new BookSnapshot(id, isbn, title, author);
    }
}