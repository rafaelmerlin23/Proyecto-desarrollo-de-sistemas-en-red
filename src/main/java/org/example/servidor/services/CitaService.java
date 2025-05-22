package org.example.servidor.services;


import org.example.servidor.database.DatabaseManager;
import org.example.servidor.database.dtos.CitaCrearDTO;
import org.example.servidor.database.dtos.CitaDTO;
import org.example.servidor.database.dtos.UpdateCitaDTO;
import org.example.servidor.database.entities.*;
import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CitaService {

    public static CitaDTO createCita(CitaCrearDTO citaDTO) {
        EntityManager em = DatabaseManager.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            Medico medico = em.getReference(Medico.class, citaDTO.getMedicoCedula());
            Paciente paciente = em.getReference(Paciente.class, citaDTO.getPacienteCurp());

            Cita newCita = new Cita();
            newCita.setFecha(citaDTO.getFecha());
            newCita.setHora(citaDTO.getHora());
            newCita.setMotivo(citaDTO.getMotivo());
            newCita.setMedico(medico);
            newCita.setPaciente(paciente);

            em.persist(newCita);
            transaction.commit();

            return new CitaDTO(newCita);

        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error al crear cita: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public static CitaDTO getCitaById(Long id) {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            Cita cita = em.find(Cita.class, id);
            return cita != null ? new CitaDTO(cita) : null;
        } finally {
            em.close();
        }
    }

    public static List<CitaDTO> getAllCitas() {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            List<Cita> citas = em.createQuery("SELECT c FROM Cita c", Cita.class).getResultList();
            return citas.stream().map(CitaDTO::new).collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    public static CitaDTO updateCita(UpdateCitaDTO citaUpdateDTO) {
        EntityManager em = DatabaseManager.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            Cita cita = em.find(Cita.class, citaUpdateDTO.getId());
            if (cita == null) {
                throw new IllegalArgumentException("Cita no encontrada con ID: " + citaUpdateDTO.getId());
            }

            Medico medico = em.getReference(Medico.class, citaUpdateDTO.getMedicoCedula());
            Paciente paciente = em.getReference(Paciente.class, citaUpdateDTO.getPacienteCurp());

            cita.setFecha(citaUpdateDTO.getFecha());
            cita.setHora(citaUpdateDTO.getHora());
            cita.setMotivo(citaUpdateDTO.getMotivo());
            cita.setMedico(medico);
            cita.setPaciente(paciente);

            transaction.commit();
            return new CitaDTO(cita);
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error al actualizar la cita: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public static boolean deleteCita(Long id) {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            em.getTransaction().begin();
            Cita cita = em.find(Cita.class, id);
            if (cita != null) {
                em.remove(cita);
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

    public static List<CitaDTO> getCitasByFecha(Date fecha) {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            List<Cita> citas = em.createQuery(
                            "SELECT c FROM Cita c WHERE c.fecha = :fecha ORDER BY c.hora", Cita.class).setParameter("fecha", fecha)
                    .getResultList();
            return citas.stream().map(CitaDTO::new).collect(Collectors.toList());
        } finally {
            em.close();
        }
    }
}