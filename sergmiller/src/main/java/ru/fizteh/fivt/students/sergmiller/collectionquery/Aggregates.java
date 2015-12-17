package ru.fizteh.fivt.students.sergmiller.collectionquery;

import ru.fizteh.fivt.students.sergmiller.collectionquery.agregatesImpl.Avg;
import ru.fizteh.fivt.students.sergmiller.collectionquery.agregatesImpl.Count;
import ru.fizteh.fivt.students.sergmiller.collectionquery.agregatesImpl.Max;
import ru.fizteh.fivt.students.sergmiller.collectionquery.agregatesImpl.Min;

import java.util.function.Function;

/**
 * Aggregate functions.
 *
 * @author sergmiller
 */
public class Aggregates {

    /**
     * Maximum value for expression for elements of given collecdtion.
     *
     * @param expression
     * @param <C>
     * @param <T>
     * @return
     */
    public static <C, T extends Comparable> Function<C, T> max(Function<C, T> expression) {
        return new Max<>(expression);
    }

    /**
     * Minimum value for expression for elements of given collecdtion.
     *
     * @param expression
     * @param <C>
     * @param <T>
     * @return
     */
    public static <C, T extends Comparable> Function<C, T> min(Function<C, T> expression) {
        return new Min<>(expression);
    }

    /**
     * Number of items in source collection that turns this expression into not null.
     *
     * @param expression
     * @param <T>
     * @return
     */
    public static <T> Function<T, Long> count(Function<T, ?> expression) {
        return new Count<>(expression);
    }

    /**
     * Average value for expression for elements of given collection.
     *
     * @param expression
     * @param <T>
     * @return
     */
    public static <T, R extends Number> Function<T, R> avg(Function<T, R> expression) {
        return new Avg(expression);
    }

}



