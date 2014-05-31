package com.denison;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author Yuri Denison
 * @since 19.05.14
 */

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class PowerBenchmark {
    private static final int SMALL = (int) Math.pow(10, 4);
    private static final int MEDIUM = (int) Math.pow(10, 8);

    private static final int SIZE = 50;
    private static int[] smallValues = new int[SIZE];
    private static int[] mediumValues = new int[SIZE];
    static {
        final Random random = new Random();
        for (int i = 0; i < SIZE; i++) {
            smallValues[i] = Math.abs(random.nextInt(SMALL)) + 1;
            mediumValues[i] = Math.abs(random.nextInt(MEDIUM)) + SMALL;
        }
    }

    private static int slow(int x) {
        int res = 1;
        while (res < x) {
            res <<= 1;
        }
        return res;
    }

    private static int fast(int x) {
        x--;
        x |= x >> 1;
        x |= x >> 2;
        x |= x >> 4;
        x |= x >> 8;
        x |= x >> 16;
        x++;
        return x;
    }

    @GenerateMicroBenchmark
    @OperationsPerInvocation(50)
    public int smallPowerFast() {
        int a = 1;
        for (int value : smallValues) {
            a += fast(value);
        }
        return a;
    }

    @GenerateMicroBenchmark
    @OperationsPerInvocation(50)
    public int smallPowerSlow() {
        int a = 1;
        for (int value : smallValues) {
            a += slow(value);
        }
        return a;
    }

    @GenerateMicroBenchmark
    @OperationsPerInvocation(15)
    public int mediumPowerFast() {
        int a = 1;
        for (int value : mediumValues) {
            a += fast(value);
        }
        return a;
    }

    @GenerateMicroBenchmark
    @OperationsPerInvocation(15)
    public int mediumPowerSlow() {
        int a = 1;
        for (int value : mediumValues) {
            a += slow(value);
        }
        return a;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + PowerBenchmark.class.getSimpleName() + ".*")
                .warmupIterations(2)
                .measurementIterations(5)
                .forks(1)
                .build();

        new Runner(opt).run();
    }

}