package ru.fizteh.fivt.students.sergmiller.collectionquery;

import java.util.Comparator;
import java.util.function.Function;

/**
 * OrderBy sort order helper methods.
 *
 * @author sergmiller
 */
public class OrderByConditions {

    /**
     * Ascending comparator.
     *
     * @param expression
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R extends Comparable<R>> Comparator<T> asc(Function<T, R> expression) {
        return (o1, o2) -> expression.apply(o1).compareTo(expression.apply(o2));
    }

    /**
     * Descending comparator.
     *
     * @param expression
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R extends Comparable<R>> Comparator<T> desc(Function<T, R> expression) {
        return (o1, o2) -> expression.apply(o2).compareTo(expression.apply(o1));
    }

}
