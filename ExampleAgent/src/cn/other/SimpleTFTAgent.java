package cn.other;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import negotiator.Agent;
import negotiator.Bid;
import negotiator.Domain;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.issue.Issue;
import negotiator.issue.Value;
import negotiator.issue.ValueReal;
import negotiator.utility.AbstractUtilitySpace;

public class SimpleTFTAgent extends Agent {
	private static int round = 0;
	private Bid myLastBid = null;
	private Action opponentAction = null;

	public SimpleTFTAgent() {
	}

	public void init() {
		opponentPreviousBids = new ArrayList();
	}

	private List<Bid> opponentPreviousBids;

	public String getVersion() {
		return "1.0";
	}

	public void ReceiveMessage(Action opponentAction) {
		this.opponentAction = opponentAction;
	}

	public Action chooseAction() {
		Action myAction = null;

		if (round == 0) {
			myAction = chooseOpeningAction();
		} else if (round == 1) {
			myAction = chooseOffer2();
		} else if (round == 2) {
			myAction = chooseOffer3();
		} else if (round == 3) {
			myAction = chooseOffer4();
		} else if ((opponentAction instanceof Offer)) {
			myAction = chooseCounterOffer();
		}

		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if ((myAction instanceof Offer)) myLastBid = ((Offer) myAction).getBid();
		if ((opponentAction instanceof Offer)) {
			opponentPreviousBids.add(((Offer) opponentAction).getBid());
		}
		System.out.println("Round " + round + ", " + getName() + " offers " + myAction);
		round += 1;

		return myAction;
	}

	private Action chooseCounterOffer() {
		Bid opponentBid = ((Offer) opponentAction).getBid();
		double opponentOffer = toOffer(opponentBid);
		Bid opponentPreviousBid = (Bid) opponentPreviousBids.get(opponentPreviousBids.size() - 1);
		double previousOpponentOffer = toOffer(opponentPreviousBid);

		double myPreviousOffer = toOffer(myLastBid);

		double myOffer = previousOpponentOffer - opponentOffer + myPreviousOffer;

		if (getName().equals("Agent B")) myOffer = 0.3D - 5.0D / round * 0.1D;
		Domain domain = utilitySpace.getDomain();
		Issue pieForOne = (Issue) domain.getIssues().get(0);
		HashMap<Integer, Value> myOfferedPackage = new HashMap();
		ValueReal value = new ValueReal(myOffer);
		myOfferedPackage.put(Integer.valueOf(pieForOne.getNumber()), value);
		Bid firstBid = null;
		try {
			firstBid = new Bid(domain, myOfferedPackage);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(getName() + " previously got " + previousOpponentOffer + " and now gets offer "
				+ opponentOffer + " and counter-offers " + myOffer);

		return new Offer(getAgentID(), firstBid);
	}

	private double toOffer(Bid bid) {
		Domain domain = utilitySpace.getDomain();
		Issue pieForOne = (Issue) domain.getIssues().get(0);
		try {
			return ((ValueReal) bid.getValue(pieForOne.getNumber())).getValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1.0D;
	}

	private ValueReal personalValue2IssueValue(double personalValue) {
		ValueReal value;
		if (getName().equals("Agent A")) {
			value = new ValueReal(personalValue);
		} else value = new ValueReal(1.0D - personalValue);
		return value;
	}

	private Action chooseOpeningAction() {
		Domain domain = utilitySpace.getDomain();
		Issue pie = (Issue) domain.getIssues().get(0);
		HashMap<Integer, Value> myOfferedPackage = new HashMap();
		myOfferedPackage.put(Integer.valueOf(pie.getNumber()), personalValue2IssueValue(0.9D));
		Bid firstBid = null;
		try {
			firstBid = new Bid(domain, myOfferedPackage);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new Offer(getAgentID(), firstBid);
	}

	private Action chooseOffer2() {
		Domain domain = utilitySpace.getDomain();
		Issue pie = (Issue) domain.getIssues().get(0);
		HashMap<Integer, Value> myOfferedPackage = new HashMap();
		myOfferedPackage.put(Integer.valueOf(pie.getNumber()), personalValue2IssueValue(0.9D));
		Bid firstBid = null;
		try {
			firstBid = new Bid(domain, myOfferedPackage);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new Offer(getAgentID(), firstBid);
	}

	private Action chooseOffer3() {
		Domain domain = utilitySpace.getDomain();
		Issue pie = (Issue) domain.getIssues().get(0);
		HashMap<Integer, Value> myOfferedPackage = new HashMap();
		myOfferedPackage.put(Integer.valueOf(pie.getNumber()), personalValue2IssueValue(0.8D));
		Bid firstBid = null;
		try {
			firstBid = new Bid(domain, myOfferedPackage);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new Offer(getAgentID(), firstBid);
	}

	private Action chooseOffer4() {
		Domain domain = utilitySpace.getDomain();
		Issue pie = (Issue) domain.getIssues().get(0);
		HashMap<Integer, Value> myOfferedPackage = new HashMap();
		myOfferedPackage.put(Integer.valueOf(pie.getNumber()), personalValue2IssueValue(0.85D));
		Bid firstBid = null;
		try {
			firstBid = new Bid(domain, myOfferedPackage);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new Offer(getAgentID(), firstBid);
	}
}