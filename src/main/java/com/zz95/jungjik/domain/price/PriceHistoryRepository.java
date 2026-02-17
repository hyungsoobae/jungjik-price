package com.zz95.jungjik.domain.price;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {

    /**
     * 특정 상품의 전체 가격 이력 조회 (수집일시 오름차순)
     */
    List<PriceHistory> findByProductIdOrderByCollectedAtAsc(Long productId);

    /**
     * 특정 상품의 기간별 가격 이력 조회 (수집일시 오름차순)
     * - 프론트 기간 필터(1주, 2주, 1달)에서 since 값을 계산해서 전달
     */
    List<PriceHistory> findByProductIdAndCollectedAtAfterOrderByCollectedAtAsc(
            Long productId,
            LocalDateTime since
    );
}
