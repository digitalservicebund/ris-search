<script setup lang="ts">
import { computed, type Component } from "vue";
import IcBaselineCheck from "~icons/ic/baseline-check";
import IcBaselineWarningAmber from "~icons/ic/baseline-warning-amber";
import IcOutlineInfo from "~icons/ic/outline-info";
import { tw } from "../../utils/tags";

const { severity = "info", hideIcon = false } = defineProps<{
  severity?: Severity;
  hideIcon?: boolean;
}>();

const variants: Record<
  Severity,
  { icon: Component; root: string; iconColor: string }
> = {
  success: {
    icon: IcBaselineCheck,
    root: tw`border-l-green-800 bg-green-200`,
    iconColor: tw`text-green-800`,
  },
  info: {
    icon: IcOutlineInfo,
    root: tw`border-l-blue-800 bg-white`,
    iconColor: tw`text-blue-800`,
  },
  warn: {
    icon: IcBaselineWarningAmber,
    root: tw`border-l-yellow-800 bg-yellow-200`,
    iconColor: tw`text-orange-700`,
  },
  error: {
    icon: IcBaselineWarningAmber,
    root: tw`border-l-red-800 bg-red-200`,
    iconColor: tw`text-red-800`,
  },
};

const variant = computed(() => variants[severity]);
</script>

<script lang="ts">
export type Severity = "success" | "info" | "warn" | "error";
</script>

<template>
  <div :class="['typo-label2-regular border-l-4 p-16', variant.root]">
    <!--
      The zero-width ::before gives the icon box a text baseline, so the icon
      can align to the baseline of the first line of content. This keeps it
      centered on that line even if the line grows (e.g. links have a larger
      line height than the surrounding text).
    -->
    <div class="flex items-baseline gap-8">
      <span
        v-if="!hideIcon"
        class="flex h-[1lh] flex-none items-center before:content-['\200b'] [&>svg]:size-20"
        :class="variant.iconColor"
      >
        <slot name="icon">
          <component :is="variant.icon" aria-hidden="true" />
        </slot>
      </span>
      <div class="flex-1 space-y-4">
        <slot />
      </div>
    </div>
  </div>
</template>
