import { userEvent } from "@testing-library/user-event";
import { render, screen } from "@testing-library/vue";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { nextTick } from "vue";
import YearInput from "./YearInput.vue";

beforeEach(() => {
  vi.spyOn(HTMLElement.prototype, "offsetParent", "get").mockImplementation(
    function (this: HTMLElement) {
      return this.parentNode as Element;
    },
  );
});

afterEach(() => {
  vi.restoreAllMocks();
});

function renderComponent(options?: {
  modelValue?: string;
  isReadOnly?: boolean;
  showClear?: boolean;
}) {
  const user = userEvent.setup();
  const props = {
    id: "identifier",
    modelValue: options?.modelValue,
    isReadOnly: options?.isReadOnly,
    showClear: options?.showClear,
  };
  const utils = render(YearInput, { props });
  return { user, props, ...utils };
}

describe("YearInput", () => {
  it("shows a year input element", () => {
    renderComponent();
    const input = screen.getByRole<HTMLInputElement>("textbox");

    expect(input).toBeInTheDocument();
    expect(input?.type).toBe("text");
  });

  it("allows typing a year inside input", async () => {
    const { user } = renderComponent();
    const input = screen.getByRole("textbox");

    await user.type(input, "2024");

    expect(input).toHaveValue("2024");
  });

  it("displays modelValue correctly", async () => {
    renderComponent({ modelValue: "2022" });
    const input = screen.getByRole("textbox");

    expect(input).toHaveValue("2022");
  });

  it("emits model update event when input is complete (4 digits)", async () => {
    const { user, emitted } = renderComponent();
    const input = screen.getByRole("textbox");

    await user.type(input, "2024");
    await nextTick();

    expect(input).toHaveValue("2024");
    expect(emitted("update:modelValue")).toEqual([["2024"]]);
  });

  it("does not emit model update for incomplete input", async () => {
    const { user, emitted } = renderComponent();
    const input = screen.getByRole("textbox");

    await user.type(input, "202");
    await nextTick();

    expect(input).toHaveValue("202");
    expect(emitted("update:modelValue")).toBeFalsy();
  });

  it("emits undefined when input is cleared", async () => {
    const { user, emitted } = renderComponent({ modelValue: "2022" });
    const input = screen.getByRole("textbox");
    expect(input).toHaveValue("2022");

    await user.clear(input);
    await nextTick();

    expect(emitted("update:modelValue")).toEqual([[undefined]]);
  });

  it("updates when the model is changed to empty string", async () => {
    const { rerender } = renderComponent({ modelValue: "2024" });

    const input = screen.getByRole("textbox");
    expect(input).toHaveValue("2024");

    await rerender({ modelValue: "" });
    expect(input).toHaveValue("");
  });

  it("updates when the model is changed to undefined", async () => {
    const { rerender } = renderComponent({ modelValue: "2024" });

    const input = screen.getByRole("textbox");
    expect(input).toHaveValue("2024");

    await rerender({ modelValue: undefined });
    expect(input).toHaveValue("");
  });

  it("does not allow letters", async () => {
    const { user } = renderComponent();
    const input = screen.getByRole("textbox");

    await user.type(input, "ABCD");
    await nextTick();

    expect(input).toHaveValue("");
  });

  it("uses a numeric inputmode", () => {
    renderComponent();
    expect(screen.getByRole("textbox")).toHaveAttribute("inputmode", "numeric");
  });

  it("sets the input to readonly", () => {
    renderComponent({ isReadOnly: true });
    expect(screen.getByRole("textbox")).toHaveAttribute("readonly");
  });

  it("sets the input to editable", () => {
    renderComponent({ isReadOnly: false });
    expect(screen.getByRole("textbox")).not.toHaveAttribute("readonly");
  });

  describe("clear button", () => {
    it("is not shown when showClear is false", () => {
      renderComponent({ modelValue: "2024", showClear: false });
      expect(
        screen.queryByRole("button", { name: "Entfernen" }),
      ).not.toBeInTheDocument();
    });

    it("is not shown when the input is empty", () => {
      renderComponent();
      expect(
        screen.queryByRole("button", { name: "Entfernen" }),
      ).not.toBeInTheDocument();
    });

    it("is shown by default when the input has a value", () => {
      renderComponent({ modelValue: "2024" });
      expect(
        screen.getByRole("button", { name: "Entfernen" }),
      ).toBeInTheDocument();
    });

    it("clears the input when clicked", async () => {
      const { user, emitted } = renderComponent({ modelValue: "2024" });

      await user.click(screen.getByRole("button", { name: "Entfernen" }));
      await nextTick();

      expect(screen.getByRole("textbox")).toHaveValue("");
      expect(emitted("update:modelValue")).toContainEqual([undefined]);
    });
  });
});
