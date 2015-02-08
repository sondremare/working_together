package overskaug.agents.behaviour;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import overskaug.tree.Task;
import overskaug.util.TaskConverter;

public class FindCapableAgentsBehaviour extends OneShotBehaviour {

    private Task task;
    private AID[] solverAgents;

    public FindCapableAgentsBehaviour(Agent agent, Task task) {
        super(agent);
        this.task = task;
    }

    @Override
    public void action() {
        String type = TaskConverter.getType(task);
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(type);
        System.out.println("TYPE: " + type);
        template.addServices(serviceDescription);
        try {
            DFAgentDescription[] result = DFService.search(myAgent, template);
            System.out.println("RESULT LENGTH: "+result.length);
            solverAgents = new AID[result.length];
            for (int i = 0; i < result.length; i++) {
                solverAgents[i] = result[i].getName();
                System.out.println(result[i].getName());
            }
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        myAgent.addBehaviour(new StartAuctionBehaviour(myAgent, task, solverAgents));
    }
}
