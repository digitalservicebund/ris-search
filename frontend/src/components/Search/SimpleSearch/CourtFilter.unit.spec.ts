import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { useSimpleSearchParamsStore } from "~/stores/searchParams";
import CourtFilter from "~/components/Search/SimpleSearch/CourtFilter.vue";
import { DocumentKind } from "@/types";
import { mountSuspended } from "@nuxt/test-utils/runtime";
import { setStoreValues } from "~/utils/testing/piniaUtils";
import type { VueWrapper } from "@vue/test-utils";
import { flushPromises } from "@vue/test-utils";
import { defaultSuggestions } from "./CourtFilter.data";

const mockData = [{ id: "TG Berlin", label: "Tagesgericht Berlin", count: 1 }];
const mockFetch = vi.fn().mockResolvedValue(mockData);
vi.stubGlobal("$fetch", mockFetch);

interface AutoCompleteStub {
  suggestions: typeof mockData;
  modelValue: (typeof mockData)[0];
}

async function getWrapper() {
  return await mountSuspended(CourtFilter, {
    global: {
      stubs: {
        AutoComplete: true,
      },
    },
  });
}

async function flushSearchDebounced(wrapper: VueWrapper) {
  await (
    wrapper as VueWrapper<{
      searchDebounced: { flush: () => Promise<void> };
    }>
  ).vm.searchDebounced.flush(); // run immediately
}

describe("court autocomplete", () => {
  it("is not visible by default", async () => {
    const wrapper = await getWrapper();
    expect(wrapper.findComponent("auto-complete-stub").exists()).toBe(false);
  });

  describe("when documentKind is set to CaseLaw", async () => {
    beforeEach(async () => {
      await setStoreValues({
        category: DocumentKind.CaseLaw,
      });
    });
    afterEach(() => mockFetch.mockClear());

    it("renders an empty field if nothing is set", async () => {
      const wrapper = await getWrapper();
      expect(wrapper.find("auto-complete-stub").exists()).toBe(true);
    });

    it("calls the API with the typed prefix and sets the results", async () => {
      const wrapper = await getWrapper();
      const autoComplete = wrapper.findComponent(
        "auto-complete-stub",
      ) as VueWrapper<AutoCompleteStub>;
      autoComplete.vm.$emit("complete", { query: "a" });
      expect(mockFetch).not.toHaveBeenCalled();
      await flushSearchDebounced(wrapper);
      expect(mockFetch).toHaveBeenCalledWith(
        expect.anything(),
        expect.objectContaining({ params: { prefix: "a" } }),
      );

      expect(autoComplete.vm.suggestions).toStrictEqual(mockData);
    });

    describe("when the dropdown is invoked", async () => {
      it("fetches results with the current value, if one is set", async () => {
        await setStoreValues({ court: "some court" });
        const wrapper = await getWrapper();
        const autoComplete = wrapper.findComponent(
          "auto-complete-stub",
        ) as VueWrapper<AutoCompleteStub>;
        autoComplete.vm.$emit("complete", {});
        await flushSearchDebounced(wrapper);
        expect(mockFetch).toHaveBeenCalledWith(
          expect.anything(),
          expect.objectContaining({
            params: {
              prefix: "some court",
            },
          }),
        );
        await flushPromises();

        expect(autoComplete.vm.suggestions).toStrictEqual(mockData);
      });

      it("uses default suggestions, if no court is set", async () => {
        const store = useSimpleSearchParamsStore();
        store.$reset();
        store.category = DocumentKind.CaseLaw;
        await nextTick();
        const wrapper = await getWrapper();
        const autoComplete = wrapper.findComponent(
          "auto-complete-stub",
        ) as VueWrapper<AutoCompleteStub>;
        autoComplete.vm.$emit("complete", {});
        await flushPromises();
        expect(mockFetch).not.toHaveBeenCalled();

        expect(autoComplete.vm.suggestions).toStrictEqual(defaultSuggestions);
      });
    });

    it("it displays the store value", async () => {
      const wrapper = await getWrapper();
      const courtId = mockData[0].id;
      await setStoreValues({ court: courtId });
      const autoComplete = wrapper.findComponent(
        "auto-complete-stub",
      ) as VueWrapper<AutoCompleteStub>;
      expect(autoComplete.vm.modelValue).toStrictEqual(courtId);
    });

    it("updates the store", async () => {
      const wrapper = await getWrapper();
      const autoComplete = wrapper.findComponent(
        "auto-complete-stub",
      ) as VueWrapper<AutoCompleteStub>;
      const courtId = "THIS SHOULD BE UPDATED";
      autoComplete.vm.$emit("update:modelValue", courtId);
      await nextTick();

      const store = useSimpleSearchParamsStore();
      expect(store.court).toBe(courtId);
    });
  });
});
