package ru.fizteh.fivt.students.sergmiller.collectionquery.impl;

import ru.fizteh.fivt.students.sergmiller.collectionquery.agregatesImpl.Aggregator;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by sergmiller on 06.10.15.
 */
public class SelectStmt<T, R> implements Query<R> {
    private boolean isDistinct;
    private boolean isUnion;
    private boolean isJoin;
    private int maxRawsNeeded;
    private Predicate<T> whereRestriction;
    private Predicate<R> havingRestriction;
    private Function<T, ?>[] currentFunctions;
    private Function<T, ?>[] groupByExpressions;
    private Class toReturn;
    private Comparator<R>[] orderByComparators;
    private BestComparatorEver<R> bestComparatorEver;
    private List<R> oldData;
    private Query<T> query;
    private List<T> currentData;
    private Stream<R> toStream;
    private UnionStmt uParent;
    private JoinClause jParent;

    @SafeVarargs
    public SelectStmt(List<T> elements,
                      Query<T> query,
                      Class<R> returnClass,
                      boolean isDistinct,
                      UnionStmt<?> uParent,
                      Function<T, ?>... functions) {
        this.query = query;
        this.oldData = new ArrayList<>();
        this.currentData = elements;
        this.toReturn = returnClass;
        this.isDistinct = isDistinct;
        this.currentFunctions = functions;
        this.maxRawsNeeded = -1;
        this.isUnion = false;
        this.isJoin = false;
        this.uParent = uParent;
    }

    public SelectStmt(JoinClause<?, ?> rcvJoinClause,
                      UnionStmt<?> rcvParentUnion, Function<T, ?>... rcvFunctions) {
        this.oldData = new ArrayList<>();
        this.currentData = null;
        this.jParent = rcvJoinClause;
        this.uParent = rcvParentUnion;
        this.currentFunctions = rcvFunctions;
        this.maxRawsNeeded = -1;
        this.isUnion = false;
        this.isJoin = true;
    }

    @SafeVarargs
    public SelectStmt(List<R> pastElements, List<T> elements, Class<R> returnClass, boolean isDistinct,
                      UnionStmt uParent, JoinClause jParent, Function<T, ?>... functions) {
        this.oldData = pastElements;
        this.currentData = elements;
        this.toReturn = returnClass;
        this.isDistinct = isDistinct;
        this.currentFunctions = functions;
        this.maxRawsNeeded = -1;
        this.isUnion = false;
        this.isJoin = true;
        this.uParent = uParent;
        this.jParent = jParent;
    }


    public SelectStmt(List<R> pastElements, List<T> elements, boolean isDistinct,
                      UnionStmt uParent, JoinClause jParent, Function<T, ?> first, Function<T, ?> second) {
        this.currentData = elements;
        this.toReturn = elements.get(0).getClass();
        this.isDistinct = isDistinct;
        this.currentFunctions = new Function[]{first, second};
        this.maxRawsNeeded = -1;
        this.isUnion = true;
        this.isJoin = true;
        this.oldData = pastElements;
        this.uParent = uParent;
        this.jParent = jParent;
    }

    public Class getToReturn() {
        return toReturn;
    }

    public Function<T, ?>[] getCurrentFunctions() {
        return currentFunctions;
    }

    public List<T> getCurrentData() {
        return currentData;
    }


    public UnionStmt getuParent() {
        return uParent;
    }


    public SelectStmt<T, R> where(Predicate<T> predicate) {
        this.whereRestriction = predicate;
        return this;
    }

    private List<R> applyDistinct(List<R> execRes) {
        if (isDistinct) {
            execRes = execRes.stream().distinct().collect(Collectors.toList());
        }
        return execRes;
    }

    private List<R> applyHaving(List<R> execRes) {
        if (havingRestriction != null) {
            List<R> filtredData = execRes.stream()
                    .filter(havingRestriction::test)
                    .collect(Collectors.toList());
            execRes = filtredData;
        }
        return execRes;
    }

    private List<R> applyOrderBy(List<R> execRes) {
        if (orderByComparators != null) {
            execRes.sort(bestComparatorEver);
        }
        return execRes;
    }

    private List<R> applyLimit(List<R> execRes) {
        if (maxRawsNeeded != -1) {
            if (maxRawsNeeded < execRes.size()) {
                execRes = execRes.subList(0, maxRawsNeeded);
            }
        }
        return execRes;
    }

    private List<R> applyUnion(List<R> execRes) {
        if (uParent != null) {
            final Iterable<R> subQuery = uParent.getsParent().execute();
            List fullExecResult = new LinkedList<>();
            subQuery.forEach(o -> fullExecResult.add(o));
            fullExecResult.addAll(execRes);
            return fullExecResult;
        }

        return execRes;
    }


    @Override
    public Iterable<R> execute() throws NoSuchElementException {
        try {
            List<R> executeResult = new LinkedList<>();
            if (jParent != null) {
                currentData = (List<T>) jParent.execute();
            }

            Class[] returnClasses = new Class[currentFunctions.length];
            Object[] arguments = new Object[currentFunctions.length];
            if (whereRestriction != null) {
                List<T> filtredData = currentData.stream()
                        .filter(whereRestriction::test)
                        .collect(Collectors.toList());
                currentData = filtredData;
            }

            if (groupByExpressions != null) {
                Map<Object[], Integer> mapped = new HashMap<>();
                List<List<T>> buckets = new ArrayList<>();
                Object[] results = new Object[groupByExpressions.length];
                currentData.stream().forEach(
                        element -> {
                            for (int i = 0; i < groupByExpressions.length; i++) {
                                results[i] = groupByExpressions[i].apply(element);
                            }
                            if (!mapped.containsKey(results)) {
                                mapped.put(results, mapped.size());
                                buckets.add(new ArrayList<>());
                            }

                            buckets.get(buckets.size() - 1).add(element);
                        }
                );

                for (List<T> group : buckets) {
                    int counter = 0;
                    for (Function thisFunction : this.currentFunctions) {
                        if (thisFunction instanceof Aggregator) {
                            arguments[counter] = ((Aggregator) thisFunction).apply(group);
                        } else {
                            arguments[counter] = thisFunction.apply(group.get(0));
                        }
                        returnClasses[counter] = arguments[counter].getClass();
                        ++counter;
                    }

                    if (isJoin) {
                        Tuple newElement = new Tuple(arguments[0], arguments[1]);
                        executeResult.add((R) newElement);
                    } else {
                        R newElement = (R) toReturn.getConstructor(returnClasses).newInstance(arguments);
                        executeResult.add(newElement);
                    }
                }

            } else {
                for (T elem : currentData) {
                    int counter = 0;
                    for (Function thisFunction : this.currentFunctions) {
                        List<T> thisElement = new ArrayList<>();
                        thisElement.add(elem);
                        if (thisFunction instanceof Aggregator) {
                            arguments[counter] = ((Aggregator) thisFunction).apply(thisElement);
                        } else {
                            arguments[counter] = thisFunction.apply(elem);
                        }
                        returnClasses[counter] = arguments[counter].getClass();
                        ++counter;
                    }


                    if (isJoin) {
                        Tuple newElement = new Tuple(arguments[0], arguments[1]);
                        executeResult.add((R) newElement);
                    } else {
                        R newElement = (R) toReturn.getConstructor(returnClasses).newInstance(arguments);
                        executeResult.add(newElement);
                    }
                }

            }

            return  applyUnion(
                        applyDistinct(
                                applyHaving(
                                        applyLimit(
                                                applyOrderBy(
                                                        executeResult)))));

        } catch (NoSuchMethodException | IllegalAccessException
                | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public final Stream<R> stream() {
        throw new UnsupportedOperationException();
    }

    public final SelectStmt<T, R> limit(int limit) {
        maxRawsNeeded = limit;
        return this;
    }

    public final SelectStmt<T, R> having(Predicate<R> predicate) {
        this.havingRestriction = predicate;
        return this;
    }

    @SafeVarargs
    public final SelectStmt<T, R> groupBy(Function<T, ?>... expressions) {
        this.groupByExpressions = expressions;
        return this;
    }

    @SafeVarargs
    public final SelectStmt<T, R> orderBy(Comparator<R>... comparators) {
        this.orderByComparators = comparators;
        this.bestComparatorEver = new BestComparatorEver<>(comparators);
        return this;
    }

    public final UnionStmt<R> union() throws NoSuchMethodException, IllegalAccessException,
            InstantiationException, InvocationTargetException {
        return new UnionStmt<>(this);
    }
}


