package org.gnucash.viewer.models;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.event.TableModelListener;

import org.gnucash.api.Const;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.viewer.panels.Messages_SingleTransactionTableModel;

/*
 * TableModel to show and edit the splits and details of a single transaction.
 */
public class SingleTransactionTableModel implements GnuCashTransactionsSplitsTableModel {
	
	enum TableCols {
		DATE,
		ACTION,
		DESCRIPTION,
		ACCOUNT,
		PLUS,
		MINUS
	}

	// The transaction that we are showing
	private GnuCashTransaction myTransaction;

	// The columns we display
	private final String[] defaultColumnNames = new String[] {
			Messages_SingleTransactionTableModel.getString("SingleTransactionTableModel.1"), 
			Messages_SingleTransactionTableModel.getString("SingleTransactionTableModel.2"), 
			Messages_SingleTransactionTableModel.getString("SingleTransactionTableModel.3"), 
			Messages_SingleTransactionTableModel.getString("SingleTransactionTableModel.4"), 
			Messages_SingleTransactionTableModel.getString("SingleTransactionTableModel.5"), 
			Messages_SingleTransactionTableModel.getString("SingleTransactionTableModel.6"), 
		};

	// How to format dates
	public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(Const.REDUCED_DATE_FORMAT_BOOK);

	// How to format currencies
	public static final NumberFormat DEFAULT_CURRENCY_FORMAT = NumberFormat.getCurrencyInstance();

	public SingleTransactionTableModel(final GnuCashTransaction trx) {
		super();
		myTransaction = trx;
	}

	public boolean isMultiCurrency() {
		if ( getTransaction() == null ) {
			return false;
		}

		for ( GnuCashTransactionSplit split : getTransaction().getSplits() ) {
			if ( ! split.getAccount().getCmdtyCurrID().getNameSpace().equals(getTransaction().getCmdtyCurrID().getNameSpace()) || 
				 ! split.getAccount().getCmdtyCurrID().equals(getTransaction().getCmdtyCurrID()) ) {
				return true;
			}
		}

		return false;
	}

	public SingleTransactionTableModel() {
		super();
		myTransaction = null;
	}

	public GnuCashTransaction getTransaction() {
		return myTransaction;
	}

	public void setTransaction(final GnuCashTransaction trx) {
		if ( trx == null ) {
			throw new IllegalArgumentException("argument <trx> is null");
		}

		Object old = myTransaction;
		if ( old == trx ) {
			return; // nothing has changed
		}
		
		myTransaction = trx;
	}

	public GnuCashTransactionSplit getTransactionSplit(final int aRowIndex) {
		return getTransactionSplits().get(aRowIndex);
	}

	public List<GnuCashTransactionSplit> getTransactionSplits() {
		GnuCashTransaction transaction = getTransaction();
		if ( transaction == null ) {
			return new LinkedList<GnuCashTransactionSplit>();
		}
		
		return new ArrayList<GnuCashTransactionSplit>(transaction.getSplits());
	}

	public int getColumnCount() {
		return defaultColumnNames.length;
	}

	public int getRowCount() {
		GnuCashTransaction transaction = getTransaction();
		if ( transaction == null ) {
			return 0;
		}
		return 1 + getTransactionSplits().size();
	}

	@SuppressWarnings("unchecked")
	public Class getColumnClass(final int columnIndex) {
		return String.class;
	}

	public Object getValueAt(final int rowIndex, final int columnIndex) {
		try {
			if ( rowIndex == 0 ) {
				// show data of transaction
				if ( columnIndex == TableCols.DATE.ordinal() )
					return getTransaction().getDatePostedFormatted();
				else if ( columnIndex == TableCols.ACTION.ordinal() )
					return getTransactionNumber(); // sic
				else if ( columnIndex == TableCols.DESCRIPTION.ordinal() )
					return getTransactionDescription();
				else if ( columnIndex == TableCols.ACCOUNT.ordinal() )
					return "";
				else if ( columnIndex == TableCols.PLUS.ordinal() )
					return "";
				else if ( columnIndex == TableCols.MINUS.ordinal() )
					return "";
				else
					throw new IllegalArgumentException("illegal column index " + columnIndex);
			}

			GnuCashTransactionSplit split = getTransactionSplit(rowIndex - 1);

			if ( columnIndex == TableCols.DATE.ordinal() ) {
				return split.getTransaction().getDatePostedFormatted();
			} else if ( columnIndex == TableCols.ACTION.ordinal() ) {
				String action = split.getActionStr();
				if ( action == null || 
					 action.trim().length() == 0 ) {
					return "";
				}
				return action;
			} else if ( columnIndex == TableCols.DESCRIPTION.ordinal() ) {
				String desc = split.getDescription();
				if ( desc == null || 
					 desc.trim().length() == 0 ) {
					return "";
				}
				return desc;
			} else if ( columnIndex == TableCols.ACCOUNT.ordinal() ) {
				return split.getAccount().getQualifiedName();
			} else if ( columnIndex == TableCols.PLUS.ordinal() ) {
				if ( split.getValue().isPositive() ) {
					if ( split.getAccount().getCmdtyCurrID().getNameSpace().equals(getTransaction().getCmdtyCurrID().getNameSpace()) && 
						 split.getAccount().getCmdtyCurrID().equals(getTransaction().getCmdtyCurrID()) ) {
						return split.getValueFormatted();
					}
					return split.getValueFormatted() + " (" + split.getQuantityFormatted() + ")";
				} else {
					return "";
				}
			} else if ( columnIndex == TableCols.MINUS.ordinal() ) {
				if ( ! split.getValue().isPositive() ) {
					if ( split.getAccount().getCmdtyCurrID().getNameSpace().equals(getTransaction().getCmdtyCurrID().getNameSpace()) && 
						 split.getAccount().getCmdtyCurrID().equals(getTransaction().getCmdtyCurrID()) ) {
						return split.getValueFormatted();
					}
					return split.getValueFormatted() + " (" + split.getQuantityFormatted() + ")";
				} else {
					return "";
				}
			} else {
				throw new IllegalArgumentException("illegal columnIndex " + columnIndex);
			}
		} catch (Exception x) {
			String message = "Internal Error in "
					+ getClass().getName() + ":getValueAt(int rowIndex="
					+ rowIndex
					+ ", int columnIndex="
					+ columnIndex
					+ ")!\n"
					+ "Exception of Type [" + x.getClass().getName() + "]\n"
					+ "\"" + x.getMessage() + "\"";
			StringWriter trace = new StringWriter();
			PrintWriter pw = new PrintWriter(trace);
			x.printStackTrace(pw);
			pw.close();
			message += trace.getBuffer();

			System.err.println(message);
			JOptionPane.showMessageDialog(null, message);
			return "ERROR";
		}
	}

	/**
	 * @return the description of the transaction as we display it. Never null.
	 */
	private Object getTransactionDescription() {
		String desc = getTransaction().getDescription();
		if ( desc == null || 
			 desc.trim().length() == 0 ) {
			return "";
		}
		return desc;
	}

	/**
	 * @return the transaction-number as we display it. Never null.
	 */
	private Object getTransactionNumber() {
		String number = getTransaction().getNumber();
		if ( number == null || 
			 number.trim().length() == 0 ) {
			return "";
		}
		return number;
	}

	public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
		// ::EMPTY
	}

	public String getColumnName(final int columnIndex) {
		return defaultColumnNames[columnIndex]; //TODO: l10n
	}

	public void addTableModelListener(final TableModelListener l) {
		// ::EMPTY
	}

	public void removeTableModelListener(final TableModelListener l) {
		// ::EMPTY
	}

	public boolean isCellEditable(final int aRowIndex, final int aColumnIndex) {
		return false;
	}
}
