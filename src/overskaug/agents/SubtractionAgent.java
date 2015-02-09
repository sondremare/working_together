package overskaug.agents;

import overskaug.agents.solvers.*;

public class SubtractionAgent extends ArithmeticAgent {

    @Override
    protected void setup() {
        addSolver(new SubtractionSolver());
        init();
    }
}
