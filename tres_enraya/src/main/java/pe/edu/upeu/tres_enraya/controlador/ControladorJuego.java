package pe.edu.upeu.tres_enraya.controlador;

import pe.edu.upeu.tres_enraya.modelo.Juego;
import pe.edu.upeu.tres_enraya.modelo.Partida;
import pe.edu.upeu.tres_enraya.modelo.TableroPosicion;
import pe.edu.upeu.tres_enraya.servicio.ServicioJuego;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

import java.util.HashMap;
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
        try {
            Juego juego = servicioJuego.crearJuego(
                request.isEsJugadorUnico(),
                request.getNombreJugadorUno(),
                request.getNombreJugadorDos(),
                request.getNumeroPartidas()
            );
            return ResponseEntity.ok(juego);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocurrió un error inesperado al iniciar el juego.");
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ocurrió un error inesperado: " + ex.getMessage());
    }

    @PutMapping("/{juegoId}/movimiento")
    public ResponseEntity<?> hacerMovimiento(@PathVariable Long juegoId, @RequestParam int posicion) {
        try {
            if (posicion < 0 || posicion > 8) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("La posición debe estar entre 0 y 8.");
            }

            Juego juegoActualizado = servicioJuego.hacerMovimiento(juegoId, posicion);

            return ResponseEntity.ok(juegoActualizado);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocurrió un error inesperado al realizar el movimiento.");
        }
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
        try {
            // Obtener el juego
            Juego juego = servicioJuego.obtenerJuegoPorId(juegoId);

            // Buscar la partida activa
            Optional<Partida> partidaActual = juego.getPartidas().stream()
                .filter(partida -> ESTADO_JUGANDO.equals(partida.getEstado()))
                .findFirst();

            // Si no se encuentra una partida activa, devolver un mensaje adecuado
            if (partidaActual.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay partida activa en este juego.");
            }

            // Mapear el estado del tablero (vacío o con el nombre del jugador)
            Map<Integer, String> estadoTablero = partidaActual.get().getTablero().stream()
                .collect(Collectors.toMap(
                    TableroPosicion::getIndice,
                    pos -> pos.getNombreJugador() != null ? pos.getNombreJugador() : VACIO
                ));

            // Crear la respuesta con la información del juego
            Map<String, Object> response = new HashMap<>();
            response.put("id", juego.getId());
            response.put("estado", juego.getEstado());
            response.put("puntajeJugadorUno", juego.getPuntajeJugadorUno());
            response.put("puntajeJugadorDos", juego.getPuntajeJugadorDos());
            response.put("ganador", juego.getGanador() != null ? juego.getGanador().getNombre() : null);
            response.put("esJugadorUnico", juego.getEsJugadorUnico());
            response.put("turnoActual", partidaActual.get().getTurnoActual());
            response.put("tablero", estadoTablero);
            response.put("fechaCreacion", juego.getFechaCreacion());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // En caso de error con la obtención del juego (si no se encuentra o algún otro problema)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Juego no encontrado.");
        }
    }
}
