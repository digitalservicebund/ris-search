import type { Meta, StoryObj } from "@storybook/vue3-vite";
import { ref } from "vue";
import IcBaselineErrorOutline from "~icons/ic/baseline-error-outline";
import { html } from "../../utils/tags";
import UiTextarea from "./Textarea.vue";

// placeholder/disabled/readonly are native attributes handled via fallthrough,
// so they are not declared props — widen the args type to expose them as controls.
type TextareaArgs = InstanceType<typeof UiTextarea>["$props"] & {
  placeholder?: string;
  disabled?: boolean;
  readonly?: boolean;
};

const meta: Meta<TextareaArgs> = {
  component: UiTextarea,
  tags: ["autodocs"],
  args: {
    fluid: false,
    placeholder: "Placeholder",
    disabled: false,
    readonly: false,
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  render: (args) => ({
    components: { UiTextarea },
    setup() {
      const model = ref("Text");
      return { args, model };
    },
    template: html`<UiTextarea v-model="model" v-bind="args" />`,
  }),
};

export const WithLabelAndHint: Story = {
  render: (args) => ({
    components: { UiTextarea },
    setup() {
      const model = ref("Text");
      return { args, model };
    },
    template: html`<div class="flex flex-col gap-2">
      <label class="typo-label2-regular" for="with-label">Label</label>
      <UiTextarea
        id="with-label"
        v-model="model"
        aria-describedby="with-label-hint"
        v-bind="args"
      />
      <small id="with-label-hint">Additional hint text</small>
    </div>`,
  }),
};

export const Disabled: Story = {
  args: { disabled: true },
  render: (args) => ({
    components: { UiTextarea },
    setup() {
      const model = ref("Text");
      return { args, model };
    },
    template: html`<UiTextarea v-model="model" v-bind="args" />`,
  }),
};

export const Readonly: Story = {
  args: { readonly: true },
  render: (args) => ({
    components: { UiTextarea },
    setup() {
      const model = ref("Text");
      return { args, model };
    },
    template: html`<UiTextarea v-model="model" v-bind="args" />`,
  }),
};

export const Invalid: Story = {
  render: (args) => ({
    components: { UiTextarea, IcBaselineErrorOutline },
    setup() {
      const model = ref("Text");
      return { args, model };
    },
    template: html`<div class="flex flex-col gap-2">
      <label class="typo-label2-regular" for="invalid-with-label">Label</label>
      <UiTextarea
        id="invalid-with-label"
        v-model="model"
        aria-invalid="true"
        aria-describedby="invalid-with-label-hint"
        v-bind="args"
      />
      <small id="invalid-with-label-hint">
        <IcBaselineErrorOutline /> Error message with helper text goes here
      </small>
    </div>`,
  }),
};

export const Fluid: Story = {
  args: { fluid: true },
  render: (args) => ({
    components: { UiTextarea },
    setup() {
      const model = ref("Text");
      return { args, model };
    },
    template: html`<UiTextarea v-model="model" v-bind="args" />`,
  }),
};
