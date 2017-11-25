package cn.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.Value;
import negotiator.utility.AbstractUtilitySpace;

public class MyNegotiationInfo {
	private AbstractUtilitySpace utilitySpace;
	private List<Object> opponents;
	private List<Issue> issues;
	private HashMap<Object, HashMap<Issue, List<Value>>> opponentPrefectOrder;
	private HashMap<Object, HashMap<Issue, HashMap<Value, Integer>>> opponentFrequency;
	private HashMap<Object, ArrayList<Bid>> opponentsBidHistory = null;
	private HashMap<Object, Double> opponentsAverage;
	private HashMap<Object, Double> opponentsVariance;
	private int round = 0;
	private int negotiatorNum = 0;
	
	public MyNegotiationInfo(AbstractUtilitySpace utilitySpace ){
		this.utilitySpace = utilitySpace;
		issues = utilitySpace.getDomain().getIssues();
		opponents = new ArrayList<Object>();
		opponentPrefectOrder = new HashMap<Object, HashMap<Issue, List<Value>>>();
		opponentFrequency = new HashMap<Object, HashMap<Issue, HashMap<Value, Integer>>>();
		opponentsBidHistory = new HashMap<Object, ArrayList<Bid>>();
		opponentsAverage = new HashMap<Object, Double>();
		opponentsVariance = new HashMap<Object, Double>();
	}


	public void updateOpponentsNum(int intValue) {
		this.negotiatorNum = intValue;
	}
	
	public void updateTimeScale(double time) {
		round++;
	}

	public void initOpponent(Object sender) {
		initNegotiatingInfo(sender);
		initOpponentsValueFrequency(sender);
		opponents.add(sender);
	}

	private void initOpponentsValueFrequency(Object sender) {
		HashMap<Issue, HashMap<Value, Integer>> issueMap = new HashMap<Issue, HashMap<Value, Integer>>();
		for (Issue issue : issues) {
			List<Value> values = getValues(issue);
			HashMap<Value, Integer> valueInteger = new HashMap<Value, Integer>();
			for (Value value : values) {
				valueInteger.put(value, 0);
			}
			issueMap.put(issue, valueInteger);
		}
		opponentFrequency.put(sender, issueMap);
	}


	private void initNegotiatingInfo(Object sender) {
		opponentsBidHistory.put(sender, new ArrayList<Bid>());
		opponentsAverage.put(sender, Double.valueOf(0.0D));
		opponentsVariance.put(sender, Double.valueOf(0.0D));
	}
	

	public void updateInfo(Object sender, Bid offeredBid) {
		try {
			updateNegotiatingInfo(sender, offeredBid);
		} catch (Exception e1) {
			System.out.println("更新谈判信息失败");
			e1.printStackTrace();
		}
		try {
			updateFrequencyList(sender, offeredBid);
		} catch (Exception e) {
			System.out.println("更新频率列表失败");
			e.printStackTrace();
		}
	}
	
	public void printInfo() {
		System.out.println("round: " + round);
		for (Map.Entry<Object, HashMap<Issue, HashMap<Value, Integer>>> oppoId : opponentFrequency.entrySet()) {
			System.out.println(((AgentID) oppoId.getKey()).getName());
			System.out.println();
			for (Map.Entry<Issue, HashMap<Value, Integer>> issue : oppoId.getValue().entrySet()) {
				System.out.println(issue.getKey().getName());
				for (Map.Entry<Value, Integer> value: issue.getValue().entrySet()) {
					System.out.println(value.getKey() + " : " + value.getValue());
				}
			}
		}
		System.out.println();
		
	}


	private void updateNegotiatingInfo(Object sender, Bid offeredBid) {
		ArrayList<Bid> bids = opponentsBidHistory.get(sender);
		bids.add(offeredBid);
		
	}
	
	private void updateFrequencyList(Object sender, Bid offeredBid) {
		HashMap<Issue, HashMap<Value, Integer>> issueMap = opponentFrequency.get(sender);
		List<Issue> oIssues = offeredBid.getIssues();
		for (Issue issue : oIssues) {
			HashMap<Value, Integer> values = issueMap.get(issue);
			Value value = offeredBid.getValue(issue.getNumber());
			values.put(value, values.get(value) + 1);
		}
	}
	
	public ArrayList<Value> getValues(Issue issue) {
		ArrayList<Value> values = new ArrayList<Value>();
//		switch (issue.getType()) {
//		case INTEGER:
//			List<negotiator.issue.ValueDiscrete> valuesDis = ((IssueDiscrete) issue).getValues();
//			for (Value value : valuesDis) {
//				values.add(value);
//			}
//			break;
//		case OBJECTIVE:
//			int min_value = ((IssueInteger) issue).getUpperBound();
//			int max_value = ((IssueInteger) issue).getUpperBound();
//			for (int j = min_value; j <= max_value; j++) {
//				Object valueObject = new Integer(j);
//				values.add((Value) valueObject);
//			}
//			break;
//		default:
//			try {
//				throw new Exception("issue type " + issue.getType() + " not supported by MyAgent");
//			} catch (Exception e) {
//				System.out.println("得到issue的value失败");
//				e.printStackTrace();
//			}
//		}
		List<negotiator.issue.ValueDiscrete> valuesDis = ((IssueDiscrete) issue).getValues();
		for (Value value : valuesDis) {
			values.add(value);
		}
		return values;
	}
	

	public AbstractUtilitySpace getUtilitySpace() {
		return utilitySpace;
	}

	public void setUtilitySpace(AbstractUtilitySpace utilitySpace) {
		this.utilitySpace = utilitySpace;
	}

	public List<Issue> getIssues() {
		return issues;
	}

	public void setIssues(List<Issue> issues) {
		this.issues = issues;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public List<Object> getOpponents() {
		return opponents;
	}

	public void setOpponents(List<Object> opponents) {
		this.opponents = opponents;
	}

	public HashMap<Object, HashMap<Issue, List<Value>>> getOpponentPrefectOrder() {
		return opponentPrefectOrder;
	}

	public void setOpponentPrefectOrder(HashMap<Object, HashMap<Issue, List<Value>>> opponentPrefectOrder) {
		this.opponentPrefectOrder = opponentPrefectOrder;
	}

	public HashMap<Object, HashMap<Issue, HashMap<Value, Integer>>> getOpponentFrequency() {
		return opponentFrequency;
	}

	public void setOpponentFrequency(HashMap<Object, HashMap<Issue, HashMap<Value, Integer>>> opponentFrequency) {
		this.opponentFrequency = opponentFrequency;
	}




	
	
}










