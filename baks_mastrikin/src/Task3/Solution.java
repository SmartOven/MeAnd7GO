package Task3;

import java.util.Scanner;

public class Solution {
    public static void main(String[] args) {
        Scanner console = new Scanner(System.in);
        String N = console.nextLine();
        System.out.println(countZerosAfterCutting(N));
    }
    public static int countZerosAfterCutting(String s) {
        char[] chars = s.toCharArray();
        int i = chars.length - 1;
        while (i >= 0 && chars[i] == '0') {
            i--;
        }
        int counter = 0;
        for (; i >= 0; i--) {
            if (chars[i] == '0') {
                counter++;
            }
        }
        return counter;
    }
}
