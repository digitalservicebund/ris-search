<template>
  <NuxtLink
    :to="{ name: 'index' }"
    aria-label="Zur Startseite"
    class="text-20 group leading-none no-underline outline-offset-4 outline-blue-800 hover:text-blue-800 focus-visible:outline-4"
  >
    <span class="block font-bold">Rechtsinformationen</span>
    <span class="text-gray-900 group-hover:text-inherit">des Bundes</span>
  </NuxtLink>
</template>
