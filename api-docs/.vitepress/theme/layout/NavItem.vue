<script setup lang="ts">
import Icon from "../components/Icon.vue";
import { useData } from "../composables/data";
import type { NavItem } from "../types";
import { isActive, prependBase } from "../utils";
import { computed } from "vue";

const { site, page } = useData();
const base = site.value.base || "/";
const props = defineProps<{ item: NavItem }>();
const item = props.item;
const active = computed(() =>
  isActive(page.value.relativePath, item.link.replaceAll("#", ""), false)
);
</script>

<template>
  <a
    class="gap-4 py-4 px-8 flex flex-row items-center hover:underline active:underline"
    :class="{ underline: active, 'no-underline': !active }"
    :href="prependBase(item.link, base)"
  >
    <Icon :id="item.icon" size="1.2em" v-if="!!item.icon" />
    {{ item.text }}
  </a>
</template>
