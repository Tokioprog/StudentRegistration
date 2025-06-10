package esfe.persistencia;

import esfe.dominio.Career;
import esfe.dominio.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class CareerDAOTest {

    private CareerDAO careerDAO;

    @BeforeEach
    void setUp(){
        careerDAO = new CareerDAO();
    }
    private Career create(Career career) throws SQLException {
        Career res = careerDAO.create(career);

        // Realiza aserciones para verificar que la creación del usuario fue exitosa
        assertNotNull(res, "La carrera ingresada no debe ser nulo."); // Verifica que el objeto retornado no sea nulo.
        assertEquals(career.getCareerName(), res.getCareerName(), "El nombre de la carrera creada debe ser igual al original.");
        return res;
    }

    private void update(Career career) throws SQLException{
        // Modifica los atributos del objeto User para simular una actualización.
        career.setCareerName(career.getCareerName() + "_u"); // Añade "_u" al final del nombre.

        // Llama al método 'update' del carrerDAO
        boolean res = careerDAO.update(career);

        // Realiza una aserción para verificar que la actualización fue exitosa.
        assertTrue(res, "La actualización de la carrera debería ser exitosa.");

        getById(career);
    }

    private void getById(Career career) throws SQLException {
        // Llama al método 'getById' del UserDAO para obtener un usuario por su ID.
        Career res = careerDAO.getById(career.getCareerId());

        // Realiza aserciones para verificar que el usuario obtenido coincide
        assertNotNull(res, "La carrera obtenida por ID no debería ser nulo.");
        assertEquals(career.getCareerId(), res.getCareerId(), "El ID de carrera obtenido debe ser igual al original.");
        assertEquals(career.getCareerName(), res.getCareerName(), "El nombre de carrera obtenido debe ser igual al esperado.");
    }

    private void search(Career career) throws SQLException {
        ArrayList<Career> careers = careerDAO.searchByName(career.getCareerName());
        boolean find = false;

        for (Career careerItem : careers) {
            // Verifica si el nombre de cada usuario encontrado contiene la cadena de búsqueda.
            if (careerItem.getCareerName().contains(career.getCareerName())) {
                find = true; // Si se encuentra una coincidencia, se establece 'find' a true.
            }
            else{
                find = false; // Si un nombre no contiene la cadena de búsqueda, se establece 'find' a false.
                break;      // Se sale del bucle, ya que se esperaba que todos los resultados contuvieran la cadena.
            }
        }

        // Realiza una aserción para verificar que todos los usuarios con el nombre buscado fue encontrado.
        assertTrue(find, "el nombre buscado no fue encontrado : " + career.getCareerName());
    }

    private void delete(Career career) throws SQLException{
        boolean res = careerDAO.delete(career);

        // Realiza una aserción para verificar que la eliminación fue exitosa.
        assertTrue(res, "La eliminación de la carrera debería ser exitosa.");

        // Intenta obtener el usuario por su ID después de la eliminación.
        Career res2 = careerDAO.getById(career.getCareerId());

        // Realiza una aserción para verificar que el usuario ya no existe en la base de datos
        assertNull(res2, "La carrera debería haber sido eliminada y no encontrado por ID.");
    }

    @Test
    void testCareerDAO() throws SQLException {
        // Crea una instancia de la clase Random para generar datos de prueba aleatorios.
        Random random = new Random();
        // Crea un nuevo objeto User con datos de prueba. El ID se establece en 0 ya que será generado por la base de datos.
        Career career = new Career(0, "Test Career");

        // Llama al método 'create'
        Career testCareer = create(career);

        // Llama al método 'update'
        update(testCareer);

        // Llama al método 'search'
        search(testCareer);

        // Llama al método 'delete' para eliminar el usuario de prueba de la base de datos y verifica la eliminación.
        delete(testCareer);
    }
}