import { userEvent } from "@testing-library/user-event";
import { render, screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import { ref } from "vue";
import InputText from "./InputText.vue";

describe("InputText", () => {
  it("renders a textbox", () => {
    render(InputText);

    expect(screen.getByRole("textbox")).toBeInTheDocument();
  });

  it("reflects the model value", () => {
    render(InputText, { props: { modelValue: "Hallo" } });

    expect(screen.getByRole("textbox")).toHaveValue("Hallo");
  });

  it("updates the model when the user types", async () => {
    const user = userEvent.setup();
    const { emitted } = render(InputText, { props: { modelValue: "" } });

    await user.type(screen.getByRole("textbox"), "abc");

    const updates = emitted("update:modelValue") as unknown[][];
    expect(updates.at(-1)?.[0]).toBe("abc");
  });

  it("passes through native attributes", () => {
    render(InputText, {
      attrs: {
        placeholder: "Suche",
        type: "search",
        "aria-label": "Suchfeld",
      },
    });

    const input = screen.getByRole("searchbox", { name: "Suchfeld" });
    expect(input).toHaveAttribute("placeholder", "Suche");
  });

  it("reflects the disabled attribute", () => {
    render(InputText, { attrs: { disabled: true } });

    expect(screen.getByRole("textbox")).toBeDisabled();
  });

  it("exposes the underlying input element", () => {
    const inputTextRef = ref<{ input: HTMLInputElement } | null>(null);
    render({
      components: { InputText },
      setup: () => ({ inputTextRef }),
      template: `<InputText ref="inputTextRef" />`,
    });

    expect(inputTextRef.value?.input).toBeInstanceOf(HTMLInputElement);
  });

  describe("clear button", () => {
    it("is not rendered by default", () => {
      render(InputText, { props: { modelValue: "text" } });

      expect(screen.queryByRole("button")).not.toBeInTheDocument();
    });

    it("is not rendered when clearable but empty", () => {
      render(InputText, { props: { clearable: true, modelValue: "" } });

      expect(
        screen.queryByRole("button", { name: "Entfernen" }),
      ).not.toBeInTheDocument();
    });

    it("is rendered when clearable and holding a value", () => {
      render(InputText, { props: { clearable: true, modelValue: "text" } });

      expect(
        screen.getByRole("button", { name: "Entfernen" }),
      ).toBeInTheDocument();
    });

    it("clears the value and emits clear when clicked", async () => {
      const user = userEvent.setup();
      const { emitted } = render(InputText, {
        props: { clearable: true, modelValue: "text" },
      });

      await user.click(screen.getByRole("button", { name: "Entfernen" }));

      expect(emitted("clear")).toHaveLength(1);
      const updates = emitted("update:modelValue") as unknown[][];
      expect(updates.at(-1)?.[0]).toBe("");
    });

    it("refocuses the input after clearing", async () => {
      const user = userEvent.setup();
      render(InputText, {
        props: { clearable: true, modelValue: "text" },
      });

      await user.click(screen.getByRole("button", { name: "Entfernen" }));

      expect(screen.getByRole("textbox")).toHaveFocus();
    });
  });
});
