package org.example.servidor.database.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "citas")
public class Cita implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha", nullable = false)
    private Date fecha;

    @Column(name = "hora", length = 10, nullable = false)
    private String hora;

    @Column(name = "motivo", length = 200, nullable = false)
    private String motivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    private static final long serialVersionUID = 1L;

    // Constructores

    public Cita(Date fecha, String hora, String motivo, Medico medico, Paciente paciente) {
        this.fecha = fecha;
        this.hora = hora;
        this.motivo = motivo;
        this.medico = medico;
        this.paciente = paciente;
    }

    public Cita() {
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public Medico getMedico() { return medico; }
    public void setMedico(Medico medico) { this.medico = medico; }

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cita cita = (Cita) o;
        return id.equals(cita.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Cita #" + id + " - " + fecha + " " + hora + " (" + motivo + ")";
    }
}