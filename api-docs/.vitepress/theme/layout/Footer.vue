<script setup lang="ts">
import bmjvLogo from "../assets/images/bmjv_logo.svg";
import FeedbackForm from "../components/FeedbackForm.vue";
import {useRoute} from "vitepress";
import IcBaselineArrowOutward from "~icons/ic/baseline-arrow-outward?width=14&height=14";
</script>

<template>
  <hr class="border-gray-400 dark:border-neutral-tertiary"/>
  <footer
    class="space-y-32"
  >
    <div class="container flex flex-col gap-32 ">
      <FeedbackForm v-if="!useRoute().path.includes('feedback')"/>
    </div>
    <hr class="border-gray-400 dark:border-neutral-tertiary"/>

    <div class="container py-32 grid grid-cols-1 gap-48 md:grid-cols-2">
      <div class="space-y-16">
        <img
            class="mt-4 self-start"
            :src="bmjvLogo"
            alt="Bundesministerium der Justiz und für Verbraucherschutz"
        />
        <div>
          <p class="ris-body2-regular">
            Ein Service im Auftrag des
            <a class="ris-link2-regular dark:text-brand-primary" href="https://www.bmj.de" target="_blank"
            >Bundesministeriums der Justiz und für Verbraucherschutz<IcBaselineArrowOutward class="ml-[0.5ch] inline align-super" /></a
            >
          </p>
          <p class="ris-body2-regular">
            Umsetzung und Betrieb durch
            <a
                class="ris-link2-regular dark:text-brand-primary"
                href="https://digitalservice.bund.de"
                target="_blank"
            >DigitalService GmbH des Bundes<IcBaselineArrowOutward class="ml-[0.5ch] inline align-super" /></a
            >
          </p>
        </div>
      </div>
      <div class="space-y-16">
        <ul class="grid grid-flow-col grid-cols-2 grid-rows-5 gap-8 text-blue-800 dark:text-brand-primary text-sm">
          <li><a href="https://testphase.rechtsinformationen.bund.de/kontakt">Kontakt</a></li>
          <li><a href="https://testphase.rechtsinformationen.bund.de/impressum">Impressum</a></li>
          <li>
            <a href="https://testphase.rechtsinformationen.bund.de/datenschutz">Datenschutzerklärung</a>
          </li>
        </ul>
      </div>
    </div>
  </footer>
</template>
