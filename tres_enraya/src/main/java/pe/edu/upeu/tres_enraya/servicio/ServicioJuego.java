package pe.edu.upeu.tres_enraya.servicio;

import pe.edu.upeu.tres_enraya.modelo.Juego;

public interface ServicioJuego {
    Juego crearJuego(boolean esJugadorUnico, String nombreJugadorUno, String nombreJugadorDos, int numeroPartidas);
    Juego hacerMovimiento(Long juegoId, int posicion);
    Juego actualizarEstadoJuego(Long juegoId, String estado, String ganador, Integer puntaje);
    void anularJuego(Long juegoId);
    void reiniciarJuego(Long juegoId);
    Juego obtenerJuegoPorId(Long juegoId);
}