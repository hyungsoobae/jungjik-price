ALTER TABLE product DROP COLUMN previous_price;
ALTER TABLE product ADD COLUMN diff_price INT;
ALTER TABLE product ADD COLUMN diff_rate DOUBLE PRECISION;

COMMENT ON COLUMN product.diff_price IS '가장 최근 가격 변동 등락폭';
COMMENT ON COLUMN product.diff_rate IS '가장 최근 가격 변동 등락률';