package pe.edu.upeu.tres_enraya.modelo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;
import java.util.HashSet;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Juego {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El estado es obligatorio")
    @Column(nullable = false)
    private String estado = "Jugando";

    private Integer puntajeJugadorUno = 0;
    private Integer puntajeJugadorDos = 0;

    @OneToOne
    private Jugador ganador;

    @NotNull
    @Column(name = "es_jugador_unico", nullable = false)
    private Boolean esJugadorUnico;

    private String turnoActual;

    @OneToOne
    private Jugador jugadorUno;

    @OneToOne
    private Jugador jugadorDos;

    @Column(name = "numero_partidas")
    private Integer numeroPartidas;

    @OneToMany(mappedBy = "juego", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<Partida> partidas = new HashSet<>();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion = new Date();

    public void agregarPartida(Partida partida) {
        partida.setJuego(this);
        this.partidas.add(partida);
    }
}
