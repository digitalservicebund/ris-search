<script setup lang="ts">
import IconWarningAmber from "~icons/ic/outline-warning-amber";
</script>
<template>
  <div
    role="alert"
    class="ris-body2-regular flex bg-yellow-200 px-8 py-16 text-pretty sm:px-16 print:hidden"
  >
    <div class="container flex gap-8">
      <IconWarningAmber class="shrink-0" />
      <div>
        <p>
          <span class="ris-body2-bold">
            Dieser Service befindet sich in der Testphase.
          </span>
          Testen Sie erste Funktionen und geben Sie Feedback für die
          Weiterentwicklung.
          <NuxtLink class="ris-link2-regular" :to="{ name: 'about' }">
            Erfahren Sie mehr über die Testphase
          </NuxtLink>
        </p>
      </div>
    </div>
  </div>
</template>
