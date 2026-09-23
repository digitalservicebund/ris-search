<script setup lang="ts">
import {
  TooltipArrow,
  TooltipContent,
  TooltipPortal,
  TooltipProvider,
  TooltipRoot,
  TooltipTrigger,
} from "reka-ui";
import { tw } from "../../utils/tags";

const { text, side = "top" } = defineProps<{
  text?: string;
  side?: "top" | "right" | "bottom" | "left";
}>();

// Classes ------------------------------------------------

const contentClass = tw`ris-label3-regular rounded-sm bg-gray-900 px-8 py-4 text-center whitespace-pre-line text-white`;

const arrowClass = tw`fill-gray-900`;
</script>

<template>
  <TooltipProvider>
    <TooltipRoot :delay-duration="0" :disabled="!text">
      <TooltipTrigger as-child>
        <slot />
      </TooltipTrigger>

      <TooltipPortal>
        <TooltipContent
          :class="contentClass"
          :collision-padding="16"
          :side="side"
        >
          {{ text }}
          <TooltipArrow :class="arrowClass" />
        </TooltipContent>
      </TooltipPortal>
    </TooltipRoot>
  </TooltipProvider>
</template>
