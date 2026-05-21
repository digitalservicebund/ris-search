import DefaultTheme from "vitepress/theme";
import type { Theme } from "vitepress";
import FeedbackForm from "./components/FeedbackForm.vue";

export default {
  extends: DefaultTheme,
  enhanceApp({ app }) {
    app.component("FeedbackForm", FeedbackForm);
  },
} satisfies Theme;
