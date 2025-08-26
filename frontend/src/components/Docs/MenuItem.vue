<script setup lang="ts">
import { computed } from "vue";
import { useRoute, useRouter } from "#imports";
import MdiChevronUp from "~icons/mdi/chevron-up";
import MdiChevronRight from "~icons/mdi/chevron-right";

export interface NavItem {
  text: string;
  link: string;
  icon?: string;
  items?: NavItem[];
}

const props = defineProps<{ item: NavItem; root?: boolean }>();
const emit = defineEmits<(e: "click") => void>();
const route = useRoute();
const router = useRouter();
const isMenuDisabled = true;

const currentHash = computed(() => (route.hash ?? "").replace("#", ""));

const matchingHashes = computed(() => {
  const hashes = new Set<string>();
  const thisHash = props.item.link?.split("#")?.[1];
  if (thisHash) hashes.add(thisHash);
  for (const child of props.item.items ?? []) {
    const parts = child.link?.split("#");
    if (parts?.[1]) hashes.add(parts[1]);
  }
  return hashes;
});

const isActive = computed(() => {
  const [itemPath = "", itemHash] = (props.item.link || "").split("#");
  if (!itemPath || !route.path.startsWith(itemPath)) return false;
  if (!itemHash) return true;
  return matchingHashes.value.has(currentHash.value);
});

function onAnchorClick(e: MouseEvent) {
  if (props.item.items?.length) {
    e.preventDefault();
    const ownHash = props.item.link.split("#")[1];
    const firstChildHash = props.item.items
      ?.find((c) => c.link.includes("#"))
      ?.link.split("#")[1];
    const targetHash = ownHash || firstChildHash;

    if (targetHash) {
      router.replace({ hash: `#${targetHash}` });
    }
    emit("click");
  } else {
    emit("click");
  }
}
</script>

<template>
  <NuxtLink
    class="flex cursor-pointer items-center px-4 pr-2 pl-8 no-underline focus:underline focus-visible:outline focus-visible:outline-offset-4 focus-visible:outline-blue-800 dark:text-blue-200"
    :class="{
      'py-4 text-base font-medium': root,
      'ris-body2-bold py-2 font-bold': !root,
      'border-l-4 border-blue-800 pl-4 text-blue-800': isActive,
    }"
    :to="isMenuDisabled ? '#' : item.link"
    @click="isMenuDisabled ? () => {} : onAnchorClick"
  >
    <span :class="item.icon" />
    <span class="ml-2">{{ item.text }}</span>
    <span v-if="item.items && item.items.length > 0" class="ml-auto">
      <MdiChevronUp v-if="isActive" size="1.25em" class="ml-auto" />
      <MdiChevronRight v-else size="1.25em" class="ml-auto" />
    </span>
  </NuxtLink>

  <div
    v-if="isActive && item.items?.length"
    class="ml-12 border-l-2 border-gray-300"
  >
    <MenuItem v-for="child in item.items" :key="child.link" :item="child" />
  </div>
</template>
