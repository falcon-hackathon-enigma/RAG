package com.genai.llm.fraud.detect.service;

import java.util.List;

public class CreditCardsContext 
{

	List<String> context;

	public List<String> getContext() {
		return context;
	}

	public void setContext(List<String> context) {
		this.context = context;
	}

	@Override
	public String toString() {
		return "CreditCardsContext [context=" + context + "]";
	}
	
	
}
