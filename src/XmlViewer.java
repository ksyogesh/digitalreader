/** 
 *  Copyright 1999-2002 Matthew Robinson and Pavel Vorobiev. 
 *  All Rights Reserved. 
 * 
 *  =================================================== 
 *  This program contains code from the book "Swing" 
 *  2nd Edition by Matthew Robinson and Pavel Vorobiev 
 *  http://www.spindoczine.com/sbe 
 *  =================================================== 
 * 
 *  The above paragraph must be included in full, unmodified 
 *  and completely intact in the beginning of any source code 
 *  file that references, copies or uses (in any way, shape 
 *  or form) code contained in this file. 
 */ 

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.tree.*;

import javax.xml.parsers.*;
import org.w3c.dom.*;

/*
Tree component which displays XML document.
avax.xml.parsers package is used to parse XML.
*/
public class XmlViewer
	extends JFrame {

	public static final String APP_NAME = "XML Viewer";

	protected Document m_doc;

	protected JTree  m_tree;
	protected DefaultTreeModel m_model;
	protected JFileChooser m_chooser;
	protected File  m_currentFile;

	public XmlViewer() {
		super(APP_NAME);
		setSize(400, 400);
		getContentPane().setLayout(new BorderLayout());

		JToolBar tb = createToolbar();
		getContentPane().add(tb, BorderLayout.NORTH);

		DefaultMutableTreeNode top = new DefaultMutableTreeNode(
			"No XML loaded");
		m_model = new DefaultTreeModel(top);
		m_tree = new JTree(m_model);

		m_tree.getSelectionModel().setSelectionMode(
			TreeSelectionModel.SINGLE_TREE_SELECTION);
		m_tree.setShowsRootHandles(true);
		m_tree.setEditable(false);

		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer() {
			public Component getTreeCellRendererComponent(JTree tree,
				Object value, boolean sel, boolean expanded,
				boolean leaf, int row, boolean hasFocus) {
				Component res = super.getTreeCellRendererComponent(tree,
					value, sel, expanded, leaf, row, hasFocus);
				if (value instanceof XmlViewerNode) {
					Node node = ((XmlViewerNode)value).getXmlNode();
					if (node instanceof Element)
						setIcon(expanded ? openIcon : closedIcon);	// No difference in Metal PLAF
					else
						setIcon(leafIcon);
				}
				return res;
			}
		};
		m_tree.setCellRenderer(renderer);

		JScrollPane s = new JScrollPane();
		s.getViewport().add(m_tree);
		getContentPane().add(s, BorderLayout.CENTER);

		m_chooser = new JFileChooser();
		m_chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		m_chooser.setFileFilter(new SimpleFilter("xml",
			"XML Files"));
		try {
			File dir = (new File(".")).getCanonicalFile();
			m_chooser.setCurrentDirectory(dir);
		} catch (IOException ex) {}
	}

	protected JToolBar createToolbar() {
		JToolBar tb = new JToolBar();
		tb.setFloatable(false);

		JButton bt = new JButton(new ImageIcon("document-open-folder.png"));
		bt.setToolTipText("Open XML file");
		ActionListener lst = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openDocument();
 			}
		};
		bt.addActionListener(lst);
		tb.add(bt);

		return tb;
	}

	public String getDocumentName() {
		return m_currentFile==null ? "Untitled" :
			m_currentFile.getName();
	}

	protected void openDocument() {
		Thread runner = new Thread() {
			public void run() {
				if (m_chooser.showOpenDialog(XmlViewer.this) !=
					JFileChooser.APPROVE_OPTION)
					return;
				File f = m_chooser.getSelectedFile();
				if (f == null || !f.isFile())
					return;

				setCursor( Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) );
				try {
					DocumentBuilderFactory docBuilderFactory =
						DocumentBuilderFactory.newInstance();
					DocumentBuilder docBuilder = docBuilderFactory.
						newDocumentBuilder();

					m_doc = docBuilder.parse(f);

					Element root = m_doc.getDocumentElement();
					root.normalize();

					DefaultMutableTreeNode top = createTreeNode(root);

					m_model.setRoot(top);
					m_tree.treeDidChange();
					expandTree(m_tree);
					m_currentFile = f;
					setTitle(APP_NAME+" ["+getDocumentName()+"]");
				}
				catch (Exception ex) {
					showError(ex, "Error reading or parsing XML file");
				}
				finally {
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}
		};
		runner.start();
	}

	protected DefaultMutableTreeNode createTreeNode(Node root) {
		if (!canDisplayNode(root))
			return null;
		XmlViewerNode treeNode = new XmlViewerNode(root);
		NodeList list = root.getChildNodes();
		for (int k=0; k<list.getLength(); k++) {
			Node nd = list.item(k);
			DefaultMutableTreeNode child = createTreeNode(nd);
			if (child != null)
				treeNode.add(child);
		}
		return treeNode;
	}

	protected boolean canDisplayNode(Node node) {
		switch (node.getNodeType()) {
		case Node.ELEMENT_NODE:
			return true;
		case Node.TEXT_NODE:
			String text = node.getNodeValue().trim();
			return !(text.equals("") || text.equals("\n") || text.equals("\r\n"));
		}
		return false;
	}

	public void showError(Exception ex, String message) {
		ex.printStackTrace();
		JOptionPane.showMessageDialog(this,
			message, APP_NAME,
			JOptionPane.WARNING_MESSAGE);
	}

    public static void expandTree(JTree tree) {
	    TreeNode root = (TreeNode)tree.getModel().getRoot();
	    TreePath path = new TreePath(root);
	    for (int k = 0; k<root.getChildCount(); k++) {
			TreeNode child = (TreeNode)root.getChildAt(k);
			expandTree(tree, path, child);
		}
	}

    public static void expandTree(JTree tree, TreePath path, TreeNode node) {
		if (path==null || node==null)
			return;
		tree.expandPath(path);
		TreePath newPath = path.pathByAddingChild(node);
	    for (int k = 0; k<node.getChildCount(); k++) {
			TreeNode child = (TreeNode)node.getChildAt(k);
			if (child != null) {
				expandTree(tree, newPath, child);
			}
		}
	}

	public static void main(String argv[]) {
		XmlViewer frame = new XmlViewer();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}

class XmlViewerNode extends DefaultMutableTreeNode {
	public XmlViewerNode(Node node) {
		super(node);
	}

	public Node getXmlNode() {
		Object obj = getUserObject();
		if (obj instanceof Node)
			return (Node)obj;
		return null;
	}

	public String toString () {
		Node node = getXmlNode();
		if (node == null)
			return getUserObject().toString();
		StringBuffer sb = new StringBuffer();
		switch (node.getNodeType()) {
		case Node.ELEMENT_NODE:
			sb.append('<');
			sb.append(node.getNodeName());
			sb.append('>');
			break;
		case Node.TEXT_NODE:
			sb.append(node.getNodeValue());
			break;
		}
		return sb.toString();
	}
}

class SimpleFilter
	extends javax.swing.filechooser.FileFilter {

	private String m_description = null;
	private String m_extension = null;

	public SimpleFilter(String extension, String description) {
		m_description = description;
		m_extension = "."+extension.toLowerCase();
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

