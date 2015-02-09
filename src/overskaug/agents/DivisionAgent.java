package overskaug.agents;

import overskaug.agents.solvers.*;

public class DivisionAgent extends ArithmeticAgent {

    @Override
    protected void setup() {
        addSolver(new DivisionSolver());
        init();
    }
}
