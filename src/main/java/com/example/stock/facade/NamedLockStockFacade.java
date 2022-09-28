package com.example.stock.facade;

import com.example.stock.repository.LockRepository;
import com.example.stock.service.StockService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class NamedLockStockFacade {
	/* 로직 실행 전, 후로 getLock, releaseLock을 호출하기 위한 클래스 */

	private final LockRepository lockRepository;
	private final StockService stockService;

	public NamedLockStockFacade(LockRepository lockRepository, StockService stockService) {
		this.lockRepository = lockRepository;
		this.stockService = stockService;
	}

	@Transactional
	public void decrease(Long id, Long quantity) {
		/* decrease 전, 후로 getLock과 releaseLock 호출 */
		try {
			lockRepository.getLock(id.toString());
			stockService.decrease(id, quantity);
		} finally {
			lockRepository.releaseLock(id.toString());
		}
	}

}
