import type { StorybookConfig } from "@storybook/vue3-vite";
import tailwindcss from "@tailwindcss/vite";
import vue from "@vitejs/plugin-vue";
import { mergeConfig } from "vite";
import { icons } from "../config/icons.ts";

const config: StorybookConfig = {
  stories: [
    "./Startseite.mdx",
    {
      directory: "../src/assets",
      files: "**/*.stories.ts",
      titlePrefix: "Styleguide",
    },
    {
      directory: "../src/components/ui",
      files: "**/*.stories.ts",
      titlePrefix: "Komponenten",
    },
  ],

  addons: ["@storybook/addon-docs"],

  framework: {
    name: "@storybook/vue3-vite",
    options: { docgen: "vue-component-meta" },
  },

  async viteFinal(viteConfig) {
    return mergeConfig(viteConfig, {
      plugins: [vue(), tailwindcss(), icons],
    });
  },
};

export default config;
