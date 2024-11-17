package pe.edu.upeu.tres_enraya.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import pe.edu.upeu.tres_enraya.modelo.Jugador;

@Repository
public interface RepositorioJugador extends JpaRepository<Jugador, Long> {
    Optional<Jugador> findByNombre(String nombre);

}