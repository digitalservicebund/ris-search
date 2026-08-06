import { userEvent } from "@testing-library/user-event";
import { render, screen } from "@testing-library/vue";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { nextTick } from "vue";
import DateInput from "./DateInput.vue";

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
  const utils = render(DateInput, { props });
  return { user, props, ...utils };
}

describe("DateInput", () => {
  it("shows an date input element", () => {
    renderComponent();
    const input = screen.getByRole<HTMLInputElement>("textbox");

    expect(input).toBeInTheDocument();
    expect(input?.type).toBe("text");
  });

  it("allows typing a date inside input", async () => {
    const { user } = renderComponent();
    const input = screen.getByRole("textbox");

    await user.type(input, "12.05.2020");

    expect(input).toHaveValue("12.05.2020");
  });

  it("displays modelValue in correct format", async () => {
    renderComponent({ modelValue: "2022-05-13" });
    const input = screen.getByRole("textbox");

    expect(input).toHaveValue("13.05.2022");
  });

  it("emits model update event when input completed and valid", async () => {
    const { user, emitted } = renderComponent({
      modelValue: "2022-05-13T18:08:14.036Z",
    });
    const input = screen.getByRole("textbox");
    expect(input).toHaveValue("13.05.2022");
    await user.clear(input);
    await user.type(input, "14.05.2022");
    await nextTick();

    expect(input).toHaveValue("14.05.2022");

    expect(emitted("update:modelValue")).toEqual([[undefined], ["2022-05-14"]]);
  });

  it("updates when the model is changed to empty string", async () => {
    const { rerender } = renderComponent({ modelValue: "2024-04-22" });

    const input = screen.getByRole("textbox");
    expect(input).toHaveValue("22.04.2024");

    await rerender({ modelValue: "" });
    expect(input).toHaveValue("");
  });

  it("updates when the model is changed to undefined", async () => {
    const { rerender } = renderComponent({ modelValue: "2024-04-22" });

    const input = screen.getByRole("textbox");
    expect(input).toHaveValue("22.04.2024");

    await rerender({ modelValue: undefined });
    expect(input).toHaveValue("");
  });

  it("removes validation errors on backspace delete", async () => {
    const { user } = renderComponent({ modelValue: "2022-05-13" });
    const input = screen.getByRole("textbox");
    expect(input).toHaveValue("13.05.2022");
    await user.clear(input);
    await user.type(input, "40.05.2022");
    expect(input).toHaveValue("40.05.2022");
    const errorLabel = screen.getByText("Kein valides Datum");
    expect(errorLabel).toBeVisible();
    await user.type(input, "{backspace}");

    expect(errorLabel).not.toBeInTheDocument();
  });

  it("does not allow invalid dates", async () => {
    const { user, emitted } = renderComponent();
    const input = screen.getByRole("textbox");
    await user.type(input, "29.02.2001");
    await nextTick();

    expect(input).toHaveValue("29.02.2001");
    expect(emitted("update:modelValue")).not.toBeTruthy();
    expect(screen.getByText("Kein valides Datum")).toBeVisible();
  });

  it("does not allow letters", async () => {
    const { user } = renderComponent();
    const input = screen.getByRole("textbox");

    await user.type(input, "AB.CD.EFGH");
    await nextTick();

    expect(input).toHaveValue("");
  });

  it("does not allow incomplete dates", async () => {
    const { user, emitted } = renderComponent();
    const input = screen.getByRole("textbox");

    await user.type(input, "03");
    await user.type(input, "{tab}");
    await nextTick();

    expect(emitted("update:modelValue")).not.toBeTruthy();
    expect(screen.getByText("Unvollständiges Datum")).toBeVisible();
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
      renderComponent({ modelValue: "2024-04-22", showClear: false });
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
      renderComponent({ modelValue: "2024-04-22" });
      expect(
        screen.getByRole("button", { name: "Entfernen" }),
      ).toBeInTheDocument();
    });

    it("clears the input when clicked", async () => {
      const { user, emitted } = renderComponent({
        modelValue: "2024-04-22",
        showClear: true,
      });

      await user.click(screen.getByRole("button", { name: "Entfernen" }));
      await nextTick();

      expect(screen.getByRole("textbox")).toHaveValue("");
      expect(emitted("update:modelValue")).toContainEqual([undefined]);
    });

    it("resets the error message when clicked", async () => {
      const { user } = renderComponent({ showClear: true });

      const input = screen.getByRole("textbox");
      await user.type(input, "29.02.2025");

      const errorLabel = screen.getByText("Kein valides Datum");
      expect(errorLabel).toBeVisible();

      await user.click(screen.getByRole("button", { name: "Entfernen" }));
      await nextTick();

      expect(errorLabel).not.toBeVisible();
    });
  });
});
