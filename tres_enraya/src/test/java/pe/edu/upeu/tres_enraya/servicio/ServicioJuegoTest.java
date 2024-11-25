package pe.edu.upeu.tres_enraya.servicio;

import pe.edu.upeu.tres_enraya.modelo.Juego;
import pe.edu.upeu.tres_enraya.modelo.Jugador;
import pe.edu.upeu.tres_enraya.repositorio.RepositorioJuego;
import pe.edu.upeu.tres_enraya.repositorio.RepositorioJugador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pe.edu.upeu.tres_enraya.servicio.impl.ServicioJuegoImpl;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ServicioJuegoTest {

    @Mock
    private RepositorioJuego repositorioJuego;

    @Mock
    private RepositorioJugador repositorioJugador;

    @InjectMocks
    private ServicioJuegoImpl servicioJuego;

    private Juego juego;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        juego = new Juego();
        juego.setId(1L);
        juego.setEsJugadorUnico(false);
        juego.setNumeroPartidas(3);

        Jugador jugadorUno = new Jugador("Jugador1");
        Jugador jugadorDos = new Jugador("Jugador2");

        juego.setJugadorUno(jugadorUno);
        juego.setJugadorDos(jugadorDos);
    }

    @Test
    void testCrearJuego() {
        when(repositorioJuego.save(any(Juego.class))).thenReturn(juego);
        when(repositorioJugador.save(any(Jugador.class))).thenReturn(new Jugador("Jugador1"));

        Juego juegoCreado = servicioJuego.crearJuego(false, "Jugador1", "Jugador2", 3);

        assertNotNull(juegoCreado);
        assertEquals(3, juegoCreado.getNumeroPartidas());
        assertEquals("Jugador1", juegoCreado.getJugadorUno().getNombre());
        assertEquals("Jugador2", juegoCreado.getJugadorDos().getNombre());
        verify(repositorioJuego, times(1)).save(any(Juego.class));
    }

    @Test
    void testObtenerJuegoPorId() {
        when(repositorioJuego.findById(1L)).thenReturn(java.util.Optional.of(juego));

        Juego juegoRecuperado = servicioJuego.obtenerJuegoPorId(1L);

        assertNotNull(juegoRecuperado);
        assertEquals(1L, juegoRecuperado.getId());
        verify(repositorioJuego, times(1)).findById(1L);
    }

    /*@Test
    void testActualizarEstadoJuego() {
        when(repositorioJuego.findById(1L)).thenReturn(java.util.Optional.of(juego));

        String nuevoEstado = "Ganado";
        String ganador = "Jugador1";
        int puntaje = 1;

        when(repositorioJugador.findByNombre(ganador)).thenReturn(java.util.Optional.of(new Jugador(ganador)));

        Juego juegoActualizado = servicioJuego.actualizarEstadoJuego(1L, nuevoEstado, ganador, puntaje);

        assertNotNull(juegoActualizado);
        assertEquals(nuevoEstado, juegoActualizado.getEstado());
        assertEquals(1, juegoActualizado.getPuntajeJugadorUno());
        verify(repositorioJuego, times(1)).save(any(Juego.class));
    }*/

    @Test
    void testAnularJuego() {
        when(repositorioJuego.findById(1L)).thenReturn(java.util.Optional.of(juego));

        servicioJuego.anularJuego(1L);

        assertEquals("Anulado", juego.getEstado());
        assertEquals(0, juego.getPuntajeJugadorUno());
        assertEquals(0, juego.getPuntajeJugadorDos());
        verify(repositorioJuego, times(1)).save(any(Juego.class));
    }

    @Test
    void testReiniciarJuego() {
        when(repositorioJuego.findById(1L)).thenReturn(java.util.Optional.of(juego));

        servicioJuego.reiniciarJuego(1L);

        assertEquals("Jugando", juego.getEstado());
        assertEquals(0, juego.getPuntajeJugadorUno());
        assertEquals(0, juego.getPuntajeJugadorDos());
        assertNull(juego.getGanador());
        verify(repositorioJuego, times(1)).save(any(Juego.class));
    }
}
