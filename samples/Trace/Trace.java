
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.trace.TraceManager;
import org.apache.xalan.trace.PrintTraceListener;

public class Trace
{	
  public static void main (String[] args)
	  throws java.io.IOException, 
			 org.apache.trax.TransformException, org.apache.trax.ProcessorException,
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
    org.apache.trax.Processor processor = org.apache.trax.Processor.newInstance("xslt");
    org.apache.trax.Templates templates = processor.process
                             (new org.xml.sax.InputSource("foo.xsl"));
    org.apache.trax.Transformer transformer = templates.newTransformer();

    // Cast the Transformer object as TransformerImpl.
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
                         ( new org.xml.sax.InputSource("foo.xml"), 
                           new org.apache.trax.Result(new java.io.FileWriter("foo.out")) );
    }
    // Close the PrintWriter and FileWriter.
    pw.close();
    fw.close();
  }
}