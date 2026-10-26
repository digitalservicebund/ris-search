<script setup lang="ts">
import type { Zitierungen } from "~/utils/zitierungen";

const { zitierungen } = defineProps<{ zitierungen: Zitierungen }>();
</script>

<template>
  <div class="content-grid gap-y-32 hyphens-auto">
    <div
      v-if="zitierungen.passivRechtsprechung || zitierungen.passivLiteratur"
      class="col-span-12 md:col-span-8 xl:col-span-7"
    >
      <h3 class="typo-headline3-regular pb-24">
        Diese Entscheidung wird zitiert in
      </h3>
      <div class="flex flex-col gap-24">
        <div v-if="zitierungen.passivRechtsprechung">
          <h4 class="typo-headline3-bold pb-16">Gerichtsentscheidungen</h4>
          <div v-html="zitierungen.passivRechtsprechung" class="zitierungen" />
        </div>
        <div v-if="zitierungen.passivLiteratur">
          <h4 class="typo-headline3-bold pb-16">Literaturnachweise</h4>
          <div v-html="zitierungen.passivLiteratur" class="zitierungen" />
        </div>
      </div>
    </div>
    <div
      v-if="zitierungen.aktivRechtsprechung"
      class="col-span-12 md:col-span-8 xl:col-span-7"
    >
      <h3 class="typo-headline3-regular pb-24">Diese Entscheidung zitiert</h3>
      <h4 class="typo-headline3-bold pb-16">Gerichtsentscheidungen</h4>
      <div v-html="zitierungen.aktivRechtsprechung" class="zitierungen" />
    </div>
  </div>
</template>

<style scoped>
@reference "~/assets/main.css";

.zitierungen {
  :deep(a) {
    @apply typo-link1-regular;
  }

  :deep(ul) {
    @apply flex flex-col gap-8;
  }

  :deep(li) {
    @apply typo-label1-regular wrap-break-word;
  }
}
</style>
