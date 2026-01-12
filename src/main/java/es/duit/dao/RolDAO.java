package es.duit.dao;

import es.duit.connections.MySqlConnection;
import es.duit.models.Rol;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.springframework.stereotype.Repository;

@Repository
public class RolDAO {

    private MySqlConnection objMySQLConnection;

    public RolDAO() {
        objMySQLConnection = new MySqlConnection();
    }

    // Método para mapear un ResultSet a un objeto Rol
    private Rol mapearRol(ResultSet rs) throws SQLException {
        Rol objRol = new Rol();
        objRol.setIdRol(rs.getInt("id_rol"));
        objRol.setNombre(rs.getString("nombre"));
        objRol.setDescripcion(rs.getString("descripcion"));
        return objRol;
    }

    public Rol obtenerPorId(int id) throws SQLException {
        objMySQLConnection.open();
        Rol rol = null;
        if (!objMySQLConnection.isError()) {
            String sql = "SELECT * FROM rol WHERE id_rol = " + id;
            ResultSet rs = objMySQLConnection.executeSelect(sql);
            if (rs != null && rs.next()) {
                rol = mapearRol(rs);
            }
        }
        objMySQLConnection.close();
        return rol;
    }

    public ArrayList<Rol> obtenerTodos() throws SQLException {
        ArrayList<Rol> lista = new ArrayList<>();
        objMySQLConnection.open();
        if (!objMySQLConnection.isError()) {
            String sql = "SELECT * FROM rol";
            ResultSet rs = objMySQLConnection.executeSelect(sql);
            while (rs != null && rs.next()) {
                lista.add(mapearRol(rs));
            }
        }
        objMySQLConnection.close();
        return lista;
    }

    // Obtener todos los roles
    public ArrayList<Rol> obtenerTodosRoles() throws SQLException {
        ArrayList<Rol> listaRoles = new ArrayList<>();
        objMySQLConnection.open();
        if (!objMySQLConnection.isError()) {
            String sql = "SELECT id_rol, nombre, descripcion FROM rol";
            ResultSet resultSet = objMySQLConnection.executeSelect(sql);
            try {
                while (resultSet != null && resultSet.next()) {
                    listaRoles.add(mapearRol(resultSet));
                }
            } catch (Exception e) {
                System.err.println("Error inesperado ejecutando la consulta SQL: " + sql);
                System.err.println("Mensaje de error: " + e.getMessage());
                e.printStackTrace();
            }
        }
        objMySQLConnection.close();
        return listaRoles;
    }

    // Obtener un rol por su ID
    public Rol obtenerRolPorId(int idRol) throws SQLException {
        Rol objRol = null;
        objMySQLConnection.open();
        if (!objMySQLConnection.isError()) {
            String sql = "SELECT id_rol, nombre, descripcion FROM rol WHERE id_rol = " + idRol;
            ResultSet objResultSet = objMySQLConnection.executeSelect(sql);
            try {
                if (objResultSet.next()) {
                    objRol = mapearRol(objResultSet);
                }
            } catch (Exception e) {
                System.err.println("Error inesperado ejecutando la consulta SQL: " + sql);
                System.err.println("Mensaje de error: " + e.getMessage());
                e.printStackTrace();
            }
        }
        objMySQLConnection.close();
        return objRol;
    }

    

}
