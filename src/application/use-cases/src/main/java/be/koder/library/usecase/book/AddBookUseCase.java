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