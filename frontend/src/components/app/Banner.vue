<script setup lang="ts">
import IconWarningAmber from "~icons/ic/outline-warning-amber";
</script>

<template>
  <div
    role="status"
    class="typo-body-regular bg-yellow-200 py-16 text-pretty print:hidden"
  >
    <div class="wrapper flex gap-8">
      <IconWarningAmber class="shrink-0" />
      <div>
        <p>
          <span class="typo-body-bold">
            Dieser Service befindet sich in der Testphase.
          </span>
          Testen Sie erste Funktionen und geben Sie Feedback für die
          Weiterentwicklung.
          <NuxtLink class="typo-link-regular" :to="{ name: 'about' }">
            Erfahren Sie mehr über die Testphase
          </NuxtLink>
        </p>
      </div>
    </div>
  </div>
</template>
