import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.trace.TraceManager;
import org.apache.xalan.trace.PrintTraceListener;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;

public class Trace
{	
  public static void main (String[] args)
	  throws java.io.IOException, 
			 TransformerException, TransformerConfigurationException,
			 java.util.TooManyListenersException, 
			 org.xml.sax.SAXException			 
  {
    // Set up a PrintTraceListener object to print to a file.
    java.io.FileWriter fw = new java.io.FileWriter("events.log");  
    java.io.PrintWriter pw = new java.io.PrintWriter(fw, true);
    PrintTraceListener ptl = new PrintTraceListener(pw);

    // Print information as each node is 'executed' in the stylesheet.
    ptl.m_traceElements = true;
    // Print information after each result-tree generation event.
    ptl.m_traceGeneration = true;
    // Print information after each selection event.
    ptl.m_traceSelection = true;
    // Print information whenever a template is invoked.
    ptl.m_traceTemplates = true;

    // Set up the transformation    
   	TransformerFactory tFactory = TransformerFactory.newInstance();
    Transformer transformer = tFactory.newTransformer(new StreamSource("foo.xsl"));

    // Cast the Transformer object to TransformerImpl.
    if (transformer instanceof TransformerImpl) 
	  {
      TransformerImpl transformerImpl = (TransformerImpl)transformer;
      // Register the TraceListener with a TraceManager associated 
      // with the TransformerImpl.
      TraceManager trMgr = transformerImpl.getTraceManager();
      trMgr.addTraceListener(ptl);
                     
      // Perform the transformation --printing information to
      // the events log during the process.
      transformer.transform
                         ( new StreamSource("foo.xml"), 
                           new StreamResult(new java.io.FileWriter("foo.out")) );
    }
    // Close the PrintWriter and FileWriter.
    pw.close();
    fw.close();
  }
}