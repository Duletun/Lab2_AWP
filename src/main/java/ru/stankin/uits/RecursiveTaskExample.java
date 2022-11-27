package ru.stankin.uits;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class RecursiveTaskExample extends RecursiveTask<int[][]> {

    private int[][] matrixA;
    private int[][] matrixB;
    private List<Integer> list;

    public RecursiveTaskExample(int[][] a, int[][] b) {
        this.matrixA = a;
        this.matrixB = b;
    }

    @Override
    protected int[][] compute() {
        // Условие 1
        if (matrixA.length != 1) {
            int[][] a1 = Matrix.getRows(matrixA, 0, matrixA.length / 2);
            int[][] b1 = Matrix.getRows(matrixA, matrixA.length / 2, matrixA.length);
            RecursiveTaskExample first = new RecursiveTaskExample(a1, matrixB);
            RecursiveTaskExample second = new RecursiveTaskExample(b1, matrixB);
            first.fork();
            second.fork();

            int[][] a = new int[matrixA.length][matrixB[0].length];

            int i;



            for (i = 0; i < first.join().length; i++) {
                a[i] = first.join()[i];
            }
            for (int j = i; j < i + second.join().length; j++) {
                a[j] = second.join()[j - i];
            }

            return a;
        } else if (matrixB[0].length != 1) { //Условие 2
            RecursiveTaskExample first = new RecursiveTaskExample(matrixA, Matrix.getCol(matrixB, 0, matrixB[0].length / 2));
            RecursiveTaskExample second = new RecursiveTaskExample(matrixA, Matrix.getCol(matrixB, matrixB[0].length / 2, matrixB[0].length));
            first.fork();
            second.fork();

            int[][] a = new int[matrixA.length][matrixB[0].length];

            int i;

            int[][] b = first.join();

            int[][] c = second.join();


            for (i = 0; i < first.join()[0].length; i++) {
                a[0][i] = first.join()[0][i];
            }
            for (int j = i; j < i + second.join()[0].length; j++) {
                a[0][j] = second.join()[0][j - i];
            }

            return a;

        } else {
            int[][] a = new int[1][1];
            int result = 0;

            for (int j = 0; j < matrixA[0].length; j++) {
                    result += matrixA[0][j] * matrixB[j][0];
            }
            a[0][0] = result;
            return a;
        }
    }
}
