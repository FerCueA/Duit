package es.duit.dao;

import es.duit.connections.MySqlConnection;
import es.duit.models.PerfilProfesional;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.springframework.stereotype.Repository;

@Repository
public class PerfilProfesionalDAO {
    private MySqlConnection objMySQLConnection;

    public PerfilProfesionalDAO() {
        objMySQLConnection = new MySqlConnection();
    }

    private PerfilProfesional mapearPerfil(ResultSet rs) throws SQLException {
        PerfilProfesional perfil = new PerfilProfesional();
        perfil.setIdProfesional(rs.getInt("id_profesional"));
        perfil.setDescripcion(rs.getString("descripcion"));
        perfil.setPrecioHora(rs.getDouble("precio_hora"));
        perfil.setNif(rs.getString("nif"));
        perfil.setFechaAlta(rs.getTimestamp("fecha_alta"));
        return perfil;
    }

    public PerfilProfesional obtenerPorId(int id) throws SQLException {
        objMySQLConnection.open();
        PerfilProfesional perfil = null;
        if (!objMySQLConnection.isError()) {
            String sql = "SELECT * FROM perfil_profesional WHERE id_profesional = " + id;
            ResultSet rs = objMySQLConnection.executeSelect(sql);
            if (rs != null && rs.next()) {
                perfil = mapearPerfil(rs);
            }
        }
        objMySQLConnection.close();
        return perfil;
    }

    public ArrayList<PerfilProfesional> obtenerTodos() throws SQLException {
        ArrayList<PerfilProfesional> lista = new ArrayList<>();
        objMySQLConnection.open();
        if (!objMySQLConnection.isError()) {
            String sql = "SELECT * FROM perfil_profesional";
            ResultSet rs = objMySQLConnection.executeSelect(sql);
            while (rs != null && rs.next()) {
                lista.add(mapearPerfil(rs));
            }
        }
        objMySQLConnection.close();
        return lista;
    }
}
