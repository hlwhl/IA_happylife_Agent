package cn.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
	private HashMap<Object, HashMap<Issue, List<MyValueFrequency>>> opponentFrequency;
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
		opponentFrequency = new HashMap<Object, HashMap<Issue, List<MyValueFrequency>>>();
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
		HashMap<Issue, List<MyValueFrequency>> issueMap = new HashMap<Issue, List<MyValueFrequency>>();
		for (Issue issue : issues) {
			List<Value> values = getValues(issue);
			List<MyValueFrequency> frequencys = new ArrayList<MyValueFrequency>();
			for (Value value : values) {
				MyValueFrequency frequency = new MyValueFrequency();
				frequency.setValue(value);
				frequency.setFrequency(0);
				frequencys.add(frequency);
			}
			issueMap.put(issue, frequencys);
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
		for (Map.Entry<Object, HashMap<Issue, List<MyValueFrequency>>> oppoId : opponentFrequency.entrySet()) {
			System.out.println(((AgentID) oppoId.getKey()).getName() + " :");
			System.out.println();
			for (Map.Entry<Issue, List<MyValueFrequency>> issues : oppoId.getValue().entrySet()) {
				System.out.println(issues.getKey().getName());
				for (MyValueFrequency frequency : issues.getValue()) {
					System.out.println(frequency.toString());
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
		
		HashMap<Issue, List<MyValueFrequency>> issueMap = opponentFrequency.get(sender);
		List<Issue> oIssues = offeredBid.getIssues();
		for (Issue issue : oIssues) {
			List<MyValueFrequency> frequencys = issueMap.get(issue);
			Value value = offeredBid.getValue(issue.getNumber());
			for (MyValueFrequency frequency : frequencys) {
				if (frequency.getValue().equals(value)){
					frequency.setFrequency(frequency.getFrequency() + 1);
				}
			}
			sort(frequencys);
		}
	}
	
	private void sort(List<MyValueFrequency> frequencys) {
		Collections.sort(frequencys, new Comparator<MyValueFrequency>(){

			/*
			 * int compare(Student o1, Student o2) 返回一个基本类型的整型，
			 * 返回负数表示：o1 小于o2，
			 * 返回0 表示：o1和o2相等，
			 * 返回正数表示：o1大于o2。
			 */
			public int compare(MyValueFrequency o1, MyValueFrequency o2) {
			
				//降序排列
				if(o1.getFrequency() < o2.getFrequency()){
					return 1;
				}
				if(o1.getFrequency() == o2.getFrequency()){
					return 0;
				}
				return -1;
			}
		}); 
		
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


	public HashMap<Object, HashMap<Issue, List<MyValueFrequency>>> getOpponentFrequency() {
		return opponentFrequency;
	}


	public void setOpponentFrequency(HashMap<Object, HashMap<Issue, List<MyValueFrequency>>> opponentFrequency) {
		this.opponentFrequency = opponentFrequency;
	}


	public HashMap<Object, ArrayList<Bid>> getOpponentsBidHistory() {
		return opponentsBidHistory;
	}


	public void setOpponentsBidHistory(HashMap<Object, ArrayList<Bid>> opponentsBidHistory) {
		this.opponentsBidHistory = opponentsBidHistory;
	}


	public HashMap<Object, Double> getOpponentsAverage() {
		return opponentsAverage;
	}


	public void setOpponentsAverage(HashMap<Object, Double> opponentsAverage) {
		this.opponentsAverage = opponentsAverage;
	}


	public HashMap<Object, Double> getOpponentsVariance() {
		return opponentsVariance;
	}


	public void setOpponentsVariance(HashMap<Object, Double> opponentsVariance) {
		this.opponentsVariance = opponentsVariance;
	}


	public int getNegotiatorNum() {
		return negotiatorNum;
	}


	public void setNegotiatorNum(int negotiatorNum) {
		this.negotiatorNum = negotiatorNum;
	}





	
	
}










