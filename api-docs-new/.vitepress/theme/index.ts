import DefaultTheme from "vitepress/theme";
import type { Theme } from "vitepress";
import Layout from "./Layout.vue";
import ActionButton from "./components/ActionButton.vue";
import Footer from "./components/Footer.vue";
import './custom.css'

export default {
  extends: DefaultTheme,
  enhanceApp({ app }) {
    app.component("ActionButton", ActionButton);
    app.component("Footer", Footer);
  },
  Layout,
} satisfies Theme;
