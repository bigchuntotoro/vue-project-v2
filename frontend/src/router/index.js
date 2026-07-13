import { createRouter, createWebHistory } from "vue-router";
import BoardList from "../views/BoardList.vue";
import BoardDetail from "../views/BoardDetail.vue";
import BoardForm from "../views/BoardForm.vue";

const routes = [
  { path: "/", redirect: "/board" },
  { path: "/board", component: BoardList },
  { path: "/board/:id", component: BoardDetail }, // :id는 동적 파라미터 (글 번호)
  { path: "/board/write", component: BoardForm }, // 글 작성
  { path: "/board/edit/:id", component: BoardForm }, // 글 수정 (작성 폼 재사용)
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
