<script setup lang="ts"></script>

<template>
  <div class="pt-20 pb-24 sm:pt-32 sm:pb-40 lg:pt-64 lg:pb-80">
    <nav aria-labelledby="breadcrumb-heading">
      <h2 id="breadcrumb-heading" class="sr-only">Brotkrumen</h2>
      <slot name="breadcrumb" />
    </nav>
    <div id="main-content" aria-labelledby="page-title">
      <slot />
    </div>
  </div>
</template>
