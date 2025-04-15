<script setup lang="ts">
import IconCheck from "~icons/ic/check";
import IconClose from "~icons/ic/close";
import PrimeVueButton from "primevue/button";
import { usePostHogStore } from "~/stores/usePostHogStore";
import { storeToRefs } from "pinia";
import StaticPageWrapper from "~/components/CustomLayouts/StaticPageWrapper.vue";

useHead({ title: "Cookie-Einstellungen" });
definePageMeta({ alias: ["/cookie-einstellungen"] });

const store = usePostHogStore();
const { userConsent } = storeToRefs(store);
function handleSetTracking(value: boolean) {
  store.setTracking(value);
}
</script>

<template>
  <StaticPageWrapper>
    <RisBreadcrumb title="Cookie-Einstellungen" base-path="/" />
    <div class="ris-body1-regular my-24 flex flex-col space-y-48">
      <h1 class="ris-heading1-regular">Cookie-Einstellungen</h1>
      <client-only>
        <div v-if="userConsent == true" class="consent-status-wrapper">
          <div class="consent-status">
            <IconCheck class="text-blue-800" />
            <div>Es werden anonyme Nutzungsdaten gespeichert</div>
          </div>
          <div>
            <PrimeVueButton
              aria-label="Cookie-Ablehnen-Button"
              label="Anonyme Nutzungsdaten nicht speichern"
              data-testid="settings-decline-cookie"
              @click="handleSetTracking(false)"
            >
              <template #icon>
                <IconClose />
              </template>
            </PrimeVueButton>
          </div>
        </div>
        <div v-else class="consent-status-wrapper">
          <div class="consent-status">
            <IconClose class="text-blue-800" />
            <div>Es werden keine anonymen Nutzungsdaten gespeichert</div>
          </div>
          <div>
            <PrimeVueButton
              aria-label="Cookie-Akzeptieren-Button"
              label="Anonyme Nutzungsdaten speichern"
              data-testid="settings-accept-cookie"
              @click="handleSetTracking(true)"
            >
              <template #icon>
                <IconCheck />
              </template>
            </PrimeVueButton>
          </div>
        </div>
      </client-only>
      <div class="mb-80 max-w-prose space-y-64">
        <p>
          Nachfolgend informieren wir Sie darüber, welche Cookies auf der
          Webseite testphase.rechtsinformationen.bund.de eingesetzt werden.
          Cookies sind kleine Textdateien, die auf dem Datenträger des Nutzenden
          gespeichert werden und über den Browser bestimmte Einstellungen und
          Daten mit dem System des BMJ austauschen.
        </p>
        <div class="space-y-24">
          <h2 class="ris-heading2-regular">Notwendige (essenzielle) Cookies</h2>
          <p>
            Im Rahmen des Projekts „Testphase Rechtsinformationsportal“ setzen
            wir für die Funktion der Webseite sogenannte notwendige
            (essenzielle) Cookies. Diese essenziellen Cookies sind für den
            Betrieb der Webseite erforderlich und können nicht deaktiviert
            werden. Sie gewährleisten grundlegende Funktionen wie die Navigation
            auf der Seite und den Zugriff auf geschützte Bereiche. Durch die
            Nutzung unserer Webseite erklären Sie sich mit dem Einsatz dieser
            notwendigen Cookies einverstanden. Folgende Cookies kommen zum
            Einsatz:
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
              Welche Kategorien von personenbezogenen Daten werden verarbeitet?
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
              Das Consent Management Tool Cookie speichert Ihre
              Cookie-Einstellungen und somit, ob Sie dem Setzen eines
              Analyse-Cookies (Ziffer 2, s.u.) zugestimmt haben oder nicht.
            </p>
          </div>
          <div>
            <p class="ris-body1-bold">Was ist die Rechtsgrundlage?</p>
            <p>
              Die Datenverarbeitung erfolgt auf Grundlage des Art. 6 Abs. 1 UAb.
              1 lit. e) und Abs. 3 lit. b) DS-GVO in Verbindung mit § 3
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
              Das Consent Management Tool Cookie speichert in Ihrem Browser die
              Einstellung, ob Sie der Verwendung von Analyse-Cookies zugestimmt
              haben oder nicht. Das Consent Management Tool Cookie hat eine
              maximale Speicherdauer von 365 Tagen. Das Consent Management Tool
              Cookie kann jederzeit über die Browser-Einstellungen gelöscht
              werden. Alternativ kann jederzeit die Vorauswahl über die
              Cookie-Einstellungen auf der Webseite geändert werden.
            </p>
          </div>
        </div>
        <div class="space-y-24">
          <h2 class="ris-heading2-regular">Analyse-Cookie</h2>
          <p>
            Zur Verbesserung und Analyse des Verhaltens der Nutzenden setzen wir
            einen Analyse-Cookie, der uns Informationen über die Nutzung unserer
            Webseite liefert (Sitzungsdaten). Dies hilft uns, das Verhalten der
            Nutzenden zu verstehen und unsere Inhalte kontinuierlich zu
            verbessern. Durch die Einwilligung zur Verwendung des
            Analyse-Cookies helfen Sie uns, die Webseite für Sie und andere
            Nutzende noch benutzerfreundlicher zu gestalten. Für die Erhebung
            der Sitzungsdaten wird die Webanalysedienst-Software von Posthog,
            Market Street, San Francisco, CA 94114, USA, verwendet. Weitere
            Informationen zum Datenschutz dieses Dienstleisters finden Sie unter
            <a href="https://posthog.com/privacy" class="ris-link1-regular"
              >https://posthog.com/privacy</a
            >. Ihre Daten werden wie folgt verarbeitet:
          </p>
          <div>
            <p class="ris-body1-bold">
              Welche Kategorien von personenbezogenen Daten werden verarbeitet?
            </p>
            <p>Daten zum Nutzungsverhalten</p>
            <ul class="list-bullet">
              <li>Besuchte Seiten</li>
              <li>Dauer des Besuchs</li>
              <li>Bewegungs-, Klick- und Scrollverhalten</li>
            </ul>
            <p class="mt-16">Technische Daten</p>
            <ul class="list-bullet">
              <li>IP-Adresse</li>
              <li>Betriebssystem</li>
              <li>Browser und Browserversion</li>
              <li>Gerätetyp</li>
              <li>Bildschirmauflösung</li>
            </ul>
            <p class="mt-16">Metadaten</p>
            <ul class="list-bullet">
              <li>Zeitpunkt des Besuchs</li>
            </ul>
            <p class="mt-16">Verlaufsdaten</p>
            <ul class="list-bullet">
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
              lit. a) DS-GVO in Verbindung mit § 25 Abs. 1 des Gesetzes über den
              Datenschutz und den Schutz der Privatsphäre in der
              Telekommunikation bei digitalen Diensten (TDDDG).
            </p>
          </div>
          <div>
            <p class="ris-body1-bold">
              Wie lange werden die Daten gespeichert?
            </p>
            <p>
              Es werden keine personenbezogenen Daten im Zuge der Webanalyse
              gespeichert. Mit Beginn der Webanalyse wird die IP-Adresse erhoben
              und unmittelbar in ein anonymisiertes Format umgewandelt. Ab
              diesem Zeitpunkt können die Analysedaten Ihnen nicht mehr
              zugeordnet werden.
            </p>
          </div>
          <div>
            <p class="ris-body1-bold">Werden Daten an Dritte weitergegeben?</p>
            <p>
              Personenbezogene Daten werden ausschließlich in der Europäischen
              Union/dem Europäischen Wirtschaftsraum verarbeitet und nicht an
              Dritte weitergeleitet.
            </p>
            <p>
              Die von dem Analyse-Cookie im Zuge der Webanalyse erhobene
              IP-Adresse wird insofern innerhalb der Europäischen Union/dem
              Europäischen Wirtschaftsraum gekürzt und unmittelbar in ein
              anonymisiertes Format umgewandelt. Ab diesem Zeitpunkt können die
              Analysedaten Ihnen nicht mehr zugeordnet werden.
            </p>
          </div>
        </div>
      </div>
    </div></StaticPageWrapper
  >
</template>

<style scoped>
@reference "@/assets/main.css";
.consent-status {
  @apply ris-heading3-regular flex flex-row items-center space-x-8;
}
.consent-status-wrapper {
  @apply flex flex-col space-y-32;
}
</style>
