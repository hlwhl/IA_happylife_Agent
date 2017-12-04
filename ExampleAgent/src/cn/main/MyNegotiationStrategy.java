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
	private Bid nashBid;

	public MyNegotiationStrategy(AbstractUtilitySpace utilitySpace) {
		this.utilitySpace = utilitySpace;
		try {
			lastBid = utilitySpace.getMaxUtilityBid();
			nashBid = new Bid(utilitySpace.getDomain());
		} catch (Exception e) {
			System.out.println("初始化策略对象得到最大值utility失败");
			e.printStackTrace();
		}
	}

	public boolean selectAccept(Bid lastReceivedOffer, double time) {
		double u = utilitySpace.getUtility(lastReceivedOffer);
		return u > currentThreshold;
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
//		System.out.println("生成" + bid);
		return bid;
	}

	public Bid normalChooseBid(double time, OppentNegotiationInfo oppent1Info, OppentNegotiationInfo oppent2Info,
			MyNegotiationInfo myInfo) {
//		updateCurrentThreshold(myInfo.getpValueList(),time);
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
//		// lastSuccessFindBidThreshold = currentThreshold;

//		if (maxScoreBid != null) {
//			lastBid = maxScoreBid;
//			return maxScoreBid;
//		}
//		return lastBid;
//		if (time < 0.7d) return lastBid;
		if(time<0.9) return lastBid;
		return nashBid;
	}

	public void updateCurrentThreshold(Map<Issue, List<Value>> pValueList, Double time) {
		HashMap<Integer, Value> tempBidSeed = new HashMap<Integer, Value>();
		Bid tempBid;
		Double tempUtility = 0d;
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
		}
		averageUtility=totalUtility/pValueList.size();
        currentThreshold =  (averageUtility-1d)*time+1d;
        System.out.println("avg: "+averageUtility+"current: "+currentThreshold);
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
			if (oppent2cal != null) scoreByBid *= oppent2cal.getScoreByBid(bid);
			if (scoreByBid >= maxScore) {
				maxBid = bid;
				maxScore = scoreByBid;
			}
			//			System.out.println(bid + " score : " + scoreByBid);
//			if (oppent1cal != null && oppent2cal != null) {
//				scoreByBid += oppent1cal.getScoreByBid(bid) * oppent2cal.getScoreByBid(bid);
//			}
//			if (scoreByBid >= maxScore) {
//				maxBid = bid;
//				maxScore = scoreByBid;
//			}
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


	public void updateNash(OppentNegotiationInfo oppent1Info, OppentNegotiationInfo oppent2Info) {
		if (oppent1Info == null && oppent2Info == null) return;
		Double currentThresholdMax = 1d;
		Double currentThresholdMin = 1d;
		Set<Bid> possibleBestBids = new HashSet<Bid>();
		if (oppent1Info != null){
			Set<Bid> possibleBids = new HashSet<Bid>();
//			Set<Bid> pBids = new HashSet<Bid>();
			while(currentThresholdMin > 0.02d){
				int num = 0;
				while (num < 15000) {
					Bid bid = generateRandomBid();
					Double utility = utilitySpace.getUtility(bid);
					if (utility >= currentThresholdMin && currentThresholdMax <= 1d) possibleBids.add(bid);
					num++;
				}
				if (possibleBids.size() < 5){
					currentThresholdMin -= 0.02d;
					continue;
				} else{
					currentThresholdMax -= 0.02d;
					currentThresholdMin -= 0.02d;
					Bid maxScoreBid = oppent1Info.getCalculateSystem().getMaxScoreBid(possibleBids);
					possibleBestBids.add(maxScoreBid);
					if (oppent2Info != null) {
						maxScoreBid = oppent2Info.getCalculateSystem().getMaxScoreBid(possibleBids);
						possibleBestBids.add(maxScoreBid);
					}
//					pBids.addAll(possibleBids);
				}
			}
			nashBid = calculateNash(possibleBestBids, oppent1Info, oppent2Info);
		}

	}

	private Bid calculateNash(Set<Bid> possibleBestBids, OppentNegotiationInfo oppent1Info,
			OppentNegotiationInfo oppent2Info) {
		if (possibleBestBids == null || possibleBestBids.size() == 0) return lastBid;
		if (oppent1Info == null && oppent2Info == null) return lastBid;
		Bid nash = lastBid;
		Double maxMultiUtility = 0d;
		if (oppent1Info != null){
			for (Bid possibleBestBid : possibleBestBids) {
				Double oppent1Utility = oppent1Info.getCalculateSystem().calculateUtility(possibleBestBid);
				Double myUtility = utilitySpace.getUtility(possibleBestBid);
				Double multiUtility = oppent1Utility * myUtility;
				if (oppent2Info != null){
					Double oppent2Utility = oppent2Info.getCalculateSystem().calculateUtility(possibleBestBid);
					multiUtility *= oppent2Utility;
				}
				if (multiUtility >= maxMultiUtility){
					nash = possibleBestBid;
					maxMultiUtility = multiUtility;
				}
			}
		}
		System.out.println("nash" + nash + "utility" + utilitySpace.getUtility(nash));
		return nash;
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

	public Double getLastSuccessFindBidThreshold() {
		return lastSuccessFindBidThreshold;
	}

	public void setLastSuccessFindBidThreshold(Double lastSuccessFindBidThreshold) {
		this.lastSuccessFindBidThreshold = lastSuccessFindBidThreshold;
	}

	public Double getCurrentThreshold() {
		return currentThreshold;
	}

	public void setCurrentThreshold(Double currentThreshold) {
		this.currentThreshold = currentThreshold;
	}

	public Bid getNashBid() {
		return nashBid;
	}

	public void setNashBid(Bid nashBid) {
		this.nashBid = nashBid;
	}
}
