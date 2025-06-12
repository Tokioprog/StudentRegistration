package esfe.persistencia;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import esfe.dominio.Career;
import esfe.dominio.Students;

/**
 * Clase StudentDAO encargada de gestionar las operaciones CRUD (Crear, Leer, Actualizar, Eliminar)
 * sobre la entidad Students en la base de datos.
 */
public class StudentDAO {
    private ConnectionManager conn;

    // Constructor que obtiene la instancia del gestor de conexiones
    public StudentDAO() {
        conn = ConnectionManager.getInstance();
    }

    /**
     * Crea un nuevo estudiante en la base de datos.
     *
     * @param student Objeto Students con los datos a guardar.
     * @return El estudiante recién creado, incluido su ID generado por la base de datos.
     * @throws SQLException si ocurre un error durante la operación.
     */
    public Students create(Students student) throws SQLException {
        Students res = null;
        try (var connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(
                     "INSERT INTO Students (code, fullName, age, careerId) VALUES (?, ?, ?, ?)",
                     java.sql.Statement.RETURN_GENERATED_KEYS)) {

            // Asignación de valores a los parámetros de la consulta
            ps.setString(1, student.getCode());
            ps.setString(2, student.getFullName());
            ps.setInt(3, student.getAge());
            ps.setInt(4, student.getCareer().getCareerId());

            // Ejecución del INSERT
            int affectedRows = ps.executeUpdate();

            // Verifica si se insertó al menos una fila
            if (affectedRows == 0) {
                throw new SQLException("Crear estudiante falló, no se afectaron filas.");
            }

            // Obtener el ID generado automáticamente por la base de datos
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int idGenerado = generatedKeys.getInt(1);
                    res = getById(idGenerado); // Recupera el estudiante recién creado
                } else {
                    throw new SQLException("No se obtuvo ID generado.");
                }
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al crear estudiante: " + ex.getMessage(), ex);
        } finally {
            conn.disconnect(); // Cierra la conexión
        }
        return res;
    }

    /**
     * Actualiza los datos de un estudiante existente.
     *
     * @param student Objeto Students con los datos actualizados.
     * @return true si la actualización fue exitosa; false en caso contrario.
     * @throws SQLException si ocurre un error durante la operación.
     */
    public boolean update(Students student) throws SQLException {
        boolean res = false;
        try (var connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(
                     "UPDATE Students SET code = ?, fullName = ?, age = ?, careerId = ? WHERE studentId = ?")) {

            // Asignación de valores a los parámetros
            ps.setString(1, student.getCode());
            ps.setString(2, student.getFullName());
            ps.setInt(3, student.getAge());
            ps.setInt(4, student.getCareer().getCareerId());
            ps.setInt(5, student.getStudentId());

            // Ejecuta la actualización
            res = ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new SQLException("Error al actualizar estudiante: " + ex.getMessage(), ex);
        } finally {
            conn.disconnect();
        }
        return res;
    }

    /**
     * Elimina un estudiante de la base de datos.
     *
     * @param student Objeto Students a eliminar (usa su studentId).
     * @return true si fue eliminado correctamente; false en caso contrario.
     * @throws SQLException si ocurre un error durante la operación.
     */
    public boolean delete(Students student) throws SQLException {
        boolean res = false;
        try (var connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(
                     "DELETE FROM Students WHERE studentId = ?")) {

            // Asigna el ID del estudiante a eliminar
            ps.setInt(1, student.getStudentId());

            // Ejecuta la eliminación
            res = ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new SQLException("Error al eliminar estudiante: " + ex.getMessage(), ex);
        } finally {
            conn.disconnect();
        }
        return res;
    }

    /**
     * Recupera un estudiante por su ID.
     *
     * @param id ID del estudiante.
     * @return El objeto Students encontrado, o null si no existe.
     * @throws SQLException si ocurre un error durante la operación.
     */
    public Students getById(int id) throws SQLException {
        Students student = null;
        try (var connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT s.studentId, s.code, s.fullName, s.age, c.careerId, c.careerName " +
                             "FROM Students s INNER JOIN Careers c ON s.careerId = c.careerId WHERE s.studentId = ?")) {

            ps.setInt(1, id);

            // Ejecuta la consulta
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    student = new Students();
                    student.setStudentId(rs.getInt("studentId"));
                    student.setCode(rs.getString("code"));
                    student.setFullName(rs.getString("fullName"));
                    student.setAge(rs.getInt("age"));

                    // Carga también la información de la carrera
                    Career career = new Career();
                    career.setCareerId(rs.getInt("careerId"));
                    career.setCareerName(rs.getString("careerName"));
                    student.setCareer(career);
                }
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al obtener estudiante por ID: " + ex.getMessage(), ex);
        } finally {
            conn.disconnect();
        }
        return student;
    }

    /**
     * Busca estudiantes cuyo nombre completo contenga una cadena dada.
     *
     * @param fullName Parte o todo el nombre a buscar.
     * @return Lista de estudiantes que coinciden con el criterio.
     * @throws SQLException si ocurre un error durante la operación.
     */
    public ArrayList<Students> searchByFullName(String fullName) throws SQLException {
        ArrayList<Students> lista = new ArrayList<>();
        try (var connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT s.studentId, s.code, s.fullName, s.age, c.careerId, c.careerName " +
                             "FROM Students s INNER JOIN Careers c ON s.careerId = c.careerId WHERE s.fullName LIKE ?")) {

            // Agrega el comodín % para la búsqueda parcial
            ps.setString(1, "%" + fullName + "%");

            // Ejecuta la consulta
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Students student = new Students();
                    student.setStudentId(rs.getInt("studentId"));
                    student.setCode(rs.getString("code"));
                    student.setFullName(rs.getString("fullName"));
                    student.setAge(rs.getInt("age"));

                    // Carga también los datos de la carrera
                    Career career = new Career();
                    career.setCareerId(rs.getInt("careerId"));
                    career.setCareerName(rs.getString("careerName"));
                    student.setCareer(career);

                    lista.add(student);
                }
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al buscar estudiantes por nombre completo: " + ex.getMessage(), ex);
        } finally {
            conn.disconnect();
        }
        return lista;
    }
}
