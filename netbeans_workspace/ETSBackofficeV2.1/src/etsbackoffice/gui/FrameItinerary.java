package etsbackoffice.gui;

import etsbackoffice.ETSBackofficeApp;
import etsbackoffice.businesslogic.AuthenticationBo;
import etsbackoffice.businesslogic.DateFormat;
import etsbackoffice.businesslogic.Enums;
import etsbackoffice.businesslogic.ItineraryBo;
import etsbackoffice.businesslogic.ReportBo;
import etsbackoffice.domain.Itinerary;
import etsbackoffice.domain.OfficeID;
import etsbackoffice.domain.PNR;
import etsbackoffice.domain.Ticket;
import etsbackoffice.report.BackofficeReporting;
import etsbackoffice.report.SegmentReport;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JProgressBar;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import org.jdesktop.application.Action;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 *
 * @author Yusuf
 */
public class FrameItinerary extends javax.swing.JFrame {

    private ItineraryBo itineraryBo = (ItineraryBo) ETSBackofficeApp.getApplication().ctx.getBean("itineraryBo");
    private List<Object> objects = new ArrayList();
    private List<Itinerary> segments = new ArrayList<Itinerary>();
    private DateFormat df = new DateFormat();
    private Integer status = null;
    private Integer gdsId = null;
    private String oid = null;
    private String airLineID = null;
    private Date from = null;
    private Date to = null;

    private int totalIssuedSegment = 0;
    private int totalReissuedSegment = 0;
    private int totalRefundedSegment = 0;
    private int totalVoidSegment = 0;
    private int totalOpenSegment = 0;
    private int segBalance =0;    
    /** Creates new form FrameItinerary */
    public FrameItinerary(java.awt.Frame parent) {
        initComponents();
        populateCmbOfficeId();
        populateCmbGDS();
    }

    private void populateCmbGDS() {
        DefaultComboBoxModel gdsModel = new DefaultComboBoxModel(Enums.GDS.values());
        cmbGds.setModel(gdsModel);
        cmbGds.insertItemAt("All", 0);
        cmbGds.setSelectedIndex(0);
    }
    
    public void search() {
        if (rdoIssue.isSelected()) {
            status = 2;
        } else if (rdoReIssue.isSelected()) {
            status = 3;
        } else if (rdoRefund.isSelected()) {
            status = 4;
        } else if (rdoVoid.isSelected()) {
            status = 5;
        } else if (rdoAll.isSelected()) {
            status = null;
        }

        if (cmbGds.getSelectedItem().equals("All")) {
            gdsId = null;
        } else {
            gdsId = Enums.GDS.valueOf(cmbGds.getSelectedItem().toString()).getId();
        }

        oid = (String) cmbOfficeId.getSelectedItem();

        if (!txtAirLineID.getText().isEmpty()) {
            airLineID = txtAirLineID.getText();
        } else {
            airLineID = null;
        }

        from = dtFrom.getDate();
        to = dtTo.getDate();

        segments = itineraryBo.findSegments(status, gdsId, oid, airLineID, from, to);
    }

    private void populateCmbOfficeId() {
        List cmbElement = new ArrayList();
        for (OfficeID oid : AuthenticationBo.getmAgent().getOfficeIDs()) {
            cmbElement.add(oid.getOfficeID());
        }

        Collections.sort(cmbElement);
        DefaultComboBoxModel cmbOfficeIdModel = new DefaultComboBoxModel(cmbElement.toArray());
        cmbOfficeId.setModel(cmbOfficeIdModel);
    }

    private void populateTblItinerary() {
        int row = 0;
        totalIssuedSegment = 0;
        totalReissuedSegment = 0;
        totalRefundedSegment = 0;
        totalVoidSegment = 0;
        totalOpenSegment = 0;

        DefaultTableModel itineraryModel;
        itineraryModel = (DefaultTableModel) tblItinerary.getModel();
        itineraryModel.getDataVector().removeAllElements();
        tblItinerary.repaint();


        for (int i = 0; i < segments.size(); i++) {

            //Object[] item = (Object[]) objects.get(i);
            
            Itinerary s = segments.get(i);
            Ticket t = s.getTickets().iterator().next();
            PNR p = s.getPnr();

            int seg = 0;
            if (t.getTktStatus() == 2) {
                seg = 1;
                totalIssuedSegment++;
            } else if (t.getTktStatus() == 3) {
                seg = 0;
                totalReissuedSegment++;
            } else if (t.getTktStatus() == 4) {
                seg = -1;
                totalRefundedSegment++;
            } else if (t.getTktStatus() == 5 || s.getAirLineID().equals("VOID")) {
                seg = 0;
                totalVoidSegment++;
            }

/*            if (s.getAirLineID().equals("VOID")) {
                seg = 0;
                totalVoidSegment = totalVoidSegment + 1;
            }*/
            if (s.getFlightNo() != null) {
                if (s.getFlightNo().equals("OPEN")) {
                    totalOpenSegment++;
                }
            }
            itineraryModel.insertRow(row, new Object[]{df.dateForGui(t.getDocIssuedate()),
                        p.getGdsPNR(), p.getTicketingAgtOID(), p.getBookingAgtOID(),
                        t.getFullTicketNo(), s.getTicketClass(), s.getAirLineID(),s.getFlightNo(), t.getTktStatusString(), s.getDeptDate(),
                        s.getDeptFrom(), s.getDeptTo(), seg});
            row++;
        }
        segBalance = totalIssuedSegment - totalRefundedSegment - totalVoidSegment-totalOpenSegment;
        lblISegment.setText(String.valueOf(totalIssuedSegment));
        lblRSegment.setText(String.valueOf(totalRefundedSegment));
        lblRISegment.setText(String.valueOf(totalReissuedSegment));
        lblVoidSegment.setText(String.valueOf(totalVoidSegment));
        lblBalance.setText(String.valueOf(segBalance));
        lblTOpenSegment.setText(String.valueOf(totalOpenSegment));
    }

    private void dlgSegment(Itinerary segment) {       
        DlgItinerary dlgSegment = new DlgItinerary(this);
        if (dlgSegment.showSegmentDialog(segment)) {
            new Thread(new threadSaveItinerary(segment)).start();
            //setSaveNeeded(true);
        } else {
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jToolBar1 = new javax.swing.JToolBar();
        btnViewReport = new javax.swing.JButton();
        btnEmailReport = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        jSeparator2 = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        pnlSearch = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        dtFrom = new org.jdesktop.swingx.JXDatePicker();
        dtTo = new org.jdesktop.swingx.JXDatePicker();
        cmbGds = new javax.swing.JComboBox();
        btnSearch = new javax.swing.JButton();
        rdoIssue = new javax.swing.JRadioButton();
        rdoReIssue = new javax.swing.JRadioButton();
        rdoRefund = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        cmbOfficeId = new javax.swing.JComboBox();
        AutoCompleteDecorator.decorate(cmbOfficeId);
        rdoVoid = new javax.swing.JRadioButton();
        rdoAll = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();
        txtAirLineID = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblItinerary = new JXTable(){public Component prepareRenderer(TableCellRenderer renderer,
            int rowIndex, int vColIndex) {
            Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
            if (rowIndex % 2 == 0 && !isCellSelected(rowIndex, vColIndex)) {
                c.setBackground(Color.LIGHT_GRAY);
                c.setForeground(Color.BLACK);
            } 
            return c;
        }
    };
    jPanel5 = new javax.swing.JPanel();
    jLabel9 = new javax.swing.JLabel();
    lblISegment = new javax.swing.JLabel();
    lblBalance1 = new javax.swing.JLabel();
    lblBalance = new javax.swing.JLabel();
    jLabel10 = new javax.swing.JLabel();
    lblRSegment = new javax.swing.JLabel();
    jSeparator1 = new javax.swing.JSeparator();
    jLabel12 = new javax.swing.JLabel();
    jLabel13 = new javax.swing.JLabel();
    lblVoidSegment = new javax.swing.JLabel();
    lblRISegment = new javax.swing.JLabel();
    jLabel6 = new javax.swing.JLabel();
    lblTOpenSegment = new javax.swing.JLabel();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getResourceMap(FrameItinerary.class);
    setTitle(resourceMap.getString("Form.title")); // NOI18N
    setName("Form"); // NOI18N

    jToolBar1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    jToolBar1.setRollover(true);
    jToolBar1.setName("jToolBar1"); // NOI18N
    jToolBar1.setPreferredSize(new java.awt.Dimension(100, 30));

    btnViewReport.setFont(resourceMap.getFont("cmbOfficeId.font")); // NOI18N
    btnViewReport.setIcon(resourceMap.getIcon("btnViewReport.icon")); // NOI18N
    btnViewReport.setText(resourceMap.getString("btnViewReport.text")); // NOI18N
    btnViewReport.setFocusable(false);
    btnViewReport.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    btnViewReport.setName("btnViewReport"); // NOI18N
    btnViewReport.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnViewReportActionPerformed(evt);
        }
    });
    jToolBar1.add(btnViewReport);

    btnEmailReport.setFont(resourceMap.getFont("cmbOfficeId.font")); // NOI18N
    btnEmailReport.setIcon(resourceMap.getIcon("btnEmailReport.icon")); // NOI18N
    btnEmailReport.setText(resourceMap.getString("btnEmailReport.text")); // NOI18N
    btnEmailReport.setFocusable(false);
    btnEmailReport.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    btnEmailReport.setName("btnEmailReport"); // NOI18N
    jToolBar1.add(btnEmailReport);

    jButton1.setFont(resourceMap.getFont("jButton1.font")); // NOI18N
    jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
    jButton1.setFocusable(false);
    jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jButton1.setName("jButton1"); // NOI18N
    jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jButton1.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton1ActionPerformed(evt);
        }
    });
    jToolBar1.add(jButton1);

    jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    jPanel4.setName("jPanel4"); // NOI18N
    jPanel4.setLayout(new java.awt.GridBagLayout());

    progressBar.setName("progressBar"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel4.add(progressBar, gridBagConstraints);

    jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
    jSeparator2.setName("jSeparator2"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
    gridBagConstraints.ipadx = 4;
    gridBagConstraints.ipady = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
    gridBagConstraints.weighty = 1.0;
    jPanel4.add(jSeparator2, gridBagConstraints);

    statusMessageLabel.setFont(resourceMap.getFont("statusMessageLabel.font")); // NOI18N
    statusMessageLabel.setText(resourceMap.getString("statusMessageLabel.text")); // NOI18N
    statusMessageLabel.setName("statusMessageLabel"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 0);
    jPanel4.add(statusMessageLabel, gridBagConstraints);

    pnlSearch.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    pnlSearch.setName("pnlSearch"); // NOI18N
    pnlSearch.setLayout(new java.awt.GridBagLayout());

    jLabel5.setFont(resourceMap.getFont("cmbOfficeId.font")); // NOI18N
    jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
    jLabel5.setName("jLabel5"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 11;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
    pnlSearch.add(jLabel5, gridBagConstraints);

    jLabel4.setFont(resourceMap.getFont("cmbOfficeId.font")); // NOI18N
    jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
    jLabel4.setName("jLabel4"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 13;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
    pnlSearch.add(jLabel4, gridBagConstraints);

    jLabel3.setFont(resourceMap.getFont("cmbOfficeId.font")); // NOI18N
    jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
    jLabel3.setName("jLabel3"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
    pnlSearch.add(jLabel3, gridBagConstraints);

    dtFrom.setFont(resourceMap.getFont("cmbOfficeId.font")); // NOI18N
    dtFrom.setName("dtFrom"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 12;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    pnlSearch.add(dtFrom, gridBagConstraints);

    dtTo.setFont(resourceMap.getFont("cmbOfficeId.font")); // NOI18N
    dtTo.setName("dtTo"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 14;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    pnlSearch.add(dtTo, gridBagConstraints);

    cmbGds.setFont(resourceMap.getFont("cmbOfficeId.font")); // NOI18N
    cmbGds.setName("cmbGds"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    pnlSearch.add(cmbGds, gridBagConstraints);

    javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getActionMap(FrameItinerary.class, this);
    btnSearch.setAction(actionMap.get("search")); // NOI18N
    btnSearch.setFont(resourceMap.getFont("cmbOfficeId.font")); // NOI18N
    btnSearch.setIcon(resourceMap.getIcon("btnSearch.icon")); // NOI18N
    btnSearch.setText(resourceMap.getString("btnSearch.text")); // NOI18N
    btnSearch.setName("btnSearch"); // NOI18N
    btnSearch.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnSearchActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 15;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
    pnlSearch.add(btnSearch, gridBagConstraints);

    buttonGroup1.add(rdoIssue);
    rdoIssue.setFont(resourceMap.getFont("cmbOfficeId.font")); // NOI18N
    rdoIssue.setText(resourceMap.getString("rdoIssue.text")); // NOI18N
    rdoIssue.setName("rdoIssue"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    pnlSearch.add(rdoIssue, gridBagConstraints);

    buttonGroup1.add(rdoReIssue);
    rdoReIssue.setFont(resourceMap.getFont("cmbOfficeId.font")); // NOI18N
    rdoReIssue.setText(resourceMap.getString("rdoReIssue.text")); // NOI18N
    rdoReIssue.setName("rdoReIssue"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    pnlSearch.add(rdoReIssue, gridBagConstraints);

    buttonGroup1.add(rdoRefund);
    rdoRefund.setFont(resourceMap.getFont("cmbOfficeId.font")); // NOI18N
    rdoRefund.setText(resourceMap.getString("rdoRefund.text")); // NOI18N
    rdoRefund.setName("rdoRefund"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    pnlSearch.add(rdoRefund, gridBagConstraints);

    jLabel1.setFont(resourceMap.getFont("cmbOfficeId.font")); // NOI18N
    jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
    jLabel1.setName("jLabel1"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 7;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
    pnlSearch.add(jLabel1, gridBagConstraints);

    cmbOfficeId.setFont(resourceMap.getFont("cmbOfficeId.font")); // NOI18N
    cmbOfficeId.setName("cmbOfficeId"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 8;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    pnlSearch.add(cmbOfficeId, gridBagConstraints);

    buttonGroup1.add(rdoVoid);
    rdoVoid.setFont(resourceMap.getFont("rdoVoid.font")); // NOI18N
    rdoVoid.setText(resourceMap.getString("rdoVoid.text")); // NOI18N
    rdoVoid.setName("rdoVoid"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    pnlSearch.add(rdoVoid, gridBagConstraints);

    buttonGroup1.add(rdoAll);
    rdoAll.setFont(resourceMap.getFont("rdoAll.font")); // NOI18N
    rdoAll.setSelected(true);
    rdoAll.setText(resourceMap.getString("rdoAll.text")); // NOI18N
    rdoAll.setName("rdoAll"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    pnlSearch.add(rdoAll, gridBagConstraints);

    jLabel2.setFont(resourceMap.getFont("jLabel2.font")); // NOI18N
    jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
    jLabel2.setName("jLabel2"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 9;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 0);
    pnlSearch.add(jLabel2, gridBagConstraints);

    txtAirLineID.setFont(resourceMap.getFont("jLabel2.font")); // NOI18N
    txtAirLineID.setText(resourceMap.getString("txtAirLineID.text")); // NOI18N
    txtAirLineID.setName("txtAirLineID"); // NOI18N
    txtAirLineID.setPreferredSize(new java.awt.Dimension(60, 20));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 10;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    pnlSearch.add(txtAirLineID, gridBagConstraints);

    jScrollPane1.setName("jScrollPane1"); // NOI18N

    tblItinerary.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {

        },
        new String [] {
            "Date", "PNR", "EffectingOID", "BookingOID", "TktNo", "TktClass", "Career", "FlNo", "Status", "TrvlDate", "OutCity", "InCity", "Seg"
        }
    ) {
        boolean[] canEdit = new boolean [] {
            false, false, false, false, false, false, false, false, false, false, false, false, false
        };

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit [columnIndex];
        }
    });
    tblItinerary.setFont(resourceMap.getFont("tblItinerary.font")); // NOI18N
    tblItinerary.setName("tblItinerary"); // NOI18N
    jScrollPane1.setViewportView(tblItinerary);
    tblItinerary.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblItinerary.columnModel.title0")); // NOI18N
    tblItinerary.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblItinerary.columnModel.title1")); // NOI18N
    tblItinerary.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblItinerary.columnModel.title2")); // NOI18N
    tblItinerary.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblItinerary.columnModel.title3")); // NOI18N
    tblItinerary.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblItinerary.columnModel.title4")); // NOI18N
    tblItinerary.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("tblItinerary.columnModel.title5")); // NOI18N
    tblItinerary.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("tblItinerary.columnModel.title11")); // NOI18N
    tblItinerary.getColumnModel().getColumn(7).setHeaderValue(resourceMap.getString("tblItinerary.columnModel.title12")); // NOI18N
    tblItinerary.getColumnModel().getColumn(8).setHeaderValue(resourceMap.getString("tblItinerary.columnModel.title6")); // NOI18N
    tblItinerary.getColumnModel().getColumn(9).setHeaderValue(resourceMap.getString("tblItinerary.columnModel.title7")); // NOI18N
    tblItinerary.getColumnModel().getColumn(10).setHeaderValue(resourceMap.getString("tblItinerary.columnModel.title8")); // NOI18N
    tblItinerary.getColumnModel().getColumn(11).setHeaderValue(resourceMap.getString("tblItinerary.columnModel.title9")); // NOI18N
    tblItinerary.getColumnModel().getColumn(12).setHeaderValue(resourceMap.getString("tblItinerary.columnModel.title10")); // NOI18N

    jPanel5.setBackground(resourceMap.getColor("jPanel5.background")); // NOI18N
    jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
    jPanel5.setName("jPanel5"); // NOI18N

    jLabel9.setFont(resourceMap.getFont("jLabel12.font")); // NOI18N
    jLabel9.setForeground(resourceMap.getColor("jLabel12.foreground")); // NOI18N
    jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
    jLabel9.setName("jLabel9"); // NOI18N

    lblISegment.setFont(resourceMap.getFont("jLabel12.font")); // NOI18N
    lblISegment.setForeground(resourceMap.getColor("jLabel12.foreground")); // NOI18N
    lblISegment.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    lblISegment.setText(resourceMap.getString("lblISegment.text")); // NOI18N
    lblISegment.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
    lblISegment.setName("lblISegment"); // NOI18N

    lblBalance1.setFont(resourceMap.getFont("jLabel12.font")); // NOI18N
    lblBalance1.setForeground(resourceMap.getColor("jLabel12.foreground")); // NOI18N
    lblBalance1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    lblBalance1.setText(resourceMap.getString("lblBalance1.text")); // NOI18N
    lblBalance1.setName("lblBalance1"); // NOI18N

    lblBalance.setFont(resourceMap.getFont("jLabel12.font")); // NOI18N
    lblBalance.setForeground(resourceMap.getColor("jLabel12.foreground")); // NOI18N
    lblBalance.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    lblBalance.setText(resourceMap.getString("lblBalance.text")); // NOI18N
    lblBalance.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
    lblBalance.setName("lblBalance"); // NOI18N

    jLabel10.setFont(resourceMap.getFont("jLabel12.font")); // NOI18N
    jLabel10.setForeground(resourceMap.getColor("jLabel12.foreground")); // NOI18N
    jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
    jLabel10.setName("jLabel10"); // NOI18N

    lblRSegment.setFont(resourceMap.getFont("jLabel12.font")); // NOI18N
    lblRSegment.setForeground(resourceMap.getColor("jLabel12.foreground")); // NOI18N
    lblRSegment.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    lblRSegment.setText(resourceMap.getString("lblRSegment.text")); // NOI18N
    lblRSegment.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
    lblRSegment.setName("lblRSegment"); // NOI18N

    jSeparator1.setName("jSeparator1"); // NOI18N

    jLabel12.setFont(resourceMap.getFont("jLabel12.font")); // NOI18N
    jLabel12.setForeground(resourceMap.getColor("jLabel12.foreground")); // NOI18N
    jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
    jLabel12.setName("jLabel12"); // NOI18N

    jLabel13.setFont(resourceMap.getFont("jLabel12.font")); // NOI18N
    jLabel13.setForeground(resourceMap.getColor("jLabel12.foreground")); // NOI18N
    jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
    jLabel13.setName("jLabel13"); // NOI18N

    lblVoidSegment.setFont(resourceMap.getFont("jLabel12.font")); // NOI18N
    lblVoidSegment.setForeground(resourceMap.getColor("jLabel12.foreground")); // NOI18N
    lblVoidSegment.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    lblVoidSegment.setText(resourceMap.getString("lblVoidSegment.text")); // NOI18N
    lblVoidSegment.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
    lblVoidSegment.setName("lblVoidSegment"); // NOI18N

    lblRISegment.setFont(resourceMap.getFont("jLabel12.font")); // NOI18N
    lblRISegment.setForeground(resourceMap.getColor("jLabel12.foreground")); // NOI18N
    lblRISegment.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    lblRISegment.setText(resourceMap.getString("lblRISegment.text")); // NOI18N
    lblRISegment.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
    lblRISegment.setName("lblRISegment"); // NOI18N

    jLabel6.setFont(resourceMap.getFont("jLabel12.font")); // NOI18N
    jLabel6.setForeground(resourceMap.getColor("jLabel12.foreground")); // NOI18N
    jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
    jLabel6.setName("jLabel6"); // NOI18N

    lblTOpenSegment.setFont(resourceMap.getFont("jLabel12.font")); // NOI18N
    lblTOpenSegment.setForeground(resourceMap.getColor("jLabel12.foreground")); // NOI18N
    lblTOpenSegment.setText(resourceMap.getString("lblTOpenSegment.text")); // NOI18N
    lblTOpenSegment.setName("lblTOpenSegment"); // NOI18N

    javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
    jPanel5.setLayout(jPanel5Layout);
    jPanel5Layout.setHorizontalGroup(
        jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel5Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 666, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel9)
                        .addComponent(jLabel10))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(lblRSegment, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblISegment, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(lblRISegment, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblVoidSegment, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE))
                            .addGap(34, 34, 34)
                            .addComponent(jLabel6)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(lblTOpenSegment, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE))
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addComponent(lblBalance1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(lblBalance, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(29, 29, 29)))))
            .addContainerGap())
    );
    jPanel5Layout.setVerticalGroup(
        jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel5Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblVoidSegment)
                        .addComponent(jLabel6)
                        .addComponent(lblTOpenSegment))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(lblRISegment))
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel12)
                        .addComponent(jLabel9)
                        .addComponent(lblISegment))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblRSegment)
                        .addComponent(jLabel13)
                        .addComponent(jLabel10))))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(lblBalance1)
                .addComponent(lblBalance))
            .addContainerGap(13, Short.MAX_VALUE))
    );

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 1148, Short.MAX_VALUE)
        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 1148, Short.MAX_VALUE)
        .addGroup(layout.createSequentialGroup()
            .addGap(4, 4, 4)
            .addComponent(pnlSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(2, 2, 2)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 933, Short.MAX_VALUE))
            .addGap(2, 2, 2))
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(2, 2, 2)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE)
                    .addGap(2, 2, 2)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createSequentialGroup()
                    .addComponent(pnlSearch, javax.swing.GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE)
                    .addGap(2, 2, 2)))
            .addGap(2, 2, 2)
            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
    );

    pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        new Thread(new threadSearch()).start();
}//GEN-LAST:event_btnSearchActionPerformed

    private void btnViewReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewReportActionPerformed
        if (!this.segments.isEmpty()) {
            BackofficeReporting rptSegment = new BackofficeReporting();
            SegmentReport sReport = new SegmentReport(this.segments);
            List<Object> invObject = new ArrayList();
            sReport.setmAgent(AuthenticationBo.getmAgent());
            sReport.setFrom(from);
            sReport.setTo(to);
            sReport.setAirLineID(this.airLineID);
            sReport.setGdsId(this.gdsId);
            sReport.setStatus(this.status);
            sReport.setOid(this.oid);
            
            sReport.setTotalIssuedSegment(totalIssuedSegment);
            sReport.setTotalReissuedSegment(totalReissuedSegment);
            sReport.setTotalRefundedSegment(totalRefundedSegment);
            sReport.setTotalVoidSegment(totalVoidSegment);
            sReport.setTotalOpenSegment(totalOpenSegment);
            sReport.setSegmentBalance(segBalance);
            
            invObject.add(sReport);
            rptSegment.viewSegmentReport(invObject);
        }
    }//GEN-LAST:event_btnViewReportActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        int row = tblItinerary.getSelectedRow();
        if (row != -1) {
            dlgSegment(this.segments.get(row));
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    /*public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new FrameItinerary().setVisible(true);
            }
        });
    }*/
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEmailReport;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnViewReport;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox cmbGds;
    private javax.swing.JComboBox cmbOfficeId;
    private org.jdesktop.swingx.JXDatePicker dtFrom;
    private org.jdesktop.swingx.JXDatePicker dtTo;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblBalance;
    private javax.swing.JLabel lblBalance1;
    private javax.swing.JLabel lblISegment;
    private javax.swing.JLabel lblRISegment;
    private javax.swing.JLabel lblRSegment;
    private javax.swing.JLabel lblTOpenSegment;
    private javax.swing.JLabel lblVoidSegment;
    private javax.swing.JPanel pnlSearch;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JRadioButton rdoAll;
    private javax.swing.JRadioButton rdoIssue;
    private javax.swing.JRadioButton rdoReIssue;
    private javax.swing.JRadioButton rdoRefund;
    private javax.swing.JRadioButton rdoVoid;
    private javax.swing.JLabel statusMessageLabel;
    private org.jdesktop.swingx.JXTable tblItinerary;
    private javax.swing.JTextField txtAirLineID;
    // End of variables declaration//GEN-END:variables
  
    //**********************threads********************
    //Thread1 to load complete pnr;
    private long startTime = 0;
    private long stopTime = 0;
    private float elapsedTime = 0;

    /**
     * @param pnrDao the pnrDao to set
     */
    private class threadSearch implements Runnable {

        public threadSearch() {
        }

        public void run() {

            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            //progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();
            statusMessageLabel.setText("Loading...");

            search();
            populateTblItinerary();
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            //progressBar.repaint(); //Refresh graphics
        }
    }
    
    private class threadSaveItinerary implements Runnable {

        private Itinerary s;

        public threadSaveItinerary(Itinerary s) {
            this.s = s;
        }

        public void run() {

            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            //progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();
            statusMessageLabel.setText("Saving ticket...");
            itineraryBo.save(s);
            populateTblItinerary();
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            //progressBar.repaint(); //Refresh graphics
        }
    }
}
