package overskaug.tree;

import java.util.List;

public class TreeNode {
    private String value;
    private TreeNode leftChild = null;
    private TreeNode rightChild = null;

    public TreeNode(String value, TreeNode left, TreeNode right) {
        this.value = value;
        this.leftChild = left;
        this.rightChild = right;
    }

    public TreeNode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public TreeNode getLeftChild() {
        return leftChild;
    }

    public void setLeftChild(TreeNode leftChild) {
        this.leftChild = leftChild;
    }

    public TreeNode getRightChild() {
        return rightChild;
    }

    public void setRightChild(TreeNode rightChild) {
        this.rightChild = rightChild;
    }

    public static TreeNode parsePrefix(List<String> expression) {
        String element = null;
        try {
            element = expression.get(0);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("The prefix notation is incorrect");
        }
        expression.remove(0);
        if (isNumeric(element)) {
            return new TreeNode(element);
        }
        TreeNode left = parsePrefix(expression);
        TreeNode right = parsePrefix(expression);
        return new TreeNode(element, left, right);
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
