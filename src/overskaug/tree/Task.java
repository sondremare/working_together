package overskaug.tree;

import java.util.List;

public class Task {
    private String value;
    private Task leftChild = null;
    private Task rightChild = null;

    public Task(String value, Task left, Task right) {
        this.value = value;
        this.leftChild = left;
        this.rightChild = right;
    }

    public Task(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Task getLeftChild() {
        return leftChild;
    }

    public void setLeftChild(Task leftChild) {
        this.leftChild = leftChild;
    }

    public Task getRightChild() {
        return rightChild;
    }

    public void setRightChild(Task rightChild) {
        this.rightChild = rightChild;
    }

    public static Task parsePrefix(List<String> expression) {
        String element = null;
        try {
            element = expression.get(0);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("The prefix notation is incorrect");
        }
        expression.remove(0);
        if (isNumeric(element)) {
            return new Task(element);
        }
        Task left = parsePrefix(expression);
        Task right = parsePrefix(expression);
        return new Task(element, left, right);
    }

    public static boolean isNumeric(String element) {
        try {
            int i = Integer.parseInt(element);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
