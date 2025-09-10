<script setup lang="ts">
import MaterialSymbolsWarningOutline from "~icons/material-symbols/warning-outline";
</script>
<template>
  <div
    role="alert"
    class="ris-body2-regular flex bg-yellow-200 px-8 py-12 sm:px-16 print:hidden"
  >
    <div class="container mx-auto flex gap-16">
      <MaterialSymbolsWarningOutline class="shrink-0" />
      <div>
        <p>
          <span class="ris-body2-bold"
            >Dieser Service befindet sich in der Testphase.</span
          >
          Testen Sie erste Funktionen und geben Sie Feedback für die
          Weiterentwicklung.
        </p>
        <NuxtLink class="ris-link2-regular text-black" to="/ueber"
          >Finden Sie heraus, was das bedeutet</NuxtLink
        >
      </div>
    </div>
  </div>
</template>
