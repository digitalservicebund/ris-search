import { renderSuspended } from "@nuxt/test-utils/runtime";
import { userEvent } from "@testing-library/user-event";
import { screen } from "@testing-library/vue";
import { describe, expect, it } from "vitest";
import NormHeadingFootnotes from "./NormHeadingFootnotes.vue";

describe("NormHeadingFootnotes", () => {
  it("shows short footnotes in full", async () => {
    await renderSuspended(NormHeadingFootnotes, {
      props: {
        html: "content",
        textLength: 100,
      },
    });

    expect(screen.getByText("content")).toBeInTheDocument();
    expect(screen.queryByRole("button")).not.toBeInTheDocument();
  });

  it("shows a button to display longer footnotes", async () => {
    const user = userEvent.setup();

    await renderSuspended(NormHeadingFootnotes, {
      props: {
        html: "longer content",
        textLength: 500,
      },
    });

    await user.click(screen.getByRole("button", { name: "Fußnote anzeigen" }));

    expect(screen.getAllByText("longer content")[0]).toBeVisible();

    await user.click(
      screen.getByRole("button", { name: "Fußnote ausblenden" }),
    );

    expect(screen.queryAllByText("longer content")[0]).not.toBeVisible();
  });
});
