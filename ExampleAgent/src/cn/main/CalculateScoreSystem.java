package cn.main;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import negotiator.Bid;
import negotiator.issue.Issue;
import negotiator.issue.Value;

public class CalculateScoreSystem {

	private Map<Issue, Map<Value, Double>> frequency;
	private Map<Issue, Map<Value, Double>> frequencyTen;
	private Map<Issue, Double> weight;
	private OppentNegotiationInfo oppentInfo;
	private Map<Issue, Value> maxScoreBid;
	private Double maxScore = 0d;
	
	public void updateMaxScoreBid(){
		for (Map.Entry<Issue, Map<Value, Double>> issue: frequency.entrySet()) {
			Value maxValue = null;
			Double maxFrequency = 0d;
			for (Map.Entry<Value, Double> value : issue.getValue().entrySet()) {
				if (value.getValue() >= maxFrequency){
					maxValue = value.getKey();
					maxFrequency = value.getValue();
				}
			}
			maxScoreBid.put(issue.getKey(), maxValue);
		}
	}
	
	public void updateMaxScore(){
		maxScore = getScoreByIssueValue(maxScoreBid);
//		MyPrint.printMaxScore(oppentInfo.getOppentID(), maxScoreBid, maxScore);
	}
	
	public void updateFrequency(HashMap<Issue, List<MyValueFrequency>> opponentFrequency, int round) {
		for (Map.Entry<Issue, List<MyValueFrequency>> frequencys: opponentFrequency.entrySet()) {
			Map<Value, Double> values = new HashMap<Value, Double>();
			for (MyValueFrequency myValueFrequency : frequencys.getValue()) {
				values.put(myValueFrequency.getValue(), Double.parseDouble(myValueFrequency.getFrequency() + ""));
			}
			frequency.put(frequencys.getKey(), values);
		}
		updateFrequencyTen(round);
	}
	
	public void updateFrequencyTen(int round){
		
		for (Map.Entry<Issue, Map<Value, Double>> issue : frequency.entrySet()) {
			Map<Value, Double> newValue = new HashMap<Value, Double>();
			for (Map.Entry<Value, Double> value : issue.getValue().entrySet()) {
				newValue.put(value.getKey(), value.getValue()*10/round);
			}
			frequencyTen.put(issue.getKey(), newValue);
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
//		MyPrint.printIssueWeight(oppentInfo.getOppentID(), weight);
		
	}
	
	public CalculateScoreSystem(OppentNegotiationInfo oppentInfo) {
		this.oppentInfo = oppentInfo;
		frequency = new HashMap<Issue, Map<Value, Double>>();
		frequencyTen = new HashMap<Issue, Map<Value, Double>>();
		weight = new HashMap<Issue, Double>();
		maxScoreBid = new HashMap<Issue, Value>();
	}

	public Bid getMaxScoreBid(Set<Bid> bids) {
		MyPrint.printScoreDetail(oppentInfo.getOppentID(), frequencyTen, weight);
		Bid maxBid = null;
		Double maxScore = 0d;
		for (Bid bid : bids) {
			Double scoreByBid = getScoreByBid(bid);
			if (scoreByBid >= maxScore){
				maxBid = bid;
				maxScore = scoreByBid;
			}
//			System.out.println(bid + " score : " + scoreByBid);
			
		}
		return maxBid;
	}

	public Double getScoreByBid(Bid bid) {
		if (bid == null) return 0d;
		Double score = 0d;
		List<Issue> issues = bid.getIssues();
		int N = issues.size();
		for (int i = 0; i < N; i++) {
			Double weight = getWeight().get(issues.get(i));
			Map<Value, Double> valueFens = getFrequencyTen().get(issues.get(i));
			for (Map.Entry<Value, Double> valueFen : valueFens.entrySet()) {
				if (valueFen.getKey().equals(bid.getValue(i + 1))){
					score += valueFen.getValue()*weight;
				}
			}
		}
		return score;
	}
	
	public Double getScoreByIssueValue(Map<Issue, Value> issueValue){
		if (issueValue == null) return 0d;
		Double score = 0d;
		for (Map.Entry<Issue, Map<Value, Double>> getIssue: frequencyTen.entrySet()) {
			Issue issue = getIssue.getKey();
			Double weight = getWeight().get(issue);
			for (Map.Entry<Value, Double> getValue : getIssue.getValue().entrySet()) {
				if (getValue.getKey().equals(issueValue.get(issue))){
					score += getValue.getValue()*weight;
				}
			}
		}
		return score;
	}

	// 计算utility
	public Double calculateUtility(Bid possibleBestBid) {
		if(possibleBestBid == null) return 0d;
		Double scoreByBid = getScoreByBid(possibleBestBid);
		Double utility = scoreByBid/maxScore;
		return utility;
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

	public Map<Issue, Map<Value, Double>> getFrequencyTen() {
		return frequencyTen;
	}

	public void setFrequencyTen(Map<Issue, Map<Value, Double>> frequencyTen) {
		this.frequencyTen = frequencyTen;
	}

	public Map<Issue, Value> getMaxScoreBid() {
		return maxScoreBid;
	}

	public void setMaxScoreBid(Map<Issue, Value> maxScoreBid) {
		this.maxScoreBid = maxScoreBid;
	}

	public Double getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(Double maxScore) {
		this.maxScore = maxScore;
	}




}
