package pe.edu.upeu.tres_enraya.controlador;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pe.edu.upeu.tres_enraya.modelo.Juego;
import pe.edu.upeu.tres_enraya.servicio.ServicioJuego;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ControladorJuego.class)
public class ControladorJuegoTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServicioJuego servicioJuego;

    @Test
    public void testIniciarJuego() throws Exception {
        Juego mockJuego = new Juego();
        mockJuego.setId(1L);
        mockJuego.setEstado("Jugando");
        mockJuego.setEsJugadorUnico(true);

        when(servicioJuego.crearJuego(anyBoolean(), anyString(), any(), anyInt())).thenReturn(mockJuego);

        mockMvc.perform(post("/api/juegos/iniciar")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "esJugadorUnico": true,
                            "nombreJugadorUno": "Jugador1",
                            "numeroPartidas": 3
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("Jugando"))
                .andExpect(jsonPath("$.esJugadorUnico").value(true));

        verify(servicioJuego, times(1)).crearJuego(eq(true), eq("Jugador1"), isNull(), eq(3));
    }

    @Test
    public void testHacerMovimiento() throws Exception {
        Juego mockJuego = new Juego();
        mockJuego.setId(1L);
        mockJuego.setEstado("Jugando");

        when(servicioJuego.hacerMovimiento(eq(1L), eq(0))).thenReturn(mockJuego);

        mockMvc.perform(put("/api/juegos/1/movimiento")
                .param("posicion", "0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("Jugando"));

        verify(servicioJuego, times(1)).hacerMovimiento(eq(1L), eq(0));
    }

    @Test
    public void testReiniciarJuego() throws Exception {
        mockMvc.perform(put("/api/juegos/1/reiniciar")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Juego y partidas reiniciados con éxito."));

        verify(servicioJuego, times(1)).reiniciarJuego(eq(1L));
    }

    @Test
    public void testAnularJuego() throws Exception {
        mockMvc.perform(put("/api/juegos/1/anular")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Juego anulado."));

        verify(servicioJuego, times(1)).anularJuego(eq(1L));
    }

    @Test
    public void testHacerMovimientoVsMaquina() throws Exception {
        Juego mockJuego = new Juego();
        mockJuego.setId(1L);
        mockJuego.setEstado("Jugando");

        when(servicioJuego.hacerMovimiento(eq(1L), eq(4))).thenReturn(mockJuego);

        mockMvc.perform(put("/api/juegos/1/movimiento")
                .param("posicion", "4")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("Jugando"));

        verify(servicioJuego, times(1)).hacerMovimiento(eq(1L), eq(4));
    }
}
