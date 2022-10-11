package przyklady03;

import java.util.concurrent.CountDownLatch;

public class CountDown {

    private static final int ITERATIONS = 100000;
    private static final int ITERATIONS_BEFORE_WAKEUP = 50000;

    private static volatile int counter = 0;
    private static final CountDownLatch latch = new CountDownLatch(ITERATIONS_BEFORE_WAKEUP);

    public static void main(String[] args) {
        Thread main = Thread.currentThread();

        Thread waker = new Thread(() -> {
            for (int i = 0; i < ITERATIONS; ++i) {
                if (Thread.interrupted()) {
                    Thread.currentThread().interrupt();
                    System.err.println("Waker interrupted");
                    main.interrupt();
                    break;
                }
                counter = i + 1;
                latch.countDown();
            }
        });

        waker.start();

        try {
            // Main threads sleeps until waker finishes ITERATIONS_BEFORE_WAKEUP iterations
            latch.await();
            System.out.println(counter); // >= ITERATIONS_BEFORE_WAKEUP
            waker.join();
            System.out.println(counter); // == ITERATIONS
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Main interrupted");
        }
    }

}
