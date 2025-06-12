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
import java.awt.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

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
        setTitle("Gestión de Carreras");
        setSize(700, 500);
        setResizable(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(mainForm);

        // --- APLICACIÓN DE ESTILOS Y MEJORAS VISUALES ---
        applyVisualEnhancements();

        txtName.addKeyListener(new KeyAdapter() {
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
            if (!txtName.getText().trim().isEmpty()) {
                search(txtName.getText());
            } else {
                DefaultTableModel emptyModel = new DefaultTableModel();
                tableCareers.setModel(emptyModel);
            }
        });

        btnUpdate.addActionListener(s -> {
            Career career = getCareerFromTableRow();
            if (career != null) {
                CareerWriteForm careerWriteForm = new CareerWriteForm(this.mainForm, CUD.UPDATE, career);
                careerWriteForm.setVisible(true);
                if (!txtName.getText().trim().isEmpty()) {
                    search(txtName.getText());
                } else {
                    DefaultTableModel emptyModel = new DefaultTableModel();
                    tableCareers.setModel(emptyModel);
                }
            }
        });

        btnDelete.addActionListener(s -> {
            Career career = getCareerFromTableRow();
            if (career != null) {
                CareerWriteForm careerWriteFormWriteForm = new CareerWriteForm(this.mainForm, CUD.DELETE, career);
                careerWriteFormWriteForm.setVisible(true);
                if (!txtName.getText().trim().isEmpty()) {
                    search(txtName.getText());
                } else {
                    DefaultTableModel emptyModel = new DefaultTableModel();
                    tableCareers.setModel(emptyModel);
                }
            }
        });
    }

    private void applyVisualEnhancements() {
        mainPanel.setBackground(new Color(240, 245, 250));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        Font textFieldFont = new Font("Arial", Font.PLAIN, 16);
        Color borderColor = new Color(150, 150, 200);
        int borderWidth = 1;

        txtName.setFont(textFieldFont);
        txtName.setBorder(new LineBorder(borderColor, borderWidth, true));
        txtName.setPreferredSize(new Dimension(250, 38));
        txtName.setBackground(Color.WHITE);

        Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
        Dimension buttonSize = new Dimension(100, 38);

        btnCreate.setFont(buttonFont);
        btnCreate.setBackground(new Color(60, 179, 113));
        btnCreate.setForeground(Color.WHITE);
        btnCreate.setFocusPainted(false);
        btnCreate.setPreferredSize(buttonSize);
        applyHoverEffect(btnCreate, new Color(80, 199, 133));

        btnUpdate.setFont(buttonFont);
        btnUpdate.setBackground(new Color(70, 130, 180));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setFocusPainted(false);
        btnUpdate.setPreferredSize(buttonSize);
        applyHoverEffect(btnUpdate, new Color(90, 150, 200));

        btnDelete.setFont(buttonFont);
        btnDelete.setBackground(new Color(200, 70, 70));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        btnDelete.setPreferredSize(buttonSize);
        applyHoverEffect(btnDelete, new Color(220, 90, 90));

        tableCareers.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableCareers.setRowHeight(25);
        tableCareers.setGridColor(new Color(200, 200, 200));
        tableCareers.setSelectionBackground(new Color(173, 216, 230));
        tableCareers.setSelectionForeground(Color.BLACK);

        tableCareers.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        tableCareers.getTableHeader().setBackground(new Color(50, 100, 150));
        tableCareers.getTableHeader().setForeground(Color.WHITE);
        tableCareers.getTableHeader().setReorderingAllowed(false);
        tableCareers.getTableHeader().setResizingAllowed(true);
    }

    private void applyHoverEffect(JButton button, Color hoverColor) {
        Color originalColor = button.getBackground();
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) { // <-- CORRECCIÓN AQUÍ
                button.setBackground(originalColor);
            }
        });
    }

    private void search(String query) {
        try {
            ArrayList<Career> careers = careerDAO.searchByName(query);
            createTable(careers);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al buscar carreras: " + ex.getMessage(),
                    "Error de Búsqueda", JOptionPane.ERROR_MESSAGE);
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
        model.addColumn("Nombre de Carrera");

        this.tableCareers.setModel(model);
        for (int i = 0; i < career.size(); i++) {
            Career career1 = career.get(i);
            model.addRow(new Object[]{career1.getCareerId(), career1.getCareerName()});
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
            if (filaSelect != -1) {
                int id = (int) this.tableCareers.getValueAt(filaSelect, 0);
                career = careerDAO.getById(id);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Por favor, seleccione una fila de la tabla.",
                        "Selección Requerida", JOptionPane.WARNING_MESSAGE);
                return null;
            }

            if (career == null || career.getCareerId() == 0) {
                JOptionPane.showMessageDialog(this,
                        "No se encontró la carrera seleccionada.",
                        "Error de Búsqueda", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            return career;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Ocurrió un error al obtener la carrera: " + ex.getMessage(),
                    "Error del Sistema", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}