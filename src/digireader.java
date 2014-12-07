import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;
import javax.swing.JMenuBar;

import org.jpedal.PdfDecoder;
import org.jpedal.examples.jpaneldemo.JPanelDemo;
import org.jpedal.examples.simpleviewer.utils.FileFilterer;

import java.awt.Button;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.ImageIcon;
import java.awt.Toolkit;
import javax.swing.JComboBox;
import java.beans.PropertyVetoException;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class digireader extends JFrame {

	private JPanel contentPane;
	private UIManager.LookAndFeelInfo[] m_infos;
	private String currentFile=null;
	private int currentPage=1;
	private PdfDecoder pdfDecoder;
	private JTextField pageCounter2=new JTextField(4);
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		UIManager.LookAndFeelInfo plafinfo[] = UIManager.getInstalledLookAndFeels();
		boolean nimbusfound=false;
		int nimbusindex=0;

		for (int look = 0; look < plafinfo.length; look++) {
		if(plafinfo[look].getClassName().toLowerCase().contains("nimbus"))
		{
		nimbusfound=true;
		nimbusindex=look;
		}
		}

		try {

		if(nimbusfound)
		{ 
		UIManager.setLookAndFeel(plafinfo[nimbusindex].getClassName());
		}
		else

		UIManager.setLookAndFeel(
		UIManager.getCrossPlatformLookAndFeelClassName());

		}
		catch(Exception e)
		{
		}
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					digireader frame = new digireader();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public digireader() {
		setTitle("DIGITAL READER");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 1280, 700);
		m_infos = UIManager.getInstalledLookAndFeels();
	    String[] LAFNames = new String[m_infos.length];
	    for(int i=0; i<m_infos.length; i++) {
	        LAFNames[i] = m_infos[i].getName();
	      }
	    
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JButton btnText = new JButton("       TEXT       ");
		btnText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = e.getActionCommand();
				if(s=="       TEXT       "){
				/**********************************************
				* This is where i am calling the window2 *
				*********************************************/
					BasicTextEditor tester = new BasicTextEditor(); 
				tester.setVisible(true);
				//this.setVisible(false);
				}
			}
		});
		menuBar.add(btnText);
		
		JButton btnPdf = new JButton("        PDF         ");
		btnPdf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = e.getActionCommand();
				if(s=="        PDF         "){
				/**********************************************
				* This is where i am calling the window2 *
				*********************************************/
					JPanelDemo tester = new JPanelDemo(); 
				tester.setVisible(true);
				//this.setVisible(false);
				}
			}
		});
		menuBar.add(btnPdf);
		
		JButton btnRtf = new JButton("          RTF         ");
		btnRtf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = e.getActionCommand();
				if(s=="          RTF         "){
				/**********************************************
				* This is where i am calling the window2 *
				*********************************************/
					WordProcessor tester = new WordProcessor(); 
				tester.setVisible(true);
				//this.setVisible(false);
				}
			}
		});
		menuBar.add(btnRtf);
		
		JButton btnHtml = new JButton("           HTML         ");
		btnHtml.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = e.getActionCommand();
				if(s=="           HTML         "){
				/**********************************************
				* This is where i am calling the window2 *
				*********************************************/
					HtmlProcessor tester = new HtmlProcessor(); 
				tester.setVisible(true);
				//this.setVisible(false);
				}
			}
		});
		menuBar.add(btnHtml);
		
		JButton btnHtml11 = new JButton("XML");
		btnHtml11.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = e.getActionCommand();
				if(s=="XML"){
				/**********************************************
				* This is where i am calling the window2 *
				*********************************************/
					XmlViewer tester = new XmlViewer(); 
				tester.setVisible(true);
				//this.setVisible(false);
				}
			}
		});
		menuBar.add(btnHtml11);
		JButton btnHtml1 = new JButton("ImageViewer");
		btnHtml1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = e.getActionCommand();
				if(s=="ImageViewer"){
				/**********************************************
				* This is where i am calling the window2 *
				*********************************************/
					ImageViewer tester = new ImageViewer(); 
				tester.setVisible(true);
				//this.setVisible(false);
				}
			}
		});
		menuBar.add(btnHtml1);
		
		final JComboBox comboBox = new JComboBox(LAFNames);
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == comboBox)
			      {
				  int index = comboBox.getSelectedIndex();
				  if (index < 0)
					return;
			      String lfClass = m_infos[index].getClassName();
			      comboBox.hidePopup(); // BUG WORKAROUND
			      try {
			        UIManager.setLookAndFeel(lfClass);
			      SwingUtilities.updateComponentTreeUI(digireader.this);
			      }
			      catch(Exception ex) {
			        System.out.println("Could not load " + lfClass);
			        ex.printStackTrace();
			      }
			      comboBox.setSelectedIndex(index);
			    }
			  }
		
		});
		menuBar.add(comboBox);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JToolBar toolBar = new JToolBar();
		toolBar.setBounds(0, -2, 756, 47);
		contentPane.add(toolBar);
		
		JButton btnNewButton = new JButton("");
		btnNewButton.setIcon(new ImageIcon("address-book-new.png"));
		toolBar.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				selectFile();
			}
		});
		btnNewButton_1.setIcon(new ImageIcon("document-open-folder.png"));
		toolBar.add(btnNewButton_1);
		
		JButton button = new JButton("");
		button.setIcon(new ImageIcon("document-save.png"));
		toolBar.add(button);
		
		JButton btnNewButton_2 = new JButton("");
		btnNewButton_2.setIcon(new ImageIcon("document-preview.png"));
		toolBar.add(btnNewButton_2);
		
		JButton btnNewButton_3 = new JButton("");
		btnNewButton_3.setIcon(new ImageIcon("configure.png"));
		toolBar.add(btnNewButton_3);
		
		JButton btnNewButton_4 = new JButton("");
		btnNewButton_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = e.getActionCommand();
				if(s==""){
				/**********************************************
				* This is where i am calling the window2 *
				*********************************************/
					About tester = new About(); 
				tester.setVisible(true);
				//this.setVisible(false);
				}
			}
		});
		btnNewButton_4.setIcon(new ImageIcon("help-about.png"));
		toolBar.add(btnNewButton_4);
		
		JButton btnNewButton_5 = new JButton("");
		btnNewButton_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = e.getActionCommand();
				if(s==""){
				/**********************************************
				* This is where i am calling the window2 *
				*********************************************/
					JPanelDemo11 tester = new JPanelDemo11(); 
				tester.setVisible(true);
				//this.setVisible(false);
				}
			}
		});
		btnNewButton_5.setIcon(new ImageIcon("document-properties.png"));
		toolBar.add(btnNewButton_5);
		
		JButton btnNewButton_6 = new JButton("");
		btnNewButton_6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = e.getActionCommand();
				if(s==""){
				/**********************************************
				* This is where i am calling the window2 *
				*********************************************/
					WordProcessor11 tester = new WordProcessor11(); 
				tester.setVisible(true);
				//this.setVisible(false);
				}
			}
		});
		btnNewButton_6.setIcon(new ImageIcon("edit-select-all.png"));
		toolBar.add(btnNewButton_6);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 46, 1272, 592);
		contentPane.add(tabbedPane);
		
		JTabbedPane tabbedPane_1 = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("                               Main Window                       ",new asd());
		
		JTabbedPane tabbedPane_2 = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("                               File Search                       ", new fdg());
		JTabbedPane tabbedPane_3 = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("                               File Search2                       ", new SearchFiles());
		JTabbedPane tabbedPane_4 = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("                               Text Search                       ", new GrepFrame());
	}
	private void selectFile() {

        JFileChooser open = new JFileChooser(".");
    open.setFileSelectionMode(JFileChooser.FILES_ONLY);
    
    String[] pdf = new String[] { "pdf" };
    String[] txt = new String[] { "txt" };
    String[] rtf = new String[] { "rtf" };
    String[] html = new String[] { "html" };
    open.addChoosableFileFilter(new FileFilterer(pdf,"Pdf (*.pdf)"));
    open.addChoosableFileFilter(new FileFilterer(txt,"txt (*.txt)"));
    open.addChoosableFileFilter(new FileFilterer(rtf,"rtf (*.rtf)"));
    open.addChoosableFileFilter(new FileFilterer(html,"html (*.html)"));
    
    int resultOfFileSelect = JFileChooser.ERROR_OPTION;
    while(resultOfFileSelect==JFileChooser.ERROR_OPTION){
      
      resultOfFileSelect = open.showOpenDialog(this);
      
      if(resultOfFileSelect==JFileChooser.ERROR_OPTION)
        System.err.println("JFileChooser error");
      
      if(resultOfFileSelect==JFileChooser.APPROVE_OPTION){
        currentFile = open.getSelectedFile().getAbsolutePath();
        
        currentPage = 1;
        try{
          //close the current pdf before opening another
          pdfDecoder.closePdfFile();
          
//          this opens the PDF and reads its internal details
          pdfDecoder.openPdfFile(currentFile);
          
          //check for password encription and acertain
          if(!checkEncryption()){
            //if file content is not accessable make user select a different file
            resultOfFileSelect = JFileChooser.CANCEL_OPTION;
          }
          
//          these 2 lines opens page 1 at 100% scaling
          pdfDecoder.decodePage(currentPage);
          pdfDecoder.setPageParameters(1,1); //values scaling (1=100%). page number
          pdfDecoder.invalidate();
          
        }catch(Exception e){
          e.printStackTrace();
        }
        
        //set page number display
        pageCounter2.setText(String.valueOf(currentPage));
        JTextComponent pageCounter3 = null;
		pageCounter3.setText("of "+pdfDecoder.getPageCount());
        
      //  setTitle(viewerTitle+" - "+currentFile);
        
        repaint();
      }
    }
  }

	private boolean checkEncryption() {
		// TODO Auto-generated method stub
		return false;
	}
}
