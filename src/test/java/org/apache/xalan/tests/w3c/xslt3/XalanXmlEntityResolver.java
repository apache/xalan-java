package org.apache.xalan.tests.w3c.xslt3;

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A class definition, representing an XML parse entity resolver.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XalanXmlEntityResolver implements EntityResolver {

	@Override
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {

		InputStream inpStream = getClass().getResourceAsStream("/xhtml_entities.dtd");
        if (inpStream != null) {
            return new InputSource(inpStream);
        }
        
        return null;
	}

}
