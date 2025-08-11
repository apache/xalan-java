<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
				exclude-result-prefixes="xs"
				version="3.0">
				
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSL stylesheet test case, to test XPath 3.1 function fn:json-to-xml 
         with xdm map's key duplicates resolution option 'use-first'. -->				
	
	<xsl:output method="xml" indent="yes"/>

    <xsl:variable name="jsonStr" as="xs:string">
       { "a" : 1,
	     "b" : {"x" : 10, "y" : 11, "x" : 12},
		 "b" : 3,
		 "a" : 4
       }        
    </xsl:variable>

    <xsl:template match="/">
      <xsl:sequence select="json-to-xml($jsonStr, map{'duplicates' : 'use-first'})"/>
    </xsl:template>

</xsl:stylesheet>
