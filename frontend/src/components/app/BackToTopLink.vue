<script setup lang="ts">
import { Button } from "primevue";
import IcBaselineKeyboardArrowUp from "~icons/ic/baseline-keyboard-arrow-up";

const scrollTarget = "top";

function scrollToTop() {
  document.getElementById(scrollTarget)?.scrollIntoView({ behavior: "smooth" });
}
</script>

<template>
  <div class="back-to-top-link">
    <Button
      :href="`#${scrollTarget}`"
      aria-label="Zum Seitenanfang"
      as="a"
      rounded
      severity="secondary"
      @click.prevent="scrollToTop"
    >
      <template #icon>
        <IcBaselineKeyboardArrowUp />
      </template>
    </Button>
  </div>
</template>

<style scoped>
@reference "~/assets/main.css";

.back-to-top-link {
  @apply fixed right-16 bottom-(--offset-bottom) [--offset-bottom:1rem];
}

:root:has([data-back-to-top-adjust="drawer"]) {
  .back-to-top-link {
    /* drawer height + 1rem on mobile */
    @apply [--offset-bottom:4.875rem] md:[--offset-bottom:1rem];
  }
}

@supports (animation-range: normal) {
  .back-to-top-link {
    @apply animate-[slide-in_linear_both] [animation-range:normal_8rem] [animation-timeline:scroll(root)];
  }

  @keyframes slide-in {
    from {
      translate: 0 calc(100% + var(--offset-bottom));
    }
    to {
      translate: 0 0;
    }
  }
}
</style>
