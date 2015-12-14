package ru.fizteh.fivt.students.sergmiller.threads;

import java.util.*;

/**
 * Created by sergmiller on 06.12.15.
 */

public class BlockingQueue<T> {
    private Object offerCounterAccessObj = new Object();
    private Object takeCounterAccessObj = new Object();
    private Object queueAccessSyncObj = new Object();
    private Object actionSyncObj = new Object();
    private int maxQueueSize;
    private int queueSize;
    private volatile long currentOfferCounter;
    private volatile long currentTakeCounter;
    private long offerCounter;
    private long takeCounter;
    private List<T> queue;

    public BlockingQueue(final int newMaxQueueSize) {
        maxQueueSize = newMaxQueueSize;
        currentOfferCounter = 0;
        currentTakeCounter = 0;
        offerCounter = 0;
        takeCounter = 0;
        queueSize = 0;
        queue = new LinkedList<>();
    }

    public final void offer(final List<T> list) {
        offer(list, 0);
    }

    public final void offer(final List<T> list, final long timeout) {
        final boolean existTimeLimit;
        if (timeout > 0) {
            existTimeLimit = true;
        } else {
            existTimeLimit = false;
        }
        long timeToStop = System.currentTimeMillis() + timeout;
        long timeToSleep;

        if (list.size() > maxQueueSize) {
            return;
        }
        long orderNumber;

        synchronized (offerCounterAccessObj) {
            orderNumber = offerCounter++;
            if (offerCounter == Long.MAX_VALUE) {
                offerCounter = 0;
            }
        }

        try {
            synchronized (actionSyncObj) {
                while (true) {
                    if (currentOfferCounter == orderNumber) {
                        if (list.size() + queueSize <= maxQueueSize) {
                            synchronized (queueAccessSyncObj) {
                                queue.addAll(list);
                                queueSize += list.size();
                            }

                            ++currentOfferCounter;
                            actionSyncObj.notifyAll();
                            //System.out.println("I'm ended");
                            throw new InterruptedException("");
                        }
                    }
                    //System.out.println("I'm wait");
                    actionSyncObj.notifyAll();
                    if (existTimeLimit) {
                        timeToSleep = timeToStop - System.currentTimeMillis();
                        if (timeToSleep < 0) {
                            ++currentOfferCounter;
                            actionSyncObj.notifyAll();
                            //System.out.println("I'm suspended");
                            throw new InterruptedException("");
                        }
                        actionSyncObj.wait(timeToSleep);
                    } else {
                        actionSyncObj.wait();
                    }
                }
            }
        } catch (InterruptedException e) {
            return;
        }
    }

    public final List take(final int qnt) {
        return take(qnt, 0);
    }

    public final List take(final int qnt, final long timeout) {
        final boolean existTimeLimit;
        if (timeout > 0) {
            existTimeLimit = true;
        } else {
            existTimeLimit = false;
        }
        long timeToStop = System.currentTimeMillis() + timeout;
        long timeToSleep;

        if (qnt == 0) {
            return new LinkedList<>();
        }

        if (qnt > maxQueueSize) {
            return null;
        }

        long orderNumber;

        synchronized (takeCounterAccessObj) {
            orderNumber = takeCounter++;
            if (takeCounter == Long.MAX_VALUE) {
                takeCounter = 0;
            }
        }

        try {
            synchronized (actionSyncObj) {
                while (true) {
                    if (currentTakeCounter == orderNumber) {
                        if (qnt <= queueSize) {
                            List answer;
                            synchronized (queueAccessSyncObj) {
                                answer = new LinkedList<>(queue.subList(0, qnt));
                                queue.subList(0, qnt).clear();
                                queueSize -= qnt;
                            }

                            ++currentTakeCounter;
                            actionSyncObj.notifyAll();
                            //System.out.println("I'm ended");
                            return answer;
                        }
                    }
                    //System.out.println("I'm wait");
                    actionSyncObj.notifyAll();
                    if (existTimeLimit) {
                        timeToSleep = timeToStop - System.currentTimeMillis();
                        if (timeToSleep < 0) {
                            ++currentTakeCounter;
                            actionSyncObj.notifyAll();
                            //System.out.println("I'm suspended");
                            throw new InterruptedException("");
                        }
                        actionSyncObj.wait(timeToSleep);
                    } else {
                        actionSyncObj.wait();
                    }
                }
            }
        } catch (InterruptedException e) {
            return null;
        }
    }
}



