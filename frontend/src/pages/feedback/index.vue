<script setup lang="ts">
import FeedbackForm from "~/components/Analytics/FeedbackForm.vue";
import StaticPageWrapper from "~/components/CustomLayouts/StaticPageWrapper.vue";

useHead({ title: "Feedback geben" });
</script>

<template>
  <StaticPageWrapper>
    <RisBreadcrumb title="Feedback" base-path="/" />
    <PageHeader title="Geben Sie uns Feedback" />
    <main class="max-w-prose">
      <p>
        Das Rechtsinformationsportal befindet sich in der Testphase und wird auf
        der Grundlage der Rückmeldungen der Nutzerinnen und Nutzer
        kontinuierlich angepasst. Ihr Feedback hilft uns, den Service zu
        verbessern.
      </p>
      <h2 class="ris-heading2-regular mt-64">So geben Sie uns Feedback</h2>
      <ul class="list-bullet mt-16">
        <li>Beschreiben Sie Ihr Anliegen so genau wie möglich.</li>
        <li>
          Beziehen Sie sich auf die Darstellung eines bestimmten Gesetzes oder
          einer bestimmten Entscheidung? Teilen Sie uns den Titel oder Link mit
          oder benutzen Sie das Feedback-Feld am Ende der betreffenden Seite.
        </li>
        <li>Bitte geben Sie keine persönlichen Daten an.</li>
      </ul>
      <FeedbackForm hide-intro class="mt-24" /></main
  ></StaticPageWrapper>
</template>
