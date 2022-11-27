package be.koder.library.test;

import be.koder.library.domain.book.BookSnapshot;
import be.koder.library.vocabulary.book.BookId;

public enum BookObjectMother {

    INSTANCE;

    public final BookSnapshot harryPotterAndThePhilosophersStone = new BookSnapshot(
            BookId.createNew(),
            "0-7475-3269-9",
            "Harry Potter and the Philosopher's Stone",
            "J. K. Rowling"
    );
}