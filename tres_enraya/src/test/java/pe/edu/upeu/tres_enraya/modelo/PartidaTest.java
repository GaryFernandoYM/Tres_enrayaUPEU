package pe.edu.upeu.tres_enraya.modelo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class PartidaTest {

    private Partida partida;
    private Juego juego;
    private Jugador jugadorUno;
    private Jugador jugadorDos;

    @BeforeEach
    void setUp() {
        // Configurar un juego con jugadores
        jugadorUno = new Jugador(1L, "Jugador 1");
        jugadorDos = new Jugador(2L, "Jugador 2");

        juego = new Juego();
        juego.setId(1L);
        juego.setJugadorUno(jugadorUno);
        juego.setJugadorDos(jugadorDos);

        // Crear una nueva partida asociada al juego
        partida = new Partida();
        partida.setId(1L);
        partida.setJuego(juego);
        partida.setTurnoActual(jugadorUno.getNombre());
        partida.setTablero(new HashSet<>());
    }

    @Test
    void testPartidaInicializacion() {
        // Verificar los valores iniciales de la partida
        assertEquals("Jugando", partida.getEstado(), "El estado inicial debería ser 'Jugando'.");
        assertEquals(jugadorUno.getNombre(), partida.getTurnoActual(), "El turno inicial debería ser del jugador 1.");
        assertNull(partida.getGanador(), "El ganador debería ser nulo al iniciar la partida.");
        assertEquals(0, partida.getPuntajeJugadorUno(), "El puntaje inicial del jugador 1 debería ser 0.");
        assertEquals(0, partida.getPuntajeJugadorDos(), "El puntaje inicial del jugador 2 debería ser 0.");
        assertTrue(partida.getTablero().isEmpty(), "El tablero debería estar vacío al iniciar.");
    }

    @Test
    void testReiniciarPartida() {
        // Configurar un estado de la partida antes del reinicio
        partida.setEstado("Terminado");
        partida.setGanador(jugadorUno);
        partida.setPuntajeJugadorUno(3);
        partida.setPuntajeJugadorDos(2);
        partida.setTurnoActual(jugadorDos.getNombre());

        // Reiniciar la partida
        partida.reiniciarPartida();

        // Verificar que se hayan restablecido los valores iniciales
        assertEquals("Jugando", partida.getEstado(), "El estado debería restablecerse a 'Jugando'.");
        assertNull(partida.getGanador(), "El ganador debería restablecerse a nulo.");
        assertEquals(0, partida.getPuntajeJugadorUno(), "El puntaje del jugador 1 debería restablecerse a 0.");
        assertEquals(0, partida.getPuntajeJugadorDos(), "El puntaje del jugador 2 debería restablecerse a 0.");
        assertEquals(jugadorUno.getNombre(), partida.getTurnoActual(), "El turno inicial debería ser del jugador 1.");
        assertTrue(partida.getTablero().stream().allMatch(pos -> pos.getNombreJugador() == null),
                "Todas las posiciones del tablero deberían restablecerse a nulo.");
    }

    @Test
    void testAsignarGanador() {
        // Asignar un ganador a la partida
        partida.setGanador(jugadorDos);

        // Verificar que el ganador haya sido asignado correctamente
        assertEquals(jugadorDos, partida.getGanador(), "El ganador debería ser el jugador 2.");
    }

    @Test
    void testRelacionConJuego() {
        // Verificar que la partida esté correctamente asociada al juego
        assertEquals(juego, partida.getJuego(), "La partida debería estar asociada al juego especificado.");
        assertEquals(juego.getId(), partida.getJuego().getId(), "El ID del juego debería coincidir.");
    }

    @Test
    void testIgualdadDePartidas() {
        // Crear otra partida con el mismo ID
        Partida otraPartida = new Partida();
        otraPartida.setId(1L);

        // Verificar igualdad basada en el ID
        assertEquals(partida, otraPartida, "Dos partidas con el mismo ID deberían ser iguales.");
    }

    @Test
    void testDiferenciaDePartidas() {
        // Crear otra partida con un ID diferente
        Partida otraPartida = new Partida();
        otraPartida.setId(2L);

        // Verificar que las partidas sean diferentes
        assertNotEquals(partida, otraPartida, "Dos partidas con IDs diferentes no deberían ser iguales.");
    }
}
