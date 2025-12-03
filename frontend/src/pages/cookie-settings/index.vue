<script setup lang="ts">
import { storeToRefs } from "pinia";
import PrimeVueButton from "primevue/button";
import Message from "primevue/message";
import StaticPageWrapper from "~/components/CustomLayouts/StaticPageWrapper.vue";
import { useStaticPageSeo } from "~/composables/useStaticPageSeo";
import { usePostHogStore } from "~/stores/usePostHogStore";
import IconCheck from "~icons/ic/check";
import IconClose from "~icons/ic/close";

definePageMeta({ alias: ["/cookie-einstellungen"] });

const store = usePostHogStore();

if (import.meta.server) {
  const cookieHeader = useRequestHeaders(["cookie"]);
  const cookies = cookieHeader.cookie || "";
  const consentMatch = cookies.match(/consent_given=([^;]+)/);
  if (consentMatch) {
    const consentValue = consentMatch[1];
    store.userConsent = consentValue === "true";
  }
}

const isClient = ref(false);
onMounted(() => {
  store.initialize();
  isClient.value = true;
});

const { userConsent } = storeToRefs(store);
function handleSetTracking(value: boolean) {
  store.setTracking(value);
}

useStaticPageSeo("cookies");
</script>

<template>
  <StaticPageWrapper>
    <template #breadcrumb>
      <RisBreadcrumb :items="[{ label: 'Cookie-Einstellungen' }]" />
    </template>
    <div class="ris-body1-regular my-24 flex max-w-prose flex-col space-y-48">
      <h1 id="page-title" class="ris-heading1-regular mb-64">
        Cookie-Einstellungen
      </h1>
      <aside aria-label="Einstellungen ändern">
        <h2 class="ris-heading2-regular mb-40 hidden md:block">
          Sind Sie mit der Nutzung von Analyse-Cookies einverstanden?
        </h2>
        <div class="mb-40 w-fit" data-testid="consent-status-wrapper">
          <Message severity="info" class="ris-body2-regular mb-40 bg-white">
            <template #icon>
              <IconCheck v-if="userConsent" class="text-blue-800" />
              <IconClose v-else class="text-blue-800" />
            </template>
            <template v-if="isClient">
              <div v-if="userConsent">
                <p class="ris-body2-bold">
                  Ich bin mit der Nutzung von Analyse-Cookies einverstanden.
                </p>
                <p>Damit helfen Sie uns, das Portal weiter zu verbessern.</p>
              </div>
              <div v-else>
                <p class="ris-body2-bold">
                  Ich bin mit der Nutzung von Analyse-Cookies nicht
                  einverstanden.
                </p>
                <p>
                  Ihre Nutzung des Portals wird nicht zu Analysezwecken erfasst.
                </p>
              </div>
            </template>
            <template v-else>
              <div v-if="userConsent">
                <p class="ris-body2-bold">
                  Ich bin mit der Nutzung von System-Cookies einverstanden.
                </p>
                <p>
                  Wir verwenden aktuell keine Analyse-Cookies, weil JavaScript
                  ausgeschaltet ist.
                </p>
              </div>
              <div v-else>
                <p class="ris-body2-bold">
                  Ich bin mit der Nutzung von Analyse-Cookies nicht
                  einverstanden.
                </p>
                <p>
                  Ihre Nutzung des Portals wird nicht zu Analysezwecken erfasst.
                </p>
              </div>
            </template>
          </Message>
          <form
            v-if="userConsent"
            action="/api/cookie-consent"
            method="POST"
            @submit.prevent="handleSetTracking(false)"
          >
            <input type="hidden" name="consent" value="false" />
            <PrimeVueButton
              aria-label="Cookie-Ablehnen-Button"
              label="Cookies ablehnen"
              data-testid="settings-decline-cookie"
              type="submit"
            />
          </form>
          <form
            v-else
            action="/api/cookie-consent"
            method="POST"
            @submit.prevent="handleSetTracking(true)"
          >
            <input type="hidden" name="consent" value="true" />
            <PrimeVueButton
              aria-label="Cookie-Akzeptieren-Button"
              label="Cookies akzeptieren"
              data-testid="settings-accept-cookie"
              type="submit"
            />
          </form>
        </div>
      </aside>
      <div class="mb-80 max-w-prose space-y-64">
        <p>
          Nachfolgend informieren wir Sie darüber, welche Cookies auf der
          Webseite
          <span class="ris-body1-bold">
            testphase.rechtsinformationen.bund.de
          </span>
          eingesetzt werden. Cookies sind kleine Textdateien, die auf dem
          Datenträger des Nutzenden gespeichert werden und über den Browser
          bestimmte Einstellungen und Daten mit dem System des BMJ austauschen.
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
              Das Consent Management Cookie speichert Ihre Cookie-Einstellungen
              und somit, ob Sie dem Setzen eines Analyse-Cookies (Ziffer 2,
              s.u.) zugestimmt haben oder nicht.
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
              Das Consent Management Cookie speichert in Ihrem Browser die
              Einstellung, ob Sie der Verwendung von Analyse-Cookies zugestimmt
              haben oder nicht. Der Consent-Cookie hat eine maximale
              Speicherdauer von 365 Tagen. Der Consent-Cookie kann jederzeit
              über die Browser-Einstellungen gelöscht werden. Alternativ kann
              jederzeit die Vorauswahl über die Cookie-Einstellungen auf der
              Webseite geändert werden.
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
            der Sitzungsdaten wird die Webanalysedienst-Software von PostHog,
            Market Street, San Francisco, CA 94114, USA, verwendet. Weitere
            Informationen zum Datenschutz finden Sie in der
            <ExternalLink
              url="https://posthog.com/privacy"
              class="ris-link1-regular"
              >Datenschutzerklärung des Dienstleisters PostHog</ExternalLink
            >. Ihre Daten werden wie folgt verarbeitet:
          </p>
          <div>
            <p class="ris-body1-bold">
              Welche Kategorien von personenbezogenen Daten werden verarbeitet?
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
    </div>
  </StaticPageWrapper>
</template>

<style scoped>
@reference "~/assets/main.css";
.consent-status {
  @apply ris-heading3-regular flex flex-row items-center space-x-8;
}
.consent-status-wrapper {
  @apply flex flex-col space-y-32;
}
</style>
