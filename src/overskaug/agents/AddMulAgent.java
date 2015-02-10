package overskaug.agents;

import overskaug.agents.solvers.*;

public class AddMulAgent extends ArithmeticAgent {

    @Override
    protected void setup() {
        addSolver(new AdditionSolver());
        addSolver(new MultiplicationSolver());
        init();
    }
}
