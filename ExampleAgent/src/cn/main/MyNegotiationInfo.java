package cn.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.utility.AbstractUtilitySpace;
import negotiator.utility.AdditiveUtilitySpace;
import negotiator.utility.EvaluatorDiscrete;

public class MyNegotiationInfo {
	private AbstractUtilitySpace utilitySpace;
	private List<Issue> issues;
	private Map<Issue, List<MyValueEvaluation>> prefectOrder;

	public MyNegotiationInfo(AbstractUtilitySpace utilitySpace) throws Exception {
		this.utilitySpace = utilitySpace;
		issues = utilitySpace.getDomain().getIssues();
		initPrefectOrder();
		printPrefectOrder();
	}

	private void printPrefectOrder() {
		for (Map.Entry<Issue, List<MyValueEvaluation>> oppoInfo : prefectOrder.entrySet()) {
			System.out.println("Issue : " + oppoInfo.getKey());
			for (int i = 0; i < oppoInfo.getValue().size(); i++) {
				MyValueEvaluation value = oppoInfo.getValue().get(i);
				System.out.println(value.getValue().toString() + " : " + value.getEvaluation());
			}
		}
	}

	private void initPrefectOrder() throws Exception {
		prefectOrder = new HashMap<Issue, List<MyValueEvaluation>>();
		int N = issues.size();
		for (int i = 0; i < N; i++) {
			IssueDiscrete di = (IssueDiscrete)issues.get(i);
			EvaluatorDiscrete de = (EvaluatorDiscrete)((AdditiveUtilitySpace) utilitySpace).getEvaluator(i + 1);
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
}
