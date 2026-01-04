
package es.duit.dao;

import es.duit.connections.MySqlConnection;
import es.duit.models.Categoria;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CategoriaDAO {
    private MySqlConnection objMySQLConnection;

    public CategoriaDAO() {
        objMySQLConnection = new MySqlConnection();
    }

     private Categoria mapearCategoria(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria();
        categoria.setIdCategoria(rs.getInt("id_categoria"));
        categoria.setNombre(rs.getString("nombre"));
        categoria.setDescripcion(rs.getString("descripcion"));
        return categoria;
    }

    public Categoria obtenerCategoriaPorId(int id) throws SQLException {
        objMySQLConnection.open();
        Categoria categoria = null;
        if (!objMySQLConnection.isError()) {
            String sql = "SELECT * FROM categoria WHERE id_categoria = " + id;
            ResultSet rs = objMySQLConnection.executeSelect(sql);
            try {
                if (rs != null && rs.next()) {
                    categoria = mapearCategoria(rs);
                }
            } catch (Exception e) {
                System.err.println("Error inesperado ejecutando la consulta SQL: " + sql);
                System.err.println("Mensaje de error: " + e.getMessage());
                e.printStackTrace();
            }
        }
        objMySQLConnection.close();
        return categoria;
    }

    public ArrayList<Categoria> obtenerTodasCategorias() throws SQLException {
        ArrayList<Categoria> lista = new ArrayList<>();
        objMySQLConnection.open();
        if (!objMySQLConnection.isError()) {
            String sql = "SELECT * FROM categoria";
            ResultSet rs = objMySQLConnection.executeSelect(sql);
            try {
                while (rs != null && rs.next()) {
                    lista.add(mapearCategoria(rs));
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

    public void insertarCategoria(Categoria categoria) throws SQLException {
        objMySQLConnection.open();
        if (!objMySQLConnection.isError()) {
            String sql = String.format(
                "INSERT INTO categoria (nombre, descripcion) VALUES ('%s', '%s')",
                categoria.getNombre(), categoria.getDescripcion()
            );
            objMySQLConnection.executeInsert(sql);
        }
        objMySQLConnection.close();
    }

    public void actualizarCategoria(Categoria categoria) throws SQLException {
        objMySQLConnection.open();
        if (!objMySQLConnection.isError()) {
            String sql = String.format(
                "UPDATE categoria SET nombre = '%s', descripcion = '%s' WHERE id_categoria = %d",
                categoria.getNombre(), categoria.getDescripcion(), categoria.getIdCategoria()
            );
            objMySQLConnection.executeUpdateOrDelete(sql);
        }
        objMySQLConnection.close();
    }

    public void eliminarCategoria(int id) throws SQLException {
        objMySQLConnection.open();
        if (!objMySQLConnection.isError()) {
            String sql = "DELETE FROM categoria WHERE id_categoria = " + id;
            objMySQLConnection.executeUpdateOrDelete(sql);
        }
        objMySQLConnection.close();
    }

   
}
