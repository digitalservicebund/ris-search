import * as fs from "node:fs/promises";
import { defineLoader } from "vitepress";
import type { LocalizedText } from "./utils/classes";

export interface FeedbackSurvey {
  id: string;
  name: string;
  type: "api";
  questions: FeedbackSurveyQuestion[];
}

export interface FeedbackSurveyQuestion {
  type: "open";
  question: LocalizedText;
}

declare const data: FeedbackSurvey[];
export { data };

export default defineLoader({
  watch: ["../../docs/data/feedback-surveys.json"],
  async load(files) {
    let surveys: FeedbackSurvey[] = [];
    for (const file of files) {
      const buffer = await fs.readFile(file, "utf-8");
      const json = JSON.parse(buffer) as FeedbackSurvey[];
      surveys = surveys.concat(json);
    }

    return surveys;
  },
});
