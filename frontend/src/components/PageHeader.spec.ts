import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import PageHeader from "./PageHeader.vue";

describe("PageHeader", () => {
  it("shows the title and content", async () => {
    await renderSuspended(PageHeader, {
      props: { title: "Titel" },
      slots: { default: "Inhalt" },
    });
    expect(
      screen.getByRole("heading", { level: 1, name: "Titel" }),
    ).toBeInTheDocument();
    expect(screen.getByText("Inhalt")).toBeInTheDocument();
  });
});
