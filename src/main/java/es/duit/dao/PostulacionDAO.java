package es.duit.dao;

import es.duit.connections.MySqlConnection;
import es.duit.models.Postulacion;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.springframework.stereotype.Repository;

@Repository
public class PostulacionDAO {
    private MySqlConnection objMySQLConnection;

    public PostulacionDAO() {
        objMySQLConnection = new MySqlConnection();
    }

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

    public void insertarPostulacion(Postulacion postulacion) throws SQLException {
        objMySQLConnection.open();
        if (!objMySQLConnection.isError()) {
            String sql = String.format(
                    "INSERT INTO postulacion (id_solicitud, id_profesional, mensaje, precio_propuesto, fecha_postulacion, estado) VALUES (%d, %d, '%s', %.2f, NOW(), '%s')",
                    postulacion.getIdSolicitud(), postulacion.getIdProfesional(), postulacion.getMensaje(),
                    postulacion.getPrecioPropuesto(), postulacion.getEstado().name());
            objMySQLConnection.executeInsert(sql);
        }
        objMySQLConnection.close();
    }
}
