import { renderSuspended } from "@nuxt/test-utils/runtime";
import { userEvent } from "@testing-library/user-event";
import { screen } from "@testing-library/vue";
import { describe, expect, it } from "vitest";
import HeadingFootnotes from "./HeadingFootnotes.vue";

describe("HeadingFootnotes", () => {
  it("shows short footnotes in full", async () => {
    await renderSuspended(HeadingFootnotes, {
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

    await renderSuspended(HeadingFootnotes, {
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

  it.each([
    ["shows footnotes up to three lines in full", 180, false],
    ["shows a button for footnotes longer than three lines", 181, true],
  ])("%s", async (_, length, hasButton) => {
    await renderSuspended(HeadingFootnotes, {
      props: { html: "content", textLength: length },
    });

    expect(screen.queryByRole("button") !== null).toBe(hasButton);
  });

  it("shows footnotes without a text length in full", async () => {
    await renderSuspended(HeadingFootnotes, { props: { html: "content" } });

    expect(screen.queryByRole("button")).not.toBeInTheDocument();
  });
});
