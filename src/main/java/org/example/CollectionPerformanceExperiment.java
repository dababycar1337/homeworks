package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public final class CollectionPerformanceExperiment {
    private static final int N = 10_000;
    private static final int WARMUPS = 2;
    private static final int RUNS = 5;
    private static long sink;

    private CollectionPerformanceExperiment() {}

    public static void main(String[] args) {
        System.out.printf("%-28s %8s %15s %15s%n", "Operation", "N", "ArrayList (ms)", "LinkedList (ms)");
        System.out.println("-".repeat(72));

        report("add to end", CollectionPerformanceExperiment::addToEnd);
        report("add to front", CollectionPerformanceExperiment::addToFront);
        report("insert in middle", CollectionPerformanceExperiment::insertMiddle);
        report("indexed iteration", CollectionPerformanceExperiment::indexedIteration);
        report("remove from front", CollectionPerformanceExperiment::removeFront);
        report("remove from end", CollectionPerformanceExperiment::removeEnd);

        System.out.println("\nВыводы см. в README.md");
    }

    private interface Scenario { long run(List<Integer> list); }

    private static void report(String label, Scenario scenario) {
        long arrayMedian = measure(scenario, new ArrayList<>());
        long linkedMedian = measure(scenario, new LinkedList<>());
        System.out.printf("%-28s %8d %15.3f %15.3f%n", label, N, arrayMedian / 1_000_000.0, linkedMedian / 1_000_000.0);
    }

    private static long measure(Scenario scenario, List<Integer> prototype) {
        for (int i = 0; i < WARMUPS; i++) {
            sink += scenario.run(newList(prototype));
        }
        long[] times = new long[RUNS];
        for (int i = 0; i < RUNS; i++) {
            List<Integer> list = newList(prototype);
            long start = System.nanoTime();
            sink += scenario.run(list);
            times[i] = System.nanoTime() - start;
        }
        return median(times);
    }

    private static List<Integer> newList(List<Integer> prototype) {
        return prototype instanceof ArrayList ? new ArrayList<>() : new LinkedList<>();
    }

    private static long median(long[] values) {
        long[] copy = values.clone();
        Arrays.sort(copy);
        return copy[copy.length / 2];
    }

    private static long addToEnd(List<Integer> list) {
        for (int i = 0; i < N; i++) {
            list.add(i);
        }
        return list.size();
    }

    private static long addToFront(List<Integer> list) {
        for (int i = 0; i < N; i++) {
            list.add(0, i);
        }
        return list.size();
    }

    private static long insertMiddle(List<Integer> list) {
        for (int i = 0; i < N / 2; i++) {
            list.add(list.size() / 2, i);
        }
        return list.size();
    }

    private static long indexedIteration(List<Integer> list) {
        for (int i = 0; i < N; i++) {
            list.add(i);
        }
        long sum = 0;
        for (int i = 0; i < list.size(); i++) {
            sum += list.get(i);
        }
        return sum;
    }

    private static long removeFront(List<Integer> list) {
        for (int i = 0; i < N; i++) {
            list.add(i);
        }
        long removed = 0;
        while (!list.isEmpty()) {
            removed += list.remove(0);
        }
        return removed;
    }

    private static long removeEnd(List<Integer> list) {
        for (int i = 0; i < N; i++) {
            list.add(i);
        }
        long removed = 0;
        while (!list.isEmpty()) {
            removed += list.remove(list.size() - 1);
        }
        return removed;
    }
}