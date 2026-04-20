<script setup lang="ts">
import { computed, useSlots } from "vue";
import { data } from "../classes.data";
import ClassHeader from "./ClassHeader.vue";
import ClassProperty from "./ClassProperty.vue";

const props = defineProps<{ name: string; class?: string }>();
const cls = computed(() => data[props.name]);
const slots = useSlots();
</script>

<template>
  <div
    class="border border-gray-400 rounded overflow-x-hidden"
    :class="props.class"
  >
    <template v-if="!!cls">
      <dl class="divide-y divide-gray-400">
        <ClassHeader :cls="cls" />
        <template v-for="prop in cls.props" :key="prop.id">
          <ClassProperty :prop="prop" />
        </template>
      </dl>

      <div
        class="flex flex-col gap-8 p-8 border-t border-gray-400"
        v-if="!!slots.default"
      >
        <strong class="block font-medium text-sm py-4">Related classes</strong>
        <slot />
      </div>
    </template>
    <template v-else>
      <div class="p-16 text-center">
        Class "{{ props.name }}" was not found.
      </div>
    </template>
  </div>
</template>
