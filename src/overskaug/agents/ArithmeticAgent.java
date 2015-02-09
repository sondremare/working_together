package overskaug.agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import overskaug.agents.solvers.AdditionSolver;
import overskaug.agents.solvers.ArithmeticSolver;
import overskaug.agents.solvers.UnsupportedArithmeticOperation;
import overskaug.tree.Task;
import overskaug.util.TaskUtils;

import java.util.ArrayList;

public class ArithmeticAgent extends Agent {

    ArrayList<Task> taskList = new ArrayList<Task>();

    ArrayList<ArithmeticSolver> solvers = new ArrayList<ArithmeticSolver>();

    public void initSolvers() {
        AdditionSolver additionSolver = new AdditionSolver();
        solvers.add(additionSolver);
    }

    public void addSolver(ArithmeticSolver solver) {
        this.solvers.add(solver);
    }

    public ArrayList<ArithmeticSolver> getSolvers() {
        return this.solvers;
    }

    public void init() {
        for (ArithmeticSolver solver : solvers) {
            DFAgentDescription dfAgentDescription = new DFAgentDescription();
            dfAgentDescription.setName(getAID());
            ServiceDescription serviceDescription = new ServiceDescription();
            serviceDescription.setType(solver.getClass().getSimpleName());
            serviceDescription.setName(getClass().getName());
            dfAgentDescription.addServices(serviceDescription);
            try {
                DFService.register(this, dfAgentDescription);
            } catch (FIPAException e) {
                e.printStackTrace();
            }
        }

        addBehaviour(new ReceiveCFPBehaviour());
        addBehaviour(new PerformTaskBehaviour());
    }

    protected void setup() {
        /* empty as this class should not be used directly */

    }

    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        System.out.println(getClass().getName()+" "+getAID().getName()+" terminating");
    }

    private class ReceiveCFPBehaviour extends CyclicBehaviour {

        @Override
        public void action() {
            MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(ACLMessage.CFP);
            ACLMessage message = myAgent.receive(messageTemplate);
            if (message != null) {
                String content = message.getContent();
                ACLMessage reply = message.createReply();

                String type = TaskUtils.getType(TaskUtils.parse(content));
                boolean capable = false;
                for (ArithmeticSolver solver : solvers) {
                    if (solver.getClass().getSimpleName().equals(type)) {
                        capable = true;
                    }
                }
                if (capable) {
                    double timeToComplete = taskList.size() * 1 + 1; //1 second per task already queued, and 1 second for the task at hand
                    reply.setPerformative(ACLMessage.PROPOSE);
                    reply.setContent(String.valueOf(timeToComplete));
                } else {
                    reply.setPerformative(ACLMessage.REFUSE);
                    reply.setContent("NA");
                }
                myAgent.send(reply);
            } else {
                block();
            }

        }
    }

    private class PerformTaskBehaviour extends CyclicBehaviour {

        @Override
        public void action() {
            MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
            ACLMessage message = myAgent.receive(messageTemplate);
            if (message != null) {
                Task task = TaskUtils.parse(message.getContent());
                taskList.add(task);
                ACLMessage reply = message.createReply();
                double result = 0;
                boolean failed = false;
                try {
                    for (ArithmeticSolver solver : solvers) {
                        if (solver.getClass().getSimpleName().equals(TaskUtils.getType(task))) {
                            result = solver.solve(task);
                        }
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (UnsupportedArithmeticOperation unsupportedArithmeticOperation) {
                    failed = true;
                }
                if (failed) {
                    reply.setPerformative(ACLMessage.FAILURE);
                    reply.setContent("Not able to solve task");
                } else {
                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent(String.valueOf(result));
                }
                taskList.remove(task);
                myAgent.send(reply);
            }
            else {
                block();
            }
        }
    }

}
