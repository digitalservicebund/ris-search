import { ref } from "vue";
import type { TextMatch } from "~/types/api";
import {
  type ExtendedTextMatch,
  useSearchResultSections,
} from "./useSearchResultSections";

const fields = new Map([
  ["first", { id: "first-id", title: "First" }],
  ["second", { id: "second-id", title: "Second" }],
  ["third", { id: "third-id", title: "Third" }],
]);

describe("useSearchResultSections", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("returns only matches whose names are in the fields map", async () => {
    const textMatches: TextMatch[] = [
      { name: "first", text: "text 1" },
      { name: "unknown", text: "text u" },
      { name: "second", text: "text 2" },
    ];
    const previewSections = useSearchResultSections(textMatches, fields);

    expect(previewSections.value.map((s) => s.id)).toEqual([
      "first-id",
      "second-id",
    ]);
  });

  it("orders sections according to the fields map", async () => {
    const textMatches: TextMatch[] = [
      { name: "third", text: "text 3" },
      { name: "first", text: "text 1" },
      { name: "second", text: "text 2" },
    ];
    const previewSections = useSearchResultSections(textMatches, fields);

    expect(previewSections.value.map((s) => s.id)).toEqual([
      "first-id",
      "second-id",
      "third-id",
    ]);
  });

  it("adds id and title from the fields map to each section", async () => {
    const textMatches: TextMatch[] = [{ name: "second", text: "some text" }];
    const previewSections = useSearchResultSections(textMatches, fields);

    expect(previewSections.value[0]).toMatchObject({
      id: "second-id",
      title: "Second",
    });
  });

  it("returns an empty array when there are no text matches", async () => {
    const previewSections = useSearchResultSections([], fields);

    expect(previewSections.value).toEqual([]);
  });

  it("limits results when a section limit is provided", async () => {
    const textMatches: TextMatch[] = [
      { name: "first", text: "text 1" },
      { name: "second", text: "text 2" },
      { name: "third", text: "text 3" },
    ];
    const previewSections = useSearchResultSections(textMatches, fields, 2);

    expect(previewSections.value).toHaveLength(2);
    expect(previewSections.value.map((s: ExtendedTextMatch) => s.id)).toEqual([
      "first-id",
      "second-id",
    ]);
  });

  it("returns all results when no section limit is provided", async () => {
    const textMatches: TextMatch[] = [
      { name: "first", text: "text 1" },
      { name: "second", text: "text 2" },
      { name: "third", text: "text 3" },
    ];
    const previewSections = useSearchResultSections(textMatches, fields);

    expect(previewSections.value).toHaveLength(3);
  });

  it("accepts a reactive text matches ref and reacts to changes", async () => {
    const textMatches = ref<TextMatch[]>([{ name: "first", text: "initial" }]);
    const previewSections = useSearchResultSections(textMatches, fields);

    expect(previewSections.value).toHaveLength(1);

    textMatches.value = [
      { name: "first", text: "updated" },
      { name: "second", text: "also here" },
    ];
    await nextTick();

    expect(previewSections.value).toHaveLength(2);
  });
});
