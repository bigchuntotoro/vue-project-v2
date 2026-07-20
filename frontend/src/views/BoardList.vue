<script setup>
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import axios from "axios";

const router = useRouter();

// 페이징 관련 상태 관리
const boardList = ref([]); // 현재 페이지의 게시글 데이터
const currentPage = ref(1); // 현재 활성화된 페이지 번호
const pageSize = ref(10); // 한 페이지당 보여줄 개수
const totalItems = ref(0); // 전체 게시글 개수 (0으로 초기화하여 NaN 방지)

// [추가] 검색 관련 상태 관리
const searchType = ref("all"); // 검색 기준 (all: 전체, title: 제목, writer: 작성자, content: 내용)
const searchKeyword = ref(""); // 검색어 입력값

// 총 페이지 수 계산
const totalPages = computed(() => {
  return Math.ceil(totalItems.value / pageSize.value) || 1;
});

// 백엔드 데이터 조회 함수 (검색 파라미터 포함)
const getList = async (page = 1) => {
  try {
    const response = await axios.get("http://localhost:8080/api/board/list", {
      params: {
        page: page,
        size: pageSize.value,
        searchType: searchType.value, // 검색 유형 전달
        searchKeyword: searchKeyword.value, // 검색어 전달
      },
    });

    // 백엔드 반환 구조: { list: [...], total: 25 }
    if (response.data && Array.isArray(response.data.list)) {
      boardList.value = response.data.list;
      totalItems.value = Number(response.data.total) || 0;
    } else if (Array.isArray(response.data)) {
      // 하위 호환성 예외 처리 (배열만 넘어왔을 경우)
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

// [추가] 검색 실행 함수 (검색 시 항상 1페이지부터 조회)
const handleSearch = () => {
  getList(1);
};

// [추가] 검색 조건 초기화 함수
const handleResetSearch = () => {
  searchType.value = "all";
  searchKeyword.value = "";
  getList(1);
};

// 페이지 변경 함수
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
    <!-- 상단 헤더 영역 -->
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

    <!-- [추가] 검색 영역 -->
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

    <!-- 테이블 카드 레이아웃 -->
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
            <!-- 페이징 시 역순 번호 계산식 보정 -->
            <td class="num-cell">
              {{ totalItems - (currentPage - 1) * pageSize - index }}
            </td>
            <td class="title-cell">{{ board.title }}</td>
            <td class="writer-cell">
              <span class="avatar">{{
                board.writer?.substring(0, 1) || "U"
              }}</span>
              {{ board.writer }}
            </td>
            <td class="view-cell">
              <span class="badge">{{ board.viewCnt }}</span>
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

    <!-- 모던 페이징 네비게이션 바 -->
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
/* 시스템 폰트 및 모던 스타일 적용 */
.container {
  width: 900px;
  margin: 50px auto;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto,
    "Helvetica Neue", Arial, sans-serif;
  color: #333d4b; /* 토스 스타일의 깊은 그레이 */
}

/* 헤더 스타일 */
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

/* [추가] 검색바 스타일 */
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
  transition: border-color 0.2s;
}
.search-select:focus {
  border-color: #4f46e5;
}
.search-input {
  width: 240px;
  padding: 10px 14px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.2s;
}
.search-input:focus {
  border-color: #4f46e5;
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
  transition: background-color 0.2s;
}
.search-btn:hover {
  background-color: #2563eb;
}
.reset-btn {
  background-color: #f3f4f6;
  color: #4b5563;
  border: 1px solid #d1d5db;
  padding: 10px 14px;
  font-size: 14px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}
.reset-btn:hover {
  background-color: #e5e7eb;
}

/* 버튼 스타일 (인디고 블루 테마) */
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
  transition: all 0.2s ease;
}
.write-btn:hover {
  background-color: #4338ca;
  transform: translateY(-1px);
  box-shadow: 0 6px 12px -2px rgba(79, 70, 229, 0.3);
}

/* 테이블 카드 감싸기 (그림자 및 라운드 처리) */
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
}

/* 테이블 헤더 */
th {
  background-color: #f9fafb;
  color: #6b7280;
  font-size: 13px;
  font-weight: 600;
  padding: 16px 24px;
  border-bottom: 1px solid #edf0f2;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* 테이블 바디 */
td {
  padding: 18px 24px;
  border-bottom: 1px solid #f2f4f6;
  font-size: 14px;
  vertical-align: middle;
}

tr:last-child td {
  border-bottom: none;
}

/* 행 오버 효과 */
tbody tr {
  cursor: pointer;
  transition: background-color 0.15s ease;
}
tbody tr:hover {
  background-color: #f8fafc;
}

/* 셀별 커스텀 스타일 */
.num-cell {
  color: #8b95a1;
  font-weight: 500;
  text-align: center;
}
.title-cell {
  font-weight: 600;
  color: #191f28;
  max-width: 400px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
tbody tr:hover .title-cell {
  color: #4f46e5; /* 마우스 올렸을 때 제목 색상 강조 */
}

/* 작성자 프로필 느낌 아이콘 추가 */
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

/* 데이터가 없을 때 스타일 */
.empty-cell {
  padding: 60px 0;
}
.empty-state {
  text-align: center;
  color: #8b95a1;
}
.empty-state p {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
}
.empty-state .sub {
  font-size: 13px;
  margin-top: 6px;
  color: #b0b8c1;
}

/* 페이징 컴포넌트 추가 스타일 */
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
  font-weight: 500;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
}
.page-nav-btn:hover:not(:disabled) {
  background-color: #f3f4f6;
  border-color: #9ca3af;
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
  transition: all 0.2s;
}
.page-num-btn:hover:not(.active) {
  background-color: #f3f4f6;
  border-color: #d1d5db;
}
.page-num-btn.active {
  background-color: #4f46e5;
  color: white;
  border-color: #4f46e5;
  box-shadow: 0 2px 4px rgba(79, 70, 229, 0.2);
}
</style>
