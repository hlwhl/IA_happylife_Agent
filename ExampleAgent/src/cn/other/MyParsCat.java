package cn.other;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import agents.anac.y2016.parscat.ParsCat;
import negotiator.AgentID;
import negotiator.Bid;
import negotiator.BidHistory;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.EndNegotiationWithAnOffer;
import negotiator.actions.Offer;
import negotiator.bidding.BidDetails;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.IssueInteger;
import negotiator.issue.Value;
import negotiator.issue.ValueInteger;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.parties.NegotiationInfo;
import negotiator.timeline.TimeLineInfo;
import negotiator.utility.AbstractUtilitySpace;

public class MyParsCat extends AbstractNegotiationParty {
	private TimeLineInfo TimeLineInfo = null;
	private Bid maxBid = null;
	private AbstractUtilitySpace utilSpace = null;
	private BidHistory OtherAgentsBidHistory;
	private double tresh;
	private double t1 = 0.0D;
	private double u2 = 1.0D;

	public static void main(String[] args) {
	}

	public void ParsCat() {
		this.OtherAgentsBidHistory = new BidHistory();
	}

	public void init(NegotiationInfo info) {
		super.init(info);

		this.utilSpace = info.getUtilitySpace();
		this.TimeLineInfo = info.getTimeline();
		try {
			this.maxBid = this.utilSpace.getMaxUtilityBid();
		} catch (Exception ex) {
			Logger.getLogger(ParsCat.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public Action chooseAction(List<Class<? extends Action>> validActions) {
		try {
			if (this.OtherAgentsBidHistory.isEmpty()) { return new Offer(getPartyId(), this.maxBid); }
			Action action = new Offer(getPartyId(), getRandomBid());
			Bid myBid = ((Offer) action).getBid();
			double myOfferedUtil = getUtility(myBid);
			double time = this.TimeLineInfo.getTime();
			if (this.OtherAgentsBidHistory.getLastBid() == myBid) { return new Accept(getPartyId(),
					this.OtherAgentsBidHistory.getLastBid()); }
			Bid OtherAgentBid = this.OtherAgentsBidHistory.getLastBid();
			double offeredUtilFromOpponent = getUtility(OtherAgentBid);
			if (isAcceptable(offeredUtilFromOpponent, myOfferedUtil,
					time)) { return new Accept(getPartyId(), OtherAgentBid); }
			return action;
		} catch (Exception e) {
		}
		return new Offer(getPartyId(), this.maxBid);
	}

	private boolean isAcceptable(double offeredUtilFromOtherAgent, double myOfferedUtil, double time) throws Exception {
		if (offeredUtilFromOtherAgent == myOfferedUtil) { return true; }
		double t = time;
		double Util = 1.0D;
		if (time <= 0.25D) {
			Util = 1.0D - t * 0.4D;
		}
		if ((time > 0.25D) && (time <= 0.375D)) {
			Util = 0.9D + (t - 0.25D) * 0.4D;
		}
		if ((time > 0.375D) && (time <= 0.5D)) {
			Util = 0.95D - (t - 0.375D) * 0.4D;
		}
		if ((time > 0.5D) && (time <= 0.6D)) {
			Util = 0.9D - (t - 0.5D);
		}
		if ((time > 0.6D) && (time <= 0.7D)) {
			Util = 0.8D + (t - 0.6D) * 2.0D;
		}
		if ((time > 0.7D) && (time <= 0.8D)) {
			Util = 1.0D - (t - 0.7D) * 3.0D;
		}
		if ((time > 0.8D) && (time <= 0.9D)) {
			Util = 0.7D + (t - 0.8D) * 1.0D;
		}
		if ((time > 0.9D) && (time <= 0.95D)) {
			Util = 0.8D - (t - 0.9D) * 6.0D;
		}
		if (time > 0.95D) {
			Util = 0.5D + (t - 0.95D) * 4.0D;
		}
		if (Util > 1.0D) {
			Util = 0.8D;
		}
		return offeredUtilFromOtherAgent >= Util;
	}

	private Bid getRandomBid() throws Exception {
		HashMap<Integer, Value> values = new HashMap();
		List<Issue> issues = this.utilSpace.getDomain().getIssues();
		Random randomnr = new Random();
		Bid bid = null;
		double xxx = 0.001D;
		long counter = 1000L;
		double check = 0.0D;
		while (counter == 1000L) {
			counter = 0L;
			do {
				for (Issue lIssue : issues) {
					switch (lIssue.getType()) {
					case INTEGER:
						IssueDiscrete lIssueDiscrete = (IssueDiscrete) lIssue;
						int optionIndex = randomnr.nextInt(lIssueDiscrete.getNumberOfValues());
						values.put(Integer.valueOf(lIssue.getNumber()), lIssueDiscrete.getValue(optionIndex));
						break;
					case OBJECTIVE:
						IssueInteger lIssueInteger = (IssueInteger) lIssue;
						int optionIndex2 = lIssueInteger.getLowerBound()
								+ randomnr.nextInt(lIssueInteger.getUpperBound() - lIssueInteger.getLowerBound());
						values.put(Integer.valueOf(lIssueInteger.getNumber()), new ValueInteger(optionIndex2));
						break;
					default:
						throw new Exception("issue type " + lIssue.getType() + " not supported by SamantaAgent2");
					}
				}
				bid = new Bid(this.utilitySpace.getDomain(), values);
				if (this.t1 < 0.5D) {
					this.tresh = (1.0D - this.t1 / 4.0D);
					xxx = 0.01D;
				}
				if ((this.t1 >= 0.5D) && (this.t1 < 0.8D)) {
					this.tresh = (0.9D - this.t1 / 5.0D);
					xxx = 0.02D;
				}
				if ((this.t1 >= 0.8D) && (this.t1 < 0.9D)) {
					this.tresh = (0.7D + this.t1 / 5.0D);
					xxx = 0.02D;
				}
				if ((this.t1 >= 0.9D) && (this.t1 < 0.95D)) {
					this.tresh = (0.8D + this.t1 / 5.0D);
					xxx = 0.02D;
				}
				if (this.t1 >= 0.95D) {
					this.tresh = (1.0D - this.t1 / 4.0D - 0.01D);
					xxx = 0.02D;
				}
				if (this.t1 == 1.0D) {
					this.tresh = 0.5D;
					xxx = 0.05D;
				}
				this.tresh -= check;
				if (this.tresh > 1.0D) {
					this.tresh = 1.0D;
					xxx = 0.01D;
				}
				if (this.tresh <= 0.5D) {
					this.tresh = 0.49D;
					xxx = 0.01D;
				}
				counter += 1L;
			} while (((getUtility(bid) < this.tresh - xxx) || (getUtility(bid) > this.tresh + xxx))
					&& (counter < 1000L));
			check += 0.01D;
		}
		if ((getUtility(bid) < getUtility(this.OtherAgentsBidHistory.getBestBidDetails().getBid()))
				&& (getNumberOfParties() == 2)) { return this.OtherAgentsBidHistory.getBestBidDetails().getBid(); }
		return bid;
	}

	public void receiveMessage(AgentID sender, Action action) {
		super.receiveMessage(sender, action);
		if ((action instanceof Offer)) {
			Bid bid = ((Offer) action).getBid();
			try {
				BidDetails opponentBid = new BidDetails(bid, this.utilSpace.getUtility(bid),
						this.TimeLineInfo.getTime());
				this.u2 = this.utilSpace.getUtility(bid);
				this.t1 = this.TimeLineInfo.getTime();
				this.OtherAgentsBidHistory.add(opponentBid);
			} catch (Exception e) {
				EndNegotiationWithAnOffer localEndNegotiationWithAnOffer = new EndNegotiationWithAnOffer(getPartyId(),
						this.maxBid);
			}
		}
	}

	public String getDescription() {
		return "ANAC2016 ParsCat";
	}
}
