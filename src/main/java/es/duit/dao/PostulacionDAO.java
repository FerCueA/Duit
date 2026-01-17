package es.duit.dao;

import es.duit.connections.MySqlConnection;
import es.duit.models.Postulacion;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.springframework.stereotype.Repository;

@Repository
public class PostulacionDAO {
    // CONEXIÓN MYSQL
    private MySqlConnection objMySQLConnection;

    // CONSTRUCTOR
    public PostulacionDAO() {
        objMySQLConnection = new MySqlConnection();
    }

    // MAPEO DE RESULTSET A POSTULACION
    private Postulacion mapearPostulacion(ResultSet rs) throws SQLException {
        Postulacion postulacion = new Postulacion();
        postulacion.setIdPostulacion(rs.getInt("id_postulacion"));
        postulacion.setIdSolicitud(rs.getInt("id_solicitud"));
        postulacion.setIdProfesional(rs.getInt("id_profesional"));
        postulacion.setMensaje(rs.getString("mensaje"));
        postulacion.setPrecioPropuesto(rs.getDouble("precio_propuesto"));
        postulacion.setFechaPostulacion(rs.getTimestamp("fecha_postulacion"));
        String estadoStr = rs.getString("estado");
        postulacion.setEstado(Postulacion.EstadoPostulacion.valueOf(estadoStr));
        return postulacion;
    }

    // OBTENER POSTULACIÓN POR ID
    public Postulacion obtenerPorId(int id) throws SQLException {
        objMySQLConnection.open();
        Postulacion postulacion = null;
        if (!objMySQLConnection.isError()) {
            String sql = "SELECT * FROM postulacion WHERE id_postulacion = " + id;
            ResultSet rs = objMySQLConnection.executeSelect(sql);
            if (rs != null && rs.next()) {
                postulacion = mapearPostulacion(rs);
            }
        }
        objMySQLConnection.close();
        return postulacion;
    }

    // OBTENER TODAS LAS POSTULACIONES
    public ArrayList<Postulacion> obtenerTodas() throws SQLException {
        ArrayList<Postulacion> lista = new ArrayList<>();
        objMySQLConnection.open();
        if (!objMySQLConnection.isError()) {
            String sql = "SELECT * FROM postulacion";
            ResultSet rs = objMySQLConnection.executeSelect(sql);
            while (rs != null && rs.next()) {
                lista.add(mapearPostulacion(rs));
            }
        }
        objMySQLConnection.close();
        return lista;
    }

    // INSERTAR POSTULACIÓN
    public void insertarPostulacion(Postulacion postulacion) throws SQLException {
        objMySQLConnection.open();
        if (!objMySQLConnection.isError()) {
            String mensajeSeguro = postulacion.getMensaje().replace("'", "''");
            // Usar punto como separador decimal
            String precioStr = String.format(java.util.Locale.US, "%.2f", postulacion.getPrecioPropuesto());
            String sql = String.format(
                    "INSERT INTO postulacion (id_solicitud, id_profesional, mensaje, precio_propuesto, fecha_postulacion, estado) VALUES (%d, %d, '%s', %s, NOW(), '%s')",
                    postulacion.getIdSolicitud(), postulacion.getIdProfesional(), mensajeSeguro,
                    precioStr, postulacion.getEstado().name());
            System.out.println("[DEBUG] SQL a ejecutar: " + sql);
            objMySQLConnection.executeInsert(sql);
            if (objMySQLConnection.isError()) {
                System.err.println("[ERROR] Error al insertar postulación: " + objMySQLConnection.msgError());
            } else {
                System.out.println("[DEBUG] Postulación insertada correctamente");
            }
        } else {
            System.err.println(
                    "[ERROR] Error de conexión antes de insertar postulación: " + objMySQLConnection.msgError());
        }
        objMySQLConnection.close();
    }

    // OBTENER POSTULACIONES POR SOLICITUD
    public ArrayList<Postulacion> obtenerPorSolicitud(int idSolicitud) throws SQLException {
        ArrayList<Postulacion> lista = new ArrayList<>();
        objMySQLConnection.open();
        if (!objMySQLConnection.isError()) {
            String sql = "SELECT * FROM postulacion WHERE id_solicitud = " + idSolicitud;
            ResultSet rs = objMySQLConnection.executeSelect(sql);
            while (rs != null && rs.next()) {
                lista.add(mapearPostulacion(rs));
            }
        }
        objMySQLConnection.close();
        return lista;
    }

    // CANCELAR POSTULACIÓN
    public void rechazarPostulacion(int idPostulacion) throws SQLException {
        objMySQLConnection.open();
        if (!objMySQLConnection.isError()) {
            String sql = "UPDATE postulacion SET estado = 'RECHAZADA' WHERE id_postulacion = " + idPostulacion;
            objMySQLConnection.executeInsert(sql);
        }
        objMySQLConnection.close();
    }

    // ACEPTAR POSTULACIÓN
    public void aceptarPostulacion(int idPostulacion) throws SQLException {
        objMySQLConnection.open();
        if (!objMySQLConnection.isError()) {
            String sql = "UPDATE postulacion SET estado = 'ACEPTADA' WHERE id_postulacion = " + idPostulacion;
            objMySQLConnection.executeInsert(sql);
        }
        objMySQLConnection.close();
    }
}
