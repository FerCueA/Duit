package es.duit.dao;

import es.duit.connections.MySqlConnection;
import es.duit.models.Usuario;

import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    private final MySqlConnection mySqlConnection;

    public UsuarioDAO(MySqlConnection mySqlConnection) {
        this.mySqlConnection = mySqlConnection;
    }

    public List<Usuario> obtenerTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String query = "SELECT id_usuario, nombre, email, password, telefono, fecha_registro, estado, id_rol FROM usuario";

        try {
            mySqlConnection.open();
            ResultSet resultSet = mySqlConnection.executeSelect(query);

            while (resultSet != null && resultSet.next()) {
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(resultSet.getInt("id_usuario"));
                usuario.setNombre(resultSet.getString("nombre"));
                usuario.setEmail(resultSet.getString("email"));
                usuario.setPassword(resultSet.getString("password"));
                usuario.setTelefono(resultSet.getString("telefono"));
                usuario.setFechaRegistro(resultSet.getTimestamp("fecha_registro"));
                usuario.setEstado(resultSet.getString("estado"));
                usuario.setIdRol(resultSet.getInt("id_rol"));
                usuarios.add(usuario);
            }
        } catch (Exception e) {
            System.err.println("Error inesperado ejecutando la consulta SQL: " + query);
            System.err.println("Mensaje de error: " + e.getMessage());
            e.printStackTrace(); // Registro detallado del error
        } finally {
            mySqlConnection.close();
        }

        return usuarios;
    }

    public Usuario obtenerPorId(int idUsuario) {
        Usuario usuario = null;
        String query = "SELECT id_usuario, nombre, email, password, telefono, fecha_registro, estado, id_rol FROM usuario WHERE id_usuario = " + idUsuario;

        try {
            mySqlConnection.open();
            ResultSet resultSet = mySqlConnection.executeSelect(query);

            if (resultSet != null && resultSet.next()) {
                usuario = new Usuario();
                usuario.setIdUsuario(resultSet.getInt("id_usuario"));
                usuario.setNombre(resultSet.getString("nombre"));
                usuario.setEmail(resultSet.getString("email"));
                usuario.setPassword(resultSet.getString("password"));
                usuario.setTelefono(resultSet.getString("telefono"));
                usuario.setFechaRegistro(resultSet.getTimestamp("fecha_registro"));
                usuario.setEstado(resultSet.getString("estado"));
                usuario.setIdRol(resultSet.getInt("id_rol"));
            }
        } catch (Exception e) {
            System.err.println("Error inesperado ejecutando la consulta SQL: " + query);
            System.err.println("Mensaje de error: " + e.getMessage());
            e.printStackTrace(); // Registro detallado del error
        } finally {
            mySqlConnection.close();
        }

        return usuario;
    }

    public Usuario obtenerPorEmail(String email) {
        Usuario usuario = null;
        String query = "SELECT id_usuario, nombre, email, password, telefono, fecha_registro, estado, id_rol FROM usuario WHERE email = '" + email + "'";

        try {
            mySqlConnection.open();
            ResultSet resultSet = mySqlConnection.executeSelect(query);

            if (resultSet != null && resultSet.next()) {
                usuario = new Usuario();
                usuario.setIdUsuario(resultSet.getInt("id_usuario"));
                usuario.setNombre(resultSet.getString("nombre"));
                usuario.setEmail(resultSet.getString("email"));
                usuario.setPassword(resultSet.getString("password"));
                usuario.setTelefono(resultSet.getString("telefono"));
                usuario.setFechaRegistro(resultSet.getTimestamp("fecha_registro"));
                usuario.setEstado(resultSet.getString("estado"));
                usuario.setIdRol(resultSet.getInt("id_rol"));
            }
        } catch (Exception e) {
            System.err.println("Error inesperado ejecutando la consulta SQL: " + query);
            System.err.println("Mensaje de error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            mySqlConnection.close();
        }

        return usuario;
    }

    public boolean insertar(Usuario usuario) {
        String query = "INSERT INTO usuario (nombre, email, password, telefono, estado, id_rol) VALUES ('" +
                usuario.getNombre() + "', '" +
                usuario.getEmail() + "', '" +
                usuario.getPassword() + "', '" +
                usuario.getTelefono() + "', '" +
                usuario.getEstado() + "', " +
                usuario.getIdRol() + ")";

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

    public boolean actualizar(Usuario usuario) {
        String query = "UPDATE usuario SET nombre = '" + usuario.getNombre() +
                "', email = '" + usuario.getEmail() +
                "', password = '" + usuario.getPassword() +
                "', telefono = '" + usuario.getTelefono() +
                "', estado = '" + usuario.getEstado() +
                "', id_rol = " + usuario.getIdRol() +
                " WHERE id_usuario = " + usuario.getIdUsuario();

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

    public boolean eliminar(int idUsuario) {
        String query = "DELETE FROM usuario WHERE id_usuario = " + idUsuario;

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
