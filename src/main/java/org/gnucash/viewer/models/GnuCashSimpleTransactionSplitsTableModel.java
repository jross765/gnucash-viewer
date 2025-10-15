package org.gnucash.viewer.models;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.event.TableModelListener;

import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;

/*
 * A Table model that shows a given list of transaction.
 */
public class GnuCashSimpleTransactionSplitsTableModel implements GnuCashTransactionsSplitsTableModel {
	
	// CAUTION: This enum is on purpose redundant to the one
	// in GnuCashSimpleAccountTransactionsTableModel.
	// The two are, admittedly, *almost* identical, but
	// *not entirely*, and this, in part, is "by chance",
	// so to speak.
	enum TableCols {
		DATE,
		TRANSACTION,
		DESCRIPTION,
		PLUS,
		MINUS
	}

    private final List<? extends GnuCashTransactionSplit> mySplits;


    // The columns we display.
    private final String[] defaultColumnNames = new String[] {
			Messages_GnuCashSimpleAccountTransactionSplitsTableModel.getString("GnuCashSimpleAccountTransactionSplitsTableModel.1"), 
			Messages_GnuCashSimpleAccountTransactionSplitsTableModel.getString("GnuCashSimpleAccountTransactionSplitsTableModel.2"), 
			Messages_GnuCashSimpleAccountTransactionSplitsTableModel.getString("GnuCashSimpleAccountTransactionSplitsTableModel.3"), 
			Messages_GnuCashSimpleAccountTransactionSplitsTableModel.getString("GnuCashSimpleAccountTransactionSplitsTableModel.4"), 
			Messages_GnuCashSimpleAccountTransactionSplitsTableModel.getString("GnuCashSimpleAccountTransactionSplitsTableModel.5")
		};

    /**
     * @param java.util.List<? extends GnuCashTransactionSplit> the splits to display.
     */
    public GnuCashSimpleTransactionSplitsTableModel(final List<? extends GnuCashTransactionSplit> aList) {
        super();
        mySplits = aList;
    }

    /**
     * the Table will be empty.
     *
     */
    public GnuCashSimpleTransactionSplitsTableModel() {
        super();
        mySplits = null;
    }

    /**
     * {@inheritDoc}
     */
    public int getColumnCount() {
        return defaultColumnNames.length;
    }

    /**
     * {@inheritDoc}
     */
    public int getRowCount() {
        List<? extends GnuCashTransactionSplit> transactionSplits = getTransactionSplits();
        if (transactionSplits == null) {
            return 0;
        }
        return transactionSplits.size();
    }

    /**
     * @return the splits that affect this account.
     */
    public List<? extends GnuCashTransactionSplit> getTransactionSplits() {
        if ( mySplits == null ) {
            return new LinkedList<GnuCashTransactionSplit>();
        }
        
        return mySplits;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public Class getColumnClass(final int columnIndex) {
        return String.class;
    }


    // How to format dates.
    public static final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
    
    // How to format currencies.
    private  NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
    
    // How to format currencies.
    public static final NumberFormat defaultCurrencyFormat = NumberFormat.getCurrencyInstance();


    /**
     * Get the TransactionsSplit at the given index.
     * Throws an exception if the index is invalid.
     * @param rowIndex the split to get
     * @return the split
     */
    public GnuCashTransactionSplit getTransactionSplit(final int rowIndex) {
        GnuCashTransactionSplit split = getTransactionSplits().get(rowIndex);
        return split;
    }

    /**
     * {@inheritDoc}
     */
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        try {
            GnuCashTransactionSplit split = getTransactionSplit(rowIndex);

            updateCurrencyFormat(split);

            if ( columnIndex == TableCols.DATE.ordinal() ) {
				return split.getTransaction().getDatePostedFormatted();
            } else if ( columnIndex == TableCols.TRANSACTION.ordinal() ) {
                String desc = split.getTransaction().getDescription();
                if ( desc == null || 
                	 desc.trim().length() == 0 ) {
                    return "";
                }
                return desc;
            } else if ( columnIndex == TableCols.DESCRIPTION.ordinal() ) {
                String desc = split.getDescription();
                if ( desc == null || 
                	 desc.trim().length() == 0 ) {
                    return "";
                }
                return desc;
            } else if ( columnIndex == TableCols.PLUS.ordinal() ) {
            	if ( split.getQuantity().isPositive() ) {
//                  //T O D O: use default-currency here
//                  if (account != null && !account.getCurrencyID().equals("EUR")) {
//                      return split.getValueFormatet();
//                  }
            		return currencyFormat.format(split.getQuantity());
            	} else {
            		return "";
            	}
            } else if ( columnIndex ==  TableCols.MINUS.ordinal() ) {
                if ( ! split.getQuantity().isPositive() ) {
//                    if (account != null && !account.getCurrencyID().equals("EUR")) {
//                        return split.getValueFormatet();
//                    }
                	return currencyFormat.format(split.getQuantity());
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

            final String message2 = message;
            System.err.println(message);
            Runnable runnable = new Runnable() {
                public void run() {
                    JOptionPane.showMessageDialog(null, message2);
                }
            };
            new Thread(runnable).start();
            return "ERROR";
        }
    }

    /**
     * @param split the split whos account to use for the currency
     */
    private void updateCurrencyFormat(final GnuCashTransactionSplit split) {
        currencyFormat = NumberFormat.getNumberInstance();
        try {
            if ( split.getAccount().getCmdtyCurrID().getType() == GCshCmdtyCurrID.Type.CURRENCY ) {
                Currency currency = Currency.getInstance(split.getAccount().getCmdtyCurrID().getCode());
                currencyFormat = NumberFormat.getCurrencyInstance();
                currencyFormat.setCurrency(currency);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
    	// ::EMPTY
    }

    /**
     * {@inheritDoc}
     */
    public String getColumnName(final int columnIndex) {
        return defaultColumnNames[columnIndex];
    }

    /**
     * @see #addTableModelListener(TableModelListener)
     */
    private final Set<TableModelListener> myTableModelListeners = new HashSet<TableModelListener>();


    /**
     * @see #addTableModelListener(TableModelListener)
     * @return the tableModelListeners
     */
    protected Set<TableModelListener> getTableModelListeners() {
        return myTableModelListeners;
    }

    /**
     *
     * @see javax.swing.table.TableModel#addTableModelListener(javax.swing.event.TableModelListener)
     */
    public void addTableModelListener(final TableModelListener l) {
        myTableModelListeners.add(l);
    }

    /**
     *
     * @see javax.swing.table.TableModel#removeTableModelListener(javax.swing.event.TableModelListener)
     */
    public void removeTableModelListener(final TableModelListener l) {
        myTableModelListeners.remove(l);
    }

}
