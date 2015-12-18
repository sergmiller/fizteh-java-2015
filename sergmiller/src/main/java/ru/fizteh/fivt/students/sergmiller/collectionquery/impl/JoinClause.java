package ru.fizteh.fivt.students.sergmiller.collectionquery.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created by sergmiller on 18.12.15.
 */

public class JoinClause<T, J> {
    private UnionStmt<?> unionStmt;
    private Query<J> sdQuery;
    private Query<T> ftQuery;
    private List<T> firstElements = new ArrayList<>();
    private List<J> secondElements = new ArrayList<>();
    private BiPredicate<T, J> predicate;
    private Function<T, ? extends Comparable<?>> leftKey;
    private Function<J, ? extends Comparable<?>> rightKey;

    public UnionStmt<?> getUnionStmt() {
        return unionStmt;
    }

    public JoinClause(UnionStmt<?> unionStmt, List<T> firstElements, Query<T> ftQuery, Query<J> sdQuery) {
        this.unionStmt = unionStmt;
        this.firstElements = firstElements;
        this.ftQuery = ftQuery;
        this.sdQuery = sdQuery;
        this.secondElements = null;
    }

    public JoinClause(UnionStmt<?> unionStmt, List<T> firstElements, Query<T> ftQuery, Stream<J> stream) {
        this.unionStmt = unionStmt;
        this.firstElements = firstElements;
        this.ftQuery = ftQuery;
        this.sdQuery = null;
        stream.forEach(o -> secondElements.add(o));
    }

    public JoinClause(UnionStmt<?> unionStmt, List<T> firstElements, Query<T> ftQuery, Iterable<J> iterable) {
        this.unionStmt = unionStmt;
        this.firstElements = firstElements;
        this.ftQuery = ftQuery;
        this.sdQuery = null;
        iterable.forEach(o -> secondElements.add(o));
    }

    public FromStmt<Tuple<T, J>> on(BiPredicate<T, J> condition) {
        this.predicate = condition;
        this.leftKey = null;
        this.rightKey = null;
        return new FromStmt<>(this, unionStmt);
    }

    public <K extends Comparable<?>> FromStmt<Tuple<T, J>> on(
            Function<T, K> leftKey,
            Function<J, K> rightKey) {
        this.leftKey = leftKey;
        this.rightKey = rightKey;
        this.predicate = null;
        return new FromStmt<>(this, unionStmt);
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


    public final List<Tuple<T, J>> execute() {
        List<Tuple<T, J>> joinedElements = new ArrayList<>();
        if (firstElements == null) {
            Iterable<T> res1 = ftQuery.execute();
            res1.forEach(o -> firstElements.add(o));
        }

        if (secondElements == null) {
            Iterable<J> res2 = sdQuery.execute();
            res2.forEach(o -> secondElements.add(o));
        }

        if (predicate == null) {
            HashMap<Object, Object> map = new HashMap<>();
            firstElements.forEach(e -> map.put(leftKey.apply(e), e));
            secondElements.forEach(e -> {
                T l = (T) map.get(rightKey.apply(e));
                if (l != null) {
                    joinedElements.add(new Tuple<>(l, e));
                }
            });
        } else {
            firstElements.forEach(first ->
                    secondElements.forEach(second -> {
                        if (predicate.test(first, second)) {
                            joinedElements.add(new Tuple<>(first, second));
                        }
                    }));
        }

        return joinedElements;
    }
}
