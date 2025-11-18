<script setup lang="ts">
const handleClick = () => {
  const header = document.getElementById("top");
  if (header) {
    header.scrollIntoView({ behavior: "smooth" });
  }
};
</script>
<template>
  <NuxtLink
    :to="{ hash: '#top' }"
    aria-label="zum Seitenanfang"
    @click.prevent="handleClick"
  >
    <svg
      viewBox="0 0 42 42"
      width="42"
      height="42"
      version="1.1"
      focusable="false"
      aria-hidden="true"
    >
      <path
        d="M21 1a20 20 0 1 0 20 20A20 20 0 0 0 21 1Zm0 38a18 18 0 1 1 18-18 18 18 0 0 1-18 18Z"
      />
      <path
        d="M21.2 16.5a.3.3 0 0 0-.4 0l-6.6 6.6a.2.2 0 0 0 0 .3l1 1.1a.2.2 0 0 0 .4 0l5.4-5.4 5.4 5.4a.2.2 0 0 0 .3 0l1-1a.2.2 0 0 0 0-.4Z"
      />
    </svg>
  </NuxtLink>
</template>
