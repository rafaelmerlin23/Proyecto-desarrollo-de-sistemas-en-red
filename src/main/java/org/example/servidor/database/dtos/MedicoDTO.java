package org.example.servidor.database.dtos;

import java.io.Serializable;

public class MedicoDTO implements Serializable {
    private String cedula;
    private String nombre;
    private String especialidad;
    private String email;

    public MedicoDTO() {}

    public MedicoDTO(String cedula, String nombre, String especialidad, String email) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.especialidad = especialidad;
        this.email = email;
    }

    public MedicoDTO(org.example.servidor.database.entities.Medico medico) {
        this.cedula = medico.getCedula();
        this.nombre = medico.getNombre();
        this.especialidad = medico.getEspecialidad();
        this.email = medico.getEmail();
    }

    // Getters y setters
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}

