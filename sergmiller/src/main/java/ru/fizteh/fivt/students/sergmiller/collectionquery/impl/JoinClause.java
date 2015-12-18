package ru.fizteh.fivt.students.sergmiller.collectionquery.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created by sergmiller on 18.12.15.
 */

public class JoinClause<T, J> {
    private FromStmt<T> fromStmt;
    private Query<J> secondQuery;
    private List<T> firstElements = new ArrayList<>();
    private List<J> secondElements = new ArrayList<>();
    private List<Tuple<T, J>> joinedElements = new ArrayList<>();
    private BiPredicate<T, J> predicate;
    private Function<T, ? extends Comparable<?>> leftKey;
    private Function<J, ? extends Comparable<?>> rightKey;

//    public JoinClause(List<T> firstElements, Iterable<J> secondElements) {
//        this.firstElements
//                .addAll(firstElements.stream()
//                        .collect(Collectors.toList()));
//        secondElements.forEach(o -> this.secondElements.add(o));
//    }

    public FromStmt<T> getFromStmt() {
        return fromStmt;
    }

    public JoinClause(FromStmt<T> fromStmt, Query<J> query) {
        this.fromStmt = fromStmt;
        this.secondQuery = query;
    }

    public JoinClause(FromStmt<T> fromStmt, Stream<J> stream) {
        this.fromStmt = fromStmt;
        stream.forEach(o -> secondElements.add(o));
    }

    public JoinClause(FromStmt<T> fromStmt, Iterable<J> iterable) {
        this.fromStmt = fromStmt;
        iterable.forEach(o -> secondElements.add(o));
    }

    public FromStmt<Tuple<T, J>> on(BiPredicate<T, J> condition) {
//        firstElements.forEach(first ->
//                secondElements.forEach(second -> {
//                    if (condition.test(first, second)) {
//                        this.joinedElements.add(new Tuple<>(first, second));
//                    }
//                }));
        this.predicate = condition;
        this.leftKey = null;
        this.rightKey = null;
        return new FromStmt<>(this);
    }

    public <K extends Comparable<?>> FromStmt<Tuple<T, J>> on(
            Function<T, K> leftKey,
            Function<J, K> rightKey) {
        this.leftKey = leftKey;
        this.rightKey = rightKey;
        this.predicate = null;
        return new FromStmt<>(this);
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
        return new SelectStmt(fromStmt.getData(), clazz, false, fromStmt.getUparent(), this, s);
    }


    public final <F, S> SelectStmt<T, Tuple<F, S>> select(Function<T, F> first, Function<T, S> second) {
        return new SelectStmt(fromStmt.getData(), true, fromStmt.getUparent(), this, first, second);
    }

    @SafeVarargs
    public final <T, R> SelectStmt<T, R> selectDistinct(Class<R> clazz, Function<T, ?>... s) {
        return new SelectStmt(fromStmt.getData(), clazz, true, fromStmt.getUparent(), this, s);
    }

    public final <F, S> SelectStmt<T, Tuple<F, S>> selectDistinct(Function<T, F> first, Function<T, S> second) {
        return new SelectStmt(fromStmt.getData(), true, fromStmt.getUparent(), this, first, second);
    }

    /**
     * Selects the only defined expression as is without wrapper.
     *
     * @param s
     * @param <R>
     * @return statement resulting in collection of R
     */
//    public final <R> SelectStmt<T, R> selectDistinct(Function<T, R> s) {
//        throw new UnsupportedOperationException();
//    }

}
