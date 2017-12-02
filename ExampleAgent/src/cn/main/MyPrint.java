package cn.main;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import negotiator.AgentID;
import negotiator.issue.Issue;
import negotiator.issue.Value;

public class MyPrint {

	public static void printPValueList(Map<Issue, List<Value>> pValueList) {
		for (Map.Entry<Issue, List<Value>> issueValues: pValueList.entrySet()) {
			System.out.println("issue : " + issueValues.getKey().getName());
			for (Value value : issueValues.getValue()) {
				System.out.println(" value : " + value.toString());
			}
		}
		System.out.println("");
	}

	public static void printPrefectOrder(Map<Issue, List<MyValueEvaluation>> prefectOrder) {
		System.out.println("Tittle : prefectOrder");
		for (Map.Entry<Issue, List<MyValueEvaluation>> oppoInfo : prefectOrder.entrySet()) {
			System.out.println("Issue : " + oppoInfo.getKey());
			for (int i = 0; i < oppoInfo.getValue().size(); i++) {
				MyValueEvaluation value = oppoInfo.getValue().get(i);
				System.out.println(" " + value.getValue().toString() + " : " + value.getEvaluation());
			}
		}
		System.out.println(" ");
		
	}

	public static void printOpponentFrequency(HashMap<Issue, List<MyValueFrequency>> opponentFrequency) {
		System.out.println("Tittle : opponentFrequency");
		for (Map.Entry<Issue, List<MyValueFrequency>> oppoInfo : opponentFrequency.entrySet()) {
			System.out.println("Issue : " + oppoInfo.getKey());
			for (int i = 0; i < oppoInfo.getValue().size(); i++) {
				MyValueFrequency value = oppoInfo.getValue().get(i);
				System.out.println(" " + value.getValue().toString() + " : " + value.getFrequency());
			}
		}
		System.out.println(" ");
	}

	public static void printThreshold(Double minThreshold, Double maxThreshold, Double averageThreshold) {
		System.out.println("minThreshold : " + minThreshold + "; maxThreshold : " + maxThreshold + "; averageThreshold : " + averageThreshold);
		
	}

	public static void printIssueWeight(AgentID OppentID, Map<Issue, Double> opponentsIssueWeight) {
		System.out.println("OppentID : " + OppentID + "--Tittle : opponentsIssueWeight");
		for (Map.Entry<Issue, Double> issue : opponentsIssueWeight.entrySet()) {
			System.out.println(" Issue : " + issue.getKey() + "--" + issue.getValue());
		}
		System.out.println(" ");
	}

	public static void printIssueVariance(AgentID OppentID, LinkedHashMap<Issue, Double> opponentsIssueVariance) {
		System.out.println("OppentID : " + OppentID + "--Tittle : opponentsIssueVariance");
		for (Map.Entry<Issue, Double> issue : opponentsIssueVariance.entrySet()) {
			System.out.println(" Issue : " + issue.getKey() + "--" + issue.getValue());
		}
		System.out.println(" ");
		
	}

	// 打印配置文件详细分数信息
	public static void printPreferenceInfo(Map<Issue, Map<Value, String>> scoreCondition) {
		System.out.println("自己配置文件详细得分");
		for (Map.Entry<Issue, Map<Value, String>> issue : scoreCondition.entrySet()) {
			System.out.println("issue: " + issue.getKey());
			for (Map.Entry<Value, String> value : issue.getValue().entrySet()) {
				System.out.println(value.getKey() + " " + value.getValue() + " ");
			}
			System.out.println("");
		}
		
		
	}

}
