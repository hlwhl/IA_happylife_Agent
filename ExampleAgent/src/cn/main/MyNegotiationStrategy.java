package cn.main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import negotiator.Bid;
import negotiator.actions.Action;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.Value;
import negotiator.issue.ValueDiscrete;
import negotiator.utility.AbstractUtilitySpace;

public class MyNegotiationStrategy {
	private AbstractUtilitySpace utilitySpace;
	private Bid lastBid;
	private Double lastSuccessFindBidTime = 0.15;
	private Double lastSuccessFindBidThreshold = 1d;
	private Double currentThreshold = 1d;

	public MyNegotiationStrategy(AbstractUtilitySpace utilitySpace) {
		this.utilitySpace = utilitySpace;
		try {
			lastBid = utilitySpace.getMaxUtilityBid();
		} catch (Exception e) {
			System.out.println("初始化策略对象得到最大值utility失败");
			e.printStackTrace();
		}

	}

	public boolean selectAccept(Bid lastReceivedOffer, double time) {
		double u = utilitySpace.getUtility(lastReceivedOffer);
//		double utility = 0;
//		if (time <= 0.25D) {
//			utility = 1.0D - time * 0.4D;
//		}
//		if ((time > 0.25D) && (time <= 0.375D)) {
//			utility = 0.9D + (time - 0.25D) * 0.4D;
//		}
//		if ((time > 0.375D) && (time <= 0.5D)) {
//			utility = 0.95D - (time - 0.375D) * 0.4D;
//		}
//		if ((time > 0.5D) && (time <= 0.6D)) {
//			utility = 0.9D - (time - 0.5D);
//		}
//		if ((time > 0.6D) && (time <= 0.7D)) {
//			utility = 0.8D + (time - 0.6D) * 2.0D;
//		}
//		if ((time > 0.7D) && (time <= 0.8D)) {
//			utility = 1.0D - (time - 0.7D) * 3.0D;
//		}
//		if ((time > 0.8D) && (time <= 0.9D)) {
//			utility = 0.7D + (time - 0.8D) * 1.0D;
//		}
//		if ((time > 0.9D) && (time <= 0.95D)) {
//			utility = 0.8D - (time - 0.9D) * 6.0D;
//		}
//		if (time > 0.95D) {
//			utility = 0.5D + (time - 0.95D) * 4.0D;
//		}
//		if (time > 1) {
//			utility = 0.8;
//		}
		return u > currentThreshold;
	}

	public boolean selectEndNegotiation(double time) {

		return false;
	}

	public Action OfferAction() {

		return null;
	}

	public Bid getRandomFromPValueList(Map<Issue, List<Value>> pvl) {
		// 从myInfo.pValueList随机组合bid
		Bid bid = null;
		Random r = new Random();
		HashMap<Integer, Value> bidP = new HashMap<Integer, Value>();

		for (Map.Entry<Issue, List<Value>> issueValues : pvl.entrySet()) {
			bidP.put(issueValues.getKey().getNumber(),
					new ValueDiscrete(issueValues.getValue().get(r.nextInt(issueValues.getValue().size())).toString()));
			bid = new Bid(utilitySpace.getDomain(), bidP);
		}
		System.out.println("生成" + bid);
		return bid;
	}

	public Bid normalChooseBid(double time, OppentNegotiationInfo oppent1Info, OppentNegotiationInfo oppent2Info,
			MyNegotiationInfo myInfo) {
		//		Double currentThreshold = time * -(Double.parseDouble(6 + "") / Double.parseDouble(17 + ""))
		//				+ Double.parseDouble(179 + "") / Double.parseDouble(170 + "");
//		Double currentThreshold = getCurrentThreshold(myInfo.getpValueList(), time);
		updateCurrentThreshold(myInfo.getpValueList(),time);
		Set<Bid> possibleBids = new HashSet<Bid>();
		int num = 0;
		while (num < 15000) {
			Bid bid = generateRandomBid();
			Double utility = utilitySpace.getUtility(bid);
			if (utility >= currentThreshold && utility <= 1d) possibleBids.add(bid);
			num++;
		}
		System.out.println("------------------currentThreshold-------------------" + currentThreshold);
		System.out.println("------------------possibleBidsize-------------------" + possibleBids.size());
		System.out.println(
				"------------------lastSuccessFindBidThreshold-------------------" + lastSuccessFindBidThreshold);
		if (possibleBids.size() < 5) return lastBid;
		Bid maxScoreBid = getMaxScoreBid(possibleBids, oppent1Info, oppent2Info);
		// lastSuccessFindBidThreshold = currentThreshold;
		if (maxScoreBid != null) {
			lastBid = maxScoreBid;
			return maxScoreBid;
		}
		return lastBid;
	}

	public void updateCurrentThreshold(Map<Issue, List<Value>> pValueList, Double time) {
		HashMap<Integer, Value> tempBidSeed = new HashMap<Integer, Value>();
		Bid tempBid;
		Double maxUtility = 0d;
		Double minUtility = 0d;
		Double tempUtility = 0d;
		Double targetUtility;
		Double totalUtility=0d;
		Double averageUtility=0d;
		for (int i = 0; i < pValueList.size(); i++) {
			for (Map.Entry<Issue, List<Value>> entry : pValueList.entrySet()) {
				for (int j = 0; j < entry.getValue().size(); j++) {
					tempBidSeed.put(entry.getKey().getNumber(), entry.getValue().get(j));
				}
			}
			tempBid = new Bid(utilitySpace.getDomain(), tempBidSeed);
			tempUtility = utilitySpace.getUtility(tempBid);
			totalUtility+=tempUtility;
			if (tempUtility > maxUtility) {
				maxUtility = tempUtility;
			}
			if (tempUtility < minUtility) {
				minUtility = tempUtility;
			}
		}
		averageUtility=totalUtility/pValueList.size();
		targetUtility = (maxUtility - minUtility) * 0.75 + minUtility;
		if ((targetUtility - 1d) * time + 1 > averageUtility) {
			currentThreshold = (targetUtility - 1d) * time + 1 ;   //y=at+b
		}
		currentThreshold =  averageUtility;
	}


	private Bid getMaxScoreBid(Set<Bid> possibleBids, OppentNegotiationInfo oppent1Info,
			OppentNegotiationInfo oppent2Info) {
		if (oppent1Info == null && oppent2Info == null) return lastBid;
		CalculateScoreSystem oppent1cal = null;
		CalculateScoreSystem oppent2cal = null;
		if (oppent1Info != null) oppent1cal = oppent1Info.getCalculateSystem();
		if (oppent2Info != null) oppent2cal = oppent2Info.getCalculateSystem();
		if (oppent1cal != null)
			MyPrint.printScoreDetail(oppent1Info.getOppentID(), oppent1cal.getFrequencyTen(), oppent1cal.getWeight());
		if (oppent2cal != null)
			MyPrint.printScoreDetail(oppent2Info.getOppentID(), oppent2cal.getFrequencyTen(), oppent2cal.getWeight());

		Bid maxBid = null;
		Double maxScore = 0d;
		for (Bid bid : possibleBids) {
			Double scoreByBid = 0d;
			if (oppent1cal != null) scoreByBid += oppent1cal.getScoreByBid(bid);
			if (oppent2cal != null) scoreByBid += oppent2cal.getScoreByBid(bid);
			if (scoreByBid >= maxScore) {
				maxBid = bid;
				maxScore = scoreByBid;
			}
			//			System.out.println(bid + " score : " + scoreByBid);

		}
		return maxBid;
	}

	protected Bid generateRandomBid() {
		try {
			HashMap<Integer, Value> values = new HashMap<Integer, Value>();
			Iterator<Issue> var3 = this.utilitySpace.getDomain().getIssues().iterator();

			while (var3.hasNext()) {
				Issue currentIssue = (Issue) var3.next();
				values.put(currentIssue.getNumber(), this.getRandomValue(currentIssue));
			}

			return new Bid(this.utilitySpace.getDomain(), values);
		} catch (Exception var4) {
			return new Bid(this.utilitySpace.getDomain());
		}
	}

	protected Value getRandomValue(Issue currentIssue) throws Exception {
		Object currentValue;
		int index;
		Random rand = new Random();
		IssueDiscrete discreteIssue = (IssueDiscrete) currentIssue;
		index = rand.nextInt(discreteIssue.getNumberOfValues());
		currentValue = discreteIssue.getValue(index);
		return (Value) currentValue;
	}

	public AbstractUtilitySpace getUtilitySpace() {
		return utilitySpace;
	}

	public void setUtilitySpace(AbstractUtilitySpace utilitySpace) {
		this.utilitySpace = utilitySpace;
	}

	public Bid getLastBid() {
		return lastBid;
	}

	public void setLastBid(Bid lastBid) {
		this.lastBid = lastBid;
	}

	public Double getLastSuccessFindBidTime() {
		return lastSuccessFindBidTime;
	}

	public void setLastSuccessFindBidTime(Double lastSuccessFindBidTime) {
		this.lastSuccessFindBidTime = lastSuccessFindBidTime;
	}

}
