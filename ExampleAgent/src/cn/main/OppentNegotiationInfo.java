package cn.main;

import java.util.*;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.Value;
import negotiator.issue.ValueDiscrete;
import negotiator.utility.AbstractUtilitySpace;

public class OppentNegotiationInfo {
	private AgentID OppentID;
	private AbstractUtilitySpace utilitySpace;
	private List<Issue> issues;
	private HashMap<Issue, List<MyValueFrequency>> opponentFrequency;
	private ArrayList<Bid> opponentsBidHistory = null;
	private LinkedHashMap<Issue, Double> opponentsIssueVariance;
	private Double opponentsAverage;
	private Double opponentsVariance;
	private CalculateScoreSystem calculateSystem;
	private List<MyValueFrequency> frequencys;

	public OppentNegotiationInfo(AbstractUtilitySpace utilitySpace, AgentID id) {
		this.utilitySpace = utilitySpace;
		issues = utilitySpace.getDomain().getIssues();
		opponentFrequency = new HashMap<Issue, List<MyValueFrequency>>();
		opponentsBidHistory = new ArrayList<Bid>();
		opponentsIssueVariance = new LinkedHashMap<Issue, Double>();
		opponentsAverage = 0.0D;
		opponentsVariance = 0.0D;
		setOppentID(id);
		initOpponentValueFrequency();
		calculateSystem = new CalculateScoreSystem(this);
	}

	public void optionOppentInfo(Bid b, int round) {
		opponentsBidHistory.add(b);
		updateFrequencyList(b);
		caluOpponentsIssueVariance();
		calculateSystem.updateFrequency(opponentFrequency, round);
		calculateSystem.updateWeight(opponentsIssueVariance);
	}

	private void initOpponentValueFrequency() {
		for (Issue issue : issues) {
			List<Value> values = getValues(issue);
			List<MyValueFrequency> frequencys = new ArrayList<MyValueFrequency>();
			for (Value value : values) {
				MyValueFrequency frequency = new MyValueFrequency();
				frequency.setValue(value);
				frequency.setFrequency(0);
				frequencys.add(frequency);
			}
			this.opponentFrequency.put(issue, frequencys);
		}
	}


	public Bid getMaxFrequencyBid() {
		HashMap<Integer, Value> bidP = new HashMap<Integer, Value>();
		for (Map.Entry<Issue, List<MyValueFrequency>> oppoInfo : opponentFrequency.entrySet()) {
			// 输出信息
			System.out.println(
					"频次最高" + oppoInfo.getValue().get(0).getValue().toString() + "为" + oppoInfo.getValue().get(0)
							.getFrequency());
			// bid生成
			bidP.put(oppoInfo.getKey().getNumber(),
					new ValueDiscrete(oppoInfo.getValue().get(0).getValue().toString()));
		}
		return new Bid(utilitySpace.getDomain(), bidP);
	}

	private void updateFrequencyList(Bid offeredBid) {
		List<Issue> oIssues = offeredBid.getIssues();
		for (Issue issue : oIssues) {
			frequencys = opponentFrequency.get(issue);
			Value value = offeredBid.getValue(issue.getNumber());
			for (MyValueFrequency frequency : frequencys) {
				if (frequency.getValue().equals(value)) {
					frequency.setFrequency(frequency.getFrequency() + 1);
				}
			}
			sort(frequencys);
		}
	}

	private void sort(List<MyValueFrequency> frequencys) {
		Collections.sort(frequencys, new Comparator<MyValueFrequency>() {

			/* int compare(Student o1, Student o2) 返回一个基本类型的整型， 返回负数表示：o1 小于o2， 返回0 表示：o1和o2相等， 返回正数表示：o1大于o2。 */
			public int compare(MyValueFrequency o1, MyValueFrequency o2) {

				// 降序排列
				if (o1.getFrequency() < o2.getFrequency()) {
					return 1;
				}
				if (o1.getFrequency() == o2.getFrequency()) {
					return 0;
				}
				return -1;
			}
		});

	}

	public ArrayList<Value> getValues(Issue issue) {
		ArrayList<Value> values = new ArrayList<Value>();
		List<negotiator.issue.ValueDiscrete> valuesDis = ((IssueDiscrete) issue).getValues();
		for (Value value : valuesDis) {
			values.add(value);
		}
		return values;
	}

	//计算对手Issue间的方差
	public void caluOpponentsIssueVariance() {
		int totalDiffentTimes = 0;
		for (int i = 0; i < utilitySpace.getDomain().getIssues().size(); i++) {
			int diffirentTimes = 0;
			String compare;
			for (int j = 0; j < opponentsBidHistory.size(); j++) {
				if (j > 0) {
					compare = opponentsBidHistory.get(j - 1).getValue(i+1).toString();
					if (!compare.equals(opponentsBidHistory.get(j).getValue(i+1).toString())) {
						diffirentTimes++;
					}
				}
			}
			totalDiffentTimes += diffirentTimes;
		}

		for (int i = 0; i < utilitySpace.getDomain().getIssues().size(); i++) {
			int diffirentTimes = 0;
			String compare;
			for (int j = 0; j < opponentsBidHistory.size(); j++) {
				if (j > 0) {
					compare = opponentsBidHistory.get(j - 1).getValue(i+1).toString();
					if (!compare.equals(opponentsBidHistory.get(j).getValue(i+1).toString())) {
						diffirentTimes++;
					}
				}
			}
			Double variance = 1.0-(Double.valueOf(diffirentTimes) / Double.valueOf(totalDiffentTimes));
			opponentsIssueVariance.put(utilitySpace.getDomain().getIssues().get(i), variance);
		}
		MyPrint.printIssueVariance(OppentID, opponentsIssueVariance);
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

	public HashMap<Issue, List<MyValueFrequency>> getOpponentFrequency() {
		return opponentFrequency;
	}

	public void setOpponentFrequency(HashMap<Issue, List<MyValueFrequency>> opponentFrequency) {
		this.opponentFrequency = opponentFrequency;
	}

	public ArrayList<Bid> getOpponentsBidHistory() {
		return opponentsBidHistory;
	}

	public Double getOpponentsAverage() {
		return opponentsAverage;
	}

	public void setOpponentsAverage(Double opponentAverage) {
		this.opponentsAverage = opponentAverage;
	}

	public Double getOpponentsVariance() {
		return opponentsVariance;
	}

	public void setOpponentsVariance(Double opponentVariance) {
		this.opponentsVariance = opponentVariance;
	}

	public LinkedHashMap<Issue, Double> getOpponentsIssueVariance() {
		return opponentsIssueVariance;
	}

	public void setOpponentsIssueVariance(LinkedHashMap<Issue, Double> opponentsIssueVariance) {
		this.opponentsIssueVariance = opponentsIssueVariance;
	}

	public CalculateScoreSystem getCalculateSystem() {
		return calculateSystem;
	}

	public void setCalculateSystem(CalculateScoreSystem calculateSystem) {
		this.calculateSystem = calculateSystem;
	}

	public List<MyValueFrequency> getFrequencys() {
		return frequencys;
	}

	public void setFrequencys(List<MyValueFrequency> frequencys) {
		this.frequencys = frequencys;
	}

	public void setOpponentsBidHistory(ArrayList<Bid> opponentsBidHistory) {
		this.opponentsBidHistory = opponentsBidHistory;
	}

	public AgentID getOppentID() {
		return OppentID;
	}

	public void setOppentID(AgentID oppentID) {
		OppentID = oppentID;
	}


}
