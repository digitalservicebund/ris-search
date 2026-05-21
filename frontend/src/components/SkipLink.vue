<script setup lang="ts">
import { Button } from "primevue";
import { NuxtLink } from "#components";

const props = defineProps<{
  to: SkipLink["to"];
}>();

const { skipLinkTarget } = useCssModule();

const route = useRoute();

async function focusTarget() {
  await nextTick();

  const target = document.getElementById(props.to.slice(1));
  if (!target) return;

  // Make the item focusable in case it isn't, add a class preventing outlines
  // from showing up. Note that this is a deliberately naive implementation
  // that does all we need it to do for now, but has limitateions:
  //
  // - Classes are not removed again when focus changes - assumes skip links
  //   are fixed per page and don't change
  // - Assumes skip links only target non-interactive elements such as
  //   containers
  target.setAttribute("tabindex", target.getAttribute("tabindex") || "-1");
  target.classList.add(skipLinkTarget);
  target.focus({ preventScroll: true });
}

// Clone the route so search params are retained while navigating
const linkTarget = computed(() => ({ ...route, hash: props.to }));
</script>

<template>
  <Button
    :as="NuxtLink"
    :to="linkTarget"
    class="not-focus:sr-only focus-visible:outline-none!"
    severity="warn"
    @click="focusTarget"
  >
    <slot />
  </Button>
</template>

<style module>
.skipLinkTarget:focus-visible {
  outline: none;
}
</style>
