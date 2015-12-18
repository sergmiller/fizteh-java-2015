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
    private UnionStmt uParent;
    private JoinClause jParent;
    private Query<T> query;

    public List<T> getData() {
        return data;
    }

    public UnionStmt getUparent() {
        return uParent;
    }

    public JoinClause getJparent() {
        return jParent;
    }

    public FromStmt(Iterable<T> iterable, UnionStmt uParent, JoinClause jParent) {
        iterable.forEach(o -> data.add(o));
        this.query = null;
        this.uParent = uParent;
        this.jParent = jParent;
    }

    public FromStmt(Stream<T> stream, UnionStmt uParent, JoinClause jParent) {
        stream.forEach(o -> data.add(o));
        this.query = null;
        this.uParent = uParent;
        this.jParent = jParent;
    }

    public FromStmt(Query<T> query, UnionStmt uParent, JoinClause jParent) {
        this.data = null;
        this.query = query;
        this.uParent = uParent;
        this.jParent = jParent;
    }

    public FromStmt(JoinClause joinClause) {
        this.data = joinClause.getFromStmt().data;
        this.query = joinClause.getFromStmt().query;
        this.uParent = joinClause.getFromStmt().uParent;
        this.jParent = joinClause;
    }


    public static <T> FromStmt<T> from(Iterable<T> iterable) {
        return new FromStmt<T>(iterable, null, null);
    }

    public static <T> FromStmt<T> from(Stream<T> stream) {
        return new FromStmt<T>(stream, null, null);
    }

    public static <T> FromStmt<T> from(Query query) {
        return new FromStmt<T>(query, null, null);

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
        return new SelectStmt<>(data, clazz, false, uParent, jParent, s);
    }

    public final <F, S> SelectStmt<T, Tuple<F, S>> select(Function<T, F> first, Function<T, S> second) {
        return new SelectStmt(data, false, uParent, jParent, first, second);
    }

    @SafeVarargs
    public final <T, R> SelectStmt<T, R> selectDistinct(Class<R> clazz, Function<T, ?>... s) {
        return new SelectStmt(data, clazz, true, uParent, jParent, s);
    }

    /**
     * Selects the only defined expression as is without wrapper.
     *
     * @param s
     * @param <R>
     * @return statement resulting in collection of R
     */
    public final <R> SelectStmt<T, R> selectDistinct(Function<T, R> s) {
        throw new UnsupportedOperationException();
    }

    public <J> JoinClause<T, J> join(Iterable<J> iterable) {
        return new JoinClause<>(this, iterable);
    }

    public <J> JoinClause<T, J> join(Stream<J> stream) {
        return new JoinClause<>(this, stream);
    }

    public <J> JoinClause<T, J> join(Query<J> query) {
        return new JoinClause<>(this, query);
    }

}


