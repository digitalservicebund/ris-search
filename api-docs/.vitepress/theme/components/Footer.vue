<script setup lang="ts">
import bmjvLogo from "../assets/images/bmjv_logo.svg";
</script>

<template>
  <footer>
    <div :class="$style.container">
      <div :class="$style.branding">
        <img
          :src="bmjvLogo"
          alt="Bundesministerium der Justiz und für Verbraucherschutz"
        />
        <div>
          <ul>
            <li>
              <a href="https://testphase.rechtsinformationen.bund.de/kontakt"
                >Kontakt</a
              >
            </li>
            <li>
              <a href="https://testphase.rechtsinformationen.bund.de/impressum"
                >Impressum</a
              >
            </li>
            <li>
              <a
                href="https://testphase.rechtsinformationen.bund.de/datenschutzerklaerung"
                >Datenschutzerklärung</a
              >
            </li>
          </ul>
        </div>
      </div>
      <div>
        <p>
          Ein Service im Auftrag des
          <a href="https://www.bmj.de" target="_blank"
            >Bundesministeriums der Justiz und für Verbraucherschutz</a
          >
        </p>
        <p>
          Umsetzung und Betrieb durch
          <a href="https://digitalservice.bund.de" target="_blank"
            >DigitalService GmbH des Bundes</a
          >
        </p>
      </div>
    </div>
  </footer>
</template>

<style module>
.container {
  display: flex;
  flex-direction: column;
  font-size: 0.875rem;

  & p {
    margin: 0;
  }
}

.branding {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  flex-wrap: wrap;

  & img {
    padding-block-end: 1rem;
  }

  & ul {
    list-style: none;
    padding-block-end: 1rem;
    padding-inline-start: 0;
    margin-block: 0;
  }
}
</style>
