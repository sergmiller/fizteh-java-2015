package ru.fizteh.fivt.students.sergmiller.collectionquery.agregatesImpl;


import java.util.List;
import java.util.function.Function;

/**
 * Created by sergmiller on 17.12.15.
 */
public class Count<T> implements Aggregator<T, Long> {
    private Function<T, ?> thisFunction;

    public Count(Function<T, ?> function) {
        thisFunction = function;
    }

    @Override
    public Long apply(List<T> list) {
        long count = 0;
        for (T e : list) {
            count += apply(e);
        }
        return count;
    }

    @Override
    public Long apply(T e) {
        if (thisFunction.apply(e) != null) {
            return 1L;
        }
        return 0L;
    }
}



