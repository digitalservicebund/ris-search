<script setup lang="ts">
import { computed, onUnmounted, ref, useId, useTemplateRef } from "vue";
import IcBaselineCheck from "~icons/ic/baseline-check";
import IcBaselineContentCopy from "~icons/ic/baseline-content-copy";

const apiUrl = "https://testphase.rechtsinformationen.bund.de/v1/document";

// The code of the examples is copied to the clipboard as given, so keep it in
// sync with the markup in the template

const curl = {
  id: "curl",
  label: "cURL",
  code: `curl "${apiUrl}?searchTerm=Mietrecht&size=10"`,
};

const javascript = {
  id: "javascript",
  label: "JavaScript",
  code: [
    `const url = "${apiUrl}";`,
    "const response = await fetch(`${url}?searchTerm=Mietrecht&size=10`);",
    "const { totalItems, member } = await response.json();",
  ].join("\n"),
};

const examples = [curl, javascript];

const COPY_MESSAGE_TIMEOUT = 6000;

const uid = useId();
const list = useTemplateRef<HTMLElement>("list");

const activeId = ref(curl.id);
const activeExample = computed(
  () => examples.find((example) => example.id === activeId.value) ?? curl,
);

const select = (index: number) => {
  const example = examples[index];
  if (!example) return;

  activeId.value = example.id;
  const buttons = list.value?.querySelectorAll<HTMLElement>('[role="tab"]');
  buttons?.[index]?.focus();
};

const onKeydown = (event: KeyboardEvent) => {
  const current = examples.findIndex(
    (example) => example.id === activeId.value,
  );

  switch (event.key) {
    case "ArrowRight":
      select((current + 1) % examples.length);
      break;
    case "ArrowLeft":
      select((current - 1 + examples.length) % examples.length);
      break;
    case "Home":
      select(0);
      break;
    case "End":
      select(examples.length - 1);
      break;
    default:
      return;
  }

  event.preventDefault();
};

const copied = ref(false);
let resetTimeout: ReturnType<typeof setTimeout> | undefined;

const copy = async () => {
  try {
    await navigator.clipboard.writeText(activeExample.value.code);
  } catch {
    return;
  }

  copied.value = true;
  clearTimeout(resetTimeout);
  resetTimeout = setTimeout(() => {
    copied.value = false;
  }, COPY_MESSAGE_TIMEOUT);
};

onUnmounted(() => clearTimeout(resetTimeout));
</script>

<template>
  <div
    class="code-example rounded-sm border-8 border-(--code-frame) bg-(--code-frame) text-blue-400"
  >
    <div class="flex items-center justify-between gap-8 pb-8">
      <div
        ref="list"
        aria-label="Programmiersprache des Beispiels"
        aria-orientation="horizontal"
        class="flex min-w-0 overflow-x-auto"
        role="tablist"
        @keydown="onKeydown"
      >
        <button
          v-for="example in examples"
          :id="`${uid}-tab-${example.id}`"
          :key="example.id"
          :aria-controls="`${uid}-panel-${example.id}`"
          :aria-selected="example.id === activeId"
          :tabindex="example.id === activeId ? 0 : -1"
          class="typo-label2-bold flex h-40 shrink-0 cursor-pointer items-center rounded-sm px-8 whitespace-nowrap text-gray-600 -outline-offset-4 outline-white hover:text-white focus-visible:outline-4 aria-selected:bg-(--code-surface) aria-selected:text-white md:h-48 md:px-24"
          role="tab"
          type="button"
          @click="activeId = example.id"
        >
          {{ example.label }}
        </button>
      </div>

      <button
        class="typo-label2-regular flex h-40 shrink-0 cursor-pointer items-center gap-8 rounded-sm px-12 text-blue-400 -outline-offset-4 outline-white hover:text-white focus-visible:outline-4 md:h-48 md:px-16"
        type="button"
        @click="copy"
      >
        <component
          :is="copied ? IcBaselineCheck : IcBaselineContentCopy"
          aria-hidden="true"
          class="size-16 shrink-0"
        />
        {{ copied ? "Kopiert" : "Kopieren" }}
      </button>
    </div>

    <!-- Spelled out as markup to highlight it, in a `pre` so its whitespace
    survives compilation. Keep it in sync with the code above. -->
    <div
      :id="`${uid}-panel-curl`"
      :aria-labelledby="`${uid}-tab-curl`"
      :hidden="activeId !== 'curl'"
      class="panel"
      role="tabpanel"
    >
      <pre><code><span aria-hidden="true" class="prompt">$ </span><span class="command">curl</span> <span class="string">"https://testphase.rechtsinformationen.bund.de/v1/document?searchTerm=Mietrecht&amp;size=10"</span></code></pre>
    </div>

    <div
      :id="`${uid}-panel-javascript`"
      :aria-labelledby="`${uid}-tab-javascript`"
      :hidden="activeId !== 'javascript'"
      class="panel"
      role="tabpanel"
    >
      <pre><code><span class="keyword">const</span> url = <span class="string">"https://testphase.rechtsinformationen.bund.de/v1/document"</span>;
<span class="keyword">const</span> response = <span class="keyword">await</span> fetch(<span class="string">`${url}?searchTerm=Mietrecht&amp;size=10`</span>);
<span class="keyword">const</span> { totalItems, member } = <span class="keyword">await</span> response.json();</code></pre>
    </div>

    <output class="sr-only">{{ copied ? "Kopiert" : "" }}</output>
  </div>
</template>

<style scoped>
@reference "~/assets/main.css";

.code-example {
  --code-frame: #00131f;
  --code-surface: #002438;
  --code-line: #004b76;
}

.panel {
  @apply rounded-sm border border-(--code-line) bg-(--code-surface) px-16 py-12;
}

.panel pre {
  @apply font-mono text-[0.8125rem] leading-20 wrap-break-word whitespace-pre-wrap md:text-[0.875rem] md:leading-24;
}

.prompt {
  @apply text-blue-600 select-none;
}

.command {
  @apply text-white;
}

.keyword {
  @apply text-orange-300;
}

.string {
  @apply text-green-300;
}
</style>
