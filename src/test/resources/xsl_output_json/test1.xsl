<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"				
				version="3.0">
				
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSL 3 test case, to test xsl:output method="json" -->				

	<xsl:output method="json"/>

	<xsl:template match="/">
	   {
	      "a" : 1,
		  "b" : 2,
		  "c" : 3,
		  "d" : 4,
		  "e" " 5
	   }
	</xsl:template>

</xsl:stylesheet>
