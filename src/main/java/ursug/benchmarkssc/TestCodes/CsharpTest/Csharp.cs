using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using System.Runtime.InteropServices;


class Program
{

    static void StaticAllocation(int X)
    {
        int[] arr = new int[X];
        for (int i = 0; i < X; ++i)
        {
            arr[i] = i * 2;
        }
    }

    static void DynamicAllocation(int X)
    {
        List<int> dynamicList = new List<int>();
        for (int i = 0; i < X; i++)
        {
            dynamicList.Add(i * 2);
        }
    }

    static void StaticMemoryAccess(int X)
    {
        int[] arr = new int[X];
        for (int i = 0; i < X; ++i)
        {
            arr[i] = i * 2;
        }
        for (int i = 0; i < X; ++i)
        {
            int temp = arr[i];
        }
    }

    static void DynamicMemoryAccess(int X)
    {
        List<int> dynamicList = new List<int>();
        for (int i = 0; i < X; i++)
        {
            dynamicList.Add(i * 2);
        }
        for (int i = 0; i < X; ++i)
        {
            int temp = dynamicList[i];
        }
    }
// ???? prea mult timp ia sa aloce
    static void MeasureThreadCreation(int X)
        {
            Stopwatch stopwatch = new Stopwatch();
            List<Thread> threads = new List<Thread>();
            stopwatch.Start();

            for (int i = 0; i < X; i++)
            {
                Thread t = new Thread(() =>
                {});
                threads.Add(t);
                t.Start();
            }
            foreach (Thread t in threads)
            {
                t.Join();
            }
            stopwatch.Stop();
            Console.WriteLine((long)((stopwatch.ElapsedTicks * 1000000) / Stopwatch.Frequency));

        }


    static void MeasureContextSwitchTime(int iterations)
         {
             int turn = 0;
             int counter = 0;

             void ThreadFunction(int threadId)
             {
                 for (int i = 0; i < iterations; ++i)
                 {
                     while (Volatile.Read(ref turn) != threadId) ;
                     Interlocked.Increment(ref counter);
                     Volatile.Write(ref turn, 1 - threadId);
                 }
             }

             var thread0 = new Thread(() => ThreadFunction(0));
             var thread1 = new Thread(() => ThreadFunction(1));

             var stopwatch = Stopwatch.StartNew();

             turn = 0;
             thread0.Start();
             thread1.Start();

             thread0.Join();
             thread1.Join();

             stopwatch.Stop();
            Console.WriteLine((long)((stopwatch.ElapsedTicks * 1000000) / Stopwatch.Frequency));
         }

             [DllImport("kernel32.dll")]
             public static extern IntPtr GetCurrentThread();

             [DllImport("kernel32.dll")]
             public static extern bool SetThreadAffinityMask(IntPtr hThread, IntPtr dwThreadAffinityMask);

             static void PinThreadToCore(int coreId)
             {
                 IntPtr affinityMask = (IntPtr)(1 << coreId);
                 SetThreadAffinityMask(GetCurrentThread(), affinityMask);
             }

             static void ThreadTask(ManualResetEvent signal, ManualResetEvent done, int numThreads)
             {
                 PinThreadToCore(0);
                 signal.WaitOne();

                 int sum = 0;
                 for (int i = 0; i < numThreads; ++i)
                 {
                     sum += i;
                 }

                 done.Set();
             }

             static void MeasureThreadMigrationTime(int numThreads)
             {
                 var signal = new ManualResetEvent(false);
                 var done = new ManualResetEvent(false);

                 Stopwatch stopwatch =new  Stopwatch();
                    stopwatch.Start();
                 var tasks = new List<Task>();

                 // Launch numThreads threads
                 for (int i = 0; i < numThreads; ++i)
                 {
                     tasks.Add(Task.Run(() =>
                     {
                         ThreadTask(signal, done, numThreads);
                         // Simulate migration by pinning to core 1 after task completion
                         PinThreadToCore(1);
                         signal.Set();
                     }));
                 }
                 Thread.Sleep(100);
                 signal.Set();

                 Task.WhenAll(tasks).Wait();

                 stopwatch.Stop();
            Console.WriteLine((long)((stopwatch.ElapsedTicks * 1000000) / Stopwatch.Frequency));

             }


    static void Main(string[] args)
    {
        if (args.Length < 2)
        {
            return;
        }

        int X = int.Parse(args[0]);
        string testcode = args[1];

        Stopwatch stopwatch = new Stopwatch();

        if (testcode == "MEMORY_ALLOCATION_STATIC")
        {
            stopwatch.Start();
            StaticAllocation(X);
            stopwatch.Stop();
            Console.WriteLine((long)((stopwatch.ElapsedTicks * 1000000) / Stopwatch.Frequency));
        }
        else if (testcode == "MEMORY_ALLOCATION_DYNAMIC")
        {
            stopwatch.Start();
            DynamicAllocation(X);
            stopwatch.Stop();
            Console.WriteLine((long)((stopwatch.ElapsedTicks * 1000000) / Stopwatch.Frequency));
        }
        else if (testcode == "MEMORY_ACCESS_STATIC")
        {
            stopwatch.Start();
            StaticMemoryAccess(X);
            stopwatch.Stop();
            Console.WriteLine((long)((stopwatch.ElapsedTicks * 1000000) / Stopwatch.Frequency));
        }
        else if (testcode == "MEMORY_ACCESS_DYNAMIC")
        {
            stopwatch.Start();
            DynamicMemoryAccess(X);
            stopwatch.Stop();
            Console.WriteLine((long)((stopwatch.ElapsedTicks * 1000000) / Stopwatch.Frequency));
        }
         else if (testcode == "THREAD_CREATION")
        {
            MeasureThreadCreation(X);
        }
        else if (testcode == "THREAD_CONTEXT_SWITCH")
        {
            MeasureContextSwitchTime(X);
        }
        else if(testcode == "THREAD_MIGRATION"){
            MeasureThreadMigrationTime(X);
        }
    }
}
