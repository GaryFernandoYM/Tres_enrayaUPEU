package pe.edu.upeu.tres_enraya.modelo;

import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JugadorTest {

    private final Validator validator;

    // Inicializamos el validador para las pruebas de restricciones
    public JugadorTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    void testJugadorCreacionConNombreValido() {
        // Crear un jugador con un nombre válido
        Jugador jugador = new Jugador("Carlos");

        // Validar que el nombre sea el esperado
        assertNotNull(jugador.getNombre(), "El nombre no debería ser nulo.");
        assertEquals("Carlos", jugador.getNombre(), "El nombre debería ser 'Carlos'.");
    }

    @Test
    void testJugadorCreacionSinNombre() {
        // Crear un jugador sin nombre
        Jugador jugador = new Jugador();

        // Validar restricciones de Bean Validation
        Set<ConstraintViolation<Jugador>> violations = validator.validate(jugador);

        // Debería haber una violación por el campo "nombre" nulo
        assertFalse(violations.isEmpty(), "Debería haber restricciones violadas.");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("El nombre es obligatorio")),
                "El mensaje de error debería indicar que el nombre es obligatorio.");
    }

    @Test
    void testJugadorCreacionNombreCorto() {
        // Crear un jugador con un nombre demasiado corto
        Jugador jugador = new Jugador("A");

        // Validar restricciones de Bean Validation
        Set<ConstraintViolation<Jugador>> violations = validator.validate(jugador);

        // Debería haber una violación por el tamaño del nombre
        assertFalse(violations.isEmpty(), "Debería haber restricciones violadas.");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("El nombre debe tener entre 2 y 50 caracteres")),
                "El mensaje de error debería indicar que el nombre es demasiado corto.");
    }

    @Test
    void testJugadorCreacionNombreLargo() {
        // Crear un jugador con un nombre demasiado largo
        String nombreLargo = "A".repeat(51); // Nombre de 51 caracteres
        Jugador jugador = new Jugador(nombreLargo);

        // Validar restricciones de Bean Validation
        Set<ConstraintViolation<Jugador>> violations = validator.validate(jugador);

        // Debería haber una violación por el tamaño del nombre
        assertFalse(violations.isEmpty(), "Debería haber restricciones violadas.");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("El nombre debe tener entre 2 y 50 caracteres")),
                "El mensaje de error debería indicar que el nombre es demasiado largo.");
    }

    @Test
    void testJugadorIdInicialmenteNulo() {
        // Crear un jugador
        Jugador jugador = new Jugador("Carlos");

        // Validar que el ID sea inicialmente nulo (antes de ser persistido)
        assertNull(jugador.getId(), "El ID debería ser nulo antes de persistir el jugador.");
    }
}
