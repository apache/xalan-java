// Imported TraX classes
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;

// Imported SAX classes
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

// Imported java.io classes
import java.io.FileOutputStream;
import java.io.IOException;	

public class UseStylesheetPI
{
  public static void main(String[] args)
	throws TransformerException, TransformerConfigurationException, 
         SAXException, IOException	   
	{
	  String media= null , title = null, charset = null;
	  try
	  {	
    	TransformerFactory tFactory = TransformerFactory.newInstance();
      Source stylesheet = tFactory.getAssociatedStylesheet
        (new StreamSource("fooX.xml"),media, title, charset);
      
      Transformer transformer = tFactory.newTransformer(stylesheet);
        
		   transformer.transform(new StreamSource("fooX.xml"), 
                             new StreamResult(new java.io.FileOutputStream("foo.out")));
	  }
  	  catch (Exception e)
	  {
	    e.printStackTrace();
	  }
  }
}
