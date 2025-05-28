<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
               {{NS_DECL}} 
               version="3.0">

  <!-- Author: mukulg@apache.org -->
  
  <!-- An XSL stylesheet template document for W3C XSLT 3.0 test 
       cases which are verified by Xalan-J's XSLT 3.0 implementation, that 
       specify expected result as following within the respective test set 
       file document:
       
       <test-case ...>
         <result>
            <all-of ...>
              <assert> ... </assert>
              ...
            </all-of>
         </result>
       </test-case>
  -->
  
  <xsl:template match="/">
     <result>
        {{XPATH_ASSERT_LIST}}
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
