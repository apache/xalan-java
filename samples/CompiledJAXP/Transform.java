import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import org.apache.xalan.xsltc.trax.TransformerFactoryImpl;

public class Transform {

    public static void main(String[] args){
        Transform app = new Transform();
        app.run(args);
    }

    /**
     * Reads a Templates object from a file, the Templates object creates
     * a translet and wraps it in a Transformer. The translet performs the
     * transformation on behalf of the Transformer.transform() method.
     */
    public void run(String[] args){
        String xml = args[0];
        String translet = args[1];

        try {
	    StreamSource document = new StreamSource(xml);
	    StreamResult result = new StreamResult(System.out);
	    Templates templates = readTemplates(translet);
	    Transformer transformer = templates.newTransformer();
            transformer.transform(document, result);
        }
	catch (Exception e) {
            System.err.println("Exception: " + e); 
	    e.printStackTrace();
        }
        System.exit(0);
    }

    /**
     * Reads a Templates object from a file
     */
    private Templates readTemplates(String file) {
	try {
	    FileInputStream ostream = new FileInputStream(file);
	    ObjectInputStream p = new ObjectInputStream(ostream);
	    Templates templates = (Templates)p.readObject();
	    ostream.close();
	    return(templates);
	}
	catch (Exception e) {
	    System.err.println(e);
	    e.printStackTrace();
	    System.err.println("Could not write file "+file);
	    return null;
	}
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

    public void usage() {
        System.err.println("Usage: run <xml_file> <xsl_file>");
        System.exit(1);
    }

}
