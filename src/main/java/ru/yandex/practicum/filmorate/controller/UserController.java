package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/users")
    public User create(@Valid @RequestBody User user) throws ValidationException {
        return userService.getUserStorage().createUser(user);
    }

    @GetMapping("/users")
    public Collection<User> getUsers() {
        return userService.getUserStorage().getUsers();
    }

    @PutMapping("/users")
    public User update(@Valid @RequestBody User user) throws ValidationException {
        return userService.getUserStorage().updateUser(user);
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable Integer id) {
        return userService.getUserStorage().getUserById(id);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void createFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        userService.createFriend(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getFriends(@PathVariable Integer id) {
        return userService.getFriends(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}
