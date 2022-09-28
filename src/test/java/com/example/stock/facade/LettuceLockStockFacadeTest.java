package com.example.stock.facade;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("Lettuce lock 테스트")
class LettuceLockStockFacadeTest {

	@Autowired
	private LettuceLockStockFacade lettuceLockStockFacade;

	@Autowired
	private StockRepository stockRepository;

	@BeforeEach
	public void before() {
		Stock stock = new Stock(1L, 100L);
		stockRepository.saveAndFlush(stock);
	}

	@AfterEach
	public void after() {
		stockRepository.deleteAll();
	}

	@Test
	public void 동시에_100개의_요청() throws InterruptedException {
		int threadCount = 100;
		Long id = stockRepository.findAll().stream().findFirst().get().getId();

		// 비동기로 실행하는 작업을 단순화해서 사용할 수 있게 해주는 Java API
		ExecutorService executorService = Executors.newFixedThreadPool(32);

		// 다른 스레드에서 실행 중인 작업이 완료될 때까지 대기하게 해주는 클래스
		CountDownLatch latch = new CountDownLatch(threadCount);

		for (int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				try {
					lettuceLockStockFacade.decrease(id, 1L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					latch.countDown();
				}
			});
		}
		latch.await();

		Stock stock = stockRepository.findById(id).orElseThrow();

		// 100 - (1*100) = 0
		assertThat(stock.getQuantity()).isEqualTo(0L);
	}
}
