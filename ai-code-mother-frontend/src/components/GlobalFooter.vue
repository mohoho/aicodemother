<template>
  <a-layout-footer class="footer">
    <div class="footer-content">
      <p class="copyright">
        <a
          href="https://github.com/mohoho"
          target="_blank"
          rel="noopener noreferrer"
          class="author-link"
        >
          AI代码生成平台
        </a>
      </p>
    </div>
  </a-layout-footer>
</template>

<script setup lang="ts">
// 无需额外的响应式数据
</script>

<style scoped>
.footer {
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  text-align: center;
  padding: 20px;
  box-shadow: 0 -2px 8px rgba(24, 144, 255, 0.1);
}

.copyright {
  margin: 0;
  color:rgb(96, 93, 93);
  font-size: 14px;
}

.author-link {
  color:rgb(96, 96, 96);
  text-decoration: none;
  transition: all 0.3s ease;
  font-weight: 500;
}

.author-link:hover {
  color: #40a9ff;
  text-decoration: underline;
  transform: translateY(-1px);
}
</style>
