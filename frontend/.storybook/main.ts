import type { StorybookConfig } from "@storybook/vue3-vite";
import tailwindcss from "@tailwindcss/vite";
import vue from "@vitejs/plugin-vue";
import { mergeConfig } from "vite";
import { icons } from "../config/icons.ts";

const config: StorybookConfig = {
  stories: ["../src/components/ui/**/*.stories.@(ts|tsx)"],

  addons: [],

  framework: {
    name: "@storybook/vue3-vite",
    options: {},
  },

  async viteFinal(viteConfig) {
    return mergeConfig(viteConfig, {
      plugins: [vue(), tailwindcss(), icons],
    });
  },
};

export default config;
