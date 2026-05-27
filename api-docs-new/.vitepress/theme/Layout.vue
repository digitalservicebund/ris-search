<script setup lang="ts">
import DefaultTheme from "vitepress/theme";
import FeedbackForm from "./components/FeedbackForm.vue";
import Footer from "./components/Footer.vue";

const { Layout } = DefaultTheme;
</script>

<template>
  <Layout>
    <template #doc-before>
      <div class="vp-doc">
        <div class="warning custom-block">
          <p class="custom-block-title">This is a trial service.</p>
          <p>
            This API may be subject to changes and improvements as we learn from
            usage. This means that you may use this software and build
            applications that utilise it. However as we learn from feedback we
            may make changes to the software.
          </p>
        </div>
      </div>
    </template>
    <template #home-hero-before>
      <div class="vp-doc">
        <div class="warning custom-block">
          <p class="custom-block-title">This is a trial service.</p>
          <p>
            This API may be subject to changes and improvements as we learn from
            usage. This means that you may use this software and build
            applications that utilise it. However as we learn from feedback we
            may make changes to the software.
          </p>
        </div>
      </div>
    </template>
    <template #doc-footer-before>
      <div class="vp-doc feedback">
        <hr />
        <FeedbackForm />
      </div>
    </template>
    <template #doc-after>
      <div class="vp-doc footer">
        <Footer />
      </div>
    </template>
  </Layout>
</template>

<style scoped>
.feedback {
  margin-top: 2rem;
  margin-bottom: 2rem;
}

.footer {
  margin-top: 4rem;
  margin-bottom: 2rem;
}
</style>
