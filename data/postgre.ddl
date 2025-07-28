create table public.market_daily_performance (
    data_date date,
    index_code varchar(10),
    index_name varchar(100),
    index_value decimal(12,4),
    point_change decimal(12,4),
    percentage_change decimal(5,2)
);
insert into public.market_daily_performance (data_date, index_code, index_name, index_value, point_change, percentage_change) values
('2025-07-10', 'SPX',  'S&P 500',         5532.1500,  25.1300,  0.46),
('2025-07-10', 'DJI',  'Dow Jones',       39888.1200, 102.4500, 0.26),
('2025-07-10', 'IXIC', 'Nasdaq',          18322.4400, 134.8800, 0.74),
('2025-07-10', 'HSI',  'Hang Seng',       18765.9200, -150.3200, -0.79),
('2025-07-10', 'SSE',  'Shanghai SE',     2987.5100,  -6.4300,  -0.21),
('2025-07-10', 'N225', 'Nikkei 225',      41320.7700, 312.8000, 0.76),
('2025-07-10', 'FTSE', 'FTSE 100',        8264.5100,   5.2400,  0.06),
('2025-07-10', 'DAX',  'DAX Index',       18230.2000, -42.1000, -0.23),
('2025-07-10', 'STI',  'Straits Times',   3310.4400,  12.8700,  0.39),
('2025-07-10', 'KOSPI','KOSPI',           2763.5500, -21.5700, -0.78);
select * from public.market_daily_performance;

-- 其他SQL (ruoyi-Vue3-PostgreSQL):
-- https://github.com/libin9iOak/ruoyi-Vue3-PostgreSQL/blob/main/sql/%E7%AC%AC%E4%B8%80%E6%AD%A5-postgresql.sql