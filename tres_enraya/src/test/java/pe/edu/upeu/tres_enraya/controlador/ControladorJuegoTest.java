package pe.edu.upeu.tres_enraya.controlador;

import pe.edu.upeu.tres_enraya.modelo.Juego;
import pe.edu.upeu.tres_enraya.servicio.ServicioJuego;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ControladorJuegoTest {

    @Mock
    private ServicioJuego servicioJuego;  // Mock del servicio

    @InjectMocks
    private ControladorJuego controladorJuego;  // Inyección del controlador

    private MockMvc mockMvc;  // MockMvc para simular las peticiones HTTP

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Inicializa los mocks
        mockMvc = MockMvcBuilders.standaloneSetup(controladorJuego).build();  // Configura MockMvc
    }

    @Test
    void testIniciarJuego() throws Exception {
        // Simula el servicio para iniciar el juego
        Juego juego = new Juego();
        juego.setId(1L);
        juego.setEstado("En curso");

        when(servicioJuego.crearJuego(anyBoolean(), anyString(), anyString(), anyInt())).thenReturn(juego);

        // Realiza la petición POST para iniciar el juego
        mockMvc.perform(post("/api/juegos/iniciar")
                        .param("esJugadorUnico", "false")
                        .param("nombreJugadorUno", "Jugador 1")
                        .param("nombreJugadorDos", "Jugador 2")
                        .param("numeroPartidas", "3"))
                .andExpect(status().isOk())  // Espera que el status sea 200 OK
                .andExpect(jsonPath("$.id").value(1L))  // Verifica que el ID del juego sea 1
                .andExpect(jsonPath("$.estado").value("En curso"));  // Verifica el estado del juego
    }

    @Test
    void testHacerMovimiento() throws Exception {
        // Crea un objeto Juego simulado con datos de prueba
        Juego juegoSimulado = new Juego();
        juegoSimulado.setId(1L);  // ID del juego
        juegoSimulado.setEstado("En curso");  // Estado del juego
        juegoSimulado.setPuntajeJugadorUno(1);  // Puntaje jugador 1
        juegoSimulado.setPuntajeJugadorDos(0);  // Puntaje jugador 2

        // Simula el comportamiento del servicio para el movimiento
        when(servicioJuego.hacerMovimiento(anyLong(), anyInt())).thenReturn(juegoSimulado);

        // Realiza la petición PUT para hacer un movimiento
        mockMvc.perform(put("/api/juegos/1/movimiento")
                        .param("posicion", "5"))
                .andExpect(status().isOk())  // Espera que el status sea 200 OK
                .andExpect(jsonPath("$.id").value(1L))  // Verifica que el ID sea correcto
                .andExpect(jsonPath("$.estado").value("En curso"))  // Verifica el estado del juego
                .andExpect(jsonPath("$.puntajeJugadorUno").value(1))  // Verifica puntaje del jugador 1
                .andExpect(jsonPath("$.puntajeJugadorDos").value(0));  // Verifica puntaje del jugador 2
    }


    @Test
    void testReiniciarJuego() throws Exception {
        // Simula el servicio para reiniciar el juego
        doNothing().when(servicioJuego).reiniciarJuego(anyLong());

        // Realiza la petición PUT para reiniciar el juego
        mockMvc.perform(put("/api/juegos/1/reiniciar"))
                .andExpect(status().isOk())  // Espera que el status sea 200 OK
                .andExpect(content().string("Juego y partidas reiniciados con éxito."));  // Verifica el mensaje de respuesta
    }

    /*@Test
    void testObtenerEstadoJuego() throws Exception {
        // Simula las posiciones del tablero
        TableroPosicion posicion1 = new TableroPosicion();
        posicion1.setIndice(1);
        posicion1.setNombreJugador("Jugador1");

        TableroPosicion posicion2 = new TableroPosicion();
        posicion2.setIndice(2);
        posicion2.setNombreJugador(null);

        TableroPosicion posicion3 = new TableroPosicion();
        posicion3.setIndice(3);
        posicion3.setNombreJugador("Jugador2");

        // Simula la partida
        Partida partidaSimulada = new Partida();
        partidaSimulada.setEstado("Jugando");
        partidaSimulada.setTurnoActual("Jugador1");

        Set<TableroPosicion> tableroSimulado = new HashSet<>();
        tableroSimulado.add(posicion1);
        tableroSimulado.add(posicion2);
        tableroSimulado.add(posicion3);
        partidaSimulada.setTablero(tableroSimulado);

        // Simula el juego
        Juego juegoSimulado = new Juego();
        juegoSimulado.setId(1L);
        juegoSimulado.setEstado("Jugando");
        juegoSimulado.setPuntajeJugadorUno(10);
        juegoSimulado.setPuntajeJugadorDos(5);
        juegoSimulado.setEsJugadorUnico(false);
        juegoSimulado.setFechaCreacion(new Date());

        Set<Partida> partidasSimuladas = new HashSet<>();
        partidasSimuladas.add(partidaSimulada);
        juegoSimulado.setPartidas(partidasSimuladas);

        // Configura el mock para que devuelva el juego simulado
        when(servicioJuego.obtenerJuegoPorId(1L)).thenReturn(juegoSimulado);

        // Realiza la petición GET para obtener el estado del juego
        mockMvc.perform(get("/api/juegos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.estado").value("Jugando"));
    }*/




    @Test
    void testAnularJuego() throws Exception {
        // Simula el servicio para anular el juego
        doNothing().when(servicioJuego).anularJuego(anyLong());

        // Realiza la petición PUT para anular el juego
        mockMvc.perform(put("/api/juegos/1/anular"))
                .andExpect(status().isOk())  // Espera que el status sea 200 OK
                .andExpect(content().string("Juego anulado."));  // Verifica el mensaje de respuesta
    }
}
