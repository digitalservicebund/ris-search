<template>
  <NuxtLoadingIndicator :color="false" class="bg-blue-800" />

  <AnalyticsConsentBanner />

  <div class="min-h-[50vh] bg-gray-100">
    <AppHeader />
    <main id="main">
      <slot />
    </main>
  </div>

  <AppFooter />
</template>
