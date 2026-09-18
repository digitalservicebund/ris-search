<?xml version="1.0" encoding="UTF-8"?>
<!--

Diese Datei ist Bestandteil der Schematronregeln des Standards LegalDocML.de 1.8.2 (07.08.2025).
Sie wird inkludiert von der Schematron-Hauptdatei.

                
********************************* Hinweis zur Lizensierung ***********************************
 2025 Copyright © 2021–2025 Bundesministerium des Innern und für Heimat,
 Referat DG II 6, Maßnahmen Enterprise Resource Management und Elektronische Verwaltungsarbeit

      Lizenz: CC-BY-3.0 (Creative Commons Namensnennung 3.0)
 Information: https://creativecommons.org/licenses/by/3.0/legalcode.de
**********************************************************************************************

-->
<sch:pattern xmlns:sch="http://purl.oclc.org/dsdl/schematron">
   <sch:rule role="error"
             context="akn:identification/akn:FRBRExpression/akn:FRBRauthor/@href">
      <sch:assert id="SCH-ENTWF-hrefLiterals.expression.FRBRauthor"
                  test="if ($ist-entwurfsfassung) then (. = ('recht.bund.de/institution/bundesregierung', 'recht.bund.de/institution/bundestag', 'recht.bund.de/institution/bundesrat', 'recht.bund.de/institution/bundeskanzler', 'recht.bund.de/institution/bundespraesident')) else true()">In der Entwurfsfassung ist das Literal "<sch:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich "recht.bund.de/institution/bundesregierung", "recht.bund.de/institution/bundestag", "recht.bund.de/institution/bundesrat", "recht.bund.de/institution/bundeskanzler" sowie "recht.bund.de/institution/bundespraesident".</sch:assert>
   </sch:rule>
   <sch:rule role="error"
             context="akn:identification/akn:FRBRWork/akn:FRBRauthor/@href">
      <sch:assert id="SCH-ENTWF-hrefLiterals.work.FRBRauthor"
                  test="if ($ist-entwurfsfassung) then (. = ('recht.bund.de/institution/bundesregierung', 'recht.bund.de/institution/bundestag', 'recht.bund.de/institution/bundesrat', 'recht.bund.de/institution/bundeskanzler', 'recht.bund.de/institution/bundespraesident')) else true()">In der Entwurfsfassung ist das Literal "<sch:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich "recht.bund.de/institution/bundesregierung", "recht.bund.de/institution/bundestag", "recht.bund.de/institution/bundesrat", "recht.bund.de/institution/bundeskanzler" sowie "recht.bund.de/institution/bundespraesident".</sch:assert>
   </sch:rule>
   <sch:rule role="error"
             context="akn:identification/akn:FRBRExpression/akn:FRBRdate/@name">
      <sch:assert id="SCH-ENTWF-nameLiterals.expression.FRBRdate"
                  test="if ($ist-entwurfsfassung) then (. = ('version')) else true()">In der Entwurfsfassung ist das Literal "<sch:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt ist ausschließlich "version".</sch:assert>
   </sch:rule>
   <sch:rule role="error"
             context="akn:identification/akn:FRBRExpression/akn:FRBRthis/@value">
      <sch:assert id="SCH-ENTWF-valueLiterals.expression.FRBRthis"
                  test="if ($ist-entwurfsfassung) then (matches(., '^eli/dl/\d{4}/[a-zäöüß-]+/[0-9]+/[a-zäöüß-]+/[a-zäöüß-]+/\d{4}-\d{2}-\d{2}/[0-9]+/[a-z]{3}/[a-zöäüß\-]+-\d+$')) else true()">In der Entwurfsfassung ist das Literal "<sch:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "eli/dl/\d{4}/[a-zäöüß-]+/[0-9]+/[a-zäöüß-]+/[a-zäöüß-]+/\d{4}-\d{2}-\d{2}/[0-9]+/[a-z]{3}/[a-zöäüß\-]+-\d+" entsprechen.</sch:assert>
   </sch:rule>
   <sch:rule role="error"
             context="akn:identification/akn:FRBRExpression/akn:FRBRuri/@value">
      <sch:assert id="SCH-ENTWF-valueLiterals.expression.FRBRuri"
                  test="if ($ist-entwurfsfassung) then (matches(., '^eli/dl/\d{4}/[a-zäöüß-]+/[0-9]+/[a-zäöüß-]+/[a-zäöüß-]+/\d{4}-\d{2}-\d{2}/[0-9]+/[a-z]{3}$')) else true()">In der Entwurfsfassung ist das Literal "<sch:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "eli/dl/\d{4}/[a-zäöüß-]+/[0-9]+/[a-zäöüß-]+/[a-zäöüß-]+/\d{4}-\d{2}-\d{2}/[0-9]+/[a-z]{3}" entsprechen.</sch:assert>
   </sch:rule>
   <sch:rule role="error"
             context="akn:identification/akn:FRBRManifestation/akn:FRBRthis/@value">
      <sch:assert id="SCH-ENTWF-valueLiterals.manifestation.FRBRthis"
                  test="if ($ist-entwurfsfassung) then (matches(., '^eli/dl/\d{4}/[a-zäöüß-]+/[0-9]+/[a-zäöüß-]+/[a-zäöüß-]+/\d{4}-\d{2}-\d{2}/[0-9]+/[a-z]{3}/[a-zöäüß\-]+-\d+\.[a-zöäüß]+$')) else true()">In der Entwurfsfassung ist das Literal "<sch:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "eli/dl/\d{4}/[a-zäöüß-]+/[0-9]+/[a-zäöüß-]+/[a-zäöüß-]+/\d{4}-\d{2}-\d{2}/[0-9]+/[a-z]{3}/[a-zöäüß\-]+-\d+\.[a-zöäüß]+" entsprechen.</sch:assert>
   </sch:rule>
   <sch:rule role="error"
             context="akn:identification/akn:FRBRManifestation/akn:FRBRuri/@value">
      <sch:assert id="SCH-ENTWF-valueLiterals.manifestation.FRBRuri"
                  test="if ($ist-entwurfsfassung) then (matches(., '^eli/dl/\d{4}/[a-zäöüß-]+/[0-9]+/[a-zäöüß-]+/[a-zäöüß-]+/\d{4}-\d{2}-\d{2}/[0-9]+/[a-z]{3}/[a-zöäüß\-]+-\d+\.[a-zöäüß]+$')) else true()">In der Entwurfsfassung ist das Literal "<sch:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "eli/dl/\d{4}/[a-zäöüß-]+/[0-9]+/[a-zäöüß-]+/[a-zäöüß-]+/\d{4}-\d{2}-\d{2}/[0-9]+/[a-z]{3}/[a-zöäüß\-]+-\d+\.[a-zöäüß]+" entsprechen.</sch:assert>
   </sch:rule>
   <sch:rule role="error"
             context="akn:identification/akn:FRBRWork/akn:FRBRname/@value">
      <sch:assert id="SCH-ENTWF-valueLiterals.work.FRBRname"
                  test="if ($ist-entwurfsfassung) then (. = ('regelungsentwurf', 'sonstiges-dokument')) else true()">In der Entwurfsfassung ist das Literal "<sch:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich "regelungsentwurf" und "sonstiges-dokument".</sch:assert>
   </sch:rule>
   <sch:rule role="error"
             context="akn:identification/akn:FRBRWork/akn:FRBRnumber/@value">
      <sch:assert id="SCH-ENTWF-valueLiterals.work.FRBRnumber"
                  test="if ($ist-entwurfsfassung) then (matches(., '^[0-9]+$')) else true()">In der Entwurfsfassung ist das Literal "<sch:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "[0-9]+" entsprechen.</sch:assert>
   </sch:rule>
   <sch:rule role="error"
             context="akn:identification/akn:FRBRWork/akn:FRBRsubtype/@value">
      <sch:assert id="SCH-ENTWF-valueLiterals.work.FRBRsubtype"
                  test="if ($ist-entwurfsfassung) then (matches(., '^(rechtsetzungsdokument|aenderungsantrag|anlage-regelungstext|anschreiben|anschreiben-einigungsvorschlag-des-vermittlungsausschusses|anschreiben-vorschlag-an-bundesrat|antrag|austauschseite|begruendung-aenderungsantrag|begruendung-entschliessungsantrag|begruendung-regelungstext|bekanntmachungstext|bekanntmachungstext-berichtigung|bekanntmachungstext-entscheidung-des-bundesverfassungsgerichts|bericht|berichtigung|beschluss-des-bundesrates|beschlussempfehlung|beschlussvorschlag-der-bundesregierung|denkschrift|entschliessungsantrag|gegenaeusserung-der-bundesregierung|gesetze-beschluss-des-bundestages|gutachtliche-stellungnahme|mitteilung-an-bundesrat|nkr-stellungnahme|regelungstext-entwurf|sonstiges-dokument|sprechzettel-für-regierungssprecher|stellungnahme-bundesrat|synopse|unterrichtung|vereinbarung-entwurf|vorblatt-beschlussempfehlung|vorblatt-regelungstext|vorlage-an-bundesrat|vorschlag-an-bundesrat|wahlvorschlag|gesetzesbeschluss-des-bundestages)(-[0-9]+)$')) else true()">In der Entwurfsfassung ist das Literal "<sch:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "(rechtsetzungsdokument|aenderungsantrag|anlage-regelungstext|anschreiben|anschreiben-einigungsvorschlag-des-vermittlungsausschusses|anschreiben-vorschlag-an-bundesrat|antrag|austauschseite|begruendung-aenderungsantrag|begruendung-entschliessungsantrag|begruendung-regelungstext|bekanntmachungstext|bekanntmachungstext-berichtigung|bekanntmachungstext-entscheidung-des-bundesverfassungsgerichts|bericht|berichtigung|beschluss-des-bundesrates|beschlussempfehlung|beschlussvorschlag-der-bundesregierung|denkschrift|entschliessungsantrag|gegenaeusserung-der-bundesregierung|gesetze-beschluss-des-bundestages|gutachtliche-stellungnahme|mitteilung-an-bundesrat|nkr-stellungnahme|regelungstext-entwurf|sonstiges-dokument|sprechzettel-für-regierungssprecher|stellungnahme-bundesrat|synopse|unterrichtung|vereinbarung-entwurf|vorblatt-beschlussempfehlung|vorblatt-regelungstext|vorlage-an-bundesrat|vorschlag-an-bundesrat|wahlvorschlag|gesetzesbeschluss-des-bundestages)(-[0-9]+)" entsprechen.</sch:assert>
   </sch:rule>
   <sch:rule role="error"
             context="akn:identification/akn:FRBRWork/akn:FRBRthis/@value">
      <sch:assert id="SCH-ENTWF-valueLiterals.work.FRBRthis"
                  test="if ($ist-entwurfsfassung) then (matches(., '^eli/dl/\d{4}/[a-zäöüß-]+/[0-9]+/[a-zäöüß-]+/[a-zäöüß\-]+-\d+$')) else true()">In der Entwurfsfassung ist das Literal "<sch:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "eli/dl/\d{4}/[a-zäöüß-]+/[0-9]+/[a-zäöüß-]+/[a-zäöüß\-]+-\d+" entsprechen.</sch:assert>
   </sch:rule>
   <sch:rule role="error"
             context="akn:identification/akn:FRBRWork/akn:FRBRuri/@value">
      <sch:assert id="SCH-ENTWF-valueLiterals.work.FRBRuri"
                  test="if ($ist-entwurfsfassung) then (matches(., '^eli/dl/\d{4}/[a-zäöüß-]+/[0-9]+/[a-zäöüß-]+$')) else true()">In der Entwurfsfassung ist das Literal "<sch:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "eli/dl/\d{4}/[a-zäöüß-]+/[0-9]+/[a-zäöüß-]+" entsprechen.</sch:assert>
   </sch:rule>
</sch:pattern>
