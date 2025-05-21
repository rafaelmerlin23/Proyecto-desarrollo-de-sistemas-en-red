package org.example.servidor.database.dtos;


import java.io.Serializable;

public class PacienteDTO extends MedicoDTO implements Serializable {
    private String curp;
    private String nombre;
    private String telefono;
    private String email;


    public PacienteDTO(org.example.servidor.database.entities.Paciente paciente) {
        this.curp = paciente.getCurp();
        this.nombre = paciente.getNombre();
        this.telefono = paciente.getTelefono();
        this.email = paciente.getEmail();
    }

    public String getCurp() {
        return curp;
    }


    public void setCurp(String curp) {
        this.curp = curp;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}