/*
** FILE:   GrepFrame.java
**
** (c) 2001 Steve Withall.
**
** Parts copyright Steven R. Brandt/Stevesoft.
**
** This software comes without express or implied warranty.
** No claim is made about the suitability of this software for any
** purpose and neither Steven Brandt nor SteveSoft shall be liable
** for damages suffered by the user of this software.
**
** HISTORY:
**    02Jan01  stevew  Created, based on Steven Brandt's guigrep.
*/


import com.stevesoft.pat.NonDirFileRegex;
import com.stevesoft.pat.Regex;
import com.stevesoft.pat.RegRes;

import javax.swing.plaf.metal.MetalLookAndFeel;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleContext;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JViewport;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;

import java.io.File;
import java.io.FileWriter;

import java.util.Calendar;
import java.util.GregorianCalendar;

import java.lang.Character;

//*****************************************************************************
/** A frame to search the contents of a set of files with pathnames based
 *  on the current directory (as per 'grep') and display the results.
 */
public class GrepFrame extends    JInternalFrame
                       implements ActionListener
{
    /** The pattern we're searching for. */
    Regex                SearchPattern;

    /** This class's main frame. */
    GrepFrame               MainFrame;

    /** The pathname of the file currently being used. */
    String               FilePathnameString = new String();

    /** The walker that performs the search on each directory. */
    GrepDirectoryWalker  GrepSubDirWalker = new GrepDirectoryWalker();

    //-----------------------------------------------------------------------------
    // First line.
    JButton        SearchButton       = new JButton("Search");
    JTextField     SearchPatternField = new JTextField(30);
    JTextField     FilePatternField   = new JTextField(10);

   // JButton        AboutButton        = new JButton("About");
    //JButton        ExitButton         = new JButton("Exit");

    // Second line.
    JTextField     DirPatternField    = new JTextField(40);
    JButton        BrowseButton       = new JButton("Browse");
    JCheckBox      SubDirsTooCheckBox = new JCheckBox("Search sub-directories?");

    JButton        SaveResultsButton  = new JButton("Save results");

    /** The document into which the results of our search are placed. Note that
     *  this must be declared *after* GrepSubDirWalker, because it uses the latter's
     *  style context. */
    DefaultStyledDocument  ResultsDocument = new DefaultStyledDocument(GrepSubDirWalker.getResultsStyleContext());

    /** The results display area itself. */
    JTextPane              ResultsTextPane = new JTextPane(ResultsDocument);

    //*****************************************************************************
    /** Constructor.
     *
     *  @param  InputFrameTitle     The title to display at the top of the frame
     */
    public GrepFrame()
    {
        super();

        MainFrame       = this;

	    Container  TheContentPane = getContentPane();
	    TheContentPane.setLayout(new BorderLayout());

        initTopPanel();
        initTextPane();
        calculateBounds();
	    show();

        //addWindowListener(new GrepFrameListener() );
    }

    //*****************************************************************************
    /** Calculate and set the size and position of this frame.
     */
    protected void calculateBounds()
    {
        Toolkit    DefaultToolkit = Toolkit.getDefaultToolkit();
        Dimension  ScreenSize     = DefaultToolkit.getScreenSize();
        int        FrameWidth     = ScreenSize.width  - 100;
        int        FrameHeight    = ScreenSize.height - 100;
        setBounds(50, 50, FrameWidth, FrameHeight);
    }

    //*****************************************************************************
    /** Initialise the input/control panel at the top of the frame.
     */
    private void initTopPanel()
    {
        GridBagConstraints TopPanelConstraints = new GridBagConstraints();
        GridBagLayout      TopPanelLayout      = new GridBagLayout();
        JPanel             TopPanel            = new JPanel();
        TopPanel.setLayout(TopPanelLayout);

        TopPanelConstraints.insets     = new Insets(4, 4, 4, 4);
        TopPanelConstraints.anchor     = GridBagConstraints.WEST;
        TopPanelConstraints.gridwidth  = 1;
        TopPanelConstraints.gridheight = 1;
        TopPanelConstraints.fill       = GridBagConstraints.HORIZONTAL;
        TopPanelConstraints.weighty    = 0.0;

        // The first line.
        TopPanelConstraints.gridy   = 0;
        TopPanelConstraints.gridx   = 0;
        TopPanelConstraints.weightx = 1.0;
        Container  FirstLineContainer = buildFirstLine();
        TopPanel.add(FirstLineContainer, TopPanelConstraints);

        TopPanelConstraints.gridx   = 1;
        TopPanelConstraints.weightx = 0.0;
      //  TopPanel.add(AboutButton, TopPanelConstraints);

        TopPanelConstraints.gridx   = 2;
        TopPanelConstraints.weightx = 0.0;
   //     TopPanel.add(ExitButton, TopPanelConstraints);

        // The second line.
        TopPanelConstraints.gridy     = 1;
        TopPanelConstraints.gridx     = 0;
        TopPanelConstraints.weightx   = 1.0;
        Container  SecondLineContainer = buildSecondLine();
        TopPanel.add(SecondLineContainer, TopPanelConstraints);

        TopPanelConstraints.gridx     = 1;
        TopPanelConstraints.weightx   = 0.0;
        TopPanelConstraints.gridwidth = 2;
        TopPanel.add(SaveResultsButton, TopPanelConstraints);

    	BrowseButton.addActionListener(new BrowseButtonListener() );
    	SearchButton.addActionListener(new SearchButtonListener() );
    	//ExitButton.addActionListener(new ExitButtonListener() );
    	//AboutButton.addActionListener(new AboutButtonListener() );
    	SaveResultsButton.addActionListener(new SaveButtonListener() );

	    getContentPane().add("North", TopPanel);
    }

    //*****************************************************************************
    /** Construct a container for the main components of the first line of the
     *  screen.
     *
     *  @return  A new container of all the main first line components
     */
    private Container buildFirstLine()
    {
        GridBagConstraints FirstLineConstraints = new GridBagConstraints();
        GridBagLayout      FirstLineLayout      = new GridBagLayout();
        Container          FirstLineContainer   = new Container();
        FirstLineContainer.setLayout(FirstLineLayout);

        FirstLineConstraints.insets     = new Insets(0, 2, 2, 0);
        FirstLineConstraints.anchor     = GridBagConstraints.WEST;
        FirstLineConstraints.gridwidth  = 1;
        FirstLineConstraints.gridheight = 1;
        FirstLineConstraints.fill       = GridBagConstraints.HORIZONTAL;
        FirstLineConstraints.weightx    = 0.0;
        FirstLineConstraints.weighty    = 0.0;
        FirstLineConstraints.gridy      = 0;
        FirstLineConstraints.gridx      = 0;
        FirstLineContainer.add(SearchButton, FirstLineConstraints);
        SearchButton.setNextFocusableComponent(SearchPatternField);

        FirstLineConstraints.gridx = 1;
        FirstLineContainer.add(new JLabel("for"), FirstLineConstraints);

        FirstLineConstraints.gridx = 2;
        FirstLineConstraints.weightx    = 1.0;
        FirstLineContainer.add(SearchPatternField, FirstLineConstraints);
        SearchPatternField.setNextFocusableComponent(FilePatternField);
        SearchPatternField.addActionListener(this);

        FirstLineConstraints.gridx = 3;
        FirstLineConstraints.weightx    = 0.0;
        FirstLineContainer.add(new JLabel("in files"), FirstLineConstraints);

        FirstLineConstraints.gridx = 4;
        FirstLineConstraints.weightx    = 0.3;
        FilePatternField.setText("*.java");  //TEMP
        FirstLineContainer.add(FilePatternField, FirstLineConstraints);
        FilePatternField.setNextFocusableComponent(DirPatternField);
        FilePatternField.addActionListener(this);

        return FirstLineContainer;
    }

    //*****************************************************************************
    /** Construct a container for the main components of the second line of the
     *  screen.
     *
     *  @return  A new container of all the main second line components
     */
    private Container buildSecondLine()
    {
        GridBagConstraints SecondLineConstraints = new GridBagConstraints();
        GridBagLayout      SecondLineLayout      = new GridBagLayout();
        Container          SecondLineContainer   = new Container();
        SecondLineContainer.setLayout(SecondLineLayout);

        SecondLineConstraints.insets     = new Insets(0, 2, 2, 0);
        SecondLineConstraints.anchor     = GridBagConstraints.WEST;
        SecondLineConstraints.gridwidth  = 1;
        SecondLineConstraints.gridheight = 1;
        SecondLineConstraints.fill       = GridBagConstraints.HORIZONTAL;
        SecondLineConstraints.weightx    = 0.0;
        SecondLineConstraints.weighty    = 0.0;
        SecondLineConstraints.gridy      = 0;
        SecondLineConstraints.gridx      = 0;
        SecondLineContainer.add(new JLabel("in directory"), SecondLineConstraints);

        SecondLineConstraints.gridx = 1;
        SecondLineConstraints.weightx    = 1.0;
        SecondLineContainer.add(DirPatternField, SecondLineConstraints);
        DirPatternField.setNextFocusableComponent(BrowseButton);
        DirPatternField.addActionListener(this);

        SecondLineConstraints.gridx = 2;
        SecondLineConstraints.weightx    = 0.0;
        SecondLineContainer.add(BrowseButton, SecondLineConstraints);
        BrowseButton.setNextFocusableComponent(SubDirsTooCheckBox);

        SecondLineConstraints.gridx  = 3;
        SecondLineConstraints.insets = new Insets(0, 6, 2, 0);
        SecondLineContainer.add(SubDirsTooCheckBox, SecondLineConstraints);
        SubDirsTooCheckBox.setSelected(true);
        SubDirsTooCheckBox.setNextFocusableComponent(SearchButton);
        return SecondLineContainer;
    }

    //*****************************************************************************
    /** Initialise the text pane.
     */
    public void initTextPane()
    {
	    setResultsFont();
	    ResultsTextPane.setBorder(null);

        // Scroller.
    	JScrollPane  ResultsScrollPane = new JScrollPane();
    	JViewport    ResultsViewport   = ResultsScrollPane.getViewport();
    	ResultsViewport.add(ResultsTextPane);

    	JPanel       ResultsPanel = new JPanel();
    	ResultsPanel.setLayout(new BorderLayout());
    	ResultsPanel.add("Center", ResultsScrollPane);

    	getContentPane().add("Center", ResultsPanel);
    }

    //*****************************************************************************
    /** Set the font used for displaying the results. The size of the font is
     *  determined from that of the current metal user text font.
     */
    public void setResultsFont()
    {
        Font  UserTextFont = MetalLookAndFeel.getUserTextFont();
        if (ResultsTextPane != null)
	        ResultsTextPane.setFont(new Font("Monospaced", Font.PLAIN, UserTextFont.getSize() ));
    }

    //*****************************************************************************
    /** Perform a search according to the values specified on screen.
     */
    public void performSearch()
    {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) );

        try
        {
            // Create the search pattern.
            String  SearchPatternString = SearchPatternField.getText();
            if (    SearchPatternString == null
                 || SearchPatternString.equals("") )
                throw new ScreenInputException("No search pattern entered",
                                               SearchPatternField);
            SearchPattern = new Regex(SearchPatternString);
            SearchPattern.optimize();

            // Build the definition of the root directory to search.
            String  FilePatternString = FilePatternField.getText();
            if (    FilePatternString == null
                 || FilePatternString.equals("") )
                throw new ScreenInputException("No file pattern entered",
                                               FilePatternField);

            String  DirPathString     = DirPatternField.getText();
            if (    DirPathString == null
                 || DirPathString.equals("") )
                throw new ScreenInputException("No directory specified",
                                               DirPatternField);
            File  DirectoryFile = new File(DirPathString);
            if (!DirectoryFile.exists() )
                throw new ScreenInputException("Directory '" + DirPathString + "'does not exist",
                                               DirPatternField);

            // Prepare the walker that performs the grep on each directory.
            GrepSubDirWalker.prepareSearch(SearchPattern,
                                           FilePatternString,
                                           DirPathString,
                                           ResultsDocument);

            if (SubDirsTooCheckBox.isSelected() )
            {
                // Process named directory and its sub-directories.
                Directory  RootDirectory = new Directory(DirPathString);
                RootDirectory.walk(GrepSubDirWalker);
            }
            else
                // Process just the named directory.
                GrepSubDirWalker.processDirectory(DirPathString);

            GrepSubDirWalker.appendStatistics();  // Show statistics
        }
        catch (NoClassDefFoundError  InputException)
        {
            showPatMissingDialog();
        }
        catch (ScreenInputException  InputException)
        {
            InputException.requestComponentFocus();
            showExceptionDialog(InputException);
        }
        catch (Exception  InputException)
        {
            showExceptionDialog(InputException);
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR) );
    }

    //*****************************************************************************
    /** Construct file name of the form 'grep<yyyymmdd><pattern>.txt'.
     *
     *  @return  Default file name into which to save the current results
     */
    public String createDefaultSaveFileName()
    {
        StringBuffer  SaveFileNameBuffer = new StringBuffer("grep");

        // Add the date to the file name.
        GregorianCalendar  NowCalendar = new GregorianCalendar();
        int                Year        = NowCalendar.get(Calendar.YEAR);
        int                Month       = NowCalendar.get(Calendar.MONTH) + 1;
        int                Day         = NowCalendar.get(Calendar.DAY_OF_MONTH);
        SaveFileNameBuffer.append(Year);
        if (Month < 10)
            SaveFileNameBuffer.append('0');
        SaveFileNameBuffer.append(Month);
        if (Day < 10)
            SaveFileNameBuffer.append('0');
        SaveFileNameBuffer.append(Day);

        // Add the search pattern to the file name (excluding illegal characters).
        String  SearchPatternString = SearchPatternField.getText();
        for (int CharIndex = 0; CharIndex < SearchPatternString.length(); CharIndex++)
        {
            char  CurrentChar = SearchPatternString.charAt(CharIndex);
            if (Character.isJavaIdentifierPart(CurrentChar) )
                SaveFileNameBuffer.append(CurrentChar);
        }

        SaveFileNameBuffer.append(".txt");
        return (SaveFileNameBuffer.toString() );
    }

    //*****************************************************************************
    /** Pop up a dialog to display the given exception.
     *
     *  @param  InputException  The exception whose message is to be displayed
     */
    public void showExceptionDialog(Exception  InputException)
    {
        JOptionPane.showMessageDialog(MainFrame,
                                      InputException.getMessage(),
                                      "Error",
                                      JOptionPane.ERROR_MESSAGE);
    }

    //*****************************************************************************
    /** Pop up a dialog to explain that the pat software is missing.
     */
    public void showPatMissingDialog()
    {
        SearchButton.setEnabled(false);
        JOptionPane.showMessageDialog(MainFrame,
		                              "The PAT regular expression library is not accessible.\n"
                                        + "1. Make sure the software is installed.\n"
                                        + "2. Check it is included in the CLASSPATH.\n"
                                        + "For more details press the 'About' button.",
                                      "PAT software error",
                                      JOptionPane.ERROR_MESSAGE);
    }

    //*****************************************************************************
    /** Get the results from the last search in the form of a string.
     *
     *  @return  A string representation of the results
     */
    public String getResultsString()
    {
        String  ResultsString = null;
        try
        {
            ResultsString = ResultsDocument.getText(ResultsDocument.getStartPosition().getOffset(),
                                                    ResultsDocument.getLength() );
        }
        catch (BadLocationException  InputException)
        {
	        System.out.println("GrepFrame.getResultsString: Results document not initialised: "
	                               + InputException);
        }
        return ResultsString;
    }

    //*****************************************************************************
    /** Retrieve the text area in which the source is displayed and edited.
     *
     *  @return  An array of the actions supported by this class
     */
    static public Action[] getActions()
    {
        JTextPane  TempTextPane = new JTextPane();
	    return(TempTextPane.getActions() );
    }

    //*****************************************************************************
    /** An action has been passed to the frame itself.
     */
    public void actionPerformed(ActionEvent  InputEvent)
    {
        performSearch();
    }

    //*****************************************************************************
    // Button listener inner classes.
    //*****************************************************************************
    /** Handle pressing of the "Browse" button. Pop up a file select dialog to
     *  choose a directory to search.
     */
    class BrowseButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent  InputEvent)
        {
		    JFileChooser  SearchDirChooser = new JFileChooser(FilePathnameString);
   	        Dimension     ScreenSize       = Toolkit.getDefaultToolkit().getScreenSize();
            Dimension     ChooserDimension = SearchDirChooser.getPreferredSize();
            ChooserDimension.height = ScreenSize.height - 170;
            SearchDirChooser.setPreferredSize(ChooserDimension);
            SearchDirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            SearchDirChooser.setDialogTitle("Select directory to search");

		    int  ChooseState = SearchDirChooser.showOpenDialog(MainFrame);
		    if (ChooseState == JFileChooser.APPROVE_OPTION)
		    {
			    File  OpenFile = SearchDirChooser.getSelectedFile();
			    if (OpenFile == null)
		            JOptionPane.showMessageDialog(MainFrame,
		                                          "No directory chosen - none opened");
			    else
			        FilePathnameString = OpenFile.getPath();
		    }

    	    validate();

            DirPatternField.setText(FilePathnameString);
        }
    }

    //*****************************************************************************
    /** Handle pressing of the "Search" button. Perform a search as defined by the
     *  input values.
     */
    class SearchButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent  InputEvent)
        {
            performSearch();
        }
    }

    //*****************************************************************************
    /** Handle pressing of the "Exit" button. End the application.
     */
    class ExitButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent  InputEvent)
        {
  	        System.exit(0);
        }
    }

    //*****************************************************************************
    /** Handle pressing of the "About" button. Display credits.
     */
    class AboutButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent  InputEvent)
        {
            JOptionPane.showMessageDialog(MainFrame,
                "This file search utility is written by\n"
	      + "Steve Withall\n"
              + "and powered by the PAT regular expression\n"
	      + "library written by Steven R. Brandt.\n"
              + "See the PAT home page at\n"
	      + "http://javaregex.com",
                "About",
              JOptionPane.INFORMATION_MESSAGE);
        }
    }

    //*****************************************************************************
    /** Handle pressing of the "Save results" button. Save the displayed results to
     *  a file.
     */
    class SaveButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent  InputEvent)
        {
            // Construct file name of the form 'grep<yyyymmdd><pattern>.txt'.
            String        DefaultSaveFileName = createDefaultSaveFileName();
            File          DefaultSaveFile     = new File(FilePathnameString
                                                           + File.separatorChar
                                                           + DefaultSaveFileName);
		    JFileChooser  SaveFileChooser = new JFileChooser(DefaultSaveFile);
            SaveFileChooser.setDialogTitle("Select file into which to save search results");
            SaveFileChooser.setSelectedFile(DefaultSaveFile);
            SaveFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		    int  ChooseState = SaveFileChooser.showSaveDialog(MainFrame);
		    if (ChooseState == JFileChooser.APPROVE_OPTION)
		    {
			    File  SaveFile = SaveFileChooser.getSelectedFile();
			    if (SaveFile == null)
		            JOptionPane.showMessageDialog(MainFrame,
		                                          "No file chosen - no save performed");
			    else
			    {
                    String  ResultsString = getResultsString();
                    if (ResultsString == null)
    		            JOptionPane.showMessageDialog(MainFrame,
		                                              "No search results to save");
                    else
                    {
    			        String  SavePathString = SaveFile.getPath();
                        try
                        {
                            FileWriter  SaveFileWriter = new FileWriter(SavePathString);
                            SaveFileWriter.write(ResultsString);
                            SaveFileWriter.close();
                        }
                        catch (Exception  InputException)
                        {
        		            JOptionPane.showMessageDialog(MainFrame,
		                                                  "Error writing results file "
                                                             + SavePathString
                                                             + ": "
                                                             + InputException);
                        }
                    }
			    }
		    }
        }
    }

    //*****************************************************************************
    // Miscellaneous inner class.
    //*****************************************************************************
    /** Class to receive frame events, to allow us the opportunity to set the
     *  focus to the right field.
     */
    class GrepFrameListener extends WindowAdapter
    {
        public void windowActivated(WindowEvent  InputEvent)
        {
            // Check that pat is callable, and display a dialog if not.
            try
            {
                Class.forName("com.stevesoft.pat.Regex");
                SearchPatternField.requestFocus();
            }
            catch (ClassNotFoundException  InputException)
            {
                showPatMissingDialog();
            }
        }
    }
}

//*****************************************************************************
