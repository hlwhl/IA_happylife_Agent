package cn.main;

import java.math.BigDecimal;
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
	
	public void printScoreDetail(){
		System.out.println("对方配置文件详细得分");
		for (Map.Entry<Issue, Map<Value, Double>> frequency : frequencyTen.entrySet()) {
			System.out.println("issue: " + frequency.getKey());
			for (Map.Entry<Value, Double> value : frequency.getValue().entrySet()) {
				BigDecimal b = new BigDecimal(value.getValue());  
				double valueD = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				BigDecimal bb = new BigDecimal(weight.get(frequency.getKey()));  
				double valueDD = bb.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				BigDecimal bbb = new BigDecimal(valueD*valueDD);  
				double valueDDD = bbb.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				String detail = valueD + " x " + valueDD + " = " + valueDDD;
				System.out.println(value.getKey() + " " + detail + " ");
			}
			System.out.println("");
		}
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
		MyPrint.printIssueWeight(oppentInfo.getOppentID(), weight);
		
	}
	
	public CalculateScoreSystem(OppentNegotiationInfo oppentInfo) {
		this.oppentInfo = oppentInfo;
		frequency = new HashMap<Issue, Map<Value, Double>>();
		frequencyTen = new HashMap<Issue, Map<Value, Double>>();
		weight = new HashMap<Issue, Double>();
	}

	public Bid getMaxScoreBid(Set<Bid> bids) {
		printScoreDetail();
		
		Bid maxBid = null;
		Double maxScore = 0d;
		for (Bid bid : bids) {
			Double scoreByBid = getScoreByBid(bid);
			if (scoreByBid >= maxScore){
				maxBid = bid;
				maxScore = scoreByBid;
			}
			System.out.println(bid + " score : " + scoreByBid);
			
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


	

}
