import type { Meta, StoryObj } from "@storybook/vue3-vite";
import { ref } from "vue";
import IcBaselineErrorOutline from "~icons/ic/baseline-error-outline";
import { html } from "../../utils/tags";
// Imported as UiInputText, not InputText, for consistency with the other ui
// stories and to keep the tag distinct in the html`` templates.
import UiInputText from "./InputText.vue";

// placeholder/disabled/readonly are native attributes handled via fallthrough,
// so they are not declared props — widen the args type to expose them as controls.
type InputTextArgs = InstanceType<typeof UiInputText>["$props"] & {
  placeholder?: string;
  disabled?: boolean;
  readonly?: boolean;
};

const meta: Meta<InputTextArgs> = {
  component: UiInputText,
  tags: ["autodocs"],
  args: {
    size: "small",
    fluid: false,
    clearable: false,
    placeholder: "Placeholder",
    disabled: false,
    readonly: false,
  },
  argTypes: {
    size: { control: "select", options: ["small", "large"] },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  render: (args) => ({
    components: { UiInputText },
    setup() {
      const model = ref("Text");
      return { args, model };
    },
    template: html`<UiInputText v-model="model" v-bind="args" />`,
  }),
};

export const WithLabelAndHint: Story = {
  render: (args) => ({
    components: { UiInputText },
    setup() {
      const model = ref("Text");
      return { args, model };
    },
    template: html`<div class="flex flex-col gap-2">
      <label class="typo-label2-regular" for="with-top-label">Label</label>
      <UiInputText
        id="with-top-label"
        v-model="model"
        aria-describedby="with-top-label-hint"
        v-bind="args"
      />
      <small id="with-top-label-hint">Additional hint text</small>
    </div>`,
  }),
};

export const WithHorizontalLabel: Story = {
  render: (args) => ({
    components: { UiInputText },
    setup() {
      const model = ref("Text");
      return { args, model };
    },
    template: html`<div class="flex items-center gap-16">
      <label class="typo-label2-regular" for="with-left-label">Label</label>
      <UiInputText id="with-left-label" v-model="model" v-bind="args" />
    </div>`,
  }),
};

export const Disabled: Story = {
  args: { disabled: true },
  render: (args) => ({
    components: { UiInputText },
    setup() {
      const model = ref("Text");
      return { args, model };
    },
    template: html`<UiInputText v-model="model" v-bind="args" />`,
  }),
};

export const Readonly: Story = {
  args: { readonly: true },
  render: (args) => ({
    components: { UiInputText },
    setup() {
      const model = ref("Text");
      return { args, model };
    },
    template: html`<UiInputText v-model="model" v-bind="args" />`,
  }),
};

export const Invalid: Story = {
  render: (args) => ({
    components: { UiInputText, IcBaselineErrorOutline },
    setup() {
      const model = ref("Text");
      return { args, model };
    },
    template: html`<div class="flex flex-col gap-2">
      <label class="typo-label2-regular" for="invalid">Label</label>
      <UiInputText
        id="invalid"
        v-model="model"
        aria-invalid="true"
        aria-describedby="invalid-hint"
        v-bind="args"
      />
      <small id="invalid-hint">
        <IcBaselineErrorOutline /> Error message with helper text goes here
      </small>
    </div>`,
  }),
};

export const Fluid: Story = {
  args: { fluid: true },
  render: (args) => ({
    components: { UiInputText },
    setup() {
      const model = ref("Text");
      return { args, model };
    },
    template: html`<UiInputText v-model="model" v-bind="args" />`,
  }),
};

export const Clearable: Story = {
  args: { clearable: true },
  render: (args) => ({
    components: { UiInputText },
    setup() {
      const model = ref("Text");
      return { args, model };
    },
    template: html`<UiInputText
      v-model="model"
      aria-label="Suchbegriff"
      v-bind="args"
    />`,
  }),
};

export const SearchType: Story = {
  args: { size: "large" },
  render: (args) => ({
    components: { UiInputText },
    setup() {
      const model = ref("Suchbegriff");
      return { args, model };
    },
    template: html`<UiInputText
      v-model="model"
      aria-label="Suche"
      type="search"
      v-bind="args"
    />`,
  }),
};
