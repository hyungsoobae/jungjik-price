ALTER TABLE product ADD COLUMN current_price INT NOT NULL DEFAULT 0;
COMMENT ON COLUMN product.current_price IS '현재 최신 가격';