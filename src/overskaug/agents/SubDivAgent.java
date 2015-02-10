package overskaug.agents;

import overskaug.agents.solvers.*;

public class SubDivAgent extends ArithmeticAgent {

    @Override
    protected void setup() {
        addSolver(new SubtractionSolver());
        addSolver(new DivisionSolver());
        init();
    }
}
