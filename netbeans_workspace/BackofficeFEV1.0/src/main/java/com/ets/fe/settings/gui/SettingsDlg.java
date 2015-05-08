package com.ets.fe.settings.gui;

import com.ets.fe.settings.model.AppSettings;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Yusuf
 */
public class SettingsDlg extends JDialog implements ActionListener {

    private boolean save;
    private AppSettings settings;
    private JFileChooser fileChooser = new JFileChooser();
    private byte[] companyLogo;
    private byte[] atolLogo;
    private byte[] iataLogo;
    private byte[] otherLogo;

    public SettingsDlg(Frame parent) {
        super(parent, true);
        initComponents();
        btnSave.addActionListener(this);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpeg", "png", "jpg");
        fileChooser.setFileFilter(filter);
    }

    private void displayLogo(byte[] bFile) {
        try {
            Image img = ImageIO.read(new ByteArrayInputStream(bFile));
            ImageIcon imgIcon = new ImageIcon(img);
            lblCompanyLogo.setText("");
            lblCompanyLogo.setIcon(imgIcon);
        } catch (IOException ex) {
            Logger.getLogger(SettingsDlg.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void displayAtolLogo(byte[] bFile) {
        try {
            Image img = ImageIO.read(new ByteArrayInputStream(bFile));
            ImageIcon imgIcon = new ImageIcon(img);
            lblAtolLogo.setText("");
            lblAtolLogo.setIcon(imgIcon);
        } catch (IOException ex) {
            Logger.getLogger(SettingsDlg.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void displayIataLogo(byte[] bFile) {
        try {
            Image img = ImageIO.read(new ByteArrayInputStream(bFile));
            ImageIcon imgIcon = new ImageIcon(img);
            lblIataLogo.setText("");
            lblIataLogo.setIcon(imgIcon);
        } catch (IOException ex) {
            Logger.getLogger(SettingsDlg.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void displayOtherLogo(byte[] bFile) {
        try {
            Image img = ImageIO.read(new ByteArrayInputStream(bFile));
            ImageIcon imgIcon = new ImageIcon(img);
            lblOtherLogo.setText("");
            lblOtherLogo.setIcon(imgIcon);
        } catch (IOException ex) {
            Logger.getLogger(SettingsDlg.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean showDialog(AppSettings settings) {
        this.settings = settings;
        if (settings != null) {
            txtEmail.setText(settings.getEmail());
            txtEmailPassword.setText(settings.getEmailPassword());
            txtSmtpServer.setText(settings.getEmailHost());
            txtPort.setText(String.valueOf(settings.getPort()));
            txtVAT.setText(settings.getVatRate().toString());
            txtMainAgentID.setText(settings.getMainAgentID());
            txtOtherInvTC.setText(settings.getoInvTAndC());
            txtTktInvTC.setText(settings.gettInvTAndC());
            txtTicketingInvoiceFooter.setText(settings.gettInvFooter());
            txtOtherInvoiceFooter.setText(settings.getoInvFooter());

            if (settings.getCompanyLogo() != null) {
                companyLogo = settings.getCompanyLogo();
                displayLogo(companyLogo);
            }

            if (settings.getAtolLogo() != null) {
                atolLogo = settings.getAtolLogo();
                displayAtolLogo(atolLogo);
            }

            if (settings.getIataLogo() != null) {
                iataLogo = settings.getIataLogo();
                displayIataLogo(iataLogo);
            }

            if (settings.getOtherLogo() != null) {
                otherLogo = settings.getOtherLogo();
                displayOtherLogo(otherLogo);
            }

            chkSmtpAuth.setSelected(Boolean.valueOf(settings.getSmtp_auth()));
            chkStarttls.setSelected(Boolean.valueOf(settings.getStarttls_enable()));
        }
        save = false;
        setLocationRelativeTo(this);
        setVisible(true);

        if (save) {
            settings.setEmail(txtEmail.getText().trim());
            settings.setPort(Integer.valueOf(txtPort.getText()));
            settings.setEmailHost(txtSmtpServer.getText());
            settings.setEmailPassword(String.valueOf(txtEmailPassword.getPassword()));
            settings.setVatRate(new BigDecimal(txtVAT.getText().trim()));
            settings.setMainAgentID(txtMainAgentID.getText().trim());
            
            settings.setoInvTAndC(txtOtherInvTC.getText().trim());
            settings.settInvTAndC(txtTktInvTC.getText().trim());
            settings.settInvFooter(txtTicketingInvoiceFooter.getText().trim());
            settings.setoInvFooter(txtOtherInvoiceFooter.getText().trim());
            
            settings.setSmtp_auth(String.valueOf(chkSmtpAuth.isSelected()));
            settings.setStarttls_enable(String.valueOf(chkStarttls.isSelected()));

            settings.setCompanyLogo(companyLogo);
            settings.setAtolLogo(atolLogo);
            settings.setIataLogo(iataLogo);
            settings.setOtherLogo(otherLogo);
        }
        return save;
    }

    private void processCompanyLogo(File file) {
        byte[] bFile = new byte[(int) file.length()];
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(bFile);
            fileInputStream.close();
            companyLogo = bFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        displayLogo(companyLogo);
    }

    private void processAtolLogo(File file) {
        byte[] bFile = new byte[(int) file.length()];
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(bFile);
            fileInputStream.close();
            atolLogo = bFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        displayAtolLogo(atolLogo);
    }

    private void processIataLogo(File file) {
        byte[] bFile = new byte[(int) file.length()];
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(bFile);
            fileInputStream.close();
            iataLogo = bFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        displayIataLogo(iataLogo);
    }

    private void processOtherLogo(File file) {
        byte[] bFile = new byte[(int) file.length()];
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(bFile);
            fileInputStream.close();
            otherLogo = bFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        displayOtherLogo(otherLogo);
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

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtTktInvTC = new javax.swing.JTextArea();
        jLabel7 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtOtherInvTC = new javax.swing.JTextArea();
        jLabel8 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtTicketingInvoiceFooter = new javax.swing.JTextArea();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtOtherInvoiceFooter = new javax.swing.JTextArea();
        jPanel6 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txtVAT = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtMainAgentID = new javax.swing.JTextField();
        pnlImage = new javax.swing.JPanel();
        lblAtolLogo = new javax.swing.JLabel();
        btnBrowseAtolLogo = new javax.swing.JButton();
        btnBrowseCompanyLogo = new javax.swing.JButton();
        lblCompanyLogo = new javax.swing.JLabel();
        btnBrowseIataLogo = new javax.swing.JButton();
        lblIataLogo = new javax.swing.JLabel();
        lblOtherLogo = new javax.swing.JLabel();
        btnBrowseOtherLogo = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        txtSmtpServer = new javax.swing.JTextField();
        txtPort = new javax.swing.JTextField();
        txtEmailPassword = new javax.swing.JPasswordField();
        chkSmtpAuth = new javax.swing.JCheckBox();
        chkStarttls = new javax.swing.JCheckBox();
        btnSave = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        lblInfo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Application Settings");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 172, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("Ticketing Invoice T/C"));

        txtTktInvTC.setColumns(20);
        txtTktInvTC.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        txtTktInvTC.setLineWrap(true);
        txtTktInvTC.setRows(5);
        jScrollPane1.setViewportView(txtTktInvTC);

        jLabel7.setText("700 Char Max");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel7)))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7))
        );

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder("Other Invoice T/C"));

        txtOtherInvTC.setColumns(20);
        txtOtherInvTC.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        txtOtherInvTC.setLineWrap(true);
        txtOtherInvTC.setRows(5);
        jScrollPane2.setViewportView(txtOtherInvTC);

        jLabel8.setText("700 Char Max");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel8)))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Ticketing Invoice T/C", jPanel3);

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder("Ticketing Invoice"));

        txtTicketingInvoiceFooter.setColumns(20);
        txtTicketingInvoiceFooter.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        txtTicketingInvoiceFooter.setLineWrap(true);
        txtTicketingInvoiceFooter.setRows(3);
        jScrollPane3.setViewportView(txtTicketingInvoiceFooter);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3)
                .addContainerGap())
        );

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder("Other Invoice"));

        txtOtherInvoiceFooter.setColumns(20);
        txtOtherInvoiceFooter.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        txtOtherInvoiceFooter.setLineWrap(true);
        txtOtherInvoiceFooter.setRows(3);
        jScrollPane4.setViewportView(txtOtherInvoiceFooter);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4)
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(208, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Invoice Footer", jPanel4);

        jLabel5.setText("VAT Rate");

        txtVAT.setText("0.00");

        jLabel6.setText("Main Agent Code *");

        pnlImage.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Images", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        pnlImage.setLayout(new java.awt.GridBagLayout());

        lblAtolLogo.setForeground(new java.awt.Color(153, 153, 153));
        lblAtolLogo.setText("ATOL Logo");
        lblAtolLogo.setToolTipText("<html>\nBest view 30,30 px <br> Max 80,80 px\n</html>");
        lblAtolLogo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        lblAtolLogo.setMaximumSize(new java.awt.Dimension(80, 80));
        lblAtolLogo.setMinimumSize(new java.awt.Dimension(80, 80));
        lblAtolLogo.setPreferredSize(new java.awt.Dimension(80, 80));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 4);
        pnlImage.add(lblAtolLogo, gridBagConstraints);

        btnBrowseAtolLogo.setText("ATOL");
        btnBrowseAtolLogo.setToolTipText("Click to browse logo");
        btnBrowseAtolLogo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseAtolLogoActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        pnlImage.add(btnBrowseAtolLogo, gridBagConstraints);

        btnBrowseCompanyLogo.setText("Business");
        btnBrowseCompanyLogo.setToolTipText("Click to browse logo");
        btnBrowseCompanyLogo.setMaximumSize(new java.awt.Dimension(85, 23));
        btnBrowseCompanyLogo.setMinimumSize(new java.awt.Dimension(85, 23));
        btnBrowseCompanyLogo.setPreferredSize(new java.awt.Dimension(85, 23));
        btnBrowseCompanyLogo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseCompanyLogoActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        pnlImage.add(btnBrowseCompanyLogo, gridBagConstraints);

        lblCompanyLogo.setForeground(new java.awt.Color(153, 153, 153));
        lblCompanyLogo.setText("Business Logo");
        lblCompanyLogo.setToolTipText("Best view 65 X 65 px");
        lblCompanyLogo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        lblCompanyLogo.setMaximumSize(new java.awt.Dimension(80, 80));
        lblCompanyLogo.setMinimumSize(new java.awt.Dimension(80, 80));
        lblCompanyLogo.setPreferredSize(new java.awt.Dimension(80, 80));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 4);
        pnlImage.add(lblCompanyLogo, gridBagConstraints);

        btnBrowseIataLogo.setText("IATA");
        btnBrowseIataLogo.setToolTipText("Click to browse logo");
        btnBrowseIataLogo.setMaximumSize(new java.awt.Dimension(85, 23));
        btnBrowseIataLogo.setMinimumSize(new java.awt.Dimension(85, 23));
        btnBrowseIataLogo.setPreferredSize(new java.awt.Dimension(85, 23));
        btnBrowseIataLogo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseIataLogoActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        pnlImage.add(btnBrowseIataLogo, gridBagConstraints);

        lblIataLogo.setForeground(new java.awt.Color(153, 153, 153));
        lblIataLogo.setText("IATA Logo");
        lblIataLogo.setToolTipText("");
        lblIataLogo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        lblIataLogo.setMaximumSize(new java.awt.Dimension(90, 40));
        lblIataLogo.setMinimumSize(new java.awt.Dimension(90, 40));
        lblIataLogo.setPreferredSize(new java.awt.Dimension(90, 40));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 4);
        pnlImage.add(lblIataLogo, gridBagConstraints);

        lblOtherLogo.setForeground(new java.awt.Color(153, 153, 153));
        lblOtherLogo.setText("Other Logo");
        lblOtherLogo.setToolTipText("Max 70, 35 px");
        lblOtherLogo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        lblOtherLogo.setMaximumSize(new java.awt.Dimension(70, 35));
        lblOtherLogo.setMinimumSize(new java.awt.Dimension(70, 35));
        lblOtherLogo.setPreferredSize(new java.awt.Dimension(70, 35));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 4);
        pnlImage.add(lblOtherLogo, gridBagConstraints);

        btnBrowseOtherLogo.setText("Other");
        btnBrowseOtherLogo.setToolTipText("Click to browse logo");
        btnBrowseOtherLogo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseOtherLogoActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        pnlImage.add(btnBrowseOtherLogo, gridBagConstraints);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(pnlImage, javax.swing.GroupLayout.DEFAULT_SIZE, 516, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtVAT, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
                            .addComponent(txtMainAgentID, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(336, 336, 336))))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtVAT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtMainAgentID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(38, 38, 38)
                .addComponent(pnlImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(156, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Other Settings", jPanel6);

        jLabel1.setText("Email Address *");

        jLabel2.setText("Email Password *");

        jLabel3.setText("Outgoing Mail Server *");

        jLabel4.setText("Port *");

        chkSmtpAuth.setText("SMTP Auth");

        chkStarttls.setText("STARTTLS");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPort, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(txtSmtpServer, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                        .addComponent(txtEmail)
                        .addComponent(txtEmailPassword))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(chkStarttls)
                        .addGap(18, 18, 18)
                        .addComponent(chkSmtpAuth)))
                .addContainerGap(198, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtEmailPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(txtSmtpServer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(4, 4, 4)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkStarttls)
                    .addComponent(chkSmtpAuth))
                .addContainerGap(249, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Email Settings", jPanel7);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 439, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        btnSave.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/save18.png"))); // NOI18N
        btnSave.setText("Save");

        btnClose.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/exit18.png"))); // NOI18N
        btnClose.setText("Close");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        lblInfo.setText("Message");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(315, 315, 315)
                .addComponent(btnClose)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSave)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 1, Short.MAX_VALUE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lblInfo)
                        .addGap(11, 11, 11))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnClose)
                            .addComponent(btnSave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnBrowseCompanyLogoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseCompanyLogoActionPerformed
        int choice = fileChooser.showOpenDialog(this);
        if (choice != JFileChooser.APPROVE_OPTION) {
            return;
        }
        processCompanyLogo(fileChooser.getSelectedFile());
    }//GEN-LAST:event_btnBrowseCompanyLogoActionPerformed

    private void btnBrowseAtolLogoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseAtolLogoActionPerformed
        int choice = fileChooser.showOpenDialog(this);
        if (choice != JFileChooser.APPROVE_OPTION) {
            return;
        }
        processAtolLogo(fileChooser.getSelectedFile());
    }//GEN-LAST:event_btnBrowseAtolLogoActionPerformed

    private void btnBrowseIataLogoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseIataLogoActionPerformed
        int choice = fileChooser.showOpenDialog(this);
        if (choice != JFileChooser.APPROVE_OPTION) {
            return;
        }
        processIataLogo(fileChooser.getSelectedFile());
    }//GEN-LAST:event_btnBrowseIataLogoActionPerformed

    private void btnBrowseOtherLogoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseOtherLogoActionPerformed
        int choice = fileChooser.showOpenDialog(this);
        if (choice != JFileChooser.APPROVE_OPTION) {
            return;
        }
        processOtherLogo(fileChooser.getSelectedFile());
    }//GEN-LAST:event_btnBrowseOtherLogoActionPerformed

    @Override
    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();
        if (source == btnSave) {
            if (!txtEmail.getText().isEmpty()
                    && !txtEmailPassword.getText().isEmpty()
                    && !txtPort.getText().isEmpty()
                    && !txtSmtpServer.getText().isEmpty()) {
                save = true;
                setVisible(false);
            } else {
                lblInfo.setText("Enter mandatory fields");
            }
        } else if (source == btnClose) {
            setVisible(false);
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowseAtolLogo;
    private javax.swing.JButton btnBrowseCompanyLogo;
    private javax.swing.JButton btnBrowseIataLogo;
    private javax.swing.JButton btnBrowseOtherLogo;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox chkSmtpAuth;
    private javax.swing.JCheckBox chkStarttls;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblAtolLogo;
    private javax.swing.JLabel lblCompanyLogo;
    private javax.swing.JLabel lblIataLogo;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JLabel lblOtherLogo;
    private javax.swing.JPanel pnlImage;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JPasswordField txtEmailPassword;
    private javax.swing.JTextField txtMainAgentID;
    private javax.swing.JTextArea txtOtherInvTC;
    private javax.swing.JTextArea txtOtherInvoiceFooter;
    private javax.swing.JTextField txtPort;
    private javax.swing.JTextField txtSmtpServer;
    private javax.swing.JTextArea txtTicketingInvoiceFooter;
    private javax.swing.JTextArea txtTktInvTC;
    private javax.swing.JTextField txtVAT;
    // End of variables declaration//GEN-END:variables
}
