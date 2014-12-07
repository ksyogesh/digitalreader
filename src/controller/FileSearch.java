package controller;

//this file is for file search
import java.io.File;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.Color;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.DefaultListModel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

public class FileSearch extends JInternalFrame implements ActionListener, ItemListener {

	private String MAINPATH = "C:\\"; // Default path on start up.
	boolean FolderSystem;
	File mainFile;
	DefaultListModel listModel = new DefaultListModel();

	JPanel northPanel = new JPanel(new FlowLayout());
	JPanel southPanel, mainPanel, centerPanel;
	JLabel lblPath, lblFilename;
	JButton searchButton = new JButton("Search");
	JButton runButton = new JButton("Run");
	JButton fileButton = new JButton("Folder...");
	JTextField searchField = new JTextField(20);
	JList searchList = new JList(listModel);
	JScrollPane scrollList = new JScrollPane(searchList);
	JFileChooser filechooser = new JFileChooser();
	JRadioButton rbFolder;
	JRadioButton rbSystem;
	ButtonGroup btGroup;
	Container con;

	public FileSearch() {

		super("DIGITAL READER-FILE SEARCH");
		FolderSystem = true;
		lblPath = new JLabel("Progress..");
		lblFilename = new JLabel("File/Foler Name : ");
		southPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		mainPanel = new JPanel(new BorderLayout());
		southPanel.add(lblPath);
		lblPath.setPreferredSize(new Dimension(550, 20));
		centerPanel.setPreferredSize(new Dimension(550, 25));
		btGroup = new ButtonGroup();
		rbFolder = new JRadioButton("Foler Specific", true);
		rbSystem = new JRadioButton("Entire System");
		btGroup.add(rbFolder);
		btGroup.add(rbSystem);

		centerPanel.add(rbFolder);
		centerPanel.add(rbSystem);

		con = getContentPane();
		scrollList
				.setBorder(BorderFactory.createTitledBorder(
						BorderFactory.createLineBorder(Color.blue),
						"Search Result..."));
		this.setSize(600, 350);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setupComps();
		this.setVisible(true);
	}

	private void setupComps() {

		runButton.addActionListener(this);
		searchButton.addActionListener(this);
		fileButton.addActionListener(this);
		rbFolder.addItemListener(this);
		rbSystem.addItemListener(this);

		filechooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		northPanel.add(lblFilename);
		northPanel.add(searchField);
		northPanel.add(searchButton);
		northPanel.add(runButton);
		northPanel.add(fileButton);

		mainPanel.add(northPanel, BorderLayout.NORTH);
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		mainPanel.add(southPanel, BorderLayout.SOUTH);
		con.add(mainPanel, BorderLayout.NORTH);
		con.add(scrollList, BorderLayout.CENTER);
	}

	// Don't get scared here, everythings very simple ...
	public void actionPerformed(ActionEvent e) {
		if ((e.getSource().equals(searchButton))
				&& (!(searchField.getText().trim().equals("")))) {

			listModel.clear();
			if (FolderSystem) {
				mainFile = new File(MAINPATH);
				new SearchFile(mainFile, searchField.getText().trim(),
						listModel, lblPath);
			} else {
				File[] roots = File.listRoots();
				for (int i = 0; i < roots.length; i++)
					new SearchFile(roots[i], searchField.getText().trim(),
							listModel, lblPath);
			}

		} else if ((e.getSource().equals(runButton))) {
			if (listModel.size() > 0) {

				System.out.println((String) (listModel.get(searchList
						.getSelectedIndex())));
				try {
					Runtime.getRuntime().exec(
							"rundll32 SHELL32.DLL,ShellExec_RunDLL "
									+ (String) (listModel.get(searchList
											.getSelectedIndex())));
				} catch (Exception exception) {
					System.out
							.println("An error occured trying to execute file.");
				}
			} else
				JOptionPane.showMessageDialog(this,
						"There is no selected list Item to run",
						"Error in List item", JOptionPane.ERROR_MESSAGE);
		}
		if ((e.getSource().equals(fileButton))) {
			int i = filechooser.showOpenDialog(this);
			if (i == JFileChooser.APPROVE_OPTION) {
				MAINPATH = filechooser.getSelectedFile().getAbsolutePath()
						+ "\\";
				System.out.println(MAINPATH);
			}
		}
	}

	public void itemStateChanged(ItemEvent i) {
		if (rbFolder.isSelected() == true) {
			FolderSystem = true;
			fileButton.setEnabled(true);
		} else if (rbSystem.isSelected() == true) {
			FolderSystem = false;
			fileButton.setEnabled(false);
		}
	}

	class SearchFile extends Thread {
		File Filedir;
		String Filename;
		DefaultListModel listmodel;
		JLabel lblpath;

		public SearchFile(File d, String n, DefaultListModel dm, JLabel l) {
			Filedir = d;
			Filename = n;
			listmodel = dm;
			lblpath = l;
			this.start();
		}

		// this is a recursive file search function
		public void run() {
			search(Filedir, Filename, listmodel, lblpath);
			if (listmodel.size() > 0) {
				searchList.setSelectedIndex(0);
			}
		}

		private void search(File dir, String name, DefaultListModel model,
				JLabel lb) {
			lb.setText(dir.getAbsolutePath());

			if (dir.getName().toLowerCase().equals(name.toLowerCase())) {

				model.addElement(dir.getAbsolutePath());
				System.out.println(dir.getAbsolutePath());
			} else if (dir.isDirectory()) {
				String[] children = dir.list();
				for (int i = 0; i < children.length; i++) {
					search(new File(dir, children[i]), name, model, lb);
				}

			}
		}
	}// end of class Search File

	public static void main(String args[]) {
		new FileSearch();
	}
}