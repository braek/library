package be.koder.library.test;

import be.koder.library.api.book.AddBookPresenter;
import be.koder.library.vocabulary.book.BookId;

public final class MockAddBookPresenter implements AddBookPresenter {

    private BookId bookId;

    @Override
    public void added(BookId bookId) {
        this.bookId = bookId;
    }

    @Override
    public void isbnAlreadyRegistered() {
        // Do nothing
    }

    public BookId getBookId() {
        return bookId;
    }
}