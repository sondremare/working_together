package overskaug.agents;

import overskaug.agents.solvers.*;
public class SubMulAgent extends ArithmeticAgent {

    @Override
    protected void setup() {
        addSolver(new SubtractionSolver());
        addSolver(new MultiplicationSolver());
        init();
    }
}
