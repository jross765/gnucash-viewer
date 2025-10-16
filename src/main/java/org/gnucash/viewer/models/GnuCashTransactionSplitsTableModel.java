package org.gnucash.viewer.models;



import javax.swing.table.TableModel;

import org.gnucash.api.read.GnuCashTransactionSplit;


/*
 * Table models implementing this interface contain a list of transactions.
 * They may be all transactions of an account, a search-result or s.t. similar.
 */
public interface GnuCashTransactionSplitsTableModel extends TableModel {

    /**
     * Get the number of transactons.
     * @return an integer >=0
     */
    int getRowCount();

    /**
     * Get the TransactionsSplit at the given index.
     * Throws an exception if the index is invalid.
     * @param rowIndex the split to get
     * @return the split
     */
    GnuCashTransactionSplit getTransactionSplit(final int rowIndex);


}
