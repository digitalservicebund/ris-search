<script setup lang="ts">

import Icon from "../components/Icon.vue";
import {useRoute} from "vitepress";
import {computed, ref} from "vue";
import type {NavItem} from "../types";

const props = defineProps<{ item: NavItem, root?: boolean}>();
defineEmits<{"click": () => void}>();
const route = useRoute()

const hash = ref(window.location.hash.replace("#", ""))

const handleHashChange = () => {
  console.log("hash change", location.hash)
  hash.value = window.location.hash.replace("#", "")
}
window.addEventListener('hashchange', handleHashChange)

// collect hash URLs of self and children
const matchingHashes = computed(() => {
  const hashes = new Set<string>();
  const thisHash = props.item.link?.split('#')?.[1]
  if (thisHash) hashes.add(thisHash)
  for (const child of props.item.items ?? []) {
    const parts = child.link?.split("#");
    if (parts?.[1]) {
      hashes.add(parts[1]);
    }
  }
  return hashes;
})

const isActive = computed(() => {
  const split = props.item.link?.split("#");
  const itemPath = split?.[0]
  const itemHash = split?.[1]

  // compare current path
  if (!itemPath || !route.path.startsWith(itemPath)) return false;
  // if path matches, compare hash
  if (!itemHash) return true; // items without hash always match at this stage
  return matchingHashes.value.has(hash.value);
})

</script>

<template>
  <a
    class="flex items-center cursor-pointer dark:text-blue-200 px-4 pr-2 pl-8 no-underline focus:underline focus-visible:outline-4 focus-visible:outline focus-visible:outline-offset-4 focus-visible:outline-blue-800"
    :class="{
      'py-4 font-medium text-base': root,
      'py-2 ris-body2-bold font-bold': !root,
      'text-blue-800 pl-4 border-l-4 border-blue-800': isActive,
       }
    "
    :href="item.link"
    @click="$emit('click')"
  >
    <span :class="item.icon" />
    <span class="ml-2">{{ item.text }}</span>
    <Icon
      :id="isActive ? 'chevron-up' : 'chevron-right'"
      size="1.25em"
      class="ml-auto"
      v-if="item.items && item.items.length > 0"
    /> </a
  >
  <div v-if="isActive && item.items?.length" class="ml-12 border-l-2 border-gray-300">
    <MenuItem
      v-for="child in item.items"
      :item="child"
      :key="child.link"
      />
  </div>
</template>

