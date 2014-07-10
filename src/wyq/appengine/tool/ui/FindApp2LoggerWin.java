package wyq.appengine.tool.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import wyq.appengine.FAppLogger;
import wyq.appengine.component.file.TextFile;
import wyq.swing.ui.util.LongRunDialog;

public class FindApp2LoggerWin {

	private JFrame frmLogger;
	private JTextArea textAreaLog;
	private JScrollPane scrollPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FindApp2LoggerWin window = new FindApp2LoggerWin();
					window.frmLogger.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public FindApp2LoggerWin() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmLogger = new JFrame();
		frmLogger.setTitle("Logger");
		frmLogger.setBounds(100, 100, 450, 300);

		JMenuBar menuBar = new JMenuBar();
		frmLogger.setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		final JFileChooser fileChooser = new JFileChooser();

		JMenuItem mntmSave = new JMenuItem("Save...");
		mntmSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// save output
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
				int saveResult = fileChooser.showSaveDialog(frmLogger);

				final File saveFile = fileChooser.getSelectedFile();
				if (JFileChooser.APPROVE_OPTION != saveResult
						|| saveFile == null) {
					return;
				}

				LongRunDialog.run(frmLogger, new Runnable() {

					@Override
					public void run() {
						try {
							TextFile txtFile = new TextFile(saveFile);
							txtFile.writeAll(textAreaLog.getText(), false);
							txtFile.close();
						} catch (Exception e) {
							e.printStackTrace();
							JOptionPane.showMessageDialog(frmLogger, e,
									"Error", JOptionPane.ERROR_MESSAGE);
						}
					}
				});

			}
		});
		mnFile.add(mntmSave);

		JMenu mnSettings = new JMenu("Settings");
		menuBar.add(mnSettings);

		final JCheckBoxMenuItem chckbxmntmAutoScroll = new JCheckBoxMenuItem(
				"Auto Scroll");
		chckbxmntmAutoScroll.setSelected(true);
		mnSettings.add(chckbxmntmAutoScroll);

		JPanel panel = new JPanel();
		frmLogger.getContentPane().add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout(0, 0));

		progressBar = new JProgressBar();
		panel.add(progressBar);
		progressBar.setVisible(false);

		scrollPane = new JScrollPane();
		frmLogger.getContentPane().add(scrollPane, BorderLayout.CENTER);

		textAreaLog = new JTextArea();
		textAreaLog.setEditable(false);
		scrollPane.setViewportView(textAreaLog);

		autoscroll = chckbxmntmAutoScroll.isSelected();
		chckbxmntmAutoScroll.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				autoscroll = chckbxmntmAutoScroll.isSelected();
			}
		});
	}

	private boolean autoscroll = false;
	private FindApp2Logger logger = new FindApp2Logger();
	private static final String NEW_LINE = System.getProperty("line.separator");
	private JProgressBar progressBar;

	public class FindApp2Logger implements FAppLogger {

		@Override
		public synchronized void log(Object o) {
			textAreaLog.append(String.valueOf(o));
			textAreaLog.append(NEW_LINE);
			if (autoscroll) {
				JScrollBar verticalScrollBar = scrollPane
						.getVerticalScrollBar();
				verticalScrollBar.setValue(verticalScrollBar.getMaximum());
			}
		}

	}

	public FindApp2Logger getLogger() {
		return logger;
	}

	public void setVisible(boolean show) {
		frmLogger.setVisible(show);
	}

}
