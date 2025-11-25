import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import FeatureCard from "./FeatureCard.vue";

describe("FeatureCard", () => {
  it("renders content and applies custom class", async () => {
    const { container } = await renderSuspended(FeatureCard, {
      props: { innerClass: "custom-x" },
      slots: { default: "Hello" },
    });
    expect(screen.getByText("Hello")).toBeInTheDocument();
    expect(container.querySelector(".custom-x")).toBeTruthy();
  });
});
