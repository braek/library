package be.koder.library.api.book;

import be.koder.library.vocabulary.book.Isbn;

public interface AddBook {
    void addBook(Isbn isbn, String title, String author, AddBookPresenter presenter);
}