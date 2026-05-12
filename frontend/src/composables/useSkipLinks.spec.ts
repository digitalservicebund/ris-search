import { renderSuspended } from "@nuxt/test-utils/runtime";
import { describe, expect, it } from "vitest";
import { nextTick } from "vue";
import {
  provideSkipLinks,
  useSkipLinks,
  type SkipLink,
  type SkipLinksRegistry,
} from "./useSkipLinks";

describe("provideSkipLinks", () => {
  async function mountProvider() {
    let registry: SkipLinksRegistry | undefined;
    await renderSuspended(
      defineComponent({
        setup() {
          registry = provideSkipLinks();
          return () => h("div");
        },
      }),
    );
    return registry!;
  }

  it("registers links", async () => {
    const { links, register } = await mountProvider();
    register([{ label: "Main content", to: "#main" }]);
    expect(links.value).toEqual([{ label: "Main content", to: "#main" }]);
  });

  it("accumulates links from multiple registrations", async () => {
    const { links, register } = await mountProvider();
    register([{ label: "Main content", to: "#main" }]);
    register([{ label: "Navigation", to: "#nav" }]);
    expect(links.value).toHaveLength(2);
    expect(links.value).toContainEqual({ label: "Main content", to: "#main" });
    expect(links.value).toContainEqual({ label: "Navigation", to: "#nav" });
  });

  it("removes links when the cleanup function is called", async () => {
    const { links, register } = await mountProvider();
    const cleanup = register([{ label: "Main content", to: "#main" }]);
    cleanup();
    expect(links.value).toEqual([]);
  });

  it("cleanup only removes links registered with the original call", async () => {
    const { links, register } = await mountProvider();
    const cleanup = register([{ label: "Main content", to: "#main" }]);
    register([{ label: "Navigation", to: "#nav" }]);
    cleanup();
    expect(links.value).toEqual([{ label: "Navigation", to: "#nav" }]);
  });
});

describe("useSkipLinks", () => {
  async function mountProvider(links?: MaybeRefOrGetter<SkipLink[]>) {
    let registry: SkipLinksRegistry | undefined;
    let result: Ref<SkipLink[]> | undefined;

    const child = defineComponent({
      setup() {
        result = useSkipLinks(links);
        return () => h("div");
      },
    });

    const wrapper = await renderSuspended(
      defineComponent({
        setup() {
          registry = provideSkipLinks();
          return () => h(child);
        },
      }),
    );

    return { wrapper, registry: registry!, result: result! };
  }

  it("throws when no registry is provided", async () => {
    await expect(
      renderSuspended(
        defineComponent({
          setup() {
            useSkipLinks([]);
          },
          template: "<div></div>",
        }),
      ),
    ).rejects.toThrow("skip links registry has not been provided");
  });

  it("returns the registry links ref", async () => {
    const { registry, result } = await mountProvider([]);
    expect(result).toBe(registry.links);
  });

  it("registers provided links immediately", async () => {
    const { registry } = await mountProvider([{ label: "Main", to: "#main" }]);
    expect(registry.links.value).toEqual([{ label: "Main", to: "#main" }]);
  });

  it("re-registers when reactive links change", async () => {
    const links = ref<SkipLink[]>([{ label: "Main", to: "#main" }]);
    const { registry } = await mountProvider(links);

    links.value = [{ label: "Nav", to: "#nav" }];
    await nextTick();

    expect(registry.links.value).toEqual([{ label: "Nav", to: "#nav" }]);
  });

  it("removes old links before registering new ones on update", async () => {
    const links = ref<SkipLink[]>([{ label: "Main", to: "#main" }]);
    const { registry } = await mountProvider(links);

    links.value = [{ label: "Nav", to: "#nav" }];
    await nextTick();

    expect(registry.links.value).not.toContainEqual({
      label: "Main",
      to: "#main",
    });
  });

  it("cleans up links on unmount", async () => {
    const { wrapper, registry } = await mountProvider([
      { label: "Main", to: "#main" },
    ]);
    wrapper.unmount();
    expect(registry.links.value).toEqual([]);
  });

  it("does not register links when called without arguments", async () => {
    const { registry } = await mountProvider();
    expect(registry.links.value).toEqual([]);
  });
});
