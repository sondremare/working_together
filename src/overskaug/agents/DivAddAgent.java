package overskaug.agents;

import overskaug.agents.solvers.*;

public class DivAddAgent extends ArithmeticAgent {

    @Override
    protected void setup() {
        addSolver(new DivisionSolver());
        addSolver(new AdditionSolver());
        init();
    }
}
