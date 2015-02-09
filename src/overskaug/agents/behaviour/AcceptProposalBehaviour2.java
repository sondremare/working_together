package overskaug.agents.behaviour;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import overskaug.tree.Task;
import overskaug.util.TaskConverter;

public class AcceptProposalBehaviour2 extends OneShotBehaviour {

    private Task task;
    private ACLMessage reply;
    private AID bestBidder;

    public AcceptProposalBehaviour2(Agent myAgent, Task task, AID bestBidder, ACLMessage reply) {
        super(myAgent);
        this.task = task;
        this.bestBidder = bestBidder;
        this.reply = reply;
    }

    @Override
    public void action() {
        ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
        order.addReceiver(bestBidder);
        order.setContent(TaskConverter.stringify(task));
        order.setConversationId("solveArithmeticTask");
        order.setReplyWith("order"+System.currentTimeMillis());
        myAgent.send(order);
        myAgent.addBehaviour(new ReceiveSolutionBehaviour2(myAgent, task, order));
    }
}
