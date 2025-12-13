import { renderSuspended } from "@nuxt/test-utils/runtime";
import userEvent from "@testing-library/user-event";
import { screen } from "@testing-library/vue";
import { describe, it, expect, vi } from "vitest";
import BackToTopLink from "./BackToTopLink.vue";

describe("BackToTopLink", () => {
  it("renders the link with correct attributes", async () => {
    await renderSuspended(BackToTopLink);

    expect(
      screen.getByRole("link", { name: "zum Seitenanfang" }),
    ).toHaveAttribute("href", expect.stringContaining("#top"));
  });

  it("scrolls to the top element when clicked", async () => {
    const mockScrollIntoView = vi.fn();

    // @ts-expect-error -- Doesn't need to be a real HTML element
    vi.spyOn(document, "getElementById").mockImplementation((id) => {
      if (id === "top") return { scrollIntoView: mockScrollIntoView };
      else return null;
    });

    const user = userEvent.setup();

    await renderSuspended(BackToTopLink);

    await user.click(screen.getByRole("link", { name: "zum Seitenanfang" }));

    expect(document.getElementById).toHaveBeenCalledWith("top");
    expect(mockScrollIntoView).toHaveBeenCalledWith({ behavior: "smooth" });
  });

  it("does not throw error if header element is not found", async () => {
    vi.spyOn(document, "getElementById").mockReturnValue(null);
    let hasError = false;

    const user = userEvent.setup();

    await renderSuspended(
      defineComponent({
        components: { BackToTopLink },
        setup() {
          onErrorCaptured(() => {
            hasError = true;
          });
        },
        template: `<BackToTopLink />`,
      }),
    );

    await user.click(screen.getByRole("link", { name: "zum Seitenanfang" }));

    expect(hasError).toBe(false);
  });
});
