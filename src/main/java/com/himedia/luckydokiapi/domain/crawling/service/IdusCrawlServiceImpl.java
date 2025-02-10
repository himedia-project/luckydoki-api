package com.himedia.luckydokiapi.domain.crawling.service;

import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class IdusCrawlServiceImpl implements CrawlService {

    private static final String BASE_IMAGE_URL = "https://thumbnail6.coupangcdn.com/thumbnails/remote/292x292q65ex/image/";

    public List<ProductDTO.Request> crawl(String url) {
        // Chrome 옵션 설정
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

        WebDriver driver = new ChromeDriver(options);
        List<ProductDTO.Request> productDTOList = new ArrayList<>();
        ProductDTO.Request request = null;

        try {
            // 페이지 로딩
            driver.get(url);
            // 페이지 로딩 대기
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // 상품명 추출 (명시적 대기 적용)
            String name = wait.until(ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//*[@id=\"__nuxt\"]/div/div/div[5]/div[1]/div/div[1]/div[2]/div[3]")))
                    .getText()
                    .split("\n")[0];  // 첫 번째 줄만 가져오기
            log.info("상품명: {}", name);
            
            // 가격 정보 추출
            String priceText = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//*[@id=\"__nuxt\"]/div/div/div[5]/div[1]/div/div[1]/div[2]/div[4]/div/div[1]/div/span[1]")))
                .getText().replaceAll("[^0-9]", "");
            int price = Integer.parseInt(priceText);
            log.info("가격: {}", price);
            
            // 할인가격 (할인이 없다면 원래 가격으로 설정)
            int discountPrice = price;
            try {
                String discountPriceText = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("#__nuxt > div > div > div.appContents > div:nth-child(1) > div > div.PdpAtf > div.PdpAtf__right > div.flex.items-center.justify-between.mb-\\[12px\\] > div > div.flex.items-center.mb-\\[4px\\] > div:nth-child(2) > span")))
                    .getText().replaceAll("[^0-9]", "");
                if (!discountPriceText.isEmpty()) {
                    discountPrice = Integer.parseInt(discountPriceText);
                }
            } catch (TimeoutException e) {
                log.info("할인 가격이 없습니다.");
            }
            log.info("할인 가격: {}", discountPrice);

            // 상품 설명 추출
            String description = "";
            try {
                // 작품정보 탭으로 이동
                WebElement descTab = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(text(), '작품정보')]")));
                descTab.click();

                // 페이지 로딩을 위한 잠시 대기
                Thread.sleep(1000);

                // 작품정보 내용 추출
                description = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//*[@id=\"DESC\"]/div[1]/div[1]/div[2]"))).getText();
                log.info("상품 설명: {}", description);
            } catch (Exception e) {
                log.error("상품 설명 추출 중 오류 발생: ", e);
            }

            // 이미지 URL 리스트 추출
//            List<String> imagePathList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
//                            By.cssSelector("#__nuxt > div > div > div.appContents > div:nth-child(1) > div > div.DesktopProductDetailImagePreview > div > div.BaseOverlay__content > div > div > div > div.BaseCarousel > section > div.carousel__viewport > ol > li.carousel__slide.carousel__slide--visible.carousel__slide--active > div > img")))
//                    .stream()
//                    .map(img -> {
//                        String srcUrl = img.getAttribute("src");
//                        // "//" 로 시작하는 URL을 "https://" 로 변환
//                        return srcUrl.startsWith("//") ? "https:" + srcUrl : srcUrl;
//                    })
//                    .collect(Collectors.toList());
//            log.info("이미지 URL 리스트: {}", imagePathList);

            // 태그 리스트 추출
            List<String> tagStrList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.xpath("//*[@id=\"__nuxt\"]/div/div/div[5]/div[1]/div/div[3]/div[1]/div[4]/div[2]/span/div/div/div/span[2]")))
                .stream()
                .map(WebElement::getText)
                .filter(text -> !text.isEmpty())  // 빈 문자열 필터링
                .collect(Collectors.toList());
            log.info("태그 리스트: {}", tagStrList);

            // DTO 생성
            request = ProductDTO.Request.builder()
                    .name(name)
                    .price(price)
                    .discountPrice(discountPrice)
                    .description(description)
//                    .uploadFileNames(imagePathList)
                    .tagStrList(tagStrList)
                    .build();

            productDTOList.add(request);

        } catch (Exception e) {
            log.error("크롤링 중 오류 발생: ", e);
        } finally {
            driver.quit();
        }

        return productDTOList;
    }

}

