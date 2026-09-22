<script setup lang="ts">
const { label, icon } = defineProps<{
  /** Used as both the trigger button's label and the drawer's title */
  label: string;
  icon: Component;
}>();

const emit = defineEmits<{
  /** "Anwenden" was clicked - the drawer closes right after */
  apply: [];
  /** "Zurücksetzen" was clicked - the drawer closes right after */
  reset: [];
}>();

/** The drawer's open state, exposed so callers can react to it opening/closing */
const visible = defineModel<boolean>("visible", { default: false });

// @ts-expect-error -- usage in template not detected
const { triggerRef } = useDrawer(visible);

const drawerId = useId();

function handleReset() {
  emit("reset");
  visible.value = false;
}

function handleApply() {
  emit("apply");
  visible.value = false;
}
</script>

<template>
  <div class="md:hidden" v-bind="$attrs">
    <UiButton
      ref="triggerRef"
      :aria-controls="drawerId"
      :aria-expanded="visible"
      :label="label"
      class="w-full"
      severity="info"
      @click="visible = true"
    >
      <template #icon>
        <component :is="icon" />
      </template>
    </UiButton>

    <UiDrawer
      :id="drawerId"
      :aria-label="label"
      :header="label"
      v-model:visible="visible"
    >
      <template #default="{ appendTarget }">
        <div class="mobile-action-drawer-content space-y-24">
          <slot :append-target="appendTarget" />
        </div>
      </template>

      <template #footer>
        <div class="flex gap-8">
          <UiButton
            class="flex-1"
            label="Zurücksetzen"
            severity="secondary"
            @click="handleReset"
          />
          <UiButton
            class="flex-1"
            label="Anwenden"
            severity="primary"
            @click="handleApply"
          />
        </div>
      </template>
    </UiDrawer>
  </div>
</template>

<style lang="css" scoped>
@reference "~/assets/main.css";

@supports not (container-type: scroll-state) {
  .mobile-action-drawer-content {
    @apply py-12;
  }
}
</style>
