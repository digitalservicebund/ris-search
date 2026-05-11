import { mountSuspended } from "@nuxt/test-utils/runtime";
import { describe, expect, it } from "vitest";
import { createApp, defineComponent, h, nextTick, ref } from "vue";
import { provideSkipLinks, useSkipLinks, type SkipLink } from "./useSkipLinks";

describe("useSkipLinks", () => {
  it("syncs provided skip links into the registry", async () => {
    const links = ref<SkipLink[]>([]);
    const skipLinks = ref<SkipLink[]>([
      { label: "Zum Inhalt", to: "#main" },
      { label: "Zum Fußbereich", to: "#footer" },
    ]);

    const RegisterSkipLinks = defineComponent({
      setup() {
        const registeredLinks = useSkipLinks(skipLinks);
        links.value = registeredLinks.value;

        watchEffect(() => {
          links.value = registeredLinks.value;
        });

        return () => h("div");
      },
    });

    await mountSuspended(
      defineComponent({
        setup() {
          provideSkipLinks();
          return () => h(RegisterSkipLinks);
        },
      }),
    );

    expect(links.value).toEqual(skipLinks.value);

    skipLinks.value = [{ label: "Zu den Filtern", to: "#filters" }];
    await nextTick();

    expect(links.value).toEqual(skipLinks.value);
  });

  it("throws when no registry is provided", async () => {
    const app = createApp({ render: () => null });

    expect(() =>
      app.runWithContext(() => {
        useSkipLinks([]);
      }),
    ).toThrow("Skip links registry is not available");
  });
});
