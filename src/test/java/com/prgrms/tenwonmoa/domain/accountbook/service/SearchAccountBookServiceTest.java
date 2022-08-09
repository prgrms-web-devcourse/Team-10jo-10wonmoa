package com.prgrms.tenwonmoa.domain.accountbook.service;

import static com.prgrms.tenwonmoa.common.fixture.Fixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prgrms.tenwonmoa.domain.accountbook.AccountBookConst;
import com.prgrms.tenwonmoa.domain.accountbook.dto.AccountBookItem;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindAccountBookResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.service.SearchAccountBookCmd;
import com.prgrms.tenwonmoa.domain.accountbook.repository.SearchAccountBookRepository;
import com.prgrms.tenwonmoa.domain.category.Category;
import com.prgrms.tenwonmoa.domain.category.CategoryType;
import com.prgrms.tenwonmoa.domain.category.UserCategory;
import com.prgrms.tenwonmoa.domain.common.page.PageCustomRequest;
import com.prgrms.tenwonmoa.domain.user.User;

@DisplayName("가계부 검색 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class SearchAccountBookServiceTest {

	@Mock
	private SearchAccountBookRepository accountBookRepository;

	@InjectMocks
	private SearchAccountBookService accountBookService;

	private final User user = createUser();

	private final UserCategory foodCategory = new UserCategory(createUser(),
		new Category("식비", CategoryType.EXPENDITURE));

	private final UserCategory cultureCategory = new UserCategory(createUser(),
		new Category("문화생활", CategoryType.EXPENDITURE));

	private final UserCategory salaryCategory = new UserCategory(createUser(), new Category("월급", CategoryType.INCOME));

	private final Long userId = 1L;

	@Test
	void 가계부_검색_성공() {
		//given
		AccountBookItem latestIncome = new AccountBookItem(1L, CategoryType.INCOME.name(), 30000L,
			"용돈", salaryCategory.getCategoryName(), LocalDateTime.now().minusHours(1L));

		AccountBookItem secondExpenditure = new AccountBookItem(1L, CategoryType.EXPENDITURE.name(), 10000L,
			"점심", foodCategory.getCategoryName(), LocalDateTime.now().minusHours(5L));

		AccountBookItem thirdExpenditure = new AccountBookItem(2L, CategoryType.EXPENDITURE.name(), 20000L,
			"영화", cultureCategory.getCategoryName(), LocalDateTime.now().minusDays(1L));

		AccountBookItem fourthExpenditure = new AccountBookItem(3L, CategoryType.EXPENDITURE.name(), 30000L,
			"문화생활", cultureCategory.getCategoryName(), LocalDateTime.now().minusDays(2L));

		AccountBookItem fifthIncome = new AccountBookItem(2L, CategoryType.INCOME.name(), 100000L,
			"월급", salaryCategory.getCategoryName(), LocalDateTime.now().minusDays(3L));

		SearchAccountBookCmd cmd = SearchAccountBookCmd.of("1,2,3", 0L, 1_000_000_000L,
			AccountBookConst.LEFT_MOST_REGISTER_DATE, AccountBookConst.RIGHT_MOST_REGISTER_DATE, "");
		PageCustomRequest pageRequest = new PageCustomRequest(1, 3);

		given(accountBookRepository.searchAccountBook(cmd.getMinPrice(), cmd.getMaxPrice(), cmd.getStart(),
			cmd.getEnd(), cmd.getContent(), cmd.getCategories(), userId, pageRequest))
			.willReturn(List.of(latestIncome, secondExpenditure, thirdExpenditure));

		//when
		FindAccountBookResponse<AccountBookItem> findAccountBookResponse =
			accountBookService.searchAccountBooks(userId, cmd, pageRequest);

		//then
		assertThat(findAccountBookResponse.getResults().size()).isEqualTo(pageRequest.getSize());
		assertThat(findAccountBookResponse.getResults())
			.extracting(AccountBookItem::getCategoryName)
			.containsExactlyInAnyOrder(
				latestIncome.getCategoryName(),
				secondExpenditure.getCategoryName(),
				thirdExpenditure.getCategoryName());

		assertThat(findAccountBookResponse.getIncomeSum()).isEqualTo(latestIncome.getAmount());
		assertThat(findAccountBookResponse.getExpenditureSum())
			.isEqualTo(secondExpenditure.getAmount() + thirdExpenditure.getAmount());
	}

}
