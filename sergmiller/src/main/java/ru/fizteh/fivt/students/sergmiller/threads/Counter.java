package ru.fizteh.fivt.students.sergmiller.threads;

/**
 * Created by sergmiller on 05.12.15...
 */

final class Counter {
    private Counter() {
    }

    private static int countLast = 0;
    private static int totalNumber;

    private static Object syncObj = new Object();

    private static void runner(final int myNumber) {
        Thread thread = new Thread() {
            @SuppressWarnings("checkstyle.magicnumber")
            @Override
            public void run() {
                try {
                    synchronized (syncObj) {
                        while (true) {
                            if (myNumber == countLast) {
                                System.out.print("Thread-" + myNumber + "\n");
                                ++countLast;
                                if (countLast == totalNumber) {

                                    Thread.sleep(1000); //just for view
                                    countLast = 0;
                                }
                                syncObj.notifyAll();
                            } else {
                                syncObj.wait();
                            }
                        }
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        thread.start();
    }

    public static void main(final String[] arg) {
        totalNumber = new Integer(arg[0]);
        for (int i = 0; i < totalNumber; ++i) {
            runner(i);
        }
    }
}

