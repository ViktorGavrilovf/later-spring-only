package ru.practicum.user;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();
    User saveUser(User user);
}
