package cn.other;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import agents.anac.y2015.agentBuyogV2.LearningFunction;
import agents.anac.y2015.agentBuyogV2.OpponentInfo;
import flanagan.analysis.Regression;
import misc.Range;
import negotiator.AgentID;
import negotiator.Bid;
import negotiator.BidHistory;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.EndNegotiation;
import negotiator.actions.Offer;
import negotiator.analysis.BidSpace;
import negotiator.bidding.BidDetails;
import negotiator.boaframework.SortedOutcomeSpace;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.IssueInteger;
import negotiator.issue.Value;
import negotiator.issue.ValueDiscrete;
import negotiator.issue.ValueInteger;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.parties.NegotiationInfo;
import negotiator.utility.AdditiveUtilitySpace;
import negotiator.utility.EvaluatorDiscrete;
import negotiator.utility.EvaluatorInteger;

// Referenced classes of package agents.anac.y2015.agentBuyogV2:
//            OpponentInfo, LearningFunction

public class AgentBuyogMain extends AbstractNegotiationParty {

	public AgentBuyogMain() {
		initialized = false;
		numberOfRounds = 0;
	}

	public void init(NegotiationInfo info) {
		super.init(info);
		totalHistory = new BidHistory();
		myBidHistory = new BidHistory();
		AandBscommonBids = new BidHistory();
		sortedUtilitySpace = new SortedOutcomeSpace(utilitySpace);
	}

	public Action chooseAction(List possibleActions) {
		if (timeline.getTime() >= 1.0D) return new EndNegotiation(getPartyId());
		numberOfRounds++;
		double timePerRound = timeline.getTime() / (double) numberOfRounds;
		double remainingRounds = (1.0D - timeline.getTime()) / timePerRound;
		BidDetails bestBid = null;
		double minimumPoint = utilitySpace.getDiscountFactor() * 0.69999999999999996D
				+ utilitySpace.getReservationValueUndiscounted() * 0.29999999999999999D;
		BidDetails bestAgreeableBidSoFar = null;
		double bestAgreeableBidsUtility = 0.0D;
		double mostRecentBidsUtility = 0.0D;
		if (AandBscommonBids != null && AandBscommonBids.size() > 0) {
			bestAgreeableBidSoFar = AandBscommonBids.getBestBidDetails();
			bestAgreeableBidsUtility = AandBscommonBids.getBestBidDetails().getMyUndiscountedUtil();
		}
		if (totalHistory != null && totalHistory.size() > 0)
			mostRecentBidsUtility = totalHistory.getLastBidDetails().getMyUndiscountedUtil();
		OpponentInfo difficultAgent = null;
		if (infoA != null && infoB != null && infoA.getAgentDifficulty() != null
				&& infoB.getAgentDifficulty() != null) {
			if (infoA.getAgentDifficulty().doubleValue() <= infoB.getAgentDifficulty().doubleValue())
				difficultAgent = infoA;
			else difficultAgent = infoB;
			minimumPoint = utilitySpace.getDiscountFactor() * difficultAgent.getAgentDifficulty().doubleValue();
		}
		double acceptanceThreshold = minimumPoint + (1.0D - minimumPoint) * (1.0D - Math.pow(timeline.getTime(), 1.8D));
		if (remainingRounds <= 3D) acceptanceThreshold *= 0.5D;
		if (acceptanceThreshold < utilitySpace.getReservationValueUndiscounted()) {
			acceptanceThreshold = utilitySpace.getReservationValueUndiscounted();
			if (utilitySpace.getDiscountFactor() < 1.0D && remainingRounds > 3D)
				return new EndNegotiation(getPartyId());
		}
		if (possibleActions.contains(Accept.class) && mostRecentBidsUtility >= acceptanceThreshold
				&& mostRecentBidsUtility >= bestAgreeableBidsUtility && remainingRounds <= 3D)
			return new Accept(getPartyId(), totalHistory.getLastBidDetails().getBid());
		if (bestAgreeableBidsUtility > acceptanceThreshold) {
			bestBid = bestAgreeableBidSoFar;
		} else {
			Range range = new Range(acceptanceThreshold, 1.0D);
			List bidsInWindow = sortedUtilitySpace.getBidsinRange(range);
			bestBid = getBestBidFromList(bidsInWindow);
		}
		if (possibleActions.contains(Accept.class) && mostRecentBidsUtility >= acceptanceThreshold
				&& mostRecentBidsUtility >= bestAgreeableBidsUtility
				&& mostRecentBidsUtility >= bestBid.getMyUndiscountedUtil()) {
			return new Accept(getPartyId(), totalHistory.getLastBidDetails().getBid());
		} else {
			totalHistory.add(bestBid);
			return new Offer(getPartyId(), bestBid.getBid());
		}
	}

	private BidDetails getBestBidFromList(List bidsInWindow) {
		double bestBidOpponentsUtil = 0.0D;
		BidDetails bestBid = null;
		if (infoA == null || infoB == null || infoA.getAgentDifficulty() == null
				|| infoB.getAgentDifficulty() == null) {
			Random random = new Random();
			return (BidDetails) bidsInWindow.get(random.nextInt(bidsInWindow.size()));
		}
		try {
			bestBid = findNearestBidToKalai(bidsInWindow, Double.valueOf(1.0D), Double.valueOf(1.0D));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bestBid;
	}

	private BidDetails findNearestBidToKalai(List bidsInWindow, Double infoAKalai, Double infoBKalai) {
		BidDetails nearestBid = (BidDetails) bidsInWindow.get(0);
		Double shortestDistance = getDistance(nearestBid, infoAKalai, infoBKalai);
		for (Iterator iterator = bidsInWindow.iterator(); iterator.hasNext();) {
			BidDetails bid = (BidDetails) iterator.next();
			Double bidDistance = getDistance(bid, infoAKalai, infoBKalai);
			if (bidDistance.doubleValue() < shortestDistance.doubleValue()) {
				shortestDistance = bidDistance;
				nearestBid = bid;
			}
		}

		return nearestBid;
	}

	private Double getDistance(BidDetails bid, Double infoAKalai, Double infoBKalai) {
		try {
			return Double.valueOf(Math.sqrt((1.0D - infoA.getAgentDifficulty().doubleValue())
					* Math.pow(infoA.getOpponentUtilitySpace().getUtility(bid.getBid()) - infoAKalai.doubleValue(), 2D)
					+ (1.0D - infoB.getAgentDifficulty().doubleValue()) * Math.pow(
							infoB.getOpponentUtilitySpace().getUtility(bid.getBid()) - infoBKalai.doubleValue(), 2D)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Double.valueOf(0.0D);
	}

	public void receiveMessage(AgentID sender, Action action) {
		super.receiveMessage(sender, action);
		if (sender == null || !(action instanceof Offer) && !(action instanceof Accept)) return;
		Bid bid = null;
		if (!initialized) initializeOpponentInfo(sender);
		if (action instanceof Offer) {
			bid = ((Offer) action).getBid();
			try {
				totalHistory.add(new BidDetails(bid, utilitySpace.getUtility(bid), timeline.getTime()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (action instanceof Accept) bid = totalHistory.getLastBid();
		OpponentInfo senderInfo = getOpponentInfoObjectOfSender(sender);
		OpponentInfo otherInfo = getOpponentInfoObjectOfOther(sender);
		updateOpponentBidHistory(senderInfo, bid);
		updateCommonBids(otherInfo, bid);
		updateOpponentModel(senderInfo);
	}

	private void updateCommonBids(OpponentInfo otherInfo, Bid bid) {
		if (otherInfo == null) return;
		if (otherInfo.containsBid(bid)) try {
			AandBscommonBids.add(new BidDetails(bid, utilitySpace.getUtility(bid)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private OpponentInfo getOpponentInfoObjectOfOther(Object sender) {
		if (infoA != null && infoA.getAgentID().equals(sender.toString())) return infoB;
		if (infoB != null && infoB.getAgentID().equals(sender.toString())) return infoA;
		else return null;
	}

	private void updateOpponentModel(OpponentInfo senderInfo) {
		if (senderInfo == null) return;
		if ((double) senderInfo.getAgentBidHistory().size() >= 50D) {
			LearningFunction function = new LearningFunction(
					((BidDetails) senderInfo.getAgentBidHistory().getHistory().get(0)).getMyUndiscountedUtil());
			double xData[] = new double[senderInfo.getBestBids().size()];
			double yData[] = new double[senderInfo.getBestBids().size()];
			double yWeights[] = new double[senderInfo.getBestBids().size()];
			double step[] = { 0.0050000000000000001D, 0.0050000000000000001D };
			double initialEstimates[] = { 0.0D, 0.0D };
			for (int i = 0; i < senderInfo.getBestBids().size(); i++) {
				xData[i] = ((BidDetails) senderInfo.getBestBids().getHistory().get(i)).getTime();
				yData[i] = ((BidDetails) senderInfo.getBestBids().getHistory().get(i)).getMyUndiscountedUtil();
				yWeights[i] = ((Integer) senderInfo.getBidPointWeights().get(i)).intValue();
			}

			Regression regression = new Regression(xData, yData, yWeights);
			regression.simplex(function, initialEstimates, step);
			double bestEstimates[] = regression.getBestEstimates();
			double alpha = bestEstimates[0];
			double beta = bestEstimates[1];
			double slopeStandardScale = Math.pow(2.7182818284590451D, alpha) * beta
					* Math.pow(timeline.getTime(), beta - 1.0D);
			double slopeFromZeroToOne = Math.atan(slopeStandardScale) / 1.5707963267948966D;
			double adjustedLeniency = slopeFromZeroToOne + slopeFromZeroToOne / 1.0D;
			if (adjustedLeniency > 1.0D) adjustedLeniency = 1.0D;
			senderInfo.setLeniency(Double.valueOf(adjustedLeniency));
		} else {
			senderInfo.setLeniency(Double.valueOf(-1D));
		}
		AdditiveUtilitySpace opponentUtilitySpace = senderInfo.getOpponentUtilitySpace();
		if (senderInfo.getAgentBidHistory().size() < 2) return;
		int numberOfUnchanged = 0;
		int numberOfIssues = opponentUtilitySpace.getDomain().getIssues().size();
		BidHistory opponentsBidHistory = senderInfo.getAgentBidHistory();
		BidDetails opponentsLatestBid = opponentsBidHistory.getLastBidDetails();
		BidDetails opponentsSecondLastBid = (BidDetails) opponentsBidHistory.getHistory()
				.get(opponentsBidHistory.size() - 2);
		HashMap changed = determineDifference(senderInfo, opponentsSecondLastBid, opponentsLatestBid);
		for (Iterator iterator = changed.values().iterator(); iterator.hasNext();) {
			Boolean hasChanged = (Boolean) iterator.next();
			if (!hasChanged.booleanValue()) numberOfUnchanged++;
		}

		double goldenValue = (0.29999999999999999D
				* (1.0D - Math.pow(timeline.getTime(), 1.3D + utilitySpace.getDiscountFactor())))
				/ (double) numberOfIssues;
		double totalSum = 1.0D + goldenValue * (double) numberOfUnchanged;
		double maximumWeight = 1.0D - ((double) numberOfIssues * goldenValue) / totalSum;
		for (Iterator iterator1 = changed.keySet().iterator(); iterator1.hasNext();) {
			Integer issueNumber = (Integer) iterator1.next();
			if (!((Boolean) changed.get(issueNumber)).booleanValue()
					&& opponentUtilitySpace.getWeight(issueNumber.intValue()) < maximumWeight)
				opponentUtilitySpace.setWeight(
						opponentUtilitySpace.getDomain().getObjectivesRoot().getObjective(issueNumber.intValue()),
						(opponentUtilitySpace.getWeight(issueNumber.intValue()) + goldenValue) / totalSum);
			else opponentUtilitySpace.setWeight(
					opponentUtilitySpace.getDomain().getObjectivesRoot().getObjective(issueNumber.intValue()),
					opponentUtilitySpace.getWeight(issueNumber.intValue()) / totalSum);
		}

		try {
			for (Iterator iterator2 = opponentUtilitySpace.getEvaluators().iterator(); iterator2.hasNext();) {
				java.util.Map.Entry issueEvaluatorEntry = (java.util.Map.Entry) iterator2.next();
				if (issueEvaluatorEntry.getKey() instanceof IssueDiscrete)
					((EvaluatorDiscrete) issueEvaluatorEntry.getValue())
							.setEvaluation(
									opponentsLatestBid.getBid().getValue(
											((IssueDiscrete) issueEvaluatorEntry.getKey()).getNumber()),
									(int) (100D
											* (1.0D - Math.pow(timeline.getTime(),
													1.3D + utilitySpace.getDiscountFactor()))
											+ (double) ((EvaluatorDiscrete) issueEvaluatorEntry.getValue())
													.getEvaluationNotNormalized(
															(ValueDiscrete) opponentsLatestBid.getBid().getValue(
																	((IssueDiscrete) issueEvaluatorEntry.getKey())
																			.getNumber()))
													.intValue()));
				else if (issueEvaluatorEntry.getKey() instanceof IssueInteger) {
					int issueNumber = ((IssueInteger) issueEvaluatorEntry.getKey()).getNumber();
					Value opponentsLatestValueForIssue = opponentsLatestBid.getBid().getValue(issueNumber);
					int opponentsLatestValueForIssueAsInteger = ((ValueInteger) opponentsLatestValueForIssue)
							.getValue();
					int upperBound = ((IssueInteger) issueEvaluatorEntry.getKey()).getUpperBound();
					int lowerBound = ((IssueInteger) issueEvaluatorEntry.getKey()).getLowerBound();
					double midPoint = Math.ceil(lowerBound + (upperBound - lowerBound) / 2) + 1.0D;
					if (midPoint > (double) opponentsLatestValueForIssueAsInteger) {
						double distanceFromMidPoint = midPoint - (double) opponentsLatestValueForIssueAsInteger;
						double normalizedDistanceFromMidPoint = distanceFromMidPoint / (midPoint - (double) lowerBound);
						double total = 1.0D;
						double newLowEndEvaluation = ((EvaluatorInteger) issueEvaluatorEntry.getValue())
								.getEvaluation(lowerBound).doubleValue()
								+ 0.01D * normalizedDistanceFromMidPoint * (1.0D
										- Math.pow(timeline.getTime(), 1.3D + utilitySpace.getDiscountFactor()));
						double highEndEvaluation = ((EvaluatorInteger) issueEvaluatorEntry.getValue())
								.getEvaluation(upperBound).doubleValue();
						if (newLowEndEvaluation > 1.0D) total = newLowEndEvaluation + highEndEvaluation;
						((EvaluatorInteger) issueEvaluatorEntry.getValue())
								.setLinearFunction(newLowEndEvaluation / total, highEndEvaluation / total);
					} else {
						double distanceFromMidPoint = ((double) opponentsLatestValueForIssueAsInteger - midPoint)
								+ 1.0D;
						double normalizedDistanceFromMidPoint = distanceFromMidPoint
								/ (((double) upperBound - midPoint) + 1.0D);
						double total = 1.0D;
						double newHighEndEvaluation = ((EvaluatorInteger) issueEvaluatorEntry.getValue())
								.getEvaluation(upperBound).doubleValue()
								+ 0.01D * normalizedDistanceFromMidPoint * (1.0D
										- Math.pow(timeline.getTime(), 1.3D + utilitySpace.getDiscountFactor()));
						double lowEndEvaluation = ((EvaluatorInteger) issueEvaluatorEntry.getValue())
								.getEvaluation(lowerBound).doubleValue();
						if (newHighEndEvaluation > 1.0D) total = newHighEndEvaluation + lowEndEvaluation;
						((EvaluatorInteger) issueEvaluatorEntry.getValue()).setLinearFunction(lowEndEvaluation / total,
								newHighEndEvaluation / total);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			BidSpace bidSpace = new BidSpace(utilitySpace, opponentUtilitySpace, true);
			double kalaiPoint = bidSpace.getKalaiSmorodinsky().getUtilityA().doubleValue();
			if (kalaiPoint <= 0.40000000000000002D) kalaiPoint += 0.10000000000000001D;
			else if (kalaiPoint <= 0.69999999999999996D) kalaiPoint += 0.050000000000000003D;
			if (kalaiPoint > senderInfo.getBestBids().getBestBidDetails().getMyUndiscountedUtil())
				senderInfo.setDomainCompetitiveness(Double.valueOf(kalaiPoint));
			else senderInfo.setDomainCompetitiveness(
					Double.valueOf(senderInfo.getBestBids().getBestBidDetails().getMyUndiscountedUtil()));
			if ((double) senderInfo.getAgentBidHistory().size() >= 50D) {
				double domainWeight = 1.0D - Math.pow(senderInfo.getLeniency().doubleValue(), 1.75D);
				double agentDifficulty = (1.0D - domainWeight) * senderInfo.getLeniency().doubleValue()
						+ domainWeight * senderInfo.getDomainCompetitiveness().doubleValue();
				senderInfo.setAgentDifficulty(Double.valueOf(agentDifficulty));
			} else {
				double agentDifficulty = senderInfo.getDomainCompetitiveness().doubleValue();
				senderInfo.setAgentDifficulty(Double.valueOf(agentDifficulty));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private HashMap determineDifference(OpponentInfo senderInfo, BidDetails first, BidDetails second) {
		HashMap changed = new HashMap();
		try {
			for (Iterator iterator = senderInfo.getOpponentUtilitySpace().getDomain().getIssues().iterator(); iterator
					.hasNext();) {
				Issue i = (Issue) iterator.next();
				if (i instanceof IssueDiscrete) changed.put(Integer.valueOf(i.getNumber()),
						Boolean.valueOf(!((ValueDiscrete) first.getBid().getValue(i.getNumber()))
								.equals((ValueDiscrete) second.getBid().getValue(i.getNumber()))));
				else if (i instanceof IssueInteger) changed.put(Integer.valueOf(i.getNumber()),
						Boolean.valueOf(!((ValueInteger) first.getBid().getValue(i.getNumber()))
								.equals((ValueInteger) second.getBid().getValue(i.getNumber()))));
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return changed;
	}

	private OpponentInfo getOpponentInfoObjectOfSender(Object sender) {
		if (infoA != null && infoA.getAgentID().equals(sender.toString())) return infoA;
		if (infoB != null && infoB.getAgentID().equals(sender.toString())) return infoB;
		else return null;
	}

	private void initializeOpponentInfo(Object sender) {
		if (infoA == null) infoA = new OpponentInfo(sender.toString(), (AdditiveUtilitySpace) utilitySpace);
		else if (infoB == null) infoB = new OpponentInfo(sender.toString(), (AdditiveUtilitySpace) utilitySpace);
		if (infoA != null && infoB != null) initialized = true;
	}

	private void updateOpponentBidHistory(OpponentInfo opponent, Bid bid) {
		if (opponent == null || bid == null) return;
		try {
			opponent.getAgentBidHistory().add(new BidDetails(bid, utilitySpace.getUtility(bid), timeline.getTime()));
			for (Iterator iterator = opponent.getBidPointWeights().iterator(); iterator.hasNext();) {
				Integer i = (Integer) iterator.next();
				if (i.intValue() > 1) i = Integer.valueOf(i.intValue() - 1);
			}

			opponent.getBidPointWeights().add(Integer.valueOf(300));
			if (opponent.getBestBid() == null
					|| utilitySpace.getUtility(bid) >= utilitySpace.getUtility(opponent.getBestBid())) {
				opponent.setBestBid(bid);
				opponent.getBestBids().add(new BidDetails(bid, utilitySpace.getUtility(bid), timeline.getTime()));
			} else {
				opponent.getBestBids().add(new BidDetails(opponent.getBestBid(),
						utilitySpace.getUtility(opponent.getBestBid()), timeline.getTime()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getDescription() {
		return "ANAC2015-13-Buyog";
	}

	private static final double alphaDefault = 0D;
	private static final double betaDefault = 0D;
	private static final double issueWeightsConstant = 0.29999999999999999D;
	private static final double issueValuesConstant = 100D;
	private static final double minimumHistorySize = 50D;
	private static final double learningTimeController = 1.3D;
	private static final int maxWeightForBidPoint = 300;
	private static final double leniencyAdjuster = 1D;
	private static final double domainWeightController = 1.75D;
	private static final double timeConcessionController = 1.8D;
	private static final double lastSecondConcessionFactor = 0.5D;
	private static final double kalaiPointCorrection = 0.10000000000000001D;
	private OpponentInfo infoA;
	private OpponentInfo infoB;
	private BidHistory myBidHistory;
	private BidHistory AandBscommonBids;
	private BidHistory totalHistory;
	private boolean initialized;
	private SortedOutcomeSpace sortedUtilitySpace;
	private int numberOfRounds;
}