<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0"
                xmlns:sql="/org.apache.xalan.lib.sql.XConnection"
                extension-element-prefixes="sql">
  <xsl:output method="html" indent="yes"/>
  <xsl:param name="query" select="&quot;SELEC FirstName, LastName, Street1, City, Zip  FROM Account where AcctID='2019990000'&quot;"/>
 
  <xsl:template match="/">
    <!-- 1. Make the connection -->
    <xsl:variable name="accounts"
                  select="sql:new('com.sybase.jdbc2.jdbc.SybDriver',
                                'jdbc:sybase:Tds:localhost:5000/smart911', 'sa',
                               '')"/>
    <!--2. Execute the query -->
    <xsl:variable name="table" select='sql:query($accounts, $query)'/>
                               
    
   	<xsl:apply-templates select="$table/row-set" />
 		<xsl:apply-templates select="$table/ext-error" />
    
    <!-- 3. Close the connection -->
    <xsl:value-of select="sql:close($accounts)"/>

  </xsl:template>

  <xsl:template match="row-set">
    
    <HTML>
      <HEAD>
      </HEAD>
      <BODY>
        <TABLE border="1">
          <TR>
          <!-- Get column-label attribute from each column-header-->
          <xsl:for-each select="row-set/column-header">
            <TH><xsl:value-of select="@column-label"/></TH>
          </xsl:for-each>
          </TR>
          <xsl:apply-templates select="/row-set/row"/>
          <xsl:text>&#10;</xsl:text>
        </TABLE>
      </BODY>
    </HTML> 
  </xsl:template>

  <xsl:template match="row">
        <TR>
          <xsl:apply-templates select="col"/>
        </TR>
  </xsl:template>

  <xsl:template match="col">
    <TD>
      <!-- Here is the column data -->
      <xsl:value-of select="text()"/>
    </TD>
  </xsl:template>

  <xsl:template match="//ext-error">
    <xsl:text>Woops, an error occured: </xsl:text>
  	<xsl:apply-templates />
  </xsl:template>


</xsl:stylesheet>
