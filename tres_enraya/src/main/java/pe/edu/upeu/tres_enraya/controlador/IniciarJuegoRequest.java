package pe.edu.upeu.tres_enraya.controlador;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class IniciarJuegoRequest {

    @NotNull
    private boolean esJugadorUnico;

    @Size(min = 2, max = 50)
    private String nombreJugadorUno;

    @Size(min = 2, max = 50)
    private String nombreJugadorDos;

    @Min(1)
    private int numeroPartidas;

    public boolean isEsJugadorUnico() {
        return esJugadorUnico;
    }

    public void setEsJugadorUnico(boolean esJugadorUnico) {
        this.esJugadorUnico = esJugadorUnico;
    }

    public String getNombreJugadorUno() {
        return nombreJugadorUno;
    }

    public void setNombreJugadorUno(String nombreJugadorUno) {
        this.nombreJugadorUno = nombreJugadorUno;
    }

    public String getNombreJugadorDos() {
        return nombreJugadorDos;
    }

    public void setNombreJugadorDos(String nombreJugadorDos) {
        this.nombreJugadorDos = nombreJugadorDos;
    }

    public int getNumeroPartidas() {
        return numeroPartidas;
    }

    public void setNumeroPartidas(int numeroPartidas) {
        this.numeroPartidas = numeroPartidas;
    }
}
