package org.example.servidor.database.dtos;

import java.io.Serializable;
import java.util.Date;

public class CitaCrearDTO  implements Serializable {
    private Date fecha;
    private String hora;
    private String motivo;
    private String medicoCedula;  // ID del médico
    private String pacienteCurp;  // ID del paciente

    // Constructor vacío
    public CitaCrearDTO() {
    }

    // Constructor con parámetros
    public CitaCrearDTO(Date fecha, String hora, String motivo, String medicoCedula, String pacienteCurp) {
        this.fecha = fecha;
        this.hora = hora;
        this.motivo = motivo;
        this.medicoCedula = medicoCedula;
        this.pacienteCurp = pacienteCurp;
    }

    // Getters y Setters
    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getMedicoCedula() {
        return medicoCedula;
    }

    public void setMedicoCedula(String medicoCedula) {
        this.medicoCedula = medicoCedula;
    }

    public String getPacienteCurp() {
        return pacienteCurp;
    }

    public void setPacienteCurp(String pacienteCurp) {
        this.pacienteCurp = pacienteCurp;
    }
}
