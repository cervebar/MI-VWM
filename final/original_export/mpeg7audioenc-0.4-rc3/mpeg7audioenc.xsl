<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:HTML="http://www.w3.org/Profiles/XHTML-transitional"
                xmlns:mp7ae="mpeg7audioenc"
                xsi:schemaLocation="mpeg7audioenc http://www.ient.rwth-aachen.de/team/crysandt/software/mpeg7audioenc/mpeg7audioenc.xsd"
                version="1.0">
<xsl:output method="html"/>

<xsl:template match="/">
  <HTML><HEAD><TITLE>MPEG-7 Audio Encoder Configuration</TITLE></HEAD><BODY>
  <center>
  <h1>Configuration file for the<br/>
  <a href="http://sourceforge.net/projects/mpeg7audioenc/">Java MPEG-7 Audio Encoder</a>
  </h1></center>
  <xsl:apply-templates/>
  </BODY></HTML>
</xsl:template>

<!--
<xsl:template match="mp7ae:Module[@xsi:type='Resizer']">
<h2>Resizer</h2>
<table>
<tr><td>Module Name:</td><td><xsl:value-of select="@xsi:type"/></td></tr>
		<xsl:apply-templates/>
		</table>
 </xsl:template>
-->

<xsl:template match="mp7ae:Config">
<xsl:apply-templates select="mp7ae:Usage" />
<h2>Configuration</h2>
<xsl:apply-templates select="mp7ae:Module"/>
</xsl:template>

<xsl:template match="mp7ae:Usage">
<h2>Usage</h2>
<xsl:value-of select="text()" />
</xsl:template>

<xsl:template match="mp7ae:Module">
<h3><xsl:value-of select="@xsi:type"/></h3>
<center>
<table style="text-align: right; width: 90%;">
<xsl:for-each select="*">
<tr>
<td style="width: 40%"><xsl:value-of select="name()" /></td>
<td style="width: 60%"><xsl:value-of select="." /></td>
</tr>
</xsl:for-each>
</table>
</center>
</xsl:template>

<!--
 <xsl:template match="mp7ae:loEdge">
   <tr><td>Low Edge</td><td><xsl:value-of select="."/></td><td>Kommentar</td></tr>
 </xsl:template>
-->

</xsl:stylesheet>
