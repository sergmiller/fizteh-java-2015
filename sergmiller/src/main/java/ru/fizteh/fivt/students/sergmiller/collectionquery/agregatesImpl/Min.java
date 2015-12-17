package ru.fizteh.fivt.students.sergmiller.collectionquery.agregatesImpl;


import java.util.List;
import java.util.function.Function;

/**
 * Created by sergmiller on 17.12.15.
 */
public class Min<T, R extends Comparable<R>> implements Aggregator<T, R> {
    private Function<T, R> thisFunction;

    public Min(Function<T, R> function) {
        thisFunction = function;
    }

    @Override
    public R apply(List<T> list) {
        if (list.size() == 0) {
            return null;
        }

        R answer = apply(list.get(0));

        for (T e : list) {
            if (answer.compareTo(apply(e)) > 0) {
                answer = apply(e);
            }
        }

        return answer;
    }

    @Override
    public R apply(T e) {
        return thisFunction.apply(e);
    }
}



