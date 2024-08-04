package com.genai.llm.fraud.detect.service;

import org.springframework.stereotype.Service;
import org.testcontainers.shaded.org.apache.commons.lang3.StringUtils;

@Service
public class Utils 
{
	public String handleInputs(String value, String defaultValue) 
	{
		String result = value;
		if(StringUtils.isBlank(value))
		{
			result = defaultValue;
		}
		return result;
	}
	
	public Float handleInputs(Float value, Float defaultValue) 
	{
		Float result = value;
		if(value == null || value == 0 || StringUtils.isBlank(value.toString()))
		{
			result = defaultValue;
		}
		return result;
	}
	
	public Integer handleInputs(Integer value, Integer defaultValue) 
	{
		Integer result = value;
		if(value == null || value == 0 || StringUtils.isBlank(value.toString()))
		{
			result = defaultValue;
		}
		return result;
	}
	
	public Double handleInputs(Double value, Double defaultValue) 
	{
		Double result = value;
		if(value == null || value == 0 || StringUtils.isBlank(value.toString()))
		{
			result = defaultValue;
		}
		return result;
	}
}