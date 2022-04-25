package Task4;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Scanner;

public class Solution {
    public static void main(String[] args) {
        Scanner console = new Scanner(System.in);
        int n = console.nextInt();
        MatrixFiller matrixFiller = new MatrixFiller(n);
        matrixFiller.fill();
        System.out.println(matrixFiller);
    }

    private static class MatrixFiller {
        private final int n;
        private final char[][] matrix;

        public MatrixFiller(int n) {
            this.n = n;
            matrix = new char[n][n];
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < n - 1; i++) {
                for (int j = 0; j < n; j++) {
                    stringBuilder.append(matrix[i][j]);
                }
                stringBuilder.append("\n");
            }
            for (int j = 0; j < n; j++) {
                stringBuilder.append(matrix[n - 1][j]);
            }
            return stringBuilder.toString();
        }

        public void fill() {
            fillDiagonals();
            fillTheRest();
        }

        private void fillDiagonals() {
            for (int i = 0; i < n; i++) {
                matrix[i][i] = 'a';
                matrix[i][n - 1 - i] = 'a';
            }
        }

        private void fillTheRest() {
            ArrayDeque<TableCell> queue = new ArrayDeque<>();
            for (int i = 0; i < n; i++) {
                queue.addLast(new TableCell(i, i, 'a'));
                queue.addLast(new TableCell(i, n - 1 - i, 'a'));
            }
            while (!queue.isEmpty()) {
                // Getting table cell from the Queue
                TableCell currentTableCell = queue.pollFirst();
                int x1 = currentTableCell.x;
                int y1 = currentTableCell.y;

                // Getting its unmarked neighbours
                ArrayList<TableCell> currentUnmarkedNeighbours = currentTableCell.getUnmarkedNeighbours(matrix);

                // For each of them - mark them and add to the queue
                for (TableCell neighbour: currentUnmarkedNeighbours) {
                    int x2 = neighbour.x;
                    int y2 = neighbour.y;
                    char nextChar;
                    if (matrix[x1][y1] == 'z') {
                        nextChar = 'a';
                    } else {
                        nextChar = (char) (matrix[x1][y1] + 1);
                    }
                    matrix[x2][y2] = nextChar;
                    queue.addLast(neighbour);
                }
            }
        }

        private static class TableCell {
            public int x;
            public int y;
            public char value;

            public TableCell(int x, int y, char value) {
                this.x = x;
                this.y = y;
                this.value = value;
            }
            
            public ArrayList<TableCell> getUnmarkedNeighbours(char[][] matrix) {
                char unmarkedChar = (char)(0);
                int n = matrix.length;
                int m = matrix[0].length;
                ArrayList<TableCell> unmarkedNeighbours = new ArrayList<>(4);
                if (x - 1 >= 0 && matrix[x - 1][y] == unmarkedChar) {
                    // [x-1][y]
                    unmarkedNeighbours.add(new TableCell(x - 1, y, matrix[x - 1][y]));
                }
                if (x + 1 < n && matrix[x + 1][y] == unmarkedChar) {
                    // [x+1][y]
                    unmarkedNeighbours.add(new TableCell(x + 1, y, matrix[x + 1][y]));
                }
                if (y - 1 >= 0 && matrix[x][y - 1] == unmarkedChar) {
                    // [x][y-1]
                    unmarkedNeighbours.add(new TableCell(x, y - 1, matrix[x][y - 1]));
                }
                if (y + 1 < m && matrix[x][y + 1] == unmarkedChar) {
                    // [x][y+1]
                    unmarkedNeighbours.add(new TableCell(x, y + 1, matrix[x][y + 1]));
                }
                return unmarkedNeighbours;
            }
        }
    }
}
