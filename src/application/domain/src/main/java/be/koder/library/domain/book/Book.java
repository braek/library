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