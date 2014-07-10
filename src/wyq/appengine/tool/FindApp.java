package wyq.appengine.tool;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import wyq.appengine.FAppLogger;
import wyq.appengine.component.file.TextFile;
import wyq.appengine.tool.MuliThreadFileFinder.Result;

public class FindApp implements MuliThreadFileFinder.AsynchronizedFileHandler {

	TextFile output;
	String searchPattern;
	int forwardLength = 10;
	int previousLength = 10;
	boolean debugDetail = false;
	boolean isTrim = true;
	boolean doubleQuoted = true;
	boolean fullFileName = false;
	boolean onlyPrintMatchFile = true;
	boolean useLineExactPatternMatch = true;

	public static void main(String[] args) throws Exception {

		// set look and feel
		UIManager
				.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");

		FindApp app = new FindApp();
		StringBuilder confirmMsg = new StringBuilder();

		setProperty(app, confirmMsg);

		// search pattern
		app.searchPattern = JOptionPane.showInputDialog(null,
				"input search pattern (Regex)", "input",
				JOptionPane.QUESTION_MESSAGE);
		if (app.searchPattern == null) {
			System.exit(-1);
		}
		confirmMsg.append("searchPattern:" + app.searchPattern);
		confirmMsg.append(TextFile.LINE_SEP);
		log("searchPattern:" + app.searchPattern);

		// file filter pattern
		final String fileMatchPattern = JOptionPane.showInputDialog(null,
				"input file match pattern (Regex)", "input",
				JOptionPane.QUESTION_MESSAGE);
		if (fileMatchPattern == null) {
			System.exit(-1);
		}
		confirmMsg.append("fileMatchPattern:" + fileMatchPattern);
		confirmMsg.append(TextFile.LINE_SEP);
		log("fileMatchPattern:" + fileMatchPattern);
		FileFilter fileFilter = app.new FindAppFileFilter(fileMatchPattern);

		// save output
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int saveResult = fileChooser.showSaveDialog(null);
		File saveFile = fileChooser.getSelectedFile();
		if (JFileChooser.APPROVE_OPTION != saveResult || saveFile == null) {
			System.exit(-1);
		}
		if (!saveFile.exists()) {
			saveFile.getParentFile().mkdirs();
			saveFile.createNewFile();
		}
		app.output = new TextFile(saveFile);
		confirmMsg.append("output:" + app.output);
		confirmMsg.append(TextFile.LINE_SEP);
		log("output:" + app.output);

		// choose dir
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int openResult = fileChooser.showOpenDialog(null);
		File openDir = fileChooser.getSelectedFile();
		if (JFileChooser.APPROVE_OPTION != openResult || openDir == null
				|| !openDir.exists()) {
			System.exit(-1);
		}
		confirmMsg.append("baseDir:" + openDir);
		confirmMsg.append(TextFile.LINE_SEP);
		log("baseDir:" + openDir);

		// confirm
		confirmMsg.append("Confirm? Press OK to start.");
		int confirm = JOptionPane.showConfirmDialog(null,
				confirmMsg.toString(), "confirm", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.WARNING_MESSAGE);
		if (confirm != JOptionPane.OK_OPTION) {
			System.exit(-1);
		}

		// start
		log("start process...");
		long start = System.currentTimeMillis();
		Result result = MuliThreadFileFinder.search(openDir, fileFilter, app);

		// wait for complete
		result.waitTermination();

		// done
		app.close();
		long timeused = System.currentTimeMillis() - start;
		log(app.matchFileCount + " file(s) matched.");
		log(app.scanFileCount + " file(s) scaned.");
		log("process finished. " + timeused + " ms used.");
		JOptionPane.showMessageDialog(null, "Finished. " + timeused
				+ " ms used.", "done", JOptionPane.WARNING_MESSAGE);

	}

	public static void setProperty(FindApp app, StringBuilder confirmMsg) {
		// set forward & previous length
		int propFwdLen = getIntSystemProp("findApp.forwardLength");
		if (propFwdLen > -1) {
			app.forwardLength = propFwdLen;
			confirmMsg.append("forwardLength:" + app.forwardLength);
			confirmMsg.append(TextFile.LINE_SEP);
			log("forwardLength:" + app.forwardLength);
		}
		int propPrevLen = getIntSystemProp("findApp.previousLength");
		if (propPrevLen > -1) {
			app.previousLength = propPrevLen;
			confirmMsg.append("previousLength:" + app.previousLength);
			confirmMsg.append(TextFile.LINE_SEP);
			log("previousLength:" + app.previousLength);
		}
		// log detail
		boolean propLogDetail = getBooleanSystemProp("findApp.debugDetail",
				app.debugDetail);
		if (propLogDetail != app.debugDetail) {
			app.debugDetail = propLogDetail;
			confirmMsg.append("debugDetail:" + app.debugDetail);
			confirmMsg.append(TextFile.LINE_SEP);
			log("debugDetail:" + app.debugDetail);
		}
		// trim
		boolean propIsTrim = getBooleanSystemProp("findApp.isTrim", app.isTrim);
		if (propIsTrim != app.isTrim) {
			app.isTrim = propIsTrim;
			confirmMsg.append("isTrim:" + app.isTrim);
			confirmMsg.append(TextFile.LINE_SEP);
			log("isTrim:" + app.isTrim);
		}
		// doubleQuoted
		boolean propDoubQuot = getBooleanSystemProp("findApp.doubleQuoted",
				app.doubleQuoted);
		if (propDoubQuot != app.doubleQuoted) {
			app.doubleQuoted = propDoubQuot;
			confirmMsg.append("doubleQuoted:" + app.doubleQuoted);
			confirmMsg.append(TextFile.LINE_SEP);
			log("doubleQuoted:" + app.doubleQuoted);
		}
		// fullFileName
		boolean propFullFileName = getBooleanSystemProp("findApp.fullFileName",
				app.fullFileName);
		if (propFullFileName != app.fullFileName) {
			app.fullFileName = propFullFileName;
			confirmMsg.append("fullFileName:" + app.fullFileName);
			confirmMsg.append(TextFile.LINE_SEP);
			log("fullFileName:" + app.fullFileName);
		}
		// onlyPrintMatchFile
		boolean onlyPrintMatchFile = getBooleanSystemProp(
				"findApp.onlyPrintMatchFile", app.onlyPrintMatchFile);
		if (onlyPrintMatchFile != app.onlyPrintMatchFile) {
			app.onlyPrintMatchFile = onlyPrintMatchFile;
			confirmMsg.append("onlyPrintMatchFile:" + app.onlyPrintMatchFile);
			confirmMsg.append(TextFile.LINE_SEP);
			log("onlyPrintMatchFile:" + app.onlyPrintMatchFile);
		}
		// useLineExactPatternMatch
		boolean useLineExactPatternMatch = getBooleanSystemProp(
				"findApp.useLineExactPatternMatch",
				app.useLineExactPatternMatch);
		if (useLineExactPatternMatch != app.useLineExactPatternMatch) {
			app.useLineExactPatternMatch = useLineExactPatternMatch;
			confirmMsg.append("useLineExactPatternMatch:"
					+ app.useLineExactPatternMatch);
			confirmMsg.append(TextFile.LINE_SEP);
			log("useLineExactPatternMatch:" + app.useLineExactPatternMatch);
		}

	}

	static FAppLogger logger = new FAppLogger() {

		@Override
		public void log(Object o) {
			System.out.println(o);
		}

	};

	public static void log(Object o) {
		logger.log(o);
	}

	private static int getIntSystemProp(String key) {
		String prop = System.getProperty(key);
		int result = -1;
		if (prop != null) {
			try {
				result = Integer.parseInt(prop);
			} catch (Exception e) {
				// ignore
				e.printStackTrace();
			}
		}
		return result;
	}

	private static boolean getBooleanSystemProp(String key, boolean def) {
		String prop = System.getProperty(key);
		boolean result = def;
		if (prop != null) {
			try {
				result = Boolean.parseBoolean(prop);
			} catch (Exception e) {
				// ignore
				e.printStackTrace();
			}
		}
		return result;
	}

	private int scanFileCount = 0;
	private int matchFileCount = 0;

	private void scanFile() {
		scanFileCount++;
	}

	private void matchFile() {
		matchFileCount++;
	}

	@Override
	public void handle(File f) {
		TextFile javaFile = new TextFile(f);
		try {

			if (debugDetail) {
				log("handleFile:" + f);
			}
			String line = null;
			StringBuilder out = new StringBuilder();
			List<String> previousLines = new ArrayList<String>();

			Pattern regexPattern = Pattern.compile(searchPattern);

			while ((line = javaFile.readLine()) != null) {

				if (isLineMatch(line, regexPattern)) {
					if (debugDetail) {
						log("file[" + f + "] hit on line [" + line + "]");
					}

					if (fullFileName) {
						out.append(javaFile.getAbsolutePath());
					} else {
						out.append(javaFile.getName());
					}
					out.append("\t");

					// start
					out.append("\"");
					// previous
					for (String previousLine : previousLines) {
						out.append(previousLine);
						out.append(TextFile.LINE_SEP);
					}
					previousLines.clear();

					// current match line
					line = lineFilter(line);
					out.append(line);

					// forward
					line = javaFile.readLine();
					for (int i = 0; i < forwardLength && line != null; i++, line = javaFile
							.readLine()) {
						line = lineFilter(line);
						if (line.length() > 0) {
							out.append(line);
							out.append(TextFile.LINE_SEP);
						}
					}

					// end
					out.append("\"");
					out.append(TextFile.LINE_SEP);
				} else {
					while (previousLines.size() > previousLength) {
						previousLines.remove(0);
					}
					line = lineFilter(line);
					if (line.length() > 0) {
						previousLines.add(line);
					}
				}
			}
			if (out.length() > 0) {
				synchronized (output) {
					output.write(out.toString());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				javaFile.close();
			} catch (IOException e) {
				// ignore
				e.printStackTrace();
			}
		}
	}

	protected boolean isLineMatch(String line, Pattern regexPattern) {
		Matcher matcher = regexPattern.matcher(line);
		if (useLineExactPatternMatch) {
			return matcher.matches();
		} else {
			return matcher.find();
		}
	}

	protected String lineFilter(String line) {
		String filtered = line;
		if (isTrim) {
			filtered = filtered.trim();
		}
		if (doubleQuoted) {
			filtered = filtered.replaceAll("\"", "\"\"");
		}
		return filtered;
	}

	public void close() throws Exception {
		output.flush();
		output.close();
	}

	public class FindAppFileFilter implements FileFilter {

		private String fileMatchPattern = null;

		public FindAppFileFilter(String fileMatchPattern) {
			this.fileMatchPattern = fileMatchPattern;
		}

		@Override
		public boolean accept(File pathname) {
			FindApp.this.scanFile();
			if (fileMatchPattern == null || fileMatchPattern.length() == 0) {
				FindApp.this.matchFile();
				return true;
			}
			boolean isMatched = pathname.getName().matches(fileMatchPattern);
			if (!FindApp.this.onlyPrintMatchFile
					|| (FindApp.this.onlyPrintMatchFile && isMatched)) {
				log("matches(" + fileMatchPattern + "):" + isMatched
						+ "\tfile:" + pathname);
			}
			if (isMatched) {
				FindApp.this.matchFile();
			}
			return pathname.isFile() && isMatched;
		}
	}

	public TextFile getOutput() {
		return output;
	}

	public void setOutput(TextFile output) {
		this.output = output;
	}

	public String getSearchPattern() {
		return searchPattern;
	}

	public void setSearchPattern(String searchPattern) {
		this.searchPattern = searchPattern;
	}

	public int getForwardLength() {
		return forwardLength;
	}

	public void setForwardLength(int forwardLength) {
		this.forwardLength = forwardLength;
	}

	public int getPreviousLength() {
		return previousLength;
	}

	public void setPreviousLength(int previousLength) {
		this.previousLength = previousLength;
	}

	public boolean isDebugDetail() {
		return debugDetail;
	}

	public void setDebugDetail(boolean debugDetail) {
		this.debugDetail = debugDetail;
	}

	public boolean isTrim() {
		return isTrim;
	}

	public void setTrim(boolean isTrim) {
		this.isTrim = isTrim;
	}

	public boolean isDoubleQuoted() {
		return doubleQuoted;
	}

	public void setDoubleQuoted(boolean doubleQuoted) {
		this.doubleQuoted = doubleQuoted;
	}

	public boolean isFullFileName() {
		return fullFileName;
	}

	public void setFullFileName(boolean fullFileName) {
		this.fullFileName = fullFileName;
	}

	public boolean isOnlyPrintMatchFile() {
		return onlyPrintMatchFile;
	}

	public void setOnlyPrintMatchFile(boolean onlyPrintMatchFile) {
		this.onlyPrintMatchFile = onlyPrintMatchFile;
	}

	public static FAppLogger getLogger() {
		return logger;
	}

	public static void setLogger(FAppLogger logger) {
		FindApp.logger = logger;
	}

	public boolean isUseLineExactPatternMatch() {
		return useLineExactPatternMatch;
	}

	public void setUseLineExactPatternMatch(boolean useLineExactPatternMatch) {
		this.useLineExactPatternMatch = useLineExactPatternMatch;
	}

	public int getScanFileCount() {
		return scanFileCount;
	}

	public int getMatchFileCount() {
		return matchFileCount;
	}

}
