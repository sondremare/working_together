package overskaug.util;

import overskaug.tree.Task;

import java.util.ArrayList;
import java.util.Arrays;

public class TaskUtils {

    public static ArrayList<String> convertExpressionToList(String expression) {
        return new ArrayList<String>(Arrays.asList(expression.split(" ")));
    }

    public static Task parse(String task) {
        String[] taskVariables = task.split(" ");
        Task leftChild = new Task(taskVariables[1]);
        Task rightChild = new Task(taskVariables[2]);
        return new Task(taskVariables[0], leftChild, rightChild);
    }

    public static String stringify(Task task) {
        return task.getValue() + " " + task.getLeftChild().getValue() + " " +task.getRightChild().getValue();
    }

    public static String getType(Task task) {
        if (task.getValue().equals("+")) {
            return "AdditionSolver";
        } else if (task.getValue().equals("-")) {
            return "SubtractionSolver";
        } else if (task.getValue().equals("*")) {
            return "MultiplicationSolver";
        } else if (task.getValue().equals("/")) {
            return "DivisionSolver";
        }
        return null;
    }

    /* This method traverses the tree structure and returns all task currently ready for solving */
    public static ArrayList<Task> findSolvableTask(Task root) {
        ArrayList<Task> solvableTasks = new ArrayList<Task>();
        if (!Task.isNumeric(root.getValue())) {
            traverse(root, solvableTasks);
        }
        return solvableTasks;

    }

    public static Task traverse(Task root, ArrayList<Task> tasks) {
        if (root.getLeftChild() != null && root.getRightChild() != null) {
            Task left = traverse(root.getLeftChild(), tasks);
            Task right = traverse(root.getRightChild(), tasks);
            if (!Task.isNumeric(root.getValue()) && Task.isNumeric(left.getValue()) && Task.isNumeric(right.getValue())) {
                tasks.add(root);
            }
        }
        return root;
    }
}
