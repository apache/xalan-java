<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0"
                xmlns:sql="/org.apache.xalan.lib.sql.XConnection"
                extension-element-prefixes="sql">
  <xsl:output method="html" indent="yes"/>
  <xsl:param name="q1" select="&quot;SELECT FirstName, LastName, Street1, City, Zip  FROM Account where AcctID= ? &quot;"/>
  <xsl:param name="q1type" select="string"/>
 
  <xsl:template match="/">
    <!-- 1. Make the connection -->
    <xsl:param name="cinfo" select="//DBINFO" />
    <xsl:variable name="accounts"
                  select='sql:new($cinfo)'/>
    <HTML>
      <HEAD>
      </HEAD>
      <BODY>
        <TABLE border="1">
        <!--2. Execute the query -->
	<xsl:param name="q1param" select="//QUERY" />
        <xsl:variable name="foo" select='sql:addParameterFromElement($accounts, $q1param)' />
	<xsl:variable name="table" select='sql:pquery($accounts, $q1, $q1type)'/>
          <TR>
          <!-- Get column-label attribute from each column-header-->
          <xsl:for-each select="$table/row-set/column-header">
            <TH><xsl:value-of select="@column-label"/></TH>
          </xsl:for-each>
          </TR>
          <xsl:apply-templates select="$table/row-set/row"/>
          <xsl:text>&#10;</xsl:text>
        </TABLE>
      </BODY>
    </HTML> 
    <!-- 3. Close the connection -->
    <xsl:value-of select="sql:close($accounts)"/>
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

</xsl:stylesheet>
