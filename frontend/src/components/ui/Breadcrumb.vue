<script setup lang="ts" generic="T extends BreadcrumbItem">
import { tw } from "../../utils/tags";

export type BreadcrumbItem = {
  label: string;
  url?: string;
};

const { model = [] } = defineProps<{
  model?: T[];
}>();

// Classes ------------------------------------------------

const list = tw`m-0 flex flex-wrap items-center gap-x-2 gap-y-4 p-0 md:flex-nowrap`;

const item = tw`ris-label2-regular [&>a]:ris-link2-regular flex flex-nowrap items-center text-gray-900 *:line-clamp-1 md:flex-none md:last:flex-auto [&>a]:not-hover:no-underline`;

const separator = tw`flex items-center text-gray-800`;
</script>

<template>
  <nav>
    <ol :class="list">
      <template v-for="(breadcrumbItem, index) in model" :key="index">
        <li :class="item">
          <slot name="item" :item="breadcrumbItem" :index="index">{{
            breadcrumbItem.label
          }}</slot>
        </li>

        <li
          v-if="index < model.length - 1"
          :class="separator"
          aria-hidden="true"
        >
          <slot name="separator">/</slot>
        </li>
      </template>
    </ol>
  </nav>
</template>
