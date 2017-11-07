package cn.other;
import agents.anac.y2010.AgentSmith.ABidStrategy;
import agents.anac.y2010.AgentSmith.BidHistory;
import agents.anac.y2010.AgentSmith.PreferenceProfileManager;
import agents.anac.y2010.AgentSmith.SmithBidStrategy;
import negotiator.Agent;
import negotiator.Bid;
import negotiator.SupportedNegotiationSetting;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.utility.AdditiveUtilitySpace;

// Referenced classes of package agents.anac.y2010.AgentSmith:
//            BidHistory, PreferenceProfileManager, SmithBidStrategy, ABidStrategy

public class AgentSmith extends Agent
{

    public AgentSmith()
    {
        firstRound = true;
        sMargin = 0.90000000000000002D;
    }

    public String getVersion()
    {
        return "3";
    }

    public String getName()
    {
        return "Agent Smith";
    }

    public void init()
    {
        fBidHistory = new BidHistory();
        fPreferenceProfile = new PreferenceProfileManager(fBidHistory, (AdditiveUtilitySpace)utilitySpace);
        fBidStrategy = new SmithBidStrategy(fBidHistory, (AdditiveUtilitySpace)utilitySpace, fPreferenceProfile, getAgentID());
    }

    public void ReceiveMessage(Action pAction)
    {
        if(pAction == null)
            return;
        if(pAction instanceof Offer)
        {
            Bid lBid = ((Offer)pAction).getBid();
            fBidHistory.addOpponentBid(lBid);
            fPreferenceProfile.addBid(lBid);
        }
    }

    public Action chooseAction()
    {
        Bid currentBid = null;
        Action currentAction = null;
        try
        {
        	
            if(fBidHistory.getOpponentLastBid() != null && utilitySpace.getUtility(fBidHistory.getOpponentLastBid()) > sMargin)
                currentAction = new Accept(getAgentID(), fBidHistory.getOpponentLastBid());
            else
            if(firstRound && fBidHistory.getMyLastBid() == null)
            {
                firstRound = !firstRound;
                currentBid = getInitialBid();
                currentAction = new Offer(getAgentID(), currentBid);
                Bid lBid = ((Offer)currentAction).getBid();
                fBidHistory.addMyBid(lBid);
            } else
            {
                double utilOpponent = utilitySpace.getUtility(fBidHistory.getOpponentLastBid());
                double utilOur = utilitySpace.getUtility(fBidHistory.getMyLastBid());
                if(utilOpponent >= utilOur)
                {
                    currentAction = new Accept(getAgentID(), fBidHistory.getOpponentLastBid());
                } else
                {
                    currentAction = fBidStrategy.getNextAction(timeline.getTime());
                    if(currentAction instanceof Offer)
                    {
                        Bid lBid = ((Offer)currentAction).getBid();
                        fBidHistory.addMyBid(lBid);
                    }
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return currentAction;
    }

    private Bid getInitialBid()
        throws Exception
    {
        return utilitySpace.getMaxUtilityBid();
    }

    public SupportedNegotiationSetting getSupportedNegotiationSetting()
    {
        return SupportedNegotiationSetting.getLinearUtilitySpaceInstance();
    }

    public String getDescription()
    {
        return "ANAC 2010 - AgentSmith";
    }

    private PreferenceProfileManager fPreferenceProfile;
    private ABidStrategy fBidStrategy;
    private agents.anac.y2010.AgentSmith.BidHistory fBidHistory;
    private boolean firstRound;
    private double sMargin;
}
