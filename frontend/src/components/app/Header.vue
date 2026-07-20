<script setup lang="ts">
import IcBaselineClose from "~icons/ic/baseline-close";
import IcBaselineMenu from "~icons/ic/baseline-menu";

const open = ref(false);

function toggleMenu() {
  open.value = !open.value;
}

const mobileMenuId = useId();
</script>

<template>
  <header id="top" class="border-b border-b-gray-400 bg-white print:hidden">
    <AppBanner />
    <nav
      class="content-wrapper flex flex-col gap-24 py-20"
      aria-label="Hauptmenü"
    >
      <div class="flex items-center justify-between gap-16">
        <AppLogo />

        <!-- Mobile menu toggle -->
        <div class="float-end lg:hidden">
          <button
            type="button"
            class="typo-label1-regular hover:typo-link-regular flex cursor-pointer items-center gap-8 outline-offset-4 outline-blue-800 focus-visible:outline-4"
            :aria-expanded="open"
            :aria-controls="mobileMenuId"
            @click="toggleMenu()"
          >
            <IcBaselineMenu v-if="!open" size="1.25em" />
            <IcBaselineClose v-else size="1.25em" />
            Menü
          </button>
        </div>

        <!-- Desktop nav -->
        <AppHeaderNav class="hidden lg:inline-block" list-class="items-end" />
      </div>

      <!-- Mobile nav -->
      <AppHeaderNav
        :id="mobileMenuId"
        :hidden="!open"
        data-testid="mobile-nav"
        class="inline-block items-center lg:hidden"
        list-class="items-center gap-y-8"
        @select-item="toggleMenu()"
      />
    </nav>
  </header>
</template>
