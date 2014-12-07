import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

public class BasicTextEditor extends JFrame {

	public static final String APP_NAME = "DIGITAL READER";

	public static final String FONTS[] = { "Serif", "SansSerif", "Courier" };
	protected Font m_fonts[];

	protected JTextArea m_editor;
	protected JMenuItem[] m_fontMenus;
	protected JCheckBoxMenuItem m_bold;
	protected JCheckBoxMenuItem m_italic;

	protected JFileChooser m_chooser;
	protected File m_currentFile;

	protected JToolBar m_toolBar;
	protected JComboBox m_cbFonts;
	protected SmallToggleButton m_bBold;
	protected SmallToggleButton m_bItalic;

	protected ColorMenu m_cmFrg; // NEW
	protected ColorMenu m_cmBkg; // NEW

	public BasicTextEditor() {
		super(APP_NAME + "-TEXT");
		setSize(450, 350);
		ImageIcon icon = new ImageIcon("Dr.jpg");
		setIconImage(icon.getImage());

		m_fonts = new Font[FONTS.length];
		for (int k = 0; k < FONTS.length; k++)
			m_fonts[k] = new Font(FONTS[k], Font.PLAIN, 12);

		m_editor = new JTextArea();
		JScrollPane ps = new JScrollPane(m_editor);
		getContentPane().add(ps, BorderLayout.CENTER);

		JMenuBar menuBar = createMenuBar();
		setJMenuBar(menuBar);

		m_chooser = new JFileChooser();
		m_chooser.setCurrentDirectory(new File("."));

		updateEditor();
	}

	protected JMenuBar createMenuBar() {
		final JMenuBar menuBar = new JMenuBar();

		JMenu mFile = new JMenu("File");
		mFile.setMnemonic('f');

		ImageIcon iconNew = new ImageIcon("New16.gif");
		Action actionNew = new AbstractAction("New", iconNew) {
			public void actionPerformed(ActionEvent e) {
				m_editor.setText("");
				m_currentFile = null;
				setTitle(APP_NAME + " [Untitled]");
			}
		};

		JMenuItem item = new JMenuItem(actionNew);
		item.setIcon(new ImageIcon("New16.gif"));
		item.setMnemonic('n');
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				InputEvent.CTRL_MASK));
		mFile.add(item);

		ImageIcon iconOpen = new ImageIcon("Open16.gif");
		Action actionOpen = new AbstractAction("Open...", iconOpen) {
			public void actionPerformed(ActionEvent e) {
				int result = m_chooser.showOpenDialog(BasicTextEditor.this);
				if (result != JFileChooser.APPROVE_OPTION)
					return;
				File f = m_chooser.getSelectedFile();
				if (f == null || !f.isFile())
					return;
				m_currentFile = f;
				try {
					FileReader in = new FileReader(m_currentFile);
					m_editor.read(in, null);
					in.close();
					setTitle(APP_NAME + " [" + m_currentFile.getName() + "]");
				} catch (IOException ex) {
					showError(ex, "Error reading file " + m_currentFile);
				}
			}
		};

		item = new JMenuItem(actionOpen);
		item.setMnemonic('o');
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				InputEvent.CTRL_MASK));
		mFile.add(item);

		ImageIcon iconSave = new ImageIcon("Save16.gif");
		Action actionSave = new AbstractAction("Save...", iconSave) {
			public void actionPerformed(ActionEvent e) {
				if (m_currentFile == null) {
					int result = m_chooser.showSaveDialog(BasicTextEditor.this);
					repaint();
					if (result != JFileChooser.APPROVE_OPTION)
						return;
					File f = m_chooser.getSelectedFile();
					if (f == null)
						return;
					m_currentFile = f;
					setTitle(APP_NAME + " [" + m_currentFile.getName() + "]");
				}

				try {
					FileWriter out = new FileWriter(m_currentFile);
					m_editor.write(out);
					out.close();
				} catch (IOException ex) {
					showError(ex, "Error saving file " + m_currentFile);
				}
			}
		};

		item = new JMenuItem(actionSave);
		item.setMnemonic('s');
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				InputEvent.CTRL_MASK));
		mFile.add(item);

		mFile.addSeparator();

		Action actionExit = new AbstractAction("Exit") {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		};
		item = new JMenuItem(actionExit);
		item.setMnemonic('x');
		mFile.add(item);
		menuBar.add(mFile);

		// Create toolbar
		m_toolBar = new JToolBar();
		JButton bNew = new SmallButton(actionNew, "New text");
		m_toolBar.add(bNew);
		JButton bOpen = new SmallButton(actionOpen, "Open text file");
		m_toolBar.add(bOpen);
		JButton bSave = new SmallButton(actionSave, "Save text file");
		m_toolBar.add(bSave);
		getContentPane().add(m_toolBar, BorderLayout.NORTH);

		ActionListener fontListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateEditor();
			}
		};

		JMenu mFont = new JMenu("Font");
		mFont.setMnemonic('o');

		ButtonGroup group = new ButtonGroup();
		m_fontMenus = new JMenuItem[FONTS.length];
		for (int k = 0; k < FONTS.length; k++) {
			int m = k + 1;
			m_fontMenus[k] = new JRadioButtonMenuItem(m + " " + FONTS[k]);
			m_fontMenus[k].setSelected(k == 0);
			m_fontMenus[k].setMnemonic('1' + k);
			m_fontMenus[k].setFont(m_fonts[k]);
			m_fontMenus[k].addActionListener(fontListener);
			group.add(m_fontMenus[k]);
			mFont.add(m_fontMenus[k]);
		}

		mFont.addSeparator();

		// Add combobox to tollbar
		m_toolBar.addSeparator();
		m_cbFonts = new JComboBox(FONTS);
		m_cbFonts.setMaximumSize(m_cbFonts.getPreferredSize());
		m_cbFonts.setToolTipText("Available fonts");
		ActionListener lst = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = m_cbFonts.getSelectedIndex();
				if (index < 0)
					return;
				m_fontMenus[index].setSelected(true);
				updateEditor();
			}
		};
		m_cbFonts.addActionListener(lst);
		m_toolBar.add(m_cbFonts);

		m_toolBar.addSeparator();
		m_bold = new JCheckBoxMenuItem("Bold");
		m_bold.setMnemonic('b');
		Font fn = m_fonts[1].deriveFont(Font.BOLD);
		m_bold.setFont(fn);
		m_bold.setSelected(false);
		m_bold.addActionListener(fontListener);
		mFont.add(m_bold);

		m_italic = new JCheckBoxMenuItem("Italic");
		m_italic.setMnemonic('i');
		fn = m_fonts[1].deriveFont(Font.ITALIC);
		m_italic.setFont(fn);
		m_italic.setSelected(false);
		m_italic.addActionListener(fontListener);
		mFont.add(m_italic);

		menuBar.add(mFont);

		ImageIcon img1 = new ImageIcon("Bold16.gif");
		m_bBold = new SmallToggleButton(false, img1, img1, "Bold font");
		lst = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				m_bold.setSelected(m_bBold.isSelected());
				updateEditor();
			}
		};
		m_bBold.addActionListener(lst);
		m_toolBar.add(m_bBold);

		img1 = new ImageIcon("Italic16.gif");
		m_bItalic = new SmallToggleButton(false, img1, img1, "Italic font");
		lst = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				m_italic.setSelected(m_bItalic.isSelected());
				updateEditor();
			}
		};
		m_bItalic.addActionListener(lst);
		m_toolBar.add(m_bItalic);
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
		m_toolBar.add(btnRtf);
		// Add color selection menu
		JMenu mOpt = new JMenu("Options");
		mOpt.setMnemonic('p');

		m_cmFrg = new ColorMenu("Foreground"); // NEW
		m_cmFrg.setColor(m_editor.getForeground());
		m_cmFrg.setMnemonic('f');
		lst = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ColorMenu m = (ColorMenu) e.getSource();
				m_editor.setForeground(m.getColor());
			}
		};
		m_cmFrg.addActionListener(lst);
		mOpt.add(m_cmFrg);

		m_cmBkg = new ColorMenu("Background"); // NEW
		m_cmBkg.setColor(m_editor.getBackground());
		m_cmBkg.setMnemonic('b');
		lst = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ColorMenu m = (ColorMenu) e.getSource();
				m_editor.setBackground(m.getColor());
			}
		};
		m_cmBkg.addActionListener(lst);
		mOpt.add(m_cmBkg);

		// Chooser menu - NEW
		Action actionChooser = new AbstractAction("Color Chooser") {
			public void actionPerformed(ActionEvent e) {
				BasicTextEditor.this.repaint();

				JColorChooser colorChooser = new JColorChooser();
				PreviewPanel previewPanel = new PreviewPanel(colorChooser);
				colorChooser.setPreviewPanel(previewPanel);

				JDialog colorDialog = JColorChooser.createDialog(
						BasicTextEditor.this,
						"Select Background and Foreground Color", true,
						colorChooser, previewPanel, null);
				previewPanel.setTextForeground(m_editor.getForeground());
				previewPanel.setTextBackground(m_editor.getBackground());
				colorDialog.show();

				if (previewPanel.isSelected()) {
					m_editor.setBackground(previewPanel.getTextBackground());
					m_cmBkg.setColor(previewPanel.getTextBackground());
					m_editor.setForeground(previewPanel.getTextForeground());
					m_cmFrg.setColor(previewPanel.getTextForeground());
				}
			}
		};
		mOpt.addSeparator();
		item = mOpt.add(actionChooser);
		item.setMnemonic('c');

		menuBar.add(mOpt);

		// Add "About" menu
		JMenu mHelp = new JMenu("Help");
		mHelp.setMnemonic('h');

		Action actionAbout = new AbstractAction("About", new ImageIcon(
				"About16.gif")) {
			public void actionPerformed(ActionEvent e) {
				AboutBox dlg = new AboutBox(BasicTextEditor.this);
				dlg.show();
			}
		};
		item = mHelp.add(actionAbout);
		item.setMnemonic('a');
		menuBar.add(mHelp);

		return menuBar;
	}

	protected void updateEditor() {
		int index = -1;
		for (int k = 0; k < m_fontMenus.length; k++) {
			if (m_fontMenus[k].isSelected()) {
				index = k;
				break;
			}
		}
		if (index == -1)
			return;

		if (index == 2) { // Courier
			m_bold.setSelected(false);
			m_bold.setEnabled(false);
			m_italic.setSelected(false);
			m_italic.setEnabled(false);
			m_bBold.setSelected(false);
			m_bBold.setEnabled(false);
			m_bItalic.setSelected(false);
			m_bItalic.setEnabled(false);
		} else {
			m_bold.setEnabled(true);
			m_italic.setEnabled(true);
			m_bBold.setEnabled(true);
			m_bItalic.setEnabled(true);
		}

		// Synchronize toolbar and menu components
		m_cbFonts.setSelectedIndex(index);
		boolean isBold = m_bold.isSelected();
		boolean isItalic = m_italic.isSelected();
		if (m_bBold.isSelected() != isBold)
			m_bBold.setSelected(isBold);
		if (m_bItalic.isSelected() != isItalic)
			m_bItalic.setSelected(isItalic);

		int style = Font.PLAIN;
		if (m_bold.isSelected())
			style |= Font.BOLD;
		if (m_italic.isSelected())
			style |= Font.ITALIC;
		Font fn = m_fonts[index].deriveFont(style);
		m_editor.setFont(fn);
		m_editor.repaint();
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
		BasicTextEditor frame = new BasicTextEditor();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public String BasicTextEditor() {
		return null;
		// TODO Auto-generated method stub

	}
}

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

class SmallToggleButton extends JToggleButton implements ItemListener {

	protected Border m_raised = new SoftBevelBorder(BevelBorder.RAISED);
	protected Border m_lowered = new SoftBevelBorder(BevelBorder.LOWERED);
	protected Insets m_ins = new Insets(4, 4, 4, 4);

	public SmallToggleButton(boolean selected, ImageIcon imgUnselected,
			ImageIcon imgSelected, String tip) {
		super(imgUnselected, selected);
		setHorizontalAlignment(CENTER);
		setBorder(selected ? m_lowered : m_raised);
		setMargin(m_ins);
		setToolTipText(tip);
		setRequestFocusEnabled(false);
		setSelectedIcon(imgSelected);
		addItemListener(this);
	}

	public float getAlignmentY() {
		return 0.5f;
	}

	// Overridden for 1.4 bug fix
	public Insets getInsets() {
		return m_ins;
	}

	public Border getBorder() {
		return (isSelected() ? m_lowered : m_raised);
	}

	public void itemStateChanged(ItemEvent e) {
		setBorder(isSelected() ? m_lowered : m_raised);
	}
}

// Custom menu component
class ColorMenu extends JMenu {

	protected Border m_unselectedBorder;
	protected Border m_selectedBorder;
	protected Border m_activeBorder;

	protected Hashtable m_panes;
	protected ColorPane m_selected;

	public ColorMenu(String name) {
		super(name);
		m_unselectedBorder = new CompoundBorder(new MatteBorder(1, 1, 1, 1,
				getBackground()), new BevelBorder(BevelBorder.LOWERED,
				Color.white, Color.gray));
		m_selectedBorder = new CompoundBorder(new MatteBorder(2, 2, 2, 2,
				Color.red), new MatteBorder(1, 1, 1, 1, getBackground()));
		m_activeBorder = new CompoundBorder(new MatteBorder(2, 2, 2, 2,
				Color.blue), new MatteBorder(1, 1, 1, 1, getBackground()));

		JPanel p = new JPanel();
		p.setBorder(new EmptyBorder(5, 5, 5, 5));
		p.setLayout(new GridLayout(8, 8));
		m_panes = new Hashtable();

		int[] values = new int[] { 0, 128, 192, 255 };
		for (int r = 0; r < values.length; r++) {
			for (int g = 0; g < values.length; g++) {
				for (int b = 0; b < values.length; b++) {
					Color c = new Color(values[r], values[g], values[b]);
					ColorPane pn = new ColorPane(c);
					p.add(pn);
					m_panes.put(c, pn);
				}
			}
		}
		add(p);
	}

	public void setColor(Color c) {
		Object obj = m_panes.get(c);
		if (obj == null)
			return;
		if (m_selected != null)
			m_selected.setSelected(false);
		m_selected = (ColorPane) obj;
		m_selected.setSelected(true);
	}

	public Color getColor() {
		if (m_selected == null)
			return null;
		return m_selected.getColor();
	}

	public void doSelection() {
		fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
				getActionCommand()));
	}

	class ColorPane extends JPanel implements MouseListener {
		protected Color m_c;
		protected boolean m_selected;

		public ColorPane(Color c) {
			m_c = c;
			setBackground(c);
			setBorder(m_unselectedBorder);
			String msg = "R " + c.getRed() + ", G " + c.getGreen() + ", B "
					+ c.getBlue();
			setToolTipText(msg);
			addMouseListener(this);
		}

		public Color getColor() {
			return m_c;
		}

		public Dimension getPreferredSize() {
			return new Dimension(15, 15);
		}

		public Dimension getMaximumSize() {
			return getPreferredSize();
		}

		public Dimension getMinimumSize() {
			return getPreferredSize();
		}

		public void setSelected(boolean selected) {
			m_selected = selected;
			if (m_selected)
				setBorder(m_selectedBorder);
			else
				setBorder(m_unselectedBorder);
		}

		public boolean isSelected() {
			return m_selected;
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseClicked(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
			setColor(m_c);
			MenuSelectionManager.defaultManager().clearSelectedPath();
			doSelection();
		}

		public void mouseEntered(MouseEvent e) {
			setBorder(m_activeBorder);
		}

		public void mouseExited(MouseEvent e) {
			setBorder(m_selected ? m_selectedBorder : m_unselectedBorder);
		}
	}
}

class AboutBox extends JDialog {

	public AboutBox(Frame owner) {
		super(owner, "About", true);

		JLabel lbl = new JLabel(new ImageIcon("icon.gif"));
		JPanel p = new JPanel();
		Border b1 = new BevelBorder(BevelBorder.LOWERED);
		Border b2 = new EmptyBorder(5, 5, 5, 5);
		lbl.setBorder(new CompoundBorder(b1, b2));
		p.add(lbl);
		getContentPane().add(p, BorderLayout.WEST);

		String message = "DIGITAL READER\n" + "TEXT READER";
		JTextArea txt = new JTextArea(message);
		txt.setBorder(new EmptyBorder(5, 10, 5, 10));
		txt.setFont(new Font("Helvetica", Font.BOLD, 12));
		txt.setEditable(false);
		txt.setBackground(getBackground());
		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.add(txt);

		message = "JVM version " + System.getProperty("java.version") + "\n"
				+ " by " + System.getProperty("java.vendor");
		txt = new JTextArea(message);
		txt.setBorder(new EmptyBorder(5, 10, 5, 10));
		txt.setFont(new Font("Arial", Font.PLAIN, 12));
		txt.setEditable(false);
		txt.setLineWrap(true);
		txt.setWrapStyleWord(true);
		txt.setBackground(getBackground());
		p.add(txt);

		getContentPane().add(p, BorderLayout.CENTER);

		final JButton btOK = new JButton("OK");
		ActionListener lst = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		};
		btOK.addActionListener(lst);
		p = new JPanel();
		p.add(btOK);
		getRootPane().setDefaultButton(btOK);
		getRootPane().registerKeyboardAction(lst,
				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
				JComponent.WHEN_IN_FOCUSED_WINDOW);
		getContentPane().add(p, BorderLayout.SOUTH);

		// That will transer focus from first component upon dialog's show
		WindowListener wl = new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				btOK.requestFocus();
			}
		};
		addWindowListener(wl);

		pack();
		setResizable(false);
		setLocationRelativeTo(owner);
	}
}

// Color preview panel - NEW
class PreviewPanel extends JPanel implements ChangeListener, ActionListener {
	protected JColorChooser m_chooser;
	protected JLabel m_preview;
	protected JToggleButton m_btBack;
	protected JToggleButton m_btFore;
	protected boolean m_isSelected = false;

	public PreviewPanel(JColorChooser chooser) {
		this(chooser, Color.white, Color.black);
	}

	public PreviewPanel(JColorChooser chooser, Color background,
			Color foreground) {
		m_chooser = chooser;
		chooser.getSelectionModel().addChangeListener(this);

		setLayout(new BorderLayout());
		JPanel p = new JPanel(new GridLayout(2, 1, 0, 0));
		ButtonGroup group = new ButtonGroup();
		m_btBack = new JToggleButton("Background");
		m_btBack.setSelected(true);
		m_btBack.addActionListener(this);
		group.add(m_btBack);
		p.add(m_btBack);
		m_btFore = new JToggleButton("Foreground");
		m_btFore.addActionListener(this);
		group.add(m_btFore);
		p.add(m_btFore);
		add(p, BorderLayout.WEST);

		p = new JPanel(new BorderLayout());
		Border b1 = new EmptyBorder(5, 10, 5, 10);
		Border b2 = new BevelBorder(BevelBorder.RAISED);
		Border b3 = new EmptyBorder(2, 2, 2, 2);
		Border cb1 = new CompoundBorder(b1, b2);
		Border cb2 = new CompoundBorder(cb1, b3);
		p.setBorder(cb2);

		m_preview = new JLabel("Text colors preview", JLabel.CENTER);
		m_preview.setBackground(background);
		m_preview.setForeground(foreground);
		m_preview.setFont(new Font("Arial", Font.BOLD, 24));
		m_preview.setOpaque(true);
		p.add(m_preview, BorderLayout.CENTER);
		add(p, BorderLayout.CENTER);

		m_chooser.setColor(background);
	}

	protected boolean isSelected() {
		return m_isSelected;
	}

	public void setTextBackground(Color c) {
		m_preview.setBackground(c);
	}

	public Color getTextBackground() {
		return m_preview.getBackground();
	}

	public void setTextForeground(Color c) {
		m_preview.setForeground(c);
	}

	public Color getTextForeground() {
		return m_preview.getForeground();
	}

	public void stateChanged(ChangeEvent evt) {
		Color c = m_chooser.getColor();
		if (c != null) {
			if (m_btBack.isSelected())
				m_preview.setBackground(c);
			else
				m_preview.setForeground(c);
		}
	}

	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() == m_btBack)
			m_chooser.setColor(getTextBackground());
		else if (evt.getSource() == m_btFore)
			m_chooser.setColor(getTextForeground());
		else
			m_isSelected = true;
	}
}
