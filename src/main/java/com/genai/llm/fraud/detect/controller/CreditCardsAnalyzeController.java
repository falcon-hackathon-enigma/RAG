package com.genai.llm.fraud.detect.controller;

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

import io.micrometer.common.util.StringUtils;

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
	public ResponseEntity<String> creditcardsExamineBig(@RequestBody CreditCardsAnalyze body) throws Exception
	{
		System.out.println("\n---- user input "+ body.toString());
		
		String response = null;
		if(StringUtils.isBlank(body.getUsrMsg()))
		{
			response = "Error: User prompt cannot be empty";
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); 
		}
		
		response = retrievalSvc.orchestrate(body.getSystemMsg(), body.getUsrMsg(), body.getMaxLimit(), body.getMinScore(), body.getTemperature());
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping("/creditcards")
	public ResponseEntity<String> creditcardsExamineSimple(@RequestParam(defaultValue = "") String systemMsg, 
														  @RequestParam(defaultValue = "") String usrMsg, 
														  @RequestParam(defaultValue = "") int maxLimit, 
														  @RequestParam(defaultValue = "") double minScore, 
														  @RequestParam(defaultValue = "") Float temperature) 
														  throws Exception
	{
		System.out.println("\n---- user input systemMsg "+ systemMsg);
		System.out.println("---- user input usrMsg "+ usrMsg);
		System.out.println("---- user input maxLimitVectorDBRetrieval "+ maxLimit);
		System.out.println("---- user input minScoreVectorDBRetrieval "+ minScore);
		System.out.println("---- user input temperature "+ temperature);
		
		String response = null;
		if(StringUtils.isBlank(usrMsg))
		{
			response = "Error: User prompt cannot be empty";
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); 
		}
		
		response = retrievalSvc.orchestrate(systemMsg, usrMsg, maxLimit, minScore, temperature);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}	
	
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
		String resourcePath = currentDir + "/"+ "src/main/resources/application.properties";
		String vectorDbName = new FileUtilsService().extractFields("vector.db.name", resourcePath);
		vectorDataSvc.load(fileNameWithFullPath, vectorDbName);
		
		response = "Vector DB new context loaded";
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
