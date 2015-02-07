package overskaug;

import overskaug.tree.Task;

import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        //String task = "+ * 5 2 - 7 2";
        String task = "+ * 5 2 - 7 + 10 3";
        Task root = Task.parsePrefix(convertExpressionToList(task));
        ArrayList<Task> stuff = findSolvableTasks(root);
        System.out.println("TOE");
    }

    public static ArrayList<String> convertExpressionToList(String expression) {
        return new ArrayList<String>(Arrays.asList(expression.split(" ")));
    }

    /* This method traverses the tree structure and returns all task currently ready for solving */
    public static ArrayList<Task> findSolvableTasks(Task root) {
        ArrayList<Task> solvableTasks = new ArrayList<Task>();
        traverse(root, solvableTasks);
        return solvableTasks;

    }

    public static Task traverse(Task root, ArrayList<Task> tasks) {
        if (root.getLeftChild() != null && root.getRightChild() != null) {
            Task left = traverse(root.getLeftChild(), tasks);
            Task right = traverse(root.getRightChild(), tasks);
            if (Task.isNumeric(left.getValue())&& Task.isNumeric(right.getValue())) {
                tasks.add(root);
            }
        }
        return root;
    }
}
