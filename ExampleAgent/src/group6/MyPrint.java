package group6;

import negotiator.AgentID;
import negotiator.issue.Issue;
import negotiator.issue.Value;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MyPrint {

	public static void printPValueList(Map<Issue, List<Value>> pValueList) {
		for (Map.Entry<Issue, List<Value>> issueValues : pValueList.entrySet()) {
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
		System.out.println("minThreshold : " + minThreshold + "; maxThreshold : " + maxThreshold
				+ "; averageThreshold : " + averageThreshold);

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

	// 打印对方配置文件信息
	public static void printScoreDetail(AgentID agentID, Map<Issue, Map<Value, Double>> frequencyTen,
			Map<Issue, Double> weight) {
		System.out.println(agentID + "配置文件详细得分");
		for (Map.Entry<Issue, Map<Value, Double>> frequency : frequencyTen.entrySet()) {
			System.out.println("issue: " + frequency.getKey());
			for (Map.Entry<Value, Double> value : frequency.getValue().entrySet()) {
				BigDecimal b = new BigDecimal(value.getValue());
				double valueD = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				BigDecimal bb;
				if (weight.get(frequency.getKey()).equals(Double.NaN)) {
					bb = new BigDecimal(0);
				} else {
					bb = new BigDecimal(weight.get(frequency.getKey()));
				}
				double valueDD = bb.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				BigDecimal bbb = new BigDecimal(valueD * valueDD);
				double valueDDD = bbb.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				String detail = valueD + " x " + valueDD + " = " + valueDDD;
				System.out.println(value.getKey() + " " + detail + " ");
			}
			System.out.println("");
		}
	}

	// 打印得分最高的对方Bid
	public static void printMaxScore(AgentID agentID, Map<Issue, Value> maxScoreBid, Double maxScore) {
		System.out.println(agentID + "的分最高Bid");
		for (Map.Entry<Issue, Value> score : maxScoreBid.entrySet()) {
			System.out.print(score.getKey() + " " + score.getValue() + " ");
		}
		System.out.println("score : " + maxScore);

	}

}
