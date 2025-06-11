package esfe.presentacion.careers;
import esfe.persistencia.CareerDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import esfe.dominio.Career;
import esfe.presentacion.users.MainForm;
import esfe.utils.CUD;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class CareerReadingForm extends JDialog {
    private JPanel mainPanel;
    private JTextField txtName;
    private JButton btnCreate;
    private JTable tableCareers;
    private JButton btnUpdate;
    private JButton btnDelete;

    private CareerDAO careerDAO;
    private MainForm mainForm;
    // Objeto DAO para acceder a la base de datos y referencia al formulario principal.

    public CareerReadingForm(MainForm mainForm) {
        this.mainForm = mainForm; // Guarda referencia al formulario principal.
        careerDAO = new CareerDAO(); // Inicializa el DAO.

        setContentPane(mainPanel); // Establece el panel principal como contenido.
        setModal(true); // Hace que la ventana sea modal.
        setTitle("Buscar Carrera"); // Título de la ventana.
        pack(); // Ajusta el tamaño automáticamente.
        setLocationRelativeTo(mainForm); // Centra la ventana respecto al formulario principal.

        // Listener para detectar cuando se suelta una tecla en el campo de texto.
        txtName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (!txtName.getText().trim().isEmpty()) {
                    search(txtName.getText()); // Busca carreras por nombre.
                } else {
                    // Si no hay texto, se limpia la tabla.
                    DefaultTableModel emptyModel = new DefaultTableModel();
                    tableCareers.setModel(emptyModel);
                }
            }
        });

        // Acción del botón Crear
        btnCreate.addActionListener(s -> {
            // Abre formulario en modo CREAR con un nuevo objeto Career vacío.
            CareerWriteForm careerWriteForm = new CareerWriteForm(this.mainForm, CUD.CREATE, new Career());
            careerWriteForm.setVisible(true); // Muestra el formulario.
            // Limpia la tabla después de cerrar.
            DefaultTableModel emptyModel = new DefaultTableModel();
            tableCareers.setModel(emptyModel);
        });

        // Acción del botón Actualizar
        btnUpdate.addActionListener(s -> {
            Career career = getCareerFromTableRow(); // Obtiene carrera seleccionada en la tabla.
            if (career != null) {
                // Abre formulario en modo ACTUALIZAR con los datos de la carrera seleccionada.
                CareerWriteForm careerWriteForm = new CareerWriteForm(this.mainForm, CUD.UPDATE, career);
                careerWriteForm.setVisible(true);
                // Limpia la tabla después de cerrar.
                DefaultTableModel emptyModel = new DefaultTableModel();
                tableCareers.setModel(emptyModel);
            }
        });

        // Acción del botón Eliminar
        btnDelete.addActionListener(s -> {
            Career career = getCareerFromTableRow(); // Obtiene carrera seleccionada.
            if (career != null) {
                // Abre formulario en modo ELIMINAR con los datos de la carrera.
                CareerWriteForm careerWriteFormWriteForm = new CareerWriteForm(this.mainForm, CUD.DELETE, career);
                careerWriteFormWriteForm.setVisible(true);
                // Limpia la tabla después de cerrar.
                DefaultTableModel emptyModel = new DefaultTableModel();
                tableCareers.setModel(emptyModel);
            }
        });
    }

    // Método para buscar carreras por nombre
    private void search(String query) {
        try {
            ArrayList<Career> careers = careerDAO.searchByName(query); // Busca en la base de datos.
            createTable(careers); // Crea la tabla con los resultados.
        } catch (Exception ex) {
            // Muestra mensaje de error si ocurre una excepción.
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    // Método que crea y llena la tabla con las carreras encontradas
    public void createTable(ArrayList<Career> career) {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hace que las celdas no sean editables.
            }
        };
        model.addColumn("Id");
        model.addColumn("Nombre");

        this.tableCareers.setModel(model); // Asigna el modelo a la tabla.
        Object row[] = null;
        for (int i = 0; i < career.size(); i++) {
            Career career1 = career.get(i);
            model.addRow(row); // Agrega una nueva fila vacía.
            model.setValueAt(career1.getCareerId(), i, 0); // Establece el ID en la columna 0.
            model.setValueAt(career1.getCareerName(), i, 1); // Establece el nombre en la columna 1.
        }
        hideCol(0); // Oculta la columna ID.
    }

    // Método para ocultar visualmente una columna de la tabla (en este caso, el ID).
    private void hideCol(int pColumna) {
        this.tableCareers.getColumnModel().getColumn(pColumna).setMaxWidth(0);
        this.tableCareers.getColumnModel().getColumn(pColumna).setMinWidth(0);
        this.tableCareers.getTableHeader().getColumnModel().getColumn(pColumna).setMaxWidth(0);
        this.tableCareers.getTableHeader().getColumnModel().getColumn(pColumna).setMinWidth(0);
    }

    // Método que obtiene el objeto Career desde la fila seleccionada de la tabla
    private Career getCareerFromTableRow() {
        Career career = null;
        try {
            int filaSelect = this.tableCareers.getSelectedRow(); // Obtiene la fila seleccionada.
            int id = 0;
            if (filaSelect != -1) {
                // Si hay fila seleccionada, obtiene el ID desde la columna 0.
                id = (int) this.tableCareers.getValueAt(filaSelect, 0);
            } else {
                // Si no hay fila seleccionada, muestra advertencia.
                JOptionPane.showMessageDialog(null,
                        "Seleccionar una fila de la tabla.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            career = careerDAO.getById(id); // Consulta la base de datos por el ID.
            if (career.getCareerId() == 0) {
                // Si no se encuentra una carrera con ese ID, muestra advertencia.
                JOptionPane.showMessageDialog(null,
                        "No se encontró ninguna carrera.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            return career; // Devuelve el objeto carrera.
        } catch (Exception ex) {
            // Si ocurre un error, muestra mensaje.
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
