package ru.fizteh.fivt.students.sergmiller.threads;

import java.util.*;

/**
 * Created by sergmiller on 06.12.15.
 */

public class BlockingQueue<T> {
    private Object offerCounterAccessObj = new Object();
    private Object takeCounterAccessObj = new Object();
    private Object queueAccessObj = new Object();
    private Object actionSyncObj = new Object();
    private volatile int maxQueueSize;
    private volatile int queueSize;
    private volatile long currentOfferCounter;
    private volatile long currentTakeCounter;
    private volatile long offerCounter;
    private volatile long takeCounter;
    private volatile List<T> queue;


    public BlockingQueue(final int newMaxQueueSize) {
        maxQueueSize = newMaxQueueSize;
        currentOfferCounter = 0;
        currentTakeCounter = 0;
        offerCounter = 0;
        takeCounter = 0;
        queueSize = 0;
        queue = new LinkedList<>();
    }

    private class TimerThread extends Thread {
        private final long timeout;
        private Thread masterThread;

        TimerThread(final long newTimeout, Thread thread) {
            timeout = newTimeout;
            masterThread = thread;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(timeout);
                System.out.println("wake up");
                synchronized (masterThread) {
                    System.out.println("interrupt master");
                    masterThread.interrupt();
                }
            } catch (InterruptedException e) {
            }
        }
    }

    public final void offer(final List<T> list, final long timeout) {
        TimerThread timerThread = new TimerThread(timeout, Thread.currentThread());
        try {
            timerThread.start();
            offer(list);
            timerThread.interrupt();
        }
        catch(Exception e) {
        }
    }

    public final void offer(final List<T> list) {
        System.out.print("--");
        if (list.size() > maxQueueSize) {
            return;
        }
        System.out.print("-");
        long orderNumber;

        synchronized (offerCounterAccessObj) {
            orderNumber = offerCounter++;
            if (offerCounter == Long.MAX_VALUE) {
                offerCounter = 0;
            }
            //offerCounterAccessObj.notify();
        }

        try{
            synchronized (actionSyncObj) {
                while (true) {
                    if (currentOfferCounter == orderNumber) {

                        synchronized (queueAccessObj) {
                            synchronized (Thread.currentThread()) {
                                if (list.size() + queueSize <= maxQueueSize) {
//                                        ListIterator<T> it = list.listIterator();
//                                        while (it.hasNext()) {
//                                            queue.add(it.next());
//                                        }
                                    queue.addAll(list);

                                    queueSize += list.size();
                                    System.out.println(queueSize);

                                    ++currentOfferCounter;

                                    actionSyncObj.notifyAll();
                                    throw new InterruptedException("");
                                }

                                actionSyncObj.notifyAll();
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

//    public final List take(final int qnt, final long timeout) {
//        TimerThread timerThread = new TimerThread(timeout, Thread.currentThread());
//        List answer = new LinkedList<>();
//        try {
//            timerThread.start();
//             answer = take(qnt);
//            timerThread.interrupt();
//            // if(answer != null)
//        }catch(Exception e) {
//            return null;
//        }
//        return answer;
//    }

    public final List take(final int qnt, long timeout) {
        TimerThread timerThread = new TimerThread(timeout, Thread.currentThread());
        timerThread.start();
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

        System.out.println("i'm init");

        try {
            synchronized (actionSyncObj) {
                while (true) {
                    if (currentTakeCounter == orderNumber) {
                        synchronized (queueAccessObj) {
                            synchronized (Thread.currentThread()) {
                                if (qnt <= queueSize) {
                                    List answer = new LinkedList<>(queue.subList(0, qnt));
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

                                    ++currentTakeCounter;
                                    actionSyncObj.notifyAll();
                                    return answer;
                                }
                               // System.out.println("i'm here");
                                actionSyncObj.notifyAll();
                            }
                        }
                    } else {
                        System.out.println("i'm waited");
                        actionSyncObj.wait();
                    }
                }
            }
        } catch (InterruptedException e) {
            System.out.println("i'm interrupted");
            return null;
        }
    }
}



