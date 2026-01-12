
package es.duit.dao;

import es.duit.connections.MySqlConnection;
import es.duit.models.Solicitud;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.springframework.stereotype.Repository;
@Repository
public class SolicitudDAO {
    private MySqlConnection objMySQLConnection;

    public SolicitudDAO() {
        objMySQLConnection = new MySqlConnection();
    }

    private Solicitud mapearSolicitud(ResultSet rs) throws SQLException {
        Solicitud solicitud = new Solicitud();
        solicitud.setIdSolicitud(rs.getInt("id_solicitud"));
        solicitud.setIdCliente(rs.getInt("id_cliente"));
        solicitud.setIdCategoria(rs.getInt("id_categoria"));
        solicitud.setTitulo(rs.getString("titulo"));
        solicitud.setDescripcion(rs.getString("descripcion"));
        solicitud.setFechaSolicitud(rs.getTimestamp("fecha_solicitud"));
        solicitud.setFechaActualizacion(rs.getTimestamp("fecha_actualizacion"));
        String estadoStr = rs.getString("estado");
        solicitud.setEstado(Solicitud.EstadoSolicitud.valueOf(estadoStr));
        return solicitud;
    }

    public Solicitud obtenerPorId(int id) throws SQLException {
        objMySQLConnection.open();
        Solicitud solicitud = null;
        if (!objMySQLConnection.isError()) {
            String sql = "SELECT * FROM solicitud WHERE id_solicitud = " + id;
            ResultSet rs = objMySQLConnection.executeSelect(sql);
            if (rs != null && rs.next()) {
                solicitud = mapearSolicitud(rs);
            }
        }
        objMySQLConnection.close();
        return solicitud;
    }

    public ArrayList<Solicitud> obtenerTodas() throws SQLException {
        ArrayList<Solicitud> lista = new ArrayList<>();
        objMySQLConnection.open();
        if (!objMySQLConnection.isError()) {
            String sql = "SELECT * FROM solicitud";
            ResultSet rs = objMySQLConnection.executeSelect(sql);
            while (rs != null && rs.next()) {
                lista.add(mapearSolicitud(rs));
            }
        }
        objMySQLConnection.close();
        return lista;
    }

    public Solicitud obtenerSolicitudPorId(int id) throws SQLException {
        objMySQLConnection.open();
        Solicitud solicitud = null;
        if (!objMySQLConnection.isError()) {
            String sql = "SELECT * FROM solicitud WHERE id_solicitud = " + id;
            ResultSet rs = objMySQLConnection.executeSelect(sql);
            try {
                if (rs != null && rs.next()) {
                    solicitud = mapearSolicitud(rs);
                }
            } catch (Exception e) {
                System.err.println("Error inesperado ejecutando la consulta SQL: " + sql);
                System.err.println("Mensaje de error: " + e.getMessage());
                e.printStackTrace();
            }
        }
        objMySQLConnection.close();
        return solicitud;
    }

    public ArrayList<Solicitud> obtenerSolicitudesPorCliente(int idCliente) throws SQLException {
        ArrayList<Solicitud> lista = new ArrayList<>();
        objMySQLConnection.open();
        if (!objMySQLConnection.isError()) {
            String sql = "SELECT * FROM solicitud WHERE id_cliente = " + idCliente;
            ResultSet rs = objMySQLConnection.executeSelect(sql);
            try {
                while (rs != null && rs.next()) {
                    lista.add(mapearSolicitud(rs));
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

    public void insertarSolicitud(Solicitud solicitud) throws SQLException {
        objMySQLConnection.open();
        if (!objMySQLConnection.isError()) {
            String sql = String.format(
                "INSERT INTO solicitud (id_cliente, id_categoria, titulo, descripcion, fecha_solicitud, estado) VALUES (%d, %d, '%s', '%s', '%tF %<tT', '%s')",
                solicitud.getIdCliente(), solicitud.getIdCategoria(), solicitud.getTitulo(), solicitud.getDescripcion(), solicitud.getFechaSolicitud(), solicitud.getEstado().name()
            );
            objMySQLConnection.executeInsert(sql);
        }
        objMySQLConnection.close();
    }

    public void actualizarSolicitud(Solicitud solicitud) throws SQLException {
        objMySQLConnection.open();
        if (!objMySQLConnection.isError()) {
            String sql = String.format(
                "UPDATE solicitud SET id_cliente = %d, id_categoria = %d, titulo = '%s', descripcion = '%s', fecha_solicitud = '%tF %<tT', estado = '%s' WHERE id_solicitud = %d",
                solicitud.getIdCliente(), solicitud.getIdCategoria(), solicitud.getTitulo(), solicitud.getDescripcion(), solicitud.getFechaSolicitud(), solicitud.getEstado(), solicitud.getIdSolicitud()
            );
            objMySQLConnection.executeUpdateOrDelete(sql);
        }
        objMySQLConnection.close();
    }

    public void eliminarSolicitud(int id) throws SQLException {
        objMySQLConnection.open();
        if (!objMySQLConnection.isError()) {
            String sql = "DELETE FROM solicitud WHERE id_solicitud = " + id;
            objMySQLConnection.executeUpdateOrDelete(sql);
        }
        objMySQLConnection.close();
    }

    public ArrayList<Solicitud> obtenerTodasSolicitudes() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'obtenerTodasSolicitudes'");
    }

   
}
