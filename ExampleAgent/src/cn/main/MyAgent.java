package cn.main;

import java.util.List;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.EndNegotiation;
import negotiator.actions.Inform;
import negotiator.actions.Offer;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.parties.NegotiationInfo;
import negotiator.timeline.TimeLineInfo;

@SuppressWarnings("serial")
public class MyAgent extends AbstractNegotiationParty {

	private Bid lastReceivedOffer;
	private Bid myLastOffer;
	private double thresholdUtility;
	private TimeLineInfo timeLineInfo;
	private MyNegotiationInfo negotiationInfo;
	private MyNegotiationStrategy negotiationStrategy;

	@Override
	public void init(NegotiationInfo info) {
		super.init(info);
		thresholdUtility = 1;
		this.timeLineInfo = info.getTimeline();
		utilitySpace = info.getUtilitySpace();
		negotiationInfo = new MyNegotiationInfo(utilitySpace);
		negotiationStrategy = new MyNegotiationStrategy(utilitySpace, negotiationInfo);
	}

	@Override
	public Action chooseAction(List<Class<? extends Action>> validActions) {
		double time = timeLineInfo.getTime();
		negotiationInfo.updateTimeScale(time);
		if (validActions.contains(Accept.class) && negotiationStrategy.selectAccept(lastReceivedOffer, time))
			return new Accept(getPartyId(), lastReceivedOffer);
		if (negotiationStrategy.selectEndNegotiation(time)) return new EndNegotiation(getPartyId());
		else return OfferAction();

	}

	private Action OfferAction() {
		Bid bid = generateRandomBid();
		while (getUtility(bid) < 0.9) {
			bid = generateRandomBid();
		}
		return new Offer(getPartyId(), bid);
	}

	@Override
	public void receiveMessage(AgentID sender, Action action) {
		super.receiveMessage(sender, action);
		if (action != null) {
			if ((action instanceof Inform) && ((Inform) action).getName() == "NumberOfAgents"
					&& (((Inform) action).getValue() instanceof Integer)) {
				Integer opponentsNum = (Integer) ((Inform) action).getValue();
				negotiationInfo.updateOpponentsNum(opponentsNum.intValue());
			} else if (action instanceof Accept) {
				if (!(negotiationInfo).getOpponents().contains(sender)) negotiationInfo.initOpponent(sender);
				// supporter_num++;
			} else if (action instanceof Offer) {
				if (!negotiationInfo.getOpponents().contains(sender)) negotiationInfo.initOpponent(sender);
				// supporter_num = 1;
				lastReceivedOffer = ((Offer) action).getBid();
				try {
					negotiationInfo.updateInfo(sender, lastReceivedOffer);
					if (timeLineInfo.getTime() > 0.99)
						negotiationInfo.printInfo();
				} catch (Exception e) {
					System.out.println("更新谈判信息失败");
					e.printStackTrace();
				}
			}
		}
	}

	private void updateThresholdUtility() {
		double currenttime = timeline.getCurrentTime();
		this.thresholdUtility = 1.0D - Math.pow(1.1, currenttime - 180);
	}

	@Override
	public String getDescription() {
		return "My agent";
	}
}
