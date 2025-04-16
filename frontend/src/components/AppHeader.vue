<script setup lang="ts">
import IcBaselineMenu from "~icons/ic/baseline-menu";
import IcBaselineClose from "~icons/ic/baseline-close";

const open = ref(false);
const toggleMenu = () => {
  open.value = !open.value;
};
</script>
<template>
  <AppBanner />
  <header id="top" class="bg-white">
    <div class="container mx-auto">
      <div class="flex flex-col gap-24 py-24">
        <div class="flex flex-row items-center justify-between gap-16">
          <a
            href="/"
            aria-label="Zur Startseite"
            class="text-20 link-hover leading-none"
          >
            <span class="block font-bold">Rechtsinformationen</span>
            <span class="text-gray-900 hover:text-inherit">des Bundes</span>
          </a>
          <div class="float-end lg:hidden">
            <button
              v-if="!open"
              class="flex cursor-pointer items-center gap-4 hover:underline active:underline"
              @click="toggleMenu"
            >
              <IcBaselineMenu id="mobile-menu" size="1.25em" />Menu
            </button>
            <IcBaselineClose
              v-else
              id="mobile-menu-close"
              :onclick="toggleMenu"
              size="1.25em"
              class="my-4"
            />
          </div>
          <AppHeaderNav class="hidden lg:inline-block" list-class="items-end" />
        </div>
        <AppHeaderNav
          v-if="open"
          class="inline-block items-center lg:hidden"
          list-class="items-center gap-y-8"
        />
      </div>
    </div>
  </header>
</template>
