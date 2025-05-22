package org.example.servidor;


import org.example.servidor.database.DatabaseManager;
import org.example.servidor.database.dtos.CitaCrearDTO;
import org.example.servidor.database.dtos.CitaDTO;
import org.example.servidor.database.dtos.UpdateCitaDTO;
import org.example.servidor.database.entities.*;
import org.example.servidor.services.*;
import org.example.shared.*;

import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.List;

public class Servidor {
    private static final int PORT = 12345;

    public static void main(String[] args) {
        DatabaseManager.initialize();
        System.out.println("Inicializando servidor médico...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor escuchando en el puerto " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        } finally {
            DatabaseManager.close();
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
                 ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream())) {

                while (true) {
                    Request request = (Request) ois.readObject();
                    Response response = processRequest(request);
                    oos.writeObject(response);
                    oos.flush();
                }

            } catch (EOFException e) {
                System.out.println("Cliente desconectado");
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error en la conexión con el cliente: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Error procesando solicitud: " + e.getMessage());
            }
        }

        private Response processRequest(Request request) {
            try {
                switch (request.getType()) {
                    // Médicos
                    case "CREATE_MEDICO":
                        return new Response(true, MedicoService.createMedico((Medico) request.getData()));
                    case "GET_MEDICO":
                        return new Response(true, MedicoService.getMedicoByCedula((String) request.getData()));
                    case "GET_ALL_MEDICOS":
                        return new Response(true, MedicoService.getAllMedicos());
                    case "UPDATE_MEDICO":
                        return new Response(true, MedicoService.updateMedico((Medico) request.getData()));
                    case "DELETE_MEDICO":
                        return new Response(true, MedicoService.deleteMedico((String) request.getData()));

                    // Pacientes
                    case "CREATE_PACIENTE":
                        return new Response(true, PacienteService.createPaciente((Paciente) request.getData()));
                    case "GET_PACIENTE":
                        return new Response(true, PacienteService.getPacienteByCurp((String) request.getData()));
                    case "GET_ALL_PACIENTES":
                        return new Response(true, PacienteService.getAllPacientes());
                    case "UPDATE_PACIENTE":
                        return new Response(true, PacienteService.updatePaciente((Paciente) request.getData()));
                    case "DELETE_PACIENTE":
                        return new Response(true, PacienteService.deletePaciente((String) request.getData()));

                    // Citas
                    case "CREATE_CITA":
                        return new Response(true, CitaService.createCita((CitaCrearDTO) request.getData()));
                    case "GET_CITA":
                        return new Response(true, CitaService.getCitaById((Long) request.getData()));
                    case "GET_ALL_CITAS":
                        return new Response(true, CitaService.getAllCitas());
                    case "GET_CITAS_BY_FECHA":
                        return new Response(true, CitaService.getCitasByFecha((Date) request.getData()));
                    case "UPDATE_CITA":
                        return new Response(true, CitaService.updateCita((UpdateCitaDTO) request.getData()));
                    case "DELETE_CITA":
                        return new Response(true, CitaService.deleteCita((Long) request.getData()));

                    default:
                        return new Response(false, "Tipo de solicitud no reconocido");
                }
            } catch (Exception e) {
                return new Response(false, e.getMessage());
            }
        }
    }
}