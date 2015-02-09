package com.ets.fe.a_main;

import com.ets.fe.pnr.model.Ticket;
import com.ets.fe.util.CheckInput;
import com.ets.fe.util.Enums;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.font.TextAttribute;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
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
public class TicketComponent extends javax.swing.JPanel {

    private Ticket ticket;
    private boolean editable;
    private List<Ticket> tickets = new ArrayList<>();

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

    public void populateTblTicket(List<Ticket> tickets) {
        this.tickets = tickets;
        tblTicket.clearSelection();
        DefaultTableModel model = (DefaultTableModel) tblTicket.getModel();
        model.getDataVector().removeAllElements();

        int row = 0;
        BigDecimal totalPurchase = new BigDecimal("0.00");
        BigDecimal totalSelling = new BigDecimal("0.00");
        BigDecimal tCom = new BigDecimal("0.00");
        BigDecimal tNetPayable = new BigDecimal("0.00");
        BigDecimal tPL = new BigDecimal("0.00");

        for (Ticket t : this.getTickets()) {
            boolean invoiced = true;
            if (t.getTicketingSalesAcDoc() == null) {
                invoiced = false;
            } else {
                invoiced = true;
            }
            totalPurchase = totalPurchase.add(t.calculateNetPurchaseFare());
            totalSelling = totalSelling.add(t.calculateNetSellingFare());

            model.insertRow(row, new Object[]{t.getFullPaxNameWithPaxNo(),
                t.getTktStatus(), t.getBaseFare(), t.getTax(), t.getCommission(), t.calculateNetPurchaseFare(),
                t.getGrossFare(), t.getDiscount(), t.calculateNetSellingFare(),
                t.calculateRevenue(), invoiced});

            row++;
        }

        model.addRow(new Object[]{"Totals", "", "", "", "", "", totalPurchase, "", "", "", totalSelling});
       
        if(!this.tickets.isEmpty()){
         tblTicket.setRowSelectionInterval(0, 0);//Select first row
        }        
    }
    
    public void displayTicket(Ticket ticket) {
        this.ticket = ticket;
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

        lblRevenue.setText("Revenue: "+ticket.calculateRevenue().toString());

        setEditing();
    }

    private ListSelectionListener tblTicketListener = new ListSelectionListener() {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int selectedRow = tblTicket.getSelectedRow();
            if (selectedRow != -1 && selectedRow != tblTicket.getRowCount() - 1) {
                ticket = getTickets().get(selectedRow);
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

        setPreferredSize(new java.awt.Dimension(900, 270));

        jScrollPane2.setPreferredSize(new java.awt.Dimension(785, 360));

        tblTicket.setBackground(new java.awt.Color(0, 0, 0));
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
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel1.add(jLabel2, gridBagConstraints);

        jLabel3.setText("TktNo");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel1.add(jLabel3, gridBagConstraints);

        jLabel4.setText("Status");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel1.add(jLabel4, gridBagConstraints);

        jLabel5.setText("B.Fare");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel1.add(jLabel5, gridBagConstraints);

        jLabel6.setText("Tax");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel1.add(jLabel6, gridBagConstraints);

        jLabel7.setText("Com");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel1.add(jLabel7, gridBagConstraints);

        jLabel8.setText("NetFare");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel1.add(jLabel8, gridBagConstraints);

        jLabel9.setText("GrsFare");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel1.add(jLabel9, gridBagConstraints);

        jLabel10.setText("Disc");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel1.add(jLabel10, gridBagConstraints);

        jLabel11.setText("ATOL");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel1.add(jLabel11, gridBagConstraints);

        jLabel12.setText("NetSelling");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel1.add(jLabel12, gridBagConstraints);

        lblRevenue.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblRevenue.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblRevenue.setText("Revenue: 0.00");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 4;
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
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(txtAirlineCode, gridBagConstraints);

        txtBaseFare.setBackground(new java.awt.Color(255, 255, 153));
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
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(txtBaseFare, gridBagConstraints);

        txtTax.setBackground(new java.awt.Color(255, 255, 153));
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
        txtTax.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTaxActionPerformed(evt);
            }
        });
        txtTax.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTaxKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(txtTax, gridBagConstraints);

        txtBspCom.setBackground(new java.awt.Color(255, 255, 153));
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
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(txtBspCom, gridBagConstraints);

        txtNetPurchaseFare.setEditable(false);
        txtNetPurchaseFare.setBackground(new java.awt.Color(0, 0, 0));
        txtNetPurchaseFare.setForeground(new java.awt.Color(255, 0, 0));
        txtNetPurchaseFare.setMinimumSize(new java.awt.Dimension(70, 20));
        txtNetPurchaseFare.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(txtNetPurchaseFare, gridBagConstraints);

        txtGrossFare.setBackground(new java.awt.Color(255, 255, 153));
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
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(txtGrossFare, gridBagConstraints);

        txtDisc.setBackground(new java.awt.Color(255, 255, 153));
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
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(txtDisc, gridBagConstraints);

        txtAtol.setBackground(new java.awt.Color(255, 255, 153));
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
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(txtAtol, gridBagConstraints);

        txtNetSellingFare.setEditable(false);
        txtNetSellingFare.setBackground(new java.awt.Color(0, 0, 0));
        txtNetSellingFare.setForeground(new java.awt.Color(255, 0, 0));
        txtNetSellingFare.setMinimumSize(new java.awt.Dimension(70, 20));
        txtNetSellingFare.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(txtNetSellingFare, gridBagConstraints);

        cmbStatus.setToolTipText("");
        cmbStatus.setMaximumSize(new java.awt.Dimension(32767, 19));
        cmbStatus.setMinimumSize(new java.awt.Dimension(28, 19));
        cmbStatus.setPreferredSize(new java.awt.Dimension(28, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
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
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(jScrollPane1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        jPanel1.add(jSeparator2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
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
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(txtTktNo, gridBagConstraints);

        dtIssueDate.setToolTipText("Date: Booking / Issue / Refund");
        dtIssueDate.setMaximumSize(new java.awt.Dimension(108, 20));
        dtIssueDate.setMinimumSize(new java.awt.Dimension(108, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(dtIssueDate, gridBagConstraints);

        jLabel13.setText("Fees");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel1.add(jLabel13, gridBagConstraints);

        txtFees.setBackground(new java.awt.Color(255, 255, 153));
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
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(txtFees, gridBagConstraints);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 657, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtTaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTaxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTaxActionPerformed

    private void txtAirlineCodeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAirlineCodeFocusLost
        this.ticket.setNumericAirLineCode(txtAirlineCode.getText());
    }//GEN-LAST:event_txtAirlineCodeFocusLost

    private void txtTktNoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTktNoFocusLost

    }//GEN-LAST:event_txtTktNoFocusLost

    private void txtBaseFareKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBaseFareKeyReleased
        keyEvents(evt, txtAtol, txtTax);
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
        keyEvents(evt, txtBaseFare, txtFees);
    }//GEN-LAST:event_txtTaxKeyReleased

    private void txtBspComKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBspComKeyReleased
        keyEvents(evt, txtFees, txtGrossFare);
    }//GEN-LAST:event_txtBspComKeyReleased

    private void txtGrossFareKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtGrossFareKeyReleased
        keyEvents(evt, txtBspCom, txtDisc);
    }//GEN-LAST:event_txtGrossFareKeyReleased

    private void txtDiscKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDiscKeyReleased
        keyEvents(evt, txtGrossFare, txtAtol);
    }//GEN-LAST:event_txtDiscKeyReleased

    private void txtAtolKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAtolKeyReleased
        keyEvents(evt, txtDisc, txtBaseFare);
    }//GEN-LAST:event_txtAtolKeyReleased

    private void txtBaseFareFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBaseFareFocusLost
        BigDecimal val = checkValue(txtBaseFare, ticket.getBaseFare().toString());
        if (ticket.getTktStatus() == Enums.TicketStatus.REFUND) {
            val = val.negate();
        }
        this.ticket.setBaseFare(val);
        calculatePurchaseBalance();
    }//GEN-LAST:event_txtBaseFareFocusLost

    private void txtTaxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTaxFocusLost
        BigDecimal val = checkValue(txtTax, ticket.getBaseFare().toString());
        if (ticket.getTktStatus() == Enums.TicketStatus.REFUND) {
            val = val.negate();
        }
        this.ticket.setTax(val);
        calculatePurchaseBalance();
    }//GEN-LAST:event_txtTaxFocusLost

    private void txtBspComFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBspComFocusLost
        BigDecimal val = checkValue(txtBspCom, ticket.getBaseFare().toString());
        if (ticket.getTktStatus() != Enums.TicketStatus.REFUND) {
            val = val.negate();
        }
        this.ticket.setCommission(val);
        calculatePurchaseBalance();
    }//GEN-LAST:event_txtBspComFocusLost

    private void txtGrossFareFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtGrossFareFocusLost
        BigDecimal val = checkValue(txtGrossFare, ticket.getBaseFare().toString());
        if (ticket.getTktStatus() == Enums.TicketStatus.REFUND) {
            val = val.negate();
        }
        this.ticket.setGrossFare(val);
        calculateSellingBalance();
    }//GEN-LAST:event_txtGrossFareFocusLost

    private void txtDiscFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDiscFocusLost
        BigDecimal val = checkValue(txtDisc, ticket.getBaseFare().toString());
        if (ticket.getTktStatus() != Enums.TicketStatus.REFUND) {
            val = val.negate();
        }
        this.ticket.setDiscount(val);
        calculateSellingBalance();
    }//GEN-LAST:event_txtDiscFocusLost

    private void txtAtolFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAtolFocusLost
        BigDecimal val = checkValue(txtAtol, ticket.getBaseFare().toString());
        if (ticket.getTktStatus() == Enums.TicketStatus.REFUND) {
            val = val.negate();
        }
        this.ticket.setAtolChg(val);
        calculateSellingBalance();
    }//GEN-LAST:event_txtAtolFocusLost

    private void txtFeesFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFeesFocusGained
        txtFees.selectAll();
    }//GEN-LAST:event_txtFeesFocusGained

    private void txtFeesFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFeesFocusLost
        BigDecimal val = checkValue(txtFees, ticket.getFee().toString());
        if (ticket.getTktStatus() == Enums.TicketStatus.REFUND) {
            val = val.negate();
        }
        this.ticket.setFee(val);
        calculateSellingBalance();
    }//GEN-LAST:event_txtFeesFocusLost

    private void txtFeesKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFeesKeyReleased
         keyEvents(evt, txtTax, txtBspCom);
    }//GEN-LAST:event_txtFeesKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
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
        txtNetPurchaseFare.setText(ticket.calculateNetPurchaseFare().toString());
        lblRevenue.setText("Revenue: " + ticket.calculateRevenue());
    }

    private void calculateSellingBalance() {
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
        return new BigDecimal(val).setScale(2);
    }

    public List<Ticket> getTickets() {
        return tickets;
    }
}
