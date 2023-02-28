package com.jira.service;

import com.jira.entity.Credentials;
import com.jira.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CredentialService {

    private TaskRepository taskRepository;

    public CredentialService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Credentials> getAllCredentials() {
        return taskRepository.findAll();
    }
    public Credentials createUser(Credentials credentials){
       return taskRepository.save(credentials);
    }

}
