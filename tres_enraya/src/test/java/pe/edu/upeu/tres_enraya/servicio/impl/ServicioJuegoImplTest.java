package pe.edu.upeu.tres_enraya.servicio.impl;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import pe.edu.upeu.tres_enraya.modelo.Juego;
import pe.edu.upeu.tres_enraya.modelo.Jugador;
import pe.edu.upeu.tres_enraya.repositorio.RepositorioJuego;
import pe.edu.upeu.tres_enraya.repositorio.RepositorioJugador;

public class ServicioJuegoImplTest {

    @Mock
    private RepositorioJuego repositorioJuego;

    @Mock
    private RepositorioJugador repositorioJugador;

    @InjectMocks
    private ServicioJuegoImpl servicioJuego;

    private Juego juego;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Configuración inicial del juego
        juego = new Juego();
        juego.setEsJugadorUnico(false);
        juego.setNumeroPartidas(3);
        juego.setJugadorUno(new Jugador("Jugador 1"));
        juego.setJugadorDos(new Jugador("Jugador 2"));
    }

    @Test
    public void testCrearJuego() {
        // Dado que el repositorioJuego y repositorioJugador están simulados,
        // vamos a hacer que el método save retorne el juego que se crea
        when(repositorioJuego.save(any(Juego.class))).thenReturn(juego);
        when(repositorioJugador.save(any(Jugador.class))).thenReturn(new Jugador("Jugador 1"));

        Juego juegoCreado = servicioJuego.crearJuego(false, "Jugador 1", "Jugador 2", 3);

        assertNotNull(juegoCreado, "El juego no debe ser nulo.");
        assertEquals("Jugador 1", juegoCreado.getJugadorUno().getNombre(), "El nombre del Jugador 1 debe coincidir.");
        assertEquals("Jugador 2", juegoCreado.getJugadorDos().getNombre(), "El nombre del Jugador 2 debe coincidir.");
        assertEquals(3, juegoCreado.getNumeroPartidas(), "El número de partidas debe coincidir.");
    }

    /*@Test
    public void testCrearJuegoConJugadorUnico() {
        // Configurar para un solo jugador
        when(repositorioJuego.save(any(Juego.class))).thenReturn(juego);
        when(repositorioJugador.save(any(Jugador.class))).thenReturn(new Jugador("Jugador 1"));

        Juego juegoCreado = servicioJuego.crearJuego(true, "Jugador 1", null, 3);

        assertNotNull(juegoCreado, "El juego no debe ser nulo.");
        assertEquals("Jugador 1", juegoCreado.getJugadorUno().getNombre(), "El nombre del Jugador 1 debe coincidir.");
        assertEquals("Kaos", juegoCreado.getJugadorDos().getNombre(), "El nombre del Jugador 2 debe ser 'Kaos'.");
    }*/
}
