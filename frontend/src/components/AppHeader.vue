<script setup lang="ts">
import IcBaselineClose from "~icons/ic/baseline-close";
import IcBaselineMenu from "~icons/ic/baseline-menu";

const open = ref(false);
const toggleMenu = () => {
  open.value = !open.value;
};
</script>
<template>
  <AppBanner />
  <header id="top" class="bg-white print:hidden">
    <div class="container mx-auto">
      <div class="flex flex-col gap-24 py-24">
        <div class="flex flex-row items-center justify-between gap-16">
          <a
            href="/"
            aria-label="Zur Startseite"
            class="text-20 group leading-none no-underline"
          >
            <span class="block font-bold">Rechtsinformationen</span>
            <span class="text-gray-900 group-hover:text-inherit"
              >des Bundes</span
            >
          </a>
          <div class="float-end lg:hidden">
            <button
              v-if="!open"
              id="mobile-menu"
              class="flex cursor-pointer items-center gap-4 hover:underline active:underline"
              @click="toggleMenu"
            >
              <IcBaselineMenu size="1.25em" />Menu
            </button>
            <button
              v-else
              id="mobile-menu-close"
              class="flex cursor-pointer items-center gap-4 hover:underline active:underline"
              :onclick="toggleMenu"
            >
              <IcBaselineClose size="1.25em" />
              Menu
            </button>
          </div>
          <AppHeaderNav class="hidden lg:inline-block" list-class="items-end" />
        </div>
        <AppHeaderNav
          v-if="open"
          class="inline-block items-center lg:hidden"
          list-class="items-center gap-y-8"
          @select-item="toggleMenu"
        />
      </div>
    </div>
  </header>
</template>
