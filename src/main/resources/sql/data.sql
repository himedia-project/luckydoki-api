-- 기존 데이터 삭제
SET FOREIGN_KEY_CHECKS = 0;  -- 외래키 체크 비활성화

-- 기존 데이터 삭제
DELETE FROM `coupon`;
DELETE FROM `category`;

-- Auto Increment 초기화
ALTER TABLE `coupon` AUTO_INCREMENT = 1;
ALTER TABLE `category` AUTO_INCREMENT = 1;

SET FOREIGN_KEY_CHECKS = 1;  -- 외래키 체크 다시 활성화

# 쿠폰 데이터 추가
INSERT INTO `coupon` (`end_date`, `start_date`, `id`,`code`, `content`, `name`, `status`, `discount_price`, `minimum_usage_amount`) VALUES ('2026-02-12', '2025-02-12', 1, '3285037658', '😊첫회원가입축하쿠폰! 3000원 할인이 됩니다!', '🎉회원가입축하쿠폰', 'ACTIVE', 3000, 30000);

# 카테고리 데이터 추가
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (1, 'N', NULL, '패션/주얼리', NULL);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (2, 'N', NULL, '주얼리', 1);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (3, 'N', NULL, '케이스/문구', NULL);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (4, 'N', NULL, '반려동물', NULL);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (5, 'N', NULL, '사료/간식', 4);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (6, 'Y', NULL, '사료', 5);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (7, 'Y', NULL, '간식', 5);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (8, 'N', NULL, '반려패션', 4);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (9, 'Y', NULL, '의류', 8);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (10, 'Y', NULL, '패션악세서리', 8);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (11, 'N', NULL, '반려용품', 4);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (12, 'Y', NULL, '장난감', 11);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (13, 'N', NULL, '케이스/엑세서리', 3);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (14, 'N', NULL, '문구/취미', 3);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (15, 'N', NULL, '기념일/파티', 3);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (16, 'Y', NULL, '폰케이스', 13);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (17, 'Y', NULL, '노트북케이스', 13);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (18, 'Y', NULL, '다이어리/스티커', 14);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (19, 'Y', NULL, '노트/필기도구', 14);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (20, 'Y', NULL, '인형/장난감', 14);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (21, 'Y', NULL, '카드/편지지', 15);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (22, 'Y', NULL, '반지', 2);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (23, 'Y', NULL, '귀걸이', 2);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (24, 'Y', NULL, '목걸이', 2);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (25, 'Y', NULL, '팔찌', 2);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (26, 'Y', NULL, '발찌', 2);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (27, 'N', NULL, '의류', 1);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (28, 'Y', NULL, '홈웨어', 27);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (29, 'Y', NULL, '티셔츠/니트/셔츠', 27);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (30, 'Y', NULL, '바지', 27);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (31, 'Y', NULL, '아우터', 27);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (32, 'Y', NULL, '원피스', 27);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (33, 'Y', NULL, '생활한복', 27);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (34, 'N', NULL, '패션잡화', 1);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (35, 'Y', NULL, '여성신발', 34);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (36, 'Y', NULL, '남성신발', 34);





