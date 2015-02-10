package overskaug.agents.solvers;

import overskaug.tree.Task;

public class SubtractionSolver implements ArithmeticSolver {

    @Override
    public double solve(Task task) throws UnsupportedArithmeticOperation {
        if (task.getValue().equals("-")) {
            double operand1 = Double.parseDouble(task.getLeftChild().getValue());
            double operand2 = Double.parseDouble(task.getRightChild().getValue());
            return operand1 - operand2;
        } else {
            throw new UnsupportedArithmeticOperation(getClass().getName() + " does not support: "+task.getValue());
        }
    }
}
