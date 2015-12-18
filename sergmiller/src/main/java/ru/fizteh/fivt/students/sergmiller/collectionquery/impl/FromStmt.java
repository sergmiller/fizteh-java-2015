package ru.fizteh.fivt.students.sergmiller.collectionquery.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created by sergmiller on 06.10.15.
 */
public class FromStmt<T> {
    private List<T> data = new ArrayList<>();
    private UnionStmt<?> uParent;
    private JoinClause<?, ?> joinInside;
    private Query<T> query;

    public List<T> getData() {
        return data;
    }

    public UnionStmt getUparent() {
        return uParent;
    }

    public JoinClause getJoinInside() {
        return joinInside;
    }

    public Query<T> getQuery() {
        return query;
    }

    public FromStmt(Iterable<T> iterable) {
        iterable.forEach(e -> data.add(e));
    }

    public FromStmt(Stream<T> stream) {
        stream.forEach(e -> data.add(e));
    }

    public FromStmt(Query rcvQuery) {
        this.query = rcvQuery;
    }

    public <R> FromStmt(JoinClause<?, ?> clause, UnionStmt<?> rcvParentUnion) {
        this.joinInside = clause;
        this.uParent = rcvParentUnion;
    }

    public <R> FromStmt(Iterable<T> iterable, UnionStmt<?> rcvParentUnion) {
        this.uParent = rcvParentUnion;
        iterable.forEach(e -> data.add(e));
    }

    public <R> FromStmt(Stream<T> stream, UnionStmt<?> rcvParentUnion) {
        this.uParent = rcvParentUnion;
        stream.forEach(e -> data.add(e));
    }

    public <R> FromStmt(Query rcvQuery, UnionStmt<?> rcvParentUnion) {
        this.uParent = rcvParentUnion;
        this.query = rcvQuery;
    }

    public static <T> FromStmt<T> from(Iterable<T> iterable) {
        return new FromStmt<T>(iterable);
    }

    public static <T> FromStmt<T> from(Stream<T> stream) {
        return new FromStmt<T>(stream);
    }

    public static <T> FromStmt<T> from(Query query) {
        return new FromStmt<T>(query);

    }

//    /**
//     * Selects the only defined expression as is without wrapper.
//     *
//     * @param s
//     * @param <R>
//     * @return statement resulting in collection of R
//     */
//    public final <R> SelectStmt<T, R> select(Function<T, R> s) {
//        throw new UnsupportedOperationException();
//    }

    @SafeVarargs
    public final <R> SelectStmt<T, R> select(Class<R> clazz, Function<T, ?>... s) {
        return new SelectStmt<>(data, query, clazz, false, (UnionStmt<R>) uParent, s);
    }

    public final <F, S> SelectStmt<T, Tuple<F, S>> select(Function<T, F> first, Function<T, S> second) {
        return new SelectStmt<T, Tuple<F, S>>(joinInside, uParent, first, second);
    }


    public final <R> SelectStmt<T, R> selectDistinct(Function<T, R> f) {
        return new SelectStmt<>(data, query, null, true, uParent, f);
    }

    @SafeVarargs
    public final <R> SelectStmt<T, R> selectDistinct(Class<R> clazz, Function<T, ?>... f) {
        return new SelectStmt<>(data, query, clazz, true, uParent, f);
    }


    public <J> JoinClause<T, J> join(Iterable<J> iterable) {
        return new JoinClause<>(uParent, data, query, iterable);
    }

    public <J> JoinClause<T, J> join(Stream<J> stream) {
        return new JoinClause<>(uParent, data, query, stream);
    }

    public <J> JoinClause<T, J> join(Query<J> sdQuery) {
        return new JoinClause<>(uParent, data, query, sdQuery);
    }

}


