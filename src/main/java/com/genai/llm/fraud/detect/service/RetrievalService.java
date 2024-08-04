package com.genai.llm.fraud.detect.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.testcontainers.shaded.org.apache.commons.lang3.StringUtils;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;



import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.huggingface.HuggingFaceEmbeddingModel;
import dev.langchain4j.model.output.Response;

import static java.time.Duration.ofSeconds;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Service
public class RetrievalService 
{
	@Autowired
	private VectorDataStoreService vectorDataSvc;
	
	@Autowired
	private Utils utils;
	
	
	@Value("${llm.system.message}")
	String defaultSystemMessage; 
	
	@Value("${llm.response.temperature}")
	Float defaultTemperature; 
	
	public String orchestrate(String systemMsg, 
							  String text, 
							  Integer maxResultsToRetrieveDynamic, 
							  Double minScoreRelevanceScoreDynamic, 
							  Float temperature) 
							  throws Exception 
	{	
		//step -0 : handle inputs
		systemMsg = utils.handleInputs(systemMsg, defaultSystemMessage);
		temperature = utils.handleInputs(temperature, defaultTemperature);
		
		
		//--step -1  : get context information from the DB 
		String contextFromVectorDb =  vectorDataSvc.retrieve(text, maxResultsToRetrieveDynamic, minScoreRelevanceScoreDynamic);
		
		//--step -2  : prepare full prompt
		String promptWithFullContext = systemMsg + " These are the creditcards available " + contextFromVectorDb + " \n Here is the user requirement "  +   text ;				
		System.out.println("---- promptWithFullContext \n "+promptWithFullContext);
		
		//--step -3  : invoke LLM
		return execute(promptWithFullContext, temperature);
	}	

	private String execute(String prompt, Float temperature)
			throws IOException, InterruptedException 
	{	
		if(prompt.contains("\n"))
		{
			System.out.println("\n\n backslash n found");
			prompt = prompt.replaceAll("\n", "");			
			System.out.println(" contains backslash n "+prompt.contains("\n"));
		}
		if(prompt.contains("\t"))
		{
			System.out.println(" \n\n backslash t found");
			prompt = prompt.replaceAll("\t", "");			
			System.out.println(" contains backslash t "+prompt.contains("\t"));
		}		
		if(prompt.contains("\r"))
		{
			System.out.println(" \n\n backslash r found");
			prompt = prompt.replaceAll("\r", "");			
			System.out.println(" contains backslash  r "+prompt.contains("\r"));
		}
		
		//how to feed temperature param   vj1
		HttpRequest request = HttpRequest.newBuilder()
								.uri(URI.create("https://api.ai71.ai/v1/chat/completions"))
								.header("Content-Type", "application/json")
								.header("Authorization", "Bearer " + System.getenv("ai71_token"))
								.method("POST", HttpRequest.BodyPublishers.ofString("{\n    \"model\": \"tiiuae/falcon-180b-chat\",\n    \"messages\": [\n      {\n        \"role\": \"system\",\n        \"content\":"+    "\"" + prompt  +"\""     +"\n      },\n      {\n        \"role\": \"user\",\n        \"content\": \"\"\n      }\n    ]\n  }"))
								.build();
		
		HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		
		System.out.println("\n---- response \n"+response.body());
		return response.body();		
	}
}
