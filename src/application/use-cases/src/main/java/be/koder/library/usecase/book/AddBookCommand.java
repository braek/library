package be.koder.library.usecase.book;

import be.koder.library.usecase.Command;

public record AddBookCommand(String isbn, String title, String author) implements Command {
}