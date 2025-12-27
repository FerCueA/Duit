package es.duit.dao;

import es.duit.connections.MySqlConnection;
import es.duit.models.Rol;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RolDAO {

    private final MySqlConnection mySqlConnection;

    public RolDAO(MySqlConnection mySqlConnection) {
        this.mySqlConnection = mySqlConnection;
    }

    public List<Rol> obtenerTodos() {
        List<Rol> roles = new ArrayList<>();
        String query = "SELECT id_rol, nombre_rol FROM rol";

        try {
            mySqlConnection.open();
            ResultSet resultSet = mySqlConnection.executeSelect(query);

            while (resultSet != null && resultSet.next()) {
                Rol rol = new Rol();
                rol.setIdRol(resultSet.getInt("id_rol"));
                rol.setNombreRol(resultSet.getString("nombre_rol"));
                roles.add(rol);
            }
        } catch (Exception e) {
            System.err.println("Error inesperado ejecutando la consulta SQL: " + query);
            System.err.println("Mensaje de error: " + e.getMessage());
            e.printStackTrace(); // Registro detallado del error
        } finally {
            mySqlConnection.close();
        }

        return roles;
    }

    public Rol obtenerPorId(int idRol) {
        Rol rol = null;
        String query = "SELECT id_rol, nombre_rol FROM rol WHERE id_rol = " + idRol;

        try {
            mySqlConnection.open();
            ResultSet resultSet = mySqlConnection.executeSelect(query);

            if (resultSet != null && resultSet.next()) {
                rol = new Rol();
                rol.setIdRol(resultSet.getInt("id_rol"));
                rol.setNombreRol(resultSet.getString("nombre_rol"));
            }
        } catch (Exception e) {
            System.err.println("Error inesperado ejecutando la consulta SQL: " + query);
            System.err.println("Mensaje de error: " + e.getMessage());
            e.printStackTrace(); // Registro detallado del error
        } finally {
            mySqlConnection.close();
        }

        return rol;
    }

    public boolean insertar(Rol rol) {
        String query = "INSERT INTO rol (nombre_rol) VALUES ('" + rol.getNombreRol() + "')";

        try {
            mySqlConnection.open();
            ResultSet resultSet = mySqlConnection.executeInsert(query);
            return resultSet != null && resultSet.next();
        } catch (Exception e) {
            System.err.println("Error inesperado ejecutando la consulta SQL: " + query);
            System.err.println("Mensaje de error: " + e.getMessage());
            e.printStackTrace(); // Registro detallado del error
        } finally {
            mySqlConnection.close();
        }

        return false;
    }

    public boolean actualizar(Rol rol) {
        String query = "UPDATE rol SET nombre_rol = '" + rol.getNombreRol() + "' WHERE id_rol = " + rol.getIdRol();

        try {
            mySqlConnection.open();
            int rowsAffected = mySqlConnection.executeUpdateOrDelete(query);
            return rowsAffected > 0;
        } catch (Exception e) {
            System.err.println("Error inesperado ejecutando la consulta SQL: " + query);
            System.err.println("Mensaje de error: " + e.getMessage());
            e.printStackTrace(); // Registro detallado del error
        } finally {
            mySqlConnection.close();
        }

        return false;
    }

    public boolean eliminar(int idRol) {
        String query = "DELETE FROM rol WHERE id_rol = " + idRol;

        try {
            mySqlConnection.open();
            int rowsAffected = mySqlConnection.executeUpdateOrDelete(query);
            return rowsAffected > 0;
        } catch (Exception e) {
            System.err.println("Error inesperado ejecutando la consulta SQL: " + query);
            System.err.println("Mensaje de error: " + e.getMessage());
            e.printStackTrace(); // Registro detallado del error
        } finally {
            mySqlConnection.close();
        }

        return false;
    }
}
