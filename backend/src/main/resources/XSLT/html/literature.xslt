<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema"
				xmlns:math="http://www.w3.org/1998/Math/MathML" xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0/WD17"
				xmlns:ris="http://example.com/0.1/"
				xmlns:local="http://example.com/ns/1.0" exclude-result-prefixes="ris xs math akn local xsi"
				xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
				xsi:schemaLocation="http://docs.oasis-open.org/legaldocml/ns/akn/3.0/WD17 https://docs.oasis-open.org/legaldocml/akn-core/v1.0/csprd02/part2-specs/schemas/akomantoso30.xsd">

	<xsl:output method="html" encoding="UTF-8" />

<!--	<xsl:function name="local:encode-for-uri" as="xs:string">-->
<!--		<xsl:param name="uri" as="xs:string" />-->
<!--		<xsl:variable name="uri" select="replace($uri, 'ä', 'ae')" />-->
<!--		<xsl:variable name="uri" select="replace($uri, 'ö', 'oe')" />-->
<!--		<xsl:variable name="uri" select="replace($uri, 'ü', 'ue')" />-->
<!--		<xsl:value-of select="$uri" />-->
<!--	</xsl:function>-->

	<xsl:param name="ressourcenpfad" as="xs:string" select="''"/>

	<!-- Ignored elements (content is ignored as well) -->
<!--	<xsl:template match="akn:meta" />-->

	<!-- Top level container element -->
	<xsl:template match="akn:doc">
		<html>
			<head>
				<meta charset="utf-8" />
			</head>
			<body>
				<xsl:apply-templates />
			</body>
		</html>
	</xsl:template>

	<!--***************************************************************************************-->
	<!--Heading and Subheadings-->
	<!--***************************************************************************************-->

	<!-- Titel -->
	<xsl:template match="akn:FRBRalias[@name='haupttitel']">
		<h1 id="haupttitel">
			<xsl:apply-templates/>
		</h1>
	</xsl:template>

	<!-- H2 Headings -->
	<xsl:template match="akn:motivation">
		<section id="leitsatz"><h2>Leitsatz</h2>
			<xsl:apply-templates />
		</section>
	</xsl:template>

	<xsl:template match="akn:block[@name='Orientierungssatz']">
		<section id="orientierungssatz">
			<h2>Orientierungssatz</h2>
			<xsl:apply-templates />
		</section>
	</xsl:template>

	<xsl:template match="akn:block[@name='Sonstiger Orientierungssatz']">
		<section id="sonstigerOrientierungssatz">
			<h2>Sonstiger Orientierungssatz</h2>
				<xsl:apply-templates />
		</section>
	</xsl:template>

	<xsl:template match="akn:block[@name='Gliederung']">
		<section id="gliederung">
			<h2>Gliederung</h2>
			<xsl:apply-templates />
		</section>
	</xsl:template>


	<xsl:template match="akn:block[@name='Tenor']">
		<section id="tenor">
			<h2>Tenor</h2>
			<xsl:apply-templates />
		</section>
	</xsl:template>

	<xsl:template match="akn:background">
		<section id="tatbestand">
			<h2>Tatbestand</h2>
			<xsl:apply-templates />
		</section>
	</xsl:template>

	<xsl:template match="akn:block[@name='Entscheidungsgründe']">
		<section id="entscheidungsgruende">
			<h2>Entscheidungsgründe</h2>
			<xsl:apply-templates />
		</section>
	</xsl:template>

	<xsl:template match="akn:block[@name='Gründe']">
		<section id="gruende">
			<h2>Gründe</h2>
			<xsl:apply-templates/>
		</section>
	</xsl:template>

	<xsl:template match="akn:block[@name='Sonstiger Langtext']">
		<section id="sonstigerLangtext">
			<h2>Sonstiger Langtext</h2>
			<xsl:apply-templates/>
		</section>
	</xsl:template>

	<xsl:template match="akn:block[@name='Abweichende Meinung']/akn:opinion">
		<section id="abweichendeMeinung">
			<h2>Abweichende Meinung</h2>
			<xsl:apply-templates/>
		</section>
	</xsl:template>



	<!--***************************************************************************************-->
	<!--Html transformation-->
	<!--***************************************************************************************-->

	<!-- akn html elements with same name -->
	<xsl:template match="akn:p|akn:div|akn:span|akn:sub|akn:sup">
		<xsl:element name="{local-name()}">
			<xsl:apply-templates select="@*|node()"/>
		</xsl:element>
	</xsl:template>

	<!--
        Bilder
        ######
    -->
	<xsl:template match="akn:img">
		<img src="{concat($ressourcenpfad, @src)}">
			<xsl:apply-templates select="@*[local-name() != 'src']" />
		</img>
	</xsl:template>


	<!-- akn elements with different names in HTML -->
	<xsl:template match="akn:br">
		<br />
	</xsl:template>

	<xsl:template match="akn:i">
		<em>
			<xsl:apply-templates select="@*|node()"/>
		</em>
	</xsl:template>

	<xsl:template match="akn:b">
		<strong>
			<xsl:apply-templates select="@*|node()"/>
		</strong>
	</xsl:template>

	<xsl:template match="akn:u">
		<ins>
			<xsl:apply-templates select="@*|node()"/>
		</ins>
	</xsl:template>

	<!-- html tags and attributes that are not supported in akn and needed some workaround -->
	<xsl:template match="akn:block[@name='blockquote']">
		<blockquote>
			<xsl:apply-templates select="@*|node()"/>
		</blockquote>
	</xsl:template>

	<xsl:template match="ris:*">
		<xsl:element name="{local-name()}">
			<xsl:apply-templates select="@*[not(starts-with(name(), 'akn:'))]|node()"/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="@*">
		<xsl:attribute name="{replace(name(), 'ris:', '')}"><xsl:value-of select="." /></xsl:attribute>
	</xsl:template>

	<!--***************************************************************************************-->
	<!--Border Numbers-->
	<!--***************************************************************************************-->

	<!-- Border Number -->
	<xsl:template match="akn:hcontainer[@name='randnummer']">
		<dl class="border-number">
			<dt class="number">
				<xsl:attribute name="id">
					<xsl:text>border-number-link-</xsl:text>
					<xsl:value-of select="./akn:num"/>
				</xsl:attribute>
				<xsl:value-of select="./akn:num"/>
			</dt>

			<dd class="content">
				<xsl:apply-templates select="./akn:content/*"/>
			</dd>
		</dl>
	</xsl:template>

	<!-- Link to Border Number -->
	<xsl:template match="akn:a[@class='border-number-link']">
		<a>
			<xsl:attribute name="href">
				<xsl:value-of select="concat('#', local:encode-for-uri(.))" />
			</xsl:attribute>
			<xsl:apply-templates select="@*|node()"/>
		</a>
	</xsl:template>
	<!--***************************************************************************************-->

	<!-- Fallback for elements that are not supported -->
	<xsl:template match="*">
		<span>
			<xsl:apply-templates />
		</span>
	</xsl:template>
</xsl:stylesheet>
