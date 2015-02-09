package overskaug.agents;

import overskaug.agents.solvers.*;

public class AdditionAgent extends ArithmeticAgent {

    @Override
    protected void setup() {
        addSolver(new AdditionSolver());
        init();
    }
}
