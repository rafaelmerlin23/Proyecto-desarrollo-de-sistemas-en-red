package org.example.servidor.services;


import org.example.servidor.database.DatabaseManager;
import org.example.servidor.database.dtos.MedicoDTO;
import org.example.servidor.database.entities.Medico;
import javax.persistence.*;
import java.util.List;

import java.util.stream.Collectors;

public class MedicoService {

    public static MedicoDTO createMedico(Medico medico) {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(medico);
            em.getTransaction().commit();
            return new MedicoDTO(medico);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public static MedicoDTO getMedicoByCedula(String cedula) {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            Medico medico = em.find(Medico.class, cedula);
            return medico != null ? new MedicoDTO(medico) : null;
        } finally {
            em.close();
        }
    }

    public static List<MedicoDTO> getAllMedicos() {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            List<Medico> medicos = em.createQuery("SELECT m FROM Medico m", Medico.class).getResultList();
            return medicos.stream().map(MedicoDTO::new).collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    public static MedicoDTO updateMedico(Medico medico) {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            em.getTransaction().begin();
            Medico updated = em.merge(medico);
            em.getTransaction().commit();
            return new MedicoDTO(updated);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public static boolean deleteMedico(String cedula) {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            em.getTransaction().begin();
            Medico medico = em.find(Medico.class, cedula);
            if (medico != null) {
                em.remove(medico);
                em.getTransaction().commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
