package pe.edu.upeu.tres_enraya.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.tres_enraya.modelo.Juego;

@Repository
public interface RepositorioJuego extends JpaRepository<Juego, Long> {
    @EntityGraph(attributePaths = {"partidas.tablero"})
    Optional<Juego> findWithPartidasById(Long id);
}

