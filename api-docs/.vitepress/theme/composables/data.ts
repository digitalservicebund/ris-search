import { useData as useData$ } from "vitepress";
import type { ThemeConfig } from "../types";

export const useData: typeof useData$<ThemeConfig> = useData$;
