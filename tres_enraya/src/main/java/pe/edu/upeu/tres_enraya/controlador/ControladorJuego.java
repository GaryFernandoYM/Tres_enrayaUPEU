package pe.edu.upeu.tres_enraya.controlador;

import pe.edu.upeu.tres_enraya.modelo.Juego;
import pe.edu.upeu.tres_enraya.modelo.Partida;
import pe.edu.upeu.tres_enraya.modelo.TableroPosicion;
import pe.edu.upeu.tres_enraya.servicio.ServicioJuego;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/juegos")
public class ControladorJuego {

    private final ServicioJuego servicioJuego;

    public ControladorJuego(ServicioJuego servicioJuego) {
        this.servicioJuego = servicioJuego;
    }

    private static final String ESTADO_JUGANDO = "Jugando";
    private static final String VACIO = "VACIO";

    @PostMapping("/iniciar")
    public ResponseEntity<?> iniciarJuego(@Valid @RequestBody IniciarJuegoRequest request) {
        Juego juego = servicioJuego.crearJuego(
            request.isEsJugadorUnico(),
            request.getNombreJugadorUno(),
            request.getNombreJugadorDos(),
            request.getNumeroPartidas()
        );
        return ResponseEntity.ok(juego);
    }

    @PutMapping("/{juegoId}/movimiento")
    public ResponseEntity<?> hacerMovimiento(@PathVariable Long juegoId, @RequestParam int posicion) {
        return ResponseEntity.ok(servicioJuego.hacerMovimiento(juegoId, posicion));
    }

    @PutMapping("/{juegoId}/reiniciar")
    public ResponseEntity<?> reiniciarJuego(@PathVariable Long juegoId) {
        servicioJuego.reiniciarJuego(juegoId);
        return ResponseEntity.ok("Juego y partidas reiniciados con éxito.");
    }

    @PutMapping("/{juegoId}/anular")
    public ResponseEntity<?> anularJuego(@PathVariable Long juegoId) {
        servicioJuego.anularJuego(juegoId);
        return ResponseEntity.ok("Juego anulado.");
    }

    @GetMapping("/{juegoId}")
    public ResponseEntity<?> obtenerEstadoJuego(@PathVariable Long juegoId) {
        Juego juego = servicioJuego.obtenerJuegoPorId(juegoId);

        Optional<Partida> partidaActual = juego.getPartidas().stream()
            .filter(partida -> ESTADO_JUGANDO.equals(partida.getEstado()))
            .findFirst();

        if (partidaActual.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay partida activa en este juego.");
        }

        Map<Integer, String> estadoTablero = partidaActual.get().getTablero().stream()
            .collect(Collectors.toMap(
                TableroPosicion::getIndice,
                pos -> pos.getNombreJugador() != null ? pos.getNombreJugador() : VACIO
            ));

        return ResponseEntity.ok(Map.of(
            "id", juego.getId(),
            "estado", juego.getEstado(),
            "puntajeJugadorUno", juego.getPuntajeJugadorUno(),
            "puntajeJugadorDos", juego.getPuntajeJugadorDos(),
            "ganador", juego.getGanador() != null ? juego.getGanador().getNombre() : null,
            "esJugadorUnico", juego.getEsJugadorUnico(),
            "turnoActual", partidaActual.get().getTurnoActual(),
            "tablero", estadoTablero,
            "fechaCreacion", juego.getFechaCreacion()
        ));
    }
}
