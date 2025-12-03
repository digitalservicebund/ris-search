import { screen, render } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import DetailsList from "./DetailsList.vue";

describe("DetailsList", () => {
  it("renders slot", async () => {
    render(DetailsList, { slots: { default: "Info" } });
    expect(screen.getByText("Info")).toBeInTheDocument();
  });
});
