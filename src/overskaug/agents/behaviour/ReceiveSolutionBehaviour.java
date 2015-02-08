package overskaug.agents.behaviour;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import overskaug.tree.Task;

public class ReceiveSolutionBehaviour extends CyclicBehaviour {

    private Task task;
    private ACLMessage order;


    public ReceiveSolutionBehaviour(Agent myAgent, Task task, ACLMessage order) {
        super(myAgent);
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
                System.out.println(order.getContent() + " solved by " + order.getSender().getName());
                task.setValue(reply.getContent());
            }
        }
    }
}
