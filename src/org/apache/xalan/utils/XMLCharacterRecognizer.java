package org.apache.xalan.utils;

public class XMLCharacterRecognizer
{
  /**
   * Returns whether the specified <var>ch</var> conforms to the XML 1.0 definition
   * of whitespace.  Refer to <A href="http://www.w3.org/TR/1998/REC-xml-19980210#NT-S">
   * the definition of <CODE>S</CODE></A> for details.
   * @param   ch      Character to check as XML whitespace.
   * @return          =true if <var>ch</var> is XML whitespace; otherwise =false.
   */
  public static boolean isWhiteSpace(char ch)
  {
    return (ch == 0x20) || (ch == 0x09) || (ch == 0xD) || (ch == 0xA);
  }

  /**
   * Tell if the string is whitespace.
   * @param   string      String to be trimmed.
   * @return              The trimmed string.
   */
  public static boolean isWhiteSpace(char ch[], int start, int length)
  {
    int end = start+length;
    for(int s = start;  s < end;  s++)
    {
      if (!isWhiteSpace(ch[s]))
        return false;
    }
    return true;
  }
  
  /**
   * Tell if the string is whitespace.
   * @param   string      String to be trimmed.
   * @return              The trimmed string.
   */
  public static boolean isWhiteSpace(StringBuffer buf)
  {
    int n = buf.length();
    for(int i = 0;  i < n;  i++)
    {
      if (!isWhiteSpace(buf.charAt(i)))
        return false;
    }
    return true;
  }
  
}
