package pe.edu.upeu.tres_enraya.modelo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableroPosicion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreJugador;
    private int indice;

    @ManyToOne
    @JoinColumn(name = "partida_id", nullable = false)
    @JsonBackReference

    private Partida partida;

    public void ocuparPosicion(String nombreJugador) {
        this.nombreJugador = nombreJugador;
    }

    public void setPartida(Partida partida) {
        this.partida = partida;
    }
}
