package ru.fizteh.fivt.students.sergmiller.threads;

import java.util.*;

/**
 * Created by sergmiller on 06.12.15.
 */

final class BlockingQueue<T> {
    private Object offerCounterAccessObj = new Object();
    private Object takeCounterAccessObj = new Object();
    private Object queueAccessObj = new Object();
    private Object actionSyncObj = new Object();
    private volatile int maxQueueSize;
    private volatile int queueSize;
    private volatile long offerCounter;
    private volatile long takeCounter;
    private List queue;


    BlockingQueue(final int newMaxQueueSize) {
        maxQueueSize = newMaxQueueSize;
        offerCounter = 0;
        takeCounter = 0;
        queueSize = 0;
        queue = new LinkedList<T>();
    }

    private class TimerThread extends Thread {
        private final long timeout;
        private final Thread masterThread;

        TimerThread(final long newTimeout, final Thread thread) {
            timeout = newTimeout;
            masterThread = thread;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(timeout);
                synchronized (masterThread) {
                    masterThread.interrupt();
                }
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    void offer(final List<T> list, final long timeout) {
        TimerThread slaveThread = new TimerThread(timeout, Thread.currentThread());
        slaveThread.start();
        offer(list);
    }

    void offer(final List<T> list) {
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
                    if (offerCounter == orderNumber) {

                        synchronized (queueAccessObj) {
                            synchronized (this) {
                                if (list.size() + queueSize <= maxQueueSize) {
//                                        ListIterator<T> it = list.listIterator();
//                                        while (it.hasNext()) {
//                                            queue.add(it.next());
//                                        }
                                    queue.add(list);

                                    queueSize += list.size();

                                    ++offerCounter;
                                }
                                actionSyncObj.notifyAll();
                                return;
                            }
                        }
                    } else {
                        actionSyncObj.wait();
                    }
                }
            }
        } catch (InterruptedException e) {
            return;
        }
    }

    List<T> take(final int qnt, final long timeout) {
        TimerThread slaveThread = new TimerThread(timeout, Thread.currentThread());
        slaveThread.start();
        return take(qnt);
    }

    List<T> take(final int qnt) {
        long orderNumber;
        List answer = new LinkedList<T>();

        synchronized (takeCounterAccessObj) {
            orderNumber = takeCounter++;
            if (takeCounter == Long.MAX_VALUE) {
                takeCounter = 0;
            }
        }

        try {
            synchronized (actionSyncObj) {
                while (true) {
                    if (takeCounter == orderNumber) {
                        synchronized (queueAccessObj) {
                            synchronized (this) {
                                if (qnt <= queueSize) {
                                    answer = new LinkedList<>(queue.subList(0, qnt));
                                    queue.subList(0, qnt).clear();
//                                    ListIterator<T> it = queue.listIterator();
//                                    int i = 0;
//                                    while (i < qnt) {
//                                        answer.add(it.next());
//                                        it.remove();
//                                        it.hasNext();
//
//                                        ++i;
//                                    }
                                    queueSize -= qnt;

                                    ++takeCounter;
                                }

                                actionSyncObj.notifyAll();
                                return answer;
                            }
                        }
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



