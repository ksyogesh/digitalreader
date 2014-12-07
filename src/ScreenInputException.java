//*****************************************************************************
/*
** FILE:   ScreenInputException.java
**
** (c) 2001 Steve Withall.
**
** HISTORY:
**    06Jan01  stevew  Created.
*/


import javax.swing.JComponent;

//*****************************************************************************
/** An exception to be thrown as a result of an error in entry of data on a
 *  screen. Its salient feature is the ability to record the JComponent whose
 *  data caused the error (so that the code that handles the exception may
 *  request this component to take focus). Note that we only handle JComponents,
 *  so we're restricted to supporting Swing-based components.
 */
public class ScreenInputException extends Exception
{
    /** The component that caused the exception. */
    protected JComponent  ComponentInError = null;

    //*****************************************************************************
    /** Constructor.
     *
     *  @param  InputMessageText       The text of the exception message
     *  @param  InputComponentInError  The component that caused the exception
     */
    public ScreenInputException(String      InputMessageText,
                                JComponent  InputComponentInError)
    {
        super(InputMessageText);
        ComponentInError = InputComponentInError;
    }

    //*****************************************************************************
    /** Ask the component in error to take focus.
     */
    public void requestComponentFocus()
    {
        if (ComponentInError != null)
            ComponentInError.requestFocus();
    }

    //*****************************************************************************
    /** Get the component that caused the exception.
     *
     *  @return  The component that caused the exception
     */
    public JComponent getComponentInError()
    {
        return ComponentInError;
    }
}

//*****************************************************************************