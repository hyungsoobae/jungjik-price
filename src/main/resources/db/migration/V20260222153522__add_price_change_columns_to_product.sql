ALTER TABLE product ADD COLUMN previous_price INT;
ALTER TABLE product ADD COLUMN price_changed_at TIMESTAMP;

COMMENT ON COLUMN product.previous_price IS '가격 변동 이전 가격';
COMMENT ON COLUMN product.price_changed_at IS '가장 최근 가격 변동 일시';