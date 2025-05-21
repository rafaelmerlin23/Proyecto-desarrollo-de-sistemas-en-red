package org.example.servidor.services;


import org.example.servidor.database.DatabaseManager;
import org.example.servidor.database.dtos.CitaDTO;
import org.example.servidor.database.entities.*;
import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CitaService {

    public static CitaDTO createCita(Cita cita) {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(cita);
            em.getTransaction().commit();
            return new CitaDTO(cita);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public static CitaDTO getCitaById(Long id) {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            Cita cita = em.find(Cita.class,id);
            return cita != null ? new CitaDTO(cita):null;
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

    public static CitaDTO updateCita(Cita cita) {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            em.getTransaction().begin();
            Cita updated = em.merge(cita);
            em.getTransaction().commit();
            return new CitaDTO(updated);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
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
                    "SELECT c FROM Cita c WHERE c.fecha = :fecha ORDER BY c.hora", Cita.class).setParameter("fecha",fecha)
                    .getResultList();
            return citas.stream().map(CitaDTO::new).collect(Collectors.toList());
        } finally {
            em.close();
        }
    }
}