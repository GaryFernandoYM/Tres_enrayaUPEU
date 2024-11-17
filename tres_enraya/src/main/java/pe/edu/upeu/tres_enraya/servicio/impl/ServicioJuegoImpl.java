package pe.edu.upeu.tres_enraya.servicio.impl;

import pe.edu.upeu.tres_enraya.modelo.Juego;
import pe.edu.upeu.tres_enraya.modelo.Jugador;
import pe.edu.upeu.tres_enraya.modelo.Partida;
import pe.edu.upeu.tres_enraya.modelo.TableroPosicion;
import pe.edu.upeu.tres_enraya.repositorio.RepositorioJuego;
import pe.edu.upeu.tres_enraya.repositorio.RepositorioJugador;
import pe.edu.upeu.tres_enraya.servicio.ServicioJuego;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Random;


@Service
public class ServicioJuegoImpl implements ServicioJuego {

    @Autowired
    private RepositorioJuego repositorioJuego;

    @Autowired
    private RepositorioJugador repositorioJugador;

    private final Random random = new Random();

    @Override
public Juego crearJuego(boolean esJugadorUnico, String nombreJugadorUno, String nombreJugadorDos, int numeroPartidas) {
    Juego juego = new Juego();
    juego.setEsJugadorUnico(esJugadorUnico);
    juego.setNumeroPartidas(numeroPartidas);

    Jugador jugadorUno = new Jugador(nombreJugadorUno);
    repositorioJugador.save(jugadorUno);
    juego.setJugadorUno(jugadorUno);

    Jugador jugadorDos = esJugadorUnico ? new Jugador("Kaos") : new Jugador(nombreJugadorDos);
    repositorioJugador.save(jugadorDos);
    juego.setJugadorDos(jugadorDos);

    // Crea partidas sin reutilizar IDs persistentes
    for (int i = 0; i < numeroPartidas; i++) {
        Partida partida = new Partida();
        partida.setTurnoActual(jugadorUno.getNombre());
        Set<TableroPosicion> posiciones = new HashSet<>();
        
        for (int j = 0; j < 9; j++) {
            TableroPosicion posicion = new TableroPosicion();
            posicion.setIndice(j);
            posicion.setPartida(partida); // Establece referencia bidireccional
            posiciones.add(posicion);
        }
        
        partida.setTablero(posiciones);
        partida.setJuego(juego);  // Asocia la partida con el juego para persistencia
        juego.agregarPartida(partida);  // Asegura el uso de una colección mutable para agregar
    }

    return repositorioJuego.save(juego);
}
@Override
public Juego hacerMovimiento(Long juegoId, int posicion) {
    Juego juego = obtenerJuegoPorId(juegoId);
    Partida partidaActual = obtenerPartidaActual(juego);

    if (partidaActual == null || !"Jugando".equals(partidaActual.getEstado())) {
        throw new RuntimeException("No hay partida activa.");
    }

    // Ordena las posiciones en el tablero para garantizar el acceso correcto por índice
    List<TableroPosicion> posicionesList = new ArrayList<>(partidaActual.getTablero());
    posicionesList.sort(Comparator.comparingInt(TableroPosicion::getIndice));

    TableroPosicion posicionTablero = posicionesList.get(posicion);
    if (posicionTablero.getNombreJugador() != null) {
        throw new RuntimeException("Posición ya ocupada.");
    }

    // Identificar al jugador en turno
    Jugador jugadorEnTurno = partidaActual.getTurnoActual().equals(juego.getJugadorUno().getNombre())
            ? juego.getJugadorUno()
            : juego.getJugadorDos();

    // Ocupa la posición actual y verifica si el jugador ha ganado
    posicionTablero.ocuparPosicion(jugadorEnTurno.getNombre());

    if (verificarGanador(partidaActual, jugadorEnTurno.getNombre())) {
        partidaActual.setEstado("Ganado");
        partidaActual.setGanador(jugadorEnTurno);

        // Actualiza el puntaje del jugador en el juego y en la partida
        if (jugadorEnTurno.equals(juego.getJugadorUno())) {
            partidaActual.setPuntajeJugadorUno(partidaActual.getPuntajeJugadorUno() + 1);
            juego.setPuntajeJugadorUno(juego.getPuntajeJugadorUno() + 1);
        } else {
            partidaActual.setPuntajeJugadorDos(partidaActual.getPuntajeJugadorDos() + 1);
            juego.setPuntajeJugadorDos(juego.getPuntajeJugadorDos() + 1);
        }

        verificarJuegoCompleto(juego);

    } else if (verificarEmpate(partidaActual)) {
        // Si es empate, reiniciar la partida actual
        partidaActual.reiniciarPartida();
    } else {
        // Cambia el turno al otro jugador
        partidaActual.setTurnoActual(jugadorEnTurno == juego.getJugadorUno()
                ? juego.getJugadorDos().getNombre()
                : juego.getJugadorUno().getNombre());

        // Realiza movimiento de la máquina si es juego de un solo jugador
        if (juego.getEsJugadorUnico() && partidaActual.getTurnoActual().equals("Kaos")) {
            hacerMovimientoMaquina(partidaActual);
        }
    }

    // Guardar la partida y el estado del juego
    return repositorioJuego.save(juego);
}

// Método para verificar combinaciones ganadoras en el tablero
private boolean verificarGanador(Partida partida, String nombreJugador) {
    int[][] combinacionesGanadoras = {
        {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
        {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
        {0, 4, 8}, {2, 4, 6}
    };

    // Convertir las posiciones a una lista ordenada por índice para simplificar el acceso
    List<TableroPosicion> posicionesList = new ArrayList<>(partida.getTablero());
    posicionesList.sort(Comparator.comparingInt(TableroPosicion::getIndice));

    // Verificar cada combinación ganadora
    for (int[] combinacion : combinacionesGanadoras) {
        if (nombreJugador.equals(posicionesList.get(combinacion[0]).getNombreJugador()) &&
            nombreJugador.equals(posicionesList.get(combinacion[1]).getNombreJugador()) &&
            nombreJugador.equals(posicionesList.get(combinacion[2]).getNombreJugador())) {
            return true;
        }
    }
    return false;
}


    
    private void hacerMovimientoMaquina(Partida partida) {
    int posicion;
    List<TableroPosicion> posicionesList = new ArrayList<>(partida.getTablero());

    do {
        posicion = random.nextInt(9);
    } while (posicionesList.get(posicion).getNombreJugador() != null);

    TableroPosicion posicionMaquina = posicionesList.get(posicion);
    posicionMaquina.ocuparPosicion("Kaos");

    if (verificarGanador(partida, "Kaos")) {
        partida.setEstado("Ganado");
        partida.setGanador(partida.getJuego().getJugadorDos());
        actualizarPuntaje(partida.getJuego(), partida.getJuego().getJugadorDos());
    } else if (posicionesList.stream().allMatch(pos -> pos.getNombreJugador() != null)) {
        partida.reiniciarPartida();
    } else {
        partida.setTurnoActual(partida.getJuego().getJugadorUno().getNombre());
    }
}


    @Override
    public void anularJuego(Long juegoId) {
        Juego juego = repositorioJuego.findById(juegoId)
                .orElseThrow(() -> new RuntimeException("Juego no encontrado"));
        juego.setEstado("Anulado");
        juego.setPuntajeJugadorUno(0);
        juego.setPuntajeJugadorDos(0);
        repositorioJuego.save(juego);
    }

    @Override
    public Juego obtenerJuegoPorId(Long juegoId) {
        return repositorioJuego.findById(juegoId)
                .orElseThrow(() -> new RuntimeException("Juego con ID " + juegoId + " no encontrado."));
    }

    @Override
    public Juego actualizarEstadoJuego(Long juegoId, String estado, String ganador, Integer puntaje) {
        Juego juego = repositorioJuego.findById(juegoId)
                .orElseThrow(() -> new RuntimeException("Juego no encontrado"));

        juego.setEstado(estado);

        if (ganador != null && !ganador.isEmpty()) {
            Jugador jugadorGanador = repositorioJugador.findByNombre(ganador)
                    .orElseThrow(() -> new RuntimeException("Jugador no encontrado: " + ganador));
            juego.setGanador(jugadorGanador);
        } else {
            juego.setGanador(null);
        }

        return repositorioJuego.save(juego);
    }

    private void verificarJuegoCompleto(Juego juego) {
        boolean todasGanadas = juego.getPartidas().stream().allMatch(p -> !"Jugando".equals(p.getEstado()));
        if (todasGanadas) {
            juego.setEstado("Completo");
            determinarGanadorFinal(juego);
        }
    }

    private void determinarGanadorFinal(Juego juego) {
        if (juego.getPuntajeJugadorUno() > juego.getPuntajeJugadorDos()) {
            juego.setGanador(juego.getJugadorUno());
        } else if (juego.getPuntajeJugadorUno() < juego.getPuntajeJugadorDos()) {
            juego.setGanador(juego.getJugadorDos());
        } else {
            juego.setGanador(null); // Empate
        }
    }

    private Partida obtenerPartidaActual(Juego juego) {
        return juego.getPartidas().stream()
                .filter(partida -> "Jugando".equals(partida.getEstado()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No hay partida activa."));
    }

    private void actualizarPuntaje(Juego juego, Jugador ganador) {
        if (ganador.getNombre().equals(juego.getJugadorUno().getNombre())) {
            juego.setPuntajeJugadorUno(juego.getPuntajeJugadorUno() + 1);
        } else if (ganador.getNombre().equals(juego.getJugadorDos().getNombre())) {
            juego.setPuntajeJugadorDos(juego.getPuntajeJugadorDos() + 1);
        }
    }
    

    @Override
    public void reiniciarJuego(Long juegoId) {
        // Obtén el juego por su ID
        Juego juego = obtenerJuegoPorId(juegoId);
        
        // Restablece el estado general del juego
        juego.setEstado("Jugando");
        juego.setGanador(null);
        juego.setPuntajeJugadorUno(0);
        juego.setPuntajeJugadorDos(0);
        
        // Itera sobre cada partida asociada al juego y reinícialas
        for (Partida partida : juego.getPartidas()) {
            partida.reiniciarPartida(); // Restaura el estado, puntajes, y el turno inicial
            partida.setEstado("Jugando");
            partida.setGanador(null);
            partida.setPuntajeJugadorUno(0);
            partida.setPuntajeJugadorDos(0);
            
            // Limpia el tablero para cada partida
            for (TableroPosicion posicion : partida.getTablero()) {
                posicion.setNombreJugador(null); // Borra el nombre del jugador de cada posición
            }
        }
        
        // Guarda el juego y todas las partidas en el repositorio
        repositorioJuego.save(juego);
    }
    

    
    private boolean verificarEmpate(Partida partida) {
        // Convertir el Set de posiciones en una List para simplificar el manejo
        List<TableroPosicion> posicionesList = new ArrayList<>(partida.getTablero());
    
        // Verificar que todas las posiciones están ocupadas y que no hay un ganador
        boolean todasOcupadas = posicionesList.stream().allMatch(pos -> pos.getNombreJugador() != null);
        
        // Es empate si todas las posiciones están ocupadas y la partida no tiene ganador
        return todasOcupadas && partida.getGanador() == null;
    }
   
    
    
    
    
}
