package org.gnucash.viewer.panels;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.viewer.Const;
import org.gnucash.viewer.actions.TransactionSplitAction;
import org.gnucash.viewer.models.GnuCashSimpleAccountTransactionsTableModel;
import org.gnucash.viewer.models.GnuCashTransactionsSplitsTableModel;
import org.gnucash.viewer.widgets.MultiLineToolTip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * This Panel shows a list of transactions.
 * These transactions currently can only be "all transactions
 * of a single account"
 * but later other transaction-lists like search-results will be possible.
 */
public class TransactionsPanel extends JPanel {

	private static final Logger LOGGER = LoggerFactory.getLogger(TransactionsPanel.class);

	private static final int DEFAULT_WIDTH = 300;
	private static final int DEFAULT_HEIGHT = 200;

	// For serializing
	private static final long serialVersionUID = 1L;

	// A scrollpane for ${@link #transactionTable}}
	private JScrollPane transactionTableScrollPane = null;

	// A table showing the transactions
	private JTable transactionTable;

	// A panel holding ${@link #selectionSummaryLabel}}
	private JPanel selectionSummaryPanel = null;

	// A label showing summary-information about the selected transactions.
	private JLabel selectionSummaryLabel = null;

	// A drop-down List to select the account the numbers
	// in ${@link #selectionSummaryLabel}} shall refer to.
	private JComboBox selectionSummaryAccountComboBox = null;

	// The model of our ${@link #transactionTable}
	private GnuCashTransactionsSplitsTableModel model;

	// The panel to show a single transaction
	private ShowTransactionPanel mySingleTransactionPanel;

	// A JPanel showing either {@link #getSelectionSummaryPanel()} or {@link #getSingleTransactionPanel()}
	private JPanel mySummaryPanel;

	// The actions we have on Splits
	private Collection<TransactionSplitAction> mySplitActions;

	/**
	 * @return Returns the model
	 * @see #model
	 */
	public GnuCashTransactionsSplitsTableModel getModel() {
		return model;
	}

	/**
	 * @param aModel The model to set.
	 * @see #model
	 */
	protected void setModel(final GnuCashTransactionsSplitsTableModel aModel) {
		if ( aModel == null ) {
			throw new IllegalArgumentException("argument <aModel> is null"); //$NON-NLS-1$
		}

		Object old = model;
		if (old == aModel) {
			return; // nothing has changed
		}
		model = aModel;

		getTransactionTable().setModel(model);
		getTransactionTable().setAutoCreateRowSorter(true);

		TableColumn balanceColumn = null;
		try {
			balanceColumn = getTransactionTable().getColumn(Messages_TransactionsPanel.getString("TransactionsPanel.1")); //$NON-NLS-1$
		} catch (Exception e) {
			// column is allowed not to exist
		}
		
		// ---
		// BEGIN col widths
		FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(transactionTable.getFont());
		
		int currencyWidthDefault = SwingUtilities.computeStringWidth(metrics, GnuCashSimpleAccountTransactionsTableModel.DEFAULT_CURRENCY_FORMAT.format(Const.TABLE_COL_AMOUNT_WIDTH_VAL_SMALL));
		int currencyWidthMax     = SwingUtilities.computeStringWidth(metrics, GnuCashSimpleAccountTransactionsTableModel.DEFAULT_CURRENCY_FORMAT.format(Const.TABLE_COL_AMOUNT_WIDTH_VAL_BIG));

		getTransactionTable().getColumn(Messages_TransactionsPanel.getString("TransactionsPanel.2")).setPreferredWidth( //$NON-NLS-1$
				SwingUtilities.computeStringWidth(metrics, GnuCashSimpleAccountTransactionsTableModel.DATE_FORMAT.format(LocalDateTime.now())) + Const.TABLE_COL_EXTRA_WIDTH);
//		getTransactionTable().getColumn(Messages_TransactionsPanel.getString("TransactionsPanel.6")).setPreferredWidth(Const.PANEL_DEFAULT_WIDTH); //$NON-NLS-1$
//		getTransactionTable().getColumn(Messages_TransactionsPanel.getString("TransactionsPanel.7")).setPreferredWidth(Const.PANEL_DEFAULT_WIDTH); //$NON-NLS-1$
		getTransactionTable().getColumn(Messages_TransactionsPanel.getString("TransactionsPanel.3")).setPreferredWidth(currencyWidthDefault); //$NON-NLS-1$
		getTransactionTable().getColumn(Messages_TransactionsPanel.getString("TransactionsPanel.4")).setPreferredWidth(currencyWidthDefault); //$NON-NLS-1$
		if (balanceColumn != null) {
			balanceColumn.setPreferredWidth(currencyWidthDefault);
		}

		getTransactionTable().getColumn(Messages_TransactionsPanel.getString("TransactionsPanel.2")).setMinWidth(Const.TABLE_COL_MIN_WIDTH); //$NON-NLS-1$
		getTransactionTable().getColumn(Messages_TransactionsPanel.getString("TransactionsPanel.6")).setMinWidth(Const.TABLE_COL_MIN_WIDTH); //$NON-NLS-1$
		getTransactionTable().getColumn(Messages_TransactionsPanel.getString("TransactionsPanel.7")).setMinWidth(Const.TABLE_COL_MIN_WIDTH); //$NON-NLS-1$
		getTransactionTable().getColumn(Messages_TransactionsPanel.getString("TransactionsPanel.3")).setMinWidth(Const.TABLE_COL_MIN_WIDTH); //$NON-NLS-1$
		getTransactionTable().getColumn(Messages_TransactionsPanel.getString("TransactionsPanel.4")).setMinWidth(Const.TABLE_COL_MIN_WIDTH); //$NON-NLS-1$
		if (balanceColumn != null) {
			balanceColumn.setMinWidth(Const.TABLE_COL_MIN_WIDTH);
		}

//		getTransactionTable().getColumn(Messages_TransactionsPanel.getString("TransactionsPanel.2")).setMaxWidth(
//				SwingUtilities.computeStringWidth(metrics, KMyMoneySimpleAccountTransactionsTableModel.dateFormat.format(LocalDateTime.now())) + Const.TABLE_COL_WIDTH_TOL);
		getTransactionTable().getColumn(Messages_TransactionsPanel.getString("TransactionsPanel.2")).setMaxWidth(Const.TABLE_COL_MAX_WIDTH); //$NON-NLS-1$
//		getTransactionTable().getColumn(Messages_TransactionsPanel.getString("TransactionsPanel.6")).setMaxWidth(Const.PANEL_MAX_WIDTH); //$NON-NLS-1$
//		getTransactionTable().getColumn(Messages_TransactionsPanel.getString("TransactionsPanel.7")).setMaxWidth(Const.PANEL_MAX_WIDTH); //$NON-NLS-1$
		getTransactionTable().getColumn(Messages_TransactionsPanel.getString("TransactionsPanel.3")).setMaxWidth(currencyWidthMax); //$NON-NLS-1$
		getTransactionTable().getColumn(Messages_TransactionsPanel.getString("TransactionsPanel.4")).setMaxWidth(currencyWidthMax); //$NON-NLS-1$
		if (balanceColumn != null) {
			balanceColumn.setMaxWidth(currencyWidthMax);
		}
		// END col widths
		// ---
		
		getTransactionTable().getColumn(Messages_TransactionsPanel.getString("TransactionsPanel.6")).setCellRenderer(new DesriptionCellRenderer()); //$NON-NLS-1$
		getTransactionTable().getColumn(Messages_TransactionsPanel.getString("TransactionsPanel.7")).setCellRenderer(new DesriptionCellRenderer()); //$NON-NLS-1$

		updateSelectionSummaryAccountList();
		updateSelectionSummary();
		getSingleTransactionPanel().setTransaction(null);

	}

	/**
	 * This is the default constructor.
	 */
	public TransactionsPanel() {
		super();
		initialize();
	}

	/**
	 * Give an account who's transactions to display.
	 *
	 * @param account if null, an empty table will be shown.
	 */
	public void setAccount(final GnuCashAccount account) {
		if ( account == null ) {
			setModel(new GnuCashSimpleAccountTransactionsTableModel());
		} else {
			setModel(new GnuCashSimpleAccountTransactionsTableModel(account));
		}
	}

	/**
	 * This method initializes this panel.
	 */
	private void initialize() {
		this.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		this.setLayout(new BorderLayout());
		this.add(getTransactionTableScrollPane(), BorderLayout.CENTER);
		this.add(getSummaryPanel(), BorderLayout.SOUTH);
	}

	/**
	 * @return a JPanel showing either {@link #getSelectionSummaryPanel()} or {@link #getSingleTransactionPanel()}.
	 */
	private JPanel getSummaryPanel() {
		if ( mySummaryPanel == null ) {
			mySummaryPanel = new JPanel();
			mySummaryPanel.setLayout(new CardLayout());
			mySummaryPanel.add(getSelectionSummaryPanel(), Messages_TransactionsPanel.getString("TransactionsPanel.15")); //$NON-NLS-1$
			getSingleTransactionPanel().setVisible(false);
			mySummaryPanel.add(getSingleTransactionPanel(), Messages_TransactionsPanel.getString("TransactionsPanel.16")); //$NON-NLS-1$
			getSummaryPanel().setPreferredSize(getSelectionSummaryPanel().getPreferredSize());
		}

		return mySummaryPanel;
	}

	/**
	 * This method initializes transactionTableScrollPane
	 *
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getTransactionTableScrollPane() {
		if ( transactionTableScrollPane == null ) {
			transactionTableScrollPane = new JScrollPane();
			transactionTableScrollPane.setViewportView(getTransactionTable());
		}
		return transactionTableScrollPane;
	}

	/**
	 * This method initializes transactionTable.
	 *
	 * @return javax.swing.JTable
	 */
	protected JTable getTransactionTable() {
		if ( transactionTable == null ) {
			transactionTable = new JTable() {

				/**
				 * Our TransactionsPanel.java.
				 * @see long
				 */
				private static final long serialVersionUID = 1L;

				/**
				 * @see javax.swing.JTable#getToolTipText(java.awt.event.MouseEvent)
				 */

				@Override
				public String getToolTipText(final MouseEvent event) {
					java.awt.Point p = event.getPoint();
					int rowIndex = rowAtPoint(p);
					// convertColumnIndexToModel is needed,
					// because the user may reorder columns
					//int realColumnIndex = convertColumnIndexToModel(columnAtPoint(p));
					if ( rowIndex >= 0 ) {
						GnuCashSimpleAccountTransactionsTableModel model = (GnuCashSimpleAccountTransactionsTableModel) getModel();
						GnuCashTransactionSplit localSplit = model.getTransactionSplit(rowIndex);
						GnuCashTransaction transaction = localSplit.getTransaction();
						StringBuilder output = new StringBuilder();
						output.append("\"") //$NON-NLS-1$
								.append(transaction.getNumber())
								.append("\t \"") //$NON-NLS-1$
								.append(transaction.getDescription())
								.append("\"\t [") //$NON-NLS-1$
								.append(localSplit.getAccount().getQualifiedName())
								.append("]\t ") //$NON-NLS-1$
								.append(localSplit.getQuantity())
								.append(localSplit.getAccount().getCmdtyCurrID().getType() == GCshCmdtyCurrID.Type.CURRENCY ? " "  : " x ") //$NON-NLS-1$ //$NON-NLS-2$
								.append(localSplit.getAccount().getCmdtyCurrID())
								.append("\n"); //$NON-NLS-1$

						for ( GnuCashTransactionSplit split : transaction.getSplits() ) {
							output.append("\"") //$NON-NLS-1$
									.append(split.getAction())
									.append("\"\t \"") //$NON-NLS-1$
									.append(split.getDescription())
									.append("\"\t [") //$NON-NLS-1$
									.append(split.getAccount().getQualifiedName())
									.append("]\t ") //$NON-NLS-1$
									.append(split.getQuantity())
									.append(localSplit.getAccount().getCmdtyCurrID().getType() == GCshCmdtyCurrID.Type.CURRENCY ? " " : " x ") //$NON-NLS-1$ //$NON-NLS-2$
									.append(split.getAccount().getCmdtyCurrID())
									.append("\n"); //$NON-NLS-1$
						}
						if ( ! transaction.isBalanced() ) {
							output.append("TRANSACTION IS NOT BALANACED! missung=" + transaction.getBalanceFormatted()); //$NON-NLS-1$
						}

						return output.toString();
					}

					// show default-tooltip
					return super.getToolTipText(event);
				}

				@Override
				public JToolTip createToolTip() {
					MultiLineToolTip tip = new MultiLineToolTip();
					tip.setComponent(this);
					return tip;
				}

			};

			// add a listener to call updateSelectionSummary() every time
			// the user changes the selected rows.
			transactionTable.getSelectionModel().addListSelectionListener(
					new ListSelectionListener() {
						public void valueChanged(
								final javax.swing.event.ListSelectionEvent e) {
							try {
								updateSelectionSummaryAccountList();
								updateSelectionSummary();
								if ( getTransactionTable().getSelectedRowCount() == 1 ) {
									GnuCashTransactionSplit transactionSplit = model.getTransactionSplit(getTransactionTable().getSelectedRow
											());
									//                               setTransaction(transactionSplit.getTransaction());

									getSingleTransactionPanel().setTransaction(transactionSplit.getTransaction());
									getSingleTransactionPanel().setVisible(true);
									getSelectionSummaryPanel().setVisible(false);
									getSummaryPanel().setPreferredSize(getSingleTransactionPanel().getPreferredSize());
									//((CardLayout) getSelectionSummaryPanel().getLayout()).next(getSummaryPanel());
								} else {
									getSingleTransactionPanel().setVisible(false);
									getSingleTransactionPanel().setTransaction(null);
									getSelectionSummaryPanel().setVisible(true);
									getSummaryPanel().setPreferredSize(getSelectionSummaryPanel().getPreferredSize());
									//((CardLayout) getSelectionSummaryPanel().getLayout()).next(getSummaryPanel());
								}
							}
							catch (Exception e1) {
								LOGGER.error("", e1); //$NON-NLS-1$
							}
						}

						;
					});
			setModel(new GnuCashSimpleAccountTransactionsTableModel());
		}
		return transactionTable;
	}

	/**
	 * This method initializes selectionSummaryLabel.
	 *
	 * @return JLabel
	 */
	private JLabel getSelectionSummaryLabel() {
		if ( selectionSummaryLabel == null ) {
			selectionSummaryLabel = new JLabel();
			selectionSummaryLabel.setText(""); //$NON-NLS-1$
		}
		return selectionSummaryLabel;
	}

	/**
	 * This method initializes selectionSummaryLabel.
	 *
	 * @return JLabel
	 */
	private JComboBox getSelectionSummaryAccountComboBox() {
		if ( selectionSummaryAccountComboBox == null ) {
			selectionSummaryAccountComboBox = new JComboBox();
			selectionSummaryAccountComboBox.setEditable(false);
			selectionSummaryAccountComboBox.addItemListener(new ItemListener() {
				public void itemStateChanged(final java.awt.event.ItemEvent e) {
					updateSelectionSummary();
				}

				;
			});
		}
		return selectionSummaryAccountComboBox;
	}

	/**
	 * This method initializes selectionSummaryPanel.
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getSelectionSummaryPanel() {
		if ( selectionSummaryPanel == null ) {
			selectionSummaryPanel = new JPanel();
			selectionSummaryPanel.setLayout(new BorderLayout());
			selectionSummaryPanel.add(getSelectionSummaryLabel(),
					BorderLayout.CENTER);
			selectionSummaryPanel.add(getSelectionSummaryAccountComboBox(),
					BorderLayout.SOUTH);
		}
		return selectionSummaryPanel;
	}

	/**
	 * This method initializes ShowTransactionPanel.
	 *
	 * @return javax.swing.JPanel
	 */
	protected ShowTransactionPanel getSingleTransactionPanel() {
		if ( mySingleTransactionPanel == null ) {
			mySingleTransactionPanel = new ShowTransactionPanel();
			mySingleTransactionPanel.setSplitActions(getSplitActions());
		}
		return mySingleTransactionPanel;
	}

	/**
	 * Put all accounts into the model of
	 * the selectionSummaryAccountComboBox.
	 */
	private void updateSelectionSummaryAccountList() {
		Set<GnuCashAccount> accounts = new TreeSet<GnuCashAccount>();

		int selectedCount = getTransactionTable().getSelectedRowCount();
		if (selectedCount < 1) {
			// get all accounts of all splits
			int count = model.getRowCount();

			for ( int i = 0; i < count; i++ ) {
				GnuCashTransactionSplit transactionSplit =
						model.getTransactionSplit(i);

				try {
					GnuCashTransaction transaction = transactionSplit.getTransaction();
					if ( transaction == null ) {
						LOGGER.error("updateSelectionSummaryAccountList: Split has no transaction"); //$NON-NLS-1$
					} else {
						Collection<? extends GnuCashTransactionSplit> splits =
								transaction.getSplits();
						for ( GnuCashTransactionSplit split : splits ) {
							try {
								GnuCashAccount account = split.getAccount();
								if ( account != null ) {
									if (!accounts.contains(account)) {
										accounts.add(account);
									}
								}
							}
							catch (Exception x) {
								System.err.println("Ignoring account in " //$NON-NLS-1$
										+ "TransactionPanel::updateSelectionSummary" //$NON-NLS-1$
										+ "AccountList() because of:"); //$NON-NLS-1$
								x.printStackTrace(System.err);
							}
						}
					}
				}
				catch (Exception e) {
					LOGGER.error("updateSelectionSummaryAccountList: Problem in " //$NON-NLS-1$
									+ getClass().getName(),
							e);
				}
			}
		} else {
			// show a summary only for the selected transactions
			int[] selectedRows = getTransactionTable().getSelectedRows();

			for (int selectedRow : selectedRows) {
				GnuCashTransactionSplit transactionSplit = model.getTransactionSplit(selectedRow);
				Collection<? extends GnuCashTransactionSplit> splits = transactionSplit.getTransaction().getSplits();
				for (GnuCashTransactionSplit split : splits) {
					if (!accounts.contains(split.getAccount())) {
						accounts.add(split.getAccount());
					}
				}

			}
		}

		DefaultComboBoxModel aModel = new DefaultComboBoxModel();
		for (GnuCashAccount account : accounts) {
			aModel.addElement(account);
		}

		JComboBox list = getSelectionSummaryAccountComboBox();
		list.setModel(aModel);
		list.setSelectedIndex(-1);
	}

	/**
	 * If the user has selected an account to display a summary for,
	 * we add all splits of the given split's transaction that have
	 * that account. If nothing is selected, we add the given split.
	 *
	 * @param retval the list of splits to add to
	 * @param split  the split who's transaction to look at
	 */
	private void replaceSplitsWithSelectedAccountsSplits(final Set<GnuCashTransactionSplit> retval, final GnuCashTransactionSplit split) {
		JComboBox combo = getSelectionSummaryAccountComboBox();
		GnuCashAccount selectedAccount =
				(GnuCashAccount) combo.getSelectedItem();
		if ( selectedAccount == null ) {
			retval.add(split);
		} else {
			GnuCashTransaction transaction = split.getTransaction();
			for ( GnuCashTransactionSplit split2 : transaction.getSplits() ) {
				if ( split2 == null ) {
					continue;
				}
				GnuCashAccount account = split2.getAccount();
				if ( account != null && account.equals(selectedAccount) ) {
					retval.add(split2);
				}
			}
		}

	}

	/**
	 * If the user has selected an account to display a summary for,
	 * we return All splits of the selected transactions that are
	 * for that account. Else we return all selected Splits.
	 * If no Splits are selected, we use all splits as the selected
	 * ones.
	 *
	 * @return the splits of the selected/all transactions featuring this/the
	 * selected account.
	 */
	private Collection<GnuCashTransactionSplit> getSplitsForSummary() {
		int[] selectedRows = getTransactionTable().getSelectedRows();
		Set<GnuCashTransactionSplit> retval
				= new HashSet<GnuCashTransactionSplit>();

		if ( selectedRows == null || selectedRows.length == 0 ) {
			int count = model.getRowCount();
			for (int i = 0; i < count; i++) {
				GnuCashTransactionSplit transactionSplit = model.getTransactionSplit(i);
				replaceSplitsWithSelectedAccountsSplits(retval, transactionSplit);
			}
		} else {
			for ( int selectedRow : selectedRows ) {
				GnuCashTransactionSplit transactionSplit = model.getTransactionSplit(selectedRow);
				replaceSplitsWithSelectedAccountsSplits(retval, transactionSplit);
			}
		}

		return retval;
	}

	/**
	 * Update the text in the selectionSummaryLabel
	 * to show summary-information about the currently
	 * selected transactions.
	 */
	private void updateSelectionSummary() {

		int selectedCount = getTransactionTable().getSelectedRowCount();
		FixedPointNumber valueSumPlus = new FixedPointNumber(0);
		FixedPointNumber valueSumMinus = new FixedPointNumber(0);
		FixedPointNumber valueSumBalance = new FixedPointNumber(0);
		NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

		Collection<GnuCashTransactionSplit> splits;
		splits = getSplitsForSummary();
		for ( GnuCashTransactionSplit transactionSplit : splits ) {
			FixedPointNumber value = transactionSplit.getValue();
			valueSumBalance.add(value);
			if (value.isPositive()) {
				valueSumPlus.add(value);
			} else {
				valueSumMinus.add(value);
			}
		}

		if (selectedCount < 1) {
			// show a summary for all transactions displayed
			int count = model.getRowCount();
			getSelectionSummaryLabel().setText(count + Messages_TransactionsPanel.getString("TransactionsPanel.39") //$NON-NLS-1$
					+ currencyFormat.format(valueSumPlus)
					+ currencyFormat.format(valueSumMinus)
					+ "=" + currencyFormat.format(valueSumBalance)); //$NON-NLS-1$
		} else {
			// show a summary only for the selected transactions
			getSelectionSummaryLabel().setText(selectedCount
					+ Messages_TransactionsPanel.getString("TransactionsPanel.41") //$NON-NLS-1$
					+ currencyFormat.format(valueSumPlus)
					+ currencyFormat.format(valueSumMinus)
					+ "=" + currencyFormat.format(valueSumBalance)); //$NON-NLS-1$
		}

	}

	/**
	 * @param aTransaction the transactions to show in detail
	 */
	public void setTransaction(final GnuCashTransaction aTransaction) {
		TableModel temp = getTransactionTable().getModel();
		if ( temp != null && temp instanceof GnuCashTransactionsSplitsTableModel ) {
			GnuCashTransactionsSplitsTableModel tblModel = (GnuCashTransactionsSplitsTableModel) temp;
			int max = tblModel.getRowCount();
			for ( int i = 0; i < max; i++ ) {
				if ( tblModel.getTransactionSplit(i).getTransaction().getID().equals( aTransaction.getID() ) ) {
					getTransactionTable().getSelectionModel().setSelectionInterval(i, i);
					return;
				}
			}
		}
		getSingleTransactionPanel().setTransaction(aTransaction);
		getSingleTransactionPanel().setVisible(true);
		getSelectionSummaryPanel().setVisible(false);
		getSummaryPanel().setPreferredSize(getSingleTransactionPanel().getPreferredSize());
	}

	/**
	 * @param aSplitActions the actions we shall offer on splits.
	 */
	public void setSplitActions(final Collection<TransactionSplitAction> aSplitActions) {
		LOGGER.info("setSplitActions: TransactionsPanel is given " + (mySplitActions == null ? "no" : mySplitActions.size()) + " split-actions"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		mySplitActions = aSplitActions;
		getSingleTransactionPanel().setSplitActions(mySplitActions);
	}

	/**
	 * @return the actions we shall offer on splits.
	 */
	protected Collection<TransactionSplitAction> getSplitActions() {
		return mySplitActions;
	}
}
