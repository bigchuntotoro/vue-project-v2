<template>
  <div id="app">
    <header>
      <h1>Vue & Spring Boot 간단 게시판</h1>
      <hr />
    </header>

    <!-- 라우터에 설정된 컴포넌트(List, Detail 등)들이 경로에 맞춰 여기에 렌더링됩니다. -->
    <RouterView />
  </div>
</template>

<style>
#app {
  font-family: sans-serif;
  padding: 20px;
}
header {
  text-align: center;
  margin-bottom: 30px;
}
</style>
