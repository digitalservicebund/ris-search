import type { Meta, StoryObj } from "@storybook/vue3-vite";
import { html } from "../utils/tags";

const meta: Meta<never> = {
  title: "tailwind/colors",
  tags: ["autodocs"],
};

export default meta;

const colors = [
  // Base colors
  "black",
  "white",

  // Grays
  "gray-100",
  "gray-200",
  "gray-300",
  "gray-400",
  "gray-500",
  "gray-600",
  "gray-700",
  "gray-800",
  "gray-900",
  "gray-1000",

  // Blues
  "blue-100",
  "blue-200",
  "blue-300",
  "blue-400",
  "blue-500",
  "blue-600",
  "blue-700",
  "blue-800",
  "blue-900",

  // Greens
  "green-100",
  "green-200",
  "green-300",
  "green-400",
  "green-500",
  "green-600",
  "green-700",
  "green-800",
  "green-900",

  // Yellows
  "yellow-100",
  "yellow-200",
  "yellow-300",
  "yellow-400",
  "yellow-500",
  "yellow-600",
  "yellow-700",
  "yellow-800",
  "yellow-900",

  // Oranges
  "orange-100",
  "orange-200",
  "orange-300",
  "orange-400",
  "orange-500",
  "orange-600",
  "orange-700",
  "orange-800",
  "orange-900",

  // Reds
  "red-100",
  "red-200",
  "red-300",
  "red-400",
  "red-500",
  "red-600",
  "red-700",
  "red-800",
  "red-900",
];

const tableRows = colors
  .map(
    (color) => html`
      <tr>
        <td class="border border-gray-300 px-16 py-8">${color}</td>
        <td class="border border-gray-300 px-16 py-8">
          <div
            style="background-color: var(--color-${color}); width: 2rem; height: 2rem; border-radius: 50%;"
          ></div>
        </td>
      </tr>
    `,
  )
  .join("");

export const Colors: StoryObj<typeof meta> = {
  render: () => ({
    template: html`
      <table>
        <thead>
          <tr>
            <th class="border border-gray-300 px-16 py-8 text-left">
              Color Name
            </th>
            <th class="border border-gray-300 px-16 py-8 text-left">Color</th>
          </tr>
        </thead>
        <tbody>
          ${tableRows}
        </tbody>
      </table>
    `,
  }),
};
