<script setup>
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import axios from "axios";

const router = useRouter();

// 페이징 및 데이터 상태
const boardList = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const totalItems = ref(0);

// 검색 상태
const searchType = ref("all");
const searchKeyword = ref("");

const totalPages = computed(() => {
  return Math.ceil(totalItems.value / pageSize.value) || 1;
});

const getList = async (page = 1) => {
  try {
    const response = await axios.get("http://localhost:8080/api/board/list", {
      params: {
        page: page,
        size: pageSize.value,
        searchType: searchType.value,
        searchKeyword: searchKeyword.value,
      },
    });

    if (response.data && Array.isArray(response.data.list)) {
      boardList.value = response.data.list;
      totalItems.value = Number(response.data.total) || 0;
    } else if (Array.isArray(response.data)) {
      boardList.value = response.data;
      totalItems.value = response.data.length;
    } else {
      boardList.value = [];
      totalItems.value = 0;
    }

    currentPage.value = page;
  } catch (error) {
    alert("목록을 불러오는데 실패했습니다.");
    console.error(error);
  }
};

const handleSearch = () => {
  getList(1);
};

const handleResetSearch = () => {
  searchType.value = "all";
  searchKeyword.value = "";
  getList(1);
};

const changePage = (page) => {
  if (page < 1 || page > totalPages.value) return;
  getList(page);
};

onMounted(() => {
  getList(1);
});
</script>

<template>
  <div class="container">
    <!-- 헤더 -->
    <div class="board-header">
      <div class="title-section">
        <h2>커뮤니티 게시판</h2>
        <p class="subtitle">다양한 이야기와 정보를 나누는 공간입니다.</p>
      </div>
      <button class="write-btn" @click="router.push('/board/write')">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="16"
          height="16"
          fill="currentColor"
          viewBox="0 0 16 16"
        >
          <path
            d="M12.854.146a.5.5 0 0 0-.707 0L10.5 1.793 14.207 5.5l1.647-1.646a.5.5 0 0 0 0-.708zm.646 6.061L9.793 2.5 3.293 9H3.5a.5.5 0 0 1 .5.5v.5h.5a.5.5 0 0 1 .5.5v.5h.5a.5.5 0 0 1 .5.5v.5h.5a.5.5 0 0 1 .5.5v.207zm-7.468 7.468A.5.5 0 0 1 6 13.5V13h-.5a.5.5 0 0 1-.5-.5V12h-.5a.5.5 0 0 1-.5-.5V11h-.5a.5.5 0 0 1-.5-.5V10h-.5a.5.5 0 0 1-.175-.032l-.179.178a.5.5 0 0 0-.11.168l-2 5a.5.5 0 0 0 .65.65l5-2a.5.5 0 0 0 .168-.11z"
          />
        </svg>
        새 글 작성
      </button>
    </div>

    <!-- 검색 바 -->
    <div class="search-bar">
      <select v-model="searchType" class="search-select">
        <option value="all">전체 (제목+내용)</option>
        <option value="title">제목</option>
        <option value="writer">작성자</option>
        <option value="content">내용</option>
      </select>
      <input
        type="text"
        v-model="searchKeyword"
        class="search-input"
        placeholder="검색어를 입력하세요..."
        @keyup.enter="handleSearch"
      />
      <button class="search-btn" @click="handleSearch">검색</button>
      <button v-if="searchKeyword" class="reset-btn" @click="handleResetSearch">
        초기화
      </button>
    </div>

    <!-- 테이블 -->
    <div class="table-card">
      <table>
        <thead>
          <tr>
            <th style="width: 10%">번호</th>
            <th style="width: 55%">제목</th>
            <th style="width: 20%">작성자</th>
            <th style="width: 15%">조회수</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="(board, index) in boardList"
            :key="board.boardId"
            @click="router.push(`/board/${board.boardId}`)"
          >
            <td class="num-cell">
              {{ totalItems - (currentPage - 1) * pageSize - index }}
            </td>

            <!-- [첨부파일 표시 적용] 제목 영역 -->
            <td class="title-cell">
              <div class="title-wrapper">
                <span class="title-text">{{ board.title }}</span>

                <!-- fileList 배열 또는 fileCount 숫자로 존재 여부 확인 -->
                <span
                  v-if="
                    (board.fileList && board.fileList.length > 0) ||
                    board.fileCount > 0
                  "
                  class="file-badge"
                  title="첨부파일 있음"
                >
                  📎
                  <span class="file-count-text">
                    {{
                      board.fileList ? board.fileList.length : board.fileCount
                    }}
                  </span>
                </span>
              </div>
            </td>

            <td class="writer-cell">
              <span class="avatar">{{
                board.writer?.substring(0, 1) || "U"
              }}</span>
              <span class="writer-name">{{ board.writer }}</span>
            </td>
            <td class="view-cell">
              <span class="badge">{{
                board.viewCnt ?? board.viewCount ?? 0
              }}</span>
            </td>
          </tr>
          <tr v-if="boardList.length === 0">
            <td colspan="4" class="empty-cell">
              <div class="empty-state">
                <p>등록된 게시글이 없거나 검색 결과가 없습니다.</p>
                <p class="sub">다른 검색어로 시도하거나 첫 글을 남겨보세요!</p>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 페이지네이션 -->
    <div class="pagination-container" v-if="boardList.length > 0">
      <button
        class="page-nav-btn"
        :disabled="currentPage === 1"
        @click="changePage(currentPage - 1)"
      >
        이전
      </button>

      <div class="page-numbers">
        <button
          v-for="page in totalPages"
          :key="page"
          class="page-num-btn"
          :class="{ active: currentPage === page }"
          @click="changePage(page)"
        >
          {{ page }}
        </button>
      </div>

      <button
        class="page-nav-btn"
        :disabled="currentPage === totalPages"
        @click="changePage(currentPage + 1)"
      >
        다음
      </button>
    </div>
  </div>
</template>

<style scoped>
.container {
  width: 900px;
  margin: 50px auto;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto,
    "Helvetica Neue", Arial, sans-serif;
  color: #333d4b;
}

/* 헤더 */
.board-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 20px;
}
.title-section h2 {
  font-size: 26px;
  font-weight: 700;
  margin: 0 0 6px 0;
  letter-spacing: -0.5px;
}
.title-section .subtitle {
  font-size: 14px;
  color: #8b95a1;
  margin: 0;
}

/* 검색 */
.search-bar {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
  justify-content: flex-end;
  align-items: center;
}
.search-select {
  padding: 10px 14px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  background-color: white;
  color: #374151;
}
.search-input {
  width: 240px;
  padding: 10px 14px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
}
.search-btn {
  background-color: #3b82f6;
  color: white;
  border: none;
  padding: 10px 18px;
  font-size: 14px;
  font-weight: 600;
  border-radius: 8px;
  cursor: pointer;
}
.reset-btn {
  background-color: #f3f4f6;
  color: #4b5563;
  border: 1px solid #d1d5db;
  padding: 10px 14px;
  font-size: 14px;
  border-radius: 8px;
  cursor: pointer;
}

.write-btn {
  background-color: #4f46e5;
  color: white;
  border: none;
  padding: 12px 20px;
  font-size: 14px;
  font-weight: 600;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  box-shadow: 0 4px 6px -1px rgba(79, 70, 229, 0.2);
}

.table-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  border: 1px solid #f2f4f6;
}

table {
  width: 100%;
  border-collapse: collapse;
  text-align: left;
  table-layout: fixed;
}

th {
  background-color: #f9fafb;
  color: #6b7280;
  font-size: 13px;
  font-weight: 600;
  padding: 16px 24px;
  border-bottom: 1px solid #edf0f2;
  text-transform: uppercase;
}

td {
  padding: 18px 24px;
  border-bottom: 1px solid #f2f4f6;
  font-size: 14px;
  vertical-align: middle;
}

tbody tr {
  cursor: pointer;
}
tbody tr:hover {
  background-color: #f8fafc;
}

.num-cell {
  color: #8b95a1;
  font-weight: 500;
  text-align: center;
}

/* 제목 셀 및 첨부파일 배지 스타일 */
.title-cell {
  vertical-align: middle;
}

.title-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
}

.title-text {
  font-weight: 600;
  color: #191f28;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  min-width: 0;
  flex-shrink: 1;
}

tbody tr:hover .title-text {
  color: #4f46e5;
}

/* 첨부파일 배지 */
.file-badge {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  font-size: 11px;
  background-color: #e0e7ff;
  color: #4338ca;
  padding: 3px 7px;
  border-radius: 6px;
  font-weight: 600;
  flex-shrink: 0; /* 제목이 길어져도 배지는 축소되지 않음 */
  line-height: 1;
}

.file-count-text {
  font-size: 11px;
}

.writer-cell {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #4e5968;
}

.avatar {
  width: 26px;
  height: 26px;
  background-color: #e0e7ff;
  color: #4f46e5;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  font-weight: 700;
  flex-shrink: 0;
}

.writer-name {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.view-cell {
  text-align: center;
}

.badge {
  background-color: #f2f4f6;
  color: #6b7280;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.empty-cell {
  padding: 60px 0;
  text-align: center;
}
.empty-state p {
  margin: 0;
  font-size: 16px;
  color: #8b95a1;
}
.empty-state .sub {
  font-size: 13px;
  margin-top: 6px;
  color: #b0b8c1;
}

/* 페이징 */
.pagination-container {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;
  margin-top: 28px;
}
.page-numbers {
  display: flex;
  gap: 6px;
}
.page-nav-btn {
  background-color: white;
  border: 1px solid #d1d5db;
  color: #4b5563;
  padding: 8px 14px;
  font-size: 13px;
  border-radius: 6px;
  cursor: pointer;
}
.page-nav-btn:disabled {
  color: #d1d5db;
  border-color: #e5e7eb;
  cursor: not-allowed;
}
.page-num-btn {
  background-color: white;
  border: 1px solid #e5e7eb;
  color: #4b5563;
  width: 34px;
  height: 34px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 600;
  border-radius: 6px;
  cursor: pointer;
}
.page-num-btn.active {
  background-color: #4f46e5;
  color: white;
  border-color: #4f46e5;
}
</style>
