using System;
using System.Diagnostics;

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
            Console.WriteLine(stopwatch.ElapsedMilliseconds);
        }
        else if (testcode == "MEMORY_ALLOCATION_DYNAMIC")
        {
            stopwatch.Start();
            DynamicAllocation(X);
            stopwatch.Stop();
            Console.WriteLine(stopwatch.ElapsedMilliseconds);
        }
        else if (testcode == "MEMORY_ACCESS_STATIC")
        {
            stopwatch.Start();
            StaticMemoryAccess(X);
            stopwatch.Stop();
            Console.WriteLine(stopwatch.ElapsedMilliseconds);
        }
        else if (testcode == "MEMORY_ACCESS_DYNAMIC")
        {
            stopwatch.Start();
            DynamicMemoryAccess(X);
            stopwatch.Stop();
            Console.WriteLine(stopwatch.ElapsedMilliseconds);
        }
    }
}
