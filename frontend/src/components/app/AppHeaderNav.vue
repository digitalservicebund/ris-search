<script lang="ts" setup>
import IconSearch from "~icons/ic/search";

defineProps<{ listClass: string }>();

defineEmits<{ selectItem: [] }>();
</script>

<template>
  <nav aria-label="Hauptnavigation">
    <ul
      class="flex flex-col justify-end gap-x-28 md:flex-row md:items-center"
      :class="listClass"
    >
      <li>
        <NuxtLink
          class="nav-link"
          :to="{ name: 'search' }"
          @click="$emit('selectItem')"
        >
          <IconSearch />Suche
        </NuxtLink>
      </li>
      <li>
        <NuxtLink
          class="nav-link"
          :to="{ name: 'feedback' }"
          @click="$emit('selectItem')"
        >
          Feedback geben
        </NuxtLink>
      </li>
      <li>
        <NuxtLink
          class="nav-link"
          :to="{ name: 'about' }"
          @click="$emit('selectItem')"
        >
          Über den Service
        </NuxtLink>
      </li>
    </ul>
  </nav>
</template>

<style lang="css" scoped>
@reference "~/assets/main.css";

.nav-link {
  @apply ris-link1-regular link-hover flex items-center gap-4 decoration-3 aria-[current=page]:underline;
}
</style>
