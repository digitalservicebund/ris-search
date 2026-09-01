<script setup lang="ts">
import IcBaselineClose from "~icons/ic/baseline-close";
import IcBaselineMenu from "~icons/ic/baseline-menu";

const open = ref(false);

function toggleMenu() {
  open.value = !open.value;
}

const mobileMainMenuId = useId();
const mobileServiceMenuId = useId();
</script>

<template>
  <header
    id="top"
    class="flex flex-col border-b border-b-gray-400 bg-white print:hidden"
  >
    <AppBanner />
    <AppServiceMenu
      class="content-gutters hidden py-16 md:flex md:justify-end"
      list-class="flex flex-row items-center gap-24"
      @select-item="toggleMenu()"
    />
    <nav class="flex flex-col pt-16 md:gap-24 md:pt-0" aria-label="Hauptmenü">
      <!-- Desktop nav -->
      <div
        class="content-gutters flex items-center justify-between gap-16 pb-16"
      >
        <AppLogo />

        <!-- Mobile menu toggle -->
        <div class="float-end md:hidden">
          <button
            type="button"
            class="ris-label3-regular inline-flex cursor-pointer flex-col items-center gap-4 text-[0.875rem] text-blue-800 outline-offset-4 outline-blue-800 focus-visible:outline-4"
            :aria-expanded="open"
            :aria-controls="`${mobileMainMenuId} ${mobileServiceMenuId}`"
            @click="toggleMenu()"
          >
            <IcBaselineMenu v-if="!open" size="1.25em" />
            <IcBaselineClose v-else size="1.25em" />
            Menü
          </button>
        </div>

        <AppMainMenu
          class="hidden md:inline-block"
          list-class="flex justify-end gap-x-28 flex-row items-center"
        />
      </div>

      <!-- Mobile nav -->
      <AppMainMenu
        :id="mobileMainMenuId"
        :hidden="!open"
        data-testid="mobile-main-menu"
        class="inline-block border-t border-gray-400 py-24 md:hidden"
        list-class="content-gutters flex flex-col items-start gap-16"
        @select-item="toggleMenu()"
      />
    </nav>
    <AppServiceMenu
      :id="mobileServiceMenuId"
      :hidden="!open"
      data-testid="mobile-service-menu"
      class="inline-block border-t border-gray-400 py-24 md:hidden"
      list-class="content-gutters flex flex-col items-start gap-16"
      @select-item="toggleMenu()"
    />
  </header>
</template>
