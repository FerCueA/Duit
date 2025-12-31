package es.duit.dao;

import es.duit.connections.MySqlConnection;
import es.duit.models.Usuario;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.springframework.stereotype.Repository;

@Repository
public class UsuarioDAO {

    private MySqlConnection objMySQLConnection;

    public UsuarioDAO() {
        objMySQLConnection = new MySqlConnection();
    }

    // Obtener todos los usuarios
    public ArrayList<Usuario> obtenerTodosUsuarios() throws SQLException {

        ArrayList<Usuario> objListaUsuarios = new ArrayList<>();
        objMySQLConnection.open();

        if (!objMySQLConnection.isError()) {

            String sql = "SELECT id_usuario, nombre, apellidos, username, email, password, telefono, id_rol, activo, fecha_registro FROM usuario";
            ResultSet objResultSet = objMySQLConnection.executeSelect(sql);

            try {
                while (objResultSet != null && objResultSet.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setIdUsuario(objResultSet.getInt("id_usuario"));
                    usuario.setNombre(objResultSet.getString("nombre"));
                    usuario.setApellidos(objResultSet.getString("apellidos"));
                    usuario.setUsername(objResultSet.getString("username"));
                    usuario.setEmail(objResultSet.getString("email"));
                    usuario.setPassword(objResultSet.getString("password"));
                    usuario.setTelefono(objResultSet.getString("telefono"));
                    usuario.setIdRol(objResultSet.getInt("id_rol"));
                    usuario.setActivo(objResultSet.getBoolean("activo"));
                    usuario.setFechaRegistro(objResultSet.getTimestamp("fecha_registro"));
                    objListaUsuarios.add(usuario);
                }
            } catch (Exception e) {
                System.err.println("Error inesperado ejecutando la consulta SQL: " + sql);
                System.err.println("Mensaje de error: " + e.getMessage());
                e.printStackTrace();
            }
        }

        objMySQLConnection.close();
        return objListaUsuarios;

    }

    // Obtener datos de un usuario por su username

    public Usuario obtenerUsuarioPorUsername(String username) {
        Usuario objUsuario = null;
        objMySQLConnection.open();

        if (!objMySQLConnection.isError()) {
            String query = "SELECT id_usuario, nombre, apellidos, username, email, password, telefono, id_rol, activo, fecha_registro FROM usuario WHERE username = '"
                    + username + "'";
            ResultSet resultSet = objMySQLConnection.executeSelect(query);

            try {
                if (resultSet != null && resultSet.next()) {
                    objUsuario = new Usuario();
                    objUsuario.setIdUsuario(resultSet.getInt("id_usuario"));
                    objUsuario.setNombre(resultSet.getString("nombre"));
                    objUsuario.setApellidos(resultSet.getString("apellidos"));
                    objUsuario.setUsername(resultSet.getString("username"));
                    objUsuario.setEmail(resultSet.getString("email"));
                    objUsuario.setPassword(resultSet.getString("password"));
                    objUsuario.setTelefono(resultSet.getString("telefono"));
                    objUsuario.setIdRol(resultSet.getInt("id_rol"));
                    objUsuario.setActivo(resultSet.getBoolean("activo"));
                    objUsuario.setFechaRegistro(resultSet.getTimestamp("fecha_registro"));
                }
            } catch (Exception e) {
                System.err.println("Error inesperado ejecutando la consulta SQL: " + query);
                System.err.println("Mensaje de error: " + e.getMessage());
                e.printStackTrace();
            }
        }

        objMySQLConnection.close();
        return objUsuario;
    }

}
