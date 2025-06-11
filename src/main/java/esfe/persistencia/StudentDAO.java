package esfe.persistencia;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import esfe.dominio.Career;
import esfe.dominio.Students;

public class StudentDAO {
    private ConnectionManager conn;
    public StudentDAO() {
        conn = ConnectionManager.getInstance();
    }

    public Students create(Students student) throws SQLException {
        Students res = null;
        try (var connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(
                     "INSERT INTO Students (code, fullName, age, careerId) VALUES (?, ?, ?, ?)",
                     java.sql.Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, student.getCode());
            ps.setString(2, student.getFullName());
            ps.setInt(3, student.getAge());
            ps.setInt(4, student.getCareer().getCareerId());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Crear estudiante falló, no se afectaron filas.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int idGenerado = generatedKeys.getInt(1);
                    res = getById(idGenerado);
                } else {
                    throw new SQLException("No se obtuvo ID generado.");
                }
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al crear estudiante: " + ex.getMessage(), ex);
        } finally {
            conn.disconnect();
        }
        return res;
    }

    public boolean update(Students student) throws SQLException {
        boolean res = false;
        try (var connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(
                     "UPDATE Students SET code = ?, fullName = ?, age = ?, careerId = ? WHERE studentId = ?")) {

            ps.setString(1, student.getCode());
            ps.setString(2, student.getFullName());
            ps.setInt(3, student.getAge());
            ps.setInt(4, student.getCareer().getCareerId());
            ps.setInt(5, student.getStudentId());

            res = ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new SQLException("Error al actualizar estudiante: " + ex.getMessage(), ex);
        } finally {
            conn.disconnect();
        }
        return res;
    }

    public boolean delete(Students student) throws SQLException {
        boolean res = false;
        try (var connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(
                     "DELETE FROM Students WHERE studentId = ?")) {

            ps.setInt(1, student.getStudentId());

            res = ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new SQLException("Error al eliminar estudiante: " + ex.getMessage(), ex);
        } finally {
            conn.disconnect();
        }
        return res;
    }

    public Students getById(int id) throws SQLException {
        Students student = null;
        try (var connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT s.studentId, s.code, s.fullName, s.age, c.careerId, c.careerName " +
                             "FROM Students s INNER JOIN Careers c ON s.careerId = c.careerId WHERE s.studentId = ?")) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    student = new Students();
                    student.setStudentId(rs.getInt("studentId"));
                    student.setCode(rs.getString("code"));
                    student.setFullName(rs.getString("fullName"));
                    student.setAge(rs.getInt("age"));

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

    public ArrayList<Students> searchByFullName(String fullName) throws SQLException {
        ArrayList<Students> lista = new ArrayList<>();
        try (var connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT s.studentId, s.code, s.fullName, s.age, c.careerId, c.careerName " +
                             "FROM Students s INNER JOIN Careers c ON s.careerId = c.careerId WHERE s.fullName LIKE ?")) {

            ps.setString(1, "%" + fullName + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Students student = new Students();
                    student.setStudentId(rs.getInt("studentId"));
                    student.setCode(rs.getString("code"));
                    student.setFullName(rs.getString("fullName"));
                    student.setAge(rs.getInt("age"));

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
