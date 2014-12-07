//*****************************************************************************
/*
** FILE:   GrepDirectoryWalker.java
**
** (c) 2001 Steve Withall.
**
** HISTORY:
**    04Jan01  stevew  Created.
*/


import com.stevesoft.pat.NonDirFileRegex;
import com.stevesoft.pat.Regex;
import com.stevesoft.pat.RegRes;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import java.awt.Color;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

//*****************************************************************************
/** Gather statistics about a node tree: how many nodes of each type is
 *  present, how many elements of each type, and so on.
 */
public class GrepDirectoryWalker implements DirectoryWalker
{
    /** The pattern we're searching for. */
    Regex                  SearchPattern     = null;

    /** The pattern that defines the types of files to search (eg. '*.java'). */
    String                 FilePatternString = null;

    /** The path name of the search's root directory (used solely so we can
     *  highlight it differently in the display). */
    String                 RootDirPathString = null;

    /** The document into which the results of our search are placed. */
    DefaultStyledDocument  ResultsDocument   = null;

    //-----------------------------------------------------------------------------
    // Styles to be used in displaying the results.
    StyleContext  ResultsStyleContext = new StyleContext();

    Style         DefaultStyle;
    Style         MainStyle;
    Style         RootPathStyle;
    Style         FileNameStyle;
    Style         NormalContentStyle;
    Style         FoundTextStyle;
    Style         StatisticsStyle;

    //-----------------------------------------------------------------------------
    // Attributes for gathering search statistics.
    int  LineSearchCount = 0;
    int  FileSearchCount = 0;
    int  DirSearchCount  = 0;
    int  LineMatchCount  = 0;
    int  FileMatchCount  = 0;

    //*****************************************************************************
    /** Default constructor.
     */
    public GrepDirectoryWalker()
    {
        createStyles();
    }

    //*****************************************************************************
    /** Constructor.
     *
     *  @param  InputSearchPattern      The pattern to search for
     *  @param  InputFilePatternString  A pattern defining the types of files to
     *                                   search (eg. '*.java')
     *  @param  InputRootDirPathString  The path name of the search's root directory
     *                                   (used solely so we can highlight it
     *                                   differently in the display)
     *  @param  InputResultsDocument    The text pane into which place the search
     *                                   results
     */
    public GrepDirectoryWalker(Regex                  InputSearchPattern,
                                  String                 InputFilePatternString,
                                  String                 InputRootDirPathString,
                                  DefaultStyledDocument  InputResultsDocument)
    {
        createStyles();
        prepareSearch(InputSearchPattern,
                      InputFilePatternString,
                      InputRootDirPathString,
                      InputResultsDocument);
    }

    //*****************************************************************************
    /** Set up the styles used for the results.
     */
    private void createStyles()
    {
        DefaultStyle  = ResultsStyleContext.getStyle(StyleContext.DEFAULT_STYLE);
        MainStyle     = ResultsStyleContext.addStyle("MainStyle", DefaultStyle);
        StyleConstants.setFontFamily(MainStyle, "monospaced");
        StyleConstants.setFontSize(MainStyle, 12);

        RootPathStyle = ResultsStyleContext.addStyle("RootPath", null);
        StyleConstants.setForeground(RootPathStyle, new Color(166, 171, 218) );  // Faded blue

        FileNameStyle = ResultsStyleContext.addStyle("FileName", null);
        StyleConstants.setForeground(FileNameStyle, Color.blue);

        NormalContentStyle = ResultsStyleContext.addStyle("NormalContent", null);

        FoundTextStyle = ResultsStyleContext.addStyle("FoundText", null);
        StyleConstants.setForeground(FoundTextStyle, Color.red);

        StatisticsStyle = ResultsStyleContext.addStyle("Statistics", null);
        StyleConstants.setForeground(StatisticsStyle, new Color(0, 139, 0) );  // Dark green
    }

    //*****************************************************************************
    /** Set up the values that define the search parameters - except for the
     *  directory to search, because there may be several of them and each one is
     *  searched using its own call to processDirectory().
     *
     *  @param  InputSearchPattern      The pattern to search for
     *  @param  InputFilePatternString  A pattern defining the types of files to
     *                                   search (eg. '*.java')
     *  @param  InputRootDirPathString  The path name of the search's root directory
     *                                   (used solely so we can highlight it
     *                                   differently in the display)
     *  @param  InputResultsDocument    The text pane into which place the search
     *                                   results
     */
    public void prepareSearch(Regex                  InputSearchPattern,
                              String                 InputFilePatternString,
                              String                 InputRootDirPathString,
                              DefaultStyledDocument  InputResultsDocument)
    {
        SearchPattern     = InputSearchPattern;
        FilePatternString = InputFilePatternString;
        RootDirPathString = InputRootDirPathString + File.separatorChar;
        ResultsDocument   = InputResultsDocument;

        clearResults();
        resetStatistics();
    }

    //*****************************************************************************
    /** Clear the results text pane.
     */
    public void clearResults()
    {
        try
        {
            ResultsDocument.remove(0, ResultsDocument.getLength() );  // Clear the document
        }
        catch(BadLocationException  InputException)
        {
            System.out.println("GrepDirectoryWalker.clearResults: Error when clearing results document: "
                                  + InputException);
        }
    }

    //*****************************************************************************
    /** Reset the statistical counters to their initial values.
     */
    public void resetStatistics()
    {
        LineSearchCount = 0;
        FileSearchCount = 0;
        DirSearchCount  = 0;
        LineMatchCount  = 0;
        FileMatchCount  = 0;
    }

    //*****************************************************************************
    /** Process the given directory - which means perform the required search on
     *  the required files in this directory.
     *
     *  @param  InputDirPath  The pathname of the directory on which the walker is
     *                         to act
     */
    public void processDirectory(String  InputDirPath) throws Exception
    {
        DirSearchCount++;

        // Build the definition of the files in the directory which are to be searched.
        String  DirPatternString = null;
        if (FilePatternString == null)
            DirPatternString = InputDirPath;
        else
        {
            if ((InputDirPath.charAt(InputDirPath.length() - 1) ) == File.separatorChar)
                DirPatternString = InputDirPath + FilePatternString;
            else
                DirPatternString = InputDirPath + File.separatorChar + FilePatternString;
        }

        String    CurrentFileName  = null;
        String    PreviousFileName = null;
        String[]  TargetFileName = NonDirFileRegex.list(DirPatternString);
        try
        {
            for (int FileIndex = 0; FileIndex < TargetFileName.length; FileIndex++)
            {
                CurrentFileName = TargetFileName[FileIndex];
                BufferedReader  TargetFileReader = new BufferedReader(new FileReader(CurrentFileName));
                String          CurrentLine      = TargetFileReader.readLine();
                int             CurrentLineNum   = 1;
                while (CurrentLine != null)
                {
                    if (SearchPattern.search(CurrentLine) )
                    {
                        RegRes  SearchResult = SearchPattern.result();

                        if (    PreviousFileName == null
                             || !CurrentFileName.equals(PreviousFileName) )
                        {
                            appendStyledText(RootDirPathString, RootPathStyle);
                            //TEMP The '+ 2' below is hideous; we must find a better way.
                            appendStyledText(CurrentFileName.substring(RootDirPathString.length() + 2)
                                                + "\n",
                                             FileNameStyle);
                            PreviousFileName = CurrentFileName;
                            FileMatchCount++;
                        }
                        appendStyledInt(CurrentLineNum, 4, RootPathStyle);
                        appendStyledText(": ", RootPathStyle);
                        appendStyledText(SearchResult.left().replace('\t',' '),
                                         NormalContentStyle);
                        appendStyledText(SearchResult.substring().replace('\t',' '),
                                         FoundTextStyle);
                        searchLineRemainder(SearchResult.right() );

                        LineMatchCount++;
                    }
                    CurrentLineNum++;
                    LineSearchCount++;
                    CurrentLine = TargetFileReader.readLine();
                }

                FileSearchCount++;
                TargetFileReader.close();
            }
        }
        catch(Exception  InputException)
        {
            if (    InputException.getMessage() == null
                 || InputException.getMessage().equals("") )
            {
                System.out.println("GrepDirectoryWalker.processDirectory: Error in grep: "
                                  + InputException);
            }
            throw InputException;
        }
    }

    //*****************************************************************************
    /** Search the given string (on the assumption that it is the remainder of a
     *  line in which a match has already been found), and add it to the display
     *  pane with the matches highlighted. This method calls itself recursively,
     *  so it will highlight *every* occurrence of the search pattern, no matter
     *  how many there are.
     *
     *  @param  InputLineRemainder  The remainder of the line to be search
     */
    public void searchLineRemainder(String  InputLineRemainder)
    {
        if (SearchPattern.search(InputLineRemainder) )
        {
            RegRes  SearchResult = SearchPattern.result();

            appendStyledText(SearchResult.left().replace('\t',' '),
                             NormalContentStyle);
            appendStyledText(SearchResult.substring().replace('\t',' '),
                             FoundTextStyle);
            searchLineRemainder(SearchResult.right() );
        }
        else
            appendStyledText(InputLineRemainder.replace('\t',' ') + "\n",
                             NormalContentStyle);
    }

    //*****************************************************************************
    /** Add an integer to the text pane, with leading spaces as required to make
     *  it up to the given minimum length.
     *
     *  @param  InputInt            The integer to add
     *  @param  InputMinimumLength  The minimum string length to add to the text pane
     *  @param  InputStyle          The style to apply to the text
     */
    public void appendStyledInt(int    InputInt,
                                int    InputMinimumLength,
                                Style  InputStyle)
    {
        appendStyledText(StringAligner.alignInt(InputInt, InputMinimumLength),
                         InputStyle);
    }

    //*****************************************************************************
    /** Add a chunk of styled text to the text pane.
     *
     *  @param  InputString  The text to add
     *  @param  InputStyle   The style to apply to the text
     */
    public void appendStyledText(String  InputString,
                                 Style   InputStyle)
    {
        try
        {
            int  DocLength = ResultsDocument.getLength();
            ResultsDocument.insertString(DocLength, InputString, null);
            ResultsDocument.setCharacterAttributes(DocLength,
                                                   InputString.length(),
                                                   InputStyle,
                                                   false);
        }
        catch(BadLocationException  InputException)
        {
            System.out.println("GrepDirectoryWalker.appendStyledText: Bad location exception:"
                                  + InputException);
        }
    }

    //*****************************************************************************
    /** Add to the text pane the statistics we've gathered.
     */
    public void appendStatistics()
    {
        appendStyledText("Statistics:          Lines       Files      Directories\n",
                         StatisticsStyle);

        appendStyledText("    Matched:  ", StatisticsStyle);
        appendStyledInt(LineMatchCount, 12, StatisticsStyle);
        appendStyledInt(FileMatchCount, 12, StatisticsStyle);
        appendStyledText("\n", StatisticsStyle);

        appendStyledText("    Searched: ", StatisticsStyle);
        appendStyledInt(LineSearchCount, 12, StatisticsStyle);
        appendStyledInt(FileSearchCount, 12, StatisticsStyle);
        appendStyledInt(DirSearchCount,  12, StatisticsStyle);
    }

    //*****************************************************************************
    /** Retrieve the style context used in the text pane.
     *
     *  @return  The style context used in the text pane
     */
    public StyleContext getResultsStyleContext()
    {
        return(ResultsStyleContext);
    }
}

//*****************************************************************************
