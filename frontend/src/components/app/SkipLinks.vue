<script setup lang="ts">
import type { SkipLink } from "~/types/router";

const props = defineProps<{
  /**
   * Use this to manually provide skip links to the component when
   * definePageMeta is not available (prefer definePageMeta if possible).
   */
  links?: SkipLink[];
}>();

const route = useRoute();
const router = useRouter();

const links = computed(() => [
  ...(route.meta.skipLinks ?? []),
  ...(props.links ?? []),
]);

const wrapper = useTemplateRef("wrapper");

const unregister = router.afterEach((to, from) => {
  if (to.path !== from.path) wrapper.value?.focus();
});

onUnmounted(unregister);
</script>

<template>
  <nav
    v-if="links.length"
    ref="wrapper"
    aria-label="Sprunglinks"
    tabindex="-1"
    class="pointer-events-none fixed inset-x-0 top-8 z-10 flex justify-center gap-8 px-8"
  >
    <SkipLink
      v-for="link in links"
      :key="link.label"
      :to="link.to"
      class="pointer-events-auto"
    >
      {{ link.label }}
    </SkipLink>
  </nav>
</template>
