<script setup lang="ts">
import {
  Dialog,
  DialogPanel,
  TransitionChild,
  TransitionRoot,
} from "@headlessui/vue";
import { computed, ref } from "vue";
import FeedbackSurvey from "./FeedbackSurvey.vue";
import Icon from "./Icon.vue";

const isEnabled =
  !import.meta.env.SSR &&
  window.posthog &&
  window.posthog.has_opted_in_capturing();

const props = defineProps<{ id: string; context: string; minimal?: boolean }>();
const isOpen = ref(false);
const minimal = computed(() =>
  typeof props.minimal === "boolean" ? props.minimal : false
);

const handleCloseModal = () => {
  isOpen.value = false;
};

const handleOpenModel = () => {
  isOpen.value = true;
};
</script>

<template>
  <button
    v-if="isEnabled"
    @click="handleOpenModel"
    class="flex flex-row gap-4 items-center bg-yellow-100 text-yellow-900 border-yellow-400 border rounded px-4 py-2 hover:border-yellow-900"
  >
    <Icon id="chat-bubble" />
    <span class="text-sm" v-if="!minimal">Send feedback</span>
  </button>

  <TransitionRoot appear :show="isOpen" as="template">
    <Dialog as="div" @close="handleCloseModal" class="relative z-10">
      <TransitionChild
        as="template"
        enter="duration-300 ease-out"
        enter-from="opacity-0"
        enter-to="opacity-100"
        leave="duration-200 ease-in"
        leave-from="opacity-100"
        leave-to="opacity-0"
      >
        <div class="fixed inset-0 bg-black/25" />
      </TransitionChild>

      <div class="fixed inset-16 overflow-y-auto">
        <div class="flex min-h-full items-start md:items-center justify-center">
          <TransitionChild
            as="template"
            enter="duration-300 ease-out"
            enter-from="opacity-0 scale-95"
            enter-to="opacity-100 scale-100"
            leave="duration-200 ease-in"
            leave-from="opacity-100 scale-100"
            leave-to="opacity-0 scale-95"
          >
            <DialogPanel
              class="w-full max-w-md transform overflow-hidden rounded bg-background-primary shadow-xl transition-all"
            >
              <FeedbackSurvey :id="props.id" :context="props.context" />
            </DialogPanel>
          </TransitionChild>
        </div>
      </div>
    </Dialog>
  </TransitionRoot>
</template>
