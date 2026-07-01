<template>
  <div class="base-grid print:block">
    <div class="col-span-12 pt-32 pb-32 md:col-span-7 md:pb-56">
      <slot />
    </div>

    <div
      v-if="$slots.sidebar"
      class="col-span-12 row-start-1 md:col-span-4 md:col-start-9 md:row-start-auto md:h-full md:border-l md:border-gray-400 print:hidden"
    >
      <div
        class="overflow-auto md:sticky md:top-0 md:max-h-[calc(min(100%,100dvh))]"
      >
        <slot name="sidebar" />
      </div>
    </div>
  </div>
</template>
