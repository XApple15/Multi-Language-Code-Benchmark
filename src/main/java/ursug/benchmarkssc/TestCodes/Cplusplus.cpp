#include <iostream>
#include <string>
#include <chrono>


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
    return 0;
}
