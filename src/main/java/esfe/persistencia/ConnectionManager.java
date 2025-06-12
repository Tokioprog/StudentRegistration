package esfe.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {

    // Cadena de conexión al servidor SQL Server alojado en Somee.com
    // Incluye datos como el servidor, base de datos, usuario, contraseña y configuraciones de seguridad.
    private static final String STR_CONNECTION =
            "jdbc:sqlserver://StudentRegistration.mssql.somee.com:1433;" + // Dirección y puerto del servidor
                    "database=StudentRegistration;" +                       // Nombre de la base de datos
                    "user=roberto117_SQLLogin_1;" +                        // Usuario de la base de datos
                    "password=vu88zc6rsa;" +                               // Contraseña del usuario
                    "encrypt=true;" +                                      // Encripta la conexión
                    "trustServerCertificate=true;" +                       // Confía en el certificado del servidor
                    "loginTimeout=30;";                                    // Tiempo de espera de conexión (en segundos)

    private Connection connection;         // Objeto que mantiene la conexión activa
    private static ConnectionManager instance; // Instancia única (Singleton)

    // Constructor privado para implementar el patrón Singleton
    private ConnectionManager() {
        this.connection = null;
        try {
            // Carga la clase del driver JDBC de SQL Server
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            // Si el driver no se encuentra, lanza una excepción de tiempo de ejecución
            throw new RuntimeException("Error al cargar el driver JDBC de SQL Server", e);
        }
    }

    // Método para obtener la conexión activa. Si no existe o está cerrada, se crea una nueva.
    public synchronized Connection connect() throws SQLException {
        if (this.connection == null || this.connection.isClosed()) {
            try {
                this.connection = DriverManager.getConnection(STR_CONNECTION);
            } catch (SQLException exception) {
                // Si ocurre un error al conectar, se lanza una excepción con más contexto
                throw new SQLException("Error al conectar a la base de datos: " + exception.getMessage(), exception);
            }
        }
        return this.connection;
    }

    // Método para cerrar la conexión si está activa
    public void disconnect() throws SQLException {
        if (this.connection != null) {
            try {
                this.connection.close(); // Intenta cerrar la conexión
            } catch (SQLException exception) {
                throw new SQLException("Error al cerrar la conexión: " + exception.getMessage(), exception);
            } finally {
                this.connection = null; // Libera la referencia
            }
        }
    }

    // Método estático para obtener la única instancia de ConnectionManager
    public static synchronized ConnectionManager getInstance() {
        if (instance == null) {
            instance = new ConnectionManager(); // Crea la instancia si no existe
        }
        return instance;
    }
}
