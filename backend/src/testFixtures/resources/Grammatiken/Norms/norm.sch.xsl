<?xml version="1.0" encoding="UTF-8" ?>
<stylesheet
  xmlns="http://www.w3.org/1999/XSL/Transform"
  xmlns:akn="http://rechtsinformationen.bund.de/schema/norm/0.1"
  xmlns:brat="http://MetadatenBundesrat.LegalDocML.de/1.9/"
  xmlns:breg="http://MetadatenBundesregierung.LegalDocML.de/1.9/"
  xmlns:btag="http://MetadatenBundestag.LegalDocML.de/1.9/"
  xmlns:fhilf="http://MetadatenFormulierungshilfe.LegalDocML.de/1.9/"
  xmlns:fkt="lokale-funktionen"
  xmlns:nkr="http://MetadatenNormenkontrollrat.LegalDocML.de/1.9/"
  xmlns:redok="http://MetadatenRechtsetzungsdokument.LegalDocML.de/1.9/"
  xmlns:regtxt="http://MetadatenRegelungstext.LegalDocML.de/1.9/"
  xmlns:sonst="http://MetadatenSonstigerVeroeffentlichungstext.LegalDocML.de/1.9/"
  xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  version="3.0"
  expand-text="false"
>
  <variable
    name="Q{http://dmaus.name/ns/2023/schxslt}phase"
    as="Q{http://www.w3.org/2001/XMLSchema}string"
    select="'#ALL'"
  />
  <param
    name="form"
    select="/akn:akomaNtoso/*/akn:meta/akn:proprietary/regtxt:legalDocML.de_metadaten/regtxt:form"
  />
  <param name="form-stammform" select="'stammform'" />
  <param name="form-mantelform" select="'mantelform'" />
  <param name="form-eingebundene-stammform" select="'eingebundene-stammform'" />
  <param name="form-nicht-vorhanden" select="'nicht-vorhanden'" />
  <param
    name="authority-egesetzgebung"
    select="'https://www.egesetzgebung.bund.de'"
  />
  <param name="authority-everkündung" select="'https://www.recht.bund.de'" />
  <param
    name="authority-rechtsinformationssystem"
    select="'https://www.ris.bund.de'"
  />
  <param
    name="ist-entwurfsfassung"
    select="starts-with(/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRuri/@value, $authority-egesetzgebung)"
  />
  <param
    name="ist-verkündungsfassung"
    select="starts-with(/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRuri/@value, $authority-everkündung)"
  />
  <param
    name="ist-konsolidierte-fassung"
    select="starts-with(/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRuri/@value, $authority-rechtsinformationssystem)"
  />
  <param
    name="typ"
    select="/akn:akomaNtoso/*/akn:meta/akn:proprietary/regtxt:legalDocML.de_metadaten/regtxt:typ"
  />
  <param name="typ-gesetz" select="'gesetz'" />
  <param
    name="typ-sonstige-bekanntmachung"
    select="'sonstige-bekanntmachung'"
  />
  <param name="typ-verordnung" select="'verordnung'" />
  <param name="typ-vertragsgesetz" select="'vertragsgesetz'" />
  <param name="typ-vertragsverordnung" select="'vertragsverordnung'" />
  <param name="typ-verwaltungsvorschrift" select="'verwaltungsvorschrift'" />
  <param name="typ-satzung" select="'satzung'" />
  <param name="teildokument-uri" select="/akn:akomaNtoso/*/@name" />
  <param
    name="art-anschreiben-uri"
    select="( '/akn/ontology/de/concept/documenttype/bund/anschreiben', '/akn/ontology/de/concept/documenttype/bund/anschreiben-einigungsvorschlag-des-vermittlungsausschusses', '/akn/ontology/de/concept/documenttype/bund/anschreiben-vorschlag-an-bundesrat' )"
  />
  <param
    name="art-begründung-uri"
    select="( '/akn/ontology/de/concept/documenttype/bund/begruendung-aenderungsantrag', '/akn/ontology/de/concept/documenttype/bund/begruendung-entschliessungsantrag', '/akn/ontology/de/concept/documenttype/bund/begruendung-regelungstext' )"
  />
  <param
    name="art-regelungstext-uri"
    select="( '/akn/ontology/de/concept/documenttype/bund/regelungstext-entwurf', '/akn/ontology/de/concept/documenttype/bund/regelungstext' )"
  />
  <param
    name="art-vereinbarung-uri"
    select="( '/akn/ontology/de/concept/documenttype/bund/vereinbarung-entwurf', '/akn/ontology/de/concept/documenttype/bund/vereinbarung-verkuendung' )"
  />
  <param
    name="art-vorblatt-uri"
    select="( '/akn/ontology/de/concept/documenttype/bund/vorblatt-regelungstext', '/akn/ontology/de/concept/documenttype/bund/vorblatt-beschlussempfehlung' )"
  />
  <param
    name="art-vorblatt-regelungstext-uri"
    select="'/akn/ontology/de/concept/documenttype/bund/vorblatt-regelungstext'"
  />
  <param
    name="art-vorblatt-beschlussempfehlung-uri"
    select="'/akn/ontology/de/concept/documenttype/bund/vorblatt-beschlussempfehlung'"
  />
  <param
    name="art-anlage-regelungstext-uri"
    select="'/akn/ontology/de/concept/documenttype/bund/anlage-regelungstext'"
  />
  <param
    name="art-bericht-uri"
    select="'/akn/ontology/de/concept/documenttype/bund/bericht'"
  />
  <param
    name="art-bekanntmachungstext-uri"
    select="( '/akn/ontology/de/concept/documenttype/bund/sonstiger-veroeffentlichungstext' )"
  />
  <param
    name="bearbeitende-institution-frbrauthor"
    select="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRExpression/akn:FRBRauthor/@href"
  />
  <param
    name="refersto-literal-geltungszeitregel"
    select="'geltungszeitregel'"
  />
  <param
    name="refersto-literal-geltungszeitregel-inkrafttreten"
    select="'geltungszeitregel-inkrafttreten'"
  />
  <param
    name="refersto-literal-geltungszeitregel-ausserkrafttreten"
    select="'geltungszeitregel-ausserkrafttreten'"
  />
  <param name="refersto-literal-hauptaenderung" select="'hauptaenderung'" />
  <param name="refersto-literal-folgeaenderung" select="'folgeaenderung'" />
  <param
    name="refersto-literal-eingebundene-stammform"
    select="'eingebundene-stammform'"
  />
  <param name="refersto-literal-stammform" select="'stammform'" />
  <param name="refersto-literal-mantelform" select="'mantelform'" />
  <param name="refersto-literal-vertragsgesetz" select="'vertragsgesetz'" />
  <param
    name="refersto-literal-vertragsverordnung"
    select="'vertragsverordnung'"
  />
  <param
    name="refersto-literal-ausschussueberweisung"
    select="'ausschussueberweisung'"
  />
  <param
    name="type-literal-ereignisreferenz-generation"
    select="'generation'"
  />
  <param name="type-literal-ereignisreferenz-repeal" select="'repeal'" />
  <param name="type-literal-ereignisreferenz-amendment" select="'amendment'" />
  <param
    name="refersto-literal-ereignisreferenz-verkuendung"
    select="'verkuendung'"
  />
  <param
    name="refersto-literal-ereignisreferenz-entwurfsfassung-ausfertigung-mit-unbekanntem-datum"
    select="'ausfertigung-mit-noch-unbekanntem-datum'"
  />
  <param
    name="refersto-literal-ereignisreferenz-entwurfsfassung-verkuendung-mit-unbekanntem-datum"
    select="'verkuendung-mit-noch-unbekanntem-datum'"
  />
  <param
    name="refersto-literal-ereignisreferenz-entwurfsfassung-inkrafttreten"
    select="'inkrafttreten'"
  />
  <param
    name="refersto-literal-ereignisreferenz-entwurfsfassung-inkrafttreten-mit-unbekanntem-datum"
    select="'inkrafttreten-mit-noch-unbekanntem-datum'"
  />
  <param
    name="refersto-literal-ereignisreferenz-entwurfsfassung-ausserkrafttreten"
    select="'ausserkrafttreten'"
  />
  <param
    name="refersto-literal-ereignisreferenz-entwurfsfassung-ausserkrafttreten-mit-unbekanntem-datum"
    select="'ausserkrafttreten-mit-noch-unbekanntem-datum'"
  />
  <param
    name="refersto-literal-ereignisreferenz-verkündungsfassung-ausfertigung"
    select="'ausfertigung'"
  />
  <param
    name="refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten"
    select="'inkrafttreten'"
  />
  <param
    name="refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-grundsaetzlich"
    select="'inkrafttreten-grundsaetzlich'"
  />
  <param
    name="refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-abweichend"
    select="'inkrafttreten-abweichend'"
  />
  <param
    name="refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-mit-unbekanntem-datum"
    select="'inkrafttreten-mit-noch-unbekanntem-datum'"
  />
  <param
    name="refersto-literal-ereignisreferenz-verkündungsfassung-ausserkrafttreten"
    select="'ausserkrafttreten'"
  />
  <param
    name="refersto-literal-ereignisreferenz-verkündungssfassung-ausserkrafttreten-mit-unbekanntem-datum"
    select="'ausserkrafttreten-mit-noch-unbekanntem-datum'"
  />
  <param
    name="refersto-literal-ereignisreferenz-neufassung"
    select="'neufassung'"
  />
  <param
    name="dokumentarten-mit-lebenszyklus-angaben-formulierung-satzanfang-nominativ"
    select="'Ein Regelungstext, ein Bekanntmachungstext oder eine Vereinbarung'"
  />
  <param
    name="refersto-literal-vorblattabschnitt-erfüllungsaufwand"
    select="'vorblattabschnitt-erfuellungsaufwand'"
  />
  <param
    name="literal-deklaration-ausnahme-eid-zählweise"
    select="'ordinale-zaehlung-eid'"
  />
  <param name="präfix-eid-nummerierbar" select="'n'" />
  <param name="präfix-eid-zitierbar" select="'z'" />
  <param name="zitierbare-elementtypen" select="('article', 'paragraph')" />
  <param name="platzhalter-datum-unbekannt" select="'0001-01-01'" />
  <param
    name="eli-präfix-entwurfsfassung"
    select="'https://www.egesetzgebung.bund.de/eli/dl'"
  />
  <param
    name="eli-präfix-verkündungsfassung"
    select="'https://www.recht.bund.de/eli/bund'"
  />
  <param
    name="eli-präfix-konsolidierte-fassung"
    select="'https://www.ris.bund.de/eli/bund'"
  />
  <param
    name="eli-agent-entwurf"
    select="tokenize(/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRauthor/@href, '/')[last()]"
  />
  <param
    name="eli-agent-verkündung-oder-konsolidierung"
    select="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRname/@value"
  />
  <param
    name="eli-year"
    select="(: -- Nimmt als Wert die in der folgenden Reihenfolge gesuchte letzte (!) gefundene Jahresangabe an -- :) ( (: das erste Entwurfsfassung-FRBRdate; es darf nur eins geben! :) substring(/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRdate[@name = 'erstellungsdatum'][1]/@date, 1, 4), (: Jahr der Ausfertigung :) if (not(empty(/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRdate[@name = 'ausfertigungsdatum']/@date))) then (substring(/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRdate[@name = 'ausfertigungsdatum']/@date, 1, 4)) else (), (: Jahr der Verkündung :) if (not(empty(/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRdate[@name = 'verkuendungsdatum']/@date))) then substring(/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRdate[@name = 'verkuendungsdatum']/@date, 1, 4) else () )[last()]"
  />
  <param
    name="eli-natural-identifier"
    select="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRnumber/@value"
  />
  <param name="eli-process-identifier" select="$eli-natural-identifier" />
  <param
    name="eli-type-of-legislative-process-document"
    select="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRname/@value"
  />
  <param
    name="eli-point-in-time"
    select="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRExpression/akn:FRBRdate/@date"
  />
  <param
    name="eli-version"
    select="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRExpression/akn:FRBRversionNumber/@value"
  />
  <param
    name="eli-language"
    select="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRExpression/akn:FRBRlanguage/@language"
  />
  <param
    name="eli-subtype"
    select="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRsubtype/@value"
  />
  <param
    name="eli-format"
    select="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRManifestation/akn:FRBRformat/@value"
  />
  <param
    name="eli-point-in-time-manifestation"
    select="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRManifestation/akn:FRBRdate/@date"
  />
  <param
    name="eli-subagent"
    select="tokenize(/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRExpression/akn:FRBRauthor/@href, '/')[last()]"
  />
  <param
    name="FRBRthis-verkündungsfassung-work-beschreibung"
    select="'Der eindeutige Bezeichner für Teildokumente in der Verkündungsfassung auf der Work-Ebene'"
  />
  <param
    name="FRBRthis-verkündungsfassung-work-aufbau"
    select="concat($eli-präfix-verkündungsfassung, '/{agent}/{year}/{natural identifier}/{subtype}')"
  />
  <param
    name="FRBRthis-verkündungsfassung-work-inhalt"
    select="string-join(($eli-präfix-verkündungsfassung, $eli-agent-verkündung-oder-konsolidierung, $eli-year, $eli-natural-identifier, $eli-subtype), '/')"
  />
  <param
    name="FRBRthis-verkündungsfassung-expression-beschreibung"
    select="'Der eindeutige Bezeichner für Teildokumente in der Verkündungsfassung auf der Expression-Ebene'"
  />
  <param
    name="FRBRthis-verkündungsfassung-expression-aufbau"
    select="concat($eli-präfix-verkündungsfassung, '/{agent}/{year}/{natural identifier}/{language}/{subtype}')"
  />
  <param
    name="FRBRthis-verkündungsfassung-expression-inhalt"
    select="string-join(($eli-präfix-verkündungsfassung, $eli-agent-verkündung-oder-konsolidierung, $eli-year, $eli-natural-identifier, $eli-language, $eli-subtype), '/')"
  />
  <param
    name="FRBRthis-verkündungsfassung-manifestation-beschreibung"
    select="'Der eindeutige Bezeichner für Teildokumente in der Verkündungsfassung auf der Manifestation-Ebene'"
  />
  <param
    name="FRBRthis-verkündungsfassung-manifestation-aufbau"
    select="concat($eli-präfix-verkündungsfassung, '/{agent}/{year}/{natural identifier}/{language}/{point in time manifestation}/{subtype}.{format}')"
  />
  <param
    name="FRBRthis-verkündungsfassung-manifestation-inhalt"
    select="string-join(($eli-präfix-verkündungsfassung, $eli-agent-verkündung-oder-konsolidierung, $eli-year, $eli-natural-identifier, $eli-language, concat($eli-subtype, '.', $eli-format)), '/')"
  />
  <param
    name="FRBRthis-konsolidierte-fassung-work-beschreibung"
    select="'Der eindeutige Bezeichner für Teildokumente in einer konsolidierten Fassung auf der Work-Ebene'"
  />
  <param
    name="FRBRthis-konsolidierte-fassung-work-aufbau"
    select="concat($eli-präfix-konsolidierte-fassung, '/{agent}/{year}/{natural identifier}/{subtype}')"
  />
  <param
    name="FRBRthis-konsolidierte-fassung-work-inhalt"
    select="string-join(($eli-präfix-konsolidierte-fassung, $eli-agent-verkündung-oder-konsolidierung, $eli-year, $eli-natural-identifier, $eli-subtype), '/')"
  />
  <param
    name="FRBRthis-konsolidierte-fassung-expression-beschreibung"
    select="'Der eindeutige Bezeichner für Teildokumente in einer konsolidierten Fassung auf der Expression-Ebene'"
  />
  <param
    name="FRBRthis-konsolidierte-fassung-expression-aufbau"
    select="concat($eli-präfix-konsolidierte-fassung, '/{agent}/{year}/{natural identifier}/{point in time}/{version}/{language}/{subtype}')"
  />
  <param
    name="FRBRthis-konsolidierte-fassung-expression-inhalt"
    select="string-join(($eli-präfix-konsolidierte-fassung, $eli-agent-verkündung-oder-konsolidierung, $eli-year, $eli-natural-identifier, $eli-point-in-time, $eli-version, $eli-language, $eli-subtype), '/')"
  />
  <param
    name="FRBRthis-konsolidierte-fassung-manifestation-beschreibung"
    select="'Der eindeutige Bezeichner für Teildokumente in einer konsolidierten Fassung auf der Manifestation-Ebene'"
  />
  <param
    name="FRBRthis-konsolidierte-fassung-manifestation-aufbau"
    select="concat($eli-präfix-konsolidierte-fassung, '/{agent}/{year}/{natural identifier}/{point in time}/{version}/{language}/{point in time manifestation}/{subtype}.{format}')"
  />
  <param
    name="FRBRthis-konsolidierte-fassung-manifestation-inhalt"
    select="string-join(($eli-präfix-konsolidierte-fassung, $eli-agent-verkündung-oder-konsolidierung, $eli-year, $eli-natural-identifier, $eli-point-in-time, $eli-version, $eli-language, $eli-point-in-time-manifestation, concat($eli-subtype, '.', $eli-format)), '/')"
  />
  <param
    name="FRBRthis-entwurfsfassung-work-beschreibung"
    select="'Der eindeutige Bezeichner für Teildokumente in der Entwurfsfassung auf der Work-Ebene'"
  />
  <param
    name="FRBRthis-entwurfsfassung-work-aufbau"
    select="concat($eli-präfix-entwurfsfassung, '/{year}/{agent}/{process identifier}/{type of legislative process document}/{subtype}')"
  />
  <param
    name="FRBRthis-entwurfsfassung-work-inhalt"
    select="string-join(($eli-präfix-entwurfsfassung, $eli-year, $eli-agent-entwurf, $eli-process-identifier, $eli-type-of-legislative-process-document, $eli-subtype), '/')"
  />
  <param
    name="FRBRthis-entwurfsfassung-expression-beschreibung"
    select="'Der eindeutige Bezeichner für Teildokumente in der Entwurfsfassung auf der Expression-Ebene'"
  />
  <param
    name="FRBRthis-entwurfsfassung-expression-aufbau"
    select="concat($eli-präfix-entwurfsfassung, '/{year}/{agent}/{process identifier}/{type of legislative process document}/{subagent}/{point in time}/{version}/{language}/{subtype}')"
  />
  <param
    name="FRBRthis-entwurfsfassung-expression-inhalt"
    select="string-join(($eli-präfix-entwurfsfassung, $eli-year, $eli-agent-entwurf, $eli-process-identifier, $eli-type-of-legislative-process-document, $eli-subagent, $eli-point-in-time, $eli-version, $eli-language, $eli-subtype), '/')"
  />
  <param
    name="FRBRthis-entwurfsfassung-manifestation-beschreibung"
    select="'Der eindeutige Bezeichner für Teildokumente in der Entwurfsfassung auf der Manifestation-Ebene'"
  />
  <param
    name="FRBRthis-entwurfsfassung-manifestation-aufbau"
    select="concat($eli-präfix-entwurfsfassung, '/{year}/{agent}/{process identifier}/{type of legislative process document}/{subagent}/{point in time}/{version}/{language}/{subtype}.{format}')"
  />
  <param
    name="FRBRthis-entwurfsfassung-manifestation-inhalt"
    select="string-join(($eli-präfix-entwurfsfassung, $eli-year, $eli-agent-entwurf, $eli-process-identifier, $eli-type-of-legislative-process-document, $eli-subagent, $eli-point-in-time, $eli-version, $eli-language, concat($eli-subtype, '.', $eli-format)), '/')"
  />
  <param
    name="FRBRuri-verkündungsfassung-work-beschreibung"
    select="'Der eindeutige Bezeichner für die Work-Ebene in der Verkündungsfassung'"
  />
  <param
    name="FRBRuri-verkündungsfassung-work-aufbau"
    select="concat($eli-präfix-verkündungsfassung, '/{agent}/{year}/{natural identifier}')"
  />
  <param
    name="FRBRuri-verkündungsfassung-work-inhalt"
    select="string-join(($eli-präfix-verkündungsfassung, $eli-agent-verkündung-oder-konsolidierung, $eli-year, $eli-natural-identifier), '/')"
  />
  <param
    name="FRBRuri-verkündungsfassung-expression-beschreibung"
    select="'Der eindeutige Bezeichner für die Expression-Ebene in der Verkündungsfassung'"
  />
  <param
    name="FRBRuri-verkündungsfassung-expression-aufbau"
    select="concat($eli-präfix-verkündungsfassung, '{agent}/{year}/{natural identifier}/{language}')"
  />
  <param
    name="FRBRuri-verkündungsfassung-expression-inhalt"
    select="string-join(($eli-präfix-verkündungsfassung, $eli-agent-verkündung-oder-konsolidierung, $eli-year, $eli-natural-identifier, $eli-language), '/')"
  />
  <param
    name="FRBRuri-verkündungsfassung-manifestation-beschreibung"
    select="'Der eindeutige Bezeichner für die Manifestation-Ebene in der Verkündungsfassung'"
  />
  <param
    name="FRBRuri-verkündungsfassung-manifestation-aufbau"
    select="$FRBRthis-verkündungsfassung-manifestation-aufbau"
  />
  <param
    name="FRBRuri-verkündungsfassung-manifestation-inhalt"
    select="$FRBRthis-verkündungsfassung-manifestation-inhalt"
  />
  <param
    name="FRBRuri-konsolidierte-fassung-work-beschreibung"
    select="'Der eindeutige Bezeichner für die Work-Ebene der konsolidierten Fassung'"
  />
  <param
    name="FRBRuri-konsolidierte-fassung-work-aufbau"
    select="concat($eli-präfix-konsolidierte-fassung, '/{agent}/{year}/{natural identifier}')"
  />
  <param
    name="FRBRuri-konsolidierte-fassung-work-inhalt"
    select="string-join(($eli-präfix-konsolidierte-fassung, $eli-agent-verkündung-oder-konsolidierung, $eli-year, $eli-natural-identifier), '/')"
  />
  <param
    name="FRBRuri-konsolidierte-fassung-expression-beschreibung"
    select="'Der eindeutige Bezeichner für die Expression-Ebene in der konsolidierten Fassung'"
  />
  <param
    name="FRBRuri-konsolidierte-fassung-expression-aufbau"
    select="concat($eli-präfix-konsolidierte-fassung, '{agent}/{year}/{natural identifier}/{point in time}/{version}/{language}')"
  />
  <param
    name="FRBRuri-konsolidierte-fassung-expression-inhalt"
    select="string-join(($eli-präfix-konsolidierte-fassung, $eli-agent-verkündung-oder-konsolidierung, $eli-year, $eli-natural-identifier, $eli-point-in-time, $eli-version, $eli-language), '/')"
  />
  <param
    name="FRBRuri-konsolidierte-fassung-manifestation-beschreibung"
    select="'Der eindeutige Bezeichner für die Manifestation-Ebene in der konsolidierten Fassung'"
  />
  <param
    name="FRBRuri-konsolidierte-fassung-manifestation-aufbau"
    select="$FRBRthis-konsolidierte-fassung-manifestation-aufbau"
  />
  <param
    name="FRBRuri-konsolidierte-fassung-manifestation-inhalt"
    select="$FRBRthis-konsolidierte-fassung-manifestation-inhalt"
  />
  <param
    name="FRBRuri-entwurfsfassung-work-beschreibung"
    select="'Der eindeutige Bezeichner für die Work-Ebene in der Entwurfsfassung'"
  />
  <param
    name="FRBRuri-entwurfsfassung-work-aufbau"
    select="concat($eli-präfix-entwurfsfassung, '/{year}/{agent}/{process identifier}/{type of legislative process document}')"
  />
  <param
    name="FRBRuri-entwurfsfassung-work-inhalt"
    select="string-join(($eli-präfix-entwurfsfassung, $eli-year, $eli-agent-entwurf, $eli-process-identifier, $eli-type-of-legislative-process-document), '/')"
  />
  <param
    name="FRBRuri-entwurfsfassung-expression-beschreibung"
    select="'Der eindeutige Bezeichner für die Expression-Ebene in der Entwurfsfassung'"
  />
  <param
    name="FRBRuri-entwurfsfassung-expression-aufbau"
    select="concat($eli-präfix-entwurfsfassung, '/{year}/{agent}/{process identifier}/{type of legislative process document}/{subagent}/{point in time}/{version}/{language}')"
  />
  <param
    name="FRBRuri-entwurfsfassung-expression-inhalt"
    select="string-join(($eli-präfix-entwurfsfassung, $eli-year, $eli-agent-entwurf, $eli-process-identifier, $eli-type-of-legislative-process-document, $eli-subagent, $eli-point-in-time, $eli-version, $eli-language), '/')"
  />
  <param
    name="FRBRuri-entwurfsfassung-manifestation-beschreibung"
    select="'Der eindeutige Bezeichner für die Manifestation-Ebene in der Entwurfsfassung'"
  />
  <param
    name="FRBRuri-entwurfsfassung-manifestation-aufbau"
    select="concat($eli-präfix-entwurfsfassung, '/{year}/{agent}/{process identifier}/{type of legislative process document}/{subagent}/{point in time}/{version}/{language}/{subtype}.{format}')"
  />
  <param
    name="FRBRuri-entwurfsfassung-manifestation-inhalt"
    select="string-join(($eli-präfix-entwurfsfassung, $eli-year, $eli-agent-entwurf, $eli-process-identifier, $eli-type-of-legislative-process-document, $eli-subagent, $eli-point-in-time, $eli-version, $eli-language, concat($eli-subtype, '.', $eli-format)), '/')"
  />
  <xsl:key
    xmlns="http://purl.oclc.org/dsdl/schematron"
    xmlns:sch="http://purl.oclc.org/dsdl/schematron"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    name="nodes-by-GUID"
    match="*[@GUID]"
    use="@GUID"
  />
  <xsl:key
    xmlns="http://purl.oclc.org/dsdl/schematron"
    xmlns:sch="http://purl.oclc.org/dsdl/schematron"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    name="nodes-by-eId"
    match="*[@eId]"
    use="@eId"
  />
  <mode use-accumulators="" />
  <mode
    name="Q{http://dmaus.name/ns/2023/schxslt}validate"
    use-accumulators=""
    on-no-match="shallow-skip"
  />
  <mode
    name="group.d7e180"
    on-no-match="shallow-skip"
    streamable="false"
    use-accumulators=""
  />
  <template match="* | root()" mode="group.d7e180" priority="-10">
    <apply-templates select="@*" mode="#current" />
    <apply-templates select="node()" mode="#current" />
  </template>
  <variable name="fassung-entwurfsfassung" select="'false()'" />
  <variable
    name="zulässige-literale-in-kombination-mit-repeal"
    select="($refersto-literal-ereignisreferenz-verkündungsfassung-ausserkrafttreten, $refersto-literal-ereignisreferenz-entwurfsfassung-ausserkrafttreten, $refersto-literal-ereignisreferenz-entwurfsfassung-ausserkrafttreten-mit-unbekanntem-datum, $refersto-literal-ereignisreferenz-verkündungssfassung-ausserkrafttreten-mit-unbekanntem-datum)"
  />
  <template
    match="/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri and $form = $form-eingebundene-stammform]"
    mode="group.d7e180"
    priority="121"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e180' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00019"
          context="/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri and $form = $form-eingebundene-stammform]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00019"
          context="/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri and $form = $form-eingebundene-stammform]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e184'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e187'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e190'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e184') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(not(./akn:preface/akn:block))">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00019-000"
                    role="error"
                    test="not(./akn:preface/akn:block)"
                    ruleId="SCH-00019"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Ein Regelungstext in Stammform in einer Mantelform darf keinen Datumscontainer innerhalb des Dokumentenkopfes enthalten.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00019-000"
                role="error"
                test="not(./akn:preface/akn:block)"
                ruleId="SCH-00019"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e187') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(not(./akn:preamble/akn:formula))">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00019-005"
                    role="error"
                    test="not(./akn:preamble/akn:formula)"
                    ruleId="SCH-00019"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Ein Regelungstext in Stammform in einer Mantelform darf keine Eingangsformel enthalten.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00019-005"
                role="error"
                test="not(./akn:preamble/akn:formula)"
                ruleId="SCH-00019"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e190') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(not(./akn:conclusions))">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00019-010"
                    role="error"
                    test="not(./akn:conclusions)"
                    ruleId="SCH-00019"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Ein Regelungstext in Stammform in einer Mantelform darf keinen Schlussteil enthalten.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00019-010"
                role="error"
                test="not(./akn:conclusions)"
                ruleId="SCH-00019"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e180', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri and $typ = ($typ-gesetz, $typ-vertragsgesetz)]"
    mode="group.d7e180"
    priority="120"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e180' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00020"
          context="/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri and $typ = ($typ-gesetz, $typ-vertragsgesetz)]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00020"
          context="/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri and $typ = ($typ-gesetz, $typ-vertragsgesetz)]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e196'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e199'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e196') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(./akn:preamble/akn:formula)">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00020-005"
                    role="error"
                    test="./akn:preamble/akn:formula"
                    ruleId="SCH-00020"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Für ein Gesetz muss eine Eingangsformel verwendet werden.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00020-005"
                role="error"
                test="./akn:preamble/akn:formula"
                ruleId="SCH-00020"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e199') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(not(./akn:conclusions/akn:formula))">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00020-010"
                    role="warn"
                    test="not(./akn:conclusions/akn:formula)"
                    ruleId="SCH-00020"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Für ein Gesetz in der Entwurfsfassung wird in der Regel keine Schlussformel benutzt.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00020-010"
                role="warn"
                test="not(./akn:conclusions/akn:formula)"
                ruleId="SCH-00020"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e180', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri and $typ = ($typ-verordnung, $typ-vertragsverordnung)]"
    mode="group.d7e180"
    priority="119"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e180' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00030"
          context="/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri and $typ = ($typ-verordnung, $typ-vertragsverordnung)]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00030"
          context="/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri and $typ = ($typ-verordnung, $typ-vertragsverordnung)]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e205'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e208'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e205') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(not(./akn:preamble/akn:formula))">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00030-005"
                    role="error"
                    test="not(./akn:preamble/akn:formula)"
                    ruleId="SCH-00030"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Eine Verordnung darf keine Eingangsformel enthalten.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00030-005"
                role="error"
                test="not(./akn:preamble/akn:formula)"
                ruleId="SCH-00030"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e208') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(not(./akn:conclusions/akn:formula))">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00030-010"
                    role="warn"
                    test="not(./akn:conclusions/akn:formula)"
                    ruleId="SCH-00030"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Für eine Verordnung in der Entwurfsfassung wird in der Regel keine Schlussformel benutzt.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00030-010"
                role="warn"
                test="not(./akn:conclusions/akn:formula)"
                ruleId="SCH-00030"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e180', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri and $typ = $typ-verwaltungsvorschrift]"
    mode="group.d7e180"
    priority="118"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e180' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00040"
          context="/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri and $typ = $typ-verwaltungsvorschrift]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00040"
          context="/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri and $typ = $typ-verwaltungsvorschrift]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e214'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e217'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e214') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(not(./akn:preamble/akn:formula))">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00040-005"
                    role="error"
                    test="not(./akn:preamble/akn:formula)"
                    ruleId="SCH-00040"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Eine Verwaltungsvorschrift darf keine Eingangsformel enthalten.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00040-005"
                role="error"
                test="not(./akn:preamble/akn:formula)"
                ruleId="SCH-00040"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e217') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(not(./akn:conclusions/akn:formula))">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00040-010"
                    role="warn"
                    test="not(./akn:conclusions/akn:formula)"
                    ruleId="SCH-00040"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Für eine Verwaltungsvorschrift in der Entwurfsfassung wird in der Regel keine Schlussformel benutzt.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00040-010"
                role="warn"
                test="not(./akn:conclusions/akn:formula)"
                ruleId="SCH-00040"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e180', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="/akn:akomaNtoso/akn:act[$ist-verkündungsfassung and @name = $art-regelungstext-uri and $form = $form-eingebundene-stammform]"
    mode="group.d7e180"
    priority="117"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e226' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00048"
          context="/akn:akomaNtoso/akn:act[$ist-verkündungsfassung and @name = $art-regelungstext-uri and $form = $form-eingebundene-stammform]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00048"
          context="/akn:akomaNtoso/akn:act[$ist-verkündungsfassung and @name = $art-regelungstext-uri and $form = $form-eingebundene-stammform]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e232'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e235'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e238'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e232') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(not(./akn:preface/akn:block))">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00048-000"
                    role="error"
                    test="not(./akn:preface/akn:block)"
                    ruleId="SCH-00048"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Ein Regelungstext in Stammform in einer Mantelform darf keinen Datums-Container innerhalb des Dokumentenkopfes enthalten.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00048-000"
                role="error"
                test="not(./akn:preface/akn:block)"
                ruleId="SCH-00048"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e235') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(not(./akn:preamble/akn:formula))">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00048-005"
                    role="error"
                    test="not(./akn:preamble/akn:formula)"
                    ruleId="SCH-00048"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Ein Regelungstext in Stammform in einer Mantelform darf keine Eingangsformel enthalten.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00048-005"
                role="error"
                test="not(./akn:preamble/akn:formula)"
                ruleId="SCH-00048"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e238') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(not(./akn:conclusions))">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00048-010"
                    role="error"
                    test="not(./akn:conclusions)"
                    ruleId="SCH-00048"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Ein Regelungstext in Stammform in einer Mantelform darf keinen Schlussteil enthalten.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00048-010"
                role="error"
                test="not(./akn:conclusions)"
                ruleId="SCH-00048"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e226', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="/akn:akomaNtoso/akn:act[$ist-verkündungsfassung and @name = $art-regelungstext-uri and $typ = ($typ-verordnung, $typ-vertragsverordnung)]"
    mode="group.d7e180"
    priority="116"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e226' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00060"
          context="/akn:akomaNtoso/akn:act[$ist-verkündungsfassung and @name = $art-regelungstext-uri and $typ = ($typ-verordnung, $typ-vertragsverordnung)]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00060"
          context="/akn:akomaNtoso/akn:act[$ist-verkündungsfassung and @name = $art-regelungstext-uri and $typ = ($typ-verordnung, $typ-vertragsverordnung)]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e244'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e244') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(not(./akn:preamble/akn:formula))">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00060-005"
                    role="error"
                    test="not(./akn:preamble/akn:formula)"
                    ruleId="SCH-00060"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Eine Verordnung darf keine Eingangsformel enthalten.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00060-005"
                role="error"
                test="not(./akn:preamble/akn:formula)"
                ruleId="SCH-00060"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e226', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="/akn:akomaNtoso/akn:act[$ist-verkündungsfassung and @name = $art-regelungstext-uri and $typ = $typ-verwaltungsvorschrift]"
    mode="group.d7e180"
    priority="115"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e226' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00070"
          context="/akn:akomaNtoso/akn:act[$ist-verkündungsfassung and @name = $art-regelungstext-uri and $typ = $typ-verwaltungsvorschrift]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00070"
          context="/akn:akomaNtoso/akn:act[$ist-verkündungsfassung and @name = $art-regelungstext-uri and $typ = $typ-verwaltungsvorschrift]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e250'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e250') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(not(./akn:preamble/akn:formula))">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00070-005"
                    role="error"
                    test="not(./akn:preamble/akn:formula)"
                    ruleId="SCH-00070"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Eine Verwaltungsvorschrift darf keine Eingangsformel enthalten.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00070-005"
                role="error"
                test="not(./akn:preamble/akn:formula)"
                ruleId="SCH-00070"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e226', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="/akn:akomaNtoso/akn:act[@name = $art-regelungstext-uri and $ist-verkündungsfassung and not($form = $form-eingebundene-stammform)]"
    mode="group.d7e180"
    priority="114"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e255' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00071"
          context="/akn:akomaNtoso/akn:act[@name = $art-regelungstext-uri and $ist-verkündungsfassung and not($form = $form-eingebundene-stammform)]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00071"
          context="/akn:akomaNtoso/akn:act[@name = $art-regelungstext-uri and $ist-verkündungsfassung and not($form = $form-eingebundene-stammform)]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e259'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e262'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e259') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(./akn:preface/akn:block)">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00071-005"
                    role="error"
                    test="./akn:preface/akn:block"
                    ruleId="SCH-00071"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Für einen Regelungstext in der Verkündungsfassung muss ein Datums-Container innerhalb des Dokumentenkopfes verwendet werden. </svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00071-005"
                role="error"
                test="./akn:preface/akn:block"
                ruleId="SCH-00071"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e262') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(./akn:conclusions/akn:blockContainer)">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00071-010"
                    role="error"
                    test="./akn:conclusions/akn:blockContainer"
                    ruleId="SCH-00071"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Für einen Regelungstext in der Verkündungsfassung muss ein Signaturblock verwendet werden.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00071-010"
                role="error"
                test="./akn:conclusions/akn:blockContainer"
                ruleId="SCH-00071"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e255', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="akn:act/akn:conclusions/akn:formula"
    mode="group.d7e180"
    priority="113"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e269' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00073"
          context="akn:act/akn:conclusions/akn:formula"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00073"
          context="akn:act/akn:conclusions/akn:formula"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e273'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e273') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(empty(preceding-sibling::akn:*))">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00073-005"
                    test="empty(preceding-sibling::akn:*)"
                    ruleId="SCH-00073"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Die Schlussformel (akn:formula) muss das erste Element im Schluss des Regelungstextes (akn:conclusions) sein.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00073-005"
                test="empty(preceding-sibling::akn:*)"
                ruleId="SCH-00073"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e269', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="/akn:akomaNtoso/*[@name = $art-regelungstext-uri and $typ = ($typ-gesetz, $typ-vertragsgesetz)]/akn:preamble"
    mode="group.d7e180"
    priority="112"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e282' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00100"
          context="/akn:akomaNtoso/*[@name = $art-regelungstext-uri and $typ = ($typ-gesetz, $typ-vertragsgesetz)]/akn:preamble"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00100"
          context="/akn:akomaNtoso/*[@name = $art-regelungstext-uri and $typ = ($typ-gesetz, $typ-vertragsgesetz)]/akn:preamble"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e286'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e286') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(not(./akn:citations))">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00100-005"
                    role="error"
                    test="not(./akn:citations)"
                    ruleId="SCH-00100"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Ein Gesetz darf keine Ermächtigungsnorm enthalten.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00100-005"
                role="error"
                test="not(./akn:citations)"
                ruleId="SCH-00100"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e282', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="/akn:akomaNtoso/* [ @name = $art-regelungstext-uri and $typ = ($typ-verordnung, $typ-vertragsverordnung) and $form = ($form-stammform, $form-mantelform) and ($ist-entwurfsfassung or $ist-verkündungsfassung) ]/akn:preamble"
    mode="group.d7e180"
    priority="111"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e282' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00110"
          context="/akn:akomaNtoso/* [ @name = $art-regelungstext-uri and $typ = ($typ-verordnung, $typ-vertragsverordnung) and $form = ($form-stammform, $form-mantelform) and ($ist-entwurfsfassung or $ist-verkündungsfassung) ]/akn:preamble"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00110"
          context="/akn:akomaNtoso/* [ @name = $art-regelungstext-uri and $typ = ($typ-verordnung, $typ-vertragsverordnung) and $form = ($form-stammform, $form-mantelform) and ($ist-entwurfsfassung or $ist-verkündungsfassung) ]/akn:preamble"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e292'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e292') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(./akn:citations)">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00110-005"
                    role="error"
                    test="./akn:citations"
                    ruleId="SCH-00110"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Eine Verordnung muss Ermächtigungsnormen bereitstellen.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00110-005"
                role="error"
                test="./akn:citations"
                ruleId="SCH-00110"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e282', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="akn:quotedStructure//akn:article [ $form = $form-mantelform and $typ = ($typ-gesetz, $typ-verordnung, $typ-satzung, $typ-verwaltungsvorschrift, $typ-vertragsgesetz, $typ-vertragsverordnung) ]"
    mode="group.d7e180"
    priority="110"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e297' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00120"
          context="akn:quotedStructure//akn:article [ $form = $form-mantelform and $typ = ($typ-gesetz, $typ-verordnung, $typ-satzung, $typ-verwaltungsvorschrift, $typ-vertragsgesetz, $typ-vertragsverordnung) ]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00120"
          context="akn:quotedStructure//akn:article [ $form = $form-mantelform and $typ = ($typ-gesetz, $typ-verordnung, $typ-satzung, $typ-verwaltungsvorschrift, $typ-vertragsgesetz, $typ-vertragsverordnung) ]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e301'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e301') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not((@refersTo = ($refersto-literal-mantelform, $refersto-literal-stammform, $refersto-literal-geltungszeitregel, $refersto-literal-geltungszeitregel-inkrafttreten, $refersto-literal-geltungszeitregel-ausserkrafttreten, $refersto-literal-vertragsgesetz, $refersto-literal-vertragsverordnung)))"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00120-000"
                    test="(@refersTo = ($refersto-literal-mantelform, $refersto-literal-stammform, $refersto-literal-geltungszeitregel, $refersto-literal-geltungszeitregel-inkrafttreten, $refersto-literal-geltungszeitregel-ausserkrafttreten, $refersto-literal-vertragsgesetz, $refersto-literal-vertragsverordnung))"
                    ruleId="SCH-00120"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Wird innerhalb eines Änderungsbefehls eine Einzelvorschrift in Gänze geändert, neugefasst, hinzugefügt oder gelöscht, so muss diese näher - als Mantelform oder Stammform, als Geltungszeitregel oder als Vertragsgesetz bzw. Vertragsverordnung - bestimmt werden.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00120-000"
                test="(@refersTo = ($refersto-literal-mantelform, $refersto-literal-stammform, $refersto-literal-geltungszeitregel, $refersto-literal-geltungszeitregel-inkrafttreten, $refersto-literal-geltungszeitregel-ausserkrafttreten, $refersto-literal-vertragsgesetz, $refersto-literal-vertragsverordnung))"
                ruleId="SCH-00120"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e297', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="akn:article[not(ancestor::akn:quotedStructure)]/@refersTo [ $form = $form-mantelform and $typ = ($typ-gesetz, $typ-verordnung, $typ-satzung, $typ-verwaltungsvorschrift, $typ-vertragsgesetz, $typ-vertragsverordnung) and ($ist-entwurfsfassung or $ist-verkündungsfassung) ]"
    mode="group.d7e180"
    priority="109"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e306' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00121"
          context="akn:article[not(ancestor::akn:quotedStructure)]/@refersTo [ $form = $form-mantelform and $typ = ($typ-gesetz, $typ-verordnung, $typ-satzung, $typ-verwaltungsvorschrift, $typ-vertragsgesetz, $typ-vertragsverordnung) and ($ist-entwurfsfassung or $ist-verkündungsfassung) ]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00121"
          context="akn:article[not(ancestor::akn:quotedStructure)]/@refersTo [ $form = $form-mantelform and $typ = ($typ-gesetz, $typ-verordnung, $typ-satzung, $typ-verwaltungsvorschrift, $typ-vertragsgesetz, $typ-vertragsverordnung) and ($ist-entwurfsfassung or $ist-verkündungsfassung) ]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e310'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e310') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(. = ($refersto-literal-hauptaenderung, $refersto-literal-folgeaenderung, $refersto-literal-eingebundene-stammform, $refersto-literal-geltungszeitregel, $refersto-literal-geltungszeitregel-inkrafttreten, $refersto-literal-geltungszeitregel-ausserkrafttreten))"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00121-000"
                    test=". = ($refersto-literal-hauptaenderung, $refersto-literal-folgeaenderung, $refersto-literal-eingebundene-stammform, $refersto-literal-geltungszeitregel, $refersto-literal-geltungszeitregel-inkrafttreten, $refersto-literal-geltungszeitregel-ausserkrafttreten)"
                    ruleId="SCH-00121"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Liegt ein Regelungstext in Mantelform vor und seine Einzelvorschriften sind mittels @refersTo näher bestimmt, so dürfen lediglich folgende Literale verwendet werden: "hauptaenderung", "folgeaenderung", "eingebundene-stammform", "geltungszeitregel", "geltungszeitregel-inkrafttreten", "geltungszeitregel-ausserkrafttreten".</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00121-000"
                test=". = ($refersto-literal-hauptaenderung, $refersto-literal-folgeaenderung, $refersto-literal-eingebundene-stammform, $refersto-literal-geltungszeitregel, $refersto-literal-geltungszeitregel-inkrafttreten, $refersto-literal-geltungszeitregel-ausserkrafttreten)"
                ruleId="SCH-00121"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e306', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="akn:article/@refersTo [ $form = $form-stammform and $typ = ($typ-gesetz, $typ-verordnung, $typ-satzung, $typ-verwaltungsvorschrift, $typ-vertragsgesetz, $typ-vertragsverordnung) and ($ist-entwurfsfassung or $ist-verkündungsfassung) ]"
    mode="group.d7e180"
    priority="108"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e315' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00122"
          context="akn:article/@refersTo [ $form = $form-stammform and $typ = ($typ-gesetz, $typ-verordnung, $typ-satzung, $typ-verwaltungsvorschrift, $typ-vertragsgesetz, $typ-vertragsverordnung) and ($ist-entwurfsfassung or $ist-verkündungsfassung) ]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00122"
          context="akn:article/@refersTo [ $form = $form-stammform and $typ = ($typ-gesetz, $typ-verordnung, $typ-satzung, $typ-verwaltungsvorschrift, $typ-vertragsgesetz, $typ-vertragsverordnung) and ($ist-entwurfsfassung or $ist-verkündungsfassung) ]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e319'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e319') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not((. = $refersto-literal-geltungszeitregel and count(//akn:article/@refersTo) = 1) or (. = $refersto-literal-geltungszeitregel-inkrafttreten and (every $r in //akn:article/@refersTo satisfies $r = $refersto-literal-geltungszeitregel-inkrafttreten or $r = $refersto-literal-geltungszeitregel-ausserkrafttreten)))"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00122-000"
                    test="(. = $refersto-literal-geltungszeitregel and count(//akn:article/@refersTo) = 1) or (. = $refersto-literal-geltungszeitregel-inkrafttreten and (every $r in //akn:article/@refersTo satisfies $r = $refersto-literal-geltungszeitregel-inkrafttreten or $r = $refersto-literal-geltungszeitregel-ausserkrafttreten))"
                    ruleId="SCH-00122"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >In einem Regelungstext in Stammform darf entweder genau eine mit refersTo="geltungszeitregel" ausgezeichnete Einzelvorschrift und keine weiteren mit refersTo ausgezeichneten Einzelvorschriften haben oder genau eine mit refersTo="geltungszeitregel-inkrafttreten" ausgezeichnete Einzelvorschrift haben und optional eine mit refersTo="geltungszeitregel-ausserkrafttreten" ausgezeichnete Einzelvorschrift.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00122-000"
                test="(. = $refersto-literal-geltungszeitregel and count(//akn:article/@refersTo) = 1) or (. = $refersto-literal-geltungszeitregel-inkrafttreten and (every $r in //akn:article/@refersTo satisfies $r = $refersto-literal-geltungszeitregel-inkrafttreten or $r = $refersto-literal-geltungszeitregel-ausserkrafttreten))"
                ruleId="SCH-00122"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e315', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="akn:article[descendant::akn:mod and not(ancestor::akn:quotedStructure)] [ $form = $form-mantelform and $typ = ($typ-gesetz, $typ-verordnung, $typ-satzung, $typ-verwaltungsvorschrift, $typ-vertragsgesetz, $typ-vertragsverordnung) ]"
    mode="group.d7e180"
    priority="107"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e324' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00123"
          context="akn:article[descendant::akn:mod and not(ancestor::akn:quotedStructure)] [ $form = $form-mantelform and $typ = ($typ-gesetz, $typ-verordnung, $typ-satzung, $typ-verwaltungsvorschrift, $typ-vertragsgesetz, $typ-vertragsverordnung) ]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00123"
          context="akn:article[descendant::akn:mod and not(ancestor::akn:quotedStructure)] [ $form = $form-mantelform and $typ = ($typ-gesetz, $typ-verordnung, $typ-satzung, $typ-verwaltungsvorschrift, $typ-vertragsgesetz, $typ-vertragsverordnung) ]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e328'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e328') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(@refersTo = ($refersto-literal-hauptaenderung, $refersto-literal-folgeaenderung, $refersto-literal-geltungszeitregel, $refersto-literal-geltungszeitregel-inkrafttreten, $refersto-literal-geltungszeitregel-ausserkrafttreten))"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00123-000"
                    test="@refersTo = ($refersto-literal-hauptaenderung, $refersto-literal-folgeaenderung, $refersto-literal-geltungszeitregel, $refersto-literal-geltungszeitregel-inkrafttreten, $refersto-literal-geltungszeitregel-ausserkrafttreten)"
                    ruleId="SCH-00123"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Eine Einzelvorschrift, die einen Änderungsbefehl beinhaltet, muss entweder als Hauptänderung, Folgeänderung oder als Geltungszeit ausgezeichnet werden.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00123-000"
                test="@refersTo = ($refersto-literal-hauptaenderung, $refersto-literal-folgeaenderung, $refersto-literal-geltungszeitregel, $refersto-literal-geltungszeitregel-inkrafttreten, $refersto-literal-geltungszeitregel-ausserkrafttreten)"
                ruleId="SCH-00123"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e324', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="/akn:akomaNtoso/akn:bill/akn:meta/akn:identification/akn:FRBRWork"
    mode="group.d7e180"
    priority="106"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e333' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00125"
          context="/akn:akomaNtoso/akn:bill/akn:meta/akn:identification/akn:FRBRWork"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00125"
          context="/akn:akomaNtoso/akn:bill/akn:meta/akn:identification/akn:FRBRWork"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e337'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e337') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(count(akn:FRBRdate) eq 1)">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00125-000"
                    test="count(akn:FRBRdate) eq 1"
                    ruleId="SCH-00125"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path(akn:FRBRdate[last()])"
                      />
                    </where-populated>
                    <svrl:text
                    > In einer Entwurfsfassung darf das Datum auf Work-Ebene nur genau einmal vorkommen. </svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00125-000"
                test="count(akn:FRBRdate) eq 1"
                ruleId="SCH-00125"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path(akn:FRBRdate[last()])"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e333', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri]/akn:preamble"
    mode="group.d7e180"
    priority="105"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e346' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00130"
          context="/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri]/akn:preamble"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00130"
          context="/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri]/akn:preamble"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e350'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e353'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e350') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="count(./akn:blockContainer[@refersTo = 'inhaltsuebersicht']) gt 1"
              >
                <variable
                  name="successful-report"
                  as="element(svrl:successful-report)"
                >
                  <svrl:successful-report
                    id="SCH-00130-005"
                    role="error"
                    test="count(./akn:blockContainer[@refersTo = 'inhaltsuebersicht']) gt 1"
                    ruleId="SCH-00130"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Es darf maximal eine Inhaltsübersicht geben.</svrl:text>
                  </svrl:successful-report>
                </variable>
                <sequence select="$successful-report" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-report
                severityThreshold="info"
                id="SCH-00130-005"
                role="error"
                test="count(./akn:blockContainer[@refersTo = 'inhaltsuebersicht']) gt 1"
                ruleId="SCH-00130"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-report>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e353') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="count(./akn:blockContainer[@refersTo = 'anlagenuebersicht']) gt 1"
              >
                <variable
                  name="successful-report"
                  as="element(svrl:successful-report)"
                >
                  <svrl:successful-report
                    id="SCH-00130-010"
                    role="error"
                    test="count(./akn:blockContainer[@refersTo = 'anlagenuebersicht']) gt 1"
                    ruleId="SCH-00130"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Es darf maximal eine Anlagenübersicht geben.</svrl:text>
                  </svrl:successful-report>
                </variable>
                <sequence select="$successful-report" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-report
                severityThreshold="info"
                id="SCH-00130-010"
                role="error"
                test="count(./akn:blockContainer[@refersTo = 'anlagenuebersicht']) gt 1"
                ruleId="SCH-00130"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-report>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e346', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="/akn:akomaNtoso/*/akn:body[$form = $form-mantelform and ($ist-entwurfsfassung or $ist-verkündungsfassung)]"
    mode="group.d7e180"
    priority="104"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e361' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00150"
          context="/akn:akomaNtoso/*/akn:body[$form = $form-mantelform and ($ist-entwurfsfassung or $ist-verkündungsfassung)]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00150"
          context="/akn:akomaNtoso/*/akn:body[$form = $form-mantelform and ($ist-entwurfsfassung or $ist-verkündungsfassung)]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e365'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e365') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="./akn:book or ./akn:part or ./akn:chapter or ./akn:subchapter or ./akn:section or ./akn:subsection or ./akn:title or ./akn:subtitle"
              >
                <variable
                  name="successful-report"
                  as="element(svrl:successful-report)"
                >
                  <svrl:successful-report
                    id="SCH-00150-005"
                    role="error"
                    test="./akn:book or ./akn:part or ./akn:chapter or ./akn:subchapter or ./akn:section or ./akn:subsection or ./akn:title or ./akn:subtitle"
                    ruleId="SCH-00150"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Der Hauptteil einer Mantelform wird nicht in weitere Gliederungsabschnitte untergliedert.</svrl:text>
                  </svrl:successful-report>
                </variable>
                <sequence select="$successful-report" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-report
                severityThreshold="info"
                id="SCH-00150-005"
                role="error"
                test="./akn:book or ./akn:part or ./akn:chapter or ./akn:subchapter or ./akn:section or ./akn:subsection or ./akn:title or ./akn:subtitle"
                ruleId="SCH-00150"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-report>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e361', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="/akn:akomaNtoso/*/akn:body[ $form = ($form-mantelform, $form-stammform) and $typ = ($typ-gesetz, $typ-verordnung, $typ-satzung, $typ-vertragsgesetz, $typ-vertragsverordnung) and ($ist-entwurfsfassung or $ist-verkündungsfassung) ]"
    mode="group.d7e180"
    priority="103"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e370' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00160"
          context="/akn:akomaNtoso/*/akn:body[ $form = ($form-mantelform, $form-stammform) and $typ = ($typ-gesetz, $typ-verordnung, $typ-satzung, $typ-vertragsgesetz, $typ-vertragsverordnung) and ($ist-entwurfsfassung or $ist-verkündungsfassung) ]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00160"
          context="/akn:akomaNtoso/*/akn:body[ $form = ($form-mantelform, $form-stammform) and $typ = ($typ-gesetz, $typ-verordnung, $typ-satzung, $typ-vertragsgesetz, $typ-vertragsverordnung) and ($ist-entwurfsfassung or $ist-verkündungsfassung) ]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e374'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e374') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not((count(//akn:article[@refersTo = $refersto-literal-geltungszeitregel]) = 1 and empty(//akn:article[@refersTo = ($refersto-literal-geltungszeitregel-inkrafttreten, $refersto-literal-geltungszeitregel-ausserkrafttreten)])) or (count(//akn:article[@refersTo = $refersto-literal-geltungszeitregel-inkrafttreten]) = 1 and count(//akn:article[@refersTo = $refersto-literal-geltungszeitregel-ausserkrafttreten]) le 1 and empty(//akn:article[@refersTo = $refersto-literal-geltungszeitregel])))"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00160-010"
                    role="error"
                    test="(count(//akn:article[@refersTo = $refersto-literal-geltungszeitregel]) = 1 and empty(//akn:article[@refersTo = ($refersto-literal-geltungszeitregel-inkrafttreten, $refersto-literal-geltungszeitregel-ausserkrafttreten)])) or (count(//akn:article[@refersTo = $refersto-literal-geltungszeitregel-inkrafttreten]) = 1 and count(//akn:article[@refersTo = $refersto-literal-geltungszeitregel-ausserkrafttreten]) le 1 and empty(//akn:article[@refersTo = $refersto-literal-geltungszeitregel]))"
                    ruleId="SCH-00160"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Innerhalb eines Regelungstextes in der Entwurfsfassung oder Verkündungsfassung, der in Stamm- oder Mantelform vorliegt, muss es entweder genau eine Einzelvorschrift bzgl. der Geltungszeit (refersTo="geltungszeitregel") ODER eine Einzelvorschrift bzgl. des Inkrafttretens (refersTo="geltungszeitregel-inkrafttreten") und optional eine Geltungszeitregel bzgl. des Außerkrafttretens (refersTo="geltungszeitregel-ausserkrafttreten") geben.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00160-010"
                role="error"
                test="(count(//akn:article[@refersTo = $refersto-literal-geltungszeitregel]) = 1 and empty(//akn:article[@refersTo = ($refersto-literal-geltungszeitregel-inkrafttreten, $refersto-literal-geltungszeitregel-ausserkrafttreten)])) or (count(//akn:article[@refersTo = $refersto-literal-geltungszeitregel-inkrafttreten]) = 1 and count(//akn:article[@refersTo = $refersto-literal-geltungszeitregel-ausserkrafttreten]) le 1 and empty(//akn:article[@refersTo = $refersto-literal-geltungszeitregel]))"
                ruleId="SCH-00160"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e370', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="/akn:akomaNtoso/*/akn:body[$form = $form-eingebundene-stammform]"
    mode="group.d7e180"
    priority="102"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e370' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00170"
          context="/akn:akomaNtoso/*/akn:body[$form = $form-eingebundene-stammform]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00170"
          context="/akn:akomaNtoso/*/akn:body[$form = $form-eingebundene-stammform]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e380'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e380') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if ($form = $form-eingebundene-stammform) then (count(//akn:article[@refersTo = ($refersto-literal-geltungszeitregel, $refersto-literal-geltungszeitregel-inkrafttreten)]) = 0) else ())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00170-000"
                    test="if ($form = $form-eingebundene-stammform) then (count(//akn:article[@refersTo = ($refersto-literal-geltungszeitregel, $refersto-literal-geltungszeitregel-inkrafttreten)]) = 0) else ()"
                    ruleId="SCH-00170"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Ein Regelungstext als eingebundene Stammform darf keine Einzelvorschrift bezüglich der Geltungszeit bzw. bezüglich des Inkrafttretens besitzen.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00170-000"
                test="if ($form = $form-eingebundene-stammform) then (count(//akn:article[@refersTo = ($refersto-literal-geltungszeitregel, $refersto-literal-geltungszeitregel-inkrafttreten)]) = 0) else ()"
                ruleId="SCH-00170"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e370', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="akn:article[@refersTo = $refersto-literal-geltungszeitregel]"
    mode="group.d7e180"
    priority="101"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e385' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00180"
          context="akn:article[@refersTo = $refersto-literal-geltungszeitregel]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00180"
          context="akn:article[@refersTo = $refersto-literal-geltungszeitregel]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e389'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e389') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="$ist-entwurfsfassung">
                <variable
                  name="successful-report"
                  as="element(svrl:successful-report)"
                >
                  <svrl:successful-report
                    id="SCH-00180-000"
                    role="warn"
                    test="$ist-entwurfsfassung"
                    ruleId="SCH-00180"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Gemäß HdR 4 sollen Inkrafttreten und Außerkrafttreten in getrennten Einzelvorschriften gefasst werden, die entsprechend mit den refersTo-Literalen geltungszeitregel-inkrafttreten und geltungszeitregel-ausserkrafttreten auszuzeichnen sind. Das refersTo-Literal "geltungszeitregel" soll in Entwurfsfassungen nicht mehr verwendet werden.</svrl:text>
                  </svrl:successful-report>
                </variable>
                <sequence select="$successful-report" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-report
                severityThreshold="info"
                id="SCH-00180-000"
                role="warn"
                test="$ist-entwurfsfassung"
                ruleId="SCH-00180"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-report>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e385', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="//akn:article/akn:paragraph//akn:list[$ist-entwurfsfassung]"
    mode="group.d7e180"
    priority="100"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e398' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00200"
          context="//akn:article/akn:paragraph//akn:list[$ist-entwurfsfassung]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00200"
          context="//akn:article/akn:paragraph//akn:list[$ist-entwurfsfassung]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e402'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e402') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="count(ancestor::akn:list) &gt; 4">
                <variable
                  name="successful-report"
                  as="element(svrl:successful-report)"
                >
                  <svrl:successful-report
                    id="SCH-00200-005"
                    role="warn"
                    test="count(ancestor::akn:list) &gt; 4"
                    ruleId="SCH-00200"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Es ist maximal eine Vierfachuntergliederung von Sätzen erlaubt.</svrl:text>
                  </svrl:successful-report>
                </variable>
                <sequence select="$successful-report" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-report
                severityThreshold="info"
                id="SCH-00200-005"
                role="warn"
                test="count(ancestor::akn:list) &gt; 4"
                ruleId="SCH-00200"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-report>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e398', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="//akn:article[$ist-entwurfsfassung]"
    mode="group.d7e180"
    priority="99"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e407' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00210"
          context="//akn:article[$ist-entwurfsfassung]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00210"
          context="//akn:article[$ist-entwurfsfassung]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e411'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e411') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="count(./akn:paragraph) &gt; 6">
                <variable
                  name="successful-report"
                  as="element(svrl:successful-report)"
                >
                  <svrl:successful-report
                    id="SCH-00210-005"
                    role="warn"
                    test="count(./akn:paragraph) &gt; 6"
                    ruleId="SCH-00210"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Es ist maximal eine Sechsfachuntergliederung in Absätzen erlaubt.</svrl:text>
                  </svrl:successful-report>
                </variable>
                <sequence select="$successful-report" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-report
                severityThreshold="info"
                id="SCH-00210-005"
                role="warn"
                test="count(./akn:paragraph) &gt; 6"
                ruleId="SCH-00210"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-report>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e407', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template match="akn:book" mode="group.d7e180" priority="98">
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e419' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule id="SCH-00230" context="akn:book">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule id="SCH-00230" context="akn:book">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e423'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e423') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="akn:book[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]"
              >
                <variable
                  name="successful-report"
                  as="element(svrl:successful-report)"
                >
                  <svrl:successful-report
                    id="SCH-00230-005"
                    role="error"
                    test="akn:book[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]"
                    ruleId="SCH-00230"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path(akn:book[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')])"
                      />
                    </where-populated>
                    <svrl:text
                    > Innerhalb eines Gliederungsabschnitts "Buch" darf diese Gliederungsebene nicht verwendet werden.</svrl:text>
                  </svrl:successful-report>
                </variable>
                <sequence select="$successful-report" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-report
                severityThreshold="info"
                id="SCH-00230-005"
                role="error"
                test="akn:book[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]"
                ruleId="SCH-00230"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path(akn:book[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')])"
                  />
                </where-populated>
              </svrl:skipped-report>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e419', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template match="akn:part" mode="group.d7e180" priority="97">
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e419' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule id="SCH-00240" context="akn:part">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule id="SCH-00240" context="akn:part">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e429'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e429') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="(akn:book | akn:part)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]"
              >
                <variable
                  name="successful-report"
                  as="element(svrl:successful-report)"
                >
                  <svrl:successful-report
                    id="SCH-00240-005"
                    role="error"
                    test="(akn:book | akn:part)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]"
                    ruleId="SCH-00240"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path((akn:book | akn:part)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')])"
                      />
                    </where-populated>
                    <svrl:text
                    > Innerhalb eines Gliederungsabschnitts "Teil" darf diese Gliederungsebene nicht verwendet werden.</svrl:text>
                  </svrl:successful-report>
                </variable>
                <sequence select="$successful-report" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-report
                severityThreshold="info"
                id="SCH-00240-005"
                role="error"
                test="(akn:book | akn:part)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]"
                ruleId="SCH-00240"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path((akn:book | akn:part)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')])"
                  />
                </where-populated>
              </svrl:skipped-report>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e419', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template match="akn:chapter" mode="group.d7e180" priority="96">
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e419' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule id="SCH-00250" context="akn:chapter">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule id="SCH-00250" context="akn:chapter">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e435'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e435') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="(akn:book | akn:part | akn:chapter)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]"
              >
                <variable
                  name="successful-report"
                  as="element(svrl:successful-report)"
                >
                  <svrl:successful-report
                    id="SCH-00250-005"
                    role="error"
                    test="(akn:book | akn:part | akn:chapter)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]"
                    ruleId="SCH-00250"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path((akn:book | akn:part | akn:chapter)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')])"
                      />
                    </where-populated>
                    <svrl:text
                    > Innerhalb eines Gliederungsabschnitts "Kapitel" darf diese Gliederungsebene nicht verwendet werden.</svrl:text>
                  </svrl:successful-report>
                </variable>
                <sequence select="$successful-report" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-report
                severityThreshold="info"
                id="SCH-00250-005"
                role="error"
                test="(akn:book | akn:part | akn:chapter)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]"
                ruleId="SCH-00250"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path((akn:book | akn:part | akn:chapter)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')])"
                  />
                </where-populated>
              </svrl:skipped-report>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e419', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template match="akn:subchapter" mode="group.d7e180" priority="95">
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e419' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule id="SCH-00260" context="akn:subchapter">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule id="SCH-00260" context="akn:subchapter">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e441'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e441') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="(akn:book | akn:part | akn:chapter | akn:subchapter)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]"
              >
                <variable
                  name="successful-report"
                  as="element(svrl:successful-report)"
                >
                  <svrl:successful-report
                    id="SCH-00260-005"
                    role="error"
                    test="(akn:book | akn:part | akn:chapter | akn:subchapter)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]"
                    ruleId="SCH-00260"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path((akn:book | akn:part | akn:chapter | akn:subchapter)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')])"
                      />
                    </where-populated>
                    <svrl:text
                    > Innerhalb eines Gliederungsabschnitts "Unterkapitel" darf diese Gliederungsebene nicht verwendet werden.</svrl:text>
                  </svrl:successful-report>
                </variable>
                <sequence select="$successful-report" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-report
                severityThreshold="info"
                id="SCH-00260-005"
                role="error"
                test="(akn:book | akn:part | akn:chapter | akn:subchapter)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]"
                ruleId="SCH-00260"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path((akn:book | akn:part | akn:chapter | akn:subchapter)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')])"
                  />
                </where-populated>
              </svrl:skipped-report>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e419', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template match="akn:section" mode="group.d7e180" priority="94">
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e419' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule id="SCH-00270" context="akn:section">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule id="SCH-00270" context="akn:section">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e447'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e447') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="(akn:book | akn:part | akn:chapter | akn:subchapter | akn:section)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]"
              >
                <variable
                  name="successful-report"
                  as="element(svrl:successful-report)"
                >
                  <svrl:successful-report
                    id="SCH-00270-005"
                    role="error"
                    test="(akn:book | akn:part | akn:chapter | akn:subchapter | akn:section)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]"
                    ruleId="SCH-00270"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path((akn:book | akn:part | akn:chapter | akn:subchapter | akn:section)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')])"
                      />
                    </where-populated>
                    <svrl:text
                    > Innerhalb eines Gliederungsabschnitts "Abschnitt" darf diese Gliederungsebene nicht verwendet werden.</svrl:text>
                  </svrl:successful-report>
                </variable>
                <sequence select="$successful-report" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-report
                severityThreshold="info"
                id="SCH-00270-005"
                role="error"
                test="(akn:book | akn:part | akn:chapter | akn:subchapter | akn:section)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]"
                ruleId="SCH-00270"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path((akn:book | akn:part | akn:chapter | akn:subchapter | akn:section)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')])"
                  />
                </where-populated>
              </svrl:skipped-report>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e419', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template match="akn:subsection" mode="group.d7e180" priority="93">
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e419' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule id="SCH-00280" context="akn:subsection">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule id="SCH-00280" context="akn:subsection">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e454'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e454') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="(akn:book | akn:part | akn:chapter | akn:subchapter | akn:section | akn:subsection)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]"
              >
                <variable
                  name="successful-report"
                  as="element(svrl:successful-report)"
                >
                  <svrl:successful-report
                    id="SCH-00280-005"
                    role="error"
                    test="(akn:book | akn:part | akn:chapter | akn:subchapter | akn:section | akn:subsection)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]"
                    ruleId="SCH-00280"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path((akn:book | akn:part | akn:chapter | akn:subchapter | akn:section | akn:subsection)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')])"
                      />
                    </where-populated>
                    <svrl:text
                    > Innerhalb eines Gliederungsabschnitts "Unterabschnitt" darf diese Gliederungsebene nicht verwendet werden.</svrl:text>
                  </svrl:successful-report>
                </variable>
                <sequence select="$successful-report" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-report
                severityThreshold="info"
                id="SCH-00280-005"
                role="error"
                test="(akn:book | akn:part | akn:chapter | akn:subchapter | akn:section | akn:subsection)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]"
                ruleId="SCH-00280"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path((akn:book | akn:part | akn:chapter | akn:subchapter | akn:section | akn:subsection)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')])"
                  />
                </where-populated>
              </svrl:skipped-report>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e419', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template match="akn:title" mode="group.d7e180" priority="92">
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e419' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule id="SCH-00290" context="akn:title">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule id="SCH-00290" context="akn:title">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e460'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e460') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="(akn:book | akn:part | akn:chapter | akn:subchapter | akn:section | akn:subsection | akn:title)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]"
              >
                <variable
                  name="successful-report"
                  as="element(svrl:successful-report)"
                >
                  <svrl:successful-report
                    id="SCH-00290-005"
                    role="error"
                    test="(akn:book | akn:part | akn:chapter | akn:subchapter | akn:section | akn:subsection | akn:title)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]"
                    ruleId="SCH-00290"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path((akn:book | akn:part | akn:chapter | akn:subchapter | akn:section | akn:subsection | akn:title)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')])"
                      />
                    </where-populated>
                    <svrl:text
                    >Innerhalb eines Gliederungsabschnitts "Titel" darf diese Gliederungsebene nicht verwendet werden.</svrl:text>
                  </svrl:successful-report>
                </variable>
                <sequence select="$successful-report" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-report
                severityThreshold="info"
                id="SCH-00290-005"
                role="error"
                test="(akn:book | akn:part | akn:chapter | akn:subchapter | akn:section | akn:subsection | akn:title)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]"
                ruleId="SCH-00290"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path((akn:book | akn:part | akn:chapter | akn:subchapter | akn:section | akn:subsection | akn:title)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')])"
                  />
                </where-populated>
              </svrl:skipped-report>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e419', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template match="akn:subtitle" mode="group.d7e180" priority="91">
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e419' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule id="SCH-00300" context="akn:subtitle">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule id="SCH-00300" context="akn:subtitle">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e466'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e466') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="(akn:book | akn:part | akn:chapter | akn:subchapter | akn:section | akn:subsection | akn:title | akn:subtitle)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]"
              >
                <variable
                  name="successful-report"
                  as="element(svrl:successful-report)"
                >
                  <svrl:successful-report
                    id="SCH-00300-005"
                    role="error"
                    test="(akn:book | akn:part | akn:chapter | akn:subchapter | akn:section | akn:subsection | akn:title | akn:subtitle)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]"
                    ruleId="SCH-00300"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path((akn:book | akn:part | akn:chapter | akn:subchapter | akn:section | akn:subsection | akn:title | akn:subtitle)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')])"
                      />
                    </where-populated>
                    <svrl:text
                    >Innerhalb eines Gliederungsabschnitts "Untertitel" darf diese Gliederungsebene nicht verwendet werden.</svrl:text>
                  </svrl:successful-report>
                </variable>
                <sequence select="$successful-report" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-report
                severityThreshold="info"
                id="SCH-00300-005"
                role="error"
                test="(akn:book | akn:part | akn:chapter | akn:subchapter | akn:section | akn:subsection | akn:title | akn:subtitle)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')]"
                ruleId="SCH-00300"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path((akn:book | akn:part | akn:chapter | akn:subchapter | akn:section | akn:subsection | akn:title | akn:subtitle)[not(@refersTo = 'vom-hdr-abweichende-gliederungsebene')])"
                  />
                </where-populated>
              </svrl:skipped-report>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e419', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="regtxt:vomHdrAbweichendeGliederung"
    mode="group.d7e180"
    priority="90"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e471' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00305"
          context="regtxt:vomHdrAbweichendeGliederung"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00305"
          context="regtxt:vomHdrAbweichendeGliederung"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e475'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e475') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="count(//(akn:book | akn:part | akn:chapter | akn:subchapter | akn:section | akn:subsection | akn:title | akn:subtitle)[@refersTo = 'vom-hdr-abweichende-gliederungsebene']) = 0"
              >
                <variable
                  name="successful-report"
                  as="element(svrl:successful-report)"
                >
                  <svrl:successful-report
                    id="SCH-00305-000"
                    role="warn"
                    test="count(//(akn:book | akn:part | akn:chapter | akn:subchapter | akn:section | akn:subsection | akn:title | akn:subtitle)[@refersTo = 'vom-hdr-abweichende-gliederungsebene']) = 0"
                    ruleId="SCH-00305"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Der Marker zur Auszeichnung einer von den Regelungen des HdR abweichenden Gliederung ist vorhanden, aber es wird keine solche Abweichungen (mittels <element
                      name="code"
                      namespace=""
                    >@refersTo='vom-hdr-abweichende-gliederungsebene')</element> ausgewiesen!</svrl:text>
                  </svrl:successful-report>
                </variable>
                <sequence select="$successful-report" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-report
                severityThreshold="info"
                id="SCH-00305-000"
                role="warn"
                test="count(//(akn:book | akn:part | akn:chapter | akn:subchapter | akn:section | akn:subsection | akn:title | akn:subtitle)[@refersTo = 'vom-hdr-abweichende-gliederungsebene']) = 0"
                ruleId="SCH-00305"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-report>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e471', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="(akn:book | akn:part | akn:chapter | akn:subchapter | akn:section | akn:subsection | akn:title | akn:subtitle)[@refersTo = 'vom-hdr-abweichende-gliederungsebene']"
    mode="group.d7e180"
    priority="89"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e471' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00306"
          context="(akn:book | akn:part | akn:chapter | akn:subchapter | akn:section | akn:subsection | akn:title | akn:subtitle)[@refersTo = 'vom-hdr-abweichende-gliederungsebene']"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00306"
          context="(akn:book | akn:part | akn:chapter | akn:subchapter | akn:section | akn:subsection | akn:title | akn:subtitle)[@refersTo = 'vom-hdr-abweichende-gliederungsebene']"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e484'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e484') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(exists(//regtxt:vomHdrAbweichendeGliederung))">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00306-000"
                    role="error"
                    test="exists(//regtxt:vomHdrAbweichendeGliederung)"
                    ruleId="SCH-00306"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Eine Gliederungsebene darf nur als von den Regelungen des HdR abweichend gekennzeichnet werden, wenn im Metadatenblock der instanz-weite Marker <element
                      name="code"
                      namespace=""
                    >&lt;meta:vomHdrAbweichendeGliederung&gt;</element> vorhanden ist.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00306-000"
                role="error"
                test="exists(//regtxt:vomHdrAbweichendeGliederung)"
                ruleId="SCH-00306"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e471', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri]/akn:conclusions"
    mode="group.d7e180"
    priority="88"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e496' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00310"
          context="/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri]/akn:conclusions"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00310"
          context="/akn:akomaNtoso/akn:bill[@name = $art-regelungstext-uri]/akn:conclusions"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e500'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e503'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e500') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="./akn:blockContainer and $bearbeitende-institution-frbrauthor = 'recht.bund.de/institution/bundesregierung'"
              >
                <variable
                  name="successful-report"
                  as="element(svrl:successful-report)"
                >
                  <svrl:successful-report
                    id="SCH-00310-005"
                    role="error"
                    test="./akn:blockContainer and $bearbeitende-institution-frbrauthor = 'recht.bund.de/institution/bundesregierung'"
                    ruleId="SCH-00310"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Der Signaturblock steht nur dem Bundestag in der Entwurfsfassung optional zur Verfügung.</svrl:text>
                  </svrl:successful-report>
                </variable>
                <sequence select="$successful-report" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-report
                severityThreshold="info"
                id="SCH-00310-005"
                role="error"
                test="./akn:blockContainer and $bearbeitende-institution-frbrauthor = 'recht.bund.de/institution/bundesregierung'"
                ruleId="SCH-00310"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-report>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e503') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="./akn:blockContainer and $bearbeitende-institution-frbrauthor = 'recht.bund.de/institution/bundesrat'"
              >
                <variable
                  name="successful-report"
                  as="element(svrl:successful-report)"
                >
                  <svrl:successful-report
                    id="SCH-00310-010"
                    role="error"
                    test="./akn:blockContainer and $bearbeitende-institution-frbrauthor = 'recht.bund.de/institution/bundesrat'"
                    ruleId="SCH-00310"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Der Signaturblock steht nur dem Bundestag in der Entwurfsfassung optional zur Verfügung.</svrl:text>
                  </svrl:successful-report>
                </variable>
                <sequence select="$successful-report" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-report
                severityThreshold="info"
                id="SCH-00310-010"
                role="error"
                test="./akn:blockContainer and $bearbeitende-institution-frbrauthor = 'recht.bund.de/institution/bundesrat'"
                ruleId="SCH-00310"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-report>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e496', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="akn:act/akn:conclusions/akn:blockContainer"
    mode="group.d7e180"
    priority="87"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e508' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00320"
          context="akn:act/akn:conclusions/akn:blockContainer"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00320"
          context="akn:act/akn:conclusions/akn:blockContainer"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e512'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e512') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(count(akn:p) ge 2)">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00320-000"
                    role="warn"
                    test="count(akn:p) ge 2"
                    ruleId="SCH-00320"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Im Schlussteil sollen mindestens zwei akn:p enthalten sein (akn:p mit Ort und Datum; ein oder mehrere akn:p mit Unterschriften).</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00320-000"
                role="warn"
                test="count(akn:p) ge 2"
                ruleId="SCH-00320"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e508', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="(akn:act | akn:bill)/akn:conclusions/akn:blockContainer/akn:p[1]"
    mode="group.d7e180"
    priority="86"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e508' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00321"
          context="(akn:act | akn:bill)/akn:conclusions/akn:blockContainer/akn:p[1]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00321"
          context="(akn:act | akn:bill)/akn:conclusions/akn:blockContainer/akn:p[1]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e518'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e521'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e518') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(exists(akn:location))">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00321-010"
                    role="warn"
                    test="exists(akn:location)"
                    ruleId="SCH-00321"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Im Schlussteil soll im ersten akn:p ein Ort (akn:location) angegeben werden.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00321-010"
                role="warn"
                test="exists(akn:location)"
                ruleId="SCH-00321"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e521') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(exists(akn:date))">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00321-020"
                    role="warn"
                    test="exists(akn:date)"
                    ruleId="SCH-00321"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Im Schlussteil soll im ersten akn:p ein Datum (akn:date) angegeben werden.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00321-020"
                role="warn"
                test="exists(akn:date)"
                ruleId="SCH-00321"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e508', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="(akn:act | akn:bill)/akn:conclusions/akn:blockContainer/akn:p[position() ge 2]"
    mode="group.d7e180"
    priority="85"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e508' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00322"
          context="(akn:act | akn:bill)/akn:conclusions/akn:blockContainer/akn:p[position() ge 2]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00322"
          context="(akn:act | akn:bill)/akn:conclusions/akn:blockContainer/akn:p[position() ge 2]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e527'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e527') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(exists(akn:signature))">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00322-000"
                    role="warn"
                    test="exists(akn:signature)"
                    ruleId="SCH-00322"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Im Schlussteil soll ab dem zweiten akn:p eine Signatur(akn:signature) angegeben werden.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00322-000"
                role="warn"
                test="exists(akn:signature)"
                ruleId="SCH-00322"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e508', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="//akn:doc[@name = $art-vorblatt-regelungstext-uri]/akn:mainBody"
    mode="group.d7e180"
    priority="84"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e535' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00330"
          context="//akn:doc[@name = $art-vorblatt-regelungstext-uri]/akn:mainBody"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00330"
          context="//akn:doc[@name = $art-vorblatt-regelungstext-uri]/akn:mainBody"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e539'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e542'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e545'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e548'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e551'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e555'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e558'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e561'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e564'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e567'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e539') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-problem-und-ziel']))"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00330-005"
                    role="error"
                    test="exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-problem-und-ziel'])"
                    ruleId="SCH-00330"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Es muss der Vorblattabschnitt 'Problem und Ziel' verwendet werden.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00330-005"
                role="error"
                test="exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-problem-und-ziel'])"
                ruleId="SCH-00330"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e542') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-loesung']))"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00330-010"
                    role="error"
                    test="exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-loesung'])"
                    ruleId="SCH-00330"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Es muss der Vorblattabschnitt 'Lösung' verwendet werden.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00330-010"
                role="error"
                test="exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-loesung'])"
                ruleId="SCH-00330"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e545') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-alternativen']))"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00330-015"
                    role="error"
                    test="exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-alternativen'])"
                    ruleId="SCH-00330"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Es muss der Vorblattabschnitt 'Alternativen' verwendet werden.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00330-015"
                role="error"
                test="exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-alternativen'])"
                ruleId="SCH-00330"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e548') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-haushaltsausgaben-ohne-erfuellungsaufwand']))"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00330-020"
                    role="error"
                    test="exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-haushaltsausgaben-ohne-erfuellungsaufwand'])"
                    ruleId="SCH-00330"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Es muss der Vorblattabschnitt 'Haushaltsausgaben ohne Erfüllungsaufwand' verwendet werden.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00330-020"
                role="error"
                test="exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-haushaltsausgaben-ohne-erfuellungsaufwand'])"
                ruleId="SCH-00330"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e551') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-erfuellungsaufwand']))"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00330-025"
                    role="error"
                    test="exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-erfuellungsaufwand'])"
                    ruleId="SCH-00330"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Es muss der Vorblattabschnitt 'Erfüllungsaufwand' verwendet wird.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00330-025"
                role="error"
                test="exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-erfuellungsaufwand'])"
                ruleId="SCH-00330"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e555') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-erfuellungsaufwand']//akn:tblock[@refersTo = 'erfuellungsaufwand-fuer-die-wirtschaft']))"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00330-030"
                    role="warn"
                    test="exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-erfuellungsaufwand']//akn:tblock[@refersTo = 'erfuellungsaufwand-fuer-die-wirtschaft'])"
                    ruleId="SCH-00330"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    > Es wird empfohlen, dass innerhalb des Erfüllungsaufwands ein Unterabschnitt 'Erfüllungsaufwand Für die Wirtschaft' verwendet wird.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00330-030"
                role="warn"
                test="exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-erfuellungsaufwand']//akn:tblock[@refersTo = 'erfuellungsaufwand-fuer-die-wirtschaft'])"
                ruleId="SCH-00330"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e558') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-erfuellungsaufwand']//akn:tblock[@refersTo = 'erfuellungsaufwand-fuer-die-wirtschaft']/akn:tblock[@refersTo = 'davon-buerokratiekosten-aus-informationspflichten']))"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00330-035"
                    role="warn"
                    test="exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-erfuellungsaufwand']//akn:tblock[@refersTo = 'erfuellungsaufwand-fuer-die-wirtschaft']/akn:tblock[@refersTo = 'davon-buerokratiekosten-aus-informationspflichten'])"
                    ruleId="SCH-00330"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Es wird empfohlen, dass innerhalb des Erfüllungsaufwands für die Wirtschaft ein Unterabschnitt 'Davon Bürokratiekosten aus Informationspflichten' verwendet wird.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00330-035"
                role="warn"
                test="exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-erfuellungsaufwand']//akn:tblock[@refersTo = 'erfuellungsaufwand-fuer-die-wirtschaft']/akn:tblock[@refersTo = 'davon-buerokratiekosten-aus-informationspflichten'])"
                ruleId="SCH-00330"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e561') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-erfuellungsaufwand']//akn:tblock[@refersTo = 'erfuellungsaufwand-fuer-buergerinnen-und-buerger']))"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00330-040"
                    role="warn"
                    test="exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-erfuellungsaufwand']//akn:tblock[@refersTo = 'erfuellungsaufwand-fuer-buergerinnen-und-buerger'])"
                    ruleId="SCH-00330"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    > Es wird empfohlen, dass innerhalb des Erfüllungsaufwands ein Unterabschnitt 'Erfüllungsaufwand Für Bürgerinnen und Bürger' verwendet wird.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00330-040"
                role="warn"
                test="exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-erfuellungsaufwand']//akn:tblock[@refersTo = 'erfuellungsaufwand-fuer-buergerinnen-und-buerger'])"
                ruleId="SCH-00330"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e564') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-erfuellungsaufwand']//akn:tblock[@refersTo = 'erfuellungsaufwand-der-verwaltung']))"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00330-045"
                    role="warn"
                    test="exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-erfuellungsaufwand']//akn:tblock[@refersTo = 'erfuellungsaufwand-der-verwaltung'])"
                    ruleId="SCH-00330"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    > Es wird empfohlen, dass innerhalb des Erfüllungsaufwands ein Unterabschnitt 'Erfüllungsaufwand der Verwaltung' verwendet wird.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00330-045"
                role="warn"
                test="exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-erfuellungsaufwand']//akn:tblock[@refersTo = 'erfuellungsaufwand-der-verwaltung'])"
                ruleId="SCH-00330"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e567') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-weitere-kosten']))"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00330-050"
                    role="error"
                    test="exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-weitere-kosten'])"
                    ruleId="SCH-00330"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Es muss der Vorblattabschnitt 'Weitere Kosten' verwendet werden.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00330-050"
                role="error"
                test="exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-weitere-kosten'])"
                ruleId="SCH-00330"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e535', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="//akn:doc[@name = $art-vorblatt-uri]/akn:mainBody/akn:hcontainer[@refersTo = $refersto-literal-vorblattabschnitt-erfüllungsaufwand]//akn:tblock"
    mode="group.d7e180"
    priority="83"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e535' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00335"
          context="//akn:doc[@name = $art-vorblatt-uri]/akn:mainBody/akn:hcontainer[@refersTo = $refersto-literal-vorblattabschnitt-erfüllungsaufwand]//akn:tblock"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00335"
          context="//akn:doc[@name = $art-vorblatt-uri]/akn:mainBody/akn:hcontainer[@refersTo = $refersto-literal-vorblattabschnitt-erfüllungsaufwand]//akn:tblock"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e577'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <variable
            name="zulässige-literale-zum-erfüllungsaufwand"
            select="( 'erfuellungsaufwand-fuer-buergerinnen-und-buerger', 'erfuellungsaufwand-fuer-die-wirtschaft', 'davon-buerokratiekosten-aus-informationspflichten', 'erfuellungsaufwand-der-verwaltung' )"
          />
          <variable
            name="mehr-als-ein-literal"
            select="count($zulässige-literale-zum-erfüllungsaufwand) gt 1"
          />
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e577') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if (exists(@refersTo)) then (@refersTo = $zulässige-literale-zum-erfüllungsaufwand) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00335-000"
                    role="error"
                    test="if (exists(@refersTo)) then (@refersTo = $zulässige-literale-zum-erfüllungsaufwand) else true()"
                    ruleId="SCH-00335"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    > Im Kontext eines Vorblattabschnitts zum Erfüllungsaufwand kann das Literal '<for-each
                      select=".[1]"
                    >
                      <value-of select="@refersTo" />
                    </for-each>' nicht verwendet werden; zulässig <for-each
                      select=".[1]"
                    >
                      <value-of
                        select="if (xs:boolean($mehr-als-ein-literal)) then 'sind' else 'ist'"
                      />
                    </for-each> hier ausschließlich <for-each select=".[1]">
                      <value-of
                        select="if (xs:boolean($mehr-als-ein-literal)) then concat(string-join( for $literal in $zulässige-literale-zum-erfüllungsaufwand[position() lt last()] return concat('''', $literal, ''''), ', '), ' oder ', concat('''', $zulässige-literale-zum-erfüllungsaufwand[last()], '''') ) else concat('''', string-join($zulässige-literale-zum-erfüllungsaufwand), '''')"
                      />
                    </for-each>.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00335-000"
                role="error"
                test="if (exists(@refersTo)) then (@refersTo = $zulässige-literale-zum-erfüllungsaufwand) else true()"
                ruleId="SCH-00335"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e535', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="//akn:doc[@name = $art-vorblatt-beschlussempfehlung-uri]/akn:mainBody"
    mode="group.d7e180"
    priority="82"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e592' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00336"
          context="//akn:doc[@name = $art-vorblatt-beschlussempfehlung-uri]/akn:mainBody"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00336"
          context="//akn:doc[@name = $art-vorblatt-beschlussempfehlung-uri]/akn:mainBody"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e596'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e599'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e602'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e605'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e596') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-problem']))"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00336-005"
                    role="error"
                    test="exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-problem'])"
                    ruleId="SCH-00336"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Es muss der Vorblattabschnitt 'Problem' verwendet werden.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00336-005"
                role="error"
                test="exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-problem'])"
                ruleId="SCH-00336"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e599') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-loesung']))"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00336-006"
                    role="error"
                    test="exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-loesung'])"
                    ruleId="SCH-00336"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Es muss der Vorblattabschnitt 'Lösung' verwendet werden.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00336-006"
                role="error"
                test="exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-loesung'])"
                ruleId="SCH-00336"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e602') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-alternativen']))"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00336-007"
                    role="error"
                    test="exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-alternativen'])"
                    ruleId="SCH-00336"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Es muss der Vorblattabschnitt 'Alternativen' verwendet werden.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00336-007"
                role="error"
                test="exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-alternativen'])"
                ruleId="SCH-00336"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e605') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-kosten']))"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00336-008"
                    role="error"
                    test="exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-kosten'])"
                    ruleId="SCH-00336"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Es muss der Vorblattabschnitt 'Kosten' verwendet werden.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00336-008"
                role="error"
                test="exists(./akn:hcontainer[@refersTo = 'vorblattabschnitt-kosten'])"
                ruleId="SCH-00336"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e592', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="/akn:akomaNtoso/akn:doc[@name = $art-begründung-uri and $typ = ($typ-gesetz, $typ-verordnung, $typ-verwaltungsvorschrift)]"
    mode="group.d7e180"
    priority="81"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e613' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00340"
          context="/akn:akomaNtoso/akn:doc[@name = $art-begründung-uri and $typ = ($typ-gesetz, $typ-verordnung, $typ-verwaltungsvorschrift)]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00340"
          context="/akn:akomaNtoso/akn:doc[@name = $art-begründung-uri and $typ = ($typ-gesetz, $typ-verordnung, $typ-verwaltungsvorschrift)]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e617'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e620'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e623'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e617') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(./akn:mainBody/akn:hcontainer[@refersTo = 'begruendung-allgemeiner-teil'])"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00340-005"
                    role="error"
                    test="./akn:mainBody/akn:hcontainer[@refersTo = 'begruendung-allgemeiner-teil']"
                    ruleId="SCH-00340"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    > Es muss ein allgemeiner Teil der Begründung vorliegen.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00340-005"
                role="error"
                test="./akn:mainBody/akn:hcontainer[@refersTo = 'begruendung-allgemeiner-teil']"
                ruleId="SCH-00340"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e620') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(./akn:mainBody/akn:hcontainer[@refersTo = 'begruendung-besonderer-teil'])"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00340-010"
                    role="error"
                    test="./akn:mainBody/akn:hcontainer[@refersTo = 'begruendung-besonderer-teil']"
                    ruleId="SCH-00340"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    > Es muss ein besonderer Teil der Begründung vorliegen.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00340-010"
                role="error"
                test="./akn:mainBody/akn:hcontainer[@refersTo = 'begruendung-besonderer-teil']"
                ruleId="SCH-00340"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e623') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="./akn:conclusions">
                <variable
                  name="successful-report"
                  as="element(svrl:successful-report)"
                >
                  <svrl:successful-report
                    id="SCH-00340-015"
                    role="error"
                    test="./akn:conclusions"
                    ruleId="SCH-00340"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    > Eine Schlussbemerkung wird nur innerhalb einer Begründung von Vertragsrechtsakten benutzt.</svrl:text>
                  </svrl:successful-report>
                </variable>
                <sequence select="$successful-report" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-report
                severityThreshold="info"
                id="SCH-00340-015"
                role="error"
                test="./akn:conclusions"
                ruleId="SCH-00340"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-report>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e613', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="//akn:doc[@name = $art-begründung-uri]/akn:mainBody//akn:hcontainer[@refersTo = 'begruendungsabschnitt-regelungsfolgen']//akn:tblock"
    mode="group.d7e180"
    priority="80"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e613' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00345"
          context="//akn:doc[@name = $art-begründung-uri]/akn:mainBody//akn:hcontainer[@refersTo = 'begruendungsabschnitt-regelungsfolgen']//akn:tblock"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00345"
          context="//akn:doc[@name = $art-begründung-uri]/akn:mainBody//akn:hcontainer[@refersTo = 'begruendungsabschnitt-regelungsfolgen']//akn:tblock"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e633'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <variable
            name="zulässige-literale-zu-regelungsfolgen"
            select="( 'begruendung-erfuellungsaufwand-fuer-buergerinnen-und-buerger', 'begruendung-erfuellungsaufwand-fuer-die-wirtschaft', 'begruendung-erfuellungsaufwand-der-verwaltung', 'regelungsfolgen-abschnitt-rechts-und-verwaltungsvereinfachung', 'regelungsfolgen-abschnitt-nachhaltigkeitsaspekte', 'regelungsfolgen-abschnitt-erfuellungsaufwand', 'regelungsfolgen-abschnitt-weitere-kosten', 'regelungsfolgen-abschnitt-gleichstellungspolitische-relevanzpruefung', 'regelungsfolgen-abschnitt-haushaltsausgaben-ohne-erfuellungsaufwand', 'regelungsfolgen-abschnitt-weitere-regelungsfolgen' )"
          />
          <variable
            name="mehr-als-ein-literal"
            select="count($zulässige-literale-zu-regelungsfolgen) gt 1"
          />
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e633') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if (exists(@refersTo)) then (@refersTo = $zulässige-literale-zu-regelungsfolgen) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00345-000"
                    role="error"
                    test="if (exists(@refersTo)) then (@refersTo = $zulässige-literale-zu-regelungsfolgen) else true()"
                    ruleId="SCH-00345"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    > Im Kontext eines Begründungsabschnitt zu Regelungsfolgen kann das Literal '<for-each
                      select=".[1]"
                    >
                      <value-of select="@refersTo" />
                    </for-each>' nicht verwendet werden; zulässig <for-each
                      select=".[1]"
                    >
                      <value-of
                        select="if (xs:boolean($mehr-als-ein-literal)) then 'sind' else 'ist'"
                      />
                    </for-each> hier ausschließlich <for-each select=".[1]">
                      <value-of
                        select="if (xs:boolean($mehr-als-ein-literal)) then concat(string-join( for $literal in $zulässige-literale-zu-regelungsfolgen[position() lt last()] return concat('''', $literal, ''''), ', '), ' oder ', concat('''', $zulässige-literale-zu-regelungsfolgen[last()], '''') ) else concat('''', string-join($zulässige-literale-zu-regelungsfolgen), '''')"
                      />
                    </for-each>.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00345-000"
                role="error"
                test="if (exists(@refersTo)) then (@refersTo = $zulässige-literale-zu-regelungsfolgen) else true()"
                ruleId="SCH-00345"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e613', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="//akn:doc[@name = $art-begründung-uri]/akn:mainBody/akn:hcontainer[@refersTo = 'begruendung-allgemeiner-teil']"
    mode="group.d7e180"
    priority="79"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e647' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00350"
          context="//akn:doc[@name = $art-begründung-uri]/akn:mainBody/akn:hcontainer[@refersTo = 'begruendung-allgemeiner-teil']"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00350"
          context="//akn:doc[@name = $art-begründung-uri]/akn:mainBody/akn:hcontainer[@refersTo = 'begruendung-allgemeiner-teil']"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e651'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e654'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e657'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e660'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e663'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e651') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(exists(./akn:hcontainer[@refersTo = 'begruendungsabschnitt-zielsetzung-und-notwendigkeit']))"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00350-005"
                    role="warn"
                    test="exists(./akn:hcontainer[@refersTo = 'begruendungsabschnitt-zielsetzung-und-notwendigkeit'])"
                    ruleId="SCH-00350"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    > Es wird empfohlen, dass ein Begründungsabschnitt 'Zielsetzung und Notwendigkeit' verwendet wird.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00350-005"
                role="warn"
                test="exists(./akn:hcontainer[@refersTo = 'begruendungsabschnitt-zielsetzung-und-notwendigkeit'])"
                ruleId="SCH-00350"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e654') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(exists(./akn:hcontainer[@refersTo = 'begruendungsabschnitt-wesentlicher-inhalt']))"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00350-010"
                    role="warn"
                    test="exists(./akn:hcontainer[@refersTo = 'begruendungsabschnitt-wesentlicher-inhalt'])"
                    ruleId="SCH-00350"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    > Es wird empfohlen, dass ein Begründungsabschnitt 'Wesentlicher Inhalt' verwendet wird.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00350-010"
                role="warn"
                test="exists(./akn:hcontainer[@refersTo = 'begruendungsabschnitt-wesentlicher-inhalt'])"
                ruleId="SCH-00350"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e657') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(exists(./akn:hcontainer[@refersTo = 'begruendungsabschnitt-alternativen']))"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00350-015"
                    role="warn"
                    test="exists(./akn:hcontainer[@refersTo = 'begruendungsabschnitt-alternativen'])"
                    ruleId="SCH-00350"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    > Es wird empfohlen, dass ein Begründungsabschnitt 'Alternativen' verwendet wird.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00350-015"
                role="warn"
                test="exists(./akn:hcontainer[@refersTo = 'begruendungsabschnitt-alternativen'])"
                ruleId="SCH-00350"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e660') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(exists(./akn:hcontainer[@refersTo = 'begruendungsabschnitt-regelungskompetenz']))"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00350-020"
                    role="warn"
                    test="exists(./akn:hcontainer[@refersTo = 'begruendungsabschnitt-regelungskompetenz'])"
                    ruleId="SCH-00350"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    > Es wird empfohlen, dass ein Begründungsabschnitt 'Regelungskompetenz' verwendet wird.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00350-020"
                role="warn"
                test="exists(./akn:hcontainer[@refersTo = 'begruendungsabschnitt-regelungskompetenz'])"
                ruleId="SCH-00350"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e663') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(exists(./akn:hcontainer[@refersTo = 'begruendungsabschnitt-regelungsfolgen']))"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00350-025"
                    role="warn"
                    test="exists(./akn:hcontainer[@refersTo = 'begruendungsabschnitt-regelungsfolgen'])"
                    ruleId="SCH-00350"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    > Es wird empfohlen, dass ein Begründungsabschnitt 'Regelungsfolgen' oder 'Gesetzesfolgen' verwendet wird.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00350-025"
                role="warn"
                test="exists(./akn:hcontainer[@refersTo = 'begruendungsabschnitt-regelungsfolgen'])"
                ruleId="SCH-00350"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e647', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="//akn:doc[@name = $art-begründung-uri]/akn:mainBody/akn:hcontainer[@refersTo = 'begruendung-allgemeiner-teil']/akn:hcontainer[@refersTo = 'begruendungsabschnitt-regelungsfolgen']/akn:content"
    mode="group.d7e180"
    priority="78"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e672' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00360"
          context="//akn:doc[@name = $art-begründung-uri]/akn:mainBody/akn:hcontainer[@refersTo = 'begruendung-allgemeiner-teil']/akn:hcontainer[@refersTo = 'begruendungsabschnitt-regelungsfolgen']/akn:content"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00360"
          context="//akn:doc[@name = $art-begründung-uri]/akn:mainBody/akn:hcontainer[@refersTo = 'begruendung-allgemeiner-teil']/akn:hcontainer[@refersTo = 'begruendungsabschnitt-regelungsfolgen']/akn:content"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e676'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e679'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e682'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e685'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e688'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e692'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e695'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e676') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(exists(./akn:tblock[@refersTo = 'regelungsfolgen-abschnitt-rechts-und-verwaltungsvereinfachung']))"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00360-005"
                    role="warn"
                    test="exists(./akn:tblock[@refersTo = 'regelungsfolgen-abschnitt-rechts-und-verwaltungsvereinfachung'])"
                    ruleId="SCH-00360"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    > Es wird empfohlen, dass innerhalb der Regelungsfolgen ein Abschnitt 'Rechts und Verwaltungsvereinfachung' verwendet wird.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00360-005"
                role="warn"
                test="exists(./akn:tblock[@refersTo = 'regelungsfolgen-abschnitt-rechts-und-verwaltungsvereinfachung'])"
                ruleId="SCH-00360"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e679') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(exists(./akn:tblock[@refersTo = 'regelungsfolgen-abschnitt-nachhaltigkeitsaspekte']))"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00360-010"
                    role="warn"
                    test="exists(./akn:tblock[@refersTo = 'regelungsfolgen-abschnitt-nachhaltigkeitsaspekte'])"
                    ruleId="SCH-00360"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    > Es wird empfohlen, dass innerhalb der Regelungsfolgen ein Abschnitt 'Nachhaltigkeitsaspekte' verwendet wird.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00360-010"
                role="warn"
                test="exists(./akn:tblock[@refersTo = 'regelungsfolgen-abschnitt-nachhaltigkeitsaspekte'])"
                ruleId="SCH-00360"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e682') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(exists(./akn:tblock[@refersTo = 'regelungsfolgen-abschnitt-erfuellungsaufwand']))"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00360-015"
                    role="warn"
                    test="exists(./akn:tblock[@refersTo = 'regelungsfolgen-abschnitt-erfuellungsaufwand'])"
                    ruleId="SCH-00360"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    > Es wird empfohlen, dass innerhalb der Regelungsfolgen ein Abschnitt 'Erfuellungsaufwand' verwendet wird.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00360-015"
                role="warn"
                test="exists(./akn:tblock[@refersTo = 'regelungsfolgen-abschnitt-erfuellungsaufwand'])"
                ruleId="SCH-00360"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e685') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(exists(./akn:tblock[@refersTo = 'regelungsfolgen-abschnitt-weitere-kosten']))"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00360-020"
                    role="warn"
                    test="exists(./akn:tblock[@refersTo = 'regelungsfolgen-abschnitt-weitere-kosten'])"
                    ruleId="SCH-00360"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    > Es wird empfohlen, dass innerhalb der Regelungsfolgen ein Abschnitt 'Weitere Kosten' verwendet wird.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00360-020"
                role="warn"
                test="exists(./akn:tblock[@refersTo = 'regelungsfolgen-abschnitt-weitere-kosten'])"
                ruleId="SCH-00360"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e688') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(exists(./akn:tblock[@refersTo = 'regelungsfolgen-abschnitt-gleichstellungspolitische-relevanzpruefung']))"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00360-025"
                    role="warn"
                    test="exists(./akn:tblock[@refersTo = 'regelungsfolgen-abschnitt-gleichstellungspolitische-relevanzpruefung'])"
                    ruleId="SCH-00360"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    > Es wird empfohlen, dass innerhalb der Regelungsfolgen ein Abschnitt 'Gleichstellungspolitische Relevanzprüfung' verwendet wird.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00360-025"
                role="warn"
                test="exists(./akn:tblock[@refersTo = 'regelungsfolgen-abschnitt-gleichstellungspolitische-relevanzpruefung'])"
                ruleId="SCH-00360"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e692') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(exists(./akn:tblock[@refersTo = 'regelungsfolgen-abschnitt-haushaltsausgaben-ohne-erfuellungsaufwand']))"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00360-030"
                    role="warn"
                    test="exists(./akn:tblock[@refersTo = 'regelungsfolgen-abschnitt-haushaltsausgaben-ohne-erfuellungsaufwand'])"
                    ruleId="SCH-00360"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    > Es wird empfohlen, dass innerhalb der Regelungsfolgen ein Abschnitt 'Haushaltsausgaben ohne Erfüllungsaufwand' verwendet wird.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00360-030"
                role="warn"
                test="exists(./akn:tblock[@refersTo = 'regelungsfolgen-abschnitt-haushaltsausgaben-ohne-erfuellungsaufwand'])"
                ruleId="SCH-00360"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e695') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(exists(./akn:tblock[@refersTo = 'regelungsfolgen-abschnitt-weitere-regelungsfolgen']))"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00360-035"
                    role="warn"
                    test="exists(./akn:tblock[@refersTo = 'regelungsfolgen-abschnitt-weitere-regelungsfolgen'])"
                    ruleId="SCH-00360"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Es wird empfohlen, dass innerhalb der Regelungsfolgen ein Abschnitt 'Weitere Regelungsfolgen' verwendet wird.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00360-035"
                role="warn"
                test="exists(./akn:tblock[@refersTo = 'regelungsfolgen-abschnitt-weitere-regelungsfolgen'])"
                ruleId="SCH-00360"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e672', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="/akn:akomaNtoso/akn:doc[@name = $art-anschreiben-uri]"
    mode="group.d7e180"
    priority="77"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e703' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00380"
          context="/akn:akomaNtoso/akn:doc[@name = $art-anschreiben-uri]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00380"
          context="/akn:akomaNtoso/akn:doc[@name = $art-anschreiben-uri]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e707'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e707') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="$bearbeitende-institution-frbrauthor = ( 'recht.bund.de/institution/bundesregierung', 'recht.bund.de/institution/bundestag', 'recht.bund.de/institution/bundeskanzler', 'recht.bund.de/institution/bundespraesident' ) and ./akn:mainBody/akn:p/akn:date[@refersTo = 'fristablauf-datum']"
              >
                <variable
                  name="successful-report"
                  as="element(svrl:successful-report)"
                >
                  <svrl:successful-report
                    id="SCH-00380-005"
                    role="error"
                    test="$bearbeitende-institution-frbrauthor = ( 'recht.bund.de/institution/bundesregierung', 'recht.bund.de/institution/bundestag', 'recht.bund.de/institution/bundeskanzler', 'recht.bund.de/institution/bundespraesident' ) and ./akn:mainBody/akn:p/akn:date[@refersTo = 'fristablauf-datum']"
                    ruleId="SCH-00380"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    > Das Fristablaufsdatum steht nur dem Bundesrat optional zur Verfügung.</svrl:text>
                  </svrl:successful-report>
                </variable>
                <sequence select="$successful-report" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-report
                severityThreshold="info"
                id="SCH-00380-005"
                role="error"
                test="$bearbeitende-institution-frbrauthor = ( 'recht.bund.de/institution/bundesregierung', 'recht.bund.de/institution/bundestag', 'recht.bund.de/institution/bundeskanzler', 'recht.bund.de/institution/bundespraesident' ) and ./akn:mainBody/akn:p/akn:date[@refersTo = 'fristablauf-datum']"
                ruleId="SCH-00380"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-report>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e703', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="/akn:akomaNtoso/akn:doc[@name = ($art-anschreiben-uri, $art-vorblatt-uri)]/akn:preface/akn:longTitle/akn:p"
    mode="group.d7e180"
    priority="76"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e715' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00390"
          context="/akn:akomaNtoso/akn:doc[@name = ($art-anschreiben-uri, $art-vorblatt-uri)]/akn:preface/akn:longTitle/akn:p"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00390"
          context="/akn:akomaNtoso/akn:doc[@name = ($art-anschreiben-uri, $art-vorblatt-uri)]/akn:preface/akn:longTitle/akn:p"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e719'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e722'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e719') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="./akn:inline[@refersTo = $refersto-literal-ausschussueberweisung] and $bearbeitende-institution-frbrauthor = ( 'recht.bund.de/institution/bundesregierung', 'recht.bund.de/institution/bundestag', 'recht.bund.de/institution/bundeskanzler', 'recht.bund.de/institution/bundespraesident' )"
              >
                <variable
                  name="successful-report"
                  as="element(svrl:successful-report)"
                >
                  <svrl:successful-report
                    id="SCH-00390-005"
                    role="error"
                    test="./akn:inline[@refersTo = $refersto-literal-ausschussueberweisung] and $bearbeitende-institution-frbrauthor = ( 'recht.bund.de/institution/bundesregierung', 'recht.bund.de/institution/bundestag', 'recht.bund.de/institution/bundeskanzler', 'recht.bund.de/institution/bundespraesident' )"
                    ruleId="SCH-00390"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    > Eine Ausschussüberweisung steht nur dem Bundesrat optional zur Verfügung.</svrl:text>
                  </svrl:successful-report>
                </variable>
                <sequence select="$successful-report" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-report
                severityThreshold="info"
                id="SCH-00390-005"
                role="error"
                test="./akn:inline[@refersTo = $refersto-literal-ausschussueberweisung] and $bearbeitende-institution-frbrauthor = ( 'recht.bund.de/institution/bundesregierung', 'recht.bund.de/institution/bundestag', 'recht.bund.de/institution/bundeskanzler', 'recht.bund.de/institution/bundespraesident' )"
                ruleId="SCH-00390"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-report>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e722') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="./akn:docNumber and $bearbeitende-institution-frbrauthor = ( 'recht.bund.de/institution/bundesregierung', 'recht.bund.de/institution/bundeskanzler', 'recht.bund.de/institution/bundespraesident' )"
              >
                <variable
                  name="successful-report"
                  as="element(svrl:successful-report)"
                >
                  <svrl:successful-report
                    id="SCH-00390-020"
                    role="error"
                    test="./akn:docNumber and $bearbeitende-institution-frbrauthor = ( 'recht.bund.de/institution/bundesregierung', 'recht.bund.de/institution/bundeskanzler', 'recht.bund.de/institution/bundespraesident' )"
                    ruleId="SCH-00390"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    > Die Drucksachennummer steht nur dem Bundestag/Bundesrat optional zur Verfügung.</svrl:text>
                  </svrl:successful-report>
                </variable>
                <sequence select="$successful-report" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-report
                severityThreshold="info"
                id="SCH-00390-020"
                role="error"
                test="./akn:docNumber and $bearbeitende-institution-frbrauthor = ( 'recht.bund.de/institution/bundesregierung', 'recht.bund.de/institution/bundeskanzler', 'recht.bund.de/institution/bundespraesident' )"
                ruleId="SCH-00390"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-report>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e715', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="/akn:akomaNtoso/akn:act[@name = $art-vereinbarung-uri]/akn:body"
    mode="group.d7e180"
    priority="75"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e731' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00400"
          context="/akn:akomaNtoso/akn:act[@name = $art-vereinbarung-uri]/akn:body"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00400"
          context="/akn:akomaNtoso/akn:act[@name = $art-vereinbarung-uri]/akn:body"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e735'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e738'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e735') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(@refersTo = ( 'notenwechsel', 'vertrag', 'fakultativprotokoll' ))"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00400-005"
                    role="error"
                    test="@refersTo = ( 'notenwechsel', 'vertrag', 'fakultativprotokoll' )"
                    ruleId="SCH-00400"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    > Der Hauptteil einer Vereinbarung muss entweder ein Notenwechsel, ein Vertrag oder ein Fakultativprotokoll sein. </svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00400-005"
                role="error"
                test="@refersTo = ( 'notenwechsel', 'vertrag', 'fakultativprotokoll' )"
                ruleId="SCH-00400"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e738') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(count(./akn:hcontainer[@refersTo = 'verbindliche-sprachfassung']) &gt;= 1)"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00400-010"
                    role="error"
                    test="count(./akn:hcontainer[@refersTo = 'verbindliche-sprachfassung']) &gt;= 1"
                    ruleId="SCH-00400"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    > Es muss mindestens eine verbindliche Sprachfassung existieren. </svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00400-010"
                role="error"
                test="count(./akn:hcontainer[@refersTo = 'verbindliche-sprachfassung']) &gt;= 1"
                ruleId="SCH-00400"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e731', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="//akn:textualMod[@type = 'insertion']"
    mode="group.d7e180"
    priority="74"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e746' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00420"
          context="//akn:textualMod[@type = 'insertion']"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00420"
          context="//akn:textualMod[@type = 'insertion']"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e750'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e750') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(./akn:destination/@pos)">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00420-005"
                    role="error"
                    test="./akn:destination/@pos"
                    ruleId="SCH-00420"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Wenn ein Änderungsbefehl eine Einfügung beinhaltet, muss in seinen Metadaten eine Positionsangabe mittels @pos angegeben werden.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00420-005"
                role="error"
                test="./akn:destination/@pos"
                ruleId="SCH-00420"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e746', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="//akn:mod[not(ancestor::akn:mod)]"
    mode="group.d7e180"
    priority="73"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e757' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00422"
          context="//akn:mod[not(ancestor::akn:mod)]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00422"
          context="//akn:mod[not(ancestor::akn:mod)]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e765'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <variable
            name="alle-textänderungen"
            select="/akn:akomaNtoso/*/akn:meta/akn:analysis/akn:activeModifications/(akn:textualMod, akn:forceMod)/akn:source"
          />
          <variable
            name="gesuchte-änderungsbefehl-id"
            select="concat('#', @eId)"
          />
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e765') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(count($alle-textänderungen[@href = $gesuchte-änderungsbefehl-id]) = 1)"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00422-000"
                    test="count($alle-textänderungen[@href = $gesuchte-änderungsbefehl-id]) = 1"
                    ruleId="SCH-00422"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Zu jedem Änderungsbefehl im Hauptteil (akn:mod) muss es im Metadatenblock genau eine zugehörige Text- oder Geltungszeitänderung geben. Es existiert jedoch keine solche Änderung (akn:textualMod bzw. akn:forceMod), deren Quellenangabe "<for-each
                      select=".[1]"
                    >
                      <value-of select="$gesuchte-änderungsbefehl-id" />
                    </for-each>" referenziert.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00422-000"
                test="count($alle-textänderungen[@href = $gesuchte-änderungsbefehl-id]) = 1"
                ruleId="SCH-00422"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e757', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="/akn:akomaNtoso/*/akn:meta/akn:analysis/akn:activeModifications/akn:textualMod/akn:source | /akn:akomaNtoso/*/akn:meta/akn:analysis/akn:activeModifications/akn:forceMod/akn:source"
    mode="group.d7e180"
    priority="72"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e772' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00423"
          context="/akn:akomaNtoso/*/akn:meta/akn:analysis/akn:activeModifications/akn:textualMod/akn:source | /akn:akomaNtoso/*/akn:meta/akn:analysis/akn:activeModifications/akn:forceMod/akn:source"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00423"
          context="/akn:akomaNtoso/*/akn:meta/akn:analysis/akn:activeModifications/akn:textualMod/akn:source | /akn:akomaNtoso/*/akn:meta/akn:analysis/akn:activeModifications/akn:forceMod/akn:source"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e778'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e783'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <variable
            name="referenzierte-änderungsbefehl-id"
            select="substring(@href, string-length('#') + 1)"
          />
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e778') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(count(//akn:mod[@eId = $referenzierte-änderungsbefehl-id]) = 1)"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00423-000"
                    test="count(//akn:mod[@eId = $referenzierte-änderungsbefehl-id]) = 1"
                    ruleId="SCH-00423"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Zu jeder im Metadatenblock deklarierten Textänderung (akn:textualMod) oder Geltungszeitänderung (akn:forceMod) muss es im Hauptteil genau einen zugehörigen Änderungsbefehl (akn:mod) geben. Es existiert jedoch kein solcher Änderungsbefehl, dessen @eId "<for-each
                      select=".[1]"
                    >
                      <value-of select="$referenzierte-änderungsbefehl-id" />
                    </for-each>" lautet.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00423-000"
                test="count(//akn:mod[@eId = $referenzierte-änderungsbefehl-id]) = 1"
                ruleId="SCH-00423"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e783') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(starts-with(@href, '#'))">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00423-010"
                    test="starts-with(@href, '#')"
                    ruleId="SCH-00423"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    > Die Referenz auf die Quelle eines Änderungsbefehls innerhalb von akn:activeModifications ist stets ein interner Verweis; sie muss deshalb eine Raute ("#") als erstes Zeichen besitzen. </svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00423-010"
                test="starts-with(@href, '#')"
                ruleId="SCH-00423"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e772', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="akn:mod[@refersTo = 'aenderungsbefehl-umnummerierung']"
    mode="group.d7e180"
    priority="71"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e789' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00424"
          context="akn:mod[@refersTo = 'aenderungsbefehl-umnummerierung']"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00424"
          context="akn:mod[@refersTo = 'aenderungsbefehl-umnummerierung']"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e793'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e793') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(exists(akn:span[@refersTo = 'neue-verweisangabe']))"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00424-000"
                    test="exists(akn:span[@refersTo = 'neue-verweisangabe'])"
                    ruleId="SCH-00424"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Ein Umnummerierungsbefehl (akn:mod mit refers-to='aenderungsbefehl-umnummerierung') muss eine neue Verweisangabe (Element akn:span mit refers-to='neue-verweisangabe') enthalten.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00424-000"
                test="exists(akn:span[@refersTo = 'neue-verweisangabe'])"
                ruleId="SCH-00424"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e789', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="akn:span[@refersTo = 'neue-verweisangabe']"
    mode="group.d7e180"
    priority="70"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e798' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00425"
          context="akn:span[@refersTo = 'neue-verweisangabe']"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00425"
          context="akn:span[@refersTo = 'neue-verweisangabe']"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e802'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e802') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(exists(parent::akn:mod[@refersTo = 'aenderungsbefehl-umnummerierung']))"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00425-000"
                    test="exists(parent::akn:mod[@refersTo = 'aenderungsbefehl-umnummerierung'])"
                    ruleId="SCH-00425"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Die Auszeichnung als neue Verweisangabe (akn:span mit refers-to='neue-verweisangabe') ist nur zulässig als Kindelement eines Umnummerierungsbefehls (akn:mod mit refers-to='aenderungsbefehl-umnummerierung').</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00425-000"
                test="exists(parent::akn:mod[@refersTo = 'aenderungsbefehl-umnummerierung'])"
                ruleId="SCH-00425"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e798', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="akn:mod[@refersTo and not(ancestor::akn:mod)]"
    mode="group.d7e180"
    priority="69"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e807' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00426"
          context="akn:mod[@refersTo and not(ancestor::akn:mod)]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00426"
          context="akn:mod[@refersTo and not(ancestor::akn:mod)]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e819'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e827'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e835'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e843'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e851'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e859'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e867'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <variable name="textualmod-type-insertion" select="'insertion'" />
          <variable
            name="textualmod-type-substitution"
            select="'substitution'"
          />
          <variable name="textualmod-type-repeal" select="'repeal'" />
          <variable name="textualmod-type-renumbering" select="'renumbering'" />
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e819') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if (@refersTo = 'aenderungsbefehl-einfuegen') then (//akn:textualMod[akn:source/@href = concat('#', current()/@eId) and @type = $textualmod-type-insertion]) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00426-000"
                    test="if (@refersTo = 'aenderungsbefehl-einfuegen') then (//akn:textualMod[akn:source/@href = concat('#', current()/@eId) and @type = $textualmod-type-insertion]) else true()"
                    ruleId="SCH-00426"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text>Ein Änderungsbefehl mit @refersTo='<for-each
                      select=".[1]"
                    >
                      <value-of select="@refersTo" />
                    </for-each>' muss im Metadatenblock mittels zugehörigem textualMod als @type='<for-each
                      select=".[1]"
                    >
                      <value-of select="$textualmod-type-insertion" />
                    </for-each>' deklariert werden.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00426-000"
                test="if (@refersTo = 'aenderungsbefehl-einfuegen') then (//akn:textualMod[akn:source/@href = concat('#', current()/@eId) and @type = $textualmod-type-insertion]) else true()"
                ruleId="SCH-00426"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e827') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if (@refersTo = 'aenderungsbefehl-ersetzen') then (//akn:textualMod[akn:source/@href = concat('#', current()/@eId) and @type = $textualmod-type-substitution]) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00426-005"
                    test="if (@refersTo = 'aenderungsbefehl-ersetzen') then (//akn:textualMod[akn:source/@href = concat('#', current()/@eId) and @type = $textualmod-type-substitution]) else true()"
                    ruleId="SCH-00426"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text>Ein Änderungsbefehl mit @refersTo='<for-each
                      select=".[1]"
                    >
                      <value-of select="@refersTo" />
                    </for-each>' muss im Metadatenblock mittels zugehörigem textualMod als @type='<for-each
                      select=".[1]"
                    >
                      <value-of select="$textualmod-type-substitution" />
                    </for-each>' deklariert werden. <for-each select=".[1]">
                      <value-of
                        select="if (not(empty(//akn:textualMod[akn:source/@href = concat('#', current()/@eId)]))) then (concat('Das zugehörige Metadatum lautet jedoch &#34;', normalize-space(concat(//akn:textualMod[akn:source/@href = concat('#', current()/@eId)]/@type, '&#34;!')))) else ()"
                      />
                    </for-each></svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00426-005"
                test="if (@refersTo = 'aenderungsbefehl-ersetzen') then (//akn:textualMod[akn:source/@href = concat('#', current()/@eId) and @type = $textualmod-type-substitution]) else true()"
                ruleId="SCH-00426"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e835') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if (@refersTo = 'aenderungsbefehl-streichen') then (//akn:textualMod[akn:source/@href = concat('#', current()/@eId) and @type = $textualmod-type-repeal]) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00426-010"
                    test="if (@refersTo = 'aenderungsbefehl-streichen') then (//akn:textualMod[akn:source/@href = concat('#', current()/@eId) and @type = $textualmod-type-repeal]) else true()"
                    ruleId="SCH-00426"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text>Ein Änderungsbefehl mit @refersTo='<for-each
                      select=".[1]"
                    >
                      <value-of select="@refersTo" />
                    </for-each>' muss im Metadatenblock mittels zugehörigem textualMod als @type='<for-each
                      select=".[1]"
                    >
                      <value-of select="$textualmod-type-repeal" />
                    </for-each>' deklariert werden. <for-each select=".[1]">
                      <value-of
                        select="if (not(empty(//akn:textualMod[akn:source/@href = concat('#', current()/@eId)]))) then (concat('Das zugehörige Metadatum lautet jedoch &#34;', normalize-space(concat(//akn:textualMod[akn:source/@href = concat('#', current()/@eId)]/@type, '&#34;!')))) else 'Dem vorliegenden Änderungbefehl ist jedoch überhaupt kein entsprechendes Metadatum (textualMod) zugeordnet!'"
                      />
                    </for-each></svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00426-010"
                test="if (@refersTo = 'aenderungsbefehl-streichen') then (//akn:textualMod[akn:source/@href = concat('#', current()/@eId) and @type = $textualmod-type-repeal]) else true()"
                ruleId="SCH-00426"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e843') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if (@refersTo = 'aenderungsbefehl-umnummerierung') then (//akn:textualMod[akn:source/@href = concat('#', current()/@eId) and @type = $textualmod-type-renumbering]) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00426-015"
                    test="if (@refersTo = 'aenderungsbefehl-umnummerierung') then (//akn:textualMod[akn:source/@href = concat('#', current()/@eId) and @type = $textualmod-type-renumbering]) else true()"
                    ruleId="SCH-00426"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text>Ein Änderungsbefehl mit @refersTo='<for-each
                      select=".[1]"
                    >
                      <value-of select="@refersTo" />
                    </for-each>' muss im Metadatenblock mittels zugehörigem textualMod als @type='<for-each
                      select=".[1]"
                    >
                      <value-of select="$textualmod-type-renumbering" />
                    </for-each>' deklariert werden. <for-each select=".[1]">
                      <value-of
                        select="if (not(empty(//akn:textualMod[akn:source/@href = concat('#', current()/@eId)]))) then (concat('Das zugehörige Metadatum lautet jedoch &#34;', normalize-space(concat(//akn:textualMod[akn:source/@href = concat('#', current()/@eId)]/@type, '&#34;!')))) else 'Dem vorliegenden Änderungbefehl ist jedoch überhaupt kein entsprechendes Metadatum (textualMod) zugeordnet!'"
                      />
                    </for-each></svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00426-015"
                test="if (@refersTo = 'aenderungsbefehl-umnummerierung') then (//akn:textualMod[akn:source/@href = concat('#', current()/@eId) and @type = $textualmod-type-renumbering]) else true()"
                ruleId="SCH-00426"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e851') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if (@refersTo = 'aenderungsbefehl-neufassung') then (//akn:textualMod[akn:source/@href = concat('#', current()/@eId) and @type = $textualmod-type-substitution]) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00426-020"
                    test="if (@refersTo = 'aenderungsbefehl-neufassung') then (//akn:textualMod[akn:source/@href = concat('#', current()/@eId) and @type = $textualmod-type-substitution]) else true()"
                    ruleId="SCH-00426"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text>Ein Änderungsbefehl mit @refersTo='<for-each
                      select=".[1]"
                    >
                      <value-of select="@refersTo" />
                    </for-each>' muss im Metadatenblock mittels zugehörigem textualMod als @type='<for-each
                      select=".[1]"
                    >
                      <value-of select="$textualmod-type-substitution" />
                    </for-each>' deklariert werden. <for-each select=".[1]">
                      <value-of
                        select="if (not(empty(//akn:textualMod[akn:source/@href = concat('#', current()/@eId)]))) then (concat('Das zugehörige Metadatum lautet jedoch &#34;', normalize-space(concat(//akn:textualMod[akn:source/@href = concat('#', current()/@eId)]/@type, '&#34;!')))) else 'Dem vorliegenden Änderungbefehl ist jedoch überhaupt kein entsprechendes Metadatum (textualMod) zugeordnet!'"
                      />
                    </for-each></svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00426-020"
                test="if (@refersTo = 'aenderungsbefehl-neufassung') then (//akn:textualMod[akn:source/@href = concat('#', current()/@eId) and @type = $textualmod-type-substitution]) else true()"
                ruleId="SCH-00426"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e859') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if (@refersTo = 'aenderungsbefehl-ersetzen-weggefallen') then (//akn:textualMod[akn:source/@href = concat('#', current()/@eId) and @type = $textualmod-type-substitution]) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00426-025"
                    test="if (@refersTo = 'aenderungsbefehl-ersetzen-weggefallen') then (//akn:textualMod[akn:source/@href = concat('#', current()/@eId) and @type = $textualmod-type-substitution]) else true()"
                    ruleId="SCH-00426"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text>Ein Änderungsbefehl mit @refersTo='<for-each
                      select=".[1]"
                    >
                      <value-of select="@refersTo" />
                    </for-each>' muss im Metadatenblock mittels zugehörigem textualMod als @type='<for-each
                      select=".[1]"
                    >
                      <value-of select="$textualmod-type-substitution" />
                    </for-each>' deklariert werden. <for-each select=".[1]">
                      <value-of
                        select="if (not(empty(//akn:textualMod[akn:source/@href = concat('#', current()/@eId)]))) then (concat('Das zugehörige Metadatum lautet jedoch &#34;', normalize-space(concat(//akn:textualMod[akn:source/@href = concat('#', current()/@eId)]/@type, '&#34;!')))) else 'Dem vorliegenden Änderungbefehl ist jedoch überhaupt kein entsprechendes Metadatum (textualMod) zugeordnet!'"
                      />
                    </for-each></svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00426-025"
                test="if (@refersTo = 'aenderungsbefehl-ersetzen-weggefallen') then (//akn:textualMod[akn:source/@href = concat('#', current()/@eId) and @type = $textualmod-type-substitution]) else true()"
                ruleId="SCH-00426"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e867') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if (@refersTo = 'aenderungsbefehl-neufassung-weggefallen') then (//akn:textualMod[akn:source/@href = concat('#', current()/@eId) and @type = $textualmod-type-substitution]) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00426-030"
                    test="if (@refersTo = 'aenderungsbefehl-neufassung-weggefallen') then (//akn:textualMod[akn:source/@href = concat('#', current()/@eId) and @type = $textualmod-type-substitution]) else true()"
                    ruleId="SCH-00426"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text>Ein Änderungsbefehl mit @refersTo='<for-each
                      select=".[1]"
                    >
                      <value-of select="@refersTo" />
                    </for-each>' muss im Metadatenblock mittels zugehörigem textualMod als @type='<for-each
                      select=".[1]"
                    >
                      <value-of select="$textualmod-type-substitution" />
                    </for-each>' deklariert werden. <for-each select=".[1]">
                      <value-of
                        select="if (not(empty(//akn:textualMod[akn:source/@href = concat('#', current()/@eId)]))) then (concat('Das zugehörige Metadatum lautet jedoch &#34;', normalize-space(concat(//akn:textualMod[akn:source/@href = concat('#', current()/@eId)]/@type, '&#34;!')))) else 'Dem vorliegenden Änderungbefehl ist jedoch überhaupt kein entsprechendes Metadatum (textualMod) zugeordnet!'"
                      />
                    </for-each></svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00426-030"
                test="if (@refersTo = 'aenderungsbefehl-neufassung-weggefallen') then (//akn:textualMod[akn:source/@href = concat('#', current()/@eId) and @type = $textualmod-type-substitution]) else true()"
                ruleId="SCH-00426"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e807', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="/akn:akomaNtoso/(akn:act | akn:bill | akn:doc | akn:statement | akn:documentCollection)//* [(@eId and exists(parent::*[@eId])) and not(local-name() = 'article' and not(ancestor::akn:quotedStructure))]"
    mode="group.d7e180"
    priority="68"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e880' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00430"
          context="/akn:akomaNtoso/(akn:act | akn:bill | akn:doc | akn:statement | akn:documentCollection)//* [(@eId and exists(parent::*[@eId])) and not(local-name() = 'article' and not(ancestor::akn:quotedStructure))]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00430"
          context="/akn:akomaNtoso/(akn:act | akn:bill | akn:doc | akn:statement | akn:documentCollection)//* [(@eId and exists(parent::*[@eId])) and not(local-name() = 'article' and not(ancestor::akn:quotedStructure))]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e892'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <variable name="trennzeichen-zwischen-eids" select="'_'" />
          <variable name="geprüfte-eid" select="@eId" />
          <variable
            name="geprüfte-eid-lokaler-teil"
            select="tokenize(@eId, $trennzeichen-zwischen-eids)[last()]"
          />
          <variable name="vorgänger-eid" select="ancestor::*[1]/@eId" />
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e892') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not($geprüfte-eid = concat($vorgänger-eid, $trennzeichen-zwischen-eids, $geprüfte-eid-lokaler-teil))"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00430-000"
                    role="error"
                    test="$geprüfte-eid = concat($vorgänger-eid, $trennzeichen-zwischen-eids, $geprüfte-eid-lokaler-teil)"
                    ruleId="SCH-00430"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Die @eId muss als Präfix vor ihrem lokalen Teil (hier: "<for-each
                      select=".[1]"
                    >
                      <value-of select="$geprüfte-eid-lokaler-teil" />
                    </for-each>") die @eId ihres nächstgelegenen und ebenfalls über eine @eId verfügenden Vorgängers besitzen, verbunden durch das Zeichen ("<for-each
                      select=".[1]"
                    >
                      <value-of select="$trennzeichen-zwischen-eids" />
                    </for-each>"). Erwartet wird im vorliegenden Fall damit konkret: "<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="concat($vorgänger-eid, $trennzeichen-zwischen-eids, $geprüfte-eid-lokaler-teil)"
                      />
                    </for-each>".</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00430-000"
                role="error"
                test="$geprüfte-eid = concat($vorgänger-eid, $trennzeichen-zwischen-eids, $geprüfte-eid-lokaler-teil)"
                ruleId="SCH-00430"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e880', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="@eId[starts-with(tokenize(., '-')[last()], $präfix-eid-zitierbar) and parent::*/akn:num != '']"
    mode="group.d7e180"
    priority="67"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e903' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-000431"
          context="@eId[starts-with(tokenize(., '-')[last()], $präfix-eid-zitierbar) and parent::*/akn:num != '']"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-000431"
          context="@eId[starts-with(tokenize(., '-')[last()], $präfix-eid-zitierbar) and parent::*/akn:num != '']"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e933'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <variable
            name="positionsangabe-ist"
            select="tokenize(., '-')[last()]"
          />
          <variable
            name="eingabe-textknoten"
            select="parent::*/akn:num/text()"
          />
          <variable name="doppelparagraph" select="'§§ '" />
          <variable name="platzhalter" select="'µµ '" />
          <variable
            name="weißraumnormalisiert"
            select="normalize-space(lower-case($eingabe-textknoten))"
          />
          <variable
            name="mit-platzhaltern"
            select="replace($weißraumnormalisiert, $doppelparagraph, $platzhalter)"
          />
          <variable
            name="ohne-sonderzeichen"
            select="replace($mit-platzhaltern, '(§ )|(art\. )|(art )|(artikel )', '')"
          />
          <variable
            name="ohne-klammern"
            select="replace($ohne-sonderzeichen, '(\()(\d+[a-z]*)(\))', '$2')"
          />
          <variable
            name="maskiert"
            select="translate($ohne-klammern, '-_.', '~~~')"
          />
          <variable
            name="ggf-mit-doppelten-paragraphenzeichen"
            select="replace($maskiert, $platzhalter, $doppelparagraph)"
          />
          <variable
            name="normalisierte-positionsangabe-eid"
            select="encode-for-uri($ggf-mit-doppelten-paragraphenzeichen)"
          />
          <variable
            name="positionsangabe-soll"
            select="lower-case(concat($präfix-eid-zitierbar, $normalisierte-positionsangabe-eid))"
          />
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e933') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if (not(parent::*/akn:num[@refersTo = 'ordinale-zaehlung-eid'])) then $positionsangabe-ist = $positionsangabe-soll else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00431-005"
                    test="if (not(parent::*/akn:num[@refersTo = 'ordinale-zaehlung-eid'])) then $positionsangabe-ist = $positionsangabe-soll else true()"
                    ruleId="SCH-000431"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text>Eine "zitierbare" (mit dem Präfix "<for-each
                      select=".[1]"
                    >
                      <value-of select="$präfix-eid-zitierbar" />
                    </for-each>" gebildete) @eId muss als Positionsangabe den normalisierten Wert der zum Elternelement zugehörigen Art- und Zählbezeichnung (akn:num) enthalten, sofern nicht dort deklariert ist, dass die Positionsangabe ordinal erfolgen soll. Im vorliegenden Fall wurde der betreffende Teil der @eId gebildet als "<for-each
                      select=".[1]"
                    >
                      <value-of select="$positionsangabe-ist" />
                    </for-each>", während der Textknoten von akn:num "<for-each
                      select=".[1]"
                    >
                      <value-of select="parent::*/akn:num" />
                    </for-each>" lautet. Erwartet würde damit die normalisierte Angabe "<for-each
                      select=".[1]"
                    >
                      <value-of select="$positionsangabe-soll" />
                    </for-each>".</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00431-005"
                test="if (not(parent::*/akn:num[@refersTo = 'ordinale-zaehlung-eid'])) then $positionsangabe-ist = $positionsangabe-soll else true()"
                ruleId="SCH-000431"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e903', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="@eId[starts-with(tokenize(., '-')[last()], $präfix-eid-zitierbar)]"
    mode="group.d7e180"
    priority="66"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e903' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00432"
          context="@eId[starts-with(tokenize(., '-')[last()], $präfix-eid-zitierbar)]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00432"
          context="@eId[starts-with(tokenize(., '-')[last()], $präfix-eid-zitierbar)]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e947'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e947') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(exists(parent::*/akn:num))">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00432-000"
                    test="exists(parent::*/akn:num)"
                    ruleId="SCH-00432"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Nur ein Element, das über eine Art- und Zählbezeichnung verfügt, kann eine "zitierbare" (mit Präfix "<for-each
                      select=".[1]"
                    >
                      <value-of select="$präfix-eid-zitierbar" />
                    </for-each>" gebildete) @eId besitzen. Andernfalls ist Präfix "<for-each
                      select=".[1]"
                    >
                      <value-of select="$präfix-eid-nummerierbar" />
                    </for-each>" zu verwenden.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00432-000"
                test="exists(parent::*/akn:num)"
                ruleId="SCH-00432"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e903', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="akn:num[@refersTo = $literal-deklaration-ausnahme-eid-zählweise]"
    mode="group.d7e180"
    priority="65"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e957' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00433"
          context="akn:num[@refersTo = $literal-deklaration-ausnahme-eid-zählweise]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00433"
          context="akn:num[@refersTo = $literal-deklaration-ausnahme-eid-zählweise]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e961'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e964'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e961') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(local-name(parent::*) = $zitierbare-elementtypen)">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00433-000"
                    test="local-name(parent::*) = $zitierbare-elementtypen"
                    ruleId="SCH-00433"
                  >
                    <where-populated>
                      <attribute name="location" select="path(parent::*)" />
                    </where-populated>
                    <svrl:text
                    >Die Deklaration der ausnahmsweisen ordinalen Zählung eines Elementes, obwohl für es eine Art- und Zählbezeichnung vorhanden ist, darf nur für Einzelvorschriften (akn:article), juristische Absätze (akn:paragraph) oder Listenuntergliederungselemente (aksn:point) vorgenommen werden.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00433-000"
                test="local-name(parent::*) = $zitierbare-elementtypen"
                ruleId="SCH-00433"
              >
                <where-populated>
                  <attribute name="location" select="path(parent::*)" />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e964') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(starts-with(tokenize(parent::*/@eId, '-')[last()], $präfix-eid-nummerierbar))"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00433-005"
                    test="starts-with(tokenize(parent::*/@eId, '-')[last()], $präfix-eid-nummerierbar)"
                    ruleId="SCH-00433"
                  >
                    <where-populated>
                      <attribute name="location" select="path(parent::*)" />
                    </where-populated>
                    <svrl:text
                    >Die @eId eines Elementes, für welches an seinem Kindelement akn:num mittels refersTo='<for-each
                      select="parent::*[1]"
                    >
                      <value-of
                        select="$literal-deklaration-ausnahme-eid-zählweise"
                      />
                    </for-each>' deklariert wurde, dass nicht die Art- und Zählbezeichnung zu verwenden sei, sondern eine ordinale Zählung vorzunehmen ist, muss im lokalen Teil seiner eId (hier: "<for-each
                      select="parent::*[1]"
                    >
                      <value-of
                        select="tokenize(parent::*/@eId, '_')[last()]"
                      />
                    </for-each>") das Präfix "<for-each select="parent::*[1]">
                      <value-of select="$präfix-eid-nummerierbar" />
                    </for-each>" besitzen, nicht "<for-each
                      select="parent::*[1]"
                    >
                      <value-of select="$präfix-eid-zitierbar" />
                    </for-each>".</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00433-005"
                test="starts-with(tokenize(parent::*/@eId, '-')[last()], $präfix-eid-nummerierbar)"
                ruleId="SCH-00433"
              >
                <where-populated>
                  <attribute name="location" select="path(parent::*)" />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e957', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="*[@eId[starts-with(tokenize(., '-')[last()], $präfix-eid-nummerierbar)] and not(local-name() = 'article')]"
    mode="group.d7e180"
    priority="64"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e977' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00434"
          context="*[@eId[starts-with(tokenize(., '-')[last()], $präfix-eid-nummerierbar)] and not(local-name() = 'article')]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00434"
          context="*[@eId[starts-with(tokenize(., '-')[last()], $präfix-eid-nummerierbar)] and not(local-name() = 'article')]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e989'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <variable name="elementtyp-bezeichner" select="local-name(.)" />
          <variable
            name="elementrang-soll"
            select="1 + count(preceding-sibling::*[local-name() eq $elementtyp-bezeichner])"
          />
          <variable
            name="lokaler-eId-teil"
            select="tokenize(@eId, '_')[last()]"
          />
          <variable
            name="elementrang-ist"
            select="xs:integer(tokenize($lokaler-eId-teil, concat('-', $präfix-eid-nummerierbar))[last()])"
          />
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e989') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not($elementrang-ist = $elementrang-soll)">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00434-000"
                    test="$elementrang-ist = $elementrang-soll"
                    ruleId="SCH-00434"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Elemente, deren @eId im lokalen Teil (hier: "<for-each
                      select=".[1]"
                    >
                      <value-of select="$lokaler-eId-teil" />
                    </for-each>) das Präfix "<for-each select=".[1]">
                      <value-of select="$präfix-eid-nummerierbar" />
                    </for-each>" enthalten, müssen ihre Positionsangabe mittels ordinaler Zählung bilden. Konkret würde hier als Position [<for-each
                      select=".[1]"
                    >
                      <value-of select="$elementrang-soll" />
                    </for-each>] erwartet, nicht [<for-each select=".[1]">
                      <value-of select="$elementrang-ist" />
                    </for-each>].</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00434-000"
                test="$elementrang-ist = $elementrang-soll"
                ruleId="SCH-00434"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e977', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="*[local-name() = $zitierbare-elementtypen and @eId[starts-with(tokenize(., '-')[last()], $präfix-eid-nummerierbar)] and exists(akn:num)]"
    mode="group.d7e180"
    priority="63"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1002' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00435"
          context="*[local-name() = $zitierbare-elementtypen and @eId[starts-with(tokenize(., '-')[last()], $präfix-eid-nummerierbar)] and exists(akn:num)]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00435"
          context="*[local-name() = $zitierbare-elementtypen and @eId[starts-with(tokenize(., '-')[last()], $präfix-eid-nummerierbar)] and exists(akn:num)]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1006'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1006') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(akn:num/@refersTo = 'ordinale-zaehlung-eid')">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00435-000"
                    test="akn:num/@refersTo = 'ordinale-zaehlung-eid'"
                    ruleId="SCH-00435"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Bei eIds, deren Positionsangabe mittels ordinaler Zählung erfolgt (n-Präfix), obwohl tatsächlich eine Art- und Zählbezeichnung vorhanden ist, muss an ihrem Kindelement akn:num explizit deklariert sein, dass die Zählung des Elternelements nichtsdestotrotz ordinal erfolgen soll.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00435-000"
                test="akn:num/@refersTo = 'ordinale-zaehlung-eid'"
                ruleId="SCH-00435"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1002', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="akn:passiveModifications/akn:textualMod/akn:destination/@href"
    mode="group.d7e180"
    priority="62"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1014' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00436"
          context="akn:passiveModifications/akn:textualMod/akn:destination/@href"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00436"
          context="akn:passiveModifications/akn:textualMod/akn:destination/@href"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1018'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e1025'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <variable
            name="lokaler-verweis"
            select="substring(., 2) (: das Rautesymbol des lokalen Verweises überspringen :)"
          />
          <variable
            name="lokaler-verweis-ohne-zeichenbereich"
            select="replace($lokaler-verweis, '(/\d+-\d+)$', '')"
          />
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1018') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(starts-with(., '#'))">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00436-000"
                    test="starts-with(., '#')"
                    ruleId="SCH-00436"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Die Zielangabe einer Textänderung innerhalb von "aenderungenPassiv" (also: akn:passiveModifications/akn:textualMod/akn:destination/@href) muss stets einen Verweis auf ein Element innerhalb des aktuellen Dokumentes enthalten. Solche lokalen Verweise beginnen zwingend mit einer Raute ("#").</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00436-000"
                test="starts-with(., '#')"
                ruleId="SCH-00436"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1025') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(count(/akn:akomaNtoso//*[@eId eq $lokaler-verweis-ohne-zeichenbereich]) = 1)"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00436-010"
                    test="count(/akn:akomaNtoso//*[@eId eq $lokaler-verweis-ohne-zeichenbereich]) = 1"
                    ruleId="SCH-00436"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Verweise auf ein jeweiliges ELement sind innerhalb von passiveModifications grundsätzlich lokal. Zur hier angegebenen Referenz existiert jedoch im vorliegenden Dokument kein Element mit korrespondierender @eId ("<for-each
                      select=".[1]"
                    >
                      <value-of select="$lokaler-verweis-ohne-zeichenbereich" />
                    </for-each>")!</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00436-010"
                test="count(/akn:akomaNtoso//*[@eId eq $lokaler-verweis-ohne-zeichenbereich]) = 1"
                ruleId="SCH-00436"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1014', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="akn:article[@eId[starts-with(tokenize(., '-')[last()], $präfix-eid-nummerierbar)]]"
    mode="group.d7e180"
    priority="61"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1032' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00437"
          context="akn:article[@eId[starts-with(tokenize(., '-')[last()], $präfix-eid-nummerierbar)]]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00437"
          context="akn:article[@eId[starts-with(tokenize(., '-')[last()], $präfix-eid-nummerierbar)]]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1047'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <variable name="elementtyp-bezeichner" select="local-name(.)" />
          <variable
            name="id-des-übergeordneten-änderungsbefehls"
            select="ancestor::akn:mod/generate-id()"
          />
          <variable
            name="elementrang-soll"
            select="if (ancestor::akn:mod) then (1 + count(preceding::akn:article[ancestor::akn:mod/generate-id() eq $id-des-übergeordneten-änderungsbefehls])) else (1 + count(preceding::akn:article[not(ancestor::akn:mod)]))"
          />
          <variable
            name="lokaler-eId-teil"
            select="tokenize(@eId, '_')[last()]"
          />
          <variable
            name="elementrang-ist"
            select="xs:integer(tokenize($lokaler-eId-teil, concat('-', $präfix-eid-nummerierbar))[last()])"
          />
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1047') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not($elementrang-ist = $elementrang-soll)">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00437-000"
                    test="$elementrang-ist = $elementrang-soll"
                    ruleId="SCH-00437"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Bei Einzelvorschriften, für die eine ordinale Zählung deklariert wurde, muss die Positionsangabe zu ihrem Rang passen; dabei ist innerhalb von Änderungsbefehlen lokal zu zählen (d. h. nur auf der Menge der Geschwisterknoten), während außerhalb im Gegensatz dazu global zu zählen ist, wobei jedoch Einzelvorschriften innerhalb etwaiger Änderungsbefehle nicht mitgezählt werden. Konkret würde für die vorliegende Einzelvorschrift als Position [<for-each
                      select=".[1]"
                    >
                      <value-of select="$elementrang-soll" />
                    </for-each>] erwartet, nicht [<for-each select=".[1]">
                      <value-of select="$elementrang-ist" />
                    </for-each>].</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00437-000"
                test="$elementrang-ist = $elementrang-soll"
                ruleId="SCH-00437"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1032', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="/akn:akomaNtoso/(akn:act | akn:bill | akn:doc | akn:statement | akn:documentCollection)//akn:article[not(ancestor::akn:quotedStructure)]/@eId"
    mode="group.d7e180"
    priority="60"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1057' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00438"
          context="/akn:akomaNtoso/(akn:act | akn:bill | akn:doc | akn:statement | akn:documentCollection)//akn:article[not(ancestor::akn:quotedStructure)]/@eId"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00438"
          context="/akn:akomaNtoso/(akn:act | akn:bill | akn:doc | akn:statement | akn:documentCollection)//akn:article[not(ancestor::akn:quotedStructure)]/@eId"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1061'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1061') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(starts-with(., 'art-'))">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00438-000"
                    test="starts-with(., 'art-')"
                    ruleId="SCH-00438"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Die @eId einer Einzelvorschrift außerhalb einer akn:quotedStructure muss mit "art-" beginnen; anders als andere Elemente 'erbt' sie nicht die @eId ihres Elternelements als Präfix!</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00438-000"
                test="starts-with(., 'art-')"
                ruleId="SCH-00438"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1057', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="/akn:akomaNtoso/(akn:act | akn:bill | akn:doc | akn:statement | akn:documentCollection)//akn:article[ancestor::akn:quotedStructure]/@eId"
    mode="group.d7e180"
    priority="59"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1057' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00439"
          context="/akn:akomaNtoso/(akn:act | akn:bill | akn:doc | akn:statement | akn:documentCollection)//akn:article[ancestor::akn:quotedStructure]/@eId"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00439"
          context="/akn:akomaNtoso/(akn:act | akn:bill | akn:doc | akn:statement | akn:documentCollection)//akn:article[ancestor::akn:quotedStructure]/@eId"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1067'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1067') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(matches(., '([a-zäöüß0-9]+-(n[1-9]{1}[0-9]*|z[0-9a-zäöüß~%]*)_)+art-(z|n)\d*[0-9a-zäöüß~%]*$'))"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00439-000"
                    test="matches(., '([a-zäöüß0-9]+-(n[1-9]{{1}}[0-9]*|z[0-9a-zäöüß~%]*)_)+art-(z|n)\d*[0-9a-zäöüß~%]*$')"
                    ruleId="SCH-00439"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Die @eId einer Einzelvorschrift innerhalb einer akn:quotedStructure darf nicht lediglich aus ihrem eigenen Kurzbezeichner, "art-", sowie einer Positionsangabe bestehen; anders als bei Einzelvorschriften außerhalb der quotedStructure wird innerhalb ebendieser die eId nicht "abgeschnitten", sondern "erbt" zwingend die @eId ihres Elternknotens als Präfix.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00439-000"
                test="matches(., '([a-zäöüß0-9]+-(n[1-9]{{1}}[0-9]*|z[0-9a-zäöüß~%]*)_)+art-(z|n)\d*[0-9a-zäöüß~%]*$')"
                ruleId="SCH-00439"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1057', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template match="@eId" mode="group.d7e180" priority="58">
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1072' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule id="SCH-00450" context="@eId">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule id="SCH-00450" context="@eId">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1078'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <variable name="kontext-eId-inhalt" select="." />
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1078') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(count(key('nodes-by-eId', $kontext-eId-inhalt)) eq 1)"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00450-000"
                    role="error"
                    test="count(key('nodes-by-eId', $kontext-eId-inhalt)) eq 1"
                    ruleId="SCH-00450"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Eine eId muss dokumentweit einmalig sein; eId "<for-each
                      select=".[1]"
                    >
                      <value-of select="$kontext-eId-inhalt" />
                    </for-each>" kommt im vorliegenden Dokument jedoch <for-each
                      select=".[1]"
                    >
                      <value-of
                        select="count(key('nodes-by-eId', $kontext-eId-inhalt))"
                      />
                    </for-each>-mal vor!</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00450-000"
                role="error"
                test="count(key('nodes-by-eId', $kontext-eId-inhalt)) eq 1"
                ruleId="SCH-00450"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1072', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template match="@GUID" mode="group.d7e180" priority="57">
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1087' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule id="SCH-00460" context="@GUID">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule id="SCH-00460" context="@GUID">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1095'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <variable name="kontext-guid-inhalt" select="." />
          <variable
            name="häufigkeit-der-aktuellen-guid"
            select="count(key('nodes-by-GUID', $kontext-guid-inhalt))"
          />
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1095') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(xs:int($häufigkeit-der-aktuellen-guid) eq 1)">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00460-000"
                    role="error"
                    test="xs:int($häufigkeit-der-aktuellen-guid) eq 1"
                    ruleId="SCH-00460"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text>GUIDs müssen einmalig sein; "<for-each
                      select=".[1]"
                    >
                      <value-of select="$kontext-guid-inhalt" />
                    </for-each>" kommt jedoch <for-each select=".[1]">
                      <value-of select="$häufigkeit-der-aktuellen-guid" />
                    </for-each>-mal im Dokument vor!</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00460-000"
                role="error"
                test="xs:int($häufigkeit-der-aktuellen-guid) eq 1"
                ruleId="SCH-00460"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1087', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRthis"
    mode="group.d7e180"
    priority="56"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1259' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00500"
          context="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRthis"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00500"
          context="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRthis"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1263'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e1271'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e1279'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1263') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if ($ist-entwurfsfassung) then (@value = $FRBRthis-entwurfsfassung-work-inhalt) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00500-005"
                    role="error"
                    test="if ($ist-entwurfsfassung) then (@value = $FRBRthis-entwurfsfassung-work-inhalt) else true()"
                    ruleId="SCH-00500"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text><for-each select=".[1]">
                      <value-of
                        select="$FRBRthis-entwurfsfassung-work-beschreibung"
                      />
                    </for-each> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$FRBRthis-entwurfsfassung-work-aufbau"
                      />
                    </for-each>". Erwartet würde hier konkret: "<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$FRBRthis-entwurfsfassung-work-inhalt"
                      />
                    </for-each>". </svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00500-005"
                role="error"
                test="if ($ist-entwurfsfassung) then (@value = $FRBRthis-entwurfsfassung-work-inhalt) else true()"
                ruleId="SCH-00500"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1271') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if ($ist-verkündungsfassung) then (@value = $FRBRthis-verkündungsfassung-work-inhalt) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00500-010"
                    role="error"
                    test="if ($ist-verkündungsfassung) then (@value = $FRBRthis-verkündungsfassung-work-inhalt) else true()"
                    ruleId="SCH-00500"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text><for-each select=".[1]">
                      <value-of
                        select="$FRBRthis-verkündungsfassung-work-beschreibung"
                      />
                    </for-each> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$FRBRthis-verkündungsfassung-work-aufbau"
                      />
                    </for-each>". Erwartet würde hier konkret: "<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$FRBRthis-verkündungsfassung-work-inhalt"
                      />
                    </for-each>".</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00500-010"
                role="error"
                test="if ($ist-verkündungsfassung) then (@value = $FRBRthis-verkündungsfassung-work-inhalt) else true()"
                ruleId="SCH-00500"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1279') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if ($ist-konsolidierte-fassung) then (@value = $FRBRthis-konsolidierte-fassung-work-inhalt) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00500-015"
                    role="error"
                    test="if ($ist-konsolidierte-fassung) then (@value = $FRBRthis-konsolidierte-fassung-work-inhalt) else true()"
                    ruleId="SCH-00500"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text><for-each select=".[1]">
                      <value-of
                        select="$FRBRthis-konsolidierte-fassung-work-beschreibung"
                      />
                    </for-each> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$FRBRthis-konsolidierte-fassung-work-aufbau"
                      />
                    </for-each>". Erwartet würde hier konkret: "<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$FRBRthis-konsolidierte-fassung-work-inhalt"
                      />
                    </for-each>".</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00500-015"
                role="error"
                test="if ($ist-konsolidierte-fassung) then (@value = $FRBRthis-konsolidierte-fassung-work-inhalt) else true()"
                ruleId="SCH-00500"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1259', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRExpression/akn:FRBRthis"
    mode="group.d7e180"
    priority="55"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1259' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00510"
          context="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRExpression/akn:FRBRthis"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00510"
          context="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRExpression/akn:FRBRthis"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1290'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e1298'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e1306'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1290') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if ($ist-entwurfsfassung) then (@value = $FRBRthis-entwurfsfassung-expression-inhalt) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00510-005"
                    role="error"
                    test="if ($ist-entwurfsfassung) then (@value = $FRBRthis-entwurfsfassung-expression-inhalt) else true()"
                    ruleId="SCH-00510"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text><for-each select=".[1]">
                      <value-of
                        select="$FRBRthis-entwurfsfassung-expression-beschreibung"
                      />
                    </for-each> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$FRBRthis-entwurfsfassung-expression-aufbau"
                      />
                    </for-each>". Erwartet würde hier konkret: "<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$FRBRthis-entwurfsfassung-expression-inhalt"
                      />
                    </for-each>". </svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00510-005"
                role="error"
                test="if ($ist-entwurfsfassung) then (@value = $FRBRthis-entwurfsfassung-expression-inhalt) else true()"
                ruleId="SCH-00510"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1298') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if ($ist-verkündungsfassung) then (@value = $FRBRthis-verkündungsfassung-expression-inhalt) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00510-010"
                    role="error"
                    test="if ($ist-verkündungsfassung) then (@value = $FRBRthis-verkündungsfassung-expression-inhalt) else true()"
                    ruleId="SCH-00510"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text><for-each select=".[1]">
                      <value-of
                        select="$FRBRthis-verkündungsfassung-expression-beschreibung"
                      />
                    </for-each> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$FRBRthis-verkündungsfassung-expression-aufbau"
                      />
                    </for-each>". Erwartet würde hier konkret: "<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$FRBRthis-verkündungsfassung-expression-inhalt"
                      />
                    </for-each>".</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00510-010"
                role="error"
                test="if ($ist-verkündungsfassung) then (@value = $FRBRthis-verkündungsfassung-expression-inhalt) else true()"
                ruleId="SCH-00510"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1306') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if ($ist-konsolidierte-fassung) then (@value = $FRBRthis-konsolidierte-fassung-expression-inhalt) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00510-015"
                    role="error"
                    test="if ($ist-konsolidierte-fassung) then (@value = $FRBRthis-konsolidierte-fassung-expression-inhalt) else true()"
                    ruleId="SCH-00510"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text><for-each select=".[1]">
                      <value-of
                        select="$FRBRthis-konsolidierte-fassung-expression-beschreibung"
                      />
                    </for-each> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$FRBRthis-konsolidierte-fassung-expression-aufbau"
                      />
                    </for-each>". Erwartet würde hier konkret: "<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$FRBRthis-konsolidierte-fassung-expression-inhalt"
                      />
                    </for-each>".</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00510-015"
                role="error"
                test="if ($ist-konsolidierte-fassung) then (@value = $FRBRthis-konsolidierte-fassung-expression-inhalt) else true()"
                ruleId="SCH-00510"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1259', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRManifestation/akn:FRBRthis"
    mode="group.d7e180"
    priority="54"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1259' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00520"
          context="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRManifestation/akn:FRBRthis"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00520"
          context="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRManifestation/akn:FRBRthis"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1317'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e1325'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e1333'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1317') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if ($ist-entwurfsfassung) then (@value = $FRBRthis-entwurfsfassung-manifestation-inhalt) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00520-005"
                    role="error"
                    test="if ($ist-entwurfsfassung) then (@value = $FRBRthis-entwurfsfassung-manifestation-inhalt) else true()"
                    ruleId="SCH-00520"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text><for-each select=".[1]">
                      <value-of
                        select="$FRBRthis-entwurfsfassung-manifestation-beschreibung"
                      />
                    </for-each> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$FRBRthis-entwurfsfassung-manifestation-aufbau"
                      />
                    </for-each>". Erwartet würde hier konkret: "<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$FRBRthis-entwurfsfassung-manifestation-inhalt"
                      />
                    </for-each>".</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00520-005"
                role="error"
                test="if ($ist-entwurfsfassung) then (@value = $FRBRthis-entwurfsfassung-manifestation-inhalt) else true()"
                ruleId="SCH-00520"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1325') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if ($ist-verkündungsfassung) then (@value = $FRBRthis-verkündungsfassung-manifestation-inhalt) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00520-010"
                    role="error"
                    test="if ($ist-verkündungsfassung) then (@value = $FRBRthis-verkündungsfassung-manifestation-inhalt) else true()"
                    ruleId="SCH-00520"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text><for-each select=".[1]">
                      <value-of
                        select="$FRBRthis-verkündungsfassung-manifestation-beschreibung"
                      />
                    </for-each> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$FRBRthis-verkündungsfassung-manifestation-aufbau"
                      />
                    </for-each>". Erwartet würde hier konkret: "<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$FRBRthis-verkündungsfassung-manifestation-inhalt"
                      />
                    </for-each>".</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00520-010"
                role="error"
                test="if ($ist-verkündungsfassung) then (@value = $FRBRthis-verkündungsfassung-manifestation-inhalt) else true()"
                ruleId="SCH-00520"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1333') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if ($ist-konsolidierte-fassung) then (@value = $FRBRthis-konsolidierte-fassung-manifestation-inhalt) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00520-015"
                    role="error"
                    test="if ($ist-konsolidierte-fassung) then (@value = $FRBRthis-konsolidierte-fassung-manifestation-inhalt) else true()"
                    ruleId="SCH-00520"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text><for-each select=".[1]">
                      <value-of
                        select="$FRBRthis-konsolidierte-fassung-manifestation-beschreibung"
                      />
                    </for-each> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$FRBRthis-konsolidierte-fassung-manifestation-aufbau"
                      />
                    </for-each>". Erwartet würde hier konkret: "<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$FRBRthis-konsolidierte-fassung-manifestation-inhalt"
                      />
                    </for-each>".</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00520-015"
                role="error"
                test="if ($ist-konsolidierte-fassung) then (@value = $FRBRthis-konsolidierte-fassung-manifestation-inhalt) else true()"
                ruleId="SCH-00520"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1259', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRuri"
    mode="group.d7e180"
    priority="53"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1343' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00530"
          context="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRuri"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00530"
          context="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRuri"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1347'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e1355'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e1363'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1347') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if ($ist-entwurfsfassung) then (@value = $FRBRuri-entwurfsfassung-work-inhalt) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00530-005"
                    role="error"
                    test="if ($ist-entwurfsfassung) then (@value = $FRBRuri-entwurfsfassung-work-inhalt) else true()"
                    ruleId="SCH-00530"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text><for-each select=".[1]">
                      <value-of
                        select="$FRBRuri-entwurfsfassung-work-beschreibung"
                      />
                    </for-each> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<for-each
                      select=".[1]"
                    >
                      <value-of select="$FRBRuri-entwurfsfassung-work-aufbau" />
                    </for-each>". Erwartet würde hier konkret: "<for-each
                      select=".[1]"
                    >
                      <value-of select="$FRBRuri-entwurfsfassung-work-inhalt" />
                    </for-each>".</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00530-005"
                role="error"
                test="if ($ist-entwurfsfassung) then (@value = $FRBRuri-entwurfsfassung-work-inhalt) else true()"
                ruleId="SCH-00530"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1355') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if ($ist-verkündungsfassung) then (@value = $FRBRuri-verkündungsfassung-work-inhalt) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00530-010"
                    role="error"
                    test="if ($ist-verkündungsfassung) then (@value = $FRBRuri-verkündungsfassung-work-inhalt) else true()"
                    ruleId="SCH-00530"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text><for-each select=".[1]">
                      <value-of
                        select="$FRBRuri-verkündungsfassung-work-beschreibung"
                      />
                    </for-each> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$FRBRuri-verkündungsfassung-work-aufbau"
                      />
                    </for-each>". Erwartet würde hier konkret: "<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$FRBRuri-verkündungsfassung-work-inhalt"
                      />
                    </for-each>".</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00530-010"
                role="error"
                test="if ($ist-verkündungsfassung) then (@value = $FRBRuri-verkündungsfassung-work-inhalt) else true()"
                ruleId="SCH-00530"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1363') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if ($ist-konsolidierte-fassung) then (@value = $FRBRuri-konsolidierte-fassung-work-inhalt) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00530-015"
                    role="error"
                    test="if ($ist-konsolidierte-fassung) then (@value = $FRBRuri-konsolidierte-fassung-work-inhalt) else true()"
                    ruleId="SCH-00530"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text><for-each select=".[1]">
                      <value-of
                        select="$FRBRuri-konsolidierte-fassung-work-beschreibung"
                      />
                    </for-each> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$FRBRuri-verkündungsfassung-work-aufbau"
                      />
                    </for-each>". Erwartet würde hier konkret: "<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$FRBRuri-konsolidierte-fassung-work-inhalt"
                      />
                    </for-each>".</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00530-015"
                role="error"
                test="if ($ist-konsolidierte-fassung) then (@value = $FRBRuri-konsolidierte-fassung-work-inhalt) else true()"
                ruleId="SCH-00530"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1343', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRExpression/akn:FRBRuri"
    mode="group.d7e180"
    priority="52"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1343' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00540"
          context="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRExpression/akn:FRBRuri"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00540"
          context="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRExpression/akn:FRBRuri"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1374'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e1382'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e1390'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1374') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if ($ist-entwurfsfassung) then (@value = $FRBRuri-entwurfsfassung-expression-inhalt) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00540-005"
                    role="error"
                    test="if ($ist-entwurfsfassung) then (@value = $FRBRuri-entwurfsfassung-expression-inhalt) else true()"
                    ruleId="SCH-00540"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text><for-each select=".[1]">
                      <value-of
                        select="$FRBRuri-entwurfsfassung-expression-beschreibung"
                      />
                    </for-each> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$FRBRuri-entwurfsfassung-expression-aufbau"
                      />
                    </for-each>". Erwartet würde hier konkret: "<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$FRBRuri-entwurfsfassung-expression-inhalt"
                      />
                    </for-each>".</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00540-005"
                role="error"
                test="if ($ist-entwurfsfassung) then (@value = $FRBRuri-entwurfsfassung-expression-inhalt) else true()"
                ruleId="SCH-00540"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1382') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if ($ist-verkündungsfassung) then (@value = $FRBRuri-verkündungsfassung-expression-inhalt) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00540-010"
                    role="error"
                    test="if ($ist-verkündungsfassung) then (@value = $FRBRuri-verkündungsfassung-expression-inhalt) else true()"
                    ruleId="SCH-00540"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text><for-each select=".[1]">
                      <value-of
                        select="$FRBRuri-verkündungsfassung-expression-beschreibung"
                      />
                    </for-each> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$FRBRuri-verkündungsfassung-expression-aufbau"
                      />
                    </for-each>". Erwartet würde hier konkret: "<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$FRBRuri-verkündungsfassung-expression-inhalt"
                      />
                    </for-each>".</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00540-010"
                role="error"
                test="if ($ist-verkündungsfassung) then (@value = $FRBRuri-verkündungsfassung-expression-inhalt) else true()"
                ruleId="SCH-00540"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1390') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if ($ist-konsolidierte-fassung) then (@value = $FRBRuri-konsolidierte-fassung-expression-inhalt) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00540-015"
                    role="error"
                    test="if ($ist-konsolidierte-fassung) then (@value = $FRBRuri-konsolidierte-fassung-expression-inhalt) else true()"
                    ruleId="SCH-00540"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text><for-each select=".[1]">
                      <value-of
                        select="$FRBRuri-konsolidierte-fassung-expression-beschreibung"
                      />
                    </for-each> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$FRBRuri-konsolidierte-fassung-expression-aufbau"
                      />
                    </for-each>". Erwartet würde hier konkret: "<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$FRBRuri-konsolidierte-fassung-expression-inhalt"
                      />
                    </for-each>".</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00540-015"
                role="error"
                test="if ($ist-konsolidierte-fassung) then (@value = $FRBRuri-konsolidierte-fassung-expression-inhalt) else true()"
                ruleId="SCH-00540"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1343', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRManifestation/akn:FRBRuri"
    mode="group.d7e180"
    priority="51"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1343' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00550"
          context="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRManifestation/akn:FRBRuri"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00550"
          context="/akn:akomaNtoso/*/akn:meta/akn:identification/akn:FRBRManifestation/akn:FRBRuri"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1401'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e1409'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e1417'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e1425'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1401') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if ($ist-entwurfsfassung) then (@value = $FRBRuri-entwurfsfassung-manifestation-inhalt) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00550-005"
                    role="error"
                    test="if ($ist-entwurfsfassung) then (@value = $FRBRuri-entwurfsfassung-manifestation-inhalt) else true()"
                    ruleId="SCH-00550"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text><for-each select=".[1]">
                      <value-of
                        select="$FRBRuri-entwurfsfassung-manifestation-beschreibung"
                      />
                    </for-each> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$FRBRuri-entwurfsfassung-manifestation-aufbau"
                      />
                    </for-each>". Erwartet würde hier konkret: "<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$FRBRuri-entwurfsfassung-manifestation-inhalt"
                      />
                    </for-each>".</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00550-005"
                role="error"
                test="if ($ist-entwurfsfassung) then (@value = $FRBRuri-entwurfsfassung-manifestation-inhalt) else true()"
                ruleId="SCH-00550"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1409') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if ($ist-verkündungsfassung) then (@value = $FRBRuri-verkündungsfassung-manifestation-inhalt) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00550-010"
                    role="error"
                    test="if ($ist-verkündungsfassung) then (@value = $FRBRuri-verkündungsfassung-manifestation-inhalt) else true()"
                    ruleId="SCH-00550"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text><for-each select=".[1]">
                      <value-of
                        select="$FRBRuri-verkündungsfassung-manifestation-beschreibung"
                      />
                    </for-each> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$FRBRuri-verkündungsfassung-manifestation-aufbau"
                      />
                    </for-each>". Erwartet würde hier konkret: "<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$FRBRuri-verkündungsfassung-manifestation-inhalt"
                      />
                    </for-each>".</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00550-010"
                role="error"
                test="if ($ist-verkündungsfassung) then (@value = $FRBRuri-verkündungsfassung-manifestation-inhalt) else true()"
                ruleId="SCH-00550"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1417') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if ($ist-konsolidierte-fassung) then (@value = $FRBRuri-konsolidierte-fassung-manifestation-inhalt) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00550-015"
                    role="error"
                    test="if ($ist-konsolidierte-fassung) then (@value = $FRBRuri-konsolidierte-fassung-manifestation-inhalt) else true()"
                    ruleId="SCH-00550"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text><for-each select=".[1]">
                      <value-of
                        select="$FRBRuri-konsolidierte-fassung-manifestation-beschreibung"
                      />
                    </for-each> muss sich aus den Inhalten der jeweiligen Metadaten zusammensetzen in der Form "<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$FRBRuri-konsolidierte-fassung-manifestation-aufbau"
                      />
                    </for-each>". Erwartet würde hier konkret: "<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$FRBRuri-konsolidierte-fassung-manifestation-inhalt"
                      />
                    </for-each>".</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00550-015"
                role="error"
                test="if ($ist-konsolidierte-fassung) then (@value = $FRBRuri-konsolidierte-fassung-manifestation-inhalt) else true()"
                ruleId="SCH-00550"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1425') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(starts-with(@value, $authority-egesetzgebung) or starts-with(@value, $authority-everkündung) or starts-with(@value, $authority-rechtsinformationssystem))"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00550-020"
                    role="error"
                    test="starts-with(@value, $authority-egesetzgebung) or starts-with(@value, $authority-everkündung) or starts-with(@value, $authority-rechtsinformationssystem)"
                    ruleId="SCH-00550"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Der Manifestation-ELI muss zwingend mit einer der drei zulässigen authorities beginnen: "<for-each
                      select=".[1]"
                    >
                      <value-of select="$authority-egesetzgebung" />
                    </for-each>", "<for-each select=".[1]">
                      <value-of select="$authority-everkündung" />
                    </for-each>" oder "<for-each select=".[1]">
                      <value-of select="$authority-rechtsinformationssystem" />
                    </for-each>".</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00550-020"
                role="error"
                test="starts-with(@value, $authority-egesetzgebung) or starts-with(@value, $authority-everkündung) or starts-with(@value, $authority-rechtsinformationssystem)"
                ruleId="SCH-00550"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1343', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="/akn:akomaNtoso/akn:act/@name"
    mode="group.d7e180"
    priority="50"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1343' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00560"
          role="error"
          context="/akn:akomaNtoso/akn:act/@name"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00560"
          role="error"
          context="/akn:akomaNtoso/akn:act/@name"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1437'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1437') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if (. = 'regelungstext') then $ist-verkündungsfassung or $ist-konsolidierte-fassung else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00560-005"
                    test="if (. = 'regelungstext') then $ist-verkündungsfassung or $ist-konsolidierte-fassung else true()"
                    ruleId="SCH-00560"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    > Ein Regelungstext in der Verkündungsfassung darf nicht als Entwurfsfassung gekennzeichnet sein, wie es jedoch aktuell anhand von akn:FRBRWork/akn:FRBRdate/@name deklariert ist.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00560-005"
                test="if (. = 'regelungstext') then $ist-verkündungsfassung or $ist-konsolidierte-fassung else true()"
                ruleId="SCH-00560"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1343', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="akn:meta/akn:proprietary/redok:legalDocML.de_metadaten"
    mode="group.d7e180"
    priority="49"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1442' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00590"
          context="akn:meta/akn:proprietary/redok:legalDocML.de_metadaten"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00590"
          context="akn:meta/akn:proprietary/redok:legalDocML.de_metadaten"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1446'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1446') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if ($ist-entwurfsfassung) then (redok:fna = 'nicht-vorhanden' (: das Literal ist im Metadatenmodell als @default des einfachen Typs xs:token umgesetzt, daher hier als Literal anstatt einer dynamischen Referenzierung :)) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00590-000"
                    test="if ($ist-entwurfsfassung) then (redok:fna = 'nicht-vorhanden' (: das Literal ist im Metadatenmodell als @default des einfachen Typs xs:token umgesetzt, daher hier als Literal anstatt einer dynamischen Referenzierung :)) else true()"
                    ruleId="SCH-00590"
                  >
                    <where-populated>
                      <attribute name="location" select="path(redok:fna)" />
                    </where-populated>
                    <svrl:text
                    >In der Entwurfsfassung muss als Wert für den Fundstellennachweis das Literal "nicht-vorhanden" angegeben werden.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00590-000"
                test="if ($ist-entwurfsfassung) then (redok:fna = 'nicht-vorhanden' (: das Literal ist im Metadatenmodell als @default des einfachen Typs xs:token umgesetzt, daher hier als Literal anstatt einer dynamischen Referenzierung :)) else true()"
                ruleId="SCH-00590"
              >
                <where-populated>
                  <attribute name="location" select="path(redok:fna)" />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1442', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="akn:meta[$teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/rechtsetzungsdokument']"
    mode="group.d7e180"
    priority="48"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1442' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00591"
          context="akn:meta[$teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/rechtsetzungsdokument']"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00591"
          context="akn:meta[$teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/rechtsetzungsdokument']"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1452'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e1455'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e1458'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e1461'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1452') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(count(akn:proprietary/redok:legalDocML.de_metadaten) eq 1)"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00591-000"
                    role="error"
                    test="count(akn:proprietary/redok:legalDocML.de_metadaten) eq 1"
                    ruleId="SCH-00591"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Ein Rechtsetzungsdokument muss genau einen Block mit "Metadaten Rechtsetzungsdokument" besitzen.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00591-000"
                role="error"
                test="count(akn:proprietary/redok:legalDocML.de_metadaten) eq 1"
                ruleId="SCH-00591"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1455') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if ($ist-entwurfsfassung and $bearbeitende-institution-frbrauthor = 'recht.bund.de/institution/bundestag') then (count(akn:proprietary/btag:legalDocML.de_metadaten) eq 1) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00591-005"
                    role="error"
                    test="if ($ist-entwurfsfassung and $bearbeitende-institution-frbrauthor = 'recht.bund.de/institution/bundestag') then (count(akn:proprietary/btag:legalDocML.de_metadaten) eq 1) else true()"
                    ruleId="SCH-00591"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Wenn der Bundestag bearbeitende Institution ist, müssen dessen Metadaten genau einmal angegeben werden.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00591-005"
                role="error"
                test="if ($ist-entwurfsfassung and $bearbeitende-institution-frbrauthor = 'recht.bund.de/institution/bundestag') then (count(akn:proprietary/btag:legalDocML.de_metadaten) eq 1) else true()"
                ruleId="SCH-00591"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1458') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if ($ist-entwurfsfassung and $bearbeitende-institution-frbrauthor = 'recht.bund.de/institution/bundesrat') then (count(akn:proprietary/brat:legalDocML.de_metadaten) eq 1) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00591-010"
                    role="error"
                    test="if ($ist-entwurfsfassung and $bearbeitende-institution-frbrauthor = 'recht.bund.de/institution/bundesrat') then (count(akn:proprietary/brat:legalDocML.de_metadaten) eq 1) else true()"
                    ruleId="SCH-00591"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Wenn der Bundesrat bearbeitende Institution ist, müssen dessen Metadaten genau einmal angegeben werden.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00591-010"
                role="error"
                test="if ($ist-entwurfsfassung and $bearbeitende-institution-frbrauthor = 'recht.bund.de/institution/bundesrat') then (count(akn:proprietary/brat:legalDocML.de_metadaten) eq 1) else true()"
                ruleId="SCH-00591"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1461') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if ($ist-entwurfsfassung and $bearbeitende-institution-frbrauthor = 'recht.bund.de/institution/bundesregierung') then (count(akn:proprietary/breg:legalDocML.de_metadaten) eq 1) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00591-015"
                    role="error"
                    test="if ($ist-entwurfsfassung and $bearbeitende-institution-frbrauthor = 'recht.bund.de/institution/bundesregierung') then (count(akn:proprietary/breg:legalDocML.de_metadaten) eq 1) else true()"
                    ruleId="SCH-00591"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Wenn die Bundesregierung bearbeitende Institution ist, müssen deren Metadaten genau einmal angegeben werden.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00591-015"
                role="error"
                test="if ($ist-entwurfsfassung and $bearbeitende-institution-frbrauthor = 'recht.bund.de/institution/bundesregierung') then (count(akn:proprietary/breg:legalDocML.de_metadaten) eq 1) else true()"
                ruleId="SCH-00591"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1442', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="akn:meta[$teildokument-uri = ('/akn/ontology/de/concept/documenttype/bund/regelungstext-entwurf', '/akn/ontology/de/concept/documenttype/bund/regelungstext')]"
    mode="group.d7e180"
    priority="47"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1442' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00592"
          context="akn:meta[$teildokument-uri = ('/akn/ontology/de/concept/documenttype/bund/regelungstext-entwurf', '/akn/ontology/de/concept/documenttype/bund/regelungstext')]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00592"
          context="akn:meta[$teildokument-uri = ('/akn/ontology/de/concept/documenttype/bund/regelungstext-entwurf', '/akn/ontology/de/concept/documenttype/bund/regelungstext')]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1467'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1467') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(count(akn:proprietary/regtxt:legalDocML.de_metadaten) eq 1)"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00592-000"
                    test="count(akn:proprietary/regtxt:legalDocML.de_metadaten) eq 1"
                    ruleId="SCH-00592"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Ein Regelungstext muss über genau einen Metadatenblock Regelungstext verfügen.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00592-000"
                test="count(akn:proprietary/regtxt:legalDocML.de_metadaten) eq 1"
                ruleId="SCH-00592"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1442', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="akn:meta[$teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/nkr-stellungnahme']"
    mode="group.d7e180"
    priority="46"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1442' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00593"
          context="akn:meta[$teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/nkr-stellungnahme']"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00593"
          context="akn:meta[$teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/nkr-stellungnahme']"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1473'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1473') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(count(akn:proprietary/nkr:legalDocML.de_metadaten) eq 1)"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00593-000"
                    test="count(akn:proprietary/nkr:legalDocML.de_metadaten) eq 1"
                    ruleId="SCH-00593"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Die NKR-Stellungnahme muss genau einen Metadatenblock Normenkontrollrat enthalten.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00593-000"
                test="count(akn:proprietary/nkr:legalDocML.de_metadaten) eq 1"
                ruleId="SCH-00593"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1442', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="akn:meta[$teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/sonstiger-veroeffentlichungstext']"
    mode="group.d7e180"
    priority="45"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1442' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00595"
          context="akn:meta[$teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/sonstiger-veroeffentlichungstext']"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00595"
          context="akn:meta[$teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/sonstiger-veroeffentlichungstext']"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1479'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1479') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(count(akn:proprietary/sonst:legalDocML.de_metadaten) eq 1)"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00595-000"
                    test="count(akn:proprietary/sonst:legalDocML.de_metadaten) eq 1"
                    ruleId="SCH-00595"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Der Sonstige Veröffentlichungstext muss genau einen Metadatenblock Sonstiger Veröffentlichungstext enthalten.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00595-000"
                test="count(akn:proprietary/sonst:legalDocML.de_metadaten) eq 1"
                ruleId="SCH-00595"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1442', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template match="akn:meta" mode="group.d7e180" priority="44">
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1484' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule id="SCH-00594" context="akn:meta">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule id="SCH-00594" context="akn:meta">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1488'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e1491'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e1494'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e1497'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e1500'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e1504'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e1507'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e1510'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1488') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if (akn:proprietary/btag:legalDocML.de_metadaten) then ($teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/rechtsetzungsdokument') else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00594-000"
                    role="error"
                    test="if (akn:proprietary/btag:legalDocML.de_metadaten) then ($teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/rechtsetzungsdokument') else true()"
                    ruleId="SCH-00594"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Das Metadatenschema Bundestag darf nur im Rechtsetzungsdokument eingebunden werden.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00594-000"
                role="error"
                test="if (akn:proprietary/btag:legalDocML.de_metadaten) then ($teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/rechtsetzungsdokument') else true()"
                ruleId="SCH-00594"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1491') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if (akn:proprietary/brat:legalDocML.de_metadaten) then ($teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/rechtsetzungsdokument') else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00594-005"
                    role="error"
                    test="if (akn:proprietary/brat:legalDocML.de_metadaten) then ($teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/rechtsetzungsdokument') else true()"
                    ruleId="SCH-00594"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Das Metadatenschema Bundesrat darf nur im Rechtsetzungsdokument eingebunden werden.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00594-005"
                role="error"
                test="if (akn:proprietary/brat:legalDocML.de_metadaten) then ($teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/rechtsetzungsdokument') else true()"
                ruleId="SCH-00594"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1494') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if (akn:proprietary/breg:legalDocML.de_metadaten) then ($teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/rechtsetzungsdokument') else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00594-010"
                    role="error"
                    test="if (akn:proprietary/breg:legalDocML.de_metadaten) then ($teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/rechtsetzungsdokument') else true()"
                    ruleId="SCH-00594"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Das Metadatenschema Bundesregierung darf nur im Rechtsetzungsdokument eingebunden werden.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00594-010"
                role="error"
                test="if (akn:proprietary/breg:legalDocML.de_metadaten) then ($teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/rechtsetzungsdokument') else true()"
                ruleId="SCH-00594"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1497') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if (akn:proprietary/nkr:legalDocML.de_metadaten) then ($teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/nkr-stellungnahme') else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00594-015"
                    test="if (akn:proprietary/nkr:legalDocML.de_metadaten) then ($teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/nkr-stellungnahme') else true()"
                    ruleId="SCH-00594"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Das Metadatenschema Normenkontrollrat darf nur in der NKR-Stellungnahme eingebunden werden.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00594-015"
                test="if (akn:proprietary/nkr:legalDocML.de_metadaten) then ($teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/nkr-stellungnahme') else true()"
                ruleId="SCH-00594"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1500') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if (akn:proprietary/redok:legalDocML.de_metadaten) then ($teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/rechtsetzungsdokument') else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00594-020"
                    test="if (akn:proprietary/redok:legalDocML.de_metadaten) then ($teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/rechtsetzungsdokument') else true()"
                    ruleId="SCH-00594"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Das Metadatenschema Rechtsetzungsdokument darf nur im Rechtsetzungsdokument eingebunden werden.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00594-020"
                test="if (akn:proprietary/redok:legalDocML.de_metadaten) then ($teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/rechtsetzungsdokument') else true()"
                ruleId="SCH-00594"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1504') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if (akn:proprietary/fhilf:legalDocML.de_metadaten) then ($teildokument-uri = ('/akn/ontology/de/concept/documenttype/bund/begruendung-regelungstext', '/akn/ontology/de/concept/documenttype/bund/regelungstext-entwurf', '/akn/ontology/de/concept/documenttype/bund/vorblatt-regelungstext')) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00594-025"
                    test="if (akn:proprietary/fhilf:legalDocML.de_metadaten) then ($teildokument-uri = ('/akn/ontology/de/concept/documenttype/bund/begruendung-regelungstext', '/akn/ontology/de/concept/documenttype/bund/regelungstext-entwurf', '/akn/ontology/de/concept/documenttype/bund/vorblatt-regelungstext')) else true()"
                    ruleId="SCH-00594"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Das Metadatenschema Formulierungshilfe darf nur in einer Begründung zu einem Regelungstext, in einem Regelungstext (Entwurf) oder dem Vorblatt zu einem Regelungstext eingebunden werden.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00594-025"
                test="if (akn:proprietary/fhilf:legalDocML.de_metadaten) then ($teildokument-uri = ('/akn/ontology/de/concept/documenttype/bund/begruendung-regelungstext', '/akn/ontology/de/concept/documenttype/bund/regelungstext-entwurf', '/akn/ontology/de/concept/documenttype/bund/vorblatt-regelungstext')) else true()"
                ruleId="SCH-00594"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1507') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if (akn:proprietary/regtxt:legalDocML.de_metadaten) then ($teildokument-uri = ('/akn/ontology/de/concept/documenttype/bund/regelungstext-entwurf', '/akn/ontology/de/concept/documenttype/bund/regelungstext')) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00594-030"
                    test="if (akn:proprietary/regtxt:legalDocML.de_metadaten) then ($teildokument-uri = ('/akn/ontology/de/concept/documenttype/bund/regelungstext-entwurf', '/akn/ontology/de/concept/documenttype/bund/regelungstext')) else true()"
                    ruleId="SCH-00594"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Das Metadatenschema Regelungstext darf nur in einem Regelungstext (Entwurf) oder einem Regelungstext eingebunden werden.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00594-030"
                test="if (akn:proprietary/regtxt:legalDocML.de_metadaten) then ($teildokument-uri = ('/akn/ontology/de/concept/documenttype/bund/regelungstext-entwurf', '/akn/ontology/de/concept/documenttype/bund/regelungstext')) else true()"
                ruleId="SCH-00594"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1510') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if (akn:proprietary/sonst:legalDocML.de_metadaten) then ($teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/sonstiger-veroeffentlichungstext') else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00594-035"
                    test="if (akn:proprietary/sonst:legalDocML.de_metadaten) then ($teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/sonstiger-veroeffentlichungstext') else true()"
                    ruleId="SCH-00594"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Das Metadatenschema Sonstiger-Veröffentlichungstext darf nur in einem Sonstigen Veröffentlichungstext eingebunden werden.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00594-035"
                test="if (akn:proprietary/sonst:legalDocML.de_metadaten) then ($teildokument-uri = '/akn/ontology/de/concept/documenttype/bund/sonstiger-veroeffentlichungstext') else true()"
                ruleId="SCH-00594"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1484', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template match="akn:timeInterval" mode="group.d7e180" priority="43">
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1516' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule id="SCH-00600" context="akn:timeInterval">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule id="SCH-00600" context="akn:timeInterval">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1528'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e1532'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e1537'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e1542'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <variable name="beginn-geltungszeitintervall-uri" select="@start" />
          <variable name="ende-geltungszeitintervall-uri" select="@end" />
          <variable
            name="beginn-geltungszeitintervall"
            select="ancestor::akn:meta/akn:lifecycle/akn:eventRef[@eId = substring($beginn-geltungszeitintervall-uri, 2)]/@date"
          />
          <variable
            name="ende-geltungszeitintervall"
            select="ancestor::akn:meta/akn:lifecycle/akn:eventRef[@eId = substring($ende-geltungszeitintervall-uri, 2)]/@date"
          />
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1528') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(not(exists(@end) and exists(@duration)))">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00600-000"
                    test="not(exists(@end) and exists(@duration))"
                    ruleId="SCH-00600"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Die Attribute @end und @duration schließen einander aus: Es darf nur entweder eine Dauer oder ein Endzeitpunkt angegeben werden, aber nicht beides.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00600-000"
                test="not(exists(@end) and exists(@duration))"
                ruleId="SCH-00600"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1532') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(substring($beginn-geltungszeitintervall-uri, 2) = /akn:akomaNtoso/*/akn:meta/akn:lifecycle/akn:eventRef/@eId)"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00600-005"
                    test="substring($beginn-geltungszeitintervall-uri, 2) = /akn:akomaNtoso/*/akn:meta/akn:lifecycle/akn:eventRef/@eId"
                    ruleId="SCH-00600"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Der Verweis auf den Beginn des Geltungsintervalls muss auf eine in der vorliegenden Instanz vorhandene @eId einer Ereignis-Deklaration sein. Für den Verweis "<for-each
                      select=".[1]"
                    >
                      <value-of select="$beginn-geltungszeitintervall-uri" />
                    </for-each>" existiert jedoch kein passendes Verweisziel.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00600-005"
                test="substring($beginn-geltungszeitintervall-uri, 2) = /akn:akomaNtoso/*/akn:meta/akn:lifecycle/akn:eventRef/@eId"
                ruleId="SCH-00600"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1537') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if (not(empty(@end))) then (substring($ende-geltungszeitintervall-uri, 2) = /akn:akomaNtoso/*/akn:meta/akn:lifecycle/akn:eventRef/@eId) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00600-010"
                    test="if (not(empty(@end))) then (substring($ende-geltungszeitintervall-uri, 2) = /akn:akomaNtoso/*/akn:meta/akn:lifecycle/akn:eventRef/@eId) else true()"
                    ruleId="SCH-00600"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Der Verweis auf das Ende des Geltungsintervalls muss auf eine in der vorliegenden Instanz vorhandene @eId einer Ereignis-Deklaration sein. Für den Verweis "<for-each
                      select=".[1]"
                    >
                      <value-of select="$ende-geltungszeitintervall-uri" />
                    </for-each>" existiert jedoch kein passendes Verweisziel.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00600-010"
                test="if (not(empty(@end))) then (substring($ende-geltungszeitintervall-uri, 2) = /akn:akomaNtoso/*/akn:meta/akn:lifecycle/akn:eventRef/@eId) else true()"
                ruleId="SCH-00600"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1542') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if (not(empty($beginn-geltungszeitintervall)) and not(empty($ende-geltungszeitintervall))) then (xs:date($beginn-geltungszeitintervall) le xs:date($ende-geltungszeitintervall)) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00600-015"
                    test="if (not(empty($beginn-geltungszeitintervall)) and not(empty($ende-geltungszeitintervall))) then (xs:date($beginn-geltungszeitintervall) le xs:date($ende-geltungszeitintervall)) else true()"
                    ruleId="SCH-00600"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Das Ende des Geltungszeitintervalls darf zeitlich nicht vor seinem Beginn liegen. Es sind jedoch als Beginn "<for-each
                      select=".[1]"
                    >
                      <value-of select="$beginn-geltungszeitintervall" />
                    </for-each>" und als Ende "<for-each select=".[1]">
                      <value-of select="$ende-geltungszeitintervall" />
                    </for-each>" angegeben.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00600-015"
                test="if (not(empty($beginn-geltungszeitintervall)) and not(empty($ende-geltungszeitintervall))) then (xs:date($beginn-geltungszeitintervall) le xs:date($ende-geltungszeitintervall)) else true()"
                ruleId="SCH-00600"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1516', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="(akn:activeModifications | akn:passiveModifications)/akn:textualMod/akn:force | akn:article[@period] | akn:paragraph[@period] | akn:list[@period]"
    mode="group.d7e180"
    priority="42"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1516' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00610"
          context="(akn:activeModifications | akn:passiveModifications)/akn:textualMod/akn:force | akn:article[@period] | akn:paragraph[@period] | akn:list[@period]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00610"
          context="(akn:activeModifications | akn:passiveModifications)/akn:textualMod/akn:force | akn:article[@period] | akn:paragraph[@period] | akn:list[@period]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1556'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <variable
            name="verweis-auf-geltungszeitgruppe"
            select="substring(@period, 2)"
          />
          <variable
            name="geltungszeitgruppen-im-dokument"
            select="/akn:akomaNtoso/*/akn:meta/akn:temporalData/akn:temporalGroup/@eId"
          />
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1556') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not($verweis-auf-geltungszeitgruppe = $geltungszeitgruppen-im-dokument)"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00610-000"
                    test="$verweis-auf-geltungszeitgruppe = $geltungszeitgruppen-im-dokument"
                    ruleId="SCH-00610"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text>Der angegebene Wert "<for-each select=".[1]">
                      <value-of
                        select="concat('#', $verweis-auf-geltungszeitgruppe)"
                      />
                    </for-each>" ist kein gültiger Verweis auf eine Geltungszeitgruppe in diesem Dokument. <for-each
                      select=".[1]"
                    >
                      <value-of
                        select="if (not(empty($geltungszeitgruppen-im-dokument))) then if (count($geltungszeitgruppen-im-dokument) gt 2) then concat(' Technisch mögliche Angaben wären im aktuellen Dokument: ', (string-join( for $geltungszeitgruppe in $geltungszeitgruppen-im-dokument[position() lt last()] return concat('&#34;#', $geltungszeitgruppe, '&#34;'), ', ')), ' oder &#34;#', $geltungszeitgruppen-im-dokument[last()], '&#34;.') else concat(' Die technisch einzig mögliche Angabe ist im aktuellen Dokument &#34;#', $geltungszeitgruppen-im-dokument, '&#34;.') else ()"
                      />
                    </for-each></svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00610-000"
                test="$verweis-auf-geltungszeitgruppe = $geltungszeitgruppen-im-dokument"
                ruleId="SCH-00610"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1516', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template match="akn:eventRef" mode="group.d7e180" priority="41">
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1516' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule id="SCH-00620" context="akn:eventRef">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule id="SCH-00620" context="akn:eventRef">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1571'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e1578'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <variable name="ereignisart" select="@refersTo" />
          <variable name="ereignisdatum" select="@date" />
          <variable
            name="literalsuffix-für-unbekanntes-datum"
            select="'-mit-noch-unbekanntem-datum'"
          />
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1571') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if ($ereignisdatum = $platzhalter-datum-unbekannt) then (ends-with($ereignisart, $literalsuffix-für-unbekanntes-datum)) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00620-000"
                    test="if ($ereignisdatum = $platzhalter-datum-unbekannt) then (ends-with($ereignisart, $literalsuffix-für-unbekanntes-datum)) else true()"
                    ruleId="SCH-00620"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Bei Angabe eines unbekannten Ereignisdatums (durch Verwendung des Platzhaltertextes "<for-each
                      select=".[1]"
                    >
                      <value-of select="$platzhalter-datum-unbekannt" />
                    </for-each>") muss das zur näheren Bestimmung der Ereignisart im Attribut @refersTo angegebene Literal auf den Ausdruck "<for-each
                      select=".[1]"
                    >
                      <value-of select="$literalsuffix-für-unbekanntes-datum" />
                    </for-each>" enden.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00620-000"
                test="if ($ereignisdatum = $platzhalter-datum-unbekannt) then (ends-with($ereignisart, $literalsuffix-für-unbekanntes-datum)) else true()"
                ruleId="SCH-00620"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1578') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if (ends-with($ereignisart, $literalsuffix-für-unbekanntes-datum)) then ($ereignisdatum = $platzhalter-datum-unbekannt) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00620-005"
                    test="if (ends-with($ereignisart, $literalsuffix-für-unbekanntes-datum)) then ($ereignisdatum = $platzhalter-datum-unbekannt) else true()"
                    ruleId="SCH-00620"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Für ein Ereignis, dessen Datum noch unbekannt ist (hier: @refersTo = "<for-each
                      select=".[1]"
                    >
                      <value-of select="$ereignisart" />
                    </for-each>"), muss in seinem Attribut @date als Platzhalterwert "<for-each
                      select=".[1]"
                    >
                      <value-of select="$platzhalter-datum-unbekannt" />
                    </for-each>" angegeben werden.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00620-005"
                test="if (ends-with($ereignisart, $literalsuffix-für-unbekanntes-datum)) then ($ereignisdatum = $platzhalter-datum-unbekannt) else true()"
                ruleId="SCH-00620"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1516', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template match="akn:FRBRExpression" mode="group.d7e180" priority="40">
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1587' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule id="SCH-00630" context="akn:FRBRExpression">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule id="SCH-00630" context="akn:FRBRExpression">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1597'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e1600'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e1604'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <variable
            name="guid-vorherige-version"
            select="'vorherige-version-id'"
          />
          <variable
            name="guid-aktuelle-version"
            select="'aktuelle-version-id'"
          />
          <variable
            name="guid-nächste-version"
            select="'nachfolgende-version-id'"
          />
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1597') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(count(akn:FRBRalias[@name = $guid-vorherige-version]) le 1)"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00630-000"
                    test="count(akn:FRBRalias[@name = $guid-vorherige-version]) le 1"
                    ruleId="SCH-00630"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path(akn:FRBRalias[@name = $guid-vorherige-version])"
                      />
                    </where-populated>
                    <svrl:text
                    >Es darf höchstens einen einzigen Verweis auf eine Vorgängerversion geben.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00630-000"
                test="count(akn:FRBRalias[@name = $guid-vorherige-version]) le 1"
                ruleId="SCH-00630"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path(akn:FRBRalias[@name = $guid-vorherige-version])"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1600') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(count(akn:FRBRalias[@name = $guid-aktuelle-version]) eq 1)"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00630-005"
                    test="count(akn:FRBRalias[@name = $guid-aktuelle-version]) eq 1"
                    ruleId="SCH-00630"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path(akn:FRBRalias[@name = $guid-aktuelle-version])"
                      />
                    </where-populated>
                    <svrl:text
                    >Es muss genau einen Identifikator (GUID) für die vorliegende Version geben.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00630-005"
                test="count(akn:FRBRalias[@name = $guid-aktuelle-version]) eq 1"
                ruleId="SCH-00630"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path(akn:FRBRalias[@name = $guid-aktuelle-version])"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1604') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(count(akn:FRBRalias) eq count(distinct-values(akn:FRBRalias/@value)))"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00630-015"
                    test="count(akn:FRBRalias) eq count(distinct-values(akn:FRBRalias/@value))"
                    ruleId="SCH-00630"
                  >
                    <where-populated>
                      <attribute name="location" select="path(akn:FRBRalias)" />
                    </where-populated>
                    <svrl:text
                    >Sämtliche mittels @value angegebenen GUIDs der Versionen müssen sich voneinander unterscheiden."/&gt;</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00630-015"
                test="count(akn:FRBRalias) eq count(distinct-values(akn:FRBRalias/@value))"
                ruleId="SCH-00630"
              >
                <where-populated>
                  <attribute name="location" select="path(akn:FRBRalias)" />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1587', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template match="akn:meta/akn:lifecycle" mode="group.d7e180" priority="39">
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1609' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule id="SCH-00640" context="akn:meta/akn:lifecycle">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule id="SCH-00640" context="akn:meta/akn:lifecycle">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1615'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e1621'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1615') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(count(akn:eventRef[@type = $type-literal-ereignisreferenz-repeal and @refersTo = $zulässige-literale-in-kombination-mit-repeal]) le 1)"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00640-010"
                    test="count(akn:eventRef[@type = $type-literal-ereignisreferenz-repeal and @refersTo = $zulässige-literale-in-kombination-mit-repeal]) le 1"
                    ruleId="SCH-00640"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text><for-each select=".[1]">
                      <value-of
                        select="$dokumentarten-mit-lebenszyklus-angaben-formulierung-satzanfang-nominativ"
                      />
                    </for-each> kann nicht mehr als ein Außerkraftsetzen (&lt;eventRef;&gt; mit @type='<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$type-literal-ereignisreferenz-repeal"
                      />
                    </for-each>') enthalten, da das Rechtsetzungsartefakt dadurch in Gänze aufgehoben wird.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00640-010"
                test="count(akn:eventRef[@type = $type-literal-ereignisreferenz-repeal and @refersTo = $zulässige-literale-in-kombination-mit-repeal]) le 1"
                ruleId="SCH-00640"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1621') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(count(akn:eventRef[@type = $type-literal-ereignisreferenz-repeal and @refersTo = $zulässige-literale-in-kombination-mit-repeal]) le 1)"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00640-015"
                    test="count(akn:eventRef[@type = $type-literal-ereignisreferenz-repeal and @refersTo = $zulässige-literale-in-kombination-mit-repeal]) le 1"
                    ruleId="SCH-00640"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Für die Deklaration eines Außerkrafttretens (&lt;eventRef&gt; mit @type='<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$type-literal-ereignisreferenz-repeal"
                      />
                    </for-each>') ist als @refersTo-Angabe ausschließlich <for-each
                      select=".[1]"
                    >
                      <value-of
                        select="string-join(distinct-values(for $literal in $zulässige-literale-in-kombination-mit-repeal return concat('''', $literal, '''')), ' oder ')"
                      />
                    </for-each> zulässig.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00640-015"
                test="count(akn:eventRef[@type = $type-literal-ereignisreferenz-repeal and @refersTo = $zulässige-literale-in-kombination-mit-repeal]) le 1"
                ruleId="SCH-00640"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1609', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="akn:meta/akn:lifecycle[$ist-entwurfsfassung]"
    mode="group.d7e180"
    priority="38"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1630' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00650"
          context="akn:meta/akn:lifecycle[$ist-entwurfsfassung]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00650"
          context="akn:meta/akn:lifecycle[$ist-entwurfsfassung]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1634'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e1653'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e1661'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e1669'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1634') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if ($teildokument-uri = ($art-regelungstext-uri, $art-vereinbarung-uri, $art-bekanntmachungstext-uri)) then (akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-entwurfsfassung-ausfertigung-mit-unbekanntem-datum] and akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-entwurfsfassung-verkuendung-mit-unbekanntem-datum] and (akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-entwurfsfassung-inkrafttreten-mit-unbekanntem-datum] or akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-entwurfsfassung-inkrafttreten])) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00650-000"
                    test="if ($teildokument-uri = ($art-regelungstext-uri, $art-vereinbarung-uri, $art-bekanntmachungstext-uri)) then (akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-entwurfsfassung-ausfertigung-mit-unbekanntem-datum] and akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-entwurfsfassung-verkuendung-mit-unbekanntem-datum] and (akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-entwurfsfassung-inkrafttreten-mit-unbekanntem-datum] or akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-entwurfsfassung-inkrafttreten])) else true()"
                    ruleId="SCH-00650"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text><for-each select=".[1]">
                      <value-of
                        select="$dokumentarten-mit-lebenszyklus-angaben-formulierung-satzanfang-nominativ"
                      />
                    </for-each> in der Entwurfsfassung muss immer mindestens drei Ereignisse auszeichnen: Erstens einen Platzhalter für das Ausfertigungsdatum; dieser wird angegeben mittels &lt;eventRef&gt; mit @type='<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$type-literal-ereignisreferenz-generation"
                      />
                    </for-each>' und @refersTo='<for-each select=".[1]">
                      <value-of
                        select="$refersto-literal-ereignisreferenz-entwurfsfassung-ausfertigung-mit-unbekanntem-datum"
                      />
                    </for-each>'. Zweitens einen Platzhalter für das Verkündungsdatum; dieser wird angegeben mittels &lt;eventRef&gt; mit @type='<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$type-literal-ereignisreferenz-generation"
                      />
                    </for-each>' und @refersTo='<for-each select=".[1]">
                      <value-of
                        select="$refersto-literal-ereignisreferenz-entwurfsfassung-verkuendung-mit-unbekanntem-datum"
                      />
                    </for-each>'. Und drittens eine Angabe zum Inkrafttreten mittels &lt;eventRef&gt; mit @type='<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$type-literal-ereignisreferenz-generation"
                      />
                    </for-each> und @refersTo='<for-each select=".[1]">
                      <value-of
                        select="$refersto-literal-ereignisreferenz-entwurfsfassung-inkrafttreten-mit-unbekanntem-datum"
                      />
                    </for-each>' bzw., sofern das Datum bereits bekannt ist, @refersTo='<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$refersto-literal-ereignisreferenz-entwurfsfassung-inkrafttreten"
                      />
                    </for-each>'.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00650-000"
                test="if ($teildokument-uri = ($art-regelungstext-uri, $art-vereinbarung-uri, $art-bekanntmachungstext-uri)) then (akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-entwurfsfassung-ausfertigung-mit-unbekanntem-datum] and akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-entwurfsfassung-verkuendung-mit-unbekanntem-datum] and (akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-entwurfsfassung-inkrafttreten-mit-unbekanntem-datum] or akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-entwurfsfassung-inkrafttreten])) else true()"
                ruleId="SCH-00650"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1653') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if ($ist-entwurfsfassung) then (akn:eventRef[@type = ($type-literal-ereignisreferenz-generation, $type-literal-ereignisreferenz-repeal)]) else ())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00650-005"
                    test="if ($ist-entwurfsfassung) then (akn:eventRef[@type = ($type-literal-ereignisreferenz-generation, $type-literal-ereignisreferenz-repeal)]) else ()"
                    ruleId="SCH-00650"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text><for-each select=".[1]">
                      <value-of
                        select="$dokumentarten-mit-lebenszyklus-angaben-formulierung-satzanfang-nominativ"
                      />
                    </for-each> in der Entwurfsfassung kann nur initiale Ereignisse (Inkrafttreten, Ausfertigung, teilweises Außerkrafttreten, d.h. @type = '<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$type-literal-ereignisreferenz-generation"
                      />
                    </for-each>') oder ein finales Außerkrafttreten (d.h. @type = '<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$type-literal-ereignisreferenz-repeal"
                      />
                    </for-each>') aufweisen.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00650-005"
                test="if ($ist-entwurfsfassung) then (akn:eventRef[@type = ($type-literal-ereignisreferenz-generation, $type-literal-ereignisreferenz-repeal)]) else ()"
                ruleId="SCH-00650"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1661') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if ($teildokument-uri = ($art-regelungstext-uri, $art-vereinbarung-uri, $art-bekanntmachungstext-uri)) then (count(akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-entwurfsfassung-ausfertigung-mit-unbekanntem-datum]) = 1) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00650-010"
                    test="if ($teildokument-uri = ($art-regelungstext-uri, $art-vereinbarung-uri, $art-bekanntmachungstext-uri)) then (count(akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-entwurfsfassung-ausfertigung-mit-unbekanntem-datum]) = 1) else true()"
                    ruleId="SCH-00650"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text><for-each select=".[1]">
                      <value-of
                        select="$dokumentarten-mit-lebenszyklus-angaben-formulierung-satzanfang-nominativ"
                      />
                    </for-each> in der Entwurfsfassung muss genau einen Platzhalter für das noch unbekannte Datum der Ausfertigung enthalten (&lt;eventRef&gt; mit @type = '<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$type-literal-ereignisreferenz-generation"
                      />
                    </for-each>' und @refersTo = '<for-each select=".[1]">
                      <value-of
                        select="$refersto-literal-ereignisreferenz-entwurfsfassung-ausfertigung-mit-unbekanntem-datum"
                      />
                    </for-each>').</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00650-010"
                test="if ($teildokument-uri = ($art-regelungstext-uri, $art-vereinbarung-uri, $art-bekanntmachungstext-uri)) then (count(akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-entwurfsfassung-ausfertigung-mit-unbekanntem-datum]) = 1) else true()"
                ruleId="SCH-00650"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1669') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if ($teildokument-uri = ($art-regelungstext-uri)) then (count(akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-entwurfsfassung-verkuendung-mit-unbekanntem-datum]) = 1) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00650-015"
                    test="if ($teildokument-uri = ($art-regelungstext-uri)) then (count(akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-entwurfsfassung-verkuendung-mit-unbekanntem-datum]) = 1) else true()"
                    ruleId="SCH-00650"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text><for-each select=".[1]">
                      <value-of
                        select="$dokumentarten-mit-lebenszyklus-angaben-formulierung-satzanfang-nominativ"
                      />
                    </for-each> in der Entwurfsfassung muss genau einen Platzhalter für das noch unbekannte Datum der Verkündung enthalten (&lt;eventRef&gt; mit @type = '<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$type-literal-ereignisreferenz-generation"
                      />
                    </for-each>' und @refersTo = '<for-each select=".[1]">
                      <value-of
                        select="$refersto-literal-ereignisreferenz-entwurfsfassung-verkuendung-mit-unbekanntem-datum"
                      />
                    </for-each>').</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00650-015"
                test="if ($teildokument-uri = ($art-regelungstext-uri)) then (count(akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-entwurfsfassung-verkuendung-mit-unbekanntem-datum]) = 1) else true()"
                ruleId="SCH-00650"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1630', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="akn:meta/akn:lifecycle[$ist-verkündungsfassung or $ist-konsolidierte-fassung]"
    mode="group.d7e180"
    priority="37"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1679' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00660"
          context="akn:meta/akn:lifecycle[$ist-verkündungsfassung or $ist-konsolidierte-fassung]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00660"
          context="akn:meta/akn:lifecycle[$ist-verkündungsfassung or $ist-konsolidierte-fassung]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1683'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e1702'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e1725'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <variable
            name="datum-ausfertigung"
            select="(akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-ausfertigung]) (: immer den ersten Wert nehmen, falls es unerwartet mehrere gibt, damit hier diejenige SCH-Regel zum Tragen kommt, die prüft, dass es nur genau ein initiales Ausfertigungsdatum gibt, und nicht ein Fehler auf Ebene des SCH-Prozessors die weitere Verarbeitung blockiert. :)[1]/@date"
          />
          <variable
            name="frühestes-datum-inkrafttreten-als-reine-ziffern"
            select="min(for $n in akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten]/@date return format-date($n, '[Y,4][M,2][D,2]'))"
          />
          <variable
            name="datum-inkrafttreten"
            select="if (not(empty($frühestes-datum-inkrafttreten-als-reine-ziffern))) then (xs:date(concat( substring($frühestes-datum-inkrafttreten-als-reine-ziffern, 1, 4), '-', substring($frühestes-datum-inkrafttreten-als-reine-ziffern, 5, 2), '-', substring($frühestes-datum-inkrafttreten-als-reine-ziffern, 7, 2)))) else '0001-01-01'"
          />
          <variable
            name="datum-ausserkafttreten"
            select="akn:eventRef[@type = $type-literal-ereignisreferenz-repeal and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-ausserkrafttreten]/@date"
          />
          <variable
            name="frühestes-datum-amendment-ausfertigung-als-reine-ziffern"
            select="min(for $n in akn:eventRef[@type = $type-literal-ereignisreferenz-amendment and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-ausfertigung]/@date return format-date($n, '[Y,4][M,2][D,2]'))"
          />
          <variable
            name="frühestes-datum-amendment-ausfertigung"
            select="if (not(empty($frühestes-datum-amendment-ausfertigung-als-reine-ziffern))) then (xs:date(concat( substring($frühestes-datum-amendment-ausfertigung-als-reine-ziffern, 1, 4), '-', substring($frühestes-datum-amendment-ausfertigung-als-reine-ziffern, 5, 2), '-', substring($frühestes-datum-amendment-ausfertigung-als-reine-ziffern, 7, 2)))) else '0001-01-01'"
          />
          <variable
            name="ausfertigungsdatum-stammform"
            select="(akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-ausfertigung]/@date)[1]"
          />
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1683') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-ausfertigung] and ( akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten] or akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-grundsaetzlich] or akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-abweichend] or akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-mit-unbekanntem-datum] ))"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00660-000"
                    test="akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-ausfertigung] and ( akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten] or akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-grundsaetzlich] or akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-abweichend] or akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-mit-unbekanntem-datum] )"
                    ruleId="SCH-00660"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text><for-each select=".[1]">
                      <value-of
                        select="$dokumentarten-mit-lebenszyklus-angaben-formulierung-satzanfang-nominativ"
                      />
                    </for-each> in der Verkündungsfassung muss immer mindestens zwei Ereignisse auszeichnen: Erstens das Ausfertigungsdatum; dieses wird angegeben mittels &lt;eventRef&gt; mit @type='<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$type-literal-ereignisreferenz-generation"
                      />
                    </for-each>' und @refersTo='<for-each select=".[1]">
                      <value-of
                        select="$refersto-literal-ereignisreferenz-verkündungsfassung-ausfertigung"
                      />
                    </for-each>'. Und zweitens eine Angabe zum Inkrafttreten mittels &lt;eventRef&gt; mit @type='<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$type-literal-ereignisreferenz-generation"
                      />
                    </for-each> und @refersTo='<for-each select=".[1]">
                      <value-of
                        select="$refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten"
                      />
                    </for-each>' bzw. bei grundsätzlichem oder abweichendem Inkrafttreten @refersTo='<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-grundsaetzlich"
                      />
                    </for-each>' oder @refersTo='<for-each select=".[1]">
                      <value-of
                        select="$refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-abweichend"
                      />
                    </for-each>'. Oder sofern das Datum noch unbekannt ist, @refersTo='<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-mit-unbekanntem-datum"
                      />
                    </for-each>'.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00660-000"
                test="akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-ausfertigung] and ( akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten] or akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-grundsaetzlich] or akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-abweichend] or akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-mit-unbekanntem-datum] )"
                ruleId="SCH-00660"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1702') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(count(akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-ausfertigung]) = 1)"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00660-005"
                    test="count(akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-ausfertigung]) = 1"
                    ruleId="SCH-00660"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text><for-each select=".[1]">
                      <value-of
                        select="$dokumentarten-mit-lebenszyklus-angaben-formulierung-satzanfang-nominativ"
                      />
                    </for-each> in der Verkündungsfassung muss genau ein konkretes Ausfertigungsdatum enthalten (&lt;eventRef&gt; mit @type = '<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$type-literal-ereignisreferenz-generation"
                      />
                    </for-each>' und @refersTo = '<for-each select=".[1]">
                      <value-of
                        select="$refersto-literal-ereignisreferenz-verkündungsfassung-ausfertigung"
                      />
                    </for-each>').</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00660-005"
                test="count(akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-ausfertigung]) = 1"
                ruleId="SCH-00660"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1725') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if (not(xs:date($frühestes-datum-amendment-ausfertigung) = xs:date('0001-01-01'))) then (xs:date($frühestes-datum-amendment-ausfertigung) ge xs:date($ausfertigungsdatum-stammform)) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00660-020"
                    test="if (not(xs:date($frühestes-datum-amendment-ausfertigung) = xs:date('0001-01-01'))) then (xs:date($frühestes-datum-amendment-ausfertigung) ge xs:date($ausfertigungsdatum-stammform)) else true()"
                    ruleId="SCH-00660"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Das initiale Ausfertigungsdatum der Stammform kann nicht hinter dem Ausfertigungsdatum des Änderungsgesetzes liegen.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00660-020"
                test="if (not(xs:date($frühestes-datum-amendment-ausfertigung) = xs:date('0001-01-01'))) then (xs:date($frühestes-datum-amendment-ausfertigung) ge xs:date($ausfertigungsdatum-stammform)) else true()"
                ruleId="SCH-00660"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1679', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="akn:meta/akn:lifecycle/akn:eventRef [($ist-verkündungsfassung or $ist-konsolidierte-fassung) and @type = $type-literal-ereignisreferenz-generation ]"
    mode="group.d7e180"
    priority="36"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1730' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00665"
          context="akn:meta/akn:lifecycle/akn:eventRef [($ist-verkündungsfassung or $ist-konsolidierte-fassung) and @type = $type-literal-ereignisreferenz-generation ]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00665"
          context="akn:meta/akn:lifecycle/akn:eventRef [($ist-verkündungsfassung or $ist-konsolidierte-fassung) and @type = $type-literal-ereignisreferenz-generation ]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1734'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1734') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if (@refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-grundsaetzlich) then not( preceding-sibling::akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-abweichend]/@date eq current()/@date or following-sibling::akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-abweichend]/@date eq current()/@date ) else if (@refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-abweichend) then not( preceding-sibling::akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-grundsaetzlich]/@date eq current()/@date or following-sibling::akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-grundsaetzlich]/@date eq current()/@date ) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00665-025"
                    test="if (@refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-grundsaetzlich) then not( preceding-sibling::akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-abweichend]/@date eq current()/@date or following-sibling::akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-abweichend]/@date eq current()/@date ) else if (@refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-abweichend) then not( preceding-sibling::akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-grundsaetzlich]/@date eq current()/@date or following-sibling::akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-grundsaetzlich]/@date eq current()/@date ) else true()"
                    ruleId="SCH-00665"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Das grundsätzliche und das abweichende Inkrafttretensdatum dürfen nicht identisch sein.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00665-025"
                test="if (@refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-grundsaetzlich) then not( preceding-sibling::akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-abweichend]/@date eq current()/@date or following-sibling::akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-abweichend]/@date eq current()/@date ) else if (@refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-abweichend) then not( preceding-sibling::akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-grundsaetzlich]/@date eq current()/@date or following-sibling::akn:eventRef[@type = $type-literal-ereignisreferenz-generation and @refersTo = $refersto-literal-ereignisreferenz-verkündungsfassung-inkrafttreten-grundsaetzlich]/@date eq current()/@date ) else true()"
                ruleId="SCH-00665"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1730', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="akn:meta/akn:lifecycle[$ist-verkündungsfassung and $teildokument-uri = $art-regelungstext-uri]"
    mode="group.d7e180"
    priority="35"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1740' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00667"
          context="akn:meta/akn:lifecycle[$ist-verkündungsfassung and $teildokument-uri = $art-regelungstext-uri]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00667"
          context="akn:meta/akn:lifecycle[$ist-verkündungsfassung and $teildokument-uri = $art-regelungstext-uri]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1744'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1744') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(count(akn:eventRef[@refersTo = $refersto-literal-ereignisreferenz-verkuendung]) = 1)"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00667-000"
                    role="error"
                    test="count(akn:eventRef[@refersTo = $refersto-literal-ereignisreferenz-verkuendung]) = 1"
                    ruleId="SCH-00667"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >In der Verkündungsfassung eines Regelungstextes muss genau ein &lt;eventRef&gt; mit @refersTo="<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="$refersto-literal-ereignisreferenz-verkuendung"
                      />
                    </for-each>" vorhanden sein. </svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00667-000"
                role="error"
                test="count(akn:eventRef[@refersTo = $refersto-literal-ereignisreferenz-verkuendung]) = 1"
                ruleId="SCH-00667"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1740', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="akn:p[parent::akn:longTitle and $teildokument-uri = $art-regelungstext-uri]"
    mode="group.d7e180"
    priority="34"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1753' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00680"
          context="akn:p[parent::akn:longTitle and $teildokument-uri = $art-regelungstext-uri]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00680"
          context="akn:p[parent::akn:longTitle and $teildokument-uri = $art-regelungstext-uri]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1757'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1757') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(count(akn:docTitle) = 1)">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00680-000"
                    test="count(akn:docTitle) = 1"
                    ruleId="SCH-00680"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path(parent::akn:longTitle)"
                      />
                    </where-populated>
                    <svrl:text
                    >Ein dokumentenkopfTitel (akn:longtitle) muss genau einen Dokumententitel (akn:docTitle) besitzen.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00680-000"
                test="count(akn:docTitle) = 1"
                ruleId="SCH-00680"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path(parent::akn:longTitle)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1753', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template match="akn:session" mode="group.d7e180" priority="33">
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1762' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule id="SCH-00730" context="akn:session">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule id="SCH-00730" context="akn:session">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1766'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1766') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(starts-with(@refersTo, '#') and substring(@refersTo, 2) = //akn:organization/@eId)"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00730-000"
                    test="starts-with(@refersTo, '#') and substring(@refersTo, 2) = //akn:organization/@eId"
                    ruleId="SCH-00730"
                  >
                    <where-populated>
                      <attribute name="location" select="path(@refersTo)" />
                    </where-populated>
                    <svrl:text
                    >Es muss einen lokalen Verweis auf eine Organisation geben, deren Sitzung ausgezeichnet wird. Dieser besteht aus einer Raute (#), gefolgt von der @eId der betreffenden akn:organization.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00730-000"
                test="starts-with(@refersTo, '#') and substring(@refersTo, 2) = //akn:organization/@eId"
                ruleId="SCH-00730"
              >
                <where-populated>
                  <attribute name="location" select="path(@refersTo)" />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1762', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template match="akn:person" mode="group.d7e180" priority="32">
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1774' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule id="SCH-00740" context="akn:person">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule id="SCH-00740" context="akn:person">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1780'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e1785'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <variable
            name="referenzierte-eId"
            select="substring-after(@refersTo, '#')"
          />
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1780') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if (@refersTo) then (count(ancestor::akn:akomaNtoso/*/akn:meta/akn:references/akn:TLCPerson[@eId = $referenzierte-eId]) = 1) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00740-000"
                    test="if (@refersTo) then (count(ancestor::akn:akomaNtoso/*/akn:meta/akn:references/akn:TLCPerson[@eId = $referenzierte-eId]) = 1) else true()"
                    ruleId="SCH-00740"
                  >
                    <where-populated>
                      <attribute name="location" select="path(@refersTo)" />
                    </where-populated>
                    <svrl:text
                    >In den Metadaten existiert kein korrespondierender Personenverweis mit der @eId="<for-each
                      select="@refersTo[1]"
                    >
                      <value-of select="$referenzierte-eId" />
                    </for-each>".</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00740-000"
                test="if (@refersTo) then (count(ancestor::akn:akomaNtoso/*/akn:meta/akn:references/akn:TLCPerson[@eId = $referenzierte-eId]) = 1) else true()"
                ruleId="SCH-00740"
              >
                <where-populated>
                  <attribute name="location" select="path(@refersTo)" />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1785') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(@refersTo) and ($ist-entwurfsfassung or $ist-verkündungsfassung)"
              >
                <variable
                  name="successful-report"
                  as="element(svrl:successful-report)"
                >
                  <svrl:successful-report
                    id="SCH-00740-005"
                    role="warn"
                    test="not(@refersTo) and ($ist-entwurfsfassung or $ist-verkündungsfassung)"
                    ruleId="SCH-00740"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Aus Kompatibilitätsgründen ist es zulässig, eine Person ohne Referenz auf eine TLCPerson anzugeben; dies sollte jedoch nur in begründeten Ausnahmefällen geschehen.</svrl:text>
                  </svrl:successful-report>
                </variable>
                <sequence select="$successful-report" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-report
                severityThreshold="info"
                id="SCH-00740-005"
                role="warn"
                test="not(@refersTo) and ($ist-entwurfsfassung or $ist-verkündungsfassung)"
                ruleId="SCH-00740"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-report>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1774', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template match="akn:role" mode="group.d7e180" priority="31">
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1774' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule id="SCH-00750" context="akn:role">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule id="SCH-00750" context="akn:role">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1793'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e1798'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <variable
            name="referenzierte-eId"
            select="substring-after(@refersTo, '#')"
          />
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1793') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if (@refersTo) then (count(ancestor::akn:akomaNtoso/*/akn:meta/akn:references/akn:TLCRole[@eId = $referenzierte-eId]) = 1) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00750-000"
                    test="if (@refersTo) then (count(ancestor::akn:akomaNtoso/*/akn:meta/akn:references/akn:TLCRole[@eId = $referenzierte-eId]) = 1) else true()"
                    ruleId="SCH-00750"
                  >
                    <where-populated>
                      <attribute name="location" select="path(@refersTo)" />
                    </where-populated>
                    <svrl:text
                    >In den Metadaten existiert kein korrespondierender Funktionsbezeichnungsverweis mit der @eId="<for-each
                      select="@refersTo[1]"
                    >
                      <value-of select="$referenzierte-eId" />
                    </for-each>".</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00750-000"
                test="if (@refersTo) then (count(ancestor::akn:akomaNtoso/*/akn:meta/akn:references/akn:TLCRole[@eId = $referenzierte-eId]) = 1) else true()"
                ruleId="SCH-00750"
              >
                <where-populated>
                  <attribute name="location" select="path(@refersTo)" />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1798') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(@refersTo) and ($ist-entwurfsfassung or $ist-verkündungsfassung)"
              >
                <variable
                  name="successful-report"
                  as="element(svrl:successful-report)"
                >
                  <svrl:successful-report
                    id="SCH-00750-005"
                    role="warn"
                    test="not(@refersTo) and ($ist-entwurfsfassung or $ist-verkündungsfassung)"
                    ruleId="SCH-00750"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Aus Kompatibilitätsgründen ist es zulässig, eine Funktionsbezeichnung ohne Referenz auf eine TLCRole anzugeben; dies sollte jedoch nur in begründeten Ausnahmefällen geschehen.</svrl:text>
                  </svrl:successful-report>
                </variable>
                <sequence select="$successful-report" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-report
                severityThreshold="info"
                id="SCH-00750-005"
                role="warn"
                test="not(@refersTo) and ($ist-entwurfsfassung or $ist-verkündungsfassung)"
                ruleId="SCH-00750"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-report>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1774', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template match="akn:organization" mode="group.d7e180" priority="30">
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1774' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule id="SCH-00760" context="akn:organization">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule id="SCH-00760" context="akn:organization">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1806'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e1811'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e1814'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <variable
            name="referenzierte-eId"
            select="substring-after(@refersTo, '#')"
          />
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1806') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if (@refersTo) then (count(ancestor::akn:akomaNtoso/*/akn:meta/akn:references/akn:TLCOrganization[@eId = $referenzierte-eId]) = 1) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00760-000"
                    test="if (@refersTo) then (count(ancestor::akn:akomaNtoso/*/akn:meta/akn:references/akn:TLCOrganization[@eId = $referenzierte-eId]) = 1) else true()"
                    ruleId="SCH-00760"
                  >
                    <where-populated>
                      <attribute name="location" select="path(@refersTo)" />
                    </where-populated>
                    <svrl:text
                    >In den Metadaten existiert kein korrespondierender Organisationsverweis mit der @eId="<for-each
                      select="@refersTo[1]"
                    >
                      <value-of select="$referenzierte-eId" />
                    </for-each>".</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00760-000"
                test="if (@refersTo) then (count(ancestor::akn:akomaNtoso/*/akn:meta/akn:references/akn:TLCOrganization[@eId = $referenzierte-eId]) = 1) else true()"
                ruleId="SCH-00760"
              >
                <where-populated>
                  <attribute name="location" select="path(@refersTo)" />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1811') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(@refersTo) and ($ist-entwurfsfassung or $ist-verkündungsfassung)"
              >
                <variable
                  name="successful-report"
                  as="element(svrl:successful-report)"
                >
                  <svrl:successful-report
                    id="SCH-00760-005"
                    role="warn"
                    test="not(@refersTo) and ($ist-entwurfsfassung or $ist-verkündungsfassung)"
                    ruleId="SCH-00760"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Aus Kompatibilitätsgründen ist es zulässig, eine Organisation ohne Referenz auf eine TLCPerson anzugeben; dies sollte jedoch nur in begründeten Ausnahmefällen geschehen.</svrl:text>
                  </svrl:successful-report>
                </variable>
                <sequence select="$successful-report" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-report
                severityThreshold="info"
                id="SCH-00760-005"
                role="warn"
                test="not(@refersTo) and ($ist-entwurfsfassung or $ist-verkündungsfassung)"
                ruleId="SCH-00760"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-report>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1814') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if (not(@refersTo)) then (@title) else if (not(@title)) then (@refersTo) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00760-010"
                    test="if (not(@refersTo)) then (@title) else if (not(@title)) then (@refersTo) else true()"
                    ruleId="SCH-00760"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Eine akn:organization muss mindestens entweder eine Angabe @refersTo oder einen @title besitzen (oder beides).</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00760-010"
                test="if (not(@refersTo)) then (@title) else if (not(@title)) then (@refersTo) else true()"
                ruleId="SCH-00760"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1774', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template match="akn:references" mode="group.d7e180" priority="29">
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1774' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule id="SCH-00770" context="akn:references">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule id="SCH-00770" context="akn:references">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1822'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <variable
            name="referenzierte-eId"
            select="substring-after(@source, '#')"
          />
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1822') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(count(ancestor::akn:akomaNtoso/*/akn:meta/akn:references/(akn:TLCOrganization, akn:TLCPerson)[@eId = $referenzierte-eId]) = 1)"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00770-000"
                    test="count(ancestor::akn:akomaNtoso/*/akn:meta/akn:references/(akn:TLCOrganization, akn:TLCPerson)[@eId = $referenzierte-eId]) = 1"
                    ruleId="SCH-00770"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >In den Metadaten existiert kein korrespondierender Akteur (Person, Organisation) mit der @eId="<for-each
                      select=".[1]"
                    >
                      <value-of select="$referenzierte-eId" />
                    </for-each>".</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00770-000"
                test="count(ancestor::akn:akomaNtoso/*/akn:meta/akn:references/(akn:TLCOrganization, akn:TLCPerson)[@eId = $referenzierte-eId]) = 1"
                ruleId="SCH-00770"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1774', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="akn:marker[@refersTo = 'satzende']"
    mode="group.d7e180"
    priority="28"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1833' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00802"
          context="akn:marker[@refersTo = 'satzende']"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00802"
          context="akn:marker[@refersTo = 'satzende']"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1837'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1837') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(empty(@name))">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00802-000"
                    test="empty(@name)"
                    ruleId="SCH-00802"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Ein akn:marker mit @refersTo='satzende' darf kein Attribut @name haben.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00802-000"
                test="empty(@name)"
                ruleId="SCH-00802"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1833', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template match="akn:inline/@name" mode="group.d7e180" priority="27">
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1833' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule id="SCH-00803" context="akn:inline/@name">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule id="SCH-00803" context="akn:inline/@name">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1843'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1843') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if (not(parent::*/@refersTo = 'neuris')) then (. = 'attributsemantik-noch-undefiniert') else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00803-000"
                    test="if (not(parent::*/@refersTo = 'neuris')) then (. = 'attributsemantik-noch-undefiniert') else true()"
                    ruleId="SCH-00803"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Das Attribut @name darf für akn:inline nur dann mit Freitext befüllt werden, wenn @refersTo='neuris' gegeben ist.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00803-000"
                test="if (not(parent::*/@refersTo = 'neuris')) then (. = 'attributsemantik-noch-undefiniert') else true()"
                ruleId="SCH-00803"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1833', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="(akn:act | akn:bill)[akn:meta/akn:proprietary/regtxt:legalDocML.de_metadaten/regtxt:form = ($form-stammform, $form-eingebundene-stammform)]//akn:list"
    mode="group.d7e180"
    priority="26"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1851' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00810"
          context="(akn:act | akn:bill)[akn:meta/akn:proprietary/regtxt:legalDocML.de_metadaten/regtxt:form = ($form-stammform, $form-eingebundene-stammform)]//akn:list"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00810"
          context="(akn:act | akn:bill)[akn:meta/akn:proprietary/regtxt:legalDocML.de_metadaten/regtxt:form = ($form-stammform, $form-eingebundene-stammform)]//akn:list"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1855'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1855') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(false())">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00810-000"
                    test="false()"
                    ruleId="SCH-00810"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Das Element akn:list darf in Stammformen nicht verwendet werden.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00810-000"
                test="false()"
                ruleId="SCH-00810"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1851', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="/akn:akomaNtoso/akn:*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRsubtype"
    mode="group.d7e180"
    priority="25"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1863' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00820"
          context="/akn:akomaNtoso/akn:*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRsubtype"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00820"
          context="/akn:akomaNtoso/akn:*/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRsubtype"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1869'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <variable
            name="teildokument-id"
            select="tokenize(/akn:akomaNtoso/akn:*/@name, '/')[last()]"
          />
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1869') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(matches(@value, concat($teildokument-id, '-\d+')))">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00820-000"
                    test="matches(@value, concat($teildokument-id, '-\d+'))"
                    ruleId="SCH-00820"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Die Teildokumentbezeichnung muss der ontologischen Teildokument-ID entsprechen; erwartet wird hier konkret "<for-each
                      select=".[1]"
                    >
                      <value-of
                        select="concat($teildokument-id, '-', tokenize(@value, '-')[last()])"
                      />
                    </for-each>".</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00820-000"
                test="matches(@value, concat($teildokument-id, '-\d+'))"
                ruleId="SCH-00820"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1863', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="akn:statement/akn:conclusions/akn:blockContainer"
    mode="group.d7e180"
    priority="24"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1880' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00900"
          context="akn:statement/akn:conclusions/akn:blockContainer"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00900"
          context="akn:statement/akn:conclusions/akn:blockContainer"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1884'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1884') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(count(akn:p) ge 3)">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00900-000"
                    role="warn"
                    test="count(akn:p) ge 3"
                    ruleId="SCH-00900"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Im Schlussteil sollen mindestens drei akn:p enthalten sein (akn:p mit Ort und Datum; akn:p mit der Organisation / dem Auschuss; ein oder mehrere akn:p mit Unterschriften).</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00900-000"
                role="warn"
                test="count(akn:p) ge 3"
                ruleId="SCH-00900"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1880', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="akn:statement/akn:conclusions/akn:blockContainer/akn:p[1]"
    mode="group.d7e180"
    priority="23"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1880' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00901"
          context="akn:statement/akn:conclusions/akn:blockContainer/akn:p[1]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00901"
          context="akn:statement/akn:conclusions/akn:blockContainer/akn:p[1]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1890'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e1893'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1890') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(exists(akn:location))">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00901-010"
                    role="warn"
                    test="exists(akn:location)"
                    ruleId="SCH-00901"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Im Schlussteil soll im ersten akn:p ein Ort (akn:location) angegeben werden.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00901-010"
                role="warn"
                test="exists(akn:location)"
                ruleId="SCH-00901"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1893') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(exists(akn:date))">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00901-020"
                    role="warn"
                    test="exists(akn:date)"
                    ruleId="SCH-00901"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Im Schlussteil soll im ersten akn:p ein Datum (akn:date) angegeben werden.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00901-020"
                role="warn"
                test="exists(akn:date)"
                ruleId="SCH-00901"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1880', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="akn:statement/akn:conclusions/akn:blockContainer/akn:p[2]"
    mode="group.d7e180"
    priority="22"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1880' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00902"
          context="akn:statement/akn:conclusions/akn:blockContainer/akn:p[2]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00902"
          context="akn:statement/akn:conclusions/akn:blockContainer/akn:p[2]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1899'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1899') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(exists(akn:organization))">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00902-000"
                    role="warn"
                    test="exists(akn:organization)"
                    ruleId="SCH-00902"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Im Schlussteil soll im zweiten akn:p ein Ort (akn:location) angegeben werden.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00902-000"
                role="warn"
                test="exists(akn:organization)"
                ruleId="SCH-00902"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1880', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="akn:statement/akn:conclusions/akn:blockContainer/akn:p[position() ge 3]"
    mode="group.d7e180"
    priority="21"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1880' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00903"
          context="akn:statement/akn:conclusions/akn:blockContainer/akn:p[position() ge 3]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00903"
          context="akn:statement/akn:conclusions/akn:blockContainer/akn:p[position() ge 3]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1905'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1905') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(exists(akn:signature))">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00903-000"
                    role="warn"
                    test="exists(akn:signature)"
                    ruleId="SCH-00903"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Im Schlussteil soll ab dem zweiten akn:p eine Signatur(akn:signature) angegeben werden.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00903-000"
                role="warn"
                test="exists(akn:signature)"
                ruleId="SCH-00903"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1880', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="akn:doc[$teildokument-uri = $art-anlage-regelungstext-uri]"
    mode="group.d7e180"
    priority="20"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1913' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00920"
          context="akn:doc[$teildokument-uri = $art-anlage-regelungstext-uri]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00920"
          context="akn:doc[$teildokument-uri = $art-anlage-regelungstext-uri]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1917'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1917') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(count(akn:preface//akn:docTitle/akn:inline[@refersTo = 'anlageregelungstext-num']) eq 1 and count(akn:preface//akn:docTitle/akn:inline[@refersTo = 'anlageregelungstext-bezug']) le 1 and count(akn:preface//akn:docTitle/akn:inline[@refersTo = 'anlageregelungstext-heading']) le 1 and count(akn:preface//akn:docTitle/*) le 3)"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00920-000"
                    role="error"
                    test="count(akn:preface//akn:docTitle/akn:inline[@refersTo = 'anlageregelungstext-num']) eq 1 and count(akn:preface//akn:docTitle/akn:inline[@refersTo = 'anlageregelungstext-bezug']) le 1 and count(akn:preface//akn:docTitle/akn:inline[@refersTo = 'anlageregelungstext-heading']) le 1 and count(akn:preface//akn:docTitle/*) le 3"
                    ruleId="SCH-00920"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Eine Anlage zu einem Regelungstext muss in ihrem Dokumententitel Angaben zur Zählbezeichnung und kann Angaben zum Bezug und zur Überschrift besitzen.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00920-000"
                role="error"
                test="count(akn:preface//akn:docTitle/akn:inline[@refersTo = 'anlageregelungstext-num']) eq 1 and count(akn:preface//akn:docTitle/akn:inline[@refersTo = 'anlageregelungstext-bezug']) le 1 and count(akn:preface//akn:docTitle/akn:inline[@refersTo = 'anlageregelungstext-heading']) le 1 and count(akn:preface//akn:docTitle/*) le 3"
                ruleId="SCH-00920"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1913', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template match="akn:inline/@refersTo" mode="group.d7e180" priority="19">
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1913' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule id="SCH-00921" context="akn:inline/@refersTo">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule id="SCH-00921" context="akn:inline/@refersTo">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1923'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1923') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if (. = ('anlageregelungstext-num', 'anlageregelungstext-bezug', 'anlageregelungstext-heading')) then ($teildokument-uri = $art-anlage-regelungstext-uri) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00921-000"
                    role="error"
                    test="if (. = ('anlageregelungstext-num', 'anlageregelungstext-bezug', 'anlageregelungstext-heading')) then ($teildokument-uri = $art-anlage-regelungstext-uri) else true()"
                    ruleId="SCH-00921"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text>Das Literal "<for-each select=".[1]">
                      <value-of select="." />
                    </for-each>" darf nur innerhalb einer Anlage zu einem Regelungstext verwendet werden.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00921-000"
                role="error"
                test="if (. = ('anlageregelungstext-num', 'anlageregelungstext-bezug', 'anlageregelungstext-heading')) then ($teildokument-uri = $art-anlage-regelungstext-uri) else true()"
                ruleId="SCH-00921"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1913', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="akn:doc[$teildokument-uri = $art-bericht-uri]/akn:conclusions/akn:blockContainer"
    mode="group.d7e180"
    priority="18"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1933' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00930"
          context="akn:doc[$teildokument-uri = $art-bericht-uri]/akn:conclusions/akn:blockContainer"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00930"
          context="akn:doc[$teildokument-uri = $art-bericht-uri]/akn:conclusions/akn:blockContainer"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1937'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1937') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(count(akn:p) ge 2)">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00930-000"
                    role="warn"
                    test="count(akn:p) ge 2"
                    ruleId="SCH-00930"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Im Schlussteil sollen mindestens zwei akn:p enthalten sein (akn:p mit Ort und Datum; optional akn:p mit der Organisation / dem Auschuss; ein oder mehrere akn:p mit Unterschriften).</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00930-000"
                role="warn"
                test="count(akn:p) ge 2"
                ruleId="SCH-00930"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1933', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="akn:doc[$teildokument-uri = $art-bericht-uri]/akn:conclusions/akn:blockContainer/akn:p[position() ge 2 and empty(akn:organization)]"
    mode="group.d7e180"
    priority="17"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1933' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00933"
          context="akn:doc[$teildokument-uri = $art-bericht-uri]/akn:conclusions/akn:blockContainer/akn:p[position() ge 2 and empty(akn:organization)]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00933"
          context="akn:doc[$teildokument-uri = $art-bericht-uri]/akn:conclusions/akn:blockContainer/akn:p[position() ge 2 and empty(akn:organization)]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1943'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1943') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(exists(akn:signature))">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00933-000"
                    role="warn"
                    test="exists(akn:signature)"
                    ruleId="SCH-00933"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >Im Schlussteil soll nach den akn:p mit Ort und Datum und ggf. akn:p mit der Organisation eine Signatur(akn:signature) angegeben werden.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00933-000"
                role="warn"
                test="exists(akn:signature)"
                ruleId="SCH-00933"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1933', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="/akn:akomaNtoso/*/akn:meta/akn:proprietary/sonst:legalDocML.de_metadaten"
    mode="group.d7e180"
    priority="16"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1952' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00940"
          context="/akn:akomaNtoso/*/akn:meta/akn:proprietary/sonst:legalDocML.de_metadaten"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00940"
          context="/akn:akomaNtoso/*/akn:meta/akn:proprietary/sonst:legalDocML.de_metadaten"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1956'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e1959'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1956') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if (sonst:typ = 'berichtigung') then (exists(sonst:bezugstyp)) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00940-000"
                    role="error"
                    test="if (sonst:typ = 'berichtigung') then (exists(sonst:bezugstyp)) else true()"
                    ruleId="SCH-00940"
                  >
                    <where-populated>
                      <attribute name="location" select="path(sonst:typ)" />
                    </where-populated>
                    <svrl:text
                    >Wenn im Metadatenschema "Sonstiger Veröffentlichungstext" der Typ "Berichtigung" angegeben wird, muss zwingend auch ein Bezugstyp genannt werden.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00940-000"
                role="error"
                test="if (sonst:typ = 'berichtigung') then (exists(sonst:bezugstyp)) else true()"
                ruleId="SCH-00940"
              >
                <where-populated>
                  <attribute name="location" select="path(sonst:typ)" />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1959') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if (not(sonst:typ = 'berichtigung')) then (not(exists(sonst:bezugstyp))) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00940-005"
                    role="error"
                    test="if (not(sonst:typ = 'berichtigung')) then (not(exists(sonst:bezugstyp))) else true()"
                    ruleId="SCH-00940"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path(sonst:bezugstyp)"
                      />
                    </where-populated>
                    <svrl:text
                    >Ein Bezugstyp zu einem Typ darf nur angegeben werden, wenn jener Typ "Berichtigung" lautet.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00940-005"
                role="error"
                test="if (not(sonst:typ = 'berichtigung')) then (not(exists(sonst:bezugstyp))) else true()"
                ruleId="SCH-00940"
              >
                <where-populated>
                  <attribute name="location" select="path(sonst:bezugstyp)" />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1952', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="/akn:akomaNtoso/*/akn:meta/akn:proprietary/regtxt:legalDocML.de_metadaten"
    mode="group.d7e180"
    priority="15"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1964' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-00941"
          context="/akn:akomaNtoso/*/akn:meta/akn:proprietary/regtxt:legalDocML.de_metadaten"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-00941"
          context="/akn:akomaNtoso/*/akn:meta/akn:proprietary/regtxt:legalDocML.de_metadaten"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1968'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
            <map-entry key="'d7e1971'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1968') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if (regtxt:typ = 'berichtigung') then (exists(regtxt:bezugstyp)) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00941-000"
                    role="error"
                    test="if (regtxt:typ = 'berichtigung') then (exists(regtxt:bezugstyp)) else true()"
                    ruleId="SCH-00941"
                  >
                    <where-populated>
                      <attribute name="location" select="path(regtxt:typ)" />
                    </where-populated>
                    <svrl:text
                    >Wenn im Metadatenschema "Regelungstext" der Typ "Berichtigung" angegeben wird, muss zwingend auch ein Bezugstyp genannt werden.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00941-000"
                role="error"
                test="if (regtxt:typ = 'berichtigung') then (exists(regtxt:bezugstyp)) else true()"
                ruleId="SCH-00941"
              >
                <where-populated>
                  <attribute name="location" select="path(regtxt:typ)" />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1971') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if (not(regtxt:typ = 'berichtigung')) then (not(exists(regtxt:bezugstyp))) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-00941-005"
                    role="error"
                    test="if (not(regtxt:typ = 'berichtigung')) then (not(exists(regtxt:bezugstyp))) else true()"
                    ruleId="SCH-00941"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path(regtxt:bezugstyp)"
                      />
                    </where-populated>
                    <svrl:text
                    >Ein Bezugstyp zu einem Typ darf nur angegeben werden, wenn jener Typ "Berichtigung" lautet.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-00941-005"
                role="error"
                test="if (not(regtxt:typ = 'berichtigung')) then (not(exists(regtxt:bezugstyp))) else true()"
                ruleId="SCH-00941"
              >
                <where-populated>
                  <attribute name="location" select="path(regtxt:bezugstyp)" />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1964', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="@href | @src | @from | @upTo"
    mode="group.d7e180"
    priority="14"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1979' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-01010"
          context="@href | @src | @from | @upTo"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule id="SCH-01010" context="@href | @src | @from | @upTo">
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1985'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <variable
            name="teil-ohne-fragment"
            select="if (contains(., '#')) then tokenize(., '#')[1] else ."
          />
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1985') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(parent::akn:FRBRauthor or $teil-ohne-fragment = '' or matches($teil-ohne-fragment, '^([a-zöäüßA-ZÄÖÜẞ]+-)+\d+\.[a-zA-Z]+$') or starts-with($teil-ohne-fragment, 'http://') or starts-with($teil-ohne-fragment, 'https://') or starts-with($teil-ohne-fragment, '/eli'))"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-01010-005"
                    test="parent::akn:FRBRauthor or $teil-ohne-fragment = '' or matches($teil-ohne-fragment, '^([a-zöäüßA-ZÄÖÜẞ]+-)+\d+\.[a-zA-Z]+$') or starts-with($teil-ohne-fragment, 'http://') or starts-with($teil-ohne-fragment, 'https://') or starts-with($teil-ohne-fragment, '/eli')"
                    ruleId="SCH-01010"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >URI-Verweise müssen eine der drei folgenden Formen haben: 1) vollständige http/https-URI, 2) nur absoluter Pfad-Teil (beginnend mit '/eli', d. h. innerhalb derselben Authority wie das aktuelle Dokument) oder 3) nur Dateiname und ggf. Fragment (d. h. innerhalb der selben Expression).</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-01010-005"
                test="parent::akn:FRBRauthor or $teil-ohne-fragment = '' or matches($teil-ohne-fragment, '^([a-zöäüßA-ZÄÖÜẞ]+-)+\d+\.[a-zA-Z]+$') or starts-with($teil-ohne-fragment, 'http://') or starts-with($teil-ohne-fragment, 'https://') or starts-with($teil-ohne-fragment, '/eli')"
                ruleId="SCH-01010"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1979', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="/akn:akomaNtoso/*/akn:meta/akn:identification[$ist-entwurfsfassung or $ist-konsolidierte-fassung]"
    mode="group.d7e180"
    priority="13"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e1990' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-01015"
          context="/akn:akomaNtoso/*/akn:meta/akn:identification[$ist-entwurfsfassung or $ist-konsolidierte-fassung]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-01015"
          context="/akn:akomaNtoso/*/akn:meta/akn:identification[$ist-entwurfsfassung or $ist-konsolidierte-fassung]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e1997'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e1997') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(exists(akn:FRBRExpression/akn:FRBRdate))">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-01015-000"
                    role="error"
                    test="exists(akn:FRBRExpression/akn:FRBRdate)"
                    ruleId="SCH-01015"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text>
            In Entwurfs- und konsolidierten Fassungen MUSS innerhalb von &lt;FRBRExpression&gt; ein &lt;FRBRdate&gt; vorhanden sein.
         </svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-01015-000"
                role="error"
                test="exists(akn:FRBRExpression/akn:FRBRdate)"
                ruleId="SCH-01015"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e1990', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="/akn:akomaNtoso/*/akn:meta/akn:identification[$ist-verkündungsfassung]"
    mode="group.d7e180"
    priority="12"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e2003' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          id="SCH-01016"
          context="/akn:akomaNtoso/*/akn:meta/akn:identification[$ist-verkündungsfassung]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          id="SCH-01016"
          context="/akn:akomaNtoso/*/akn:meta/akn:identification[$ist-verkündungsfassung]"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e2010'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e2010') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if test="not(not(akn:FRBRExpression/akn:FRBRdate))">
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-01016-000"
                    role="error"
                    test="not(akn:FRBRExpression/akn:FRBRdate)"
                    ruleId="SCH-01016"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text>
            In der Verkündungsfassung darf innerhalb von &lt;FRBRExpression&gt; KEIN &lt;FRBRdate&gt; vorkommen.
         </svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-01016-000"
                role="error"
                test="not(akn:FRBRExpression/akn:FRBRdate)"
                ruleId="SCH-01016"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e2003', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="akn:identification/akn:FRBRExpression/akn:FRBRauthor/@href"
    mode="group.d7e180"
    priority="11"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e2015' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          role="error"
          context="akn:identification/akn:FRBRExpression/akn:FRBRauthor/@href"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          role="error"
          context="akn:identification/akn:FRBRExpression/akn:FRBRauthor/@href"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e2019'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e2019') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if ($ist-konsolidierte-fassung) then (. = ('recht.bund.de/institution/bundesregierung', 'recht.bund.de/institution/bundeskanzler', 'recht.bund.de/institution/bundespraesident')) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-KONSF-hrefLiterals.expression.FRBRauthor"
                    test="if ($ist-konsolidierte-fassung) then (. = ('recht.bund.de/institution/bundesregierung', 'recht.bund.de/institution/bundeskanzler', 'recht.bund.de/institution/bundespraesident')) else true()"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >In der konsolidierten Fassung ist das Literal "<for-each
                      select=".[1]"
                    >
                      <value-of select="." />
                    </for-each>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich "recht.bund.de/institution/bundesregierung", "recht.bund.de/institution/bundeskanzler" sowie "recht.bund.de/institution/bundespraesident".</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-KONSF-hrefLiterals.expression.FRBRauthor"
                test="if ($ist-konsolidierte-fassung) then (. = ('recht.bund.de/institution/bundesregierung', 'recht.bund.de/institution/bundeskanzler', 'recht.bund.de/institution/bundespraesident')) else true()"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e2015', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="akn:identification/akn:FRBRWork/akn:FRBRauthor/@href"
    mode="group.d7e180"
    priority="10"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e2015' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          role="error"
          context="akn:identification/akn:FRBRWork/akn:FRBRauthor/@href"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          role="error"
          context="akn:identification/akn:FRBRWork/akn:FRBRauthor/@href"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e2027'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e2027') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if ($ist-konsolidierte-fassung) then (. = ('recht.bund.de/institution/bundesregierung', 'recht.bund.de/institution/bundeskanzler', 'recht.bund.de/institution/bundespraesident')) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-KONSF-hrefLiterals.work.FRBRauthor"
                    test="if ($ist-konsolidierte-fassung) then (. = ('recht.bund.de/institution/bundesregierung', 'recht.bund.de/institution/bundeskanzler', 'recht.bund.de/institution/bundespraesident')) else true()"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >In der konsolidierten Fassung ist das Literal "<for-each
                      select=".[1]"
                    >
                      <value-of select="." />
                    </for-each>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich "recht.bund.de/institution/bundesregierung", "recht.bund.de/institution/bundeskanzler" sowie "recht.bund.de/institution/bundespraesident".</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-KONSF-hrefLiterals.work.FRBRauthor"
                test="if ($ist-konsolidierte-fassung) then (. = ('recht.bund.de/institution/bundesregierung', 'recht.bund.de/institution/bundeskanzler', 'recht.bund.de/institution/bundespraesident')) else true()"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e2015', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="akn:identification/akn:FRBRExpression/akn:FRBRdate/@name"
    mode="group.d7e180"
    priority="9"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e2015' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          role="error"
          context="akn:identification/akn:FRBRExpression/akn:FRBRdate/@name"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          role="error"
          context="akn:identification/akn:FRBRExpression/akn:FRBRdate/@name"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e2035'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e2035') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if ($ist-konsolidierte-fassung) then (. = ('ausfertigung', 'ausfertigung-aenderung')) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-KONSF-nameLiterals.expression.FRBRdate"
                    test="if ($ist-konsolidierte-fassung) then (. = ('ausfertigung', 'ausfertigung-aenderung')) else true()"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >In der konsolidierten Fassung ist das Literal "<for-each
                      select=".[1]"
                    >
                      <value-of select="." />
                    </for-each>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich "ausfertigung" und "ausfertigung-aenderung".</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-KONSF-nameLiterals.expression.FRBRdate"
                test="if ($ist-konsolidierte-fassung) then (. = ('ausfertigung', 'ausfertigung-aenderung')) else true()"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e2015', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="akn:identification/akn:FRBRExpression/akn:FRBRthis/@value"
    mode="group.d7e180"
    priority="8"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e2015' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          role="error"
          context="akn:identification/akn:FRBRExpression/akn:FRBRthis/@value"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          role="error"
          context="akn:identification/akn:FRBRExpression/akn:FRBRthis/@value"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e2043'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e2043') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if ($ist-konsolidierte-fassung) then (matches(., '^https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{4}/[-a-z0-9äöüß]+/\d{4}-\d{2}-\d{2}/\d+/[a-z]{3}/[a-zöäüß\-]+-\d+$')) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-KONS-valueLiterals.expression.FRBRthis"
                    test="if ($ist-konsolidierte-fassung) then (matches(., '^https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{{4}}/[-a-z0-9äöüß]+/\d{{4}}-\d{{2}}-\d{{2}}/\d+/[a-z]{{3}}/[a-zöäüß\-]+-\d+$')) else true()"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >In der konsolidierten Fassung ist das Literal "<for-each
                      select=".[1]"
                    >
                      <value-of select="." />
                    </for-each>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{4}/[-a-z0-9äöüß]+/\d{4}-\d{2}-\d{2}/\d+/[a-z]{3}/[a-zöäüß\-]+-\d+" entsprechen.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-KONS-valueLiterals.expression.FRBRthis"
                test="if ($ist-konsolidierte-fassung) then (matches(., '^https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{{4}}/[-a-z0-9äöüß]+/\d{{4}}-\d{{2}}-\d{{2}}/\d+/[a-z]{{3}}/[a-zöäüß\-]+-\d+$')) else true()"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e2015', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="akn:identification/akn:FRBRExpression/akn:FRBRuri/@value"
    mode="group.d7e180"
    priority="7"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e2015' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          role="error"
          context="akn:identification/akn:FRBRExpression/akn:FRBRuri/@value"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          role="error"
          context="akn:identification/akn:FRBRExpression/akn:FRBRuri/@value"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e2051'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e2051') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if ($ist-konsolidierte-fassung) then (matches(., '^https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{4}/[-a-z0-9äöüß]+/\d{4}-\d{2}-\d{2}/\d+/[a-z]{3}$')) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-KONS-valueLiterals.expression.FRBRuri"
                    test="if ($ist-konsolidierte-fassung) then (matches(., '^https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{{4}}/[-a-z0-9äöüß]+/\d{{4}}-\d{{2}}-\d{{2}}/\d+/[a-z]{{3}}$')) else true()"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >In der konsolidierten Fassung ist das Literal "<for-each
                      select=".[1]"
                    >
                      <value-of select="." />
                    </for-each>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{4}/[-a-z0-9äöüß]+/\d{4}-\d{2}-\d{2}/\d+/[a-z]{3}" entsprechen.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-KONS-valueLiterals.expression.FRBRuri"
                test="if ($ist-konsolidierte-fassung) then (matches(., '^https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{{4}}/[-a-z0-9äöüß]+/\d{{4}}-\d{{2}}-\d{{2}}/\d+/[a-z]{{3}}$')) else true()"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e2015', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="akn:identification/akn:FRBRManifestation/akn:FRBRthis/@value"
    mode="group.d7e180"
    priority="6"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e2015' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          role="error"
          context="akn:identification/akn:FRBRManifestation/akn:FRBRthis/@value"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          role="error"
          context="akn:identification/akn:FRBRManifestation/akn:FRBRthis/@value"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e2060'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e2060') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if ($ist-konsolidierte-fassung) then (matches(., '^https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{4}/[-a-z0-9äöüß]+/\d{4}-\d{2}-\d{2}/\d+/[a-z]{3}/\d{4}-\d{2}-\d{2}/[a-zöäüß\-]+-\d+\.[a-zöäüß]+$')) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-KONS-valueLiterals.manifestation.FRBRthis"
                    test="if ($ist-konsolidierte-fassung) then (matches(., '^https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{{4}}/[-a-z0-9äöüß]+/\d{{4}}-\d{{2}}-\d{{2}}/\d+/[a-z]{{3}}/\d{{4}}-\d{{2}}-\d{{2}}/[a-zöäüß\-]+-\d+\.[a-zöäüß]+$')) else true()"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >In der konsolidierten Fassung ist das Literal "<for-each
                      select=".[1]"
                    >
                      <value-of select="." />
                    </for-each>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{4}/[-a-z0-9äöüß]+/\d{4}-\d{2}-\d{2}/\d+/[a-z]{3}/\d{4}-\d{2}-\d{2}/[a-zöäüß\-]+-\d+\.[a-zöäüß]+" entsprechen.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-KONS-valueLiterals.manifestation.FRBRthis"
                test="if ($ist-konsolidierte-fassung) then (matches(., '^https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{{4}}/[-a-z0-9äöüß]+/\d{{4}}-\d{{2}}-\d{{2}}/\d+/[a-z]{{3}}/\d{{4}}-\d{{2}}-\d{{2}}/[a-zöäüß\-]+-\d+\.[a-zöäüß]+$')) else true()"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e2015', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="akn:identification/akn:FRBRManifestation/akn:FRBRuri/@value"
    mode="group.d7e180"
    priority="5"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e2015' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          role="error"
          context="akn:identification/akn:FRBRManifestation/akn:FRBRuri/@value"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          role="error"
          context="akn:identification/akn:FRBRManifestation/akn:FRBRuri/@value"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e2068'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e2068') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if ($ist-konsolidierte-fassung) then (matches(., '^https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{4}/[-a-z0-9äöüß]+/\d{4}-\d{2}-\d{2}/\d+/[a-z]{3}/\d{4}-\d{2}-\d{2}/[a-zöäüß\-]+-\d+\.[a-zöäüß]+$')) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-KONS-valueLiterals.manifestation.FRBRuri"
                    test="if ($ist-konsolidierte-fassung) then (matches(., '^https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{{4}}/[-a-z0-9äöüß]+/\d{{4}}-\d{{2}}-\d{{2}}/\d+/[a-z]{{3}}/\d{{4}}-\d{{2}}-\d{{2}}/[a-zöäüß\-]+-\d+\.[a-zöäüß]+$')) else true()"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >In der konsolidierten Fassung ist das Literal "<for-each
                      select=".[1]"
                    >
                      <value-of select="." />
                    </for-each>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{4}/[-a-z0-9äöüß]+/\d{4}-\d{2}-\d{2}/\d+/[a-z]{3}/\d{4}-\d{2}-\d{2}/[a-zöäüß\-]+-\d+\.[a-zöäüß]+" entsprechen.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-KONS-valueLiterals.manifestation.FRBRuri"
                test="if ($ist-konsolidierte-fassung) then (matches(., '^https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{{4}}/[-a-z0-9äöüß]+/\d{{4}}-\d{{2}}-\d{{2}}/\d+/[a-z]{{3}}/\d{{4}}-\d{{2}}-\d{{2}}/[a-zöäüß\-]+-\d+\.[a-zöäüß]+$')) else true()"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e2015', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="akn:identification/akn:FRBRWork/akn:FRBRname/@value"
    mode="group.d7e180"
    priority="4"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e2015' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          role="error"
          context="akn:identification/akn:FRBRWork/akn:FRBRname/@value"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          role="error"
          context="akn:identification/akn:FRBRWork/akn:FRBRname/@value"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e2076'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e2076') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if ($ist-konsolidierte-fassung) then (. = ('bgbl', 'bgbl-1', 'bgbl-2', 'banz-at', 'banz', 'ebanz', 'vkbl', 'hist')) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-KONSF-valueLiterals.work.FRBRname"
                    test="if ($ist-konsolidierte-fassung) then (. = ('bgbl', 'bgbl-1', 'bgbl-2', 'banz-at', 'banz', 'ebanz', 'vkbl', 'hist')) else true()"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >In der konsolidierten Fassung ist das Literal "<for-each
                      select=".[1]"
                    >
                      <value-of select="." />
                    </for-each>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich "bgbl", "bgbl-1", "bgbl-2", "banz-at", "banz", "ebanz", "vkbl" sowie "hist".</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-KONSF-valueLiterals.work.FRBRname"
                test="if ($ist-konsolidierte-fassung) then (. = ('bgbl', 'bgbl-1', 'bgbl-2', 'banz-at', 'banz', 'ebanz', 'vkbl', 'hist')) else true()"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e2015', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="akn:identification/akn:FRBRWork/akn:FRBRnumber/@value"
    mode="group.d7e180"
    priority="3"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e2015' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          role="error"
          context="akn:identification/akn:FRBRWork/akn:FRBRnumber/@value"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          role="error"
          context="akn:identification/akn:FRBRWork/akn:FRBRnumber/@value"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e2084'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e2084') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if ($ist-konsolidierte-fassung) then (matches(., '^[-a-zäöüß0-9]+$')) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-KONS-valueLiterals.work.FRBRnumber"
                    test="if ($ist-konsolidierte-fassung) then (matches(., '^[-a-zäöüß0-9]+$')) else true()"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >In der konsolidierten Fassung ist das Literal "<for-each
                      select=".[1]"
                    >
                      <value-of select="." />
                    </for-each>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "[-a-zäöüß0-9]+" entsprechen.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-KONS-valueLiterals.work.FRBRnumber"
                test="if ($ist-konsolidierte-fassung) then (matches(., '^[-a-zäöüß0-9]+$')) else true()"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e2015', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="akn:identification/akn:FRBRWork/akn:FRBRsubtype/@value"
    mode="group.d7e180"
    priority="2"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e2015' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          role="error"
          context="akn:identification/akn:FRBRWork/akn:FRBRsubtype/@value"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          role="error"
          context="akn:identification/akn:FRBRWork/akn:FRBRsubtype/@value"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e2092'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e2092') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if ($ist-konsolidierte-fassung) then (matches(., '^(anlage-regelungstext|rechtsetzungsdokument|regelungstext|vereinbarung-verkuendung|sonstiger-veroeffentlichungstext|sonstiges-teildokument)(-[0-9]+)$')) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-KONS-valueLiterals.work.FRBRsubtype"
                    test="if ($ist-konsolidierte-fassung) then (matches(., '^(anlage-regelungstext|rechtsetzungsdokument|regelungstext|vereinbarung-verkuendung|sonstiger-veroeffentlichungstext|sonstiges-teildokument)(-[0-9]+)$')) else true()"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >In der konsolidierten Fassung ist das Literal "<for-each
                      select=".[1]"
                    >
                      <value-of select="." />
                    </for-each>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "(anlage-regelungstext|rechtsetzungsdokument|regelungstext|vereinbarung-verkuendung|sonstiger-veroeffentlichungstext|sonstiges-teildokument)(-[0-9]+)" entsprechen.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-KONS-valueLiterals.work.FRBRsubtype"
                test="if ($ist-konsolidierte-fassung) then (matches(., '^(anlage-regelungstext|rechtsetzungsdokument|regelungstext|vereinbarung-verkuendung|sonstiger-veroeffentlichungstext|sonstiges-teildokument)(-[0-9]+)$')) else true()"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e2015', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="akn:identification/akn:FRBRWork/akn:FRBRthis/@value"
    mode="group.d7e180"
    priority="1"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e2015' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          role="error"
          context="akn:identification/akn:FRBRWork/akn:FRBRthis/@value"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          role="error"
          context="akn:identification/akn:FRBRWork/akn:FRBRthis/@value"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e2100'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e2100') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if ($ist-konsolidierte-fassung) then (matches(., '^https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{4}/[-a-z0-9äöüß]+/[a-zöäüß\-]+-\d+$')) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-KONS-valueLiterals.work.FRBRthis"
                    test="if ($ist-konsolidierte-fassung) then (matches(., '^https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{{4}}/[-a-z0-9äöüß]+/[a-zöäüß\-]+-\d+$')) else true()"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >In der konsolidierten Fassung ist das Literal "<for-each
                      select=".[1]"
                    >
                      <value-of select="." />
                    </for-each>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{4}/[-a-z0-9äöüß]+/[a-zöäüß\-]+-\d+" entsprechen.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-KONS-valueLiterals.work.FRBRthis"
                test="if ($ist-konsolidierte-fassung) then (matches(., '^https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{{4}}/[-a-z0-9äöüß]+/[a-zöäüß\-]+-\d+$')) else true()"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e2015', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template
    match="akn:identification/akn:FRBRWork/akn:FRBRuri/@value"
    mode="group.d7e180"
    priority="0"
  >
    <param
      name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
      as="Q{http://www.w3.org/2001/XMLSchema}string*"
      select="()"
    />
    <variable
      name="Q{http://dmaus.name/ns/2023/schxslt}rule-context"
      as="node()"
      select="."
    />
    <choose>
      <when test="'d7e2015' = $Q{http://dmaus.name/ns/2023/schxslt}pattern">
        <svrl:suppressed-rule
          role="error"
          context="akn:identification/akn:FRBRWork/akn:FRBRuri/@value"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:suppressed-rule>
        <next-match />
      </when>
      <otherwise>
        <svrl:fired-rule
          role="error"
          context="akn:identification/akn:FRBRWork/akn:FRBRuri/@value"
        >
          <if test="(document-uri(.), base-uri(root()))[1]">
            <attribute
              name="document"
              select="(document-uri(.), base-uri(root()))[1]"
            />
          </if>
        </svrl:fired-rule>
        <variable
          name="Q{http://dmaus.name/ns/2023/schxslt}severity"
          as="map(Q{http://www.w3.org/2001/XMLSchema}string, Q{http://www.w3.org/2001/XMLSchema}integer)"
        >
          <map>
            <map-entry key="'d7e2109'">
              <variable
                name="severity"
                as="Q{http://www.w3.org/2001/XMLSchema}string"
                select="'fatal'"
              />
              <sequence
                select="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity($severity)"
              />
            </map-entry>
          </map>
        </variable>
        <for-each select=".">
          <choose>
            <when
              test="Q{http://www.w3.org/2005/xpath-functions/map}get($Q{http://dmaus.name/ns/2023/schxslt}severity, 'd7e2109') ge Q{http://dmaus.name/ns/2023/schxslt}numeric-severity('info')"
            >
              <if
                test="not(if ($ist-konsolidierte-fassung) then (matches(., '^https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{4}/[-a-z0-9äöüß]+$')) else true())"
              >
                <variable name="failed-assert" as="element(svrl:failed-assert)">
                  <svrl:failed-assert
                    id="SCH-KONS-valueLiterals.work.FRBRuri"
                    test="if ($ist-konsolidierte-fassung) then (matches(., '^https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{{4}}/[-a-z0-9äöüß]+$')) else true()"
                  >
                    <where-populated>
                      <attribute
                        name="location"
                        select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                      />
                    </where-populated>
                    <svrl:text
                    >In der konsolidierten Fassung ist das Literal "<for-each
                      select=".[1]"
                    >
                      <value-of select="." />
                    </for-each>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{4}/[-a-z0-9äöüß]+" entsprechen.</svrl:text>
                  </svrl:failed-assert>
                </variable>
                <sequence select="$failed-assert" />
              </if>
            </when>
            <otherwise>
              <svrl:skipped-assert
                severityThreshold="info"
                id="SCH-KONS-valueLiterals.work.FRBRuri"
                test="if ($ist-konsolidierte-fassung) then (matches(., '^https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{{4}}/[-a-z0-9äöüß]+$')) else true()"
              >
                <where-populated>
                  <attribute
                    name="location"
                    select="path($Q{http://dmaus.name/ns/2023/schxslt}rule-context)"
                  />
                </where-populated>
              </svrl:skipped-assert>
            </otherwise>
          </choose>
        </for-each>
        <next-match>
          <with-param
            name="Q{http://dmaus.name/ns/2023/schxslt}pattern"
            as="Q{http://www.w3.org/2001/XMLSchema}string*"
            select="('d7e2015', $Q{http://dmaus.name/ns/2023/schxslt}pattern)"
          />
        </next-match>
      </otherwise>
    </choose>
  </template>
  <template match="root()" as="element(svrl:schematron-output)">
    <svrl:schematron-output
      schemaVersion="LegalDocML.de 1.9 (03.11.2025)"
      phase="#ALL"
      title="Regelungstext Entwurfsfassung Regelungstext Regelungstext Allgemein Klasse Inhaltsverzeichnis Klasse: Hauptteil Aufzählungen Gliederungsebenen Schlussteil Vorblatt (Regelungstext) Vorblatt (Beschlussempfehlung) Begründung Prüfkriterien Prüfkriterien Allgemeiner Teil Regelungsfolgen Anschreiben Ausschussüberweisung/Legislaturperiode/Drucksachennummer Vereinbarung Änderungsbefehle Struktureller Aufbau von eId-Textknoten Dereferenzierbarkeit lokaler Verweise von akn:destinations innerhalb einer akn:passiveModification Struktureller Aufbau der ELI-Uris (Metadaten) Verweise auf TLC-Klassen Verwendung von Markern  Regeln zu Listen in Regelungstexten  Ontologie Regeln zu Beschlussempfehlungen Regeln zu Regelungstext-Anlagen Regeln zu Berichten Regeln zu einzelnen Metadaten Regeln zu URI-Verweisen"
    >
      <svrl:ns-prefix-in-attribute-values
        prefix="akn"
        uri="http://rechtsinformationen.bund.de/schema/norm/0.1"
      />
      <svrl:ns-prefix-in-attribute-values
        prefix="regtxt"
        uri="http://MetadatenRegelungstext.LegalDocML.de/1.9/"
      />
      <svrl:ns-prefix-in-attribute-values
        prefix="redok"
        uri="http://MetadatenRechtsetzungsdokument.LegalDocML.de/1.9/"
      />
      <svrl:ns-prefix-in-attribute-values
        prefix="btag"
        uri="http://MetadatenBundestag.LegalDocML.de/1.9/"
      />
      <svrl:ns-prefix-in-attribute-values
        prefix="brat"
        uri="http://MetadatenBundesrat.LegalDocML.de/1.9/"
      />
      <svrl:ns-prefix-in-attribute-values
        prefix="breg"
        uri="http://MetadatenBundesregierung.LegalDocML.de/1.9/"
      />
      <svrl:ns-prefix-in-attribute-values
        prefix="fhilf"
        uri="http://MetadatenFormulierungshilfe.LegalDocML.de/1.9/"
      />
      <svrl:ns-prefix-in-attribute-values
        prefix="nkr"
        uri="http://MetadatenNormenkontrollrat.LegalDocML.de/1.9/"
      />
      <svrl:ns-prefix-in-attribute-values
        prefix="sonst"
        uri="http://MetadatenSonstigerVeroeffentlichungstext.LegalDocML.de/1.9/"
      />
      <svrl:ns-prefix-in-attribute-values
        prefix="fkt"
        uri="lokale-funktionen"
      />
      <svrl:ns-prefix-in-attribute-values
        prefix="xs"
        uri="http://www.w3.org/2001/XMLSchema"
      />
      <!--SchXslt2 1.11.2-->
      <try>
        <apply-templates
          select="."
          mode="Q{http://dmaus.name/ns/2023/schxslt}validate"
        />
        <catch>
          <svrl:error code="{$Q{http://www.w3.org/2005/xqt-errors}code}">
            <if test="(document-uri(.), base-uri(root()))[1]">
              <attribute
                name="document"
                select="(document-uri(.), base-uri(root()))[1]"
              />
            </if>
            <if test="$Q{http://www.w3.org/2005/xqt-errors}line-number">
              <attribute
                name="line-number"
                select="$Q{http://www.w3.org/2005/xqt-errors}line-number"
              />
            </if>
            <if test="$Q{http://www.w3.org/2005/xqt-errors}column-number">
              <attribute
                name="column-number"
                select="$Q{http://www.w3.org/2005/xqt-errors}column-number"
              />
            </if>
            <if test="$Q{http://www.w3.org/2005/xqt-errors}description">
              <value-of
                select="$Q{http://www.w3.org/2005/xqt-errors}description"
              />
            </if>
          </svrl:error>
          <variable
            name="message"
            as="Q{http://www.w3.org/2001/XMLSchema}string+"
            expand-text="yes"
          >
                    Running the ISO Schematron validation failed with a dynamic error.
                    Error code: {$Q{http://www.w3.org/2005/xqt-errors}code} Reason: {$Q{http://www.w3.org/2005/xqt-errors}description}
                  </variable>
          <message
            terminate="yes"
            error-code="Q{{http://dmaus.name/ns/2023/schxslt}}ValidationError"
          >
            <text />
            <value-of select="normalize-space(string-join($message))" />
          </message>
        </catch>
      </try>
    </svrl:schematron-output>
  </template>
  <template
    match="root()"
    as="element()*"
    mode="Q{http://dmaus.name/ns/2023/schxslt}validate"
  >
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern />
    <svrl:active-pattern>
      <attribute
        name="name"
      >expression.FRBRdate in Entwurfs- und konsolidierter Fassung</attribute>
    </svrl:active-pattern>
    <svrl:active-pattern>
      <attribute
        name="name"
      >Kein expression.FRBRdate in der Verkündungsfassung</attribute>
    </svrl:active-pattern>
    <svrl:active-pattern />
    <apply-templates select="root()" mode="group.d7e180" />
  </template>
  <function
    name="Q{http://dmaus.name/ns/2023/schxslt}numeric-severity"
    as="Q{http://www.w3.org/2001/XMLSchema}integer"
  >
    <param name="severity" as="Q{http://www.w3.org/2001/XMLSchema}string" />
    <sequence
      select="(index-of(('info', 'warning', 'error', 'fatal'), $severity), 4)[1]"
    />
  </function>
</stylesheet>
