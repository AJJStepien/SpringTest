package com.repetition.rest_api.service;

import com.repetition.rest_api.model.Task;
import com.repetition.rest_api.model.User;
import com.repetition.rest_api.model.enums.Status;
import com.repetition.rest_api.model.enums.Type;
import com.repetition.rest_api.repository.TaskRepository;
import com.repetition.rest_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public List<Task> getTasks() {
        return taskRepository.findAll();
    }

    public Task getTask(long taskId) {
        return taskRepository.findById(taskId)
                .orElseGet(() -> new Task(0, null, null, null, null, null));
    }

    public List<Task> getTasksByTypeAndStatusAndUser(Type type, Status status, long userId) {
        Optional<User> isUser = userRepository.findById(userId);
        if (isUser.isPresent()) {
            return taskRepository.findAllByUserAndStatusAndType(isUser.get(), status, type);
        }
        return taskRepository.findAllByUserAndStatusAndType(null, status, type);
    }

    public boolean updateTask(Task task) {
        return taskRepository.findById(task.getId()).flatMap(t -> {
            t.setType(task.getType());
            t.setStatus(task.getStatus());
            t.setTitle(task.getTitle());
            return Optional.of(true);
        }).orElse(false);
    }

    public boolean updateTaskOwner(Task task) {
        return taskRepository.findById(task.getId())
                .flatMap(t -> {
                    t.setUser(userRepository.getOne(task.getUser().getId()));
                    return Optional.of(true);
                }).orElse(false);
    }

    public boolean deleteTask(long taskId) {
        if (taskRepository.existsById(taskId)) {
            taskRepository.deleteById(taskId);
            return true;
        }
        return false;
    }

}
