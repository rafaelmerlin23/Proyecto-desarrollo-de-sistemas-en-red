package org.example.servidor.database.entities;


import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "pacientes")
public class Paciente implements Serializable {
    @Id
    @Column(name = "curp", length = 18, nullable = false, unique = true)
    private String curp;

    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    @Column(name = "telefono", length = 15, nullable = false)
    private String telefono;

    @Column(name = "email", length = 100, nullable = false)
    private String email;

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cita> citas;

    // Constructores
    public Paciente() {}

    public Paciente(String curp, String nombre, String telefono, String email) {
        this.curp = curp;
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
    }

    // Getters y Setters
    public String getCurp() { return curp; }
    public void setCurp(String curp) { this.curp = curp; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<Cita> getCitas() { return citas; }
    public void setCitas(List<Cita> citas) { this.citas = citas; }

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Paciente paciente = (Paciente) o;
        return curp.equals(paciente.curp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(curp);
    }

    @Override
    public String toString() {
        return nombre + " (" + curp + ")";
    }
}