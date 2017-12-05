package group6;

import negotiator.Bid;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.Value;
import negotiator.utility.AbstractUtilitySpace;
import negotiator.utility.AdditiveUtilitySpace;
import negotiator.utility.EvaluatorDiscrete;

import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;

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
	private Map<Issue, Map<Value, String>> scoreCondition = new HashMap<Issue, Map<Value, String>>(); 
	private void initPrefectOrder() throws Exception {
		prefectOrder = new HashMap<Issue, List<MyValueEvaluation>>();
		int N = issues.size();
		for (int i = 0; i < N; i++) {
			IssueDiscrete di = (IssueDiscrete) issues.get(i);
			EvaluatorDiscrete de = (EvaluatorDiscrete) ((AdditiveUtilitySpace) utilitySpace).getEvaluator(i + 1);
			int M = di.getNumberOfValues();
			List<MyValueEvaluation> values = new ArrayList<MyValueEvaluation>();
			
			//打印配置文件信息
			Map<Value, String> valueInfo = new HashMap<Value, String>();
			for (int j = 0; j < M; j++) {
				MyValueEvaluation value = new MyValueEvaluation();
				value.setEvaluation(de.getEvaluation(di.getValue(j)).doubleValue());
				value.setValue(di.getValue(j));
				values.add(value);
				
				
				BigDecimal b = new BigDecimal(de.getEvaluation(di.getValue(j)).doubleValue());  
				double valueD = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				BigDecimal bb = new BigDecimal(de.getWeight());  
				double valueDD = bb.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				BigDecimal bbb = new BigDecimal(valueD*valueDD);  
				double valueDDD = bbb.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				valueInfo.put(di.getValue(j), valueD + " x " + valueDD + " = " + valueDDD);
			}
			scoreCondition.put(di, valueInfo);
			sort(values);
			prefectOrder.put(di, values);
			
		}
//		MyPrint.printPreferenceInfo(scoreCondition);
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
		for (Entry<Issue, List<Value>> issues : pValueList.entrySet()) {
			number *= issues.getValue().size();
		}
		return number;
	}

	public void optionPValueList(OppentNegotiationInfo oppentInfo) {
		if (pValueList == null) pValueList = new HashMap<Issue, List<Value>>();
		HashMap<Issue, List<MyValueFrequency>> opponentFrequency = oppentInfo.getOpponentFrequency();
		for (Entry<Issue, List<MyValueFrequency>> issueValue : opponentFrequency.entrySet()) {
			List<MyValueEvaluation> prefectValues = prefectOrder.get(issueValue.getKey());
			double initial = 0.1d;
			while((pValueList.get(issueValue.getKey()) == null || pValueList.get(issueValue.getKey()).size() <= Math.ceil(prefectValues.size()/5)) && initial <= 1){
				getSameValue(0, initial, issueValue, prefectValues);
				initial += 0.1d;
			}
		}
		MyPrint.printPValueList(pValueList);
		getThreshold();
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