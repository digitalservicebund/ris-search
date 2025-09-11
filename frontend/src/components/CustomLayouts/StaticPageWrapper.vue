<script setup lang="ts"></script>

<template>
  <div class="pt-20 pb-24 sm:pt-32 sm:pb-40 lg:pt-64 lg:pb-80">
    <nav aria-label="Brotkrumen" class="">
      <slot name="breadcrumb" />
    </nav>
    <main id="main-content" aria-labelledby="page-title">
      <slot />
    </main>
  </div>
</template>
