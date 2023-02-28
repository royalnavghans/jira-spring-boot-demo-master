package com.jira.controller;

import com.jira.entity.Credentials;
import com.jira.entity.IssueRequest;
import com.jira.entity.Response;
import com.jira.service.CredentialService;
import com.jira.service.TaskService;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class TaskController {

    private TaskService taskService;
    private CredentialService credentialService;

    public TaskController(TaskService taskService, CredentialService credentialService) {
        this.taskService = taskService;
        this.credentialService = credentialService;
    }

    @PostMapping("/create/issue")
    public ResponseEntity<Response> createIssue(@RequestBody IssueRequest request) throws UnirestException, IOException {
        return new ResponseEntity<>(taskService.createIssue(request), HttpStatus.CREATED);
    }

    @PostMapping("/create/user")
public ResponseEntity<Credentials>createUser(Credentials credentials){
        return new ResponseEntity<>(credentialService.createUser(credentials),HttpStatus.CREATED);
}




}
