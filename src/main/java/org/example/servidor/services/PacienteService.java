package org.example.servidor.services;


import org.example.servidor.database.DatabaseManager;
import org.example.servidor.database.dtos.MedicoDTO;
import org.example.servidor.database.dtos.PacienteDTO;
import org.example.servidor.database.entities.Medico;
import org.example.servidor.database.entities.Paciente;
import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

public class PacienteService {
    public static PacienteDTO createPaciente(Paciente paciente) {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(paciente);
            em.getTransaction().commit();
            return new PacienteDTO(paciente);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public static MedicoDTO getPacienteByCurp(String curp) {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            Paciente paciente = em.find(Paciente.class,curp);
            return paciente != null ? new PacienteDTO(paciente): null;
        } finally {
            em.close();
        }
    }

    public static List<PacienteDTO> getAllPacientes() {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            List<Paciente> pacientes = em.createQuery("SELECT p FROM Paciente p", Paciente.class).getResultList();
            return pacientes.stream().map(PacienteDTO::new).collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    public static PacienteDTO updatePaciente(Paciente paciente) {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            em.getTransaction().begin();
            Paciente updated = em.merge(paciente);
            em.getTransaction().commit();
            return new PacienteDTO(updated);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public static boolean deletePaciente(String curp) {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            em.getTransaction().begin();
            Paciente paciente = em.find(Paciente.class, curp);
            if (paciente != null) {
                em.remove(paciente);
                em.getTransaction().commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}