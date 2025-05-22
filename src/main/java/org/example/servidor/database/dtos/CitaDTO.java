package org.example.servidor.database.dtos;


import java.io.Serializable;
import java.util.Date;

public class CitaDTO implements Serializable {
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

    public String getPacienteCurp() {
        return pacienteCurp;
    }

    public void setPacienteCurp(String pacienteCurp) {
        this.pacienteCurp = pacienteCurp;
    }

    public String getMedicoCedula() {
        return medicoCedula;
    }

    public void setMedicoCedula(String medicoCedula) {
        this.medicoCedula = medicoCedula;
    }

    private Long id;
    private Date fecha;
    private String medicoCedula;
    private String pacienteCurp;
    private String medicoNombre;
    private String hora;
    private String pacienteNombre;
    private String motivo;

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getMedicoNombre() {
        return medicoNombre;
    }

    public void setMedicoNombre(String medicoNombre) {
        this.medicoNombre = medicoNombre;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getPacienteNombre() {
        return pacienteNombre;
    }

    public void setPacienteNombre(String pacienteNombre) {
        this.pacienteNombre = pacienteNombre;
    }




    public CitaDTO(org.example.servidor.database.entities.Cita cita) {
        this.id = cita.getId();
        this.fecha = cita.getFecha();
        this.medicoCedula = cita.getMedico().getCedula();
        this.pacienteCurp = cita.getPaciente().getCurp();
        this.pacienteNombre = cita.getPaciente().getNombre();
        this.medicoNombre = cita.getMedico().getNombre();
        this.motivo = cita.getMotivo();
        this.hora = cita.getHora();
    }

}



