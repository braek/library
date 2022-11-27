package be.koder.library.usecase.book;

import be.koder.library.api.book.AddBook;
import be.koder.library.api.book.AddBookPresenter;
import be.koder.library.domain.EventPublisher;
import be.koder.library.domain.book.Book;
import be.koder.library.domain.book.BookAdded;
import be.koder.library.domain.book.BookRepository;
import be.koder.library.usecase.UseCase;

public final class AddBookUseCase implements UseCase<AddBookCommand, AddBookPresenter>, AddBook {

    private final BookRepository bookRepository;
    private final EventPublisher eventPublisher;

    public AddBookUseCase(BookRepository bookRepository, EventPublisher eventPublisher) {
        this.bookRepository = bookRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void addBook(String isbn, String title, String author, AddBookPresenter presenter) {
        execute(new AddBookCommand(isbn, title, author), presenter);
    }

    @Override
    public void execute(AddBookCommand command, AddBookPresenter presenter) {
        final var book = Book.createNew(command.isbn(), command.title(), command.author());
        bookRepository.save(book);
        final var snapshot = book.takeSnapshot();
        eventPublisher.publish(new BookAdded(snapshot.id()));
        presenter.added(snapshot.id());
    }
}