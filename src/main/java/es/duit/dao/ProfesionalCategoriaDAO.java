package es.duit.dao;

import es.duit.connections.MySqlConnection;
import es.duit.models.ProfesionalCategoria;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.springframework.stereotype.Repository;


@Repository
public class ProfesionalCategoriaDAO {
    // CONEXIÓN MYSQL
    private MySqlConnection objMySQLConnection;

    // CONSTRUCTOR
    public ProfesionalCategoriaDAO() {
        objMySQLConnection = new MySqlConnection();
    }

    // MAPEO DE RESULTSET A PROFESIONALCATEGORIA
    private ProfesionalCategoria mapearPC(ResultSet rs) throws SQLException {
        ProfesionalCategoria pc = new ProfesionalCategoria();
        pc.setIdProfesional(rs.getInt("id_profesional"));
        pc.setIdCategoria(rs.getInt("id_categoria"));
        return pc;
    }

    // OBTENER TODAS LAS RELACIONES PROFESIONAL-CATEGORÍA
    public ArrayList<ProfesionalCategoria> obtenerTodas() throws SQLException {
        ArrayList<ProfesionalCategoria> lista = new ArrayList<>();
        objMySQLConnection.open();
        if (!objMySQLConnection.isError()) {
            String sql = "SELECT * FROM profesional_categoria";
            ResultSet rs = objMySQLConnection.executeSelect(sql);
            while (rs != null && rs.next()) {
                lista.add(mapearPC(rs));
            }
        }
        objMySQLConnection.close();
        return lista;
    }

    // OBTENER RELACIÓN POR IDS
    public ProfesionalCategoria obtenerPorIds(int idProfesional, int idCategoria) throws SQLException {
        objMySQLConnection.open();
        ProfesionalCategoria pc = null;
        if (!objMySQLConnection.isError()) {
            String sql = String.format("SELECT * FROM profesional_categoria WHERE id_profesional = %d AND id_categoria = %d", idProfesional, idCategoria);
            ResultSet rs = objMySQLConnection.executeSelect(sql);
            if (rs != null && rs.next()) {
                pc = mapearPC(rs);
            }
        }
        objMySQLConnection.close();
        return pc;
    }
}
