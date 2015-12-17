package ru.fizteh.fivt.students.sergmiller.collectionquery.impl;

/**
 * Created by sergmiller on 06.10.15.
 */

import java.util.Comparator;

public class BestComparatorEver<R> implements Comparator<R> {
    private Comparator<R>[] currentComparators;

    BestComparatorEver(Comparator<R>... givenComparators) {
        currentComparators = givenComparators;
    }

    @Override
    public int compare(R first, R second) {
        for (Comparator<R> thisComparator : currentComparators) {
            int comparisonResult = thisComparator.compare(first, second);
            if (comparisonResult != 0) {
                return comparisonResult;
            }
        }

        return 0;
    }
}

