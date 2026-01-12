import { usePostHog } from "~/composables/usePostHog";
import { getAccessibilityRelatedMetrics } from "~/utils/postHog";

export default defineNuxtPlugin(async () => {
  const { initialize, postHog } = usePostHog();
  await initialize();
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
