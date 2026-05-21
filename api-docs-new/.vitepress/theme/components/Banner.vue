<template>
  <div class="trial-banner">
    <div class="trial-banner__inner">
      <div>
        <p>
          <strong>This is a trial service.</strong>
          This API may be subject to changes and improvements as we learn from
          usage.
        </p>
        <p>
          This means that you may use this software and build applications that
          utilise it. However as we learn from feedback we may make changes to
          the software.
        </p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.trial-banner {
  position: sticky;
  top: 0;
  z-index: 100;
  background-color: #fef08a;
  border-bottom: 1px solid #b45309;
  padding: 0.75rem 2rem;
  font-size: 0.875rem;
  color: #000;
}

.dark .trial-banner {
  background-color: #7c2d12;
  border-bottom-color: transparent;
  color: #fff;
}

.trial-banner__inner {
  display: flex;
  gap: 1rem;
  max-width: 1280px;
  margin: 0 auto;
}

.trial-banner p {
  margin: 0.125rem 0;
}
</style>
