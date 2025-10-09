package org.gnucash.viewer.models;



import javax.swing.table.TableModel;

import org.gnucash.api.read.GnuCashTransactionSplit;


/**
 * TableModels implementing this interface contain a list of transactions.
 * They may be all transactions of an account, a search-result or s.t. similar.
 */
public interface GnuCashTransactionsSplitsTableModel extends TableModel {

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
