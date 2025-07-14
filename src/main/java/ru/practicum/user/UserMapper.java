package ru.practicum.user;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class UserMapper {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy.MM.dd, HH:mm:ss").withZone(ZoneId.systemDefault());

    public static UserDto toDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        userDto.setRegistrationDate(user.getRegistrationDate() != null ?
                FORMATTER.format(user.getRegistrationDate()) : null);
        userDto.setUserState(user.getUserState() != null ? user.getUserState() : null);
        return userDto;
    }

    public static User fromDto(UserDto dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setUserState(dto.getUserState() != null ? dto.getUserState() : null);
        user.setRegistrationDate(Instant.now());
        return user;
    }
}
