package com.ets.fe.a_main;

import com.ets.fe.Application;
import com.ets.fe.a_maintask.TodayTInvoiceSearchTask;
import com.ets.fe.a_maintask.PnrSearchTask;
import com.ets.fe.acdoc.model.report.InvoiceReport;
import com.ets.fe.acdoc.model.report.TktingInvoiceSummery;
import com.ets.fe.acdoc_o.model.report.InvoiceReportOther;
import com.ets.fe.acdoc_o.model.report.OtherInvoiceSummery;
import com.ets.fe.pnr.task.DeletePnrTask;
import com.ets.fe.pnr.model.Pnr;
import com.ets.fe.util.PnrUtil;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Yusuf
 */
public class DashBoardFrame extends JInternalFrame {

    private boolean firstResize = true;
    private PnrSearch pnrSearch = new PnrSearch();
    private InvoiceSearch invoiceSearch = new InvoiceSearch();;

    private final List<PnrPanel> pnrTabs = new ArrayList<>();
    private List<TktingInvoiceSummery> tsinvlist_today = new ArrayList<>();
    private List<OtherInvoiceSummery> osinvlist_today = new ArrayList<>();

    private List<Pnr> pnrs = new ArrayList<>();

    public DashBoardFrame() {
        initComponents();
        remove_title_bar();
        lblUser.setText("User: " + Application.getLoggedOnUser().calculateFullName());          
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        splitPaneDashBoard.setDividerLocation(screenSize.width/2);        
    }

    private void showPnrPanel(Pnr pnr, DashBoardFrame parent) {
        PnrPanel panel = getPanel(pnr.getGdsPnr());
        if (panel == null) {
            PnrPanel p = new PnrPanel(pnr.getId(), this);
            mainTabPane.addTab(pnr.getGdsPnr(), p);
            pnrTabs.add(p);
            mainTabPane.setSelectedComponent(p);
        } else {
            mainTabPane.setSelectedComponent(panel);
        }
    }

    private PnrPanel getPanel(String pnr) {
        for (int i = 0; i < pnrTabs.size(); i++) {
            String _title = mainTabPane.getTitleAt(i + 1);//0 is already dashboard
            if (_title.equals(pnr)) {
                return pnrTabs.get(i);
            }
        }
        return null;
    }

    private void viewPnrFromInvoice() {
        int index = tblSTktInvToday.getSelectedRow();
        if (index != -1) {
            Long id = tsinvlist_today.get(index).getPnr_id();
            String gdspnr = tsinvlist_today.get(index).getGdsPnr();
            Pnr pnr = new Pnr();
            pnr.setId(id);
            pnr.setGdsPnr(gdspnr);
            showPnrPanel(pnr, this);
        }
    }

    private void populatePnrTable() {

        JTable table = null;

        if (tabPnr.getSelectedIndex() == 1) {
            table = tblPnrToday;
        } else {
            table = tblUninvoicedPnr;
        }

        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
        tableModel.getDataVector().removeAllElements();

        if (this.pnrs.size() > 0) {
            for (int i = 0; i < this.pnrs.size(); i++) {
                Pnr p = this.pnrs.get(i);
                tableModel.insertRow(i, new Object[]{i + 1, p.getGdsPnr(), PnrUtil.calculatePartialName(PnrUtil.calculateLeadPaxName(p.getTickets())), p.getNoOfPax(),
                    p.getTicketingAgentSine(), p.getBookingAgtOid(), p.getTicketingAgtOid(), p.getAirLineCode()});
            }
        } else {
            tableModel.insertRow(0, new Object[]{});
        }
        table.setRowSelectionInterval(0, 0);
    }

    private void populateTktInvoiceTable() {
        DefaultTableModel tableModel = (DefaultTableModel) tblSTktInvToday.getModel();
        tableModel.getDataVector().removeAllElements();

        if (tsinvlist_today.size() > 0) {
            for (int i = 0; i < tsinvlist_today.size(); i++) {
                TktingInvoiceSummery p = tsinvlist_today.get(i);
                tableModel.insertRow(i, new Object[]{i + 1, p.getType(), p.getReference(), p.getClientName(),
                    p.getAirLine(), p.getDocumentedAmount(), PnrUtil.calculatePartialName(p.getInvBy())});
            }
        } else {
            tableModel.insertRow(0, new Object[]{});
        }
        tblSTktInvToday.setRowSelectionInterval(0, 0);
    }

    private void populateOtherInvoiceTable() {
        DefaultTableModel tableModel = (DefaultTableModel) tblOInvToday.getModel();
        tableModel.getDataVector().removeAllElements();

        if (osinvlist_today.size() > 0) {
            for (int i = 0; i < osinvlist_today.size(); i++) {
                OtherInvoiceSummery p = osinvlist_today.get(i);
                tableModel.insertRow(i, new Object[]{i + 1, p.getType(), p.getReference(), p.getClientName(),
                    p.getDocumentedAmount(), PnrUtil.calculatePartialName(p.getInvBy())});
            }
        } else {
            tableModel.insertRow(0, new Object[]{});
        }
        tblOInvToday.setRowSelectionInterval(0, 0);        
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

        mainTabPane = new javax.swing.JTabbedPane();
        mainTabPane.addChangeListener(tabListener);
        jPanel1 = new javax.swing.JPanel();
        splitPaneDashBoard = new javax.swing.JSplitPane();
        LeftPanel = new javax.swing.JPanel();
        pnlInvoice = new javax.swing.JPanel();
        tabInvoice = new javax.swing.JTabbedPane();
        tabInvoice.addChangeListener(tabInvoiceListener);
        jScrollPane9 = new javax.swing.JScrollPane();
        tblSTktInvToday = new org.jdesktop.swingx.JXTable();
        jScrollPane10 = new javax.swing.JScrollPane();
        tblOInvToday = new org.jdesktop.swingx.JXTable();
        btnRefreshInvoice = new javax.swing.JButton();
        btnPnrDetails1 = new javax.swing.JButton();
        invoiceBusyLabel = new org.jdesktop.swingx.JXBusyLabel();
        pnlTask = new javax.swing.JPanel();
        tabPnr = new javax.swing.JTabbedPane();
        tabPnr.addChangeListener(tabPnrListener);
        jScrollPane1 = new javax.swing.JScrollPane();
        tblUninvoicedPnr = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblPnrToday = new javax.swing.JTable();
        btnRefreshPnr = new javax.swing.JButton();
        btnPnrDetails = new javax.swing.JButton();
        btnDeletePnr = new javax.swing.JButton();
        pnrBusyLabel = new org.jdesktop.swingx.JXBusyLabel();
        jPanel5 = new javax.swing.JPanel();
        pnlPnrSearch = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        txtPnr = new javax.swing.JTextField();
        txtInvRef = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        lblUser = new javax.swing.JLabel();

        setBorder(null);
        setPreferredSize(new java.awt.Dimension(861, 400));

        mainTabPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                System.out.println("Tab=" + mainTabPane.getSelectedIndex()+" "+e);
            }
        });

        splitPaneDashBoard.setDividerLocation(500);
        splitPaneDashBoard.setDividerSize(4);
        splitPaneDashBoard.setToolTipText("");
        splitPaneDashBoard.setPreferredSize(new java.awt.Dimension(1000, 400));

        pnlInvoice.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        pnlInvoice.setPreferredSize(new java.awt.Dimension(366, 250));
        pnlInvoice.setLayout(new java.awt.GridBagLayout());

        tabInvoice.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        jScrollPane9.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        tblSTktInvToday.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "Type", "Ref", "Client", "", "Amount", "Issue By"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblSTktInvToday.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        tblSTktInvToday.setSortable(false);
        tblSTktInvToday.getTableHeader().setReorderingAllowed(false);
        tblSTktInvToday.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblSTktInvTodayMouseClicked(evt);
            }
        });
        jScrollPane9.setViewportView(tblSTktInvToday);
        if (tblSTktInvToday.getColumnModel().getColumnCount() > 0) {
            tblSTktInvToday.getColumnModel().getColumn(0).setMaxWidth(30);
            tblSTktInvToday.getColumnModel().getColumn(1).setMinWidth(50);
            tblSTktInvToday.getColumnModel().getColumn(1).setPreferredWidth(70);
            tblSTktInvToday.getColumnModel().getColumn(1).setMaxWidth(120);
            tblSTktInvToday.getColumnModel().getColumn(2).setMinWidth(55);
            tblSTktInvToday.getColumnModel().getColumn(2).setPreferredWidth(65);
            tblSTktInvToday.getColumnModel().getColumn(2).setMaxWidth(100);
            tblSTktInvToday.getColumnModel().getColumn(4).setMaxWidth(35);
            tblSTktInvToday.getColumnModel().getColumn(5).setMinWidth(50);
            tblSTktInvToday.getColumnModel().getColumn(5).setPreferredWidth(100);
            tblSTktInvToday.getColumnModel().getColumn(5).setMaxWidth(150);
            tblSTktInvToday.getColumnModel().getColumn(6).setMinWidth(65);
            tblSTktInvToday.getColumnModel().getColumn(6).setPreferredWidth(80);
            tblSTktInvToday.getColumnModel().getColumn(6).setMaxWidth(130);
        }

        tabInvoice.addTab("Ticketing Invoice", jScrollPane9);

        jScrollPane10.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        tblOInvToday.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "Type", "Ref", "Client", "Amount", "Issued By"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblOInvToday.setSortable(false);
        tblOInvToday.getTableHeader().setReorderingAllowed(false);
        jScrollPane10.setViewportView(tblOInvToday);
        if (tblOInvToday.getColumnModel().getColumnCount() > 0) {
            tblOInvToday.getColumnModel().getColumn(0).setMaxWidth(30);
        }

        tabInvoice.addTab("Other Invoice", jScrollPane10);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlInvoice.add(tabInvoice, gridBagConstraints);

        btnRefreshInvoice.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/refresh18.png"))); // NOI18N
        btnRefreshInvoice.setBorderPainted(false);
        btnRefreshInvoice.setMaximumSize(new java.awt.Dimension(35, 25));
        btnRefreshInvoice.setMinimumSize(new java.awt.Dimension(35, 25));
        btnRefreshInvoice.setPreferredSize(new java.awt.Dimension(35, 25));
        btnRefreshInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshInvoiceActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlInvoice.add(btnRefreshInvoice, gridBagConstraints);

        btnPnrDetails1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/details18.png"))); // NOI18N
        btnPnrDetails1.setBorderPainted(false);
        btnPnrDetails1.setMaximumSize(new java.awt.Dimension(35, 25));
        btnPnrDetails1.setMinimumSize(new java.awt.Dimension(35, 25));
        btnPnrDetails1.setPreferredSize(new java.awt.Dimension(35, 25));
        btnPnrDetails1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnPnrDetails1MouseClicked(evt);
            }
        });
        btnPnrDetails1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPnrDetails1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlInvoice.add(btnPnrDetails1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        pnlInvoice.add(invoiceBusyLabel, gridBagConstraints);

        pnlTask.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        pnlTask.setLayout(new java.awt.GridBagLayout());

        tabPnr.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        tabPnr.setPreferredSize(new java.awt.Dimension(457, 250));

        jScrollPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        tblUninvoicedPnr.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        tblUninvoicedPnr.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "PNR", "LeadPax", "Qty", "Tkt.AgentSine", "B.OfficeID", "T.OfficeID", ""
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblUninvoicedPnr.getTableHeader().setReorderingAllowed(false);
        tblUninvoicedPnr.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblUninvoicedPnrMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblUninvoicedPnr);
        if (tblUninvoicedPnr.getColumnModel().getColumnCount() > 0) {
            tblUninvoicedPnr.getColumnModel().getColumn(0).setPreferredWidth(25);
            tblUninvoicedPnr.getColumnModel().getColumn(0).setMaxWidth(25);
            tblUninvoicedPnr.getColumnModel().getColumn(2).setMinWidth(120);
            tblUninvoicedPnr.getColumnModel().getColumn(3).setMaxWidth(30);
            tblUninvoicedPnr.getColumnModel().getColumn(7).setPreferredWidth(35);
        }

        tabPnr.addTab("UnInvoicedPNR", jScrollPane1);

        jScrollPane2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        tblPnrToday.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "PNR", "LeadPax", "Qty", "Tkt.AgentSine", "B.OfficeID", "T.OfficeID", ""
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblPnrToday.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tblPnrToday);
        if (tblPnrToday.getColumnModel().getColumnCount() > 0) {
            tblPnrToday.getColumnModel().getColumn(0).setMaxWidth(35);
            tblPnrToday.getColumnModel().getColumn(3).setMaxWidth(30);
            tblPnrToday.getColumnModel().getColumn(7).setMaxWidth(35);
        }

        tabPnr.addTab("PNRToday", jScrollPane2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlTask.add(tabPnr, gridBagConstraints);

        btnRefreshPnr.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/refresh18.png"))); // NOI18N
        btnRefreshPnr.setBorderPainted(false);
        btnRefreshPnr.setMaximumSize(new java.awt.Dimension(35, 25));
        btnRefreshPnr.setMinimumSize(new java.awt.Dimension(35, 25));
        btnRefreshPnr.setPreferredSize(new java.awt.Dimension(35, 25));
        btnRefreshPnr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshPnrActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        pnlTask.add(btnRefreshPnr, gridBagConstraints);

        btnPnrDetails.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/details18.png"))); // NOI18N
        btnPnrDetails.setBorderPainted(false);
        btnPnrDetails.setMaximumSize(new java.awt.Dimension(35, 25));
        btnPnrDetails.setMinimumSize(new java.awt.Dimension(35, 25));
        btnPnrDetails.setPreferredSize(new java.awt.Dimension(35, 25));
        btnPnrDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPnrDetailsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        pnlTask.add(btnPnrDetails, gridBagConstraints);

        btnDeletePnr.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/delete18.png"))); // NOI18N
        btnDeletePnr.setBorderPainted(false);
        btnDeletePnr.setMaximumSize(new java.awt.Dimension(35, 25));
        btnDeletePnr.setMinimumSize(new java.awt.Dimension(35, 25));
        btnDeletePnr.setPreferredSize(new java.awt.Dimension(35, 25));
        btnDeletePnr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeletePnrActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_END;
        gridBagConstraints.insets = new java.awt.Insets(50, 0, 0, 0);
        pnlTask.add(btnDeletePnr, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        pnlTask.add(pnrBusyLabel, gridBagConstraints);

        javax.swing.GroupLayout LeftPanelLayout = new javax.swing.GroupLayout(LeftPanel);
        LeftPanel.setLayout(LeftPanelLayout);
        LeftPanelLayout.setHorizontalGroup(
            LeftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlTask, javax.swing.GroupLayout.DEFAULT_SIZE, 499, Short.MAX_VALUE)
            .addComponent(pnlInvoice, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        LeftPanelLayout.setVerticalGroup(
            LeftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LeftPanelLayout.createSequentialGroup()
                .addComponent(pnlTask, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(pnlInvoice, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE))
        );

        splitPaneDashBoard.setLeftComponent(LeftPanel);

        pnlPnrSearch.setBorder(javax.swing.BorderFactory.createTitledBorder("Search"));
        pnlPnrSearch.setPreferredSize(new java.awt.Dimension(457, 250));

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel6.setText("Gds PNR");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(jLabel6, gridBagConstraints);

        txtPnr.setToolTipText("GDS Pnr");
        txtPnr.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPnrFocusGained(evt);
            }
        });
        txtPnr.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPnrKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtPnr, gridBagConstraints);

        txtInvRef.setToolTipText("Invoice Reference");
        txtInvRef.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtInvRefFocusGained(evt);
            }
        });
        txtInvRef.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtInvRefKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtInvRef, gridBagConstraints);

        jLabel5.setText("Inv Refference");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(jLabel5, gridBagConstraints);

        jLabel4.setText("Pax Name");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(jLabel4, gridBagConstraints);

        txtName.setToolTipText("Example: Surname/Fore Names(s)");
        txtName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtNameFocusGained(evt);
            }
        });
        txtName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNameKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(txtName, gridBagConstraints);

        btnSearch.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/search18.png"))); // NOI18N
        btnSearch.setText("Search");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(btnSearch, gridBagConstraints);

        javax.swing.GroupLayout pnlPnrSearchLayout = new javax.swing.GroupLayout(pnlPnrSearch);
        pnlPnrSearch.setLayout(pnlPnrSearchLayout);
        pnlPnrSearchLayout.setHorizontalGroup(
            pnlPnrSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPnrSearchLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlPnrSearchLayout.setVerticalGroup(
            pnlPnrSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPnrSearchLayout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 10, Short.MAX_VALUE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Unpaid Flight"));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 205, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(pnlPnrSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 82, Short.MAX_VALUE))
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(pnlPnrSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        splitPaneDashBoard.setRightComponent(jPanel5);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitPaneDashBoard, javax.swing.GroupLayout.DEFAULT_SIZE, 877, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitPaneDashBoard, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE)
        );

        mainTabPane.addTab("Dashboard", jPanel1);

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setMaximumSize(new java.awt.Dimension(32767, 24));
        jPanel3.setMinimumSize(new java.awt.Dimension(71, 24));
        jPanel3.setPreferredSize(new java.awt.Dimension(980, 24));
        jPanel3.setLayout(new java.awt.GridBagLayout());

        progressBar.setMaximumSize(new java.awt.Dimension(32767, 18));
        progressBar.setMinimumSize(new java.awt.Dimension(10, 18));
        progressBar.setPreferredSize(new java.awt.Dimension(146, 18));
        progressBar.setStringPainted(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 1, 0, 1);
        jPanel3.add(progressBar, gridBagConstraints);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanel3.add(jSeparator2, gridBagConstraints);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Message:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 2, 1, 2);
        jPanel3.add(jLabel2, gridBagConstraints);

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblUser.setText("User:");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblUser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblUser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(0, 0, 0)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 344, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(mainTabPane))
                .addGap(2, 2, 2))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addComponent(mainTabPane)
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRefreshPnrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshPnrActionPerformed
        pnrSearch.pnrTask();
    }//GEN-LAST:event_btnRefreshPnrActionPerformed

    private void btnPnrDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPnrDetailsActionPerformed
        if (tabPnr.getSelectedIndex() == 0) {
            int index = tblUninvoicedPnr.getSelectedRow();
            if (index != -1) {
                Pnr pnr = this.pnrs.get(index);
                showPnrPanel(pnr, this);
            }
        } else if (tabPnr.getSelectedIndex() == 0) {
            int index = tblPnrToday.getSelectedRow();
            if (index != -1) {
                Pnr pnr = this.pnrs.get(index);
                showPnrPanel(pnr, this);
            }
        }
    }//GEN-LAST:event_btnPnrDetailsActionPerformed

    private void btnDeletePnrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeletePnrActionPerformed
        pnrSearch.deletePnr();
    }//GEN-LAST:event_btnDeletePnrActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        pnrSearch.querySearch();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void txtPnrKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPnrKeyReleased
        int key = evt.getKeyCode();
        if (key == KeyEvent.VK_ENTER) {
            pnrSearch.querySearch();
        }
    }//GEN-LAST:event_txtPnrKeyReleased

    private void txtInvRefKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtInvRefKeyReleased
        int key = evt.getKeyCode();
        if (key == KeyEvent.VK_ENTER) {
            pnrSearch.querySearch();
        }
    }//GEN-LAST:event_txtInvRefKeyReleased

    private void txtNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNameKeyReleased
        int key = evt.getKeyCode();
        if (key == KeyEvent.VK_ENTER) {
            pnrSearch.querySearch();
        }
    }//GEN-LAST:event_txtNameKeyReleased

    private void btnRefreshInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshInvoiceActionPerformed
        invoiceSearch.invoiceTask();
    }//GEN-LAST:event_btnRefreshInvoiceActionPerformed

    private void tblUninvoicedPnrMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblUninvoicedPnrMouseClicked
        if (evt.getClickCount() == 2) {
            int index = tblUninvoicedPnr.getSelectedRow();
            if (index != -1) {
                Pnr pnr = this.pnrs.get(index);
                showPnrPanel(pnr, this);
            }
        }
    }//GEN-LAST:event_tblUninvoicedPnrMouseClicked

    private void btnPnrDetails1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnPnrDetails1MouseClicked

    }//GEN-LAST:event_btnPnrDetails1MouseClicked

    private void btnPnrDetails1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPnrDetails1ActionPerformed
        viewPnrFromInvoice();
    }//GEN-LAST:event_btnPnrDetails1ActionPerformed

    private void tblSTktInvTodayMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSTktInvTodayMouseClicked
        if (evt.getClickCount() == 2) {
            viewPnrFromInvoice();
        }
    }//GEN-LAST:event_tblSTktInvTodayMouseClicked

    private void txtPnrFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPnrFocusGained
        txtPnr.selectAll();
        txtName.setText("");
        txtInvRef.setText("");
    }//GEN-LAST:event_txtPnrFocusGained

    private void txtInvRefFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtInvRefFocusGained
        txtPnr.setText("");
        txtName.setText("");
        txtInvRef.selectAll();
    }//GEN-LAST:event_txtInvRefFocusGained

    private void txtNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNameFocusGained
        txtPnr.setText("");
        txtName.selectAll();
        txtInvRef.setText("");
    }//GEN-LAST:event_txtNameFocusGained


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel LeftPanel;
    private javax.swing.JButton btnDeletePnr;
    private javax.swing.JButton btnPnrDetails;
    private javax.swing.JButton btnPnrDetails1;
    private javax.swing.JButton btnRefreshInvoice;
    private javax.swing.JButton btnRefreshPnr;
    private javax.swing.JButton btnSearch;
    private org.jdesktop.swingx.JXBusyLabel invoiceBusyLabel;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblUser;
    private javax.swing.JTabbedPane mainTabPane;
    private javax.swing.JPanel pnlInvoice;
    private javax.swing.JPanel pnlPnrSearch;
    private javax.swing.JPanel pnlTask;
    private org.jdesktop.swingx.JXBusyLabel pnrBusyLabel;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JSplitPane splitPaneDashBoard;
    private javax.swing.JTabbedPane tabInvoice;
    private javax.swing.JTabbedPane tabPnr;
    private org.jdesktop.swingx.JXTable tblOInvToday;
    private javax.swing.JTable tblPnrToday;
    private org.jdesktop.swingx.JXTable tblSTktInvToday;
    private javax.swing.JTable tblUninvoicedPnr;
    private javax.swing.JTextField txtInvRef;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtPnr;
    // End of variables declaration//GEN-END:variables

    private void remove_title_bar() {
        putClientProperty("JInternalFrame.isPalette", Boolean.TRUE);
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        ((BasicInternalFrameUI) this.getUI()).setNorthPane(null);
        this.setBorder(null);
    }

    public void removeTabRefference(String pnr) {
        PnrPanel panel = getPanel(pnr);
        pnrTabs.remove(panel);
        mainTabPane.remove(panel);
    }

    private ChangeListener tabListener = new ChangeListener() {

        @Override
        public void stateChanged(ChangeEvent e) {
            if (mainTabPane.getSelectedIndex() == 0) {
                pnrSearch.pnrTask();
                invoiceSearch.invoiceTask();
            } else {

            }
        }
    };

    private ChangeListener tabPnrListener = new ChangeListener() {

        @Override
        public void stateChanged(ChangeEvent e) {
            pnrSearch.pnrTask();
        }
    };

    private ChangeListener tabInvoiceListener = new ChangeListener() {

        @Override
        public void stateChanged(ChangeEvent e) {
            invoiceSearch.invoiceTask();
        }
    };

    private class InvoiceSearch implements PropertyChangeListener {

        private TodayTInvoiceSearchTask invoiceSearchTask;
        private String taskType = null;

        public void invoiceTask() {
            if (tabInvoice.getSelectedIndex() == 0) {
                taskType = "TSINVOICE_TODAY";
                invoiceSearchTask = new TodayTInvoiceSearchTask(taskType, invoiceBusyLabel);
            } else if (tabInvoice.getSelectedIndex() == 1) {
                taskType = "OSINVOICE_TODAY";
                invoiceSearchTask = new TodayTInvoiceSearchTask(taskType, invoiceBusyLabel);
            }
            invoiceSearchTask.addPropertyChangeListener(this);
            invoiceSearchTask.execute();
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ("progress".equals(evt.getPropertyName())) {
                int progress = (Integer) evt.getNewValue();
                progressBar.setValue(progress);
                if (progress == 100) {
                    try {
                        switch (taskType) {
                            case "TSINVOICE_TODAY":
                            case "OSINVOICE_TODAY":
                                Object obj = invoiceSearchTask.get();
                                if (obj instanceof InvoiceReport) {
                                    InvoiceReport rpt = (InvoiceReport) invoiceSearchTask.get();
                                    tsinvlist_today = rpt.getInvoices();
                                    populateTktInvoiceTable();
                                } else {
                                    InvoiceReportOther rpt1 = (InvoiceReportOther) invoiceSearchTask.get();
                                    osinvlist_today = rpt1.getInvoices();
                                    populateOtherInvoiceTable();
                                }
                                taskType = "";
                            default:
                        }
                    } catch (InterruptedException | ExecutionException ex) {
                        Logger.getLogger(DashBoardFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    private class PnrSearch implements PropertyChangeListener {

        private PnrSearchTask pnrSearchTask;
        private String taskType = null;

        private void querySearch() {
            String name = txtName.getText();
            String pnr = txtPnr.getText();
            String invRef = txtInvRef.getText();
            taskType = "QUERY_SEARCH";
            pnrSearchTask = new PnrSearchTask(taskType, pnrBusyLabel, pnr, invRef, name);
            pnrSearchTask.addPropertyChangeListener(this);
            pnrSearchTask.execute();
        }

        private void deletePnr() {
            int index = tblUninvoicedPnr.getSelectedRow();
            int choice = JOptionPane.showConfirmDialog(null, "Delete pnr permanently?", "Delete PNR", JOptionPane.YES_NO_OPTION);

            if (index != -1) {
                if (choice == JOptionPane.YES_OPTION) {
                    Pnr pnr = pnrs.get(index);
                    this.taskType = "DELETE_PNR";
                    DeletePnrTask task = new DeletePnrTask(pnr, pnrBusyLabel);
                    task.addPropertyChangeListener(this);
                    task.execute();
                }
            }
        }

        public void pnrTask() {
            if (tabPnr.getSelectedIndex() == 0) {
                taskType = "UNINVOICED_PNR";
                pnrSearchTask = new PnrSearchTask(taskType, pnrBusyLabel);
                pnrSearchTask.addPropertyChangeListener(this);
                pnrSearchTask.execute();
            } else if (tabPnr.getSelectedIndex() == 1) {
                taskType = "PNRTODAY";
                pnrSearchTask = new PnrSearchTask(taskType, pnrBusyLabel);
                pnrSearchTask.addPropertyChangeListener(this);
                pnrSearchTask.execute();
            }
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ("progress".equals(evt.getPropertyName())) {
                int progress = (Integer) evt.getNewValue();
                progressBar.setValue(progress);
                if (progress == 100) {
                    try {
                        switch (taskType) {
                            case "UNINVOICED_PNR":
                            case "QUERY_SEARCH":
                            case "PNRTODAY":
                                pnrs = new ArrayList<>();
                                pnrs = pnrSearchTask.get();
                                populatePnrTable();
                                taskType = "";
                                break;
                            case "DELETE_PNR":
                                pnrTask();
                            default:
                        }
                    } catch (InterruptedException | ExecutionException ex) {
                        Logger.getLogger(DashBoardFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }
}
