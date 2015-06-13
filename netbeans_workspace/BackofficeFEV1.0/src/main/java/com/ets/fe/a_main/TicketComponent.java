package com.ets.fe.a_main;

import com.ets.fe.pnr.logic.TicketLogic;
import com.ets.fe.pnr.model.Ticket;
import com.ets.fe.pnr.task.TicketTask;
import com.ets.fe.util.CheckInput;
import com.ets.fe.util.Enums;
import com.ets.fe.util.PnrUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.font.TextAttribute;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import org.jdesktop.swingx.JXTable;

/**
 *
 * @author Yusuf
 */
public class TicketComponent extends javax.swing.JPanel implements PropertyChangeListener {

    private boolean saveNeeded = false;

    private Ticket ticket;
    private int selectedRow = 0;

    private boolean editable;
    private List<Ticket> tickets = new ArrayList<>();
    private TicketTask ticketTask;
    private String taskType = "";
    private PnrPanel pnrPanel;
    
    public TicketComponent() {
        initComponents();

        CheckInput a = new CheckInput();
        CheckInput b = new CheckInput();
        CheckInput c = new CheckInput();
        CheckInput d = new CheckInput();
        CheckInput e = new CheckInput();
        CheckInput f = new CheckInput();
        CheckInput g = new CheckInput();

        a.setNegativeAccepted(true);
        b.setNegativeAccepted(true);
        c.setNegativeAccepted(true);
        d.setNegativeAccepted(true);
        e.setNegativeAccepted(true);
        f.setNegativeAccepted(true);

        txtBaseFare.setDocument(a);
        txtTax.setDocument(b);
        txtBspCom.setDocument(c);
        txtGrossFare.setDocument(d);
        txtDisc.setDocument(e);
        txtAtol.setDocument(f);
        txtAirlineCode.setDocument(g);

        cmbTicketStatus();
    }

    private void cmbTicketStatus() {
        DefaultComboBoxModel model = new DefaultComboBoxModel(Enums.TicketStatus.values());
        cmbStatus.setModel(model);
        //cmbStatus.setSelectedItem(ticket.getTktStatus());
    }

    private void deleteTicket(Ticket ticket) {
        if (ticket.getTicketingSalesAcDoc() == null) {
            if (ticket.getId() != null) {
                busyLabel.setBusy(true);
                taskType = "DELETE";
                ticketTask = new TicketTask(ticket, taskType, busyLabel);
                ticketTask.addPropertyChangeListener(this);
                ticketTask.execute();
            }
        } else {
            tickets.remove(ticket);
            populateTblTicket(tickets);
        }
    }

    public void populateTblTicket(List<Ticket> tickets) {
        //this.tickets = tickets;
        DefaultTableModel model = (DefaultTableModel) tblTicket.getModel();
        model.getDataVector().removeAllElements();

        BigDecimal totalPurchase = new BigDecimal("0.00");
        BigDecimal totalSelling = new BigDecimal("0.00");
        BigDecimal tPL = new BigDecimal("0.00");

        int row = 0;
        for (Ticket t : this.getTickets()) {
            boolean invoiced = t.getTicketingSalesAcDoc() != null;

            totalPurchase = totalPurchase.add(t.calculateNetPurchaseFare());
            totalSelling = totalSelling.add(t.calculateNetSellingFare());
            tPL = tPL.add(t.calculateRevenue());

            model.insertRow(row, new Object[]{PnrUtil.calculatePartialName(t.getFullPaxName()),
                t.getTktStatus(), t.getBaseFare(), t.getTax(), t.getCommission(), t.calculateNetPurchaseFare(),
                t.getGrossFare(), t.getDiscount(), t.calculateNetSellingFare(),
                t.calculateRevenue(), invoiced});

            row++;
        }

        model.addRow(new Object[]{"Total:", "", "", "", "", totalPurchase, "", "", totalSelling, tPL});

        if (!this.tickets.isEmpty()) {
            tblTicket.setRowSelectionInterval(selectedRow, selectedRow);
        }
    }

    public void displayTicket(Ticket ticket) {
        //this.ticket = ticket;
        if (ticket.getTicketingSalesAcDoc() != null) {
            editable = false;
        } else {
            editable = true;
        }
        txtName.setText(this.ticket.getSurName() + "/" + this.ticket.getForeName());
        txtAirlineCode.setText(this.ticket.getNumericAirLineCode());
        txtTktNo.setText(this.ticket.getTicketNo());
        cmbStatus.setSelectedItem(this.ticket.getTktStatus());
        dtIssueDate.setDate(this.ticket.getDocIssuedate());

        txtBaseFare.setText(this.ticket.getBaseFare().toString());
        txtTax.setText(this.ticket.getTax().toString());
        txtFees.setText(this.ticket.getFee().toString());
        txtBspCom.setText(this.ticket.getCommission().toString());
        txtNetPurchaseFare.setText(ticket.calculateNetPurchaseFare().toString());

        txtGrossFare.setText(this.ticket.getGrossFare().toString());
        txtDisc.setText(this.ticket.getDiscount().toString());
        txtAtol.setText(this.ticket.getAtolChg().toString());
        txtNetSellingFare.setText(this.ticket.calculateNetSellingFare().toString());

        lblRevenue.setText("Revenue: " + ticket.calculateRevenue().toString());

        setEditing();
    }

    private ListSelectionListener tblTicketListener = new ListSelectionListener() {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int row = tblTicket.getSelectedRow();
            if (row != -1 && row != tblTicket.getRowCount() - 1) {
                selectedRow = row;
                ticket = getTickets().get(row);
                displayTicket(ticket);
            }
        }
    };

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane2 = new javax.swing.JScrollPane();
        tblTicket = new JXTable(){
            public Component prepareRenderer(TableCellRenderer renderer,int rowIndex, int vColIndex) 
            {
                Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
                String s = this.getModel().getValueAt(rowIndex, 1).toString();
                if (s.equalsIgnoreCase("BOOK") ) 
                {c.setForeground(Color.yellow);
                } else if(s.equalsIgnoreCase("ISSUE")){
                    c.setForeground(Color.green);
                }else if(s.equalsIgnoreCase("REISSUE")){
                    c.setForeground(Color.cyan);}
                else if(s.equalsIgnoreCase("REFUND")){
                    c.setForeground(Color.red);
                }else if(s.equalsIgnoreCase("VOID")){c.setForeground(Color.ORANGE);
                    Map  attributes = c.getFont().getAttributes();
                    attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
                    Font newFont = new Font(attributes);
                    c.setFont(newFont);}
                else{
                    c.setForeground(Color.WHITE);
                }
                return c;} 
        };
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        lblRevenue = new javax.swing.JLabel();
        txtAirlineCode = new javax.swing.JTextField();
        txtBaseFare = new javax.swing.JTextField();
        txtTax = new javax.swing.JTextField();
        txtBspCom = new javax.swing.JTextField();
        txtNetPurchaseFare = new javax.swing.JTextField();
        txtGrossFare = new javax.swing.JTextField();
        txtDisc = new javax.swing.JTextField();
        txtAtol = new javax.swing.JTextField();
        txtNetSellingFare = new javax.swing.JTextField();
        cmbStatus = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtName = new javax.swing.JTextArea();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        txtTktNo = new javax.swing.JTextField();
        dtIssueDate = new org.jdesktop.swingx.JXDatePicker();
        jLabel13 = new javax.swing.JLabel();
        txtFees = new javax.swing.JTextField();
        btnPrev = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        btnView = new javax.swing.JButton();
        btnRefund = new javax.swing.JButton();
        btnReIssue = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnVoid = new javax.swing.JButton();
        btnNewTicket = new javax.swing.JButton();
        busyLabel = new org.jdesktop.swingx.JXBusyLabel();

        setPreferredSize(new java.awt.Dimension(900, 270));
        setLayout(new java.awt.GridBagLayout());

        jScrollPane2.setPreferredSize(new java.awt.Dimension(785, 360));

        tblTicket.setBackground(new java.awt.Color(0, 51, 51));
        tblTicket.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "P.Name", "Status", "Base Fare", "Tax", "Com", "Net.Fare", "G.Selling", "Disc", "NetSelling", "Rev", "Inv"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblTicket.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        tblTicket.setSelectionBackground(new java.awt.Color(0, 0, 0));
        tblTicket.setSelectionForeground(new java.awt.Color(0, 0, 0));
        tblTicket.setSortable(false);
        tblTicket.getTableHeader().setReorderingAllowed(false);
        tblTicket.getSelectionModel().addListSelectionListener(tblTicketListener);
        jScrollPane2.setViewportView(tblTicket);
        if (tblTicket.getColumnModel().getColumnCount() > 0) {
            tblTicket.getColumnModel().getColumn(0).setPreferredWidth(120);
            tblTicket.getColumnModel().getColumn(1).setMinWidth(60);
            tblTicket.getColumnModel().getColumn(1).setPreferredWidth(65);
            tblTicket.getColumnModel().getColumn(1).setMaxWidth(65);
            tblTicket.getColumnModel().getColumn(4).setMinWidth(55);
            tblTicket.getColumnModel().getColumn(4).setPreferredWidth(55);
            tblTicket.getColumnModel().getColumn(4).setMaxWidth(65);
            tblTicket.getColumnModel().getColumn(7).setMinWidth(55);
            tblTicket.getColumnModel().getColumn(7).setPreferredWidth(55);
            tblTicket.getColumnModel().getColumn(7).setMaxWidth(65);
            tblTicket.getColumnModel().getColumn(10).setMinWidth(30);
            tblTicket.getColumnModel().getColumn(10).setPreferredWidth(25);
            tblTicket.getColumnModel().getColumn(10).setMaxWidth(30);
        }

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPane2, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Ticket");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel1.add(jLabel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 4, 0);
        jPanel1.add(jSeparator1, gridBagConstraints);

        jLabel2.setText("Name");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel1.add(jLabel2, gridBagConstraints);

        jLabel3.setText("TktNo");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel1.add(jLabel3, gridBagConstraints);

        jLabel4.setText("Status");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel1.add(jLabel4, gridBagConstraints);

        jLabel5.setText("B.Fare");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel1.add(jLabel5, gridBagConstraints);

        jLabel6.setText("Tax");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel1.add(jLabel6, gridBagConstraints);

        jLabel7.setText("Com");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel1.add(jLabel7, gridBagConstraints);

        jLabel8.setText("NetFare");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel1.add(jLabel8, gridBagConstraints);

        jLabel9.setText("GrsFare");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel1.add(jLabel9, gridBagConstraints);

        jLabel10.setText("Disc");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel1.add(jLabel10, gridBagConstraints);

        jLabel11.setText("ATOL");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel1.add(jLabel11, gridBagConstraints);

        jLabel12.setText("NetSelling");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel1.add(jLabel12, gridBagConstraints);

        lblRevenue.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblRevenue.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblRevenue.setText("Revenue: 0.00");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 2, 2, 2);
        jPanel1.add(lblRevenue, gridBagConstraints);

        txtAirlineCode.setToolTipText("Airline Code");
        txtAirlineCode.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtAirlineCodeFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(txtAirlineCode, gridBagConstraints);

        txtBaseFare.setBackground(new java.awt.Color(204, 255, 204));
        txtBaseFare.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        txtBaseFare.setMinimumSize(new java.awt.Dimension(70, 20));
        txtBaseFare.setPreferredSize(new java.awt.Dimension(70, 20));
        txtBaseFare.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtBaseFareFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBaseFareFocusLost(evt);
            }
        });
        txtBaseFare.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBaseFareKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(txtBaseFare, gridBagConstraints);

        txtTax.setBackground(new java.awt.Color(204, 255, 204));
        txtTax.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        txtTax.setMinimumSize(new java.awt.Dimension(70, 20));
        txtTax.setPreferredSize(new java.awt.Dimension(70, 20));
        txtTax.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtTaxFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtTaxFocusLost(evt);
            }
        });
        txtTax.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTaxKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(txtTax, gridBagConstraints);

        txtBspCom.setBackground(new java.awt.Color(204, 255, 204));
        txtBspCom.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        txtBspCom.setMinimumSize(new java.awt.Dimension(70, 20));
        txtBspCom.setPreferredSize(new java.awt.Dimension(70, 20));
        txtBspCom.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtBspComFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBspComFocusLost(evt);
            }
        });
        txtBspCom.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBspComKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(txtBspCom, gridBagConstraints);

        txtNetPurchaseFare.setEditable(false);
        txtNetPurchaseFare.setBackground(new java.awt.Color(0, 0, 0));
        txtNetPurchaseFare.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        txtNetPurchaseFare.setForeground(new java.awt.Color(255, 255, 0));
        txtNetPurchaseFare.setMinimumSize(new java.awt.Dimension(70, 20));
        txtNetPurchaseFare.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(txtNetPurchaseFare, gridBagConstraints);

        txtGrossFare.setBackground(new java.awt.Color(255, 255, 204));
        txtGrossFare.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        txtGrossFare.setMinimumSize(new java.awt.Dimension(70, 20));
        txtGrossFare.setPreferredSize(new java.awt.Dimension(70, 20));
        txtGrossFare.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtGrossFareFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtGrossFareFocusLost(evt);
            }
        });
        txtGrossFare.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtGrossFareKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(txtGrossFare, gridBagConstraints);

        txtDisc.setBackground(new java.awt.Color(255, 255, 204));
        txtDisc.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        txtDisc.setMinimumSize(new java.awt.Dimension(70, 20));
        txtDisc.setPreferredSize(new java.awt.Dimension(70, 20));
        txtDisc.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDiscFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDiscFocusLost(evt);
            }
        });
        txtDisc.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDiscKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(txtDisc, gridBagConstraints);

        txtAtol.setBackground(new java.awt.Color(255, 255, 204));
        txtAtol.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        txtAtol.setMinimumSize(new java.awt.Dimension(70, 20));
        txtAtol.setPreferredSize(new java.awt.Dimension(70, 20));
        txtAtol.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtAtolFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtAtolFocusLost(evt);
            }
        });
        txtAtol.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAtolKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(txtAtol, gridBagConstraints);

        txtNetSellingFare.setEditable(false);
        txtNetSellingFare.setBackground(new java.awt.Color(0, 0, 0));
        txtNetSellingFare.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        txtNetSellingFare.setForeground(new java.awt.Color(255, 255, 0));
        txtNetSellingFare.setToolTipText("");
        txtNetSellingFare.setMinimumSize(new java.awt.Dimension(70, 20));
        txtNetSellingFare.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(txtNetSellingFare, gridBagConstraints);

        cmbStatus.setToolTipText("");
        cmbStatus.setMaximumSize(new java.awt.Dimension(32767, 19));
        cmbStatus.setMinimumSize(new java.awt.Dimension(28, 19));
        cmbStatus.setPreferredSize(new java.awt.Dimension(28, 19));
        cmbStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbStatusActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(cmbStatus, gridBagConstraints);

        txtName.setEditable(false);
        txtName.setColumns(12);
        txtName.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        txtName.setLineWrap(true);
        txtName.setRows(2);
        txtName.setToolTipText("Surname / Forename(s)");
        jScrollPane1.setViewportView(txtName);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(jScrollPane1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        jPanel1.add(jSeparator2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        jPanel1.add(jSeparator3, gridBagConstraints);

        txtTktNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtTktNoFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(txtTktNo, gridBagConstraints);

        dtIssueDate.setToolTipText("Date: Booking / Issue / Refund");
        dtIssueDate.setMaximumSize(new java.awt.Dimension(108, 20));
        dtIssueDate.setMinimumSize(new java.awt.Dimension(108, 20));
        dtIssueDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dtIssueDateActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(dtIssueDate, gridBagConstraints);

        jLabel13.setText("Fees");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel1.add(jLabel13, gridBagConstraints);

        txtFees.setBackground(new java.awt.Color(204, 255, 204));
        txtFees.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        txtFees.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtFeesFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtFeesFocusLost(evt);
            }
        });
        txtFees.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFeesKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(txtFees, gridBagConstraints);

        btnPrev.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnPrev.setText("<");
        btnPrev.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrevActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(btnPrev, gridBagConstraints);

        btnNext.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnNext.setText(">");
        btnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(btnNext, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        add(jPanel1, gridBagConstraints);

        btnView.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/details18.png"))); // NOI18N
        btnView.setMaximumSize(new java.awt.Dimension(35, 22));
        btnView.setMinimumSize(new java.awt.Dimension(35, 22));
        btnView.setPreferredSize(new java.awt.Dimension(35, 22));

        btnRefund.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/refund1-18.png"))); // NOI18N
        btnRefund.setToolTipText("Refund Ticket");
        btnRefund.setMaximumSize(new java.awt.Dimension(35, 22));
        btnRefund.setMinimumSize(new java.awt.Dimension(35, 22));
        btnRefund.setPreferredSize(new java.awt.Dimension(35, 22));
        btnRefund.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefundActionPerformed(evt);
            }
        });

        btnReIssue.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/reissue.png"))); // NOI18N
        btnReIssue.setToolTipText("Re-Issue Issued Ticket");
        btnReIssue.setMaximumSize(new java.awt.Dimension(35, 22));
        btnReIssue.setMinimumSize(new java.awt.Dimension(35, 22));
        btnReIssue.setPreferredSize(new java.awt.Dimension(35, 22));
        btnReIssue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReIssueActionPerformed(evt);
            }
        });

        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/delete18.png"))); // NOI18N
        btnDelete.setToolTipText("Delete Ticket");
        btnDelete.setMaximumSize(new java.awt.Dimension(35, 22));
        btnDelete.setMinimumSize(new java.awt.Dimension(35, 22));
        btnDelete.setPreferredSize(new java.awt.Dimension(35, 22));
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnVoid.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/void18-1.png"))); // NOI18N
        btnVoid.setToolTipText("VOID Ticket");
        btnVoid.setMaximumSize(new java.awt.Dimension(35, 22));
        btnVoid.setMinimumSize(new java.awt.Dimension(35, 22));
        btnVoid.setPreferredSize(new java.awt.Dimension(35, 22));
        btnVoid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVoidActionPerformed(evt);
            }
        });

        btnNewTicket.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/plus18.png"))); // NOI18N
        btnNewTicket.setToolTipText("Add Ticket Manually");
        btnNewTicket.setMaximumSize(new java.awt.Dimension(35, 22));
        btnNewTicket.setMinimumSize(new java.awt.Dimension(35, 22));
        btnNewTicket.setPreferredSize(new java.awt.Dimension(35, 22));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnView, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnRefund, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnReIssue, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnNewTicket, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnVoid, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(busyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(btnView, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnNewTicket, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(btnRefund, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(btnReIssue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 86, Short.MAX_VALUE)
                .addComponent(btnVoid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17)
                .addComponent(busyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(jPanel2, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void txtAirlineCodeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAirlineCodeFocusLost
        this.ticket.setNumericAirLineCode(txtAirlineCode.getText());
        setSaveNeeded(true);
    }//GEN-LAST:event_txtAirlineCodeFocusLost

    private void txtTktNoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTktNoFocusLost
        String val = txtTktNo.getText();
        if (val != null && !val.isEmpty()) {
            ticket.setTicketNo(val);
            setSaveNeeded(true);
        }
    }//GEN-LAST:event_txtTktNoFocusLost

    private void txtBaseFareKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBaseFareKeyReleased
        int key = evt.getKeyCode();
        String text = txtBaseFare.getText().replaceAll("[^.0-9]", "");
        if (key != KeyEvent.VK_ENTER && key != KeyEvent.VK_DOWN && key != KeyEvent.VK_UP
                && key != KeyEvent.VK_RIGHT && key != KeyEvent.VK_LEFT) {
            if (!text.isEmpty() && key != KeyEvent.VK_PERIOD && key != KeyEvent.VK_DECIMAL) {
                BigDecimal val = new BigDecimal(text);
                if (ticket.getTktStatus() == Enums.TicketStatus.REFUND) {
                    val = val.negate();
                }
                this.ticket.setBaseFare(val);
                calculatePurchaseBalance();
                populateTblTicket(tickets);
                setSaveNeeded(true);
            }
        } else {
            keyEvents(evt, txtAtol, txtTax);
        }
    }//GEN-LAST:event_txtBaseFareKeyReleased

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

    private void txtTaxKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTaxKeyReleased
        int key = evt.getKeyCode();
        String text = txtTax.getText().replaceAll("[^.0-9]", "");
        if (key != KeyEvent.VK_ENTER && key != KeyEvent.VK_DOWN && key != KeyEvent.VK_UP
                && key != KeyEvent.VK_RIGHT && key != KeyEvent.VK_LEFT) {
            if (!text.isEmpty() && key != KeyEvent.VK_PERIOD && key != KeyEvent.VK_DECIMAL) {
                BigDecimal val = new BigDecimal(text);
                if (ticket.getTktStatus() == Enums.TicketStatus.REFUND) {
                    val = val.negate();
                }
                this.ticket.setTax(val);
                calculatePurchaseBalance();
                populateTblTicket(tickets);
                setSaveNeeded(true);
            }
        } else {
            keyEvents(evt, txtBaseFare, txtFees);
        }
    }//GEN-LAST:event_txtTaxKeyReleased

    private void txtBspComKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBspComKeyReleased
        int key = evt.getKeyCode();
        String text = txtBspCom.getText().replaceAll("[^.0-9]", "");
        if (key != KeyEvent.VK_ENTER && key != KeyEvent.VK_DOWN && key != KeyEvent.VK_UP
                && key != KeyEvent.VK_RIGHT && key != KeyEvent.VK_LEFT) {
            if (!text.isEmpty() && key != KeyEvent.VK_PERIOD && key != KeyEvent.VK_DECIMAL) {
                BigDecimal val = new BigDecimal(text);
                if (ticket.getTktStatus() != Enums.TicketStatus.REFUND) {
                    val = val.negate();
                }
                this.ticket.setCommission(val);
                calculatePurchaseBalance();
                populateTblTicket(tickets);
                setSaveNeeded(true);
            }

        } else {
            keyEvents(evt, txtFees, txtGrossFare);
        }
    }//GEN-LAST:event_txtBspComKeyReleased

    private void txtGrossFareKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtGrossFareKeyReleased
        int key = evt.getKeyCode();
        String text = txtGrossFare.getText().replaceAll("[^.0-9]", "");
        if (key != KeyEvent.VK_ENTER && key != KeyEvent.VK_DOWN && key != KeyEvent.VK_UP
                && key != KeyEvent.VK_RIGHT && key != KeyEvent.VK_LEFT) {
            if (!text.isEmpty() && key != KeyEvent.VK_PERIOD && key != KeyEvent.VK_DECIMAL) {
                BigDecimal val = new BigDecimal(text);
                if (ticket.getTktStatus() == Enums.TicketStatus.REFUND) {
                    val = val.negate();
                }
                this.ticket.setGrossFare(val);
                calculateSellingBalance();
                populateTblTicket(tickets);
                setSaveNeeded(true);
            }
        } else {
            keyEvents(evt, txtBspCom, txtDisc);
        }
    }//GEN-LAST:event_txtGrossFareKeyReleased

    private void txtDiscKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDiscKeyReleased
        int key = evt.getKeyCode();
        String text = txtDisc.getText().replaceAll("[^.0-9]", "");
        if (key != KeyEvent.VK_ENTER && key != KeyEvent.VK_DOWN && key != KeyEvent.VK_UP
                && key != KeyEvent.VK_RIGHT && key != KeyEvent.VK_LEFT) {
            if (!text.isEmpty() && key != KeyEvent.VK_PERIOD && key != KeyEvent.VK_DECIMAL) {
                BigDecimal val = new BigDecimal(text);
                if (ticket.getTktStatus() != Enums.TicketStatus.REFUND) {
                    val = val.negate();
                }
                this.ticket.setDiscount(val);
                calculateSellingBalance();
                populateTblTicket(tickets);
                setSaveNeeded(true);
            }
        } else {
            keyEvents(evt, txtGrossFare, txtAtol);
        }
    }//GEN-LAST:event_txtDiscKeyReleased

    private void txtAtolKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAtolKeyReleased
        int key = evt.getKeyCode();
        String text = txtAtol.getText().replaceAll("[^.0-9]", "");
        if (key != KeyEvent.VK_ENTER && key != KeyEvent.VK_DOWN && key != KeyEvent.VK_UP
                && key != KeyEvent.VK_RIGHT && key != KeyEvent.VK_LEFT) {
            if (!text.isEmpty() && key != KeyEvent.VK_PERIOD && key != KeyEvent.VK_DECIMAL) {
                BigDecimal val = new BigDecimal(text);
                if (ticket.getTktStatus() == Enums.TicketStatus.REFUND) {
                    val = val.negate();
                }
                this.ticket.setAtolChg(val);
                calculateSellingBalance();
                populateTblTicket(tickets);
                setSaveNeeded(true);
            }
        } else {
            keyEvents(evt, txtDisc, txtBaseFare);
        }
    }//GEN-LAST:event_txtAtolKeyReleased

    private void txtBaseFareFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBaseFareFocusLost

    }//GEN-LAST:event_txtBaseFareFocusLost

    private void txtTaxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTaxFocusLost

    }//GEN-LAST:event_txtTaxFocusLost

    private void txtBspComFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBspComFocusLost

    }//GEN-LAST:event_txtBspComFocusLost

    private void txtGrossFareFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtGrossFareFocusLost
        txtGrossFare.setText(this.ticket.getGrossFare().toString());
    }//GEN-LAST:event_txtGrossFareFocusLost

    private void txtDiscFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDiscFocusLost

    }//GEN-LAST:event_txtDiscFocusLost

    private void txtAtolFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAtolFocusLost

    }//GEN-LAST:event_txtAtolFocusLost

    private void txtFeesFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFeesFocusGained
        txtFees.selectAll();
    }//GEN-LAST:event_txtFeesFocusGained

    private void txtFeesFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFeesFocusLost

    }//GEN-LAST:event_txtFeesFocusLost

    private void txtFeesKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFeesKeyReleased
        int key = evt.getKeyCode();
        String text = txtFees.getText().replaceAll("[^.0-9]", "");
        if (key != KeyEvent.VK_ENTER && key != KeyEvent.VK_DOWN && key != KeyEvent.VK_UP
                && key != KeyEvent.VK_RIGHT && key != KeyEvent.VK_LEFT) {
            if (!text.isEmpty() && key != KeyEvent.VK_PERIOD && key != KeyEvent.VK_DECIMAL) {
                BigDecimal val = new BigDecimal(text);
//                if (ticket.getTktStatus() == Enums.TicketStatus.REFUND) {
//                    val = val.negate();
//                }
                this.ticket.setFee(val);
                calculatePurchaseBalance();
                populateTblTicket(tickets);
                setSaveNeeded(true);
            }
        } else {
            keyEvents(evt, txtTax, txtBspCom);
        }
    }//GEN-LAST:event_txtFeesKeyReleased

    private void cmbStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbStatusActionPerformed
        ticket.setTktStatus((Enums.TicketStatus) cmbStatus.getSelectedItem());
    }//GEN-LAST:event_cmbStatusActionPerformed

    private void btnRefundActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefundActionPerformed
        int index = tblTicket.getSelectedRow();
        if (index != -1) {
            Ticket t = TicketLogic.createNewRefund(ticket);
            if (t != null && !ticketExist(t)) {
                this.tickets.add(t);
                populateTblTicket(tickets);
            }
            setSaveNeeded(true);
        }
    }//GEN-LAST:event_btnRefundActionPerformed

    private void btnReIssueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReIssueActionPerformed
        int index = tblTicket.getSelectedRow();
        if (index != -1) {
            Ticket t = TicketLogic.createNewReIssue(ticket);
            if (t != null && !ticketExist(t)) {
                this.tickets.add(t);
                populateTblTicket(tickets);
            }
            setSaveNeeded(true);
        }
    }//GEN-LAST:event_btnReIssueActionPerformed

    private void btnVoidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVoidActionPerformed

        int index = tblTicket.getSelectedRow();
        if (index != -1) {
            if (JOptionPane.showConfirmDialog(null, "Void Ticket !!! Are you sure?", "WARNING",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

                TicketLogic.voidTicket(ticket);
                populateTblTicket(tickets);
                setSaveNeeded(true);
            }
        }
    }//GEN-LAST:event_btnVoidActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        if (JOptionPane.showConfirmDialog(null, "Delete Ticket !!! Are you sure?", "WARNING",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            deleteTicket(ticket);
            setSaveNeeded(true);
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        if (selectedRow < tblTicket.getRowCount() - 1) {
            selectedRow++;
            tblTicket.setRowSelectionInterval(selectedRow, selectedRow);
        }
    }//GEN-LAST:event_btnNextActionPerformed

    private void btnPrevActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrevActionPerformed
        if (selectedRow > 0) {
            selectedRow--;
            tblTicket.setRowSelectionInterval(selectedRow, selectedRow);
        }
    }//GEN-LAST:event_btnPrevActionPerformed

    private void dtIssueDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dtIssueDateActionPerformed
        ticket.setDocIssuedate(dtIssueDate.getDate());
    }//GEN-LAST:event_dtIssueDateActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnNewTicket;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnPrev;
    private javax.swing.JButton btnReIssue;
    private javax.swing.JButton btnRefund;
    private javax.swing.JButton btnView;
    private javax.swing.JButton btnVoid;
    private org.jdesktop.swingx.JXBusyLabel busyLabel;
    private javax.swing.JComboBox cmbStatus;
    private org.jdesktop.swingx.JXDatePicker dtIssueDate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel lblRevenue;
    private org.jdesktop.swingx.JXTable tblTicket;
    private javax.swing.JTextField txtAirlineCode;
    private javax.swing.JTextField txtAtol;
    private javax.swing.JTextField txtBaseFare;
    private javax.swing.JTextField txtBspCom;
    private javax.swing.JTextField txtDisc;
    private javax.swing.JTextField txtFees;
    private javax.swing.JTextField txtGrossFare;
    private javax.swing.JTextArea txtName;
    private javax.swing.JTextField txtNetPurchaseFare;
    private javax.swing.JTextField txtNetSellingFare;
    private javax.swing.JTextField txtTax;
    private javax.swing.JTextField txtTktNo;
    // End of variables declaration//GEN-END:variables

    private void setEditing() {
        if (!editable) {
            txtBaseFare.setEditable(false);
            txtTax.setEditable(false);
            txtFees.setEditable(false);
            txtBspCom.setEditable(false);
            txtGrossFare.setEditable(false);
            txtDisc.setEditable(false);
            txtAtol.setEditable(false);
        } else {
            txtBaseFare.setEditable(true);
            txtTax.setEditable(true);
            txtFees.setEditable(true);
            txtBspCom.setEditable(true);

            txtGrossFare.setEditable(true);
            txtDisc.setEditable(true);
            txtAtol.setEditable(true);
        }
    }

    private void keyEvents(java.awt.event.KeyEvent evt, JTextField previousField, JTextField nextField) {
        int key = evt.getKeyCode();
        if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_DOWN) {
            nextField.requestFocus();
        } else if (key == KeyEvent.VK_UP) {
            previousField.requestFocus();
        }
    }

    private void calculatePurchaseBalance() {
        //populateTblTicket(this.tickets);
        txtNetPurchaseFare.setText(ticket.calculateNetPurchaseFare().toString());
        lblRevenue.setText("Revenue: " + ticket.calculateRevenue());
    }

    private void calculateSellingBalance() {
        //populateTblTicket(this.tickets);
        txtNetSellingFare.setText(ticket.calculateNetSellingFare().toString());
        lblRevenue.setText("Revenue: " + ticket.calculateRevenue());
    }

    private BigDecimal checkValue(JTextField jTextField, String oldValue) {
        String val = jTextField.getText();
        if (val != null && !val.isEmpty()) {
            val = val.trim();
            jTextField.setBackground(Color.WHITE);
        } else {
            jTextField.setText(oldValue);
        }
        return new BigDecimal(val);
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    private boolean ticketExist(Ticket ticket) {
        boolean exist = false;
        for (Ticket t : tickets) {
            if (t.getTicketNo().equals(ticket.getTicketNo())
                    && t.getTktStatus().equals(ticket.getTktStatus())) {
                exist = true;
                break;
            }
        }
        return exist;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress".equals(evt.getPropertyName())) {
            int progress = (Integer) evt.getNewValue();
            if (progress == 100) {
                try {
                    switch (taskType) {
                        case "DELETE":

                            ticket = ticketTask.get();

                            if (ticketTask.getStatus() == 200) {
                                for (Ticket t : tickets) {
                                    if (Objects.equals(t.getId(), ticket.getId())) {
                                        tickets.remove(t);
                                        break;
                                    }
                                }
                                populateTblTicket(tickets);
                            }
                            break;
                        case "UPDATE":
                            break;
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(TicketComponent.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    busyLabel.setBusy(false);
                }
            }
        }
    }

    public boolean isSaveNeeded() {
        return saveNeeded;
    }

    public void setSaveNeeded(boolean saveNeeded) {
        this.saveNeeded = saveNeeded;
        pnrPanel.editingLogic();
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public void setPnrPanel(PnrPanel pnrPanel) {
        this.pnrPanel = pnrPanel;
    }
}
