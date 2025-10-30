import { render, screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import ValidityDatesMetadataFields from "./ValidityDatesMetadataFields.vue";
import { parseDateGermanLocalTime } from "~/utils/dateFormatting";

describe("ValidityDatesMetadataFields.vue", () => {
  it("displays formatted 'valid from' date if present", () => {
    render(ValidityDatesMetadataFields, {
      props: {
        validFrom: parseDateGermanLocalTime("2025-01-01"),
      },
    });

    expect(screen.getByText("Gültig ab")).toBeInTheDocument();
    expect(screen.getByLabelText("Gültig ab")).toHaveTextContent("01.01.2025");
  });

  it("shows placeholder if 'valid from' date is undefined", () => {
    render(ValidityDatesMetadataFields);

    expect(screen.getByText("Gültig ab")).toBeInTheDocument();
    expect(screen.getByLabelText("Gültig ab")).toHaveTextContent("—");
  });

  it("displays formatted 'valid to' date if present", () => {
    render(ValidityDatesMetadataFields, {
      props: {
        validTo: parseDateGermanLocalTime("2025-06-01"),
      },
    });

    expect(screen.getByText("Gültig bis")).toBeInTheDocument();
    expect(screen.getByLabelText("Gültig bis")).toHaveTextContent("1.06.2025");
  });

  it("shows placeholder if 'valid to' date is undefined", () => {
    render(ValidityDatesMetadataFields);

    expect(screen.getByText("Gültig bis")).toBeInTheDocument();
    expect(screen.getByLabelText("Gültig bis")).toHaveTextContent("—");
  });
});
