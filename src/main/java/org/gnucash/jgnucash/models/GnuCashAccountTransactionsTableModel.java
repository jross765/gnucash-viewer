package org.gnucash.jgnucash.models;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.write.GnuCashWritableAccount;
import org.gnucash.api.write.GnuCashWritableTransactionSplit;
import org.gnucash.viewer.models.GnuCashSimpleAccountTransactionsTableModel;

/**
 * Version  of GnuCashSimpleAccountTransactionsTableModel that reacts to
 * updates
 */
public class GnuCashAccountTransactionsTableModel extends GnuCashSimpleAccountTransactionsTableModel {
	//TODO: allow editing by the user of transaction-descriptions+dates and split-values for transactions with exactly 2 splits.

	/**
	 * Listener that we attach to the account and the displayed splits
	 * to be informed about changes.
	 */
	private final MyPropertyChangeListener myPropertyChangeListener = new MyPropertyChangeListener();
	/**
	 * The splits we have added our listener to.
	 */
	private final Set<GnuCashWritableTransactionSplit> myObservedSplits = new HashSet<GnuCashWritableTransactionSplit>();

	/**
	 * @param anAccount the account whos transactions to display
	 */
	public GnuCashAccountTransactionsTableModel(final GnuCashAccount anAccount) {
		super(anAccount);

		// inform our listeners about changes
		if (anAccount instanceof GnuCashWritableAccount) {
			GnuCashWritableAccount wacc = (GnuCashWritableAccount) anAccount;
			// ::TODO
			// wacc.addPropertyChangeListener("transactionSplits", myPropertyChangeListener);
		}
	}

	/**
	 * Empty  model.
	 */
	public GnuCashAccountTransactionsTableModel() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {
		GnuCashTransactionSplit split = getTransactionSplit(rowIndex);
		if (split instanceof GnuCashWritableTransactionSplit) {
			GnuCashWritableTransactionSplit ws = (GnuCashWritableTransactionSplit) split;
			if (!myObservedSplits.contains(ws)) {
				// ::TODO
				// ws.addPropertyChangeListener(myPropertyChangeListener);
				myObservedSplits.add(ws);
			}
		}
		return super.getValueAt(rowIndex, columnIndex);
	}

	/**
	 * (c) 2009 by <a href="http://Wolschon.biz>Wolschon Softwaredesign und Beratung</a>.<br/>
	 * Project: jgnucashLib-GPL<br/>
	 * MyPropertyChangeListener<br/>
	 * created: 12.10.2009 <br/>
	 *<br/><br/>
	 * Listener that we attach to the account and the displayed splits
	 * to be informed about changes.
	 * @author  <a href="mailto:Marcus@Wolschon.biz">fox</a>
	 */
	private final class MyPropertyChangeListener implements
			PropertyChangeListener {
		/**
		 * {@inheritDoc}
		 */
		public void propertyChange(final PropertyChangeEvent aEvt) {
			Set<TableModelListener> listeners = getTableModelListeners();
			TableModelEvent event = new TableModelEvent(GnuCashAccountTransactionsTableModel.this);
			for (TableModelListener tableModelListener : listeners) {
				tableModelListener.tableChanged(event);
			}
		}
	}

}
