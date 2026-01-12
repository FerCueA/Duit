
package es.duit.dao;

import es.duit.connections.MySqlConnection;
import es.duit.models.Trabajo;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.springframework.stereotype.Repository;

@Repository
public class TrabajoDAO {
    private MySqlConnection objMySQLConnection;

    public TrabajoDAO() {
        objMySQLConnection = new MySqlConnection();
    }


    private Trabajo mapearTrabajo(ResultSet rs) throws SQLException {
        Trabajo trabajo = new Trabajo();
        trabajo.setIdTrabajo(rs.getInt("id_trabajo"));
        trabajo.setIdPostulacion(rs.getInt("id_postulacion"));
        trabajo.setPrecioAcordado(rs.getDouble("precio_acordado"));
        trabajo.setFechaInicio(rs.getTimestamp("fecha_inicio"));
        trabajo.setFechaFin(rs.getTimestamp("fecha_fin"));
        trabajo.setObservaciones(rs.getString("observaciones"));
        String estadoStr = rs.getString("estado");
        trabajo.setEstado(Trabajo.EstadoTrabajo.valueOf(estadoStr));
        return trabajo;
    }

    public Trabajo obtenerPorId(int id) throws SQLException {
        objMySQLConnection.open();
        Trabajo trabajo = null;
        if (!objMySQLConnection.isError()) {
            String sql = "SELECT * FROM trabajo WHERE id_trabajo = " + id;
            ResultSet rs = objMySQLConnection.executeSelect(sql);
            if (rs != null && rs.next()) {
                trabajo = mapearTrabajo(rs);
            }
        }
        objMySQLConnection.close();
        return trabajo;
    }

    public ArrayList<Trabajo> obtenerTodos() throws SQLException {
        ArrayList<Trabajo> lista = new ArrayList<>();
        objMySQLConnection.open();
        if (!objMySQLConnection.isError()) {
            String sql = "SELECT * FROM trabajo";
            ResultSet rs = objMySQLConnection.executeSelect(sql);
            while (rs != null && rs.next()) {
                lista.add(mapearTrabajo(rs));
            }
        }
        objMySQLConnection.close();
        return lista;
    }
    public Trabajo obtenerTrabajoPorId(int id) throws SQLException {
        objMySQLConnection.open();
        Trabajo trabajo = null;
        if (!objMySQLConnection.isError()) {
            String sql = "SELECT * FROM trabajo WHERE id_trabajo = " + id;
            ResultSet rs = objMySQLConnection.executeSelect(sql);
            try {
                if (rs != null && rs.next()) {
                    trabajo = mapearTrabajo(rs);
                }
            } catch (Exception e) {
                System.err.println("Error inesperado ejecutando la consulta SQL: " + sql);
                System.err.println("Mensaje de error: " + e.getMessage());
                e.printStackTrace();
            }
        }
        objMySQLConnection.close();
        return trabajo;
    }

    public ArrayList<Trabajo> obtenerTrabajosPorSolicitud(int idSolicitud) throws SQLException {
        ArrayList<Trabajo> lista = new ArrayList<>();
        objMySQLConnection.open();
        // La tabla trabajo ahora tiene id_postulacion, no id_solicitud
        // Si necesitas obtener trabajos por solicitud, deberías hacer un JOIN con postulacion
        if (!objMySQLConnection.isError()) {
            String sql = "SELECT t.* FROM trabajo t JOIN postulacion p ON t.id_postulacion = p.id_postulacion WHERE p.id_solicitud = " + idSolicitud;
            ResultSet rs = objMySQLConnection.executeSelect(sql);
            try {
                while (rs != null && rs.next()) {
                    lista.add(mapearTrabajo(rs));
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

    public void insertarTrabajo(Trabajo trabajo) throws SQLException {
        objMySQLConnection.open();
        if (!objMySQLConnection.isError()) {
            String sql = String.format(
                "INSERT INTO trabajo (id_postulacion, precio_acordado, fecha_inicio, fecha_fin, estado, observaciones) VALUES (%d, %.2f, %s, %s, '%s', '%s')",
                trabajo.getIdPostulacion(), trabajo.getPrecioAcordado(),
                trabajo.getFechaInicio() != null ? String.format("'%tF %<tT'", trabajo.getFechaInicio()) : "NULL",
                trabajo.getFechaFin() != null ? String.format("'%tF %<tT'", trabajo.getFechaFin()) : "NULL",
                trabajo.getEstado().name(),
                trabajo.getObservaciones() != null ? trabajo.getObservaciones().replace("'", "''") : ""
            );
            objMySQLConnection.executeInsert(sql);
        }
        objMySQLConnection.close();
    }

    public void actualizarTrabajo(Trabajo trabajo) throws SQLException {
        objMySQLConnection.open();
        if (!objMySQLConnection.isError()) {
            String sql = String.format(
                "UPDATE trabajo SET id_postulacion = %d, precio_acordado = %.2f, fecha_inicio = %s, fecha_fin = %s, estado = '%s', observaciones = '%s' WHERE id_trabajo = %d",
                trabajo.getIdPostulacion(), trabajo.getPrecioAcordado(),
                trabajo.getFechaInicio() != null ? String.format("'%tF %<tT'", trabajo.getFechaInicio()) : "NULL",
                trabajo.getFechaFin() != null ? String.format("'%tF %<tT'", trabajo.getFechaFin()) : "NULL",
                trabajo.getEstado().name(),
                trabajo.getObservaciones() != null ? trabajo.getObservaciones().replace("'", "''") : "",
                trabajo.getIdTrabajo()
            );
            objMySQLConnection.executeUpdateOrDelete(sql);
        }
        objMySQLConnection.close();
    }

    public void eliminarTrabajo(int id) throws SQLException {
        objMySQLConnection.open();
        if (!objMySQLConnection.isError()) {
            String sql = "DELETE FROM trabajo WHERE id_trabajo = " + id;
            objMySQLConnection.executeUpdateOrDelete(sql);
        }
        objMySQLConnection.close();
    }

  
}
