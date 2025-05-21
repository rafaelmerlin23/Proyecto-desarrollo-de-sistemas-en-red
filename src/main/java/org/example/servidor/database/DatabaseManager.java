package org.example.servidor.database;

import org.apache.openjpa.persistence.*;
import org.example.servidor.database.entities.*;
import javax.persistence.*;

public class DatabaseManager {
    private static EntityManagerFactory emf;

    public static void initialize() {
        try {
            emf = Persistence.createEntityManagerFactory("medical-system");
            System.out.println("Conexión a la base de datos establecida correctamente");
        } catch (Exception e) {
            System.err.println("Error al inicializar la base de datos:");
            e.printStackTrace();
        }
    }

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}