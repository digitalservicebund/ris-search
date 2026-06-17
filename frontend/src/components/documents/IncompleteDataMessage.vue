<script setup lang="ts">
import { Message } from "primevue";
</script>

<template>
  <Message
    severity="warn"
    class="typo-body-regular max-w-prose"
    role="status"
    aria-live="off"
  >
    <p class="typo-body-bold mt-2">
      Dieser Service befindet sich in der Testphase.
    </p>
    <p>
      Der Datenbestand ist noch nicht vollständig und der Service in
      Entwicklung. Wir arbeiten an der Ergänzung und Darstellung aller Inhalte.
    </p>
  </Message>
</template>
