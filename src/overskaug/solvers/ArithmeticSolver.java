package overskaug.solvers;

import overskaug.tree.TreeNode;

public interface ArithmeticSolver {

    public double solve(TreeNode task) throws UnsupportedArithmeticOperation;

}
