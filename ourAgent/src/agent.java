import negotiator.actions.Action;
import negotiator.parties.AbstractNegotiationParty;

import java.util.List;

public class agent extends AbstractNegotiationParty {
    @Override
    public Action chooseAction(List<Class<? extends Action>> list) {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }
}
