import { createError, defineEventHandler } from "h3";

export default defineEventHandler(() => {
  throw createError({ message: "Test error" });
});
