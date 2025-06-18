export enum InputType {
  TEXT = "text",
  FILE = "file",
  DROPDOWN = "dropdown",
  DATE = "date",
  CHECKBOX = "checkbox",
  RADIO = "radio",
  CHIPS = "chips",
  DATECHIPS = "datechips",
  NESTED = "nested",
  COMBOBOX = "combobox",
  TEXTAREA = "textarea",
  DATE_TIME = "date_time",
  YEAR = "year",
  TIME = "time",
  UNDEFINED_DATE = "undefined_date",
}

export type ValidationError = {
  code?: string;
  message: string;
  instance: string;
};

export enum LabelPosition {
  TOP = "top",
  RIGHT = "right",
}

export enum StatusCardType {
  IMPLEMENTED,
  IN_PROGRESS,
  PLANNED,
}

export enum BadgeColor {
  BLUE,
  GREEN,
  YELLOW,
}

//BASE
export interface BaseInputAttributes {
  ariaLabel: string;
  validationError?: ValidationError;
  labelPosition?: LabelPosition;
}

export interface BaseInputField {
  name: string;
  type: InputType;
  label: string;
  required?: boolean;
  inputAttributes: BaseInputAttributes;
}

//DROPDOWN
export type DropdownInputModelType = string;

export type DropdownItem = {
  label: string;
  value: DropdownInputModelType;
};

export interface DropdownAttributes extends BaseInputAttributes {
  placeholder?: string;
  items: DropdownItem[];
}

export interface DropdownInputField extends BaseInputField {
  type: InputType.DROPDOWN;
  inputAttributes: DropdownAttributes;
}

export const sortMode = {
  default: "default",
  date: "date",
  courtName: "courtName",
};
