package overskaug.agents;

import overskaug.agents.solvers.*;

public class AddSubAgent extends ArithmeticAgent {

    @Override
    protected void setup() {
        addSolver(new AdditionSolver());
        addSolver(new SubtractionSolver());
        init();
    }
}
