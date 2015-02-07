package overskaug;

import overskaug.tree.TreeNode;

import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        String task = "+ * 5 2 - 7 2";
        ArrayList<String> list = new ArrayList<String>(Arrays.asList(task.split(" ")));
        TreeNode root = TreeNode.parsePrefix(list);
    }
}
