import { render, screen } from "@testing-library/vue";
import { describe, expect, it, vi } from "vitest";
import NormMetadataFields from "./NormMetadataFields.vue";
import { parseDateGermanLocalTime } from "~/utils/dateFormatting";
import type { ValidityStatus } from "~/utils/norm";

const mocks = vi.hoisted(() => {
  return {
    usePrivateFeaturesFlag: vi.fn().mockReturnValue(false),
  };
});

vi.mock("~/composables/usePrivateFeaturesFlag", () => {
  return { usePrivateFeaturesFlag: mocks.usePrivateFeaturesFlag };
});

describe("NormMetadataFields", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    vi.restoreAllMocks();
  });

  it("displays abbreviation", () => {
    const expectedAbbreviation = "FooBar";
    render(NormMetadataFields, {
      props: {
        abbreviation: expectedAbbreviation,
      },
    });

    expect(screen.getByText("Abkürzung")).toBeInTheDocument();
    expect(screen.getByLabelText("Abkürzung")).toHaveTextContent("FooBar");
  });

  it("does not show abbreviation if abbreviation is undefined", () => {
    render(NormMetadataFields);
    expect(screen.queryByText("Abkürzung")).not.toBeInTheDocument();
    expect(screen.queryByText("FooBar")).not.toBeInTheDocument();
  });

  it.each([
    ["InForce", "Aktuell gültig"],
    ["Expired", "Außer Kraft"],
    ["FutureInForce", "Zukünftig in Kraft"],
    [undefined, "—"],
  ])("status %s is displayed as %s", (status, expected) => {
    render(NormMetadataFields, {
      props: {
        status: status as ValidityStatus,
      },
    });

    expect(screen.getByText("Status")).toBeInTheDocument();
    expect(screen.getByLabelText("Status")).toHaveTextContent(expected);
  });

  it("shows validity dates metadata fields", () => {
    mocks.usePrivateFeaturesFlag.mockReturnValue(true);
    const validFrom = parseDateGermanLocalTime("2025-01-01");
    const validTo = parseDateGermanLocalTime("2025-06-01");

    render(NormMetadataFields, {
      props: {
        validFrom: validFrom,
        validTo: validTo,
      },
    });

    expect(screen.getByText("Gültig ab")).toBeInTheDocument();
    expect(screen.getByLabelText("Gültig ab")).toHaveTextContent("01.01.2025");
    expect(screen.getByText("Gültig bis")).toBeInTheDocument();
    expect(screen.getByLabelText("Gültig bis")).toHaveTextContent("01.06.2025");
  });

  it("hides 'valid from' and 'valid to' fields on prototype", () => {
    mocks.usePrivateFeaturesFlag.mockReturnValue(false);

    expect(screen.queryByText("Gültig ab")).not.toBeInTheDocument();
    expect(screen.queryByText("01.01.2025")).not.toBeInTheDocument();
    expect(screen.queryByText("Gültig bis")).not.toBeInTheDocument();
    expect(screen.queryByText("01.06.2025")).not.toBeInTheDocument();
  });
});
