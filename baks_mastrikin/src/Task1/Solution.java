package Task1;

import java.util.Scanner;

public class Solution {
    public static void main(String[] args) {
        Scanner console = new Scanner(System.in);
        int firstGroupTasks = console.nextInt();
        int secondGroupTasks = console.nextInt();
        int tasksCount = console.nextInt();

        // Every student (or except one) solved all tasks
        int minStudentNumberSecondGroup = secondGroupTasks / tasksCount;

        // Every student solved exactly 1 task
        int maxStudentNumberFirstGroup = firstGroupTasks;
        if (secondGroupTasks % tasksCount > 0) {
            minStudentNumberSecondGroup++;
        }
        if (minStudentNumberSecondGroup < maxStudentNumberFirstGroup) {
            System.out.println("Yes");
        } else {
            System.out.println("No");
        }
    }
}
