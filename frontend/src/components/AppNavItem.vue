<script setup lang="ts">
export interface NavItem {
  text: string;
  link: string;
  icon?: Component;
}

const props = defineProps<{ item: NavItem }>();
const item = props.item;
const router = useRouter();
const active = computed(
  () => router.currentRoute.value.path === props.item.link,
);
</script>

<template>
  <NuxtLink
    class="flex flex-row items-center gap-4 px-12 py-8 text-xl hover:underline active:underline"
    active-class="underline"
    :class="{ underline: active, 'no-underline': !active }"
    :href="item.link"
  >
    <component :is="item.icon" class="h-20" />
    {{ item.text }}
  </NuxtLink>
</template>
