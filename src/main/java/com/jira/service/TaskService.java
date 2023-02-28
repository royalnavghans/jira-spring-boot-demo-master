package com.jira.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jira.entity.Credentials;
import com.jira.entity.IssueRequest;
import com.jira.entity.Response;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class TaskService {

    private CredentialService credentialService;
    private JiraService jiraService;


    public TaskService(CredentialService credentialService, JiraService jiraService) {
        this.credentialService = credentialService;
        this.jiraService = jiraService;
    }

    public Response createIssue(IssueRequest request) throws UnirestException, IOException {

        Credentials credentials = credentialService.getAllCredentials().get(0);

        ObjectNode payload = buildPayload(request, credentials);

        HttpResponse<JsonNode> response = Unirest.post(credentials.getUrl() + "/rest/api/3/issue")
                .basicAuth(credentials.getUsername(), credentials.getToken())
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .body(payload)
                .asJson();
        JsonNode body = response.getBody();
        return buildResponse(body);

    }

    public ObjectNode buildPayload(IssueRequest request, Credentials credentials) throws UnirestException {
        JsonNode metadata = jiraService.getMetadata(request, credentials);

        JSONArray jsonArray = metadata.getArray();
        JSONObject projectMainJsonObject = jsonArray.getJSONObject(0);
        JSONArray projectsArray = projectMainJsonObject.getJSONArray("projects");
        JSONObject projectsObject = projectsArray.getJSONObject(0);
        JSONArray issueTypesArray = projectsObject.getJSONArray("issuetypes");
        JSONObject issueTypesObject = issueTypesArray.getJSONObject(0);
        JSONObject fieldsObject = issueTypesObject.getJSONObject("fields");

        JsonNodeFactory jnf = JsonNodeFactory.instance;
        ObjectNode payload = jnf.objectNode();
        ObjectNode fields = payload.putObject("fields");
        fields.put("summary", request.getSummary());

        ObjectNode description = fields.putObject("description");
        description.put("type", "doc");
        description.put("version", 1);
        ArrayNode contentMain = description.putArray("content");
        ObjectNode content0 = contentMain.addObject();
        content0.put("type", "paragraph");
        ArrayNode content1 = content0.putArray("content");
        ObjectNode currentObj = content1.addObject();
        currentObj.put("text", request.getDescription());
        currentObj.put("type", "text");

        ObjectNode issueType = fields.putObject("issuetype");
        issueType.put("id", issueTypesObject.getString("id"));
        ObjectNode project = fields.putObject("project");
        project.put("id", projectsObject.getString("id"));

        ObjectMapper objectMapper = new ObjectMapper();

        Unirest.setObjectMapper(new com.mashape.unirest.http.ObjectMapper() {
            @Override
            public <T> T readValue(String s, Class<T> aClass) {
                try {
                    return objectMapper.readValue(s, aClass);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public String writeValue(Object o) {
                try {
                    return objectMapper.writeValueAsString(o);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return payload;
    }

    private Response buildResponse(JsonNode response) throws JsonProcessingException {
        Response responseObject = new Response();
        responseObject.setId((String) response.getObject().get("id"));
        responseObject.setKey((String) response.getObject().get("key"));
        responseObject.setSelf((String) response.getObject().get("self"));
        return responseObject;
    }
}
