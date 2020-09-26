package com.repetition.rest_api.controller;

import com.repetition.rest_api.mapper.TaskMapper;
import com.repetition.rest_api.model.Task;
import com.repetition.rest_api.model.dto.CreateTaskDto;
import com.repetition.rest_api.model.dto.TaskDto;
import com.repetition.rest_api.model.dto.UpdateTaskDto;
import com.repetition.rest_api.model.enums.Status;
import com.repetition.rest_api.model.enums.Type;
import com.repetition.rest_api.service.TaskService;
import com.repetition.rest_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class TaskController {
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private UserService userService;

    @GetMapping("/tasks")
    public List<TaskDto> getTasks() {
        return taskMapper.toDtos(taskService.getTasks());
    }

    @GetMapping("/tasks/{taskId}")
    public TaskDto getTask(@PathVariable("taskId") long taskId) {
        return taskMapper.toDto(taskService.getTask(taskId));
    }

    @PostMapping("/addTask")
    public void createTask(CreateTaskDto createTaskDto) {
        taskService.createTask(taskMapper.fromDto(createTaskDto));
    }

    @GetMapping("/tasksWithFilter")
    public List<TaskDto> getTasksByTypeAndStatusAndUser(
            @RequestParam Status status,
            @RequestParam Type type,
            @RequestParam long userId) {
        return taskMapper.toDtos(taskService.getTasksByTypeAndStatusAndUser(type, status, userId));
    }

    @PutMapping("/editTask/{taskId}")
    @Transactional
    public boolean editTask(@PathVariable("taskId") long taskId,
                            @RequestParam String title,
                            @RequestParam Status status,
                            @RequestParam Type type) {
        Task task = taskService.getTask(taskId);
        task.setType(type);
        task.setStatus(status);
        task.setTitle(title);
        return taskService.updateTask(task);
    }

    @PutMapping("/editTaskOwner/{taskId}")
    @Transactional
    public boolean editTaskOwner(@PathVariable("taskId") long taskId,
                                 @RequestParam long userId){
        Task task = taskService.getTask(taskId);

        task.setUser(userService.getUser(userId));
        return taskService.updateTaskOwner(task);
    }
    @DeleteMapping("/deleteTask/{taskId}")
    public boolean deleteTask(@PathVariable("taskId") long taskId){
        return taskService.deleteTask(taskId);
    }
    @GetMapping("/groupTasksByStatus")
    public Map<Status, List<Task>> groupTasksByStatus(){
        return taskService.getTasks().stream().collect(Collectors.groupingBy(Task::getStatus));
    }


    // 1. edycja zadania: title, status, type
    // 2. zmiana właściciela zadania
    // 3. usuwanie zadania po id

}
