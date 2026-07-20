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
  fileList: [], // 💡 기존 첨부파일 목록
});

// 파일 관련 상태 관리
const selectedFiles = ref([]); // 새롭게 선택한 파일들 (File 객체)
const deleteFileIds = ref([]); // 💡 수정 시 삭제할 기존 파일 PK 목록
const fileInputRef = ref(null);

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

// 💡 이미지 파일 여부 확인
const isImage = (filename) => {
  if (!filename) return false;
  const ext = filename.split(".").pop().toLowerCase();
  return ["jpg", "jpeg", "png", "gif", "webp", "bmp", "svg"].includes(ext);
};

// 💡 백엔드 이미지 URL 생성
const getFileUrl = (saveName) => {
  return `http://localhost:8080/upload/${saveName}`;
};

// 💡 기존 파일 삭제 목록에 추가 (화면상에서 우선 제거)
const removeExistingFile = (fileId) => {
  deleteFileIds.value.push(fileId);
  board.value.fileList = board.value.fileList.filter(
    (file) => file.fileId !== fileId,
  );
};

// 새로 추가할 파일 선택 시 (최대 5개 제한 검증)
const handleFileChange = (event) => {
  const files = Array.from(event.target.files);
  const currentTotalCount =
    (board.value.fileList?.length || 0) + selectedFiles.value.length;

  if (currentTotalCount + files.length > 5) {
    alert("첨부파일은 기존 파일 포함 최대 5개까지만 등록 가능합니다.");
    event.target.value = "";
    return;
  }

  selectedFiles.value = [...selectedFiles.value, ...files];
  event.target.value = "";
};

// 선택한 신규 파일 목록에서 특정 파일 제거
const removeSelectedFile = (index) => {
  selectedFiles.value.splice(index, 1);
};

// 파일 용량 단위 변환 함수
const formatFileSize = (bytes) => {
  if (!bytes || bytes === 0) return "0 Bytes";
  const k = 1024;
  const sizes = ["Bytes", "KB", "MB"];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + " " + sizes[i];
};

// 저장 버튼 클릭시 (등록 혹은 수정)
const saveBoard = async () => {
  if (
    !board.value.title ||
    !board.value.content ||
    (!isEditMode.value && !board.value.writer)
  ) {
    alert("모든 필수 필드를 입력해주세요.");
    return;
  }

  try {
    const formData = new FormData();

    if (isEditMode.value) {
      // 💡 1. 수정 처리 (Multipart/FormData)
      // 수정할 기본 데이터와 삭제할 파일 ID 리스트 전달
      const updateData = {
        title: board.value.title,
        content: board.value.content,
        deleteFileIds: deleteFileIds.value, // 삭제 대상 파일 ID 배열
      };

      const boardBlob = new Blob([JSON.stringify(updateData)], {
        type: "application/json",
      });
      formData.append("board", boardBlob);

      // 새롭게 추가 첨부한 파일들
      selectedFiles.value.forEach((file) => {
        formData.append("files", file);
      });

      await axios.put(`http://localhost:8080/api/board/${boardId}`, formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });

      alert("수정되었습니다.");
      router.push(`/board/${boardId}`);
    } else {
      // 2. 신규 등록 처리
      const boardBlob = new Blob([JSON.stringify(board.value)], {
        type: "application/json",
      });
      formData.append("board", boardBlob);

      selectedFiles.value.forEach((file) => {
        formData.append("files", file);
      });

      await axios.post("http://localhost:8080/api/board", formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });

      alert("등록되었습니다.");
      router.push("/board");
    }
  } catch (error) {
    console.error(error);
    alert(error.response?.data || "저장에 실패했습니다.");
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
            ? "기존 내용을 다듬고 첨부파일을 관리할 수 있습니다."
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
          rows="10"
          placeholder="나누고 싶은 이야기를 자유롭게 적어보세요."
        ></textarea>
      </div>

      <!-- 🖼️ [수정 모드] 기존 첨부파일 및 이미지 관리 목록 -->
      <div
        v-if="isEditMode && board.fileList && board.fileList.length > 0"
        class="form-group"
      >
        <label>기존 첨부파일</label>
        <ul class="file-list">
          <li
            v-for="file in board.fileList"
            :key="file.fileId"
            class="file-item existing-file-item"
          >
            <div class="file-info">
              <!-- 이미지 파일인 경우 썸네일 노출 -->
              <img
                v-if="isImage(file.originalName)"
                :src="getFileUrl(file.saveName)"
                alt="thumb"
                class="thumb-img"
              />
              <span v-else class="file-icon">📄</span>

              <span class="file-name">{{ file.originalName }}</span>
              <span class="file-size" v-if="file.fileSize"
                >({{ formatFileSize(file.fileSize) }})</span
              >
            </div>
            <button
              type="button"
              class="remove-file-btn"
              @click="removeExistingFile(file.fileId)"
              title="파일 삭제"
            >
              ✕
            </button>
          </li>
        </ul>
      </div>

      <!-- 📎 첨부파일 추가 업로드 영역 (등록/수정 공통) -->
      <div class="form-group">
        <div class="file-header">
          <label>{{ isEditMode ? "추가 파일 첨부" : "첨부파일" }}</label>
          <span class="file-count">
            <b>{{ (board.fileList?.length || 0) + selectedFiles.length }}</b> /
            5개
          </span>
        </div>

        <!-- 커스텀 파일 선택 버튼 -->
        <div class="file-upload-box" @click="fileInputRef.click()">
          <input
            ref="fileInputRef"
            type="file"
            multiple
            class="hidden-file-input"
            @change="handleFileChange"
          />
          <div class="upload-placeholder">
            <span class="upload-icon">📁</span>
            <span>클릭하여 파일을 선택하세요 (최대 5개)</span>
          </div>
        </div>

        <!-- 새로 선택된 파일 리스트 -->
        <ul v-if="selectedFiles.length > 0" class="file-list">
          <li
            v-for="(file, index) in selectedFiles"
            :key="index"
            class="file-item"
          >
            <div class="file-info">
              <span class="new-tag">NEW</span>
              <span class="file-name">{{ file.name }}</span>
              <span class="file-size">({{ formatFileSize(file.size) }})</span>
            </div>
            <button
              type="button"
              class="remove-file-btn"
              @click="removeSelectedFile(index)"
            >
              ✕
            </button>
          </li>
        </ul>
      </div>
    </div>

    <!-- 하단 버튼 영역 -->
    <div class="btn-group">
      <button type="button" class="cancel-btn" @click="router.back()">
        취소
      </button>
      <button type="button" class="submit-btn" @click="saveBoard">
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
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto,
    "Helvetica Neue", Arial, sans-serif;
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
input[type="text"],
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

input::placeholder,
textarea::placeholder {
  color: #b0b8c1;
}

input[type="text"]:focus,
textarea:focus {
  border-color: #4f46e5;
  box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.12);
}

.disabled-input {
  background-color: #f9fafb;
  color: #8b95a1;
  border-color: #e5e7eb;
  cursor: not-allowed;
}

textarea {
  resize: vertical;
  font-family: inherit;
  line-height: 1.5;
}

/* ---------------- 파일 업로드 스타일 ---------------- */
.file-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.file-count {
  font-size: 13px;
  color: #8b95a1;
}

.file-count b {
  color: #4f46e5;
}

.hidden-file-input {
  display: none;
}

.file-upload-box {
  border: 2px dashed #e5e7eb;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  background-color: #f9fafb;
  cursor: pointer;
  transition: all 0.2s ease;
}

.file-upload-box:hover {
  border-color: #4f46e5;
  background-color: #f5f3ff;
}

.upload-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 14px;
  color: #6b7280;
}

.upload-icon {
  font-size: 18px;
}

/* 파일 목록 스타일 */
.file-list {
  list-style: none;
  padding: 0;
  margin: 12px 0 0 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.file-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 14px;
  background-color: #f3f4f6;
  border-radius: 8px;
  font-size: 14px;
}

.existing-file-item {
  background-color: #f8fafc;
  border: 1px solid #e2e8f0;
}

.file-info {
  display: flex;
  align-items: center;
  gap: 10px;
  overflow: hidden;
}

/* 🖼️ 기존 썸네일 이미지 스타일 */
.thumb-img {
  width: 36px;
  height: 36px;
  object-fit: cover;
  border-radius: 6px;
  border: 1px solid #cbd5e1;
  flex-shrink: 0;
}

.file-icon {
  font-size: 18px;
}

.new-tag {
  font-size: 10px;
  font-weight: 700;
  background-color: #4f46e5;
  color: white;
  padding: 2px 6px;
  border-radius: 4px;
}

.file-name {
  color: #374151;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 380px;
}

.file-size {
  color: #9ca3af;
  font-size: 12px;
}

.remove-file-btn {
  background: none;
  border: none;
  color: #9ca3af;
  font-size: 14px;
  padding: 4px 8px;
  border-radius: 4px;
  cursor: pointer;
}

.remove-file-btn:hover {
  color: #ef4444;
  background-color: #fee2e2;
}

/* ---------------- 버튼 스타일 ---------------- */
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

.cancel-btn {
  background-color: #f2f4f6;
  color: #4e5968;
}
.cancel-btn:hover {
  background-color: #e5e8eb;
}

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
