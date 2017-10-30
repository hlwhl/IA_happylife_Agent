import negotiator.AgentID;
import negotiator.actions.Action;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.parties.NegotiationInfo;

import java.util.List;

public class agent extends AbstractNegotiationParty {
    @Override
    public void init(NegotiationInfo info) {
        super.init(info);
    }

    @Override
    public Action chooseAction(List<Class<? extends Action>> list) {
        	System.out.print("aaa");
    		return null;
    }

    @Override
    public void receiveMessage(AgentID sender, Action act) {
        super.receiveMessage(sender, act);
    }

    @Override
    public String getDescription() {
        return null;
    }
}
