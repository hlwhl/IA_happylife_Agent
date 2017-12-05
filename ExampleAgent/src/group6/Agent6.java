package group6;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.EndNegotiation;
import negotiator.actions.Offer;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.parties.NegotiationInfo;
import negotiator.timeline.TimeLineInfo;

import java.util.List;

@SuppressWarnings("serial")
public class Agent6 extends AbstractNegotiationParty {

	private Bid lastReceivedOffer;
	private Bid myLastOffer;
	private double thresholdUtility;
	private Double updatePValueTime = 0.1D;
	private Double updateNashTime = 0.1D;
	private TimeLineInfo timeLineInfo;
	private MyNegotiationStrategy negotiationStrategy;
	private MyNegotiationInfo myInfo;
	private OppentNegotiationInfo oppent1Info;
	private OppentNegotiationInfo oppent2Info;
	private int round = 0;

	@Override
	public void init(NegotiationInfo info) {
		super.init(info);
		thresholdUtility = 1.0;
		this.timeLineInfo = info.getTimeline();
		utilitySpace = info.getUtilitySpace();
		negotiationStrategy = new MyNegotiationStrategy(utilitySpace);
		try {
			myInfo = new MyNegotiationInfo(utilitySpace, negotiationStrategy);
		} catch (Exception e) {
			System.out.println("初始化自己谈判信息失败");
			e.printStackTrace();
		}
	}

	@Override
	public Action chooseAction(List<Class<? extends Action>> validActions) {
		round++;
		double time = timeLineInfo.getTime();
		if (validActions.contains(Accept.class) && negotiationStrategy.selectAccept(lastReceivedOffer, time))
			return new Accept(getPartyId(), lastReceivedOffer);
		if (negotiationStrategy.selectEndNegotiation(time)) return new EndNegotiation(getPartyId());
		else try {
			return OfferAction();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return OfferAction();
	}

	private Action OfferAction() {

		if (timeline.getTime() < 0.8) {
			Bid bid = null;
			try {
				bid = utilitySpace.getMaxUtilityBid();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return new Offer(getPartyId(), bid);
		} else {
			Bid bid = negotiationStrategy.normalChooseBid(timeline.getTime(), oppent1Info, oppent2Info, myInfo, round);
			return new Offer(getPartyId(), bid);
		}
	}

	@Override
	public void receiveMessage(AgentID sender, Action action) {
		super.receiveMessage(sender, action);
		if (action != null) {
			if (action instanceof Offer) {
				// 初始化oppentInfo
				lastReceivedOffer = ((Offer) action).getBid();
				if (oppent1Info == null) {
					oppent1Info = new OppentNegotiationInfo(utilitySpace, sender);
				} else if (oppent2Info == null && !oppent1Info.getOppentID().equals(sender)) {
					oppent2Info = new OppentNegotiationInfo(utilitySpace, sender);
				}

				OppentNegotiationInfo oppentInfo = null;
				if (oppent1Info.getOppentID().equals(sender)) {
					oppentInfo = oppent1Info;
				} else if (oppent2Info.getOppentID().equals(sender)) {
					oppentInfo = oppent2Info;
				}

				Offer offer = (Offer) action;
				oppentInfo.optionOppentInfo(offer.getBid(), round);
				double time = timeLineInfo.getTime();
				if (time > updatePValueTime) {
					myInfo.optionPValueList(oppentInfo);
					updatePValueTime += 0.1D;
					MyPrint.printOpponentFrequency(oppentInfo.getOpponentFrequency());
				}
				if (time > updateNashTime) {
					negotiationStrategy.updateNash(oppent1Info, oppent2Info);
					updateNashTime += 0.1D;
				}
			} else if (action instanceof Accept) {
				// TODO
			}
		}
	}

	@Override
	public String getDescription() {
		return "group6 agent";
	}

	public Bid getLastReceivedOffer() {
		return lastReceivedOffer;
	}

	public void setLastReceivedOffer(Bid lastReceivedOffer) {
		this.lastReceivedOffer = lastReceivedOffer;
	}

	public Bid getMyLastOffer() {
		return myLastOffer;
	}

	public void setMyLastOffer(Bid myLastOffer) {
		this.myLastOffer = myLastOffer;
	}

	public double getThresholdUtility() {
		return thresholdUtility;
	}

	public void setThresholdUtility(double thresholdUtility) {
		this.thresholdUtility = thresholdUtility;
	}

	public Double getUpdatePValueTime() {
		return updatePValueTime;
	}

	public void setUpdatePValueTime(Double updatePValueTime) {
		this.updatePValueTime = updatePValueTime;
	}

	public TimeLineInfo getTimeLineInfo() {
		return timeLineInfo;
	}

	public void setTimeLineInfo(TimeLineInfo timeLineInfo) {
		this.timeLineInfo = timeLineInfo;
	}

	public MyNegotiationStrategy getNegotiationStrategy() {
		return negotiationStrategy;
	}

	public void setNegotiationStrategy(MyNegotiationStrategy negotiationStrategy) {
		this.negotiationStrategy = negotiationStrategy;
	}

	public MyNegotiationInfo getMyInfo() {
		return myInfo;
	}

	public void setMyInfo(MyNegotiationInfo myInfo) {
		this.myInfo = myInfo;
	}

	public OppentNegotiationInfo getOppent1Info() {
		return oppent1Info;
	}

	public void setOppent1Info(OppentNegotiationInfo oppent1Info) {
		this.oppent1Info = oppent1Info;
	}

	public OppentNegotiationInfo getOppent2Info() {
		return oppent2Info;
	}

	public void setOppent2Info(OppentNegotiationInfo oppent2Info) {
		this.oppent2Info = oppent2Info;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}
	
	
}