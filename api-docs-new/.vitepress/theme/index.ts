import type { Theme } from "vitepress";
import DefaultTheme from "vitepress/theme";
import SwaggerButton from "./components/SwaggerButton.vue";
import Layout from "./Layout.vue";
import "./custom.css";

export default {
  extends: DefaultTheme,
  enhanceApp({ app }) {
    app.component("SwaggerButton", SwaggerButton);
  },
  Layout,
} satisfies Theme;
