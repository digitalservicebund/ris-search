<script setup lang="ts">
import bmjvLogo from "../assets/images/bmjv_logo.svg";
</script>

<template>
  <footer>
    <div class="container">
      <div class="branding">
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
                href="https://testphase.rechtsinformationen.bund.de/datenschutz"
                >Datenschutzerklärung</a
              >
            </li>
          </ul>
        </div>
      </div>
      <div class="container-2">
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

<style scoped>
.container {
  display: flex;
  flex-direction: column;
}

.branding {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: start;
  flex-wrap: wrap;
}

.branding img {
  padding-bottom: 16px;
}

.vp-doc ul {
  margin: 0;
  padding: 0;
}

.branding ul {
  list-style: none;
  padding-bottom: 16px;
}

.container-2 {
  display: flex;
  flex-direction: column;
}

.container-2 p {
  margin: 0;
}
</style>
