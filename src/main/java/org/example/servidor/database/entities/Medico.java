package org.example.servidor.database.entities;


import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "medicos")
public class Medico implements Serializable {
    @Id
    @Column(name = "cedula", length = 20, nullable = false, unique = true)
    private String cedula;

    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    @Column(name = "especialidad", length = 50, nullable = false)
    private String especialidad;

    @Column(name = "email", length = 100, nullable = false)
    private String email;

    @OneToMany(mappedBy = "medico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cita> citas;

    private static final long serialVersionUID = 1L;

    // Constructores
    public Medico() {}

    public Medico(String cedula, String nombre, String especialidad, String email) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.especialidad = especialidad;
        this.email = email;
    }

    // Getters y Setters
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<Cita> getCitas() { return citas; }
    public void setCitas(List<Cita> citas) { this.citas = citas; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Medico medico = (Medico) o;
        return cedula.equals(medico.cedula);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cedula);
    }

    @Override
    public String toString() {
        return nombre + " (" + especialidad + ")";
    }
}