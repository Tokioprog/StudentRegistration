package esfe.persistencia;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import esfe.dominio.Career;

public class CareerDAO {
    private ConnectionManager conn;

    // Constructor: inicializa el gestor de conexión como instancia única (singleton)
    public CareerDAO() {
        conn = ConnectionManager.getInstance();
    }

    // Metodo para crear una nueva carrera en la base de datos
    public Career create(Career career) throws SQLException {
        Career res = null;
        try (var connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(
                     "INSERT INTO Careers (careerName) VALUES (?)",//Consulta para insertar en la base de datos
                     java.sql.Statement.RETURN_GENERATED_KEYS)) {

            // Asigna el nombre de la carrera al parámetro del query
            ps.setString(1, career.getCareerName());

            // Ejecuta el INSERT y obtiene la cantidad de filas afectadas
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                // Si no se insertó ninguna fila, lanza una excepción
                throw new SQLException("Crear carrera falló, no se afectaron filas.");
            }

            // Obtiene el ID generado automáticamente por la base de datos
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int idGenerado = generatedKeys.getInt(1);
                    // Llama al metodo getById para devolver el objeto creado con ID
                    res = getById(idGenerado);
                } else {
                    throw new SQLException("No se obtuvo ID generado.");
                }
            }
        } catch (SQLException ex) {
            // Captura y relanza la excepción con un mensaje personalizado
            throw new SQLException("Error al crear carrera: " + ex.getMessage(), ex);
        } finally {
            // Cierra la conexión
            conn.disconnect();
        }
        return res;
    }

    // Metodo para actualizar los datos de una carrera existente
    public boolean update(Career career) throws SQLException {
        boolean res = false;
        try (var connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(
                     "UPDATE Careers SET careerName = ? WHERE careerId = ?")) {//Consulta a base de datos

            // Asigna los valores para actualizar el nombre de la carrera
            ps.setString(1, career.getCareerName());
            ps.setInt(2, career.getCareerId());

            // Ejecuta la actualización y devuelve true si al menos una fila fue afectada
            res = ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            //Manda mensaje si la actualizacion falló
            throw new SQLException("Error al actualizar carrera: " + ex.getMessage(), ex);
        } finally {
            conn.disconnect();
        }
        return res;
    }

    // Metodo para eliminar una carrera de la base de datos
    public boolean delete(Career career) throws SQLException {
        boolean res = false;
        try (var connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(
                     "DELETE FROM Careers WHERE careerId = ?")) {//Consulta

            // Asigna el ID de la carrera a eliminar
            ps.setInt(1, career.getCareerId());

            // Ejecuta la eliminación y devuelve true si al menos una fila fue afectada
            res = ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            //Mensaje si hubo un error al eliminar
            throw new SQLException("Error al eliminar carrera: " + ex.getMessage(), ex);
        } finally {
            conn.disconnect();
        }
        return res;
    }

    // Metodo para obtener una carrera por su ID
    public Career getById(int id) throws SQLException {
        Career career = null;
        try (var connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT careerId, careerName FROM Careers WHERE careerId = ?")) {

            // Asigna el ID al parámetro del query
            ps.setInt(1, id);

            // Ejecuta la consulta y verifica si hay un resultado
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Crea un objeto Career y le asigna los valores obtenidos de la BD
                    career = new Career();
                    career.setCareerId(rs.getInt("careerId"));
                    career.setCareerName(rs.getString("careerName"));
                }
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al obtener carrera por ID: " + ex.getMessage(), ex);
        } finally {
            conn.disconnect();
        }
        return career;
    }

    // Metodo para buscar carreras por nombre (búsqueda parcial con LIKE)
    public ArrayList<Career> searchByName(String name) throws SQLException {
        ArrayList<Career> lista = new ArrayList<>();
        try (var connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT careerId, careerName FROM Careers WHERE careerName LIKE ?")) {

            // Usa el comodín % para búsqueda parcial por nombre
            ps.setString(1, "%" + name + "%");

            // Ejecuta la consulta y recorre los resultados
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Crea un objeto Career por cada fila encontrada y lo agrega a la lista
                    Career career = new Career();
                    career.setCareerId(rs.getInt("careerId"));
                    career.setCareerName(rs.getString("careerName"));
                    lista.add(career);
                }
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al buscar carreras por nombre: " + ex.getMessage(), ex);
        } finally {
            conn.disconnect();
        }
        return lista;
    }
}
