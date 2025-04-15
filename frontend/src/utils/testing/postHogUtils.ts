export function getPostHogConfig(
  key: string | undefined,
  host: string | undefined,
  surveyId: string | undefined,
) {
  return {
    public: {
      analytics: {
        posthogKey: key,
        posthogHost: host,
        feedbackSurveyId: surveyId,
      },
    },
  };
}
