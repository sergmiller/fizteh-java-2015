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

    public List<T> getData() {
        return data;
    }

    public UnionStmt getUparent() {
        return uParent;
    }

    public FromStmt(Iterable<T> iterable, UnionStmt uParent) {
        iterable.forEach(o -> data.add(o));
        this.uParent = uParent;

    }

    public FromStmt(Stream<T> stream, UnionStmt uParent) {
        stream.forEach(o -> data.add(o));
        this.uParent = uParent;
    }


    public static <T> FromStmt<T> from(Iterable<T> iterable) {
        return new FromStmt<T>(iterable, null);
    }

    public static <T> FromStmt<T> from(Stream<T> stream) {
        return new FromStmt<T>(stream, null);
    }

    public static <T> FromStmt<T> from(Query query) {
        throw new UnsupportedOperationException();
    }

    @SafeVarargs
    public final <R> SelectStmt<T, R> select(Class<R> clazz, Function<T, ?>... s) {
        return new SelectStmt<>(data, clazz, false, uParent, s);
    }

    /**
     * Selects the only defined expression as is without wrapper.
     *
     * @param s
     * @param <R>
     * @return statement resulting in collection of R
     */
    public final <R> SelectStmt<T, R> select(Function<T, R> s) {
        throw new UnsupportedOperationException();
    }

//    /**
//     * Selects the only defined expression as is without wrapper.
//     *
//     * @param first
//     * @param second
//     * @param <F>
//     * @param <S>
//     * @return statement resulting in collection of R
//     */
//    public final <F, S> SelectStmt<T, Tuple<F, S>> select(Function<T, F> first, Function<T, S> second) {
//        return new SelectStmt(data, false, uParent, first, second);
//    }

    @SafeVarargs
    public final <T, R> SelectStmt<T, R> selectDistinct(Class<R> clazz, Function<T, ?>... s) {
        return new SelectStmt(data, clazz, true, uParent, s);
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

//    public <J> JoinClause<T, J> join(Iterable<J> iterable) {
//        return new JoinClause<>(data, iterable);
//    }
//
//
//    public class JoinClause<T, J> {
//        private List<T> firstElements = new ArrayList<>();
//        private List<J> secondElements = new ArrayList<>();
//        private List<Tuple<T, J>> joinedElements = new ArrayList<>();
//
//        public JoinClause(List<T> firstElements, Iterable<J> secondElements) {
//            this.firstElements
//                    .addAll(firstElements.stream()
//                            .collect(Collectors.toList()));
//            secondElements.forEach(o -> this.secondElements.add(o));
//        }
//
//        public FromStmt<Tuple<T, J>> on(BiPredicate<T, J> condition) {
//            firstElements.forEach(first ->
//                    secondElements.forEach(second -> {
//                        if (condition.test(first, second)) {
//                            this.joinedElements.add(new Tuple<>(first, second));
//                        }
//                    }));
//            return new FromStmt<T>(joinedElements, );
//        }
//
//        public <K extends Comparable<?>> FromStmt<Tuple<T, J>> on(
//                Function<T, K> leftKey,
//                Function<J, K> rightKey) {
//            throw new UnsupportedOperationException();
//        }
//    }
}
