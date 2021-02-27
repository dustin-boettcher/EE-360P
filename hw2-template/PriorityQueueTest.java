import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import javax.print.attribute.standard.RequestingUserName;
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
    void multiThreadedOperation() throws InterruptedException {
        int maxSize = 20;
        int numThreads = 10;
        PriorityQueue priorityQueue = new PriorityQueue(maxSize);
        final ExecutorService pool = Executors.newFixedThreadPool(numThreads);
        Runnable r1 = () -> {
            priorityQueue.add("node0" + Thread.currentThread().getName(), 9);
            priorityQueue.add("node1" + Thread.currentThread().getName(), 8);
        };

        Runnable r2 = () -> {
            System.out.println(priorityQueue.getFirst());
            System.out.println(priorityQueue.getFirst());
        };

        for (int i = 0; i < numThreads/2; i++) {
            pool.submit(r1);
            pool.submit(r2);
        }

        System.out.println("Sleeping main thread for 5 seconds (add + remove)");
        Thread.sleep(5000);

        Runnable s0 = () -> {
            System.out.println(priorityQueue.getFirst());
        };

        pool.submit(s0);

        System.out.println("Sleeping main thread for 5 seconds (remove then add)");
        Thread.sleep(5000);

        Runnable s1 = () -> {
            priorityQueue.add("node0", 9);
        };

        pool.submit(s1);

        System.out.println("Sleeping main thread for 5 seconds");
        Thread.sleep(5000);

        // Add 21 nodes (one more than maxSize)
        Runnable s2 = () -> {
            for (int i = 0; i <= 20; i++) {
                priorityQueue.add("node1", 9);
            }
        };

        pool.submit(s2);

        System.out.println("Sleeping main thread for 5 seconds (add more than maxSize then remove)");
        Thread.sleep(5000);

        // Remove 20 nodes
        Runnable s3 = () -> {
            for (int i = 0; i < 20; i++) {
                System.out.println(priorityQueue.getFirst() + " " + i);
            }

        };

        pool.submit(s3);

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