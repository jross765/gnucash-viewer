package org.gnucash.jgnucash.models;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Set;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.write.GnuCashWritableTransactionSplit;
import org.gnucash.viewer.models.GnuCashSimpleTransactionSplitsTableModel;

/**
 * Version  of GnuCashSimpleTransactionSplitsTableModel that reacts to
 * updates
 */
public class GnuCashTransactionSplitsTableModel extends GnuCashSimpleTransactionSplitsTableModel {
	//TODO: allow editing by the user of transaction-descriptions+dates and split-values for transactions with exactly 2 splits.

	/**
	 * Listener that we attach to the account and the displayed splits
	 * to be informed about changes.
	 */
	private final MyPropertyChangeListener myPropertyChangeListener = new MyPropertyChangeListener();

	/**
	 * @param aList splits to display
	 */
	public GnuCashTransactionSplitsTableModel(final List<? extends GnuCashTransactionSplit> aList) {
		super(aList);

		for (GnuCashTransactionSplit split : aList) {
			if (split instanceof GnuCashWritableTransactionSplit) {
				GnuCashWritableTransactionSplit ws = (GnuCashWritableTransactionSplit) split;
				// ::TODO
				// ws.addPropertyChangeListener(myPropertyChangeListener);
			}
		}
	}

	/**
	 * Empty  model.
	 */
	public GnuCashTransactionSplitsTableModel() {
		super();
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
			TableModelEvent event = new TableModelEvent(GnuCashTransactionSplitsTableModel.this);
			for (TableModelListener tableModelListener : listeners) {
				tableModelListener.tableChanged(event);
			}
		}
	}

}
