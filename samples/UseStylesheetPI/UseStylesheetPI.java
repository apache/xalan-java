// Imported TraX classes
import org.apache.trax.Processor; 
import org.apache.trax.Templates;
import org.apache.trax.Transformer; 
import org.apache.trax.Result;
import org.apache.trax.ProcessorException; 
import org.apache.trax.ProcessorFactoryException;
import org.apache.trax.TransformException; 

// Imported SAX classes
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

// Imported java.io classes
import java.io.FileOutputStream;
import java.io.IOException;	

public class UseStylesheetPI
{
  public static void main(String[] args)
	throws ProcessorException, ProcessorFactoryException, 
           TransformException, SAXException, IOException	   
	{
	  String media= null , title = null, charset = null;
	  try
	  {	
      Processor processor = Processor.newInstance("xslt");
      InputSource[] stylesheet = processor.getAssociatedStylesheets
		  (new InputSource("fooX.xml"),media, title, charset);
      Templates templates = processor.processMultiple(stylesheet);

		Transformer transformer = templates.newTransformer();

		transformer.transform(new InputSource("fooX.xml"), new Result(new java.io.FileOutputStream("foo.out")));
	  }
  	  catch (SAXException se)
	  {
		System.err.println(se.toString());
	    se.printStackTrace();
	    Exception e = se.getException();
		if(e!=null)
		{
			e.printStackTrace();
		}
	  }
	  catch (Exception e)
	  {
		System.err.println(e.toString());
	    e.printStackTrace();}
	  }
}
