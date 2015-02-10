package overskaug.agents;

import overskaug.agents.solvers.*;

public class MulDivAgent extends ArithmeticAgent {

    @Override
    protected void setup() {
        addSolver(new MultiplicationSolver());
        addSolver(new DivisionSolver());
        init();
    }
}
