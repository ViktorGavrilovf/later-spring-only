package ru.practicum.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.config.AppConfig;
import ru.practicum.config.PersistenceConfig;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(properties = {
        "jakarta.persistence.jdbc.url=jdbc:postgresql://localhost:5432/later",
        "jakarta.persistence.jdbc.user=dbuser",
        "jakarta.persistence.jdbc.password=12345",
        "jakarta.persistence.jdbc.driver=org.postgresql.Driver",
        "hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect"
})
@SpringJUnitConfig({AppConfig.class, PersistenceConfig.class, UserServiceImpl.class})
class UserServiceImplTest {

    private final EntityManager em;
    private final UserService service;

    @Test
    void testSaveUser() {
        UserDto userDto = makeUserDto("some@email.com", "Пётр", "Иванов");

        service.saveUser(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getFirstName(), equalTo(userDto.getFirstName()));
        assertThat(user.getLastName(), equalTo(userDto.getLastName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
        assertThat(user.getUserState(), equalTo(userDto.getUserState()));
        assertThat(user.getRegistrationDate(), notNullValue());
    }

    @Test
    void testGetAllUsers() {
        List<UserDto> sourceUsers = List.of(
                makeUserDto("ivan@email", "Ivan", "Ivanov"),
                makeUserDto("petr@email", "Petr", "Petrov"),
                makeUserDto("vasilii@email", "Vasilii", "Vasiliev")
        );

        for (UserDto userDto : sourceUsers) {
            service.saveUser(userDto);
        }

        List<User> userList = service.getAllUsers().stream().map(UserMapper::fromDto).toList();

        TypedQuery<User> query = em.createQuery("select u from User u", User.class);
        List<User> targetUser = query.getResultList();

        assertThat(userList.size(), equalTo(targetUser.size()));
        assertThat(userList, is(targetUser));
    }

    private UserDto makeUserDto(String email, String firstName, String lastName) {
        UserDto dto = new UserDto();
        dto.setEmail(email);
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setUserState(UserState.ACTIVE);

        return dto;
    }
}