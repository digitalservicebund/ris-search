<script lang="ts" setup>
import IconSearch from "~icons/ic/search";

defineProps<{ listClass: string }>();

defineEmits<{ selectItem: [] }>();
</script>

<template>
  <div>
    <ul
      class="flex flex-col justify-end gap-x-28 md:flex-row md:items-center"
      :class="listClass"
    >
      <li>
        <NuxtLink
          class="nav-link"
          :to="{ name: 'suche' }"
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
          :to="{ name: 'ueber' }"
          @click="$emit('selectItem')"
        >
          Über den Service
        </NuxtLink>
      </li>
    </ul>
  </div>
</template>

<style scoped>
@reference "~/assets/main.css";

.nav-link {
  @apply typo-link-regular link-hover flex items-center gap-4 aria-[current=page]:underline aria-[current=page]:decoration-[0.1875rem];
}
</style>
