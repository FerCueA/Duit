
package es.duit.connections;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySqlConnection {

	private String host = "localhost";
	private String puerto = "3306";
	private String nameDB = "duit_db";
	private String usuario = "root";
	private String password = "";
	private boolean autocomit;
	private boolean flagError;
	private String msgError;
	private Connection connection;

	// Inicializa los parámetros por defecto
	private void _initialize() {
		this.flagError = false;
		this.msgError = "";
		this.connection = null;
	}

	// Inicializa la bandera y mensaje de error
	private void _initializeError() {
		this.flagError = false;
		this.msgError = "";
	}

	// Constructor por defecto
	public MySqlConnection() {
		this._initialize();
		this.autocomit = true;
	}

	// Constructor con autocomit
	public MySqlConnection(boolean _autocomit) {
		this._initialize();
		this.autocomit = _autocomit;
	}

	// Constructor con nombre de base de datos
	public MySqlConnection(String _nameDB) {
		this._initialize();
		this.nameDB = _nameDB;
		this.autocomit = true;
	}

	// Constructor con nombre de base de datos y autocomit
	public MySqlConnection(String _nameDB, boolean _autocomit) {
		this._initialize();
		this.nameDB = _nameDB;
		this.autocomit = _autocomit;
	}

	// Constructor con todos los parámetros de conexión
	public MySqlConnection(String _host, String _puerto, String _nameDB, String _usuario, String _password) {
		this._initialize();
		this.host = _host;
		this.puerto = _puerto;
		this.nameDB = _nameDB;
		this.usuario = _usuario;
		this.password = _password;
		this.autocomit = true;
	}

	// Constructor con todos los parámetros de conexión y autocomit
	public MySqlConnection(String _host, String _puerto, String _nameDB, String _usuario, String _password,
			boolean _autocomit) {
		this._initialize();
		this.host = _host;
		this.puerto = _puerto;
		this.nameDB = _nameDB;
		this.usuario = _usuario;
		this.password = _password;
		this.autocomit = _autocomit;
	}

	// Abre la conexión a la base de datos
	public void open() {
		try { // Se reinician las banderas de error.
			this._initializeError();
			// Si el objeto connection no es vacío o está cerrado se procede a abrirlo.
			if ((this.connection == null) || ((this.connection != null) && (this.connection.isClosed()))) {
				// Se carga el jdbc
				Class.forName("com.mysql.cj.jdbc.Driver");
				// Se indican los datos de conexión y se intenta obtener el objeto abierto (esta
				// instrucción es crítica)
				this.connection = DriverManager.getConnection(
						"jdbc:mysql://" + this.host + ":" + this.puerto + "/" + this.nameDB, this.usuario,
						this.password);
				// Se indica si se realizará autocomit (cada operación será independiente) o si
				// se estará en modo de transacción (delega en la capa de datos el commit).
				this.connection.setAutoCommit(this.autocomit);
			}
		} catch (ClassNotFoundException ex) {
			this.flagError = true;
			this.msgError = "Error al registrar el dricer. +Info: " + ex.getMessage();
		} catch (Exception ex) {
			this.flagError = true;
			this.msgError = "Error en Open. +Info: " + ex.getMessage();
		}
	}

	// Cierra la conexión a la base de datos
	public void close() {
		try {
			this._initializeError();
			if ((this.connection != null) && (!this.connection.isClosed()))
				this.connection.close();
		} catch (SQLException ex) {
			this.flagError = true;
			this.msgError = "Error en close. +Info: " + ex.getMessage();
		}

	}

	// Ejecuta una consulta SELECT y retorna un ResultSet
	public ResultSet executeSelect(String _sql) {
		try {
			this._initializeError();
			if (this.connection != null) {
				if (!this.connection.isClosed()) {
					java.sql.Statement objStament = this.connection.createStatement();
					ResultSet rs = objStament.executeQuery(_sql);
					return rs;
				} else {
					this.flagError = true;
					this.msgError = "Error en ExecuteSelect. +Info: Conexión cerrada.";
				}
			} else {
				this.flagError = true;
				this.msgError = "Error en ExecuteSelect. +Info: Conexión no inicializada.";
			}
		} catch (SQLException ex) {
			this.flagError = true;
			this.msgError = "Error en ExecuteSelect. +Info: " + ex.getMessage();
		}

		try {
			if ((this.flagError) && (!this.connection.getAutoCommit())) {
				this.connection.rollback();
			}
		} catch (SQLException ex) {
			this.flagError = true;
			this.msgError = "Error en intento de rollback en ExecuteSelect. +Info: " + ex.getMessage();
		}

		return null;
	}

	// Ejecuta una consulta INSERT y retorna las claves generadas
	public ResultSet executeInsert(String _sql) {

		try {
			this._initializeError();
			if (this.connection != null) {
				if (!this.connection.isClosed()) {
					PreparedStatement objStament = this.connection.prepareStatement(_sql,
							Statement.RETURN_GENERATED_KEYS);
					objStament.execute();
					ResultSet rs = objStament.getGeneratedKeys();
					return rs;
				} else {
					this.flagError = true;
					this.msgError = "Error en ExecuteQuery. +Info: Conexión cerrada.";
				}
			} else {
				this.flagError = true;
				this.msgError = "Error en ExecuteQuery. +Info: Conexión no inicializada.";
			}
		} catch (SQLException ex) {
			this.flagError = true;
			this.msgError = "Error en ExecuteQuery. +Info: " + ex.getMessage();
		}

		try {
			if ((this.flagError) && (!this.connection.getAutoCommit())) {
				this.connection.rollback();
			}
		} catch (SQLException ex) {
			this.flagError = true;
			this.msgError = "Error en intento de rollback en ExecuteQuery. +Info: " + ex.getMessage();
		}

		return null;
	}

	// Ejecuta una consulta UPDATE o DELETE y retorna el número de filas afectadas
	public int executeUpdateOrDelete(String _sql) {
		int NumRows = 0;
		try {
			this._initializeError();
			if (this.connection != null) {
				if (!this.connection.isClosed()) {
					PreparedStatement objStament = this.connection.prepareStatement(_sql);
					NumRows = objStament.executeUpdate();
				} else {
					this.flagError = true;
					this.msgError = "Error en executeUpdateOrDelete. +Info: Conexión cerrada.";
				}
			} else {
				this.flagError = true;
				this.msgError = "Error en executeUpdateOrDelete. +Info: Conexión no inicializada.";
			}
		} catch (SQLException ex) {
			this.flagError = true;
			this.msgError = "Error en executeUpdateOrDelete. +Info: " + ex.getMessage();
		}

		try {
			if ((this.flagError) && (!this.connection.getAutoCommit())) {
				this.connection.rollback();
			}
		} catch (SQLException ex) {
			this.flagError = true;
			this.msgError = "Error en intento de rollback en executeUpdateOrDelete. +Info: " + ex.getMessage();
		}

		return NumRows;
	}

	// Realiza commit si el autocomit está deshabilitado
	public void commit() {
		try {
			this._initializeError();
			if (!this.connection.getAutoCommit()) {
				this.connection.commit();
			}
		} catch (SQLException ex) {
			this.flagError = true;
			this.msgError = "Error en commit. +Info: " + ex.getMessage();
		}
	}

	// Realiza rollback si el autocomit está deshabilitado
	public void rollback() {
		try {
			this._initializeError();
			if (!this.connection.getAutoCommit()) {
				this.connection.rollback();
			}
		} catch (SQLException ex) {
			this.flagError = true;
			this.msgError = "Error en rollback. +Info: " + ex.getMessage();
		}
	}

	// Devuelve si hay error
	public boolean isError() {
		return this.flagError;
	}

	// Devuelve el mensaje de error
	public String msgError() {
		return this.msgError;
	}

}
