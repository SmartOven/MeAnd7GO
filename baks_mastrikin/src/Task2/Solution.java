package Task2;

import java.util.Scanner;

public class Solution {
    public static void main(String[] args) {
        Scanner console = new Scanner(System.in);
        String op1 = console.nextLine();
        String op2 = console.nextLine();
        String op3 = console.nextLine();
        printVariations(new char[]{op1.charAt(0), op2.charAt(0), op3.charAt(0)});
    }

    private static void printVariations(char[] operators) {
        String[] combinations = {
                "abc", "acb", "bac", "bca", "cab", "cba"
        };
        for (String combination: combinations) {
            int a = combination.indexOf('a');
            int b = combination.indexOf('b');
            int c = combination.indexOf('c');
            if (combinationIsValid(a, b, c, operators)) {
                System.out.println(combination);
            }
        }
    }
    private static boolean combinationIsValid(int a, int b, int c, char[] operators) {
        int[][] pairs = {
                {a, b}, {a, c}, {b, c}
        };
        for (int i = 0; i < operators.length; i++) {
            char op = operators[i];
            int left = pairs[i][0];
            int right = pairs[i][1];
            if (op == '<' && !(left < right)) {
                return false;
            }
            if (op == '>' && !(left > right)) {
                return false;
            }
        }
        return true;
    }
}
