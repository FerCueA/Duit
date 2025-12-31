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

    // Obtener todos los roles
    public ArrayList<Rol> obtenerTodosRoles() throws SQLException {

        ArrayList<Rol> listaRoles = new ArrayList<>();
        objMySQLConnection.open();

        if (!objMySQLConnection.isError()) {
            String sql = "SELECT id_rol, nombre, descripcion FROM rol";
            ResultSet resultSet = objMySQLConnection.executeSelect(sql);

            try {
                while (resultSet.next()) {
                    Rol objRol = new Rol();
                    objRol.setIdRol(resultSet.getInt("id_rol"));
                    objRol.setNombre(resultSet.getString("nombre"));
                    objRol.setDescripcion(resultSet.getString("descripcion"));
                    listaRoles.add(objRol);
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
                    objRol = new Rol();
                    objRol.setIdRol(objResultSet.getInt("id_rol"));
                    objRol.setNombre(objResultSet.getString("nombre"));
                    objRol.setDescripcion(objResultSet.getString("descripcion"));
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
