<script setup lang="ts">
import { useStaticPageSeo } from "~/composables/useStaticPageSeo";

definePageMeta({ alias: ["/datenschutz"], layout: false });

useStaticPageSeo("datenschutz");
</script>

<template>
  <div>
    <NuxtLayout name="static-page">
      <template #breadcrumb>
        <RisBreadcrumb :items="[{ label: 'Datenschutzerklärung' }]" />
      </template>

      <template #pageTitle>Datenschutzerklärung</template>

      <div class="space-y-64">
        <section class="space-y-16">
          <p>Zuletzt aktualisiert: [23. April 2025]</p>
          <p>
            Für das Bundesministerium der Justiz und für Verbraucherschutz
            (BMJV) hat der Schutz personenbezogener Daten einen hohen
            Stellenwert. Auf dieser Seite informieren wir Sie über die
            Verarbeitung Ihrer personenbezogenen Daten, wenn Sie
          </p>
          <ul class="list-bullet">
            <li>
              unsere Webseiten testphase.rechtsinformationen.bund.de
              (Informationen zum Einsatz von Cookies finden Sie in den
              <NuxtLink
                class="ris-link1-regular"
                :to="{ name: 'cookie-settings' }"
                >Cookie-Einstellungen</NuxtLink
              >) bzw. docs.rechtsinformationen.bund.de besuchen (siehe Abschnitt
              A);
            </li>
            <li>
              die Kontakt-E-Mail-Adresse unserer Auftragsverarbeiterin
              <a
                href="mailto:rechtsinformationen@digitalservice.bund.de"
                class="ris-link1-regular"
                >rechtsinformationen@digitalservice.bund.de</a
              >
              benutzen (siehe Abschnitt B);
            </li>
            <li>unsere Feedback-Box benutzen (siehe Abschnitt C).</li>
          </ul>
          <p>
            Außerdem erhalten Sie Informationen zur Weitergabe Ihrer
            personenbezogenen Daten an Dritte, zur Dauer der Speicherung Ihrer
            personenbezogenen Daten, zu Maßnahmen zur Einhaltung der
            Datensicherheit sowie über Ihre Rechte in Bezug auf die Verarbeitung
            Ihrer personenbezogenen Daten und darüber, an wen Sie sich
            diesbezüglich wenden können (siehe Abschnitt D).
          </p>
          <p>
            Das Bundesministerium der Justiz und für Verbraucherschutz (BMJV)
            verarbeitet Ihre personenbezogenen Daten gemäß den
            datenschutzrechtlichen Vorschriften der Verordnung (EU) 2016/679
            (Datenschutz-Grundverordnung – „DS-GVO“), des
            Bundesdatenschutzgesetzes („BDSG“) in der ab dem 25. Mai 2018
            geltenden Fassung, sowie des
            Telekommunikation-Digitale-Dienste-Datenschutz-Gesetzes („TDDDG“).
          </p>
          <p>
            Personenbezogene Daten sind Angaben zu einer Person oder
            Informationen, die sich auf eine Person beziehen (z.B. Name,
            Adresse, Postanschrift, Telefonnummer, IP-Adresse) und die dazu
            genutzt werden können, die Identität einer Person zu erfahren; es
            sind alle Informationen, die sich auf eine identifizierte oder
            identifizierbare natürliche Person beziehen. Als identifizierbar
            wird eine natürliche Person angesehen, die direkt oder indirekt –
            insbesondere mittels Zuordnung zu einer Kennung wie einem Namen, zu
            einer Kennnummer, zu Standortdaten oder zu einer Online-Kennung –
            identifiziert werden kann.
          </p>
          <dl class="[&_dt]:mt-32">
            <dt class="ris-body1-bold">1. Verantwortliche Organisation:</dt>
            <dd>
              Bundesministerium der Justiz und für Verbraucherschutz<br />Anton-Wilhelm-Amo-Straße
              37, 10117 Berlin<br />Telefon: +49 (0)30 18 580 – 0<br /><br />E-Mail:
              <a href="mailto:poststelle@bmj.bund.de" class="ris-link1-regular"
                >poststelle@bmj.bund.de</a
              >
            </dd>
            <dt class="ris-body1-bold">
              2. Die behördliche Datenschutzbeauftragte des BMJV:
            </dt>
            <dd>
              Bundesministerium der Justiz und für Verbraucherschutz<br />Behördliche
              Datenschutzbeauftragte<br />Anton-Wilhelm-Amo-Straße 37, 10117
              Berlin<br />Telefon: +49 (0)30 18 580 – 0<br /><br />E-Mail:
              <a href="mailto:datenschutz@bmj.bund.de" class="ris-link1-regular"
                >datenschutz@bmj.bund.de</a
              >
            </dd>
            <dt class="ris-body1-bold">
              3. Ansprechpartner für Angelegenheiten des Datenschutzes bei der
              Auftragsverarbeiterin
            </dt>
            <dd>
              Sie können Ihre datenschutzrechtlichen Fragen auch an den
              Datenschutzbeauftragten der Auftragsverarbeiterin, der
              DigitalService GmbH des Bundes (DS), richten. Diesen erreichen Sie
              unter der E-Mail-Adresse:
              <a
                href="mailto:datenschutz@digitalservice.bund.de"
                class="ris-link1-regular"
                >datenschutz@digitalservice.bund.de</a
              >.
            </dd>
          </dl>
        </section>
        <section>
          <h2 class="ris-heading2-regular">Abschnitt A</h2>
          <h3 class="ris-heading3-regular">
            I. Datenschutzinformationen für die Besucher der Webseiten
            testphase.rechtsinformationen.bund.de bzw.
            docs.rechtsinformationen.bund.de
          </h3>
          <h4 class="ris-subhead-bold">I.1. Zweck der Datenverarbeitung</h4>
          <p>
            Wenn Sie diese Webseiten aufrufen, speichern wir automatisch Ihre
            Internet-Protokoll-Adresse (IP-Adresse). Dieses Datum wird aus
            Sicherheitsgründen verwendet sowie um die Webseiten zugänglich
            machen zu können (Bereitstellung und Optimierung einer
            funktionsfähigen Webseite sowie der Inhalte und Leistungen) und um
            eventuell auftretende technische Probleme zu erkennen.
          </p>
          <h4 class="ris-subhead-bold">
            I.2. Kategorien personenbezogener Daten (Nutzungsdaten)
          </h4>
          <ul class="list-bullet">
            <li>Ihre Internet-Protokoll-Adresse (IP-Adresse)</li>
          </ul>
          <h4 class="ris-subhead-bold">
            I.3. Rechtsgrundlage für den Betrieb der Webseiten:
          </h4>
          <p>
            Die Datenverarbeitung erfolgt auf Grundlage des Artikel 6 Abs. 1
            UAbs. 1 lit. e) und Abs. 3 lit. b) DS-GVO in Verbindung mit § 3
            E-Government-Gesetz (EGovG) im Rahmen der Öffentlichkeitsarbeit zur
            bedarfsorientierten Bereitstellung von Informationen zu den dem BMJV
            übertragenen Aufgaben.
          </p>
        </section>
        <section>
          <h2 class="ris-heading2-regular">Abschnitt B</h2>
          <h3 class="ris-heading3-regular">
            II. Datenschutzinformationen bei Nutzung der Kontakt-E-Mail-Adresse
          </h3>
          <h4 class="ris-subhead-bold">II.1. Zweck der Datenverarbeitung</h4>
          <p>
            Auf den Webseiten besteht für die Nutzenden die Möglichkeit, mit uns
            über die E-Mail-Adresse
            <a
              href="mailto:rechtsinformationen@digitalservice.bund.de"
              class="ris-link1-regular"
              >rechtsinformationen@digitalservice.bund.de</a
            >
            in Kontakt zu treten. Die dabei erhobenen personenbezogenen Daten
            werden ausschließlich dafür verwendet, um mit Ihnen zu
            kommunizieren.
          </p>
          <h4 class="ris-subhead-bold">
            II.2. Kategorien personenbezogener Daten
          </h4>
          <ul class="list-bullet">
            <li>Name/Vorname</li>
            <li>E-Mail-Adresse</li>
            <li>Anschrift (optional)</li>
          </ul>
          <h4 class="ris-subhead-bold">
            II.3. Rechtsgrundlage für die Verarbeitung Ihrer personenbezogenen
            Daten bei Nutzung der Kontakt-E-Mail-Adresse
          </h4>
          <p>
            Die Datenverarbeitung erfolgt auf Grundlage des Artikel 6 Abs. 1
            UAbs. 1 lit. e) und Abs. 3 lit. b) DS-GVO in Verbindung mit § 3 BDSG
            und ist zur Wahrnehmung unserer Aufgaben erforderlich.
          </p>
        </section>
        <section>
          <h2 class="ris-heading2-regular">Abschnitt C</h2>
          <h3 class="ris-heading3-regular">
            III. Datenschutzinformationen bei Nutzung der Feedback Box
          </h3>
          <h4 class="ris-subhead-bold">III. 1. Zweck der Datenverarbeitung</h4>
          <p>
            Es besteht für Sie die Möglichkeit, uns Feedback über unsere
            Feedback Box einzureichen. Ihr Feedback hilft uns, die Inhalte
            unseres Service kontinuierlich zu verbessern. Sie werden
            ausdrücklich gebeten, keine personenbezogenen Daten in die Feedback
            Box einzutragen.
          </p>
          <h4 class="ris-subhead-bold">
            III.2. Kategorien personenbezogener Daten
          </h4>
          <ul class="list-bullet">
            <li>Ihre Internet-Protokoll-Adresse (IP-Adresse)</li>
          </ul>
          <h4 class="ris-subhead-bold">
            III.3. Rechtsgrundlage für die Verarbeitung Ihrer personenbezogenen
            Daten bei Nutzung der Feedback Box
          </h4>
          <p>
            Die Datenverarbeitung erfolgt auf Grundlage Ihrer Einwilligung
            (Artikel 6 Abs. 1 UAbs. 1 lit. a) DS-GVO in Verbindung mit § 25 Abs.
            1 TDDDG).
          </p>
        </section>
        <section>
          <h2 class="ris-heading2-regular">Abschnitt D</h2>
          <h3 class="ris-heading3-regular">
            IV. Empfänger der personenbezogenen Daten/Weitergabe an Dritte:
          </h3>
          <p>
            Ihre personenbezogenen Daten werden ausschließlich auf Servern
            innerhalb der Europäischen Union/des Europäischen Wirtschaftsraums
            verarbeitet und nicht an unberechtigte Dritte weitergeleitet.
          </p>
          <h3 class="ris-heading3-regular">V. Dauer der Speicherung</h3>
          <p>
            Personenbezogene Daten von Besuchern der Webseiten werden gelöscht,
            wenn ihre Kenntnis für die in dieser Datenschutzerklärung
            beschriebenen Zwecke nicht mehr erforderlich ist, soweit nicht
            gesetzliche Bestimmungen eine längere Speicherung vorschreiben.
            Nutzungsdaten (siehe Abschnitt A) werden insofern regelmäßig für
            einen Zeitraum von 90 Tagen gespeichert. Daten aus der
            Kontakt-E-Mail-Adresse (siehe Abschnitt B) werden gelöscht, wenn die
            jeweilige Anfrage abschließend bearbeitet ist. Eine Speicherung
            personenbezogener Daten bei Benutzung der Feedback Box (siehe
            Abschnitt C) findet nicht statt.
          </p>
          <h3 class="ris-heading3-regular">
            VI. Rechte als Betroffene oder Betroffener
          </h3>
          <p>
            Jede Person, deren personenbezogene Daten verarbeitet werden, kann
            als Betroffene oder Betroffener gegenüber der verantwortlichen
            Organisation (gemäß Ziffer 1 (s.o.) das BMJV) die nachfolgend
            benannten Rechte geltend machen.
          </p>
          <h4 class="ris-subhead-bold">
            VI.1. Recht auf Auskunft (Art. 15 DS-GVO)
          </h4>
          <p>
            Mit dem Recht auf Auskunft erhält der oder die Betroffene eine
            umfassende Einsicht in die ihn angehenden personenbezogenen Daten
            und einige andere wichtige Kriterien wie beispielsweise die
            Verarbeitungszwecke oder die Dauer der Speicherung. Es gelten die in
            § 34 BDSG geregelten Ausnahmen von diesem Recht.
          </p>
          <h4 class="ris-subhead-bold">
            VI.2. Recht auf Berichtigung (Art. 16 DS-GVO)
          </h4>
          <p>
            Das Recht auf Berichtigung beinhaltet die Möglichkeit für den
            Betroffenen oder die Betroffene, unrichtige ihn oder sie angehende
            personenbezogene Daten korrigieren zu lassen.
          </p>
          <h4 class="ris-subhead-bold">
            VI.3. Recht auf Löschung (Art. 17 DS-GVO)
          </h4>
          <p>
            Das Recht auf Löschung beinhaltet die Möglichkeit für den
            Betroffenen oder die Betroffene, Daten bei der verantwortlichen
            Organisation löschen zu lassen. Dies ist allerdings nur dann
            möglich, wenn die ihn oder sie angehenden personenbezogenen Daten
            nicht mehr notwendig sind, rechtswidrig verarbeitet werden oder eine
            diesbezügliche Einwilligung widerrufen wurde. Es gelten die in § 35
            BDSG geregelten Ausnahmen von diesem Recht.
          </p>
          <h4 class="ris-subhead-bold">
            VI.4. Recht auf Einschränkung der Verarbeitung (Art. 18 DS-GVO)
          </h4>
          <p>
            Das Recht auf Einschränkung der Verarbeitung beinhaltet die
            Möglichkeit für den Betroffenen oder die Betroffene, eine weitere
            Verarbeitung der ihn oder sie angehenden personenbezogenen Daten
            vorerst zu verhindern. Eine Einschränkung tritt vor allem in der
            Prüfungsphase anderer Rechtewahrnehmungen durch den Betroffenen oder
            die Betroffene ein.
          </p>
          <h4 class="ris-subhead-bold">
            VI.5. Recht auf Datenübertragbarkeit (Art. 20 DS-GVO)
          </h4>
          <p>
            Das Recht auf Datenübertragbarkeit beinhaltet die Möglichkeit für
            den Betroffenen oder die Betroffene, die ihn oder sie angehenden
            personenbezogenen Daten in einem gängigen, maschinenlesbaren Format
            von der verantwortlichen Organisation zu erhalten, um sie ggf. an
            eine andere der verantwortliche Organisation weiterleiten zu lassen.
            Gemäß Artikel 20 Abs. 3 Satz 2 DS-GVO steht dieses Recht aber dann
            nicht zur Verfügung, wenn die Datenverarbeitung der Wahrnehmung
            öffentlicher Aufgaben dient. Dies ist beim BMJV nur dann nicht der
            Fall, wenn die Verarbeitung personenbezogener Daten zu fiskalischen
            Zwecken erfolgt.
          </p>
          <h4 class="ris-subhead-bold">
            VI.6. Recht auf Widerspruch (Art. 21 DS-GVO)
          </h4>
          <p>
            Das Recht auf Widerspruch beinhaltet die Möglichkeit für den
            Betroffenen oder die Betroffene, in einer besonderen Situation der
            weiteren Verarbeitung seiner oder ihrer personenbezogenen Daten zu
            widersprechen, soweit diese durch die Wahrnehmung öffentlicher
            Aufgaben oder öffentlicher sowie privater Interessen gerechtfertigt
            ist. Das Recht gilt gemäß § 36 BDSG nicht, wenn eine öffentliche
            Stelle durch Rechtsvorschrift zur Verarbeitung verpflichtet ist.
          </p>
          <h4 class="ris-subhead-bold">
            VI.7. Recht auf Beschwerde (Art. 77 DS-GVO)
          </h4>
          <p>
            Der oder die Betroffene hat die Möglichkeit, sich mit einer
            Beschwerde gegen die Datenverarbeitung des BMJV an die
            Bundesbeauftragte für den Datenschutz und die Informationsfreiheit
            zu wenden, die die datenschutzrechtliche Aufsicht über die
            Bundesbehörden hat. Sie ist wie folgt erreichbar:
          </p>
          <p class="mt-16">
            <span class="underline"
              >Bundesbeauftragte für Datenschutz und Informationsfreiheit</span
            ><br />Graurheindorfer Str. 153 - 53117 Bonn<br />E-Mail:
            <a href="mailto:poststelle@bfdi.bund.de" class="ris-link1-regular"
              >poststelle@bfdi.bund.de</a
            >
          </p>
        </section>
      </div>
    </NuxtLayout>
  </div>
</template>

<style scoped>
@reference "~/assets/main.css";

h2,
h3,
h4 {
  @apply mt-32 mb-8;
}
</style>
