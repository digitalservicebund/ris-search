<script lang="ts" setup>
import { Popover, PopoverButton, PopoverPanel } from "@headlessui/vue";
import { computed } from "vue";
import Icon from "../components/Icon.vue";
import { useData } from "../composables/data";
import { prependBase } from "../utils";

const { theme, site } = useData();
const base = site.value.base || "/";
const nav = computed(() => theme.value.secondaryNav);
</script>

<template>
  <nav class="flex self-end justify-item-end py-8">
    <ol class="flex">
      <li v-for="item in nav" :key="item.text" class="hidden lg:inline-block">
        <a
          :href="prependBase(item.link, base)"
          class="text-neutral-secondary px-8 gap-4 flex flex-row items-center no-underline hover:underline active:underline"
        >
          <Icon :id="item.icon" v-if="!!item.icon" />
          {{ item.text }}
        </a>
      </li>
      <Popover as="li" class="lg:hidden" v-slot="{ open }">
        <PopoverButton
          class="text-gray-900 dark:text-gray-200 px-8 gap-4 flex flex-row items-center no-underline hover:underline active:underline"
        >
          <Icon id="more-horiz" size="1.25em" v-if="!open" />
          <Icon id="close" size="1.25em" v-if="open" />
        </PopoverButton>
        <PopoverPanel v-slot="{ close }">
          <ul
            class="flex flex-col gap-8 absolute z-10 left-0 right-0 mt-8 py-8 bg-gray-200 dark:bg-gray-800"
          >
            <li v-for="item in nav" :key="item.text">
              <a
                :href="prependBase(item.link, base)"
                class="text-gray-900 dark:text-gray-200 px-8 gap-4 flex flex-row items-center no-underline hover:underline"
                @click="close"
              >
                <Icon :id="item.icon" v-if="!!item.icon" />
                {{ item.text }}
              </a>
            </li>
          </ul>
        </PopoverPanel>
      </Popover>
    </ol>
  </nav>
</template>
