<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:math="http://www.w3.org/1998/Math/MathML"
                xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0"
                xmlns:ris="http://example.com/0.1/"
                xmlns:local="http://example.com/ns/1.0"
                exclude-result-prefixes="ris xs math akn local xsi"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xsi:schemaLocation="http://docs.oasis-open.org/legaldocml/ns/akn/3.0 https://docs.oasis-open.org/legaldocml/akn-core/v1.0/os/part2-specs/schemas/akomantoso30.xsd">

    <xsl:output method="html" encoding="UTF-8" indent="yes" />

    <xsl:function name="local:encode-for-uri" as="xs:string">
        <xsl:param name="uri" as="xs:string" />
        <xsl:variable name="uri" select="replace($uri, 'ä', 'ae')" />
        <xsl:variable name="uri" select="replace($uri, 'ö', 'oe')" />
        <xsl:variable name="uri" select="replace($uri, 'ü', 'ue')" />
        <xsl:value-of select="$uri" />
    </xsl:function>

    <xsl:param name="ressourcenpfad" as="xs:string" select="''"/>

    <xsl:template match="akn:judgment">
        <html>
            <head><meta charset="utf-8" /></head>
            <body>
                <xsl:apply-templates select=".//akn:shortTitle" />

                <xsl:apply-templates select=".//akn:introduction[@ris:domainTerm = 'Leitsatz']" />

                <xsl:apply-templates select=".//ris:orientierungssatz" />

                <xsl:apply-templates select=".//ris:sonstigerOrientierungssatz" />

                <xsl:apply-templates select=".//akn:introduction[@ris:domainTerm = 'Gliederung']" />

                <xsl:apply-templates select=".//akn:decision[@ris:domainTerm = 'Tenor']" />

                <xsl:apply-templates select=".//akn:background" />

                <xsl:apply-templates select=".//akn:motivation[@ris:domainTerm = 'Entscheidungsgründe']" />

                <xsl:apply-templates select=".//akn:motivation[@ris:domainTerm = 'Gründe']" />

                <xsl:apply-templates select=".//akn:motivation[@ris:domainTerm = 'Sonstiger Langtext']" />

                <xsl:apply-templates select=".//akn:motivation[@ris:domainTerm = 'Abweichende Meinung']" />
            </body>
        </html>
    </xsl:template>

    <xsl:template match="akn:meta | akn:identification | akn:references | akn:proprietary | akn:header | akn:classification" />

    <xsl:template match="akn:judgmentBody | akn:subFlow | akn:docTitle | akn:akomaNtoso |
                         akn:embeddedStructure | akn:foreign | akn:otherAnalysis | ris:dokumentarischeKurztexte | akn:content">
        <xsl:apply-templates />
    </xsl:template>

    <xsl:template match="akn:shortTitle">
        <h1 id="title">
            <xsl:apply-templates/>
        </h1>
    </xsl:template>

    <xsl:template match="akn:introduction[@ris:domainTerm = 'Leitsatz']">
        <section id="leitsatz">
            <h2>Leitsatz</h2>
            <xsl:apply-templates />
        </section>
    </xsl:template>

    <xsl:template match="ris:orientierungssatz">
        <section id="orientierungssatz">
            <h2>Orientierungssatz</h2>
            <xsl:apply-templates />
        </section>
    </xsl:template>

	<xsl:template match="ris:sonstigerOrientierungssatz">
		<section id="sonstigerOrientierungssatz">
			<h2>Sonstiger Orientierungssatz</h2>
				<xsl:apply-templates />
		</section>
	</xsl:template>

    <xsl:template match="akn:introduction[@ris:domainTerm = 'Gliederung']">
		<section id="gliederung">
			<h2>Gliederung</h2>
			<xsl:apply-templates />
		</section>
	</xsl:template>


	<xsl:template match="akn:decision[@ris:domainTerm = 'Tenor']">
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

    <xsl:template match="akn:motivation[@ris:domainTerm = 'Entscheidungsgründe']">
        <section id="entscheidungsgruende">
            <h2>Entscheidungsgründe</h2>
                <xsl:apply-templates />
        </section>
    </xsl:template>

    <xsl:template match="akn:motivation[@ris:domainTerm = 'Gründe']">
        <section id="gruende">
            <h2>Gründe</h2>
                <xsl:apply-templates/>
        </section>
    </xsl:template>

    <xsl:template match="akn:motivation[@ris:domainTerm = 'Sonstiger Langtext']">
        <section id="sonstigerLangtext">
            <h2>Sonstiger Langtext</h2>
                <xsl:apply-templates/>
        </section>
    </xsl:template>

    <xsl:template match="akn:motivation[@ris:domainTerm = 'Abweichende Meinung']">
        <section id="abweichendeMeinung">
            <h2>Abweichende Meinung</h2>
                <xsl:apply-templates/>
        </section>
    </xsl:template>

    <xsl:template match="akn:p|akn:div|akn:span|akn:sub|akn:sup">
        <xsl:element name="{local-name()}">
            <xsl:apply-templates select="@*|node()"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="akn:img">
        <img src="{concat($ressourcenpfad, @src)}">
            <xsl:apply-templates select="@*[local-name() != 'src']" />
        </img>
    </xsl:template>

    <xsl:template match="akn:br"><br /></xsl:template>
    <xsl:template match="akn:i"><em><xsl:apply-templates select="@*|node()"/></em></xsl:template>
    <xsl:template match="akn:b"><strong><xsl:apply-templates select="@*|node()"/></strong></xsl:template>
    <xsl:template match="akn:u"><ins><xsl:apply-templates select="@*|node()"/></ins></xsl:template>

    <xsl:template match="ris:*">
        <xsl:element name="{local-name()}">
            <xsl:apply-templates select="@*[not(starts-with(name(), 'akn:'))]|node()"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="@*">
        <xsl:attribute name="{replace(name(), 'ris:', '')}"><xsl:value-of select="." /></xsl:attribute>
    </xsl:template>

    <xsl:template match="akn:hcontainer[@name='Randnummer' or @ris:domainTerm='Randnummer']">
        <dl class="border-number">
            <dt class="number">
                <xsl:attribute name="id">
                    <xsl:value-of select="@eId"/>
                </xsl:attribute>
                <xsl:value-of select="./akn:num"/>
            </dt>
            <dd class="content">
                <xsl:apply-templates select="./akn:content/node()"/>
            </dd>
        </dl>
    </xsl:template>

    <xsl:template match="akn:ref[@class='border-number-link']">
        <a class="border-number-link">
            <xsl:attribute name="href">
                <xsl:value-of select="@href"/>
            </xsl:attribute>

            <xsl:attribute name="aria-label">
                <xsl:value-of select="concat('Springe zu Randnummer: ', .)"/>
            </xsl:attribute>

            <xsl:apply-templates/>
        </a>
    </xsl:template>

    <xsl:template match="*">
        <span><xsl:apply-templates /></span>
    </xsl:template>

</xsl:stylesheet>
