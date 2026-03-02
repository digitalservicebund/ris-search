export type StaticPage =
  | "startseite"
  | "suche"
  | "ueber"
  | "feedback"
  | "kontakt"
  | "impressum"
  | "datenschutz"
  | "barrierefreiheit"
  | "cookies"
  | "open-source"
  | "nutzungstests"
  | "nutzungstests-datenschutz"
  | "translations-list";

export type StaticPageSeo = {
  title: string;
  description: string;
};

export const staticPageSeo: Record<StaticPage, StaticPageSeo> = {
  startseite: {
    title: "Rechtsinformationen des Bundes",
    description:
      "Nutzen Sie das neue Rechtsinformationsportal des Bundes – Gesetze, Verordnungen und Urteile auf einen Blick.",
  },
  suche: {
    title: "Suche im Rechtsinformationsportal des Bundes",
    description:
      "Finden Sie gezielt Gesetze, Verordnungen und Entscheidungen – schnell, präzise und übersichtlich.",
  },
  ueber: {
    title: "Über das neue Rechtsinformationsportal des Bundes",
    description:
      "Erfahren Sie, wie das Portal Gesetze und Urteile für alle frei zugänglich macht.",
  },
  feedback: {
    title: "Feedback zum Rechtsinformationsportal des Bundes",
    description:
      "Teilen Sie Ihre Rückmeldungen und Anregungen zur Testphase – Ihr Input hilft bei der Weiterentwicklung.",
  },
  kontakt: {
    title: "Kontakt zum Rechtsinformationsportal des Bundes",
    description:
      "Hier erreichen Sie uns bei Fragen, Hinweisen oder technischen Problemen rund um das Portal.",
  },
  impressum: {
    title: "Impressum des Rechtsinformationsportals des Bundes",
    description:
      "Angaben gemäß § 5 TMG – Herausgeber, Verantwortliche und rechtliche Hinweise zum Portal.",
  },
  datenschutz: {
    title: "Datenschutzrichtlinie des Rechtsinformationsportals des Bundes",
    description:
      "Wie wir Ihre Daten schützen, welche Rechte Sie haben und welche Verfahren wir anwenden.",
  },
  barrierefreiheit: {
    title: "Barrierefreiheit im Rechtsinformationsportal des Bundes",
    description:
      "Informationen zur digitalen Zugänglichkeit, zum technischen Standard und zur Feedback-Möglichkeit.",
  },
  cookies: {
    title: "Cookie-Einstellungen für das Rechtsinformationsportal des Bundes",
    description:
      "Wählen Sie, welche Cookies Sie zulassen – für eine bessere, datenschutzgerechte Nutzung.",
  },
  "open-source": {
    title: "Open Source im Rechtsinformationsportal des Bundes",
    description:
      "Informationen zur verwendeten Open-Source-Software, zu Lizenzen und Beteiligungsmöglichkeiten.",
  },
  nutzungstests: {
    title: "Nutzungstests zum Rechtsinformationsportal des Bundes",
    description:
      "Erfahren Sie, wie das Portal getestet wird, welche Ergebnisse vorliegen und wie Sie teilnehmen können.",
  },
  "nutzungstests-datenschutz": {
    title:
      "Datenschutzerklärung zu den Nutzungstests des Rechtsinformationsportals des Bundes",
    description:
      "Erfahren Sie, wie wir Ihre Daten bei Teilnahme an unseren Nutzungstests erfassen, verwenden und schützen",
  },
  "translations-list": {
    title: "English Translations of German Federal Laws and Regulations",
    description:
      "Access official English translations of selected German laws and regulations. These translations are for informational purposes only and are not legally binding.",
  },
};
