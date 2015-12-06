package ru.fizteh.fivt.students.sergmiller.threads;

import java.util.Random;

/**
 * Created by sergmiller on 06.12.15.
 */
final class Rollcall {
    private Rollcall() {
    }

    private static Random random = new Random();
    private static volatile int counter = 0;
    private static volatile int answers = 0;
    private static volatile boolean exitFlag = false;
    private static volatile int totalNumber;

    private static Object syncObj = new Object();

    private static void runSlave() {
        Thread thread = new Thread() {
            @SuppressWarnings("checkstyle.magicnumber")
            @Override
            public void run() {
                try {
                    synchronized (syncObj) {
                        while (true) {
                            if (exitFlag) {
                                throw new InterruptedException("");
                            }
                            if (counter < totalNumber) {

                                if (random.nextInt(10) == 0) {
                                    System.out.print("No\n");
                                } else {
                                    System.out.print("Yes\n");
                                    ++answers;
                                }
                                ++counter;
                                syncObj.notify();
                            }

                            syncObj.wait();
                        }
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        thread.start();
    }

    private static void runMaster() {
        Thread thread = new Thread() {
            @SuppressWarnings("checkstyle.magicnumber")
            @Override
            public void run() {
                try {
                    synchronized (syncObj) {
                        while (true) {
                            if (counter == totalNumber) {
                                if (answers < totalNumber) {
                                    System.out.print("Are you ready?\n");
                                    answers = 0;
                                    counter = 0;
                                    Thread.sleep(1000); //just for view
                                } else {
                                    exitFlag = true;
                                    syncObj.notifyAll();
                                    throw new InterruptedException("");
                                }
                                syncObj.notifyAll();
                            } else {
                                syncObj.notify();
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
        counter = totalNumber;
        runMaster();
        for (int i = 0; i < totalNumber; ++i) {
            runSlave();
        }
    }
}
