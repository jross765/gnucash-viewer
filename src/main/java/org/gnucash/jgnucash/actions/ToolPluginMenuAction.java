package org.gnucash.jgnucash.actions;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import org.gnucash.api.write.GnuCashWritableAccount;
import org.gnucash.api.write.GnuCashWritableFile;
import org.gnucash.jgnucash.JGnuCash;
import org.gnucash.jgnucash.plugin.ToolPlugin;
import org.java.plugin.registry.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The action-listeners we use for the ImportMenu.
 */
public final class ToolPluginMenuAction implements ActionListener {
	/**
	 * Our logger for debug- and error-output.
	 */
	static final Logger LOGGER = LoggerFactory.getLogger(ToolPluginMenuAction.class);

	/**
	 * Our JGnuCash.java.
	 * @see ToolPluginMenuAction
	 */
	private final JGnuCash myJGnuCashEditor;

	/**
	 * @param aPlugin The import-plugin.
	 * @param aPluginName The name of the plugin
	 * @param aGnuCash TODO
	 */
	public ToolPluginMenuAction(final JGnuCash aGnuCash, final Extension aPlugin, final String aPluginName) {
		super();
		myJGnuCashEditor = aGnuCash;
		ext = aPlugin;
		pluginName = aPluginName;
	}

	/**
	 * The import-plugin.
	 */
	private final Extension ext;

	/**
	 * The name of the plugin.
	 */
	private final String pluginName;

	@Override
	public void actionPerformed(final ActionEvent e) {
		try {
			GnuCashWritableFile wModel = myJGnuCashEditor.getWritableModel();
			if (wModel == null) {
				JOptionPane.showMessageDialog(myJGnuCashEditor, "No open file.",
						"Please open a gnucash-file first!",
						JOptionPane.WARNING_MESSAGE);
				return;
			}

			// Activate plug-in that declares extension.
			myJGnuCashEditor.getPluginManager().activatePlugin(ext.getDeclaringPluginDescriptor().getId());
			// Get plug-in class loader.
			ClassLoader classLoader = myJGnuCashEditor.getPluginManager().getPluginClassLoader(
					ext.getDeclaringPluginDescriptor());
			// Load Tool class.
			Class toolCls = classLoader.loadClass(ext.getParameter("class").valueAsString());
			// Create Tool instance.
			Object o = toolCls.newInstance();
			if (!(o instanceof ToolPlugin)) {
				LOGGER.error("Plugin '" + pluginName + "' does not implement ToolPlugin-interface.");
				JOptionPane.showMessageDialog(myJGnuCashEditor, "Error",
						"Plugin '" + pluginName + "' does not implement ToolPlugin-interface.",
						JOptionPane.ERROR_MESSAGE);
				return;

			}
			ToolPlugin importer = (ToolPlugin) o;
			try {
				myJGnuCashEditor.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				GnuCashWritableAccount selectedAccount = (GnuCashWritableAccount) myJGnuCashEditor.getSelectedAccount();
				String message = importer.runTool(wModel, selectedAccount);
				if (message != null && message.length() > 0) {
					JOptionPane.showMessageDialog(myJGnuCashEditor, "Tool OK",
							"The tool-use was a success:\n" + message,
							JOptionPane.INFORMATION_MESSAGE);
				}
			} catch (Exception e1) {
				LOGGER.error("Tool-use via Plugin '" + pluginName + "' failed.", e1);
				JOptionPane.showMessageDialog(myJGnuCashEditor, "Error",
						"Tool-use via Plugin '" + pluginName + "' failed.\n"
								+ "[" + e1.getClass().getName() + "]: " + e1.getMessage(),
						JOptionPane.ERROR_MESSAGE);
			} finally {
				myJGnuCashEditor.setCursor(Cursor.getDefaultCursor());
			}
		} catch (Exception e1) {
			LOGGER.error("Could not activate requested Tool-plugin '" + pluginName + "'.", e1);
			JOptionPane.showMessageDialog(myJGnuCashEditor, "Error",
					"Could not activate requested Tool-plugin '" + pluginName + "'.\n"
							+ "[" + e1.getClass().getName() + "]: " + e1.getMessage(),
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
