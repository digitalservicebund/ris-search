<script lang="ts" setup>
defineProps<{ listClass: string }>();

defineEmits<{ selectItem: [] }>();

const privateFeaturesEnabled = usePrivateFeaturesFlag();
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
          Suche
        </NuxtLink>
      </li>
      <li>
        <NuxtLink
          v-if="privateFeaturesEnabled"
          class="nav-link"
          :to="{ name: 'erweiterte-suche' }"
          @click="$emit('selectItem')"
        >
          Erweiterte Suche
        </NuxtLink>
        <NuxtLink
          v-else
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
