package ru.fizteh.fivt.students.sergmiller.threadsTests;


import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.sergmiller.threads.BlockingQueue;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import static java.util.Arrays.*;


/**
 * Created by sergmiller on 07.12.15.
 */

public class BlockingQueueTest extends TestCase{
    final static int QUEUE_SIZE = 10;
    @Test
    public void testOperationPrioriesInSingleThread() throws Exception {
      BlockingQueue queue = new BlockingQueue<Integer>(10);
        List<Integer> e1 = asList(1, 2);
        List<Integer> e2 = asList(3, 4);
        List<Integer> e3 = asList(5, 6, 7);
        //List<Integer> e4 = asList(8, 9, 10, 11);

        queue.offer(e1);
        queue.offer(e2);
        queue.offer(e3);
        assertEquals(asList(1, 2, 3), queue.take(3, 1000));
        assertEquals(asList(4,5), queue.take(2, 1000));
//        assertEquals(null, queue.take(3, 1000));
    }
}
