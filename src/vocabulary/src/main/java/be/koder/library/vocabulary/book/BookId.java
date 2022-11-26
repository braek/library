package be.koder.library.vocabulary.book;

import java.util.Objects;
import java.util.UUID;

public final class BookId {

    private final UUID uuid;

    private BookId(UUID uuid) {
        this.uuid = uuid;
    }

    public static BookId createNew() {
        return new BookId(UUID.randomUUID());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookId bookId = (BookId) o;
        return Objects.equals(uuid, bookId.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public String toString() {
        return uuid.toString();
    }
}