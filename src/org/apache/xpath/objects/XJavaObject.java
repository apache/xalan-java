package org.apache.xpath.objects;

/** Wrap an arbitrary Java object for passing through the XPath
 * datatype system.
 * 
 * %REVIEW% This is currently being used to wrap XSequences
 * in FuncData.java. Makes some sense because a sequence can contain
 * non-XPath datatypes, and that's exactly what FuncData is returning.
 * But it feels like we'd prefer an XSequenceObject...
 * 
 * @author keshlam
 * @since Jul 24, 2002
 */
public class XJavaObject extends XObject
{
	Object m_obj=null;
	
	public XJavaObject(Object obj)
	{
		m_obj=obj;
	}
	
	public Object object()
	{
		return m_obj;
	}
	
  /**
   * Forces the object to release it's resources.  This is more harsh than
   * detach().
   */
  public void destruct()
  {

    if (null != m_obj)
    {
      allowDetachToRelease(true);
      detach();

      m_obj = null; //******
    }
  }
  
}
