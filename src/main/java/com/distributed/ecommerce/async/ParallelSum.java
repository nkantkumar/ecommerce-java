package com.distributed.ecommerce.async;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

class ParallelSum extends RecursiveTask<Long> {
    private static final int THRESHOLD = 10_000;
    private final int[] array;
    private final int start;
    private final int end;

    public ParallelSum(int[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        int length = end - start;

        if (length <= THRESHOLD) {
            // Direct computation
            long sum = 0;
            for (int i = start; i < end; i++) {
                sum += array[i];
            }
            return sum;
        }

        // Split into subtasks
        int mid = start + length / 2;
        ParallelSum leftTask = new ParallelSum(array, start, mid);
        ParallelSum rightTask = new ParallelSum(array, mid, end);

        leftTask.fork(); // Async execution
        long rightResult = rightTask.compute(); // Sync execution
        long leftResult = leftTask.join(); // Wait for async

        return leftResult + rightResult;
    }

    public static void main(String[] args) {
        int[] array = new int[100_000];
        ForkJoinPool pool = ForkJoinPool.commonPool();
        long sum = pool.invoke(new ParallelSum(array, 0, array.length));
    }
}
