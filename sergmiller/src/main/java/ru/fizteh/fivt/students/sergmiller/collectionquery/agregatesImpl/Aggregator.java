package ru.fizteh.fivt.students.sergmiller.collectionquery.agregatesImpl;

import java.util.List;
import java.util.function.Function;

/**
 * Created by sergmiller on 17.12.15.
 */

public interface Aggregator<T, C> extends Function<T, C> {
    default C apply(List<T> currentElements) {
        return null;
    }
}

