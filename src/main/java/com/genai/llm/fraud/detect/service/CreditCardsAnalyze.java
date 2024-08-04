package com.genai.llm.fraud.detect.service;

public class CreditCardsAnalyze 
{

	String systemMsg;
	String usrMsg;
	Float temperature;
	int  maxLimit; 
	double minScore;
	
	public String getSystemMsg() {
		return systemMsg;
	}
	public void setSystemMsg(String systemMsg) {
		this.systemMsg = systemMsg;
	}
	public String getUsrMsg() {
		return usrMsg;
	}
	public void setUsrMsg(String usrMsg) {
		this.usrMsg = usrMsg;
	}
	public Float getTemperature() {
		return temperature;
	}
	public void setTemperature(Float temperature) {
		this.temperature = temperature;
	}
	public int getMaxLimit() {
		return maxLimit;
	}
	public void setMaxLimit(int maxLimit) {
		this.maxLimit = maxLimit;
	}
	public double getMinScore() {
		return minScore;
	}
	public void setMinScore(double minScore) {
		this.minScore = minScore;
	}
	
	@Override
	public String toString() {
		return "CreditCardsAnalyze [systemMsg=" + systemMsg + ", usrMsg=" + usrMsg + ", temperature=" + temperature
				+ ", maxLimit=" + maxLimit + ", minScore=" + minScore + "]";
	}	
}
