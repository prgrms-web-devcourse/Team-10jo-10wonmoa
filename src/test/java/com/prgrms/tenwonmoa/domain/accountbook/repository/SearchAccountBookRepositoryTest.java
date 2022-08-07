package com.prgrms.tenwonmoa.domain.accountbook.repository;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static com.prgrms.tenwonmoa.domain.accountbook.AccountBookConst.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.prgrms.tenwonmoa.common.RepositoryTest;
import com.prgrms.tenwonmoa.domain.accountbook.Expenditure;
import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.CategoryType;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.common.page.PageCustomRequest;
import com.prgrms.tenwonmoa.domain.user.User;

@DisplayName("가계부 검색 리포지토리 테스트")
class SearchAccountBookRepositoryTest extends RepositoryTest {

	@Autowired
	private SearchAccountBookRepository repository;

	private User user;

	private Category expenditureCategory;
	private Category expenditureCategory2;

	private UserCategory expenditureUserCategory;
	private UserCategory expenditureUserCategory2;

	@BeforeEach
	void setup() {
		user = save(createUser());

		expenditureCategory = save(new Category("식비", CategoryType.EXPENDITURE));
		expenditureCategory2 = save(new Category("문화생활", CategoryType.EXPENDITURE));

		expenditureUserCategory = save(new UserCategory(user, expenditureCategory));
		expenditureUserCategory2 = save(new UserCategory(user, expenditureCategory2));
	}

	@Test
	void 금액_검색_조건으로_지출_조회() {
		save(new Expenditure(
			LocalDateTime.now().minusDays(2), 1000L, "점심식사",
			expenditureCategory.getName(), user, expenditureUserCategory));

		save(new Expenditure(
			LocalDateTime.now().minusDays(1), 10000L, "영화",
			expenditureCategory2.getName(), user, expenditureUserCategory2));

		save(new Expenditure(
			LocalDateTime.now().minusDays(1), 10000L, "영화관람",
			expenditureCategory2.getName(), user, expenditureUserCategory2));

		List<Long> allUserCategoryIds = List.of(expenditureUserCategory.getId(), expenditureUserCategory2.getId());

		List<Expenditure> firstPage = repository.searchExpenditures(
			1000L, 20000L, LEFT_MOST_REGISTER_DATE, RIGHT_MOST_REGISTER_DATE,
			"", allUserCategoryIds, user.getId(), new PageCustomRequest(1, 2));

		List<Expenditure> secondPage = repository.searchExpenditures(
			1000L, 20000L, LEFT_MOST_REGISTER_DATE, RIGHT_MOST_REGISTER_DATE,
			"", allUserCategoryIds, user.getId(), new PageCustomRequest(2, 2));

		assertThat(firstPage).extracting(Expenditure::getAmount).containsExactly(10000L, 10000L);
		assertThat(secondPage).extracting(Expenditure::getAmount).containsExactly(1000L);
	}

	@Test
	void 내용_조건으로_지출_조회() {
		save(new Expenditure(
			LocalDateTime.now().minusDays(2), 1000L, "점심식사",
			expenditureCategory.getName(), user, expenditureUserCategory));

		save(new Expenditure(
			LocalDateTime.now().minusDays(3), 10000L, "영화",
			expenditureCategory2.getName(), user, expenditureUserCategory2));

		save(new Expenditure(
			LocalDateTime.now().minusDays(2), 10000L, "영화관람",
			expenditureCategory2.getName(), user, expenditureUserCategory2));

		save(new Expenditure(
			LocalDateTime.now().minusDays(1), 10000L, "문화 영화 관람",
			expenditureCategory2.getName(), user, expenditureUserCategory2));

		List<Long> allUserCategoryIds = List.of(expenditureUserCategory.getId(), expenditureUserCategory2.getId());

		List<Expenditure> firstPage = repository.searchExpenditures(
			AMOUNT_MIN, AMOUNT_MAX, LEFT_MOST_REGISTER_DATE,
			RIGHT_MOST_REGISTER_DATE, "영화", allUserCategoryIds, user.getId(), new PageCustomRequest(1, 2));

		List<Expenditure> secondPage = repository.searchExpenditures(
			AMOUNT_MIN, AMOUNT_MAX, LEFT_MOST_REGISTER_DATE,
			RIGHT_MOST_REGISTER_DATE, "영화", allUserCategoryIds, user.getId(), new PageCustomRequest(2, 2));

		assertThat(firstPage).extracting(Expenditure::getContent).containsExactly("문화 영화 관람", "영화관람");
		assertThat(secondPage).extracting(Expenditure::getContent).containsExactly("영화");
	}

	@Test
	void 등록날짜_조건으로_지출_조회() {
		LocalDateTime registerDate = LocalDateTime.now().minusDays(10);
		LocalDateTime registerDate2 = LocalDateTime.now().minusDays(5);
		LocalDateTime registerDate3 = LocalDateTime.now().minusDays(2);
		LocalDateTime registerDate4 = LocalDateTime.now().minusDays(1);

		save(new Expenditure(
			registerDate, 1000L, "점심식사",
			expenditureCategory.getName(), user, expenditureUserCategory));

		save(new Expenditure(
			registerDate2, 10000L, "영화",
			expenditureCategory2.getName(), user, expenditureUserCategory2));

		save(new Expenditure(
			registerDate3, 10000L, "영화관람",
			expenditureCategory2.getName(), user, expenditureUserCategory2));

		save(new Expenditure(
			registerDate4, 10000L, "문화 영화 관람",
			expenditureCategory2.getName(), user, expenditureUserCategory2));

		List<Long> allUserCategoryIds = List.of(expenditureUserCategory.getId(), expenditureUserCategory2.getId());

		List<Expenditure> results = repository.searchExpenditures(
			AMOUNT_MIN, AMOUNT_MAX, LocalDate.now().minusDays(6),
			LocalDate.now(), "", allUserCategoryIds, user.getId(), new PageCustomRequest(1, 10));

		assertThat(results).extracting(Expenditure::getRegisterDate)
			.containsExactly(registerDate4, registerDate3, registerDate2);
	}

	@Test
	void 카테고리_아이디로_지출_조회() {
		save(new Expenditure(
			LocalDateTime.now().minusDays(10), 1000L, "점심식사",
			expenditureCategory.getName(), user, expenditureUserCategory));

		save(new Expenditure(
			LocalDateTime.now().minusDays(5), 10000L, "영화",
			expenditureCategory2.getName(), user, expenditureUserCategory2));

		save(new Expenditure(
			LocalDateTime.now().minusDays(2), 10000L, "영화관람",
			expenditureCategory2.getName(), user, expenditureUserCategory2));

		save(new Expenditure(
			LocalDateTime.now().minusDays(1), 10000L, "문화 영화 관람",
			expenditureCategory2.getName(), user, expenditureUserCategory2));

		List<Expenditure> results = repository.searchExpenditures(
			AMOUNT_MIN, AMOUNT_MAX, LEFT_MOST_REGISTER_DATE,
			RIGHT_MOST_REGISTER_DATE, "", List.of(expenditureUserCategory.getId()), user.getId(),
			new PageCustomRequest(1, 10));

		List<Expenditure> results2 = repository.searchExpenditures(
			AMOUNT_MIN, AMOUNT_MAX, LEFT_MOST_REGISTER_DATE,
			RIGHT_MOST_REGISTER_DATE, "", List.of(expenditureUserCategory2.getId()), user.getId(),
			new PageCustomRequest(1, 10));

		assertThat(results).hasSize(1);
		assertThat(results2).hasSize(3);
	}
}
