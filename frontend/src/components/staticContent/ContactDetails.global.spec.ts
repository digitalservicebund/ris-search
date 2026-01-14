import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import ContactDetails from "./ContactDetails.global.vue";

describe("ContactDetails", () => {
  it("shows contact information", async () => {
    await renderSuspended(ContactDetails);
    const email = screen.getByRole("link", { name: /rechtsinformationen@/i });
    expect(email).toHaveAttribute("href", expect.stringContaining("mailto:"));
    expect(screen.getByText(/Berlin/)).toBeInTheDocument();
  });
});
