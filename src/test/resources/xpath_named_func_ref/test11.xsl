<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"				
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSL stylesheet test case, to test XML Schema constructor functions 
         for schema built-in types used in two different ways. One as XPath named 
         function reference and called via XPath dynamic function call syntax, 
         and second with usual XML Schema constructor function call syntax. -->                
 
    <xsl:output method="xml" indent="yes"/>
 
    <xsl:template name="main">
       <result>
          <one>
			  <xsl:variable name="dateFunc" select="xs:date#1" as="function(*)"/>
			  <a>	                
				 <xsl:value-of select="current-date() gt $dateFunc('2005-10-05')"/>
			  </a>
			  <b>	                
				 <xsl:value-of select="current-date() lt $dateFunc('2005-10-05')"/>
			  </b>
			  <c>
				 <xsl:value-of select="$dateFunc('2005-10-05') + xs:dayTimeDuration('P2DT2H30M0S')"/>
			  </c>
		  </one>	   
		  <two>
			  <a>	                
				 <xsl:value-of select="current-date() gt xs:date('2005-10-05')"/>
			  </a>
			  <b>	                
				 <xsl:value-of select="current-date() lt xs:date('2005-10-05')"/>
			  </b>
			  <c>
				 <xsl:value-of select="xs:date('2005-10-05') + xs:dayTimeDuration('P2DT2H30M0S')"/>
			  </c>
		  </two>		  
       </result>
    </xsl:template>
    
    <!--
      * Licensed to the Apache Software Foundation (ASF) under one
      * or more contributor license agreements. See the NOTICE file
      * distributed with this work for additional information
      * regarding copyright ownership. The ASF licenses this file
      * to you under the Apache License, Version 2.0 (the "License");
      * you may not use this file except in compliance with the License.
      * You may obtain a copy of the License at
      *
      *     http://www.apache.org/licenses/LICENSE-2.0
      *
      * Unless required by applicable law or agreed to in writing, software
      * distributed under the License is distributed on an "AS IS" BASIS,
      * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
      * See the License for the specific language governing permissions and
      * limitations under the License.
    -->
	
</xsl:stylesheet>
