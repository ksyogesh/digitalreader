//*****************************************************************************
/*
** FILE:   Directory.java
**
** (c) 2001 Steve Withall.
**
** HISTORY:
**    04Jan01  stevew  Created.
*/


import java.io.File;

//*****************************************************************************
/** A class for performing processing on a directory in a file system.
*/
public class Directory
{
    /** The name of this directory. */
    protected String   DirectoryName = null;

    //*****************************************************************************
    /** Construct a node with a path name.
     *
     *  @param  InputDirectoryName  The path name of the directory
     */
    public Directory(String  InputDirectoryName)
    {
        DirectoryName = InputDirectoryName;
    }

    //*****************************************************************************
    /** Default constructor.
     */
    public Directory()
    {
    }

    //*****************************************************************************
    /** Walk this node. The given DirectoryWalker is passed around and allowed
     *  to  visit every directory in the tree. What it does for each directory
     *  depends on the nature of the DirectoryWalker.
     *
     *  @param  InputNodeWalker  The walker whose processNode() method is to be
     *                            invoked for this node and each of its children
     */
    public void walk(DirectoryWalker  InputDirectoryWalker) throws Exception
    {
        InputDirectoryWalker.processDirectory(DirectoryName);

        walkChildren(InputDirectoryWalker);
    }

    //*****************************************************************************
    /** Walk this node's children.
     *
     *  @param  InputNodeWalker  The walker whose processNode() method is to be
     *                            invoked for each child
     */
    public void walkChildren(DirectoryWalker  InputDirectoryWalker) throws Exception
    {
        File          DirectoryFile    = new File(DirectoryName);
        File[]        DirContentFile   = DirectoryFile.listFiles();
        File          CurrentFile      = null;
        Directory  CurrentDirectory = null;
        for (int FileIndex = 0; FileIndex < DirContentFile.length; FileIndex++)
        {
            CurrentFile = DirContentFile[FileIndex];
            if (CurrentFile.isDirectory() )
            {
                CurrentDirectory = new Directory(CurrentFile.getPath() );
                CurrentDirectory.walk(InputDirectoryWalker);
            }
        }
    }

    //*****************************************************************************
    /** Set the name of this directory.
     *
     *  @param  NodeName   The name of this directory
     */
    public void setName(String  InputDirectoryName)
    {
        DirectoryName = InputDirectoryName;
    }

    //*****************************************************************************
    /** Get the name of this directory.
     *
     *  @return  The name of this directory
     */
    public String getName()
    {
        return DirectoryName;
    }
}

//*****************************************************************************