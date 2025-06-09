package esfe.persistencia;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import esfe.dominio.User;
import esfe.utils.PasswordHasher;

public class UserDAO {
    private ConnectionManager conn;
    private PreparedStatement ps;
    private ResultSet rs;

    public UserDAO(){
        conn = ConnectionManager.getInstance();
    }

    public User create(User user) throws SQLException {
        User res = null; // Variable para almacenar el usuario creado que se retornará.
        try{
            PreparedStatement ps = conn.connect().prepareStatement(
                    "INSERT INTO " +
                            "Users (FullName, PasswordHash, Email, Status)" +
                            "VALUES (?, ?, ?, ?)",
                    java.sql.Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, user.getName()); // Asignar el nombre del usuario.
            ps.setString(2, PasswordHasher.hashPassword(user.getPasswordHash())); // Hashear la contraseña antes de guardarla.
            ps.setString(3, user.getEmail()); // Asignar el correo electrónico del usuario.
            ps.setByte(4, user.getStatus());   // Asignar el estado del usuario.

            // Ejecutar la sentencia de inserción y obtener el número de filas afectadas.
            int affectedRows = ps.executeUpdate();

            // Verificar si la inserción fue exitosa
            if (affectedRows != 0) {
                // Obtener las claves generadas automáticamente por la base de datos (en este caso, el ID).
                ResultSet  generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    // Obtener el ID generado. Generalmente la primera columna contiene la clave primaria.
                    int idGenerado= generatedKeys.getInt(1);
                    // Recuperar el usuario completo utilizando el ID generado.
                    res = getById(idGenerado);
                } else {
                    // Lanzar una excepción si la creación del usuario falló y no se obtuvo un ID.
                    throw new SQLException("Error al crear el usuario, no se obtuvo ninguna identificación.");
                }
            }
            ps.close();
        }catch (SQLException ex){
            throw new SQLException("Error al crear el usuario: " + ex.getMessage(), ex);
        } finally {
            // Bloque finally para asegurar que los recursos se liberen.
            ps = null;         // Establecer la sentencia preparada a null.
            conn.disconnect(); // Desconectar de la base de datos.
        }
        return res; // Retornar el usuario creado (con su ID asignado) o null si hubo un error.
    }

    public boolean update(User user) throws SQLException{
        boolean res = false; // Variable para indicar si la actualización fue exitosa.
        try{
            // Preparar la sentencia SQL para actualizar la información de un usuario.
            ps = conn.connect().prepareStatement(
                    "UPDATE Users " +
                            "SET FullName = ?, Email = ?, Status = ? " +
                            "WHERE UserId = ?"
            );
            ps.setString(1, user.getName());  // Asignar el nuevo nombre del usuario.
            ps.setString(2, user.getEmail()); // Asignar el nuevo correo electrónico del usuario.
            ps.setByte(3, user.getStatus());    // Asignar el nuevo estado del usuario.
            ps.setInt(4, user.getId());       // Establecer la condición WHERE para identificar el usuario a actualizar por su ID.

            // Ejecutar la sentencia de actualización y verificar si se afectó alguna fila.
            if(ps.executeUpdate() > 0){
                res = true; // Si executeUpdate() retorna un valor mayor que 0, significa que la actualización fue exitosa.
            }
            ps.close(); // Cerrar la sentencia preparada para liberar recursos.
        }catch (SQLException ex){
            // Capturar cualquier excepción SQL que ocurra durante el proceso.
            throw new SQLException("Error al modificar el usuario: " + ex.getMessage(), ex);
        } finally {
            // Bloque finally para asegurar que los recursos se liberen.
            ps = null;         // Establecer la sentencia preparada a null.
            conn.disconnect(); // Desconectar de la base de datos.
        }

        return res;
    }

    public boolean delete(User user) throws SQLException{
        boolean res = false; // Variable para indicar si la eliminación fue exitosa.
        try{
            ps = conn.connect().prepareStatement(
                    "DELETE FROM Users WHERE UserId = ?"
            );
            ps.setInt(1, user.getId());

            if(ps.executeUpdate() > 0){
                res = true; // Si executeUpdate() retorna un valor mayor que 0, significa que la eliminación fue exitosa.
            }
            ps.close();
        }catch (SQLException ex){
            throw new SQLException("Error al eliminar el usuario: " + ex.getMessage(), ex);
        } finally {
            ps = null;         // Establecer la sentencia preparada a null.
            conn.disconnect(); // Desconectar de la base de datos.
        }

        return res;
    }

    public ArrayList<User> search(String name) throws SQLException{
        ArrayList<User> records  = new ArrayList<>(); // Lista para almacenar los usuarios encontrados.

        try {
            // Preparar la sentencia SQL para buscar usuarios por nombre (usando LIKE para búsqueda parcial).
            ps = conn.connect().prepareStatement("SELECT UserId, FullName, Email, Status " +
                    "FROM Users " +
                    "WHERE FullName LIKE ?");
            // El '%' al inicio y al final permiten la búsqueda de la cadena 'name' en cualquier parte del nombre del usuario.
            ps.setString(1, "%" + name + "%");

            rs = ps.executeQuery();

            // Iterar a través de cada fila del resultado.
            while (rs.next()){
                // Crear un nuevo objeto User para cada registro encontrado.
                User user = new User();
                // Asignar los valores de las columnas a los atributos del objeto User.
                user.setId(rs.getInt(1));       // Obtener el ID del usuario.
                user.setName(rs.getString(2));   // Obtener el nombre del usuario.
                user.setEmail(rs.getString(3));  // Obtener el correo electrónico del usuario.
                user.setStatus(rs.getByte(4));    // Obtener el estado del usuario.
                // Agregar el objeto User a la lista de resultados.
                records.add(user);
            }
            ps.close();
            rs.close();
        } catch (SQLException ex){
            throw new SQLException("Error al buscar usuarios: " + ex.getMessage(), ex);
        } finally {
            // Bloque finally para asegurar que los recursos se liberen.
            ps = null;         // Establecer la sentencia preparada a null.
            rs = null;         // Establecer el conjunto de resultados a null.
            conn.disconnect(); // Desconectar de la base de datos.
        }
        return records; // Retornar la lista de usuarios encontrados.
    }
    public User getById(int id) throws SQLException{
        User user  = new User(); // Inicializar un objeto User que se retornará.

        try {
            ps = conn.connect().prepareStatement("SELECT UserId, FullName, Email, Status " +
                    "FROM Users " +
                    "WHERE UserId = ?");

            ps.setInt(1, id);
            rs = ps.executeQuery();

            // Verificar si se encontró algún registro.
            if (rs.next()) {
                // Si se encontró un usuario, asignar los valores de las columnas al objeto User.
                user.setId(rs.getInt(1));       // Obtener el ID del usuario.
                user.setName(rs.getString(2));   // Obtener el nombre del usuario.
                user.setEmail(rs.getString(3));  // Obtener el correo electrónico del usuario.
                user.setStatus(rs.getByte(4));    // Obtener el estado del usuario.
            } else {
                user = null;
            }
            ps.close();
            rs.close();
        } catch (SQLException ex){
            throw new SQLException("Error al obtener un usuario por id: " + ex.getMessage(), ex);
        } finally {
            // Bloque finally para asegurar que los recursos se liberen.
            ps = null;         // Establecer la sentencia preparada a null.
            rs = null;         // Establecer el conjunto de resultados a null.
            conn.disconnect(); // Desconectar de la base de datos.
        }
        return user;
    }

    public User authenticate(User user) throws SQLException{

        User userAutenticate = new User(); // Inicializar un objeto User para almacenar el usuario autenticado.

        try {
            ps = conn.connect().prepareStatement("SELECT UserId, FullName, Email, Status " +
                    "FROM Users " +
                    "WHERE Email = ? AND PasswordHash = ? AND Status = 1");

            ps.setString(1, user.getEmail());
            ps.setString(2, PasswordHasher.hashPassword(user.getPasswordHash()));
            rs = ps.executeQuery();

            if (rs.next()) {
                userAutenticate.setId(rs.getInt(1));       // Obtener el ID del usuario autenticado.
                userAutenticate.setName(rs.getString(2));   // Obtener el nombre del usuario autenticado.
                userAutenticate.setEmail(rs.getString(3));  // Obtener el correo electrónico del usuario autenticado.
                userAutenticate.setStatus(rs.getByte(4));    // Obtener el estado del usuario autenticado.
            } else {
                userAutenticate = null;
            }
            ps.close();
            rs.close();
        } catch (SQLException ex){
            throw new SQLException("Error al autenticar un usuario por id: " + ex.getMessage(), ex);
        } finally {
            // Bloque finally para asegurar que los recursos se liberen.
            ps = null;         // Establecer la sentencia preparada a null.
            rs = null;         // Establecer el conjunto de resultados a null.
            conn.disconnect(); // Desconectar de la base de datos.
        }
        return userAutenticate;
    }

    public boolean updatePassword(User user) throws SQLException{
        boolean res = false; // Variable para indicar si la actualización de la contraseña fue exitosa.
        try{
            ps = conn.connect().prepareStatement(
                    "UPDATE Users " +
                            "SET PasswordHash = ? " +
                            "WHERE UserId = ?"
            );
            // Hashear la nueva contraseña proporcionada antes de establecerla en la consulta.
            ps.setString(1, PasswordHasher.hashPassword(user.getPasswordHash()));
            // Establecer el ID del usuario cuya contraseña se va a actualizar en la cláusula WHERE.
            ps.setInt(2, user.getId());

            // Ejecutar la sentencia de actualización y verificar si se afectó alguna fila.
            if(ps.executeUpdate() > 0){
                res = true; // Si executeUpdate() retorna un valor mayor que 0, la actualización fue exitosa.
            }
            ps.close(); // Cerrar la sentencia preparada para liberar recursos.
        }catch (SQLException ex){
            throw new SQLException("Error al modificar el password del usuario: " + ex.getMessage(), ex);
        } finally {
            // Bloque finally para asegurar que los recursos se liberen.
            ps = null;         // Establecer la sentencia preparada a null.
            conn.disconnect(); // Desconectar de la base de datos.
        }

        return res;
    }
}