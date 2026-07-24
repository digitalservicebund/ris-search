<script setup lang="ts">
import { Button, Drawer } from "primevue";

const { label, icon } = defineProps<{
  /** Used as both the trigger button's label and the drawer's title */
  label: string;
  icon: Component;
}>();

const emit = defineEmits<{
  /** "Anwenden" was clicked - the drawer closes right after */
  apply: [];
  /** "Zurücksetzen" was clicked - the drawer stays open */
  reset: [];
}>();

const {
  visible,
  // @ts-expect-error -- usage in template not detected
  triggerRef,
  closeButtonProps,
} = useDrawer();

const drawerId = useId();

function handleApply() {
  emit("apply");
  visible.value = false;
}
</script>

<template>
  <div class="md:hidden" v-bind="$attrs">
    <Button
      ref="triggerRef"
      severity="info"
      :label="label"
      class="w-full"
      :aria-controls="drawerId"
      :aria-expanded="visible"
      @click="visible = true"
    >
      <template #icon>
        <component :is="icon" />
      </template>
    </Button>

    <Drawer
      :id="drawerId"
      v-model:visible="visible"
      :aria-label="label"
      block-scroll
      :header="label"
      position="bottom"
      :close-button-props="closeButtonProps"
    >
      <div class="flex flex-col gap-24">
        <slot />
      </div>

      <div class="mt-24 flex gap-8">
        <Button
          severity="secondary"
          label="Zurücksetzen"
          class="flex-1"
          @click="emit('reset')"
        />
        <Button
          severity="primary"
          label="Anwenden"
          class="flex-1"
          @click="handleApply"
        />
      </div>
    </Drawer>
  </div>
</template>
