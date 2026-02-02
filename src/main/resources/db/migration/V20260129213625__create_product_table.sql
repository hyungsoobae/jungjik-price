CREATE TABLE product (
    id BIGSERIAL PRIMARY KEY,
    external_product_id VARCHAR(50) NOT NULL,
    source VARCHAR(30) NOT NULL,
    name VARCHAR(255) NOT NULL,
    product_url TEXT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT uq_product_source_external_id UNIQUE (source, external_product_id)
);

COMMENT ON TABLE product IS '가격 추적 대상 상품 정보';
COMMENT ON COLUMN product.id IS '상품 ID';
COMMENT ON COLUMN product.external_product_id IS '외부 상품 ID';
COMMENT ON COLUMN product.source IS '상품 출처';
COMMENT ON COLUMN product.name IS '상품명';
COMMENT ON COLUMN product.product_url IS '상품 페이지 URL';
COMMENT ON COLUMN product.is_active IS '가격 추적 활성 여부';
COMMENT ON COLUMN product.created_at IS '등록일시';
COMMENT ON COLUMN product.updated_at IS '수정일시';



CREATE TABLE price_history (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    price INT NOT NULL,
    collected_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_price_history_product FOREIGN KEY (product_id) REFERENCES product (id)
);

COMMENT ON TABLE price_history IS '상품 가격 수집 이력';
COMMENT ON COLUMN price_history.id IS '가격 이력 ID';
COMMENT ON COLUMN price_history.product_id IS '상품 ID';
COMMENT ON COLUMN price_history.price IS '상품 가격';
COMMENT ON COLUMN price_history.collected_at IS '수집일시';