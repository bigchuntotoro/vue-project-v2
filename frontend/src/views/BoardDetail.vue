<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import axios from "axios";

const route = useRoute();
const router = useRouter();
const board = ref({});
const boardId = route.params.id;

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

// 💡 [추가] 이미지 파일 여부 확인 함수
const isImage = (filename) => {
  if (!filename) return false;
  const ext = filename.split(".").pop().toLowerCase();
  return ["jpg", "jpeg", "png", "gif", "webp", "bmp", "svg"].includes(ext);
};

// 💡 [추가] 백엔드 파일 다운로드/조회 URL 생성 (파일 PK 또는 saveName 기준)
const getFileUrl = (saveName) => {
  // 백엔드 static 리소스 설정 또는 FileDownload Controller 주소
  return `http://localhost:8080/upload/${saveName}`;
};

onMounted(() => {
  getDetail();
});
</script>

<template>
  <div class="container" v-if="board.boardId">
    <!-- 상단 이동 경로 버튼 -->
    <div class="navigation">
      <span class="back-link" @click="router.push('/board')"
        >← 게시판 목록으로 돌아가기</span
      >
    </div>

    <!-- 상세 보기 카드 레이아웃 -->
    <div class="detail-card">
      <!-- 1. 헤더 영역 (제목 & 메타정보) -->
      <div class="card-header">
        <h1 class="post-title">{{ board.title }}</h1>
        <div class="post-meta">
          <div class="meta-left">
            <span class="avatar">{{
              board.writer?.substring(0, 1) || "U"
            }}</span>
            <span class="writer-name">{{ board.writer }}</span>
          </div>
          <div class="meta-right">
            <span class="meta-item"
              >조회수 <strong>{{ board.viewCnt }}</strong></span
            >
          </div>
        </div>
      </div>

      <!-- 2. 본문 영역 -->
      <div class="card-body">
        <!-- 🖼️ [추가] 이미지 첨부파일 영역 (본문 상단에 자동 출력) -->
        <div
          v-if="board.fileList && board.fileList.length > 0"
          class="image-gallery"
        >
          <template v-for="file in board.fileList" :key="file.fileId">
            <div v-if="isImage(file.originalName)" class="image-item">
              <img
                :src="getFileUrl(file.saveName)"
                :alt="file.originalName"
                class="content-image"
              />
            </div>
          </template>
        </div>

        <!-- 본문 텍스트 -->
        <div class="content">{{ board.content }}</div>

        <!-- 📎 [추가] 전체 첨부파일 다운로드 목록 영역 -->
        <div
          v-if="board.fileList && board.fileList.length > 0"
          class="attachment-section"
        >
          <div class="attachment-header">
            <span>📎 첨부파일 ({{ board.fileList.length }})</span>
          </div>
          <ul class="file-list">
            <li
              v-for="file in board.fileList"
              :key="file.fileId"
              class="file-item"
            >
              <a
                :href="getFileUrl(file.saveName)"
                target="_blank"
                download
                class="file-download-link"
              >
                <span class="file-name">{{ file.originalName }}</span>
                <span class="file-size" v-if="file.fileSize">
                  ({{ Math.round(file.fileSize / 1024) }} KB)
                </span>
              </a>
            </li>
          </ul>
        </div>
      </div>
    </div>

    <!-- 하단 버튼 영역 -->
    <div class="btn-group">
      <div class="left-btns">
        <button class="list-btn" @click="router.push('/board')">목록</button>
      </div>
      <div class="right-btns">
        <button class="edit-btn" @click="router.push(`/board/edit/${boardId}`)">
          수정
        </button>
        <button class="delete-btn" @click="deleteBoard">삭제</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 시스템 폰트 및 모던 스타일 통합 */
.container {
  width: 750px;
  margin: 40px auto;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto,
    "Helvetica Neue", Arial, sans-serif;
  color: #333d4b;
}

/* 상단 내비게이션 */
.navigation {
  margin-bottom: 16px;
}
.back-link {
  font-size: 14px;
  color: #4f46e5;
  font-weight: 500;
  cursor: pointer;
  transition: color 0.15s ease;
}
.back-link:hover {
  color: #4338ca;
  text-decoration: underline;
}

/* 메인 상세 보기 카드 */
.detail-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
  border: 1px solid #f2f4f6;
  overflow: hidden;
  margin-bottom: 24px;
}

/* 카드 헤더 */
.card-header {
  padding: 32px 32px 24px 32px;
  border-bottom: 1px solid #f2f4f6;
  background-color: #fafbfc;
}
.post-title {
  font-size: 26px;
  font-weight: 700;
  color: #191f28;
  margin: 0 0 16px 0;
  line-height: 1.4;
  letter-spacing: -0.5px;
}
.post-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.meta-left {
  display: flex;
  align-items: center;
  gap: 10px;
}
.avatar {
  width: 28px;
  height: 28px;
  background-color: #e0e7ff;
  color: #4f46e5;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
}
.writer-name {
  font-weight: 600;
  color: #4e5968;
  font-size: 14px;
}
.meta-right {
  font-size: 13px;
  color: #8b95a1;
}
.meta-item strong {
  color: #4e5968;
  font-weight: 600;
}

/* 카드 본문 영역 */
.card-body {
  padding: 40px 32px;
  min-height: 280px;
  background-color: #ffffff;
}

/* 🖼️ 이미지 갤러리 스타일 */
.image-gallery {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 24px;
}
.image-item {
  width: 100%;
  border-radius: 12px;
  overflow: hidden;
  background-color: #f8fafc;
  border: 1px solid #f1f5f9;
}
.content-image {
  width: 100%;
  max-height: 500px;
  object-fit: contain;
  display: block;
}

/* 텍스트 본문 */
.content {
  font-size: 16px;
  line-height: 1.8;
  color: #333d4b;
  white-space: pre-wrap;
  word-break: break-all;
}

/* 📎 첨부파일 목록 영역 스타일 */
.attachment-section {
  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px dashed #e5e7eb;
}
.attachment-header {
  font-size: 14px;
  font-weight: 600;
  color: #4b5563;
  margin-bottom: 12px;
}
.file-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.file-item {
  background-color: #f9fafb;
  padding: 10px 14px;
  border-radius: 8px;
  border: 1px solid #f3f4f6;
  display: flex;
  align-items: center;
}
.file-download-link {
  color: #4f46e5;
  text-decoration: none;
  font-size: 13px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 6px;
}
.file-download-link:hover {
  text-decoration: underline;
}
.file-size {
  color: #9ca3af;
  font-size: 12px;
}

/* 버튼 스타일 */
.btn-group {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.right-btns {
  display: flex;
  gap: 12px;
}

button {
  padding: 11px 22px;
  font-size: 14px;
  font-weight: 600;
  border-radius: 8px;
  cursor: pointer;
  border: none;
  transition: all 0.2s ease;
}

.list-btn {
  background-color: #f2f4f6;
  color: #4e5968;
}
.list-btn:hover {
  background-color: #e5e8eb;
}

.edit-btn {
  background-color: #4f46e5;
  color: white;
}
.edit-btn:hover {
  background-color: #4338ca;
}

.delete-btn {
  background-color: #fee2e2;
  color: #ef4444;
}
.delete-btn:hover {
  background-color: #fca5a5;
  color: #b91c1c;
}
</style>
