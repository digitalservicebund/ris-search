<script setup lang="ts">
interface Props {
  text: string;
  href: string;
  target?: string;
  rel?: string;
}

const props = withDefaults(defineProps<Props>(), {
  target: "_blank",
  rel: "noreferrer",
});
</script>

<template>
  <a
    class="action-button"
    :href="props.href"
    :target="props.target"
    :rel="props.rel"
  >
    {{ props.text }}
  </a>
</template>

<style scoped>
.action-button {
  display: inline-block;
  border: 1px solid var(--vp-button-brand-border);
  border-radius: 20px;
  padding: 0 20px;
  line-height: 38px;
  font-size: 14px;
  font-weight: 600;
  white-space: nowrap;
  text-align: center;
  color: var(--vp-button-brand-text);
  background-color: var(--vp-button-brand-bg);
  transition:
    color 0.25s,
    border-color 0.25s,
    background-color 0.25s;
  text-decoration: none;
}

.action-button:hover {
  border-color: var(--vp-button-brand-hover-border);
  color: var(--vp-button-brand-hover-text);
  background-color: var(--vp-button-brand-hover-bg);
}

.action-button:active {
  border-color: var(--vp-button-brand-active-border);
  color: var(--vp-button-brand-active-text);
  background-color: var(--vp-button-brand-active-bg);
  transition:
    color 0.1s,
    border-color 0.1s,
    background-color 0.1s;
}
</style>
