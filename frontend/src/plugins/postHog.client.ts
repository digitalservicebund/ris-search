import { usePostHog } from "~/composables/usePostHog";
import { getAccessibilityRelatedMetrics } from "~/utils/postHog";

export default defineNuxtPlugin(() => {
  const { initialize, postHog } = usePostHog();
  initialize();
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
