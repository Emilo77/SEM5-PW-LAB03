package przyklady03;

import java.util.concurrent.Semaphore;

public class TwoCounterIncreasers {

    private static final int ITERATIONS = 10000000;

    private static int counter = 0;

    private static final Semaphore mutex = new Semaphore(1);

    private static class Increaser implements Runnable {

        private static void checkInterrupt() throws InterruptedException {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
        }

        private static void localSection() throws InterruptedException {
            checkInterrupt();
        }

        private static void criticalSection() throws InterruptedException {
            checkInterrupt();
            ++counter;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < ITERATIONS; ++i) {
                    localSection();
                    mutex.acquire();
                    try {
                        criticalSection();
                        // We don't catch the InterruptedException here,
                        // so it can be handled in the outer try block.
                        // This finally block is run before the outer
                        // catch block.
                    } finally {
                        mutex.release();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Increaser interrupted");
            }
        }

    }

    public static void main(String[] args) {
        Thread first = new Thread(new Increaser());
        Thread second = new Thread(new Increaser());
        first.start();
        second.start();
        try {
            first.join();
            second.join();
            System.out.println(counter);
            assert counter == 2 * ITERATIONS;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Main interrupted");
        }
    }

}
