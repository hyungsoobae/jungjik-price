ALTER TABLE product ADD COLUMN current_price INT DEFAULT 0;
COMMENT ON COLUMN price_history.id IS '현재 최신 가격';