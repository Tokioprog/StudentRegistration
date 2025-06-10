package esfe.presentacion.users;
import esfe.dominio.User;

import javax.swing.*;

public class MainForm extends JFrame {
    private User userAutenticate;

    public User getUserAutenticate() {
        return userAutenticate;
    }

    public void setUserAutenticate(User userAutenticate) {
        this.userAutenticate = userAutenticate;
    }
}
