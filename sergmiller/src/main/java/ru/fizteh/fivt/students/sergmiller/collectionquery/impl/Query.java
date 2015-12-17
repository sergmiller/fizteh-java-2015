package ru.fizteh.fivt.students.sergmiller.collectionquery.impl;

import java.util.stream.Stream;

/**
 * @author sergmiller
 */
public interface Query<R> {

    Iterable<R> execute();

    Stream<R> stream();
}

