<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import axios from "axios";

const route = useRoute();
const router = useRouter();
const board = ref({});
const boardId = route.params.id; // URL에 넘어온 글 번호

// 상세 데이터 요청
const getDetail = async () => {
  try {
    const response = await axios.get(
      `http://localhost:8080/api/board/${boardId}`,
    );
    board.value = response.data;
  } catch (error) {
    alert("글을 불러올 수 없습니다.");
    router.push("/board");
  }
};

// 게시글 삭제
const deleteBoard = async () => {
  if (confirm("정말 삭제하시겠습니까?")) {
    try {
      await axios.delete(`http://localhost:8080/api/board/${boardId}`);
      alert("삭제되었습니다.");
      router.push("/board");
    } catch (error) {
      alert("삭제에 실패했습니다.");
    }
  }
};

onMounted(() => {
  getDetail();
});
</script>

<template>
  <div class="container" v-if="board.boardId">
    <h2>게시글 상세 보기</h2>
    <div class="detail-box">
      <p><strong>제목:</strong> {{ board.title }}</p>
      <p>
        <strong>작성자:</strong> {{ board.writer }} | <strong>조회수:</strong>
        {{ board.viewCnt }}
      </p>
      <hr />
      <div class="content">{{ board.content }}</div>
    </div>
    <div class="btn-group">
      <button @click="router.push('/board')">목록으로</button>
      <button @click="router.push(`/board/edit/${boardId}`)">수정</button>
      <button @click="deleteBoard" class="btn-danger">삭제</button>
    </div>
  </div>
</template>

<style scoped>
.container {
  width: 600px;
  margin: 0 auto;
}
.detail-box {
  border: 1px solid #ccc;
  padding: 20px;
  margin-top: 20px;
}
.content {
  min-height: 200px;
  white-space: pre-wrap;
} /* 줄바꿈 유지 */
.btn-group {
  margin-top: 20px;
  display: flex;
  gap: 10px;
}
.btn-danger {
  background-color: red;
  color: white;
}
</style>
