<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:template match="/">
    <HTML>
      <HEAD>
         <TITLE>Xalan for Java Version 2</TITLE>
      </HEAD>
      <BODY>
        <!-- H1>Xalan for Java Version 2</H1 -->
        <H2>Xalan for Java Version 2: <xsl:value-of select="todo/@title"/></H2>
        <xsl:for-each select="todo">
          <xsl:for-each select="actions">
              <xsl:for-each select="target-release-description">
                <p>
                  <xsl:apply-templates/>
                </p>
              </xsl:for-each>
              <xsl:for-each select="action">
                <xsl:if test="normalize-space(.)">
                  <p>
                   <xsl:number/>) <xsl:apply-templates/>
                   <xsl:if test="@*">
                    <BR/>
                   </xsl:if>
                   <xsl:apply-templates select="@*"/>
                  </p>
                </xsl:if>
            </xsl:for-each>
            <HR/>
          </xsl:for-each>

          <xsl:for-each select="completed">
            <H3>Completed: </H3>
            <xsl:for-each select="action">
              <xsl:if test="normalize-space(.)">
                <p>
                 <xsl:number/>) <xsl:apply-templates/>
                 <xsl:if test="@*">
                  <BR/>
                 </xsl:if>
                 <xsl:apply-templates select="@*"/>
                </p>
              </xsl:if>
          </xsl:for-each>
          <HR/>
        </xsl:for-each>

          <H3>Developers: </H3>
          <ul>
          <xsl:for-each select="devs/person">
            <li>
              <a href="mailto:{@email}">
                <xsl:value-of select="@name"/>
                <xsl:text> (</xsl:text><xsl:value-of select="@id"/><xsl:text>)</xsl:text>
              </a>
            </li>
          </xsl:for-each>
          </ul>
       </xsl:for-each>

      </BODY>
    </HTML>
  </xsl:template>

  <xsl:template match="action/@*">
    <b><xsl:value-of select="name(.)"/>:</b><xsl:text> </xsl:text><xsl:value-of select="."/>
    <xsl:if test="not (position()=last())">
      <xsl:text>, </xsl:text>
    </xsl:if>
  </xsl:template>

  <xsl:template match="target-release-description/date">
    <b>For release: <xsl:value-of select="."/></b>
  </xsl:template>

  <xsl:template match="issue">
    <BR/><b>Issue </b><xsl:text>[</xsl:text><xsl:value-of select="@id"/>
    <xsl:text>]: </xsl:text>
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="target-release-description/level">
    <xsl:text>, </xsl:text><xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="target-release-description/goal">
    <BR/><b>Goal </b><xsl:text>[</xsl:text><xsl:value-of select="@type"/>
    <xsl:text>]: </xsl:text>
    <xsl:apply-templates/>
  </xsl:template>

</xsl:stylesheet>
