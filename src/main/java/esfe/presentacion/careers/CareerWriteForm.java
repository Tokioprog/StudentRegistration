package esfe.presentacion.careers;

import esfe.dominio.Career;
import esfe.persistencia.CareerDAO;
import esfe.presentacion.users.MainForm;
import esfe.utils.CBOption;
import esfe.utils.CUD;

import javax.swing.*;

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
        careerDAO = new CareerDAO();
        setContentPane(mainPanel);
        setModal(true);
        init();
        pack();
        setLocationRelativeTo(mainForm);

        btnCancel.addActionListener(s -> this.dispose());
        btnOk.addActionListener(s -> ok());
    }

    private void init() {
        switch (this.cud) {
            case CREATE:
                setTitle("Crear Carrera");
                btnOk.setText("Guardar");
                break;
            case UPDATE:
                setTitle("Modificar Carrera");
                btnOk.setText("Guardar");
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
                        if (career.getCareerId() > 0) {
                            r = true;
                        }
                        break;
                    case UPDATE:
                        r = careerDAO.update(this.en);
                        break;
                    case DELETE:
                        r = careerDAO.delete(this.en);
                        break;
                }
                if (r) {
                    JOptionPane.showMessageDialog(null,
                            "Transacción realizada exitosamente",
                            "Información", JOptionPane.INFORMATION_MESSAGE);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(null,
                            "No se logró realizar ninguna acción",
                            "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                JOptionPane.showMessageDialog(null,
                        "Los campos con * son obligatorios",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
}
