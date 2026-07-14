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
            <td class="num-cell">{{ boardList.length - index }}</td>
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
                <p>등록된 게시글이 없습니다.</p>
                <p class="sub">첫 번째 이야기를 먼저 남겨보세요!</p>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<style scoped>
/* 시스템 폰트 및 모던 스타일 적용 */
.container {
  width: 900px;
  margin: 50px auto;
  font-family:
    -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue",
    Arial, sans-serif;
  color: #333d4b; /* 토스 스타일의 깊은 그레이 */
}

/* 헤더 스타일 */
.board-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 24px;
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
</style>
