import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import org.apache.xalan.xsltc.trax.TransformerFactoryImpl;

public class Compile {

    public static void main(String[] args){
        Compile app = new Compile();
        app.run(args[0]);
    }

    /**
     * Compiles an XSL stylesheet into a translet, wraps the translet
     * inside a Templates object and dumps it to a file.
     */
    public void run(String xsl) {
        try {
	    // Get an input stream for the XSL stylesheet
	    StreamSource stylesheet = new StreamSource(xsl);

	    // The TransformerFactory will compile the stylesheet and
	    // put the translet classes inside the Templates object
	    TransformerFactory factory = TransformerFactory.newInstance();
	    Templates templates = factory.newTemplates(stylesheet);

	    // Send the Templates object to a '.translet' file
	    dumpTemplate(getBaseName(xsl)+".translet", templates);
        }
	catch (Exception e) {
            System.err.println("Exception: " + e); 
	    e.printStackTrace();
        }
        System.exit(0);
    }

    /**
     * Returns the base-name of a file/url
     */
    private String getBaseName(String filename) {
	int start = filename.lastIndexOf(File.separatorChar);
	int stop  = filename.lastIndexOf('.');
	if (stop <= start) stop = filename.length() - 1;
	return filename.substring(start+1, stop);
    }

    /**
     * Writes a Templates object to a file
     */
    private void dumpTemplate(String file, Templates templates) {
	try {
	    FileOutputStream ostream = new FileOutputStream(file);
	    ObjectOutputStream p = new ObjectOutputStream(ostream);
	    p.writeObject(templates);
	    p.flush();
	    ostream.close();
	}
	catch (Exception e) {
	    System.err.println(e);
	    e.printStackTrace();
	    System.err.println("Could not write file "+file);
	}
    }

    private void usage() {
        System.err.println("Usage: compile <xsl_file>");
        System.exit(1);
    }

}
