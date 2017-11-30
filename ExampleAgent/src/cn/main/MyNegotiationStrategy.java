package cn.main;

import negotiator.Bid;
import negotiator.actions.Action;
import negotiator.utility.AbstractUtilitySpace;

public class MyNegotiationStrategy {
	private AbstractUtilitySpace utilitySpace;

	public MyNegotiationStrategy(AbstractUtilitySpace utilitySpace) {
		this.utilitySpace = utilitySpace;
	}

	public boolean selectAccept(Bid lastReceivedOffer, double time) {
		double u = utilitySpace.getUtility(lastReceivedOffer);
		double utility = 0;
		if (time <= 0.25D) {
			utility = 1.0D - time * 0.4D;
		}
		if ((time > 0.25D) && (time <= 0.375D)) {
			utility = 0.9D + (time - 0.25D) * 0.4D;
		}
		if ((time > 0.375D) && (time <= 0.5D)) {
			utility = 0.95D - (time - 0.375D) * 0.4D;
		}
		if ((time > 0.5D) && (time <= 0.6D)) {
			utility = 0.9D - (time - 0.5D);
		}
		if ((time > 0.6D) && (time <= 0.7D)) {
			utility = 0.8D + (time - 0.6D) * 2.0D;
		}
		if ((time > 0.7D) && (time <= 0.8D)) {
			utility = 1.0D - (time - 0.7D) * 3.0D;
		}
		if ((time > 0.8D) && (time <= 0.9D)) {
			utility = 0.7D + (time - 0.8D) * 1.0D;
		}
		if ((time > 0.9D) && (time <= 0.95D)) {
			utility = 0.8D - (time - 0.9D) * 6.0D;
		}
		if (time > 0.95D) {
			utility = 0.5D + (time - 0.95D) * 4.0D;
		}
		if (time > 1) {
			utility = 0.8;
		}
		if(time>0.99){

		}
		System.out.println("测试"+utility);
		//		if (time > 0.99) {
		//			utility=utilitySpace.getReservationValue();
		//		}
		return u > utility;
	}

	public boolean selectEndNegotiation(double time) {

		return false;
	}

	public Action OfferAction() {

		return null;
	}
}
