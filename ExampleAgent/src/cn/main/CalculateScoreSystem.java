package cn.main;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import negotiator.Bid;
import negotiator.issue.Issue;
import negotiator.issue.Value;

public class CalculateScoreSystem {

	private Map<Issue, Map<Value, Double>> frequency;
	private Map<Issue, Double> weight;
	private OppentNegotiationInfo oppentInfo;
	
	public void updateFrequency(HashMap<Issue, List<MyValueFrequency>> opponentFrequency) {
		for (Map.Entry<Issue, List<MyValueFrequency>> frequencys: opponentFrequency.entrySet()) {
			Map<Value, Double> values = new HashMap<Value, Double>();
			for (MyValueFrequency myValueFrequency : frequencys.getValue()) {
				values.put(myValueFrequency.getValue(), Double.parseDouble(myValueFrequency.getFrequency() + ""));
			}
			frequency.put(frequencys.getKey(), values);
		}
	}
	
	public void updateWeight(LinkedHashMap<Issue, Double> opponentsIssueVariance) {
		Double totalWeight = 0d;
		for (Map.Entry<Issue, Double> issue : opponentsIssueVariance.entrySet()) {
			totalWeight += issue.getValue();
		}
		for (Map.Entry<Issue, Double> issue : opponentsIssueVariance.entrySet()) {
			weight.put(issue.getKey(), issue.getValue()/totalWeight);
		}
		MyPrint.printIssueWeight(oppentInfo.getOppentID(), weight);
		
	}
	
	public CalculateScoreSystem(OppentNegotiationInfo oppentInfo) {
		this.oppentInfo = oppentInfo;
		frequency = new HashMap<Issue, Map<Value, Double>>();
		weight = new HashMap<Issue, Double>();
	}

	public Bid getMaxScoreBid(List<Bid> bids) {
		return null;
	}

	public Double getScoreByBid(Bid bid) {
		if (bid == null) return 0d;
		return null;
	}

	public Map<Issue, Double> getWeight() {
		return weight;
	}

	public void setWeight(Map<Issue, Double> weight) {
		this.weight = weight;
	}

	public Map<Issue, Map<Value, Double>> getFrequency() {
		return frequency;
	}

	public void setFrequency(Map<Issue, Map<Value, Double>> frequency) {
		this.frequency = frequency;
	}

	public OppentNegotiationInfo getOppentInfo() {
		return oppentInfo;
	}

	public void setOppentInfo(OppentNegotiationInfo oppentInfo) {
		this.oppentInfo = oppentInfo;
	}

	

	

}
