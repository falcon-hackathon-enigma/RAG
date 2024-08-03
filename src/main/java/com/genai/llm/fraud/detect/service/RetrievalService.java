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
	
	public String orchestrate(String systemMsg, String text, Integer maxResultsToRetrieveDynamic, 
							  Double minScoreRelevanceScoreDynamic, Float temperature) 
							  throws Exception 
	{	
		//--step -1  : get context information from the DB 
		String contextFromVectorDb =  vectorDataSvc.retrieve(text);
		
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
			System.out.println("\n found");
			prompt = prompt.replaceAll("\n", "");			
			System.out.println(" contains \n "+prompt.contains("\n"));
		}
		if(prompt.contains("\t"))
		{
			System.out.println("\t found");
			prompt = prompt.replaceAll("\t", "");			
			System.out.println(" contains \t "+prompt.contains("\t"));
		}		
		if(prompt.contains("\r"))
		{
			System.out.println("\r found");
			prompt = prompt.replaceAll("\r", "");			
			System.out.println(" contains \r "+prompt.contains("\r"));
		}
		
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
