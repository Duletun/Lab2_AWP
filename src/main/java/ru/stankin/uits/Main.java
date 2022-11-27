package ru.stankin.uits;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class Main {

    private static AtomicInteger[][] a_matrixA;
    private static AtomicInteger[][] a_matrixB;
    private static AtomicInteger[][] a_result;

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        int[][] matrixA = Matrix.generate(4, 5);
        int[][] matrixB = Matrix.generate(5, 4);

        /////////////////////
        // 1 Часть задания //
        /////////////////////

        System.out.print("\n[Результат выполнения задания 1: С использованием Fork-Join Pool]");

        System.out.print("\nМатрица А:");
        Matrix.print(matrixA);
        System.out.print("\nМатрица B:");
        Matrix.print(matrixB);

        if(matrixA.length == matrixB[0].length) {
            RecursiveTaskExample recursiveTaskExample = new RecursiveTaskExample(matrixA, matrixB);
            ForkJoinPool forkJoinPool = new ForkJoinPool();
            ForkJoinTask<int[][]> submit = forkJoinPool.submit(recursiveTaskExample);
            System.out.print("\nРезультат:");
            Matrix.print(submit.get());
        }else{
            System.out.print("Невозможно умножить матрицы");
            System.exit(0);
        }

        /////////////////////
        // 2 Часть задания //
        /////////////////////

        System.out.print("\n\n[Результат выполнения задания 2: C использованием произвольного пула потоков]\n");
        System.out.print("Вывод потоков:\n");

        a_matrixA = Matrix.makeAtomic(matrixA);
        a_matrixB = Matrix.makeAtomic(matrixB);
        a_result = Matrix.fillAtomic(a_matrixA.length,a_matrixA[0].length);

        ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (int i = 0; i < matrixA.length; i++) {
            for (int j = 0; j < matrixB[i].length; j++) {
                MatrixMulti multiply = new MatrixMulti(i, j, a_matrixA[i], a_matrixB, a_result);
                service.execute(multiply);
            }
        }
        service.shutdown();


        try {
            service.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            System.out.println("Ошибка в потоках");
        }finally{
            System.out.print("\nРезультат:\n");
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    System.out.print(a_result[i][j] + "  ");
                }
                System.out.println();
            }
        }

    }
}