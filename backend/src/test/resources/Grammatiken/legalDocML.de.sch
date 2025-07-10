<?xml version="1.0" encoding="UTF-8"?>
<sch:schema xmlns:fkt="lokale-funktionen"
            xmlns:sch="http://purl.oclc.org/dsdl/schematron"
            queryBinding="xslt2"
            schemaVersion="LegalDocML.de 1.8.1 (10.06.2025)">
<!--

********************************* Hinweis zur Lizensierung ***********************************
 2025 Copyright © 2021–2025 Bundesministerium des Innern und für Heimat,
 Referat DG II 6, Maßnahmen Enterprise Resource Management und Elektronische Verwaltungsarbeit

      Lizenz: CC-BY-3.0 (Creative Commons Namensnennung 3.0)
 Information: https://creativecommons.org/licenses/by/3.0/legalcode.de
**********************************************************************************************

-->
   <sch:ns uri="http://Inhaltsdaten.LegalDocML.de/1.8.1/" prefix="akn"/>
   <sch:ns uri="http://MetadatenRegelungstext.LegalDocML.de/1.8.1/"
           prefix="regtxt"/>
   <sch:ns uri="http://MetadatenRechtsetzungsdokument.LegalDocML.de/1.8.1/"
           prefix="redok"/>
   <sch:ns uri="http://MetadatenBundestag.LegalDocML.de/1.8.1/" prefix="btag"/>
   <sch:ns uri="http://MetadatenBundesrat.LegalDocML.de/1.8.1/" prefix="brat"/>
   <sch:ns uri="http://MetadatenBundesregierung.LegalDocML.de/1.8.1/"
           prefix="breg"/>
   <sch:ns uri="http://MetadatenFormulierungshilfe.LegalDocML.de/1.8.1/"
           prefix="fhilf"/>
   <sch:ns uri="http://MetadatenNormenkontrollrat.LegalDocML.de/1.8.1/"
           prefix="nkr"/>
   <sch:ns uri="lokale-funktionen" prefix="fkt"/>
   <!-- Dokumenteigenschaften: Form -->
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="form"
            value="/akn:akomaNtoso/*/akn:meta/akn:proprietary/regtxt:legalDocML.de_metadaten/regtxt:form"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="form-stammform"
            value="'stammform'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="form-mantelform"
            value="'mantelform'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="form-eingebundene-stammform"
            value="'eingebundene-stammform'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="form-nicht-vorhanden"
            value="'nicht-vorhanden'"/>
   <!-- Dokumenteigenschaften: Fassung -->
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="fassung"
            value="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRdate/@name"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="fassung-entwurfsfassung"
            value="'erstellungsdatum'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="fassung-verkündungsfassung"
            value="('verkuendungsfassung-verkuendungsdatum', 'verkuendungsfassung-ausfertigungsdatum')"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="fassung-neufassung"
            value="('neufassung-verkuendungsdatum', 'neufassung-ausfertigungsdatum')"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="ist-entwurfsfassung"
            value="$fassung = 'erstellungsdatum'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="ist-verkündungsfassung"
            value="$fassung = ($fassung-verkündungsfassung, $fassung-neufassung)"/>
   <!-- Dokumenteigenschaften: Typ -->
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="typ"
            value="/akn:akomaNtoso/*/akn:meta/akn:proprietary/regtxt:legalDocML.de_metadaten/regtxt:typ"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="typ-gesetz"
            value="'gesetz'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="typ-sonstige-bekanntmachung"
            value="'sonstige-bekanntmachung'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="typ-verordnung"
            value="'verordnung'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="typ-vertragsgesetz"
            value="'vertragsgesetz'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="typ-vertragsverordnung"
            value="'vertragsverordnung'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="typ-verwaltungsvorschrift"
            value="'verwaltungsvorschrift'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="typ-satzung"
            value="'satzung'"/>
   <!-- Dokumenteigenschaften: Ontologischer Teildokument-Typ (früher: "meta:art") -->
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="teildokument-uri"
            value="/akn:akomaNtoso/*/@name"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="art-anschreiben-uri"
            value="( '/akn/ontology/de/concept/documenttype/bund/anschreiben', '/akn/ontology/de/concept/documenttype/bund/anschreiben-einigungsvorschlag-des-vermittlungsausschusses', '/akn/ontology/de/concept/documenttype/bund/anschreiben-vorschlag-an-bundesrat' )"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="art-begründung-uri"
            value="( '/akn/ontology/de/concept/documenttype/bund/begruendung-aenderungsantrag', '/akn/ontology/de/concept/documenttype/bund/begruendung-entschliessungsantrag', '/akn/ontology/de/concept/documenttype/bund/begruendung-regelungstext' )"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="art-regelungstext-uri"
            value="( '/akn/ontology/de/concept/documenttype/bund/regelungstext-entwurf', '/akn/ontology/de/concept/documenttype/bund/regelungstext-neufassung', '/akn/ontology/de/concept/documenttype/bund/regelungstext-verkuendung' )"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="art-vereinbarung-uri"
            value="( '/akn/ontology/de/concept/documenttype/bund/vereinbarung-entwurf', '/akn/ontology/de/concept/documenttype/bund/vereinbarung-verkuendung' )"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="art-vorblatt-uri"
            value="( '/akn/ontology/de/concept/documenttype/bund/vorblatt-regelungstext', '/akn/ontology/de/concept/documenttype/bund/vorblatt-beschlussempfehlung' )"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="art-vorblatt-regelungstext-uri"
            value="'/akn/ontology/de/concept/documenttype/bund/vorblatt-regelungstext'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="art-vorblatt-beschlussempfehlung-uri"
            value="'/akn/ontology/de/concept/documenttype/bund/vorblatt-beschlussempfehlung'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="art-anlage-regelungstext-uri"
            value="'/akn/ontology/de/concept/documenttype/bund/anlage-regelungstext'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="art-bericht-uri"
            value="'/akn/ontology/de/concept/documenttype/bund/bericht'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="art-bekanntmachungstext-uri"
            value="( '/akn/ontology/de/concept/documenttype/bund/bekanntmachungstext', '/akn/ontology/de/concept/documenttype/bund/bekanntmachungstext-berichtigung', '/akn/ontology/de/concept/documenttype/bund/bekanntmachungstext-entscheidung-des-bundesverfassungsgerichts' )"/>
   <!-- Determinanten für Dokumentvarianten: initiant -->
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="initiant"
            value="/akn:akomaNtoso/*/akn:meta/akn:proprietary/redok:legalDocML.de_metadaten/redok:initiant"/>
   <!-- Determinanten für Dokumentvarianten: bearbeitende-institution -->
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="bearbeitende-institution"
            value="/akn:akomaNtoso/*/akn:meta/akn:proprietary/redok:legalDocML.de_metadaten/redok:bearbeitendeInstitution"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="bearbeitende-institution-frbrauthor"
            value="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRExpression/akn:FRBRauthor/@href"/>
   <!-- Nähere Qualifizierungen innerhalb von Einzelvorschriften -->
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="refersto-literal-geltungszeitregel"
            value="'geltungszeitregel'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="refersto-literal-geltungszeitregel-inkrafttreten"
            value="'geltungszeitregel-inkrafttreten'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="refersto-literal-geltungszeitregel-ausserkrafttreten"
            value="'geltungszeitregel-ausserkrafttreten'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="refersto-literal-hauptaenderung"
            value="'hauptaenderung'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="refersto-literal-folgeaenderung"
            value="'folgeaenderung'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="refersto-literal-eingebundene-stammform"
            value="'eingebundene-stammform'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="refersto-literal-stammform"
            value="'stammform'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="refersto-literal-mantelform"
            value="'mantelform'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="refersto-literal-vertragsgesetz"
            value="'vertragsgesetz'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="refersto-literal-vertragsverordnung"
            value="'vertragsverordnung'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="refersto-literal-ausschussueberweisung"
            value="'ausschussueberweisung'"/>
   <!-- Lebenszyklus-bezogene Angaben -->
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="type-literal-ereignisreferenz-generation"
            value="'generation'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="type-literal-ereignisreferenz-repeal"
            value="'repeal'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="type-literal-ereignisreferenz-amendment"
            value="'amendment'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="refersto-literal-ereignisreferenz-entwurfsfassung-ausfertigung-mit-unbekanntem-datum"
            value="'ausfertigung-mit-noch-unbekanntem-datum'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="refersto-literal-ereignisreferenz-entwurfsfassung-inkrafttreten"
            value="'inkrafttreten'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="refersto-literal-ereignisreferenz-entwurfsfassung-inkrafttreten-mit-unbekanntem-datum"
            value="'inkrafttreten-mit-noch-unbekanntem-datum'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="refersto-literal-ereignisreferenz-entwurfsfassung-ausserkrafttreten"
            value="'ausserkrafttreten'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="refersto-literal-ereignisreferenz-entwurfsfassung-ausserkrafttreten-mit-unbekanntem-datum"
            value="'ausserkrafttreten-mit-noch-unbekanntem-datum'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="refersto-literal-ereignisreferenz-verkündungsfassung-ausfertigung"
            value="'ausfertigung'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten"
            value="'inkrafttreten'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-grundsaetzlich"
            value="'inkrafttreten-grundsaetzlich'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-abweichend"
            value="'inkrafttreten-abweichend'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-mit-unbekanntem-datum"
            value="'inkrafttreten-mit-noch-unbekanntem-datum'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="refersto-literal-ereignisreferenz-verkündungsfassung-ausserkrafttreten"
            value="'ausserkrafttreten'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="refersto-literal-ereignisreferenz-verkündungssfassung-ausserkrafttreten-mit-unbekanntem-datum"
            value="'ausserkrafttreten-mit-noch-unbekanntem-datum'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="refersto-literal-ereignisreferenz-neufassung"
            value="'neufassung'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="dokumentarten-mit-lebenszyklus-angaben-formulierung-satzanfang-nominativ"
            value="'Ein Regelungstext, ein Bekanntmachungstext oder eine Vereinbarung'"/>
   <!-- Sonstige Literale innerhalb der fachlichen Semantik -->
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="refersto-literal-vorblattabschnitt-erfüllungsaufwand"
            value="'vorblattabschnitt-erfuellungsaufwand'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="literal-deklaration-ausnahme-eid-zählweise"
            value="'ordinale-zaehlung-eid'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="präfix-eid-nummerierbar"
            value="'n'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="präfix-eid-zitierbar"
            value="'z'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="zitierbare-elementtypen"
            value="('article', 'paragraph')"/>
   <!-- ================================================================================================================================================ -->
   <!-- Lookup-Table zur Beschleunigung der uniqueness-Prüfung -->
   <xsl:key xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="nodes-by-GUID"
            match="*[@GUID]"
            use="@GUID"/>
   <xsl:key xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="nodes-by-eId"
            match="*[@eId]"
            use="@eId"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="platzhalter-datum-unbekannt"
            value="'0001-01-01'"/>
   <!-- ================================================================================================================================================ -->
   <!-- Regeln -->
   <sch:title xmlns:xsl="http://www.w3.org/1999/XSL/Transform">Regelungstext Entwurfsfassung</sch:title>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00019"
                context="/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri and $form = $form-eingebundene-stammform]">
         <sch:assert id="SCH-00019-000" role="error" test="not(./akn:preface/akn:block)">Ein Regelungstext in Stammform in einer Mantelform darf keinen Datumscontainer innerhalb des Dokumentenkopfes enthalten.</sch:assert>
         <sch:assert id="SCH-00019-005"
                     role="error"
                     test="not(./akn:preamble/akn:formula)">Ein Regelungstext in Stammform in einer Mantelform darf keine Eingangsformel enthalten.</sch:assert>
         <sch:assert id="SCH-00019-010" role="error" test="not(./akn:conclusions)">Ein Regelungstext in Stammform in einer Mantelform darf keinen Schlussteil enthalten.</sch:assert>
      </sch:rule>
      <sch:rule id="SCH-00020"
                context="/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri and $typ = ($typ-gesetz, $typ-vertragsgesetz)]">
         <sch:assert id="SCH-00020-005" role="error" test="./akn:preamble/akn:formula">Für ein Gesetz muss eine Eingangsformel verwendet werden.</sch:assert>
         <sch:assert id="SCH-00020-010"
                     role="warn"
                     test="not(./akn:conclusions/akn:formula)">Für ein Gesetz in der Entwurfsfassung wird in der Regel keine Schlussformel benutzt.</sch:assert>
      </sch:rule>
      <sch:rule id="SCH-00030"
                context="/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri and $typ = ($typ-verordnung, $typ-vertragsverordnung)]">
         <sch:assert id="SCH-00030-005"
                     role="error"
                     test="not(./akn:preamble/akn:formula)">Eine Verordnung darf keine Eingangsformel enthalten.</sch:assert>
         <sch:assert id="SCH-00030-010"
                     role="warn"
                     test="not(./akn:conclusions/akn:formula)">Für eine Verordnung in der Entwurfsfassung wird in der Regel keine Schlussformel benutzt.</sch:assert>
      </sch:rule>
      <sch:rule id="SCH-00040"
                context="/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri and $typ = $typ-verwaltungsvorschrift]">
         <sch:assert id="SCH-00040-005"
                     role="error"
                     test="not(./akn:preamble/akn:formula)">Eine Verwaltungsvorschrift darf keine Eingangsformel enthalten.</sch:assert>
         <sch:assert id="SCH-00040-010"
                     role="warn"
                     test="not(./akn:conclusions/akn:formula)">Für eine Verwaltungsvorschrift in der Entwurfsfassung wird in der Regel keine Schlussformel benutzt.</sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:title xmlns:xsl="http://www.w3.org/1999/XSL/Transform">Regelungstext Verkündungsfassung/Neufassung</sch:title>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00048"
                context="/akn:akomaNtoso/akn:act[$fassung = $fassung-entwurfsfassung and @name = $art-regelungstext-uri and $form = $form-eingebundene-stammform]">
         <sch:assert id="SCH-00048-000" role="error" test="not(./akn:preface/akn:block)">Ein Regelungstext in Stammform in einer Mantelform darf keinen Datums-Container innerhalb des Dokumentenkopfes enthalten.</sch:assert>
         <sch:assert id="SCH-00048-005"
                     role="error"
                     test="not(./akn:preamble/akn:formula)">Ein Regelungstext in Stammform in einer Mantelform darf keine Eingangsformel enthalten.</sch:assert>
         <sch:assert id="SCH-00048-010" role="error" test="not(./akn:conclusions)">Ein Regelungstext in Stammform in einer Mantelform darf keinen Schlussteil enthalten.</sch:assert>
      </sch:rule>
      <sch:rule id="SCH-00049"
                context="/akn:akomaNtoso/akn:act[@name = $art-regelungstext-uri and $fassung = $fassung-neufassung]">
         <!--<sch:assert id="SCH-00049-005" role="error" test="not(./akn:preamble/akn:formula)">Ein Regelungstext in der Neufassung darf keine Eingangsformel enthalten.</sch:assert>-->
         <!--<sch:assert id="SCH-00049-010" role="error" test="not(./akn:conclusions)">Ein Regelungstext in der Neufassung darf keine Schlussformel enthalten.</sch:assert>-->
         <sch:assert id="SCH-00049-015" role="error" test="$form = $form-stammform">Ein Regelungstext als Neufassung darf nur in einer Stammform vorkommen.</sch:assert>
         <sch:assert id="SCH-00049-020" role="error" test="not(./akn:preface/akn:block)">Für einen Regelungstext in der Neufassung darf kein Datums-Container innerhalb des Dokumentenkopfes verwendet werden.</sch:assert>
      </sch:rule>
      <sch:rule id="SCH-00050"
                context="/akn:akomaNtoso/akn:act[$fassung = $fassung-entwurfsfassung and @name = $art-regelungstext-uri and $typ = ($typ-gesetz, $typ-vertragsgesetz)]">
         <sch:assert id="SCH-00050-005" role="error" test="./akn:preamble/akn:formula">Für ein Gesetz muss eine Eingangsformel verwendet werden.</sch:assert>
      </sch:rule>
      <sch:rule id="SCH-00060"
                context="/akn:akomaNtoso/akn:act[$fassung = $fassung-entwurfsfassung and @name = $art-regelungstext-uri and $typ = ($typ-verordnung, $typ-vertragsverordnung)]">
         <sch:assert id="SCH-00060-005"
                     role="error"
                     test="not(./akn:preamble/akn:formula)">Eine Verordnung darf keine Eingangsformel enthalten.</sch:assert>
      </sch:rule>
      <sch:rule id="SCH-00070"
                context="/akn:akomaNtoso/akn:act[$fassung = $fassung-entwurfsfassung and @name = $art-regelungstext-uri and $typ = $typ-verwaltungsvorschrift]">
         <sch:assert id="SCH-00070-005"
                     role="error"
                     test="not(./akn:preamble/akn:formula)">Eine Verwaltungsvorschrift darf keine Eingangsformel enthalten.</sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00071"
                context="/akn:akomaNtoso/akn:act[@name = $art-regelungstext-uri and $fassung = $fassung-verkündungsfassung and not($form = $form-eingebundene-stammform)]">
         <sch:assert id="SCH-00071-005" role="error" test="./akn:preface/akn:block">Für einen Regelungstext in der Verkündungsfassung muss ein Datums-Container innerhalb des Dokumentenkopfes verwendet werden. </sch:assert>
         <!--<sch:assert id="SCH-00071-010" role="error" test="./akn:conclusions/akn:blockContainer">Für einen Regelungstext in der Verkündungsfassung muss ein Signaturblock verwendet werden.</sch:assert>-->
      </sch:rule>
   </sch:pattern>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <!--<sch:rule id="SCH-00072" context="/akn:akomaNtoso/akn:act[@name = $art-regelungstext-uri and $typ = ($typ-gesetz) and not($fassung = $fassung-neufassung or $form = $form-eingebundene-stammform)]">
         <sch:assert id="SCH-00072-005" role="error" test="exists(./akn:conclusions/akn:formula)">Für einen Regelungstext in der Verkündungsfassung muss eine Schlussformel verwendet werden.</sch:assert>
      </sch:rule>-->
   </sch:pattern>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00073" context="akn:act/akn:conclusions/akn:formula">
         <sch:assert id="SCH-00073-005" test="empty(preceding-sibling::akn:*)">Die Schlussformel (akn:formula) muss das erste Element im Schluss des Regelungstextes (akn:conclusions) sein.</sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:title xmlns:xsl="http://www.w3.org/1999/XSL/Transform">Regelungstext Allgemein</sch:title>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00100"
                context="/akn:akomaNtoso/*[@name = $art-regelungstext-uri and $typ = ($typ-gesetz, $typ-vertragsgesetz)]/akn:preamble">
         <sch:assert id="SCH-00100-005" role="error" test="not(./akn:citations)">Ein Gesetz darf keine Ermächtigungsnorm enthalten.</sch:assert>
      </sch:rule>
      <sch:rule id="SCH-00110"
                context="/akn:akomaNtoso/*[@name = $art-regelungstext-uri and $typ = ($typ-verordnung, $typ-vertragsverordnung) and not($fassung = $fassung-neufassung)]/akn:preamble">
         <sch:assert id="SCH-00110-005" role="error" test="./akn:citations">Eine Verordnung muss Ermächtigungsnormen bereitstellen.</sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00120"
                context="akn:quotedStructure//akn:article [ $form = $form-mantelform and $typ = ($typ-gesetz, $typ-verordnung, $typ-satzung, $typ-verwaltungsvorschrift, $typ-vertragsgesetz, $typ-vertragsverordnung) and $fassung = ($fassung-entwurfsfassung, $fassung-verkündungsfassung) ]">
         <sch:assert id="SCH-00120-000"
                     test="(@refersTo = ($refersto-literal-mantelform, $refersto-literal-stammform, $refersto-literal-geltungszeitregel, $refersto-literal-geltungszeitregel-inkrafttreten, $refersto-literal-geltungszeitregel-ausserkrafttreten, $refersto-literal-vertragsgesetz, $refersto-literal-vertragsverordnung))">Wird innerhalb eines Änderungsbefehls eine Einzelvorschrift in Gänze geändert, neugefasst, hinzugefügt oder gelöscht, so muss diese näher - als Mantelform oder Stammform, als Geltungszeitregel oder als Vertragsgesetz bzw. Vertragsverordnung - bestimmt werden.</sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00121"
                context="akn:article[not(ancestor::akn:quotedStructure)]/@refersTo [ $form = $form-mantelform and $typ = ($typ-gesetz, $typ-verordnung, $typ-satzung, $typ-verwaltungsvorschrift, $typ-vertragsgesetz, $typ-vertragsverordnung) and $fassung = ($fassung-entwurfsfassung, $fassung-verkündungsfassung) ]">
         <sch:assert id="SCH-00121-000"
                     test=". = ($refersto-literal-hauptaenderung, $refersto-literal-folgeaenderung, $refersto-literal-eingebundene-stammform, $refersto-literal-geltungszeitregel, $refersto-literal-geltungszeitregel-inkrafttreten, $refersto-literal-geltungszeitregel-ausserkrafttreten)">Liegt ein Regelungstext in Mantelform vor und seine Einzelvorschriften sind mittels @refersTo näher bestimmt, so dürfen lediglich folgende Literale verwendet werden: "hauptaenderung", "folgeaenderung", "eingebundene-stammform", "geltungszeitregel", "geltungszeitregel-inkrafttreten", "geltungszeitregel-ausserkrafttreten".</sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00122"
                context="akn:article/@refersTo [ $form = $form-stammform and $typ = ($typ-gesetz, $typ-verordnung, $typ-satzung, $typ-verwaltungsvorschrift, $typ-vertragsgesetz, $typ-vertragsverordnung) and $fassung = ($fassung-entwurfsfassung, $fassung-verkündungsfassung, $fassung-neufassung) ]">
         <sch:assert id="SCH-00122-000"
                     test="(. = $refersto-literal-geltungszeitregel and count(//akn:article/@refersTo) = 1) or (. = $refersto-literal-geltungszeitregel-inkrafttreten and (every $r in //akn:article/@refersTo satisfies $r = $refersto-literal-geltungszeitregel-inkrafttreten or $r = $refersto-literal-geltungszeitregel-ausserkrafttreten))">In einem Regelungstext in Stammform darf entweder genau eine mit refersTo="geltungszeitregel" ausgezeichnete Einzelvorschrift und keine weiteren mit refersTo ausgezeichneten Einzelvorschriften haben oder genau eine mit refersTo="geltungszeitregel-inkrafttreten" ausgezeichnete Einzelvorschrift haben und optional eine mit refersTo="geltungszeitregel-ausserkrafttreten" ausgezeichnete Einzelvorschrift.</sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00123"
                context="akn:article[descendant::akn:mod and not(ancestor::akn:quotedStructure)] [ $form = $form-mantelform and $typ = ($typ-gesetz, $typ-verordnung, $typ-satzung, $typ-verwaltungsvorschrift, $typ-vertragsgesetz, $typ-vertragsverordnung) and $fassung = ($fassung-entwurfsfassung, $fassung-verkündungsfassung) ]">
         <sch:assert id="SCH-00123-000"
                     test="@refersTo = ($refersto-literal-hauptaenderung, $refersto-literal-folgeaenderung, $refersto-literal-geltungszeitregel, $refersto-literal-geltungszeitregel-inkrafttreten, $refersto-literal-geltungszeitregel-ausserkrafttreten)">Eine Einzelvorschrift, die einen Änderungsbefehl beinhaltet, muss entweder als Hauptänderung, Folgeänderung oder als Geltungszeit ausgezeichnet werden.</sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00125"
                context="/akn:akomaNtoso/akn:bill/akn:meta/akn:identification/akn:FRBRWork[$fassung = $fassung-entwurfsfassung]"
                subject="akn:FRBRdate[last()]">
         <sch:assert id="SCH-00125-000" test="count(akn:FRBRdate) eq 1"> In einer Entwurfsfassung darf das Datum auf Work-Ebene nur genau einmal vorkommen. </sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:title xmlns:xsl="http://www.w3.org/1999/XSL/Transform">Klasse Inhaltsverzeichnis</sch:title>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00130"
                context="/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri]/akn:preamble">
         <sch:report id="SCH-00130-005"
                     role="error"
                     test="count(./akn:blockContainer[@refersTo = 'inhaltsuebersicht']) gt 1">Es darf maximal eine Inhaltsübersicht geben.</sch:report>
         <sch:report id="SCH-00130-010"
                     role="error"
                     test="count(./akn:blockContainer[@refersTo = 'anlagenuebersicht']) gt 1">Es darf maximal eine Anlagenübersicht geben.</sch:report>
      </sch:rule>
   </sch:pattern>
   <sch:title xmlns:xsl="http://www.w3.org/1999/XSL/Transform">Klasse: Hauptteil</sch:title>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00150"
                context="/akn:akomaNtoso/*/akn:body[$form = $form-mantelform]">
         <sch:report id="SCH-00150-005"
                     role="error"
                     test="./akn:book or ./akn:part or ./akn:chapter or ./akn:subchapter or ./akn:section or ./akn:subsection or ./akn:title or ./akn:subtitle">Der Hauptteil einer Mantelform wird nicht in weitere Gliederungsabschnitte untergliedert.</sch:report>
      </sch:rule>
   </sch:pattern>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00160"
                context="/akn:akomaNtoso/*/akn:body[ $form = ($form-mantelform, $form-stammform) and $typ = ($typ-gesetz, $typ-verordnung, $typ-satzung, $typ-vertragsgesetz, $typ-vertragsverordnung) and $fassung = $fassung-entwurfsfassung ]">
         <sch:assert id="SCH-00160-010"
                     role="error"
                     test="(count(//akn:article[@refersTo = $refersto-literal-geltungszeitregel]) = 1 and empty(//akn:article[@refersTo = ($refersto-literal-geltungszeitregel-inkrafttreten, $refersto-literal-geltungszeitregel-ausserkrafttreten)])) or (count(//akn:article[@refersTo = $refersto-literal-geltungszeitregel-inkrafttreten]) = 1 and count(//akn:article[@refersTo = $refersto-literal-geltungszeitregel-ausserkrafttreten]) le 1 and empty(//akn:article[@refersTo = $refersto-literal-geltungszeitregel]))">Innerhalb eines Regelungstextes in der Entwurfsfassung <!--(sowohl in der Entwurfs- als auch der Verkündungsfassung)-->, der in Stamm- oder Mantelform vorliegt, muss es entweder genau eine Einzelvorschrift bzgl. der Geltungszeit (refersTo="geltungszeitregel") ODER eine Einzelvorschrift bzgl. des Inkrafttretens (refersTo="geltungszeitregel-inkrafttreten") und optional eine Geltungszeitregel bzgl. des Außerkrafttretens (refersTo="geltungszeitregel-ausserkrafttreten") geben.</sch:assert>
      </sch:rule>
      <sch:rule id="SCH-00170"
                context="/akn:akomaNtoso/*/akn:body[$form = $form-eingebundene-stammform]">
         <sch:assert id="SCH-00170-000"
                     test="if ($form = $form-eingebundene-stammform) then (count(//akn:article[@refersTo = ($refersto-literal-geltungszeitregel, $refersto-literal-geltungszeitregel-inkrafttreten)]) = 0) else ()">Ein Regelungstext als eingebundene Stammform darf keine Einzelvorschrift bezüglich der Geltungszeit bzw. bezüglich des Inkrafttretens besitzen.</sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00180"
                context="akn:article[@refersTo = $refersto-literal-geltungszeitregel]">
         <sch:report id="SCH-00180-000"
                     test="$fassung = $fassung-entwurfsfassung"
                     role="warn">Gemäß HdR 4 sollen Inkrafttreten und Außerkrafttreten in getrennten Einzelvorschriften gefasst werden, die entsprechend mit den refersTo-Literalen geltungszeitregel-inkrafttreten und geltungszeitregel-ausserkrafttreten auszuzeichnen sind. Das refersTo-Literal "geltungszeitregel" soll in Entwurfsfassungen nicht mehr verwendet werden.</sch:report>
      </sch:rule>
   </sch:pattern>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00185"
                context="akn:article[empty(ancestor::akn:quotedStructure) and @refersTo = $refersto-literal-geltungszeitregel-ausserkrafttreten]">
         <sch:report id="SCH-00185-000"
                     test="$form = ($form-eingebundene-stammform, $form-mantelform)">Eine Einzelvorschrift zum Außerkrafttreten darf es nur in einer Stammform bzw. eingebundenen Stammform geben.</sch:report>
      </sch:rule>
   </sch:pattern>
   <sch:title xmlns:xsl="http://www.w3.org/1999/XSL/Transform">Aufzählungen</sch:title>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00200"
                context="//akn:article/akn:paragraph//akn:list[$ist-entwurfsfassung]">
         <sch:report id="SCH-00200-005" role="warn" test="count(ancestor::akn:list) &gt; 4">Es ist maximal eine Vierfachuntergliederung von Sätzen erlaubt.</sch:report>
      </sch:rule>
   </sch:pattern>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00210" context="//akn:article[$ist-entwurfsfassung]">
         <sch:report id="SCH-00210-005" role="warn" test="count(./akn:paragraph) &gt; 6">Es ist maximal eine Sechsfachuntergliederung in Absätzen erlaubt.</sch:report>
      </sch:rule>
   </sch:pattern>
   <sch:title xmlns:xsl="http://www.w3.org/1999/XSL/Transform">Gliederungsebenen</sch:title>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00230" context="akn:book">
         <sch:report id="SCH-00230-005"
                     role="error"
                     test="akn:book[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]"
                     subject="akn:book[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]"> Innerhalb eines Gliederungsabschnitts "Buch" darf diese Gliederungsebene nicht verwendet werden.</sch:report>
      </sch:rule>
      <sch:rule id="SCH-00240" context="akn:part">
         <sch:report id="SCH-00240-005"
                     role="error"
                     test="(akn:book | akn:part)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]"
                     subject="(akn:book | akn:part)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]"> Innerhalb eines Gliederungsabschnitts "Teil" darf diese Gliederungsebene nicht verwendet werden.</sch:report>
      </sch:rule>
      <sch:rule id="SCH-00250" context="akn:chapter">
         <sch:report id="SCH-00250-005"
                     role="error"
                     test="(akn:book | akn:part | akn:chapter)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]"
                     subject="(akn:book | akn:part | akn:chapter)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]"> Innerhalb eines Gliederungsabschnitts "Kapitel" darf diese Gliederungsebene nicht verwendet werden.</sch:report>
      </sch:rule>
      <sch:rule id="SCH-00260" context="akn:subchapter">
         <sch:report id="SCH-00260-005"
                     role="error"
                     test="(akn:book | akn:part | akn:chapter | akn:subchapter)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]"
                     subject="(akn:book | akn:part | akn:chapter | akn:subchapter)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]"> Innerhalb eines Gliederungsabschnitts "Unterkapitel" darf diese Gliederungsebene nicht verwendet werden.</sch:report>
      </sch:rule>
      <sch:rule id="SCH-00270" context="akn:section">
         <sch:report id="SCH-00270-005"
                     role="error"
                     test="(akn:book | akn:part | akn:chapter | akn:subchapter | akn:section)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]"
                     subject="(akn:book | akn:part | akn:chapter | akn:subchapter | akn:section)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]"> Innerhalb eines Gliederungsabschnitts "Abschnitt" darf diese Gliederungsebene nicht verwendet werden.</sch:report>
      </sch:rule>
      <sch:rule id="SCH-00280" context="akn:subsection">
         <sch:report id="SCH-00280-005"
                     role="error"
                     test="(akn:book | akn:part | akn:chapter | akn:subchapter | akn:section | akn:subsection)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]"
                     subject="(akn:book | akn:part | akn:chapter | akn:subchapter | akn:section | akn:subsection)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]"> Innerhalb eines Gliederungsabschnitts "Unterabschnitt" darf diese Gliederungsebene nicht verwendet werden.</sch:report>
      </sch:rule>
      <sch:rule id="SCH-00290" context="akn:title">
         <sch:report id="SCH-00290-005"
                     role="error"
                     test="(akn:book | akn:part | akn:chapter | akn:subchapter | akn:section | akn:subsection | akn:title)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]"
                     subject="(akn:book | akn:part | akn:chapter | akn:subchapter | akn:section | akn:subsection | akn:title)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]">Innerhalb eines Gliederungsabschnitts "Titel" darf diese Gliederungsebene nicht verwendet werden.</sch:report>
      </sch:rule>
      <sch:rule id="SCH-00300" context="akn:subtitle">
         <sch:report id="SCH-00300-005"
                     role="error"
                     test="(akn:book | akn:part | akn:chapter | akn:subchapter | akn:section | akn:subsection | akn:title | akn:subtitle)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]"
                     subject="(akn:book | akn:part | akn:chapter | akn:subchapter | akn:section | akn:subsection | akn:title | akn:subtitle)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]">Innerhalb eines Gliederungsabschnitts "Untertitel" darf diese Gliederungsebene nicht verwendet werden.</sch:report>
      </sch:rule>
   </sch:pattern>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00305" context="regtxt:vomHdrAbweichendeGliederung">
         <sch:report id="SCH-00305-000"
                     role="warn"
                     test="count(//(akn:book | akn:part | akn:chapter | akn:subchapter | akn:section | akn:subsection | akn:title | akn:subtitle)[@refersTo = 'vom-hdr-abweichende-gliederungsebene']) = 0">Der Marker zur Auszeichnung einer von den Regelungen des HdR abweichenden Gliederung ist vorhanden, aber es wird keine solche Abweichungen (mittels <code>@refersTo='vom-hdr-abweichende-gliederungsebene')</code> ausgewiesen!</sch:report>
      </sch:rule>
      <sch:rule id="SCH-00306"
                context="(akn:book | akn:part | akn:chapter | akn:subchapter | akn:section | akn:subsection | akn:title | akn:subtitle)[@refersTo = 'vom-hdr-abweichende-gliederungsebene']">
         <sch:assert id="SCH-00306-000"
                     role="error"
                     test="exists(//regtxt:vomHdrAbweichendeGliederung)">Eine Gliederungsebene darf nur als von den Regelungen des HdR abweichend gekennzeichnet werden, wenn im Metadatenblock der instanz-weite Marker <code>&lt;meta:vomHdrAbweichendeGliederung&gt;</code> vorhanden ist.</sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:title xmlns:xsl="http://www.w3.org/1999/XSL/Transform">Schlussteil</sch:title>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00310"
                context="/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri]/akn:conclusions">
         <sch:report id="SCH-00310-005"
                     role="error"
                     test="./akn:blockContainer and $bearbeitende-institution-frbrauthor = 'recht.bund.de/institution/bundesregierung'">Der Signaturblock steht nur dem Bundestag in der Entwurfsfassung optional zur Verfügung.</sch:report>
         <sch:report id="SCH-00310-010"
                     role="error"
                     test="./akn:blockContainer and $bearbeitende-institution-frbrauthor = 'recht.bund.de/institution/bundesrat'">Der Signaturblock steht nur dem Bundestag in der Entwurfsfassung optional zur Verfügung.</sch:report>
      </sch:rule>
   </sch:pattern>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00320" context="akn:act/akn:conclusions/akn:blockContainer">
         <sch:assert role="warn" id="SCH-00320-000" test="count(akn:p) ge 2">Im Schlussteil sollen mindestens zwei akn:p enthalten sein (akn:p mit Ort und Datum; ein oder mehrere akn:p mit Unterschriften).</sch:assert>
      </sch:rule>
      <sch:rule id="SCH-00321"
                context="(akn:act | akn:bill)/akn:conclusions/akn:blockContainer/akn:p[1]">
         <sch:assert role="warn" id="SCH-00321-010" test="exists(akn:location)">Im Schlussteil soll im ersten akn:p ein Ort (akn:location) angegeben werden.</sch:assert>
         <sch:assert role="warn" id="SCH-00321-020" test="exists(akn:date)">Im Schlussteil soll im ersten akn:p ein Datum (akn:date) angegeben werden.</sch:assert>
      </sch:rule>
      <sch:rule id="SCH-00322"
                context="(akn:act | akn:bill)/akn:conclusions/akn:blockContainer/akn:p[position() ge 2]">
         <sch:assert role="warn" id="SCH-00322-000" test="exists(akn:signature)">Im Schlussteil soll ab dem zweiten akn:p eine Signatur(akn:signature) angegeben werden.</sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:title xmlns:xsl="http://www.w3.org/1999/XSL/Transform">Vorblatt (Regelungstext)</sch:title>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00330"
                context="//akn:doc[@name = $art-vorblatt-regelungstext-uri]/akn:mainBody">
         <sch:assert id="SCH-00330-005"
                     role="error"
                     test="exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-problem-und-ziel'])">Es muss der Vorblattabschnitt 'Problem und Ziel' verwendet werden.</sch:assert>
         <sch:assert id="SCH-00330-010"
                     role="error"
                     test="exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-loesung'])">Es muss der Vorblattabschnitt 'Lösung' verwendet werden.</sch:assert>
         <sch:assert id="SCH-00330-015"
                     role="error"
                     test="exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-alternativen'])">Es muss der Vorblattabschnitt 'Alternativen' verwendet werden.</sch:assert>
         <sch:assert id="SCH-00330-020"
                     role="error"
                     test="exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-haushaltsausgaben-ohne-erfuellungsaufwand'])">Es muss der Vorblattabschnitt 'Haushaltsausgaben ohne Erfüllungsaufwand' verwendet werden.</sch:assert>
         <sch:assert id="SCH-00330-025"
                     role="error"
                     test="exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-erfuellungsaufwand'])">Es muss der Vorblattabschnitt 'Erfüllungsaufwand' verwendet wird.</sch:assert>
         <sch:assert id="SCH-00330-030"
                     role="warn"
                     test="exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-erfuellungsaufwand']//akn:tblock[@refersTo = 'erfuellungsaufwand-fuer-die-wirtschaft'])"> Es wird empfohlen, dass innerhalb des Erfüllungsaufwands ein Unterabschnitt 'Erfüllungsaufwand Für die Wirtschaft' verwendet wird.</sch:assert>
         <sch:assert id="SCH-00330-035"
                     role="warn"
                     test="exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-erfuellungsaufwand']//akn:tblock[@refersTo = 'erfuellungsaufwand-fuer-die-wirtschaft']/akn:tblock[@refersTo = 'davon-buerokratiekosten-aus-informationspflichten'])">Es wird empfohlen, dass innerhalb des Erfüllungsaufwands für die Wirtschaft ein Unterabschnitt 'Davon Bürokratiekosten aus Informationspflichten' verwendet wird.</sch:assert>
         <sch:assert id="SCH-00330-040"
                     role="warn"
                     test="exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-erfuellungsaufwand']//akn:tblock[@refersTo = 'erfuellungsaufwand-fuer-buergerinnen-und-buerger'])"> Es wird empfohlen, dass innerhalb des Erfüllungsaufwands ein Unterabschnitt 'Erfüllungsaufwand Für Bürgerinnen und Bürger' verwendet wird.</sch:assert>
         <sch:assert id="SCH-00330-045"
                     role="warn"
                     test="exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-erfuellungsaufwand']//akn:tblock[@refersTo = 'erfuellungsaufwand-der-verwaltung'])"> Es wird empfohlen, dass innerhalb des Erfüllungsaufwands ein Unterabschnitt 'Erfüllungsaufwand der Verwaltung' verwendet wird.</sch:assert>
         <sch:assert id="SCH-00330-050"
                     role="error"
                     test="exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-weitere-kosten'])">Es muss der Vorblattabschnitt 'Weitere Kosten' verwendet werden.</sch:assert>
      </sch:rule>
      <sch:rule id="SCH-00335"
                context="//akn:doc[@name = $art-vorblatt-uri]/akn:mainBody/akn:hcontainer[@refersTo = $refersto-literal-vorblattabschnitt-erfüllungsaufwand]//akn:tblock">
         <sch:let name="zulässige-literale-zum-erfüllungsaufwand"
                  value="( 'erfuellungsaufwand-fuer-buergerinnen-und-buerger', 'erfuellungsaufwand-fuer-die-wirtschaft', 'davon-buerokratiekosten-aus-informationspflichten', 'erfuellungsaufwand-der-verwaltung' )"/>
         <sch:let name="mehr-als-ein-literal"
                  value="count($zulässige-literale-zum-erfüllungsaufwand) gt 1"/>
         <sch:assert id="SCH-00335-000"
                     role="error"
                     test="if (exists(@refersTo)) then (@refersTo = $zulässige-literale-zum-erfüllungsaufwand) else true()"> Im Kontext eines Vorblattabschnitts zum Erfüllungsaufwand kann das Literal '<sch:value-of select="@refersTo"/>' nicht verwendet werden; zulässig <sch:value-of select="if (xs:boolean($mehr-als-ein-literal)) then 'sind' else 'ist'"/> hier ausschließlich <sch:value-of select="if (xs:boolean($mehr-als-ein-literal)) then concat(string-join( for $literal in $zulässige-literale-zum-erfüllungsaufwand[position() lt last()] return concat('''', $literal, ''''), ', '), ' oder ', concat('''', $zulässige-literale-zum-erfüllungsaufwand[last()], '''') ) else concat('''', string-join($zulässige-literale-zum-erfüllungsaufwand), '''')"/>.</sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:title xmlns:xsl="http://www.w3.org/1999/XSL/Transform">Vorblatt (Beschlussempfehlung)</sch:title>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00336"
                context="//akn:doc[@name = $art-vorblatt-beschlussempfehlung-uri]/akn:mainBody">
         <sch:assert id="SCH-00336-005"
                     role="error"
                     test="exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-problem'])">Es muss der Vorblattabschnitt 'Problem' verwendet werden.</sch:assert>
         <sch:assert id="SCH-00336-006"
                     role="error"
                     test="exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-loesung'])">Es muss der Vorblattabschnitt 'Lösung' verwendet werden.</sch:assert>
         <sch:assert id="SCH-00336-007"
                     role="error"
                     test="exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-alternativen'])">Es muss der Vorblattabschnitt 'Alternativen' verwendet werden.</sch:assert>
         <sch:assert id="SCH-00336-008"
                     role="error"
                     test="exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-kosten'])">Es muss der Vorblattabschnitt 'Kosten' verwendet werden.</sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:title xmlns:xsl="http://www.w3.org/1999/XSL/Transform">Begründung</sch:title>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00340"
                context="/akn:akomaNtoso/akn:doc[@name = $art-begründung-uri and $typ = ($typ-gesetz, $typ-verordnung, $typ-verwaltungsvorschrift)]">
         <sch:assert id="SCH-00340-005"
                     role="error"
                     test="./akn:mainBody/akn:hcontainer[@refersTo = 'begruendung-allgemeiner-teil']"> Es muss ein allgemeiner Teil der Begründung vorliegen.</sch:assert>
         <sch:assert id="SCH-00340-010"
                     role="error"
                     test="./akn:mainBody/akn:hcontainer[@refersTo = 'begruendung-besonderer-teil']"> Es muss ein besonderer Teil der Begründung vorliegen.</sch:assert>
         <sch:report id="SCH-00340-015" role="error" test="./akn:conclusions"> Eine Schlussbemerkung wird nur innerhalb einer Begründung von Vertragsrechtsakten benutzt.</sch:report>
      </sch:rule>
      <sch:rule id="SCH-00345"
                context="//akn:doc[@name = $art-begründung-uri]/akn:mainBody//akn:hcontainer[@refersTo = 'begruendungsabschnitt-regelungsfolgen']//akn:tblock">
         <sch:let name="zulässige-literale-zu-regelungsfolgen"
                  value="( 'begruendung-erfuellungsaufwand-fuer-buergerinnen-und-buerger', 'begruendung-erfuellungsaufwand-fuer-die-wirtschaft', 'begruendung-erfuellungsaufwand-der-verwaltung', 'regelungsfolgen-abschnitt-rechts-und-verwaltungsvereinfachung', 'regelungsfolgen-abschnitt-nachhaltigkeitsaspekte', 'regelungsfolgen-abschnitt-erfuellungsaufwand', 'regelungsfolgen-abschnitt-weitere-kosten', 'regelungsfolgen-abschnitt-gleichstellungspolitische-relevanzpruefung', 'regelungsfolgen-abschnitt-haushaltsausgaben-ohne-erfuellungsaufwand', 'regelungsfolgen-abschnitt-weitere-regelungsfolgen' )"/>
         <sch:let name="mehr-als-ein-literal"
                  value="count($zulässige-literale-zu-regelungsfolgen) gt 1"/>
         <sch:assert id="SCH-00345-000"
                     role="error"
                     test="if (exists(@refersTo)) then (@refersTo = $zulässige-literale-zu-regelungsfolgen) else true()"> Im Kontext eines Begründungsabschnitt zu Regelungsfolgen kann das Literal '<sch:value-of select="@refersTo"/>' nicht verwendet werden; zulässig <sch:value-of select="if (xs:boolean($mehr-als-ein-literal)) then 'sind' else 'ist'"/> hier ausschließlich <sch:value-of select="if (xs:boolean($mehr-als-ein-literal)) then concat(string-join( for $literal in $zulässige-literale-zu-regelungsfolgen[position() lt last()] return concat('''', $literal, ''''), ', '), ' oder ', concat('''', $zulässige-literale-zu-regelungsfolgen[last()], '''') ) else concat('''', string-join($zulässige-literale-zu-regelungsfolgen), '''')"/>.</sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:title xmlns:xsl="http://www.w3.org/1999/XSL/Transform">Prüfkriterien</sch:title>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00350"
                context="//akn:doc[@name = $art-begründung-uri]/akn:mainBody/akn:hcontainer[@refersTo = 'begruendung-allgemeiner-teil']">
         <sch:assert id="SCH-00350-005"
                     role="warn"
                     test="exists(./akn:hcontainer[@refersTo = 'begruendungsabschnitt-zielsetzung-und-notwendigkeit'])"> Es wird empfohlen, dass ein Begründungsabschnitt 'Zielsetzung und Notwendigkeit' verwendet wird.</sch:assert>
         <sch:assert id="SCH-00350-010"
                     role="warn"
                     test="exists(./akn:hcontainer[@refersTo = 'begruendungsabschnitt-wesentlicher-inhalt'])"> Es wird empfohlen, dass ein Begründungsabschnitt 'Wesentlicher Inhalt' verwendet wird.</sch:assert>
         <sch:assert id="SCH-00350-015"
                     role="warn"
                     test="exists(./akn:hcontainer[@refersTo = 'begruendungsabschnitt-alternativen'])"> Es wird empfohlen, dass ein Begründungsabschnitt 'Alternativen' verwendet wird.</sch:assert>
         <sch:assert id="SCH-00350-020"
                     role="warn"
                     test="exists(./akn:hcontainer[@refersTo = 'begruendungsabschnitt-regelungskompetenz'])"> Es wird empfohlen, dass ein Begründungsabschnitt 'Regelungskompetenz' verwendet wird.</sch:assert>
         <sch:assert id="SCH-00350-025"
                     role="warn"
                     test="exists(./akn:hcontainer[@refersTo = 'begruendungsabschnitt-regelungsfolgen'])"> Es wird empfohlen, dass ein Begründungsabschnitt 'Regelungsfolgen' oder 'Gesetzesfolgen' verwendet wird.</sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:title xmlns:xsl="http://www.w3.org/1999/XSL/Transform">Prüfkriterien Allgemeiner Teil Regelungsfolgen</sch:title>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00360"
                context="//akn:doc[@name = $art-begründung-uri]/akn:mainBody/akn:hcontainer[@refersTo = 'begruendung-allgemeiner-teil']/akn:hcontainer[@refersTo = 'begruendungsabschnitt-regelungsfolgen']/akn:content">
         <sch:assert id="SCH-00360-005"
                     role="warn"
                     test="exists(./akn:tblock[@refersTo = 'regelungsfolgen-abschnitt-rechts-und-verwaltungsvereinfachung'])"> Es wird empfohlen, dass innerhalb der Regelungsfolgen ein Abschnitt 'Rechts und Verwaltungsvereinfachung' verwendet wird.</sch:assert>
         <sch:assert id="SCH-00360-010"
                     role="warn"
                     test="exists(./akn:tblock[@refersTo = 'regelungsfolgen-abschnitt-nachhaltigkeitsaspekte'])"> Es wird empfohlen, dass innerhalb der Regelungsfolgen ein Abschnitt 'Nachhaltigkeitsaspekte' verwendet wird.</sch:assert>
         <sch:assert id="SCH-00360-015"
                     role="warn"
                     test="exists(./akn:tblock[@refersTo = 'regelungsfolgen-abschnitt-erfuellungsaufwand'])"> Es wird empfohlen, dass innerhalb der Regelungsfolgen ein Abschnitt 'Erfuellungsaufwand' verwendet wird.</sch:assert>
         <sch:assert id="SCH-00360-020"
                     role="warn"
                     test="exists(./akn:tblock[@refersTo = 'regelungsfolgen-abschnitt-weitere-kosten'])"> Es wird empfohlen, dass innerhalb der Regelungsfolgen ein Abschnitt 'Weitere Kosten' verwendet wird.</sch:assert>
         <sch:assert id="SCH-00360-025"
                     role="warn"
                     test="exists(./akn:tblock[@refersTo = 'regelungsfolgen-abschnitt-gleichstellungspolitische-relevanzpruefung'])"> Es wird empfohlen, dass innerhalb der Regelungsfolgen ein Abschnitt 'Gleichstellungspolitische Relevanzprüfung' verwendet wird.</sch:assert>
         <sch:assert id="SCH-00360-030"
                     role="warn"
                     test="exists(./akn:tblock[@refersTo = 'regelungsfolgen-abschnitt-haushaltsausgaben-ohne-erfuellungsaufwand'])"> Es wird empfohlen, dass innerhalb der Regelungsfolgen ein Abschnitt 'Haushaltsausgaben ohne Erfüllungsaufwand' verwendet wird.</sch:assert>
         <sch:assert id="SCH-00360-035"
                     role="warn"
                     test="exists(./akn:tblock[@refersTo = 'regelungsfolgen-abschnitt-weitere-regelungsfolgen'])">Es wird empfohlen, dass innerhalb der Regelungsfolgen ein Abschnitt 'Weitere Regelungsfolgen' verwendet wird.</sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:title xmlns:xsl="http://www.w3.org/1999/XSL/Transform">Anschreiben</sch:title>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00380"
                context="/akn:akomaNtoso/akn:doc[@name = $art-anschreiben-uri]">
         <sch:report id="SCH-00380-005"
                     role="error"
                     test="$bearbeitende-institution-frbrauthor = ( 'recht.bund.de/institution/bundesregierung', 'recht.bund.de/institution/bundestag', 'recht.bund.de/institution/bundeskanzler', 'recht.bund.de/institution/bundespraesident' ) and ./akn:mainBody/akn:p/akn:date[@refersTo = 'fristablauf-datum']"> Das Fristablaufsdatum steht nur dem Bundesrat optional zur Verfügung.</sch:report>
      </sch:rule>
   </sch:pattern>
   <sch:title xmlns:xsl="http://www.w3.org/1999/XSL/Transform">Ausschussüberweisung/Legislaturperiode/Drucksachennummer</sch:title>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00390"
                context="/akn:akomaNtoso/akn:doc[@name = ($art-anschreiben-uri, $art-vorblatt-uri)]/akn:preface/akn:longTitle/akn:p">
         <sch:report id="SCH-00390-005"
                     role="error"
                     test="./akn:inline [@refersTo = $refersto-literal-ausschussueberweisung] and $bearbeitende-institution-frbrauthor = ( 'recht.bund.de/institution/bundesregierung', 'recht.bund.de/institution/bundestag', 'recht.bund.de/institution/bundeskanzler', 'recht.bund.de/institution/bundespraesident' )"> Eine Ausschussüberweisung steht nur dem Bundesrat optional zur Verfügung.</sch:report>
         <sch:report id="SCH-00390-020"
                     role="error"
                     test="./akn:docNumber and $bearbeitende-institution-frbrauthor = ( 'recht.bund.de/institution/bundesregierung', 'recht.bund.de/institution/bundeskanzler', 'recht.bund.de/institution/bundespraesident' )"> Die Drucksachennummer steht nur dem Bundestag/Bundesrat optional zur Verfügung.</sch:report>
      </sch:rule>
   </sch:pattern>
   <sch:title xmlns:xsl="http://www.w3.org/1999/XSL/Transform">Vereinbarung</sch:title>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00400"
                context="/akn:akomaNtoso/akn:act[@name = $art-vereinbarung-uri]/akn:body">
         <sch:assert id="SCH-00400-005"
                     role="error"
                     test="@refersTo = ( 'notenwechsel', 'vertrag', 'fakultativprotokoll' )"> Der Hauptteil einer Vereinbarung muss entweder ein Notenwechsel, ein Vertrag oder ein Fakultativprotokoll sein. </sch:assert>
         <sch:assert id="SCH-00400-010"
                     role="error"
                     test="count(./akn:hcontainer[@refersTo = 'verbindliche-sprachfassung']) &gt;= 1"> Es muss mindestens eine verbindliche Sprachfassung existieren. </sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:title xmlns:xsl="http://www.w3.org/1999/XSL/Transform">Änderungsbefehle</sch:title>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00420" context="//akn:textualMod[@type = 'insertion']">
         <sch:assert id="SCH-00420-005" role="error" test="./akn:destination/@pos">Wenn ein Änderungsbefehl eine Einfügung beinhaltet, muss in seinen Metadaten eine Positionsangabe mittels @pos angegeben werden.</sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00421"
                context="akn:article [ $form = ($form-stammform, $form-eingebundene-stammform) or $fassung = $fassung-neufassung or $typ = $typ-sonstige-bekanntmachung ]">
         <sch:assert id="SCH-00421-000" test="not(exists(descendant::akn:mod))">Änderungsbefehle dürfen nur im Rahmen einer Mantelform vorkommen.</sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00422" context="//akn:mod[not(ancestor::akn:mod)]">
         <sch:let name="alle-textänderungen"
                  value="/akn:akomaNtoso/*/akn:meta/akn:analysis/akn:activeModifications/(akn:textualMod, akn:forceMod)/akn:source"/>
         <sch:let name="gesuchte-änderungsbefehl-id" value="concat('#', @eId)"/>
         <sch:assert id="SCH-00422-000"
                     test="count($alle-textänderungen[@href = $gesuchte-änderungsbefehl-id]) = 1">Zu jedem Änderungsbefehl im Hauptteil (akn:mod) muss es im Metadatenblock genau eine zugehörige Text- oder Geltungszeitänderung geben. Es existiert jedoch keine solche Änderung (akn:textualMod bzw. akn:forceMod), deren Quellenangabe "<sch:value-of select="$gesuchte-änderungsbefehl-id"/>" referenziert.</sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00423"
                context="/akn:akomaNtoso/*/akn:meta/akn:analysis/akn:activeModifications/akn:textualMod/akn:source | /akn:akomaNtoso/*/akn:meta/akn:analysis/akn:activeModifications/akn:forceMod/akn:source">
         <sch:let name="referenzierte-änderungsbefehl-id"
                  value="substring(@href, string-length('#') + 1)"/>
         <sch:assert id="SCH-00423-000"
                     test="count(//akn:mod[@eId = $referenzierte-änderungsbefehl-id]) = 1">Zu jeder im Metadatenblock deklarierten Textänderung (akn:textualMod) oder Geltungszeitänderung (akn:forceMod) muss es im Hauptteil genau einen zugehörigen Änderungsbefehl (akn:mod) geben. Es existiert jedoch kein solcher Änderungsbefehl, dessen @eId "<sch:value-of select="$referenzierte-änderungsbefehl-id"/>" lautet.</sch:assert>
         <sch:assert id="SCH-00423-010" test="starts-with(@href, '#')"> Die Referenz auf die Quelle eines Änderungsbefehls innerhalb von akn:activeModifications ist stets ein interner Verweis; sie muss deshalb eine Raute ("#") als erstes Zeichen besitzen. </sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00424"
                context="akn:mod[@refersTo = 'aenderungsbefehl-umnummerierung']">
         <sch:assert id="SCH-00424-000"
                     test="exists(akn:span[@refersTo = 'neue-verweisangabe'])">Ein Umnummerierungsbefehl (akn:mod mit refers-to='aenderungsbefehl-umnummerierung') muss eine neue Verweisangabe (Element akn:span mit refers-to='neue-verweisangabe') enthalten.</sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00425" context="akn:span[@refersTo = 'neue-verweisangabe']">
         <sch:assert id="SCH-00425-000"
                     test="exists(parent::akn:mod[@refersTo = 'aenderungsbefehl-umnummerierung'])">Die Auszeichnung als neue Verweisangabe (akn:span mit refers-to='neue-verweisangabe') ist nur zulässig als Kindelement eines Umnummerierungsbefehls (akn:mod mit refers-to='aenderungsbefehl-umnummerierung').</sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00426" context="akn:mod[@refersTo and not(ancestor::akn:mod)]">
         <sch:let name="textualmod-type-insertion" value="'insertion'"/>
         <sch:let name="textualmod-type-substitution" value="'substitution'"/>
         <sch:let name="textualmod-type-repeal" value="'repeal'"/>
         <sch:let name="textualmod-type-renumbering" value="'renumbering'"/>
         <sch:assert id="SCH-00426-000"
                     test="if (@refersTo = 'aenderungsbefehl-einfuegen') then (//akn:textualMod[akn:source/@href = concat('#', current()/@eId) and @type = $textualmod-type-insertion]) else true()">Ein Änderungsbefehl mit @refersTo='<sch:value-of select="@refersTo"/>' muss im Metadatenblock mittels zugehörigem textualMod als @type='<sch:value-of select="$textualmod-type-insertion"/>' deklariert werden.</sch:assert>
         <sch:assert id="SCH-00426-005"
                     test="if (@refersTo = 'aenderungsbefehl-ersetzen') then (//akn:textualMod[akn:source/@href = concat('#', current()/@eId) and @type = $textualmod-type-substitution]) else true()">Ein Änderungsbefehl mit @refersTo='<sch:value-of select="@refersTo"/>' muss im Metadatenblock mittels zugehörigem textualMod als @type='<sch:value-of select="$textualmod-type-substitution"/>' deklariert werden. <sch:value-of select="if (not(empty(//akn:textualMod[akn:source/@href = concat('#', current()/@eId)]))) then (concat('Das zugehörige Metadatum lautet jedoch &#34;', normalize-space(concat(//akn:textualMod[akn:source/@href = concat('#', current()/@eId)]/@type, '&#34;!')))) else ()"/>
         </sch:assert>
         <sch:assert id="SCH-00426-010"
                     test="if (@refersTo = 'aenderungsbefehl-streichen') then (//akn:textualMod[akn:source/@href = concat('#', current()/@eId) and @type = $textualmod-type-repeal]) else true()">Ein Änderungsbefehl mit @refersTo='<sch:value-of select="@refersTo"/>' muss im Metadatenblock mittels zugehörigem textualMod als @type='<sch:value-of select="$textualmod-type-repeal"/>' deklariert werden. <sch:value-of select="if (not(empty(//akn:textualMod[akn:source/@href = concat('#', current()/@eId)]))) then (concat('Das zugehörige Metadatum lautet jedoch &#34;', normalize-space(concat(//akn:textualMod[akn:source/@href = concat('#', current()/@eId)]/@type, '&#34;!')))) else 'Dem vorliegenden Änderungbefehl ist jedoch überhaupt kein entsprechendes Metadatum (textualMod) zugeordnet!'"/>
         </sch:assert>
         <sch:assert id="SCH-00426-015"
                     test="if (@refersTo = 'aenderungsbefehl-umnummerierung') then (//akn:textualMod[akn:source/@href = concat('#', current()/@eId) and @type = $textualmod-type-renumbering]) else true()">Ein Änderungsbefehl mit @refersTo='<sch:value-of select="@refersTo"/>' muss im Metadatenblock mittels zugehörigem textualMod als @type='<sch:value-of select="$textualmod-type-renumbering"/>' deklariert werden. <sch:value-of select="if (not(empty(//akn:textualMod[akn:source/@href = concat('#', current()/@eId)]))) then (concat('Das zugehörige Metadatum lautet jedoch &#34;', normalize-space(concat(//akn:textualMod[akn:source/@href = concat('#', current()/@eId)]/@type, '&#34;!')))) else 'Dem vorliegenden Änderungbefehl ist jedoch überhaupt kein entsprechendes Metadatum (textualMod) zugeordnet!'"/>
         </sch:assert>
         <sch:assert id="SCH-00426-020"
                     test="if (@refersTo = 'aenderungsbefehl-neufassung') then (//akn:textualMod[akn:source/@href = concat('#', current()/@eId) and @type = $textualmod-type-substitution]) else true()">Ein Änderungsbefehl mit @refersTo='<sch:value-of select="@refersTo"/>' muss im Metadatenblock mittels zugehörigem textualMod als @type='<sch:value-of select="$textualmod-type-substitution"/>' deklariert werden. <sch:value-of select="if (not(empty(//akn:textualMod[akn:source/@href = concat('#', current()/@eId)]))) then (concat('Das zugehörige Metadatum lautet jedoch &#34;', normalize-space(concat(//akn:textualMod[akn:source/@href = concat('#', current()/@eId)]/@type, '&#34;!')))) else 'Dem vorliegenden Änderungbefehl ist jedoch überhaupt kein entsprechendes Metadatum (textualMod) zugeordnet!'"/>
         </sch:assert>
         <sch:assert id="SCH-00426-025"
                     test="if (@refersTo = 'aenderungsbefehl-ersetzen-weggefallen') then (//akn:textualMod[akn:source/@href = concat('#', current()/@eId) and @type = $textualmod-type-substitution]) else true()">Ein Änderungsbefehl mit @refersTo='<sch:value-of select="@refersTo"/>' muss im Metadatenblock mittels zugehörigem textualMod als @type='<sch:value-of select="$textualmod-type-substitution"/>' deklariert werden. <sch:value-of select="if (not(empty(//akn:textualMod[akn:source/@href = concat('#', current()/@eId)]))) then (concat('Das zugehörige Metadatum lautet jedoch &#34;', normalize-space(concat(//akn:textualMod[akn:source/@href = concat('#', current()/@eId)]/@type, '&#34;!')))) else 'Dem vorliegenden Änderungbefehl ist jedoch überhaupt kein entsprechendes Metadatum (textualMod) zugeordnet!'"/>
         </sch:assert>
         <sch:assert id="SCH-00426-030"
                     test="if (@refersTo = 'aenderungsbefehl-neufassung-weggefallen') then (//akn:textualMod[akn:source/@href = concat('#', current()/@eId) and @type = $textualmod-type-substitution]) else true()">Ein Änderungsbefehl mit @refersTo='<sch:value-of select="@refersTo"/>' muss im Metadatenblock mittels zugehörigem textualMod als @type='<sch:value-of select="$textualmod-type-substitution"/>' deklariert werden. <sch:value-of select="if (not(empty(//akn:textualMod[akn:source/@href = concat('#', current()/@eId)]))) then (concat('Das zugehörige Metadatum lautet jedoch &#34;', normalize-space(concat(//akn:textualMod[akn:source/@href = concat('#', current()/@eId)]/@type, '&#34;!')))) else 'Dem vorliegenden Änderungbefehl ist jedoch überhaupt kein entsprechendes Metadatum (textualMod) zugeordnet!'"/>
         </sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:title xmlns:xsl="http://www.w3.org/1999/XSL/Transform">Struktureller Aufbau von eId-Textknoten</sch:title>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00430"
                context="/akn:akomaNtoso/(akn:act | akn:bill | akn:doc | akn:statement | akn:documentCollection)//*[@eId and not(local-name() = ('article', 'quotedStructure')) and exists(parent::*[@eId])]">
         <sch:let name="trennzeichen-zwischen-eids" value="'_'"/>
         <sch:let name="geprüfte-eid" value="@eId"/>
         <sch:let name="geprüfte-eid-lokaler-teil"
                  value="tokenize(@eId, $trennzeichen-zwischen-eids)[last()]"/>
         <sch:let name="vorgänger-eid" value="ancestor::*[1]/@eId"/>
         <sch:assert id="SCH-00430-000"
                     role="error"
                     test="$geprüfte-eid = concat($vorgänger-eid, $trennzeichen-zwischen-eids, $geprüfte-eid-lokaler-teil)">Die @eId muss als Präfix vor ihrem lokalen Teil (hier: "<sch:value-of select="$geprüfte-eid-lokaler-teil"/>") die @eId ihres nächstgelegenen und ebenfalls über eine @eId verfügenden Vorgängers besitzen, verbunden durch das Zeichen ("<sch:value-of select="$trennzeichen-zwischen-eids"/>"). Erwartet wird im vorliegenden Fall damit konkret: "<sch:value-of select="concat($vorgänger-eid, $trennzeichen-zwischen-eids, $geprüfte-eid-lokaler-teil)"/>".</sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <!-- z-Typ-eIds müssen mittels der normalisierten Inhalte der betreffenden Art- und Zählbezeichnung gebildet werden -->
      <sch:rule id="SCH-000431"
                context="@eId[starts-with(tokenize(., '-')[last()], $präfix-eid-zitierbar) and parent::*/akn:num != '']">
         <sch:let name="positionsangabe-ist" value="tokenize(., '-')[last()]"/>
         <sch:let name="eingabe-textknoten" value="parent::*/akn:num/text()"/>
         <!-- Schritt 1: Weißraumnormalisierung und Umwandlung in Kleinbuchstaben -->
         <sch:let name="weißraumnormalisiert"
                  value="normalize-space(lower-case($eingabe-textknoten))"/>
         <!-- Schritt 2: ggf. Weglassen von '§ ', 'Art. ', 'Art ' und 'Artikel ' (in den Suchausdrücken kleingeschrieben, da bereits lower-case aus vorherigemn Schritt) -->
         <sch:let name="ohne-sonderzeichen"
                  value="replace($weißraumnormalisiert, '(§ )|(art\. )|(art )|(artikel )', '')"/>
         <!-- Schritt 3: Weglassen der Klammern bei Ausdrücken wie '(1)' oder '(1337bb)' -->
         <sch:let name="ohne-klammern"
                  value="replace($ohne-sonderzeichen, '(\()(\d+[a-z]*)(\))', '$2')"/>
         <!-- Schritt 4: Maskierung von -, _ und . als ~ -->
         <sch:let name="maskiert" value="translate($ohne-klammern, '-_.', '~~~')"/>
         <!-- Schritt 5: uri-Encoding -->
         <sch:let name="normalisierte-positionsangabe-eid"
                  value="encode-for-uri($maskiert)"/>
         <sch:let name="positionsangabe-soll"
                  value="lower-case(concat($präfix-eid-zitierbar, $normalisierte-positionsangabe-eid))"/>
         <sch:assert id="SCH-00431-005"
                     test="if (not(parent::*/akn:num[@refersTo = 'ordinale-zaehlung-eid'])) then $positionsangabe-ist = $positionsangabe-soll else true()">Eine "zitierbare" (mit dem Präfix "<sch:value-of select="$präfix-eid-zitierbar"/>" gebildete) @eId muss als Positionsangabe den normalisierten Wert der zum Elternelement zugehörigen Art- und Zählbezeichnung (akn:num) enthalten, sofern nicht dort deklariert ist, dass die Positionsangabe ordinal erfolgen soll. Im vorliegenden Fall wurde der betreffende Teil der @eId gebildet als "<sch:value-of select="$positionsangabe-ist"/>", während der Textknoten von akn:num "<sch:value-of select="parent::*/akn:num"/>" lautet. Erwartet würde damit die normalisierte Angabe "<sch:value-of select="$positionsangabe-soll"/>".</sch:assert>
      </sch:rule>
      <sch:rule id="SCH-00432"
                context="@eId[starts-with(tokenize(., '-')[last()], $präfix-eid-zitierbar)]">
         <sch:assert id="SCH-00432-000" test="exists(parent::*/akn:num)">Nur ein Element, das über eine Art- und Zählbezeichnung verfügt, kann eine "zitierbare" (mit Präfix "<sch:value-of select="$präfix-eid-zitierbar"/>" gebildete) @eId besitzen. Andernfalls ist Präfix "<sch:value-of select="$präfix-eid-nummerierbar"/>" zu verwenden.</sch:assert>
      </sch:rule>
   </sch:pattern>
   <!-- Wenn akn:num/@refersTo ist, dann darf das Elternelement kein z-Typ sein, sondern muss eine n-Typ-eId besitzen -->
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00433"
                context="akn:num[@refersTo = $literal-deklaration-ausnahme-eid-zählweise]"
                subject="parent::*">
         <sch:assert id="SCH-00433-000"
                     test="local-name(parent::*) = $zitierbare-elementtypen">Die Deklaration der ausnahmsweisen ordinalen Zählung eines Elementes, obwohl für es eine Art- und Zählbezeichnung vorhanden ist, darf nur für Einzelvorschriften (akn:article), juristische Absätze (akn:paragraph) oder Listenuntergliederungselemente (akn:point) vorgenommen werden.</sch:assert>
         <sch:assert id="SCH-00433-005"
                     test="starts-with(tokenize(parent::*/@eId, '-')[last()], $präfix-eid-nummerierbar)">Die @eId eines Elementes, für welches an seinem Kindelement akn:num mittels refersTo='<sch:value-of select="$literal-deklaration-ausnahme-eid-zählweise"/>' deklariert wurde, dass nicht die Art- und Zählbezeichnung zu verwenden sei, sondern eine ordinale Zählung vorzunehmen ist, muss im lokalen Teil seiner eId (hier: "<sch:value-of select="tokenize(parent::*/@eId, '_')[last()]"/>") das Präfix "<sch:value-of select="$präfix-eid-nummerierbar"/>" besitzen, nicht "<sch:value-of select="$präfix-eid-zitierbar"/>".</sch:assert>
      </sch:rule>
   </sch:pattern>
   <!-- Wenn n-Typ, dann ordinale Zählung (außer bei Einzelvorschriften außerhalb von quotedStructures: Diese global zählen): Positionsangabe muss zum Rang passen -->
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00434"
                context="*[@eId[starts-with(tokenize(., '-')[last()], $präfix-eid-nummerierbar)]]">
         <sch:let name="elementtyp-bezeichner" value="local-name(.)"/>
         <sch:let name="einzelvorschrift" value="'article'"/>
         <sch:let name="elementrang-soll"
                  value="if ($elementtyp-bezeichner = $einzelvorschrift and not(ancestor::akn:quotedStructure)) then (1 + count(preceding::*[local-name() eq $einzelvorschrift])) else (1 + count(preceding-sibling::*[local-name() eq $elementtyp-bezeichner]))"/>
         <sch:let name="lokaler-eId-teil" value="tokenize(@eId, '_')[last()]"/>
         <sch:let name="elementrang-ist"
                  value="xs:integer(tokenize($lokaler-eId-teil, concat('-', $präfix-eid-nummerierbar))[last()])"/>
         <sch:assert id="SCH-00434-000" test="$elementrang-ist = $elementrang-soll">Elemente, deren @eId im lokalen Teil (hier: "<sch:value-of select="$lokaler-eId-teil"/>) das Präfix "<sch:value-of select="$präfix-eid-nummerierbar"/>" enthalten, müssen ihre Positionsangabe mittels ordinaler Zählung bilden. Konkret würde hier als Postion [<sch:value-of select="$elementrang-soll"/>] erwartet, nicht [<sch:value-of select="$elementrang-ist"/>].</sch:assert>
      </sch:rule>
   </sch:pattern>
   <!-- Wenn akn:num vorhanden und keine Ausnahme qua @refersTo, dann kein n-Typ, sondern z-Typ! -->
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00435"
                context="*[local-name() = $zitierbare-elementtypen and @eId[starts-with(tokenize(., '-')[last()], $präfix-eid-nummerierbar)] and exists(akn:num)]">
         <sch:assert id="SCH-00435-000" test="akn:num/@refersTo = 'ordinale-zaehlung-eid'">Bei eIds, deren Positionsangabe mittels ordinaler Zählung erfolgt (n-Präfix), obwohl tatsächlich eine Art- und Zählbezeichnung vorhanden ist, muss an ihrem Kindelement akn:num explizit deklariert sein, dass die Zählung des Elternelements nichtsdestotrotz ordinal erfolgen soll.</sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:title xmlns:xsl="http://www.w3.org/1999/XSL/Transform">Dereferenzierbarkeit lokaler Verweise von akn:destinations innerhalb einer akn:passiveModification</sch:title>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00436"
                context="akn:passiveModifications/akn:textualMod/akn:destination/@href">
         <sch:assert id="SCH-00436-000" test="starts-with(., '#')">Die Zielangabe einer Textänderung innerhalb von "aenderungenPassiv" (also: akn:passiveModifications/akn:textualMod/akn:destination/@href) muss stets einen Verweis auf ein Element innerhalb des aktuellen Dokumentes enthalten. Solche lokalen Verweise beginnen zwingend mit einer Raute ("#").</sch:assert>
         <sch:let name="lokaler-verweis"
                  value="substring(., 2) (: das Rautesymbol des lokalen Verweises überspringen :)"/>
         <sch:let name="lokaler-verweis-ohne-zeichenbereich"
                  value="replace($lokaler-verweis, '(/\d+-\d+)$', '')"/>
         <sch:assert id="SCH-00436-010"
                     test="count(/akn:akomaNtoso//*[@eId eq $lokaler-verweis-ohne-zeichenbereich]) = 1">Verweise auf ein jeweiliges ELement sind innerhalb von passiveModifications grundsätzlich lokal. Zur hier angegebenen Referenz existiert jedoch im vorliegenden Dokument kein Element mit korrespondierender @eId ("<sch:value-of select="$lokaler-verweis-ohne-zeichenbereich"/>")!</sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <!-- dokumentweite eId-uniqueness -->
      <sch:rule id="SCH-00450" context="@eId">
         <sch:let name="kontext-eId-inhalt" value="."/>
         <sch:assert id="SCH-00450-000"
                     role="error"
                     test="count(key('nodes-by-eId', $kontext-eId-inhalt)) eq 1">Eine eId muss dokumentweit einmalig sein; eId "<sch:value-of select="$kontext-eId-inhalt"/>" kommt im vorliegenden Dokument jedoch <sch:value-of select="count(key('nodes-by-eId', $kontext-eId-inhalt))"/>-mal vor!</sch:assert>
      </sch:rule>
   </sch:pattern>
   <!-- dokumentweite GUID-uniqueness -->
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00460" context="@GUID">
         <sch:let name="kontext-guid-inhalt" value="."/>
         <sch:let name="häufigkeit-der-aktuellen-guid"
                  value="count(key('nodes-by-GUID', $kontext-guid-inhalt))"/>
         <sch:assert id="SCH-00460-000"
                     role="error"
                     test="xs:int($häufigkeit-der-aktuellen-guid) eq 1">GUIDs müssen einmalig sein; "<sch:value-of select="$kontext-guid-inhalt"/>" kommt jedoch <sch:value-of select="$häufigkeit-der-aktuellen-guid"/>-mal im Dokument vor!</sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:title xmlns:xsl="http://www.w3.org/1999/XSL/Transform">Struktureller Aufbau der ELI-Uris (Metadaten)</sch:title>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="eli-präfix-entwurfsfassung"
            value="'eli/dl'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="eli-präfix-verkündungsfassung"
            value="'eli/bund'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="eli-agent-entwurf"
            value="tokenize(/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRauthor/@href, '/')[last()]"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="eli-agent-verkündung"
            value="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRname/@value"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="eli-year"
            value="(: -- Nimmt als Wert die in der folgenden Reihenfolge gesuchte letzte (!) gefundene Jahresangabe an -- :) ( (: das erste Entwurfsfassung-FRBRdate; es darf nur eins geben! :) substring(/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRdate[@name = 'erstellungsdatum'][1]/@date, 1, 4), (: Jahr der Ausfertigung der Neufassung :) if (not(empty(/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRdate[@name = 'neufassung-ausfertigungsdatum']/@date))) then (substring(/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRdate[@name = 'neufassung-ausfertigungsdatum']/@date, 1, 4)) else (), (: Jahr der Ausfertigung der Verkündungsfassung :) if (not(empty(/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRdate[@name = 'verkuendungsfassung-ausfertigungsdatum']/@date))) then substring(/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRdate[@name = 'verkuendungsfassung-ausfertigungsdatum']/@date, 1, 4) else (), (: Jahr der Neufassung :) if (not(empty(/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRdate[@name = 'neufassung-verkuendungsdatum']/@date))) then substring(/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRdate[@name = 'neufassung-verkuendungsdatum']/@date, 1, 4) else (), (: Jahr der Verkündung :) if (not(empty(/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRdate[@name = 'verkuendungsfassung-verkuendungsdatum']/@date))) then (substring(/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRdate[@name = 'verkuendungsfassung-verkuendungsdatum']/@date, 1, 4)) else () )[last()]"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="eli-natural-identifier"
            value="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRnumber/@value"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="eli-process-identifier"
            value="$eli-natural-identifier"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="eli-type-of-legislative-process-document"
            value="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRname/@value"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="eli-point-in-time"
            value="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRExpression/akn:FRBRdate/@date"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="eli-version"
            value="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRExpression/akn:FRBRversionNumber/@value"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="eli-language"
            value="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRExpression/akn:FRBRlanguage/@language"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="eli-subtype"
            value="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRsubtype/@value"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="eli-format"
            value="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRManifestation/akn:FRBRformat/@value"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="eli-point-in-time-manifestation"
            value="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRManifestation/akn:FRBRdate/@date"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="eli-subagent"
            value="tokenize(/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRExpression/akn:FRBRauthor/@href, '/')[last()]"/>
   <!-- FRBRthis -->
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="FRBRthis-verkündungsfassung-work-beschreibung"
            value="'Der eindeutige Bezeichner für Teildokumente in der Verkündungsfassung auf der Work-Ebene'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="FRBRthis-verkündungsfassung-work-aufbau"
            value="concat($eli-präfix-verkündungsfassung, '/{agent}/{year}/{natural identifier}/{subtype}')"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="FRBRthis-verkündungsfassung-work-inhalt"
            value="string-join(($eli-präfix-verkündungsfassung, $eli-agent-verkündung, $eli-year, $eli-natural-identifier, $eli-subtype), '/')"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="FRBRthis-verkündungsfassung-expression-beschreibung"
            value="'Der eindeutige Bezeichner für Teildokumente in der Verkündungsfassung auf der Expression-Ebene'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="FRBRthis-verkündungsfassung-expression-aufbau"
            value="concat($eli-präfix-verkündungsfassung, '/{agent}/{year}/{natural identifier}/{point in time}/{version}/{language}/{subtype}')"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="FRBRthis-verkündungsfassung-expression-inhalt"
            value="string-join(($eli-präfix-verkündungsfassung, $eli-agent-verkündung, $eli-year, $eli-natural-identifier, $eli-point-in-time, $eli-version, $eli-language, $eli-subtype), '/')"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="FRBRthis-verkündungsfassung-manifestation-beschreibung"
            value="'Der eindeutige Bezeichner für Teildokumente in der Verkündungsfassung auf der Manifestation-Ebene'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="FRBRthis-verkündungsfassung-manifestation-aufbau"
            value="concat($eli-präfix-verkündungsfassung, '/{agent}/{year}/{natural identifier}/{point in time}/{version}/{language}/{point in time manifestation}/{subtype}.{format}')"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="FRBRthis-verkündungsfassung-manifestation-inhalt"
            value="string-join(($eli-präfix-verkündungsfassung, $eli-agent-verkündung, $eli-year, $eli-natural-identifier, $eli-point-in-time, $eli-version, $eli-language, $eli-point-in-time-manifestation, concat($eli-subtype, '.', $eli-format)), '/')"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="FRBRthis-entwurfsfassung-work-beschreibung"
            value="'Der eindeutige Bezeichner für Teildokumente in der Entwurfsfassung auf der Work-Ebene'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="FRBRthis-entwurfsfassung-work-aufbau"
            value="concat($eli-präfix-entwurfsfassung, '/{year}/{agent}/{process identifier}/{type of legislative process document}/{subtype}')"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="FRBRthis-entwurfsfassung-work-inhalt"
            value="string-join(($eli-präfix-entwurfsfassung, $eli-year, $eli-agent-entwurf, $eli-process-identifier, $eli-type-of-legislative-process-document, $eli-subtype), '/')"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="FRBRthis-entwurfsfassung-expression-beschreibung"
            value="'Der eindeutige Bezeichner für Teildokumente in der Entwurfsfassung auf der Expression-Ebene'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="FRBRthis-entwurfsfassung-expression-aufbau"
            value="concat($eli-präfix-entwurfsfassung, '/{year}/{agent}/{process identifier}/{type of legislative process document}/{subagent}/{point in time}/{version}/{language}/{subtype}')"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="FRBRthis-entwurfsfassung-expression-inhalt"
            value="string-join(($eli-präfix-entwurfsfassung, $eli-year, $eli-agent-entwurf, $eli-process-identifier, $eli-type-of-legislative-process-document, $eli-subagent, $eli-point-in-time, $eli-version, $eli-language, $eli-subtype), '/')"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="FRBRthis-entwurfsfassung-manifestation-beschreibung"
            value="'Der eindeutige Bezeichner für Teildokumente in der Entwurfsfassung auf der Manifestation-Ebene'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="FRBRthis-entwurfsfassung-manifestation-aufbau"
            value="concat($eli-präfix-entwurfsfassung, '/{year}/{agent}/{process identifier}/{type of legislative process document}/{subagent}/{point in time}/{version}/{language}/{subtype}.{format}')"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="FRBRthis-entwurfsfassung-manifestation-inhalt"
            value="string-join(($eli-präfix-entwurfsfassung, $eli-year, $eli-agent-entwurf, $eli-process-identifier, $eli-type-of-legislative-process-document, $eli-subagent, $eli-point-in-time, $eli-version, $eli-language, concat($eli-subtype, '.', $eli-format)), '/')"/>
   <!-- FRBRuri -->
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="FRBRuri-verkündungsfassung-work-beschreibung"
            value="'Der eindeutige Bezeichner für die Work-Ebene in der Verkündungsfassung'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="FRBRuri-verkündungsfassung-work-aufbau"
            value="concat($eli-präfix-verkündungsfassung, '/{agent}/{year}/{natural identifier}')"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="FRBRuri-verkündungsfassung-work-inhalt"
            value="string-join(($eli-präfix-verkündungsfassung, $eli-agent-verkündung, $eli-year, $eli-natural-identifier), '/')"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="FRBRuri-verkündungsfassung-expression-beschreibung"
            value="'Der eindeutige Bezeichner für die Expression-Ebene in der Verkündungsfassung'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="FRBRuri-verkündungsfassung-expression-aufbau"
            value="concat($eli-präfix-verkündungsfassung, '{agent}/{year}/{natural identifier}/{point in time}/{version}/{language}')"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="FRBRuri-verkündungsfassung-expression-inhalt"
            value="string-join(($eli-präfix-verkündungsfassung, $eli-agent-verkündung, $eli-year, $eli-natural-identifier, $eli-point-in-time, $eli-version, $eli-language), '/')"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="FRBRuri-verkündungsfassung-manifestation-beschreibung"
            value="'Der eindeutige Bezeichner für die Manifestation-Ebene in der Verkündungsfassung'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="FRBRuri-verkündungsfassung-manifestation-aufbau"
            value="$FRBRthis-verkündungsfassung-manifestation-aufbau"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="FRBRuri-verkündungsfassung-manifestation-inhalt"
            value="$FRBRthis-verkündungsfassung-manifestation-inhalt"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="FRBRuri-entwurfsfassung-work-beschreibung"
            value="'Der eindeutige Bezeichner für die Work-Ebene in der Entwurfsfassung'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="FRBRuri-entwurfsfassung-work-aufbau"
            value="concat($eli-präfix-entwurfsfassung, '/{year}/{agent}/{process identifier}/{type of legislative process document}')"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="FRBRuri-entwurfsfassung-work-inhalt"
            value="string-join(($eli-präfix-entwurfsfassung, $eli-year, $eli-agent-entwurf, $eli-process-identifier, $eli-type-of-legislative-process-document), '/')"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="FRBRuri-entwurfsfassung-expression-beschreibung"
            value="'Der eindeutige Bezeichner für die Expression-Ebene in der Entwurfsfassung'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="FRBRuri-entwurfsfassung-expression-aufbau"
            value="concat($eli-präfix-entwurfsfassung, '/{year}/{agent}/{process identifier}/{type of legislative process document}/{subagent}/{point in time}/{version}/{language}')"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="FRBRuri-entwurfsfassung-expression-inhalt"
            value="string-join(($eli-präfix-entwurfsfassung, $eli-year, $eli-agent-entwurf, $eli-process-identifier, $eli-type-of-legislative-process-document, $eli-subagent, $eli-point-in-time, $eli-version, $eli-language), '/')"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="FRBRuri-entwurfsfassung-manifestation-beschreibung"
            value="'Der eindeutige Bezeichner für die Manifestation-Ebene in der Entwurfsfassung'"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="FRBRuri-entwurfsfassung-manifestation-aufbau"
            value="concat($eli-präfix-entwurfsfassung, '/{year}/{agent}/{process identifier}/{type of legislative process document}/{subagent}/{point in time}/{version}/{language}/{subtype}.{format}')"/>
   <sch:let xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            name="FRBRuri-entwurfsfassung-manifestation-inhalt"
            value="string-join(($eli-präfix-entwurfsfassung, $eli-year, $eli-agent-entwurf, $eli-process-identifier, $eli-type-of-legislative-process-document, $eli-subagent, $eli-point-in-time, $eli-version, $eli-language, concat($eli-subtype, '.', $eli-format)), '/')"/>
   <!-- Regeln für die Zusammensetzung der ELI-Uris -->
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <!-- FRBRthis: Work-Ebene -->
      <sch:rule id="SCH-00500"
                context="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRthis">
         <sch:assert id="SCH-00500-005"
                     role="error"
                     test="if ($ist-entwurfsfassung) then (@value = $FRBRthis-entwurfsfassung-work-inhalt) else true()">
            <sch:value-of select="$FRBRthis-entwurfsfassung-work-beschreibung"/> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<sch:value-of select="$FRBRthis-entwurfsfassung-work-aufbau"/>". Erwartet würde hier konkret: "<sch:value-of select="$FRBRthis-entwurfsfassung-work-inhalt"/>". </sch:assert>
         <sch:assert id="SCH-00500-010"
                     role="error"
                     test="if ($ist-verkündungsfassung) then (@value = $FRBRthis-verkündungsfassung-work-inhalt) else true()">
            <sch:value-of select="$FRBRthis-verkündungsfassung-work-beschreibung"/> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<sch:value-of select="$FRBRthis-verkündungsfassung-work-aufbau"/>". Erwartet würde hier konkret: "<sch:value-of select="$FRBRthis-verkündungsfassung-work-inhalt"/>".</sch:assert>
      </sch:rule>
      <!-- FRBRthis: Expression-Ebene -->
      <sch:rule id="SCH-00510"
                context="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRExpression/akn:FRBRthis">
         <sch:assert id="SCH-00510-005"
                     role="error"
                     test="if ($ist-entwurfsfassung) then (@value = $FRBRthis-entwurfsfassung-expression-inhalt) else true()">
            <sch:value-of select="$FRBRthis-entwurfsfassung-expression-beschreibung"/> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<sch:value-of select="$FRBRthis-entwurfsfassung-expression-aufbau"/>". Erwartet würde hier konkret: "<sch:value-of select="$FRBRthis-entwurfsfassung-expression-inhalt"/>". </sch:assert>
         <sch:assert id="SCH-00510-010"
                     role="error"
                     test="if ($ist-verkündungsfassung) then (@value = $FRBRthis-verkündungsfassung-expression-inhalt) else true()">
            <sch:value-of select="$FRBRthis-verkündungsfassung-expression-beschreibung"/> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<sch:value-of select="$FRBRthis-verkündungsfassung-expression-aufbau"/>". Erwartet würde hier konkret: "<sch:value-of select="$FRBRthis-verkündungsfassung-expression-inhalt"/>".</sch:assert>
      </sch:rule>
      <!-- FRBRthis: Manifestation-Ebene -->
      <sch:rule id="SCH-00520"
                context="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRManifestation/akn:FRBRthis">
         <sch:assert id="SCH-00520-005"
                     role="error"
                     test="if ($ist-entwurfsfassung) then (@value = $FRBRthis-entwurfsfassung-manifestation-inhalt) else true()">
            <sch:value-of select="$FRBRthis-entwurfsfassung-manifestation-beschreibung"/> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<sch:value-of select="$FRBRthis-entwurfsfassung-manifestation-aufbau"/>". Erwartet würde hier konkret: "<sch:value-of select="$FRBRthis-entwurfsfassung-manifestation-inhalt"/>".</sch:assert>
         <sch:assert id="SCH-00520-010"
                     role="error"
                     test="if ($ist-verkündungsfassung) then (@value = $FRBRthis-verkündungsfassung-manifestation-inhalt) else true()">
            <sch:value-of select="$FRBRthis-verkündungsfassung-manifestation-beschreibung"/> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<sch:value-of select="$FRBRthis-verkündungsfassung-manifestation-aufbau"/>". Erwartet würde hier konkret: "<sch:value-of select="$FRBRthis-verkündungsfassung-manifestation-inhalt"/>".</sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <!-- FRBRuri: Work-Ebene -->
      <sch:rule id="SCH-00530"
                context="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRuri">
         <sch:assert id="SCH-00530-005"
                     role="error"
                     test="if ($ist-entwurfsfassung) then (@value = $FRBRuri-entwurfsfassung-work-inhalt) else true()">
            <sch:value-of select="$FRBRuri-entwurfsfassung-work-beschreibung"/> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<sch:value-of select="$FRBRuri-entwurfsfassung-work-aufbau"/>". Erwartet würde hier konkret: "<sch:value-of select="$FRBRuri-entwurfsfassung-work-inhalt"/>".</sch:assert>
         <sch:assert id="SCH-00530-010"
                     role="error"
                     test="if ($ist-verkündungsfassung) then (@value = $FRBRuri-verkündungsfassung-work-inhalt) else true()">
            <sch:value-of select="$FRBRuri-verkündungsfassung-work-beschreibung"/> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<sch:value-of select="$FRBRuri-verkündungsfassung-work-aufbau"/>". Erwartet würde hier konkret: "<sch:value-of select="$FRBRuri-verkündungsfassung-work-inhalt"/>".</sch:assert>
      </sch:rule>
      <!-- FRBRuri: Expression-Ebene -->
      <sch:rule id="SCH-00540"
                context="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRExpression/akn:FRBRuri">
         <sch:assert id="SCH-00540-005"
                     role="error"
                     test="if ($ist-entwurfsfassung) then (@value = $FRBRuri-entwurfsfassung-expression-inhalt) else true()">
            <sch:value-of select="$FRBRuri-entwurfsfassung-expression-beschreibung"/> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<sch:value-of select="$FRBRuri-entwurfsfassung-expression-aufbau"/>". Erwartet würde hier konkret: "<sch:value-of select="$FRBRuri-entwurfsfassung-expression-inhalt"/>".</sch:assert>
         <sch:assert id="SCH-00540-010"
                     role="error"
                     test="if ($ist-verkündungsfassung) then (@value = $FRBRuri-verkündungsfassung-expression-inhalt) else true()">
            <sch:value-of select="$FRBRuri-verkündungsfassung-expression-beschreibung"/> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<sch:value-of select="$FRBRuri-verkündungsfassung-expression-aufbau"/>". Erwartet würde hier konkret: "<sch:value-of select="$FRBRuri-verkündungsfassung-expression-inhalt"/>".</sch:assert>
      </sch:rule>
      <!-- FRBRdate: Expression-Ebene: Unbekanntes Inkrafttreten -->
      <sch:rule id="SCH-00541"
                context="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRExpression/akn:FRBRdate">
         <sch:let name="unbekanntes-inkrafttretensdatum-aenderung-literal"
                  value="'aenderung-unbestimmtes-inkrafttreten'"/>
         <sch:let name="unbekanntes-inkrafttretensdatum-verkuendung-literal"
                  value="'verkuendung-unbestimmtes-inkrafttreten'"/>
         <sch:assert id="SCH-00541-000"
                     test="if (@name = ($unbekanntes-inkrafttretensdatum-aenderung-literal, $unbekanntes-inkrafttretensdatum-verkuendung-literal)) then (@date = $platzhalter-datum-unbekannt) else true()"
                     role="error">Ist bei einer Verkündungsfassung das Inkrafttretensdatum unbekannt, muss als Wert für @date der Platzhalter '<sch:value-of select="$platzhalter-datum-unbekannt"/>' angegeben werden.</sch:assert>
         <sch:assert id="SCH-00541-005"
                     test="if (@date = $platzhalter-datum-unbekannt) then (@name = ($unbekanntes-inkrafttretensdatum-aenderung-literal, $unbekanntes-inkrafttretensdatum-verkuendung-literal)) else true()"
                     role="error">Der Platzhalter '<sch:value-of select="$platzhalter-datum-unbekannt"/>' darf in @date nur angegeben werden, wenn das Datum mittels @name='<sch:value-of select="$unbekanntes-inkrafttretensdatum-aenderung-literal"/>' oder '<sch:value-of select="$unbekanntes-inkrafttretensdatum-verkuendung-literal"/>' als unbekanntes Inkrafttretensdatum deklariert ist.</sch:assert>
      </sch:rule>
      <!-- FRBRuri: Manifestation-Ebene -->
      <sch:rule id="SCH-00550"
                context="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRManifestation/akn:FRBRuri">
         <sch:assert id="SCH-00550-005"
                     role="error"
                     test="if ($ist-entwurfsfassung) then (@value = $FRBRuri-entwurfsfassung-manifestation-inhalt) else true()">
            <sch:value-of select="$FRBRuri-entwurfsfassung-manifestation-beschreibung"/> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<sch:value-of select="$FRBRuri-entwurfsfassung-manifestation-aufbau"/>". Erwartet würde hier konkret: "<sch:value-of select="$FRBRuri-entwurfsfassung-manifestation-inhalt"/>".</sch:assert>
         <sch:assert id="SCH-00550-010"
                     role="error"
                     test="if ($ist-verkündungsfassung) then (@value = $FRBRuri-verkündungsfassung-manifestation-inhalt) else true()">
            <sch:value-of select="$FRBRuri-verkündungsfassung-manifestation-beschreibung"/> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<sch:value-of select="$FRBRuri-verkündungsfassung-manifestation-aufbau"/>". Erwartet würde hier konkret: "<sch:value-of select="$FRBRuri-verkündungsfassung-manifestation-inhalt"/>".</sch:assert>
      </sch:rule>
      <!-- akn:FRBRdate/@name auf Work-Ebene ist abhängig von Verkündungs- vs. Entwurfsfassung -->
      <sch:rule id="SCH-00560" context="/akn:akomaNtoso/akn:act/@name" role="error">
         <sch:assert id="SCH-00560-005"
                     test="if (. = 'regelungstext') then $ist-verkündungsfassung else true()"> Ein Regelungstext in der Verkündungsfassung darf nicht als Entwurfsfassung gekennzeichnet sein, wie es jedoch aktuell anhand von akn:FRBRWork/akn:FRBRdate/@name deklariert ist.</sch:assert>
      </sch:rule>
   </sch:pattern>
   <!-- Metadaten: proprietary -->
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00590"
                context="akn:meta/akn:proprietary/redok:legalDocML.de_metadaten">
         <sch:assert id="SCH-00590-000"
                     subject="redok:fna"
                     test="if ($fassung = $fassung-entwurfsfassung) then (redok:fna = 'nicht-vorhanden' (: das Literal ist im Metadatenmodell als @default des einfachen Typs xs:token umgesetzt, daher hier als Literal anstatt einer dynamischen Referenzierung :)) else true()">In der Entwurfsfassung muss als Wert für den Fundstellennachweis das Literal "nicht-vorhanden" angegeben werden.</sch:assert>
      </sch:rule>
      <!-- Das Rechtsetzungsdokument muss abhängig davon, wer bearbeitende Institution ist, über bestimmte Metadaten-Schemata-Einbindungen verfügen -->
      <sch:rule id="SCH-00591"
                context="akn:meta[$teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/rechtsetzungsdokument']">

         <!-- Das Rechtsetzungsdokument muss stets einen Metadatenblock Rechtsetzungsdokument besitzen -->
         <sch:assert id="SCH-00591-000"
                     role="error"
                     test="count(akn:proprietary/redok:legalDocML.de_metadaten) eq 1">Ein Rechtsetzungsdokument muss genau einen Block mit "Metadaten Rechtsetzungsdokument" besitzen.</sch:assert>
         <sch:let name="bearbeitende-institution"
                  value="akn:proprietary/redok:legalDocML.de_metadaten/redok:bearbeitendeInstitution"/>
         <!-- Metadaten Bundestag -->
         <sch:assert id="SCH-00591-005"
                     role="error"
                     test="if ($bearbeitende-institution = 'bundestag') then (count(akn:proprietary/btag:legalDocML.de_metadaten) eq 1) else true()">Wenn der Bundestag bearbeitende Institution ist, müssen dessen Metadaten genau einmal angegeben werden.</sch:assert>
         <!-- Metadaten Bundesrat -->
         <sch:assert id="SCH-00591-010"
                     role="error"
                     test="if ($bearbeitende-institution = 'bundesrat') then (count(akn:proprietary/brat:legalDocML.de_metadaten) eq 1) else true()">Wenn der Bundesrat bearbeitende Institution ist, müssen dessen Metadaten genau einmal angegeben werden.</sch:assert>
         <!-- Metadaten Bundesregierung -->
         <sch:assert id="SCH-00591-015"
                     role="error"
                     test="if ($bearbeitende-institution = 'bundesregierung') then (count(akn:proprietary/breg:legalDocML.de_metadaten) eq 1) else true()">Wenn die Bundesregierung bearbeitende Institution ist, müssen deren Metadaten genau einmal angegeben werden.</sch:assert>
      </sch:rule>
      <!-- Ein Regelungstext muss über einen Metadatenblock Regelungstext verfügen -->
      <sch:rule id="SCH-00592"
                context="akn:meta[$teildokument-uri = ('/akn/ontology/de/concept/documenttype/bund/regelungstext-entwurf', '/akn/ontology/de/concept/documenttype/bund/regelungstext-verkuendung', '/akn/ontology/de/concept/documenttype/bund/regelungstext-neufassung')]">
         <sch:assert id="SCH-00592-000"
                     test="count(akn:proprietary/regtxt:legalDocML.de_metadaten) eq 1">Ein Regelungstext muss über genau einen Metadatenblock Regelungstext verfügen.</sch:assert>
      </sch:rule>
      <!-- Die NKR-Stellungnahme muss den Metadatenblock Normenkontrollrat enthalten  -->
      <sch:rule id="SCH-00593"
                context="akn:meta[$teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/nkr-stellungnahme']">
         <sch:assert id="SCH-00593-000"
                     test="count(akn:proprietary/nkr:legalDocML.de_metadaten) eq 1">Die NKR-Stellungnahme muss genau einen Metadatenblock Normenkontrollrat enthalten.</sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00594" context="akn:meta">
         <!-- Metadaten Bundestag: Nur im Rechtsetzungsdokument -->
         <sch:assert id="SCH-00594-000"
                     role="error"
                     test="if (akn:proprietary/btag:legalDocML.de_metadaten) then ($teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/rechtsetzungsdokument') else true()">Das Metadatenschema Bundestag darf nur im Rechtsetzungsdokument eingebunden werden.</sch:assert>
         <!-- Metadaten Bundesrat: Nur im Rechtsetzungsdokument -->
         <sch:assert id="SCH-00594-005"
                     role="error"
                     test="if (akn:proprietary/brat:legalDocML.de_metadaten) then ($teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/rechtsetzungsdokument') else true()">Das Metadatenschema Bundesrat darf nur im Rechtsetzungsdokument eingebunden werden.</sch:assert>
         <!-- Metadaten Bundesregierung: Nur im Rechtsetzungsdokument -->
         <sch:assert id="SCH-00594-010"
                     role="error"
                     test="if (akn:proprietary/breg:legalDocML.de_metadaten) then ($teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/rechtsetzungsdokument') else true()">Das Metadatenschema Bundesregierung darf nur im Rechtsetzungsdokument eingebunden werden.</sch:assert>
         <!-- Metadaten NKR: Nur in NKR-Stellungnahme -->
         <sch:assert id="SCH-00594-015"
                     test="if (akn:proprietary/nkr:legalDocML.de_metadaten) then ($teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/nkr-stellungnahme') else true()">Das Metadatenschema Normenkontrollrat darf nur in der NKR-Stellungnahme eingebunden werden.</sch:assert>
         <!-- Metadaten Rechtsetzungsdokument: Nur im Rechtsetzungsdokument -->
         <sch:assert id="SCH-00594-020"
                     test="if (akn:proprietary/redok:legalDocML.de_metadaten) then ($teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/rechtsetzungsdokument') else true()">Das Metadatenschema Rechtsetzungsdokument darf nur im Rechtsetzungsdokument eingebunden werden.</sch:assert>
         <!-- Metadaten Formulierungshilfe: Nur in begruendung-regelungstext, regelungstext-entwurf oder vorblatt-regelungstext -->
         <sch:assert id="SCH-00594-025"
                     test="if (akn:proprietary/fhilf:legalDocML.de_metadaten) then ($teildokument-uri = ('/akn/ontology/de/concept/documenttype/bund/begruendung-regelungstext', '/akn/ontology/de/concept/documenttype/bund/regelungstext-entwurf', '/akn/ontology/de/concept/documenttype/bund/vorblatt-regelungstext')) else true()">Das Metadatenschema Formulierungshilfe darf nur in einer Begründung zu einem Regelungstext, in einem Regelungstext (Entwurf) oder dem Vorblatt zu einem Regelungstext eingebunden werden.</sch:assert>
         <!-- Metadaten Regelungstext: Nur in regelungstext-entwurf, regelungstext-verkuendung oder regelungstext-neufassung -->
         <sch:assert id="SCH-00594-030"
                     test="if (akn:proprietary/regtxt:legalDocML.de_metadaten) then ($teildokument-uri = ('/akn/ontology/de/concept/documenttype/bund/regelungstext-entwurf', '/akn/ontology/de/concept/documenttype/bund/regelungstext-verkuendung', '/akn/ontology/de/concept/documenttype/bund/regelungstext-neufassung')) else true()">Das Metadatenschema Regelungstext darf nur in einem Regelungstext (Entwurf), einem Regelungstext (Verkündung) oder Regelungstext (Neufassung) eingebunden werden.</sch:assert>
      </sch:rule>
   </sch:pattern>
   <!-- Regeln zur Geltungszeit -->
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00600" context="akn:timeInterval">
         <sch:let name="beginn-geltungszeitintervall-uri" value="@start"/>
         <sch:let name="ende-geltungszeitintervall-uri" value="@end"/>
         <sch:let name="beginn-geltungszeitintervall"
                  value="ancestor::akn:meta/akn:lifecycle/akn:eventRef[@eId = substring($beginn-geltungszeitintervall-uri, 2)]/@date"/>
         <sch:let name="ende-geltungszeitintervall"
                  value="ancestor::akn:meta/akn:lifecycle/akn:eventRef[@eId = substring($ende-geltungszeitintervall-uri, 2)]/@date"/>
         <sch:assert id="SCH-00600-000" test="not(exists(@end) and exists(@duration))">Die Attribute @end und @duration schließen einander aus: Es darf nur entweder eine Dauer oder ein Endzeitpunkt angegeben werden, aber nicht beides.</sch:assert>
         <sch:assert id="SCH-00600-005"
                     test="substring($beginn-geltungszeitintervall-uri, 2) = /akn:akomaNtoso/*/akn:meta/akn:lifecycle/akn:eventRef/@eId">Der Verweis auf den Beginn des Geltungsintervalls muss auf eine in der vorliegenden Instanz vorhandene @eId einer Ereignis-Deklaration sein. Für den Verweis "<sch:value-of select="$beginn-geltungszeitintervall-uri"/>" existiert jedoch kein passendes Verweisziel.</sch:assert>
         <sch:assert id="SCH-00600-010"
                     test="if (not(empty(@end))) then (substring($ende-geltungszeitintervall-uri, 2) = /akn:akomaNtoso/*/akn:meta/akn:lifecycle/akn:eventRef/@eId) else true()">Der Verweis auf das Ende des Geltungsintervalls muss auf eine in der vorliegenden Instanz vorhandene @eId einer Ereignis-Deklaration sein. Für den Verweis "<sch:value-of select="$ende-geltungszeitintervall-uri"/>" existiert jedoch kein passendes Verweisziel.</sch:assert>
         <sch:assert id="SCH-00600-015"
                     test="if (not(empty($beginn-geltungszeitintervall)) and not(empty($ende-geltungszeitintervall))) then (xs:date($beginn-geltungszeitintervall) le xs:date($ende-geltungszeitintervall)) else true()">Das Ende des Geltungszeitintervalls darf zeitlich nicht vor seinem Beginn liegen. Es sind jedoch als Beginn "<sch:value-of select="$beginn-geltungszeitintervall"/>" und als Ende "<sch:value-of select="$ende-geltungszeitintervall"/>" angegeben.</sch:assert>
      </sch:rule>
      <sch:rule id="SCH-00610"
                context="(akn:activeModifications | akn:passiveModifications)/akn:textualMod/akn:force | akn:article[@period] | akn:paragraph[@period] | akn:list[@period]">
         <sch:let name="verweis-auf-geltungszeitgruppe" value="substring(@period, 2)"/>
         <sch:let name="geltungszeitgruppen-im-dokument"
                  value="/akn:akomaNtoso/*/akn:meta/akn:temporalData/akn:temporalGroup/@eId"/>
         <sch:assert id="SCH-00610-000"
                     test="$verweis-auf-geltungszeitgruppe = $geltungszeitgruppen-im-dokument">Der angegebene Wert "<sch:value-of select="concat('#', $verweis-auf-geltungszeitgruppe)"/>" ist kein gültiger Verweis auf eine Geltungszeitgruppe in diesem Dokument. <sch:value-of select="if (not(empty($geltungszeitgruppen-im-dokument))) then if (count($geltungszeitgruppen-im-dokument) gt 2) then concat(' Technisch mögliche Angaben wären im aktuellen Dokument: ', (string-join( for $geltungszeitgruppe in $geltungszeitgruppen-im-dokument[position() lt last()] return concat('&#34;#', $geltungszeitgruppe, '&#34;'), ', ')), ' oder &#34;#', $geltungszeitgruppen-im-dokument[last()], '&#34;.') else concat(' Die technisch einzig mögliche Angabe ist im aktuellen Dokument &#34;#', $geltungszeitgruppen-im-dokument, '&#34;.') else ()"/>
         </sch:assert>
      </sch:rule>
      <sch:rule id="SCH-00620" context="akn:eventRef">
         <sch:let name="ereignisart" value="@refersTo"/>
         <sch:let name="ereignisdatum" value="@date"/>
         <sch:let name="literalsuffix-für-unbekanntes-datum"
                  value="'-mit-noch-unbekanntem-datum'"/>
         <sch:assert id="SCH-00620-000"
                     test="if ($ereignisdatum = $platzhalter-datum-unbekannt) then (ends-with($ereignisart, $literalsuffix-für-unbekanntes-datum)) else true()">Bei Angabe eines unbekannten Ereignisdatums (durch Verwendung des Platzhaltertextes "<sch:value-of select="$platzhalter-datum-unbekannt"/>") muss das zur näheren Bestimmung der Ereignisart im Attribut @refersTo angegebene Literal auf den Ausdruck "<sch:value-of select="$literalsuffix-für-unbekanntes-datum"/>" enden.</sch:assert>
         <sch:assert id="SCH-00620-005"
                     test="if (ends-with($ereignisart, $literalsuffix-für-unbekanntes-datum)) then ($ereignisdatum = $platzhalter-datum-unbekannt) else true()">Für ein Ereignis, dessen Datum noch unbekannt ist (hier: @refersTo = "<sch:value-of select="$ereignisart"/>"), muss in seinem Attribut @date als Platzhalterwert "<sch:value-of select="$platzhalter-datum-unbekannt"/>" angegeben werden.</sch:assert>
      </sch:rule>
   </sch:pattern>
   <!-- Regeln zur Versionierung (Identifikation von Vorgänger- aktueller und Nachfolger-Version) -->
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00630" context="akn:FRBRExpression">
         <sch:let name="guid-vorherige-version" value="'vorherige-version-id'"/>
         <sch:let name="guid-aktuelle-version" value="'aktuelle-version-id'"/>
         <sch:let name="guid-nächste-version" value="'nachfolgende-version-id'"/>
         <sch:assert id="SCH-00630-000"
                     subject="akn:FRBRalias[@name = $guid-vorherige-version]"
                     test="count(akn:FRBRalias[@name = $guid-vorherige-version]) le 1">Es darf höchstens einen einzigen Verweis auf eine Vorgängerversion geben.</sch:assert>
         <sch:assert id="SCH-00630-005"
                     subject="akn:FRBRalias[@name = $guid-aktuelle-version]"
                     test="count(akn:FRBRalias[@name = $guid-aktuelle-version]) eq 1">Es muss genau einen Identifikator (GUID) für die vorliegende Version geben.</sch:assert>
         <sch:assert id="SCH-00630-015"
                     subject="akn:FRBRalias"
                     test="count(akn:FRBRalias) eq count(distinct-values(akn:FRBRalias/@value))">Sämtliche mittels @value angegebenen GUIDs der Versionen müssen sich voneinander unterscheiden."/&gt;</sch:assert>
      </sch:rule>
   </sch:pattern>
   <!-- Regeln zum Lebenszyklus -->
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:let name="zulässige-literale-in-kombination-mit-repeal"
               value="($refersto-literal-ereignisreferenz-verkündungsfassung-ausserkrafttreten, $refersto-literal-ereignisreferenz-entwurfsfassung-ausserkrafttreten, $refersto-literal-ereignisreferenz-entwurfsfassung-ausserkrafttreten-mit-unbekanntem-datum, $refersto-literal-ereignisreferenz-verkündungssfassung-ausserkrafttreten-mit-unbekanntem-datum)"/>
      <sch:rule id="SCH-00640"
                context="akn:meta/akn:lifecycle (: Entwurfs- und Verkündungsfassung sowie Neufassung :)">
         <sch:assert id="SCH-00640-010"
                     test="count(akn:eventRef[@type = $type-literal-ereignisreferenz-repeal and @refersTo = $zulässige-literale-in-kombination-mit-repeal]) le 1">
            <sch:value-of select="$dokumentarten-mit-lebenszyklus-angaben-formulierung-satzanfang-nominativ"/> kann nicht mehr als ein Außerkraftsetzen (&lt;eventRef;&gt; mit @type='<sch:value-of select="$type-literal-ereignisreferenz-repeal"/>') enthalten, da das Rechtsetzungsartefakt dadurch in Gänze aufgehoben wird.</sch:assert>
         <sch:assert id="SCH-00640-015"
                     test="count(akn:eventRef[@type = $type-literal-ereignisreferenz-repeal and @refersTo = $zulässige-literale-in-kombination-mit-repeal]) le 1">Für die Deklaration eines Außerkrafttretens (&lt;eventRef&gt; mit @type='<sch:value-of select="$type-literal-ereignisreferenz-repeal"/>') ist als @refersTo-Angabe ausschließlich <sch:value-of select="string-join(distinct-values(for $literal in $zulässige-literale-in-kombination-mit-repeal return concat('''', $literal, '''')), ' oder ')"/> zulässig.</sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00650"
                context="akn:meta/akn:lifecycle[$fassung = $fassung-entwurfsfassung]">
         <sch:assert id="SCH-00650-000"
                     test="if ($teildokument-uri = ($art-regelungstext-uri, $art-vereinbarung-uri, $art-bekanntmachungstext-uri)) then (akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-entwurfsfassung-ausfertigung-mit-unbekanntem-datum] and (akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-entwurfsfassung-inkrafttreten-mit-unbekanntem-datum] or akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-entwurfsfassung-inkrafttreten])) else true()">
            <sch:value-of select="$dokumentarten-mit-lebenszyklus-angaben-formulierung-satzanfang-nominativ"/> in der Entwurfsfassung muss immer mindestens zwei Ereignisse auszeichnen: Erstens einen Platzhalter für das Ausfertigungsdatum; dieser wird angegeben mittels &lt;eventRef&gt; mit @type='<sch:value-of select="$type-literal-ereignisreferenz-generation"/>' und @refersTo='<sch:value-of select="$refersto-literal-ereignisreferenz-entwurfsfassung-ausfertigung-mit-unbekanntem-datum"/>'. Und zweitens eine Angabe zum Inkrafttreten mittels &lt;eventRef&gt; mit @type='<sch:value-of select="$type-literal-ereignisreferenz-generation"/> und @refersTo='<sch:value-of select="$refersto-literal-ereignisreferenz-entwurfsfassung-inkrafttreten-mit-unbekanntem-datum"/>' bzw., sofern das Datum bereits bekannt ist, @refersTo='<sch:value-of select="$refersto-literal-ereignisreferenz-entwurfsfassung-inkrafttreten"/>'.</sch:assert>
         <sch:assert id="SCH-00650-005"
                     test="if ($fassung = $fassung-entwurfsfassung) then (akn:eventRef[@type = ($type-literal-ereignisreferenz-generation, $type-literal-ereignisreferenz-repeal)]) else ()">
            <sch:value-of select="$dokumentarten-mit-lebenszyklus-angaben-formulierung-satzanfang-nominativ"/> in der Entwurfsfassung kann nur initiale Ereignisse (Inkrafttreten, Ausfertigung, teilweises Außerkrafttreten, d.h. @type = '<sch:value-of select="$type-literal-ereignisreferenz-generation"/>') oder ein finales Außerkrafttreten (d.h. @type = '<sch:value-of select="$type-literal-ereignisreferenz-repeal"/>') aufweisen.</sch:assert>
         <sch:assert id="SCH-00650-010"
                     test="if ($teildokument-uri = ($art-regelungstext-uri, $art-vereinbarung-uri, $art-bekanntmachungstext-uri)) then (count(akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-entwurfsfassung-ausfertigung-mit-unbekanntem-datum]) = 1) else true()">
            <sch:value-of select="$dokumentarten-mit-lebenszyklus-angaben-formulierung-satzanfang-nominativ"/> in der Entwurfsfassung muss genau einen Platzhalter für das noch unbekannte Datum der Ausfertigung enthalten (&lt;eventRef&gt; mit @type = '<sch:value-of select="$type-literal-ereignisreferenz-generation"/>' und @refersTo = '<sch:value-of select="$refersto-literal-ereignisreferenz-entwurfsfassung-ausfertigung-mit-unbekanntem-datum"/>').</sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00660"
                context="akn:meta/akn:lifecycle[$fassung = ($fassung-verkündungsfassung, $fassung-neufassung)] (: Verkündungfassung oder Neufassung :)">
         <sch:assert id="SCH-00660-000"
                     test="akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-ausfertigung] and ( akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten] or akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-grundsaetzlich] or akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-abweichend] or akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-mit-unbekanntem-datum] )">
            <sch:value-of select="$dokumentarten-mit-lebenszyklus-angaben-formulierung-satzanfang-nominativ"/> in der Verkündungsfassung muss immer mindestens zwei Ereignisse auszeichnen: Erstens das Ausfertigungsdatum; dieses wird angegeben mittels &lt;eventRef&gt; mit @type='<sch:value-of select="$type-literal-ereignisreferenz-generation"/>' und @refersTo='<sch:value-of select="$refersto-literal-ereignisreferenz-verkündungsfassung-ausfertigung"/>'. Und zweitens eine Angabe zum Inkrafttreten mittels &lt;eventRef&gt; mit @type='<sch:value-of select="$type-literal-ereignisreferenz-generation"/> und @refersTo='<sch:value-of select="$refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten"/>' bzw. bei grundsätzlichem oder abweichendem Inkrafttreten @refersTo='<sch:value-of select="$refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-grundsaetzlich"/>' oder @refersTo='<sch:value-of select="$refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-abweichend"/>'. Oder sofern das Datum noch unbekannt ist, @refersTo='<sch:value-of select="$refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-mit-unbekanntem-datum"/>'.</sch:assert>
         <sch:assert id="SCH-00660-005"
                     test="count(akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-ausfertigung]) = 1">
            <sch:value-of select="$dokumentarten-mit-lebenszyklus-angaben-formulierung-satzanfang-nominativ"/> in der Verkündungsfassung muss genau ein konkretes Ausfertigungsdatum enthalten (&lt;eventRef&gt; mit @type = '<sch:value-of select="$type-literal-ereignisreferenz-generation"/>' und @refersTo = '<sch:value-of select="$refersto-literal-ereignisreferenz-verkündungsfassung-ausfertigung"/>').</sch:assert>
         <sch:let name="datum-ausfertigung"
                  value="(akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-ausfertigung]) (: immer den ersten Wert nehmen, falls es unerwartet mehrere gibt, damit hier diejenige SCH-Regel zum Tragen kommt, die prüft, dass es nur genau ein initiales Ausfertigungsdatum gibt, und nicht ein Fehler auf Ebene des SCH-Prozessors die weitere Verarbeitung blockiert. :)[1]/@date"/>
         <sch:let name="frühestes-datum-inkrafttreten-als-reine-ziffern"
                  value="min(for $n in akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten]/@date return format-date($n, '[Y,4][M,2][D,2]'))"/>
         <sch:let name="datum-inkrafttreten"
                  value="if (not(empty($frühestes-datum-inkrafttreten-als-reine-ziffern))) then (xs:date(concat( substring($frühestes-datum-inkrafttreten-als-reine-ziffern, 1, 4), '-', substring($frühestes-datum-inkrafttreten-als-reine-ziffern, 5, 2), '-', substring($frühestes-datum-inkrafttreten-als-reine-ziffern, 7, 2)))) else '0001-01-01'"/>
         <sch:let name="datum-ausserkafttreten"
                  value="akn:eventRef[@type = $type-literal-ereignisreferenz-repeal and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-ausserkrafttreten]/@date"/>
         <sch:assert id="SCH-00660-015"
                     test="if (akn:eventRef[@type = $type-literal-ereignisreferenz-repeal and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-ausserkrafttreten]) then (xs:date($datum-ausserkafttreten) gt xs:date($datum-ausfertigung)) else true()">Das Datum des Außerkrafttretens muss nach der Ausfertigung liegen; angegeben wurden jedoch für das Außerkrafttreten '<sch:value-of select="$datum-ausserkafttreten"/>' und für die Ausfertigung '<sch:value-of select="$datum-ausfertigung"/>'.</sch:assert>
         <sch:let name="frühestes-datum-amendment-als-reine-ziffern"
                  value="min(for $n in akn:eventRef[@type = $type-literal-ereignisreferenz-amendment (: inkl. aller möglichen @refersTo :)]/@date return format-date($n, '[Y,4][M,2][D,2]'))"/>
         <sch:let name="frühestes-datum-amendment"
                  value="if (not(empty($frühestes-datum-amendment-als-reine-ziffern))) then (xs:date(concat( substring($frühestes-datum-amendment-als-reine-ziffern, 1, 4), '-', substring($frühestes-datum-amendment-als-reine-ziffern, 5, 2), '-', substring($frühestes-datum-amendment-als-reine-ziffern, 7, 2)))) else '0001-01-01'"/>
         <sch:let name="ausfertigungsdatum"
                  value="(akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-ausfertigung]/@date)[1]"/>
         <sch:assert id="SCH-00660-020"
                     test="if (not(xs:date($frühestes-datum-amendment) = xs:date('0001-01-01'))) then (xs:date($frühestes-datum-amendment) gt xs:date($ausfertigungsdatum)) else true()">
            <sch:value-of select="$dokumentarten-mit-lebenszyklus-angaben-formulierung-satzanfang-nominativ"/> können erst geändert werden, nachdem er/sie initial ausgefertigt wurde (d. h. die früheste Änderung - akn:eventRef[@type = '<sch:value-of select="$type-literal-ereignisreferenz-amendment"/>']/@date - muss nach der initialen Ausfertigung - &lt;eventRef&gt; mit @type = '<sch:value-of select="$type-literal-ereignisreferenz-generation"/>' und @refersTo = '<sch:value-of select="$refersto-literal-ereignisreferenz-verkündungsfassung-ausfertigung"/>' - erfolgen).</sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00665"
                context="akn:meta/akn:lifecycle/akn:eventRef [$fassung = ($fassung-verkündungsfassung, $fassung-neufassung) and @type = $type-literal-ereignisreferenz-generation ]">
         <sch:assert id="SCH-00665-025"
                     test="if (@refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-grundsaetzlich) then not( preceding-sibling::akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-abweichend]/@date eq current()/@date or following-sibling::akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-abweichend]/@date eq current()/@date ) else if (@refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-abweichend) then not( preceding-sibling::akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-grundsaetzlich]/@date eq current()/@date or following-sibling::akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-grundsaetzlich]/@date eq current()/@date ) else true()">Das grundsätzliche und das abweichende Inkrafttretensdatum dürfen nicht identisch sein.</sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00670"
                context="akn:meta/akn:lifecycle[$fassung = $fassung-neufassung and $teildokument-uri = $art-regelungstext-uri] (: nur Neufassung :)">
         <sch:assert id="SCH-00670-000"
                     test="count(akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-neufassung]) ge 1">Ein Regelungstext als Neufassung muss mindestens ein Neufassungsereignis enthalten (&lt;eventRef&gt; mit @type = '<sch:value-of select="$type-literal-ereignisreferenz-generation"/>' und @refersTo = '<sch:value-of select="$refersto-literal-ereignisreferenz-neufassung"/>'.).</sch:assert>
         <sch:let name="frühestes-datum-neufassung-als-reine-ziffern"
                  value="min(for $n in akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = 'neufassung']/@date return format-date($n, '[Y,4][M,2][D,2]'))"/>
         <sch:let name="früheste-neufassung"
                  value="if (not(empty($frühestes-datum-neufassung-als-reine-ziffern))) then (xs:date(concat( substring($frühestes-datum-neufassung-als-reine-ziffern, 1, 4), '-', substring($frühestes-datum-neufassung-als-reine-ziffern, 5, 2), '-', substring($frühestes-datum-neufassung-als-reine-ziffern, 7, 2)))) else '0001-01-01'"/>
         <sch:let name="ausfertigung"
                  value="akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-ausfertigung]/@date"/>
         <sch:assert id="SCH-00670-005"
                     test="xs:date($früheste-neufassung) gt xs:date($ausfertigung)">Das Datum der frühesten Neufassung (&lt;eventRef&gt; mit @type='<sch:value-of select="$type-literal-ereignisreferenz-generation"/>' und @rfersTo='<sch:value-of select="$refersto-literal-ereignisreferenz-neufassung"/>') muss nach dem initialen Ausfertigungsdatum (&lt;eventRef&gt; mit @type='<sch:value-of select="$type-literal-ereignisreferenz-generation"/>' und @refersTo='<sch:value-of select="$refersto-literal-ereignisreferenz-verkündungsfassung-ausfertigung"/>') liegen; angegeben wurden jedoch als Ausfertigungsdatum '<sch:value-of select="$ausfertigung"/>' und als Datum der Neufassung '<sch:value-of select="$früheste-neufassung"/>'.</sch:assert>
      </sch:rule>
   </sch:pattern>
   <!-- Regeln zu Kardinalitäten von Inline-Elementen -->
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00680"
                context="akn:p[parent::akn:longTitle and $teildokument-uri = $art-regelungstext-uri]">
         <sch:assert id="SCH-00680-000"
                     subject="parent::akn:longTitle"
                     test="count(akn:docTitle) = 1">Ein dokumentenkopfTitel (akn:longtitle) muss genau einen Dokumententitel (akn:docTitle) besitzen.</sch:assert>
      </sch:rule>
   </sch:pattern>
   <!-- Sitzung (z. B. eines Ausschusses) -->
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00730" context="akn:session" subject="@refersTo">
         <sch:assert id="SCH-00730-000"
                     test="starts-with(@refersTo, '#') and substring(@refersTo, 2) = //akn:organization/@eId">Es muss einen lokalen Verweis auf eine Organisation geben, deren Sitzung ausgezeichnet wird. Dieser besteht aus einer Raute (#), gefolgt von der @eId der betreffenden akn:organization.</sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:title xmlns:xsl="http://www.w3.org/1999/XSL/Transform">Verwendung von Markern</sch:title>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00802" context="akn:marker[@refersTo = 'satzende']">
         <sch:assert id="SCH-00802-000" test="empty(@name)">Ein akn:marker mit @refersTo='satzende' darf kein Attribut @name haben.</sch:assert>
      </sch:rule>
      <sch:rule id="SCH-00803" context="akn:inline/@name">
         <!-- akn:inline/@name ist mandatorisch; ein fixes Literal kann aber mit XSD 1.0 auf Schemaebene nicht konditional (nämlich genau dann, wenn nicht @refersTo="neuris" gilt) vergeben werden; daher wird dieser Aspekt behelfsweise in Schematronm geregelt; vgl. spezifikation#660. -->
         <sch:assert id="SCH-00803-000"
                     test="if (not(parent::*/@refersTo = 'neuris')) then (. = 'attributsemantik-noch-undefiniert') else true()">Das Attribut @name darf für akn:inline nur dann mit Freitext befüllt werden, wenn @refersTo='neuris' gegeben ist.</sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:title xmlns:xsl="http://www.w3.org/1999/XSL/Transform"> Regeln zu Listen in Regelungstexten </sch:title>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00810"
                context="(akn:act | akn:bill)[akn:meta/akn:proprietary/regtxt:legalDocML.de_metadaten/regtxt:form = ($form-stammform, $form-eingebundene-stammform)]//akn:list">
         <sch:assert id="SCH-00810-000" test="false()">Das Element akn:list darf in Stammformen nicht verwendet werden.</sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:title xmlns:xsl="http://www.w3.org/1999/XSL/Transform">Ontologie</sch:title>
   <!-- Ontologie der (Teil-)dokumente -->
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00820"
                context="/akn:akomaNtoso/akn:*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRsubtype">
         <sch:let name="teildokument-id"
                  value="tokenize(/akn:akomaNtoso/akn:*/@name, '/')[last()]"/>
         <sch:assert id="SCH-00820-000"
                     test="matches(@value, concat($teildokument-id, '-\d+'))">Die Teildokumentbezeichnung muss der ontologischen Teildokument-ID entsprechen; erwwartet wird hier konkret "<sch:value-of select="concat($teildokument-id, '-', tokenize(@value, '-')[last()])"/>".</sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:title xmlns:xsl="http://www.w3.org/1999/XSL/Transform">Regeln zu Beschlussempfehlungen</sch:title>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00900"
                context="akn:statement/akn:conclusions/akn:blockContainer">
         <sch:assert role="warn" id="SCH-00900-000" test="count(akn:p) ge 3">Im Schlussteil sollen mindestens drei akn:p enthalten sein (akn:p mit Ort und Datum; akn:p mit der Organisation / dem Auschuss; ein oder mehrere akn:p mit Unterschriften).</sch:assert>
      </sch:rule>
      <sch:rule id="SCH-00901"
                context="akn:statement/akn:conclusions/akn:blockContainer/akn:p[1]">
         <sch:assert role="warn" id="SCH-00901-010" test="exists(akn:location)">Im Schlussteil soll im ersten akn:p ein Ort (akn:location) angegeben werden.</sch:assert>
         <sch:assert role="warn" id="SCH-00901-020" test="exists(akn:date)">Im Schlussteil soll im ersten akn:p ein Datum (akn:date) angegeben werden.</sch:assert>
      </sch:rule>
      <sch:rule id="SCH-00902"
                context="akn:statement/akn:conclusions/akn:blockContainer/akn:p[2]">
         <sch:assert role="warn" id="SCH-00902-000" test="exists(akn:organization)">Im Schlussteil soll im zweiten akn:p ein Ort (akn:location) angegeben werden.</sch:assert>
      </sch:rule>
      <sch:rule id="SCH-00903"
                context="akn:statement/akn:conclusions/akn:blockContainer/akn:p[position() ge 3]">
         <sch:assert role="warn" id="SCH-00903-000" test="exists(akn:signature)">Im Schlussteil soll ab dem zweiten akn:p eine Signatur(akn:signature) angegeben werden.</sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:title xmlns:xsl="http://www.w3.org/1999/XSL/Transform">Regeln zu Regelungstext-Anlagen</sch:title>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <!-- Wenn anlage-regelungstext, dann zwingend Angaben zur Anlage im docTitle (und nur diese) -->
      <sch:rule id="SCH-00920"
                context="akn:doc[$teildokument-uri = $art-anlage-regelungstext-uri]">
         <sch:assert id="SCH-00920-000"
                     role="error"
                     test="count(akn:preface//akn:docTitle/akn:inline[@refersTo = 'anlageregelungstext-num']) eq 1 and count(akn:preface//akn:docTitle/akn:inline[@refersTo = 'anlageregelungstext-bezug']) le 1 and count(akn:preface//akn:docTitle/akn:inline[@refersTo = 'anlageregelungstext-heading']) le 1 and count(akn:preface//akn:docTitle/*) le 3">Eine Anlage zu einem Regelungstext muss in ihrem Dokumententitel Angaben zur Zählbezeichnung und kann Angaben zum Bezug und zur Überschrift besitzen.</sch:assert>
      </sch:rule>
      <!-- inline/@refersTo-Literale für Regelungstextanlagen nur in ebendiesen erlauben -->
      <sch:rule id="SCH-00921" context="akn:inline/@refersTo">
         <sch:assert id="SCH-00921-000"
                     role="error"
                     test="if (. = ('anlageregelungstext-num', 'anlageregelungstext-bezug', 'anlageregelungstext-heading')) then ($teildokument-uri = $art-anlage-regelungstext-uri) else true()">Das Literal "<sch:value-of select="."/>" darf nur innerhalb einer Anlage zu einem Regelungstext verwendet werden.</sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:title xmlns:xsl="http://www.w3.org/1999/XSL/Transform">Regeln zu Berichten</sch:title>
   <sch:pattern xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <sch:rule id="SCH-00930"
                context="akn:doc[$teildokument-uri = $art-bericht-uri]/akn:conclusions/akn:blockContainer">
         <sch:assert role="warn" id="SCH-00930-000" test="count(akn:p) ge 2">Im Schlussteil sollen mindestens zwei akn:p enthalten sein (akn:p mit Ort und Datum; optional akn:p mit der Organisation / dem Auschuss; ein oder mehrere akn:p mit Unterschriften).</sch:assert>
      </sch:rule>
      <sch:rule id="SCH-00933"
                context="akn:doc[$teildokument-uri = $art-bericht-uri]/akn:conclusions/akn:blockContainer/akn:p[position() ge 2 and empty(akn:organization)]">
         <sch:assert role="warn" id="SCH-00933-000" test="exists(akn:signature)">Im Schlussteil soll nach den akn:p mit Ort und Datum und ggf. akn:p mit der Organisation eine Signatur(akn:signature) angegeben werden.</sch:assert>
      </sch:rule>
   </sch:pattern>
   <!-- Einschränkungen für Attributtypen, deren Facetten nur in Verkündungsfassungen bzw. Entwurfsfassungen Gültigkeit besitzen -->
   <sch:title>Zulässigkeit von Literalen / Mustern je Attribut an FRBR-Typen, abhängig von der Fassung (Entwurf vs. Verkündung)</sch:title>
   <sch:include href="legalDocML.de-frbr-metadaten-facetten-entwurfsfassung.sch"/>
   <sch:include href="legalDocML.de-frbr-metadaten-facetten-verkündungsfassung.sch"/>
</sch:schema>
