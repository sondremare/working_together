package overskaug.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import overskaug.tree.Task;
import overskaug.util.TaskConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class TaskAdministrator extends Agent {

    private String expressionToSolve;
    HashMap<String, AID[]> solverAgentMap = new HashMap<String, AID[]>();
    private ArrayList<Task> tasksToSolve;

    protected void setup() {
        System.out.println("Task administrator "+getAID().getName()+" is ready.");

        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            expressionToSolve = (String) args[0];
            System.out.println("Trying to solve: "+expressionToSolve);
            Task rootNode = Task.parsePrefix(convertExpressionToList(expressionToSolve));
            tasksToSolve = findSolvableTask(rootNode);

            addBehaviour(new TickerBehaviour(this, 10000) {
                @Override
                protected void onTick() {
                    String type = TaskConverter.getType(tasksToSolve.get(0));
                    DFAgentDescription template = new DFAgentDescription();
                    ServiceDescription serviceDescription = new ServiceDescription();
                    serviceDescription.setType(type);
                    System.out.println("TYPE: " + type);
                    template.addServices(serviceDescription);
                    try {
                        DFAgentDescription[] result = DFService.search(myAgent, template);
                        System.out.println("RESULT LENGTH: "+result.length);
                        AID[] solverAgents = new AID[result.length];
                        for (int i = 0; i < result.length; i++) {
                            solverAgents[i] = result[i].getName();
                            System.out.println(result[i].getName());
                        }
                        solverAgentMap.put(type, solverAgents);
                    } catch (FIPAException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public ArrayList<String> convertExpressionToList(String expression) {
        return new ArrayList<String>(Arrays.asList(expression.split(" ")));
    }

    /* This method traverses the tree structure and returns all task currently ready for solving */
    public static ArrayList<Task> findSolvableTask(Task root) {
        ArrayList<Task> solvableTasks = new ArrayList<Task>();
        traverse(root, solvableTasks);
        return solvableTasks;

    }

    public static Task traverse(Task root, ArrayList<Task> tasks) {
        if (root.getLeftChild() != null && root.getRightChild() != null) {
            Task left = traverse(root.getLeftChild(), tasks);
            Task right = traverse(root.getRightChild(), tasks);
            if (left == null && right == null) {
                tasks.add(root);
                return root;
            }
        }
        return null;
    }
}
