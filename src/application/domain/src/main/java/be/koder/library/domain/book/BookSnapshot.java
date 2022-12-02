package be.koder.library.domain.book;

import be.koder.library.vocabulary.book.Author;
import be.koder.library.vocabulary.book.BookId;
import be.koder.library.vocabulary.book.Isbn;
import be.koder.library.vocabulary.book.Title;

public record BookSnapshot(BookId id, Isbn isbn, Title title, Author author) {
}