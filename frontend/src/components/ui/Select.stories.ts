import type { Meta, StoryObj } from "@storybook/vue3-vite";
import { ref } from "vue";
import IcBaselineErrorOutline from "~icons/ic/baseline-error-outline";
import { html } from "../../utils/tags";
import UiSelect from "./Select.vue";

const options = [
  { label: "Relevanz", value: "default" },
  { label: "Datum: Neueste zuerst", value: "-date" },
  { label: "Datum: Älteste zuerst", value: "date" },
  { label: "Aktenzeichen", value: "fileNumber" },
];

const meta: Meta<typeof UiSelect> = {
  component: UiSelect,

  tags: ["autodocs"],

  args: {
    options,
    placeholder: undefined,
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

/**
 * The field tracks the width of the selected option rather than staying as wide
 * as the longest one, so switching between "Relevanz" and "Datum: Neueste
 * zuerst" resizes it. Browsers without `field-sizing` support keep it at the
 * width of the longest option.
 */
export const Default: Story = {
  render: (args) => ({
    components: { UiSelect },
    setup() {
      const selected = ref("default");
      return { args, selected };
    },
    template: html`<UiSelect v-bind="args" v-model="selected" />`,
  }),
};

/**
 * Without a selection, the placeholder is shown as a disabled option so it can
 * never be picked.
 */
export const WithPlaceholder: Story = {
  args: { placeholder: "Bitte auswählen" },
  render: (args) => ({
    components: { UiSelect },
    setup() {
      const selected = ref("");
      return { args, selected };
    },
    template: html`<UiSelect v-bind="args" v-model="selected" />`,
  }),
};

/**
 * The accessible name comes from an associated label. Use `aria-labelledby`
 * when the label should not also toggle the field.
 */
export const WithLabel: Story = {
  render: (args) => ({
    components: { UiSelect },
    setup() {
      const selected = ref("default");
      return { args, selected };
    },
    template: html`
      <label for="with-label" class="typo-label2-regular mb-4 block">
        Sortieren nach
      </label>
      <UiSelect v-bind="args" v-model="selected" id="with-label" />
    `,
  }),
};

export const WithHorizontalLabel: Story = {
  render: (args) => ({
    components: { UiSelect },
    setup() {
      const selected = ref("default");
      return { args, selected };
    },
    template: html`
      <span class="flex items-center gap-8">
        <label for="with-horizontal-label" class="typo-label2-regular">
          Sortieren nach
        </label>
        <UiSelect v-bind="args" v-model="selected" id="with-horizontal-label" />
      </span>
    `,
  }),
};

/**
 * Options may also be plain strings, in which case each string is used as both
 * the label and the value.
 */
export const PlainStringOptions: Story = {
  args: {
    options: ["25", "50", "100"],
  },
  render: (args) => ({
    components: { UiSelect },
    setup() {
      const selected = ref("50");
      return { args, selected };
    },
    template: html`<UiSelect v-bind="args" v-model="selected" />`,
  }),
};

export const Disabled: Story = {
  render: (args) => ({
    components: { UiSelect },
    setup() {
      const selected = ref("default");
      return { args, selected };
    },
    template: html`<UiSelect v-bind="args" v-model="selected" disabled />`,
  }),
};

/**
 * There is no `invalid` prop. Set `aria-invalid` instead, so the accessible
 * state and the styling can never disagree, and point `aria-describedby` at the
 * hint. A `<small>` placed directly after the select is styled as that hint
 * automatically.
 */
export const Invalid: Story = {
  render: (args) => ({
    components: { UiSelect, IcBaselineErrorOutline },
    setup() {
      const selected = ref("");
      return { args, selected };
    },
    template: html`
      <UiSelect
        v-bind="args"
        v-model="selected"
        aria-describedby="invalid-hint"
        aria-invalid="true"
        id="invalid"
        placeholder="Bitte auswählen"
      />
      <small id="invalid-hint">
        <IcBaselineErrorOutline />
        Bitte wählen Sie eine Option aus
      </small>
    `,
  }),
};
