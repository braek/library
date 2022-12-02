package be.koder.library.usecase.book;

import be.koder.library.usecase.Command;
import be.koder.library.vocabulary.book.Author;
import be.koder.library.vocabulary.book.Isbn;
import be.koder.library.vocabulary.book.Title;

public record AddBookCommand(Isbn isbn, Title title, Author author) implements Command {
}