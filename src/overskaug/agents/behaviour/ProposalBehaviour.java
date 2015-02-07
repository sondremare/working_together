package overskaug.agents.behaviour;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class ProposalBehaviour extends CyclicBehaviour{
    @Override
    public void action() {
        ACLMessage message = myAgent.receive();
        if (message != null) {
            String task = message.getContent();
            ACLMessage reply = message.createReply();



        }
    }
}
