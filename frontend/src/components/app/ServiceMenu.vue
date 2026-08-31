<script lang="ts" setup>
import IcBaselineLocalLibrary from "~icons/ic/baseline-local-library";
import IcBaselineSignLanguage from "~icons/ic/baseline-sign-language";
defineProps<{ listClass: string }>();

defineEmits<{ selectItem: [] }>();

const privateFeaturesEnabled = usePrivateFeaturesFlag();
</script>

<template>
  <nav aria-label="Servicemenü">
    <ul :class="listClass">
      <li>
        <NuxtLink
          class="nav-link"
          :to="{ name: 'translations' }"
          @click="$emit('selectItem')"
        >
          English translations
        </NuxtLink>
      </li>
      <li>
        <NuxtLink
          class="nav-link"
          to="https://docs.rechtsinformationen.bund.de"
        >
          API-Dokumentation
        </NuxtLink>
      </li>
      <li v-if="privateFeaturesEnabled">
        <NuxtLink
          class="nav-link"
          :to="{ name: 'gebaerdensprache' }"
          @click="$emit('selectItem')"
        >
          <IcBaselineSignLanguage class="h-24 w-24" />Gebärdensprache
        </NuxtLink>
      </li>
      <li v-if="privateFeaturesEnabled">
        <NuxtLink
          class="nav-link"
          :to="{ name: 'leichte-sprache' }"
          @click="$emit('selectItem')"
        >
          <IcBaselineLocalLibrary class="h-24 w-24" />Leichte Sprache
        </NuxtLink>
      </li>
      <li>
        <NuxtLink
          class="nav-link"
          :to="{ name: 'kontakt' }"
          @click="$emit('selectItem')"
        >
          Kontakt
        </NuxtLink>
      </li>
    </ul>
  </nav>
</template>

<style scoped>
@reference "~/assets/main.css";

.nav-link {
  @apply ris-link2-regular 2xl:2xl:ris-label1-regular link-hover flex items-center gap-4 text-gray-900! aria-[current=page]:underline aria-[current=page]:decoration-[0.1875rem];
}
</style>
