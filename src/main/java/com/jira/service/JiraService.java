package com.jira.service;

import com.jira.entity.Credentials;
import com.jira.entity.IssueRequest;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.stereotype.Service;

@Service
public class JiraService {


    public JsonNode getMetadata(IssueRequest request, Credentials credentials) throws UnirestException {

        String api = String.format(
                credentials.getUrl() + "/rest/api/3/issue/createmeta?projectKeys=%s&issuetypeNames=%s&expand=projects.issuetypes.fields",
                request.getProjectKey(),
                request.getIssueType()
        );

        HttpResponse<JsonNode> response = Unirest.get(api)
                .basicAuth(credentials.getUsername(), credentials.getToken())
                .header("Accept", "application/json")
                .asJson();
        return response.getBody();
    }

}
