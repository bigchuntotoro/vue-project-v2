<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import axios from "axios";

const router = useRouter();
const boardList = ref([]); // 게시글 데이터를 담을 반응형 배열

// 백엔드로부터 목록을 가져오는 함수
const getList = async () => {
  try {
    const response = await axios.get("http://localhost:8080/api/board/list");
    boardList.value = response.data;
  } catch (error) {
    alert("목록을 불러오는데 실패했습니다.");
    console.error(error);
  }
};

// 컴포넌트가 화면에 마운트(장착)되면 실행
onMounted(() => {
  getList();
});
</script>

<template>
  <div class="container">
    <h2>게시판 목록</h2>
    <button @click="router.push('/board/write')">글쓰기</button>

    <table>
      <thead>
        <tr>
          <th>번호</th>
          <th>제목</th>
          <th>작성자</th>
          <th>조회수</th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="board in boardList"
          :key="board.boardId"
          @click="router.push(`/board/${board.boardId}`)"
        >
          <td>{{ board.boardId }}</td>
          <td class="title-link">{{ board.title }}</td>
          <td>{{ board.writer }}</td>
          <td>{{ board.viewCnt }}</td>
        </tr>
        <tr v-if="boardList.length === 0">
          <td colspan="4">등록된 게시글이 없습니다.</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
/* 간단한 스타일링 */
.container {
  width: 800px;
  margin: 0 auto;
}
table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}
th,
td {
  border: 1px solid #ddd;
  padding: 10px;
  text-align: center;
}
.title-link {
  cursor: pointer;
  text-align: left;
  color: blue;
}
button {
  float: right;
  margin-bottom: 10px;
}
</style>
