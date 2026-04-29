package mx.edu.cetys.software_quality_lab.users;

import mx.edu.cetys.software_quality_lab.users.exceptions.InvalidUserDataException;
import mx.edu.cetys.software_quality_lab.validators.EmailValidatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final EmailValidatorService emailValidatorService;

    public UserService(UserRepository userRepository, EmailValidatorService emailValidatorService) {
        this.userRepository = userRepository;
        this.emailValidatorService = emailValidatorService;
    }

    /**
     * Registrar un nuevo usuario aplicando todas las reglas de negocio.
     *
     * Reglas a implementar (lanzar InvalidUserDataException a menos que se indique):
     *  1. Username  — entre 5 y 20 caracteres, solo letras minúsculas, dígitos y guion bajo (_),
     *                 NO debe comenzar ni terminar con guion bajo
     *  2. First name — entre 2 y 50 caracteres, solo letras (se permiten acentos: á, é, ñ, etc.)
     *  3. Last name  — entre 2 y 50 caracteres, solo letras (se permiten acentos)
     *  4. Age        — debe ser mayor a 12 y menor o igual a 120
     *  5. Phone      — exactamente 10 dígitos, sin letras ni símbolos
     *  6. Email      — delegar a emailValidatorService.isValid(email);
     *                  lanzar InvalidUserDataException si regresa false
     *  7. Unicidad del username — si userRepository.existsByUsername regresa true,
     *                             lanzar DuplicateUsernameException
     */
    UserController.UserResponse registerUser(UserController.UserRequest request) {
        log.info("Iniciando registro de usuario, username={}", request.username());
        // TODO: implementar las reglas 1-7, luego guardar en BD y mapear la respuesta
        // Checar si esta mal el username con todas las reglas
        if (request.username() == null || request.username().isBlank() || request.username().length() > 20 || request.username().length() < 5 || !request.username().matches("^[a-zA-Z0-9_]$") || request.username().startsWith("_") || request.username().endsWith("_")) {
            throw new InvalidUserDataException("El nombre del usuario no es valido");
        }

        // checar si el firstName esta entre 2 a 50 caracteres y con restricciones validas
        if (request.firstName() == null || request.firstName().isBlank() || request.firstName().length() > 50 || request.firstName().length() < 2 || !request.firstName().matches("^[a-zA-Záéíóúñ]")){
            throw new InvalidUserDataException("El primer nombre no es valido")
        }
        // Lo mismo pero con el lastName
        if (request.lastName() == null || request.lastName().isBlank() || request.lastName().length() > 50 || request.lastName().length() < 2 || !request.lastName().matches("^[a-zA-Záéíóúñ]")){
            throw new InvalidUserDataException("El ultimo nombre no es valido")
        }

        // checar si la edad es mayor a 12 o igual o menor a 120
        if (request.age() < 12 || request.age() > 120){
            throw new InvalidUserDataException("La edad valida es solo entre 12 a 120")
        }

        // checar si el telefono es de exactamente 10 digitos
        if (request.phone().length() != 10 || !request.phone().matches("[0-9]+")){
            throw new InvalidUserDataException("El telefono no es valido")
        }
        if (!emailValidatorService.isValid(request.email())){
            throw new InvalidUserDataException("El correo ingresado no es aceptado")
        }
        if (userRepository.existsByUsername()){
            throw new DuplicateUserNameException("El nombre de usuario ya existe")
        }

        var newUser = userRepository.save(
                new User(request.username(), request.firstName(), request.lastName(), request.phone(), request.email(), request.age())
        )

        return mapToResponse(newUser);

    }

    /**
     * Buscar un usuario por ID.
     * Lanzar UserNotFoundException (HTTP 404) si el usuario no existe.
     */
    UserController.UserResponse getUserById(Long id) {
        log.info("Buscando usuario por ID, id={}", id);
        // TODO: buscar por id con findById, lanzar UserNotFoundException si está vacío, mapear y regresar

        if (id == null || id <= 0){
            throw new InvalidUserDataException("El ID del usuario debe ser un numero positivo")
        }

        var userFromDB = userRepository.findById(id);

        if (userFromDB.isEmpty()){
            throw new UserNotFoundException("User con id " + id + " no se ha encontrado");
        }

        return mapToResponse(userFromDB.get());

    }

    /**
     * Suspender un usuario ACTIVO.
     * Lanzar UserNotFoundException si el usuario no existe.
     * Lanzar InvalidUserDataException si el usuario ya está SUSPENDED.
     */
    UserController.UserResponse suspendUser(Long id) {
        log.info("Suspendiendo usuario, id={}", id);
        // TODO: buscar usuario, validar status, cambiar a SUSPENDED, guardar, mapear y regresar

        var userFromDB = userRepository.findById(id)

        if (userFromDB.isEmpty()) {
            throw new UserNotFoundException("User con id " + id + " no se ha encontrado");
        }

        var user = userFromDB.get();
        user.setStatus(true);
        var saved = userRepository.save(user);

        log.info("User marcado como suspendido, id = {}", saved.getId());

        return mapToResponse(saved);

    }

    private UserController.UserResponse mapToResponse(User user) {
        // TODO: mapear los campos de la Entity User al record UserController.UserResponse

        return new UserController.UserResponse(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getEmail,
                user.getAge(),
                user.getStatus()
        )
    }
}
