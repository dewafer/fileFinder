package wyq.swing.ui.util;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

public class LongRunDialog<T> extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3376995747565306776L;
	private static final String PROGRESS_BAR_LABEL = "Please wait a moment.";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager
							.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
					final LongRunDialog<String> dialog = new LongRunDialog<String>();
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setCall(new Callable<String>() {

						@Override
						public String call() {
							try {
								Thread.sleep(5000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							return "finished";
						}
					});
					dialog.setFinish(new Finish<String>() {

						@Override
						public void finish(String result) {
							JOptionPane.showMessageDialog(dialog, result);
						}
					});
					dialog.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private LongRunner longRunner = new LongRunner();

	public LongRunDialog() {
		this(null);
	}

	/**
	 * Create the dialog.
	 */
	public LongRunDialog(Window owner) {

		super(owner);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				longRunner.execute();
			}
		});
		// setType(Type.POPUP);
		setUndecorated(true);
		setModalityType(ModalityType.TOOLKIT_MODAL);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 320, 46);
		setLocationRelativeTo(owner);

		JProgressBar progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBar.setIndeterminate(true);
		progressBar.setString(PROGRESS_BAR_LABEL);
		getContentPane().add(progressBar, BorderLayout.CENTER);

	}

	private Callable<T> call;
	private Runnable run;
	private Finish<T> finish;

	class LongRunner extends SwingWorker<T, Object> {

		@Override
		protected T doInBackground() throws Exception {
			if (call != null) {
				return call.call();
			}
			if (run != null) {
				run.run();
			}
			return null;
		}

		@Override
		protected void done() {
			try {
				T result = get();
				if (finish != null) {
					finish.finish(result);
				}
				// } catch (InterruptedException | ExecutionException e) {
				// e.printStackTrace();
				// }
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}

			LongRunDialog.this.setVisible(false);
			LongRunDialog.this.dispose();
		}

	}

	/**
	 * @return the work
	 */
	public Callable<T> getCall() {
		return call;
	}

	/**
	 * @param work
	 *            the work to set
	 */
	public void setCall(Callable<T> work) {
		this.call = work;
	}

	public interface Finish<T> {
		public void finish(T result);
	}

	/**
	 * @return the finish
	 */
	public Finish<T> getFinish() {
		return finish;
	}

	/**
	 * @param finish
	 *            the finish to set
	 */
	public void setFinish(Finish<T> finish) {
		this.finish = finish;
	}

	public static <T> void run(Window owner, Callable<T> work, Finish<T> finish) {
		LongRunDialog<T> dialog = new LongRunDialog<T>(owner);
		dialog.setCall(work);
		dialog.setFinish(finish);
		dialog.setVisible(true);
	}

	public static void run(Window owner, Runnable run) {
		LongRunDialog<Object> dialog = new LongRunDialog<Object>(owner);
		dialog.setRun(run);
		dialog.setVisible(true);
	}

	/**
	 * @return the run
	 */
	public Runnable getRun() {
		return run;
	}

	/**
	 * @param run
	 *            the run to set
	 */
	public void setRun(Runnable run) {
		this.run = run;
	}
}
