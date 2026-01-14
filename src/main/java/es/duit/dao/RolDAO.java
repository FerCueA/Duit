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

    // MAPEO DE RESULTSET A ROL
    private Rol mapearRol(ResultSet rs) throws SQLException {
        Rol objRol = new Rol();
        objRol.setIdRol(rs.getInt("id_rol"));
        objRol.setNombre(rs.getString("nombre"));
        objRol.setDescripcion(rs.getString("descripcion"));
        return objRol;
    }


    // OBTENER TODOS LOS ROLES
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

    // OBTENER ROL POR ID
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
