<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"				
                version="3.0">
				
   <!-- Author: mukulg@apache.org -->
   				
   <!-- use with test1.xml -->
   
   <!-- An XSL 3 stylesheet test case, to test xsl:merge instruction. -->			
                
   <xsl:output method="xml" indent="yes"/>
   
   <xsl:template match="/info">
      <result>
		 <xsl:merge>
		    <xsl:merge-source name="one" select="seq[1]/val" sort-before-merge="yes">    
			   <xsl:merge-key select="."/>
		    </xsl:merge-source>
		    <xsl:merge-source name="two" select="seq[2]/val" sort-before-merge="yes">
			   <xsl:merge-key select="."/>
		    </xsl:merge-source>
			<xsl:merge-source name="three" select="seq[3]/val" sort-before-merge="yes">
			   <xsl:merge-key select="."/>
		    </xsl:merge-source>
		    <xsl:merge-action>
			   <key value="{current-merge-key()}" occurenceCount="{count(current-merge-group())}"/>
		    </xsl:merge-action>
		 </xsl:merge>		 
	  </result>
   </xsl:template>
   
   <!--
      * Licensed to the Apache Software Foundation (ASF) under one
      * or more contributor license agreements. See the NOTICE file
      * distributed with this work for additional information
      * regarding copyright ownership. The ASF licenses this file
      * to you under the Apache License, Version 2.0 (the  "License");
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
