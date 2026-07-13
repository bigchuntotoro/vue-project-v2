<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import axios from "axios";

const route = useRoute();
const router = useRouter();

const boardId = route.params.id; // 수정 모드일 때만 존재
const isEditMode = ref(false);

const board = ref({
  title: "",
  content: "",
  writer: "",
});

// 수정 모드일 경우 기존 데이터 세팅
const initForm = async () => {
  if (boardId) {
    isEditMode.value = true;
    try {
      const response = await axios.get(
        `http://localhost:8080/api/board/${boardId}`,
      );
      board.value = response.data;
    } catch (error) {
      alert("데이터를 가져오지 못했습니다.");
    }
  }
};

// 저장 버튼 클릭시 (등록 혹은 수정)
const saveBoard = async () => {
  if (
    !board.value.title ||
    !board.value.content ||
    (!isEditMode.value && !board.value.writer)
  ) {
    alert("모든 필드를 입력해주세요.");
    return;
  }

  try {
    if (isEditMode.value) {
      // 1. 수정 처리 (PUT)
      await axios.put(
        `http://localhost:8080/api/board/${boardId}`,
        board.value,
      );
      alert("수정되었습니다.");
      router.push(`/board/${boardId}`);
    } else {
      // 2. 등록 처리 (POST)
      await axios.post("http://localhost:8080/api/board", board.value);
      alert("등록되었습니다.");
      router.push("/board");
    }
  } catch (error) {
    alert("저장에 실패했습니다.");
  }
};

onMounted(() => {
  initForm();
});
</script>

<template>
  <div class="container">
    <h2>{{ isEditMode ? "게시글 수정" : "게시글 작성" }}</h2>
    <div class="form-group">
      <label>제목</label>
      <input
        v-model="board.title"
        type="text"
        placeholder="제목을 입력하세요"
      />
    </div>
    <div class="form-group">
      <label>작성자</label>
      <!-- 수정 모드일 때는 작성자 수정 불가 처리 -->
      <input
        v-model="board.writer"
        type="text"
        :disabled="isEditMode"
        placeholder="작성자 이름"
      />
    </div>
    <div class="form-group">
      <label>내용</label>
      <textarea
        v-model="board.content"
        rows="10"
        placeholder="내용을 입력하세요"
      ></textarea>
    </div>

    <div class="btn-group">
      <button @click="saveBoard">{{ isEditMode ? "수정완료" : "등록" }}</button>
      <button @click="router.back()">취소</button>
    </div>
  </div>
</template>

<style scoped>
.container {
  width: 600px;
  margin: 0 auto;
}
.form-group {
  margin-bottom: 15px;
  display: flex;
  flex-direction: column;
}
.form-group label {
  margin-bottom: 5px;
  font-weight: bold;
}
input,
textarea {
  padding: 8px;
  border: 1px solid #ccc;
  font-size: 14px;
}
.btn-group {
  display: flex;
  gap: 10px;
}
</style>
