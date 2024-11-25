package pe.edu.upeu.tres_enraya.servicio.impl;

import org.junit.jupiter.api.Test;
import pe.edu.upeu.tres_enraya.modelo.Juego;
import pe.edu.upeu.tres_enraya.modelo.Jugador;
import pe.edu.upeu.tres_enraya.modelo.Partida;
import pe.edu.upeu.tres_enraya.modelo.TableroPosicion;
import pe.edu.upeu.tres_enraya.repositorio.RepositorioJuego;
import pe.edu.upeu.tres_enraya.repositorio.RepositorioJugador;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ServicioJuegoImplTest {

    private final RepositorioJuego repositorioJuego = mock(RepositorioJuego.class);
    private final RepositorioJugador repositorioJugador = mock(RepositorioJugador.class);
    private final ServicioJuegoImpl servicioJuego = new ServicioJuegoImpl(repositorioJuego, repositorioJugador);

    @Test
    void testCrearJuego() {
        Jugador jugadorUno = new Jugador("Jugador1");
        Jugador jugadorDos = new Jugador("Jugador2");

        when(repositorioJugador.save(any(Jugador.class))).thenReturn(jugadorUno, jugadorDos);
        when(repositorioJuego.save(any(Juego.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Juego juego = servicioJuego.crearJuego(false, "Jugador1", "Jugador2", 3);

        assertNotNull(juego);
        assertEquals("Jugador1", juego.getJugadorUno().getNombre());
        assertEquals("Jugador2", juego.getJugadorDos().getNombre());
        assertEquals(3, juego.getNumeroPartidas());
        assertEquals("Jugando", juego.getEstado());
        verify(repositorioJugador, times(2)).save(any(Jugador.class));
        verify(repositorioJuego, times(1)).save(any(Juego.class));
    }

    @Test
    void testHacerMovimiento() {
        Juego mockJuego = crearMockJuego("Jugador1", "Jugador2");
        Partida partida = mockJuego.getPartidas().iterator().next();
        partida.setTurnoActual("Jugador1");

        when(repositorioJuego.findById(1L)).thenReturn(Optional.of(mockJuego));
        when(repositorioJuego.save(any(Juego.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Juego resultado = servicioJuego.hacerMovimiento(1L, 0);

        assertNotNull(resultado);
        assertTrue(resultado.getPartidas().stream()
                .flatMap(p -> p.getTablero().stream())
                .anyMatch(pos -> pos.getIndice() == 0 && "Jugador1".equals(pos.getNombreJugador())));
        verify(repositorioJuego, times(1)).findById(1L);
        verify(repositorioJuego, times(1)).save(any(Juego.class));
    }

    @Test
    void testReiniciarJuego() {
        Juego mockJuego = crearMockJuego("Jugador1", "Jugador2");

        when(repositorioJuego.findById(1L)).thenReturn(Optional.of(mockJuego));

        servicioJuego.reiniciarJuego(1L);

        assertEquals("Jugando", mockJuego.getEstado());
        assertEquals(0, mockJuego.getPuntajeJugadorUno());
        assertEquals(0, mockJuego.getPuntajeJugadorDos());
        assertTrue(mockJuego.getPartidas().stream().allMatch(p -> "Jugando".equals(p.getEstado())));
        assertTrue(mockJuego.getPartidas().stream().allMatch(p -> p.getTablero().stream()
                .allMatch(pos -> pos.getNombreJugador() == null)));

        verify(repositorioJuego, times(1)).findById(1L);
        verify(repositorioJuego, times(1)).save(mockJuego);
    }

   
    @Test
    void testHacerMovimientoVsMaquina() {
        Juego mockJuego = crearMockJuego("Jugador1", "Kaos");
        mockJuego.setEsJugadorUnico(true);
        Partida partida = mockJuego.getPartidas().iterator().next();
        partida.setTurnoActual("Jugador1");

        when(repositorioJuego.findById(1L)).thenReturn(Optional.of(mockJuego));
        when(repositorioJuego.save(any(Juego.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Juego resultado = servicioJuego.hacerMovimiento(1L, 0);

    assertNotNull(resultado);

    assertTrue(resultado.getPartidas().stream()
            .flatMap(p -> p.getTablero().stream())
            .anyMatch(pos -> pos.getIndice() == 0 && "Jugador1".equals(pos.getNombreJugador())));

    assertTrue(resultado.getPartidas().stream()
            .flatMap(p -> p.getTablero().stream())
            .anyMatch(pos -> pos.getNombreJugador() != null && !"Jugador1".equals(pos.getNombreJugador())));

    verify(repositorioJuego, times(1)).findById(1L);
    verify(repositorioJuego, times(1)).save(any(Juego.class));
}


    @Test
    void testAnularJuego() {
        Juego mockJuego = crearMockJuego("Jugador1", "Jugador2");

        when(repositorioJuego.findById(1L)).thenReturn(Optional.of(mockJuego));

        servicioJuego.anularJuego(1L);

        assertEquals("Anulado", mockJuego.getEstado());
        assertEquals(0, mockJuego.getPuntajeJugadorUno());
        assertEquals(0, mockJuego.getPuntajeJugadorDos());
        verify(repositorioJuego, times(1)).findById(1L);
        verify(repositorioJuego, times(1)).save(mockJuego);
    }

    private Juego crearMockJuego(String jugadorUnoNombre, String jugadorDosNombre) {
    Juego mockJuego = new Juego();
    mockJuego.setId(1L);
    mockJuego.setEstado("Jugando");
    mockJuego.setEsJugadorUnico(false);

    Jugador jugadorUno = new Jugador(jugadorUnoNombre);
    Jugador jugadorDos = new Jugador(jugadorDosNombre);

    mockJuego.setJugadorUno(jugadorUno);
    mockJuego.setJugadorDos(jugadorDos);

    Partida partida = new Partida();
    partida.setEstado("Jugando");
    partida.setJuego(mockJuego);

    Set<TableroPosicion> mockTablero = new HashSet<>();
    for (int i = 0; i < 9; i++) {
        TableroPosicion pos = new TableroPosicion();
        pos.setIndice(i);
        pos.setNombreJugador(null);
        pos.setPartida(partida);
        mockTablero.add(pos);
    }
    partida.setTablero(mockTablero);

    mockJuego.setPartidas(Set.of(partida));
    return mockJuego;
}

}
