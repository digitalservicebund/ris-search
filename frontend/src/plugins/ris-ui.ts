import PrimeVue from "primevue/config";
import { RisUiTheme, RisUiLocale } from "@digitalservicebund/ris-ui/primevue";
import "@digitalservicebund/ris-ui/fonts.css";

export default defineNuxtPlugin((nuxtApp) => {
  nuxtApp.vueApp.use(PrimeVue, {
    pt: RisUiTheme,
    unstyled: true,
    locale: RisUiLocale.deDE,
  });
});
