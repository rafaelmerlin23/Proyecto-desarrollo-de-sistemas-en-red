package org.example.servidor.database.dtos;


import java.io.Serializable;
import java.util.Date;

public class UpdateCitaDTO implements Serializable {
    private Long id;
    private Date fecha;
    private String hora;
    private String motivo;
    private String medicoCedula;
    private String pacienteCurp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getMedicoCedula() {
        return medicoCedula;
    }

    public void setMedicoCedula(String medicoCedula) {
        this.medicoCedula = medicoCedula;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getPacienteCurp() {
        return pacienteCurp;
    }

    public void setPacienteCurp(String pacienteCurp) {
        this.pacienteCurp = pacienteCurp;
    }

    // Constructor vacío
    public UpdateCitaDTO() {
    }

    // Constructor con parámetros
    public UpdateCitaDTO(Long id, Date fecha, String hora, String motivo, String medicoCedula, String pacienteCurp) {
        this.id = id;
        this.fecha = fecha;
        this.hora = hora;
        this.motivo = motivo;
        this.medicoCedula = medicoCedula;
        this.pacienteCurp = pacienteCurp;
    }

}