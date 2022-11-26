package be.koder.library.domain;

import java.util.Optional;

public interface Repository<AGGREGATE_ID, AGGREGATE> {

    Optional<AGGREGATE> getById(AGGREGATE_ID id);

    void save(AGGREGATE aggregate);
}