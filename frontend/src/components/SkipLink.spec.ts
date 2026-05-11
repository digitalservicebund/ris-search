import { renderSuspended } from "@nuxt/test-utils/runtime";
import { userEvent } from "@testing-library/user-event";
import { screen } from "@testing-library/vue";
import { describe, expect, it } from "vitest";
import SkipLink from "./SkipLink.vue";

describe("SkipLink", () => {
  it("focuses the target element when activated", async () => {
    const user = userEvent.setup();

    await renderSuspended(SkipLink, {
      props: { to: "#target" },
      slots: { default: () => "Zum Inhalt springen" },
    });

    const target = document.createElement("main");
    target.id = "target";
    document.body.append(target);

    await user.click(screen.getByRole("link", { name: "Zum Inhalt springen" }));

    expect(target).toHaveFocus();
    expect(target).toHaveAttribute("tabindex", "-1");
    expect(target.className).toContain("skipLinkTarget");

    target.remove();
  });
});
