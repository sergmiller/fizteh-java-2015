package ru.fizteh.fivt.students.sergmiller.collectionquery.impl;

import java.util.stream.Stream;

/**
 * Created by sergmiller on 09.10.15.
 */
public class UnionStmt<R> {
    private SelectStmt sParent;

    public UnionStmt(SelectStmt<?, R> rcvParent) {
        this.sParent = rcvParent;
    }

    public SelectStmt getsParent() {
        return sParent;
    }

    public final <T> FromStmt<T> from(Iterable<T> list) {
        return new FromStmt<T>(list, this);
    }

    public final <T> FromStmt<T> from(Stream<T> stream) {
        return new FromStmt<T>(stream, this);
    }

    public final <T> FromStmt<T> from(Query query) {
        return new FromStmt<T>(query, this);
    }

//    public final LinkedList<R> execute() throws CqlException {
//        return parent.executeGetLinkedList();
//    }
}


