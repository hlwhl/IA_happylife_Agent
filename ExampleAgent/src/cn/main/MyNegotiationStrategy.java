package cn.main;

import negotiator.Bid;
import negotiator.actions.Action;
import negotiator.utility.AbstractUtilitySpace;

public class MyNegotiationStrategy {
	private AbstractUtilitySpace utilitySpace;
	private MyNegotiationInfo negotiationInfo;

	public MyNegotiationStrategy(AbstractUtilitySpace utilitySpace, MyNegotiationInfo negotiationInfo) {
		this.utilitySpace = utilitySpace;
		this.negotiationInfo = negotiationInfo;
	}

	public boolean selectAccept(Bid lastReceivedOffer, double time) {
		double utility = utilitySpace.getUtility(lastReceivedOffer);
		return utility > 0.9d;
	}

	public boolean selectEndNegotiation(double time) {
		
		return false;
	}

	public Action OfferAction() {
		
		return null;
	}
}
