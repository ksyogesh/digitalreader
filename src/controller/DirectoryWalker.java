package controller;

//*****************************************************************************
/*
** FILE:   DirectoryWalker.java
**
** (c) 2001 Steve Withall.
**
** HISTORY:
**    04Jan01  stevew  Created.
*/


//*****************************************************************************
/** An interface to be implemented by any object that may walk a tree of
 *  Directorys, that is that may be passed into the walk() method of a
 *  Directory.
*/
public interface DirectoryWalker
{
    //*****************************************************************************
    /** Process the given node. The meaning of 'process' will be dependent on the
     *  the type of walker.
     *
     *  @param  InputDirectoryName  The name of the directory on which the walker
     *                               is to act
     */
    public void processDirectory(String  InputDirectoryName) throws Exception;
}

//*****************************************************************************