package grisu.frontend.blender.swing;

import grisu.control.ServiceInterface;
import grisu.frontend.view.swing.GrisuNavigationPanel;
import grisu.frontend.view.swing.jobmonitoring.batch.BatchJobTabbedPane;
import grisu.frontend.view.swing.login.GrisuSwingClient;
import grisu.frontend.view.swing.login.LoginPanel;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;


public class BlenderMainPanel extends JPanel implements GrisuSwingClient {
	private JTabbedPane tabbedPane;
	private BlenderJobCreationPanel blenderJobCreationPanel;
	private BatchJobTabbedPane batchJobTabbedPane;

	private ServiceInterface si;
	private LoginPanel lp;
	private GrisuNavigationPanel grisuNavigationPanel;

	/**
	 * Create the panel.
	 */
	public BlenderMainPanel() {
		setLayout(new BorderLayout());
	}

	private BatchJobTabbedPane getBatchJobTabbedPane() {
		if (batchJobTabbedPane == null) {
			batchJobTabbedPane = new BatchJobTabbedPane(si, "blender");
		}
		return batchJobTabbedPane;
	}

	private BlenderJobCreationPanel getBlenderJobCreationPanel() {
		if (blenderJobCreationPanel == null) {
			blenderJobCreationPanel = new BlenderJobCreationPanel();
			blenderJobCreationPanel.setServiceInterface(si);
		}
		return blenderJobCreationPanel;
	}

	@Override
	public JPanel getRootPanel() {
		return this;
	}

	private JTabbedPane getTabbedPane() {
		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane(SwingConstants.LEFT);
			tabbedPane.addTab("Create job", null, getBlenderJobCreationPanel(),
					null);
			tabbedPane.addTab("Monitor", null, getBatchJobTabbedPane(), null);
		}
		return tabbedPane;
	}

	@Override
	public void setLoginPanel(LoginPanel lp) {
		this.lp = lp;
	}

	@Override
	public void setServiceInterface(ServiceInterface si) {

		this.si = si;
		add(getTabbedPane(), BorderLayout.CENTER);

	}
}
