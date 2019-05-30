package com.mulodo.springbootdocker.controller;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ConsumeWebService {

	@Value("${GIT_API_URL}")
	String GIT_API_URL;

	@Value("${GIT_REPOS_ENDPOINT}")
	String GIT_REPOS_ENDPOINT;

	@Value("${GIT_ACCESS_TOKEN}")
	String GIT_ACCESS_TOKEN;

	@Value("${SONARQUBE_API_URL}")
	String SONARQUBE_API_URL;

	@Value("${SONAR_PROJECT_ENDPOINT}")
	String SONAR_PROJECT_ENDPOINT;

	@Value("${SONAR_USERNAME}")
	String SONAR_USERNAME;
	
	@Value("${SONAR_PASSWORD}")
	String SONAR_PASSWORD;
	
	@Value("${REDMINE_API_URL}")
	String REDMINE_API_URL;

	@Value("${REDMINE_PROJECT_ENDPOINT}")
	String REDMINE_PROJECT_ENDPOINT;

	@Value("${REDMINE_USERNAME}")
	String REDMINE_USERNAME;
	
	@Value("${REDMINE_PASSWORD}")
	String REDMINE_PASSWORD;
	
	private final String AUTHBASIC = "Basic";
	private final String OAUTH = "OAuth";
	

	@Autowired
	RestTemplate restTemplate;

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	private String exchangeAPI(String authType, String apiURL, String token, String username, String password) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		if(authType.equals(AUTHBASIC)) {
			headers.setBasicAuth(username, password);
		}
		if(authType.equals(OAUTH)) {
			headers.setBearerAuth(token);
		}
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		System.out.println(apiURL);
		return restTemplate.exchange(apiURL, HttpMethod.GET, entity, String.class)
				.getBody();
	}
	
	public String exchangeAPIAuthBasic(String apiURL, String username, String password) {
		return exchangeAPI(AUTHBASIC, apiURL, null, username, password);
	}
	
	public String exchangeAPIOAuth(String apiURL, String token) {
		return exchangeAPI(OAUTH, apiURL, token, null, null);
	}
	
	@CrossOrigin
	@RequestMapping(value = "/api/git/repos")
	public String getGitRepos() {
		return exchangeAPIOAuth((GIT_API_URL + "/" + GIT_REPOS_ENDPOINT), GIT_ACCESS_TOKEN);
	}

	@CrossOrigin
	@RequestMapping(value = "/api/sonar/projects")
	public String getSonarProjects() {
		return exchangeAPIAuthBasic((SONARQUBE_API_URL + "/" + SONAR_PROJECT_ENDPOINT), SONAR_USERNAME, SONAR_PASSWORD);
	}
	
	@CrossOrigin
	@RequestMapping(value = "/api/redmine/projects")
	public String getRedmineProjects() {
		return exchangeAPIAuthBasic((REDMINE_API_URL + "/" + REDMINE_PROJECT_ENDPOINT), REDMINE_USERNAME, REDMINE_PASSWORD);
	}

}