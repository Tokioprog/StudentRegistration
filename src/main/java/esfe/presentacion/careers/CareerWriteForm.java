package esfe.presentacion.careers;

import esfe.dominio.Career;
import esfe.persistencia.CareerDAO;
import esfe.presentacion.users.MainForm;
import esfe.utils.CUD;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class CareerWriteForm extends JDialog {
    private JTextField txtName;
    private JButton btnOk;
    private JButton btnCancel;
    private JPanel mainPanel;

    private CareerDAO careerDAO;
    private MainForm mainForm;
    private CUD cud;
    private Career en;

    public CareerWriteForm(MainForm mainForm, CUD cud, Career career) {
        this.cud = cud;
        this.en = career;
        this.mainForm = mainForm;
        this.careerDAO = new CareerDAO();

        setContentPane(mainPanel);
        setModal(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(400, 200);
        setResizable(false);
        init();
        setLocationRelativeTo(mainForm);

        // --- APLICACIÓN DE ESTILOS Y MEJORAS VISUALES ---
        applyVisualEnhancements();

        btnCancel.addActionListener(s -> this.dispose());
        btnOk.addActionListener(s -> ok());
    }

    private void applyVisualEnhancements() {
        mainPanel.setBackground(new Color(245, 245, 250));
        mainPanel.setBorder(new EmptyBorder(25, 30, 25, 30));

        Font textFieldFont = new Font("Arial", Font.PLAIN, 16);
        Color borderColor = new Color(150, 150, 200);
        int borderWidth = 1;

        txtName.setFont(textFieldFont);
        txtName.setBorder(new LineBorder(borderColor, borderWidth, true));
        txtName.setPreferredSize(new Dimension(280, 38));
        txtName.setBackground(Color.WHITE);

        Font buttonFont = new Font("Segoe UI", Font.BOLD, 15);
        Dimension buttonSize = new Dimension(120, 40);

        btnOk.setFont(buttonFont);
        btnOk.setForeground(Color.WHITE);
        btnOk.setFocusPainted(false);
        btnOk.setPreferredSize(buttonSize);
        btnOk.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        if (this.cud == CUD.DELETE) {
            btnOk.setBackground(new Color(200, 70, 70));
            btnOk.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) { btnOk.setBackground(new Color(220, 90, 90)); }
                public void mouseExited(java.awt.event.MouseEvent evt) { btnOk.setBackground(new Color(200, 70, 70)); }
            });
        } else {
            btnOk.setBackground(new Color(60, 179, 113));
            btnOk.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) { btnOk.setBackground(new Color(80, 199, 133)); }
                public void mouseExited(java.awt.event.MouseEvent evt) { btnOk.setBackground(new Color(60, 179, 113)); }
            });
        }

        btnCancel.setFont(buttonFont);
        btnCancel.setBackground(new Color(150, 150, 150));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFocusPainted(false);
        btnCancel.setPreferredSize(buttonSize);
        btnCancel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnCancel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnCancel.setBackground(new Color(170, 170, 170));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) { // <-- CORRECCIÓN AQUÍ
                btnCancel.setBackground(new Color(150, 150, 150));
            }
        });
    }

    private void init() {
        switch (this.cud) {
            case CREATE:
                setTitle("Crear Nueva Carrera");
                btnOk.setText("Guardar");
                break;
            case UPDATE:
                setTitle("Modificar Carrera Existente");
                btnOk.setText("Actualizar");
                break;
            case DELETE:
                setTitle("Eliminar Carrera");
                btnOk.setText("Eliminar");
                break;
        }

        setValuesControls(this.en);
    }

    private void setValuesControls(Career career) {
        txtName.setText(career.getCareerName());

        if (this.cud == CUD.DELETE) {
            txtName.setEditable(false);
            txtName.setBackground(new Color(230, 230, 230));
        }
    }

    private boolean getValuesControls() {
        boolean res = false;

        if (txtName.getText().trim().isEmpty()) {
            return res;
        }
        else if (this.cud != CUD.CREATE && this.en.getCareerId() == 0) {
            return res;
        }

        res = true;
        this.en.setCareerName(txtName.getText());
        return res;
    }

    private void ok() {
        try {
            boolean res = getValuesControls();
            if (res) {
                boolean r = false;

                switch (this.cud) {
                    case CREATE:
                        Career career = careerDAO.create(this.en);
                        r = career.getCareerId() > 0;
                        break;
                    case UPDATE:
                        r = careerDAO.update(this.en);
                        break;
                    case DELETE:
                        r = careerDAO.delete(this.en);
                        break;
                }

                if (r) {
                    JOptionPane.showMessageDialog(this,
                            "Operación realizada exitosamente.",
                            "Información", JOptionPane.INFORMATION_MESSAGE);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "No se logró realizar la operación.",
                            "Error de Operación", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Por favor, complete el campo Nombre de Carrera.",
                        "Validación de Campos", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Ocurrió un error: " + ex.getMessage(),
                    "Error del Sistema", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}