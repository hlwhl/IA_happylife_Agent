package cn.other;
import agents.anac.y2016.atlas3.etc.bidSearch;
import agents.anac.y2016.atlas3.etc.negotiationInfo;
import agents.anac.y2016.atlas3.etc.negotiationStrategy;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import negotiator.*;
import negotiator.actions.*;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.parties.NegotiationInfo;
import negotiator.timeline.TimeLineInfo;
import negotiator.utility.AbstractUtilitySpace;

public class Atlas32016 extends AbstractNegotiationParty
{

    public Atlas32016()
    {
        offeredBid = null;
        supporter_num = 0;
        CList_index = 0;
        isPrinting = false;
    }

    public void init(NegotiationInfo info)
    {
        super.init(info);
        if(isPrinting)
            System.out.println("*** SampleAgent2016 v1.0 ***");
        timeLineInfo = info.getTimeline();
        utilitySpace = info.getUtilitySpace();
        negotiationInfo = new negotiationInfo(utilitySpace, isPrinting);
        negotiationStrategy = new negotiationStrategy(utilitySpace, negotiationInfo, isPrinting);
        try
        {
            bidSearch = new bidSearch(utilitySpace, negotiationInfo, isPrinting);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public Action chooseAction(List validActions)
    {
        double time = timeLineInfo.getTime();
        negotiationInfo.updateTimeScale(time);
        ArrayList CList = negotiationInfo.getPBList();
        if(time > 1.0D - negotiationInfo.getTimeScale() * (double)(CList.size() + 1))
            try
            {
                return chooseFinalAction(offeredBid, CList);
            }
            catch(Exception e)
            {
                System.out.println("\u6700\u7D42\u63D0\u6848\u30D5\u30A7\u30FC\u30BA\u306B\u304A\u3051\u308BAction\u306E\u9078\u629E\u306B\u5931\u6557\u3057\u307E\u3057\u305F");
                e.printStackTrace();
            }
        if(validActions.contains(Accept.class) && negotiationStrategy.selectAccept(offeredBid, time))
            return new Accept(getPartyId(), offeredBid);
        if(negotiationStrategy.selectEndNegotiation(time))
            return new EndNegotiation(getPartyId());
        else
            return OfferAction();
    }

    public Action OfferAction()
    {
        Bid offerBid = bidSearch.getBid(utilitySpace.getDomain().getRandomBid(null), negotiationStrategy.getThreshold(timeLineInfo.getTime()));
        return OfferBidAction(offerBid);
    }

    public Action OfferBidAction(Bid offerBid)
    {
        negotiationInfo.updateMyBidHistory(offerBid);
        return new Offer(getPartyId(), offerBid);
    }

    public Action chooseFinalAction(Bid offeredBid, ArrayList CList)
        throws Exception
    {
        double offeredBid_util = 0.0D;
        double rv = utilitySpace.getReservationValue().doubleValue();
        if(offeredBid != null)
            offeredBid_util = utilitySpace.getUtility(offeredBid);
        if(CList_index >= CList.size())
        {
            if(offeredBid_util >= rv)
                return new Accept(getPartyId(), offeredBid);
            OfferAction();
        }
        Bid CBid = (Bid)CList.get(CList_index);
        double CBid_util = utilitySpace.getUtility(CBid);
        if(CBid_util > offeredBid_util && CBid_util > rv)
        {
            CList_index++;
            OfferBidAction(CBid);
        } else
        if(offeredBid_util > rv)
            return new Accept(getPartyId(), offeredBid);
        return OfferAction();
    }

    public void receiveMessage(AgentID sender, Action action)
    {
        super.receiveMessage(sender, action);
        if(isPrinting)
            System.out.println((new StringBuilder("Sender:")).append(sender).append(", Action:").append(action).toString());
        if(action != null)
        {
            if((action instanceof Inform) && ((Inform)action).getName() == "NumberOfAgents" && (((Inform)action).getValue() instanceof Integer))
            {
                Integer opponentsNum = (Integer)((Inform)action).getValue();
                negotiationInfo.updateOpponentsNum(opponentsNum.intValue());
                if(isPrinting)
                    System.out.println((new StringBuilder("NumberofNegotiator:")).append(negotiationInfo.getNegotiatorNum()).toString());
            } else
            if(action instanceof Accept)
            {
                if(!negotiationInfo.getOpponents().contains(sender))
                    negotiationInfo.initOpponent(sender);
                supporter_num++;
            } else
            if(action instanceof Offer)
            {
                if(!negotiationInfo.getOpponents().contains(sender))
                    negotiationInfo.initOpponent(sender);
                supporter_num = 1;
                offeredBid = ((Offer)action).getBid();
                try
                {
                    negotiationInfo.updateInfo(sender, offeredBid);
                }
                catch(Exception e)
                {
                    System.out.println("\u4EA4\u6E09\u60C5\u5831\u306E\u66F4\u65B0\u306B\u5931\u6557\u3057\u307E\u3057\u305F");
                    e.printStackTrace();
                }
            } else
            {
                boolean _tmp = action instanceof EndNegotiation;
            }
            if(supporter_num == negotiationInfo.getNegotiatorNum() - 1 && offeredBid != null)
                try
                {
                    negotiationInfo.updatePBList(offeredBid);
                }
                catch(Exception e)
                {
                    System.out.println("PBList\u306E\u66F4\u65B0\u306B\u5931\u6557\u3057\u307E\u3057\u305F");
                    e.printStackTrace();
                }
        }
    }

    public String getDescription()
    {
        return "ANAC2016 Atlas3";
    }

    private TimeLineInfo timeLineInfo;
    private AbstractUtilitySpace utilitySpace;
    private negotiationInfo negotiationInfo;
    private bidSearch bidSearch;
    private negotiationStrategy negotiationStrategy;
    private Bid offeredBid;
    private int supporter_num;
    private int CList_index;
    private boolean isPrinting;
}
