#include <iostream>
#include <string>
#include <chrono>
#include <thread>
#include <vector>
#include <atomic>
#include <functional>
#include <windows.h>
#include <process.h>


#ifdef _WIN32
void pinThreadToCore(int coreId) {
    DWORD_PTR mask = 1ULL << coreId; // Core affinity mask
    if (SetThreadAffinityMask(GetCurrentThread(), mask) == 0) {
        std::cerr << "Failed to set thread affinity. Error: " << GetLastError() << std::endl;
    }
}
#elif __linux__
void pinThreadToCore(int coreId) {
    cpu_set_t cpuset;
    CPU_ZERO(&cpuset);
    CPU_SET(coreId, &cpuset);
    int rc = pthread_setaffinity_np(pthread_self(), sizeof(cpu_set_t), &cpuset);
    if (rc != 0) {
        std::cerr << "Failed to set thread affinity. Error: " << rc << std::endl;
    }
}
#endif

void threadTask(std::atomic<bool>& signal, int numThreads) {
    // Initially pin to core 0
    pinThreadToCore(0);

    // Wait for the signal to start
    while (!signal.load(std::memory_order_acquire))
        ;

    // Simulate migration to core 1 after work
    pinThreadToCore(1);
}

void measureThreadMigrationTime(int numThreads) {
    using clock = std::chrono::high_resolution_clock;

    std::atomic<bool> signal(false); // Signal to start threads
    std::vector<std::thread> threads;

    auto start = clock::now();

    // Create and launch threads
    for (int i = 0; i < numThreads; ++i) {
        threads.emplace_back(threadTask, std::ref(signal), numThreads);
    }

    signal.store(true, std::memory_order_release);

    // Wait for all threads to complete
    for (auto& t : threads) {
        t.join();
    }

    auto end = clock::now();
    auto duration = std::chrono::duration_cast<std::chrono::microseconds>(end - start);
    std::cout<<duration.count();
}








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
     std::atomic<int> turn(0);
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
    else if(testcode == "THREAD_MIGRATION"){
        measureThreadMigrationTime(X);
    }
    return 0;
}
