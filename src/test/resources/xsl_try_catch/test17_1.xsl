<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:err="http://www.w3.org/2005/xqt-errors"
                exclude-result-prefixes="err"				
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- An XSL stylesheet test case, to test stylesheet variable reference 
        within xsl:catch instruction. -->                
				
   <xsl:output method="xml" indent="yes"/>				

   <xsl:variable name="mesg" select="'Exception caught'"/>
   
   <xsl:template match="/">
      <result>	     
	     <xsl:try select="1 div 0">		    
		    <xsl:catch errors="*">			   
			   <xsl:value-of select="$mesg || '. ' || $err:code || ', ' || $err:description || '.'"/>
			</xsl:catch>
		 </xsl:try>
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
