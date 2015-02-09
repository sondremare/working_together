package overskaug.agents;

import overskaug.agents.solvers.*;

public class MultiplicationAgent extends ArithmeticAgent {

    @Override
    protected void setup() {
        addSolver(new MultiplicationSolver());
        init();
    }
}
