package com.genai.llm.fraud.detect.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.genai.llm.fraud.detect.service.CreditCardsAnalyze;
import com.genai.llm.fraud.detect.service.FileUtilsService;
import com.genai.llm.fraud.detect.service.RetrievalService;
import com.genai.llm.fraud.detect.service.VectorDataStoreService;

@RestController
//@RequestMapping(value = "/gen-ai/v1/llm")
@RequestMapping(value = "/genai/llm")
public class CreditCardsAnalyzeController  
{	
	@Autowired
	private RetrievalService retrievalSvc;
	
	@Autowired
	private VectorDataStoreService vectorDataSvc;

	
	@PostMapping("/creditcards")
	public ResponseEntity<String> creditcardsExamineBig(@RequestBody CreditCardsAnalyze body, @RequestParam(defaultValue = "-1") int maxLimit, 
												   @RequestParam(defaultValue = "-1") double minScore) 
												  throws Exception
	{
		String response = retrievalSvc.orchestrate(body.getSystemMsg(), body.getUsrMsg(),  maxLimit, minScore, body.getTemperature());
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping("/creditcards")
	public ResponseEntity<String> creditcardsExamineSimple(@RequestParam(defaultValue = "") String systemMsg, @RequestParam(defaultValue = "") String usrMsg, 
													  @RequestParam(defaultValue = "-1") int maxLimit, @RequestParam(defaultValue = "-1") double minScore, 
													  @RequestParam(defaultValue = "0") Float temperature) 
													  throws Exception
	{
		String response = retrievalSvc.orchestrate(systemMsg, usrMsg, maxLimit, minScore, temperature);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}	
	
	
	//Chroma operations
	
	/*
	 * endpoint to load newer contexts provided by the user
	 */
	@PostMapping("/context")
	public ResponseEntity<String> loadContext(@RequestParam("file") String fileNameWithFullPath) 
	{
		String response = null;
		if(fileNameWithFullPath == null || "".equals(fileNameWithFullPath.trim()))
		{
			response = "File is empty. Nothing to load";
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}		
	
		
		String currentDir = System.getProperty("user.dir");
		String resourcePath = currentDir + "\\"+ "\\src\\main\\resources\\application.properties";
		String vectorDbName = new FileUtilsService().extractFields("vector.db.name", resourcePath);
		vectorDataSvc.load(fileNameWithFullPath, vectorDbName);
		
		response = "Vector DB new context loaded";
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	

	/*
	 * endpoint to get a response from the local LLM inference engine 
	 */
	@GetMapping("/retrieve")
	public ResponseEntity<String> retrieve( @RequestParam("systemMsg") String systemMsg, @RequestParam("usrMsg") String usrMsg,											 
											@RequestParam(defaultValue = "-1") int maxLimit, 
											@RequestParam(defaultValue = "-1") double minScore,
											@RequestParam(defaultValue = "0.5") float temperature) throws Exception
	{
		System.out.println("---- started retrieve ");
		String response = retrievalSvc.orchestrate(systemMsg, usrMsg,  maxLimit, minScore, temperature);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	/*
	 * endpoint to load customer genuine transaction pattern contexts from PDFs provided by the bank
	 */
	/*
	@PostMapping("/contextWithGenuineTxns")
	public ResponseEntity<String> loadContextWithGenuineTxns() 
	{
		String fileNameWithFullPath1 = "C:\\Vijay\\Java\\projects\\openapi-ai-trials\\LLM-gen-ai\\langchain4j\\gen-ai-llm-fraud-detect\\training-docs\\CardTransactions_Genuine_JohnMayo.pdf";
		String fileNameWithFullPath2 = "C:\\Vijay\\Java\\projects\\openapi-ai-trials\\LLM-gen-ai\\langchain4j\\gen-ai-llm-fraud-detect\\training-docs\\CardTransactions_Genuine_AdamThorpe.pdf";
		
		String response = null;		
		String currentDir = System.getProperty("user.dir");
		String resourcePath = currentDir + "\\"+ "\\src\\main\\resources\\application.properties";
		String vectorDbName = new FileUtilsService().extractFields("vector.db.name", resourcePath);
		
		vectorDataSvc.loadWithGenuineTxns(fileNameWithFullPath1, vectorDbName);
		vectorDataSvc.loadWithGenuineTxns(fileNameWithFullPath2, vectorDbName);
		
		response = "Vector DB new context loaded";
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	*/
	
}
