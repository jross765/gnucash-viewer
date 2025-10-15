package org.gnucash.viewer.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * A Tree model representing the accounts in a GnuCash file.
 */
public class GnuCashAccountsTreeModel implements TreeModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashAccountsTreeModel.class);

    public GnuCashAccountsTreeModel(final GnuCashFile file) {
        super();
        setFile(file);
    }

    // The tree-root
    private GnuCashAccountTreeRootEntry rootEntry;

    public static class GnuCashAccountTreeRootEntry extends GnuCashAccountTreeEntry {

        // where we get our data from.
        private final GnuCashFile file;

        public GnuCashAccountTreeRootEntry(final GnuCashFile aFile) {
            super(getRootAccount(aFile));
            file = aFile;
        }

        private static GnuCashAccount getRootAccount(final GnuCashFile aFile) {
            if (aFile == null) {
                throw new IllegalArgumentException("argument <aFile> is null");
            }
            
            GnuCashAccount root = aFile.getRootAccount();
            if (root == null) {
                throw new IllegalArgumentException("root-account is null");
            }
            
            return root;

        }

        public GnuCashFile getFile() {
            return file;
        }

        @Override
        public String toString() {
            return "";
        }

        @Override
        public List<? extends GnuCashAccount> getChildAccounts() {
        	ArrayList result = new ArrayList<GnuCashAccount>();
        	result.add( file.getRootAccount() );
            return result;
        }
    }

    public static class GnuCashAccountTreeEntry {

        // The account we represent
        private final GnuCashAccount myAccount;

        public GnuCashAccountTreeEntry(final GnuCashAccount anAccount) {
            super();
            
            if (anAccount == null) {
                throw new IllegalArgumentException("argument <anAccount> is null");
            }
            
            myAccount = anAccount;
        }

        public GnuCashAccount getAccount() {
            return myAccount;
        }

        @Override
        public String toString() {
            String hidden = getAccount().getUserDefinedAttribute("hidden");
            if (hidden != null && hidden.equalsIgnoreCase("true")) {
                return "[hidden]" + getAccount().getName();
            }
            return getAccount().getName();
        }

        // The tree-nodes below us
        private volatile List<GnuCashAccountTreeEntry> childTreeNodes = null;

        public List<GnuCashAccountTreeEntry> getChildTreeNodes() {
            if ( childTreeNodes == null ) {
                Collection<? extends GnuCashAccount> c = getChildAccounts();
                childTreeNodes = new ArrayList<GnuCashAccountTreeEntry>(c.size());
                for ( GnuCashAccount gnucashAccount : c ) {
                    GnuCashAccount subaccount = gnucashAccount;
                    childTreeNodes.add(new GnuCashAccountTreeEntry(subaccount));
                }
            }

            return childTreeNodes;
        }

        public Collection<? extends GnuCashAccount> getChildAccounts() {
            return myAccount.getChildren();
        }
    }

    public Object getRoot() {
        return rootEntry;
    }

    public int getChildCount(final Object parent) {
        return ((GnuCashAccountTreeEntry) parent).getChildTreeNodes().size();
    }

    public boolean isLeaf(final Object node) {
        return getChildCount(node) == 0;
    }

    private final Set<TreeModelListener> listeners = new HashSet<TreeModelListener>();

    public void addTreeModelListener(final TreeModelListener l) {
        listeners.add(l);

    }

    public void removeTreeModelListener(final TreeModelListener l) {
        listeners.remove(l);
    }

    public Object getChild(final Object parent, final int index) {
        return ((GnuCashAccountTreeEntry) parent).getChildTreeNodes().get(index);
    }

    public int getIndexOfChild(final Object parent, final Object child) {
        return ((GnuCashAccountTreeEntry) parent).getChildTreeNodes().indexOf(child);
    }

    public void valueForPathChanged(final TreePath path, final Object newValue) {
        // TODO unsupported
    }

    public GnuCashFile getFile() {
        return rootEntry.getFile();
    }

    public void setFile(final GnuCashFile file) {
        if (file == null) {
            throw new IllegalArgumentException("argument <file> is null");
        }
        
        rootEntry = new GnuCashAccountTreeRootEntry(file);

        fireTreeStructureChanged(getPathToRoot());
    }

    protected TreePath getPathToRoot() {
    	return new TreePath(getRoot());
    }

    protected void fireTreeStructureChanged(final TreePath path) {
		TreeModelEvent evt = new TreeModelEvent( this, path );

		for ( TreeModelListener listener : listeners ) {
			listener.treeStructureChanged( evt );
		}
    }

}
