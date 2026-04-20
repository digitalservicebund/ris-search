---
aside: false
outline: false
title: Endpoints
prose: false
---

<script setup lang="ts">
import { useData } from 'vitepress'
const { isDark } = useData()
</script>

<p class="mb-12 text-right">
<a href="/data/openapi.json">View OpenAPI JSON</a>
</p>
<div class="not-prose openapi">
<OASpec :isDark="isDark" />
</div>
