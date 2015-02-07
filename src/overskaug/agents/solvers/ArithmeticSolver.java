package overskaug.agents.solvers;

import overskaug.tree.Task;

public interface ArithmeticSolver {

    public double solve(Task task) throws UnsupportedArithmeticOperation;

}
