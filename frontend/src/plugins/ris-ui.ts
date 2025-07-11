import { RisUiTheme, RisUiLocale } from "@digitalservicebund/ris-ui/primevue";
import PrimeVue from "primevue/config";
import "@digitalservicebund/ris-ui/fonts.css";
import ToastService from "primevue/toastservice";
import Tooltip from "primevue/tooltip";

export default defineNuxtPlugin((nuxtApp) => {
  const app = nuxtApp.vueApp;
  app.use(PrimeVue, {
    pt: RisUiTheme,
    unstyled: true,
    locale: RisUiLocale.deDE,
  });
  app.use(ToastService);
  app.directive("tooltip", Tooltip);
});
