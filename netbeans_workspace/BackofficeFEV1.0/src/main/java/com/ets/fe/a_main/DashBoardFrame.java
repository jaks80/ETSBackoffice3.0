package com.ets.fe.a_main;

import com.ets.fe.Application;
import com.ets.fe.a_maintask.TodayTInvoiceSearchTask;
import com.ets.fe.a_maintask.PnrSearchTask;
import com.ets.fe.acdoc.model.report.InvoiceReport;
import com.ets.fe.acdoc.model.report.TktingInvoiceSummery;
import com.ets.fe.acdoc.task.SalesAcDocReportingTask;
import com.ets.fe.acdoc_o.gui.OtherInvoiceDlg;
import com.ets.fe.acdoc_o.gui.OtherSalesAcDocumentDlg;
import com.ets.fe.acdoc_o.model.report.InvoiceReportOther;
import com.ets.fe.acdoc_o.model.report.OtherInvoiceSummery;
import com.ets.fe.pnr.task.DeletePnrTask;
import com.ets.fe.pnr.model.Pnr;
import com.ets.fe.util.DateUtil;
import com.ets.fe.util.Enums;
import com.ets.fe.util.PnrUtil;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
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
    private InvoiceSearch invoiceSearch = new InvoiceSearch();
    private DueRefund dueRefund = new DueRefund();
    private UnpaidFlightSearch unpaidFlightSearch = new UnpaidFlightSearch();

    private DeletePnrTask deletePnrTask;

    private final List<PnrPanel> pnrTabs = new ArrayList<>();
    private List<TktingInvoiceSummery> tsinvlist_today = new ArrayList<>();
    private List<OtherInvoiceSummery> osinvlist_today = new ArrayList<>();
    private List<TktingInvoiceSummery> unpaid_flight = new ArrayList<>();
    private List<TktingInvoiceSummery> due_refund = new ArrayList<>();

    private List<Pnr> pnrs = new ArrayList<>();

    public DashBoardFrame() {
        initComponents();
        remove_title_bar();
        lblUser.setText("User: " + Application.getLoggedOnUser().calculateFullName());
        initButton();
        unpaidFlightSearch.search();
        dueRefund.search();
    }

    private void initButton() {
        if (Application.getLoggedOnUser().getUserType().getId() < 1) {
            btnDeletePnr.setVisible(false);
        }
    }

    private void showPnrPanel(final Pnr pnr, final DashBoardFrame parent) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                PnrPanel panel = getPanel(pnr.getGdsPnr());
                if (panel == null) {
                    PnrPanel p = new PnrPanel(pnr.getId(), parent);
                    mainTabPane.addTab(pnr.getGdsPnr(), p);
                    pnrTabs.add(p);
                    mainTabPane.setSelectedComponent(p);
                } else {
                    mainTabPane.setSelectedComponent(panel);
                }
            }
        });
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

    private void viewPnr(Long pnrid, String gdspnr) {
        Pnr pnr = new Pnr();
        pnr.setId(pnrid);
        pnr.setGdsPnr(gdspnr);
        showPnrPanel(pnr, this);
    }

    public void viewOtherDocument(OtherInvoiceSummery invSummery) {

        Window w = SwingUtilities.getWindowAncestor(this);
        Frame owner = w instanceof Frame ? (Frame) w : null;

        if (invSummery.getType().equals(Enums.AcDocType.INVOICE)) {

            OtherInvoiceDlg dlg = new OtherInvoiceDlg(owner);
            dlg.showDialog(invSummery.getId());
        } else if (invSummery.getType().equals(Enums.AcDocType.DEBITMEMO)
                || invSummery.getType().equals(Enums.AcDocType.CREDITMEMO)) {

            OtherSalesAcDocumentDlg dlg = new OtherSalesAcDocumentDlg(owner);
            dlg.showDialog(invSummery.getId());
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
                tableModel.insertRow(i, new Object[]{i + 1, p.getType(), p.getReference(),p.getGdsPnr(), p.getClientName(),
                    p.getAirLine(), p.getDocumentedAmount(), PnrUtil.calculatePartialName(p.getInvBy())});
            }
        } else {
            tableModel.insertRow(0, new Object[]{});
        }
        tblSTktInvToday.setRowSelectionInterval(0, 0);
    }

    private void populateDueFlightTable() {
        DefaultTableModel tableModel = (DefaultTableModel) tblUnpaidFlight.getModel();
        tableModel.getDataVector().removeAllElements();

        if (unpaid_flight.size() > 0) {
            for (int i = 0; i < unpaid_flight.size(); i++) {
                TktingInvoiceSummery p = unpaid_flight.get(i);
                tableModel.insertRow(i, new Object[]{i + 1, p.getOutBoundDetails(), p.getReference(), p.getClientName(),
                    p.getDocumentedAmount(), p.getDue()});
            }
        } else {
            tableModel.insertRow(0, new Object[]{});
        }
        tblSTktInvToday.setRowSelectionInterval(0, 0);
    }

    private void populateDueRefundTable() {
        DefaultTableModel tableModel = (DefaultTableModel) tblDueRefund.getModel();
        tableModel.getDataVector().removeAllElements();

        if (due_refund.size() > 0) {
            for (int i = 0; i < due_refund.size(); i++) {
                TktingInvoiceSummery p = due_refund.get(i);
                tableModel.insertRow(i, new Object[]{i + 1, p.getReference(), p.getClientName(), p.getLeadPsgr(),
                    p.getDocumentedAmount(), p.getDue(), PnrUtil.calculatePartialName(p.getInvBy())});
            }
        } else {
            tableModel.insertRow(0, new Object[]{});
        }
        tblDueRefund.setRowSelectionInterval(0, 0);
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
        pnlPnrSearch = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        txtPnr = new javax.swing.JTextField();
        txtInvRef = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        pnlInvoice2 = new javax.swing.JPanel();
        tabUnpaidFlight = new javax.swing.JTabbedPane();
        jScrollPane13 = new javax.swing.JScrollPane();
        tblUnpaidFlight = new org.jdesktop.swingx.JXTable();
        jScrollPane14 = new javax.swing.JScrollPane();
        tblDueRefund = new org.jdesktop.swingx.JXTable();
        btnRefreshInvoice2 = new javax.swing.JButton();
        btnPnrDetails3 = new javax.swing.JButton();
        invoiceBusyLabel2 = new org.jdesktop.swingx.JXBusyLabel();
        jPanel3 = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        lblUser = new javax.swing.JLabel();

        setBorder(null);
        setPreferredSize(new java.awt.Dimension(861, 400));

        mainTabPane.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        mainTabPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                System.out.println("Tab=" + mainTabPane.getSelectedIndex()+" "+e);
            }
        });

        jPanel1.setLayout(new java.awt.GridBagLayout());

        pnlInvoice.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        pnlInvoice.setPreferredSize(new java.awt.Dimension(366, 250));
        pnlInvoice.setLayout(new java.awt.GridBagLayout());

        tabInvoice.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        jScrollPane9.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        tblSTktInvToday.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "Type", "InvNo", "PNR", "Client", "", "Amount", "Issue By"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblSTktInvToday.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        tblSTktInvToday.getTableHeader().setReorderingAllowed(false);
        tblSTktInvToday.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblSTktInvTodayMouseClicked(evt);
            }
        });
        jScrollPane9.setViewportView(tblSTktInvToday);
        if (tblSTktInvToday.getColumnModel().getColumnCount() > 0) {
            tblSTktInvToday.getColumnModel().getColumn(0).setMaxWidth(30);
            tblSTktInvToday.getColumnModel().getColumn(1).setMinWidth(55);
            tblSTktInvToday.getColumnModel().getColumn(1).setPreferredWidth(65);
            tblSTktInvToday.getColumnModel().getColumn(1).setMaxWidth(100);
            tblSTktInvToday.getColumnModel().getColumn(2).setMinWidth(55);
            tblSTktInvToday.getColumnModel().getColumn(2).setPreferredWidth(65);
            tblSTktInvToday.getColumnModel().getColumn(2).setMaxWidth(100);
            tblSTktInvToday.getColumnModel().getColumn(3).setMinWidth(65);
            tblSTktInvToday.getColumnModel().getColumn(3).setPreferredWidth(65);
            tblSTktInvToday.getColumnModel().getColumn(3).setMaxWidth(100);
            tblSTktInvToday.getColumnModel().getColumn(5).setMaxWidth(30);
            tblSTktInvToday.getColumnModel().getColumn(6).setMinWidth(50);
            tblSTktInvToday.getColumnModel().getColumn(6).setPreferredWidth(100);
            tblSTktInvToday.getColumnModel().getColumn(6).setMaxWidth(150);
            tblSTktInvToday.getColumnModel().getColumn(7).setMaxWidth(100);
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
        tblOInvToday.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        tblOInvToday.setSortable(false);
        tblOInvToday.getTableHeader().setReorderingAllowed(false);
        tblOInvToday.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblOInvTodayMouseClicked(evt);
            }
        });
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
        btnRefreshInvoice.setToolTipText("<html>\nRefresh Ticketing Invoice/ Other Invoice<br>\nHighlighted tab will be refreshed.\n</html>");
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
        btnPnrDetails1.setToolTipText("Retrieve Seleted PNR");
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

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(pnlInvoice, gridBagConstraints);

        pnlTask.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        pnlTask.setLayout(new java.awt.GridBagLayout());

        tabPnr.setToolTipText("Refresh InInvoiced PNR / PNR Today");
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
        tblUninvoicedPnr.setToolTipText("<html>\nUnInvoiced PNR table. Click on \"Refresh\" button/  perform search to see PNR in this list<br>\n</html>");
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
            tblUninvoicedPnr.getColumnModel().getColumn(7).setMaxWidth(30);
        }

        tabPnr.addTab("UnInvoicedPNR", jScrollPane1);

        jScrollPane2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        tblPnrToday.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
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
        btnRefreshPnr.setToolTipText("<html>\nRefresh UninvoicedPNR/ PNRToday<br>\nHighlighted tab will be refreshed.\n</html>");
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
        btnPnrDetails.setToolTipText("Retrieve Seleted PNR");
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
        btnDeletePnr.setToolTipText("Delete Selected PNR. Past segment  PNR can not be deleted.");
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

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(pnlTask, gridBagConstraints);

        pnlPnrSearch.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        pnlPnrSearch.setPreferredSize(new java.awt.Dimension(457, 250));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Search PNR", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel6.setText("Gds PNR");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(jLabel6, gridBagConstraints);

        txtPnr.setToolTipText("Search by GDS PNR");
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

        txtInvRef.setToolTipText("Search by Invoice Reference");
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

        jLabel5.setText("Invoice No");
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

        txtName.setToolTipText("<html>\nSearch by Pax Name. Example: Surname/Fore Names(s)<br>\nOr, Surname or Part of Surname.\n</html>");
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
        btnSearch.setToolTipText("Search PNR");
        btnSearch.setMinimumSize(new java.awt.Dimension(120, 30));
        btnSearch.setPreferredSize(new java.awt.Dimension(120, 30));
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
                .addGap(2, 2, 2)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlPnrSearchLayout.setVerticalGroup(
            pnlPnrSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPnrSearchLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(149, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(pnlPnrSearch, gridBagConstraints);

        pnlInvoice2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        pnlInvoice2.setPreferredSize(new java.awt.Dimension(366, 250));
        pnlInvoice2.setLayout(new java.awt.GridBagLayout());

        tabUnpaidFlight.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        jScrollPane13.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        tblUnpaidFlight.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "Flight", "InvNo", "Client", "Amount", "Due"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblUnpaidFlight.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        tblUnpaidFlight.setSortable(false);
        tblUnpaidFlight.getTableHeader().setReorderingAllowed(false);
        tblUnpaidFlight.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblUnpaidFlightMouseClicked(evt);
            }
        });
        jScrollPane13.setViewportView(tblUnpaidFlight);
        if (tblUnpaidFlight.getColumnModel().getColumnCount() > 0) {
            tblUnpaidFlight.getColumnModel().getColumn(0).setMaxWidth(30);
            tblUnpaidFlight.getColumnModel().getColumn(1).setMinWidth(50);
            tblUnpaidFlight.getColumnModel().getColumn(1).setPreferredWidth(120);
            tblUnpaidFlight.getColumnModel().getColumn(1).setMaxWidth(120);
            tblUnpaidFlight.getColumnModel().getColumn(2).setMinWidth(55);
            tblUnpaidFlight.getColumnModel().getColumn(2).setPreferredWidth(65);
            tblUnpaidFlight.getColumnModel().getColumn(2).setMaxWidth(100);
            tblUnpaidFlight.getColumnModel().getColumn(4).setMinWidth(50);
            tblUnpaidFlight.getColumnModel().getColumn(4).setPreferredWidth(100);
            tblUnpaidFlight.getColumnModel().getColumn(4).setMaxWidth(150);
            tblUnpaidFlight.getColumnModel().getColumn(5).setMinWidth(65);
            tblUnpaidFlight.getColumnModel().getColumn(5).setPreferredWidth(80);
            tblUnpaidFlight.getColumnModel().getColumn(5).setMaxWidth(130);
        }

        tabUnpaidFlight.addTab("Unpaid Flight", jScrollPane13);

        jScrollPane14.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        tblDueRefund.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "InvNo", "Client", "LeadPax", "InvAmount", "RFDAmount", "Issued By"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDueRefund.setFont(new java.awt.Font("Courier New", 0, 11)); // NOI18N
        tblDueRefund.setSortable(false);
        tblDueRefund.getTableHeader().setReorderingAllowed(false);
        tblDueRefund.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDueRefundMouseClicked(evt);
            }
        });
        jScrollPane14.setViewportView(tblDueRefund);
        if (tblDueRefund.getColumnModel().getColumnCount() > 0) {
            tblDueRefund.getColumnModel().getColumn(0).setMaxWidth(30);
            tblDueRefund.getColumnModel().getColumn(1).setMinWidth(55);
            tblDueRefund.getColumnModel().getColumn(1).setPreferredWidth(65);
            tblDueRefund.getColumnModel().getColumn(1).setMaxWidth(100);
        }

        tabUnpaidFlight.addTab("Outstanding Refund", jScrollPane14);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlInvoice2.add(tabUnpaidFlight, gridBagConstraints);

        btnRefreshInvoice2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/refresh18.png"))); // NOI18N
        btnRefreshInvoice2.setToolTipText("<html>\nRefresh Ticketing Invoice/ Other Invoice<br>\nHighlighted tab will be refreshed.\n</html>");
        btnRefreshInvoice2.setBorderPainted(false);
        btnRefreshInvoice2.setMaximumSize(new java.awt.Dimension(35, 25));
        btnRefreshInvoice2.setMinimumSize(new java.awt.Dimension(35, 25));
        btnRefreshInvoice2.setPreferredSize(new java.awt.Dimension(35, 25));
        btnRefreshInvoice2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshInvoice2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlInvoice2.add(btnRefreshInvoice2, gridBagConstraints);

        btnPnrDetails3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/details18.png"))); // NOI18N
        btnPnrDetails3.setToolTipText("Retrieve Seleted PNR");
        btnPnrDetails3.setBorderPainted(false);
        btnPnrDetails3.setMaximumSize(new java.awt.Dimension(35, 25));
        btnPnrDetails3.setMinimumSize(new java.awt.Dimension(35, 25));
        btnPnrDetails3.setPreferredSize(new java.awt.Dimension(35, 25));
        btnPnrDetails3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnPnrDetails3MouseClicked(evt);
            }
        });
        btnPnrDetails3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPnrDetails3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlInvoice2.add(btnPnrDetails3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        pnlInvoice2.add(invoiceBusyLabel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(pnlInvoice2, gridBagConstraints);

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

        lblUser.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
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
                    .addComponent(mainTabPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1006, Short.MAX_VALUE))
                .addGap(2, 2, 2))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addComponent(mainTabPane, javax.swing.GroupLayout.DEFAULT_SIZE, 524, Short.MAX_VALUE)
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
        int index = tblSTktInvToday.getSelectedRow();
        if (index != -1) {
            Long id = tsinvlist_today.get(index).getPnr_id();
            String gdspnr = tsinvlist_today.get(index).getGdsPnr();
            viewPnr(id, gdspnr);
        }
    }//GEN-LAST:event_btnPnrDetails1ActionPerformed

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

    private void tblUnpaidFlightMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblUnpaidFlightMouseClicked
        if (evt.getClickCount() == 2) {
            int index = tblUnpaidFlight.getSelectedRow();
            if (index != -1) {
                Long id = unpaid_flight.get(index).getPnr_id();
                String gdspnr = unpaid_flight.get(index).getGdsPnr();
                viewPnr(id, gdspnr);
            }
        }
    }//GEN-LAST:event_tblUnpaidFlightMouseClicked

    private void btnRefreshInvoice2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshInvoice2ActionPerformed
        if (tabUnpaidFlight.getSelectedIndex() == 0) {
            unpaidFlightSearch.search();
        } else if (tabUnpaidFlight.getSelectedIndex() == 1) {
            dueRefund.search();
        }
    }//GEN-LAST:event_btnRefreshInvoice2ActionPerformed

    private void btnPnrDetails3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnPnrDetails3MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btnPnrDetails3MouseClicked

    private void btnPnrDetails3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPnrDetails3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnPnrDetails3ActionPerformed

    private void tblSTktInvTodayMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSTktInvTodayMouseClicked
        if (evt.getClickCount() == 2) {
            int index = tblSTktInvToday.getSelectedRow();
            if (index != -1) {
                Long id = tsinvlist_today.get(index).getPnr_id();
                String gdspnr = tsinvlist_today.get(index).getGdsPnr();
                viewPnr(id, gdspnr);
            }
        }
    }//GEN-LAST:event_tblSTktInvTodayMouseClicked

    private void tblDueRefundMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDueRefundMouseClicked
        if (evt.getClickCount() == 2) {
            int index = tblDueRefund.getSelectedRow();
            if (index != -1) {
                Long id = due_refund.get(index).getPnr_id();
                String gdspnr = due_refund.get(index).getGdsPnr();
                viewPnr(id, gdspnr);
            }
        }
    }//GEN-LAST:event_tblDueRefundMouseClicked

    private void tblOInvTodayMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblOInvTodayMouseClicked
        if (evt.getClickCount() == 2) {
            int index = tblOInvToday.getSelectedRow();
            if (index != -1) {                
                viewOtherDocument(osinvlist_today.get(index));
            }
        }
    }//GEN-LAST:event_tblOInvTodayMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDeletePnr;
    private javax.swing.JButton btnPnrDetails;
    private javax.swing.JButton btnPnrDetails1;
    private javax.swing.JButton btnPnrDetails3;
    private javax.swing.JButton btnRefreshInvoice;
    private javax.swing.JButton btnRefreshInvoice2;
    private javax.swing.JButton btnRefreshPnr;
    private javax.swing.JButton btnSearch;
    private org.jdesktop.swingx.JXBusyLabel invoiceBusyLabel;
    private org.jdesktop.swingx.JXBusyLabel invoiceBusyLabel2;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblUser;
    private javax.swing.JTabbedPane mainTabPane;
    private javax.swing.JPanel pnlInvoice;
    private javax.swing.JPanel pnlInvoice2;
    private javax.swing.JPanel pnlPnrSearch;
    private javax.swing.JPanel pnlTask;
    private org.jdesktop.swingx.JXBusyLabel pnrBusyLabel;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JTabbedPane tabInvoice;
    private javax.swing.JTabbedPane tabPnr;
    private javax.swing.JTabbedPane tabUnpaidFlight;
    private org.jdesktop.swingx.JXTable tblDueRefund;
    private org.jdesktop.swingx.JXTable tblOInvToday;
    private javax.swing.JTable tblPnrToday;
    private org.jdesktop.swingx.JXTable tblSTktInvToday;
    private javax.swing.JTable tblUninvoicedPnr;
    private org.jdesktop.swingx.JXTable tblUnpaidFlight;
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

    public void removeTabRefference(final String pnr) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                PnrPanel panel = getPanel(pnr);
                pnrTabs.remove(panel);
                mainTabPane.remove(panel);
            }
        });
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

    private class UnpaidFlightSearch implements PropertyChangeListener {

        private SalesAcDocReportingTask salesAcDocReportingTask;

        public void search() {
            Date dateFrom = new java.util.Date();
            Date dateTo = DateUtil.addDays(dateFrom, 7);
            invoiceBusyLabel2.setBusy(true);
            salesAcDocReportingTask = new SalesAcDocReportingTask("UNPAID_FLIGHT", null, null, dateFrom, dateTo, null);
            salesAcDocReportingTask.addPropertyChangeListener(this);
            salesAcDocReportingTask.execute();
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ("progress".equals(evt.getPropertyName())) {
                int progress = (Integer) evt.getNewValue();
                progressBar.setValue(progress);
                if (progress == 100) {
                    try {
                        InvoiceReport report = salesAcDocReportingTask.get();
                        unpaid_flight = report.getInvoices();
                        tabUnpaidFlight.setTitleAt(0, "Unpaid Flight: " + String.valueOf(unpaid_flight.size()));
                        populateDueFlightTable();
                        invoiceBusyLabel2.setBusy(false);
                    } catch (InterruptedException | ExecutionException ex) {
                        Logger.getLogger(DashBoardFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    private class DueRefund implements PropertyChangeListener {

        private SalesAcDocReportingTask task;

        public void search() {
            Date dateFrom = DateUtil.minusDays(new java.util.Date(), 90);
            Date dateTo = DateUtil.addDays(new java.util.Date(), 60);
            task = new SalesAcDocReportingTask("OUTSTANDING", Enums.AcDocType.REFUND, null, null, dateFrom, dateTo, null);
            task.addPropertyChangeListener(this);
            task.execute();
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ("progress".equals(evt.getPropertyName())) {
                int progress = (Integer) evt.getNewValue();
                progressBar.setValue(progress);
                if (progress == 100) {
                    try {
                        InvoiceReport report = task.get();
                        due_refund = report.getInvoices();
                        tabUnpaidFlight.setTitleAt(1, "Due Refund: " + String.valueOf(due_refund.size()));
                        populateDueRefundTable();
                        invoiceBusyLabel2.setBusy(false);
                    } catch (InterruptedException | ExecutionException ex) {
                        Logger.getLogger(DashBoardFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

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
                    deletePnrTask = new DeletePnrTask(pnr, pnrBusyLabel);
                    deletePnrTask.addPropertyChangeListener(this);
                    deletePnrTask.execute();
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
                                Integer status = deletePnrTask.get();
                                if (status == 200) {
                                    removeTabRefference(deletePnrTask.getPnr().getGdsPnr());
                                    pnrTask();
                                }
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
