import { createApp } from "vue";
import App from "./App.vue";
import router from "./router"; // router/index.js 로드

const app = createApp(App);

app.use(router); // 라우터 장착
app.mount("#app"); // HTML index.html의 <div id="app">에 매핑
