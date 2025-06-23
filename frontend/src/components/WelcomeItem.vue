<script setup>
defineProps({
  heading: {
    type: String,
    required: true,
  },
})
</script>

<template>
  <div class="item">
    <div class="icon">
      <slot name="icon" />
    </div>
    <div class="details">
      <h3>
        <a href="#" @click.prevent><slot name="heading" /></a>
      </h3>
      <div class="description">
        <slot />
      </div>
    </div>
  </div>
</template>

<style scoped>
.item {
  margin-top: 2rem;
  display: flex;
  position: relative;
}

.item:first-child {
  margin-top: 0;
}

.icon {
  text-align: center;
  width: 140px;
  height: 37px;
  margin-right: 0.5rem;
  color: var(--color-text);
}

.details {
  flex: 1;
  min-width: 0;
}

.details h3 {
  font-weight: 500;
  font-size: 1.2rem;
  line-height: 1.4;
  margin: 0;
  color: var(--color-heading);
}

.details h3 a {
  color: var(--color-heading);
  text-decoration: none;
}

.details h3 a:hover {
  text-decoration: underline;
}

.description {
  margin-top: 0.25rem;
  font-size: 0.875rem;
  color: var(--color-text);
}

.description :deep(a) {
  color: var(--color-text);
  text-decoration: none;
}

.description :deep(a:hover) {
  text-decoration: underline;
}

@media (min-width: 1024px) {
  .item {
    margin-top: 0;
    padding: 0.75rem 0 0.75rem;
  }

  .icon {
    position: absolute;
    border: 1px solid var(--color-border);
    background: var(--color-background);
    border-radius: 8px;
    width: 1.25rem;
    height: 1.25rem;
    top: 0.75rem;
    left: 0;
    margin: 0;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .details {
    padding-left: 1.5rem;
  }
}
</style> 