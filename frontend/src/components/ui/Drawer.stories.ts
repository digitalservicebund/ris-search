import type { Meta, StoryObj } from "@storybook/vue3-vite";
import { ref } from "vue";
import { html } from "../../utils/tags";
import UiButton from "./Button.vue";
import UiDrawer from "./Drawer.vue";

const meta: Meta<typeof UiDrawer> = {
  title: "Drawer",
  component: UiDrawer,
  tags: ["autodocs"],
  args: {
    header: "Drawer",
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  render: (args) => ({
    components: { UiButton, UiDrawer },
    setup() {
      const visible = ref(false);
      return { args, visible };
    },
    template: html`
      <UiButton label="Show" @click="visible = true" />
      <UiDrawer v-model:visible="visible" v-bind="args">
        <div
          class="h-384 content-center border border-dashed border-blue-800 bg-blue-200 text-center"
        >
          <p>Content area</p>
        </div>
        <template #footer>
          <div class="h-40 content-center bg-blue-200 text-center">Footer</div>
        </template>
      </UiDrawer>
    `,
  }),
};

export const ScrollingContent: Story = {
  render: (args) => ({
    components: { UiButton, UiDrawer },
    setup() {
      const visible = ref(false);
      return { args, visible };
    },
    template: html`
      <UiButton label="Show" @click="visible = true" />
      <UiDrawer v-model:visible="visible" v-bind="args">
        <div
          class="h-[50rem] content-center border border-dashed border-blue-800 bg-blue-200 text-center"
        >
          <p>Content area</p>
        </div>
        <template #footer>
          <div class="h-40 content-center bg-blue-200 text-center">Footer</div>
        </template>
      </UiDrawer>
    `,
  }),
};

export const NoFooter: Story = {
  render: (args) => ({
    components: { UiButton, UiDrawer },
    setup() {
      const visible = ref(false);
      return { args, visible };
    },
    template: html`
      <UiButton label="Show" @click="visible = true" />
      <UiDrawer v-model:visible="visible" v-bind="args">
        <div
          class="h-384 content-center border border-dashed border-blue-800 bg-blue-200 text-center"
        >
          <p>Content area</p>
        </div>
      </UiDrawer>
    `,
  }),
};
