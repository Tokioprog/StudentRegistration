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

    public CareerReadingForm(MainForm mainForm) {
        this.mainForm = mainForm;
        careerDAO = new CareerDAO();
        setContentPane(mainPanel);
        setModal(true);
        setTitle("Buscar Carrera");
        pack();
        setLocationRelativeTo(mainForm);
        txtName.addKeyListener(new KeyAdapter() {
            // Sobrescribe el método keyReleased, que se llama cuando se suelta una tecla.
            @Override
            public void keyReleased(KeyEvent e) {
                if (!txtName.getText().trim().isEmpty()) {
                    search(txtName.getText());
                } else {
                    DefaultTableModel emptyModel = new DefaultTableModel();
                    tableCareers.setModel(emptyModel);
                }
            }
        });
        btnCreate.addActionListener(s -> {
            CareerWriteForm careerWriteForm = new CareerWriteForm(this.mainForm, CUD.CREATE, new Career());
            careerWriteForm.setVisible(true);
            DefaultTableModel emptyModel = new DefaultTableModel();
            tableCareers.setModel(emptyModel);
        });
        btnUpdate.addActionListener(s -> {
            Career career = getCareerFromTableRow();
            if (career != null) {
                CareerWriteForm careerWriteForm = new CareerWriteForm(this.mainForm, CUD.UPDATE, career);
                careerWriteForm.setVisible(true);
                DefaultTableModel emptyModel = new DefaultTableModel();
                tableCareers.setModel(emptyModel);
            }
        });

        btnDelete.addActionListener(s -> {
            Career career = getCareerFromTableRow();
            if (career != null) {
                CareerWriteForm careerWriteFormWriteForm = new CareerWriteForm(this.mainForm, CUD.DELETE, career);
                careerWriteFormWriteForm.setVisible(true);
                DefaultTableModel emptyModel = new DefaultTableModel();
                tableCareers.setModel(emptyModel);
            }
        });
    }

    private void search(String query) {
        try {
            ArrayList<Career> careers = careerDAO.searchByName(query);
            createTable(careers);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    public void createTable(ArrayList<Career> career) {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        model.addColumn("Id");
        model.addColumn("Nombre");

        this.tableCareers.setModel(model);
        Object row[] = null;
        for (int i = 0; i < career.size(); i++) {
            Career career1 = career.get(i);
            model.addRow(row);
            model.setValueAt(career1.getCareerId(), i, 0);
            model.setValueAt(career1.getCareerName(), i, 1);
        }
        hideCol(0);
    }

    private void hideCol(int pColumna) {
        this.tableCareers.getColumnModel().getColumn(pColumna).setMaxWidth(0);
        this.tableCareers.getColumnModel().getColumn(pColumna).setMinWidth(0);
        this.tableCareers.getTableHeader().getColumnModel().getColumn(pColumna).setMaxWidth(0);
        this.tableCareers.getTableHeader().getColumnModel().getColumn(pColumna).setMinWidth(0);
    }

    private Career getCareerFromTableRow() {
        Career career = null;
        try {
            int filaSelect = this.tableCareers.getSelectedRow();
            int id = 0; // Inicializa la variable id a 0.
            if (filaSelect != -1) {
                id = (int) this.tableCareers.getValueAt(filaSelect, 0);
            } else {
                JOptionPane.showMessageDialog(null,
                        "Seleccionar una fila de la tabla.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            career = careerDAO.getById(id);
            if (career.getCareerId() == 0) {
                JOptionPane.showMessageDialog(null,
                        "No se encontró ninguna carrera.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            return career;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
