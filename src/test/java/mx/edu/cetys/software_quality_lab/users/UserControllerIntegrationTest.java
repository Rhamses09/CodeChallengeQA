package mx.edu.cetys.software_quality_lab.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    // Limpiar la BD antes de cada prueba para garantizar un estado independiente
    @BeforeEach
    public void limpiarBD() {
        userRepository.deleteAll();
    }

    // ─── POST /users ──────────────────────────────────────────────────────────

    @Test
    void shouldCreateUserAndReturn201() throws Exception {
        // El email sigue el formato del EmailValidatorService: usuario#proveedor.dominio
        String body = """
                {
                    "username": "rhamses_noob67",
                    "firstName": "Rhamses",
                    "lastName": "Orozco",
                    "phone": "6464000031",
                    "email": "mepic4n#gmil.com",
                    "age": 13,
                    "status": "ACTIVE"
                }""";

        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isCreated())                                    // HTTP 201
                .andExpect(jsonPath("$.info").value("User creado exitosamente"))
                .andExpect(jsonPath("$.response.user.username").value("rhamses_noob67"))
                .andExpect(jsonPath("$.response.user.firstName").value("Rhamses"))
                .andExpect(jsonPath("$.response.user.lastName").value("Orozco"))
                .andExpect(jsonPath("$.response.user.phone").value("6464000031"))
                .andExpect(jsonPath("$.response.user.email").value("mepic4n#gmil.com"))
                .andExpect(jsonPath("$.response.user.age").value(13))
                .andExpect(jsonPath("$.response.user.status").value("ACTIVE"))
                .andExpect(jsonPath("$.response.user.id").isNumber())                // el ID fue generado por la BD
                .andExpect(jsonPath("$.error").isEmpty());
    }

    @Test
    void shouldReturn400WhenUsernameIsTooShort() throws Exception {
        String body = """
                {
                    "username": "rham",
                    "firstName": "Rhamses",
                    "lastName": "Orozco",
                    "phone": "6464000031",
                    "email": "mepic4n#gmil.com",
                    "age": 13,
                    "status": "ACTIVE"
                }""";

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())                                 // HTTP 400
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void shouldReturn400WhenAgeIsExactlyTwelve() throws Exception {
        String body = """
                {
                    "username": "rhamses_noob67",
                    "firstName": "Rhamses",
                    "lastName": "Orozco",
                    "phone": "6464000031",
                    "email": "mepic4n#gmil.com",
                    "age": 12,
                    "status": "ACTIVE"
                }""";

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())                                 // HTTP 400
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void shouldReturn400WhenPhoneIsInvalid() throws Exception {
        String body = """
                {
                    "username": "rhamses_noob67",
                    "firstName": "Rhamses",
                    "lastName": "Orozco",
                    "phone": "123456789",
                    "email": "mepic4n#gmil.com",
                    "age": 13,
                    "status": "ACTIVE"
                }""";

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())                                 // HTTP 400
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void shouldReturn400WhenEmailIsInvalid() throws Exception {
        String body = """
                {
                    "username": "rhamses_noob67",
                    "firstName": "Rhamses",
                    "lastName": "Orozco",
                    "phone": "6464000031",
                    "email": "mepic4n#gmil.com",
                    "age": 13,
                    "status": "ACTIVE"
                }""";

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())                                 // HTTP 400
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void shouldReturn409WhenUsernameIsDuplicated() throws Exception {
        // TODO: guardar un usuario directamente via repository con el mismo username
        // TODO: realizar segundo POST /users con el mismo username
        // TODO: andExpect status 409
    }

    // ─── GET /users/{id} ─────────────────────────────────────────────────────

    @Test
    void shouldReturn200AndUserWhenFound() throws Exception {
        }

    @Test
    void shouldReturn404WhenUserNotFound() throws Exception {
        // TODO: realizar GET /users/9999 (id inexistente)
        // TODO: andExpect status 404
    }

    // ─── PATCH /users/{id}/suspend ────────────────────────────────────────────

    @Test
    void shouldSuspendUserAndReturn200() throws Exception {
        // TODO: guardar un usuario ACTIVE via repository
        // TODO: realizar PATCH /users/{id}/suspend
        // TODO: andExpect status 200
        // TODO: andExpect jsonPath("$.response.user.status") == "SUSPENDED"
    }

    @Test
    void shouldReturn400WhenSuspendingAlreadySuspendedUser() throws Exception {
        // TODO: guardar un usuario con status SUSPENDED via repository
        // TODO: realizar PATCH /users/{id}/suspend
        // TODO: andExpect status 400
    }
}
