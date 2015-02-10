package overskaug.agents.solvers;

import overskaug.tree.Task;
import overskaug.util.TaskUtils;

public class SubtractionSolver implements ArithmeticSolver {

    @Override
    public double solve(Task task) throws UnsupportedArithmeticOperation {
        System.out.println("SOLVING: "+ TaskUtils.stringify(task));
        if (task.getValue().equals("-")) {
            double operand1 = Double.parseDouble(task.getLeftChild().getValue());
            double operand2 = Double.parseDouble(task.getRightChild().getValue());
            return operand1 - operand2;
        } else {
            throw new UnsupportedArithmeticOperation(getClass().getName() + " does not support: "+task.getValue());
        }
    }
}
