package overskaug.agents.behaviour;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import overskaug.tree.Task;

public class ReceiveBidBehaviour2 extends CyclicBehaviour {

    private Task task;
    private AID[] solverAgents;
    private ACLMessage cfp;
    private AID bestBidder;
    private int shortestTime;
    private int replyCounter = 0;

    public ReceiveBidBehaviour2(Agent myAgent, Task task, AID[] solverAgents, ACLMessage cfp) {
        super(myAgent);
        this.task = task;
        this.solverAgents = solverAgents;
        this.cfp = cfp;
    }

    @Override
    public void action() {
        MessageTemplate messageTemplate = MessageTemplate.and(MessageTemplate.MatchConversationId("solver"),
                MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
        ACLMessage reply = myAgent.receive(messageTemplate);
        if (reply != null) {
            if (reply.getPerformative() == ACLMessage.PROPOSE) {
                int time = Integer.parseInt(reply.getContent());
                if (bestBidder == null || time < shortestTime) {
                    shortestTime = time;
                    bestBidder = reply.getSender();
                }
            }
            replyCounter++;
            if (replyCounter >= solverAgents.length) {
                myAgent.addBehaviour(new AcceptProposalBehaviour2(myAgent, task, bestBidder, reply));
            }

        } else {
            block();
        }
    }
}
