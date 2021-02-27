import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class PriorityQueueTest {

    @Test
    void basicInsertionAndSearching() {
        PriorityQueue priorityQueue = new PriorityQueue(5);
        // Insertion on empty list
        assertEquals(0, priorityQueue.add("node0", 9));
        assertEquals(0, priorityQueue.search("node0"));

        // Ordering via priority
        assertEquals(1, priorityQueue.add("node1", 0));
        assertEquals(0, priorityQueue.search("node0"));
        assertEquals(1, priorityQueue.search("node1"));

        // Ordering via priority
        assertEquals(1, priorityQueue.add("node2", 1));
        assertEquals(0, priorityQueue.search("node0"));
        assertEquals(1, priorityQueue.search("node2"));
        assertEquals(2, priorityQueue.search("node1"));

        // Ordering via order of insertion
        assertEquals(2, priorityQueue.add("node3", 1));
        assertEquals(0, priorityQueue.search("node0"));
        assertEquals(1, priorityQueue.search("node2"));
        assertEquals(2, priorityQueue.search("node3"));
        assertEquals(3, priorityQueue.search("node1"));
    }

    @Test
    void multiThreadedOperation() {
        PriorityQueue priorityQueue = new PriorityQueue(100);
        final ExecutorService pool = Executors.newFixedThreadPool(20);
        Runnable r = () -> {
            priorityQueue.add("node0" + Thread.currentThread().getName(), 9);
            priorityQueue.add("node1" + Thread.currentThread().getName(), 8);
            assertNotEquals(-1, priorityQueue.search("node0" + Thread.currentThread().getName()));
            assertNotEquals(-1, priorityQueue.search("node1" + Thread.currentThread().getName()));
            System.out.println(priorityQueue.getFirst());
            System.out.println(priorityQueue.getFirst());
        };

        for (int i = 0; i < 5; i++) {
            pool.submit(r);
        }

        pool.shutdown();
        try {
            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

//    @Test
//    void noInsertionOnFullQueue() {
//        // should endlessly loop
//        PriorityQueue priorityQueue = new PriorityQueue(2);
//        priorityQueue.add("node0", 9);
//        priorityQueue.add("node1", 9);
//        priorityQueue.add("node2", 9);
//    }

//    @Test
//    void waitOnEmptyQueue() {
//        // should endlessly loop
//        PriorityQueue priorityQueue = new PriorityQueue(1);
//        priorityQueue.getFirst();
//
//
//        priorityQueue.add("node0", 9);
//        //assertEquals("node0", priorityQueue.add());
//    }

    @Test
    void basicRemoval() {
        PriorityQueue priorityQueue = new PriorityQueue(5);

        // Ordering via priority
        priorityQueue.add("node0", 8);
        priorityQueue.add("node1", 5);
        priorityQueue.add("node2", 9);
        assertEquals("node2", priorityQueue.getFirst());
        assertEquals("node0", priorityQueue.getFirst());
        assertEquals("node1", priorityQueue.getFirst());
        assertEquals(-1, priorityQueue.search("node0"));
        assertEquals(-1, priorityQueue.search("node1"));
        assertEquals(-1, priorityQueue.search("node2"));

        // Ordering via order of insertion
        priorityQueue.add("node0", 9);
        priorityQueue.add("node1", 9);
        priorityQueue.add("node2", 9);
        assertEquals("node0", priorityQueue.getFirst());
        assertEquals("node1", priorityQueue.getFirst());
        assertEquals("node2", priorityQueue.getFirst());
        assertEquals(-1, priorityQueue.search("node0"));
        assertEquals(-1, priorityQueue.search("node1"));
        assertEquals(-1, priorityQueue.search("node2"));
    }
}