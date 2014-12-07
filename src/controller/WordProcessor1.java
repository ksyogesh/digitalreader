package controller;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.text.rtf.*;

import view.digireader;

public class WordProcessor1 extends JInternalFrame {
	protected JTextPane m_monitor;
	protected StyleContext m_context;
	protected DefaultStyledDocument m_doc;
	protected RTFEditorKit m_kit;
	protected JFileChooser m_chooser;
	protected SimpleFilter m_rtfFilter;
	protected JToolBar m_toolBar;

	public WordProcessor1() {
		super("DIGITAL READER-RTF");
		setSize(600, 400);

		// Make sure we install the editor kit before creating
		// the initial document.
		m_monitor = new JTextPane();
		m_kit = new RTFEditorKit();
		m_monitor.setEditorKit(m_kit);
		m_context = new StyleContext();
		m_doc = new DefaultStyledDocument(m_context);
		m_monitor.setDocument(m_doc);

		JScrollPane ps = new JScrollPane(m_monitor);
		getContentPane().add(ps, BorderLayout.CENTER);

		JMenuBar menuBar = createMenuBar();
		setJMenuBar(menuBar);

		m_chooser = new JFileChooser();
		m_chooser.setCurrentDirectory(new File("."));
		m_rtfFilter = new SimpleFilter("rtf", "RTF Documents");
		m_chooser.setFileFilter(m_rtfFilter);

		setVisible(true);
	}

	protected JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		JMenu mFile = new JMenu("File");
		mFile.setMnemonic('f');

		ImageIcon iconNew = new ImageIcon("address-book-new.png");
		Action actionNew = new AbstractAction("New", iconNew) {
			public void actionPerformed(ActionEvent e) {
				m_doc = new DefaultStyledDocument(m_context);
				m_monitor.setDocument(m_doc);
			}
		};
		JMenuItem item = mFile.add(actionNew);
		item.setMnemonic('n');

		ImageIcon iconOpen = new ImageIcon("document-open-folder.png");
		Action actionOpen = new AbstractAction("Open...", iconOpen) {
			public void actionPerformed(ActionEvent e) {
				WordProcessor1.this.setCursor(Cursor
						.getPredefinedCursor(Cursor.WAIT_CURSOR));
				Thread runner = new Thread() {
					public void run() {
						if (m_chooser.showOpenDialog(WordProcessor1.this) != JFileChooser.APPROVE_OPTION)
							return;
						WordProcessor1.this.repaint();
						File fChoosen = m_chooser.getSelectedFile();

						// Recall that text component read/write operations are
						// thread safe. Its ok to do this in a separate thread.
						try {
							InputStream in = new FileInputStream(fChoosen);
							m_doc = new DefaultStyledDocument(m_context);
							m_kit.read(in, m_doc, 0);
							m_monitor.setDocument(m_doc);
							in.close();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						WordProcessor1.this.setCursor(Cursor
								.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
				};
				runner.start();
			}
		};
		item = mFile.add(actionOpen);
		item.setMnemonic('o');

		ImageIcon iconSave = new ImageIcon("document-save.png");
		Action actionSave = new AbstractAction("Save...", iconSave) {
			public void actionPerformed(ActionEvent e) {
				WordProcessor1.this.setCursor(Cursor
						.getPredefinedCursor(Cursor.WAIT_CURSOR));
				Thread runner = new Thread() {
					public void run() {
						if (m_chooser.showSaveDialog(WordProcessor1.this) != JFileChooser.APPROVE_OPTION)
							return;
						WordProcessor1.this.repaint();
						File fChoosen = m_chooser.getSelectedFile();

						// Recall that text component read/write operations are
						// thread safe. Its ok to do this in a separate thread.
						try {
							OutputStream out = new FileOutputStream(fChoosen);
							m_kit.write(out, m_doc, 0, m_doc.getLength());
							out.close();
						} catch (Exception ex) {
							ex.printStackTrace();
						}

						// Make sure chooser is updated to reflect new file
						m_chooser.rescanCurrentDirectory();
						WordProcessor1.this.setCursor(Cursor
								.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
				};
				runner.start();
			}
		};
		item = mFile.add(actionSave);
		item.setMnemonic('s');

		mFile.addSeparator();

		Action actionExit = new AbstractAction("Exit") {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		};

		item = mFile.add(actionExit);
		item.setMnemonic('x');
		menuBar.add(mFile);
		JButton btnFfgfdg = new JButton("Main Window");
		btnFfgfdg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = e.getActionCommand();
				if (s == "Main Window") {
					/**********************************************
					 * This is where i am calling the window2 *
					 *********************************************/
					digireader tester = new digireader();
					tester.setVisible(true);
					// this.setVisible(false);
				}
			}
		});
		menuBar.add(btnFfgfdg);

		m_toolBar = new JToolBar();
		JButton bNew = new SmallButton(actionNew, "New document");
		m_toolBar.add(bNew);

		JButton bOpen = new SmallButton(actionOpen, "Open RTF document");
		m_toolBar.add(bOpen);

		JButton bSave = new SmallButton(actionSave, "Save RTF document");
		m_toolBar.add(bSave);

		getContentPane().add(m_toolBar, BorderLayout.NORTH);

		return menuBar;
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
		new WordProcessor1();

	}
}

// Class SmallButton unchanged from section 4.8

class SmallButton extends JButton implements MouseListener {
	protected Border m_raised;
	protected Border m_lowered;
	protected Border m_inactive;

	public SmallButton(Action act, String tip) {
		super((Icon) act.getValue(Action.SMALL_ICON));
		m_raised = new BevelBorder(BevelBorder.RAISED);
		m_lowered = new BevelBorder(BevelBorder.LOWERED);
		m_inactive = new EmptyBorder(2, 2, 2, 2);
		setBorder(m_inactive);
		setMargin(new Insets(1, 1, 1, 1));
		setToolTipText(tip);
		addActionListener(act);
		addMouseListener(this);
		setRequestFocusEnabled(false);
	}

	public float getAlignmentY() {
		return 0.5f;
	}

	public void mousePressed(MouseEvent e) {
		setBorder(m_lowered);
	}

	public void mouseReleased(MouseEvent e) {
		setBorder(m_inactive);
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
		setBorder(m_raised);
	}

	public void mouseExited(MouseEvent e) {
		setBorder(m_inactive);
	}
}

// Class SimpleFilter unchanged from section 14.1.9

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
