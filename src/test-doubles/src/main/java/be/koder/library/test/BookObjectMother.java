package be.koder.library.test;

import be.koder.library.domain.book.BookSnapshot;
import be.koder.library.vocabulary.book.BookId;
import be.koder.library.vocabulary.book.Isbn;

public enum BookObjectMother {

    INSTANCE;

    public final BookSnapshot harryPotterAndThePhilosophersStone = new BookSnapshot(
            BookId.createNew(),
            Isbn.fromString("0747532699"),
            "Harry Potter and the Philosopher's Stone",
            "J. K. Rowling"
    );
}