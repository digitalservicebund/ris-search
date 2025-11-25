import { userEvent } from "@testing-library/user-event";
import { render, screen } from "@testing-library/vue";
import InputText from "primevue/inputtext";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { nextTick } from "vue";
import RisYearInput from "./RisYearInput.vue";

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
  stubs?: Record<string, object>;
}) {
  const user = userEvent.setup();
  const props = {
    id: "identifier",
    modelValue: options?.modelValue,
    isReadOnly: options?.isReadOnly,
  };
  const utils = render(RisYearInput, {
    props,
    global: {
      stubs: options?.stubs,
    },
  });
  return { user, props, ...utils };
}

describe("RisYearInput", () => {
  it("shows a year input element", () => {
    renderComponent();
    const input = screen.getByRole<HTMLInputElement>("textbox");

    expect(input).toBeInTheDocument();
    expect(input?.type).toBe("text");
  });

  it("allows typing a year inside input (stubbed inputMask)", async () => {
    renderComponent({
      stubs: {
        InputMask: InputText,
      },
    });
    const input = screen.getByRole("textbox");

    await userEvent.type(input, "2024");

    expect(input).toHaveValue("2024");
  });

  it("displays modelValue correctly", async () => {
    renderComponent({ modelValue: "2022" });
    const input = screen.getByRole("textbox");

    expect(input).toHaveValue("2022");
  });

  it("emits model update event when input is complete (4 digits)", async () => {
    const { emitted } = renderComponent({
      stubs: {
        InputMask: InputText,
      },
    });
    const input = screen.getByRole("textbox");

    await userEvent.type(input, "2024");
    await nextTick();

    expect(input).toHaveValue("2024");
    expect(emitted()["update:modelValue"]).toEqual([["2024"]]);
  });

  it("does not emit model update for incomplete input", async () => {
    const { emitted } = renderComponent({
      stubs: {
        InputMask: InputText,
      },
    });
    const input = screen.getByRole("textbox");

    await userEvent.type(input, "202");
    await nextTick();

    expect(input).toHaveValue("202");
    expect(emitted()["update:modelValue"]).toBeFalsy();
  });

  it("emits undefined when input is cleared", async () => {
    const { emitted } = renderComponent({
      modelValue: "2022",
      stubs: {
        InputMask: InputText,
      },
    });
    const input = screen.getByRole("textbox");
    expect(input).toHaveValue("2022");

    await userEvent.clear(input);
    await nextTick();

    expect(emitted()["update:modelValue"]).toEqual([[undefined]]);
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
    renderComponent();
    const input = screen.getByRole("textbox");

    await userEvent.type(input, "ABCD");
    await nextTick();

    expect(input).toHaveTextContent("");
  });

  it("sets the input to readonly", () => {
    renderComponent({ isReadOnly: true });
    expect(screen.getByRole("textbox")).toHaveAttribute("readonly");
  });

  it("sets the input to editable", () => {
    renderComponent({ isReadOnly: false });
    expect(screen.getByRole("textbox")).not.toHaveAttribute("readonly");
  });
});
