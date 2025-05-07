<script setup lang="ts">
import FeedbackForm from "~/components/Analytics/FeedbackForm.vue";
</script>

<template>
  <footer class="flex flex-col gap-32 px-16 py-32">
    <client-only>
      <FeedbackForm />
    </client-only>
    <hr />
    <div class="grid grid-cols-1 gap-48 md:grid-cols-2 lg:grid-cols-3">
      <div class="space-y-16">
        <p class="font-bold text-gray-800">Suche</p>
        <ul class="space-y-16">
          <li><NuxtLink href="/">Einfache Suche</NuxtLink></li>
          <li><NuxtLink href="/advanced-search">Erweiterte Suche</NuxtLink></li>
          <li><a href="#">Tipps & Tricks zur Bedienung</a></li>
        </ul>
      </div>
      <div class="space-y-16">
        <p class="font-bold text-gray-800">Informationen</p>
        <ul class="space-y-16">
          <li><NuxtLink href="/about">Über die Webseite</NuxtLink></li>
          <li><a href="#">Häufig gestellte Fragen</a></li>
          <li><a href="#">Glossar</a></li>
        </ul>
      </div>
      <div class="space-y-16">
        <p class="font-bold text-gray-800">Ressourcen</p>
        <ul class="space-y-16">
          <li>
            <ExternalLink
              url="https://neuris-portal-api-docs-production.obs-website.eu-de.otc.t-systems.com/en/"
              >API Dokumentation</ExternalLink
            >
          </li>
          <li>
            <NuxtLink href="/opensource">Open Source</NuxtLink>
          </li>
        </ul>
      </div>
      <div class="space-y-16">
        <p class="font-bold text-gray-800">Support & Feedback</p>
        <ul class="space-y-16 text-blue-800">
          <li><NuxtLink to="/feedback">Feedback geben</NuxtLink></li>
          <li><NuxtLink to="/barrierefreiheit">Barrierefreiheit</NuxtLink></li>
          <li><a href="#">Barriere melden</a></li>
        </ul>
      </div>
      <div class="space-y-16">
        <p class="font-bold text-gray-800">Rechtliches</p>
        <ul class="space-y-16">
          <li><NuxtLink to="/datenschutz">Datenschutz</NuxtLink></li>
          <li><NuxtLink to="/impressum">Impressum</NuxtLink></li>
          <li>
            <NuxtLink to="/cookie-einstellungen">Cookie-Einstellungen</NuxtLink>
          </li>
        </ul>
      </div>
      <div class="space-y-16">
        <p class="font-bold text-gray-800">Weitere Rechtsquellen</p>
        <ul class="space-y-16">
          <li>
            <ExternalLink
              url="https://www.justiz.de/onlinedienste/bundesundlandesrecht/index.php"
              >Landesrecht der Bundesländer</ExternalLink
            >
          </li>
          <li>
            <ExternalLink
              url="https://www.justiz.de/onlinedienste/rechtsprechung/index.php"
              >Gerichtsentscheidungen der Bundesländer</ExternalLink
            >
          </li>
          <li>
            <ExternalLink url="https://www.recht.bund.de/"
              >Bundesgesetzblatt</ExternalLink
            >
          </li>
          <li class="inline-block">
            <ExternalLink url="https://n-lex.europa.eu/n-lex/index?lang=de"
              >N-Lex</ExternalLink
            >
            <span class="ris-label2-regular"
              >&nbsp;Nationales Recht der EU-Länder</span
            >
          </li>
        </ul>
      </div>
    </div>
    <div class="mt-24 flex justify-between">
      <div>
        <p class="ris-body2-regular">
          Ein Projekt des
          <ExternalLink url="https://www.bmj.de"
            >Bundesministeriums der Justiz und für
            Verbraucherschutz</ExternalLink
          >
          und des
          <ExternalLink url="https://www.bundesjustizamt.de"
            >Bundesamts für Justiz</ExternalLink
          >
        </p>
        <p class="ris-body2-regular">
          Umsetzung und Betrieb durch
          <ExternalLink url="https://digitalservice.bund.de"
            >DigitalService GmbH des Bundes</ExternalLink
          >
        </p>
      </div>
      <BackToTopLink />
    </div>
  </footer>
</template>
