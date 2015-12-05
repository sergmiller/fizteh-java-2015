package ru.fizteh.fivt.students.sergmiller.threads;

/**
 * Created by sergmiller on 05.12.15...
 */

final class Counter {
    static volatile int countLast = 0;
    static volatile int totalNumber;
    private static final Object monitor = new Object();

    private static void runner(final int myNumber) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (monitor) {
                        while (true) {
                            if (myNumber == countLast) {
                                System.out.print("Thread-" + myNumber + "\n");
                                ++countLast;
                                if (countLast == totalNumber) {
                                    Thread.sleep(1000);//just for view
                                    countLast = 0;
                                }
                                monitor.notifyAll();
                            } else {
                                monitor.wait();
                            }
                        }
                    }
                } catch (InterruptedException e) {
                }
            }
        });

        thread.start();
    }

    public static void main(String arg[]) {
        totalNumber = new Integer(arg[0]);
        for (int i = 0; i < totalNumber; ++i) {
            runner(i);
        }
    }
}