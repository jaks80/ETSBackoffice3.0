package com.ets.fe.a_main;

import com.ets.fe.APIConfig;
import com.ets.fe.Application;
import com.ets.fe.LockApp;
import com.ets.fe.security.Cryptography;
import com.ets.fe.settings.task.LoginTask;
import com.ets.fe.settings.model.User;
import com.ets.fe.util.DirectoryHandler;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.ExecutionException;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author Yusuf
 */
public class DlgLogin extends JDialog implements PropertyChangeListener {

    private boolean login;
    private LoginTask loginTask;
    private APIConfig aPIConfig;
    private Main main = null;

    public DlgLogin() {

        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension dimension = tk.getScreenSize();
        int screenWidth = dimension.width;
        int screenHeight = dimension.height;
        setLocation(screenWidth / 2 - 200, screenHeight / 2 - 200);
        initComponents();       
        aPIConfig = new APIConfig();
    }

    private void writeConfProps() {
        String agentId = JOptionPane.showInputDialog("Enter 10 Digit Alpha Numaric Agent ID: ");

        if (agentId != null && !agentId.isEmpty()) {
                APIConfig.writeConfProps(agentId.trim());
        }

    }

    private void loginTask() {
        btnLogin.setEnabled(false);
        if (txtUserName.getText().isEmpty() || txtPWord.getPassword().length == 0) {
            JOptionPane.showMessageDialog(null, "Enter UserName & Password...",
                    "ETSBackoffice login....", JOptionPane.WARNING_MESSAGE);
        } else {
            String loginId = txtUserName.getText().trim();
            char[] p = txtPWord.getPassword();
            char[] newP = txtNewPassword.getPassword();

            String enc_loginId = Cryptography.encryptString(loginId);
            String enc_password = Cryptography.encryptString(p);
            String enc_newPassword = null;

            if (newP.length > 0) {
                enc_newPassword = Cryptography.encryptString(newP);
            }

            loginTask = new LoginTask(enc_loginId, enc_password, enc_newPassword, progressBar);
            loginTask.addPropertyChangeListener(this);
            loginTask.execute();
        }
    }

    public boolean showLoginDialog() {
        setLocationRelativeTo(this);
        setVisible(true);

        if (login) {
        }
        return login;
    }

    public void login() {
        login = true;
        setVisible(false);
    }

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getFullName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(Main.class.getFullName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(Main.class.getFullName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(Main.class.getFullName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(Main.class.getFullName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
        //</editor-fold>

        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {

                try {
                    DirectoryHandler.setAppDirectories();
                    LockApp l = new LockApp();
                    DlgLogin dlgLogin = new DlgLogin();
                    dlgLogin.setVisible(true);
                } catch (RuntimeException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "ETSBackoffice is already running!", "ETSBAckoffice", JOptionPane.WARNING_MESSAGE);
                    System.exit(0);
                    return;
                }
            }
        }
        );
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        progressBar = new javax.swing.JProgressBar();
        jPanel1 = new javax.swing.JPanel();
        btnLogin = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        txtUserName = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtPWord = new javax.swing.JPasswordField();
        txtNewPassword = new javax.swing.JPasswordField();
        lblMessage = new javax.swing.JLabel();
        lblSettings = new javax.swing.JLabel();
        lblBanner = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        progressBar.setPreferredSize(new java.awt.Dimension(146, 16));
        progressBar.setStringPainted(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 350, 0, 0);
        getContentPane().add(progressBar, gridBagConstraints);

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        btnLogin.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnLogin.setText("Login");
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPanel1.add(btnLogin, gridBagConstraints);

        btnCancel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPanel1.add(btnCancel, gridBagConstraints);

        txtUserName.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        txtUserName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtUserNameKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 2, 0);
        jPanel1.add(txtUserName, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("New Password");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel1.add(jLabel4, gridBagConstraints);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Password");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel1.add(jLabel3, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("User Name");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel1.add(jLabel2, gridBagConstraints);

        txtPWord.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPWordKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 0);
        jPanel1.add(txtPWord, gridBagConstraints);

        txtNewPassword.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNewPasswordKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 0);
        jPanel1.add(txtNewPassword, gridBagConstraints);

        lblMessage.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblMessage.setForeground(new java.awt.Color(255, 0, 0));
        lblMessage.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
        jPanel1.add(lblMessage, gridBagConstraints);

        lblSettings.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/settings24.png"))); // NOI18N
        lblSettings.setToolTipText("Enter Agent ID");
        lblSettings.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblSettingsMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(lblSettings, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 15;
        gridBagConstraints.ipady = 15;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(30, 190, 0, 0);
        getContentPane().add(jPanel1, gridBagConstraints);

        lblBanner.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/banner.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(lblBanner, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtPWordKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPWordKeyReleased
        int key = evt.getKeyCode();
        if (key == KeyEvent.VK_ENTER) {
            loginTask();
        }
    }//GEN-LAST:event_txtPWordKeyReleased

    private void txtNewPasswordKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNewPasswordKeyReleased
        int key = evt.getKeyCode();
        if (key == KeyEvent.VK_ENTER) {
            loginTask();
        }
    }//GEN-LAST:event_txtNewPasswordKeyReleased

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        loginTask();
    }//GEN-LAST:event_btnLoginActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btnCancelActionPerformed

    private void txtUserNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtUserNameKeyReleased
        int key = evt.getKeyCode();
        if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_DOWN) {
            txtPWord.requestFocus();
        }
    }//GEN-LAST:event_txtUserNameKeyReleased

    private void lblSettingsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSettingsMouseClicked
        writeConfProps();
    }//GEN-LAST:event_lblSettingsMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnLogin;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblBanner;
    private javax.swing.JLabel lblMessage;
    private javax.swing.JLabel lblSettings;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JPasswordField txtNewPassword;
    private javax.swing.JPasswordField txtPWord;
    private javax.swing.JTextField txtUserName;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress".equals(evt.getPropertyName())) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
            if (progress == 100) {
                try {
                    User user = loginTask.get();
                    if (user != null && user.getUserType() != null) {
                        txtUserName.setText(user.getLoginID());//Leave login id here for automated logout
                        txtPWord.setText("");
                        txtNewPassword.setText("");
                        btnLogin.setEnabled(true);
                        Application.setLoggedOnUser(user);
                        setVisible(false);
                        Application.loadSettings();
                        if (main == null) {
                            showMainFrame(this);

                        }

                    } else {
                        btnLogin.setEnabled(true);
                        lblMessage.setText("Could not login...");
                    }
                } catch (InterruptedException | ExecutionException ex) {

                }
            }
        }
    }

    private void showMainFrame(final DlgLogin dlg) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                main = new Main();
                Main.setDlgLogin(dlg);
                main.setVisible(true);
                main.setTitle("ETSBackoffice V:" + Application.getProp().getProperty("version") + " " + Application.getMainAgent().getName());
            }
        });
    }
}
