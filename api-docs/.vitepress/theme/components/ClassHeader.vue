<script setup lang="ts">
import { useData } from "vitepress";
import type { Class } from "../utils/classes";
import CodeBadge from "./CodeBadge.vue";
import FeedbackInlineSurvey from "./FeedbackInlineSurvey.vue";
import Icon from "./Icon.vue";

const { lang } = useData();
const props = defineProps<{ cls: Class }>();
const cls = props.cls;
</script>

<template>
  <div
    class="flex flex-row items-start gap-6 px-16 py-12 target:bg-gray-100"
    :id="cls.slug"
  >
    <div class="w-20 text-center flex-shrink-0">
      <Icon id="deployed-code" size="1.2em" class="text-brand-primary" />
    </div>
    <div class="flex flex-col gap-6 flex-grow overflow-x-hidden">
      <dt class="flex flex-col items-start md:flex-row flex-wrap gap-x-4">
        <a :href="'#' + cls.slug" class="link-anchor">
          <CodeBadge tint="blue" class="text-sm">
            {{ cls.id.replaceAll("recht:", "") }}
          </CodeBadge>
        </a>
        <CodeBadge class="text-gray-900" v-if="!!cls.subclassOf">
          extends {{ cls.subclassOf }}
        </CodeBadge>
      </dt>
      <dd
        class="text-gray-800 text-sm"
        v-html="cls.descriptions[lang]"
        v-if="!!cls.descriptions[lang]"
      />
    </div>
    <div class="self-center flex-shrink-0">
      <FeedbackInlineSurvey
        id="018c3a3a-5fb9-0000-ec8f-74ebc8be23b6"
        minimal
        :context="cls.slug"
      />
    </div>
  </div>
</template>
