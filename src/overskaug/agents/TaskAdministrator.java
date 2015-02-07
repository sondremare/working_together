package overskaug.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import overskaug.tree.Task;

import java.util.ArrayList;
import java.util.Arrays;

public class TaskAdministrator extends Agent {

    private String expressionToSolve;
    private AID[] solverAgents;

    protected void setup() {
        System.out.println("Task administrator "+getAID().getName()+" is ready.");

        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            expressionToSolve = (String) args[0];
            System.out.println("Trying to solve: "+expressionToSolve);
            Task rootNode = Task.parsePrefix(convertExpressionToList(expressionToSolve));


            addBehaviour(new TickerBehaviour(this, 6000) {
                @Override
                protected void onTick() {
                    DFAgentDescription template = new DFAgentDescription();
                    ServiceDescription serviceDescription = new ServiceDescription();
                    serviceDescription.setType("arithmetic-solving");
                }
            });
        }
    }

    public ArrayList<String> convertExpressionToList(String expression) {
        return new ArrayList<String>(Arrays.asList(expression.split(" ")));
    }

    /* This method traverses the tree structure and returns all task currently ready for solving */
    public ArrayList<Task> findSolvableTask(Task root) {
        ArrayList<Task> solvableTasks = new ArrayList<Task>();
        traverse(root, solvableTasks);
        return solvableTasks;

    }

    public Task traverse(Task root, ArrayList<Task> tasks) {
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
