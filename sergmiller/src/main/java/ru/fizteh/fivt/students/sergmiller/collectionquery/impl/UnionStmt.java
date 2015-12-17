package ru.fizteh.fivt.students.sergmiller.collectionquery.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by sergmiller on 09.10.15.
 */
public class UnionStmt<T, R> {
    private List<R> oldQueries = new ArrayList<>();
    private List<T> currentQuery = new ArrayList<>();
    private List<Tuple<T, R>> oldTupleElements = new ArrayList<>();
    private SelectStmt sParent;
    private boolean isTuple;

    public SelectStmt getsParent() {
        return sParent;
    }

    public UnionStmt(SelectStmt sParent) {
        this.sParent = sParent;
    }

    public <T> FromStmt<T> from(Iterable<T> iterable) {
        return new FromStmt<T>(iterable, this);
    }

    public <T> FromStmt<T> from(Stream<T> stream) {
        return new FromStmt<T>(stream, this);
    }
}


