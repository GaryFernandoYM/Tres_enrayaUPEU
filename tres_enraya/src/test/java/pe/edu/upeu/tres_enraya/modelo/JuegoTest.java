package pe.edu.upeu.tres_enraya.modelo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JuegoTest {

    private Juego juego;

    @BeforeEach
    void setUp() {
        juego = new Juego();
    }

    @Test
    void testJuegoCreacionValoresPorDefecto() {
        // Validar que los valores iniciales sean correctos
        assertNotNull(juego.getEstado(), "El estado debería estar inicializado.");
        assertEquals("Jugando", juego.getEstado(), "El estado inicial debería ser 'Jugando'.");
        assertEquals(0, juego.getPuntajeJugadorUno(), "El puntaje del jugador uno debería ser 0.");
        assertEquals(0, juego.getPuntajeJugadorDos(), "El puntaje del jugador dos debería ser 0.");
        assertNotNull(juego.getFechaCreacion(), "La fecha de creación no debería ser nula.");
        assertNotNull(juego.getPartidas(), "La lista de partidas no debería ser nula.");
        assertTrue(juego.getPartidas().isEmpty(), "La lista de partidas debería estar vacía al inicio.");
    }

    @Test
    void testAgregarPartida() {
        // Crear una instancia de Partida
        Partida partida = new Partida();
        partida.setId(1L);

        // Agregar la partida al juego
        juego.agregarPartida(partida);

        // Validar que la partida se haya agregado correctamente
        assertEquals(1, juego.getPartidas().size(), "Debería haber una partida en la lista.");
        assertTrue(juego.getPartidas().contains(partida), "La partida agregada debería estar en la lista.");
        assertEquals(juego, partida.getJuego(), "La partida debería estar asociada al juego actual.");
    }

    @Test
    void testSetGanador() {
        // Crear una instancia de Jugador
        Jugador ganador = new Jugador();
        ganador.setId(1L);
        ganador.setNombre("Jugador 1");

        // Establecer el ganador en el juego
        juego.setGanador(ganador);

        // Validar que el ganador se estableció correctamente
        assertNotNull(juego.getGanador(), "El ganador no debería ser nulo.");
        assertEquals(ganador, juego.getGanador(), "El ganador debería ser 'Jugador 1'.");
    }

    @Test
    void testEsJugadorUnico() {
        // Establecer el valor de 'esJugadorUnico'
        juego.setEsJugadorUnico(true);

        // Validar que el valor se estableció correctamente
        assertTrue(juego.getEsJugadorUnico(), "El juego debería ser de un solo jugador.");
    }
}
