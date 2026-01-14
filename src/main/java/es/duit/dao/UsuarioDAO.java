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

       // Método para mapear un ResultSet a un objeto Usuario
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getInt("id_usuario"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setApellidos(rs.getString("apellidos"));
        usuario.setUsername(rs.getString("username"));
        usuario.setEmail(rs.getString("email"));
        usuario.setPassword(rs.getString("password"));
        usuario.setTelefono(rs.getString("telefono"));
        usuario.setIdRol(rs.getInt("id_rol"));
        int idDireccion = rs.getInt("id_direccion");
        usuario.setIdDireccion(rs.wasNull() ? null : idDireccion);
        usuario.setActivo(rs.getBoolean("activo"));
        usuario.setFechaRegistro(rs.getTimestamp("fecha_registro"));
        return usuario;
    }

    
    // ===================== OBTENER TODOS LOS USUARIOS =====================
    public ArrayList<Usuario> obtenerTodosUsuarios() throws SQLException {
        ArrayList<Usuario> objListaUsuarios = new ArrayList<>();
        objMySQLConnection.open();
        if (!objMySQLConnection.isError()) {
            String sql = "SELECT id_usuario, nombre, apellidos, username, email, password, telefono, id_rol, id_direccion, activo, fecha_registro FROM usuario";
            ResultSet objResultSet = objMySQLConnection.executeSelect(sql);
            try {
                while (objResultSet != null && objResultSet.next()) {
                    objListaUsuarios.add(mapearUsuario(objResultSet));
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
    // ===================== OBTENER USUARIO POR USERNAME =====================
    public Usuario obtenerUsuarioPorUsername(String username) {
        Usuario objUsuario = null;
        objMySQLConnection.open();
        if (!objMySQLConnection.isError()) {
                String query = "SELECT id_usuario, nombre, apellidos, username, email, password, telefono, id_rol, id_direccion, activo, fecha_registro FROM usuario WHERE username = '"
                    + username + "'";
            ResultSet resultSet = objMySQLConnection.executeSelect(query);
            try {
                if (resultSet != null && resultSet.next()) {
                    objUsuario = mapearUsuario(resultSet);
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

 
    // ===================== INSERTAR USUARIO =====================
    public void insertarUsuario(Usuario usuario) throws SQLException {
        objMySQLConnection.open();
        if (!objMySQLConnection.isError()) {
            String sql = String.format(
                    "INSERT INTO usuario (nombre, apellidos, username, email, password, telefono, id_rol, activo, fecha_registro) "
                            +
                            "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', %d, %b, '%s')",
                    usuario.getNombre(), usuario.getApellidos(), usuario.getUsername(), usuario.getEmail(),
                    usuario.getPassword(), usuario.getTelefono(), usuario.getIdRol(), usuario.isActivo(),
                    usuario.getFechaRegistro().toString());
            objMySQLConnection.executeInsert(sql);
        }
        objMySQLConnection.close();
    }
    // ===================== ELIMINAR USUARIO POR ID =====================
    public void eliminarUsuario(int idUsuario) throws SQLException {
        objMySQLConnection.open();
        if (!objMySQLConnection.isError()) {
            String sql = "DELETE FROM usuario WHERE id_usuario = " + idUsuario;
            objMySQLConnection.executeUpdateOrDelete(sql);
        }
        objMySQLConnection.close();
    }
    // ===================== ACTUALIZAR USUARIO =====================
    public void actualizarUsuario(Usuario usuario) throws SQLException {
        objMySQLConnection.open();
        if (!objMySQLConnection.isError()) {
            String sql = String.format(
                "UPDATE usuario SET nombre = '%s', apellidos = '%s', username = '%s', email = '%s', password = '%s', telefono = '%s', id_rol = %d, activo = %b, fecha_registro = '%s' WHERE id_usuario = %d",
                usuario.getNombre(), usuario.getApellidos(), usuario.getUsername(), usuario.getEmail(),
                usuario.getPassword(), usuario.getTelefono(), usuario.getIdRol(), usuario.isActivo(),
                usuario.getFechaRegistro().toString(), usuario.getIdUsuario()
            );
            objMySQLConnection.executeUpdateOrDelete(sql);
        }
        objMySQLConnection.close();
    }
    // ===================== OBTENER USUARIO POR ID  =====================
    public Usuario obtenerUsuarioPorId(int idUsuario) {
        Usuario objUsuario = null;
        objMySQLConnection.open();
        if (!objMySQLConnection.isError()) {
            String query = "SELECT id_usuario, nombre, apellidos, username, email, password, telefono, id_rol, activo, fecha_registro FROM usuario WHERE id_usuario = '" + idUsuario + "'";
            ResultSet resultSet = objMySQLConnection.executeSelect(query);
            try {
                if (resultSet != null && resultSet.next()) {
                    objUsuario = mapearUsuario(resultSet);
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
    // ===================== BUSCAR USUARIOS POR FILTRO =====================
    public ArrayList<Usuario> buscarUsuarios(String filtro) throws SQLException {
        ArrayList<Usuario> lista = new ArrayList<>();
        objMySQLConnection.open();
        if (!objMySQLConnection.isError()) {
            String sql = String.format(
                "SELECT id_usuario, nombre, apellidos, username, email, password, telefono, id_rol, id_direccion, activo, fecha_registro FROM usuario WHERE nombre LIKE '%%%s%%' OR apellidos LIKE '%%%s%%' OR username LIKE '%%%s%%' OR email LIKE '%%%s%%'",
                filtro, filtro, filtro, filtro
            );
            ResultSet rs = objMySQLConnection.executeSelect(sql);
            while (rs != null && rs.next()) {
                lista.add(mapearUsuario(rs));
            }
        }
        objMySQLConnection.close();
        return lista;
    }

}
