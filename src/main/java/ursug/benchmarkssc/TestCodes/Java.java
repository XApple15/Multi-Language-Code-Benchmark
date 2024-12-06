package ursug.benchmarkssc.TestCodes;


import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Java {

    // Static memory allocation
    static void staticAllocation(int X) {
        int[] arr = new int[X];
        for (int i = 0; i < X; ++i) {
            arr[i] = i * 2;
        }
    }

    // Dynamic memory allocation
    static void dynamicAllocation(int X) {
        List<Integer> dynamicList = new ArrayList<>();
        for (int i = 0; i < X; i++) {
            dynamicList.add(i * 2);
        }
    }

    // Static memory access
    static void staticMemoryAccess(int X) {
        int[] arr = new int[X];
        for (int i = 0; i < X; ++i) {
            arr[i] = i * 2;
        }
        for (int i = 0; i < X; ++i) {
            int temp = arr[i];
        }
    }

    // Dynamic memory access
    static void dynamicMemoryAccess(int X) {
        List<Integer> dynamicList = new ArrayList<>();
        for (int i = 0; i < X; i++) {
            dynamicList.add(i * 2);
        }

        for (int i = 0; i < X; ++i) {
            int temp = dynamicList.get(i);
        }
    }

    static void measureThreadCreation(int X) {
        long startTime = System.nanoTime();
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < X; i++) {
            Thread t = new Thread(() -> {
            });
            threads.add(t);
            t.start();
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.nanoTime();
        System.out.println((endTime - startTime) / 1000);
    }

    public static void measureContextSwitchTime(int iterations) {
        AtomicInteger turn = new AtomicInteger(0); // Used to signal which thread should run
        AtomicInteger counter = new AtomicInteger(0);

        // Create two threads, each with its own threadId
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < iterations; i++) {
                while (turn.get() != 0) {
                    // Busy-wait
                }
                counter.incrementAndGet();
                turn.set(1);
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < iterations; i++) {
                while (turn.get() != 1) {
                }
                counter.incrementAndGet();
                turn.set(0);
            }
        });

        long start = System.nanoTime();

        turn.set(0);
        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        long end = System.nanoTime();
        long totalTime = (end - start) / 1000;
        System.out.println(totalTime);
    }


    public interface Kernel32 extends Library {
        Kernel32 INSTANCE = (Kernel32) Native.load("kernel32", Kernel32.class);

        // Get current thread handle
        Pointer GetCurrentThread();

        // Set thread affinity mask
        boolean SetThreadAffinityMask(Pointer hThread, Pointer dwThreadAffinityMask);
    }


    static void pinThreadToCore(int coreId) {
        Pointer affinityMask = new Pointer(1 << coreId);
        Kernel32.INSTANCE.SetThreadAffinityMask(Kernel32.INSTANCE.GetCurrentThread(), affinityMask);
    }

    static void threadTask(AtomicBoolean signal, AtomicBoolean done, int numThreads) {
        // Pin thread to core 0
        pinThreadToCore(0);

        // Wait for signal to start the task
        while (!signal.get()) {
        }

        int sum = 0;
        for (int i = 0; i < numThreads; ++i) {
            sum += i;
        }

        done.set(true);
    }

    static void measureThreadMigrationTime(int numThreads) {
        AtomicBoolean signal = new AtomicBoolean(false);
        AtomicBoolean done = new AtomicBoolean(false);

        long startTime = System.nanoTime();

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            futures.add(executor.submit(() -> {
                threadTask(signal, done, numThreads);
                pinThreadToCore(1);
                signal.set(true);
            }));
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        signal.set(true);

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.nanoTime();
        long elapsedTime = (endTime - startTime) / 1000000;  // Convert to milliseconds
        System.out.println(elapsedTime);
        executor.shutdown();
    }


    public static void main(String[] args) {
        if (args.length < 2) {
            return;
        }

        int X = Integer.parseInt(args[0]);
        String testCode = args[1];

        long startTime, endTime;

        switch (testCode) {
            case "MEMORY_ALLOCATION_STATIC":
                startTime = System.nanoTime();
                staticAllocation(X);
                endTime = System.nanoTime();
                System.out.println((endTime - startTime) / 1000);
                break;

            case "MEMORY_ALLOCATION_DYNAMIC":
                startTime = System.nanoTime();
                dynamicAllocation(X);
                endTime = System.nanoTime();
                System.out.println((endTime - startTime) / 1000);
                break;

            case "MEMORY_ACCESS_STATIC":
                startTime = System.nanoTime();
                staticMemoryAccess(X);
                endTime = System.nanoTime();
                System.out.println((endTime - startTime) / 1000);
                break;

            case "MEMORY_ACCESS_DYNAMIC":
                startTime = System.nanoTime();
                dynamicMemoryAccess(X);
                endTime = System.nanoTime();
                System.out.println((endTime - startTime) / 1000);
                break;

            case "THREAD_CREATION":
                measureThreadCreation(X);
                break;
            case "THREAD_CONTEXT_SWITCH":
                measureContextSwitchTime(X);
                break;
            case "THREAD_MIGRATION":
                measureThreadMigrationTime(X);
                break;
            default:
                //System.out.println("Invalid test code.");
                break;
        }
    }
}
