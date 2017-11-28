package cn.main;

import java.util.List;
import java.util.Map;

import negotiator.issue.Issue;
import negotiator.issue.Value;

public class MyPrint {

	public static void printPValueList(Map<Issue, List<Value>> pValueList) {
		for (Map.Entry<Issue, List<Value>> issueValues: pValueList.entrySet()) {
			System.out.println("issue : " + issueValues.getKey().getName());
			for (Value value : issueValues.getValue()) {
				System.out.println("		value : " + value.toString());
			}
			System.out.println("");
		}
	}

	public static void printPrefectOrder(Map<Issue, List<MyValueEvaluation>> prefectOrder) {
		for (Map.Entry<Issue, List<MyValueEvaluation>> oppoInfo : prefectOrder.entrySet()) {
			System.out.println("Issue : " + oppoInfo.getKey());
			for (int i = 0; i < oppoInfo.getValue().size(); i++) {
				MyValueEvaluation value = oppoInfo.getValue().get(i);
				System.out.println(value.getValue().toString() + " : " + value.getEvaluation());
			}
		}
		
	}
}
