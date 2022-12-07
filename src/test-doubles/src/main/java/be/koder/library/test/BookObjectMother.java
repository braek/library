package be.koder.library.test;

import be.koder.library.domain.book.BookSnapshot;
import be.koder.library.vocabulary.book.Author;
import be.koder.library.vocabulary.book.BookId;
import be.koder.library.vocabulary.book.Isbn;
import be.koder.library.vocabulary.book.Title;

public enum BookObjectMother {

    INSTANCE;

    public final BookSnapshot mobyDick = new BookSnapshot(
            BookId.createNew(),
            Isbn.fromString("9780553213119"),
            Title.fromString("Moby-Dick"),
            Author.fromString("Herman Melville")
    );

    public final BookSnapshot theGreatGatsby = new BookSnapshot(
            BookId.createNew(),
            Isbn.fromString("9780241341469"),
            Title.fromString("The Great Gatsby"),
            Author.fromString("F. Scott Fitzgerald")
    );
}