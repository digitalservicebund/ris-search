import { mount, flushPromises } from "@vue/test-utils";
import { nextTick } from "vue";
import { vi } from "vitest";
import AdvancedSearch from "./AdvancedSearch.vue";
import * as searchService from "@/services/searchService";
import { mountSuspended } from "@nuxt/test-utils/runtime";
import type { AxiosResponse } from "axios";
import { DocumentKind } from "~/types";

type SearchResultPage = {
  member: [{ id: string; title: string }];
};

interface AdvancedSearchState {
  currentSearchMode: string;
  searchInput: string;
  updateQuery: (query: string) => void;
  searchResults: SearchResultPage[];
  currentPage: SearchResultPage;
  isLoading: boolean;
  currentMessage: string;
  pageNumber: number;
  handleReset: () => void;
  hasError: boolean;
  message: string;
  currentSorting: string;
  currentDocumentKind: string;
  disableAfterConfirmation: () => void;
  modeChangeConfirmed: boolean;
  builderUsed: boolean;
  textEntered: boolean;
  documentKind: DocumentKind;
  search: () => Promise<void>;
  updatePage: (pageNumber: number) => Promise<void>;
}

interface InstanceWithSetupState {
  setupState: AdvancedSearchState;
}

const testPage: SearchResultPage = {
  member: [{ id: "1", title: "test record" }],
};

const stubs = {
  DropdownInput: {
    template: "<div></div>",
    props: ["modelValue", "items", "disabled", "id", "aria-label"],
  },
  SortSelect: {
    template: "<div>SortSelect</div>",
    props: ["modelValue", "documentKind"],
  },
  SearchResult: {
    template: "<div>SearchResult</div>",
    props: ["searchResult"],
  },
  InputText: {
    template: `<input
            :value="modelValue"
            @input="$emit('update:modelValue', $event.target.value)"
            @keyup.enter="$emit('enter-released')"
            :disabled="disabled"
            id="searchInput"
            placeholder="Suchanfrage" />`,
    props: ["modelValue", "disabled"],
  },
  Button: {
    template: `<button :disabled="disabled" @click="$emit('click')">
          <slot />
        </button>`,
    props: ["disabled", "label"],
  },
  SearchQueryBuilder: {
    name: "SearchQueryBuilder",
    template: '<div data-test="search-query-builder">SearchQueryBuilder</div>',
    props: ["currentDocumentKind"],
  },
  Pagination: {
    name: "Pagination",
    template: "<div><slot /></div>",
    props: ["isLoading", "page"],
  },
};

const wrapper = mount(AdvancedSearch, { global: { stubs } });

describe("AdvancedSearch.vue", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("should disable user input initially if component is not ready", async () => {
    const notReadyComponent = await mountSuspended(AdvancedSearch, {
      global: { stubs },
    });
    expect(notReadyComponent.html()).toContain("Lade Suche...");
  });

  it("should enable user input after component is ready", async () => {
    expect(wrapper.html()).not.toContain("Lade Suche...");
  });

  it("handleSearchSubmit should show error if search input is empty", async () => {
    const state = (wrapper.vm.$ as unknown as InstanceWithSetupState)
      .setupState;
    state.currentSearchMode = "text";
    state.searchInput = "";
    await nextTick();

    const input = wrapper.find("#searchInput");
    await input.trigger("keyup.enter");
    await flushPromises();

    expect(state.hasError).toBe(true);
    expect(wrapper.html()).toContain("Bitte geben Sie eine Suchanfrage ein");
  });

  it("handleSearchSubmit should perform search when input is provided", async () => {
    const state = (wrapper.vm.$ as unknown as InstanceWithSetupState)
      .setupState;
    state.currentSearchMode = "text";
    state.searchInput = "test query";

    vi.spyOn(searchService, "advancedSearch").mockResolvedValue({
      data: testPage,
    } as AxiosResponse);

    await nextTick();
    const input = wrapper.find("#searchInput");
    await input.trigger("keyup.enter");
    await flushPromises();

    expect(state.isLoading).toBe(false);
    expect(state.message).toBe("");
    expect(state.currentPage).toEqual(testPage);
    expect(state.pageNumber).toBe(0);
  });

  it("search function should set error on failure", async () => {
    const state = (wrapper.vm.$ as unknown as InstanceWithSetupState)
      .setupState;
    state.currentSearchMode = "text";
    state.searchInput = "test query";

    const errorMessage = "Search failed";
    vi.spyOn(searchService, "advancedSearch").mockRejectedValue(
      new Error(errorMessage),
    );

    const input = wrapper.find("#searchInput");
    await input.trigger("keyup.enter");
    await flushPromises();

    expect(state.message).toBe(errorMessage);
    expect(state.hasError).toBe(true);
    expect(state.isLoading).toBe(false);
  });

  it("handleReset should clear search inputs and errors", async () => {
    const state = (wrapper.vm.$ as unknown as InstanceWithSetupState)
      .setupState;
    state.searchInput = "test query";
    state.currentPage = testPage;
    state.pageNumber = 2;
    state.message = "test message";
    state.hasError = true;
    state.handleReset();
    await nextTick();

    expect(state.searchInput).toBe("");
    expect(state.currentPage).toBeUndefined();
    expect(state.pageNumber).toBe(0);
    expect(state.message).toBe("");
    expect(state.hasError).toBe(false);
  });

  it("watch on currentSorting should trigger search when searchInput exists", async () => {
    const state = (wrapper.vm.$ as unknown as InstanceWithSetupState)
      .setupState;
    state.searchInput = "query";

    const advancedSearchSpy = vi
      .spyOn(searchService, "advancedSearch")
      .mockResolvedValue({ data: { member: [] } } as AxiosResponse);

    state.currentSorting = "newSort";
    await nextTick();
    await flushPromises();

    expect(advancedSearchSpy).toHaveBeenCalledWith({
      query: "query",
      itemsPerPage: 100,
      pageNumber: state.pageNumber,
      sort: "newSort",
      documentKind: state.currentDocumentKind,
    });
  });

  it("watch on currentDocumentKind should reset searchInput and currentPage", async () => {
    const state = (wrapper.vm.$ as unknown as InstanceWithSetupState)
      .setupState;
    state.searchInput = "query";
    state.currentPage = testPage;

    state.currentDocumentKind = "R";
    await nextTick();

    expect(state.searchInput).toBe("");
    expect(state.currentPage).toBeUndefined();
  });

  describe("disableAfterConfirmation", () => {
    it("should prompt confirmation and set textEntered if confirmed", async () => {
      const state = (wrapper.vm.$ as unknown as InstanceWithSetupState)
        .setupState;
      state.builderUsed = true;
      state.modeChangeConfirmed = false;

      vi.spyOn(window, "confirm").mockReturnValue(true);
      state.disableAfterConfirmation();

      expect(state.modeChangeConfirmed).toBe(true);
      expect(state.textEntered).toBe(true);
    });

    it("should not set textEntered to true if confirmation is not accepted", async () => {
      const state = (wrapper.vm.$ as unknown as InstanceWithSetupState)
        .setupState;
      state.builderUsed = true;
      state.modeChangeConfirmed = false;
      state.textEntered = false;

      vi.spyOn(window, "confirm").mockReturnValue(false);
      state.disableAfterConfirmation();

      expect(state.modeChangeConfirmed).toBe(false);
      expect(state.textEntered).toBe(false);
    });

    it("should set textEntered if builderUsed is false", async () => {
      const state = (wrapper.vm.$ as unknown as InstanceWithSetupState)
        .setupState;
      state.builderUsed = false;
      state.modeChangeConfirmed = false;

      state.disableAfterConfirmation();
      expect(state.textEntered).toBe(true);
    });
  });

  it("updatePage should update pageNumber, trigger search, and scroll to top", async () => {
    vi.useFakeTimers();
    const state = (wrapper.vm.$ as unknown as InstanceWithSetupState)
      .setupState;

    const searchSpy = vi.spyOn(state, "search").mockResolvedValue(undefined);
    const scrollToSpy = vi
      .spyOn(window, "scrollTo")
      .mockImplementation(() => {});

    await state.updatePage(2);
    expect(state.pageNumber).toBe(2);
    await flushPromises();

    vi.advanceTimersByTime(100);
    expect(scrollToSpy).toHaveBeenCalledWith({
      top: 0,
      behavior: "smooth",
    });

    searchSpy.mockRestore();
    scrollToSpy.mockRestore();
    vi.useRealTimers();
  });

  it("updateQuery should set builderUsed to true and update searchInput", async () => {
    const state = (wrapper.vm.$ as unknown as InstanceWithSetupState)
      .setupState;
    state.searchInput = "";
    state.builderUsed = false;

    state.updateQuery("new lucene query");
    expect(state.builderUsed).toBe(true);
    expect(state.searchInput).toBe("new lucene query");
  });

  describe("computed documentKind", () => {
    it("should return the selected document kind if it is either R or N", () => {
      const state = (wrapper.vm.$ as unknown as InstanceWithSetupState)
        .setupState;
      state.currentDocumentKind = "R";
      const computedValue = state.documentKind;
      expect(computedValue).toBe("R");
    });

    it("should return all documents if selected document kind is invalid", () => {
      const state = (wrapper.vm.$ as unknown as InstanceWithSetupState)
        .setupState;
      state.currentDocumentKind = "invalid";
      const computedValue = state.documentKind;
      expect(computedValue).toBe(DocumentKind.All);
    });
  });

  describe("Template rendering", () => {
    it('should render InputText when currentSearchMode is "text"', async () => {
      const state = (wrapper.vm.$ as unknown as InstanceWithSetupState)
        .setupState;
      state.currentSearchMode = "text";
      await nextTick();
      expect(wrapper.find("#searchInput").exists()).toBe(true);
    });

    it("should render the search and reset button only when searchInput is not empty", async () => {
      const state = (wrapper.vm.$ as unknown as InstanceWithSetupState)
        .setupState;
      state.currentSearchMode = "text";
      state.searchInput = "";
      await nextTick();
      expect(wrapper.find('[aria-label="Suchen"]').exists()).toBe(false);
      expect(wrapper.find('[aria-label="Zurücksetzen"]').exists()).toBe(false);

      state.searchInput = "some query";
      await nextTick();
      expect(wrapper.find('[aria-label="Suchen"]').exists()).toBe(true);
      expect(wrapper.find('[aria-label="Zurücksetzen"]').exists()).toBe(true);
    });

    it("should render Pagination when searchInput is not empty and there are some search results", async () => {
      const state = (wrapper.vm.$ as unknown as InstanceWithSetupState)
        .setupState;
      state.searchInput = "some query";
      state.searchResults = [testPage];
      await nextTick();
      expect(wrapper.findComponent({ name: "Pagination" }).exists()).toBe(true);
    });
  });
});
