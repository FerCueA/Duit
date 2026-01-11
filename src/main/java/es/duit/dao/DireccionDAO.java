
package es.duit.dao;

import es.duit.connections.MySqlConnection;
import es.duit.models.Direccion;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.springframework.stereotype.Repository;

@Repository
public class DireccionDAO {
    private MySqlConnection objMySQLConnection;

    public DireccionDAO() {
        objMySQLConnection = new MySqlConnection();
    }

      private Direccion mapearDireccion(ResultSet rs) throws SQLException {
        Direccion direccion = new Direccion();
        direccion.setIdDireccion(rs.getInt("id_direccion"));
        direccion.setIdUsuario(rs.getInt("id_usuario"));
        direccion.setDireccion(rs.getString("direccion"));
        direccion.setCiudad(rs.getString("ciudad"));
        direccion.setCodigoPostal(rs.getString("codigo_postal"));
        return direccion;
    }
    public Direccion obtenerDireccionPorId(int id) throws SQLException {
        objMySQLConnection.open();
        Direccion direccion = null;
        if (!objMySQLConnection.isError()) {
            String sql = "SELECT * FROM direccion WHERE id_direccion = " + id;
            ResultSet rs = objMySQLConnection.executeSelect(sql);
            try {
                if (rs != null && rs.next()) {
                    direccion = mapearDireccion(rs);
                }
            } catch (Exception e) {
                System.err.println("Error inesperado ejecutando la consulta SQL: " + sql);
                System.err.println("Mensaje de error: " + e.getMessage());
                e.printStackTrace();
            }
        }
        objMySQLConnection.close();
        return direccion;
    }

    public ArrayList<Direccion> obtenerDireccionesPorUsuario(int idUsuario) throws SQLException {
        ArrayList<Direccion> lista = new ArrayList<>();
        objMySQLConnection.open();
        if (!objMySQLConnection.isError()) {
            String sql = "SELECT * FROM direccion WHERE id_usuario = " + idUsuario;
            ResultSet rs = objMySQLConnection.executeSelect(sql);
            try {
                while (rs != null && rs.next()) {
                    lista.add(mapearDireccion(rs));
                }
            } catch (Exception e) {
                System.err.println("Error inesperado ejecutando la consulta SQL: " + sql);
                System.err.println("Mensaje de error: " + e.getMessage());
                e.printStackTrace();
            }
        }
        objMySQLConnection.close();
        return lista;
    }

    public void insertarDireccion(Direccion direccion) throws SQLException {
        objMySQLConnection.open();
        if (!objMySQLConnection.isError()) {
            String sql = String.format(
                "INSERT INTO direccion (id_usuario, direccion, ciudad, codigo_postal) VALUES (%d, '%s', '%s', '%s')",
                direccion.getIdUsuario(), direccion.getDireccion(), direccion.getCiudad(), direccion.getCodigoPostal()
            );
            objMySQLConnection.executeInsert(sql);
        }
        objMySQLConnection.close();
    }

    public void actualizarDireccion(Direccion direccion) throws SQLException {
        objMySQLConnection.open();
        if (!objMySQLConnection.isError()) {
            String sql = String.format(
                "UPDATE direccion SET id_usuario = %d, direccion = '%s', ciudad = '%s', codigo_postal = '%s' WHERE id_direccion = %d",
                direccion.getIdUsuario(), direccion.getDireccion(), direccion.getCiudad(), direccion.getCodigoPostal(), direccion.getIdDireccion()
            );
            objMySQLConnection.executeUpdateOrDelete(sql);
        }
        objMySQLConnection.close();
    }

    public void eliminarDireccion(int id) throws SQLException {
        objMySQLConnection.open();
        if (!objMySQLConnection.isError()) {
            String sql = "DELETE FROM direccion WHERE id_direccion = " + id;
            objMySQLConnection.executeUpdateOrDelete(sql);
        }
        objMySQLConnection.close();
    }

  
}
