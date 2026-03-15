CREATE TABLE product_target_price_alert (
    id           BIGSERIAL PRIMARY KEY,
    product_id   BIGINT NOT NULL,
    target_price INT NOT NULL,
    created_at   TIMESTAMP NOT NULL DEFAULT now(),
    updated_at   TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT fk_product_target_price_alert_product FOREIGN KEY (product_id) REFERENCES product (id),
    CONSTRAINT uq_product_target_price_alert_product UNIQUE (product_id)
);

COMMENT ON TABLE product_target_price_alert IS '상품 목표가 알림 설정';
COMMENT ON COLUMN product_target_price_alert.id IS '알림 설정 ID';
COMMENT ON COLUMN product_target_price_alert.product_id IS '상품 ID';
COMMENT ON COLUMN product_target_price_alert.target_price IS '목표가';
COMMENT ON COLUMN product_target_price_alert.created_at IS '등록일시';
COMMENT ON COLUMN product_target_price_alert.updated_at IS '수정일시';