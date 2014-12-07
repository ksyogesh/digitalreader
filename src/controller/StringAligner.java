package controller;

//*****************************************************************************
/*
** FILE:   StringAligner.java
**
** (c) 2001 Steve Withall.
**
** HISTORY:
**    06Jan01  stevew  Created.
*/


//*****************************************************************************
/** <p>Utility class for prepending spaces as required to make a given integer
 *  or string up to a given length.</p>
 *
 *  <p>In future, we might wish to improve sophistication by using the facilities
 *  of the standard DecimalFormat class.</p>
 */
public abstract class StringAligner
{
    //*****************************************************************************
    /** <p>Convert an integer to a string, adding as many leading spaces as are
     *  needed to bring it up to the given minimum length.</p>
     *
     *  @param  InputInt            Integer to convert to string
     *  @param  InputMinimumLength  The minimum acceptable length of the result
     */
    static public String alignInt(int  InputInt,
                                  int  InputMinimumLength)
    {
        return (alignString(String.valueOf(InputInt), InputMinimumLength) );
    }

    //*****************************************************************************
    /** <p>Return a string of at least the given minimum length, by adding as many
     *  spaces to the front of the given string as are needed to achieve that length.</p>
     *
     *  @param  InputSourceString   String to convert to string
     *  @param  InputMinimumLength  The minimum acceptable length of the result
     */
    static public String alignString(String  InputSourceString,
                                     int     InputMinimumLength)
    {
        String  ResultString       = null;
        int     SourceStringLength = InputSourceString.length();
        if (SourceStringLength >= InputMinimumLength)
            ResultString = InputSourceString;
        else
        {
            StringBuffer  ResultStringBuffer = new StringBuffer();
            for (int SpaceIndex = 0;
                 SpaceIndex < (InputMinimumLength - SourceStringLength);
                 SpaceIndex++)
            {
                ResultStringBuffer.append(' ');
            }
            ResultStringBuffer.append(InputSourceString);
            ResultString = ResultStringBuffer.toString();
        }
        return ResultString;
    }
}

//*****************************************************************************