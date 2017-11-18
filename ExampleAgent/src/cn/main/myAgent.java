package cn.main;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.parties.NegotiationInfo;
import negotiator.timeline.TimeLineInfo;

import java.util.List;

public class myAgent extends AbstractNegotiationParty {

    private Bid lastReceivedOffer;
    private Bid myLastOffer;
    private double thresholdUtility;
    private TimeLineInfo timeline;

    @Override
    public void init(NegotiationInfo info) {
        super.init(info);
        thresholdUtility=1;
        this.timeline=info.getTimeline();
    }

    @Override
    public Action chooseAction(List<Class<? extends Action>> list) {
        updateThresholdUtility();
        if(lastReceivedOffer!=null && this.utilitySpace.getUtility(lastReceivedOffer)>=thresholdUtility){
            return new Accept(this.getPartyId(),this.lastReceivedOffer);
        }else{
            do {
                myLastOffer=generateRandomBid();
            }while (this.getUtility(myLastOffer)<thresholdUtility);
            return new Offer(this.getPartyId(),myLastOffer);
        }
    }

    @Override
    public void receiveMessage(AgentID sender, Action act) {
        super.receiveMessage(sender, act);
        if(act instanceof Offer){
            Offer offer= (Offer) act;
            lastReceivedOffer=offer.getBid();
        }
    }


    private void updateThresholdUtility(){
        double currenttime=timeline.getCurrentTime();
        this.thresholdUtility=1.0D-Math.pow(1.1,timeline.getCurrentTime()-180);
    }

    @Override
    public String getDescription() {
        return null;
    }
}
