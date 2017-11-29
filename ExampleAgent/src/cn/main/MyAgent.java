package cn.main;

import java.util.List;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.EndNegotiation;
import negotiator.actions.Offer;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.parties.NegotiationInfo;
import negotiator.timeline.TimeLineInfo;

@SuppressWarnings("serial")
public class MyAgent extends AbstractNegotiationParty {

	private Bid lastReceivedOffer;
	private Bid myLastOffer;
	private double thresholdUtility;
	private Double updatePValueTime = 0.1D;
	private TimeLineInfo timeLineInfo;
	private MyNegotiationStrategy negotiationStrategy;
	private MyNegotiationInfo myInfo;
	private OppentNegotiationInfo oppent1Info;
	private OppentNegotiationInfo oppent2Info;

	@Override
	public void init(NegotiationInfo info) {
		super.init(info);
		thresholdUtility = 1.0;
		this.timeLineInfo = info.getTimeline();
		utilitySpace = info.getUtilitySpace();
		negotiationStrategy = new MyNegotiationStrategy(utilitySpace);
		try {
			myInfo = new MyNegotiationInfo(utilitySpace);
		} catch (Exception e) {
			System.out.println("初始化自己谈判信息失败");
			e.printStackTrace();
		}
	}

	@Override
	public Action chooseAction(List<Class<? extends Action>> validActions) {
		double time = timeLineInfo.getTime();
		// negotiationInfo.updateTimeScale(time);
		if (time > updatePValueTime){
			myInfo.optionPValueList(updatePValueTime, oppent1Info, oppent2Info);
			updatePValueTime += 0.1D;
			MyPrint.printOpponentFrequency(oppent1Info.getOpponentFrequency());
		}
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
			if (action instanceof Offer) {
				// 初始化oppentInfo
				lastReceivedOffer = ((Offer) action).getBid();
				if (oppent1Info == null) {
					oppent1Info = new OppentNegotiationInfo(utilitySpace, sender);
					Offer offer = (Offer) action;
					oppent1Info.addOppentHistory(offer.getBid());
				} else if (oppent2Info == null) {
					oppent2Info = new OppentNegotiationInfo(utilitySpace, sender);
					Offer offer = (Offer) action;
					oppent2Info.addOppentHistory(offer.getBid());
				}

				// 记录各个agent历史bid
				if (oppent1Info.getOppentID().equals(sender)) {
					Offer offer = (Offer) action;
					oppent1Info.addOppentHistory(offer.getBid());
				} else if (oppent2Info.getOppentID().equals(sender)) {
					Offer offer = (Offer) action;
					oppent2Info.addOppentHistory(offer.getBid());
				}
			} else if (action instanceof Accept) {
				// TODO
			}
		}

		// 输出信息
		if (timeline.getTime() > 1) {
			oppent1Info.printInfo();
			oppent2Info.printInfo();

			// 生成猜测到的对方最大utility的Bid
			// TODO:使用最大bid计算
			Bid max1Bid = oppent1Info.getMaxFrequencyBid();
			System.out.println(max1Bid.toString());
			System.out.println("对手1最大bid在我们agent的utility" + utilitySpace.getUtility(max1Bid));
			Bid max2Bid = oppent2Info.getMaxFrequencyBid();
			System.out.println(max2Bid.toString());
			System.out.println("对手2最大bid在我们agentutility" + utilitySpace.getUtility(max2Bid));
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
