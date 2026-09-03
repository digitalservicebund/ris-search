import { renderSuspended } from "@nuxt/test-utils/runtime";
import { userEvent } from "@testing-library/user-event";
import { screen, waitFor } from "@testing-library/vue";
import { afterEach, describe, expect, it, vi } from "vitest";
import CourtFilter from "~/components/search/CourtFilter.vue";
import { courtFilterDefaultSuggestions } from "~/utils/search/courtFilter";

const mockData = [{ id: "TG Berlin", label: "Tagesgericht Berlin", count: 1 }];

const mockFetch = vi.hoisted(() => vi.fn());

// The component fetches via the $risBackend injection, so the plugin providing
// it is replaced rather than $fetch itself.
vi.mock("~/plugins/risBackend", () => ({
  default: defineNuxtPlugin(() => ({ provide: { risBackend: mockFetch } })),
  extendOnRequest: (...cbs: unknown[]) => cbs,
}));

describe("court autocomplete", () => {
  afterEach(() => {
    vi.resetAllMocks();
  });

  it("exposes the label to assistive technology and shows hint text", async () => {
    await renderSuspended(CourtFilter);

    const input = screen.getByRole("combobox");
    expect(input).toHaveAccessibleName("Gericht");
    expect(
      screen.getByText("Bundesgericht auswählen oder weiteres Gericht suchen"),
    ).toBeVisible();
  });

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
    mockFetch.mockResolvedValue(mockData);
    const user = userEvent.setup();

    await renderSuspended(CourtFilter);

    const input = screen.getByRole("combobox");
    await user.type(input, "Ber");

    await waitFor(() => {
      expect(mockFetch).toHaveBeenCalledWith("/v1/case-law/courts", {
        query: { prefix: "Ber" },
      });
    });

    expect(screen.getByText("Tagesgericht Berlin")).toBeInTheDocument();
  });

  it("shows default suggestions when dropdown is opened without input", async () => {
    mockFetch.mockResolvedValue(mockData);
    const user = userEvent.setup();

    await renderSuspended(CourtFilter);

    await user.click(
      screen.getByRole("button", {
        name: "Vorschläge anzeigen",
      }),
    );

    // Default suggestions should appear without API call
    await waitFor(() => {
      expect(mockFetch).not.toHaveBeenCalled();
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
    mockFetch.mockResolvedValue(mockData);
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
      expect(mockFetch).toHaveBeenCalledWith("/v1/case-law/courts", {
        query: { prefix: "existing court" },
      });
    });
  });
});
