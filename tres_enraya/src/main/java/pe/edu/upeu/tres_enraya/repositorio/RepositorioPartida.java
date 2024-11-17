package pe.edu.upeu.tres_enraya.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.tres_enraya.modelo.Partida;

public interface RepositorioPartida extends JpaRepository<Partida, Long> {
}
