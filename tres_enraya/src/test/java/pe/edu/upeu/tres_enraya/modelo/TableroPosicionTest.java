package pe.edu.upeu.tres_enraya.modelo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TableroPosicionTest {

    @Test
    void testOcuparPosicion() {
        // Arrange
        TableroPosicion posicion = new TableroPosicion();
        String jugador = "Jugador1";

        // Act
        posicion.ocuparPosicion(jugador);

        // Assert
        assertEquals(jugador, posicion.getNombreJugador(), "La posición no fue ocupada correctamente por el jugador.");
    }

    @Test
    void testSetPartida() {
        // Arrange
        TableroPosicion posicion = new TableroPosicion();
        Partida partida = new Partida();

        // Act
        posicion.setPartida(partida);

        // Assert
        assertEquals(partida, posicion.getPartida(), "La partida no fue asignada correctamente.");
    }

    @Test
    void testConstructorAndDefaultValues() {
        // Arrange & Act
        TableroPosicion posicion = new TableroPosicion();

        // Assert
        assertNull(posicion.getId(), "El ID inicial debe ser null.");
        assertNull(posicion.getNombreJugador(), "El nombre del jugador inicial debe ser null.");
        assertEquals(0, posicion.getIndice(), "El índice inicial debe ser 0.");
        assertNull(posicion.getPartida(), "La partida inicial debe ser null.");
    }
}
