package overskaug.agents.solvers;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import overskaug.tree.Task;
import overskaug.util.TaskConverter;

import java.util.ArrayList;

public class AdditionAgent extends Agent {

    ArrayList<Task> taskList = new ArrayList<Task>();

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

                String type = TaskConverter.getType(TaskConverter.parse(content));
                boolean capable = false;
                for (ArithmeticSolver solver : solvers) {
                    if (solver.getClass().getSimpleName().equals(type)) {
                        capable = true;
                    }
                }
                if (capable) {
                    double timeToComplete = taskList.size() * 2 + 2; //1 second per task already queued, and 1 second for the task at hand
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
                Task task = TaskConverter.parse(message.getContent());
                taskList.add(task);
                ACLMessage reply = message.createReply();
                double result = 0;
                boolean failed = false;
                try {
                    for (ArithmeticSolver solver : solvers) {
                        if (solver.getClass().getSimpleName().equals(TaskConverter.getType(task))) {
                            result = solver.solve(task);
                        }
                    }
                    Thread.sleep(5000);
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
                myAgent.send(reply);
            }
            else {
                block();
            }
        }
    }

}
