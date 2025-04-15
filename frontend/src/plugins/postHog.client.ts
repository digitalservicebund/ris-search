import { usePostHogStore } from "~/stores/usePostHogStore";
import { getAccessibilityRelatedMetrics } from "~/utils/postHog";

export default defineNuxtPlugin(() => {
  const store = usePostHogStore();
  store.initialize();
  const { postHog } = storeToRefs(store);
  if (postHog.value) {
    useRouter().afterEach((to) => {
      nextTick(() => {
        postHog.value?.capture("$pageview", {
          current_url: to.fullPath,
          ...getAccessibilityRelatedMetrics(),
        });
      });
    });
  }
});
