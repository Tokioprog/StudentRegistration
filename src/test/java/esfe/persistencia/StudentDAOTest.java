package esfe.persistencia;

import esfe.dominio.Career;
import esfe.dominio.Students;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class StudentDAOTest {
    private StudentDAO studentDAO;

    @BeforeEach
    void setUp() {
        studentDAO = new StudentDAO();
    }

    private Students create(Students student) throws SQLException {
        Students res = studentDAO.create(student);
        assertNotNull(res, "El estudiante creado no debería ser nulo.");
        assertEquals(student.getCode(), res.getCode(), "El código debe coincidir.");
        assertEquals(student.getFullName(), res.getFullName(), "El nombre debe coincidir.");
        assertEquals(student.getAge(), res.getAge(), "La edad debe coincidir.");
        assertEquals(student.getCareer().getCareerId(), res.getCareer().getCareerId(), "La carrera debe coincidir.");
        return res;
    }

    private void update(Students student) throws SQLException {
        student.setCode(student.getCode() + "_mod");
        student.setFullName(student.getFullName() + " Modificado");
        student.setAge(student.getAge() + 1);

        boolean res = studentDAO.update(student);
        assertTrue(res, "La actualización del estudiante debería ser exitosa.");

        getById(student);
    }

    private void getById(Students student) throws SQLException {
        Students res = studentDAO.getById(student.getStudentId());
        assertNotNull(res, "El estudiante obtenido por ID no debería ser nulo.");
        assertEquals(student.getStudentId(), res.getStudentId());
        assertEquals(student.getFullName(), res.getFullName());
        assertEquals(student.getCode(), res.getCode());
        assertEquals(student.getAge(), res.getAge());
        assertEquals(student.getCareer().getCareerId(), res.getCareer().getCareerId());
    }

    private void search(Students student) throws SQLException {
        ArrayList<Students> list = studentDAO.searchByFullName(student.getFullName());
        assertFalse(list.isEmpty(), "La búsqueda no debería devolver una lista vacía.");
        boolean found = false;
        for (Students s : list) {
            if (s.getFullName().contains(student.getFullName())) {
                found = true;
            } else {
                found = false;
                break;
            }
        }
        assertTrue(found, "El nombre buscado no fue encontrado: " + student.getFullName());
    }

    private void delete(Students student) throws SQLException {
        boolean res = studentDAO.delete(student);
        assertTrue(res, "La eliminación del estudiante debería ser exitosa.");
        Students res2 = studentDAO.getById(student.getStudentId());
        assertNull(res2, "El estudiante debería haber sido eliminado.");
    }

    @Test
    void testStudentDAO() throws SQLException {
        Random random = new Random();
        int num = random.nextInt(1000) + 1;

        Career career = new Career();
        career.setCareerId(2); // Asegúrate de que esta carrera exista en tu tabla Careers

        Students student = new Students(0, "STU" + num, "Estudiante " + num, 20, career);

        Students testStudent = create(student);
        update(testStudent);
        search(testStudent);
        delete(testStudent);
    }
}
