import * as fs from "node:fs/promises";
import * as path from "node:path";
import * as assert from "node:assert";
import "dotenv/config";

const TOKEN = process.env.POSTHOG_API_TOKEN;
const PROJECT_ID = process.env.POSTHOG_PROJECT_ID;

assert.ok(!!TOKEN, "Failed to load PostHog token.");
assert.ok(!!PROJECT_ID, "Failed to load PostHog project.");

const file = path.join("./docs/data/feedback-surveys.json");
const forms = JSON.parse(await fs.readFile(file, "utf-8"));
const surveys = [];

let nextUrl = `https://eu.posthog.com/api/projects/${PROJECT_ID}/surveys/`;
while (nextUrl) {
  const response = await fetch(nextUrl, {
    headers: { Authorization: `Bearer ${TOKEN}` },
  });

  const data = await response.json();
  if (!data.results) {
    console.error(
      "Failed to fetch surveys. Please make sure you have a valid token."
    );
    console.error(data);
    process.exit(1);
  }

  data.results.forEach((survey) => {
    const form = forms.find((f) => f.id === survey.id) || { id: survey.id };
    if (survey.type !== "api" || survey.archived) {
      return;
    }

    form.name = survey.name;
    form.type = survey.type;
    form.createdAt = survey.created_at;
    form.questions = survey.questions.map((q, idx) => {
      const questionByIndex = form.questions ? form.questions.at(idx) : null;
      const questionByLabel = form.questions
        ? form.questions.find((i) => i.question.en === q.question)
        : null;

      if (!["open"].includes(q.type)) {
        console.warn(
          `Unsupported question type "${q.type}" found in question #${idx + 1}.`
        );
      }

      return {
        type: q.type,
        question: {
          en: q.question,
          de: questionByLabel
            ? questionByLabel.question.de
            : questionByIndex
            ? questionByIndex.question.de
            : q.question,
        },
      };
    });

    surveys.push(form);
  });

  nextUrl = data.next;
}

surveys.sort((a, b) => a.createdAt.localeCompare(b.createdAt));

console.log("Export %s feedback surveys.", surveys.length);
await fs.writeFile(file, JSON.stringify(surveys, null, 2));
