import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.text.html.*;

public class HtmlProcessor extends JFrame {

	public static final String APP_NAME = "DIGITAL READER-HTML";
	protected JTextPane m_editor;
	protected StyleSheet m_context;
	protected HTMLDocument m_doc;
	protected HTMLEditorKit m_kit;
	protected SimpleFilter m_htmlFilter;
	protected JToolBar m_toolBar;

	protected JFileChooser m_chooser;
	protected File m_currentFile;

	protected boolean m_textChanged = false;

	public HtmlProcessor() {
		super(APP_NAME);
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				"C:\\Users\\CS_Lab\\Downloads\\Dr.jpg"));
		setSize(650, 400);

		// Make sure we install the editor kit before creating
		// the initial document.
		m_editor = new JTextPane();
		m_kit = new HTMLEditorKit();
		m_editor.setEditorKit(m_kit);

		JScrollPane ps = new JScrollPane(m_editor);
		getContentPane().add(ps, BorderLayout.CENTER);

		JMenuBar menuBar = createMenuBar();
		setJMenuBar(menuBar);

		m_chooser = new JFileChooser();
		m_htmlFilter = new SimpleFilter("html", "HTML Documents");
		m_chooser.setFileFilter(m_htmlFilter);
		try {
			File dir = (new File(".")).getCanonicalFile();
			m_chooser.setCurrentDirectory(dir);
		} catch (IOException ex) {
		}

		newDocument();

		WindowListener wndCloser = new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (!promptToSave())
					return;

			}

			public void windowActivated(WindowEvent e) {
				m_editor.requestFocus();
			}
		};
		addWindowListener(wndCloser);
	}

	protected JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		JMenu mFile = new JMenu("File");
		mFile.setMnemonic('f');

		ImageIcon iconNew = new ImageIcon("New16.gif");
		Action actionNew = new AbstractAction("New", iconNew) {
			public void actionPerformed(ActionEvent e) {
				if (!promptToSave())
					return;
				newDocument();
			}
		};
		JMenuItem item = new JMenuItem(actionNew);
		item.setMnemonic('n');
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				InputEvent.CTRL_MASK));
		mFile.add(item);

		ImageIcon iconOpen = new ImageIcon("Open16.gif");
		Action actionOpen = new AbstractAction("Open...", iconOpen) {
			public void actionPerformed(ActionEvent e) {
				if (!promptToSave())
					return;
				openDocument();
			}
		};
		item = new JMenuItem(actionOpen);
		item.setMnemonic('o');
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				InputEvent.CTRL_MASK));
		mFile.add(item);

		ImageIcon iconSave = new ImageIcon("Save16.gif");
		Action actionSave = new AbstractAction("Save", iconSave) {
			public void actionPerformed(ActionEvent e) {
				saveFile(false);
			}
		};
		item = new JMenuItem(actionSave);
		item.setMnemonic('s');
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				InputEvent.CTRL_MASK));
		mFile.add(item);

		ImageIcon iconSaveAs = new ImageIcon("SaveAs16.gif");
		Action actionSaveAs = new AbstractAction("Save As...", iconSaveAs) {
			public void actionPerformed(ActionEvent e) {
				saveFile(true);
			}
		};
		item = new JMenuItem(actionSaveAs);
		item.setMnemonic('a');
		mFile.add(item);

		mFile.addSeparator();

		Action actionExit = new AbstractAction("Exit") {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		};

		item = mFile.add(actionExit);
		item.setMnemonic('x');
		menuBar.add(mFile);
		JButton btnRtf = new JButton("Main Window");
		btnRtf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = e.getActionCommand();
				if (s == "Main Window") {
					/**********************************************
					 * This is where i am calling the window2 *
					 *********************************************/
					textreader1 tester = new textreader1();
					tester.setVisible(true);
					setVisible(false);
				}
			}
		});
		menuBar.add(btnRtf);
		m_toolBar = new JToolBar();
		JButton bNew = new SmallButton(actionNew, "New document");
		m_toolBar.add(bNew);

		JButton bOpen = new SmallButton(actionOpen, "Open HTML document");
		m_toolBar.add(bOpen);

		JButton bSave = new SmallButton(actionSave, "Save HTML document");
		m_toolBar.add(bSave);

		getContentPane().add(m_toolBar, BorderLayout.NORTH);

		return menuBar;
	}

	protected String getDocumentName() {
		return m_currentFile == null ? "Untitled" : m_currentFile.getName();
	}

	protected void newDocument() {
		m_doc = (HTMLDocument) m_kit.createDefaultDocument();
		m_context = m_doc.getStyleSheet();

		m_editor.setDocument(m_doc);
		m_currentFile = null;
		setTitle(APP_NAME + " [" + getDocumentName() + "]");

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				m_editor.scrollRectToVisible(new Rectangle(0, 0, 1, 1));
				m_doc.addDocumentListener(new UpdateListener());
				m_textChanged = false;
			}
		});
	}

	protected void openDocument() {
		if (m_chooser.showOpenDialog(HtmlProcessor.this) != JFileChooser.APPROVE_OPTION)
			return;
		File f = m_chooser.getSelectedFile();
		if (f == null || !f.isFile())
			return;
		m_currentFile = f;
		setTitle(APP_NAME + " [" + getDocumentName() + "]");

		HtmlProcessor.this.setCursor(Cursor
				.getPredefinedCursor(Cursor.WAIT_CURSOR));
		// Can't do this in thread - Pavel
		try {
			InputStream in = new FileInputStream(m_currentFile);
			m_doc = (HTMLDocument) m_kit.createDefaultDocument();
			m_kit.read(in, m_doc, 0);
			m_context = m_doc.getStyleSheet();
			m_editor.setDocument(m_doc);
			in.close();
		} catch (Exception ex) {
			showError(ex, "Error reading file " + m_currentFile);
		}
		HtmlProcessor.this.setCursor(Cursor
				.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				m_editor.setCaretPosition(1);
				m_editor.scrollRectToVisible(new Rectangle(0, 0, 1, 1));
				m_doc.addDocumentListener(new UpdateListener());
				m_textChanged = false;
			}
		});
	}

	protected boolean saveFile(boolean saveAs) {
		if (!saveAs && !m_textChanged)
			return true;
		if (saveAs || m_currentFile == null) {
			if (m_chooser.showSaveDialog(HtmlProcessor.this) != JFileChooser.APPROVE_OPTION)
				return false;
			File f = m_chooser.getSelectedFile();
			if (f == null)
				return false;
			m_currentFile = f;
			setTitle(APP_NAME + " [" + getDocumentName() + "]");
		}

		HtmlProcessor.this.setCursor(Cursor
				.getPredefinedCursor(Cursor.WAIT_CURSOR));
		// Can't do this in thread - Pavel
		try {
			OutputStream out = new FileOutputStream(m_currentFile);
			m_kit.write(out, m_doc, 0, m_doc.getLength());
			out.close();
			m_textChanged = false;
		} catch (Exception ex) {
			showError(ex, "Error saving file " + m_currentFile);
		}
		HtmlProcessor.this.setCursor(Cursor
				.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		return true;
	}

	protected boolean promptToSave() {
		if (!m_textChanged)
			return true;
		int result = JOptionPane.showConfirmDialog(this, "Save changes to "
				+ getDocumentName() + "?", APP_NAME,
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.INFORMATION_MESSAGE);
		switch (result) {
		case JOptionPane.YES_OPTION:
			if (!saveFile(false))
				return false;
			return true;
		case JOptionPane.NO_OPTION:
			return true;
		case JOptionPane.CANCEL_OPTION:
			return false;
		}
		return true;
	}

	public void showError(Exception ex, String message) {
		ex.printStackTrace();
		JOptionPane.showMessageDialog(this, message, APP_NAME,
				JOptionPane.WARNING_MESSAGE);
	}

	public static void main(String argv[]) {
		UIManager.LookAndFeelInfo plafinfo[] = UIManager
				.getInstalledLookAndFeels();
		boolean nimbusfound = false;
		int nimbusindex = 0;

		for (int look = 0; look < plafinfo.length; look++) {
			if (plafinfo[look].getClassName().toLowerCase().contains("nimbus")) {
				nimbusfound = true;
				nimbusindex = look;
			}
		}

		try {

			if (nimbusfound) {
				UIManager.setLookAndFeel(plafinfo[nimbusindex].getClassName());
			} else

				UIManager.setLookAndFeel(UIManager
						.getCrossPlatformLookAndFeelClassName());

		} catch (Exception e) {
		}
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);

		HtmlProcessor frame = new HtmlProcessor();
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setVisible(true);
	}

	class UpdateListener implements DocumentListener {

		public void insertUpdate(DocumentEvent e) {
			m_textChanged = true;
		}

		public void removeUpdate(DocumentEvent e) {
			m_textChanged = true;
		}

		public void changedUpdate(DocumentEvent e) {
			m_textChanged = true;
		}
	}
}

// Class SmallButton unchanged from chapter 12

class SmallButton extends JButton implements MouseListener {
	protected Border m_raised = new SoftBevelBorder(BevelBorder.RAISED);
	protected Border m_lowered = new SoftBevelBorder(BevelBorder.LOWERED);
	protected Border m_inactive = new EmptyBorder(3, 3, 3, 3);
	protected Border m_border = m_inactive;
	protected Insets m_ins = new Insets(4, 4, 4, 4);

	public SmallButton(Action act, String tip) {
		super((Icon) act.getValue(Action.SMALL_ICON));
		setBorder(m_inactive);
		setMargin(m_ins);
		setToolTipText(tip);
		setRequestFocusEnabled(false);
		addActionListener(act);
		addMouseListener(this);
	}

	public float getAlignmentY() {
		return 0.5f;
	}

	// Overridden for 1.4 bug fix
	public Border getBorder() {
		return m_border;
	}

	// Overridden for 1.4 bug fix
	public Insets getInsets() {
		return m_ins;
	}

	public void mousePressed(MouseEvent e) {
		m_border = m_lowered;
		setBorder(m_lowered);
	}

	public void mouseReleased(MouseEvent e) {
		m_border = m_inactive;
		setBorder(m_inactive);
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
		m_border = m_raised;
		setBorder(m_raised);
	}

	public void mouseExited(MouseEvent e) {
		m_border = m_inactive;
		setBorder(m_inactive);
	}
}

// Class SimpleFilter unchanged from chapter 14

class SimpleFilter extends javax.swing.filechooser.FileFilter {
	private String m_description = null;
	private String m_extension = null;

	public SimpleFilter(String extension, String description) {
		m_description = description;
		m_extension = "." + extension.toLowerCase();
	}

	public String getDescription() {
		return m_description;
	}

	public boolean accept(File f) {
		if (f == null)
			return false;
		if (f.isDirectory())
			return true;
		return f.getName().toLowerCase().endsWith(m_extension);
	}
}
