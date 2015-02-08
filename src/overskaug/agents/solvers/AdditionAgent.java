package overskaug.agents.solvers;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.ArrayList;

public class AdditionAgent extends Agent {

    ArrayList<ArithmeticSolver> solvers = new ArrayList<ArithmeticSolver>();

    public void initSolvers() {
        AdditionSolver additionSolver = new AdditionSolver();
        solvers.add(additionSolver);
    }

    protected void setup() {
        initSolvers();
        for (ArithmeticSolver solver : solvers) {
            DFAgentDescription dfAgentDescription = new DFAgentDescription();
            dfAgentDescription.setName(getAID());
            ServiceDescription serviceDescription = new ServiceDescription();
            serviceDescription.setType(solver.getClass().getSimpleName());
            System.out.println(serviceDescription.getType());
            serviceDescription.setName(getClass().getName());
            dfAgentDescription.addServices(serviceDescription);
            try {
                System.out.println("Registered");
                DFService.register(this, dfAgentDescription);
            } catch (FIPAException e) {
                e.printStackTrace();
            }
        }

        //addBehaviour();
    }

    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        System.out.println(getClass().getName()+" "+getAID().getName()+" terminating");
    }

}
