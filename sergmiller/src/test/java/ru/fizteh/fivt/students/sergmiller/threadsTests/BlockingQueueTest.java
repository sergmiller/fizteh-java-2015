package ru.fizteh.fivt.students.sergmiller.threadsTests;


import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.sergmiller.threads.BlockingQueue;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.Arrays.*;


/**
 * Created by sergmiller on 07.12.15.
 */

public class BlockingQueueTest extends TestCase {
    final static int QUEUE_SIZE = 10;
    List<Integer> e1;
    List<Integer> e2;
    List<Integer> e3;
    List<Integer> e4;

    private static class OfferThread extends Thread {
        List<Integer> addedList;
        BlockingQueue<Integer> queue;
        final long timeout;

        OfferThread(BlockingQueue<Integer> _queue, List<Integer> _addedList) {
            queue = _queue;
            addedList = _addedList;
            timeout = 0;
        }

        OfferThread(BlockingQueue<Integer> _queue, List<Integer> _addedList, final long _timeout) {
            queue = _queue;
            addedList = _addedList;
            timeout = _timeout;
        }

        @Override
        public void run() {
            queue.offer(addedList, timeout);
        }
    }

    private static class TakeThread extends Thread {
        List<Integer> responce = asList(-1);
        final Integer request;
        BlockingQueue<Integer> queue;
        final long timeout;

        TakeThread(BlockingQueue<Integer> _queue, final Integer _request) {
            queue = _queue;
            request = _request;
            timeout = 0;
        }

        TakeThread(BlockingQueue<Integer> _queue, final Integer _request, final long _timeout) {
            queue = _queue;
            request = _request;
            timeout = _timeout;
        }

        @Override
        public void run() {
            responce = queue.take(request, timeout);
        }
    }

    @Before
    public void setUp() throws Exception {
        e1 = asList(1, 2);
        e2 = asList(3, 4);
        e3 = asList(5, 6, 7);
        e4 = asList(8, 9, 10, 11);
    }

    @Test
    public void testSingleThreadWork() {
        BlockingQueue queue = new BlockingQueue<Integer>(QUEUE_SIZE);
        queue.offer(e1);
        queue.offer(e2);
        assertEquals(asList(1, 2, 3), queue.take(3));
        assertEquals(asList(4), queue.take(1));
    }

    @Test
    public void testOfferSuspended() throws Exception {
        BlockingQueue queue = new BlockingQueue<Integer>(QUEUE_SIZE / 2);
        queue.offer(e1);
        queue.offer(e2);
        queue.offer(e3, 300);
        Thread.sleep(400);
        assertNotNull(queue.take(3));
        queue.offer(e4, 300);
        assertEquals(queue.take(5), asList(4, 8, 9, 10, 11));
    }

    @Test
    public void testTakeSuspended() throws Exception {
        BlockingQueue queue = new BlockingQueue<Integer>(QUEUE_SIZE / 2);
        queue.offer(e1);
        queue.offer(e2);
        queue.take(5, 300);
        Thread.sleep(400);
        assertNotNull(queue.take(3, 300));
        queue.offer(e4);
        assertEquals(queue.take(5), asList(4, 8, 9, 10, 11));
    }

    @Test
    public void testThreadCommunication() throws Exception {
        BlockingQueue queue = new BlockingQueue<Integer>(QUEUE_SIZE);
        OfferThread thread1 = new OfferThread(queue, e1, 200);
        OfferThread thread2 = new OfferThread(queue, e2, 200);
        OfferThread thread3 = new OfferThread(queue, e3, 200);
        OfferThread thread4 = new OfferThread(queue, e4, 200);
        TakeThread thread5 = new TakeThread(queue, 1, 200);
        TakeThread thread6 = new TakeThread(queue, 10, 200);
        thread5.start();
        thread1.start();
        thread2.start();
        thread3.start();
        thread6.start();
        thread4.start();
        Thread.sleep(300);
        assertNull(queue.take(1, 100));
    }
}

