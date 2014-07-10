package wyq.appengine.tool.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import wyq.appengine.component.file.TextFile;
import wyq.appengine.tool.FindApp;
import wyq.appengine.tool.MuliThreadFileFinder;
import wyq.appengine.tool.MuliThreadFileFinder.Result;

public class FindApp2 {

	private JFrame frmFindapp;
	private JTextField textFieldSrchPattern;
	private JTextField textFieldMatchPattern;
	private JTextField textFieldOutput;
	/**
	 * @wbp.nonvisual location=24,389
	 */
	private final JFileChooser fileChooser = new JFileChooser();
	private JTextField textFieldBaseDir;
	private JSpinner spinnerForwardLength;
	private JSpinner spinnerPreviousLength;
	private JCheckBox chckbxOnlyPrintMatchFile;
	private JCheckBox chckbxFullFileName;
	private JCheckBox chckbxDoubleQuoted;
	private JCheckBox chckbxIsTrim;
	private JCheckBox chckbxDebugDetail;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// set look and feel
					UIManager
							.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
					FindApp2 window = new FindApp2();
					window.frmFindapp.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public FindApp2() {
		initialize();
		initFindApp();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmFindapp = new JFrame();
		frmFindapp.setTitle("FindApp2");
		frmFindapp.setBounds(100, 100, 450, 350);
		frmFindapp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		frmFindapp.getContentPane().add(menuBar, BorderLayout.NORTH);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		mnFile.add(mntmExit);

		JMenu mnWindow = new JMenu("Window");
		menuBar.add(mnWindow);

		final JMenuItem mntmShowLoggerWin = new JMenuItem("Show Logger Win");
		mntmShowLoggerWin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				loggerWin.setVisible(true);
			}
		});

		mnWindow.add(mntmShowLoggerWin);

		JSeparator separator = new JSeparator();
		mnWindow.add(separator);

		chckbxmntmShowLoggerAt = new JCheckBoxMenuItem(
				"Show logger at run start");
		chckbxmntmShowLoggerAt.setSelected(true);
		mnWindow.add(chckbxmntmShowLoggerAt);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frmFindapp.getContentPane().add(tabbedPane, BorderLayout.CENTER);

		JPanel panelTab_1 = new JPanel();
		tabbedPane.addTab("Settings", null, panelTab_1, null);
		panelTab_1.setLayout(new MigLayout("", "[][grow][]", "[][][][]"));

		JLabel lblNewLabel = new JLabel("searchPattern");
		panelTab_1.add(lblNewLabel, "cell 0 0,alignx trailing");

		InputVerifier verifier = new NotEmpty();

		textFieldSrchPattern = new JTextField();
		textFieldSrchPattern.setVerifyInputWhenFocusTarget(false);
		panelTab_1.add(textFieldSrchPattern, "cell 1 0,growx");
		textFieldSrchPattern.setColumns(10);
		textFieldSrchPattern.setInputVerifier(verifier);

		JLabel lblNewLabel_1 = new JLabel("fileMatchPattern");
		panelTab_1.add(lblNewLabel_1, "cell 0 1,alignx trailing");

		textFieldMatchPattern = new JTextField();
		textFieldMatchPattern.setVerifyInputWhenFocusTarget(false);
		panelTab_1.add(textFieldMatchPattern, "cell 1 1,growx");
		textFieldMatchPattern.setColumns(10);
		textFieldMatchPattern.setInputVerifier(verifier);

		JLabel lblNewLabel_2 = new JLabel("output");
		panelTab_1.add(lblNewLabel_2, "cell 0 2,alignx trailing");

		textFieldOutput = new JTextField();
		textFieldOutput.setVerifyInputWhenFocusTarget(false);
		panelTab_1.add(textFieldOutput, "cell 1 2,growx");
		textFieldOutput.setColumns(10);
		textFieldOutput.setInputVerifier(verifier);

		JButton btnSaveOutput = new JButton("Save...");
		btnSaveOutput.setVerifyInputWhenFocusTarget(false);
		btnSaveOutput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// save output
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
				String f = textFieldOutput.getText();
				if (f != null && f.length() > 0) {
					fileChooser.setSelectedFile(new File(f));
				}
				int saveResult = fileChooser.showSaveDialog(frmFindapp);

				File saveFile = fileChooser.getSelectedFile();
				if (JFileChooser.APPROVE_OPTION != saveResult
						|| saveFile == null) {
					return;
				}
				textFieldOutput.setText(saveFile.getAbsolutePath());
				app.setOutput(new TextFile(saveFile));
			}
		});
		panelTab_1.add(btnSaveOutput, "cell 2 2");

		JLabel lblNewLabel_3 = new JLabel("baseDir");
		panelTab_1.add(lblNewLabel_3, "cell 0 3,alignx trailing");

		textFieldBaseDir = new JTextField();
		textFieldBaseDir.setVerifyInputWhenFocusTarget(false);
		panelTab_1.add(textFieldBaseDir, "cell 1 3,growx");
		textFieldBaseDir.setColumns(10);
		textFieldBaseDir.setInputVerifier(verifier);

		JButton btnOpenBaseDir = new JButton("Open...");
		btnOpenBaseDir.setVerifyInputWhenFocusTarget(false);
		btnOpenBaseDir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// choose dir
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
				String f = textFieldBaseDir.getText();
				if (f != null && f.length() > 0) {
					fileChooser.setSelectedFile(new File(f));
				}
				int openResult = fileChooser.showOpenDialog(frmFindapp);
				File openDir = fileChooser.getSelectedFile();
				if (JFileChooser.APPROVE_OPTION != openResult
						|| openDir == null || !openDir.exists()) {
					return;
				}
				textFieldBaseDir.setText(openDir.getAbsolutePath());
			}
		});
		panelTab_1.add(btnOpenBaseDir, "cell 2 3");

		JPanel panelTab_2 = new JPanel();
		tabbedPane.addTab("Advance Settins", null, panelTab_2, null);
		panelTab_2.setLayout(new MigLayout("", "[][grow]", "[][][][][][][][]"));

		JLabel lblNewLabel_4 = new JLabel("forwardLength");
		panelTab_2.add(lblNewLabel_4, "cell 0 0,alignx trailing");

		spinnerForwardLength = new JSpinner();
		spinnerForwardLength.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Integer value = (Integer) spinnerForwardLength.getValue();
				app.setForwardLength(value.intValue());
			}
		});
		panelTab_2.add(spinnerForwardLength, "cell 1 0,growx");

		JLabel lblNewLabel_5 = new JLabel("previousLength");
		panelTab_2.add(lblNewLabel_5, "cell 0 1,alignx trailing");

		spinnerPreviousLength = new JSpinner();
		spinnerPreviousLength.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Integer value = (Integer) spinnerPreviousLength.getValue();
				app.setPreviousLength(value.intValue());
			}
		});
		panelTab_2.add(spinnerPreviousLength, "cell 1 1,growx");

		JLabel lblNewLabel_6 = new JLabel("debugDetail");
		panelTab_2.add(lblNewLabel_6, "cell 0 2,alignx trailing");

		chckbxDebugDetail = new JCheckBox("print debug details");
		chckbxDebugDetail.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				app.setDebugDetail(chckbxDebugDetail.isSelected());
			}
		});
		panelTab_2.add(chckbxDebugDetail, "cell 1 2");

		JLabel lblNewLabel_7 = new JLabel("isTrim");
		panelTab_2.add(lblNewLabel_7, "cell 0 3,alignx trailing");

		chckbxIsTrim = new JCheckBox("trim each line");
		chckbxIsTrim.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				app.setTrim(chckbxIsTrim.isSelected());
			}
		});
		panelTab_2.add(chckbxIsTrim, "cell 1 3");

		JLabel lblNewLabel_8 = new JLabel("doubleQuoted");
		panelTab_2.add(lblNewLabel_8, "cell 0 4,alignx trailing");

		chckbxDoubleQuoted = new JCheckBox("use double quotes");
		chckbxDoubleQuoted.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				app.setDoubleQuoted(chckbxDoubleQuoted.isSelected());
			}
		});
		panelTab_2.add(chckbxDoubleQuoted, "cell 1 4");

		JLabel lblNewLabel_9 = new JLabel("fullFileName");
		panelTab_2.add(lblNewLabel_9, "cell 0 5,alignx trailing");

		chckbxFullFileName = new JCheckBox("out put file full path name");
		chckbxFullFileName.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				app.setFullFileName(chckbxFullFileName.isSelected());
			}
		});
		panelTab_2.add(chckbxFullFileName, "cell 1 5");

		JLabel lblNewLabel_10 = new JLabel("onlyPrintMatchFile");
		panelTab_2.add(lblNewLabel_10, "cell 0 6,alignx trailing");

		chckbxOnlyPrintMatchFile = new JCheckBox(
				"only print files that match the pattern");
		chckbxOnlyPrintMatchFile.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				app.setOnlyPrintMatchFile(chckbxOnlyPrintMatchFile.isSelected());
			}
		});
		panelTab_2.add(chckbxOnlyPrintMatchFile, "cell 1 6");

		JLabel lblUselineexactpattermatch = new JLabel(
				"useLineExactPatterMatch");
		panelTab_2.add(lblUselineexactpattermatch, "cell 0 7,alignx trailing");

		chckbxUseLineExactPatterMatch = new JCheckBox(
				"line exactly matches pattern");
		chckbxUseLineExactPatterMatch.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				app.setUseLineExactPatternMatch(chckbxUseLineExactPatterMatch
						.isSelected());
			}
		});
		panelTab_2.add(chckbxUseLineExactPatterMatch, "cell 1 7");

		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		frmFindapp.getContentPane().add(panel, BorderLayout.SOUTH);

		final JButton btnRun = new JButton("Run");
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				StringBuilder confirmMsg = new StringBuilder();

				// search pattern
				if (!verify(textFieldSrchPattern,
						"Please input search pattern.")) {
					return;
				}
				String searchPattern = textFieldSrchPattern.getText();
				app.setSearchPattern(searchPattern);
				confirmMsg.append("searchPattern:" + app.getSearchPattern());
				confirmMsg.append(TextFile.LINE_SEP);
				log("searchPattern:" + app.getSearchPattern());

				// file filter pattern
				if (!verify(textFieldMatchPattern,
						"Please input file match pattern.")) {
					return;
				}
				String fileMatchPattern = textFieldMatchPattern.getText();
				confirmMsg.append("fileMatchPattern:" + fileMatchPattern);
				confirmMsg.append(TextFile.LINE_SEP);
				log("fileMatchPattern:" + fileMatchPattern);
				FileFilter fileFilter = app.new FindAppFileFilter(
						fileMatchPattern);

				// save output
				if (!verify(textFieldOutput, "Please input output file.")) {
					return;
				}
				String txtOutputField = textFieldOutput.getText();
				File saveFile = new File(txtOutputField);
				if (!saveFile.exists()) {
					saveFile.getParentFile().mkdirs();
					try {
						saveFile.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(frmFindapp, e, "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				app.setOutput(new TextFile(saveFile));
				confirmMsg.append("output:" + app.getOutput());
				confirmMsg.append(TextFile.LINE_SEP);
				log("output:" + app.getOutput());

				// choose dir
				if (!verify(textFieldBaseDir, "Please input base dir.")) {
					return;
				}
				String baseDir = textFieldBaseDir.getText();
				confirmMsg.append("baseDir:" + baseDir);
				confirmMsg.append(TextFile.LINE_SEP);
				log("baseDir:" + baseDir);

				// confirm
				confirmMsg.append("Confirm? Press OK to start.");
				int confirm = JOptionPane.showConfirmDialog(null,
						confirmMsg.toString(), "confirm",
						JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.WARNING_MESSAGE);
				if (confirm != JOptionPane.OK_OPTION) {
					return;
				}

				if (chckbxmntmShowLoggerAt.isSelected()) {
					loggerWin.setVisible(true);
				}

				// start
				log("start process...");
				final long start = System.currentTimeMillis();
				final Result result = MuliThreadFileFinder.search(new File(
						baseDir), fileFilter, app);

				btnRun.setEnabled(false);

				exec.execute(new Runnable() {

					@Override
					public void run() {
						// wait for complete
						result.waitTermination();

						// done
						try {
							app.close();
						} catch (Exception e) {
							e.printStackTrace();
							JOptionPane.showMessageDialog(frmFindapp, e,
									"Error", JOptionPane.ERROR_MESSAGE);
						}
						long timeused = System.currentTimeMillis() - start;
						log(app.getMatchFileCount() + " file(s) matched.");
						log(app.getScanFileCount() + " file(s) scaned.");
						log("process finished. " + timeused + " ms used.");
						JOptionPane.showMessageDialog(null, "Finished. "
								+ timeused + " ms used.", "done",
								JOptionPane.WARNING_MESSAGE);

						btnRun.setEnabled(true);
					}
				});

			}
		});
		panel.add(btnRun);
	}

	private Executor exec = Executors.newSingleThreadExecutor();
	private FindApp app = null;
	private String fileMatchPattern = null;
	private JCheckBox chckbxUseLineExactPatterMatch;

	private void initFindApp() {

		FindApp.setLogger(loggerWin.getLogger());

		app = new FindApp();

		StringBuilder confirmMsg = new StringBuilder();
		FindApp.setProperty(app, confirmMsg);

		textFieldSrchPattern.setText(app.getSearchPattern());
		textFieldMatchPattern.setText(fileMatchPattern);
		TextFile output = app.getOutput();
		if (output != null) {
			textFieldOutput.setText(output.getAbsolutePath());
		}

		spinnerForwardLength.setValue(app.getForwardLength());
		spinnerPreviousLength.setValue(app.getPreviousLength());

		chckbxDebugDetail.setSelected(app.isDebugDetail());
		chckbxDoubleQuoted.setSelected(app.isDoubleQuoted());
		chckbxFullFileName.setSelected(app.isFullFileName());
		chckbxIsTrim.setSelected(app.isTrim());
		chckbxOnlyPrintMatchFile.setSelected(app.isOnlyPrintMatchFile());
		chckbxUseLineExactPatterMatch.setSelected(app
				.isUseLineExactPatternMatch());

	}

	private void log(Object o) {
		loggerWin.getLogger().log(o);
	}

	private FindApp2LoggerWin loggerWin = new FindApp2LoggerWin();
	private JCheckBoxMenuItem chckbxmntmShowLoggerAt;

	private void showWarning(String msg) {
		JOptionPane.showMessageDialog(frmFindapp, msg, "Warning",
				JOptionPane.WARNING_MESSAGE);
	}

	private boolean verify(JTextField txtField, String warnMsg) {
		boolean verified = txtField.getInputVerifier().verify(txtField);
		if (!verified) {
			showWarning(warnMsg);
			txtField.requestFocus();
		}
		return verified;
	}

	private class NotEmpty extends InputVerifier {

		@Override
		public boolean verify(JComponent input) {
			JTextField txtField = (JTextField) input;
			String value = txtField.getText();
			boolean vaild = (value != null && value.trim().length() > 0);
			return vaild;
		}

	}
}
