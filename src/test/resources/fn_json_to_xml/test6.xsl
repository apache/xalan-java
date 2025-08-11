<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
				exclude-result-prefixes="xs"
				version="3.0">
				
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSL stylesheet test case, to test XPath 3.1 function fn:json-to-xml 
         with xdm map's key duplicates resolution option 'retain'. -->				
	
	<xsl:output method="xml" indent="yes"/>

    <!-- JSON string example, specified within W3C XSLT 3.0 
	     test suite. -->
    <xsl:variable name="jsonStr" as="xs:string">
       {"one": {"a":2, "b":3, "c":4, "a":5},
        "two": {"a":2, "b":3, "c":4, "a":5},
        "one": {"a":3, "b":4, "c":5, "a":6},
        "two": {"a":3, "b":4, "c":5, "a":6}
       }        
    </xsl:variable>

    <xsl:template match="/">
      <xsl:sequence select="json-to-xml($jsonStr, map{'duplicates' : 'retain'})"/>
    </xsl:template>

</xsl:stylesheet>
