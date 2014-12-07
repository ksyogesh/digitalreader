/* <!-- in case someone opens this in a browser... --> <pre> */
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

/**
 * This is a little sample program that demonstrates a fairly quick way
 * to search for text in a file. It doesn't search for text patterns using
 * wildcard characters or regular expressions or anything, but just for a
 * specific piece of text.
 * <p>
 * For the sake of example, I went ahead and wrapped the searchFile method
 * (which is the one that actually does the searching) in a small graphical
 * Swing application that will let you enter a directory to search in, a
 * text string to search for, whether you want a case-sensitive or insensitive 
 * search, and whether you should also search subdirectories. It also times
 * the search, and lets you double-click on any of the search results to open
 * them up in a window for viewing.
 * <p>
 * As an application, this program also has a few other little examples of
 * working with Swing. It shows you how to handle double-clicks in a JList,
 * how to listen for the ENTER key in a JTextField, how to open a file in 
 * a window, and how to launch a separate search thread with a button click
 * so your application won't lock up as it's performing a search (and so you
 * can stop a search that's in progress). It also shows you an easy way to get
 * the current directory (String dir = new File("").getAbsolutePath();). 
 * The code is a little rough, and there are a few more global variables than 
 * I would prefer, but it'll give you a good idea of how to implement this sort 
 * of thing in your own programs.
 * <p>
 * version 1.0<br>
 * Julian Robichaux ( http://www.nsftools.com )
 * 
 * @author Julian Robichaux ( http://www.nsftools.com )
 * @version 1.0
 */
class SearchFiles extends JInternalFrame
{
	String searchDir = "";
	boolean stopSearching = false;
	JLabel status = new JLabel(" ");
	
	// search variables -- these were made global so
	// we wouldn't have to reallocate them every time
	// we performed a search when we're searching a lot
	// of files in a directory
	private int bufSize = 2048;
	private byte buf[] = new byte[bufSize];
	private byte[] searchBytesLC;
	private byte[] searchBytesUC;
	private int sbLen;
	
	String dir = new File("").getAbsolutePath();
	public static void main (String[] args)
	{
		//String dir = "D:\jdk1.3.1_02\docs";
		// use the current directory, if one wasn't passed at the command line
		String dir = new File("").getAbsolutePath();
		if (args.length > 0)
			dir = args[0];
		
		SearchFiles sf = new SearchFiles();
	}
	
	
	public SearchFiles ()
	{
		// just for fun, we'll set the look-and-feel to the system L&F
		// for this platform (this is optional -- if you don't do this,
		// you'll get the default Java look-and-feel, which is usually
		// "Metal")
		
		
		// most of the Swing components here have been declared final,
		// so we can easily pass them to the various ActionListeners
		searchDir = dir;
		final JTextField dirField = new JTextField(dir);
		final JTextField searchField = new JTextField();
		final JCheckBox csCheckBox = new JCheckBox("case-sensitive");
		final JCheckBox subdirCheckBox = new JCheckBox("search subdirectories", true);
		
		// this is the list that will hold the search results
		final JList resultList = new JList();
		// handle double-clicks, which should open the file that was double-clicked
		MouseListener ml = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if ((resultList.getSelectedIndex() != -1) && (e.getClickCount() == 2))  {
					String selection = (String)resultList.getSelectedValue();
					displayFile(searchDir + File.separator + selection);
				}
			}
		};
		resultList.addMouseListener(ml);
		
		// put the JList in a scroll pane
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getViewport().setView(resultList);
		scrollPane.setPreferredSize(new Dimension(400, 350));
		
		// this gets called if the user is in the searchField and presses ENTER,
		// which we're interpreting to mean that they want to start a search
		searchField.addActionListener( new ActionListener()  {
			public void actionPerformed(ActionEvent e)  {
				searchDir = dirField.getText();
				startSearchThread(resultList, searchDir, searchField.getText(), 
						csCheckBox.isSelected(), subdirCheckBox.isSelected());
			}
		} );
		
		// the button that allows the user to easily choose a directory to search
		JButton dirButton = new JButton("...");
		dirButton.setMargin(new Insets(1, 2, 1, 2));
		dirButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser(dirField.getText());
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = chooser.showOpenDialog(dirField.getParent());
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					dirField.setText(chooser.getSelectedFile().toString());
					searchDir = dirField.getText();
				}
			}
		});
		
		// the button that launches a search
		JButton searchButton = new JButton("Search");
		searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				searchDir = dirField.getText();
				startSearchThread(resultList, searchDir, searchField.getText(), 
						csCheckBox.isSelected(), subdirCheckBox.isSelected());
			}
		});
		
		// the button that stops a search in progress
		JButton stopButton = new JButton("Stop");
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopSearching = true;
			}
		});
		
		
		// try to lay everything out nicely on a JPanel
		JPanel n0 = new JPanel(new BorderLayout(5, 5));
		n0.add(new JLabel("Dir: "), BorderLayout.WEST);
		n0.add(dirField, BorderLayout.CENTER);
		n0.add(dirButton, BorderLayout.EAST);
		JPanel n1 = new JPanel(new BorderLayout(5, 5));
		n1.add(new JLabel("Find: "), BorderLayout.WEST);
		n1.add(searchField, BorderLayout.CENTER);
		JPanel n2 = new JPanel();
		n2.add(csCheckBox);
		n2.add(subdirCheckBox);
		n1.add(n2, BorderLayout.SOUTH);
		
		JPanel north = new JPanel(new BorderLayout(5, 5));
		north.add(n0, BorderLayout.NORTH);
		north.add(n1, BorderLayout.CENTER);
		
		JPanel buttons = new JPanel(new FlowLayout());
		buttons.add(searchButton);
		buttons.add(stopButton);
		north.add(buttons, BorderLayout.SOUTH);
		
		JPanel panel = new JPanel(new BorderLayout(5, 5));
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel.add(north, BorderLayout.NORTH);
		panel.add(scrollPane, BorderLayout.CENTER);
		panel.add(status, BorderLayout.SOUTH);
		
		// add everything to the parent frame
		getContentPane().add(panel);
		
		// make sure we exit properly when the window is closed
	
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		// and let's see what we've got
		setTitle("Search In Files");
		//setSize(800, 600);
		pack();	// don't pack() if we're doing a setSize()
		show();
		
	}
	
	
	private void startSearchThread (final JList list, final String dir, final String searchTerm, 
				final boolean caseSensitive, final boolean searchSubdirs)
	{
		// searches are performed in a separate thread, so that the
		// Swing window doesn't lock up as we're performing a search
		// (this also allows us to implement a "stop" button, to halt
		// a search in progress)
		Thread searchThread = new Thread() {
			public void run() {
				// try to stop any other searches we have going
				stopSearching = true;
				try { sleep(250); } catch (Exception e) {}
				// clear the current list
				Vector files = new Vector();
				list.setListData(files);
				// start a new search
				long startTime = System.currentTimeMillis();
				stopSearching = false;
				
				if (caseSensitive) {
					searchBytesLC = searchTerm.getBytes();
					searchBytesUC = searchTerm.getBytes();
				} else {
					searchBytesLC = searchTerm.toLowerCase().getBytes();
					searchBytesUC = searchTerm.toUpperCase().getBytes();
				}
				sbLen = searchBytesLC.length;
				
				doSearch(list, dir, searchTerm, caseSensitive, searchSubdirs, files);
				long endTime = System.currentTimeMillis();
				String finished = (stopSearching) ? "Search stopped." : "Finished.";
				status.setText(finished + " " + files.size() + " results, " + 
								String.valueOf(endTime - startTime) + " ms" + 
								" (double-click a file name to view)");
				stopSearching = true;
			}
		};
		searchThread.start();
	}
	
	
	private void doSearch (JList list, String dir, String searchTerm, 
				boolean caseSensitive, boolean searchSubdirs, Vector files)
	{
		// this function calls the searchFile method on all the files
		// in a directory, and it recursively calls itself for any
		// subdirectories that it finds
		status.setText("Searching " + dir.substring(searchDir.length()));
		try
		{
			File f = new File(dir);
			File[] theFiles = f.listFiles();
			for (int i = 0; i < theFiles.length; i++)
			{
				if (stopSearching)
					return;
				
				if ((theFiles[i].isDirectory()) && (searchSubdirs))
				{
					doSearch(list, theFiles[i].getPath(), searchTerm, caseSensitive, 
								searchSubdirs, files);
				}  else if (searchFile(theFiles[i], searchTerm, caseSensitive))  {
					files.add(theFiles[i].toString().substring(searchDir.length()+1));
					list.setListData(files);
				}
			}
			
		}  catch (Exception e)  {
			System.out.println("Search Error: " + e);
		}
		
	}
	
	
	/**
	 * This is the function that performs the actual file searches. 
	 * After much testing, I found that my searches were the fastest
	 * if I converted the search String to a byte[] array, opened up
	 * the file as a FileInputStream, and searched the Stream byte-by-byte
	 * against the search String byte[] array. The obvious way to do it,
	 * which was reading the file as text Strings and doing an indexOf()
	 * search to find the search term, was much slower because of all the
	 * conversions that were taking place and all the immutable String
	 * objects that had to be created (especially on a case-insensitive
	 * search).
	 */
	private boolean searchFile (File f, String searchTerm, boolean caseSensitive)
	{
		/*
		 * The following variables are global, and have been set
		 * in the startSearchThread method for efficiency (so we
		 * only have to set them once for multiple searches).
		 * If you're only searching a few files, it makes more sense
		 * to make these local variables within this method
		int bufSize = 2048;
		byte buf[] = new byte[bufSize];
		byte[] searchBytesLC = (caseSensitive) ? searchTerm.getBytes() : searchTerm.toLowerCase().getBytes();
		byte[] searchBytesUC = (caseSensitive) ? searchTerm.getBytes() : searchTerm.toUpperCase().getBytes();
		int sbLen = searchBytesLC.length;
		*/
		boolean retVal = false;
		int sbPos = 0;
		int len = 0, i = 0;
		
		try
		{
			FileInputStream fr = new FileInputStream(f);
			//byte buf[] = new byte[bufSize];
			while ((len = fr.read(buf, 0, bufSize)) >= 0) {
				for (i = 0; i < len; i++) {
					if ((buf[i] == searchBytesLC[sbPos]) || 
						(buf[i] == searchBytesUC[sbPos])) {
						sbPos++;
						if (sbPos == sbLen) {
							retVal = true;
							fr.close();
							return true;
						}
					} else {
						sbPos = 0;
					}
				}
			}
			fr.close();
		} catch (Exception e) {
			System.out.println("Error searching " + f + ": " + e);
			//e.printStackTrace();
			//System.out.println("i="+i+";len="+len+";sbPos="+sbPos+";sbLen="+sbLen);
		}
		
		return retVal;
	}
	
	
	private void displayFile (String fileName)
	{
		// this method just displays a file in a new window
		final JFrame dialog = new JFrame(fileName);
		
		//JEditorPane viewer = new JEditorPane();
		//viewer.setEditable(false);
		//JScrollPane jsp = new JScrollPane(viewer);
		try {
			Runtime.getRuntime().exec("rundll32 SHELL32.DLL,ShellExec_RunDLL " +fileName);
		} catch (Exception e) {
			//viewer.setText("Error opening " + fileName + ": " + e);
		}
		//dialog.getContentPane().add(jsp, BorderLayout.CENTER);
		
        
		
		//dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		//dialog.pack();
		//dialog.setSize(600, 600);
		//dialog.show();
	}
	
}