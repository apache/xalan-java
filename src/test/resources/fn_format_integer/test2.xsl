<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
    
   <!-- An XSL stylesheet test case, to test XPath function 
        fn:format-integer with a picture string having format 
        modifier. -->                  
				
   <xsl:output method="xml" indent="yes"/>				

   <xsl:template match="/">
      <result>
	     <one>
            <xsl:value-of select="format-integer(15, 'w;c')"/>, <xsl:value-of select="format-integer(15, 'w;o')"/>
		 </one>
         <two>
            <xsl:value-of select="format-integer(15, 'W;c')"/>, <xsl:value-of select="format-integer(15, 'W;o')"/>
		 </two>
         <three>
		    <a>
               <xsl:value-of select="format-integer(750, 'Ww;c')"/>, <xsl:value-of select="format-integer(750, 'Ww;o')"/>
		    </a>
		    <b>
		       <xsl:value-of select="format-integer(751, 'W;c')"/>, <xsl:value-of select="format-integer(751, 'W;o')"/>
		    </b>
		    <c>
		       <xsl:value-of select="format-integer(752, 'Ww;c')"/>, <xsl:value-of select="format-integer(752, 'Ww;o')"/>
		    </c>
		    <d>
		       <xsl:value-of select="format-integer(753, 'Ww;c')"/>, <xsl:value-of select="format-integer(753, 'Ww;o')"/>
		    </d>
		    <e>
		       <xsl:value-of select="format-integer(754, 'Ww;c')"/>, <xsl:value-of select="format-integer(754, 'Ww;o')"/>
		    </e>
		   <f>
		       <xsl:value-of select="format-integer(755, 'Ww;c')"/>, <xsl:value-of select="format-integer(755, 'Ww;o')"/>
		   </f>
		 </three>
		 <four>
		    <a>
		       <xsl:for-each select="1 to 15">
			      <number inp="{.}" cardinalForm="{format-integer(., '1;c')}" ordinalForm="{format-integer(., '1;o')}"/>
			   </xsl:for-each>
			</a>
		    <b>
		       <xsl:for-each select="50 to 55">
			      <number inp="{.}" cardinalForm="{format-integer(., '1;c')}" ordinalForm="{format-integer(., '1;o')}"/>
			   </xsl:for-each>
			</b>
		 </four>
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
