import { renderSuspended } from "@nuxt/test-utils/runtime";
import { userEvent } from "@testing-library/user-event";
import { screen, waitFor } from "@testing-library/vue";
import { afterEach, describe, expect, it, vi } from "vitest";
import CourtFilter from "~/components/search/CourtFilter.vue";
import { courtFilterDefaultSuggestions } from "~/utils/search/courtFilter";

const mockData = [{ id: "TG Berlin", label: "Tagesgericht Berlin", count: 1 }];

vi.mock("~/plugins/risBackend", () => ({
  default: vi.fn(),
  extendOnRequest: (...cbs: unknown[]) => cbs,
}));

describe("court autocomplete", () => {
  afterEach(() => {
    vi.resetAllMocks();
  });

  it("is not visible by default", async () => {
    await renderSuspended(CourtFilter);

    expect(screen.queryByLabelText("Gericht")).not.toBeInTheDocument();
  });

  it("is not visible for non-CaseLaw categories", async () => {
    await renderSuspended(CourtFilter);

    expect(screen.queryByLabelText("Gericht")).not.toBeInTheDocument();
  });

  describe("when category is set to CaseLaw", () => {
    it("renders an empty input field", async () => {
      await renderSuspended(CourtFilter);

      const input = screen.getByRole("combobox");
      expect(input).toBeInTheDocument();
      expect(input).toHaveValue("");
    });

    it("displays the passed model value", async () => {
      const courtId = mockData[0]?.id;
      await renderSuspended(CourtFilter, {
        props: {
          modelValue: courtId,
        },
      });

      const input = screen.getByRole("combobox");
      expect(input).toHaveValue(courtId);
    });

    it("calls the API when typing and shows suggestions", async () => {
      const fetchSpy = vi
        .spyOn(globalThis as any, "$fetch")
        .mockResolvedValue(mockData);
      const user = userEvent.setup();

      await renderSuspended(CourtFilter);

      const input = screen.getByRole("combobox");
      await user.type(input, "Ber");

      await waitFor(() => {
        expect(fetchSpy).toHaveBeenCalledWith(
          expect.anything(),
          expect.objectContaining({ params: { prefix: "Ber" } }),
        );
      });

      expect(screen.getByText("Tagesgericht Berlin")).toBeInTheDocument();
    });

    it("shows default suggestions when dropdown is opened without input", async () => {
      const fetchSpy = vi
        .spyOn(globalThis as any, "$fetch")
        .mockResolvedValue(mockData);
      const user = userEvent.setup();

      await renderSuspended(CourtFilter);

      await user.click(
        screen.getByRole("button", {
          name: "Vorschläge anzeigen",
        }),
      );

      // Default suggestions should appear without API call
      await waitFor(() => {
        expect(fetchSpy).not.toHaveBeenCalled();
      });

      // Check that default suggestions are shown
      for (const suggestion of courtFilterDefaultSuggestions) {
        expect(screen.getByText(suggestion.label)).toBeInTheDocument();
      }
    });

    it("emits update when selecting a suggestion", async () => {
      const user = userEvent.setup();

      const { emitted } = await renderSuspended(CourtFilter);

      const dropdownButton = screen.getByRole("button");
      await user.click(dropdownButton);

      const firstSuggestion = courtFilterDefaultSuggestions[0]!;
      await user.click(screen.getByText(firstSuggestion.label));

      expect(emitted("update:modelValue")).toContainEqual([firstSuggestion.id]);
    });

    it("uses current value as search prefix when dropdown is opened", async () => {
      const fetchSpy = vi
        .spyOn(globalThis as any, "$fetch")
        .mockResolvedValue(mockData);
      const user = userEvent.setup();

      await renderSuspended(CourtFilter, {
        props: {
          modelValue: "existing court",
        },
      });

      await user.click(
        screen.getByRole("button", {
          name: "Vorschläge anzeigen",
        }),
      );

      await waitFor(() => {
        expect(fetchSpy).toHaveBeenCalledWith(
          expect.anything(),
          expect.objectContaining({ params: { prefix: "existing court" } }),
        );
      });
    });
  });
});
