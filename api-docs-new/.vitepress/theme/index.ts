import DefaultTheme from "vitepress/theme";
import type { Theme } from "vitepress";
import Layout from "./Layout.vue";
import SwaggerButton from "./components/SwaggerButton.vue";
import Footer from "./components/Footer.vue";
import "./custom.css";

export default {
  extends: DefaultTheme,
  enhanceApp({ app }) {
    app.component("SwaggerButton", SwaggerButton);
    app.component("Footer", Footer);
  },
  Layout,
} satisfies Theme;
