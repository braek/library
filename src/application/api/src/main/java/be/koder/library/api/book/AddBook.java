package be.koder.library.api.book;

import be.koder.library.vocabulary.book.Author;
import be.koder.library.vocabulary.book.Isbn;
import be.koder.library.vocabulary.book.Title;

public interface AddBook {
    void addBook(Isbn isbn, Title title, Author author, AddBookPresenter presenter);
}