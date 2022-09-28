package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockService {

	private StockRepository stockRepository;

	public StockService(StockRepository stockRepository) {
		this.stockRepository = stockRepository;
	}

	/* Facade의 트랜잭션과 별도로 트랜잭션을 실행하도록 한다 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public synchronized void decrease(Long id, Long quantity) {

		Stock stock = stockRepository.findById(id).orElseThrow();

		stock.decrease(quantity);
		stockRepository.saveAndFlush(stock);
	}
}
