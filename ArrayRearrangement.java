package przyklady03;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ThreadLocalRandom;

public class ArrayRearrangement {

    private static final int MAX_DELAY_MS = 4000;
    private static final int THREADS_COUNT = 10;
    private static final int[] data = new int[THREADS_COUNT];

    private static int counter = 0;
    private static final String[] messages = {
        "set their cell to their id",
        "read the value from the other end of the array and reset it to -1",
        "set their cell to the previously read value"
    };

    private static final CyclicBarrier barrier = new CyclicBarrier(THREADS_COUNT,
        ArrayRearrangement::printData);

    // This method is always run by a single thread,
    // so it's safe to use non-atomic operations here
    private static void printData() {
        System.out.print("Now everyone has ");
        System.out.println(messages[counter]);

        System.out.println("The data is now:");
        for (int x : data) {
            System.out.print(" " + x);
        }
        System.out.println();

        counter++;
    }

    private static void sleep() throws InterruptedException {
        Thread.sleep(ThreadLocalRandom.current().nextInt(MAX_DELAY_MS));
        System.out.println(Thread.currentThread().getName() + " has finished");
    }

    public static void main(final String[] args) {
        Thread[] threads = new Thread[THREADS_COUNT];

        for (int i = 0; i < THREADS_COUNT; ++i) {
            threads[i] = new Thread(new Helper(i), "Helper" + i);
        }

        for (int i = 0; i < THREADS_COUNT; ++i) {
            threads[i].start();
        }

        try {
            for (int i = 0; i < THREADS_COUNT; ++i) {
                threads[i].join();
            }
        } catch (InterruptedException e) {
            System.err.println("Main interrupted");
            Thread.currentThread().interrupt();
        }
    }

    private static class Helper implements Runnable {

        private final int id;

        public Helper(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                sleep();
                data[id] = id;
                barrier.await();

                sleep();
                int value = data[THREADS_COUNT - 1 - id];
                data[THREADS_COUNT - 1 - id] = -1;
                barrier.await();

                sleep();
                data[id] = value;
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                Thread t = Thread.currentThread();
                t.interrupt();
                System.err.println(t.getName() + " interrupted");
            }
        }
    }
}
