import java.util.*;

public class MyCounter {
  Hashtable counters = new Hashtable ();

  public MyCounter () 
  {}

  public void init(org.apache.xalan.extensions.XSLProcessorContext context, 
                   org.w3c.dom.Element elem) 
  {
    String name = elem.getAttribute("name");
    String value = elem.getAttribute("value");
    int val;
    try 
    {
      val = Integer.parseInt (value);
    } 
    catch (NumberFormatException e) 
    {
      e.printStackTrace ();
      val = 0;
    }
    counters.put (name, new Integer (val));
  }

  public int read(String name) 
  {
    Integer cval = (Integer) counters.get (name);
    return (cval == null) ? 0 : cval.intValue ();
  }

  public void incr(org.apache.xalan.extensions.XSLProcessorContext context, 
                   org.w3c.dom.Element elem) {
    String name = elem.getAttribute("name");
    Integer cval = (Integer) counters.get(name);
    int nval = (cval == null) ? 0 : (cval.intValue () + 1);
    counters.put (name, new Integer (nval));
  }
}
