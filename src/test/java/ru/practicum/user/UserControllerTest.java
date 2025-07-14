package ru.practicum.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController controller;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private UserDto userDto;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
        userDto = new UserDto(
                1L,
                "John",
                "Doe",
                "john.doe@mail.com",
                "2022.07.03 19:55:00",
                UserState.ACTIVE);
    }

    @Test
    void saveNewUser() throws Exception {
        when(userService.saveUser(any())).thenReturn(userDto);

        mvc.perform(post("/users")
                .content(mapper.writeValueAsString(userDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.firstName", is(userDto.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(userDto.getLastName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void getAllUsers() throws Exception {
        List<UserDto> users = List.of(
                new UserDto(1L, "John", "Doe", "john.doe@mail.com", "2022.07.03 19:55:00", UserState.ACTIVE),
                new UserDto(2L, "Jane", "Smith", "jane.smith@mail.com", "2022.07.04 09:00:00", UserState.ACTIVE)
        );

        when(userService.getAllUsers()).thenReturn(users);

        mvc.perform(get("/users")
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(users.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].firstName", is(users.get(0).getFirstName())))
                .andExpect(jsonPath("$[0].lastName", is(users.get(0).getLastName())))
                .andExpect(jsonPath("$[0].email", is(users.get(0).getEmail())))
                .andExpect(jsonPath("$[1].id", is(users.get(1).getId()), Long.class))
                .andExpect(jsonPath("$[1].firstName", is(users.get(1).getFirstName())))
                .andExpect(jsonPath("$[1].lastName", is(users.get(1).getLastName())))
                .andExpect(jsonPath("$[1].email", is(users.get(1).getEmail())));
    }
}
