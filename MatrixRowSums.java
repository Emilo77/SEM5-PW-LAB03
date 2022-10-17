package przyklady03;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.function.IntBinaryOperator;

public class MatrixRowSums {

    private static final int ROWS = 10;
    private static final int COLUMNS = 100;

    private static int rowIndex = 0;

    static int []rowSums = new int[ROWS];

    static int currentRowSum = 0;

    private static final Semaphore mutex = new Semaphore(1);

    private static final CyclicBarrier barrier = new CyclicBarrier(COLUMNS,
            MatrixRowSums::calculate_row);

    private static void calculate_row() {
        rowSums[rowIndex] = currentRowSum;
        currentRowSum = 0;
        rowIndex++;
    }

    private static class Helper implements Runnable {
        int columnIndex;
        private final IntBinaryOperator definition;

        Helper(int columnNumber, IntBinaryOperator definition) {
            this.columnIndex = columnNumber;
            this.definition = definition;
        }
        @Override
        public void run() {
            try {
                for(int i = 0; i < ROWS; i++) {
                    mutex.acquire();
                    try {
                        currentRowSum += definition.applyAsInt(rowIndex, columnIndex);
                    } finally {
                        mutex.release();
                        barrier.await();
                    }
                }
            } catch (InterruptedException | BrokenBarrierException e) {
                System.err.println("Thread interrupted");
                Thread.currentThread().interrupt();
            }

        }
    }

    private static class Matrix {

        private final int rows;
        private final int columns;
        private final IntBinaryOperator definition;

        public Matrix(int rows, int columns, IntBinaryOperator definition) {
            this.rows = rows;
            this.columns = columns;
            this.definition = definition;
        }

        public int[] rowSums() {
            int[] rowSums = new int[rows];
            for (int row = 0; row < rows; ++row) {
                int sum = 0;
                for (int column = 0; column < columns; ++column) {
                    sum += definition.applyAsInt(row, column);
                }
                rowSums[row] = sum;
            }
            return rowSums;
        }

        public int[] rowSumsConcurrent() {
            Thread[] threads = new Thread[COLUMNS];
            for (int i = 0; i < COLUMNS; ++i) {
                threads[i] = new Thread(new Helper(i, definition));
            }

            for (int i = 0; i < COLUMNS; ++i) {
                threads[i].start();
            }

            try {
                for (int i = 0; i < COLUMNS; ++i) {
                    threads[i].join();
                }
            } catch (InterruptedException e) {
                System.err.println("Main interrupted");
                Thread.currentThread().interrupt();
            }

            rowIndex = 0;
            return rowSums;
        }
    }

    public static void test(int n, boolean print) {
        System.out.println("Starting random test with " + n + " attempts...");
        Random random = new Random();

        for (int i = 0; i < n; i++) {
            int randomModulo = random.nextInt(1,100);
            Matrix matrix = new Matrix(ROWS, COLUMNS, (row, column) -> {
                int a = 2 * column + 1;
                return (row + 1) * (a % randomModulo - 2) * a;
            });
            int[] rowSums = matrix.rowSums();
            int[] rowSumsConcurrent = matrix.rowSumsConcurrent();

            if (print) {
                for (int k = 0; k < rowSums.length; k++) {
                    System.out.println(k + " -> Seq: " + rowSums[k] + " Con: " + rowSumsConcurrent[k]);
                }
            }

            for(int j = 0; j < ROWS; j++) {
                if (rowSumsConcurrent[j] != rowSums[j]) {
                    System.out.println("Błąd! " + i);
                    return;
                }
            }
        }
        System.out.println("All tests passed!");
    }

    public static void main(String[] args) {

        Matrix matrix = new Matrix(ROWS, COLUMNS, (row, column) -> {
            int a = 2 * column + 1;
            return (row + 1) * (a % 4 - 2) * a;
        });
        int[] rowSums = matrix.rowSums();
        int[] rowSumsConcurrent = matrix.rowSumsConcurrent();

        for (int i = 0; i < rowSums.length; i++) {
            System.out.println(i + " -> " + rowSums[i]);
        }

        for (int i = 0; i < rowSumsConcurrent.length; i++) {
            System.out.println(i + " -> " + rowSumsConcurrent[i]);
        }

        test(100, false); //change second parameter to true to see results
    }

}
