package cn.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import negotiator.Bid;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.Value;
import negotiator.utility.AbstractUtilitySpace;
import negotiator.utility.AdditiveUtilitySpace;
import negotiator.utility.EvaluatorDiscrete;

public class MyNegotiationInfo {
	private AbstractUtilitySpace utilitySpace;
	private MyNegotiationStrategy strategy;
	private List<Issue> issues;
	private Map<Issue, List<MyValueEvaluation>> prefectOrder;
	private Map<Issue, List<Value>> pValueList;
	private Double minThreshold = 1.0d;
	private Double maxThreshold = 0.0d;
	private Double averageThreshold = 0.0d;

	public MyNegotiationInfo(AbstractUtilitySpace utilitySpace, MyNegotiationStrategy strategy) throws Exception {
		this.utilitySpace = utilitySpace;
		this.strategy = strategy;
		issues = utilitySpace.getDomain().getIssues();
		initPrefectOrder();
		MyPrint.printPrefectOrder(prefectOrder);
	}

	private void initPrefectOrder() throws Exception {
		prefectOrder = new HashMap<Issue, List<MyValueEvaluation>>();
		int N = issues.size();
		for (int i = 0; i < N; i++) {
			IssueDiscrete di = (IssueDiscrete) issues.get(i);
			EvaluatorDiscrete de = (EvaluatorDiscrete) ((AdditiveUtilitySpace) utilitySpace).getEvaluator(i + 1);
			int M = di.getNumberOfValues();
			List<MyValueEvaluation> values = new ArrayList<MyValueEvaluation>();
			for (int j = 0; j < M; j++) {
				MyValueEvaluation value = new MyValueEvaluation();
				value.setEvaluation(de.getEvaluation(di.getValue(j)).doubleValue());
				value.setValue(di.getValue(j));
				values.add(value);
			}
			sort(values);
			prefectOrder.put(di, values);
		}
	}

	private void sort(List<MyValueEvaluation> frequencys) {
		Collections.sort(frequencys, new Comparator<MyValueEvaluation>() {

			/* int compare(Student o1, Student o2) 返回一个基本类型的整型， 返回负数表示：o1 小于o2， 返回0 表示：o1和o2相等， 返回正数表示：o1大于o2。 */
			public int compare(MyValueEvaluation o1, MyValueEvaluation o2) {

				// 降序排列
				if (o1.getEvaluation() < o2.getEvaluation()) { return 1; }
				if (o1.getEvaluation() == o2.getEvaluation()) { return 0; }
				return -1;
			}
		});

	}

	public void optionPValueList(OppentNegotiationInfo oppent1Info,
			OppentNegotiationInfo oppent2Info) {
		pValueList = new HashMap<Issue, List<Value>>();
		if (oppent1Info != null) {
			optionPValueList(oppent1Info);
		}
		if (oppent2Info != null) {
			optionPValueList(oppent2Info);
		}
		MyPrint.printPValueList(pValueList);
		getThreshold();
	}

	private void getThreshold() {
		minThreshold = 1.0d;
		maxThreshold = 0.0d;
		averageThreshold = 0.0d;
		int num = getPossiblePValueNum();
		for (int i = 0; i < 2 * num; i++) {
			Bid bid = strategy.getRandomFromPValueList(pValueList);
			double utility = utilitySpace.getUtility(bid);
			if (utility < minThreshold)
				minThreshold = utility;
			if (utility > maxThreshold)
				maxThreshold = utility;
		}
		averageThreshold = (minThreshold + maxThreshold)/2;
		MyPrint.printThreshold(minThreshold, maxThreshold, averageThreshold);
	}

	private int getPossiblePValueNum() {
		int number = 1;
		for (Map.Entry<Issue, List<Value>> issues : pValueList.entrySet()) {
			number *= issues.getValue().size();
		}
		return number;
	}

	private void optionPValueList(OppentNegotiationInfo oppentInfo) {
		HashMap<Issue, List<MyValueFrequency>> opponentFrequency = oppentInfo.getOpponentFrequency();
		for (Map.Entry<Issue, List<MyValueFrequency>> issueValue : opponentFrequency.entrySet()) {
			List<MyValueEvaluation> prefectValues = prefectOrder.get(issueValue.getKey());
			double initial = 0.1d;
			while((pValueList.get(issueValue.getKey()) == null || pValueList.get(issueValue.getKey()).size() <= Math.ceil(prefectValues.size()/5)) && initial <= 1){
				getSameValue(0, initial, issueValue, prefectValues);
				initial += 0.1d;
			}
		}
	}


	private void getSameValue(double startScale, double endScale, Entry<Issue, List<MyValueFrequency>> issueValue,
			List<MyValueEvaluation> myValues) {
		List<Value> sameValues = new ArrayList<Value>();
		List<MyValueFrequency> oppentValues = issueValue.getValue();
		Integer startIndex = getStartIndex(startScale, oppentValues.size());
		Integer endIndex = getEndIndex(endScale, oppentValues.size());
		for (int i = startIndex; i <= endIndex; i++) {
			for (int j = startIndex; j <= endIndex; j++) {
				if (oppentValues.get(i).getValue().equals(myValues.get(j).getValue()))
					sameValues.add(oppentValues.get(i).getValue());
			}
		}
		if (sameValues.size() == 0)
			return;
		pValueList.put(issueValue.getKey(), sameValues);
	}

	private Integer getStartIndex(double startScale, int size) {
		return (int) Math.floor(startScale * (size - 1));
	}
	
	private Integer getEndIndex(double endScale, int size) {
		return (int) Math.floor(endScale * (size - 1));
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

	public Map<Issue, List<MyValueEvaluation>> getPrefectOrder() {
		return prefectOrder;
	}

	public void setPrefectOrder(Map<Issue, List<MyValueEvaluation>> prefectOrder) {
		this.prefectOrder = prefectOrder;
	}

	public Map<Issue, List<Value>> getpValueList() {
		return pValueList;
	}

	public void setpValueList(Map<Issue, List<Value>> pValueList) {
		this.pValueList = pValueList;
	}

	public MyNegotiationStrategy getStrategy() {
		return strategy;
	}

	public void setStrategy(MyNegotiationStrategy strategy) {
		this.strategy = strategy;
	}

	public Double getMinThreshold() {
		return minThreshold;
	}

	public void setMinThreshold(Double minThreshold) {
		this.minThreshold = minThreshold;
	}

	public Double getMaxThreshold() {
		return maxThreshold;
	}

	public void setMaxThreshold(Double maxThreshold) {
		this.maxThreshold = maxThreshold;
	}

	public Double getAverageThreshold() {
		return averageThreshold;
	}

	public void setAverageThreshold(Double averageThreshold) {
		this.averageThreshold = averageThreshold;
	}
	
}