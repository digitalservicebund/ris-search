import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import ContentWrapper from "./ContentWrapper.vue";

describe("ContentWrapper", () => {
  it("renders without border by default", async () => {
    await renderSuspended(ContentWrapper, { slots: { default: "Content" } });
    const el = screen.getByText("Content").closest("div");
    expect(el).not.toHaveClass("border-b");
  });

  it("adds border classes when border prop is true", async () => {
    await renderSuspended(ContentWrapper, {
      props: { border: true },
      slots: { default: "Content" },
    });
    const el = screen.getByText("Content").closest("div");
    expect(el).toHaveClass("border-b");
  });
});
