package overskaug.converter;

import overskaug.tree.Task;

public class TaskConverter {

    public static Task parse(String task) {
        String[] taskVariables = task.split(" ");
        Task leftChild = new Task(taskVariables[1]);
        Task rightChild = new Task(taskVariables[2]);
        return new Task(taskVariables[0], leftChild, rightChild);
    }

    public static String stringify(Task task) {
        return task.getValue() + " " + task.getLeftChild().getValue() + " " +task.getRightChild().getValue();
    }
}
