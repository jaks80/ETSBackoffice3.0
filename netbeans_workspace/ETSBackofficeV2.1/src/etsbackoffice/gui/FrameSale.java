package etsbackoffice.gui;

import etsbackoffice.ETSBackofficeApp;
import etsbackoffice.businesslogic.AgentBo;
import etsbackoffice.businesslogic.AuthenticationBo;
import etsbackoffice.businesslogic.Enums;
import etsbackoffice.businesslogic.TicketBo;
import etsbackoffice.domain.Agent;
import etsbackoffice.domain.Ticket;
import etsbackoffice.report.BackofficeReporting;
import etsbackoffice.report.TicketingReport;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JProgressBar;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import org.jdesktop.application.Action;
import org.jdesktop.swingx.JXTable;

/**
 *
 * @author Yusuf
 */
public class FrameSale extends javax.swing.JFrame{

    DefaultTableModel salesModel = new DefaultTableModel();
    private TicketBo ticketBo = (TicketBo) ETSBackofficeApp.getApplication().ctx.getBean("ticketBo");
    private AgentBo agentBo = (AgentBo) ETSBackofficeApp.getApplication().ctx.getBean("agentBo");
    List<Ticket> soldTickets = new ArrayList<Ticket>();
    List<Agent> vendors = new ArrayList();
    Object[][] sSummery;
    
    private int ticketingType;
    private Integer gdsId;
    private Long vendorId = null;
    private Integer tktStatus = null;
    private String career = null;
    Date from, to;
    /** Creates new form FrameSale */
    public FrameSale(java.awt.Frame parent) {
        initComponents();       
        populateCmbTktStatus();
        populateCmbGDS();
    }

    private void populateCmbGDS() {
        DefaultComboBoxModel gdsModel = new DefaultComboBoxModel(Enums.GDS.values());
        cmbGds.setModel(gdsModel);
        cmbGds.insertItemAt("All", 0);
        cmbGds.setSelectedIndex(0);
    }
      
    private void populateCmbTktStatus() {
        DefaultComboBoxModel tktStatusModel = new DefaultComboBoxModel(Enums.TicketStatus.values());
        
        cmbTktStatus.setModel(tktStatusModel);
        cmbTktStatus.insertItemAt("All", 0);
        cmbTktStatus.setSelectedIndex(0);           
    }
        
    @Action
    public void search() {
        new Thread(new threadSales()).start();
    }

    private void searchSales() {

        if (cmbGds.getSelectedItem().equals("All")) {
            gdsId = null;
        } else {
            //gdsId = Integer.valueOf(cmbGds.getSelectedIndex());
            gdsId = Enums.GDS.valueOf(cmbGds.getSelectedItem().toString()).getId();
        }

        if (cmbTktStatus.getSelectedIndex() == 0) {
            this.tktStatus = null;
        } else {
            this.tktStatus = Enums.TicketStatus.valueOf(cmbTktStatus.getSelectedItem().toString()).getId();
        }
        if (txtCareerCode.getText().trim().isEmpty()) {
            this.career = null;
        } else {
            this.career = txtCareerCode.getText().trim();
        }

        if (rdoSelf.isSelected()) {
            ticketingType = 1;
            vendorId = Long.valueOf(1);
        } else if (rdoThirdParty.isSelected()) {
            ticketingType = 2;
            if (cmbVendors.getSelectedIndex() > 0) {
                String[] data = cmbVendors.getSelectedItem().toString().split("-");
                vendorId = Long.valueOf(data[2]);
            }else{
            vendorId = null;
            }
        } else if(rdoAllType.isSelected()){
            ticketingType = 0;
        }

        from = dtFrom.getDate();
        to = dtTo.getDate();
        soldTickets = ticketBo.findSoldTickets(ticketingType, gdsId, vendorId, tktStatus, career, from, to);
        populateTblSales();
    }

    private void populateCmbVendors() {
        List cmbElement = new ArrayList();

        vendors = agentBo.vendorList();
        for (Agent a : vendors) {
            cmbElement.add(a.getName() + "-" + "" + "-" + a.getContactableId());
        }

        Collections.sort(cmbElement);
        DefaultComboBoxModel cmbVendorsModel = new DefaultComboBoxModel(cmbElement.toArray());
        cmbVendorsModel.insertElementAt("All", 0);
        cmbVendors.setModel(cmbVendorsModel);
        cmbVendors.setSelectedIndex(0);
    }

    private void populateTblSales() {
        int row = 0;

        salesModel = (DefaultTableModel) tblTicket.getModel();
        salesModel.getDataVector().removeAllElements();
        tblTicket.repaint();
        BigDecimal totalIssuedFare = new BigDecimal("0.00");
        BigDecimal totalRefundFare = new BigDecimal("0.00");
        BigDecimal totalTax = new BigDecimal("0.00");
        BigDecimal totalTaxRefund = new BigDecimal("0.00");
        BigDecimal totalBSPCom = new BigDecimal("0.00");
        BigDecimal totalBSPComRefund = new BigDecimal("0.00");    
        //BigDecimal totalFess = new BigDecimal("0.00");
        BigDecimal saleBalance = new BigDecimal("0.00");
        BigDecimal refundBalance = new BigDecimal("0.00");
        
        BigDecimal comBalance = new BigDecimal("0.00");
        int totalIssue = 0;
        int totalRefund = 0;

        for (Ticket t : this.soldTickets) {
            BigDecimal fee = new BigDecimal("0.00");
            
            if (t.getTktStatus() == 2 || t.getTktStatus() == 3) {
                totalIssuedFare = totalIssuedFare.add(t.getBaseFare());
                totalTax = totalTax.add(t.getTax());
                totalBSPCom = totalBSPCom.add(t.getBspCom());
                ++totalIssue;
            } else if (t.getTktStatus() == 4) {
                totalRefundFare = totalRefundFare.add(t.getBaseFare()).add(t.getTicketRefundDetails().getCancellationFee());
                totalTaxRefund = totalTaxRefund.add(t.getTax());
                totalBSPComRefund = totalBSPComRefund.add(t.getBspCom());
                fee = t.getTicketRefundDetails().getCancellationFee();
                //totalFess = totalFess.add(fee);
                ++totalRefund;
            }

            salesModel.insertRow(row, new Object[]{row + 1, t.getDocIssuedate(), t.getTktStatusString(),
                        t.getPnr().getGdsPNR(), t.getPnr().getBookingAgtOID(), t.getPnr().getTicketingAgtOID(),
                        t.getPnr().getServicingCareer().getCode(), t.getFullPaxNameWithPaxNo(),
                        t.getNumericAirLineCode() + "-" + t.getTicketNo(), t.getBaseFare(), t.getTax(),
                        t.getTotalFare(), t.getBspCom(),fee, t.getNetFare()});
            row++;
        }
        
        saleBalance = totalIssuedFare.add(totalTax).add(totalBSPCom);
        refundBalance = totalRefundFare.add(totalTaxRefund).add(totalBSPComRefund);
        comBalance = totalBSPCom.add(totalBSPComRefund);
        
        salesModel.insertRow(salesModel.getRowCount(), new Object[]{"","","","","","","","","Totals",totalIssuedFare.add(totalRefundFare)
                ,totalTax.add(totalTaxRefund)});
       sSummery = new Object[6][4];//[row][col]
       
       sSummery[0][0] = "Fare";
       sSummery[1][0] = "Tax";
       sSummery[2][0] = "Doc Amount";
       sSummery[3][0] = "Com";
       //sSummery[4][0] = "Fee";
       sSummery[4][0] = "Remit";              
       
       sSummery[0][1] = totalIssuedFare;
       sSummery[0][2] = totalRefundFare;
       sSummery[0][3] = totalIssuedFare.add(totalRefundFare);
       
       sSummery[1][1] = totalTax;
       sSummery[1][2] = totalTaxRefund;
       sSummery[1][3] = totalTax.add(totalTaxRefund);
       
       sSummery[2][1] = totalIssuedFare.add(totalTax);
       sSummery[2][2] = totalRefundFare.add(totalTaxRefund);
       sSummery[2][3] = null;
       
       sSummery[3][1] = totalBSPCom;
       sSummery[3][2] = totalBSPComRefund;
       sSummery[3][3] = comBalance;
       
       //sSummery[4][1] = 0.00;
       //sSummery[4][2] = 0.00;
       //sSummery[4][3] = 0.00;
       
       sSummery[4][1] = saleBalance;
       sSummery[4][2] = refundBalance;
       sSummery[4][3] = saleBalance.add(refundBalance);
       
       populateTblSSummery(sSummery);      
    }
    
    private void populateTblSSummery(Object[][] values){
        String html = "<html>"+
"<table width=\"100%\" height=\"120\" border=\"0\" align=\"left\" cellpadding=\"0\" cellspacing=\"1\">"+
  "<tr>"+
    "<td width=\"126\" align=\"right\" valign=\"middle\">&nbsp;</td>"+
    "<td width=\"64\" align=\"right\" valign=\"middle\">Sales</span></td>"+
    "<td width=\"67\" align=\"right\" valign=\"middle\">Refund</span></td>"+
    "<td width=\"82\" align=\"right\" valign=\"middle\">Balance</span></td>"+
  "</tr>"+
  "<tr>"+
    "<td align=\"right\" valign=\"middle\">Fare</span></td>"+
    "<td align=\"right\" valign=\"middle\">"+sSummery[0][1]+"</td>"+
    "<td align=\"right\" valign=\"middle\">"+sSummery[0][2]+"</td>"+
    "<td align=\"right\" valign=\"middle\">"+sSummery[0][3]+"</td>"+
  "</tr>"+
  "<tr>"+
    "<td align=\"right\" valign=\"middle\">Tax</span></td>"+
    "<td align=\"right\" valign=\"middle\">"+sSummery[1][1]+"</td>"+
    "<td align=\"right\" valign=\"middle\">"+sSummery[1][2]+"</td>"+
    "<td align=\"right\" valign=\"middle\">"+sSummery[1][3]+"</td>"+
  "</tr>"+
  "<tr>"+
    "<td align=\"right\" valign=\"middle\">Doc Amount</span></td>"+
    "<td align=\"right\" valign=\"middle\">"+sSummery[2][1]+"</td>"+
    "<td align=\"right\" valign=\"middle\">"+sSummery[2][2]+"</td>"+
    "<td align=\"right\" valign=\"middle\">"+sSummery[2][3]+"</td>"+
  "</tr>"+
  "<tr>"+
    "<td align=\"right\" valign=\"middle\">Com</span></td>"+
    "<td align=\"right\" valign=\"middle\">"+sSummery[3][1]+"</td>"+
    "<td align=\"right\" valign=\"middle\">"+sSummery[3][2]+"</td>"+
    "<td align=\"right\" valign=\"middle\">"+sSummery[3][3]+"</td>"+
  "</tr>"+
 /*    "<tr>"+
    "<td align=\"right\" valign=\"middle\">Fee</span></td>"+
    "<td align=\"right\" valign=\"middle\">"+sSummery[4][1]+"</td>"+
    "<td align=\"right\" valign=\"middle\">"+sSummery[4][2]+"</td>"+
    "<td align=\"right\" valign=\"middle\">"+sSummery[4][3]+"</td>"+
  "</tr>"+   */          
  "<tr>"+
    "<td align=\"right\" valign=\"middle\">Remit</span></td>"+
    "<td align=\"right\" valign=\"middle\">"+sSummery[4][1]+"</td>"+
    "<td align=\"right\" valign=\"middle\">"+sSummery[4][2]+"</td>"+
    "<td align=\"right\" valign=\"middle\">"+sSummery[4][3]+"</td>"+
  "</tr>"+
"</table>"+
"</html>";
        
        
        HTMLEditorKit kit = new HTMLEditorKit();        
        StyleSheet s = kit.getStyleSheet();
        
        s.addRule("body {font-family: Tahoma; font-size: 10 px;font-weight: bold;color : white;}");

        txtSaleSummery.setEditorKit(kit);
        txtSaleSummery.setText(html);
    }
    
    private ActionListener rdoSelfListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            cmbVendors.setSelectedIndex(0);
            cmbVendors.setEnabled(false);
            cmbGds.setEnabled(true);
        }
    };
    private ActionListener rdoThirdPartyListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            cmbGds.setSelectedIndex(0);
            cmbGds.setEnabled(false);
            cmbVendors.setEnabled(true);
            populateCmbVendors();
        }
    };
    private ActionListener rdoAllListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            cmbGds.setSelectedIndex(0);
            cmbGds.setEnabled(false);
            cmbVendors.setSelectedIndex(0);
            cmbVendors.setEnabled(false);
        }
    };

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
        toolBar = new javax.swing.JToolBar();
        btnViewReport = new javax.swing.JButton();
        btnEmailReport = new javax.swing.JButton();
        btnTicket = new javax.swing.JButton();
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
        rdoSelf = new javax.swing.JRadioButton();
        rdoSelf.addActionListener(rdoSelfListener);
        rdoThirdParty = new javax.swing.JRadioButton();
        rdoThirdParty.addActionListener(rdoThirdPartyListener);
        rdoAllType = new javax.swing.JRadioButton();
        rdoAllType.addActionListener(rdoAllListener);
        jLabel1 = new javax.swing.JLabel();
        cmbVendors = new javax.swing.JComboBox();
        jXBusyLabel1 = new org.jdesktop.swingx.JXBusyLabel();
        jLabel2 = new javax.swing.JLabel();
        cmbTktStatus = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        txtCareerCode = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtSaleSummery = new javax.swing.JEditorPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTicket = new JXTable(){public Component prepareRenderer(TableCellRenderer renderer,
            int rowIndex, int vColIndex) {
            Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);  
            String s = "";
            Object o = tblTicket.getModel().getValueAt(rowIndex, 2);       
            if(o!=null){
                s = o.toString();
            }
            if (s.equalsIgnoreCase("BOOK") ) {                
                c.setForeground(Color.yellow);
            } else if(s.equalsIgnoreCase("ISSUE")){
                c.setForeground(Color.green);
            }else if(s.equalsIgnoreCase("REISSUE")){
                c.setForeground(Color.cyan);
            }else if(s.equalsIgnoreCase("REFUND")){
                c.setForeground(Color.red);
            }else if(s.equalsIgnoreCase("VOID")){
                c.setForeground(Color.ORANGE);
            }else{
                c.setForeground(Color.WHITE);
            }
            return c;
        }
    };

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getResourceMap(FrameSale.class);
    setTitle(resourceMap.getString("Form.title")); // NOI18N
    setName("Form"); // NOI18N
    getContentPane().setLayout(new java.awt.GridBagLayout());

    toolBar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    toolBar.setRollover(true);
    toolBar.setName("toolBar"); // NOI18N

    btnViewReport.setFont(resourceMap.getFont("btnViewReport.font")); // NOI18N
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
    toolBar.add(btnViewReport);

    btnEmailReport.setFont(resourceMap.getFont("btnEmailReport.font")); // NOI18N
    btnEmailReport.setIcon(resourceMap.getIcon("btnEmailReport.icon")); // NOI18N
    btnEmailReport.setText(resourceMap.getString("btnEmailReport.text")); // NOI18N
    btnEmailReport.setFocusable(false);
    btnEmailReport.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    btnEmailReport.setName("btnEmailReport"); // NOI18N
    toolBar.add(btnEmailReport);

    btnTicket.setFont(resourceMap.getFont("btnTicket.font")); // NOI18N
    btnTicket.setIcon(resourceMap.getIcon("btnTicket.icon")); // NOI18N
    btnTicket.setText(resourceMap.getString("btnTicket.text")); // NOI18N
    btnTicket.setFocusable(false);
    btnTicket.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    btnTicket.setName("btnTicket"); // NOI18N
    btnTicket.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnTicketActionPerformed(evt);
        }
    });
    toolBar.add(btnTicket);

    jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
    jButton1.setEnabled(false);
    jButton1.setFocusable(false);
    jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jButton1.setName("jButton1"); // NOI18N
    jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jButton1.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton1ActionPerformed(evt);
        }
    });
    toolBar.add(jButton1);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
    getContentPane().add(toolBar, gridBagConstraints);

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

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
    getContentPane().add(jPanel4, gridBagConstraints);

    pnlSearch.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    pnlSearch.setName("pnlSearch"); // NOI18N
    pnlSearch.setLayout(new java.awt.GridBagLayout());

    jLabel5.setFont(resourceMap.getFont("dtTo.font")); // NOI18N
    jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
    jLabel5.setName("jLabel5"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(6, 4, 2, 2);
    pnlSearch.add(jLabel5, gridBagConstraints);

    jLabel4.setFont(resourceMap.getFont("dtTo.font")); // NOI18N
    jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
    jLabel4.setName("jLabel4"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(6, 4, 2, 2);
    pnlSearch.add(jLabel4, gridBagConstraints);

    jLabel3.setFont(resourceMap.getFont("dtTo.font")); // NOI18N
    jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
    jLabel3.setName("jLabel3"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(6, 4, 2, 2);
    pnlSearch.add(jLabel3, gridBagConstraints);

    dtFrom.setFont(resourceMap.getFont("dtTo.font")); // NOI18N
    dtFrom.setName("dtFrom"); // NOI18N
    dtFrom.setPreferredSize(new java.awt.Dimension(180, 20));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(6, 2, 2, 2);
    pnlSearch.add(dtFrom, gridBagConstraints);

    dtTo.setFont(resourceMap.getFont("dtTo.font")); // NOI18N
    dtTo.setName("dtTo"); // NOI18N
    dtTo.setPreferredSize(new java.awt.Dimension(180, 20));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(6, 2, 2, 2);
    pnlSearch.add(dtTo, gridBagConstraints);

    cmbGds.setFont(resourceMap.getFont("dtTo.font")); // NOI18N
    cmbGds.setName("cmbGds"); // NOI18N
    cmbGds.setPreferredSize(new java.awt.Dimension(180, 20));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 0.6;
    gridBagConstraints.insets = new java.awt.Insets(6, 2, 2, 2);
    pnlSearch.add(cmbGds, gridBagConstraints);

    javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(etsbackoffice.ETSBackofficeApp.class).getContext().getActionMap(FrameSale.class, this);
    btnSearch.setAction(actionMap.get("search")); // NOI18N
    btnSearch.setFont(resourceMap.getFont("btnSearch.font")); // NOI18N
    btnSearch.setIcon(resourceMap.getIcon("btnSearch.icon")); // NOI18N
    btnSearch.setText(resourceMap.getString("btnSearch.text")); // NOI18N
    btnSearch.setName("btnSearch"); // NOI18N
    btnSearch.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnSearchActionPerformed(evt);
        }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 6;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    pnlSearch.add(btnSearch, gridBagConstraints);

    buttonGroup1.add(rdoSelf);
    rdoSelf.setFont(resourceMap.getFont("rdoThirdParty.font")); // NOI18N
    rdoSelf.setSelected(true);
    rdoSelf.setText(resourceMap.getString("rdoSelf.text")); // NOI18N
    rdoSelf.setName("rdoSelf"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(6, 2, 2, 2);
    pnlSearch.add(rdoSelf, gridBagConstraints);

    buttonGroup1.add(rdoThirdParty);
    rdoThirdParty.setFont(resourceMap.getFont("rdoThirdParty.font")); // NOI18N
    rdoThirdParty.setText(resourceMap.getString("rdoThirdParty.text")); // NOI18N
    rdoThirdParty.setName("rdoThirdParty"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(6, 2, 2, 2);
    pnlSearch.add(rdoThirdParty, gridBagConstraints);

    buttonGroup1.add(rdoAllType);
    rdoAllType.setFont(resourceMap.getFont("rdoThirdParty.font")); // NOI18N
    rdoAllType.setText(resourceMap.getString("rdoAllType.text")); // NOI18N
    rdoAllType.setName("rdoAllType"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(6, 2, 2, 2);
    pnlSearch.add(rdoAllType, gridBagConstraints);

    jLabel1.setFont(resourceMap.getFont("dtTo.font")); // NOI18N
    jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
    jLabel1.setName("jLabel1"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(6, 4, 2, 2);
    pnlSearch.add(jLabel1, gridBagConstraints);

    cmbVendors.setFont(resourceMap.getFont("dtTo.font")); // NOI18N
    cmbVendors.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All" }));
    cmbVendors.setEnabled(false);
    cmbVendors.setName("cmbVendors"); // NOI18N
    cmbVendors.setPreferredSize(new java.awt.Dimension(180, 20));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 0.6;
    gridBagConstraints.insets = new java.awt.Insets(6, 2, 2, 2);
    pnlSearch.add(cmbVendors, gridBagConstraints);

    jXBusyLabel1.setText(resourceMap.getString("jXBusyLabel1.text")); // NOI18N
    jXBusyLabel1.setName("jXBusyLabel1"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 5;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    pnlSearch.add(jXBusyLabel1, gridBagConstraints);

    jLabel2.setFont(resourceMap.getFont("jLabel2.font")); // NOI18N
    jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
    jLabel2.setName("jLabel2"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 5;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 2);
    pnlSearch.add(jLabel2, gridBagConstraints);

    cmbTktStatus.setFont(resourceMap.getFont("cmbTktStatus.font")); // NOI18N
    cmbTktStatus.setName("cmbTktStatus"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 6;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    pnlSearch.add(cmbTktStatus, gridBagConstraints);

    jLabel6.setFont(resourceMap.getFont("jLabel6.font")); // NOI18N
    jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
    jLabel6.setName("jLabel6"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 5;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    pnlSearch.add(jLabel6, gridBagConstraints);

    txtCareerCode.setFont(resourceMap.getFont("txtCareerCode.font")); // NOI18N
    txtCareerCode.setText(resourceMap.getString("txtCareerCode.text")); // NOI18N
    txtCareerCode.setName("txtCareerCode"); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 6;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    pnlSearch.add(txtCareerCode, gridBagConstraints);

    jScrollPane2.setName("jScrollPane2"); // NOI18N

    txtSaleSummery.setBackground(resourceMap.getColor("txtSaleSummery.background")); // NOI18N
    txtSaleSummery.setContentType(resourceMap.getString("txtSaleSummery.contentType")); // NOI18N
    txtSaleSummery.setEditable(false);
    txtSaleSummery.setFont(resourceMap.getFont("txtSaleSummery.font")); // NOI18N
    txtSaleSummery.setForeground(resourceMap.getColor("txtSaleSummery.foreground")); // NOI18N
    txtSaleSummery.setName("txtSaleSummery"); // NOI18N
    jScrollPane2.setViewportView(txtSaleSummery);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridheight = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    pnlSearch.add(jScrollPane2, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 2, 2, 2);
    getContentPane().add(pnlSearch, gridBagConstraints);

    jScrollPane1.setAutoscrolls(true);
    jScrollPane1.setName("jScrollPane1"); // NOI18N

    tblTicket.setBackground(resourceMap.getColor("tblTicket.background")); // NOI18N
    tblTicket.setForeground(resourceMap.getColor("tblTicket.foreground")); // NOI18N
    tblTicket.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {

        },
        new String [] {
            "", "T.Date", "Status", "PNR", "BookingAgt", "TktingAgt", "Career", "PaxName", "TktNo", "BaseFare", "Tax", "TotalFare", "BSPCom", "Fee", "NetFare"
        }
    ) {
        boolean[] canEdit = new boolean [] {
            false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
        };

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit [columnIndex];
        }
    });
    tblTicket.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
    tblTicket.setFont(resourceMap.getFont("tblTicket.font")); // NOI18N
    tblTicket.setName("tblTicket"); // NOI18N
    tblTicket.setShowHorizontalLines(false);
    tblTicket.setShowVerticalLines(false);
    tblTicket.getTableHeader().setReorderingAllowed(false);
    tblTicket.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            tblTicketMouseClicked(evt);
        }
    });
    jScrollPane1.setViewportView(tblTicket);
    tblTicket.getColumnModel().getColumn(0).setPreferredWidth(40);
    tblTicket.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblTicket.columnModel.title0")); // NOI18N
    tblTicket.getColumnModel().getColumn(1).setMinWidth(80);
    tblTicket.getColumnModel().getColumn(1).setPreferredWidth(80);
    tblTicket.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblTicket.columnModel.title1")); // NOI18N
    tblTicket.getColumnModel().getColumn(2).setMinWidth(80);
    tblTicket.getColumnModel().getColumn(2).setPreferredWidth(80);
    tblTicket.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblTicket.columnModel.title2")); // NOI18N
    tblTicket.getColumnModel().getColumn(3).setMinWidth(80);
    tblTicket.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblTicket.columnModel.title3")); // NOI18N
    tblTicket.getColumnModel().getColumn(4).setMinWidth(80);
    tblTicket.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblTicket.columnModel.title4")); // NOI18N
    tblTicket.getColumnModel().getColumn(5).setMinWidth(80);
    tblTicket.getColumnModel().getColumn(5).setPreferredWidth(80);
    tblTicket.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("tblTicket.columnModel.title5")); // NOI18N
    tblTicket.getColumnModel().getColumn(6).setMinWidth(80);
    tblTicket.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("tblTicket.columnModel.title6")); // NOI18N
    tblTicket.getColumnModel().getColumn(7).setMinWidth(150);
    tblTicket.getColumnModel().getColumn(7).setMaxWidth(400);
    tblTicket.getColumnModel().getColumn(7).setHeaderValue(resourceMap.getString("tblTicket.columnModel.title7")); // NOI18N
    tblTicket.getColumnModel().getColumn(8).setMinWidth(80);
    tblTicket.getColumnModel().getColumn(8).setHeaderValue(resourceMap.getString("tblTicket.columnModel.title8")); // NOI18N
    tblTicket.getColumnModel().getColumn(9).setMinWidth(80);
    tblTicket.getColumnModel().getColumn(9).setHeaderValue(resourceMap.getString("tblTicket.columnModel.title9")); // NOI18N
    tblTicket.getColumnModel().getColumn(10).setMinWidth(80);
    tblTicket.getColumnModel().getColumn(10).setHeaderValue(resourceMap.getString("tblTicket.columnModel.title10")); // NOI18N
    tblTicket.getColumnModel().getColumn(11).setMinWidth(80);
    tblTicket.getColumnModel().getColumn(11).setHeaderValue(resourceMap.getString("tblTicket.columnModel.title11")); // NOI18N
    tblTicket.getColumnModel().getColumn(12).setMinWidth(80);
    tblTicket.getColumnModel().getColumn(12).setHeaderValue(resourceMap.getString("tblTicket.columnModel.title12")); // NOI18N
    tblTicket.getColumnModel().getColumn(13).setHeaderValue(resourceMap.getString("tblTicket.columnModel.title14")); // NOI18N
    tblTicket.getColumnModel().getColumn(14).setMaxWidth(80);
    tblTicket.getColumnModel().getColumn(14).setHeaderValue(resourceMap.getString("tblTicket.columnModel.title13")); // NOI18N

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
    getContentPane().add(jScrollPane1, gridBagConstraints);

    pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
}//GEN-LAST:event_btnSearchActionPerformed

    private void btnTicketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTicketActionPerformed
        int row = tblTicket.getSelectedRow();
        if (row != -1) {
            frameTicket(this.soldTickets.get(row), row);
        }
    }//GEN-LAST:event_btnTicketActionPerformed

    private void btnViewReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewReportActionPerformed
        if (!this.soldTickets.isEmpty()) {
            BackofficeReporting rptTicketing = new BackofficeReporting();
            TicketingReport tReport = new TicketingReport();

            List<Object> invObject = new ArrayList();
            tReport.setmAgent(AuthenticationBo.getmAgent());
            tReport.setFrom(from);
            tReport.setTo(to);
            tReport.setCareer(this.career);
            if (this.gdsId != null) {
                tReport.setGDS(Enums.GDS.valueOf(this.gdsId));
            } else {
                tReport.setGDS("All");
            }

            if (this.tktStatus != null) {
                tReport.setTktStatus(Enums.TicketStatus.valueOf(this.tktStatus));
            } else {
                tReport.setTktStatus("All");
            }

            if (this.ticketingType == 0) {
                tReport.setTktingType("All");
            } else if (this.ticketingType == 1) {
                tReport.setTktingType("Self Ticketing");
            } else if (this.ticketingType == 2) {
                tReport.setTktingType("3rd Party Ticketing");
            }

            if (this.gdsId == null) {
                tReport.setGDS("All");
            } else {
                tReport.setGDS(Enums.GDS.valueOf(this.gdsId));
            }

            tReport.setTickets(soldTickets);
            tReport.setsSummery(sSummery);

            invObject.add(tReport);
            rptTicketing.viewTicketingReport(invObject);
        }
    }//GEN-LAST:event_btnViewReportActionPerformed

    private void tblTicketMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblTicketMouseClicked
        if (evt.getClickCount() == 2) {
            int row = tblTicket.getSelectedRow();
            if (row != -1) {
                frameTicket(this.soldTickets.get(row), row);
            }
        }
    }//GEN-LAST:event_tblTicketMouseClicked
    
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        List<Ticket> ticketsToDelete = new ArrayList<Ticket>();
        
        for (Ticket t : this.soldTickets) {
            if (t.getTicketNo().equals("5382759064")) {
                //t.setAccountingDocumentLine(null);
                //t.setPurchaseAccountingDocumentLine(null);
                //t.setPnr(null);
                
                ticketsToDelete.add(t);
            }
        }
        ticketBo.deleteAll(ticketsToDelete);
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
 /*   public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new FrameSale().setVisible(true);
            }
        });
    }*/
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEmailReport;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnTicket;
    private javax.swing.JButton btnViewReport;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox cmbGds;
    private javax.swing.JComboBox cmbTktStatus;
    private javax.swing.JComboBox cmbVendors;
    private org.jdesktop.swingx.JXDatePicker dtFrom;
    private org.jdesktop.swingx.JXDatePicker dtTo;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator2;
    private org.jdesktop.swingx.JXBusyLabel jXBusyLabel1;
    private javax.swing.JPanel pnlSearch;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JRadioButton rdoAllType;
    private javax.swing.JRadioButton rdoSelf;
    private javax.swing.JRadioButton rdoThirdParty;
    private javax.swing.JLabel statusMessageLabel;
    private org.jdesktop.swingx.JXTable tblTicket;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JTextField txtCareerCode;
    private javax.swing.JEditorPane txtSaleSummery;
    // End of variables declaration//GEN-END:variables
    

    @Action
    public void frameTicket(Ticket ticket, int index) {
        if (ticket.getTktStatus() != 4) {
            DlgTicket frameTicket = new DlgTicket(this);
            if (frameTicket.showTicketDialog(ticket)) {
                new Thread(new threadSaveTicket(ticket)).start();
                //populateTblSales();
            } else {
            }
        } else {
            DlgTicketR frameRefundTicket = new DlgTicketR(this);

            if (frameRefundTicket.showTicketDialog(ticket)) {
                new Thread(new threadSaveTicket(ticket)).start();
            } else {
                frameRefundTicket = null;
            }
        }
    }
   
    //**********************threads********************
    //Thread1 to load complete pnr;
    private long startTime = 0;
    private long stopTime = 0;
    private float elapsedTime = 0;

    /**
     * @param pnrDao the pnrDao to set
     */
    private class threadSales implements Runnable {

        public threadSales() {
        }

        public void run() {

            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            //progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();
            statusMessageLabel.setText("Loading...");

            searchSales();
            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            progressBar.setIndeterminate(false);
            //progressBar.repaint(); //Refresh graphics
        }
    }

    private class threadSaveTicket implements Runnable {

        private Ticket t;

        public threadSaveTicket(Ticket t) {
            this.t = t;
        }

        public void run() {

            progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); //Set value
            //progressBar.repaint(); //Refresh graphics
            startTime = System.currentTimeMillis();
            statusMessageLabel.setText("Saving ticket...");
            ticketBo.setTicket(t);
            ticketBo.saveTicket();

            stopTime = System.currentTimeMillis();
            elapsedTime = (stopTime - startTime);
            statusMessageLabel.setText("Task Completed in: " + elapsedTime / 1000 + " seconds");
            populateTblSales();

            progressBar.setIndeterminate(false);
            //progressBar.repaint(); //Refresh graphics
        }
    }
}
