package ru.fizteh.fivt.students.sergmiller.collectionquery.agregatesImpl;

import java.util.List;
import java.util.function.Function;

/**
 * Created by sergmiller on 17.12.15.
 */
public class Avg<T> implements Aggregator<T, Double> {
    private Function<T, ? extends Number> thisFunction;

    public Avg(Function<T, ? extends Number> function) {
        thisFunction = function;
    }

    @Override
    public Double apply(List<T> list) {
        Double res = 0.0;
        if (list.size() == 0) {
            return res;
        }

        for (T e : list) {
            res += apply(e);
        }

        return res / list.size();
    }

    @Override
    public Double apply(T e) {
        if (thisFunction.apply(e) != null) {
            return thisFunction.apply(e).doubleValue();
        }
        return 0D;
    }
}



