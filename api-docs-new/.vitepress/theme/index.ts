import DefaultTheme from "vitepress/theme";
import type { Theme } from "vitepress";
import FeedbackForm from "./components/FeedbackForm.vue";
import Layout from "./Layout.vue";

export default {
  extends: DefaultTheme,
  Layout,
  enhanceApp({ app }) {
    app.component("FeedbackForm", FeedbackForm);
  },
} satisfies Theme;
