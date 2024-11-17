package pe.edu.upeu.tres_enraya.controlador;

import pe.edu.upeu.tres_enraya.modelo.Juego;
import pe.edu.upeu.tres_enraya.modelo.Partida;
import pe.edu.upeu.tres_enraya.modelo.TableroPosicion;
import pe.edu.upeu.tres_enraya.servicio.ServicioJuego;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/juegos")
public class ControladorJuego {

    @Autowired
    private ServicioJuego servicioJuego;

    @PostMapping("/iniciar")
    public ResponseEntity<?> iniciarJuego(
            @RequestParam boolean esJugadorUnico,
            @RequestParam String nombreJugadorUno,
            @RequestParam(required = false) String nombreJugadorDos,
            @RequestParam int numeroPartidas) {  // Añadimos el parámetro de número de partidas
        
        if (nombreJugadorUno == null || nombreJugadorUno.length() < 2 || nombreJugadorUno.length() > 50) {
            return ResponseEntity.badRequest().body("El nombre del Jugador Uno debe tener entre 2 y 50 caracteres.");
        }
        if (!esJugadorUnico && (nombreJugadorDos == null || nombreJugadorDos.length() < 2 || nombreJugadorDos.length() > 50)) {
            return ResponseEntity.badRequest().body("El nombre del Jugador Dos debe tener entre 2 y 50 caracteres.");
        }
        
        Juego juego = servicioJuego.crearJuego(esJugadorUnico, nombreJugadorUno, nombreJugadorDos, numeroPartidas);  // Añadimos numeroPartidas
        return ResponseEntity.ok(juego);
    }

    @PutMapping("/{juegoId}/movimiento")
    public ResponseEntity<?> hacerMovimiento(@PathVariable Long juegoId, @RequestParam int posicion) {
        try {
            return ResponseEntity.ok(servicioJuego.hacerMovimiento(juegoId, posicion));
        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            if ("Juego ya ha terminado.".equals(errorMessage)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Movimiento inválido: el juego ya ha terminado.");
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
        }
    }
    @PutMapping("/{juegoId}/reiniciar")
    public ResponseEntity<?> reiniciarJuego(@PathVariable Long juegoId) {
        try {
            servicioJuego.reiniciarJuego(juegoId);
            return ResponseEntity.ok("Juego y partidas reiniciados con éxito.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    
    @PutMapping("/{juegoId}/anular")
    public ResponseEntity<?> anularJuego(@PathVariable Long juegoId) {
        try {
            servicioJuego.anularJuego(juegoId);
            return ResponseEntity.ok("Juego anulado.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    

    @GetMapping("/{juegoId}")
    public ResponseEntity<?> obtenerEstadoJuego(@PathVariable Long juegoId) {
        try {
            Juego juego = servicioJuego.obtenerJuegoPorId(juegoId);

            // Obtener la partida actual
            Optional<Partida> partidaActual = juego.getPartidas().stream()
                    .filter(partida -> "Jugando".equals(partida.getEstado()))
                    .findFirst();

            if (partidaActual.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay partida activa en este juego.");
            }
// Mapear el tablero de la partida actual
Map<Integer, String> estadoTablero = partidaActual.get().getTablero().stream()
    .collect(Collectors.toMap(
        TableroPosicion::getIndice,
        pos -> pos.getNombreJugador() != null ? pos.getNombreJugador() : "VACIO"
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
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
