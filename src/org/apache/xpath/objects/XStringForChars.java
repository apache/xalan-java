package org.apache.xpath.objects;

import org.apache.xml.utils.FastStringBuffer;
import org.apache.xml.utils.XMLString;
import org.apache.xml.utils.XMLStringFactory;
import org.apache.xml.utils.XMLCharacterRecognizer;
import java.util.Locale;


/**
 * This class will wrap a FastStringBuffer and allow for
 */
public class XStringForChars extends XString
{
  /** The start position in the fsb. */
  int m_start;
  
  /** The length of the string. */
  int m_length;
  
  protected String m_strCache = null;
  
  /**
   * Construct a XNodeSet object.
   *
   * @param val FastStringBuffer object this will wrap, must be non-null.
   * @param start The start position in the array.
   * @param length The number of characters to read from the array.
   */
  public XStringForChars(char[] val, int start, int length)
  {
    super(val);
    m_start = start;
    m_length = length;
    if(null == val)
      throw new IllegalArgumentException(
                          "The FastStringBuffer argument can not be null!!");
  }


  /**
   * Construct a XNodeSet object.
   *
   * @param val String object this will wrap.
   */
  private XStringForChars(String val)
  {
    super(val);
    throw new IllegalArgumentException(
                      "XStringForChars can not take a string for an argument!");
  }
  
  /**
   * Cast result object to a string.
   *
   * @return The string this wraps or the empty string if null
   */
  public FastStringBuffer fsb()
  {
    throw new RuntimeException("fsb() not supported for XStringForChars!");
  }
  
  /**
   * Cast result object to a string.
   *
   * @return The string this wraps or the empty string if null
   */
  public void appendToFsb(org.apache.xml.utils.FastStringBuffer fsb)
  {
    fsb.append((char[])m_obj, m_start, m_length);
  }

  
  /**
   * Tell if this object contains a java String object.
   * 
   * @return true if this XMLString can return a string without creating one.
   */
  public boolean hasString()
  {
    return (null != m_strCache);
  }

  
  /**
   * Cast result object to a string.
   *
   * @return The string this wraps or the empty string if null
   */
  public String str()
  {
    if(null == m_strCache)
      m_strCache = new String((char[])m_obj, m_start, m_length);
    
    return m_strCache;
  }

  /**
   * Directly call the
   * characters method on the passed ContentHandler for the
   * string-value. Multiple calls to the
   * ContentHandler's characters methods may well occur for a single call to
   * this method.
   *
   * @param ch A non-null reference to a ContentHandler.
   *
   * @throws org.xml.sax.SAXException
   */
  public void dispatchCharactersEvents(org.xml.sax.ContentHandler ch)
      throws org.xml.sax.SAXException
  {
    ch.characters((char[])m_obj, m_start, m_length);
  }
      
  /**
   * Directly call the
   * comment method on the passed LexicalHandler for the
   * string-value.
   *
   * @param lh A non-null reference to a LexicalHandler.
   *
   * @throws org.xml.sax.SAXException
   */
  public void dispatchAsComment(org.xml.sax.ext.LexicalHandler lh)
      throws org.xml.sax.SAXException
  {
    lh.comment((char[])m_obj, m_start, m_length);
  }
  
  /**
   * Returns the length of this string.
   *
   * @return  the length of the sequence of characters represented by this
   *          object.
   */
  public int length()
  {
    return m_length;
  }

  /**
   * Returns the character at the specified index. An index ranges
   * from <code>0</code> to <code>length() - 1</code>. The first character
   * of the sequence is at index <code>0</code>, the next at index
   * <code>1</code>, and so on, as for array indexing.
   *
   * @param      index   the index of the character.
   * @return     the character at the specified index of this string.
   *             The first character is at index <code>0</code>.
   * @exception  IndexOutOfBoundsException  if the <code>index</code>
   *             argument is negative or not less than the length of this
   *             string.
   */
  public char charAt(int index)
  {
    return ((char[])m_obj)[index];
  }

  /**
   * Copies characters from this string into the destination character
   * array.
   *
   * @param      srcBegin   index of the first character in the string
   *                        to copy.
   * @param      srcEnd     index after the last character in the string
   *                        to copy.
   * @param      dst        the destination array.
   * @param      dstBegin   the start offset in the destination array.
   * @exception IndexOutOfBoundsException If any of the following
   *            is true:
   *            <ul><li><code>srcBegin</code> is negative.
   *            <li><code>srcBegin</code> is greater than <code>srcEnd</code>
   *            <li><code>srcEnd</code> is greater than the length of this
   *                string
   *            <li><code>dstBegin</code> is negative
   *            <li><code>dstBegin+(srcEnd-srcBegin)</code> is larger than
   *                <code>dst.length</code></ul>
   * @exception NullPointerException if <code>dst</code> is <code>null</code>
   */
  public void getChars(int srcBegin, int srcEnd, char dst[], int dstBegin)
  {
    System.arraycopy((char[])m_obj, m_start+srcBegin, dst, dstBegin, m_start+srcEnd);
  }
  
}