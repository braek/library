package be.koder.library.usecase.book;

import be.koder.library.usecase.Command;
import be.koder.library.vocabulary.book.Isbn;

public record AddBookCommand(Isbn isbn, String title, String author) implements Command {
}