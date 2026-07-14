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
    <!-- 상단 타이틀 -->
    <div class="form-header">
      <h2>{{ isEditMode ? "게시글 수정" : "새 글 쓰기" }}</h2>
      <p class="subtitle">
        {{
          isEditMode
            ? "기존 내용을 다듬고 수정할 수 있습니다."
            : "새로운 이야기와 소식을 등록해 보세요."
        }}
      </p>
    </div>

    <!-- 입력 카드 영역 -->
    <div class="form-card">
      <div class="form-group">
        <label>제목</label>
        <input
          v-model="board.title"
          type="text"
          placeholder="제목을 입력하세요"
          autofocus
        />
      </div>

      <div class="form-group">
        <label>작성자</label>
        <!-- 수정 모드일 때는 작성자 수정 불가 처리 -->
        <input
          v-model="board.writer"
          type="text"
          :disabled="isEditMode"
          placeholder="작성자 이름을 입력하세요"
          :class="{ 'disabled-input': isEditMode }"
        />
      </div>

      <div class="form-group">
        <label>내용</label>
        <textarea
          v-model="board.content"
          rows="12"
          placeholder="나누고 싶은 이야기를 자유롭게 적어보세요."
        ></textarea>
      </div>
    </div>

    <!-- 하단 버튼 영역 -->
    <div class="btn-group">
      <button class="cancel-btn" @click="router.back()">취소</button>
      <button class="submit-btn" @click="saveBoard">
        {{ isEditMode ? "수정 완료" : "등록하기" }}
      </button>
    </div>
  </div>
</template>

<style scoped>
/* 시스템 폰트 통합 및 간격 조정 */
.container {
  width: 700px;
  margin: 50px auto;
  font-family:
    -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue",
    Arial, sans-serif;
  color: #333d4b;
}

/* 상단 헤더 */
.form-header {
  margin-bottom: 28px;
}
.form-header h2 {
  font-size: 24px;
  font-weight: 700;
  margin: 0 0 6px 0;
  letter-spacing: -0.5px;
}
.form-header .subtitle {
  font-size: 14px;
  color: #8b95a1;
  margin: 0;
}

/* 입력창을 감싸는 카드 레이아웃 */
.form-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
  padding: 32px;
  border: 1px solid #f2f4f6;
  margin-bottom: 24px;
}

/* 폼 그룹 레이아웃 정렬 */
.form-group {
  margin-bottom: 22px;
  display: flex;
  flex-direction: column;
}
.form-group:last-child {
  margin-bottom: 0;
}

.form-group label {
  font-size: 14px;
  font-weight: 600;
  color: #4e5968;
  margin-bottom: 8px;
}

/* 인풋 및 텍스트에어리어 통합 모던 스타일 */
input,
textarea {
  padding: 12px 16px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 15px;
  color: #191f28;
  background-color: #fff;
  transition: all 0.2s ease;
  outline: none;
}

/* 플레이스홀더 색상 은은하게 */
input::placeholder,
textarea::placeholder {
  color: #b0b8c1;
}

/* 포커스 되었을 때 트렌디한 효과 */
input:focus,
textarea:focus {
  border-color: #4f46e5;
  box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.12);
}

/* 수정 모드일 때 작성자 인풋 스타일 */
.disabled-input {
  background-color: #f9fafb;
  color: #8b95a1;
  border-color: #e5e7eb;
  cursor: not-allowed;
}
.disabled-input:focus {
  border-color: #e5e7eb;
  box-shadow: none;
}

/* 텍스트 상자 리사이즈 조절 및 글꼴 일치 */
textarea {
  resize: vertical;
  font-family: inherit;
  line-height: 1.5;
}

/* 버튼 그룹 및 개별 버튼 스타일 */
.btn-group {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

button {
  padding: 12px 24px;
  font-size: 15px;
  font-weight: 600;
  border-radius: 8px;
  cursor: pointer;
  border: none;
  transition: all 0.2s ease;
}

/* 취소 버튼 (회색 톤 무채색) */
.cancel-btn {
  background-color: #f2f4f6;
  color: #4e5968;
}
.cancel-btn:hover {
  background-color: #e5e8eb;
}

/* 등록/수정완료 버튼 (인디고 테마 반영) */
.submit-btn {
  background-color: #4f46e5;
  color: white;
  box-shadow: 0 4px 6px -1px rgba(79, 70, 229, 0.15);
}
.submit-btn:hover {
  background-color: #4338ca;
  transform: translateY(-1px);
  box-shadow: 0 6px 12px -2px rgba(79, 70, 229, 0.25);
}
</style>
