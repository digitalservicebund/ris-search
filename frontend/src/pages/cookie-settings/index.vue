<script setup lang="ts">
import { useStaticPageSeo } from "~/composables/useStaticPageSeo";

definePageMeta({ alias: ["/cookie-einstellungen"], layout: false });

useStaticPageSeo("cookies");
</script>

<template>
  <div>
    <NuxtLayout name="static-page">
      <template #breadcrumb>
        <Breadcrumbs :items="[{ label: 'Cookie-Einstellungen' }]" />
      </template>

      <template #pageTitle>Cookie-Einstellungen</template>

      <div class="flex flex-col gap-24">
        <aside aria-label="Einstellungen ändern">
          <h2 class="ris-heading2-regular mb-24 hidden md:block">
            Sind Sie mit der Nutzung von Analyse-Cookies einverstanden?
          </h2>
          <StaticContentConsentStatus />
        </aside>

        <div class="space-y-64">
          <p>
            Nachfolgend informieren wir Sie darüber, welche Cookies auf der
            Webseite
            <span class="ris-body1-bold">
              testphase.rechtsinformationen.bund.de
            </span>
            eingesetzt werden. Cookies sind kleine Textdateien, die auf dem
            Datenträger des Nutzenden gespeichert werden und über den Browser
            bestimmte Einstellungen und Daten mit dem System des BMJ
            austauschen.
          </p>
          <div class="space-y-24">
            <h2 class="ris-heading2-regular">
              Notwendige (essenzielle) Cookies
            </h2>
            <p>
              Im Rahmen des Projekts „Testphase Rechtsinformationsportal“ setzen
              wir für die Funktion der Webseite sogenannte notwendige
              (essenzielle) Cookies. Diese essenziellen Cookies sind für den
              Betrieb der Webseite erforderlich und können nicht deaktiviert
              werden. Sie gewährleisten grundlegende Funktionen wie die
              Navigation auf der Seite und den Zugriff auf geschützte Bereiche.
              Durch die Nutzung unserer Webseite erklären Sie sich mit dem
              Einsatz dieser notwendigen Cookies einverstanden. Folgende Cookies
              kommen zum Einsatz:
            </p>
            <h3 class="ris-heading3-regular">Consent Management Tool Cookie</h3>
            <div>
              <p class="ris-body1-bold">
                Wessen Daten werden verarbeitet? (Kategorien von betroffenen
                Personen)
              </p>
              <p>Nutzende der Webseite testphase.rechtsinformationen.bund.de</p>
            </div>
            <div>
              <p class="ris-body1-bold">
                Welche Kategorien von personenbezogenen Daten werden
                verarbeitet?
              </p>
              <p>
                Eindeutiges Identifizierungskennzeichen zur Wiedererkennung der
                Nutzenden von testphase.rechtsinformationen.bund.de
              </p>
            </div>
            <div>
              <p class="ris-body1-bold">
                Warum werden die Daten verarbeitet? (Zwecke der Verarbeitung)
              </p>
              <p>
                Das Consent Management Cookie speichert Ihre
                Cookie-Einstellungen und somit, ob Sie dem Setzen eines
                Analyse-Cookies (Ziffer 2, s.u.) zugestimmt haben oder nicht.
              </p>
            </div>
            <div>
              <p class="ris-body1-bold">Was ist die Rechtsgrundlage?</p>
              <p>
                Die Datenverarbeitung erfolgt auf Grundlage des Art. 6 Abs. 1
                UAb. 1 lit. e) und Abs. 3 lit. b) DS-GVO in Verbindung mit § 3
                E-Government-Gesetz (EGovG) (vgl. auch § 25 Abs. 2 Nr. 2 des
                Gesetzes über den Datenschutz und den Schutz der Privatsphäre in
                der Telekommunikation bei digitalen Diensten (TDDDG)) im Rahmen
                der Öffentlichkeitsarbeit zur bedarfsorientierten Bereitstellung
                von Informationen zu den dem BMJ übertragenen Aufgaben.
              </p>
            </div>
            <div>
              <p class="ris-body1-bold">
                Wie lange werden die Daten gespeichert?
              </p>
              <p>
                Das Consent Management Cookie speichert in Ihrem Browser die
                Einstellung, ob Sie der Verwendung von Analyse-Cookies
                zugestimmt haben oder nicht. Der Consent-Cookie hat eine
                maximale Speicherdauer von 365 Tagen. Der Consent-Cookie kann
                jederzeit über die Browser-Einstellungen gelöscht werden.
                Alternativ kann jederzeit die Vorauswahl über die
                Cookie-Einstellungen auf der Webseite geändert werden.
              </p>
            </div>
          </div>
          <div class="space-y-24">
            <h2 class="ris-heading2-regular">Analyse-Cookie</h2>
            <p>
              Zur Verbesserung und Analyse des Verhaltens der Nutzenden setzen
              wir einen Analyse-Cookie, der uns Informationen über die Nutzung
              unserer Webseite liefert (Sitzungsdaten). Dies hilft uns, das
              Verhalten der Nutzenden zu verstehen und unsere Inhalte
              kontinuierlich zu verbessern. Durch die Einwilligung zur
              Verwendung des Analyse-Cookies helfen Sie uns, die Webseite für
              Sie und andere Nutzende noch benutzerfreundlicher zu gestalten.
              Für die Erhebung der Sitzungsdaten wird die
              Webanalysedienst-Software von PostHog, Market Street, San
              Francisco, CA 94114, USA, verwendet. Weitere Informationen zum
              Datenschutz finden Sie in der
              <ExternalLink
                url="https://posthog.com/privacy"
                class="ris-link1-regular"
                >Datenschutzerklärung des Dienstleisters PostHog</ExternalLink
              >. Ihre Daten werden wie folgt verarbeitet:
            </p>
            <div>
              <p class="ris-body1-bold">
                Welche Kategorien von personenbezogenen Daten werden
                verarbeitet?
              </p>
              <p>Daten zum Nutzungsverhalten</p>
              <ul class="list-bullet mt-16">
                <li>Besuchte Seiten</li>
                <li>Dauer des Besuchs</li>
                <li>Bewegungs-, Klick- und Scrollverhalten</li>
              </ul>
              <p class="mt-16">Technische Daten</p>
              <ul class="list-bullet mt-16">
                <li>IP-Adresse</li>
                <li>Betriebssystem</li>
                <li>Browser und Browserversion</li>
                <li>Gerätetyp</li>
                <li>Bildschirmauflösung</li>
              </ul>
              <p class="mt-16">Metadaten</p>
              <ul class="list-bullet mt-16">
                <li>Zeitpunkt des Besuchs</li>
              </ul>
              <p class="mt-16">Verlaufsdaten</p>
              <ul class="list-bullet mt-16">
                <li>
                  Verweisende Seite (von welcher Seite sind Sie auf diese Seite
                  gekommen)
                </li>
              </ul>
            </div>
            <div>
              <p class="ris-body1-bold">
                Warum werden die Daten verarbeitet? (Zwecke der Verarbeitung)
              </p>
              <p>
                Die Daten werden verarbeitet, um die Inhalte kontinuierlich zu
                verbessern. Durch die Einwilligung zur Verwendung des
                Analyse-Cookies helfen Sie uns, die Webseite für Sie und andere
                Nutzende noch benutzerfreundlicher zu gestalten.
              </p>
            </div>
            <div>
              <p class="ris-body1-bold">Was ist die Rechtsgrundlage?</p>
              <p>
                Die Verarbeitung erfolgt auf Grundlage des Art. 6 Abs. 1 UAbs. 1
                lit. a) DS-GVO in Verbindung mit § 25 Abs. 1 des Gesetzes über
                den Datenschutz und den Schutz der Privatsphäre in der
                Telekommunikation bei digitalen Diensten (TDDDG).
              </p>
            </div>
            <div>
              <p class="ris-body1-bold">
                Wie lange werden die Daten gespeichert?
              </p>
              <p>
                Es werden keine personenbezogenen Daten im Zuge der Webanalyse
                gespeichert. Mit Beginn der Webanalyse wird die IP-Adresse
                erhoben und unmittelbar in ein anonymisiertes Format
                umgewandelt. Ab diesem Zeitpunkt können die Analysedaten Ihnen
                nicht mehr zugeordnet werden.
              </p>
            </div>
            <div>
              <p class="ris-body1-bold">
                Werden Daten an Dritte weitergegeben?
              </p>
              <p>
                Personenbezogene Daten werden ausschließlich in der Europäischen
                Union/dem Europäischen Wirtschaftsraum verarbeitet und nicht an
                Dritte weitergeleitet.
              </p>
              <p>
                Die von dem Analyse-Cookie im Zuge der Webanalyse erhobene
                IP-Adresse wird insofern innerhalb der Europäischen Union/dem
                Europäischen Wirtschaftsraum gekürzt und unmittelbar in ein
                anonymisiertes Format umgewandelt. Ab diesem Zeitpunkt können
                die Analysedaten Ihnen nicht mehr zugeordnet werden.
              </p>
            </div>
          </div>
        </div>
      </div>
    </NuxtLayout>
  </div>
</template>

