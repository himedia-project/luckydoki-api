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
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (2, 'N', 's_f94f8d0f-d93f-45b4-ac65-111e4bf5f53d-1-4.png', '주얼리', 1);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (3, 'N', NULL, '케이스/문구', NULL);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (4, 'N', NULL, '반려동물', NULL);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (5, 'N', 's_df0c6a17-b7d2-4fee-be57-fd783fa67f9d-7-1_bg_removed.png.png', '사료/간식', 4);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (6, 'Y', NULL, '사료', 5);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (7, 'Y', NULL, '간식', 5);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (8, 'N', 's_3c660dbb-bddd-40db-aa94-314a98319c0d-8-3.png', '반려패션', 4);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (9, 'Y', NULL, '의류', 8);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (10, 'Y', NULL, '패션악세서리', 8);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (11, 'N', 's_ccc49e59-802e-457a-97b5-cc18fe3c8e55-9-3.png', '반려용품', 4);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (12, 'Y', NULL, '장난감', 11);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (13, 'N', 's_24a47992-d83a-4ba8-addc-5c412731733c-11.png', '케이스/엑세서리', 3);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (14, 'N', 's_f09e6716-a073-4acf-8b7b-c8f145814308-5-1.png', '문구/취미', 3);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (15, 'N', 's_443d6b79-32f1-4612-b859-0464890c23b7-6-2.png', '기념일/파티', 3);
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
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (27, 'N', 's_85727f06-9cf4-4733-ab88-f0e4834d8cc3-22.png', '의류', 1);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (28, 'Y', NULL, '홈웨어', 27);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (29, 'Y', NULL, '티셔츠/니트/셔츠', 27);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (30, 'Y', NULL, '바지', 27);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (31, 'Y', NULL, '아우터', 27);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (32, 'Y', NULL, '원피스', 27);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (33, 'Y', NULL, '생활한복', 27);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (34, 'N', 's_4bf53a34-98e3-473a-a224-66dd65ac8f72-33.png', '패션잡화', 1);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (35, 'Y', NULL, '여성신발', 34);
INSERT INTO `category` (`id`, `last_type`, `logo`, `name`, `parent_id`) VALUES (36, 'Y', NULL, '남성신발', 34);




INSERT INTO `event` (created_at, end_at, id, modified_at, start_at, content, image, title) VALUES ('2025-02-18 10:43:32', '2025-03-18', 7, '2025-02-18 10:43:32', '2025-02-18', '0시부터 24시까지, 딱 하루만!
망설이는 순간 할인이 끝나요.

최대 70% 할인에 무료배송 혜택까지 더했어요.
지금 바로 득템의 행운을 누려보세요!', 's_859400be-37e9-455b-8503-cc7745f2de62-todayevent.jpg', '오늘만 할인');
INSERT INTO `event` (created_at, end_at, id, modified_at, start_at, content, image, title) VALUES ('2025-02-18 11:43:48', '2025-03-12', 8, '2025-02-18 11:43:48', '2025-02-12', '1020 새내기들을 위한 신학기 할인전이 시작되었어요.
가방부터 문구, 디지털 액세사리까지!
최대 78%까지의 할인 혜택을 만나보세요.
d+ 멤버십에 가입하면 추가 할인 혜택도 받을 수 있어요!', 's_ace1490b-1425-43be-9f96-f0dfa7d21034-star.jpg', '별작가님의 여정에 함께 동참해 보실래요~?');
INSERT INTO `event` (created_at, end_at, id, modified_at, start_at, content, image, title) VALUES ('2025-02-18 11:46:38', '2025-03-12', 10, '2025-02-18 11:46:38', '2025-02-12', '속은 든든하지만 실제로는 가벼운
식단관리 추천템으로 올해는 꼭 성공해보세요.
맛있으면서 포만감까지 채울 수 있는 수제 먹거리로
건강한 식단 관리를 완성할 수 있어요.', 's_f52a9c8a-a00a-4867-a608-7fff8f94db4c-food.jpg', '지금 시작해요! 올해의 식단관리');
INSERT INTO `event` (created_at, end_at, id, modified_at, start_at, content, image, title) VALUES ('2025-02-18 11:49:12', '2025-03-17', 11, '2025-02-18 11:49:12', '2025-02-12', '1020 새내기들을 위한 신학기 할인전이 시작되었어요.
가방부터 문구, 디지털 액세사리까지!
최대 78%까지의 할인 혜택을 만나보세요.
d+ 멤버십에 가입하면 추가 할인 혜택도 받을 수 있어요!', 's_dcd67b8f-b7e2-4f0b-8cc8-5be02de27502-newbee.jpg', '새내기들을 위한 신학기 할인전');
INSERT INTO `event` (created_at, end_at, id, modified_at, start_at, content, image, title) VALUES ('2025-02-18 11:51:11', '2025-03-10', 12, '2025-02-18 11:51:11', '2025-02-06', '우리 아이들의 어린이집, 유치원, 학교 등에서의
즐거운 마무리, 졸업식 시즌이 돌아왔습니다!
이색꽃다발부터 토퍼, 이벤트, 단체선물까지
Best 졸업 선물 작품과 함께 축하해요.', 's_033748f0-dd79-4e7c-bd6f-c3e303614eb3-graduate.jpg', '졸업의 주인공을 더 찬란하고 빛나게');



INSERT INTO luckydoki.event_bridge (created_at, event_id, id, product_id) VALUES ('2025-02-18 10:43:31.835581', 7, 2, 231);
INSERT INTO luckydoki.event_bridge (created_at, event_id, id, product_id) VALUES ('2025-02-18 10:43:31.849705', 7, 3, 230);
INSERT INTO luckydoki.event_bridge (created_at, event_id, id, product_id) VALUES ('2025-02-18 10:43:31.855937', 7, 4, 227);
INSERT INTO luckydoki.event_bridge (created_at, event_id, id, product_id) VALUES ('2025-02-18 10:43:31.861254', 7, 5, 222);
INSERT INTO luckydoki.event_bridge (created_at, event_id, id, product_id) VALUES ('2025-02-18 11:43:48.497369', 8, 6, 247);
INSERT INTO luckydoki.event_bridge (created_at, event_id, id, product_id) VALUES ('2025-02-18 11:43:48.504006', 8, 7, 245);
INSERT INTO luckydoki.event_bridge (created_at, event_id, id, product_id) VALUES ('2025-02-18 11:43:48.507631', 8, 8, 233);
INSERT INTO luckydoki.event_bridge (created_at, event_id, id, product_id) VALUES ('2025-02-18 11:43:48.511716', 8, 9, 229);
INSERT INTO luckydoki.event_bridge (created_at, event_id, id, product_id) VALUES ('2025-02-18 11:46:38.069898', 10, 13, 228);
INSERT INTO luckydoki.event_bridge (created_at, event_id, id, product_id) VALUES ('2025-02-18 11:46:38.073849', 10, 14, 229);
INSERT INTO luckydoki.event_bridge (created_at, event_id, id, product_id) VALUES ('2025-02-18 11:46:38.078525', 10, 15, 231);
INSERT INTO luckydoki.event_bridge (created_at, event_id, id, product_id) VALUES ('2025-02-18 11:49:11.603901', 11, 16, 204);
INSERT INTO luckydoki.event_bridge (created_at, event_id, id, product_id) VALUES ('2025-02-18 11:49:11.607581', 11, 17, 189);
INSERT INTO luckydoki.event_bridge (created_at, event_id, id, product_id) VALUES ('2025-02-18 11:49:11.610392', 11, 18, 170);
INSERT INTO luckydoki.event_bridge (created_at, event_id, id, product_id) VALUES ('2025-02-18 11:51:11.052629', 12, 19, 233);
INSERT INTO luckydoki.event_bridge (created_at, event_id, id, product_id) VALUES ('2025-02-18 11:51:11.056827', 12, 20, 231);
INSERT INTO luckydoki.event_bridge (created_at, event_id, id, product_id) VALUES ('2025-02-18 11:51:11.062643', 12, 21, 172);
