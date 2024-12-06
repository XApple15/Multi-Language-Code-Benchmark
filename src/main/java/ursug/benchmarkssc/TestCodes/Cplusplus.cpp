#include <iostream>
#include <string>
#include <chrono>
#include <thread>
#include <vector>
#include <atomic>
#include <functional>

void staticAllocation(int X) {
    int arr[X];
    for (int i = 0; i < X; ++i) {
        arr[i] = i * 2;
    }
}

void dynamicAllocation(int X) {
    int* arr = new (std::nothrow) int[X];
    for (int i = 0; i < X; ++i) {
        arr[i] = i * 3;
    }
    delete[] arr;
}

void staticMemoryAccess(int X) {
    int arr[X];
    for (int i = 0; i < X; ++i) {
        arr[i] = i * 2;
    }
    for (int i = 0; i < X; ++i) {
        volatile int temp = arr[i];
    }
}

void dynamicMemoryAccess(int X) {
    int* arr = new (std::nothrow) int[X];
    for (int i = 0; i < X; ++i) {
        arr[i] = i * 3;
    }
    for (int i = 0; i < X; ++i) {
        volatile int temp = arr[i];
    }
    delete[] arr;
}

void threadFunction(int threadId) {
    // Simulating some work
   // std::this_thread::sleep_for(std::chrono::milliseconds(10));  // simulate work for 10ms
}

void measureThreadCreation(int X) {
    using std::chrono::high_resolution_clock;
    auto tStart = high_resolution_clock::now();
    std::vector<std::thread> threads;

    for (int i = 0; i < X; ++i) {
        threads.push_back(std::thread(threadFunction, i));
    }
    for (auto& t : threads) {
        t.join();
    }

    auto tFinish = high_resolution_clock::now();
    auto duration = std::chrono::duration_cast<std::chrono::microseconds>(tFinish - tStart);
    std::cout << duration.count() ;
}

void measureContextSwitchTime(int iterations) {
     std::atomic<int> turn(0); // Used to signal which thread should run
        std::atomic<int> counter(0);

        auto threadFunction = [&turn, &counter, iterations](int threadId) {
            for (int i = 0; i < iterations; ++i) {
                while (turn.load(std::memory_order_acquire) != threadId);
                counter.fetch_add(1, std::memory_order_relaxed);
                turn.store(1 - threadId, std::memory_order_release);
            }
        };

        std::thread t1(threadFunction, 0);
        std::thread t2(threadFunction, 1);

        auto start = std::chrono::high_resolution_clock::now();

        turn.store(0, std::memory_order_release);

        t1.join();
        t2.join();


        auto end = std::chrono::high_resolution_clock::now();
        auto duration = std::chrono::duration_cast<std::chrono::microseconds>(end - start);
        double totalTime = duration.count();
        std::cout << totalTime;
}

int main(int argc, char* argv[]) {
    using std::chrono::high_resolution_clock;
    auto tStart = high_resolution_clock::now();
    auto tFinish = high_resolution_clock::now();
    auto duration = std::chrono::duration_cast<std::chrono::microseconds>(tFinish - tStart);

    if (argc < 3) {
        return 1;
    }
    int X = std::stoi(argv[1]);
    std::string testcode = argv[2];

    if (testcode == "MEMORY_ALLOCATION_STATIC") {
        tStart = high_resolution_clock::now();
        staticAllocation(X);
        tFinish = high_resolution_clock::now();
        duration = std::chrono::duration_cast<std::chrono::microseconds>(tFinish - tStart);
        std::cout << duration.count();
    } else if (testcode == "MEMORY_ALLOCATION_DYNAMIC") {
        tStart = high_resolution_clock::now();
        dynamicAllocation(X);
        tFinish = high_resolution_clock::now();
        duration = std::chrono::duration_cast<std::chrono::microseconds>(tFinish - tStart);
        std::cout << duration.count() ;
    }else if (testcode == "MEMORY_ACCESS_STATIC") {
        tStart = high_resolution_clock::now();
        staticMemoryAccess(X);
        tFinish = high_resolution_clock::now();
        duration = std::chrono::duration_cast<std::chrono::microseconds>(tFinish - tStart);
        std::cout << duration.count() ;
    }
    else if (testcode == "MEMORY_ACCESS_DYNAMIC") {
        tStart = high_resolution_clock::now();
        dynamicMemoryAccess(X);
        tFinish = high_resolution_clock::now();
        duration = std::chrono::duration_cast<std::chrono::microseconds>(tFinish - tStart);
        std::cout << duration.count() ;
    }
    else if (testcode == "THREAD_CREATION") {
        measureThreadCreation(X);
    }
    else if(testcode == "THREAD_CONTEXT_SWITCH") {
        measureContextSwitchTime(X);
    }
    return 0;
}
