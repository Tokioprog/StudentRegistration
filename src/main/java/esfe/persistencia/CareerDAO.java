package esfe.persistencia;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import esfe.dominio.Career;

public class CareerDAO {
    private ConnectionManager conn;

    public CareerDAO() {
        conn = ConnectionManager.getInstance();
    }

    public Career create(Career career) throws SQLException {
        Career res = null;
        try (var connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(
                     "INSERT INTO Careers (careerName) VALUES (?)",
                     java.sql.Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, career.getCareerName());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Crear carrera falló, no se afectaron filas.");
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
            throw new SQLException("Error al crear carrera: " + ex.getMessage(), ex);
        } finally {
            conn.disconnect();
        }
        return res;
    }

    public boolean update(Career career) throws SQLException {
        boolean res = false;
        try (var connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(
                     "UPDATE Careers SET careerName = ? WHERE careerId = ?")) {

            ps.setString(1, career.getCareerName());
            ps.setInt(2, career.getCareerId());

            res = ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new SQLException("Error al actualizar carrera: " + ex.getMessage(), ex);
        } finally {
            conn.disconnect();
        }
        return res;
    }

    public boolean delete(Career career) throws SQLException {
        boolean res = false;
        try (var connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(
                     "DELETE FROM Careers WHERE careerId = ?")) {

            ps.setInt(1, career.getCareerId());

            res = ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new SQLException("Error al eliminar carrera: " + ex.getMessage(), ex);
        } finally {
            conn.disconnect();
        }
        return res;
    }

    public Career getById(int id) throws SQLException {
        Career career = null;
        try (var connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT careerId, careerName FROM Careers WHERE careerId = ?")) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
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

    public ArrayList<Career> searchByName(String name) throws SQLException {
        ArrayList<Career> lista = new ArrayList<>();
        try (var connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT careerId, careerName FROM Careers WHERE careerName LIKE ?")) {

            ps.setString(1, "%" + name + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
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
