<script setup lang="ts">
import { computed, type Component } from "vue";
import IcBaselineCheck from "~icons/ic/baseline-check";
import IcBaselineWarningAmber from "~icons/ic/baseline-warning-amber";
import IcOutlineInfo from "~icons/ic/outline-info";
import { tw } from "../../utils/tags";

const { severity = "info" } = defineProps<{
  severity?: Severity;
}>();

const defaultIcons: Record<Severity, { icon: Component; class: string }> = {
  success: { icon: IcBaselineCheck, class: tw`text-green-800` },
  info: { icon: IcOutlineInfo, class: tw`text-blue-800` },
  warn: { icon: IcBaselineWarningAmber, class: tw`text-black` },
  error: { icon: IcBaselineWarningAmber, class: tw`text-red-800` },
};

const defaultIcon = computed(() => defaultIcons[severity]);

// Classes ------------------------------------------------

const base = tw`ris-body1-regular border-l-4 px-20 py-14`;

const success = tw`border-l-green-800 bg-green-200`;

const info = tw`border-l-blue-800 bg-blue-200`;

const warn = tw`border-l-yellow-800 bg-yellow-200`;

const error = tw`border-l-red-800 bg-red-200`;

const rootClass = computed(() => ({
  [base]: true,
  [success]: severity === "success",
  [info]: severity === "info",
  [warn]: severity === "warn",
  [error]: severity === "error",
}));

const iconClass = tw`h-24 w-24 flex-none`;
</script>

<script lang="ts">
export type Severity = "success" | "info" | "warn" | "error";
</script>

<template>
  <div :class="rootClass">
    <div class="flex items-start gap-8">
      <slot name="icon">
        <component
          :class="[iconClass, defaultIcon.class]"
          :is="defaultIcon.icon"
          aria-hidden="true"
        />
      </slot>
      <div class="flex-1">
        <slot />
      </div>
    </div>
  </div>
</template>
