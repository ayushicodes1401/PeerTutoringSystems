
package com.peertutoring;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import com.peertutoring.ui.LoginFrame;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new LoginFrame().setVisible(true);
            } catch (Exception exception) {
                exception.printStackTrace();

                JOptionPane.showMessageDialog(
                        null,
                        "Unable to start the application:\n"
                                + exception.getMessage(),
                        "Application Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}
