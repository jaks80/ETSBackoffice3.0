package etsbackoffice.gui;

import etsbackoffice.ETSBackofficeApp;
import etsbackoffice.businesslogic.TicketBo;
import etsbackoffice.domain.Ticket;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.jdesktop.application.Action;

/**
 *
 * @author Yusuf
 */
public class DlgTicketR extends JDialog implements ActionListener{

    private boolean save;
    private Ticket ticket;// = (Ticket) ETSBackofficeApp.getApplication().ctx.getBean("ticket");
    private TicketBo ticketBo = (TicketBo) ETSBackofficeApp.getApplication().ctx.getBean("ticketBo");
    /** Creates new form FrameTicket */
    public DlgTicketR(java.awt.Frame parent) {
        super(parent, "Passenger & Ticket Details", true);
        initComponents();
        btnOk.addActionListener(this);
        btnCancel.addActionListener(this);

        CheckInput a = new CheckInput(CheckInput.FLOAT);
        CheckInput b = new CheckInput(CheckInput.FLOAT);
        CheckInput c = new CheckInput(CheckInput.FLOAT);
        CheckInput d = new CheckInput(CheckInput.FLOAT);
        CheckInput e = new CheckInput(CheckInput.FLOAT);
        CheckInput f = new CheckInput(CheckInput.FLOAT);
        CheckInput g = new CheckInput(CheckInput.FLOAT);
        CheckInput h = new CheckInput(CheckInput.FLOAT);
        CheckInput i = new CheckInput(CheckInput.FLOAT);
        CheckInput j = new CheckInput(CheckInput.FLOAT);
        CheckInput k = new CheckInput(CheckInput.FLOAT);
                       
        c.setNegativeAccepted(true);   
        f.setNegativeAccepted(true);   
        g.setNegativeAccepted(true);        
        i.setNegativeAccepted(true);        
        k.setNegativeAccepted(true);
        
        txtFPaid.setDocument(a);
        txtFUsed.setDocument(b);
        txtBaseFare.setDocument(c);
        txtCFee.setDocument(d);
        txtCMiscFee.setDocument(e);
        txtCFeeCom.setDocument(f);
        txtTax.setDocument(g);
        txtBspCom.setDocument(h);
        txtGrossFare.setDocument(i);
        txtDisc.setDocument(j);
        txtAtol.setDocument(k);
    }

    @Action
    public void closeFrameTicketBox() {
        this.dispose();
    }

    public boolean showTicketDialog(Ticket ticket) {
        this.ticket = new Ticket();
        this.ticket = ticket;
            
        txtName.setText(this.ticket.getPaxSurName() + "/" + this.ticket.getPaxForeName());
        txtNumAirCode.setText(this.ticket.getNumericAirLineCode());
        txtTktNo.setText(this.ticket.getTicketNo());
        txtStatus.setText(this.ticket.getTktStatusString());
        datePicker.setDate(this.ticket.getDocIssuedate());
        
        txtFPaid.setText(this.ticket.getTicketRefundDetails().getFarePaid().toString());
        txtFUsed.setText(this.ticket.getTicketRefundDetails().getFareUsed().toString());
        txtBaseFare.setText(this.ticket.getBaseFare().toString());

        txtCFee.setText(this.ticket.getTicketRefundDetails().getCancellationFee().toString());
        if (this.ticket.getTicketRefundDetails().getCMiscFee() != null) {
            txtCMiscFee.setText(this.ticket.getTicketRefundDetails().getCMiscFee().toString());
        }
        if (this.ticket.getTicketRefundDetails().getCfCom() != null) {
            txtCFeeCom.setText(this.ticket.getTicketRefundDetails().getCfCom().toString());
        }
        txtTax.setText(this.ticket.getTax().toString());
        txtNetlFare.setText(this.ticket.getNetFare().toString());

        txtBspCom.setText(this.ticket.getBspCom().toString());

        txtGrossFare.setText(this.ticket.getGrossFare().toString());
        txtDisc.setText(this.ticket.getDiscount().toString());
        txtAtol.setText(this.ticket.getAtolChg().toString());
        txtNettToPay.setText(this.ticket.getNetPayble().toString());

        lblProfitLoss.setText(this.ticket.getTktdRevenue().toString());

        /*if (!this.ticket.getAccountingDocumentLine().isEmpty()) {
        txtBaseFare.setEditable(false);
        txtTax.setEditable(false);
        txtGrossFare.setEditable(false);
        txtDisc.setEditable(false);
        txtAtol.setEditable(false);
        }*/       
        
        save = false;
        setLocationRelativeTo(this);
        setVisible(true);


        if (save) {
            ticket = this.ticket;
        }

        return save;
    }    

    private void populateTotalFare() {
        txtNetlFare.setText(this.ticket.getBaseFare().add(this.ticket.getTax()).toString());
    }

    private void populateNettToPay() {
        txtNettToPay.setText(this.ticket.getNetPayble().toString());
    }
    private void populateProfitLoss(){
    lblProfitLoss.setText(this.ticket.getTktdRevenue().toString());
    }
    
    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();
        if (source == btnOk) {
            if (this.ticket.getGrossFare().compareTo(this.ticket.getNetFare()) == -1) {
                JOptionPane.showMessageDialog(null, "Gross Refund should be\nless then Sysytem Refund", "Refund Ticket", JOptionPane.WARNING_MESSAGE);
            } else {
                save = true;
                setVisible(false);
            }
        } else {
            dispose();
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblBaseFare = new javax.swing.JLabel();
        lblTax = new javax.swing.JLabel();
        lblTotalFare = new javax.swing.JLabel();
        lblBspCom = new javax.swing.JLabel();
        lblGrossFare = new javax.swing.JLabel();
        lblDisc = new javax.swing.JLabel();
        lblAtol = new javax.swing.JLabel();
        lblNetToPay = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        txtTktNo = new javax.swing.JTextField();
        txtStatus = new javax.swing.JTextField();
        txtBaseFare = new javax.swing.JTextField();
        txtTax = new javax.swing.JTextField();
        txtNetlFare = new javax.swing.JTextField();
        txtBspCom = new javax.swing.JTextField();
        jSeparator3 = new javax.swing.JSeparator();
        txtGrossFare = new javax.swing.JTextField();
        txtDisc = new javax.swing.JTextField();
        txtAtol = new javax.swing.JTextField();
        txtNettToPay = new javax.swing.JTextField();
        btnCancel = new javax.swing.JButton();
        btnOk = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel14 = new javax.swing.JLabel();
        lblProfitLoss = new javax.swing.JLabel();
        datePicker = new org.jdesktop.swingx.JXDatePicker();
        lblGFMessage = new javax.swing.JLabel();
        txtNumAirCode = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtCFee = new javax.swing.JTextField();
        txtCFeeCom = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtCMiscFee = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtFPaid = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtFUsed = new javax.swing.JTextField();
        busyLabel = new org.jdesktop.swingx.JXBusyLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getResourceMap(DlgTicketR.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(resourceMap.getFont("lblTotalFare.font")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        getContentPane().add(jLabel1, gridBagConstraints);

        jLabel2.setFont(resourceMap.getFont("lblTotalFare.font")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        getContentPane().add(jLabel2, gridBagConstraints);

        jLabel3.setFont(resourceMap.getFont("lblTotalFare.font")); // NOI18N
        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        getContentPane().add(jLabel3, gridBagConstraints);

        jLabel4.setFont(resourceMap.getFont("lblTotalFare.font")); // NOI18N
        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        getContentPane().add(jLabel4, gridBagConstraints);

        lblBaseFare.setFont(resourceMap.getFont("lblTotalFare.font")); // NOI18N
        lblBaseFare.setText(resourceMap.getString("lblBaseFare.text")); // NOI18N
        lblBaseFare.setName("lblBaseFare"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        getContentPane().add(lblBaseFare, gridBagConstraints);

        lblTax.setFont(resourceMap.getFont("lblTotalFare.font")); // NOI18N
        lblTax.setText(resourceMap.getString("lblTax.text")); // NOI18N
        lblTax.setName("lblTax"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        getContentPane().add(lblTax, gridBagConstraints);

        lblTotalFare.setFont(resourceMap.getFont("lblTotalFare.font")); // NOI18N
        lblTotalFare.setText(resourceMap.getString("lblTotalFare.text")); // NOI18N
        lblTotalFare.setName("lblTotalFare"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        getContentPane().add(lblTotalFare, gridBagConstraints);

        lblBspCom.setFont(resourceMap.getFont("lblTotalFare.font")); // NOI18N
        lblBspCom.setText(resourceMap.getString("lblBspCom.text")); // NOI18N
        lblBspCom.setName("lblBspCom"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        getContentPane().add(lblBspCom, gridBagConstraints);

        lblGrossFare.setFont(resourceMap.getFont("lblTotalFare.font")); // NOI18N
        lblGrossFare.setText(resourceMap.getString("lblGrossFare.text")); // NOI18N
        lblGrossFare.setName("lblGrossFare"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        getContentPane().add(lblGrossFare, gridBagConstraints);

        lblDisc.setFont(resourceMap.getFont("lblTotalFare.font")); // NOI18N
        lblDisc.setText(resourceMap.getString("lblDisc.text")); // NOI18N
        lblDisc.setName("lblDisc"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        getContentPane().add(lblDisc, gridBagConstraints);

        lblAtol.setFont(resourceMap.getFont("lblTotalFare.font")); // NOI18N
        lblAtol.setText(resourceMap.getString("lblAtol.text")); // NOI18N
        lblAtol.setName("lblAtol"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        getContentPane().add(lblAtol, gridBagConstraints);

        lblNetToPay.setFont(resourceMap.getFont("lblTotalFare.font")); // NOI18N
        lblNetToPay.setText(resourceMap.getString("lblNetToPay.text")); // NOI18N
        lblNetToPay.setName("lblNetToPay"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        getContentPane().add(lblNetToPay, gridBagConstraints);

        jLabel13.setFont(resourceMap.getFont("jLabel13.font")); // NOI18N
        jLabel13.setForeground(resourceMap.getColor("jLabel13.foreground")); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        getContentPane().add(jLabel13, gridBagConstraints);

        txtName.setEditable(false);
        txtName.setFont(resourceMap.getFont("txtName.font")); // NOI18N
        txtName.setText(resourceMap.getString("txtName.text")); // NOI18N
        txtName.setName("txtName"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        getContentPane().add(txtName, gridBagConstraints);

        txtTktNo.setEditable(false);
        txtTktNo.setFont(resourceMap.getFont("txtBaseFare.font")); // NOI18N
        txtTktNo.setText(resourceMap.getString("txtTktNo.text")); // NOI18N
        txtTktNo.setName("txtTktNo"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        getContentPane().add(txtTktNo, gridBagConstraints);

        txtStatus.setEditable(false);
        txtStatus.setFont(resourceMap.getFont("txtStatus.font")); // NOI18N
        txtStatus.setText(resourceMap.getString("txtStatus.text")); // NOI18N
        txtStatus.setMinimumSize(new java.awt.Dimension(150, 20));
        txtStatus.setName("txtStatus"); // NOI18N
        txtStatus.setPreferredSize(new java.awt.Dimension(150, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        getContentPane().add(txtStatus, gridBagConstraints);

        txtBaseFare.setEditable(false);
        txtBaseFare.setFont(resourceMap.getFont("txtBaseFare.font")); // NOI18N
        txtBaseFare.setText(resourceMap.getString("txtBaseFare.text")); // NOI18N
        txtBaseFare.setMinimumSize(new java.awt.Dimension(150, 20));
        txtBaseFare.setName("txtBaseFare"); // NOI18N
        txtBaseFare.setPreferredSize(new java.awt.Dimension(150, 20));
        txtBaseFare.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtBaseFareFocusGained(evt);
            }
        });
        txtBaseFare.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBaseFareKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        getContentPane().add(txtBaseFare, gridBagConstraints);

        txtTax.setFont(resourceMap.getFont("txtBaseFare.font")); // NOI18N
        txtTax.setText(resourceMap.getString("txtTax.text")); // NOI18N
        txtTax.setMinimumSize(new java.awt.Dimension(150, 20));
        txtTax.setName("txtTax"); // NOI18N
        txtTax.setPreferredSize(new java.awt.Dimension(150, 20));
        txtTax.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtTaxFocusGained(evt);
            }
        });
        txtTax.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTaxKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        getContentPane().add(txtTax, gridBagConstraints);

        txtNetlFare.setEditable(false);
        txtNetlFare.setFont(resourceMap.getFont("txtNetlFare.font")); // NOI18N
        txtNetlFare.setForeground(resourceMap.getColor("txtNetlFare.foreground")); // NOI18N
        txtNetlFare.setMinimumSize(new java.awt.Dimension(150, 20));
        txtNetlFare.setName("txtNetlFare"); // NOI18N
        txtNetlFare.setPreferredSize(new java.awt.Dimension(150, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        getContentPane().add(txtNetlFare, gridBagConstraints);

        txtBspCom.setFont(resourceMap.getFont("txtBaseFare.font")); // NOI18N
        txtBspCom.setMinimumSize(new java.awt.Dimension(150, 20));
        txtBspCom.setName("txtBspCom"); // NOI18N
        txtBspCom.setPreferredSize(new java.awt.Dimension(150, 20));
        txtBspCom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                txtBspComMouseReleased(evt);
            }
        });
        txtBspCom.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtBspComFocusGained(evt);
            }
        });
        txtBspCom.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBspComKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        getContentPane().add(txtBspCom, gridBagConstraints);

        jSeparator3.setName("jSeparator3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 4;
        getContentPane().add(jSeparator3, gridBagConstraints);

        txtGrossFare.setBackground(resourceMap.getColor("txtGrossFare.background")); // NOI18N
        txtGrossFare.setFont(resourceMap.getFont("txtBaseFare.font")); // NOI18N
        txtGrossFare.setMinimumSize(new java.awt.Dimension(150, 20));
        txtGrossFare.setName("txtGrossFare"); // NOI18N
        txtGrossFare.setPreferredSize(new java.awt.Dimension(150, 20));
        txtGrossFare.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtGrossFareFocusGained(evt);
            }
        });
        txtGrossFare.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtGrossFareKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        getContentPane().add(txtGrossFare, gridBagConstraints);

        txtDisc.setFont(resourceMap.getFont("txtBaseFare.font")); // NOI18N
        txtDisc.setMinimumSize(new java.awt.Dimension(150, 20));
        txtDisc.setName("txtDisc"); // NOI18N
        txtDisc.setPreferredSize(new java.awt.Dimension(150, 20));
        txtDisc.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDiscFocusGained(evt);
            }
        });
        txtDisc.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDiscKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        getContentPane().add(txtDisc, gridBagConstraints);

        txtAtol.setFont(resourceMap.getFont("txtBaseFare.font")); // NOI18N
        txtAtol.setMinimumSize(new java.awt.Dimension(150, 20));
        txtAtol.setName("txtAtol"); // NOI18N
        txtAtol.setPreferredSize(new java.awt.Dimension(150, 20));
        txtAtol.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtAtolFocusGained(evt);
            }
        });
        txtAtol.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAtolKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        getContentPane().add(txtAtol, gridBagConstraints);

        txtNettToPay.setEditable(false);
        txtNettToPay.setFont(resourceMap.getFont("txtNettToPay.font")); // NOI18N
        txtNettToPay.setForeground(resourceMap.getColor("txtNettToPay.foreground")); // NOI18N
        txtNettToPay.setText(resourceMap.getString("txtNettToPay.text")); // NOI18N
        txtNettToPay.setMinimumSize(new java.awt.Dimension(150, 20));
        txtNettToPay.setName("txtNettToPay"); // NOI18N
        txtNettToPay.setPreferredSize(new java.awt.Dimension(150, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        getContentPane().add(txtNettToPay, gridBagConstraints);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getActionMap(DlgTicketR.class, this);
        btnCancel.setAction(actionMap.get("closeFrameTicketBox")); // NOI18N
        btnCancel.setFont(resourceMap.getFont("btnCancel.font")); // NOI18N
        btnCancel.setIcon(resourceMap.getIcon("btnCancel.icon")); // NOI18N
        btnCancel.setText(resourceMap.getString("btnCancel.text")); // NOI18N
        btnCancel.setName("btnCancel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(6, 2, 6, 2);
        getContentPane().add(btnCancel, gridBagConstraints);

        btnOk.setFont(resourceMap.getFont("btnOk.font")); // NOI18N
        btnOk.setIcon(resourceMap.getIcon("btnOk.icon")); // NOI18N
        btnOk.setText(resourceMap.getString("btnOk.text")); // NOI18N
        btnOk.setName("btnOk"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(6, 2, 6, 2);
        getContentPane().add(btnOk, gridBagConstraints);

        jSeparator1.setName("jSeparator1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        getContentPane().add(jSeparator1, gridBagConstraints);

        jSeparator2.setName("jSeparator2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 8, 0);
        getContentPane().add(jSeparator2, gridBagConstraints);

        jLabel14.setFont(resourceMap.getFont("jLabel14.font")); // NOI18N
        jLabel14.setForeground(resourceMap.getColor("jLabel14.foreground")); // NOI18N
        jLabel14.setText(resourceMap.getString("jLabel14.text")); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 0);
        getContentPane().add(jLabel14, gridBagConstraints);

        lblProfitLoss.setFont(resourceMap.getFont("lblProfitLoss.font")); // NOI18N
        lblProfitLoss.setForeground(resourceMap.getColor("lblProfitLoss.foreground")); // NOI18N
        lblProfitLoss.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblProfitLoss.setText(resourceMap.getString("lblProfitLoss.text")); // NOI18N
        lblProfitLoss.setName("lblProfitLoss"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        getContentPane().add(lblProfitLoss, gridBagConstraints);

        datePicker.setFont(resourceMap.getFont("txtBaseFare.font")); // NOI18N
        datePicker.setName("datePicker"); // NOI18N
        datePicker.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                datePickerActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        getContentPane().add(datePicker, gridBagConstraints);

        lblGFMessage.setFont(resourceMap.getFont("lblGFMessage.font")); // NOI18N
        lblGFMessage.setForeground(resourceMap.getColor("lblGFMessage.foreground")); // NOI18N
        lblGFMessage.setText(resourceMap.getString("lblGFMessage.text")); // NOI18N
        lblGFMessage.setName("lblGFMessage"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        getContentPane().add(lblGFMessage, gridBagConstraints);

        txtNumAirCode.setEditable(false);
        txtNumAirCode.setFont(resourceMap.getFont("txtNumAirCode.font")); // NOI18N
        txtNumAirCode.setText(resourceMap.getString("txtNumAirCode.text")); // NOI18N
        txtNumAirCode.setMinimumSize(new java.awt.Dimension(150, 20));
        txtNumAirCode.setName("txtNumAirCode"); // NOI18N
        txtNumAirCode.setPreferredSize(new java.awt.Dimension(150, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        getContentPane().add(txtNumAirCode, gridBagConstraints);

        jLabel8.setFont(resourceMap.getFont("jLabel6.font")); // NOI18N
        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        getContentPane().add(jLabel8, gridBagConstraints);

        jLabel5.setFont(resourceMap.getFont("jLabel6.font")); // NOI18N
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        getContentPane().add(jLabel5, gridBagConstraints);

        txtCFee.setFont(resourceMap.getFont("txtCFeeCom.font")); // NOI18N
        txtCFee.setText(resourceMap.getString("txtCFee.text")); // NOI18N
        txtCFee.setName("txtCFee"); // NOI18N
        txtCFee.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCFeeKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        getContentPane().add(txtCFee, gridBagConstraints);

        txtCFeeCom.setFont(resourceMap.getFont("txtCFeeCom.font")); // NOI18N
        txtCFeeCom.setText(resourceMap.getString("txtCFeeCom.text")); // NOI18N
        txtCFeeCom.setName("txtCFeeCom"); // NOI18N
        txtCFeeCom.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCFeeComKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        getContentPane().add(txtCFeeCom, gridBagConstraints);

        jLabel6.setFont(resourceMap.getFont("jLabel6.font")); // NOI18N
        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        getContentPane().add(jLabel6, gridBagConstraints);

        txtCMiscFee.setFont(resourceMap.getFont("txtCFeeCom.font")); // NOI18N
        txtCMiscFee.setText(resourceMap.getString("txtCMiscFee.text")); // NOI18N
        txtCMiscFee.setName("txtCMiscFee"); // NOI18N
        txtCMiscFee.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCMiscFeeKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        getContentPane().add(txtCMiscFee, gridBagConstraints);

        jLabel7.setFont(resourceMap.getFont("jLabel7.font")); // NOI18N
        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        getContentPane().add(jLabel7, gridBagConstraints);

        txtFPaid.setEditable(false);
        txtFPaid.setFont(resourceMap.getFont("txtFPaid.font")); // NOI18N
        txtFPaid.setText(resourceMap.getString("txtFPaid.text")); // NOI18N
        txtFPaid.setName("txtFPaid"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        getContentPane().add(txtFPaid, gridBagConstraints);

        jLabel9.setFont(resourceMap.getFont("jLabel9.font")); // NOI18N
        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        getContentPane().add(jLabel9, gridBagConstraints);

        txtFUsed.setFont(resourceMap.getFont("txtFUsed.font")); // NOI18N
        txtFUsed.setText(resourceMap.getString("txtFUsed.text")); // NOI18N
        txtFUsed.setName("txtFUsed"); // NOI18N
        txtFUsed.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFUsedKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        getContentPane().add(txtFUsed, gridBagConstraints);

        busyLabel.setText(resourceMap.getString("busyLabel.text")); // NOI18N
        busyLabel.setName("busyLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        getContentPane().add(busyLabel, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtBaseFareKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBaseFareKeyReleased
        String text = txtBaseFare.getText().replaceAll("[^.0-9]", "");
        if(!text.isEmpty()){
        this.ticket.setBaseFare(new BigDecimal(text).negate().setScale(2, RoundingMode.HALF_DOWN));
        populateTotalFare();
        populateNettToPay();
        populateProfitLoss();
        }
    }//GEN-LAST:event_txtBaseFareKeyReleased

    private void txtTaxKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTaxKeyReleased
        String text = txtTax.getText();
        if (!text.isEmpty()) {
            this.ticket.setTax(new BigDecimal(text).negate());
            populateTotalFare();
            populateNettToPay();
            populateProfitLoss();
        }
    }//GEN-LAST:event_txtTaxKeyReleased

    private void txtBspComMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtBspComMouseReleased
       
    }//GEN-LAST:event_txtBspComMouseReleased

    private void txtGrossFareKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtGrossFareKeyReleased
        String text = txtGrossFare.getText().replaceAll("[^.0-9]", "");    
        if (!text.isEmpty()) {
            this.ticket.setGrossFare(new BigDecimal(text));
            if (this.ticket.getGrossFare().compareTo(this.ticket.getNetFare()) == -1) {
                lblGFMessage.setText("G.Refund is more then S.Refund");
            } else {
                lblGFMessage.setText("");
            }
            populateNettToPay();
            populateProfitLoss();
        }
    }//GEN-LAST:event_txtGrossFareKeyReleased

    private void txtDiscKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDiscKeyReleased
        String text = txtDisc.getText();
        if(!text.isEmpty()){
        this.ticket.setDiscount(new BigDecimal(text));
       populateNettToPay();
       populateProfitLoss();
        }
    }//GEN-LAST:event_txtDiscKeyReleased

    private void txtAtolKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAtolKeyReleased
        String text = txtAtol.getText().replaceAll("[^.0-9]", "");
        if(!text.isEmpty()){ 
        this.ticket.setAtolChg(new BigDecimal(text).negate());
       populateNettToPay();  
        }
    }//GEN-LAST:event_txtAtolKeyReleased

    private void txtBspComKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBspComKeyReleased
        String text = txtBspCom.getText().replaceAll("[^.0-9]", "");       
        if(!text.isEmpty()){
        this.ticket.setBspCom(new BigDecimal(text));
         txtNetlFare.setText(this.ticket.getNetFare().toString());
        populateProfitLoss();
        }
    }//GEN-LAST:event_txtBspComKeyReleased

    private void txtBaseFareFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBaseFareFocusGained
        txtBaseFare.selectAll();
    }//GEN-LAST:event_txtBaseFareFocusGained

    private void txtTaxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTaxFocusGained
        txtTax.selectAll();
    }//GEN-LAST:event_txtTaxFocusGained

    private void txtBspComFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBspComFocusGained
        txtBspCom.selectAll();
    }//GEN-LAST:event_txtBspComFocusGained

    private void txtGrossFareFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtGrossFareFocusGained
       txtGrossFare.selectAll();
    }//GEN-LAST:event_txtGrossFareFocusGained

    private void txtDiscFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDiscFocusGained
        txtDisc.selectAll();
    }//GEN-LAST:event_txtDiscFocusGained

    private void txtAtolFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAtolFocusGained
       txtAtol.selectAll();
    }//GEN-LAST:event_txtAtolFocusGained

    private void txtFUsedKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFUsedKeyReleased
        String fUsed = txtFUsed.getText();
        BigDecimal fareUsed = new BigDecimal("0.00");
        
        if(!fUsed.isEmpty()){
         fareUsed = new BigDecimal(fUsed);
        }        
        this.ticket.getTicketRefundDetails().setFareUsed(fareUsed);
        txtBaseFare.setText(this.ticket.getBFRefundCalculation().toString());
        txtNetlFare.setText(this.ticket.getNetFare().toString());
    }//GEN-LAST:event_txtFUsedKeyReleased

    private void txtCFeeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCFeeKeyReleased
       String cFee = txtCFee.getText();
        BigDecimal cancellationFee = new BigDecimal("0.00");
        
        if(!cFee.isEmpty()){
         cancellationFee = new BigDecimal(cFee);
        }        
        this.ticket.getTicketRefundDetails().setCancellationFee(cancellationFee);
        //txtBaseFare.setText(this.ticket.getBFRefundCalculation().toString());
        txtNetlFare.setText(this.ticket.getNetFare().toString());
    }//GEN-LAST:event_txtCFeeKeyReleased

    private void txtCMiscFeeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCMiscFeeKeyReleased
         String cMiscFee = txtCMiscFee.getText();
        BigDecimal cancellationMiscFee = new BigDecimal("0.00");
        
        if(!cMiscFee.isEmpty()){
         cancellationMiscFee = new BigDecimal(cMiscFee);
        }        
        this.ticket.getTicketRefundDetails().setCMiscFee(cancellationMiscFee);
        //txtBaseFare.setText(this.ticket.getBFRefundCalculation().toString());
        txtNetlFare.setText(this.ticket.getNetFare().toString());
    }//GEN-LAST:event_txtCMiscFeeKeyReleased

    private void txtCFeeComKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCFeeComKeyReleased
        String cFeeCom = txtCFeeCom.getText().replaceAll("[^.0-9]", "");    
        BigDecimal cancellationFeeCom = new BigDecimal("0.00");
        
        if(!cFeeCom.isEmpty()){
         cancellationFeeCom = new BigDecimal(cFeeCom).negate();
        }        
        this.ticket.getTicketRefundDetails().setCfCom(cancellationFeeCom);
        //txtBaseFare.setText(this.ticket.getBFRefundCalculation().toString());
        txtNetlFare.setText(this.ticket.getNetFare().toString());
    }//GEN-LAST:event_txtCFeeComKeyReleased

    private void datePickerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_datePickerActionPerformed
        this.ticket.setDocIssuedate(datePicker.getDate());
    }//GEN-LAST:event_datePickerActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOk;
    private org.jdesktop.swingx.JXBusyLabel busyLabel;
    private org.jdesktop.swingx.JXDatePicker datePicker;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel lblAtol;
    private javax.swing.JLabel lblBaseFare;
    private javax.swing.JLabel lblBspCom;
    private javax.swing.JLabel lblDisc;
    private javax.swing.JLabel lblGFMessage;
    private javax.swing.JLabel lblGrossFare;
    private javax.swing.JLabel lblNetToPay;
    private javax.swing.JLabel lblProfitLoss;
    private javax.swing.JLabel lblTax;
    private javax.swing.JLabel lblTotalFare;
    private javax.swing.JTextField txtAtol;
    private javax.swing.JTextField txtBaseFare;
    private javax.swing.JTextField txtBspCom;
    private javax.swing.JTextField txtCFee;
    private javax.swing.JTextField txtCFeeCom;
    private javax.swing.JTextField txtCMiscFee;
    private javax.swing.JTextField txtDisc;
    private javax.swing.JTextField txtFPaid;
    private javax.swing.JTextField txtFUsed;
    private javax.swing.JTextField txtGrossFare;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtNetlFare;
    private javax.swing.JTextField txtNettToPay;
    private javax.swing.JTextField txtNumAirCode;
    private javax.swing.JTextField txtStatus;
    private javax.swing.JTextField txtTax;
    private javax.swing.JTextField txtTktNo;
    // End of variables declaration//GEN-END:variables

    private class threadTicket implements Runnable {

        private long tktId;

        public threadTicket(long tktId) {
            this.tktId = tktId;
        }

        public void run() {
            busyLabel.setBusy(true);
            //ticket.setTicketRefundDetails(ticketBo.loadTicketRfdDetails(tktId));
            busyLabel.setBusy(false);
        }
    }
}
