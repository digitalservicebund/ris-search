<script setup lang="ts">
import IconWarningAmber from "~icons/ic/outline-warning-amber";
</script>

<template>
  <div
    role="status"
    class="typo-label2-regular bg-yellow-200 py-16 text-pretty print:hidden"
  >
    <!-- Icon alignment mirrors UiMessage, see the comment there -->
    <div class="content-wrapper flex items-baseline gap-8">
      <span
        class="flex h-[1lh] flex-none items-center text-orange-700 before:content-['\200b'] [&>svg]:size-20"
      >
        <IconWarningAmber aria-hidden="true" />
      </span>
      <div>
        <p>
          <span class="typo-label2-bold">
            Dieser Service befindet sich in der Testphase.
          </span>
          Testen Sie erste Funktionen und geben Sie Feedback für die
          Weiterentwicklung.
          <NuxtLink class="typo-link2-regular" :to="{ name: 'ueber' }">
            Erfahren Sie mehr über die Testphase
          </NuxtLink>
        </p>
      </div>
    </div>
  </div>
</template>
