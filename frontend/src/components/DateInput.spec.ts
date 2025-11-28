import { userEvent } from "@testing-library/user-event";
import { render, screen } from "@testing-library/vue";
import InputText from "primevue/inputtext";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { nextTick } from "vue";
import type { ValidationError } from "./DateInput.vue";
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
  validationError?: ValidationError;
  isReadOnly?: boolean;
  stubs?: Record<string, object>;
}) {
  const user = userEvent.setup();
  const props = {
    id: "identifier",
    modelValue: options?.modelValue,
    validationError: options?.validationError,
    isReadOnly: options?.isReadOnly,
  };
  const utils = render(DateInput, {
    props,
    global: {
      stubs: options?.stubs,
    },
  });
  return { user, props, ...utils };
}

describe("DateInput", () => {
  it("shows an date input element", () => {
    renderComponent();
    const input = screen.getByRole<HTMLInputElement>("textbox");

    expect(input).toBeInTheDocument();
    expect(input?.type).toBe("text");
  });

  it("allows typing a date inside input (stubbed inputMask)", async () => {
    renderComponent({
      stubs: {
        InputMask: InputText,
      },
    });
    const input = screen.getByRole("textbox");

    await userEvent.type(input, "12.05.2020");

    expect(input).toHaveValue("12.05.2020");
  });

  it("displays modelValue in correct format", async () => {
    renderComponent({ modelValue: "2022-05-13" });
    const input = screen.getByRole("textbox");

    expect(input).toHaveValue("13.05.2022");
  });

  it("emits model update event when input completed and valid", async () => {
    const { emitted } = renderComponent({
      modelValue: "2022-05-13T18:08:14.036Z",
      stubs: {
        InputMask: InputText,
      },
    });
    const input = screen.getByRole("textbox");
    expect(input).toHaveValue("13.05.2022");
    await userEvent.clear(input);
    await userEvent.type(input, "14.05.2022");
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
    const { emitted } = renderComponent({
      modelValue: "2022-05-13",
      stubs: {
        InputMask: InputText,
      },
    });
    const input = screen.getByRole("textbox");
    expect(input).toHaveValue("13.05.2022");
    await userEvent.clear(input);
    await userEvent.type(input, "40.05.2022");
    expect(input).toHaveValue("40.05.2022");
    await userEvent.type(input, "{backspace}");

    expect(emitted("update:validationError")).toBeTruthy();
  });

  it("does not allow invalid dates", async () => {
    const { emitted } = renderComponent({
      stubs: {
        InputMask: InputText,
      },
    });
    const input = screen.getByRole("textbox");
    await userEvent.type(input, "29.02.2001");
    await nextTick();

    expect(input).toHaveValue("29.02.2001");

    expect(emitted("update:modelValue")).not.toBeTruthy();

    expect(emitted("update:validationError")).toBeTruthy();

    const array: ValidationError[][] = emitted("update:validationError");

    expect(
      array.find((element) => element[0] !== undefined)?.[0]?.message,
    ).toBe("Kein valides Datum");
  });

  it("does not allow letters", async () => {
    renderComponent();
    const input = screen.getByRole("textbox");

    await userEvent.type(input, "AB.CD.EFGH");
    await nextTick();

    expect(input).toHaveTextContent("");
  });

  it("does not allow incomplete dates", async () => {
    const { emitted } = renderComponent();
    const input = screen.getByRole("textbox");

    await userEvent.type(input, "03");
    await userEvent.type(input, "{tab}");
    await nextTick();

    expect(emitted("update:modelValue")).not.toBeTruthy();
    const validationErrors = emitted()[
      "update:validationError"
    ] as ValidationError[][];

    expect(
      validationErrors
        .filter((element) => element[0] !== undefined)
        .some((element) => element[0]?.message === "Unvollständiges Datum"),
    ).toBe(true);
  });

  it("sets the input to readonly", () => {
    renderComponent({ isReadOnly: true });
    expect(screen.getByRole("textbox")).toHaveAttribute("readonly");
  });

  it("sets the input to editable", () => {
    renderComponent({ isReadOnly: false });
    expect(screen.getByRole("textbox")).not.toHaveAttribute("readonly");
  });

  it("shows error message block for external validation errors", async () => {
    const validationError = {
      message: "Externer Fehler",
      instance: "identifier",
    };
    renderComponent({ validationError });

    const errorBlock = screen.getByText("Externer Fehler");
    expect(errorBlock).toBeInTheDocument();
  });

  it("shows error message block for internal validation errors", async () => {
    renderComponent({
      stubs: {
        InputMask: InputText,
      },
    });

    const input = screen.getByRole("textbox");
    await userEvent.type(input, "29.02.2001");
    await nextTick();

    expect(input).toHaveValue("29.02.2001");

    expect(screen.getByText("Ungültige Eingabe")).toBeInTheDocument();
  });
});
