package com.genai.llm.fraud.detect.service;

public class CreditCardsAnalyze 
{

	String systemMsg;
	String usrMsg;
	Float temperature = 0.4f;
	
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
	
	@Override
	public String toString() {
		return "CreditCardsAnalyze [systemMsg=" + systemMsg + ", usrMsg=" + usrMsg + ", temperature=" + temperature + "]";
	}	
	
}
