package overskaug.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import overskaug.tree.Task;
import overskaug.util.TaskConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class TaskAdministrator extends Agent {

    private String expressionToSolve;
    HashMap<String, AID[]> solverAgentMap = new HashMap<String, AID[]>();
    private ArrayList<Task> tasksToSolve;
    private ArrayList<Task> designatedTasks = new ArrayList<Task>();
    private Task rootNode;

    protected void setup() {
        System.out.println("Task administrator "+getAID().getName()+" is ready.");

        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            expressionToSolve = (String) args[0];
            System.out.println("Trying to solve: "+expressionToSolve);
            rootNode = Task.parsePrefix(convertExpressionToList(expressionToSolve));
            tasksToSolve = findSolvableTask(rootNode);
            while (tasksToSolve.size() > 0) {
                System.out.println("Top TasksToSolve: "+TaskConverter.stringify(tasksToSolve.get(0)));
                addBehaviour(new FindCapableAgentsBehaviour(tasksToSolve.get(0)));
                designatedTasks.add(tasksToSolve.get(0));
                tasksToSolve.remove(0);
            }
        }
    }

    public ArrayList<String> convertExpressionToList(String expression) {
        return new ArrayList<String>(Arrays.asList(expression.split(" ")));
    }

    /* This method traverses the tree structure and returns all task currently ready for solving */
    public static ArrayList<Task> findSolvableTask(Task root) {
        ArrayList<Task> solvableTasks = new ArrayList<Task>();
        if (!Task.isNumeric(root.getValue())) {
            traverse(root, solvableTasks);
        }
        return solvableTasks;

    }

    public static Task traverse(Task root, ArrayList<Task> tasks) {
       /* if (Task.isNumeric(root.getValue())) {
            return null;
        } */
        if (root.getLeftChild() != null && root.getRightChild() != null) {
            Task left = traverse(root.getLeftChild(), tasks);
            Task right = traverse(root.getRightChild(), tasks);
            if (Task.isNumeric(left.getValue()) && Task.isNumeric(right.getValue())) {
                tasks.add(root);
            }
        }
        return root;
    }

    private class ReceiveTaskBehaviour extends CyclicBehaviour {

        @Override
        public void action() {
            
        }
    }

    private class FindCapableAgentsBehaviour extends OneShotBehaviour {

        private Task task;

        public FindCapableAgentsBehaviour(Task task) {
            this.task = task;
        }

        @Override
        public void action() {
            String type = TaskConverter.getType(task);
            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription serviceDescription = new ServiceDescription();
            serviceDescription.setType(type);
            template.addServices(serviceDescription);
            try {
                DFAgentDescription[] result = DFService.search(myAgent, template);
                AID[] solverAgents = new AID[result.length];
                for (int i = 0; i < result.length; i++) {
                    solverAgents[i] = result[i].getName();
                }
                solverAgentMap.put(type, solverAgents);
            } catch (FIPAException e) {
                e.printStackTrace();
            }

            myAgent.addBehaviour(new StartAuctionBehaviour(task));
        }
    }

    private class StartAuctionBehaviour extends OneShotBehaviour {

        private Task task;

        public StartAuctionBehaviour(Task task) {
            this.task = task;
        }

        @Override
        public void action() {
            ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
            String type = TaskConverter.getType(task);
            AID[] solverAgents = solverAgentMap.get(type);
            for (int i = 0; i < solverAgents.length; i++) {
                cfp.addReceiver(solverAgents[i]);
            }
            cfp.setContent(TaskConverter.stringify(task));
            cfp.setConversationId("solveArithmeticTask");
            cfp.setReplyWith("cfp"+System.currentTimeMillis());
            myAgent.send(cfp);
            myAgent.addBehaviour(new ReceiveBidBehaviour(task, cfp));
        }
    }

    public class ReceiveBidBehaviour extends CyclicBehaviour {

        private Task task;
        private ACLMessage cfp;
        private AID bestBidder;
        private double shortestTime;
        private int replyCounter = 0;

        public ReceiveBidBehaviour(Task task, ACLMessage cfp) {
            this.task = task;
            this.cfp = cfp;
        }

        @Override
        public void action() {
            MessageTemplate messageTemplate = MessageTemplate.and(MessageTemplate.MatchConversationId("solveArithmeticTask"),
                    MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
            ACLMessage reply = myAgent.receive(messageTemplate);
            String type = TaskConverter.getType(task);
            AID[] solverAgents = solverAgentMap.get(type);
            if (reply != null) {
                if (reply.getPerformative() == ACLMessage.PROPOSE) {
                    double time = Double.parseDouble(reply.getContent());
                    if (bestBidder == null || time < shortestTime) {
                        shortestTime = time;
                        bestBidder = reply.getSender();
                    }
                }
                replyCounter++;
                if (replyCounter >= solverAgents.length) {
                    myAgent.addBehaviour(new AcceptProposalBehaviour(task, bestBidder));
                }

            } else {
                block();
            }
        }
    }

    public class AcceptProposalBehaviour extends OneShotBehaviour {

        private Task task;
        private AID bestBidder;

        public AcceptProposalBehaviour(Task task, AID bestBidder) {
            this.task = task;
            this.bestBidder = bestBidder;
        }

        @Override
        public void action() {
            ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
            order.addReceiver(bestBidder);
            order.setContent(TaskConverter.stringify(task));
            order.setConversationId("solveArithmeticTask");
            order.setReplyWith("order" + System.currentTimeMillis());
            myAgent.send(order);
            myAgent.addBehaviour(new ReceiveSolutionBehaviour(task, order));
        }
    }

    public class ReceiveSolutionBehaviour extends CyclicBehaviour {

        private Task task;
        private ACLMessage order;

        public ReceiveSolutionBehaviour(Task task, ACLMessage order) {
            this.task = task;
            this.order = order;
        }

        @Override
        public void action() {
            MessageTemplate messageTemplate = MessageTemplate.and(MessageTemplate.MatchConversationId("solveArithmeticTask"),
                    MessageTemplate.MatchInReplyTo(order.getReplyWith()));
            ACLMessage reply = myAgent.receive(messageTemplate);
            if (reply != null) {
                if (reply.getPerformative() == ACLMessage.INFORM) {
                    task.setValue(reply.getContent());
                    tasksToSolve = findSolvableTask(rootNode);
                    if (tasksToSolve.size() == 0) {
                        System.out.println("Answer is: "+rootNode.getValue());
                    } else {
                        for (int i = 0; i < tasksToSolve.size(); i++) {
                            boolean alreadyAssigned = false;
                            for (int j = 0; j < designatedTasks.size(); j++) {
                                if (tasksToSolve.get(i).equals(designatedTasks.get(j))) alreadyAssigned = true;
                            }
                            if (!alreadyAssigned) {
                                //System.out.println("Bottom: taskToSolve: "+TaskConverter.stringify(tasksToSolve.get(i)));
                                designatedTasks.add(tasksToSolve.get(i));
                                addBehaviour(new FindCapableAgentsBehaviour(tasksToSolve.get(i)));
                            }
                        }
                    }
                } else {
                    addBehaviour(new FindCapableAgentsBehaviour(task));
                }
            }
        }
    }
}
