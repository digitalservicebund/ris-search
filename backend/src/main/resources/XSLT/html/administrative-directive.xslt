<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
				xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0"
				xmlns:ris="http://ldml.neuris.de/meta/"
				exclude-result-prefixes="akn ris">

	<xsl:output method="html" encoding="UTF-8" indent="yes"/>

	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="//akn:longTitle/akn:block[@name='longTitle']"/>

	<xsl:template match="akn:doc">
		<html>
			<head>
				<title>
					<xsl:value-of select="$title" />
				</title>
			</head>
			<body>
				<xsl:apply-templates select="//akn:longTitle"/>
				<xsl:apply-templates select="//akn:mainBody"/>
				<xsl:apply-templates select="//ris:tableOfContentsEntries"/>
				<xsl:apply-templates select="//ris:activeReferences"/>
				<xsl:apply-templates select="//akn:otherReferences[@source='attributsemantik-noch-undefiniert']"/>
			</body>
		</html>
	</xsl:template>


	<!-- render titles -->
	<xsl:template match="akn:longTitle">
		<!--	only show headline if it has content	-->
		<xsl:if test="akn:block">
			<h1><xsl:value-of select="$title"/></h1>
		</xsl:if>
	</xsl:template>

	<!-- Short Report -->
	<!-- if there is no short report the main body only contains an empty hcontainer -->
	<xsl:template match="akn:mainBody">
		<xsl:if test="not(akn:hcontainer)">
			<h2>Kurzreferat</h2>
			<xsl:apply-templates />
		</xsl:if>
	</xsl:template>

	<!-- Content -->
	<xsl:template match="ris:tableOfContentsEntries">
		<h2>Inhalt</h2>
		<ul>
			<xsl:apply-templates/>
		</ul>
	</xsl:template>

	<xsl:template match="ris:tableOfContentsEntry">
		<li><xsl:value-of select="."/></li>
	</xsl:template>

	<xsl:template match="ris:activeReferences">
		<h2>Verweise</h2>
		<ul>
			<xsl:for-each select="ris:activeReference">
				<li><xsl:value-of select="@reference" /></li>
			</xsl:for-each>
		</ul>
	</xsl:template>

	<xsl:template match="akn:otherReferences[@source='attributsemantik-noch-undefiniert']">
		<xsl:if test="akn:implicitReference/ris:caselawReference">
			<h2>Dieser Beitrag zitiert</h2>
			<h3>Rechtsprechung</h3>
			<ul>
				<xsl:for-each select="akn:implicitReference[ris:caselawReference]">
					<li><xsl:value-of select="@showAs" /></li>
				</xsl:for-each>
			</ul>
		</xsl:if>
	</xsl:template>

	<!--***************************************************************************************-->
	<!--Html transformation-->
	<!--***************************************************************************************-->

	<!-- akn html elements with same name -->
	<xsl:template match="akn:p|akn:div|akn:span|akn:sub|akn:sup">
		<xsl:element name="{local-name()}">
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="akn:br">
		<br />
	</xsl:template>

	<xsl:template match="akn:a[@href]">
		<a href="{@href}">
			<xsl:apply-templates/>
		</a>
	</xsl:template>

	<xsl:template match="akn:inline[@name='em']">
		<em><xsl:apply-templates/></em>
	</xsl:template>

	<xsl:template match="akn:inline[@name='strong']">
		<strong><xsl:apply-templates/></strong>
	</xsl:template>

	<xsl:template match="akn:mainBody//text()">
		<xsl:value-of select="."/>
	</xsl:template>

	<xsl:template match="text()"/>
</xsl:stylesheet>