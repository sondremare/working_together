package overskaug.agents.behaviour;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import overskaug.tree.Task;
import overskaug.util.TaskConverter;

public class StartAuctionBehaviour extends OneShotBehaviour {

    private Task task;
    private AID[] solverAgents;

    public StartAuctionBehaviour(Agent myAgent, Task task, AID[] solverAgents) {
        super(myAgent);
        this.task = task;
        this.solverAgents = solverAgents;
    }

    @Override
    public void action() {
        ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
        for (int i = 0; i < solverAgents.length; i++) {
            cfp.addReceiver(solverAgents[i]);
        }
        cfp.setContent(TaskConverter.stringify(task));
        cfp.setConversationId("solveArithmeticTask");
        cfp.setReplyWith("cfp"+System.currentTimeMillis());
        myAgent.send(cfp);
        myAgent.addBehaviour(new ReceiveBidBehaviour(myAgent, task, solverAgents, cfp));
    }
}
