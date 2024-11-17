package pe.edu.upeu.tres_enraya.modelo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Partida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String estado = "Jugando";

    @ManyToOne
    @JoinColumn(name = "juego_id", nullable = false)
    @JsonBackReference
    private Juego juego;

    private Integer puntajeJugadorUno = 0;
    private Integer puntajeJugadorDos = 0;

    @ManyToOne
    @JoinColumn(name = "ganador_id", nullable = true)
    private Jugador ganador;

    private String turnoActual;

    @OneToMany(mappedBy = "partida", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<TableroPosicion> tablero = new HashSet<>();

    public void reiniciarPartida() {
        this.estado = "Jugando";
        this.ganador = null;
        this.puntajeJugadorUno = 0;
        this.puntajeJugadorDos = 0;
        this.turnoActual = juego.getJugadorUno().getNombre();
        
        this.tablero.forEach(pos -> pos.setNombreJugador(null));
    }
    

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Partida partida = (Partida) obj;
        return id != null && id.equals(partida.id);
    }
}
