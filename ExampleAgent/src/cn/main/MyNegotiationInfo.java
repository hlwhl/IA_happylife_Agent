package cn.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.Value;
import negotiator.utility.AbstractUtilitySpace;
import negotiator.utility.AdditiveUtilitySpace;
import negotiator.utility.EvaluatorDiscrete;

public class MyNegotiationInfo {
	private AbstractUtilitySpace utilitySpace;
	private List<Issue> issues;
	private Map<Issue, List<MyValueEvaluation>> prefectOrder;
	private Map<Issue, List<Value>> pValueList;

	public MyNegotiationInfo(AbstractUtilitySpace utilitySpace) throws Exception {
		this.utilitySpace = utilitySpace;
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
	}

	//获取pValueList方法
	public Map<Issue, List<Value>> getPValueList(OppentNegotiationInfo oppent1Info,
			OppentNegotiationInfo oppent2Info) {
		pValueList = new HashMap<Issue, List<Value>>();
		if (oppent1Info != null) {
			optionPValueList(oppent1Info);
		}
		if (oppent2Info != null) {
			optionPValueList(oppent2Info);
		}
		return pValueList;
	}

	private void optionPValueList(OppentNegotiationInfo oppentInfo) {
		HashMap<Issue, List<MyValueFrequency>> opponentFrequency = oppentInfo.getOpponentFrequency();
		for (Map.Entry<Issue, List<MyValueFrequency>> issueValue : opponentFrequency.entrySet()) {
			List<MyValueEvaluation> prefectValues = prefectOrder.get(issueValue.getKey());
			double initial = 0.1d;
			while(pValueList.get(issueValue.getKey()) == null && initial <= 1){
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
}