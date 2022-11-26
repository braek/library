package be.koder.library.api.book;

public interface AddBook {
    void addBook(String isbn, String title, String author, AddBookPresenter presenter);
}