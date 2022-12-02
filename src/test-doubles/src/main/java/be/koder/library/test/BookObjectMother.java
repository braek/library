package be.koder.library.test;

import be.koder.library.domain.book.BookSnapshot;
import be.koder.library.vocabulary.book.Author;
import be.koder.library.vocabulary.book.BookId;
import be.koder.library.vocabulary.book.Isbn;
import be.koder.library.vocabulary.book.Title;

public enum BookObjectMother {

    INSTANCE;

    public final BookSnapshot harryPotterAndThePhilosophersStone = new BookSnapshot(
            BookId.createNew(),
            Isbn.fromString("0747532699"),
            Title.fromString("Harry Potter and the Philosopher's Stone"),
            Author.fromString("J. K. Rowling")
    );
}