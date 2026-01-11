
package es.duit.dao;

import es.duit.connections.MySqlConnection;
import es.duit.models.Valoracion;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.springframework.stereotype.Repository;
@Repository
public class ValoracionDAO {
    private MySqlConnection objMySQLConnection;

    public ValoracionDAO() {
        objMySQLConnection = new MySqlConnection();
    }

     private Valoracion mapearValoracion(ResultSet rs) throws SQLException {
        Valoracion valoracion = new Valoracion();
        valoracion.setIdValoracion(rs.getInt("id_valoracion"));
        valoracion.setIdTrabajo(rs.getInt("id_trabajo"));
        valoracion.setIdEmisor(rs.getInt("id_emisor"));
        valoracion.setIdReceptor(rs.getInt("id_receptor"));
        valoracion.setTipo(rs.getString("tipo"));
        valoracion.setPuntuacion(rs.getInt("puntuacion"));
        valoracion.setComentario(rs.getString("comentario"));
        valoracion.setFechaValoracion(rs.getTimestamp("fecha_valoracion"));
        return valoracion;
    }

    public Valoracion obtenerValoracionPorId(int id) throws SQLException {
        objMySQLConnection.open();
        Valoracion valoracion = null;
        if (!objMySQLConnection.isError()) {
            String sql = "SELECT * FROM valoracion WHERE id_valoracion = " + id;
            ResultSet rs = objMySQLConnection.executeSelect(sql);
            try {
                if (rs != null && rs.next()) {
                    valoracion = mapearValoracion(rs);
                }
            } catch (Exception e) {
                System.err.println("Error inesperado ejecutando la consulta SQL: " + sql);
                System.err.println("Mensaje de error: " + e.getMessage());
                e.printStackTrace();
            }
        }
        objMySQLConnection.close();
        return valoracion;
    }

    public ArrayList<Valoracion> obtenerValoracionesPorTrabajo(int idTrabajo) throws SQLException {
        ArrayList<Valoracion> lista = new ArrayList<>();
        objMySQLConnection.open();
        if (!objMySQLConnection.isError()) {
            String sql = "SELECT * FROM valoracion WHERE id_trabajo = " + idTrabajo;
            ResultSet rs = objMySQLConnection.executeSelect(sql);
            try {
                while (rs != null && rs.next()) {
                    lista.add(mapearValoracion(rs));
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

    public void insertarValoracion(Valoracion valoracion) throws SQLException {
        objMySQLConnection.open();
        if (!objMySQLConnection.isError()) {
            String sql = String.format(
                "INSERT INTO valoracion (id_trabajo, id_emisor, id_receptor, tipo, puntuacion, comentario, fecha_valoracion) VALUES (%d, %d, %d, '%s', %d, '%s', '%tF %<tT')",
                valoracion.getIdTrabajo(), valoracion.getIdEmisor(), valoracion.getIdReceptor(), valoracion.getTipo(), valoracion.getPuntuacion(), valoracion.getComentario(), valoracion.getFechaValoracion()
            );
            objMySQLConnection.executeInsert(sql);
        }
        objMySQLConnection.close();
    }

    public void actualizarValoracion(Valoracion valoracion) throws SQLException {
        objMySQLConnection.open();
        if (!objMySQLConnection.isError()) {
            String sql = String.format(
                "UPDATE valoracion SET id_trabajo = %d, id_emisor = %d, id_receptor = %d, tipo = '%s', puntuacion = %d, comentario = '%s', fecha_valoracion = '%tF %<tT' WHERE id_valoracion = %d",
                valoracion.getIdTrabajo(), valoracion.getIdEmisor(), valoracion.getIdReceptor(), valoracion.getTipo(), valoracion.getPuntuacion(), valoracion.getComentario(), valoracion.getFechaValoracion(), valoracion.getIdValoracion()
            );
            objMySQLConnection.executeUpdateOrDelete(sql);
        }
        objMySQLConnection.close();
    }

    public void eliminarValoracion(int id) throws SQLException {
        objMySQLConnection.open();
        if (!objMySQLConnection.isError()) {
            String sql = "DELETE FROM valoracion WHERE id_valoracion = " + id;
            objMySQLConnection.executeUpdateOrDelete(sql);
        }
        objMySQLConnection.close();
    }

   
}
