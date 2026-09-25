<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform xmlns:akn="http://Inhaltsdaten.LegalDocML.de/1.9/"
               xmlns:brat="http://MetadatenBundesrat.LegalDocML.de/1.9/"
               xmlns:breg="http://MetadatenBundesregierung.LegalDocML.de/1.9/"
               xmlns:btag="http://MetadatenBundestag.LegalDocML.de/1.9/"
               xmlns:error="https://doi.org/10.5281/zenodo.1495494#error"
               xmlns:fhilf="http://MetadatenFormulierungshilfe.LegalDocML.de/1.9/"
               xmlns:fkt="lokale-funktionen"
               xmlns:nkr="http://MetadatenNormenkontrollrat.LegalDocML.de/1.9/"
               xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
               xmlns:redok="http://MetadatenRechtsetzungsdokument.LegalDocML.de/1.9/"
               xmlns:regtxt="http://MetadatenRegelungstext.LegalDocML.de/1.9/"
               xmlns:sch="http://purl.oclc.org/dsdl/schematron"
               xmlns:schxslt="https://doi.org/10.5281/zenodo.1495494"
               xmlns:schxslt-api="https://doi.org/10.5281/zenodo.1495494#api"
               xmlns:sonst="http://MetadatenSonstigerVeroeffentlichungstext.LegalDocML.de/1.9/"
               xmlns:xs="http://www.w3.org/2001/XMLSchema"
               xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
               version="2.0">
   <rdf:Description xmlns:dc="http://purl.org/dc/elements/1.1/"
                    xmlns:dct="http://purl.org/dc/terms/"
                    xmlns:skos="http://www.w3.org/2004/02/skos/core#">
      <dct:creator>
         <dct:Agent>
            <skos:prefLabel>SchXslt/1.10.1 SAXON/HE 12.9</skos:prefLabel>
            <schxslt.compile.typed-variables xmlns="https://doi.org/10.5281/zenodo.1495494#">true</schxslt.compile.typed-variables>
         </dct:Agent>
      </dct:creator>
      <dct:created>2026-09-01T13:38:38.295685946Z</dct:created>
   </rdf:Description>
   <xsl:output indent="yes"/>
   <xsl:key name="nodes-by-GUID" match="*[@GUID]" use="@GUID"/>
   <xsl:key name="nodes-by-eId" match="*[@eId]" use="@eId"/>
   <xsl:param name="form"
              select="/akn:akomaNtoso/*/akn:meta/akn:proprietary/regtxt:legalDocML.de_metadaten/regtxt:form"/>
   <xsl:param name="form-stammform" select="'stammform'"/>
   <xsl:param name="form-mantelform" select="'mantelform'"/>
   <xsl:param name="form-eingebundene-stammform" select="'eingebundene-stammform'"/>
   <xsl:param name="form-nicht-vorhanden" select="'nicht-vorhanden'"/>
   <xsl:param name="authority-egesetzgebung"
              select="'https://www.egesetzgebung.bund.de'"/>
   <xsl:param name="authority-everkündung" select="'https://www.recht.bund.de'"/>
   <xsl:param name="authority-rechtsinformationssystem"
              select="'https://www.ris.bund.de'"/>
   <xsl:param name="ist-entwurfsfassung"
              select="starts-with(/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRuri/@value, $authority-egesetzgebung)"/>
   <xsl:param name="ist-verkündungsfassung"
              select="starts-with(/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRuri/@value, $authority-everkündung)"/>
   <xsl:param name="ist-konsolidierte-fassung"
              select="starts-with(/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRuri/@value, $authority-rechtsinformationssystem)"/>
   <xsl:param name="typ"
              select="/akn:akomaNtoso/*/akn:meta/akn:proprietary/regtxt:legalDocML.de_metadaten/regtxt:typ"/>
   <xsl:param name="typ-gesetz" select="'gesetz'"/>
   <xsl:param name="typ-sonstige-bekanntmachung" select="'sonstige-bekanntmachung'"/>
   <xsl:param name="typ-verordnung" select="'verordnung'"/>
   <xsl:param name="typ-vertragsgesetz" select="'vertragsgesetz'"/>
   <xsl:param name="typ-vertragsverordnung" select="'vertragsverordnung'"/>
   <xsl:param name="typ-verwaltungsvorschrift" select="'verwaltungsvorschrift'"/>
   <xsl:param name="typ-satzung" select="'satzung'"/>
   <xsl:param name="teildokument-uri" select="/akn:akomaNtoso/*/@name"/>
   <xsl:param name="art-anschreiben-uri"
              select="( '/akn/ontology/de/concept/documenttype/bund/anschreiben', '/akn/ontology/de/concept/documenttype/bund/anschreiben-einigungsvorschlag-des-vermittlungsausschusses', '/akn/ontology/de/concept/documenttype/bund/anschreiben-vorschlag-an-bundesrat' )"/>
   <xsl:param name="art-begründung-uri"
              select="( '/akn/ontology/de/concept/documenttype/bund/begruendung-aenderungsantrag', '/akn/ontology/de/concept/documenttype/bund/begruendung-entschliessungsantrag', '/akn/ontology/de/concept/documenttype/bund/begruendung-regelungstext' )"/>
   <xsl:param name="art-regelungstext-uri"
              select="( '/akn/ontology/de/concept/documenttype/bund/regelungstext-entwurf', '/akn/ontology/de/concept/documenttype/bund/regelungstext' )"/>
   <xsl:param name="art-vereinbarung-uri"
              select="( '/akn/ontology/de/concept/documenttype/bund/vereinbarung-entwurf', '/akn/ontology/de/concept/documenttype/bund/vereinbarung-verkuendung' )"/>
   <xsl:param name="art-vorblatt-uri"
              select="( '/akn/ontology/de/concept/documenttype/bund/vorblatt-regelungstext', '/akn/ontology/de/concept/documenttype/bund/vorblatt-beschlussempfehlung' )"/>
   <xsl:param name="art-vorblatt-regelungstext-uri"
              select="'/akn/ontology/de/concept/documenttype/bund/vorblatt-regelungstext'"/>
   <xsl:param name="art-vorblatt-beschlussempfehlung-uri"
              select="'/akn/ontology/de/concept/documenttype/bund/vorblatt-beschlussempfehlung'"/>
   <xsl:param name="art-anlage-regelungstext-uri"
              select="'/akn/ontology/de/concept/documenttype/bund/anlage-regelungstext'"/>
   <xsl:param name="art-bericht-uri"
              select="'/akn/ontology/de/concept/documenttype/bund/bericht'"/>
   <xsl:param name="art-bekanntmachungstext-uri"
              select="( '/akn/ontology/de/concept/documenttype/bund/sonstiger-veroeffentlichungstext' )"/>
   <xsl:param name="bearbeitende-institution-frbrauthor"
              select="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRExpression/akn:FRBRauthor/@href"/>
   <xsl:param name="refersto-literal-geltungszeitregel" select="'geltungszeitregel'"/>
   <xsl:param name="refersto-literal-geltungszeitregel-inkrafttreten"
              select="'geltungszeitregel-inkrafttreten'"/>
   <xsl:param name="refersto-literal-geltungszeitregel-ausserkrafttreten"
              select="'geltungszeitregel-ausserkrafttreten'"/>
   <xsl:param name="refersto-literal-hauptaenderung" select="'hauptaenderung'"/>
   <xsl:param name="refersto-literal-folgeaenderung" select="'folgeaenderung'"/>
   <xsl:param name="refersto-literal-eingebundene-stammform"
              select="'eingebundene-stammform'"/>
   <xsl:param name="refersto-literal-stammform" select="'stammform'"/>
   <xsl:param name="refersto-literal-mantelform" select="'mantelform'"/>
   <xsl:param name="refersto-literal-vertragsgesetz" select="'vertragsgesetz'"/>
   <xsl:param name="refersto-literal-vertragsverordnung"
              select="'vertragsverordnung'"/>
   <xsl:param name="refersto-literal-ausschussueberweisung"
              select="'ausschussueberweisung'"/>
   <xsl:param name="type-literal-ereignisreferenz-generation" select="'generation'"/>
   <xsl:param name="type-literal-ereignisreferenz-repeal" select="'repeal'"/>
   <xsl:param name="type-literal-ereignisreferenz-amendment" select="'amendment'"/>
   <xsl:param name="refersto-literal-ereignisreferenz-verkuendung"
              select="'verkuendung'"/>
   <xsl:param name="refersto-literal-ereignisreferenz-entwurfsfassung-ausfertigung-mit-unbekanntem-datum"
              select="'ausfertigung-mit-noch-unbekanntem-datum'"/>
   <xsl:param name="refersto-literal-ereignisreferenz-entwurfsfassung-verkuendung-mit-unbekanntem-datum"
              select="'verkuendung-mit-noch-unbekanntem-datum'"/>
   <xsl:param name="refersto-literal-ereignisreferenz-entwurfsfassung-inkrafttreten"
              select="'inkrafttreten'"/>
   <xsl:param name="refersto-literal-ereignisreferenz-entwurfsfassung-inkrafttreten-mit-unbekanntem-datum"
              select="'inkrafttreten-mit-noch-unbekanntem-datum'"/>
   <xsl:param name="refersto-literal-ereignisreferenz-entwurfsfassung-ausserkrafttreten"
              select="'ausserkrafttreten'"/>
   <xsl:param name="refersto-literal-ereignisreferenz-entwurfsfassung-ausserkrafttreten-mit-unbekanntem-datum"
              select="'ausserkrafttreten-mit-noch-unbekanntem-datum'"/>
   <xsl:param name="refersto-literal-ereignisreferenz-verkündungsfassung-ausfertigung"
              select="'ausfertigung'"/>
   <xsl:param name="refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten"
              select="'inkrafttreten'"/>
   <xsl:param name="refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-grundsaetzlich"
              select="'inkrafttreten-grundsaetzlich'"/>
   <xsl:param name="refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-abweichend"
              select="'inkrafttreten-abweichend'"/>
   <xsl:param name="refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-mit-unbekanntem-datum"
              select="'inkrafttreten-mit-noch-unbekanntem-datum'"/>
   <xsl:param name="refersto-literal-ereignisreferenz-verkündungsfassung-ausserkrafttreten"
              select="'ausserkrafttreten'"/>
   <xsl:param name="refersto-literal-ereignisreferenz-verkündungssfassung-ausserkrafttreten-mit-unbekanntem-datum"
              select="'ausserkrafttreten-mit-noch-unbekanntem-datum'"/>
   <xsl:param name="refersto-literal-ereignisreferenz-neufassung"
              select="'neufassung'"/>
   <xsl:param name="dokumentarten-mit-lebenszyklus-angaben-formulierung-satzanfang-nominativ"
              select="'Ein Regelungstext, ein Bekanntmachungstext oder eine Vereinbarung'"/>
   <xsl:param name="refersto-literal-vorblattabschnitt-erfüllungsaufwand"
              select="'vorblattabschnitt-erfuellungsaufwand'"/>
   <xsl:param name="literal-deklaration-ausnahme-eid-zählweise"
              select="'ordinale-zaehlung-eid'"/>
   <xsl:param name="präfix-eid-nummerierbar" select="'n'"/>
   <xsl:param name="präfix-eid-zitierbar" select="'z'"/>
   <xsl:param name="zitierbare-elementtypen" select="('article', 'paragraph')"/>
   <xsl:param name="platzhalter-datum-unbekannt" select="'0001-01-01'"/>
   <xsl:param name="eli-präfix-entwurfsfassung"
              select="'https://www.egesetzgebung.bund.de/eli/dl'"/>
   <xsl:param name="eli-präfix-verkündungsfassung"
              select="'https://www.recht.bund.de/eli/bund'"/>
   <xsl:param name="eli-präfix-konsolidierte-fassung"
              select="'https://www.ris.bund.de/eli/bund'"/>
   <xsl:param name="eli-agent-entwurf"
              select="tokenize(/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRauthor/@href, '/')[last()]"/>
   <xsl:param name="eli-agent-verkündung-oder-konsolidierung"
              select="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRname/@value"/>
   <xsl:param name="eli-year"
              select="(: -- Nimmt als Wert die in der folgenden Reihenfolge gesuchte letzte (!) gefundene Jahresangabe an -- :) ( (: das erste Entwurfsfassung-FRBRdate; es darf nur eins geben! :) substring(/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRdate[@name = 'erstellungsdatum'][1]/@date, 1, 4), (: Jahr der Ausfertigung :) if (not(empty(/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRdate[@name = 'ausfertigungsdatum']/@date))) then (substring(/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRdate[@name = 'ausfertigungsdatum']/@date, 1, 4)) else (), (: Jahr der Verkündung :) if (not(empty(/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRdate[@name = 'verkuendungsdatum']/@date))) then substring(/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRdate[@name = 'verkuendungsdatum']/@date, 1, 4) else () )[last()]"/>
   <xsl:param name="eli-natural-identifier"
              select="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRnumber/@value"/>
   <xsl:param name="eli-process-identifier" select="$eli-natural-identifier"/>
   <xsl:param name="eli-type-of-legislative-process-document"
              select="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRname/@value"/>
   <xsl:param name="eli-point-in-time"
              select="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRExpression/akn:FRBRdate/@date"/>
   <xsl:param name="eli-version"
              select="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRExpression/akn:FRBRversionNumber/@value"/>
   <xsl:param name="eli-language"
              select="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRExpression/akn:FRBRlanguage/@language"/>
   <xsl:param name="eli-subtype"
              select="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRsubtype/@value"/>
   <xsl:param name="eli-format"
              select="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRManifestation/akn:FRBRformat/@value"/>
   <xsl:param name="eli-point-in-time-manifestation"
              select="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRManifestation/akn:FRBRdate/@date"/>
   <xsl:param name="eli-subagent"
              select="tokenize(/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRExpression/akn:FRBRauthor/@href, '/')[last()]"/>
   <xsl:param name="FRBRthis-verkündungsfassung-work-beschreibung"
              select="'Der eindeutige Bezeichner für Teildokumente in der Verkündungsfassung auf der Work-Ebene'"/>
   <xsl:param name="FRBRthis-verkündungsfassung-work-aufbau"
              select="concat($eli-präfix-verkündungsfassung, '/{agent}/{year}/{natural identifier}/{subtype}')"/>
   <xsl:param name="FRBRthis-verkündungsfassung-work-inhalt"
              select="string-join(($eli-präfix-verkündungsfassung, $eli-agent-verkündung-oder-konsolidierung, $eli-year, $eli-natural-identifier, $eli-subtype), '/')"/>
   <xsl:param name="FRBRthis-verkündungsfassung-expression-beschreibung"
              select="'Der eindeutige Bezeichner für Teildokumente in der Verkündungsfassung auf der Expression-Ebene'"/>
   <xsl:param name="FRBRthis-verkündungsfassung-expression-aufbau"
              select="concat($eli-präfix-verkündungsfassung, '/{agent}/{year}/{natural identifier}/{language}/{subtype}')"/>
   <xsl:param name="FRBRthis-verkündungsfassung-expression-inhalt"
              select="string-join(($eli-präfix-verkündungsfassung, $eli-agent-verkündung-oder-konsolidierung, $eli-year, $eli-natural-identifier, $eli-language, $eli-subtype), '/')"/>
   <xsl:param name="FRBRthis-verkündungsfassung-manifestation-beschreibung"
              select="'Der eindeutige Bezeichner für Teildokumente in der Verkündungsfassung auf der Manifestation-Ebene'"/>
   <xsl:param name="FRBRthis-verkündungsfassung-manifestation-aufbau"
              select="concat($eli-präfix-verkündungsfassung, '/{agent}/{year}/{natural identifier}/{language}/{point in time manifestation}/{subtype}.{format}')"/>
   <xsl:param name="FRBRthis-verkündungsfassung-manifestation-inhalt"
              select="string-join(($eli-präfix-verkündungsfassung, $eli-agent-verkündung-oder-konsolidierung, $eli-year, $eli-natural-identifier, $eli-language, concat($eli-subtype, '.', $eli-format)), '/')"/>
   <xsl:param name="FRBRthis-konsolidierte-fassung-work-beschreibung"
              select="'Der eindeutige Bezeichner für Teildokumente in einer konsolidierten Fassung auf der Work-Ebene'"/>
   <xsl:param name="FRBRthis-konsolidierte-fassung-work-aufbau"
              select="concat($eli-präfix-konsolidierte-fassung, '/{agent}/{year}/{natural identifier}/{subtype}')"/>
   <xsl:param name="FRBRthis-konsolidierte-fassung-work-inhalt"
              select="string-join(($eli-präfix-konsolidierte-fassung, $eli-agent-verkündung-oder-konsolidierung, $eli-year, $eli-natural-identifier, $eli-subtype), '/')"/>
   <xsl:param name="FRBRthis-konsolidierte-fassung-expression-beschreibung"
              select="'Der eindeutige Bezeichner für Teildokumente in einer konsolidierten Fassung auf der Expression-Ebene'"/>
   <xsl:param name="FRBRthis-konsolidierte-fassung-expression-aufbau"
              select="concat($eli-präfix-konsolidierte-fassung, '/{agent}/{year}/{natural identifier}/{point in time}/{version}/{language}/{subtype}')"/>
   <xsl:param name="FRBRthis-konsolidierte-fassung-expression-inhalt"
              select="string-join(($eli-präfix-konsolidierte-fassung, $eli-agent-verkündung-oder-konsolidierung, $eli-year, $eli-natural-identifier, $eli-point-in-time, $eli-version, $eli-language, $eli-subtype), '/')"/>
   <xsl:param name="FRBRthis-konsolidierte-fassung-manifestation-beschreibung"
              select="'Der eindeutige Bezeichner für Teildokumente in einer konsolidierten Fassung auf der Manifestation-Ebene'"/>
   <xsl:param name="FRBRthis-konsolidierte-fassung-manifestation-aufbau"
              select="concat($eli-präfix-konsolidierte-fassung, '/{agent}/{year}/{natural identifier}/{point in time}/{version}/{language}/{point in time manifestation}/{subtype}.{format}')"/>
   <xsl:param name="FRBRthis-konsolidierte-fassung-manifestation-inhalt"
              select="string-join(($eli-präfix-konsolidierte-fassung, $eli-agent-verkündung-oder-konsolidierung, $eli-year, $eli-natural-identifier, $eli-point-in-time, $eli-version, $eli-language, $eli-point-in-time-manifestation, concat($eli-subtype, '.', $eli-format)), '/')"/>
   <xsl:param name="FRBRthis-entwurfsfassung-work-beschreibung"
              select="'Der eindeutige Bezeichner für Teildokumente in der Entwurfsfassung auf der Work-Ebene'"/>
   <xsl:param name="FRBRthis-entwurfsfassung-work-aufbau"
              select="concat($eli-präfix-entwurfsfassung, '/{year}/{agent}/{process identifier}/{type of legislative process document}/{subtype}')"/>
   <xsl:param name="FRBRthis-entwurfsfassung-work-inhalt"
              select="string-join(($eli-präfix-entwurfsfassung, $eli-year, $eli-agent-entwurf, $eli-process-identifier, $eli-type-of-legislative-process-document, $eli-subtype), '/')"/>
   <xsl:param name="FRBRthis-entwurfsfassung-expression-beschreibung"
              select="'Der eindeutige Bezeichner für Teildokumente in der Entwurfsfassung auf der Expression-Ebene'"/>
   <xsl:param name="FRBRthis-entwurfsfassung-expression-aufbau"
              select="concat($eli-präfix-entwurfsfassung, '/{year}/{agent}/{process identifier}/{type of legislative process document}/{subagent}/{point in time}/{version}/{language}/{subtype}')"/>
   <xsl:param name="FRBRthis-entwurfsfassung-expression-inhalt"
              select="string-join(($eli-präfix-entwurfsfassung, $eli-year, $eli-agent-entwurf, $eli-process-identifier, $eli-type-of-legislative-process-document, $eli-subagent, $eli-point-in-time, $eli-version, $eli-language, $eli-subtype), '/')"/>
   <xsl:param name="FRBRthis-entwurfsfassung-manifestation-beschreibung"
              select="'Der eindeutige Bezeichner für Teildokumente in der Entwurfsfassung auf der Manifestation-Ebene'"/>
   <xsl:param name="FRBRthis-entwurfsfassung-manifestation-aufbau"
              select="concat($eli-präfix-entwurfsfassung, '/{year}/{agent}/{process identifier}/{type of legislative process document}/{subagent}/{point in time}/{version}/{language}/{subtype}.{format}')"/>
   <xsl:param name="FRBRthis-entwurfsfassung-manifestation-inhalt"
              select="string-join(($eli-präfix-entwurfsfassung, $eli-year, $eli-agent-entwurf, $eli-process-identifier, $eli-type-of-legislative-process-document, $eli-subagent, $eli-point-in-time, $eli-version, $eli-language, concat($eli-subtype, '.', $eli-format)), '/')"/>
   <xsl:param name="FRBRuri-verkündungsfassung-work-beschreibung"
              select="'Der eindeutige Bezeichner für die Work-Ebene in der Verkündungsfassung'"/>
   <xsl:param name="FRBRuri-verkündungsfassung-work-aufbau"
              select="concat($eli-präfix-verkündungsfassung, '/{agent}/{year}/{natural identifier}')"/>
   <xsl:param name="FRBRuri-verkündungsfassung-work-inhalt"
              select="string-join(($eli-präfix-verkündungsfassung, $eli-agent-verkündung-oder-konsolidierung, $eli-year, $eli-natural-identifier), '/')"/>
   <xsl:param name="FRBRuri-verkündungsfassung-expression-beschreibung"
              select="'Der eindeutige Bezeichner für die Expression-Ebene in der Verkündungsfassung'"/>
   <xsl:param name="FRBRuri-verkündungsfassung-expression-aufbau"
              select="concat($eli-präfix-verkündungsfassung, '{agent}/{year}/{natural identifier}/{language}')"/>
   <xsl:param name="FRBRuri-verkündungsfassung-expression-inhalt"
              select="string-join(($eli-präfix-verkündungsfassung, $eli-agent-verkündung-oder-konsolidierung, $eli-year, $eli-natural-identifier, $eli-language), '/')"/>
   <xsl:param name="FRBRuri-verkündungsfassung-manifestation-beschreibung"
              select="'Der eindeutige Bezeichner für die Manifestation-Ebene in der Verkündungsfassung'"/>
   <xsl:param name="FRBRuri-verkündungsfassung-manifestation-aufbau"
              select="$FRBRthis-verkündungsfassung-manifestation-aufbau"/>
   <xsl:param name="FRBRuri-verkündungsfassung-manifestation-inhalt"
              select="$FRBRthis-verkündungsfassung-manifestation-inhalt"/>
   <xsl:param name="FRBRuri-konsolidierte-fassung-work-beschreibung"
              select="'Der eindeutige Bezeichner für die Work-Ebene der konsolidierten Fassung'"/>
   <xsl:param name="FRBRuri-konsolidierte-fassung-work-aufbau"
              select="concat($eli-präfix-konsolidierte-fassung, '/{agent}/{year}/{natural identifier}')"/>
   <xsl:param name="FRBRuri-konsolidierte-fassung-work-inhalt"
              select="string-join(($eli-präfix-konsolidierte-fassung, $eli-agent-verkündung-oder-konsolidierung, $eli-year, $eli-natural-identifier), '/')"/>
   <xsl:param name="FRBRuri-konsolidierte-fassung-expression-beschreibung"
              select="'Der eindeutige Bezeichner für die Expression-Ebene in der konsolidierten Fassung'"/>
   <xsl:param name="FRBRuri-konsolidierte-fassung-expression-aufbau"
              select="concat($eli-präfix-konsolidierte-fassung, '{agent}/{year}/{natural identifier}/{point in time}/{version}/{language}')"/>
   <xsl:param name="FRBRuri-konsolidierte-fassung-expression-inhalt"
              select="string-join(($eli-präfix-konsolidierte-fassung, $eli-agent-verkündung-oder-konsolidierung, $eli-year, $eli-natural-identifier, $eli-point-in-time, $eli-version, $eli-language), '/')"/>
   <xsl:param name="FRBRuri-konsolidierte-fassung-manifestation-beschreibung"
              select="'Der eindeutige Bezeichner für die Manifestation-Ebene in der konsolidierten Fassung'"/>
   <xsl:param name="FRBRuri-konsolidierte-fassung-manifestation-aufbau"
              select="$FRBRthis-konsolidierte-fassung-manifestation-aufbau"/>
   <xsl:param name="FRBRuri-konsolidierte-fassung-manifestation-inhalt"
              select="$FRBRthis-konsolidierte-fassung-manifestation-inhalt"/>
   <xsl:param name="FRBRuri-entwurfsfassung-work-beschreibung"
              select="'Der eindeutige Bezeichner für die Work-Ebene in der Entwurfsfassung'"/>
   <xsl:param name="FRBRuri-entwurfsfassung-work-aufbau"
              select="concat($eli-präfix-entwurfsfassung, '/{year}/{agent}/{process identifier}/{type of legislative process document}')"/>
   <xsl:param name="FRBRuri-entwurfsfassung-work-inhalt"
              select="string-join(($eli-präfix-entwurfsfassung, $eli-year, $eli-agent-entwurf, $eli-process-identifier, $eli-type-of-legislative-process-document), '/')"/>
   <xsl:param name="FRBRuri-entwurfsfassung-expression-beschreibung"
              select="'Der eindeutige Bezeichner für die Expression-Ebene in der Entwurfsfassung'"/>
   <xsl:param name="FRBRuri-entwurfsfassung-expression-aufbau"
              select="concat($eli-präfix-entwurfsfassung, '/{year}/{agent}/{process identifier}/{type of legislative process document}/{subagent}/{point in time}/{version}/{language}')"/>
   <xsl:param name="FRBRuri-entwurfsfassung-expression-inhalt"
              select="string-join(($eli-präfix-entwurfsfassung, $eli-year, $eli-agent-entwurf, $eli-process-identifier, $eli-type-of-legislative-process-document, $eli-subagent, $eli-point-in-time, $eli-version, $eli-language), '/')"/>
   <xsl:param name="FRBRuri-entwurfsfassung-manifestation-beschreibung"
              select="'Der eindeutige Bezeichner für die Manifestation-Ebene in der Entwurfsfassung'"/>
   <xsl:param name="FRBRuri-entwurfsfassung-manifestation-aufbau"
              select="concat($eli-präfix-entwurfsfassung, '/{year}/{agent}/{process identifier}/{type of legislative process document}/{subagent}/{point in time}/{version}/{language}/{subtype}.{format}')"/>
   <xsl:param name="FRBRuri-entwurfsfassung-manifestation-inhalt"
              select="string-join(($eli-präfix-entwurfsfassung, $eli-year, $eli-agent-entwurf, $eli-process-identifier, $eli-type-of-legislative-process-document, $eli-subagent, $eli-point-in-time, $eli-version, $eli-language, concat($eli-subtype, '.', $eli-format)), '/')"/>
   <xsl:variable name="fassung-entwurfsfassung" select="'false()'"/>
   <xsl:variable name="zulässige-literale-in-kombination-mit-repeal"
                 select="($refersto-literal-ereignisreferenz-verkündungsfassung-ausserkrafttreten, $refersto-literal-ereignisreferenz-entwurfsfassung-ausserkrafttreten, $refersto-literal-ereignisreferenz-entwurfsfassung-ausserkrafttreten-mit-unbekanntem-datum, $refersto-literal-ereignisreferenz-verkündungssfassung-ausserkrafttreten-mit-unbekanntem-datum)"/>
   <xsl:param name="schxslt.validate.initial-document-uri" as="xs:string?"/>
   <xsl:template name="schxslt.validate">
      <xsl:apply-templates select="document($schxslt.validate.initial-document-uri)"/>
   </xsl:template>
   <xsl:template match="root()">
      <xsl:param name="schxslt.validate.recursive-call"
                 as="xs:boolean"
                 select="false()"/>
      <xsl:choose>
         <xsl:when test="not($schxslt.validate.recursive-call) and (normalize-space($schxslt.validate.initial-document-uri) != '')">
            <xsl:apply-templates select="document($schxslt.validate.initial-document-uri)">
               <xsl:with-param name="schxslt.validate.recursive-call"
                               as="xs:boolean"
                               select="true()"/>
            </xsl:apply-templates>
         </xsl:when>
         <xsl:otherwise>
            <xsl:variable name="metadata" as="element()?">
               <svrl:metadata xmlns:dct="http://purl.org/dc/terms/"
                              xmlns:skos="http://www.w3.org/2004/02/skos/core#"
                              xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <dct:creator>
                     <dct:Agent>
                        <skos:prefLabel>
                           <xsl:value-of separator="/"
                                         select="(system-property('xsl:product-name'), system-property('xsl:product-version'))"/>
                        </skos:prefLabel>
                     </dct:Agent>
                  </dct:creator>
                  <dct:created>
                     <xsl:value-of select="current-dateTime()"/>
                  </dct:created>
                  <dct:source>
                     <rdf:Description xmlns:dc="http://purl.org/dc/elements/1.1/">
                        <dct:creator>
                           <dct:Agent>
                              <skos:prefLabel>SchXslt/1.10.1 SAXON/HE 12.9</skos:prefLabel>
                              <schxslt.compile.typed-variables xmlns="https://doi.org/10.5281/zenodo.1495494#">true</schxslt.compile.typed-variables>
                           </dct:Agent>
                        </dct:creator>
                        <dct:created>2026-09-01T13:38:38.295685946Z</dct:created>
                     </rdf:Description>
                  </dct:source>
               </svrl:metadata>
            </xsl:variable>
            <xsl:variable name="report" as="element(schxslt:report)">
               <schxslt:report>
                  <xsl:call-template name="d15e206"/>
               </schxslt:report>
            </xsl:variable>
            <xsl:variable name="schxslt:report" as="node()*">
               <xsl:sequence select="$metadata"/>
               <xsl:for-each select="$report/schxslt:document">
                  <xsl:for-each select="schxslt:pattern">
                     <xsl:sequence select="node()"/>
                     <xsl:sequence select="../schxslt:rule[@pattern = current()/@id]/node()"/>
                  </xsl:for-each>
               </xsl:for-each>
            </xsl:variable>
            <svrl:schematron-output xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    schemaVersion="LegalDocML.de 1.9 (03.11.2025)"
                                    title="Regelungstext Entwurfsfassung Regelungstext Regelungstext Allgemein Klasse Inhaltsverzeichnis Klasse: Hauptteil Aufzählungen Gliederungsebenen Schlussteil Vorblatt (Regelungstext) Vorblatt (Beschlussempfehlung) Begründung Prüfkriterien Prüfkriterien Allgemeiner Teil Regelungsfolgen Anschreiben Ausschussüberweisung/Legislaturperiode/Drucksachennummer Vereinbarung Änderungsbefehle Struktureller Aufbau von eId-Textknoten Dereferenzierbarkeit lokaler Verweise von akn:destinations innerhalb einer akn:passiveModification Struktureller Aufbau der ELI-Uris (Metadaten) Verweise auf TLC-Klassen Verwendung von Markern  Regeln zu Listen in Regelungstexten  Ontologie Regeln zu Beschlussempfehlungen Regeln zu Regelungstext-Anlagen Regeln zu Berichten Regeln zu einzelnen Metadaten Regeln zu URI-Verweisen Zulässigkeit von Literalen / Mustern je Attribut an FRBR-Typen, abhängig von der Fassung (Entwurf vs. Verkündung)">
               <svrl:ns-prefix-in-attribute-values prefix="akn" uri="http://Inhaltsdaten.LegalDocML.de/1.9/"/>
               <svrl:ns-prefix-in-attribute-values prefix="regtxt" uri="http://MetadatenRegelungstext.LegalDocML.de/1.9/"/>
               <svrl:ns-prefix-in-attribute-values prefix="redok"
                                                   uri="http://MetadatenRechtsetzungsdokument.LegalDocML.de/1.9/"/>
               <svrl:ns-prefix-in-attribute-values prefix="btag" uri="http://MetadatenBundestag.LegalDocML.de/1.9/"/>
               <svrl:ns-prefix-in-attribute-values prefix="brat" uri="http://MetadatenBundesrat.LegalDocML.de/1.9/"/>
               <svrl:ns-prefix-in-attribute-values prefix="breg" uri="http://MetadatenBundesregierung.LegalDocML.de/1.9/"/>
               <svrl:ns-prefix-in-attribute-values prefix="fhilf"
                                                   uri="http://MetadatenFormulierungshilfe.LegalDocML.de/1.9/"/>
               <svrl:ns-prefix-in-attribute-values prefix="nkr" uri="http://MetadatenNormenkontrollrat.LegalDocML.de/1.9/"/>
               <svrl:ns-prefix-in-attribute-values prefix="sonst"
                                                   uri="http://MetadatenSonstigerVeroeffentlichungstext.LegalDocML.de/1.9/"/>
               <svrl:ns-prefix-in-attribute-values prefix="fkt" uri="lokale-funktionen"/>
               <xsl:sequence select="$schxslt:report"/>
            </svrl:schematron-output>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="text() | @*" mode="#all" priority="-10"/>
   <xsl:template match="/" mode="#all" priority="-10">
      <xsl:apply-templates mode="#current" select="node()"/>
   </xsl:template>
   <xsl:template match="*" mode="#all" priority="-10">
      <xsl:apply-templates mode="#current" select="@*"/>
      <xsl:apply-templates mode="#current" select="node()"/>
   </xsl:template>
   <xsl:template name="d15e206">
      <schxslt:document>
         <schxslt:pattern id="d15e206">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e251">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e285">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e297">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e305">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e317">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e332">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e341">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e350">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e360">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e369">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e381">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e396">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e406">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e421">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e435">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e444">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e457">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e509">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e533">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e545">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e573">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e629">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e650">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e685">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e709">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e740">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e753">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e768">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e783">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e793">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e800">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e815">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e831">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e840">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e849">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e929">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e952">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e1026">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e1049">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e1076">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e1088">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e1109">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e1133">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e1148">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e1167">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e1346">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e1446">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e1565">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e1625">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e1676">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e1750">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e1774">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e1796">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e1850">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e1910">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e1919">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e1930">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e1939">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e1951">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e1965">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e2023">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e2044">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e2058">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e2075">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e2108">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e2132">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e2151">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e2163">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e2178">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e2191">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    name="expression.FRBRdate in Entwurfs- und konsolidierter Fassung">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e2206">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    name="Kein expression.FRBRdate in der Verkündungsfassung">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e2222">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e2323">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <schxslt:pattern id="d15e2424">
            <xsl:if test="exists(base-uri(root()))">
               <xsl:attribute name="documents" select="base-uri(root())"/>
            </xsl:if>
            <xsl:for-each select="root()">
               <svrl:active-pattern xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
                  <xsl:attribute name="documents" select="base-uri(.)"/>
               </svrl:active-pattern>
            </xsl:for-each>
         </schxslt:pattern>
         <xsl:apply-templates mode="d15e206" select="root()"/>
      </schxslt:document>
   </xsl:template>
   <xsl:template match="/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri and $form = $form-eingebundene-stammform]"
                 priority="144"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e206']">
            <schxslt:rule pattern="d15e206">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00019 for context "/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri and $form = $form-eingebundene-stammform]" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00019">
                  <xsl:attribute name="context">/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri and $form = $form-eingebundene-stammform]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e206">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00019">
                  <xsl:attribute name="context">/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri and $form = $form-eingebundene-stammform]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(not(./akn:preface/akn:block))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00019-000">
                     <xsl:attribute name="test">not(./akn:preface/akn:block)</xsl:attribute>
                     <svrl:text>Ein Regelungstext in Stammform in einer Mantelform darf keinen Datumscontainer innerhalb des Dokumentenkopfes enthalten.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(not(./akn:preamble/akn:formula))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00019-005">
                     <xsl:attribute name="test">not(./akn:preamble/akn:formula)</xsl:attribute>
                     <svrl:text>Ein Regelungstext in Stammform in einer Mantelform darf keine Eingangsformel enthalten.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(not(./akn:conclusions))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00019-010">
                     <xsl:attribute name="test">not(./akn:conclusions)</xsl:attribute>
                     <svrl:text>Ein Regelungstext in Stammform in einer Mantelform darf keinen Schlussteil enthalten.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e206')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri and $typ = ($typ-gesetz, $typ-vertragsgesetz)]"
                 priority="143"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e206']">
            <schxslt:rule pattern="d15e206">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00020 for context "/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri and $typ = ($typ-gesetz, $typ-vertragsgesetz)]" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00020">
                  <xsl:attribute name="context">/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri and $typ = ($typ-gesetz, $typ-vertragsgesetz)]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e206">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00020">
                  <xsl:attribute name="context">/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri and $typ = ($typ-gesetz, $typ-vertragsgesetz)]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(./akn:preamble/akn:formula)">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00020-005">
                     <xsl:attribute name="test">./akn:preamble/akn:formula</xsl:attribute>
                     <svrl:text>Für ein Gesetz muss eine Eingangsformel verwendet werden.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(not(./akn:conclusions/akn:formula))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="warn"
                                      id="SCH-00020-010">
                     <xsl:attribute name="test">not(./akn:conclusions/akn:formula)</xsl:attribute>
                     <svrl:text>Für ein Gesetz in der Entwurfsfassung wird in der Regel keine Schlussformel benutzt.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e206')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri and $typ = ($typ-verordnung, $typ-vertragsverordnung)]"
                 priority="142"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e206']">
            <schxslt:rule pattern="d15e206">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00030 for context "/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri and $typ = ($typ-verordnung, $typ-vertragsverordnung)]" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00030">
                  <xsl:attribute name="context">/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri and $typ = ($typ-verordnung, $typ-vertragsverordnung)]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e206">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00030">
                  <xsl:attribute name="context">/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri and $typ = ($typ-verordnung, $typ-vertragsverordnung)]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(not(./akn:preamble/akn:formula))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00030-005">
                     <xsl:attribute name="test">not(./akn:preamble/akn:formula)</xsl:attribute>
                     <svrl:text>Eine Verordnung darf keine Eingangsformel enthalten.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(not(./akn:conclusions/akn:formula))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="warn"
                                      id="SCH-00030-010">
                     <xsl:attribute name="test">not(./akn:conclusions/akn:formula)</xsl:attribute>
                     <svrl:text>Für eine Verordnung in der Entwurfsfassung wird in der Regel keine Schlussformel benutzt.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e206')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri and $typ = $typ-verwaltungsvorschrift]"
                 priority="141"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e206']">
            <schxslt:rule pattern="d15e206">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00040 for context "/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri and $typ = $typ-verwaltungsvorschrift]" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00040">
                  <xsl:attribute name="context">/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri and $typ = $typ-verwaltungsvorschrift]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e206">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00040">
                  <xsl:attribute name="context">/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri and $typ = $typ-verwaltungsvorschrift]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(not(./akn:preamble/akn:formula))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00040-005">
                     <xsl:attribute name="test">not(./akn:preamble/akn:formula)</xsl:attribute>
                     <svrl:text>Eine Verwaltungsvorschrift darf keine Eingangsformel enthalten.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(not(./akn:conclusions/akn:formula))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="warn"
                                      id="SCH-00040-010">
                     <xsl:attribute name="test">not(./akn:conclusions/akn:formula)</xsl:attribute>
                     <svrl:text>Für eine Verwaltungsvorschrift in der Entwurfsfassung wird in der Regel keine Schlussformel benutzt.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e206')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="/akn:akomaNtoso/akn:act[$ist-verkündungsfassung and @name = $art-regelungstext-uri and $form = $form-eingebundene-stammform]"
                 priority="140"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e251']">
            <schxslt:rule pattern="d15e251">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00048 for context "/akn:akomaNtoso/akn:act[$ist-verkündungsfassung and @name = $art-regelungstext-uri and $form = $form-eingebundene-stammform]" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00048">
                  <xsl:attribute name="context">/akn:akomaNtoso/akn:act[$ist-verkündungsfassung and @name = $art-regelungstext-uri and $form = $form-eingebundene-stammform]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e251">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00048">
                  <xsl:attribute name="context">/akn:akomaNtoso/akn:act[$ist-verkündungsfassung and @name = $art-regelungstext-uri and $form = $form-eingebundene-stammform]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(not(./akn:preface/akn:block))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00048-000">
                     <xsl:attribute name="test">not(./akn:preface/akn:block)</xsl:attribute>
                     <svrl:text>Ein Regelungstext in Stammform in einer Mantelform darf keinen Datums-Container innerhalb des Dokumentenkopfes enthalten.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(not(./akn:preamble/akn:formula))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00048-005">
                     <xsl:attribute name="test">not(./akn:preamble/akn:formula)</xsl:attribute>
                     <svrl:text>Ein Regelungstext in Stammform in einer Mantelform darf keine Eingangsformel enthalten.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(not(./akn:conclusions))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00048-010">
                     <xsl:attribute name="test">not(./akn:conclusions)</xsl:attribute>
                     <svrl:text>Ein Regelungstext in Stammform in einer Mantelform darf keinen Schlussteil enthalten.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e251')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="/akn:akomaNtoso/akn:act[$ist-verkündungsfassung and @name = $art-regelungstext-uri and $typ = ($typ-verordnung, $typ-vertragsverordnung)]"
                 priority="139"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e251']">
            <schxslt:rule pattern="d15e251">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00060 for context "/akn:akomaNtoso/akn:act[$ist-verkündungsfassung and @name = $art-regelungstext-uri and $typ = ($typ-verordnung, $typ-vertragsverordnung)]" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00060">
                  <xsl:attribute name="context">/akn:akomaNtoso/akn:act[$ist-verkündungsfassung and @name = $art-regelungstext-uri and $typ = ($typ-verordnung, $typ-vertragsverordnung)]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e251">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00060">
                  <xsl:attribute name="context">/akn:akomaNtoso/akn:act[$ist-verkündungsfassung and @name = $art-regelungstext-uri and $typ = ($typ-verordnung, $typ-vertragsverordnung)]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(not(./akn:preamble/akn:formula))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00060-005">
                     <xsl:attribute name="test">not(./akn:preamble/akn:formula)</xsl:attribute>
                     <svrl:text>Eine Verordnung darf keine Eingangsformel enthalten.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e251')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="/akn:akomaNtoso/akn:act[$ist-verkündungsfassung and @name = $art-regelungstext-uri and $typ = $typ-verwaltungsvorschrift]"
                 priority="138"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e251']">
            <schxslt:rule pattern="d15e251">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00070 for context "/akn:akomaNtoso/akn:act[$ist-verkündungsfassung and @name = $art-regelungstext-uri and $typ = $typ-verwaltungsvorschrift]" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00070">
                  <xsl:attribute name="context">/akn:akomaNtoso/akn:act[$ist-verkündungsfassung and @name = $art-regelungstext-uri and $typ = $typ-verwaltungsvorschrift]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e251">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00070">
                  <xsl:attribute name="context">/akn:akomaNtoso/akn:act[$ist-verkündungsfassung and @name = $art-regelungstext-uri and $typ = $typ-verwaltungsvorschrift]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(not(./akn:preamble/akn:formula))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00070-005">
                     <xsl:attribute name="test">not(./akn:preamble/akn:formula)</xsl:attribute>
                     <svrl:text>Eine Verwaltungsvorschrift darf keine Eingangsformel enthalten.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e251')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="/akn:akomaNtoso/akn:act[@name = $art-regelungstext-uri and $ist-verkündungsfassung and not($form = $form-eingebundene-stammform)]"
                 priority="137"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e285']">
            <schxslt:rule pattern="d15e285">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00071 for context "/akn:akomaNtoso/akn:act[@name = $art-regelungstext-uri and $ist-verkündungsfassung and not($form = $form-eingebundene-stammform)]" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00071">
                  <xsl:attribute name="context">/akn:akomaNtoso/akn:act[@name = $art-regelungstext-uri and $ist-verkündungsfassung and not($form = $form-eingebundene-stammform)]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e285">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00071">
                  <xsl:attribute name="context">/akn:akomaNtoso/akn:act[@name = $art-regelungstext-uri and $ist-verkündungsfassung and not($form = $form-eingebundene-stammform)]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(./akn:preface/akn:block)">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00071-005">
                     <xsl:attribute name="test">./akn:preface/akn:block</xsl:attribute>
                     <svrl:text>Für einen Regelungstext in der Verkündungsfassung muss ein Datums-Container innerhalb des Dokumentenkopfes verwendet werden. </svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(./akn:conclusions/akn:blockContainer)">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00071-010">
                     <xsl:attribute name="test">./akn:conclusions/akn:blockContainer</xsl:attribute>
                     <svrl:text>Für einen Regelungstext in der Verkündungsfassung muss ein Signaturblock verwendet werden.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e285')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:act/akn:conclusions/akn:formula"
                 priority="136"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e305']">
            <schxslt:rule pattern="d15e305">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00073 for context "akn:act/akn:conclusions/akn:formula" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00073">
                  <xsl:attribute name="context">akn:act/akn:conclusions/akn:formula</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e305">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00073">
                  <xsl:attribute name="context">akn:act/akn:conclusions/akn:formula</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(empty(preceding-sibling::akn:*))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00073-005">
                     <xsl:attribute name="test">empty(preceding-sibling::akn:*)</xsl:attribute>
                     <svrl:text>Die Schlussformel (akn:formula) muss das erste Element im Schluss des Regelungstextes (akn:conclusions) sein.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e305')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="/akn:akomaNtoso/*[@name = $art-regelungstext-uri and $typ = ($typ-gesetz, $typ-vertragsgesetz)]/akn:preamble"
                 priority="135"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e317']">
            <schxslt:rule pattern="d15e317">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00100 for context "/akn:akomaNtoso/*[@name = $art-regelungstext-uri and $typ = ($typ-gesetz, $typ-vertragsgesetz)]/akn:preamble" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00100">
                  <xsl:attribute name="context">/akn:akomaNtoso/*[@name = $art-regelungstext-uri and $typ = ($typ-gesetz, $typ-vertragsgesetz)]/akn:preamble</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e317">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00100">
                  <xsl:attribute name="context">/akn:akomaNtoso/*[@name = $art-regelungstext-uri and $typ = ($typ-gesetz, $typ-vertragsgesetz)]/akn:preamble</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(not(./akn:citations))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00100-005">
                     <xsl:attribute name="test">not(./akn:citations)</xsl:attribute>
                     <svrl:text>Ein Gesetz darf keine Ermächtigungsnorm enthalten.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e317')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="/akn:akomaNtoso/* [ @name = $art-regelungstext-uri and $typ = ($typ-verordnung, $typ-vertragsverordnung) and $form = ($form-stammform, $form-mantelform) and ($ist-entwurfsfassung or $ist-verkündungsfassung) ]/akn:preamble"
                 priority="134"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e317']">
            <schxslt:rule pattern="d15e317">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00110 for context "/akn:akomaNtoso/* [ @name = $art-regelungstext-uri and $typ = ($typ-verordnung, $typ-vertragsverordnung) and $form = ($form-stammform, $form-mantelform) and ($ist-entwurfsfassung or $ist-verkündungsfassung) ]/akn:preamble" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00110">
                  <xsl:attribute name="context">/akn:akomaNtoso/* [ @name = $art-regelungstext-uri and $typ = ($typ-verordnung, $typ-vertragsverordnung) and $form = ($form-stammform, $form-mantelform) and ($ist-entwurfsfassung or $ist-verkündungsfassung) ]/akn:preamble</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e317">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00110">
                  <xsl:attribute name="context">/akn:akomaNtoso/* [ @name = $art-regelungstext-uri and $typ = ($typ-verordnung, $typ-vertragsverordnung) and $form = ($form-stammform, $form-mantelform) and ($ist-entwurfsfassung or $ist-verkündungsfassung) ]/akn:preamble</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(./akn:citations)">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00110-005">
                     <xsl:attribute name="test">./akn:citations</xsl:attribute>
                     <svrl:text>Eine Verordnung muss Ermächtigungsnormen bereitstellen.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e317')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:quotedStructure//akn:article [ $form = $form-mantelform and $typ = ($typ-gesetz, $typ-verordnung, $typ-satzung, $typ-verwaltungsvorschrift, $typ-vertragsgesetz, $typ-vertragsverordnung) ]"
                 priority="133"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e332']">
            <schxslt:rule pattern="d15e332">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00120 for context "akn:quotedStructure//akn:article [ $form = $form-mantelform and $typ = ($typ-gesetz, $typ-verordnung, $typ-satzung, $typ-verwaltungsvorschrift, $typ-vertragsgesetz, $typ-vertragsverordnung) ]" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00120">
                  <xsl:attribute name="context">akn:quotedStructure//akn:article [ $form = $form-mantelform and $typ = ($typ-gesetz, $typ-verordnung, $typ-satzung, $typ-verwaltungsvorschrift, $typ-vertragsgesetz, $typ-vertragsverordnung) ]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e332">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00120">
                  <xsl:attribute name="context">akn:quotedStructure//akn:article [ $form = $form-mantelform and $typ = ($typ-gesetz, $typ-verordnung, $typ-satzung, $typ-verwaltungsvorschrift, $typ-vertragsgesetz, $typ-vertragsverordnung) ]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not((@refersTo = ($refersto-literal-mantelform, $refersto-literal-stammform, $refersto-literal-geltungszeitregel, $refersto-literal-geltungszeitregel-inkrafttreten, $refersto-literal-geltungszeitregel-ausserkrafttreten, $refersto-literal-vertragsgesetz, $refersto-literal-vertragsverordnung)))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00120-000">
                     <xsl:attribute name="test">(@refersTo = ($refersto-literal-mantelform, $refersto-literal-stammform, $refersto-literal-geltungszeitregel, $refersto-literal-geltungszeitregel-inkrafttreten, $refersto-literal-geltungszeitregel-ausserkrafttreten, $refersto-literal-vertragsgesetz, $refersto-literal-vertragsverordnung))</xsl:attribute>
                     <svrl:text>Wird innerhalb eines Änderungsbefehls eine Einzelvorschrift in Gänze geändert, neugefasst, hinzugefügt oder gelöscht, so muss diese näher - als Mantelform oder Stammform, als Geltungszeitregel oder als Vertragsgesetz bzw. Vertragsverordnung - bestimmt werden.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e332')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:article[not(ancestor::akn:quotedStructure)]/@refersTo [ $form = $form-mantelform and $typ = ($typ-gesetz, $typ-verordnung, $typ-satzung, $typ-verwaltungsvorschrift, $typ-vertragsgesetz, $typ-vertragsverordnung) and ($ist-entwurfsfassung or $ist-verkündungsfassung) ]"
                 priority="132"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e341']">
            <schxslt:rule pattern="d15e341">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00121 for context "akn:article[not(ancestor::akn:quotedStructure)]/@refersTo [ $form = $form-mantelform and $typ = ($typ-gesetz, $typ-verordnung, $typ-satzung, $typ-verwaltungsvorschrift, $typ-vertragsgesetz, $typ-vertragsverordnung) and ($ist-entwurfsfassung or $ist-verkündungsfassung) ]" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00121">
                  <xsl:attribute name="context">akn:article[not(ancestor::akn:quotedStructure)]/@refersTo [ $form = $form-mantelform and $typ = ($typ-gesetz, $typ-verordnung, $typ-satzung, $typ-verwaltungsvorschrift, $typ-vertragsgesetz, $typ-vertragsverordnung) and ($ist-entwurfsfassung or $ist-verkündungsfassung) ]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e341">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00121">
                  <xsl:attribute name="context">akn:article[not(ancestor::akn:quotedStructure)]/@refersTo [ $form = $form-mantelform and $typ = ($typ-gesetz, $typ-verordnung, $typ-satzung, $typ-verwaltungsvorschrift, $typ-vertragsgesetz, $typ-vertragsverordnung) and ($ist-entwurfsfassung or $ist-verkündungsfassung) ]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(. = ($refersto-literal-hauptaenderung, $refersto-literal-folgeaenderung, $refersto-literal-eingebundene-stammform, $refersto-literal-geltungszeitregel, $refersto-literal-geltungszeitregel-inkrafttreten, $refersto-literal-geltungszeitregel-ausserkrafttreten))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00121-000">
                     <xsl:attribute name="test">. = ($refersto-literal-hauptaenderung, $refersto-literal-folgeaenderung, $refersto-literal-eingebundene-stammform, $refersto-literal-geltungszeitregel, $refersto-literal-geltungszeitregel-inkrafttreten, $refersto-literal-geltungszeitregel-ausserkrafttreten)</xsl:attribute>
                     <svrl:text>Liegt ein Regelungstext in Mantelform vor und seine Einzelvorschriften sind mittels @refersTo näher bestimmt, so dürfen lediglich folgende Literale verwendet werden: "hauptaenderung", "folgeaenderung", "eingebundene-stammform", "geltungszeitregel", "geltungszeitregel-inkrafttreten", "geltungszeitregel-ausserkrafttreten".</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e341')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:article/@refersTo [ $form = $form-stammform and $typ = ($typ-gesetz, $typ-verordnung, $typ-satzung, $typ-verwaltungsvorschrift, $typ-vertragsgesetz, $typ-vertragsverordnung) and ($ist-entwurfsfassung or $ist-verkündungsfassung) ]"
                 priority="131"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e350']">
            <schxslt:rule pattern="d15e350">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00122 for context "akn:article/@refersTo [ $form = $form-stammform and $typ = ($typ-gesetz, $typ-verordnung, $typ-satzung, $typ-verwaltungsvorschrift, $typ-vertragsgesetz, $typ-vertragsverordnung) and ($ist-entwurfsfassung or $ist-verkündungsfassung) ]" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00122">
                  <xsl:attribute name="context">akn:article/@refersTo [ $form = $form-stammform and $typ = ($typ-gesetz, $typ-verordnung, $typ-satzung, $typ-verwaltungsvorschrift, $typ-vertragsgesetz, $typ-vertragsverordnung) and ($ist-entwurfsfassung or $ist-verkündungsfassung) ]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e350">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00122">
                  <xsl:attribute name="context">akn:article/@refersTo [ $form = $form-stammform and $typ = ($typ-gesetz, $typ-verordnung, $typ-satzung, $typ-verwaltungsvorschrift, $typ-vertragsgesetz, $typ-vertragsverordnung) and ($ist-entwurfsfassung or $ist-verkündungsfassung) ]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not((. = $refersto-literal-geltungszeitregel and count(//akn:article/@refersTo) = 1) or (. = $refersto-literal-geltungszeitregel-inkrafttreten and (every $r in //akn:article/@refersTo satisfies $r = $refersto-literal-geltungszeitregel-inkrafttreten or $r = $refersto-literal-geltungszeitregel-ausserkrafttreten)))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00122-000">
                     <xsl:attribute name="test">(. = $refersto-literal-geltungszeitregel and count(//akn:article/@refersTo) = 1) or (. = $refersto-literal-geltungszeitregel-inkrafttreten and (every $r in //akn:article/@refersTo satisfies $r = $refersto-literal-geltungszeitregel-inkrafttreten or $r = $refersto-literal-geltungszeitregel-ausserkrafttreten))</xsl:attribute>
                     <svrl:text>In einem Regelungstext in Stammform darf entweder genau eine mit refersTo="geltungszeitregel" ausgezeichnete Einzelvorschrift und keine weiteren mit refersTo ausgezeichneten Einzelvorschriften haben oder genau eine mit refersTo="geltungszeitregel-inkrafttreten" ausgezeichnete Einzelvorschrift haben und optional eine mit refersTo="geltungszeitregel-ausserkrafttreten" ausgezeichnete Einzelvorschrift.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e350')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:article[descendant::akn:mod and not(ancestor::akn:quotedStructure)] [ $form = $form-mantelform and $typ = ($typ-gesetz, $typ-verordnung, $typ-satzung, $typ-verwaltungsvorschrift, $typ-vertragsgesetz, $typ-vertragsverordnung) ]"
                 priority="130"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e360']">
            <schxslt:rule pattern="d15e360">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00123 for context "akn:article[descendant::akn:mod and not(ancestor::akn:quotedStructure)] [ $form = $form-mantelform and $typ = ($typ-gesetz, $typ-verordnung, $typ-satzung, $typ-verwaltungsvorschrift, $typ-vertragsgesetz, $typ-vertragsverordnung) ]" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00123">
                  <xsl:attribute name="context">akn:article[descendant::akn:mod and not(ancestor::akn:quotedStructure)] [ $form = $form-mantelform and $typ = ($typ-gesetz, $typ-verordnung, $typ-satzung, $typ-verwaltungsvorschrift, $typ-vertragsgesetz, $typ-vertragsverordnung) ]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e360">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00123">
                  <xsl:attribute name="context">akn:article[descendant::akn:mod and not(ancestor::akn:quotedStructure)] [ $form = $form-mantelform and $typ = ($typ-gesetz, $typ-verordnung, $typ-satzung, $typ-verwaltungsvorschrift, $typ-vertragsgesetz, $typ-vertragsverordnung) ]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(@refersTo = ($refersto-literal-hauptaenderung, $refersto-literal-folgeaenderung, $refersto-literal-geltungszeitregel, $refersto-literal-geltungszeitregel-inkrafttreten, $refersto-literal-geltungszeitregel-ausserkrafttreten))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00123-000">
                     <xsl:attribute name="test">@refersTo = ($refersto-literal-hauptaenderung, $refersto-literal-folgeaenderung, $refersto-literal-geltungszeitregel, $refersto-literal-geltungszeitregel-inkrafttreten, $refersto-literal-geltungszeitregel-ausserkrafttreten)</xsl:attribute>
                     <svrl:text>Eine Einzelvorschrift, die einen Änderungsbefehl beinhaltet, muss entweder als Hauptänderung, Folgeänderung oder als Geltungszeit ausgezeichnet werden.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e360')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="/akn:akomaNtoso/akn:bill/akn:meta/akn:identification/akn:FRBRWork"
                 priority="129"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e369']">
            <schxslt:rule pattern="d15e369">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00125 for context "/akn:akomaNtoso/akn:bill/akn:meta/akn:identification/akn:FRBRWork" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00125">
                  <xsl:attribute name="context">/akn:akomaNtoso/akn:bill/akn:meta/akn:identification/akn:FRBRWork</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e369">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00125">
                  <xsl:attribute name="context">/akn:akomaNtoso/akn:bill/akn:meta/akn:identification/akn:FRBRWork</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(count(akn:FRBRdate) eq 1)">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(akn:FRBRdate[last()])}"
                                      id="SCH-00125-000">
                     <xsl:attribute name="test">count(akn:FRBRdate) eq 1</xsl:attribute>
                     <svrl:text> In einer Entwurfsfassung darf das Datum auf Work-Ebene nur genau einmal vorkommen. </svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e369')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri]/akn:preamble"
                 priority="128"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e381']">
            <schxslt:rule pattern="d15e381">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00130 for context "/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri]/akn:preamble" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00130">
                  <xsl:attribute name="context">/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri]/akn:preamble</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e381">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00130">
                  <xsl:attribute name="context">/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri]/akn:preamble</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="count(./akn:blockContainer[@refersTo = 'inhaltsuebersicht']) gt 1">
                  <svrl:successful-report xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                          location="{schxslt:location(.)}"
                                          role="error"
                                          id="SCH-00130-005">
                     <xsl:attribute name="test">count(./akn:blockContainer[@refersTo = 'inhaltsuebersicht']) gt 1</xsl:attribute>
                     <svrl:text>Es darf maximal eine Inhaltsübersicht geben.</svrl:text>
                  </svrl:successful-report>
               </xsl:if>
               <xsl:if test="count(./akn:blockContainer[@refersTo = 'anlagenuebersicht']) gt 1">
                  <svrl:successful-report xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                          location="{schxslt:location(.)}"
                                          role="error"
                                          id="SCH-00130-010">
                     <xsl:attribute name="test">count(./akn:blockContainer[@refersTo = 'anlagenuebersicht']) gt 1</xsl:attribute>
                     <svrl:text>Es darf maximal eine Anlagenübersicht geben.</svrl:text>
                  </svrl:successful-report>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e381')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="/akn:akomaNtoso/*/akn:body[$form = $form-mantelform and ($ist-entwurfsfassung or $ist-verkündungsfassung)]"
                 priority="127"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e396']">
            <schxslt:rule pattern="d15e396">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00150 for context "/akn:akomaNtoso/*/akn:body[$form = $form-mantelform and ($ist-entwurfsfassung or $ist-verkündungsfassung)]" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00150">
                  <xsl:attribute name="context">/akn:akomaNtoso/*/akn:body[$form = $form-mantelform and ($ist-entwurfsfassung or $ist-verkündungsfassung)]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e396">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00150">
                  <xsl:attribute name="context">/akn:akomaNtoso/*/akn:body[$form = $form-mantelform and ($ist-entwurfsfassung or $ist-verkündungsfassung)]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="./akn:book or ./akn:part or ./akn:chapter or ./akn:subchapter or ./akn:section or ./akn:subsection or ./akn:title or ./akn:subtitle">
                  <svrl:successful-report xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                          location="{schxslt:location(.)}"
                                          role="error"
                                          id="SCH-00150-005">
                     <xsl:attribute name="test">./akn:book or ./akn:part or ./akn:chapter or ./akn:subchapter or ./akn:section or ./akn:subsection or ./akn:title or ./akn:subtitle</xsl:attribute>
                     <svrl:text>Der Hauptteil einer Mantelform wird nicht in weitere Gliederungsabschnitte untergliedert.</svrl:text>
                  </svrl:successful-report>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e396')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="/akn:akomaNtoso/*/akn:body[ $form = ($form-mantelform, $form-stammform) and $typ = ($typ-gesetz, $typ-verordnung, $typ-satzung, $typ-vertragsgesetz, $typ-vertragsverordnung) and ($ist-entwurfsfassung or $ist-verkündungsfassung) ]"
                 priority="126"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e406']">
            <schxslt:rule pattern="d15e406">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00160 for context "/akn:akomaNtoso/*/akn:body[ $form = ($form-mantelform, $form-stammform) and $typ = ($typ-gesetz, $typ-verordnung, $typ-satzung, $typ-vertragsgesetz, $typ-vertragsverordnung) and ($ist-entwurfsfassung or $ist-verkündungsfassung) ]" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00160">
                  <xsl:attribute name="context">/akn:akomaNtoso/*/akn:body[ $form = ($form-mantelform, $form-stammform) and $typ = ($typ-gesetz, $typ-verordnung, $typ-satzung, $typ-vertragsgesetz, $typ-vertragsverordnung) and ($ist-entwurfsfassung or $ist-verkündungsfassung) ]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e406">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00160">
                  <xsl:attribute name="context">/akn:akomaNtoso/*/akn:body[ $form = ($form-mantelform, $form-stammform) and $typ = ($typ-gesetz, $typ-verordnung, $typ-satzung, $typ-vertragsgesetz, $typ-vertragsverordnung) and ($ist-entwurfsfassung or $ist-verkündungsfassung) ]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not((count(//akn:article[@refersTo = $refersto-literal-geltungszeitregel]) = 1 and empty(//akn:article[@refersTo = ($refersto-literal-geltungszeitregel-inkrafttreten, $refersto-literal-geltungszeitregel-ausserkrafttreten)])) or (count(//akn:article[@refersTo = $refersto-literal-geltungszeitregel-inkrafttreten]) = 1 and count(//akn:article[@refersTo = $refersto-literal-geltungszeitregel-ausserkrafttreten]) le 1 and empty(//akn:article[@refersTo = $refersto-literal-geltungszeitregel])))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00160-010">
                     <xsl:attribute name="test">(count(//akn:article[@refersTo = $refersto-literal-geltungszeitregel]) = 1 and empty(//akn:article[@refersTo = ($refersto-literal-geltungszeitregel-inkrafttreten, $refersto-literal-geltungszeitregel-ausserkrafttreten)])) or (count(//akn:article[@refersTo = $refersto-literal-geltungszeitregel-inkrafttreten]) = 1 and count(//akn:article[@refersTo = $refersto-literal-geltungszeitregel-ausserkrafttreten]) le 1 and empty(//akn:article[@refersTo = $refersto-literal-geltungszeitregel]))</xsl:attribute>
                     <svrl:text>Innerhalb eines Regelungstextes in der Entwurfsfassung oder Verkündungsfassung, der in Stamm- oder Mantelform vorliegt, muss es entweder genau eine Einzelvorschrift bzgl. der Geltungszeit (refersTo="geltungszeitregel") ODER eine Einzelvorschrift bzgl. des Inkrafttretens (refersTo="geltungszeitregel-inkrafttreten") und optional eine Geltungszeitregel bzgl. des Außerkrafttretens (refersTo="geltungszeitregel-ausserkrafttreten") geben.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e406')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="/akn:akomaNtoso/*/akn:body[$form = $form-eingebundene-stammform]"
                 priority="125"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e406']">
            <schxslt:rule pattern="d15e406">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00170 for context "/akn:akomaNtoso/*/akn:body[$form = $form-eingebundene-stammform]" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00170">
                  <xsl:attribute name="context">/akn:akomaNtoso/*/akn:body[$form = $form-eingebundene-stammform]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e406">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00170">
                  <xsl:attribute name="context">/akn:akomaNtoso/*/akn:body[$form = $form-eingebundene-stammform]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($form = $form-eingebundene-stammform) then (count(//akn:article[@refersTo = ($refersto-literal-geltungszeitregel, $refersto-literal-geltungszeitregel-inkrafttreten)]) = 0) else ())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00170-000">
                     <xsl:attribute name="test">if ($form = $form-eingebundene-stammform) then (count(//akn:article[@refersTo = ($refersto-literal-geltungszeitregel, $refersto-literal-geltungszeitregel-inkrafttreten)]) = 0) else ()</xsl:attribute>
                     <svrl:text>Ein Regelungstext als eingebundene Stammform darf keine Einzelvorschrift bezüglich der Geltungszeit bzw. bezüglich des Inkrafttretens besitzen.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e406')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:article[@refersTo = $refersto-literal-geltungszeitregel]"
                 priority="124"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e421']">
            <schxslt:rule pattern="d15e421">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00180 for context "akn:article[@refersTo = $refersto-literal-geltungszeitregel]" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00180">
                  <xsl:attribute name="context">akn:article[@refersTo = $refersto-literal-geltungszeitregel]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e421">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00180">
                  <xsl:attribute name="context">akn:article[@refersTo = $refersto-literal-geltungszeitregel]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="$ist-entwurfsfassung">
                  <svrl:successful-report xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                          location="{schxslt:location(.)}"
                                          role="warn"
                                          id="SCH-00180-000">
                     <xsl:attribute name="test">$ist-entwurfsfassung</xsl:attribute>
                     <svrl:text>Gemäß HdR 4 sollen Inkrafttreten und Außerkrafttreten in getrennten Einzelvorschriften gefasst werden, die entsprechend mit den refersTo-Literalen geltungszeitregel-inkrafttreten und geltungszeitregel-ausserkrafttreten auszuzeichnen sind. Das refersTo-Literal "geltungszeitregel" soll in Entwurfsfassungen nicht mehr verwendet werden.</svrl:text>
                  </svrl:successful-report>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e421')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="//akn:article/akn:paragraph//akn:list[$ist-entwurfsfassung]"
                 priority="123"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e435']">
            <schxslt:rule pattern="d15e435">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00200 for context "//akn:article/akn:paragraph//akn:list[$ist-entwurfsfassung]" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00200">
                  <xsl:attribute name="context">//akn:article/akn:paragraph//akn:list[$ist-entwurfsfassung]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e435">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00200">
                  <xsl:attribute name="context">//akn:article/akn:paragraph//akn:list[$ist-entwurfsfassung]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="count(ancestor::akn:list) &gt; 4">
                  <svrl:successful-report xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                          location="{schxslt:location(.)}"
                                          role="warn"
                                          id="SCH-00200-005">
                     <xsl:attribute name="test">count(ancestor::akn:list) &gt; 4</xsl:attribute>
                     <svrl:text>Es ist maximal eine Vierfachuntergliederung von Sätzen erlaubt.</svrl:text>
                  </svrl:successful-report>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e435')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="//akn:article[$ist-entwurfsfassung]"
                 priority="122"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e444']">
            <schxslt:rule pattern="d15e444">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00210 for context "//akn:article[$ist-entwurfsfassung]" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00210">
                  <xsl:attribute name="context">//akn:article[$ist-entwurfsfassung]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e444">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00210">
                  <xsl:attribute name="context">//akn:article[$ist-entwurfsfassung]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="count(./akn:paragraph) &gt; 6">
                  <svrl:successful-report xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                          location="{schxslt:location(.)}"
                                          role="warn"
                                          id="SCH-00210-005">
                     <xsl:attribute name="test">count(./akn:paragraph) &gt; 6</xsl:attribute>
                     <svrl:text>Es ist maximal eine Sechsfachuntergliederung in Absätzen erlaubt.</svrl:text>
                  </svrl:successful-report>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e444')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:book" priority="121" mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e457']">
            <schxslt:rule pattern="d15e457">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00230 for context "akn:book" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00230">
                  <xsl:attribute name="context">akn:book</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e457">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00230">
                  <xsl:attribute name="context">akn:book</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="akn:book[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]">
                  <svrl:successful-report xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                          location="{schxslt:location(akn:book[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')])}"
                                          role="error"
                                          id="SCH-00230-005">
                     <xsl:attribute name="test">akn:book[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]</xsl:attribute>
                     <svrl:text> Innerhalb eines Gliederungsabschnitts "Buch" darf diese Gliederungsebene nicht verwendet werden.</svrl:text>
                  </svrl:successful-report>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e457')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:part" priority="120" mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e457']">
            <schxslt:rule pattern="d15e457">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00240 for context "akn:part" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00240">
                  <xsl:attribute name="context">akn:part</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e457">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00240">
                  <xsl:attribute name="context">akn:part</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="(akn:book | akn:part)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]">
                  <svrl:successful-report xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                          location="{schxslt:location((akn:book | akn:part)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')])}"
                                          role="error"
                                          id="SCH-00240-005">
                     <xsl:attribute name="test">(akn:book | akn:part)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]</xsl:attribute>
                     <svrl:text> Innerhalb eines Gliederungsabschnitts "Teil" darf diese Gliederungsebene nicht verwendet werden.</svrl:text>
                  </svrl:successful-report>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e457')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:chapter" priority="119" mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e457']">
            <schxslt:rule pattern="d15e457">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00250 for context "akn:chapter" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00250">
                  <xsl:attribute name="context">akn:chapter</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e457">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00250">
                  <xsl:attribute name="context">akn:chapter</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="(akn:book | akn:part | akn:chapter)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]">
                  <svrl:successful-report xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                          location="{schxslt:location((akn:book | akn:part | akn:chapter)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')])}"
                                          role="error"
                                          id="SCH-00250-005">
                     <xsl:attribute name="test">(akn:book | akn:part | akn:chapter)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]</xsl:attribute>
                     <svrl:text> Innerhalb eines Gliederungsabschnitts "Kapitel" darf diese Gliederungsebene nicht verwendet werden.</svrl:text>
                  </svrl:successful-report>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e457')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:subchapter" priority="118" mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e457']">
            <schxslt:rule pattern="d15e457">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00260 for context "akn:subchapter" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00260">
                  <xsl:attribute name="context">akn:subchapter</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e457">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00260">
                  <xsl:attribute name="context">akn:subchapter</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="(akn:book | akn:part | akn:chapter | akn:subchapter)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]">
                  <svrl:successful-report xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                          location="{schxslt:location((akn:book | akn:part | akn:chapter | akn:subchapter)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')])}"
                                          role="error"
                                          id="SCH-00260-005">
                     <xsl:attribute name="test">(akn:book | akn:part | akn:chapter | akn:subchapter)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]</xsl:attribute>
                     <svrl:text> Innerhalb eines Gliederungsabschnitts "Unterkapitel" darf diese Gliederungsebene nicht verwendet werden.</svrl:text>
                  </svrl:successful-report>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e457')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:section" priority="117" mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e457']">
            <schxslt:rule pattern="d15e457">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00270 for context "akn:section" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00270">
                  <xsl:attribute name="context">akn:section</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e457">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00270">
                  <xsl:attribute name="context">akn:section</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="(akn:book | akn:part | akn:chapter | akn:subchapter | akn:section)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]">
                  <svrl:successful-report xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                          location="{schxslt:location((akn:book | akn:part | akn:chapter | akn:subchapter | akn:section)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')])}"
                                          role="error"
                                          id="SCH-00270-005">
                     <xsl:attribute name="test">(akn:book | akn:part | akn:chapter | akn:subchapter | akn:section)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]</xsl:attribute>
                     <svrl:text> Innerhalb eines Gliederungsabschnitts "Abschnitt" darf diese Gliederungsebene nicht verwendet werden.</svrl:text>
                  </svrl:successful-report>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e457')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:subsection" priority="116" mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e457']">
            <schxslt:rule pattern="d15e457">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00280 for context "akn:subsection" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00280">
                  <xsl:attribute name="context">akn:subsection</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e457">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00280">
                  <xsl:attribute name="context">akn:subsection</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="(akn:book | akn:part | akn:chapter | akn:subchapter | akn:section | akn:subsection)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]">
                  <svrl:successful-report xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                          location="{schxslt:location((akn:book | akn:part | akn:chapter | akn:subchapter | akn:section | akn:subsection)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')])}"
                                          role="error"
                                          id="SCH-00280-005">
                     <xsl:attribute name="test">(akn:book | akn:part | akn:chapter | akn:subchapter | akn:section | akn:subsection)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]</xsl:attribute>
                     <svrl:text> Innerhalb eines Gliederungsabschnitts "Unterabschnitt" darf diese Gliederungsebene nicht verwendet werden.</svrl:text>
                  </svrl:successful-report>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e457')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:title" priority="115" mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e457']">
            <schxslt:rule pattern="d15e457">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00290 for context "akn:title" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00290">
                  <xsl:attribute name="context">akn:title</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e457">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00290">
                  <xsl:attribute name="context">akn:title</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="(akn:book | akn:part | akn:chapter | akn:subchapter | akn:section | akn:subsection | akn:title)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]">
                  <svrl:successful-report xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                          location="{schxslt:location((akn:book | akn:part | akn:chapter | akn:subchapter | akn:section | akn:subsection | akn:title)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')])}"
                                          role="error"
                                          id="SCH-00290-005">
                     <xsl:attribute name="test">(akn:book | akn:part | akn:chapter | akn:subchapter | akn:section | akn:subsection | akn:title)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]</xsl:attribute>
                     <svrl:text>Innerhalb eines Gliederungsabschnitts "Titel" darf diese Gliederungsebene nicht verwendet werden.</svrl:text>
                  </svrl:successful-report>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e457')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:subtitle" priority="114" mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e457']">
            <schxslt:rule pattern="d15e457">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00300 for context "akn:subtitle" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00300">
                  <xsl:attribute name="context">akn:subtitle</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e457">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00300">
                  <xsl:attribute name="context">akn:subtitle</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="(akn:book | akn:part | akn:chapter | akn:subchapter | akn:section | akn:subsection | akn:title | akn:subtitle)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]">
                  <svrl:successful-report xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                          location="{schxslt:location((akn:book | akn:part | akn:chapter | akn:subchapter | akn:section | akn:subsection | akn:title | akn:subtitle)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')])}"
                                          role="error"
                                          id="SCH-00300-005">
                     <xsl:attribute name="test">(akn:book | akn:part | akn:chapter | akn:subchapter | akn:section | akn:subsection | akn:title | akn:subtitle)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]</xsl:attribute>
                     <svrl:text>Innerhalb eines Gliederungsabschnitts "Untertitel" darf diese Gliederungsebene nicht verwendet werden.</svrl:text>
                  </svrl:successful-report>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e457')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="regtxt:vomHdrAbweichendeGliederung"
                 priority="113"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e509']">
            <schxslt:rule pattern="d15e509">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00305 for context "regtxt:vomHdrAbweichendeGliederung" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00305">
                  <xsl:attribute name="context">regtxt:vomHdrAbweichendeGliederung</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e509">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00305">
                  <xsl:attribute name="context">regtxt:vomHdrAbweichendeGliederung</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="count(//(akn:book | akn:part | akn:chapter | akn:subchapter | akn:section | akn:subsection | akn:title | akn:subtitle)[@refersTo = 'vom-hdr-abweichende-gliederungsebene']) = 0">
                  <svrl:successful-report xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                          location="{schxslt:location(.)}"
                                          role="warn"
                                          id="SCH-00305-000">
                     <xsl:attribute name="test">count(//(akn:book | akn:part | akn:chapter | akn:subchapter | akn:section | akn:subsection | akn:title | akn:subtitle)[@refersTo = 'vom-hdr-abweichende-gliederungsebene']) = 0</xsl:attribute>
                     <svrl:text>Der Marker zur Auszeichnung einer von den Regelungen des HdR abweichenden Gliederung ist vorhanden, aber es wird keine solche Abweichungen (mittels <xsl:element namespace="" name="code">@refersTo='vom-hdr-abweichende-gliederungsebene')</xsl:element> ausgewiesen!</svrl:text>
                  </svrl:successful-report>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e509')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="(akn:book | akn:part | akn:chapter | akn:subchapter | akn:section | akn:subsection | akn:title | akn:subtitle)[@refersTo = 'vom-hdr-abweichende-gliederungsebene']"
                 priority="112"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e509']">
            <schxslt:rule pattern="d15e509">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00306 for context "(akn:book | akn:part | akn:chapter | akn:subchapter | akn:section | akn:subsection | akn:title | akn:subtitle)[@refersTo = 'vom-hdr-abweichende-gliederungsebene']" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00306">
                  <xsl:attribute name="context">(akn:book | akn:part | akn:chapter | akn:subchapter | akn:section | akn:subsection | akn:title | akn:subtitle)[@refersTo = 'vom-hdr-abweichende-gliederungsebene']</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e509">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00306">
                  <xsl:attribute name="context">(akn:book | akn:part | akn:chapter | akn:subchapter | akn:section | akn:subsection | akn:title | akn:subtitle)[@refersTo = 'vom-hdr-abweichende-gliederungsebene']</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(exists(//regtxt:vomHdrAbweichendeGliederung))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00306-000">
                     <xsl:attribute name="test">exists(//regtxt:vomHdrAbweichendeGliederung)</xsl:attribute>
                     <svrl:text>Eine Gliederungsebene darf nur als von den Regelungen des HdR abweichend gekennzeichnet werden, wenn im Metadatenblock der instanz-weite Marker <xsl:element namespace="" name="code">&lt;meta:vomHdrAbweichendeGliederung&gt;</xsl:element> vorhanden ist.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e509')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri]/akn:conclusions"
                 priority="111"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e533']">
            <schxslt:rule pattern="d15e533">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00310 for context "/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri]/akn:conclusions" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00310">
                  <xsl:attribute name="context">/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri]/akn:conclusions</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e533">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00310">
                  <xsl:attribute name="context">/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri]/akn:conclusions</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="./akn:blockContainer and $bearbeitende-institution-frbrauthor = 'recht.bund.de/institution/bundesregierung'">
                  <svrl:successful-report xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                          location="{schxslt:location(.)}"
                                          role="error"
                                          id="SCH-00310-005">
                     <xsl:attribute name="test">./akn:blockContainer and $bearbeitende-institution-frbrauthor = 'recht.bund.de/institution/bundesregierung'</xsl:attribute>
                     <svrl:text>Der Signaturblock steht nur dem Bundestag in der Entwurfsfassung optional zur Verfügung.</svrl:text>
                  </svrl:successful-report>
               </xsl:if>
               <xsl:if test="./akn:blockContainer and $bearbeitende-institution-frbrauthor = 'recht.bund.de/institution/bundesrat'">
                  <svrl:successful-report xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                          location="{schxslt:location(.)}"
                                          role="error"
                                          id="SCH-00310-010">
                     <xsl:attribute name="test">./akn:blockContainer and $bearbeitende-institution-frbrauthor = 'recht.bund.de/institution/bundesrat'</xsl:attribute>
                     <svrl:text>Der Signaturblock steht nur dem Bundestag in der Entwurfsfassung optional zur Verfügung.</svrl:text>
                  </svrl:successful-report>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e533')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:act/akn:conclusions/akn:blockContainer"
                 priority="110"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e545']">
            <schxslt:rule pattern="d15e545">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00320 for context "akn:act/akn:conclusions/akn:blockContainer" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00320">
                  <xsl:attribute name="context">akn:act/akn:conclusions/akn:blockContainer</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e545">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00320">
                  <xsl:attribute name="context">akn:act/akn:conclusions/akn:blockContainer</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(count(akn:p) ge 2)">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="warn"
                                      id="SCH-00320-000">
                     <xsl:attribute name="test">count(akn:p) ge 2</xsl:attribute>
                     <svrl:text>Im Schlussteil sollen mindestens zwei akn:p enthalten sein (akn:p mit Ort und Datum; ein oder mehrere akn:p mit Unterschriften).</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e545')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="(akn:act | akn:bill)/akn:conclusions/akn:blockContainer/akn:p[1]"
                 priority="109"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e545']">
            <schxslt:rule pattern="d15e545">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00321 for context "(akn:act | akn:bill)/akn:conclusions/akn:blockContainer/akn:p[1]" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00321">
                  <xsl:attribute name="context">(akn:act | akn:bill)/akn:conclusions/akn:blockContainer/akn:p[1]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e545">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00321">
                  <xsl:attribute name="context">(akn:act | akn:bill)/akn:conclusions/akn:blockContainer/akn:p[1]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(exists(akn:location))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="warn"
                                      id="SCH-00321-010">
                     <xsl:attribute name="test">exists(akn:location)</xsl:attribute>
                     <svrl:text>Im Schlussteil soll im ersten akn:p ein Ort (akn:location) angegeben werden.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(exists(akn:date))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="warn"
                                      id="SCH-00321-020">
                     <xsl:attribute name="test">exists(akn:date)</xsl:attribute>
                     <svrl:text>Im Schlussteil soll im ersten akn:p ein Datum (akn:date) angegeben werden.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e545')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="(akn:act | akn:bill)/akn:conclusions/akn:blockContainer/akn:p[position() ge 2]"
                 priority="108"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e545']">
            <schxslt:rule pattern="d15e545">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00322 for context "(akn:act | akn:bill)/akn:conclusions/akn:blockContainer/akn:p[position() ge 2]" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00322">
                  <xsl:attribute name="context">(akn:act | akn:bill)/akn:conclusions/akn:blockContainer/akn:p[position() ge 2]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e545">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00322">
                  <xsl:attribute name="context">(akn:act | akn:bill)/akn:conclusions/akn:blockContainer/akn:p[position() ge 2]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(exists(akn:signature))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="warn"
                                      id="SCH-00322-000">
                     <xsl:attribute name="test">exists(akn:signature)</xsl:attribute>
                     <svrl:text>Im Schlussteil soll ab dem zweiten akn:p eine Signatur(akn:signature) angegeben werden.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e545')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="//akn:doc[@name = $art-vorblatt-regelungstext-uri]/akn:mainBody"
                 priority="107"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e573']">
            <schxslt:rule pattern="d15e573">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00330 for context "//akn:doc[@name = $art-vorblatt-regelungstext-uri]/akn:mainBody" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00330">
                  <xsl:attribute name="context">//akn:doc[@name = $art-vorblatt-regelungstext-uri]/akn:mainBody</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e573">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00330">
                  <xsl:attribute name="context">//akn:doc[@name = $art-vorblatt-regelungstext-uri]/akn:mainBody</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-problem-und-ziel']))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00330-005">
                     <xsl:attribute name="test">exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-problem-und-ziel'])</xsl:attribute>
                     <svrl:text>Es muss der Vorblattabschnitt 'Problem und Ziel' verwendet werden.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-loesung']))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00330-010">
                     <xsl:attribute name="test">exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-loesung'])</xsl:attribute>
                     <svrl:text>Es muss der Vorblattabschnitt 'Lösung' verwendet werden.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-alternativen']))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00330-015">
                     <xsl:attribute name="test">exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-alternativen'])</xsl:attribute>
                     <svrl:text>Es muss der Vorblattabschnitt 'Alternativen' verwendet werden.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-haushaltsausgaben-ohne-erfuellungsaufwand']))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00330-020">
                     <xsl:attribute name="test">exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-haushaltsausgaben-ohne-erfuellungsaufwand'])</xsl:attribute>
                     <svrl:text>Es muss der Vorblattabschnitt 'Haushaltsausgaben ohne Erfüllungsaufwand' verwendet werden.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-erfuellungsaufwand']))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00330-025">
                     <xsl:attribute name="test">exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-erfuellungsaufwand'])</xsl:attribute>
                     <svrl:text>Es muss der Vorblattabschnitt 'Erfüllungsaufwand' verwendet wird.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-erfuellungsaufwand']//akn:tblock[@refersTo = 'erfuellungsaufwand-fuer-die-wirtschaft']))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="warn"
                                      id="SCH-00330-030">
                     <xsl:attribute name="test">exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-erfuellungsaufwand']//akn:tblock[@refersTo = 'erfuellungsaufwand-fuer-die-wirtschaft'])</xsl:attribute>
                     <svrl:text> Es wird empfohlen, dass innerhalb des Erfüllungsaufwands ein Unterabschnitt 'Erfüllungsaufwand Für die Wirtschaft' verwendet wird.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-erfuellungsaufwand']//akn:tblock[@refersTo = 'erfuellungsaufwand-fuer-die-wirtschaft']/akn:tblock[@refersTo = 'davon-buerokratiekosten-aus-informationspflichten']))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="warn"
                                      id="SCH-00330-035">
                     <xsl:attribute name="test">exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-erfuellungsaufwand']//akn:tblock[@refersTo = 'erfuellungsaufwand-fuer-die-wirtschaft']/akn:tblock[@refersTo = 'davon-buerokratiekosten-aus-informationspflichten'])</xsl:attribute>
                     <svrl:text>Es wird empfohlen, dass innerhalb des Erfüllungsaufwands für die Wirtschaft ein Unterabschnitt 'Davon Bürokratiekosten aus Informationspflichten' verwendet wird.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-erfuellungsaufwand']//akn:tblock[@refersTo = 'erfuellungsaufwand-fuer-buergerinnen-und-buerger']))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="warn"
                                      id="SCH-00330-040">
                     <xsl:attribute name="test">exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-erfuellungsaufwand']//akn:tblock[@refersTo = 'erfuellungsaufwand-fuer-buergerinnen-und-buerger'])</xsl:attribute>
                     <svrl:text> Es wird empfohlen, dass innerhalb des Erfüllungsaufwands ein Unterabschnitt 'Erfüllungsaufwand Für Bürgerinnen und Bürger' verwendet wird.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-erfuellungsaufwand']//akn:tblock[@refersTo = 'erfuellungsaufwand-der-verwaltung']))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="warn"
                                      id="SCH-00330-045">
                     <xsl:attribute name="test">exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-erfuellungsaufwand']//akn:tblock[@refersTo = 'erfuellungsaufwand-der-verwaltung'])</xsl:attribute>
                     <svrl:text> Es wird empfohlen, dass innerhalb des Erfüllungsaufwands ein Unterabschnitt 'Erfüllungsaufwand der Verwaltung' verwendet wird.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-weitere-kosten']))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00330-050">
                     <xsl:attribute name="test">exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-weitere-kosten'])</xsl:attribute>
                     <svrl:text>Es muss der Vorblattabschnitt 'Weitere Kosten' verwendet werden.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e573')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="//akn:doc[@name = $art-vorblatt-uri]/akn:mainBody/akn:hcontainer[@refersTo = $refersto-literal-vorblattabschnitt-erfüllungsaufwand]//akn:tblock"
                 priority="106"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:variable name="zulässige-literale-zum-erfüllungsaufwand"
                    select="( 'erfuellungsaufwand-fuer-buergerinnen-und-buerger', 'erfuellungsaufwand-fuer-die-wirtschaft', 'davon-buerokratiekosten-aus-informationspflichten', 'erfuellungsaufwand-der-verwaltung' )"/>
      <xsl:variable name="mehr-als-ein-literal"
                    select="count($zulässige-literale-zum-erfüllungsaufwand) gt 1"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e573']">
            <schxslt:rule pattern="d15e573">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00335 for context "//akn:doc[@name = $art-vorblatt-uri]/akn:mainBody/akn:hcontainer[@refersTo = $refersto-literal-vorblattabschnitt-erfüllungsaufwand]//akn:tblock" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00335">
                  <xsl:attribute name="context">//akn:doc[@name = $art-vorblatt-uri]/akn:mainBody/akn:hcontainer[@refersTo = $refersto-literal-vorblattabschnitt-erfüllungsaufwand]//akn:tblock</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e573">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00335">
                  <xsl:attribute name="context">//akn:doc[@name = $art-vorblatt-uri]/akn:mainBody/akn:hcontainer[@refersTo = $refersto-literal-vorblattabschnitt-erfüllungsaufwand]//akn:tblock</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if (exists(@refersTo)) then (@refersTo = $zulässige-literale-zum-erfüllungsaufwand) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00335-000">
                     <xsl:attribute name="test">if (exists(@refersTo)) then (@refersTo = $zulässige-literale-zum-erfüllungsaufwand) else true()</xsl:attribute>
                     <svrl:text> Im Kontext eines Vorblattabschnitts zum Erfüllungsaufwand kann das Literal '<xsl:value-of select="@refersTo"/>' nicht verwendet werden; zulässig <xsl:value-of select="if (xs:boolean($mehr-als-ein-literal)) then 'sind' else 'ist'"/> hier ausschließlich <xsl:value-of select="if (xs:boolean($mehr-als-ein-literal)) then concat(string-join( for $literal in $zulässige-literale-zum-erfüllungsaufwand[position() lt last()] return concat('''', $literal, ''''), ', '), ' oder ', concat('''', $zulässige-literale-zum-erfüllungsaufwand[last()], '''') ) else concat('''', string-join($zulässige-literale-zum-erfüllungsaufwand), '''')"/>.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e573')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="//akn:doc[@name = $art-vorblatt-beschlussempfehlung-uri]/akn:mainBody"
                 priority="105"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e629']">
            <schxslt:rule pattern="d15e629">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00336 for context "//akn:doc[@name = $art-vorblatt-beschlussempfehlung-uri]/akn:mainBody" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00336">
                  <xsl:attribute name="context">//akn:doc[@name = $art-vorblatt-beschlussempfehlung-uri]/akn:mainBody</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e629">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00336">
                  <xsl:attribute name="context">//akn:doc[@name = $art-vorblatt-beschlussempfehlung-uri]/akn:mainBody</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-problem']))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00336-005">
                     <xsl:attribute name="test">exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-problem'])</xsl:attribute>
                     <svrl:text>Es muss der Vorblattabschnitt 'Problem' verwendet werden.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-loesung']))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00336-006">
                     <xsl:attribute name="test">exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-loesung'])</xsl:attribute>
                     <svrl:text>Es muss der Vorblattabschnitt 'Lösung' verwendet werden.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-alternativen']))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00336-007">
                     <xsl:attribute name="test">exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-alternativen'])</xsl:attribute>
                     <svrl:text>Es muss der Vorblattabschnitt 'Alternativen' verwendet werden.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-kosten']))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00336-008">
                     <xsl:attribute name="test">exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-kosten'])</xsl:attribute>
                     <svrl:text>Es muss der Vorblattabschnitt 'Kosten' verwendet werden.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e629')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="/akn:akomaNtoso/akn:doc[@name = $art-begründung-uri and $typ = ($typ-gesetz, $typ-verordnung, $typ-verwaltungsvorschrift)]"
                 priority="104"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e650']">
            <schxslt:rule pattern="d15e650">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00340 for context "/akn:akomaNtoso/akn:doc[@name = $art-begründung-uri and $typ = ($typ-gesetz, $typ-verordnung, $typ-verwaltungsvorschrift)]" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00340">
                  <xsl:attribute name="context">/akn:akomaNtoso/akn:doc[@name = $art-begründung-uri and $typ = ($typ-gesetz, $typ-verordnung, $typ-verwaltungsvorschrift)]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e650">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00340">
                  <xsl:attribute name="context">/akn:akomaNtoso/akn:doc[@name = $art-begründung-uri and $typ = ($typ-gesetz, $typ-verordnung, $typ-verwaltungsvorschrift)]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(./akn:mainBody/akn:hcontainer[@refersTo = 'begruendung-allgemeiner-teil'])">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00340-005">
                     <xsl:attribute name="test">./akn:mainBody/akn:hcontainer[@refersTo = 'begruendung-allgemeiner-teil']</xsl:attribute>
                     <svrl:text> Es muss ein allgemeiner Teil der Begründung vorliegen.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(./akn:mainBody/akn:hcontainer[@refersTo = 'begruendung-besonderer-teil'])">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00340-010">
                     <xsl:attribute name="test">./akn:mainBody/akn:hcontainer[@refersTo = 'begruendung-besonderer-teil']</xsl:attribute>
                     <svrl:text> Es muss ein besonderer Teil der Begründung vorliegen.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="./akn:conclusions">
                  <svrl:successful-report xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                          location="{schxslt:location(.)}"
                                          role="error"
                                          id="SCH-00340-015">
                     <xsl:attribute name="test">./akn:conclusions</xsl:attribute>
                     <svrl:text> Eine Schlussbemerkung wird nur innerhalb einer Begründung von Vertragsrechtsakten benutzt.</svrl:text>
                  </svrl:successful-report>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e650')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="//akn:doc[@name = $art-begründung-uri]/akn:mainBody//akn:hcontainer[@refersTo = 'begruendungsabschnitt-regelungsfolgen']//akn:tblock"
                 priority="103"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:variable name="zulässige-literale-zu-regelungsfolgen"
                    select="( 'begruendung-erfuellungsaufwand-fuer-buergerinnen-und-buerger', 'begruendung-erfuellungsaufwand-fuer-die-wirtschaft', 'begruendung-erfuellungsaufwand-der-verwaltung', 'regelungsfolgen-abschnitt-rechts-und-verwaltungsvereinfachung', 'regelungsfolgen-abschnitt-nachhaltigkeitsaspekte', 'regelungsfolgen-abschnitt-erfuellungsaufwand', 'regelungsfolgen-abschnitt-weitere-kosten', 'regelungsfolgen-abschnitt-gleichstellungspolitische-relevanzpruefung', 'regelungsfolgen-abschnitt-haushaltsausgaben-ohne-erfuellungsaufwand', 'regelungsfolgen-abschnitt-weitere-regelungsfolgen' )"/>
      <xsl:variable name="mehr-als-ein-literal"
                    select="count($zulässige-literale-zu-regelungsfolgen) gt 1"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e650']">
            <schxslt:rule pattern="d15e650">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00345 for context "//akn:doc[@name = $art-begründung-uri]/akn:mainBody//akn:hcontainer[@refersTo = 'begruendungsabschnitt-regelungsfolgen']//akn:tblock" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00345">
                  <xsl:attribute name="context">//akn:doc[@name = $art-begründung-uri]/akn:mainBody//akn:hcontainer[@refersTo = 'begruendungsabschnitt-regelungsfolgen']//akn:tblock</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e650">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00345">
                  <xsl:attribute name="context">//akn:doc[@name = $art-begründung-uri]/akn:mainBody//akn:hcontainer[@refersTo = 'begruendungsabschnitt-regelungsfolgen']//akn:tblock</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if (exists(@refersTo)) then (@refersTo = $zulässige-literale-zu-regelungsfolgen) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00345-000">
                     <xsl:attribute name="test">if (exists(@refersTo)) then (@refersTo = $zulässige-literale-zu-regelungsfolgen) else true()</xsl:attribute>
                     <svrl:text> Im Kontext eines Begründungsabschnitt zu Regelungsfolgen kann das Literal '<xsl:value-of select="@refersTo"/>' nicht verwendet werden; zulässig <xsl:value-of select="if (xs:boolean($mehr-als-ein-literal)) then 'sind' else 'ist'"/> hier ausschließlich <xsl:value-of select="if (xs:boolean($mehr-als-ein-literal)) then concat(string-join( for $literal in $zulässige-literale-zu-regelungsfolgen[position() lt last()] return concat('''', $literal, ''''), ', '), ' oder ', concat('''', $zulässige-literale-zu-regelungsfolgen[last()], '''') ) else concat('''', string-join($zulässige-literale-zu-regelungsfolgen), '''')"/>.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e650')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="//akn:doc[@name = $art-begründung-uri]/akn:mainBody/akn:hcontainer[@refersTo = 'begruendung-allgemeiner-teil']"
                 priority="102"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e685']">
            <schxslt:rule pattern="d15e685">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00350 for context "//akn:doc[@name = $art-begründung-uri]/akn:mainBody/akn:hcontainer[@refersTo = 'begruendung-allgemeiner-teil']" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00350">
                  <xsl:attribute name="context">//akn:doc[@name = $art-begründung-uri]/akn:mainBody/akn:hcontainer[@refersTo = 'begruendung-allgemeiner-teil']</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e685">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00350">
                  <xsl:attribute name="context">//akn:doc[@name = $art-begründung-uri]/akn:mainBody/akn:hcontainer[@refersTo = 'begruendung-allgemeiner-teil']</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(exists(./akn:hcontainer[@refersTo = 'begruendungsabschnitt-zielsetzung-und-notwendigkeit']))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="warn"
                                      id="SCH-00350-005">
                     <xsl:attribute name="test">exists(./akn:hcontainer[@refersTo = 'begruendungsabschnitt-zielsetzung-und-notwendigkeit'])</xsl:attribute>
                     <svrl:text> Es wird empfohlen, dass ein Begründungsabschnitt 'Zielsetzung und Notwendigkeit' verwendet wird.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(exists(./akn:hcontainer[@refersTo = 'begruendungsabschnitt-wesentlicher-inhalt']))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="warn"
                                      id="SCH-00350-010">
                     <xsl:attribute name="test">exists(./akn:hcontainer[@refersTo = 'begruendungsabschnitt-wesentlicher-inhalt'])</xsl:attribute>
                     <svrl:text> Es wird empfohlen, dass ein Begründungsabschnitt 'Wesentlicher Inhalt' verwendet wird.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(exists(./akn:hcontainer[@refersTo = 'begruendungsabschnitt-alternativen']))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="warn"
                                      id="SCH-00350-015">
                     <xsl:attribute name="test">exists(./akn:hcontainer[@refersTo = 'begruendungsabschnitt-alternativen'])</xsl:attribute>
                     <svrl:text> Es wird empfohlen, dass ein Begründungsabschnitt 'Alternativen' verwendet wird.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(exists(./akn:hcontainer[@refersTo = 'begruendungsabschnitt-regelungskompetenz']))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="warn"
                                      id="SCH-00350-020">
                     <xsl:attribute name="test">exists(./akn:hcontainer[@refersTo = 'begruendungsabschnitt-regelungskompetenz'])</xsl:attribute>
                     <svrl:text> Es wird empfohlen, dass ein Begründungsabschnitt 'Regelungskompetenz' verwendet wird.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(exists(./akn:hcontainer[@refersTo = 'begruendungsabschnitt-regelungsfolgen']))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="warn"
                                      id="SCH-00350-025">
                     <xsl:attribute name="test">exists(./akn:hcontainer[@refersTo = 'begruendungsabschnitt-regelungsfolgen'])</xsl:attribute>
                     <svrl:text> Es wird empfohlen, dass ein Begründungsabschnitt 'Regelungsfolgen' oder 'Gesetzesfolgen' verwendet wird.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e685')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="//akn:doc[@name = $art-begründung-uri]/akn:mainBody/akn:hcontainer[@refersTo = 'begruendung-allgemeiner-teil']/akn:hcontainer[@refersTo = 'begruendungsabschnitt-regelungsfolgen']/akn:content"
                 priority="101"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e709']">
            <schxslt:rule pattern="d15e709">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00360 for context "//akn:doc[@name = $art-begründung-uri]/akn:mainBody/akn:hcontainer[@refersTo = 'begruendung-allgemeiner-teil']/akn:hcontainer[@refersTo = 'begruendungsabschnitt-regelungsfolgen']/akn:content" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00360">
                  <xsl:attribute name="context">//akn:doc[@name = $art-begründung-uri]/akn:mainBody/akn:hcontainer[@refersTo = 'begruendung-allgemeiner-teil']/akn:hcontainer[@refersTo = 'begruendungsabschnitt-regelungsfolgen']/akn:content</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e709">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00360">
                  <xsl:attribute name="context">//akn:doc[@name = $art-begründung-uri]/akn:mainBody/akn:hcontainer[@refersTo = 'begruendung-allgemeiner-teil']/akn:hcontainer[@refersTo = 'begruendungsabschnitt-regelungsfolgen']/akn:content</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(exists(./akn:tblock[@refersTo = 'regelungsfolgen-abschnitt-rechts-und-verwaltungsvereinfachung']))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="warn"
                                      id="SCH-00360-005">
                     <xsl:attribute name="test">exists(./akn:tblock[@refersTo = 'regelungsfolgen-abschnitt-rechts-und-verwaltungsvereinfachung'])</xsl:attribute>
                     <svrl:text> Es wird empfohlen, dass innerhalb der Regelungsfolgen ein Abschnitt 'Rechts und Verwaltungsvereinfachung' verwendet wird.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(exists(./akn:tblock[@refersTo = 'regelungsfolgen-abschnitt-nachhaltigkeitsaspekte']))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="warn"
                                      id="SCH-00360-010">
                     <xsl:attribute name="test">exists(./akn:tblock[@refersTo = 'regelungsfolgen-abschnitt-nachhaltigkeitsaspekte'])</xsl:attribute>
                     <svrl:text> Es wird empfohlen, dass innerhalb der Regelungsfolgen ein Abschnitt 'Nachhaltigkeitsaspekte' verwendet wird.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(exists(./akn:tblock[@refersTo = 'regelungsfolgen-abschnitt-erfuellungsaufwand']))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="warn"
                                      id="SCH-00360-015">
                     <xsl:attribute name="test">exists(./akn:tblock[@refersTo = 'regelungsfolgen-abschnitt-erfuellungsaufwand'])</xsl:attribute>
                     <svrl:text> Es wird empfohlen, dass innerhalb der Regelungsfolgen ein Abschnitt 'Erfuellungsaufwand' verwendet wird.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(exists(./akn:tblock[@refersTo = 'regelungsfolgen-abschnitt-weitere-kosten']))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="warn"
                                      id="SCH-00360-020">
                     <xsl:attribute name="test">exists(./akn:tblock[@refersTo = 'regelungsfolgen-abschnitt-weitere-kosten'])</xsl:attribute>
                     <svrl:text> Es wird empfohlen, dass innerhalb der Regelungsfolgen ein Abschnitt 'Weitere Kosten' verwendet wird.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(exists(./akn:tblock[@refersTo = 'regelungsfolgen-abschnitt-gleichstellungspolitische-relevanzpruefung']))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="warn"
                                      id="SCH-00360-025">
                     <xsl:attribute name="test">exists(./akn:tblock[@refersTo = 'regelungsfolgen-abschnitt-gleichstellungspolitische-relevanzpruefung'])</xsl:attribute>
                     <svrl:text> Es wird empfohlen, dass innerhalb der Regelungsfolgen ein Abschnitt 'Gleichstellungspolitische Relevanzprüfung' verwendet wird.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(exists(./akn:tblock[@refersTo = 'regelungsfolgen-abschnitt-haushaltsausgaben-ohne-erfuellungsaufwand']))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="warn"
                                      id="SCH-00360-030">
                     <xsl:attribute name="test">exists(./akn:tblock[@refersTo = 'regelungsfolgen-abschnitt-haushaltsausgaben-ohne-erfuellungsaufwand'])</xsl:attribute>
                     <svrl:text> Es wird empfohlen, dass innerhalb der Regelungsfolgen ein Abschnitt 'Haushaltsausgaben ohne Erfüllungsaufwand' verwendet wird.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(exists(./akn:tblock[@refersTo = 'regelungsfolgen-abschnitt-weitere-regelungsfolgen']))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="warn"
                                      id="SCH-00360-035">
                     <xsl:attribute name="test">exists(./akn:tblock[@refersTo = 'regelungsfolgen-abschnitt-weitere-regelungsfolgen'])</xsl:attribute>
                     <svrl:text>Es wird empfohlen, dass innerhalb der Regelungsfolgen ein Abschnitt 'Weitere Regelungsfolgen' verwendet wird.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e709')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="/akn:akomaNtoso/akn:doc[@name = $art-anschreiben-uri]"
                 priority="100"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e740']">
            <schxslt:rule pattern="d15e740">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00380 for context "/akn:akomaNtoso/akn:doc[@name = $art-anschreiben-uri]" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00380">
                  <xsl:attribute name="context">/akn:akomaNtoso/akn:doc[@name = $art-anschreiben-uri]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e740">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00380">
                  <xsl:attribute name="context">/akn:akomaNtoso/akn:doc[@name = $art-anschreiben-uri]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="$bearbeitende-institution-frbrauthor = ( 'recht.bund.de/institution/bundesregierung', 'recht.bund.de/institution/bundestag', 'recht.bund.de/institution/bundeskanzler', 'recht.bund.de/institution/bundespraesident' ) and ./akn:mainBody/akn:p/akn:date[@refersTo = 'fristablauf-datum']">
                  <svrl:successful-report xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                          location="{schxslt:location(.)}"
                                          role="error"
                                          id="SCH-00380-005">
                     <xsl:attribute name="test">$bearbeitende-institution-frbrauthor = ( 'recht.bund.de/institution/bundesregierung', 'recht.bund.de/institution/bundestag', 'recht.bund.de/institution/bundeskanzler', 'recht.bund.de/institution/bundespraesident' ) and ./akn:mainBody/akn:p/akn:date[@refersTo = 'fristablauf-datum']</xsl:attribute>
                     <svrl:text> Das Fristablaufsdatum steht nur dem Bundesrat optional zur Verfügung.</svrl:text>
                  </svrl:successful-report>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e740')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="/akn:akomaNtoso/akn:doc[@name = ($art-anschreiben-uri, $art-vorblatt-uri)]/akn:preface/akn:longTitle/akn:p"
                 priority="99"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e753']">
            <schxslt:rule pattern="d15e753">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00390 for context "/akn:akomaNtoso/akn:doc[@name = ($art-anschreiben-uri, $art-vorblatt-uri)]/akn:preface/akn:longTitle/akn:p" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00390">
                  <xsl:attribute name="context">/akn:akomaNtoso/akn:doc[@name = ($art-anschreiben-uri, $art-vorblatt-uri)]/akn:preface/akn:longTitle/akn:p</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e753">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00390">
                  <xsl:attribute name="context">/akn:akomaNtoso/akn:doc[@name = ($art-anschreiben-uri, $art-vorblatt-uri)]/akn:preface/akn:longTitle/akn:p</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="./akn:inline[@refersTo = $refersto-literal-ausschussueberweisung] and $bearbeitende-institution-frbrauthor = ( 'recht.bund.de/institution/bundesregierung', 'recht.bund.de/institution/bundestag', 'recht.bund.de/institution/bundeskanzler', 'recht.bund.de/institution/bundespraesident' )">
                  <svrl:successful-report xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                          location="{schxslt:location(.)}"
                                          role="error"
                                          id="SCH-00390-005">
                     <xsl:attribute name="test">./akn:inline[@refersTo = $refersto-literal-ausschussueberweisung] and $bearbeitende-institution-frbrauthor = ( 'recht.bund.de/institution/bundesregierung', 'recht.bund.de/institution/bundestag', 'recht.bund.de/institution/bundeskanzler', 'recht.bund.de/institution/bundespraesident' )</xsl:attribute>
                     <svrl:text> Eine Ausschussüberweisung steht nur dem Bundesrat optional zur Verfügung.</svrl:text>
                  </svrl:successful-report>
               </xsl:if>
               <xsl:if test="./akn:docNumber and $bearbeitende-institution-frbrauthor = ( 'recht.bund.de/institution/bundesregierung', 'recht.bund.de/institution/bundeskanzler', 'recht.bund.de/institution/bundespraesident' )">
                  <svrl:successful-report xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                          location="{schxslt:location(.)}"
                                          role="error"
                                          id="SCH-00390-020">
                     <xsl:attribute name="test">./akn:docNumber and $bearbeitende-institution-frbrauthor = ( 'recht.bund.de/institution/bundesregierung', 'recht.bund.de/institution/bundeskanzler', 'recht.bund.de/institution/bundespraesident' )</xsl:attribute>
                     <svrl:text> Die Drucksachennummer steht nur dem Bundestag/Bundesrat optional zur Verfügung.</svrl:text>
                  </svrl:successful-report>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e753')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="/akn:akomaNtoso/akn:act[@name = $art-vereinbarung-uri]/akn:body"
                 priority="98"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e768']">
            <schxslt:rule pattern="d15e768">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00400 for context "/akn:akomaNtoso/akn:act[@name = $art-vereinbarung-uri]/akn:body" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00400">
                  <xsl:attribute name="context">/akn:akomaNtoso/akn:act[@name = $art-vereinbarung-uri]/akn:body</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e768">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00400">
                  <xsl:attribute name="context">/akn:akomaNtoso/akn:act[@name = $art-vereinbarung-uri]/akn:body</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(@refersTo = ( 'notenwechsel', 'vertrag', 'fakultativprotokoll' ))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00400-005">
                     <xsl:attribute name="test">@refersTo = ( 'notenwechsel', 'vertrag', 'fakultativprotokoll' )</xsl:attribute>
                     <svrl:text> Der Hauptteil einer Vereinbarung muss entweder ein Notenwechsel, ein Vertrag oder ein Fakultativprotokoll sein. </svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(count(./akn:hcontainer[@refersTo = 'verbindliche-sprachfassung']) &gt;= 1)">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00400-010">
                     <xsl:attribute name="test">count(./akn:hcontainer[@refersTo = 'verbindliche-sprachfassung']) &gt;= 1</xsl:attribute>
                     <svrl:text> Es muss mindestens eine verbindliche Sprachfassung existieren. </svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e768')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="//akn:textualMod[@type = 'insertion']"
                 priority="97"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e783']">
            <schxslt:rule pattern="d15e783">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00420 for context "//akn:textualMod[@type = 'insertion']" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00420">
                  <xsl:attribute name="context">//akn:textualMod[@type = 'insertion']</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e783">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00420">
                  <xsl:attribute name="context">//akn:textualMod[@type = 'insertion']</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(./akn:destination/@pos)">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00420-005">
                     <xsl:attribute name="test">./akn:destination/@pos</xsl:attribute>
                     <svrl:text>Wenn ein Änderungsbefehl eine Einfügung beinhaltet, muss in seinen Metadaten eine Positionsangabe mittels @pos angegeben werden.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e783')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="//akn:mod[not(ancestor::akn:mod)]"
                 priority="96"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:variable name="alle-textänderungen"
                    select="/akn:akomaNtoso/*/akn:meta/akn:analysis/akn:activeModifications/(akn:textualMod, akn:forceMod)/akn:source"/>
      <xsl:variable name="gesuchte-änderungsbefehl-id" select="concat('#', @eId)"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e800']">
            <schxslt:rule pattern="d15e800">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00422 for context "//akn:mod[not(ancestor::akn:mod)]" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00422">
                  <xsl:attribute name="context">//akn:mod[not(ancestor::akn:mod)]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e800">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00422">
                  <xsl:attribute name="context">//akn:mod[not(ancestor::akn:mod)]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(count($alle-textänderungen[@href = $gesuchte-änderungsbefehl-id]) = 1)">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00422-000">
                     <xsl:attribute name="test">count($alle-textänderungen[@href = $gesuchte-änderungsbefehl-id]) = 1</xsl:attribute>
                     <svrl:text>Zu jedem Änderungsbefehl im Hauptteil (akn:mod) muss es im Metadatenblock genau eine zugehörige Text- oder Geltungszeitänderung geben. Es existiert jedoch keine solche Änderung (akn:textualMod bzw. akn:forceMod), deren Quellenangabe "<xsl:value-of select="$gesuchte-änderungsbefehl-id"/>" referenziert.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e800')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="/akn:akomaNtoso/*/akn:meta/akn:analysis/akn:activeModifications/akn:textualMod/akn:source | /akn:akomaNtoso/*/akn:meta/akn:analysis/akn:activeModifications/akn:forceMod/akn:source"
                 priority="95"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:variable name="referenzierte-änderungsbefehl-id"
                    select="substring(@href, string-length('#') + 1)"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e815']">
            <schxslt:rule pattern="d15e815">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00423 for context "/akn:akomaNtoso/*/akn:meta/akn:analysis/akn:activeModifications/akn:textualMod/akn:source | /akn:akomaNtoso/*/akn:meta/akn:analysis/akn:activeModifications/akn:forceMod/akn:source" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00423">
                  <xsl:attribute name="context">/akn:akomaNtoso/*/akn:meta/akn:analysis/akn:activeModifications/akn:textualMod/akn:source | /akn:akomaNtoso/*/akn:meta/akn:analysis/akn:activeModifications/akn:forceMod/akn:source</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e815">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00423">
                  <xsl:attribute name="context">/akn:akomaNtoso/*/akn:meta/akn:analysis/akn:activeModifications/akn:textualMod/akn:source | /akn:akomaNtoso/*/akn:meta/akn:analysis/akn:activeModifications/akn:forceMod/akn:source</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(count(//akn:mod[@eId = $referenzierte-änderungsbefehl-id]) = 1)">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00423-000">
                     <xsl:attribute name="test">count(//akn:mod[@eId = $referenzierte-änderungsbefehl-id]) = 1</xsl:attribute>
                     <svrl:text>Zu jeder im Metadatenblock deklarierten Textänderung (akn:textualMod) oder Geltungszeitänderung (akn:forceMod) muss es im Hauptteil genau einen zugehörigen Änderungsbefehl (akn:mod) geben. Es existiert jedoch kein solcher Änderungsbefehl, dessen @eId "<xsl:value-of select="$referenzierte-änderungsbefehl-id"/>" lautet.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(starts-with(@href, '#'))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00423-010">
                     <xsl:attribute name="test">starts-with(@href, '#')</xsl:attribute>
                     <svrl:text> Die Referenz auf die Quelle eines Änderungsbefehls innerhalb von akn:activeModifications ist stets ein interner Verweis; sie muss deshalb eine Raute ("#") als erstes Zeichen besitzen. </svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e815')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:mod[@refersTo = 'aenderungsbefehl-umnummerierung']"
                 priority="94"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e831']">
            <schxslt:rule pattern="d15e831">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00424 for context "akn:mod[@refersTo = 'aenderungsbefehl-umnummerierung']" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00424">
                  <xsl:attribute name="context">akn:mod[@refersTo = 'aenderungsbefehl-umnummerierung']</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e831">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00424">
                  <xsl:attribute name="context">akn:mod[@refersTo = 'aenderungsbefehl-umnummerierung']</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(exists(akn:span[@refersTo = 'neue-verweisangabe']))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00424-000">
                     <xsl:attribute name="test">exists(akn:span[@refersTo = 'neue-verweisangabe'])</xsl:attribute>
                     <svrl:text>Ein Umnummerierungsbefehl (akn:mod mit refers-to='aenderungsbefehl-umnummerierung') muss eine neue Verweisangabe (Element akn:span mit refers-to='neue-verweisangabe') enthalten.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e831')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:span[@refersTo = 'neue-verweisangabe']"
                 priority="93"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e840']">
            <schxslt:rule pattern="d15e840">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00425 for context "akn:span[@refersTo = 'neue-verweisangabe']" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00425">
                  <xsl:attribute name="context">akn:span[@refersTo = 'neue-verweisangabe']</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e840">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00425">
                  <xsl:attribute name="context">akn:span[@refersTo = 'neue-verweisangabe']</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(exists(parent::akn:mod[@refersTo = 'aenderungsbefehl-umnummerierung']))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00425-000">
                     <xsl:attribute name="test">exists(parent::akn:mod[@refersTo = 'aenderungsbefehl-umnummerierung'])</xsl:attribute>
                     <svrl:text>Die Auszeichnung als neue Verweisangabe (akn:span mit refers-to='neue-verweisangabe') ist nur zulässig als Kindelement eines Umnummerierungsbefehls (akn:mod mit refers-to='aenderungsbefehl-umnummerierung').</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e840')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:mod[@refersTo and not(ancestor::akn:mod)]"
                 priority="92"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:variable name="textualmod-type-insertion" select="'insertion'"/>
      <xsl:variable name="textualmod-type-substitution" select="'substitution'"/>
      <xsl:variable name="textualmod-type-repeal" select="'repeal'"/>
      <xsl:variable name="textualmod-type-renumbering" select="'renumbering'"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e849']">
            <schxslt:rule pattern="d15e849">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00426 for context "akn:mod[@refersTo and not(ancestor::akn:mod)]" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00426">
                  <xsl:attribute name="context">akn:mod[@refersTo and not(ancestor::akn:mod)]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e849">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00426">
                  <xsl:attribute name="context">akn:mod[@refersTo and not(ancestor::akn:mod)]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if (@refersTo = 'aenderungsbefehl-einfuegen') then (//akn:textualMod[akn:source/@href = concat('#', current()/@eId) and @type = $textualmod-type-insertion]) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00426-000">
                     <xsl:attribute name="test">if (@refersTo = 'aenderungsbefehl-einfuegen') then (//akn:textualMod[akn:source/@href = concat('#', current()/@eId) and @type = $textualmod-type-insertion]) else true()</xsl:attribute>
                     <svrl:text>Ein Änderungsbefehl mit @refersTo='<xsl:value-of select="@refersTo"/>' muss im Metadatenblock mittels zugehörigem textualMod als @type='<xsl:value-of select="$textualmod-type-insertion"/>' deklariert werden.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(if (@refersTo = 'aenderungsbefehl-ersetzen') then (//akn:textualMod[akn:source/@href = concat('#', current()/@eId) and @type = $textualmod-type-substitution]) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00426-005">
                     <xsl:attribute name="test">if (@refersTo = 'aenderungsbefehl-ersetzen') then (//akn:textualMod[akn:source/@href = concat('#', current()/@eId) and @type = $textualmod-type-substitution]) else true()</xsl:attribute>
                     <svrl:text>Ein Änderungsbefehl mit @refersTo='<xsl:value-of select="@refersTo"/>' muss im Metadatenblock mittels zugehörigem textualMod als @type='<xsl:value-of select="$textualmod-type-substitution"/>' deklariert werden. <xsl:value-of select="if (not(empty(//akn:textualMod[akn:source/@href = concat('#', current()/@eId)]))) then (concat('Das zugehörige Metadatum lautet jedoch &#34;', normalize-space(concat(//akn:textualMod[akn:source/@href = concat('#', current()/@eId)]/@type, '&#34;!')))) else ()"/>
                     </svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(if (@refersTo = 'aenderungsbefehl-streichen') then (//akn:textualMod[akn:source/@href = concat('#', current()/@eId) and @type = $textualmod-type-repeal]) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00426-010">
                     <xsl:attribute name="test">if (@refersTo = 'aenderungsbefehl-streichen') then (//akn:textualMod[akn:source/@href = concat('#', current()/@eId) and @type = $textualmod-type-repeal]) else true()</xsl:attribute>
                     <svrl:text>Ein Änderungsbefehl mit @refersTo='<xsl:value-of select="@refersTo"/>' muss im Metadatenblock mittels zugehörigem textualMod als @type='<xsl:value-of select="$textualmod-type-repeal"/>' deklariert werden. <xsl:value-of select="if (not(empty(//akn:textualMod[akn:source/@href = concat('#', current()/@eId)]))) then (concat('Das zugehörige Metadatum lautet jedoch &#34;', normalize-space(concat(//akn:textualMod[akn:source/@href = concat('#', current()/@eId)]/@type, '&#34;!')))) else 'Dem vorliegenden Änderungbefehl ist jedoch überhaupt kein entsprechendes Metadatum (textualMod) zugeordnet!'"/>
                     </svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(if (@refersTo = 'aenderungsbefehl-umnummerierung') then (//akn:textualMod[akn:source/@href = concat('#', current()/@eId) and @type = $textualmod-type-renumbering]) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00426-015">
                     <xsl:attribute name="test">if (@refersTo = 'aenderungsbefehl-umnummerierung') then (//akn:textualMod[akn:source/@href = concat('#', current()/@eId) and @type = $textualmod-type-renumbering]) else true()</xsl:attribute>
                     <svrl:text>Ein Änderungsbefehl mit @refersTo='<xsl:value-of select="@refersTo"/>' muss im Metadatenblock mittels zugehörigem textualMod als @type='<xsl:value-of select="$textualmod-type-renumbering"/>' deklariert werden. <xsl:value-of select="if (not(empty(//akn:textualMod[akn:source/@href = concat('#', current()/@eId)]))) then (concat('Das zugehörige Metadatum lautet jedoch &#34;', normalize-space(concat(//akn:textualMod[akn:source/@href = concat('#', current()/@eId)]/@type, '&#34;!')))) else 'Dem vorliegenden Änderungbefehl ist jedoch überhaupt kein entsprechendes Metadatum (textualMod) zugeordnet!'"/>
                     </svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(if (@refersTo = 'aenderungsbefehl-neufassung') then (//akn:textualMod[akn:source/@href = concat('#', current()/@eId) and @type = $textualmod-type-substitution]) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00426-020">
                     <xsl:attribute name="test">if (@refersTo = 'aenderungsbefehl-neufassung') then (//akn:textualMod[akn:source/@href = concat('#', current()/@eId) and @type = $textualmod-type-substitution]) else true()</xsl:attribute>
                     <svrl:text>Ein Änderungsbefehl mit @refersTo='<xsl:value-of select="@refersTo"/>' muss im Metadatenblock mittels zugehörigem textualMod als @type='<xsl:value-of select="$textualmod-type-substitution"/>' deklariert werden. <xsl:value-of select="if (not(empty(//akn:textualMod[akn:source/@href = concat('#', current()/@eId)]))) then (concat('Das zugehörige Metadatum lautet jedoch &#34;', normalize-space(concat(//akn:textualMod[akn:source/@href = concat('#', current()/@eId)]/@type, '&#34;!')))) else 'Dem vorliegenden Änderungbefehl ist jedoch überhaupt kein entsprechendes Metadatum (textualMod) zugeordnet!'"/>
                     </svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(if (@refersTo = 'aenderungsbefehl-ersetzen-weggefallen') then (//akn:textualMod[akn:source/@href = concat('#', current()/@eId) and @type = $textualmod-type-substitution]) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00426-025">
                     <xsl:attribute name="test">if (@refersTo = 'aenderungsbefehl-ersetzen-weggefallen') then (//akn:textualMod[akn:source/@href = concat('#', current()/@eId) and @type = $textualmod-type-substitution]) else true()</xsl:attribute>
                     <svrl:text>Ein Änderungsbefehl mit @refersTo='<xsl:value-of select="@refersTo"/>' muss im Metadatenblock mittels zugehörigem textualMod als @type='<xsl:value-of select="$textualmod-type-substitution"/>' deklariert werden. <xsl:value-of select="if (not(empty(//akn:textualMod[akn:source/@href = concat('#', current()/@eId)]))) then (concat('Das zugehörige Metadatum lautet jedoch &#34;', normalize-space(concat(//akn:textualMod[akn:source/@href = concat('#', current()/@eId)]/@type, '&#34;!')))) else 'Dem vorliegenden Änderungbefehl ist jedoch überhaupt kein entsprechendes Metadatum (textualMod) zugeordnet!'"/>
                     </svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(if (@refersTo = 'aenderungsbefehl-neufassung-weggefallen') then (//akn:textualMod[akn:source/@href = concat('#', current()/@eId) and @type = $textualmod-type-substitution]) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00426-030">
                     <xsl:attribute name="test">if (@refersTo = 'aenderungsbefehl-neufassung-weggefallen') then (//akn:textualMod[akn:source/@href = concat('#', current()/@eId) and @type = $textualmod-type-substitution]) else true()</xsl:attribute>
                     <svrl:text>Ein Änderungsbefehl mit @refersTo='<xsl:value-of select="@refersTo"/>' muss im Metadatenblock mittels zugehörigem textualMod als @type='<xsl:value-of select="$textualmod-type-substitution"/>' deklariert werden. <xsl:value-of select="if (not(empty(//akn:textualMod[akn:source/@href = concat('#', current()/@eId)]))) then (concat('Das zugehörige Metadatum lautet jedoch &#34;', normalize-space(concat(//akn:textualMod[akn:source/@href = concat('#', current()/@eId)]/@type, '&#34;!')))) else 'Dem vorliegenden Änderungbefehl ist jedoch überhaupt kein entsprechendes Metadatum (textualMod) zugeordnet!'"/>
                     </svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e849')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="/akn:akomaNtoso/(akn:act | akn:bill | akn:doc | akn:statement | akn:documentCollection)//* [(@eId and exists(parent::*[@eId])) and not(local-name() = 'article' and not(ancestor::akn:quotedStructure))]"
                 priority="91"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:variable name="trennzeichen-zwischen-eids" select="'_'"/>
      <xsl:variable name="geprüfte-eid" select="@eId"/>
      <xsl:variable name="geprüfte-eid-lokaler-teil"
                    select="tokenize(@eId, $trennzeichen-zwischen-eids)[last()]"/>
      <xsl:variable name="vorgänger-eid" select="ancestor::*[1]/@eId"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e929']">
            <schxslt:rule pattern="d15e929">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00430 for context "/akn:akomaNtoso/(akn:act | akn:bill | akn:doc | akn:statement | akn:documentCollection)//* [(@eId and exists(parent::*[@eId])) and not(local-name() = 'article' and not(ancestor::akn:quotedStructure))]" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00430">
                  <xsl:attribute name="context">/akn:akomaNtoso/(akn:act | akn:bill | akn:doc | akn:statement | akn:documentCollection)//* [(@eId and exists(parent::*[@eId])) and not(local-name() = 'article' and not(ancestor::akn:quotedStructure))]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e929">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00430">
                  <xsl:attribute name="context">/akn:akomaNtoso/(akn:act | akn:bill | akn:doc | akn:statement | akn:documentCollection)//* [(@eId and exists(parent::*[@eId])) and not(local-name() = 'article' and not(ancestor::akn:quotedStructure))]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not($geprüfte-eid = concat($vorgänger-eid, $trennzeichen-zwischen-eids, $geprüfte-eid-lokaler-teil))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00430-000">
                     <xsl:attribute name="test">$geprüfte-eid = concat($vorgänger-eid, $trennzeichen-zwischen-eids, $geprüfte-eid-lokaler-teil)</xsl:attribute>
                     <svrl:text>Die @eId muss als Präfix vor ihrem lokalen Teil (hier: "<xsl:value-of select="$geprüfte-eid-lokaler-teil"/>") die @eId ihres nächstgelegenen und ebenfalls über eine @eId verfügenden Vorgängers besitzen, verbunden durch das Zeichen ("<xsl:value-of select="$trennzeichen-zwischen-eids"/>"). Erwartet wird im vorliegenden Fall damit konkret: "<xsl:value-of select="concat($vorgänger-eid, $trennzeichen-zwischen-eids, $geprüfte-eid-lokaler-teil)"/>".</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e929')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="@eId[starts-with(tokenize(., '-')[last()], $präfix-eid-zitierbar) and parent::*/akn:num != '']"
                 priority="90"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:variable name="positionsangabe-ist" select="tokenize(., '-')[last()]"/>
      <xsl:variable name="eingabe-textknoten" select="parent::*/akn:num/text()"/>
      <xsl:variable name="doppelparagraph" select="'§§ '"/>
      <xsl:variable name="platzhalter" select="'µµ '"/>
      <xsl:variable name="weißraumnormalisiert"
                    select="normalize-space(lower-case($eingabe-textknoten))"/>
      <xsl:variable name="mit-platzhaltern"
                    select="replace($weißraumnormalisiert, $doppelparagraph, $platzhalter)"/>
      <xsl:variable name="ohne-sonderzeichen"
                    select="replace($mit-platzhaltern, '(§ )|(art\. )|(art )|(artikel )', '')"/>
      <xsl:variable name="ohne-klammern"
                    select="replace($ohne-sonderzeichen, '(\()(\d+[a-z]*)(\))', '$2')"/>
      <xsl:variable name="maskiert" select="translate($ohne-klammern, '-_.', '~~~')"/>
      <xsl:variable name="ggf-mit-doppelten-paragraphenzeichen"
                    select="replace($maskiert, $platzhalter, $doppelparagraph)"/>
      <xsl:variable name="normalisierte-positionsangabe-eid"
                    select="encode-for-uri($ggf-mit-doppelten-paragraphenzeichen)"/>
      <xsl:variable name="positionsangabe-soll"
                    select="lower-case(concat($präfix-eid-zitierbar, $normalisierte-positionsangabe-eid))"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e952']">
            <schxslt:rule pattern="d15e952">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-000431 for context "@eId[starts-with(tokenize(., '-')[last()], $präfix-eid-zitierbar) and parent::*/akn:num != '']" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-000431">
                  <xsl:attribute name="context">@eId[starts-with(tokenize(., '-')[last()], $präfix-eid-zitierbar) and parent::*/akn:num != '']</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e952">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-000431">
                  <xsl:attribute name="context">@eId[starts-with(tokenize(., '-')[last()], $präfix-eid-zitierbar) and parent::*/akn:num != '']</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if (not(parent::*/akn:num[@refersTo = 'ordinale-zaehlung-eid'])) then $positionsangabe-ist = $positionsangabe-soll else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00431-005">
                     <xsl:attribute name="test">if (not(parent::*/akn:num[@refersTo = 'ordinale-zaehlung-eid'])) then $positionsangabe-ist = $positionsangabe-soll else true()</xsl:attribute>
                     <svrl:text>Eine "zitierbare" (mit dem Präfix "<xsl:value-of select="$präfix-eid-zitierbar"/>" gebildete) @eId muss als Positionsangabe den normalisierten Wert der zum Elternelement zugehörigen Art- und Zählbezeichnung (akn:num) enthalten, sofern nicht dort deklariert ist, dass die Positionsangabe ordinal erfolgen soll. Im vorliegenden Fall wurde der betreffende Teil der @eId gebildet als "<xsl:value-of select="$positionsangabe-ist"/>", während der Textknoten von akn:num "<xsl:value-of select="parent::*/akn:num"/>" lautet. Erwartet würde damit die normalisierte Angabe "<xsl:value-of select="$positionsangabe-soll"/>".</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e952')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="@eId[starts-with(tokenize(., '-')[last()], $präfix-eid-zitierbar)]"
                 priority="89"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e952']">
            <schxslt:rule pattern="d15e952">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00432 for context "@eId[starts-with(tokenize(., '-')[last()], $präfix-eid-zitierbar)]" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00432">
                  <xsl:attribute name="context">@eId[starts-with(tokenize(., '-')[last()], $präfix-eid-zitierbar)]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e952">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00432">
                  <xsl:attribute name="context">@eId[starts-with(tokenize(., '-')[last()], $präfix-eid-zitierbar)]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(exists(parent::*/akn:num))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00432-000">
                     <xsl:attribute name="test">exists(parent::*/akn:num)</xsl:attribute>
                     <svrl:text>Nur ein Element, das über eine Art- und Zählbezeichnung verfügt, kann eine "zitierbare" (mit Präfix "<xsl:value-of select="$präfix-eid-zitierbar"/>" gebildete) @eId besitzen. Andernfalls ist Präfix "<xsl:value-of select="$präfix-eid-nummerierbar"/>" zu verwenden.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e952')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:num[@refersTo = $literal-deklaration-ausnahme-eid-zählweise]"
                 priority="88"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e1026']">
            <schxslt:rule pattern="d15e1026">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00433 for context "akn:num[@refersTo = $literal-deklaration-ausnahme-eid-zählweise]" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00433">
                  <xsl:attribute name="context">akn:num[@refersTo = $literal-deklaration-ausnahme-eid-zählweise]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e1026">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00433">
                  <xsl:attribute name="context">akn:num[@refersTo = $literal-deklaration-ausnahme-eid-zählweise]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(local-name(parent::*) = $zitierbare-elementtypen)">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(parent::*)}"
                                      id="SCH-00433-000">
                     <xsl:attribute name="test">local-name(parent::*) = $zitierbare-elementtypen</xsl:attribute>
                     <svrl:text>Die Deklaration der ausnahmsweisen ordinalen Zählung eines Elementes, obwohl für es eine Art- und Zählbezeichnung vorhanden ist, darf nur für Einzelvorschriften (akn:article), juristische Absätze (akn:paragraph) oder Listenuntergliederungselemente (aksn:point) vorgenommen werden.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(starts-with(tokenize(parent::*/@eId, '-')[last()], $präfix-eid-nummerierbar))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(parent::*)}"
                                      id="SCH-00433-005">
                     <xsl:attribute name="test">starts-with(tokenize(parent::*/@eId, '-')[last()], $präfix-eid-nummerierbar)</xsl:attribute>
                     <svrl:text>Die @eId eines Elementes, für welches an seinem Kindelement akn:num mittels refersTo='<xsl:value-of select="$literal-deklaration-ausnahme-eid-zählweise"/>' deklariert wurde, dass nicht die Art- und Zählbezeichnung zu verwenden sei, sondern eine ordinale Zählung vorzunehmen ist, muss im lokalen Teil seiner eId (hier: "<xsl:value-of select="tokenize(parent::*/@eId, '_')[last()]"/>") das Präfix "<xsl:value-of select="$präfix-eid-nummerierbar"/>" besitzen, nicht "<xsl:value-of select="$präfix-eid-zitierbar"/>".</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e1026')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="*[@eId[starts-with(tokenize(., '-')[last()], $präfix-eid-nummerierbar)] and not(local-name() = 'article')]"
                 priority="87"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:variable name="elementtyp-bezeichner" select="local-name(.)"/>
      <xsl:variable name="elementrang-soll"
                    select="1 + count(preceding-sibling::*[local-name() eq $elementtyp-bezeichner])"/>
      <xsl:variable name="lokaler-eId-teil" select="tokenize(@eId, '_')[last()]"/>
      <xsl:variable name="elementrang-ist"
                    select="xs:integer(tokenize($lokaler-eId-teil, concat('-', $präfix-eid-nummerierbar))[last()])"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e1049']">
            <schxslt:rule pattern="d15e1049">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00434 for context "*[@eId[starts-with(tokenize(., '-')[last()], $präfix-eid-nummerierbar)] and not(local-name() = 'article')]" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00434">
                  <xsl:attribute name="context">*[@eId[starts-with(tokenize(., '-')[last()], $präfix-eid-nummerierbar)] and not(local-name() = 'article')]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e1049">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00434">
                  <xsl:attribute name="context">*[@eId[starts-with(tokenize(., '-')[last()], $präfix-eid-nummerierbar)] and not(local-name() = 'article')]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not($elementrang-ist = $elementrang-soll)">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00434-000">
                     <xsl:attribute name="test">$elementrang-ist = $elementrang-soll</xsl:attribute>
                     <svrl:text>Elemente, deren @eId im lokalen Teil (hier: "<xsl:value-of select="$lokaler-eId-teil"/>) das Präfix "<xsl:value-of select="$präfix-eid-nummerierbar"/>" enthalten, müssen ihre Positionsangabe mittels ordinaler Zählung bilden. Konkret würde hier als Position [<xsl:value-of select="$elementrang-soll"/>] erwartet, nicht [<xsl:value-of select="$elementrang-ist"/>].</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e1049')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="*[local-name() = $zitierbare-elementtypen and @eId[starts-with(tokenize(., '-')[last()], $präfix-eid-nummerierbar)] and exists(akn:num)]"
                 priority="86"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e1076']">
            <schxslt:rule pattern="d15e1076">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00435 for context "*[local-name() = $zitierbare-elementtypen and @eId[starts-with(tokenize(., '-')[last()], $präfix-eid-nummerierbar)] and exists(akn:num)]" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00435">
                  <xsl:attribute name="context">*[local-name() = $zitierbare-elementtypen and @eId[starts-with(tokenize(., '-')[last()], $präfix-eid-nummerierbar)] and exists(akn:num)]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e1076">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00435">
                  <xsl:attribute name="context">*[local-name() = $zitierbare-elementtypen and @eId[starts-with(tokenize(., '-')[last()], $präfix-eid-nummerierbar)] and exists(akn:num)]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(akn:num/@refersTo = 'ordinale-zaehlung-eid')">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00435-000">
                     <xsl:attribute name="test">akn:num/@refersTo = 'ordinale-zaehlung-eid'</xsl:attribute>
                     <svrl:text>Bei eIds, deren Positionsangabe mittels ordinaler Zählung erfolgt (n-Präfix), obwohl tatsächlich eine Art- und Zählbezeichnung vorhanden ist, muss an ihrem Kindelement akn:num explizit deklariert sein, dass die Zählung des Elternelements nichtsdestotrotz ordinal erfolgen soll.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e1076')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:passiveModifications/akn:textualMod/akn:destination/@href"
                 priority="85"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:variable name="lokaler-verweis"
                    select="substring(., 2) (: das Rautesymbol des lokalen Verweises überspringen :)"/>
      <xsl:variable name="lokaler-verweis-ohne-zeichenbereich"
                    select="replace($lokaler-verweis, '(/\d+-\d+)$', '')"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e1088']">
            <schxslt:rule pattern="d15e1088">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00436 for context "akn:passiveModifications/akn:textualMod/akn:destination/@href" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00436">
                  <xsl:attribute name="context">akn:passiveModifications/akn:textualMod/akn:destination/@href</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e1088">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00436">
                  <xsl:attribute name="context">akn:passiveModifications/akn:textualMod/akn:destination/@href</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(starts-with(., '#'))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00436-000">
                     <xsl:attribute name="test">starts-with(., '#')</xsl:attribute>
                     <svrl:text>Die Zielangabe einer Textänderung innerhalb von "aenderungenPassiv" (also: akn:passiveModifications/akn:textualMod/akn:destination/@href) muss stets einen Verweis auf ein Element innerhalb des aktuellen Dokumentes enthalten. Solche lokalen Verweise beginnen zwingend mit einer Raute ("#").</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(count(/akn:akomaNtoso//*[@eId eq $lokaler-verweis-ohne-zeichenbereich]) = 1)">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00436-010">
                     <xsl:attribute name="test">count(/akn:akomaNtoso//*[@eId eq $lokaler-verweis-ohne-zeichenbereich]) = 1</xsl:attribute>
                     <svrl:text>Verweise auf ein jeweiliges ELement sind innerhalb von passiveModifications grundsätzlich lokal. Zur hier angegebenen Referenz existiert jedoch im vorliegenden Dokument kein Element mit korrespondierender @eId ("<xsl:value-of select="$lokaler-verweis-ohne-zeichenbereich"/>")!</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e1088')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:article[@eId[starts-with(tokenize(., '-')[last()], $präfix-eid-nummerierbar)]]"
                 priority="84"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:variable name="elementtyp-bezeichner" select="local-name(.)"/>
      <xsl:variable name="id-des-übergeordneten-änderungsbefehls"
                    select="ancestor::akn:mod/generate-id()"/>
      <xsl:variable name="elementrang-soll"
                    select="if (ancestor::akn:mod) then (1 + count(preceding::akn:article[ancestor::akn:mod/generate-id() eq $id-des-übergeordneten-änderungsbefehls])) else (1 + count(preceding::akn:article[not(ancestor::akn:mod)]))"/>
      <xsl:variable name="lokaler-eId-teil" select="tokenize(@eId, '_')[last()]"/>
      <xsl:variable name="elementrang-ist"
                    select="xs:integer(tokenize($lokaler-eId-teil, concat('-', $präfix-eid-nummerierbar))[last()])"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e1109']">
            <schxslt:rule pattern="d15e1109">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00437 for context "akn:article[@eId[starts-with(tokenize(., '-')[last()], $präfix-eid-nummerierbar)]]" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00437">
                  <xsl:attribute name="context">akn:article[@eId[starts-with(tokenize(., '-')[last()], $präfix-eid-nummerierbar)]]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e1109">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00437">
                  <xsl:attribute name="context">akn:article[@eId[starts-with(tokenize(., '-')[last()], $präfix-eid-nummerierbar)]]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not($elementrang-ist = $elementrang-soll)">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00437-000">
                     <xsl:attribute name="test">$elementrang-ist = $elementrang-soll</xsl:attribute>
                     <svrl:text>Bei Einzelvorschriften, für die eine ordinale Zählung deklariert wurde, muss die Positionsangabe zu ihrem Rang passen; dabei ist innerhalb von Änderungsbefehlen lokal zu zählen (d. h. nur auf der Menge der Geschwisterknoten), während außerhalb im Gegensatz dazu global zu zählen ist, wobei jedoch Einzelvorschriften innerhalb etwaiger Änderungsbefehle nicht mitgezählt werden. Konkret würde für die vorliegende Einzelvorschrift als Position [<xsl:value-of select="$elementrang-soll"/>] erwartet, nicht [<xsl:value-of select="$elementrang-ist"/>].</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e1109')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="/akn:akomaNtoso/(akn:act | akn:bill | akn:doc | akn:statement | akn:documentCollection)//akn:article[not(ancestor::akn:quotedStructure)]/@eId"
                 priority="83"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e1133']">
            <schxslt:rule pattern="d15e1133">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00438 for context "/akn:akomaNtoso/(akn:act | akn:bill | akn:doc | akn:statement | akn:documentCollection)//akn:article[not(ancestor::akn:quotedStructure)]/@eId" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00438">
                  <xsl:attribute name="context">/akn:akomaNtoso/(akn:act | akn:bill | akn:doc | akn:statement | akn:documentCollection)//akn:article[not(ancestor::akn:quotedStructure)]/@eId</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e1133">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00438">
                  <xsl:attribute name="context">/akn:akomaNtoso/(akn:act | akn:bill | akn:doc | akn:statement | akn:documentCollection)//akn:article[not(ancestor::akn:quotedStructure)]/@eId</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(starts-with(., 'art-'))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00438-000">
                     <xsl:attribute name="test">starts-with(., 'art-')</xsl:attribute>
                     <svrl:text>Die @eId einer Einzelvorschrift außerhalb einer akn:quotedStructure muss mit "art-" beginnen; anders als andere Elemente 'erbt' sie nicht die @eId ihres Elternelements als Präfix!</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e1133')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="/akn:akomaNtoso/(akn:act | akn:bill | akn:doc | akn:statement | akn:documentCollection)//akn:article[ancestor::akn:quotedStructure]/@eId"
                 priority="82"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e1133']">
            <schxslt:rule pattern="d15e1133">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00439 for context "/akn:akomaNtoso/(akn:act | akn:bill | akn:doc | akn:statement | akn:documentCollection)//akn:article[ancestor::akn:quotedStructure]/@eId" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00439">
                  <xsl:attribute name="context">/akn:akomaNtoso/(akn:act | akn:bill | akn:doc | akn:statement | akn:documentCollection)//akn:article[ancestor::akn:quotedStructure]/@eId</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e1133">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00439">
                  <xsl:attribute name="context">/akn:akomaNtoso/(akn:act | akn:bill | akn:doc | akn:statement | akn:documentCollection)//akn:article[ancestor::akn:quotedStructure]/@eId</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(matches(., '([a-zäöüß0-9]+-(n[1-9]{1}[0-9]*|z[0-9a-zäöüß~%]*)_)+art-(z|n)\d*[0-9a-zäöüß~%]*$'))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00439-000">
                     <xsl:attribute name="test">matches(., '([a-zäöüß0-9]+-(n[1-9]{1}[0-9]*|z[0-9a-zäöüß~%]*)_)+art-(z|n)\d*[0-9a-zäöüß~%]*$')</xsl:attribute>
                     <svrl:text>Die @eId einer Einzelvorschrift innerhalb einer akn:quotedStructure darf nicht lediglich aus ihrem eigenen Kurzbezeichner, "art-", sowie einer Positionsangabe bestehen; anders als bei Einzelvorschriften außerhalb der quotedStructure wird innerhalb ebendieser die eId nicht "abgeschnitten", sondern "erbt" zwingend die @eId ihres Elternknotens als Präfix.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e1133')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="@eId" priority="81" mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:variable name="kontext-eId-inhalt" select="."/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e1148']">
            <schxslt:rule pattern="d15e1148">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00450 for context "@eId" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00450">
                  <xsl:attribute name="context">@eId</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e1148">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00450">
                  <xsl:attribute name="context">@eId</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(count(key('nodes-by-eId', $kontext-eId-inhalt)) eq 1)">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00450-000">
                     <xsl:attribute name="test">count(key('nodes-by-eId', $kontext-eId-inhalt)) eq 1</xsl:attribute>
                     <svrl:text>Eine eId muss dokumentweit einmalig sein; eId "<xsl:value-of select="$kontext-eId-inhalt"/>" kommt im vorliegenden Dokument jedoch <xsl:value-of select="count(key('nodes-by-eId', $kontext-eId-inhalt))"/>-mal vor!</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e1148')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="@GUID" priority="80" mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:variable name="kontext-guid-inhalt" select="."/>
      <xsl:variable name="häufigkeit-der-aktuellen-guid"
                    select="count(key('nodes-by-GUID', $kontext-guid-inhalt))"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e1167']">
            <schxslt:rule pattern="d15e1167">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00460 for context "@GUID" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00460">
                  <xsl:attribute name="context">@GUID</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e1167">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00460">
                  <xsl:attribute name="context">@GUID</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(xs:int($häufigkeit-der-aktuellen-guid) eq 1)">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00460-000">
                     <xsl:attribute name="test">xs:int($häufigkeit-der-aktuellen-guid) eq 1</xsl:attribute>
                     <svrl:text>GUIDs müssen einmalig sein; "<xsl:value-of select="$kontext-guid-inhalt"/>" kommt jedoch <xsl:value-of select="$häufigkeit-der-aktuellen-guid"/>-mal im Dokument vor!</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e1167')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRthis"
                 priority="79"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e1346']">
            <schxslt:rule pattern="d15e1346">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00500 for context "/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRthis" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00500">
                  <xsl:attribute name="context">/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRthis</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e1346">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00500">
                  <xsl:attribute name="context">/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRthis</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($ist-entwurfsfassung) then (@value = $FRBRthis-entwurfsfassung-work-inhalt) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00500-005">
                     <xsl:attribute name="test">if ($ist-entwurfsfassung) then (@value = $FRBRthis-entwurfsfassung-work-inhalt) else true()</xsl:attribute>
                     <svrl:text>
                        <xsl:value-of select="$FRBRthis-entwurfsfassung-work-beschreibung"/> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<xsl:value-of select="$FRBRthis-entwurfsfassung-work-aufbau"/>". Erwartet würde hier konkret: "<xsl:value-of select="$FRBRthis-entwurfsfassung-work-inhalt"/>". </svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(if ($ist-verkündungsfassung) then (@value = $FRBRthis-verkündungsfassung-work-inhalt) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00500-010">
                     <xsl:attribute name="test">if ($ist-verkündungsfassung) then (@value = $FRBRthis-verkündungsfassung-work-inhalt) else true()</xsl:attribute>
                     <svrl:text>
                        <xsl:value-of select="$FRBRthis-verkündungsfassung-work-beschreibung"/> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<xsl:value-of select="$FRBRthis-verkündungsfassung-work-aufbau"/>". Erwartet würde hier konkret: "<xsl:value-of select="$FRBRthis-verkündungsfassung-work-inhalt"/>".</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(if ($ist-konsolidierte-fassung) then (@value = $FRBRthis-konsolidierte-fassung-work-inhalt) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00500-015">
                     <xsl:attribute name="test">if ($ist-konsolidierte-fassung) then (@value = $FRBRthis-konsolidierte-fassung-work-inhalt) else true()</xsl:attribute>
                     <svrl:text>
                        <xsl:value-of select="$FRBRthis-konsolidierte-fassung-work-beschreibung"/> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<xsl:value-of select="$FRBRthis-konsolidierte-fassung-work-aufbau"/>". Erwartet würde hier konkret: "<xsl:value-of select="$FRBRthis-konsolidierte-fassung-work-inhalt"/>".</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e1346')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRExpression/akn:FRBRthis"
                 priority="78"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e1346']">
            <schxslt:rule pattern="d15e1346">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00510 for context "/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRExpression/akn:FRBRthis" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00510">
                  <xsl:attribute name="context">/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRExpression/akn:FRBRthis</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e1346">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00510">
                  <xsl:attribute name="context">/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRExpression/akn:FRBRthis</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($ist-entwurfsfassung) then (@value = $FRBRthis-entwurfsfassung-expression-inhalt) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00510-005">
                     <xsl:attribute name="test">if ($ist-entwurfsfassung) then (@value = $FRBRthis-entwurfsfassung-expression-inhalt) else true()</xsl:attribute>
                     <svrl:text>
                        <xsl:value-of select="$FRBRthis-entwurfsfassung-expression-beschreibung"/> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<xsl:value-of select="$FRBRthis-entwurfsfassung-expression-aufbau"/>". Erwartet würde hier konkret: "<xsl:value-of select="$FRBRthis-entwurfsfassung-expression-inhalt"/>". </svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(if ($ist-verkündungsfassung) then (@value = $FRBRthis-verkündungsfassung-expression-inhalt) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00510-010">
                     <xsl:attribute name="test">if ($ist-verkündungsfassung) then (@value = $FRBRthis-verkündungsfassung-expression-inhalt) else true()</xsl:attribute>
                     <svrl:text>
                        <xsl:value-of select="$FRBRthis-verkündungsfassung-expression-beschreibung"/> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<xsl:value-of select="$FRBRthis-verkündungsfassung-expression-aufbau"/>". Erwartet würde hier konkret: "<xsl:value-of select="$FRBRthis-verkündungsfassung-expression-inhalt"/>".</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(if ($ist-konsolidierte-fassung) then (@value = $FRBRthis-konsolidierte-fassung-expression-inhalt) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00510-015">
                     <xsl:attribute name="test">if ($ist-konsolidierte-fassung) then (@value = $FRBRthis-konsolidierte-fassung-expression-inhalt) else true()</xsl:attribute>
                     <svrl:text>
                        <xsl:value-of select="$FRBRthis-konsolidierte-fassung-expression-beschreibung"/> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<xsl:value-of select="$FRBRthis-konsolidierte-fassung-expression-aufbau"/>". Erwartet würde hier konkret: "<xsl:value-of select="$FRBRthis-konsolidierte-fassung-expression-inhalt"/>".</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e1346')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRManifestation/akn:FRBRthis"
                 priority="77"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e1346']">
            <schxslt:rule pattern="d15e1346">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00520 for context "/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRManifestation/akn:FRBRthis" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00520">
                  <xsl:attribute name="context">/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRManifestation/akn:FRBRthis</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e1346">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00520">
                  <xsl:attribute name="context">/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRManifestation/akn:FRBRthis</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($ist-entwurfsfassung) then (@value = $FRBRthis-entwurfsfassung-manifestation-inhalt) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00520-005">
                     <xsl:attribute name="test">if ($ist-entwurfsfassung) then (@value = $FRBRthis-entwurfsfassung-manifestation-inhalt) else true()</xsl:attribute>
                     <svrl:text>
                        <xsl:value-of select="$FRBRthis-entwurfsfassung-manifestation-beschreibung"/> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<xsl:value-of select="$FRBRthis-entwurfsfassung-manifestation-aufbau"/>". Erwartet würde hier konkret: "<xsl:value-of select="$FRBRthis-entwurfsfassung-manifestation-inhalt"/>".</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(if ($ist-verkündungsfassung) then (@value = $FRBRthis-verkündungsfassung-manifestation-inhalt) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00520-010">
                     <xsl:attribute name="test">if ($ist-verkündungsfassung) then (@value = $FRBRthis-verkündungsfassung-manifestation-inhalt) else true()</xsl:attribute>
                     <svrl:text>
                        <xsl:value-of select="$FRBRthis-verkündungsfassung-manifestation-beschreibung"/> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<xsl:value-of select="$FRBRthis-verkündungsfassung-manifestation-aufbau"/>". Erwartet würde hier konkret: "<xsl:value-of select="$FRBRthis-verkündungsfassung-manifestation-inhalt"/>".</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(if ($ist-konsolidierte-fassung) then (@value = $FRBRthis-konsolidierte-fassung-manifestation-inhalt) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00520-015">
                     <xsl:attribute name="test">if ($ist-konsolidierte-fassung) then (@value = $FRBRthis-konsolidierte-fassung-manifestation-inhalt) else true()</xsl:attribute>
                     <svrl:text>
                        <xsl:value-of select="$FRBRthis-konsolidierte-fassung-manifestation-beschreibung"/> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<xsl:value-of select="$FRBRthis-konsolidierte-fassung-manifestation-aufbau"/>". Erwartet würde hier konkret: "<xsl:value-of select="$FRBRthis-konsolidierte-fassung-manifestation-inhalt"/>".</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e1346')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRuri"
                 priority="76"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e1446']">
            <schxslt:rule pattern="d15e1446">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00530 for context "/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRuri" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00530">
                  <xsl:attribute name="context">/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRuri</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e1446">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00530">
                  <xsl:attribute name="context">/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRuri</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($ist-entwurfsfassung) then (@value = $FRBRuri-entwurfsfassung-work-inhalt) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00530-005">
                     <xsl:attribute name="test">if ($ist-entwurfsfassung) then (@value = $FRBRuri-entwurfsfassung-work-inhalt) else true()</xsl:attribute>
                     <svrl:text>
                        <xsl:value-of select="$FRBRuri-entwurfsfassung-work-beschreibung"/> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<xsl:value-of select="$FRBRuri-entwurfsfassung-work-aufbau"/>". Erwartet würde hier konkret: "<xsl:value-of select="$FRBRuri-entwurfsfassung-work-inhalt"/>".</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(if ($ist-verkündungsfassung) then (@value = $FRBRuri-verkündungsfassung-work-inhalt) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00530-010">
                     <xsl:attribute name="test">if ($ist-verkündungsfassung) then (@value = $FRBRuri-verkündungsfassung-work-inhalt) else true()</xsl:attribute>
                     <svrl:text>
                        <xsl:value-of select="$FRBRuri-verkündungsfassung-work-beschreibung"/> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<xsl:value-of select="$FRBRuri-verkündungsfassung-work-aufbau"/>". Erwartet würde hier konkret: "<xsl:value-of select="$FRBRuri-verkündungsfassung-work-inhalt"/>".</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(if ($ist-konsolidierte-fassung) then (@value = $FRBRuri-konsolidierte-fassung-work-inhalt) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00530-015">
                     <xsl:attribute name="test">if ($ist-konsolidierte-fassung) then (@value = $FRBRuri-konsolidierte-fassung-work-inhalt) else true()</xsl:attribute>
                     <svrl:text>
                        <xsl:value-of select="$FRBRuri-konsolidierte-fassung-work-beschreibung"/> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<xsl:value-of select="$FRBRuri-verkündungsfassung-work-aufbau"/>". Erwartet würde hier konkret: "<xsl:value-of select="$FRBRuri-konsolidierte-fassung-work-inhalt"/>".</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e1446')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRExpression/akn:FRBRuri"
                 priority="75"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e1446']">
            <schxslt:rule pattern="d15e1446">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00540 for context "/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRExpression/akn:FRBRuri" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00540">
                  <xsl:attribute name="context">/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRExpression/akn:FRBRuri</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e1446">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00540">
                  <xsl:attribute name="context">/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRExpression/akn:FRBRuri</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($ist-entwurfsfassung) then (@value = $FRBRuri-entwurfsfassung-expression-inhalt) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00540-005">
                     <xsl:attribute name="test">if ($ist-entwurfsfassung) then (@value = $FRBRuri-entwurfsfassung-expression-inhalt) else true()</xsl:attribute>
                     <svrl:text>
                        <xsl:value-of select="$FRBRuri-entwurfsfassung-expression-beschreibung"/> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<xsl:value-of select="$FRBRuri-entwurfsfassung-expression-aufbau"/>". Erwartet würde hier konkret: "<xsl:value-of select="$FRBRuri-entwurfsfassung-expression-inhalt"/>".</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(if ($ist-verkündungsfassung) then (@value = $FRBRuri-verkündungsfassung-expression-inhalt) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00540-010">
                     <xsl:attribute name="test">if ($ist-verkündungsfassung) then (@value = $FRBRuri-verkündungsfassung-expression-inhalt) else true()</xsl:attribute>
                     <svrl:text>
                        <xsl:value-of select="$FRBRuri-verkündungsfassung-expression-beschreibung"/> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<xsl:value-of select="$FRBRuri-verkündungsfassung-expression-aufbau"/>". Erwartet würde hier konkret: "<xsl:value-of select="$FRBRuri-verkündungsfassung-expression-inhalt"/>".</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(if ($ist-konsolidierte-fassung) then (@value = $FRBRuri-konsolidierte-fassung-expression-inhalt) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00540-015">
                     <xsl:attribute name="test">if ($ist-konsolidierte-fassung) then (@value = $FRBRuri-konsolidierte-fassung-expression-inhalt) else true()</xsl:attribute>
                     <svrl:text>
                        <xsl:value-of select="$FRBRuri-konsolidierte-fassung-expression-beschreibung"/> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<xsl:value-of select="$FRBRuri-konsolidierte-fassung-expression-aufbau"/>". Erwartet würde hier konkret: "<xsl:value-of select="$FRBRuri-konsolidierte-fassung-expression-inhalt"/>".</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e1446')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRManifestation/akn:FRBRuri"
                 priority="74"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e1446']">
            <schxslt:rule pattern="d15e1446">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00550 for context "/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRManifestation/akn:FRBRuri" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00550">
                  <xsl:attribute name="context">/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRManifestation/akn:FRBRuri</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e1446">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00550">
                  <xsl:attribute name="context">/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRManifestation/akn:FRBRuri</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($ist-entwurfsfassung) then (@value = $FRBRuri-entwurfsfassung-manifestation-inhalt) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00550-005">
                     <xsl:attribute name="test">if ($ist-entwurfsfassung) then (@value = $FRBRuri-entwurfsfassung-manifestation-inhalt) else true()</xsl:attribute>
                     <svrl:text>
                        <xsl:value-of select="$FRBRuri-entwurfsfassung-manifestation-beschreibung"/> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<xsl:value-of select="$FRBRuri-entwurfsfassung-manifestation-aufbau"/>". Erwartet würde hier konkret: "<xsl:value-of select="$FRBRuri-entwurfsfassung-manifestation-inhalt"/>".</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(if ($ist-verkündungsfassung) then (@value = $FRBRuri-verkündungsfassung-manifestation-inhalt) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00550-010">
                     <xsl:attribute name="test">if ($ist-verkündungsfassung) then (@value = $FRBRuri-verkündungsfassung-manifestation-inhalt) else true()</xsl:attribute>
                     <svrl:text>
                        <xsl:value-of select="$FRBRuri-verkündungsfassung-manifestation-beschreibung"/> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<xsl:value-of select="$FRBRuri-verkündungsfassung-manifestation-aufbau"/>". Erwartet würde hier konkret: "<xsl:value-of select="$FRBRuri-verkündungsfassung-manifestation-inhalt"/>".</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(if ($ist-konsolidierte-fassung) then (@value = $FRBRuri-konsolidierte-fassung-manifestation-inhalt) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00550-015">
                     <xsl:attribute name="test">if ($ist-konsolidierte-fassung) then (@value = $FRBRuri-konsolidierte-fassung-manifestation-inhalt) else true()</xsl:attribute>
                     <svrl:text>
                        <xsl:value-of select="$FRBRuri-konsolidierte-fassung-manifestation-beschreibung"/> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<xsl:value-of select="$FRBRuri-konsolidierte-fassung-manifestation-aufbau"/>". Erwartet würde hier konkret: "<xsl:value-of select="$FRBRuri-konsolidierte-fassung-manifestation-inhalt"/>".</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(starts-with(@value, $authority-egesetzgebung) or starts-with(@value, $authority-everkündung) or starts-with(@value, $authority-rechtsinformationssystem))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00550-020">
                     <xsl:attribute name="test">starts-with(@value, $authority-egesetzgebung) or starts-with(@value, $authority-everkündung) or starts-with(@value, $authority-rechtsinformationssystem)</xsl:attribute>
                     <svrl:text>Der Manifestation-ELI muss zwingend mit einer der drei zulässigen authorities beginnen: "<xsl:value-of select="$authority-egesetzgebung"/>", "<xsl:value-of select="$authority-everkündung"/>" oder "<xsl:value-of select="$authority-rechtsinformationssystem"/>".</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e1446')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="/akn:akomaNtoso/akn:act/@name" priority="73" mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e1446']">
            <schxslt:rule pattern="d15e1446">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00560 for context "/akn:akomaNtoso/akn:act/@name" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00560" role="error">
                  <xsl:attribute name="context">/akn:akomaNtoso/akn:act/@name</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e1446">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00560" role="error">
                  <xsl:attribute name="context">/akn:akomaNtoso/akn:act/@name</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if (. = 'regelungstext') then $ist-verkündungsfassung or $ist-konsolidierte-fassung else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00560-005">
                     <xsl:attribute name="test">if (. = 'regelungstext') then $ist-verkündungsfassung or $ist-konsolidierte-fassung else true()</xsl:attribute>
                     <svrl:text> Ein Regelungstext in der Verkündungsfassung darf nicht als Entwurfsfassung gekennzeichnet sein, wie es jedoch aktuell anhand von akn:FRBRWork/akn:FRBRdate/@name deklariert ist.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e1446')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:meta/akn:proprietary/redok:legalDocML.de_metadaten"
                 priority="72"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e1565']">
            <schxslt:rule pattern="d15e1565">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00590 for context "akn:meta/akn:proprietary/redok:legalDocML.de_metadaten" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00590">
                  <xsl:attribute name="context">akn:meta/akn:proprietary/redok:legalDocML.de_metadaten</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e1565">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00590">
                  <xsl:attribute name="context">akn:meta/akn:proprietary/redok:legalDocML.de_metadaten</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($ist-entwurfsfassung) then (redok:fna = 'nicht-vorhanden' (: das Literal ist im Metadatenmodell als @default des einfachen Typs xs:token umgesetzt, daher hier als Literal anstatt einer dynamischen Referenzierung :)) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(redok:fna)}"
                                      id="SCH-00590-000">
                     <xsl:attribute name="test">if ($ist-entwurfsfassung) then (redok:fna = 'nicht-vorhanden' (: das Literal ist im Metadatenmodell als @default des einfachen Typs xs:token umgesetzt, daher hier als Literal anstatt einer dynamischen Referenzierung :)) else true()</xsl:attribute>
                     <svrl:text>In der Entwurfsfassung muss als Wert für den Fundstellennachweis das Literal "nicht-vorhanden" angegeben werden.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e1565')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:meta[$teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/rechtsetzungsdokument']"
                 priority="71"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e1565']">
            <schxslt:rule pattern="d15e1565">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00591 for context "akn:meta[$teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/rechtsetzungsdokument']" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00591">
                  <xsl:attribute name="context">akn:meta[$teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/rechtsetzungsdokument']</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e1565">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00591">
                  <xsl:attribute name="context">akn:meta[$teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/rechtsetzungsdokument']</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(count(akn:proprietary/redok:legalDocML.de_metadaten) eq 1)">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00591-000">
                     <xsl:attribute name="test">count(akn:proprietary/redok:legalDocML.de_metadaten) eq 1</xsl:attribute>
                     <svrl:text>Ein Rechtsetzungsdokument muss genau einen Block mit "Metadaten Rechtsetzungsdokument" besitzen.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(if ($ist-entwurfsfassung and $bearbeitende-institution-frbrauthor = 'recht.bund.de/institution/bundestag') then (count(akn:proprietary/btag:legalDocML.de_metadaten) eq 1) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00591-005">
                     <xsl:attribute name="test">if ($ist-entwurfsfassung and $bearbeitende-institution-frbrauthor = 'recht.bund.de/institution/bundestag') then (count(akn:proprietary/btag:legalDocML.de_metadaten) eq 1) else true()</xsl:attribute>
                     <svrl:text>Wenn der Bundestag bearbeitende Institution ist, müssen dessen Metadaten genau einmal angegeben werden.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(if ($ist-entwurfsfassung and $bearbeitende-institution-frbrauthor = 'recht.bund.de/institution/bundesrat') then (count(akn:proprietary/brat:legalDocML.de_metadaten) eq 1) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00591-010">
                     <xsl:attribute name="test">if ($ist-entwurfsfassung and $bearbeitende-institution-frbrauthor = 'recht.bund.de/institution/bundesrat') then (count(akn:proprietary/brat:legalDocML.de_metadaten) eq 1) else true()</xsl:attribute>
                     <svrl:text>Wenn der Bundesrat bearbeitende Institution ist, müssen dessen Metadaten genau einmal angegeben werden.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(if ($ist-entwurfsfassung and $bearbeitende-institution-frbrauthor = 'recht.bund.de/institution/bundesregierung') then (count(akn:proprietary/breg:legalDocML.de_metadaten) eq 1) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00591-015">
                     <xsl:attribute name="test">if ($ist-entwurfsfassung and $bearbeitende-institution-frbrauthor = 'recht.bund.de/institution/bundesregierung') then (count(akn:proprietary/breg:legalDocML.de_metadaten) eq 1) else true()</xsl:attribute>
                     <svrl:text>Wenn die Bundesregierung bearbeitende Institution ist, müssen deren Metadaten genau einmal angegeben werden.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e1565')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:meta[$teildokument-uri = ('/akn/ontology/de/concept/documenttype/bund/regelungstext-entwurf', '/akn/ontology/de/concept/documenttype/bund/regelungstext')]"
                 priority="70"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e1565']">
            <schxslt:rule pattern="d15e1565">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00592 for context "akn:meta[$teildokument-uri = ('/akn/ontology/de/concept/documenttype/bund/regelungstext-entwurf', '/akn/ontology/de/concept/documenttype/bund/regelungstext')]" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00592">
                  <xsl:attribute name="context">akn:meta[$teildokument-uri = ('/akn/ontology/de/concept/documenttype/bund/regelungstext-entwurf', '/akn/ontology/de/concept/documenttype/bund/regelungstext')]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e1565">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00592">
                  <xsl:attribute name="context">akn:meta[$teildokument-uri = ('/akn/ontology/de/concept/documenttype/bund/regelungstext-entwurf', '/akn/ontology/de/concept/documenttype/bund/regelungstext')]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(count(akn:proprietary/regtxt:legalDocML.de_metadaten) eq 1)">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00592-000">
                     <xsl:attribute name="test">count(akn:proprietary/regtxt:legalDocML.de_metadaten) eq 1</xsl:attribute>
                     <svrl:text>Ein Regelungstext muss über genau einen Metadatenblock Regelungstext verfügen.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e1565')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:meta[$teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/nkr-stellungnahme']"
                 priority="69"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e1565']">
            <schxslt:rule pattern="d15e1565">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00593 for context "akn:meta[$teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/nkr-stellungnahme']" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00593">
                  <xsl:attribute name="context">akn:meta[$teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/nkr-stellungnahme']</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e1565">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00593">
                  <xsl:attribute name="context">akn:meta[$teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/nkr-stellungnahme']</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(count(akn:proprietary/nkr:legalDocML.de_metadaten) eq 1)">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00593-000">
                     <xsl:attribute name="test">count(akn:proprietary/nkr:legalDocML.de_metadaten) eq 1</xsl:attribute>
                     <svrl:text>Die NKR-Stellungnahme muss genau einen Metadatenblock Normenkontrollrat enthalten.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e1565')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:meta[$teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/sonstiger-veroeffentlichungstext']"
                 priority="68"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e1565']">
            <schxslt:rule pattern="d15e1565">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00595 for context "akn:meta[$teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/sonstiger-veroeffentlichungstext']" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00595">
                  <xsl:attribute name="context">akn:meta[$teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/sonstiger-veroeffentlichungstext']</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e1565">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00595">
                  <xsl:attribute name="context">akn:meta[$teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/sonstiger-veroeffentlichungstext']</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(count(akn:proprietary/sonst:legalDocML.de_metadaten) eq 1)">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00595-000">
                     <xsl:attribute name="test">count(akn:proprietary/sonst:legalDocML.de_metadaten) eq 1</xsl:attribute>
                     <svrl:text>Der Sonstige Veröffentlichungstext muss genau einen Metadatenblock Sonstiger Veröffentlichungstext enthalten.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e1565')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:meta" priority="67" mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e1625']">
            <schxslt:rule pattern="d15e1625">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00594 for context "akn:meta" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00594">
                  <xsl:attribute name="context">akn:meta</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e1625">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00594">
                  <xsl:attribute name="context">akn:meta</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if (akn:proprietary/btag:legalDocML.de_metadaten) then ($teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/rechtsetzungsdokument') else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00594-000">
                     <xsl:attribute name="test">if (akn:proprietary/btag:legalDocML.de_metadaten) then ($teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/rechtsetzungsdokument') else true()</xsl:attribute>
                     <svrl:text>Das Metadatenschema Bundestag darf nur im Rechtsetzungsdokument eingebunden werden.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(if (akn:proprietary/brat:legalDocML.de_metadaten) then ($teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/rechtsetzungsdokument') else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00594-005">
                     <xsl:attribute name="test">if (akn:proprietary/brat:legalDocML.de_metadaten) then ($teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/rechtsetzungsdokument') else true()</xsl:attribute>
                     <svrl:text>Das Metadatenschema Bundesrat darf nur im Rechtsetzungsdokument eingebunden werden.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(if (akn:proprietary/breg:legalDocML.de_metadaten) then ($teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/rechtsetzungsdokument') else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00594-010">
                     <xsl:attribute name="test">if (akn:proprietary/breg:legalDocML.de_metadaten) then ($teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/rechtsetzungsdokument') else true()</xsl:attribute>
                     <svrl:text>Das Metadatenschema Bundesregierung darf nur im Rechtsetzungsdokument eingebunden werden.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(if (akn:proprietary/nkr:legalDocML.de_metadaten) then ($teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/nkr-stellungnahme') else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00594-015">
                     <xsl:attribute name="test">if (akn:proprietary/nkr:legalDocML.de_metadaten) then ($teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/nkr-stellungnahme') else true()</xsl:attribute>
                     <svrl:text>Das Metadatenschema Normenkontrollrat darf nur in der NKR-Stellungnahme eingebunden werden.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(if (akn:proprietary/redok:legalDocML.de_metadaten) then ($teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/rechtsetzungsdokument') else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00594-020">
                     <xsl:attribute name="test">if (akn:proprietary/redok:legalDocML.de_metadaten) then ($teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/rechtsetzungsdokument') else true()</xsl:attribute>
                     <svrl:text>Das Metadatenschema Rechtsetzungsdokument darf nur im Rechtsetzungsdokument eingebunden werden.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(if (akn:proprietary/fhilf:legalDocML.de_metadaten) then ($teildokument-uri = ('/akn/ontology/de/concept/documenttype/bund/begruendung-regelungstext', '/akn/ontology/de/concept/documenttype/bund/regelungstext-entwurf', '/akn/ontology/de/concept/documenttype/bund/vorblatt-regelungstext')) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00594-025">
                     <xsl:attribute name="test">if (akn:proprietary/fhilf:legalDocML.de_metadaten) then ($teildokument-uri = ('/akn/ontology/de/concept/documenttype/bund/begruendung-regelungstext', '/akn/ontology/de/concept/documenttype/bund/regelungstext-entwurf', '/akn/ontology/de/concept/documenttype/bund/vorblatt-regelungstext')) else true()</xsl:attribute>
                     <svrl:text>Das Metadatenschema Formulierungshilfe darf nur in einer Begründung zu einem Regelungstext, in einem Regelungstext (Entwurf) oder dem Vorblatt zu einem Regelungstext eingebunden werden.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(if (akn:proprietary/regtxt:legalDocML.de_metadaten) then ($teildokument-uri = ('/akn/ontology/de/concept/documenttype/bund/regelungstext-entwurf', '/akn/ontology/de/concept/documenttype/bund/regelungstext')) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00594-030">
                     <xsl:attribute name="test">if (akn:proprietary/regtxt:legalDocML.de_metadaten) then ($teildokument-uri = ('/akn/ontology/de/concept/documenttype/bund/regelungstext-entwurf', '/akn/ontology/de/concept/documenttype/bund/regelungstext')) else true()</xsl:attribute>
                     <svrl:text>Das Metadatenschema Regelungstext darf nur in einem Regelungstext (Entwurf) oder einem Regelungstext eingebunden werden.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(if (akn:proprietary/sonst:legalDocML.de_metadaten) then ($teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/sonstiger-veroeffentlichungstext') else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00594-035">
                     <xsl:attribute name="test">if (akn:proprietary/sonst:legalDocML.de_metadaten) then ($teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/sonstiger-veroeffentlichungstext') else true()</xsl:attribute>
                     <svrl:text>Das Metadatenschema Sonstiger-Veröffentlichungstext darf nur in einem Sonstigen Veröffentlichungstext eingebunden werden.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e1625')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:timeInterval" priority="66" mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:variable name="beginn-geltungszeitintervall-uri" select="@start"/>
      <xsl:variable name="ende-geltungszeitintervall-uri" select="@end"/>
      <xsl:variable name="beginn-geltungszeitintervall"
                    select="ancestor::akn:meta/akn:lifecycle/akn:eventRef[@eId = substring($beginn-geltungszeitintervall-uri, 2)]/@date"/>
      <xsl:variable name="ende-geltungszeitintervall"
                    select="ancestor::akn:meta/akn:lifecycle/akn:eventRef[@eId = substring($ende-geltungszeitintervall-uri, 2)]/@date"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e1676']">
            <schxslt:rule pattern="d15e1676">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00600 for context "akn:timeInterval" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00600">
                  <xsl:attribute name="context">akn:timeInterval</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e1676">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00600">
                  <xsl:attribute name="context">akn:timeInterval</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(not(exists(@end) and exists(@duration)))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00600-000">
                     <xsl:attribute name="test">not(exists(@end) and exists(@duration))</xsl:attribute>
                     <svrl:text>Die Attribute @end und @duration schließen einander aus: Es darf nur entweder eine Dauer oder ein Endzeitpunkt angegeben werden, aber nicht beides.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(substring($beginn-geltungszeitintervall-uri, 2) = /akn:akomaNtoso/*/akn:meta/akn:lifecycle/akn:eventRef/@eId)">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00600-005">
                     <xsl:attribute name="test">substring($beginn-geltungszeitintervall-uri, 2) = /akn:akomaNtoso/*/akn:meta/akn:lifecycle/akn:eventRef/@eId</xsl:attribute>
                     <svrl:text>Der Verweis auf den Beginn des Geltungsintervalls muss auf eine in der vorliegenden Instanz vorhandene @eId einer Ereignis-Deklaration sein. Für den Verweis "<xsl:value-of select="$beginn-geltungszeitintervall-uri"/>" existiert jedoch kein passendes Verweisziel.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(if (not(empty(@end))) then (substring($ende-geltungszeitintervall-uri, 2) = /akn:akomaNtoso/*/akn:meta/akn:lifecycle/akn:eventRef/@eId) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00600-010">
                     <xsl:attribute name="test">if (not(empty(@end))) then (substring($ende-geltungszeitintervall-uri, 2) = /akn:akomaNtoso/*/akn:meta/akn:lifecycle/akn:eventRef/@eId) else true()</xsl:attribute>
                     <svrl:text>Der Verweis auf das Ende des Geltungsintervalls muss auf eine in der vorliegenden Instanz vorhandene @eId einer Ereignis-Deklaration sein. Für den Verweis "<xsl:value-of select="$ende-geltungszeitintervall-uri"/>" existiert jedoch kein passendes Verweisziel.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(if (not(empty($beginn-geltungszeitintervall)) and not(empty($ende-geltungszeitintervall))) then (xs:date($beginn-geltungszeitintervall) le xs:date($ende-geltungszeitintervall)) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00600-015">
                     <xsl:attribute name="test">if (not(empty($beginn-geltungszeitintervall)) and not(empty($ende-geltungszeitintervall))) then (xs:date($beginn-geltungszeitintervall) le xs:date($ende-geltungszeitintervall)) else true()</xsl:attribute>
                     <svrl:text>Das Ende des Geltungszeitintervalls darf zeitlich nicht vor seinem Beginn liegen. Es sind jedoch als Beginn "<xsl:value-of select="$beginn-geltungszeitintervall"/>" und als Ende "<xsl:value-of select="$ende-geltungszeitintervall"/>" angegeben.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e1676')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="(akn:activeModifications | akn:passiveModifications)/akn:textualMod/akn:force | akn:article[@period] | akn:paragraph[@period] | akn:list[@period]"
                 priority="65"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:variable name="verweis-auf-geltungszeitgruppe" select="substring(@period, 2)"/>
      <xsl:variable name="geltungszeitgruppen-im-dokument"
                    select="/akn:akomaNtoso/*/akn:meta/akn:temporalData/akn:temporalGroup/@eId"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e1676']">
            <schxslt:rule pattern="d15e1676">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00610 for context "(akn:activeModifications | akn:passiveModifications)/akn:textualMod/akn:force | akn:article[@period] | akn:paragraph[@period] | akn:list[@period]" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00610">
                  <xsl:attribute name="context">(akn:activeModifications | akn:passiveModifications)/akn:textualMod/akn:force | akn:article[@period] | akn:paragraph[@period] | akn:list[@period]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e1676">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00610">
                  <xsl:attribute name="context">(akn:activeModifications | akn:passiveModifications)/akn:textualMod/akn:force | akn:article[@period] | akn:paragraph[@period] | akn:list[@period]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not($verweis-auf-geltungszeitgruppe = $geltungszeitgruppen-im-dokument)">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00610-000">
                     <xsl:attribute name="test">$verweis-auf-geltungszeitgruppe = $geltungszeitgruppen-im-dokument</xsl:attribute>
                     <svrl:text>Der angegebene Wert "<xsl:value-of select="concat('#', $verweis-auf-geltungszeitgruppe)"/>" ist kein gültiger Verweis auf eine Geltungszeitgruppe in diesem Dokument. <xsl:value-of select="if (not(empty($geltungszeitgruppen-im-dokument))) then if (count($geltungszeitgruppen-im-dokument) gt 2) then concat(' Technisch mögliche Angaben wären im aktuellen Dokument: ', (string-join( for $geltungszeitgruppe in $geltungszeitgruppen-im-dokument[position() lt last()] return concat('&#34;#', $geltungszeitgruppe, '&#34;'), ', ')), ' oder &#34;#', $geltungszeitgruppen-im-dokument[last()], '&#34;.') else concat(' Die technisch einzig mögliche Angabe ist im aktuellen Dokument &#34;#', $geltungszeitgruppen-im-dokument, '&#34;.') else ()"/>
                     </svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e1676')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:eventRef" priority="64" mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:variable name="ereignisart" select="@refersTo"/>
      <xsl:variable name="ereignisdatum" select="@date"/>
      <xsl:variable name="literalsuffix-für-unbekanntes-datum"
                    select="'-mit-noch-unbekanntem-datum'"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e1676']">
            <schxslt:rule pattern="d15e1676">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00620 for context "akn:eventRef" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00620">
                  <xsl:attribute name="context">akn:eventRef</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e1676">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00620">
                  <xsl:attribute name="context">akn:eventRef</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($ereignisdatum = $platzhalter-datum-unbekannt) then (ends-with($ereignisart, $literalsuffix-für-unbekanntes-datum)) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00620-000">
                     <xsl:attribute name="test">if ($ereignisdatum = $platzhalter-datum-unbekannt) then (ends-with($ereignisart, $literalsuffix-für-unbekanntes-datum)) else true()</xsl:attribute>
                     <svrl:text>Bei Angabe eines unbekannten Ereignisdatums (durch Verwendung des Platzhaltertextes "<xsl:value-of select="$platzhalter-datum-unbekannt"/>") muss das zur näheren Bestimmung der Ereignisart im Attribut @refersTo angegebene Literal auf den Ausdruck "<xsl:value-of select="$literalsuffix-für-unbekanntes-datum"/>" enden.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(if (ends-with($ereignisart, $literalsuffix-für-unbekanntes-datum)) then ($ereignisdatum = $platzhalter-datum-unbekannt) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00620-005">
                     <xsl:attribute name="test">if (ends-with($ereignisart, $literalsuffix-für-unbekanntes-datum)) then ($ereignisdatum = $platzhalter-datum-unbekannt) else true()</xsl:attribute>
                     <svrl:text>Für ein Ereignis, dessen Datum noch unbekannt ist (hier: @refersTo = "<xsl:value-of select="$ereignisart"/>"), muss in seinem Attribut @date als Platzhalterwert "<xsl:value-of select="$platzhalter-datum-unbekannt"/>" angegeben werden.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e1676')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:FRBRExpression" priority="63" mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:variable name="guid-vorherige-version" select="'vorherige-version-id'"/>
      <xsl:variable name="guid-aktuelle-version" select="'aktuelle-version-id'"/>
      <xsl:variable name="guid-nächste-version" select="'nachfolgende-version-id'"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e1750']">
            <schxslt:rule pattern="d15e1750">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00630 for context "akn:FRBRExpression" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00630">
                  <xsl:attribute name="context">akn:FRBRExpression</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e1750">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00630">
                  <xsl:attribute name="context">akn:FRBRExpression</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(count(akn:FRBRalias[@name = $guid-vorherige-version]) le 1)">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(akn:FRBRalias[@name = $guid-vorherige-version])}"
                                      id="SCH-00630-000">
                     <xsl:attribute name="test">count(akn:FRBRalias[@name = $guid-vorherige-version]) le 1</xsl:attribute>
                     <svrl:text>Es darf höchstens einen einzigen Verweis auf eine Vorgängerversion geben.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(count(akn:FRBRalias[@name = $guid-aktuelle-version]) eq 1)">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(akn:FRBRalias[@name = $guid-aktuelle-version])}"
                                      id="SCH-00630-005">
                     <xsl:attribute name="test">count(akn:FRBRalias[@name = $guid-aktuelle-version]) eq 1</xsl:attribute>
                     <svrl:text>Es muss genau einen Identifikator (GUID) für die vorliegende Version geben.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(count(akn:FRBRalias) eq count(distinct-values(akn:FRBRalias/@value)))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(akn:FRBRalias)}"
                                      id="SCH-00630-015">
                     <xsl:attribute name="test">count(akn:FRBRalias) eq count(distinct-values(akn:FRBRalias/@value))</xsl:attribute>
                     <svrl:text>Sämtliche mittels @value angegebenen GUIDs der Versionen müssen sich voneinander unterscheiden."/&gt;</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e1750')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:meta/akn:lifecycle" priority="62" mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e1774']">
            <schxslt:rule pattern="d15e1774">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00640 for context "akn:meta/akn:lifecycle" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00640">
                  <xsl:attribute name="context">akn:meta/akn:lifecycle</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e1774">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00640">
                  <xsl:attribute name="context">akn:meta/akn:lifecycle</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(count(akn:eventRef[@type = $type-literal-ereignisreferenz-repeal and @refersTo = $zulässige-literale-in-kombination-mit-repeal]) le 1)">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00640-010">
                     <xsl:attribute name="test">count(akn:eventRef[@type = $type-literal-ereignisreferenz-repeal and @refersTo = $zulässige-literale-in-kombination-mit-repeal]) le 1</xsl:attribute>
                     <svrl:text>
                        <xsl:value-of select="$dokumentarten-mit-lebenszyklus-angaben-formulierung-satzanfang-nominativ"/> kann nicht mehr als ein Außerkraftsetzen (&lt;eventRef;&gt; mit @type='<xsl:value-of select="$type-literal-ereignisreferenz-repeal"/>') enthalten, da das Rechtsetzungsartefakt dadurch in Gänze aufgehoben wird.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(count(akn:eventRef[@type = $type-literal-ereignisreferenz-repeal and @refersTo = $zulässige-literale-in-kombination-mit-repeal]) le 1)">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00640-015">
                     <xsl:attribute name="test">count(akn:eventRef[@type = $type-literal-ereignisreferenz-repeal and @refersTo = $zulässige-literale-in-kombination-mit-repeal]) le 1</xsl:attribute>
                     <svrl:text>Für die Deklaration eines Außerkrafttretens (&lt;eventRef&gt; mit @type='<xsl:value-of select="$type-literal-ereignisreferenz-repeal"/>') ist als @refersTo-Angabe ausschließlich <xsl:value-of select="string-join(distinct-values(for $literal in $zulässige-literale-in-kombination-mit-repeal return concat('''', $literal, '''')), ' oder ')"/> zulässig.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e1774')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:meta/akn:lifecycle[$ist-entwurfsfassung]"
                 priority="61"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e1796']">
            <schxslt:rule pattern="d15e1796">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00650 for context "akn:meta/akn:lifecycle[$ist-entwurfsfassung]" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00650">
                  <xsl:attribute name="context">akn:meta/akn:lifecycle[$ist-entwurfsfassung]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e1796">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00650">
                  <xsl:attribute name="context">akn:meta/akn:lifecycle[$ist-entwurfsfassung]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($teildokument-uri = ($art-regelungstext-uri, $art-vereinbarung-uri, $art-bekanntmachungstext-uri)) then (akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-entwurfsfassung-ausfertigung-mit-unbekanntem-datum] and akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-entwurfsfassung-verkuendung-mit-unbekanntem-datum] and (akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-entwurfsfassung-inkrafttreten-mit-unbekanntem-datum] or akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-entwurfsfassung-inkrafttreten])) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00650-000">
                     <xsl:attribute name="test">if ($teildokument-uri = ($art-regelungstext-uri, $art-vereinbarung-uri, $art-bekanntmachungstext-uri)) then (akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-entwurfsfassung-ausfertigung-mit-unbekanntem-datum] and akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-entwurfsfassung-verkuendung-mit-unbekanntem-datum] and (akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-entwurfsfassung-inkrafttreten-mit-unbekanntem-datum] or akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-entwurfsfassung-inkrafttreten])) else true()</xsl:attribute>
                     <svrl:text>
                        <xsl:value-of select="$dokumentarten-mit-lebenszyklus-angaben-formulierung-satzanfang-nominativ"/> in der Entwurfsfassung muss immer mindestens drei Ereignisse auszeichnen: Erstens einen Platzhalter für das Ausfertigungsdatum; dieser wird angegeben mittels &lt;eventRef&gt; mit @type='<xsl:value-of select="$type-literal-ereignisreferenz-generation"/>' und @refersTo='<xsl:value-of select="$refersto-literal-ereignisreferenz-entwurfsfassung-ausfertigung-mit-unbekanntem-datum"/>'. Zweitens einen Platzhalter für das Verkündungsdatum; dieser wird angegeben mittels &lt;eventRef&gt; mit @type='<xsl:value-of select="$type-literal-ereignisreferenz-generation"/>' und @refersTo='<xsl:value-of select="$refersto-literal-ereignisreferenz-entwurfsfassung-verkuendung-mit-unbekanntem-datum"/>'. Und drittens eine Angabe zum Inkrafttreten mittels &lt;eventRef&gt; mit @type='<xsl:value-of select="$type-literal-ereignisreferenz-generation"/> und @refersTo='<xsl:value-of select="$refersto-literal-ereignisreferenz-entwurfsfassung-inkrafttreten-mit-unbekanntem-datum"/>' bzw., sofern das Datum bereits bekannt ist, @refersTo='<xsl:value-of select="$refersto-literal-ereignisreferenz-entwurfsfassung-inkrafttreten"/>'.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(if ($ist-entwurfsfassung) then (akn:eventRef[@type = ($type-literal-ereignisreferenz-generation, $type-literal-ereignisreferenz-repeal)]) else ())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00650-005">
                     <xsl:attribute name="test">if ($ist-entwurfsfassung) then (akn:eventRef[@type = ($type-literal-ereignisreferenz-generation, $type-literal-ereignisreferenz-repeal)]) else ()</xsl:attribute>
                     <svrl:text>
                        <xsl:value-of select="$dokumentarten-mit-lebenszyklus-angaben-formulierung-satzanfang-nominativ"/> in der Entwurfsfassung kann nur initiale Ereignisse (Inkrafttreten, Ausfertigung, teilweises Außerkrafttreten, d.h. @type = '<xsl:value-of select="$type-literal-ereignisreferenz-generation"/>') oder ein finales Außerkrafttreten (d.h. @type = '<xsl:value-of select="$type-literal-ereignisreferenz-repeal"/>') aufweisen.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(if ($teildokument-uri = ($art-regelungstext-uri, $art-vereinbarung-uri, $art-bekanntmachungstext-uri)) then (count(akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-entwurfsfassung-ausfertigung-mit-unbekanntem-datum]) = 1) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00650-010">
                     <xsl:attribute name="test">if ($teildokument-uri = ($art-regelungstext-uri, $art-vereinbarung-uri, $art-bekanntmachungstext-uri)) then (count(akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-entwurfsfassung-ausfertigung-mit-unbekanntem-datum]) = 1) else true()</xsl:attribute>
                     <svrl:text>
                        <xsl:value-of select="$dokumentarten-mit-lebenszyklus-angaben-formulierung-satzanfang-nominativ"/> in der Entwurfsfassung muss genau einen Platzhalter für das noch unbekannte Datum der Ausfertigung enthalten (&lt;eventRef&gt; mit @type = '<xsl:value-of select="$type-literal-ereignisreferenz-generation"/>' und @refersTo = '<xsl:value-of select="$refersto-literal-ereignisreferenz-entwurfsfassung-ausfertigung-mit-unbekanntem-datum"/>').</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(if ($teildokument-uri = ($art-regelungstext-uri)) then (count(akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-entwurfsfassung-verkuendung-mit-unbekanntem-datum]) = 1) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00650-015">
                     <xsl:attribute name="test">if ($teildokument-uri = ($art-regelungstext-uri)) then (count(akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-entwurfsfassung-verkuendung-mit-unbekanntem-datum]) = 1) else true()</xsl:attribute>
                     <svrl:text>
                        <xsl:value-of select="$dokumentarten-mit-lebenszyklus-angaben-formulierung-satzanfang-nominativ"/> in der Entwurfsfassung muss genau einen Platzhalter für das noch unbekannte Datum der Verkündung enthalten (&lt;eventRef&gt; mit @type = '<xsl:value-of select="$type-literal-ereignisreferenz-generation"/>' und @refersTo = '<xsl:value-of select="$refersto-literal-ereignisreferenz-entwurfsfassung-verkuendung-mit-unbekanntem-datum"/>').</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e1796')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:meta/akn:lifecycle[$ist-verkündungsfassung or $ist-konsolidierte-fassung]"
                 priority="60"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:variable name="datum-ausfertigung"
                    select="(akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-ausfertigung]) (: immer den ersten Wert nehmen, falls es unerwartet mehrere gibt, damit hier diejenige SCH-Regel zum Tragen kommt, die prüft, dass es nur genau ein initiales Ausfertigungsdatum gibt, und nicht ein Fehler auf Ebene des SCH-Prozessors die weitere Verarbeitung blockiert. :)[1]/@date"/>
      <xsl:variable name="frühestes-datum-inkrafttreten-als-reine-ziffern"
                    select="min(for $n in akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten]/@date return format-date($n, '[Y,4][M,2][D,2]'))"/>
      <xsl:variable name="datum-inkrafttreten"
                    select="if (not(empty($frühestes-datum-inkrafttreten-als-reine-ziffern))) then (xs:date(concat( substring($frühestes-datum-inkrafttreten-als-reine-ziffern, 1, 4), '-', substring($frühestes-datum-inkrafttreten-als-reine-ziffern, 5, 2), '-', substring($frühestes-datum-inkrafttreten-als-reine-ziffern, 7, 2)))) else '0001-01-01'"/>
      <xsl:variable name="datum-ausserkafttreten"
                    select="akn:eventRef[@type = $type-literal-ereignisreferenz-repeal and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-ausserkrafttreten]/@date"/>
      <xsl:variable name="frühestes-datum-amendment-ausfertigung-als-reine-ziffern"
                    select="min(for $n in akn:eventRef[@type = $type-literal-ereignisreferenz-amendment and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-ausfertigung]/@date return format-date($n, '[Y,4][M,2][D,2]'))"/>
      <xsl:variable name="frühestes-datum-amendment-ausfertigung"
                    select="if (not(empty($frühestes-datum-amendment-ausfertigung-als-reine-ziffern))) then (xs:date(concat( substring($frühestes-datum-amendment-ausfertigung-als-reine-ziffern, 1, 4), '-', substring($frühestes-datum-amendment-ausfertigung-als-reine-ziffern, 5, 2), '-', substring($frühestes-datum-amendment-ausfertigung-als-reine-ziffern, 7, 2)))) else '0001-01-01'"/>
      <xsl:variable name="ausfertigungsdatum-stammform"
                    select="(akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-ausfertigung]/@date)[1]"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e1850']">
            <schxslt:rule pattern="d15e1850">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00660 for context "akn:meta/akn:lifecycle[$ist-verkündungsfassung or $ist-konsolidierte-fassung]" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00660">
                  <xsl:attribute name="context">akn:meta/akn:lifecycle[$ist-verkündungsfassung or $ist-konsolidierte-fassung]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e1850">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00660">
                  <xsl:attribute name="context">akn:meta/akn:lifecycle[$ist-verkündungsfassung or $ist-konsolidierte-fassung]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-ausfertigung] and ( akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten] or akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-grundsaetzlich] or akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-abweichend] or akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-mit-unbekanntem-datum] ))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00660-000">
                     <xsl:attribute name="test">akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-ausfertigung] and ( akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten] or akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-grundsaetzlich] or akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-abweichend] or akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-mit-unbekanntem-datum] )</xsl:attribute>
                     <svrl:text>
                        <xsl:value-of select="$dokumentarten-mit-lebenszyklus-angaben-formulierung-satzanfang-nominativ"/> in der Verkündungsfassung muss immer mindestens zwei Ereignisse auszeichnen: Erstens das Ausfertigungsdatum; dieses wird angegeben mittels &lt;eventRef&gt; mit @type='<xsl:value-of select="$type-literal-ereignisreferenz-generation"/>' und @refersTo='<xsl:value-of select="$refersto-literal-ereignisreferenz-verkündungsfassung-ausfertigung"/>'. Und zweitens eine Angabe zum Inkrafttreten mittels &lt;eventRef&gt; mit @type='<xsl:value-of select="$type-literal-ereignisreferenz-generation"/> und @refersTo='<xsl:value-of select="$refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten"/>' bzw. bei grundsätzlichem oder abweichendem Inkrafttreten @refersTo='<xsl:value-of select="$refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-grundsaetzlich"/>' oder @refersTo='<xsl:value-of select="$refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-abweichend"/>'. Oder sofern das Datum noch unbekannt ist, @refersTo='<xsl:value-of select="$refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-mit-unbekanntem-datum"/>'.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(count(akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-ausfertigung]) = 1)">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00660-005">
                     <xsl:attribute name="test">count(akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-ausfertigung]) = 1</xsl:attribute>
                     <svrl:text>
                        <xsl:value-of select="$dokumentarten-mit-lebenszyklus-angaben-formulierung-satzanfang-nominativ"/> in der Verkündungsfassung muss genau ein konkretes Ausfertigungsdatum enthalten (&lt;eventRef&gt; mit @type = '<xsl:value-of select="$type-literal-ereignisreferenz-generation"/>' und @refersTo = '<xsl:value-of select="$refersto-literal-ereignisreferenz-verkündungsfassung-ausfertigung"/>').</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(if (akn:eventRef[@type = $type-literal-ereignisreferenz-repeal and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-ausserkrafttreten]) then (xs:date($datum-ausserkafttreten) gt xs:date($datum-ausfertigung)) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00660-015">
                     <xsl:attribute name="test">if (akn:eventRef[@type = $type-literal-ereignisreferenz-repeal and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-ausserkrafttreten]) then (xs:date($datum-ausserkafttreten) gt xs:date($datum-ausfertigung)) else true()</xsl:attribute>
                     <svrl:text>Das Datum des Außerkrafttretens muss nach der Ausfertigung liegen; angegeben wurden jedoch für das Außerkrafttreten '<xsl:value-of select="$datum-ausserkafttreten"/>' und für die Ausfertigung '<xsl:value-of select="$datum-ausfertigung"/>'.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(if (not(xs:date($frühestes-datum-amendment-ausfertigung) = xs:date('0001-01-01'))) then (xs:date($frühestes-datum-amendment-ausfertigung) ge xs:date($ausfertigungsdatum-stammform)) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00660-020">
                     <xsl:attribute name="test">if (not(xs:date($frühestes-datum-amendment-ausfertigung) = xs:date('0001-01-01'))) then (xs:date($frühestes-datum-amendment-ausfertigung) ge xs:date($ausfertigungsdatum-stammform)) else true()</xsl:attribute>
                     <svrl:text>Das initiale Ausfertigungsdatum der Stammform kann nicht hinter dem Ausfertigungsdatum des Änderungsgesetzes liegen.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e1850')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:meta/akn:lifecycle/akn:eventRef [($ist-verkündungsfassung or $ist-konsolidierte-fassung) and @type = $type-literal-ereignisreferenz-generation ]"
                 priority="59"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e1910']">
            <schxslt:rule pattern="d15e1910">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00665 for context "akn:meta/akn:lifecycle/akn:eventRef [($ist-verkündungsfassung or $ist-konsolidierte-fassung) and @type = $type-literal-ereignisreferenz-generation ]" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00665">
                  <xsl:attribute name="context">akn:meta/akn:lifecycle/akn:eventRef [($ist-verkündungsfassung or $ist-konsolidierte-fassung) and @type = $type-literal-ereignisreferenz-generation ]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e1910">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00665">
                  <xsl:attribute name="context">akn:meta/akn:lifecycle/akn:eventRef [($ist-verkündungsfassung or $ist-konsolidierte-fassung) and @type = $type-literal-ereignisreferenz-generation ]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if (@refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-grundsaetzlich) then not( preceding-sibling::akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-abweichend]/@date eq current()/@date or following-sibling::akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-abweichend]/@date eq current()/@date ) else if (@refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-abweichend) then not( preceding-sibling::akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-grundsaetzlich]/@date eq current()/@date or following-sibling::akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-grundsaetzlich]/@date eq current()/@date ) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00665-025">
                     <xsl:attribute name="test">if (@refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-grundsaetzlich) then not( preceding-sibling::akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-abweichend]/@date eq current()/@date or following-sibling::akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-abweichend]/@date eq current()/@date ) else if (@refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-abweichend) then not( preceding-sibling::akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-grundsaetzlich]/@date eq current()/@date or following-sibling::akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-grundsaetzlich]/@date eq current()/@date ) else true()</xsl:attribute>
                     <svrl:text>Das grundsätzliche und das abweichende Inkrafttretensdatum dürfen nicht identisch sein.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e1910')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:meta/akn:lifecycle[$ist-verkündungsfassung and $teildokument-uri = $art-regelungstext-uri]"
                 priority="58"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e1919']">
            <schxslt:rule pattern="d15e1919">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00667 for context "akn:meta/akn:lifecycle[$ist-verkündungsfassung and $teildokument-uri = $art-regelungstext-uri]" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00667">
                  <xsl:attribute name="context">akn:meta/akn:lifecycle[$ist-verkündungsfassung and $teildokument-uri = $art-regelungstext-uri]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e1919">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00667">
                  <xsl:attribute name="context">akn:meta/akn:lifecycle[$ist-verkündungsfassung and $teildokument-uri = $art-regelungstext-uri]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(count(akn:eventRef[@refersTo = $refersto-literal-ereignisreferenz-verkuendung]) = 1)">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00667-000">
                     <xsl:attribute name="test">count(akn:eventRef[@refersTo = $refersto-literal-ereignisreferenz-verkuendung]) = 1</xsl:attribute>
                     <svrl:text>In der Verkündungsfassung eines Regelungstextes muss genau ein &lt;eventRef&gt; mit @refersTo="<xsl:value-of select="$refersto-literal-ereignisreferenz-verkuendung"/>" vorhanden sein. </svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e1919')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:p[parent::akn:longTitle and $teildokument-uri = $art-regelungstext-uri]"
                 priority="57"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e1939']">
            <schxslt:rule pattern="d15e1939">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00680 for context "akn:p[parent::akn:longTitle and $teildokument-uri = $art-regelungstext-uri]" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00680">
                  <xsl:attribute name="context">akn:p[parent::akn:longTitle and $teildokument-uri = $art-regelungstext-uri]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e1939">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00680">
                  <xsl:attribute name="context">akn:p[parent::akn:longTitle and $teildokument-uri = $art-regelungstext-uri]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(count(akn:docTitle) = 1)">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(parent::akn:longTitle)}"
                                      id="SCH-00680-000">
                     <xsl:attribute name="test">count(akn:docTitle) = 1</xsl:attribute>
                     <svrl:text>Ein dokumentenkopfTitel (akn:longtitle) muss genau einen Dokumententitel (akn:docTitle) besitzen.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e1939')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:session" priority="56" mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e1951']">
            <schxslt:rule pattern="d15e1951">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00730 for context "akn:session" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00730">
                  <xsl:attribute name="context">akn:session</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e1951">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00730">
                  <xsl:attribute name="context">akn:session</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(starts-with(@refersTo, '#') and substring(@refersTo, 2) = //akn:organization/@eId)">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(@refersTo)}"
                                      id="SCH-00730-000">
                     <xsl:attribute name="test">starts-with(@refersTo, '#') and substring(@refersTo, 2) = //akn:organization/@eId</xsl:attribute>
                     <svrl:text>Es muss einen lokalen Verweis auf eine Organisation geben, deren Sitzung ausgezeichnet wird. Dieser besteht aus einer Raute (#), gefolgt von der @eId der betreffenden akn:organization.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e1951')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:person" priority="55" mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:variable name="referenzierte-eId" select="substring-after(@refersTo, '#')"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e1965']">
            <schxslt:rule pattern="d15e1965">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00740 for context "akn:person" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00740">
                  <xsl:attribute name="context">akn:person</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e1965">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00740">
                  <xsl:attribute name="context">akn:person</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if (@refersTo) then (count(ancestor::akn:akomaNtoso/*/akn:meta/akn:references/akn:TLCPerson[@eId = $referenzierte-eId]) = 1) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(@refersTo)}"
                                      id="SCH-00740-000">
                     <xsl:attribute name="test">if (@refersTo) then (count(ancestor::akn:akomaNtoso/*/akn:meta/akn:references/akn:TLCPerson[@eId = $referenzierte-eId]) = 1) else true()</xsl:attribute>
                     <svrl:text>In den Metadaten existiert kein korrespondierender Personenverweis mit der @eId="<xsl:value-of select="$referenzierte-eId"/>".</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(@refersTo) and ($ist-entwurfsfassung or $ist-verkündungsfassung)">
                  <svrl:successful-report xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                          location="{schxslt:location(.)}"
                                          role="warn"
                                          id="SCH-00740-005">
                     <xsl:attribute name="test">not(@refersTo) and ($ist-entwurfsfassung or $ist-verkündungsfassung)</xsl:attribute>
                     <svrl:text>Aus Kompatibilitätsgründen ist es zulässig, eine Person ohne Referenz auf eine TLCPerson anzugeben; dies sollte jedoch nur in begründeten Ausnahmefällen geschehen.</svrl:text>
                  </svrl:successful-report>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e1965')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:role" priority="54" mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:variable name="referenzierte-eId" select="substring-after(@refersTo, '#')"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e1965']">
            <schxslt:rule pattern="d15e1965">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00750 for context "akn:role" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00750">
                  <xsl:attribute name="context">akn:role</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e1965">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00750">
                  <xsl:attribute name="context">akn:role</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if (@refersTo) then (count(ancestor::akn:akomaNtoso/*/akn:meta/akn:references/akn:TLCRole[@eId = $referenzierte-eId]) = 1) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(@refersTo)}"
                                      id="SCH-00750-000">
                     <xsl:attribute name="test">if (@refersTo) then (count(ancestor::akn:akomaNtoso/*/akn:meta/akn:references/akn:TLCRole[@eId = $referenzierte-eId]) = 1) else true()</xsl:attribute>
                     <svrl:text>In den Metadaten existiert kein korrespondierender Funktionsbezeichnungsverweis mit der @eId="<xsl:value-of select="$referenzierte-eId"/>".</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(@refersTo) and ($ist-entwurfsfassung or $ist-verkündungsfassung)">
                  <svrl:successful-report xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                          location="{schxslt:location(.)}"
                                          role="warn"
                                          id="SCH-00750-005">
                     <xsl:attribute name="test">not(@refersTo) and ($ist-entwurfsfassung or $ist-verkündungsfassung)</xsl:attribute>
                     <svrl:text>Aus Kompatibilitätsgründen ist es zulässig, eine Funktionsbezeichnung ohne Referenz auf eine TLCRole anzugeben; dies sollte jedoch nur in begründeten Ausnahmefällen geschehen.</svrl:text>
                  </svrl:successful-report>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e1965')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:organization" priority="53" mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:variable name="referenzierte-eId" select="substring-after(@refersTo, '#')"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e1965']">
            <schxslt:rule pattern="d15e1965">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00760 for context "akn:organization" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00760">
                  <xsl:attribute name="context">akn:organization</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e1965">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00760">
                  <xsl:attribute name="context">akn:organization</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if (@refersTo) then (count(ancestor::akn:akomaNtoso/*/akn:meta/akn:references/akn:TLCOrganization[@eId = $referenzierte-eId]) = 1) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(@refersTo)}"
                                      id="SCH-00760-000">
                     <xsl:attribute name="test">if (@refersTo) then (count(ancestor::akn:akomaNtoso/*/akn:meta/akn:references/akn:TLCOrganization[@eId = $referenzierte-eId]) = 1) else true()</xsl:attribute>
                     <svrl:text>In den Metadaten existiert kein korrespondierender Organisationsverweis mit der @eId="<xsl:value-of select="$referenzierte-eId"/>".</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(@refersTo) and ($ist-entwurfsfassung or $ist-verkündungsfassung)">
                  <svrl:successful-report xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                          location="{schxslt:location(.)}"
                                          role="warn"
                                          id="SCH-00760-005">
                     <xsl:attribute name="test">not(@refersTo) and ($ist-entwurfsfassung or $ist-verkündungsfassung)</xsl:attribute>
                     <svrl:text>Aus Kompatibilitätsgründen ist es zulässig, eine Organisation ohne Referenz auf eine TLCPerson anzugeben; dies sollte jedoch nur in begründeten Ausnahmefällen geschehen.</svrl:text>
                  </svrl:successful-report>
               </xsl:if>
               <xsl:if test="not(if (not(@refersTo)) then (@title) else if (not(@title)) then (@refersTo) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00760-010">
                     <xsl:attribute name="test">if (not(@refersTo)) then (@title) else if (not(@title)) then (@refersTo) else true()</xsl:attribute>
                     <svrl:text>Eine akn:organization muss mindestens entweder eine Angabe @refersTo oder einen @title besitzen (oder beides).</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e1965')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:references" priority="52" mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:variable name="referenzierte-eId" select="substring-after(@source, '#')"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e1965']">
            <schxslt:rule pattern="d15e1965">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00770 for context "akn:references" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00770">
                  <xsl:attribute name="context">akn:references</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e1965">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00770">
                  <xsl:attribute name="context">akn:references</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(count(ancestor::akn:akomaNtoso/*/akn:meta/akn:references/(akn:TLCOrganization, akn:TLCPerson)[@eId = $referenzierte-eId]) = 1)">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00770-000">
                     <xsl:attribute name="test">count(ancestor::akn:akomaNtoso/*/akn:meta/akn:references/(akn:TLCOrganization, akn:TLCPerson)[@eId = $referenzierte-eId]) = 1</xsl:attribute>
                     <svrl:text>In den Metadaten existiert kein korrespondierender Akteur (Person, Organisation) mit der @eId="<xsl:value-of select="$referenzierte-eId"/>".</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e1965')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:marker[@refersTo = 'satzende']"
                 priority="51"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2023']">
            <schxslt:rule pattern="d15e2023">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00802 for context "akn:marker[@refersTo = 'satzende']" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00802">
                  <xsl:attribute name="context">akn:marker[@refersTo = 'satzende']</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2023">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00802">
                  <xsl:attribute name="context">akn:marker[@refersTo = 'satzende']</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(empty(@name))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00802-000">
                     <xsl:attribute name="test">empty(@name)</xsl:attribute>
                     <svrl:text>Ein akn:marker mit @refersTo='satzende' darf kein Attribut @name haben.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2023')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:inline/@name" priority="50" mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2023']">
            <schxslt:rule pattern="d15e2023">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00803 for context "akn:inline/@name" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00803">
                  <xsl:attribute name="context">akn:inline/@name</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2023">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00803">
                  <xsl:attribute name="context">akn:inline/@name</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if (not(parent::*/@refersTo = 'neuris')) then (. = 'attributsemantik-noch-undefiniert') else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00803-000">
                     <xsl:attribute name="test">if (not(parent::*/@refersTo = 'neuris')) then (. = 'attributsemantik-noch-undefiniert') else true()</xsl:attribute>
                     <svrl:text>Das Attribut @name darf für akn:inline nur dann mit Freitext befüllt werden, wenn @refersTo='neuris' gegeben ist.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2023')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="(akn:act | akn:bill)[akn:meta/akn:proprietary/regtxt:legalDocML.de_metadaten/regtxt:form = ($form-stammform, $form-eingebundene-stammform)]//akn:list"
                 priority="49"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2044']">
            <schxslt:rule pattern="d15e2044">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00810 for context "(akn:act | akn:bill)[akn:meta/akn:proprietary/regtxt:legalDocML.de_metadaten/regtxt:form = ($form-stammform, $form-eingebundene-stammform)]//akn:list" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00810">
                  <xsl:attribute name="context">(akn:act | akn:bill)[akn:meta/akn:proprietary/regtxt:legalDocML.de_metadaten/regtxt:form = ($form-stammform, $form-eingebundene-stammform)]//akn:list</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2044">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00810">
                  <xsl:attribute name="context">(akn:act | akn:bill)[akn:meta/akn:proprietary/regtxt:legalDocML.de_metadaten/regtxt:form = ($form-stammform, $form-eingebundene-stammform)]//akn:list</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(false())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00810-000">
                     <xsl:attribute name="test">false()</xsl:attribute>
                     <svrl:text>Das Element akn:list darf in Stammformen nicht verwendet werden.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2044')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="/akn:akomaNtoso/akn:*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRsubtype"
                 priority="48"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:variable name="teildokument-id"
                    select="tokenize(/akn:akomaNtoso/akn:*/@name, '/')[last()]"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2058']">
            <schxslt:rule pattern="d15e2058">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00820 for context "/akn:akomaNtoso/akn:*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRsubtype" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00820">
                  <xsl:attribute name="context">/akn:akomaNtoso/akn:*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRsubtype</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2058">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00820">
                  <xsl:attribute name="context">/akn:akomaNtoso/akn:*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRsubtype</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(matches(@value, concat($teildokument-id, '-\d+')))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-00820-000">
                     <xsl:attribute name="test">matches(@value, concat($teildokument-id, '-\d+'))</xsl:attribute>
                     <svrl:text>Die Teildokumentbezeichnung muss der ontologischen Teildokument-ID entsprechen; erwartet wird hier konkret "<xsl:value-of select="concat($teildokument-id, '-', tokenize(@value, '-')[last()])"/>".</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2058')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:statement/akn:conclusions/akn:blockContainer"
                 priority="47"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2075']">
            <schxslt:rule pattern="d15e2075">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00900 for context "akn:statement/akn:conclusions/akn:blockContainer" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00900">
                  <xsl:attribute name="context">akn:statement/akn:conclusions/akn:blockContainer</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2075">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00900">
                  <xsl:attribute name="context">akn:statement/akn:conclusions/akn:blockContainer</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(count(akn:p) ge 3)">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="warn"
                                      id="SCH-00900-000">
                     <xsl:attribute name="test">count(akn:p) ge 3</xsl:attribute>
                     <svrl:text>Im Schlussteil sollen mindestens drei akn:p enthalten sein (akn:p mit Ort und Datum; akn:p mit der Organisation / dem Auschuss; ein oder mehrere akn:p mit Unterschriften).</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2075')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:statement/akn:conclusions/akn:blockContainer/akn:p[1]"
                 priority="46"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2075']">
            <schxslt:rule pattern="d15e2075">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00901 for context "akn:statement/akn:conclusions/akn:blockContainer/akn:p[1]" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00901">
                  <xsl:attribute name="context">akn:statement/akn:conclusions/akn:blockContainer/akn:p[1]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2075">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00901">
                  <xsl:attribute name="context">akn:statement/akn:conclusions/akn:blockContainer/akn:p[1]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(exists(akn:location))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="warn"
                                      id="SCH-00901-010">
                     <xsl:attribute name="test">exists(akn:location)</xsl:attribute>
                     <svrl:text>Im Schlussteil soll im ersten akn:p ein Ort (akn:location) angegeben werden.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(exists(akn:date))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="warn"
                                      id="SCH-00901-020">
                     <xsl:attribute name="test">exists(akn:date)</xsl:attribute>
                     <svrl:text>Im Schlussteil soll im ersten akn:p ein Datum (akn:date) angegeben werden.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2075')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:statement/akn:conclusions/akn:blockContainer/akn:p[2]"
                 priority="45"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2075']">
            <schxslt:rule pattern="d15e2075">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00902 for context "akn:statement/akn:conclusions/akn:blockContainer/akn:p[2]" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00902">
                  <xsl:attribute name="context">akn:statement/akn:conclusions/akn:blockContainer/akn:p[2]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2075">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00902">
                  <xsl:attribute name="context">akn:statement/akn:conclusions/akn:blockContainer/akn:p[2]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(exists(akn:organization))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="warn"
                                      id="SCH-00902-000">
                     <xsl:attribute name="test">exists(akn:organization)</xsl:attribute>
                     <svrl:text>Im Schlussteil soll im zweiten akn:p ein Ort (akn:location) angegeben werden.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2075')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:statement/akn:conclusions/akn:blockContainer/akn:p[position() ge 3]"
                 priority="44"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2075']">
            <schxslt:rule pattern="d15e2075">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00903 for context "akn:statement/akn:conclusions/akn:blockContainer/akn:p[position() ge 3]" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00903">
                  <xsl:attribute name="context">akn:statement/akn:conclusions/akn:blockContainer/akn:p[position() ge 3]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2075">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00903">
                  <xsl:attribute name="context">akn:statement/akn:conclusions/akn:blockContainer/akn:p[position() ge 3]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(exists(akn:signature))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="warn"
                                      id="SCH-00903-000">
                     <xsl:attribute name="test">exists(akn:signature)</xsl:attribute>
                     <svrl:text>Im Schlussteil soll ab dem zweiten akn:p eine Signatur(akn:signature) angegeben werden.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2075')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:doc[$teildokument-uri = $art-anlage-regelungstext-uri]"
                 priority="43"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2108']">
            <schxslt:rule pattern="d15e2108">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00920 for context "akn:doc[$teildokument-uri = $art-anlage-regelungstext-uri]" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00920">
                  <xsl:attribute name="context">akn:doc[$teildokument-uri = $art-anlage-regelungstext-uri]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2108">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00920">
                  <xsl:attribute name="context">akn:doc[$teildokument-uri = $art-anlage-regelungstext-uri]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(count(akn:preface//akn:docTitle/akn:inline[@refersTo = 'anlageregelungstext-num']) eq 1 and count(akn:preface//akn:docTitle/akn:inline[@refersTo = 'anlageregelungstext-bezug']) le 1 and count(akn:preface//akn:docTitle/akn:inline[@refersTo = 'anlageregelungstext-heading']) le 1 and count(akn:preface//akn:docTitle/*) le 3)">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00920-000">
                     <xsl:attribute name="test">count(akn:preface//akn:docTitle/akn:inline[@refersTo = 'anlageregelungstext-num']) eq 1 and count(akn:preface//akn:docTitle/akn:inline[@refersTo = 'anlageregelungstext-bezug']) le 1 and count(akn:preface//akn:docTitle/akn:inline[@refersTo = 'anlageregelungstext-heading']) le 1 and count(akn:preface//akn:docTitle/*) le 3</xsl:attribute>
                     <svrl:text>Eine Anlage zu einem Regelungstext muss in ihrem Dokumententitel Angaben zur Zählbezeichnung und kann Angaben zum Bezug und zur Überschrift besitzen.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2108')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:inline/@refersTo" priority="42" mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2108']">
            <schxslt:rule pattern="d15e2108">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00921 for context "akn:inline/@refersTo" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00921">
                  <xsl:attribute name="context">akn:inline/@refersTo</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2108">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00921">
                  <xsl:attribute name="context">akn:inline/@refersTo</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if (. = ('anlageregelungstext-num', 'anlageregelungstext-bezug', 'anlageregelungstext-heading')) then ($teildokument-uri = $art-anlage-regelungstext-uri) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-00921-000">
                     <xsl:attribute name="test">if (. = ('anlageregelungstext-num', 'anlageregelungstext-bezug', 'anlageregelungstext-heading')) then ($teildokument-uri = $art-anlage-regelungstext-uri) else true()</xsl:attribute>
                     <svrl:text>Das Literal "<xsl:value-of select="."/>" darf nur innerhalb einer Anlage zu einem Regelungstext verwendet werden.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2108')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:doc[$teildokument-uri = $art-bericht-uri]/akn:conclusions/akn:blockContainer"
                 priority="41"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2132']">
            <schxslt:rule pattern="d15e2132">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00930 for context "akn:doc[$teildokument-uri = $art-bericht-uri]/akn:conclusions/akn:blockContainer" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00930">
                  <xsl:attribute name="context">akn:doc[$teildokument-uri = $art-bericht-uri]/akn:conclusions/akn:blockContainer</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2132">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00930">
                  <xsl:attribute name="context">akn:doc[$teildokument-uri = $art-bericht-uri]/akn:conclusions/akn:blockContainer</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(count(akn:p) ge 2)">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="warn"
                                      id="SCH-00930-000">
                     <xsl:attribute name="test">count(akn:p) ge 2</xsl:attribute>
                     <svrl:text>Im Schlussteil sollen mindestens zwei akn:p enthalten sein (akn:p mit Ort und Datum; optional akn:p mit der Organisation / dem Auschuss; ein oder mehrere akn:p mit Unterschriften).</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2132')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:doc[$teildokument-uri = $art-bericht-uri]/akn:conclusions/akn:blockContainer/akn:p[position() ge 2 and empty(akn:organization)]"
                 priority="40"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2132']">
            <schxslt:rule pattern="d15e2132">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00933 for context "akn:doc[$teildokument-uri = $art-bericht-uri]/akn:conclusions/akn:blockContainer/akn:p[position() ge 2 and empty(akn:organization)]" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00933">
                  <xsl:attribute name="context">akn:doc[$teildokument-uri = $art-bericht-uri]/akn:conclusions/akn:blockContainer/akn:p[position() ge 2 and empty(akn:organization)]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2132">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00933">
                  <xsl:attribute name="context">akn:doc[$teildokument-uri = $art-bericht-uri]/akn:conclusions/akn:blockContainer/akn:p[position() ge 2 and empty(akn:organization)]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(exists(akn:signature))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="warn"
                                      id="SCH-00933-000">
                     <xsl:attribute name="test">exists(akn:signature)</xsl:attribute>
                     <svrl:text>Im Schlussteil soll nach den akn:p mit Ort und Datum und ggf. akn:p mit der Organisation eine Signatur(akn:signature) angegeben werden.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2132')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="/akn:akomaNtoso/*/akn:meta/akn:proprietary/sonst:legalDocML.de_metadaten"
                 priority="39"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2151']">
            <schxslt:rule pattern="d15e2151">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00940 for context "/akn:akomaNtoso/*/akn:meta/akn:proprietary/sonst:legalDocML.de_metadaten" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00940">
                  <xsl:attribute name="context">/akn:akomaNtoso/*/akn:meta/akn:proprietary/sonst:legalDocML.de_metadaten</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2151">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00940">
                  <xsl:attribute name="context">/akn:akomaNtoso/*/akn:meta/akn:proprietary/sonst:legalDocML.de_metadaten</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if (sonst:typ = 'berichtigung') then (exists(sonst:bezugstyp)) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(sonst:typ)}"
                                      role="error"
                                      id="SCH-00940-000">
                     <xsl:attribute name="test">if (sonst:typ = 'berichtigung') then (exists(sonst:bezugstyp)) else true()</xsl:attribute>
                     <svrl:text>Wenn im Metadatenschema "Sonstiger Veröffentlichungstext" der Typ "Berichtigung" angegeben wird, muss zwingend auch ein Bezugstyp genannt werden.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(if (not(sonst:typ = 'berichtigung')) then (not(exists(sonst:bezugstyp))) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(sonst:bezugstyp)}"
                                      role="error"
                                      id="SCH-00940-005">
                     <xsl:attribute name="test">if (not(sonst:typ = 'berichtigung')) then (not(exists(sonst:bezugstyp))) else true()</xsl:attribute>
                     <svrl:text>Ein Bezugstyp zu einem Typ darf nur angegeben werden, wenn jener Typ "Berichtigung" lautet.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2151')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="/akn:akomaNtoso/*/akn:meta/akn:proprietary/regtxt:legalDocML.de_metadaten"
                 priority="38"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2163']">
            <schxslt:rule pattern="d15e2163">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-00941 for context "/akn:akomaNtoso/*/akn:meta/akn:proprietary/regtxt:legalDocML.de_metadaten" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00941">
                  <xsl:attribute name="context">/akn:akomaNtoso/*/akn:meta/akn:proprietary/regtxt:legalDocML.de_metadaten</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2163">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-00941">
                  <xsl:attribute name="context">/akn:akomaNtoso/*/akn:meta/akn:proprietary/regtxt:legalDocML.de_metadaten</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if (regtxt:typ = 'berichtigung') then (exists(regtxt:bezugstyp)) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(regtxt:typ)}"
                                      role="error"
                                      id="SCH-00941-000">
                     <xsl:attribute name="test">if (regtxt:typ = 'berichtigung') then (exists(regtxt:bezugstyp)) else true()</xsl:attribute>
                     <svrl:text>Wenn im Metadatenschema "Regelungstext" der Typ "Berichtigung" angegeben wird, muss zwingend auch ein Bezugstyp genannt werden.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
               <xsl:if test="not(if (not(regtxt:typ = 'berichtigung')) then (not(exists(regtxt:bezugstyp))) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(regtxt:bezugstyp)}"
                                      role="error"
                                      id="SCH-00941-005">
                     <xsl:attribute name="test">if (not(regtxt:typ = 'berichtigung')) then (not(exists(regtxt:bezugstyp))) else true()</xsl:attribute>
                     <svrl:text>Ein Bezugstyp zu einem Typ darf nur angegeben werden, wenn jener Typ "Berichtigung" lautet.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2163')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="@href | @src | @from | @upTo" priority="37" mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:variable name="teil-ohne-fragment"
                    select="if (contains(., '#')) then tokenize(., '#')[1] else ."/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2178']">
            <schxslt:rule pattern="d15e2178">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-01010 for context "@href | @src | @from | @upTo" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-01010">
                  <xsl:attribute name="context">@href | @src | @from | @upTo</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2178">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-01010">
                  <xsl:attribute name="context">@href | @src | @from | @upTo</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(parent::akn:FRBRauthor or $teil-ohne-fragment = '' or matches($teil-ohne-fragment, '^([a-zöäüßA-ZÄÖÜẞ]+-)+\d+\.[a-zA-Z]+$') or starts-with($teil-ohne-fragment, 'http://') or starts-with($teil-ohne-fragment, 'https://') or starts-with($teil-ohne-fragment, '/eli'))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-01010-005">
                     <xsl:attribute name="test">parent::akn:FRBRauthor or $teil-ohne-fragment = '' or matches($teil-ohne-fragment, '^([a-zöäüßA-ZÄÖÜẞ]+-)+\d+\.[a-zA-Z]+$') or starts-with($teil-ohne-fragment, 'http://') or starts-with($teil-ohne-fragment, 'https://') or starts-with($teil-ohne-fragment, '/eli')</xsl:attribute>
                     <svrl:text>URI-Verweise müssen eine der drei folgenden Formen haben: 1) vollständige http/https-URI, 2) nur absoluter Pfad-Teil (beginnend mit '/eli', d. h. innerhalb derselben Authority wie das aktuelle Dokument) oder 3) nur Dateiname und ggf. Fragment (d. h. innerhalb der selben Expression).</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2178')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="/akn:akomaNtoso/*/akn:meta/akn:identification[$ist-entwurfsfassung or $ist-konsolidierte-fassung]"
                 priority="36"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2191']">
            <schxslt:rule pattern="d15e2191">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-01015 for context "/akn:akomaNtoso/*/akn:meta/akn:identification[$ist-entwurfsfassung or $ist-konsolidierte-fassung]" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-01015">
                  <xsl:attribute name="context">/akn:akomaNtoso/*/akn:meta/akn:identification[$ist-entwurfsfassung or $ist-konsolidierte-fassung]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2191">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-01015">
                  <xsl:attribute name="context">/akn:akomaNtoso/*/akn:meta/akn:identification[$ist-entwurfsfassung or $ist-konsolidierte-fassung]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(exists(akn:FRBRExpression/akn:FRBRdate))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-01015-000">
                     <xsl:attribute name="test">exists(akn:FRBRExpression/akn:FRBRdate)</xsl:attribute>
                     <svrl:text>
            In Entwurfs- und konsolidierten Fassungen MUSS innerhalb von &lt;FRBRExpression&gt; ein &lt;FRBRdate&gt; vorhanden sein.
         </svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2191')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="/akn:akomaNtoso/*/akn:meta/akn:identification[$ist-verkündungsfassung]"
                 priority="35"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2206']">
            <schxslt:rule pattern="d15e2206">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule SCH-01016 for context "/akn:akomaNtoso/*/akn:meta/akn:identification[$ist-verkündungsfassung]" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-01016">
                  <xsl:attribute name="context">/akn:akomaNtoso/*/akn:meta/akn:identification[$ist-verkündungsfassung]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2206">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" id="SCH-01016">
                  <xsl:attribute name="context">/akn:akomaNtoso/*/akn:meta/akn:identification[$ist-verkündungsfassung]</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(not(akn:FRBRExpression/akn:FRBRdate))">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      role="error"
                                      id="SCH-01016-000">
                     <xsl:attribute name="test">not(akn:FRBRExpression/akn:FRBRdate)</xsl:attribute>
                     <svrl:text>
            In der Verkündungsfassung darf innerhalb von &lt;FRBRExpression&gt; KEIN &lt;FRBRdate&gt; vorkommen.
         </svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2206')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:identification/akn:FRBRExpression/akn:FRBRauthor/@href"
                 priority="34"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2222']">
            <schxslt:rule pattern="d15e2222">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule for context "akn:identification/akn:FRBRExpression/akn:FRBRauthor/@href" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRExpression/akn:FRBRauthor/@href</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2222">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRExpression/akn:FRBRauthor/@href</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($ist-entwurfsfassung) then (. = ('recht.bund.de/institution/bundesregierung', 'recht.bund.de/institution/bundestag', 'recht.bund.de/institution/bundesrat', 'recht.bund.de/institution/bundeskanzler', 'recht.bund.de/institution/bundespraesident')) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-ENTWF-hrefLiterals.expression.FRBRauthor">
                     <xsl:attribute name="test">if ($ist-entwurfsfassung) then (. = ('recht.bund.de/institution/bundesregierung', 'recht.bund.de/institution/bundestag', 'recht.bund.de/institution/bundesrat', 'recht.bund.de/institution/bundeskanzler', 'recht.bund.de/institution/bundespraesident')) else true()</xsl:attribute>
                     <svrl:text>In der Entwurfsfassung ist das Literal "<xsl:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich "recht.bund.de/institution/bundesregierung", "recht.bund.de/institution/bundestag", "recht.bund.de/institution/bundesrat", "recht.bund.de/institution/bundeskanzler" sowie "recht.bund.de/institution/bundespraesident".</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2222')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:identification/akn:FRBRWork/akn:FRBRauthor/@href"
                 priority="33"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2222']">
            <schxslt:rule pattern="d15e2222">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule for context "akn:identification/akn:FRBRWork/akn:FRBRauthor/@href" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRWork/akn:FRBRauthor/@href</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2222">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRWork/akn:FRBRauthor/@href</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($ist-entwurfsfassung) then (. = ('recht.bund.de/institution/bundesregierung', 'recht.bund.de/institution/bundestag', 'recht.bund.de/institution/bundesrat', 'recht.bund.de/institution/bundeskanzler', 'recht.bund.de/institution/bundespraesident')) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-ENTWF-hrefLiterals.work.FRBRauthor">
                     <xsl:attribute name="test">if ($ist-entwurfsfassung) then (. = ('recht.bund.de/institution/bundesregierung', 'recht.bund.de/institution/bundestag', 'recht.bund.de/institution/bundesrat', 'recht.bund.de/institution/bundeskanzler', 'recht.bund.de/institution/bundespraesident')) else true()</xsl:attribute>
                     <svrl:text>In der Entwurfsfassung ist das Literal "<xsl:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich "recht.bund.de/institution/bundesregierung", "recht.bund.de/institution/bundestag", "recht.bund.de/institution/bundesrat", "recht.bund.de/institution/bundeskanzler" sowie "recht.bund.de/institution/bundespraesident".</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2222')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:identification/akn:FRBRExpression/akn:FRBRdate/@name"
                 priority="32"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2222']">
            <schxslt:rule pattern="d15e2222">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule for context "akn:identification/akn:FRBRExpression/akn:FRBRdate/@name" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRExpression/akn:FRBRdate/@name</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2222">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRExpression/akn:FRBRdate/@name</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($ist-entwurfsfassung) then (. = ('version')) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-ENTWF-nameLiterals.expression.FRBRdate">
                     <xsl:attribute name="test">if ($ist-entwurfsfassung) then (. = ('version')) else true()</xsl:attribute>
                     <svrl:text>In der Entwurfsfassung ist das Literal "<xsl:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt ist ausschließlich "version".</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2222')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:identification/akn:FRBRExpression/akn:FRBRthis/@value"
                 priority="31"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2222']">
            <schxslt:rule pattern="d15e2222">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule for context "akn:identification/akn:FRBRExpression/akn:FRBRthis/@value" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRExpression/akn:FRBRthis/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2222">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRExpression/akn:FRBRthis/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($ist-entwurfsfassung) then (matches(., '^https://www.egesetzgebung.bund.de/eli/dl/\d{4}/[a-zäöüß-]+/[0-9]+/[a-zäöüß-]+/[a-zäöüß-]+/\d{4}-\d{2}-\d{2}/[0-9]+/[a-z]{3}/[a-zöäüß\-]+-\d+$')) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-ENTWF-valueLiterals.expression.FRBRthis">
                     <xsl:attribute name="test">if ($ist-entwurfsfassung) then (matches(., '^https://www.egesetzgebung.bund.de/eli/dl/\d{4}/[a-zäöüß-]+/[0-9]+/[a-zäöüß-]+/[a-zäöüß-]+/\d{4}-\d{2}-\d{2}/[0-9]+/[a-z]{3}/[a-zöäüß\-]+-\d+$')) else true()</xsl:attribute>
                     <svrl:text>In der Entwurfsfassung ist das Literal "<xsl:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "https://www.egesetzgebung.bund.de/eli/dl/\d{4}/[a-zäöüß-]+/[0-9]+/[a-zäöüß-]+/[a-zäöüß-]+/\d{4}-\d{2}-\d{2}/[0-9]+/[a-z]{3}/[a-zöäüß\-]+-\d+" entsprechen.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2222')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:identification/akn:FRBRExpression/akn:FRBRuri/@value"
                 priority="30"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2222']">
            <schxslt:rule pattern="d15e2222">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule for context "akn:identification/akn:FRBRExpression/akn:FRBRuri/@value" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRExpression/akn:FRBRuri/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2222">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRExpression/akn:FRBRuri/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($ist-entwurfsfassung) then (matches(., '^https://[a-z]+(\.[a-z]+)+/eli/dl/\d{4}/[a-zäöüß-]+/[0-9]+/[a-zäöüß-]+/[a-zäöüß-]+/\d{4}-\d{2}-\d{2}/[0-9]+/[a-z]{3}$')) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-ENTWF-valueLiterals.expression.FRBRuri">
                     <xsl:attribute name="test">if ($ist-entwurfsfassung) then (matches(., '^https://[a-z]+(\.[a-z]+)+/eli/dl/\d{4}/[a-zäöüß-]+/[0-9]+/[a-zäöüß-]+/[a-zäöüß-]+/\d{4}-\d{2}-\d{2}/[0-9]+/[a-z]{3}$')) else true()</xsl:attribute>
                     <svrl:text>In der Entwurfsfassung ist das Literal "<xsl:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "https://[a-z]+(\.[a-z]+)+/eli/dl/\d{4}/[a-zäöüß-]+/[0-9]+/[a-zäöüß-]+/[a-zäöüß-]+/\d{4}-\d{2}-\d{2}/[0-9]+/[a-z]{3}" entsprechen.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2222')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:identification/akn:FRBRManifestation/akn:FRBRthis/@value"
                 priority="29"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2222']">
            <schxslt:rule pattern="d15e2222">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule for context "akn:identification/akn:FRBRManifestation/akn:FRBRthis/@value" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRManifestation/akn:FRBRthis/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2222">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRManifestation/akn:FRBRthis/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($ist-entwurfsfassung) then (matches(., '^https://[a-z]+(\.[a-z]+)+/eli/dl/\d{4}/[a-zäöüß-]+/[0-9]+/[a-zäöüß-]+/[a-zäöüß-]+/\d{4}-\d{2}-\d{2}/[0-9]+/[a-z]{3}/[a-zöäüß\-]+-\d+\.[a-zöäüß]+$')) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-ENTWF-valueLiterals.manifestation.FRBRthis">
                     <xsl:attribute name="test">if ($ist-entwurfsfassung) then (matches(., '^https://[a-z]+(\.[a-z]+)+/eli/dl/\d{4}/[a-zäöüß-]+/[0-9]+/[a-zäöüß-]+/[a-zäöüß-]+/\d{4}-\d{2}-\d{2}/[0-9]+/[a-z]{3}/[a-zöäüß\-]+-\d+\.[a-zöäüß]+$')) else true()</xsl:attribute>
                     <svrl:text>In der Entwurfsfassung ist das Literal "<xsl:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "https://[a-z]+(\.[a-z]+)+/eli/dl/\d{4}/[a-zäöüß-]+/[0-9]+/[a-zäöüß-]+/[a-zäöüß-]+/\d{4}-\d{2}-\d{2}/[0-9]+/[a-z]{3}/[a-zöäüß\-]+-\d+\.[a-zöäüß]+" entsprechen.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2222')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:identification/akn:FRBRManifestation/akn:FRBRuri/@value"
                 priority="28"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2222']">
            <schxslt:rule pattern="d15e2222">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule for context "akn:identification/akn:FRBRManifestation/akn:FRBRuri/@value" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRManifestation/akn:FRBRuri/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2222">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRManifestation/akn:FRBRuri/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($ist-entwurfsfassung) then (matches(., '^https://[a-z]+(\.[a-z]+)+/eli/dl/\d{4}/[a-zäöüß-]+/[0-9]+/[a-zäöüß-]+/[a-zäöüß-]+/\d{4}-\d{2}-\d{2}/[0-9]+/[a-z]{3}/[a-zöäüß\-]+-\d+\.[a-zöäüß]+$')) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-ENTWF-valueLiterals.manifestation.FRBRuri">
                     <xsl:attribute name="test">if ($ist-entwurfsfassung) then (matches(., '^https://[a-z]+(\.[a-z]+)+/eli/dl/\d{4}/[a-zäöüß-]+/[0-9]+/[a-zäöüß-]+/[a-zäöüß-]+/\d{4}-\d{2}-\d{2}/[0-9]+/[a-z]{3}/[a-zöäüß\-]+-\d+\.[a-zöäüß]+$')) else true()</xsl:attribute>
                     <svrl:text>In der Entwurfsfassung ist das Literal "<xsl:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "https://[a-z]+(\.[a-z]+)+/eli/dl/\d{4}/[a-zäöüß-]+/[0-9]+/[a-zäöüß-]+/[a-zäöüß-]+/\d{4}-\d{2}-\d{2}/[0-9]+/[a-z]{3}/[a-zöäüß\-]+-\d+\.[a-zöäüß]+" entsprechen.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2222')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:identification/akn:FRBRWork/akn:FRBRname/@value"
                 priority="27"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2222']">
            <schxslt:rule pattern="d15e2222">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule for context "akn:identification/akn:FRBRWork/akn:FRBRname/@value" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRWork/akn:FRBRname/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2222">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRWork/akn:FRBRname/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($ist-entwurfsfassung) then (. = ('regelungsentwurf', 'sonstiges-dokument')) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-ENTWF-valueLiterals.work.FRBRname">
                     <xsl:attribute name="test">if ($ist-entwurfsfassung) then (. = ('regelungsentwurf', 'sonstiges-dokument')) else true()</xsl:attribute>
                     <svrl:text>In der Entwurfsfassung ist das Literal "<xsl:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich "regelungsentwurf" und "sonstiges-dokument".</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2222')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:identification/akn:FRBRWork/akn:FRBRnumber/@value"
                 priority="26"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2222']">
            <schxslt:rule pattern="d15e2222">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule for context "akn:identification/akn:FRBRWork/akn:FRBRnumber/@value" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRWork/akn:FRBRnumber/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2222">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRWork/akn:FRBRnumber/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($ist-entwurfsfassung) then (matches(., '^[0-9]+$')) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-ENTWF-valueLiterals.work.FRBRnumber">
                     <xsl:attribute name="test">if ($ist-entwurfsfassung) then (matches(., '^[0-9]+$')) else true()</xsl:attribute>
                     <svrl:text>In der Entwurfsfassung ist das Literal "<xsl:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "[0-9]+" entsprechen.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2222')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:identification/akn:FRBRWork/akn:FRBRsubtype/@value"
                 priority="25"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2222']">
            <schxslt:rule pattern="d15e2222">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule for context "akn:identification/akn:FRBRWork/akn:FRBRsubtype/@value" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRWork/akn:FRBRsubtype/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2222">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRWork/akn:FRBRsubtype/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($ist-entwurfsfassung) then (matches(., '^(rechtsetzungsdokument|aenderungsantrag|anlage-regelungstext|anschreiben|anschreiben-einigungsvorschlag-des-vermittlungsausschusses|anschreiben-vorschlag-an-bundesrat|antrag|austauschseite|begruendung-aenderungsantrag|begruendung-entschliessungsantrag|begruendung-regelungstext|bericht-technikfolgenabschaetzung|sonstiger-veroeffentlichungstext|bericht|berichtigung|beschluss-des-bundesrates|beschlussempfehlung|beschlussvorschlag-der-bundesregierung|denkschrift|entschliessungsantrag|gegenaeusserung-der-bundesregierung|gesetze-beschluss-des-bundestages|gutachtliche-stellungnahme|mitteilung-an-bundesrat|nkr-stellungnahme|regelungstext-entwurf|sonstiges-teildokument|sprechzettel-fuer-regierungssprecher|stellungnahme-bundesrat|synopse|unterrichtung|vereinbarung-entwurf|vorblatt-beschlussempfehlung|einspruch-bundesrat|vorblatt-regelungstext|vorlage-an-bundesrat|vorschlag-an-bundesrat|wahlvorschlag|gesetzesbeschluss-des-bundestages|sammeldrucksache-fragestunde|sammeldrucksache-schriftliche-fragen|antworten-der-bundesregierung|anfragen-an-bundesregierung)(-[0-9]+)$')) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-ENTWF-valueLiterals.work.FRBRsubtype">
                     <xsl:attribute name="test">if ($ist-entwurfsfassung) then (matches(., '^(rechtsetzungsdokument|aenderungsantrag|anlage-regelungstext|anschreiben|anschreiben-einigungsvorschlag-des-vermittlungsausschusses|anschreiben-vorschlag-an-bundesrat|antrag|austauschseite|begruendung-aenderungsantrag|begruendung-entschliessungsantrag|begruendung-regelungstext|bericht-technikfolgenabschaetzung|sonstiger-veroeffentlichungstext|bericht|berichtigung|beschluss-des-bundesrates|beschlussempfehlung|beschlussvorschlag-der-bundesregierung|denkschrift|entschliessungsantrag|gegenaeusserung-der-bundesregierung|gesetze-beschluss-des-bundestages|gutachtliche-stellungnahme|mitteilung-an-bundesrat|nkr-stellungnahme|regelungstext-entwurf|sonstiges-teildokument|sprechzettel-fuer-regierungssprecher|stellungnahme-bundesrat|synopse|unterrichtung|vereinbarung-entwurf|vorblatt-beschlussempfehlung|einspruch-bundesrat|vorblatt-regelungstext|vorlage-an-bundesrat|vorschlag-an-bundesrat|wahlvorschlag|gesetzesbeschluss-des-bundestages|sammeldrucksache-fragestunde|sammeldrucksache-schriftliche-fragen|antworten-der-bundesregierung|anfragen-an-bundesregierung)(-[0-9]+)$')) else true()</xsl:attribute>
                     <svrl:text>In der Entwurfsfassung ist das Literal "<xsl:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "(rechtsetzungsdokument|aenderungsantrag|anlage-regelungstext|anschreiben|anschreiben-einigungsvorschlag-des-vermittlungsausschusses|anschreiben-vorschlag-an-bundesrat|antrag|austauschseite|begruendung-aenderungsantrag|begruendung-entschliessungsantrag|begruendung-regelungstext|bericht-technikfolgenabschaetzung|sonstiger-veroeffentlichungstext|bericht|berichtigung|beschluss-des-bundesrates|beschlussempfehlung|beschlussvorschlag-der-bundesregierung|denkschrift|entschliessungsantrag|gegenaeusserung-der-bundesregierung|gesetze-beschluss-des-bundestages|gutachtliche-stellungnahme|mitteilung-an-bundesrat|nkr-stellungnahme|regelungstext-entwurf|sonstiges-teildokument|sprechzettel-fuer-regierungssprecher|stellungnahme-bundesrat|synopse|unterrichtung|vereinbarung-entwurf|vorblatt-beschlussempfehlung|einspruch-bundesrat|vorblatt-regelungstext|vorlage-an-bundesrat|vorschlag-an-bundesrat|wahlvorschlag|gesetzesbeschluss-des-bundestages|sammeldrucksache-fragestunde|sammeldrucksache-schriftliche-fragen|antworten-der-bundesregierung|anfragen-an-bundesregierung)(-[0-9]+)" entsprechen.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2222')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:identification/akn:FRBRWork/akn:FRBRthis/@value"
                 priority="24"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2222']">
            <schxslt:rule pattern="d15e2222">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule for context "akn:identification/akn:FRBRWork/akn:FRBRthis/@value" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRWork/akn:FRBRthis/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2222">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRWork/akn:FRBRthis/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($ist-entwurfsfassung) then (matches(., '^https://www.egesetzgebung.bund.de/eli/dl/\d{4}/[a-zäöüß-]+/[0-9]+/[a-zäöüß-]+/[a-zäöüß\-]+-\d+$')) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-ENTWF-valueLiterals.work.FRBRthis">
                     <xsl:attribute name="test">if ($ist-entwurfsfassung) then (matches(., '^https://www.egesetzgebung.bund.de/eli/dl/\d{4}/[a-zäöüß-]+/[0-9]+/[a-zäöüß-]+/[a-zäöüß\-]+-\d+$')) else true()</xsl:attribute>
                     <svrl:text>In der Entwurfsfassung ist das Literal "<xsl:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "https://www.egesetzgebung.bund.de/eli/dl/\d{4}/[a-zäöüß-]+/[0-9]+/[a-zäöüß-]+/[a-zäöüß\-]+-\d+" entsprechen.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2222')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:identification/akn:FRBRWork/akn:FRBRuri/@value"
                 priority="23"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2222']">
            <schxslt:rule pattern="d15e2222">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule for context "akn:identification/akn:FRBRWork/akn:FRBRuri/@value" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRWork/akn:FRBRuri/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2222">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRWork/akn:FRBRuri/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($ist-entwurfsfassung) then (matches(., '^https://www.egesetzgebung.bund.de/eli/dl/\d{4}/[a-zäöüß-]+/[0-9]+/[a-zäöüß-]+$')) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-ENTWF-valueLiterals.work.FRBRuri">
                     <xsl:attribute name="test">if ($ist-entwurfsfassung) then (matches(., '^https://www.egesetzgebung.bund.de/eli/dl/\d{4}/[a-zäöüß-]+/[0-9]+/[a-zäöüß-]+$')) else true()</xsl:attribute>
                     <svrl:text>In der Entwurfsfassung ist das Literal "<xsl:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "https://www.egesetzgebung.bund.de/eli/dl/\d{4}/[a-zäöüß-]+/[0-9]+/[a-zäöüß-]+" entsprechen.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2222')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:identification/akn:FRBRExpression/akn:FRBRauthor/@href"
                 priority="22"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2323']">
            <schxslt:rule pattern="d15e2323">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule for context "akn:identification/akn:FRBRExpression/akn:FRBRauthor/@href" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRExpression/akn:FRBRauthor/@href</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2323">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRExpression/akn:FRBRauthor/@href</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($ist-konsolidierte-fassung) then (. = ('recht.bund.de/institution/bundesregierung', 'recht.bund.de/institution/bundeskanzler', 'recht.bund.de/institution/bundespraesident')) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-KONSF-hrefLiterals.expression.FRBRauthor">
                     <xsl:attribute name="test">if ($ist-konsolidierte-fassung) then (. = ('recht.bund.de/institution/bundesregierung', 'recht.bund.de/institution/bundeskanzler', 'recht.bund.de/institution/bundespraesident')) else true()</xsl:attribute>
                     <svrl:text>In der konsolidierten Fassung ist das Literal "<xsl:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich "recht.bund.de/institution/bundesregierung", "recht.bund.de/institution/bundeskanzler" sowie "recht.bund.de/institution/bundespraesident".</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2323')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:identification/akn:FRBRWork/akn:FRBRauthor/@href"
                 priority="21"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2323']">
            <schxslt:rule pattern="d15e2323">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule for context "akn:identification/akn:FRBRWork/akn:FRBRauthor/@href" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRWork/akn:FRBRauthor/@href</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2323">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRWork/akn:FRBRauthor/@href</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($ist-konsolidierte-fassung) then (. = ('recht.bund.de/institution/bundesregierung', 'recht.bund.de/institution/bundeskanzler', 'recht.bund.de/institution/bundespraesident')) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-KONSF-hrefLiterals.work.FRBRauthor">
                     <xsl:attribute name="test">if ($ist-konsolidierte-fassung) then (. = ('recht.bund.de/institution/bundesregierung', 'recht.bund.de/institution/bundeskanzler', 'recht.bund.de/institution/bundespraesident')) else true()</xsl:attribute>
                     <svrl:text>In der konsolidierten Fassung ist das Literal "<xsl:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich "recht.bund.de/institution/bundesregierung", "recht.bund.de/institution/bundeskanzler" sowie "recht.bund.de/institution/bundespraesident".</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2323')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:identification/akn:FRBRExpression/akn:FRBRdate/@name"
                 priority="20"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2323']">
            <schxslt:rule pattern="d15e2323">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule for context "akn:identification/akn:FRBRExpression/akn:FRBRdate/@name" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRExpression/akn:FRBRdate/@name</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2323">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRExpression/akn:FRBRdate/@name</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($ist-konsolidierte-fassung) then (. = ('ausfertigung', 'ausfertigung-aenderung')) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-KONSF-nameLiterals.expression.FRBRdate">
                     <xsl:attribute name="test">if ($ist-konsolidierte-fassung) then (. = ('ausfertigung', 'ausfertigung-aenderung')) else true()</xsl:attribute>
                     <svrl:text>In der konsolidierten Fassung ist das Literal "<xsl:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich "ausfertigung" und "ausfertigung-aenderung".</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2323')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:identification/akn:FRBRExpression/akn:FRBRthis/@value"
                 priority="19"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2323']">
            <schxslt:rule pattern="d15e2323">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule for context "akn:identification/akn:FRBRExpression/akn:FRBRthis/@value" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRExpression/akn:FRBRthis/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2323">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRExpression/akn:FRBRthis/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($ist-konsolidierte-fassung) then (matches(., '^https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{4}/[-a-z0-9äöüß]+/\d{4}-\d{2}-\d{2}/\d+/[a-z]{3}/[a-zöäüß\-]+-\d+$')) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-KONS-valueLiterals.expression.FRBRthis">
                     <xsl:attribute name="test">if ($ist-konsolidierte-fassung) then (matches(., '^https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{4}/[-a-z0-9äöüß]+/\d{4}-\d{2}-\d{2}/\d+/[a-z]{3}/[a-zöäüß\-]+-\d+$')) else true()</xsl:attribute>
                     <svrl:text>In der konsolidierten Fassung ist das Literal "<xsl:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{4}/[-a-z0-9äöüß]+/\d{4}-\d{2}-\d{2}/\d+/[a-z]{3}/[a-zöäüß\-]+-\d+" entsprechen.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2323')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:identification/akn:FRBRExpression/akn:FRBRuri/@value"
                 priority="18"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2323']">
            <schxslt:rule pattern="d15e2323">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule for context "akn:identification/akn:FRBRExpression/akn:FRBRuri/@value" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRExpression/akn:FRBRuri/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2323">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRExpression/akn:FRBRuri/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($ist-konsolidierte-fassung) then (matches(., '^https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{4}/[-a-z0-9äöüß]+/\d{4}-\d{2}-\d{2}/\d+/[a-z]{3}$')) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-KONS-valueLiterals.expression.FRBRuri">
                     <xsl:attribute name="test">if ($ist-konsolidierte-fassung) then (matches(., '^https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{4}/[-a-z0-9äöüß]+/\d{4}-\d{2}-\d{2}/\d+/[a-z]{3}$')) else true()</xsl:attribute>
                     <svrl:text>In der konsolidierten Fassung ist das Literal "<xsl:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{4}/[-a-z0-9äöüß]+/\d{4}-\d{2}-\d{2}/\d+/[a-z]{3}" entsprechen.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2323')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:identification/akn:FRBRManifestation/akn:FRBRthis/@value"
                 priority="17"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2323']">
            <schxslt:rule pattern="d15e2323">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule for context "akn:identification/akn:FRBRManifestation/akn:FRBRthis/@value" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRManifestation/akn:FRBRthis/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2323">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRManifestation/akn:FRBRthis/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($ist-konsolidierte-fassung) then (matches(., '^https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{4}/[-a-z0-9äöüß]+/\d{4}-\d{2}-\d{2}/\d+/[a-z]{3}/\d{4}-\d{2}-\d{2}/[a-zöäüß\-]+-\d+\.[a-zöäüß]+$')) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-KONS-valueLiterals.manifestation.FRBRthis">
                     <xsl:attribute name="test">if ($ist-konsolidierte-fassung) then (matches(., '^https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{4}/[-a-z0-9äöüß]+/\d{4}-\d{2}-\d{2}/\d+/[a-z]{3}/\d{4}-\d{2}-\d{2}/[a-zöäüß\-]+-\d+\.[a-zöäüß]+$')) else true()</xsl:attribute>
                     <svrl:text>In der konsolidierten Fassung ist das Literal "<xsl:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{4}/[-a-z0-9äöüß]+/\d{4}-\d{2}-\d{2}/\d+/[a-z]{3}/\d{4}-\d{2}-\d{2}/[a-zöäüß\-]+-\d+\.[a-zöäüß]+" entsprechen.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2323')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:identification/akn:FRBRManifestation/akn:FRBRuri/@value"
                 priority="16"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2323']">
            <schxslt:rule pattern="d15e2323">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule for context "akn:identification/akn:FRBRManifestation/akn:FRBRuri/@value" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRManifestation/akn:FRBRuri/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2323">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRManifestation/akn:FRBRuri/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($ist-konsolidierte-fassung) then (matches(., '^https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{4}/[-a-z0-9äöüß]+/\d{4}-\d{2}-\d{2}/\d+/[a-z]{3}/\d{4}-\d{2}-\d{2}/[a-zöäüß\-]+-\d+\.[a-zöäüß]+$')) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-KONS-valueLiterals.manifestation.FRBRuri">
                     <xsl:attribute name="test">if ($ist-konsolidierte-fassung) then (matches(., '^https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{4}/[-a-z0-9äöüß]+/\d{4}-\d{2}-\d{2}/\d+/[a-z]{3}/\d{4}-\d{2}-\d{2}/[a-zöäüß\-]+-\d+\.[a-zöäüß]+$')) else true()</xsl:attribute>
                     <svrl:text>In der konsolidierten Fassung ist das Literal "<xsl:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{4}/[-a-z0-9äöüß]+/\d{4}-\d{2}-\d{2}/\d+/[a-z]{3}/\d{4}-\d{2}-\d{2}/[a-zöäüß\-]+-\d+\.[a-zöäüß]+" entsprechen.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2323')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:identification/akn:FRBRWork/akn:FRBRname/@value"
                 priority="15"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2323']">
            <schxslt:rule pattern="d15e2323">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule for context "akn:identification/akn:FRBRWork/akn:FRBRname/@value" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRWork/akn:FRBRname/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2323">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRWork/akn:FRBRname/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($ist-konsolidierte-fassung) then (. = ('bgbl', 'bgbl-1', 'bgbl-2', 'banz-at', 'banz', 'ebanz', 'vkbl', 'hist')) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-KONSF-valueLiterals.work.FRBRname">
                     <xsl:attribute name="test">if ($ist-konsolidierte-fassung) then (. = ('bgbl', 'bgbl-1', 'bgbl-2', 'banz-at', 'banz', 'ebanz', 'vkbl', 'hist')) else true()</xsl:attribute>
                     <svrl:text>In der konsolidierten Fassung ist das Literal "<xsl:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich "bgbl", "bgbl-1", "bgbl-2", "banz-at", "banz", "ebanz", "vkbl" sowie "hist".</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2323')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:identification/akn:FRBRWork/akn:FRBRnumber/@value"
                 priority="14"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2323']">
            <schxslt:rule pattern="d15e2323">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule for context "akn:identification/akn:FRBRWork/akn:FRBRnumber/@value" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRWork/akn:FRBRnumber/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2323">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRWork/akn:FRBRnumber/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($ist-konsolidierte-fassung) then (matches(., '^[-a-zäöüß0-9]+$')) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-KONS-valueLiterals.work.FRBRnumber">
                     <xsl:attribute name="test">if ($ist-konsolidierte-fassung) then (matches(., '^[-a-zäöüß0-9]+$')) else true()</xsl:attribute>
                     <svrl:text>In der konsolidierten Fassung ist das Literal "<xsl:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "[-a-zäöüß0-9]+" entsprechen.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2323')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:identification/akn:FRBRWork/akn:FRBRsubtype/@value"
                 priority="13"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2323']">
            <schxslt:rule pattern="d15e2323">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule for context "akn:identification/akn:FRBRWork/akn:FRBRsubtype/@value" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRWork/akn:FRBRsubtype/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2323">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRWork/akn:FRBRsubtype/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($ist-konsolidierte-fassung) then (matches(., '^(anlage-regelungstext|rechtsetzungsdokument|regelungstext|vereinbarung-verkuendung|sonstiger-veroeffentlichungstext|sonstiges-teildokument)(-[0-9]+)$')) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-KONS-valueLiterals.work.FRBRsubtype">
                     <xsl:attribute name="test">if ($ist-konsolidierte-fassung) then (matches(., '^(anlage-regelungstext|rechtsetzungsdokument|regelungstext|vereinbarung-verkuendung|sonstiger-veroeffentlichungstext|sonstiges-teildokument)(-[0-9]+)$')) else true()</xsl:attribute>
                     <svrl:text>In der konsolidierten Fassung ist das Literal "<xsl:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "(anlage-regelungstext|rechtsetzungsdokument|regelungstext|vereinbarung-verkuendung|sonstiger-veroeffentlichungstext|sonstiges-teildokument)(-[0-9]+)" entsprechen.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2323')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:identification/akn:FRBRWork/akn:FRBRthis/@value"
                 priority="12"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2323']">
            <schxslt:rule pattern="d15e2323">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule for context "akn:identification/akn:FRBRWork/akn:FRBRthis/@value" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRWork/akn:FRBRthis/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2323">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRWork/akn:FRBRthis/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($ist-konsolidierte-fassung) then (matches(., '^https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{4}/[-a-z0-9äöüß]+/[a-zöäüß\-]+-\d+$')) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-KONS-valueLiterals.work.FRBRthis">
                     <xsl:attribute name="test">if ($ist-konsolidierte-fassung) then (matches(., '^https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{4}/[-a-z0-9äöüß]+/[a-zöäüß\-]+-\d+$')) else true()</xsl:attribute>
                     <svrl:text>In der konsolidierten Fassung ist das Literal "<xsl:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{4}/[-a-z0-9äöüß]+/[a-zöäüß\-]+-\d+" entsprechen.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2323')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:identification/akn:FRBRWork/akn:FRBRuri/@value"
                 priority="11"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2323']">
            <schxslt:rule pattern="d15e2323">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule for context "akn:identification/akn:FRBRWork/akn:FRBRuri/@value" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRWork/akn:FRBRuri/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2323">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRWork/akn:FRBRuri/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($ist-konsolidierte-fassung) then (matches(., '^https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{4}/[-a-z0-9äöüß]+$')) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-KONS-valueLiterals.work.FRBRuri">
                     <xsl:attribute name="test">if ($ist-konsolidierte-fassung) then (matches(., '^https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{4}/[-a-z0-9äöüß]+$')) else true()</xsl:attribute>
                     <svrl:text>In der konsolidierten Fassung ist das Literal "<xsl:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{4}/[-a-z0-9äöüß]+" entsprechen.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2323')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:identification/akn:FRBRExpression/akn:FRBRauthor/@href"
                 priority="10"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2424']">
            <schxslt:rule pattern="d15e2424">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule for context "akn:identification/akn:FRBRExpression/akn:FRBRauthor/@href" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRExpression/akn:FRBRauthor/@href</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2424">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRExpression/akn:FRBRauthor/@href</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($ist-verkündungsfassung) then (. = ('recht.bund.de/institution/bundesregierung', 'recht.bund.de/institution/bundeskanzler', 'recht.bund.de/institution/bundespraesident')) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-VERKF-hrefLiterals.expression.FRBRauthor">
                     <xsl:attribute name="test">if ($ist-verkündungsfassung) then (. = ('recht.bund.de/institution/bundesregierung', 'recht.bund.de/institution/bundeskanzler', 'recht.bund.de/institution/bundespraesident')) else true()</xsl:attribute>
                     <svrl:text>In der Verkündungsfassung ist das Literal "<xsl:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich "recht.bund.de/institution/bundesregierung", "recht.bund.de/institution/bundeskanzler" sowie "recht.bund.de/institution/bundespraesident".</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2424')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:identification/akn:FRBRWork/akn:FRBRauthor/@href"
                 priority="9"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2424']">
            <schxslt:rule pattern="d15e2424">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule for context "akn:identification/akn:FRBRWork/akn:FRBRauthor/@href" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRWork/akn:FRBRauthor/@href</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2424">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRWork/akn:FRBRauthor/@href</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($ist-verkündungsfassung) then (. = ('recht.bund.de/institution/bundesregierung', 'recht.bund.de/institution/bundeskanzler', 'recht.bund.de/institution/bundespraesident')) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-VERKF-hrefLiterals.work.FRBRauthor">
                     <xsl:attribute name="test">if ($ist-verkündungsfassung) then (. = ('recht.bund.de/institution/bundesregierung', 'recht.bund.de/institution/bundeskanzler', 'recht.bund.de/institution/bundespraesident')) else true()</xsl:attribute>
                     <svrl:text>In der Verkündungsfassung ist das Literal "<xsl:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich "recht.bund.de/institution/bundesregierung", "recht.bund.de/institution/bundeskanzler" sowie "recht.bund.de/institution/bundespraesident".</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2424')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:identification/akn:FRBRExpression/akn:FRBRthis/@value"
                 priority="8"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2424']">
            <schxslt:rule pattern="d15e2424">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule for context "akn:identification/akn:FRBRExpression/akn:FRBRthis/@value" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRExpression/akn:FRBRthis/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2424">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRExpression/akn:FRBRthis/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($ist-verkündungsfassung) then (matches(., '^https://www.recht.bund.de/eli/bund/[-a-z0-9]+/\d{4}/((s[0-9]+[a-zäöüß]*)|([0-9]+[a-zäöüß]*))/[a-z]{3}/[a-zöäüß\-]+-\d+$')) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-VERK-valueLiterals.expression.FRBRthis">
                     <xsl:attribute name="test">if ($ist-verkündungsfassung) then (matches(., '^https://www.recht.bund.de/eli/bund/[-a-z0-9]+/\d{4}/((s[0-9]+[a-zäöüß]*)|([0-9]+[a-zäöüß]*))/[a-z]{3}/[a-zöäüß\-]+-\d+$')) else true()</xsl:attribute>
                     <svrl:text>In der Verkündungsfassung ist das Literal "<xsl:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "https://www.recht.bund.de/eli/bund/[-a-z0-9]+/\d{4}/((s[0-9]+[a-zäöüß]*)|([0-9]+[a-zäöüß]*))/[a-z]{3}/[a-zöäüß\-]+-\d+" entsprechen.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2424')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:identification/akn:FRBRExpression/akn:FRBRuri/@value"
                 priority="7"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2424']">
            <schxslt:rule pattern="d15e2424">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule for context "akn:identification/akn:FRBRExpression/akn:FRBRuri/@value" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRExpression/akn:FRBRuri/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2424">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRExpression/akn:FRBRuri/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($ist-verkündungsfassung) then (matches(., '^https://www.recht.bund.de/eli/bund/[-a-z0-9]+/\d{4}/((s[0-9]+[a-zäöüß]*)|([0-9]+[a-zäöüß]*?))/[a-z]{3}$')) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-VERK-valueLiterals.expression.FRBRuri">
                     <xsl:attribute name="test">if ($ist-verkündungsfassung) then (matches(., '^https://www.recht.bund.de/eli/bund/[-a-z0-9]+/\d{4}/((s[0-9]+[a-zäöüß]*)|([0-9]+[a-zäöüß]*?))/[a-z]{3}$')) else true()</xsl:attribute>
                     <svrl:text>In der Verkündungsfassung ist das Literal "<xsl:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "https://www.recht.bund.de/eli/bund/[-a-z0-9]+/\d{4}/((s[0-9]+[a-zäöüß]*)|([0-9]+[a-zäöüß]*?))/[a-z]{3}" entsprechen.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2424')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:identification/akn:FRBRManifestation/akn:FRBRthis/@value"
                 priority="6"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2424']">
            <schxslt:rule pattern="d15e2424">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule for context "akn:identification/akn:FRBRManifestation/akn:FRBRthis/@value" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRManifestation/akn:FRBRthis/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2424">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRManifestation/akn:FRBRthis/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($ist-verkündungsfassung) then (matches(., '^https://www.recht.bund.de/eli/bund/[-a-z0-9]+/\d{4}/((s[0-9]+[a-zäöüß]*)|([0-9]+[a-zäöüß]*))/[a-z]{3}/[a-zöäüß\-]+-\d+\.[a-zöäüß]+$')) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-VERK-valueLiterals.manifestation.FRBRthis">
                     <xsl:attribute name="test">if ($ist-verkündungsfassung) then (matches(., '^https://www.recht.bund.de/eli/bund/[-a-z0-9]+/\d{4}/((s[0-9]+[a-zäöüß]*)|([0-9]+[a-zäöüß]*))/[a-z]{3}/[a-zöäüß\-]+-\d+\.[a-zöäüß]+$')) else true()</xsl:attribute>
                     <svrl:text>In der Verkündungsfassung ist das Literal "<xsl:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "https://www.recht.bund.de/eli/bund/[-a-z0-9]+/\d{4}/((s[0-9]+[a-zäöüß]*)|([0-9]+[a-zäöüß]*))/[a-z]{3}/[a-zöäüß\-]+-\d+\.[a-zöäüß]+" entsprechen.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2424')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:identification/akn:FRBRManifestation/akn:FRBRuri/@value"
                 priority="5"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2424']">
            <schxslt:rule pattern="d15e2424">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule for context "akn:identification/akn:FRBRManifestation/akn:FRBRuri/@value" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRManifestation/akn:FRBRuri/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2424">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRManifestation/akn:FRBRuri/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($ist-verkündungsfassung) then (matches(., '^https://www.recht.bund.de/eli/bund/[-a-z0-9]+/\d{4}/((s[0-9]+[a-zäöüß]*)|([0-9]+[a-zäöüß]*))/[a-z]{3}/[a-zöäüß\-]+-\d+\.[a-zöäüß]+$')) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-VERK-valueLiterals.manifestation.FRBRuri">
                     <xsl:attribute name="test">if ($ist-verkündungsfassung) then (matches(., '^https://www.recht.bund.de/eli/bund/[-a-z0-9]+/\d{4}/((s[0-9]+[a-zäöüß]*)|([0-9]+[a-zäöüß]*))/[a-z]{3}/[a-zöäüß\-]+-\d+\.[a-zöäüß]+$')) else true()</xsl:attribute>
                     <svrl:text>In der Verkündungsfassung ist das Literal "<xsl:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "https://www.recht.bund.de/eli/bund/[-a-z0-9]+/\d{4}/((s[0-9]+[a-zäöüß]*)|([0-9]+[a-zäöüß]*))/[a-z]{3}/[a-zöäüß\-]+-\d+\.[a-zöäüß]+" entsprechen.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2424')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:identification/akn:FRBRWork/akn:FRBRname/@value"
                 priority="4"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2424']">
            <schxslt:rule pattern="d15e2424">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule for context "akn:identification/akn:FRBRWork/akn:FRBRname/@value" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRWork/akn:FRBRname/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2424">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRWork/akn:FRBRname/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($ist-verkündungsfassung) then (. = ('bgbl-1', 'bgbl-2', 'banz-at')) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-VERKF-valueLiterals.work.FRBRname">
                     <xsl:attribute name="test">if ($ist-verkündungsfassung) then (. = ('bgbl-1', 'bgbl-2', 'banz-at')) else true()</xsl:attribute>
                     <svrl:text>In der Verkündungsfassung ist das Literal "<xsl:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich "bgbl-1", "bgbl-2" sowie "banz-at".</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2424')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:identification/akn:FRBRWork/akn:FRBRnumber/@value"
                 priority="3"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2424']">
            <schxslt:rule pattern="d15e2424">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule for context "akn:identification/akn:FRBRWork/akn:FRBRnumber/@value" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRWork/akn:FRBRnumber/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2424">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRWork/akn:FRBRnumber/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($ist-verkündungsfassung) then (matches(., '^((s[0-9]+[a-zäöüß]*)|([0-9]+)[a-zäöüß]*)$')) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-VERK-valueLiterals.work.FRBRnumber">
                     <xsl:attribute name="test">if ($ist-verkündungsfassung) then (matches(., '^((s[0-9]+[a-zäöüß]*)|([0-9]+)[a-zäöüß]*)$')) else true()</xsl:attribute>
                     <svrl:text>In der Verkündungsfassung ist das Literal "<xsl:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "((s[0-9]+[a-zäöüß]*)|([0-9]+)[a-zäöüß]*)" entsprechen.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2424')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:identification/akn:FRBRWork/akn:FRBRsubtype/@value"
                 priority="2"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2424']">
            <schxslt:rule pattern="d15e2424">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule for context "akn:identification/akn:FRBRWork/akn:FRBRsubtype/@value" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRWork/akn:FRBRsubtype/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2424">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRWork/akn:FRBRsubtype/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($ist-verkündungsfassung) then (matches(., '^(anlage-regelungstext|rechtsetzungsdokument|regelungstext|vereinbarung-verkuendung|sonstiger-veroeffentlichungstext|sonstiges-teildokument)(-[0-9]+)$')) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-VERK-valueLiterals.work.FRBRsubtype">
                     <xsl:attribute name="test">if ($ist-verkündungsfassung) then (matches(., '^(anlage-regelungstext|rechtsetzungsdokument|regelungstext|vereinbarung-verkuendung|sonstiger-veroeffentlichungstext|sonstiges-teildokument)(-[0-9]+)$')) else true()</xsl:attribute>
                     <svrl:text>In der Verkündungsfassung ist das Literal "<xsl:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "(anlage-regelungstext|rechtsetzungsdokument|regelungstext|vereinbarung-verkuendung|sonstiger-veroeffentlichungstext|sonstiges-teildokument)(-[0-9]+)" entsprechen.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2424')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:identification/akn:FRBRWork/akn:FRBRthis/@value"
                 priority="1"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2424']">
            <schxslt:rule pattern="d15e2424">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule for context "akn:identification/akn:FRBRWork/akn:FRBRthis/@value" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRWork/akn:FRBRthis/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2424">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRWork/akn:FRBRthis/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($ist-verkündungsfassung) then (matches(., '^https://www.recht.bund.de/eli/bund/[-a-z0-9]+/\d{4}/((s[0-9]+[a-zäöüß]*)|([0-9]+[a-zäöüß]*))/[a-zöäüß\-]+-\d+$')) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-VERK-valueLiterals.work.FRBRthis">
                     <xsl:attribute name="test">if ($ist-verkündungsfassung) then (matches(., '^https://www.recht.bund.de/eli/bund/[-a-z0-9]+/\d{4}/((s[0-9]+[a-zäöüß]*)|([0-9]+[a-zäöüß]*))/[a-zöäüß\-]+-\d+$')) else true()</xsl:attribute>
                     <svrl:text>In der Verkündungsfassung ist das Literal "<xsl:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "https://www.recht.bund.de/eli/bund/[-a-z0-9]+/\d{4}/((s[0-9]+[a-zäöüß]*)|([0-9]+[a-zäöüß]*))/[a-zöäüß\-]+-\d+" entsprechen.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2424')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="akn:identification/akn:FRBRWork/akn:FRBRuri/@value"
                 priority="0"
                 mode="d15e206">
      <xsl:param name="schxslt:patterns-matched" as="xs:string*"/>
      <xsl:choose>
         <xsl:when test="$schxslt:patterns-matched[. = 'd15e2424']">
            <schxslt:rule pattern="d15e2424">
               <xsl:comment xmlns:svrl="http://purl.oclc.org/dsdl/svrl">WARNING: Rule for context "akn:identification/akn:FRBRWork/akn:FRBRuri/@value" shadowed by preceding rule</xsl:comment>
               <svrl:suppressed-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRWork/akn:FRBRuri/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:suppressed-rule>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="$schxslt:patterns-matched"/>
            </xsl:next-match>
         </xsl:when>
         <xsl:otherwise>
            <schxslt:rule pattern="d15e2424">
               <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" role="error">
                  <xsl:attribute name="context">akn:identification/akn:FRBRWork/akn:FRBRuri/@value</xsl:attribute>
                  <xsl:variable name="documentUri" as="xs:anyURI?" select="document-uri()"/>
                  <xsl:if test="exists($documentUri)">
                     <xsl:attribute name="document" select="$documentUri"/>
                  </xsl:if>
               </svrl:fired-rule>
               <xsl:if test="not(if ($ist-verkündungsfassung) then (matches(., '^https://www.recht.bund.de/eli/bund/[-a-z0-9]+/\d{4}/((s[0-9]+[a-zäöüß]*)|([0-9]+[a-zäöüß]*))$')) else true())">
                  <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                      location="{schxslt:location(.)}"
                                      id="SCH-VERK-valueLiterals.work.FRBRuri">
                     <xsl:attribute name="test">if ($ist-verkündungsfassung) then (matches(., '^https://www.recht.bund.de/eli/bund/[-a-z0-9]+/\d{4}/((s[0-9]+[a-zäöüß]*)|([0-9]+[a-zäöüß]*))$')) else true()</xsl:attribute>
                     <svrl:text>In der Verkündungsfassung ist das Literal "<xsl:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "https://www.recht.bund.de/eli/bund/[-a-z0-9]+/\d{4}/((s[0-9]+[a-zäöüß]*)|([0-9]+[a-zäöüß]*))" entsprechen.</svrl:text>
                  </svrl:failed-assert>
               </xsl:if>
            </schxslt:rule>
            <xsl:next-match>
               <xsl:with-param name="schxslt:patterns-matched"
                               as="xs:string*"
                               select="($schxslt:patterns-matched, 'd15e2424')"/>
            </xsl:next-match>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:function name="schxslt:location" as="xs:string">
      <xsl:param name="node" as="node()"/>
      <xsl:variable name="segments" as="xs:string*">
         <xsl:for-each select="($node/ancestor-or-self::node())">
            <xsl:variable name="position">
               <xsl:number level="single"/>
            </xsl:variable>
            <xsl:choose>
               <xsl:when test=". instance of element()">
                  <xsl:value-of select="concat('Q{', namespace-uri(.), '}', local-name(.), '[', $position, ']')"/>
               </xsl:when>
               <xsl:when test=". instance of attribute()">
                  <xsl:value-of select="concat('@Q{', namespace-uri(.), '}', local-name(.))"/>
               </xsl:when>
               <xsl:when test=". instance of processing-instruction()">
                  <xsl:value-of select="concat('processing-instruction(&#34;', name(.), '&#34;)[', $position, ']')"/>
               </xsl:when>
               <xsl:when test=". instance of comment()">
                  <xsl:value-of select="concat('comment()[', $position, ']')"/>
               </xsl:when>
               <xsl:when test=". instance of text()">
                  <xsl:value-of select="concat('text()[', $position, ']')"/>
               </xsl:when>
               <xsl:otherwise/>
            </xsl:choose>
         </xsl:for-each>
      </xsl:variable>
      <xsl:value-of select="concat('/', string-join($segments, '/'))"/>
   </xsl:function>
</xsl:transform>
