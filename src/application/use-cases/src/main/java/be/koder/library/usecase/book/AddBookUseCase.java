package be.koder.library.usecase.book;

import be.koder.library.api.book.AddBook;
import be.koder.library.api.book.AddBookPresenter;
import be.koder.library.domain.EventPublisher;
import be.koder.library.domain.book.Book;
import be.koder.library.domain.book.BookAdded;
import be.koder.library.domain.book.BookRepository;
import be.koder.library.domain.book.IsbnService;
import be.koder.library.usecase.UseCase;
import be.koder.library.vocabulary.book.Author;
import be.koder.library.vocabulary.book.Isbn;
import be.koder.library.vocabulary.book.Title;

public final class AddBookUseCase implements UseCase<AddBookCommand, AddBookPresenter>, AddBook {

    private final BookRepository bookRepository;
    private final IsbnService isbnService;
    private final EventPublisher eventPublisher;

    public AddBookUseCase(BookRepository bookRepository, IsbnService isbnService, EventPublisher eventPublisher) {
        this.bookRepository = bookRepository;
        this.isbnService = isbnService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void addBook(Isbn isbn, Title title, Author author, AddBookPresenter presenter) {
        execute(new AddBookCommand(isbn, title, author), presenter);
    }

    @Override
    public void execute(AddBookCommand command, AddBookPresenter presenter) {
        if (isbnService.exists(command.isbn())) {
            presenter.isbnAlreadyRegistered();
            return;
        }
        final var book = Book.createNew(command.isbn(), command.title(), command.author());
        bookRepository.save(book);
        final var bookId = book.takeSnapshot().id();
        eventPublisher.publish(new BookAdded(bookId));
        presenter.added(bookId);
    }
}