package com.himedia.luckydokiapi.domain.product.service;

import com.himedia.luckydokiapi.domain.notification.service.NotificationService;
import com.himedia.luckydokiapi.domain.order.service.OrderService;
import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.product.dto.ProductSearchDTO;
import com.himedia.luckydokiapi.domain.product.entity.*;
import com.himedia.luckydokiapi.domain.product.enums.ProductApproval;
import com.himedia.luckydokiapi.domain.product.enums.ProductEvent;
import com.himedia.luckydokiapi.domain.product.enums.ProductIsNew;
import com.himedia.luckydokiapi.domain.product.repository.*;
import com.himedia.luckydokiapi.domain.review.dto.ReviewRequestDTO;
import com.himedia.luckydokiapi.domain.review.service.ReviewService;
import com.himedia.luckydokiapi.domain.shop.entity.Shop;
import com.himedia.luckydokiapi.domain.shop.repository.ShopRepository;
import com.himedia.luckydokiapi.dto.PageResponseDTO;
import com.himedia.luckydokiapi.util.file.CustomFileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

import static com.himedia.luckydokiapi.domain.product.dto.ProductDTO.Response.entityToReqDTO;
import static com.himedia.luckydokiapi.domain.product.enums.ProductBest.N;
import static com.himedia.luckydokiapi.domain.product.enums.ProductBest.Y;


@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class AdminProductServiceImpl implements AdminProductService {

    private final CustomFileService fileService;

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryBridgeRepository categoryBridgeRepository;
    private final TagRepository tagRepository;
    private final ProductTagRepository productTagRepository;
    private final ShopRepository shopRepository;

    private final ReviewService reviewService;
    private final OrderService orderService;

    private final NotificationService notificationService;


    @Transactional(readOnly = true)
    @Override
    public PageResponseDTO<ProductDTO.Response> list(ProductSearchDTO requestDTO) {
        log.info("ProductAdminService list...");

        Page<Product> result = productRepository.findListBy(requestDTO);

        return PageResponseDTO.<ProductDTO.Response>withAll()
                .dtoList(result.stream().map(ProductDTO.Response::toDto).toList())
                .totalCount(result.getTotalElements())
                .pageRequestDTO(requestDTO)
                .build();
    }


    @Transactional(readOnly = true)
    @Override
    public List<ProductDTO.Response> getProductsByApprovalStatus(ProductApproval status) {
        List<Product> products = productRepository.findByApprovalStatus(status);
        return products.stream()
                .map(ProductDTO.Response::toDto)
                .toList();
    }


    @Override
    @Transactional
    public void approveProductAll(List<Long> productIds) {
        List<Product> products = productRepository.findAllById(productIds);

        for (Product product : products) {
            if (product.getApprovalStatus() == ProductApproval.Y) {
                log.info("이미 승인된 상품입니다. productId: {}", product.getId());
                continue;
            }
            product.setApprovalStatus(ProductApproval.Y); // 승인 완료 상태로 변경
        }

        productRepository.saveAll(products);
    }




    //admin 프로덕트 확인용
    @Transactional(readOnly = true)
    @Override
    public ProductDTO.Response getOne(Long id) {
        Product product = this.getEntity(id);
        return ProductDTO.Response.toDto(product);
    }

    @Override
    public Long register(ProductDTO.Request dto) {

        // 파일 업로드 처리
        if (dto.getFiles() != null || !dto.getFiles().isEmpty()) {
            List<MultipartFile> files = dto.getFiles();
            List<String> uploadFileNames = fileService.uploadToThumbnailS3Files(files);
            log.info("uploadFileNames: {}", uploadFileNames);
            dto.setUploadFileNames(uploadFileNames);
        }

        // excel 업로드 imagePathList 있을시 s3 업로드
        if (dto.getImagePathList() != null) {
            log.info("excel 업로드 이미지 파일 존재! dto.getImagePathList(): {}", dto.getImagePathList());
            dto.setUploadFileNames(fileService.uploadImagePathS3Files(dto.getImagePathList()));
        }


        Category category = this.getCategory(dto.getCategoryId());
        Shop shop = this.getShop(dto.getShopId());

        // 상품 추가
        Product newProduct = Product.of(dto, category, shop);
        newProduct.setApprovalStatus(ProductApproval.N);
        log.info("product result: {}", newProduct);
        Product result = productRepository.save(newProduct);


        // 카테고리 처리
        if (dto.getCategoryId() != null) {
            categoryBridgeRepository.save(CategoryBridge.from(category, result));
        }

        // 태그 처리
        if (dto.getTagStrList() != null) {
            dto.getTagStrList().forEach(tag -> {
                // " " 그리고 "#" 제거
                String tagName = tag.replace(" ", "").replace("#", "");
                log.info("tagName: {}", tagName);
                // 이미 기존에 존재하는 태그인지 확인
                Tag savedTag = null;
                if (tagRepository.existsByName(tagName)) {
                    log.info("이미 존재하는 태그입니다. tagName: {}", tagName);
                    savedTag = tagRepository.findByName(tagName);
                } else {
                    log.info("새로운 태그입니다. tagName: {}", tagName);
                    savedTag = tagRepository.save(Tag.from(tagName));
                }
                productTagRepository.save(ProductTag.from(savedTag, result));
            });
        }

        // 리뷰 처리(excel 업로드시)
        if (dto.getReviewList() != null) {
            ReviewRequestDTO reviewRequestDTO = new ReviewRequestDTO();
            reviewRequestDTO.setProductId(result.getId());
            reviewRequestDTO.setRating(5);
            dto.getReviewList().forEach(reviewContent -> {
                reviewRequestDTO.setContent(reviewContent);
                reviewService.createReview("user1@test.com", reviewRequestDTO);
            });
        }

        return result.getId();
    }


    @Override
    public Long modify(Long id, ProductDTO.Request dto) {

        Product product = this.getEntity(id);

        ProductDTO.Request request = entityToReqDTO(product);

        // 파일 업로드 처리
        //기존의 파일들 (데이터베이스에 존재하는 파일들 - 수정 과정에서 삭제되었을 수 있음)
        List<String> oldFileNames = request.getUploadFileNames();

        //새로 업로드 해야 하는 파일들
        List<MultipartFile> files = dto.getFiles();

        //새로 업로드되어서 만들어진 파일 이름들
        List<String> currentUploadFileNames = fileService.uploadToThumbnailS3Files(files);

        //화면에서 변화 없이 계속 유지된 파일들
        List<String> uploadedFileNames = dto.getUploadFileNames();

        //유지되는 파일들  + 새로 업로드된 파일 이름들이 저장해야 하는 파일 목록이 됨
        if (currentUploadFileNames != null && !currentUploadFileNames.isEmpty()) {

            uploadedFileNames.addAll(currentUploadFileNames);

        }

        //기존 파일들 중에서 화면에서 삭제된 파일들을 제거
        if (oldFileNames != null && !oldFileNames.isEmpty()) {

            //지워야 하는 파일 목록 찾기
            //예전 파일들 중에서 지워져야 하는 파일이름들
            List<String> removeFiles = oldFileNames.stream()
                    .filter(fileName -> !uploadedFileNames.contains(fileName)).toList();

            //실제 파일 삭제
            fileService.deleteS3Files(removeFiles);
        }

        // 상품 수정
        product.changeCategory(this.getCategory(dto.getCategoryId()));
        product.changeName(dto.getName());
        product.changePrice(dto.getPrice());
        product.changeDiscountPrice(dto.getDiscountPrice());
        product.changeDescription(dto.getDescription());
        product.changeStockNumber(dto.getStockNumber());
        product.clearImageList();

        // 새로 업로드할 파일들을 새로 추가
        List<String> uploadFileNames = dto.getUploadFileNames();

        if (uploadFileNames != null && !uploadFileNames.isEmpty()) {
            uploadFileNames.forEach(product::addImageString);
        }

        return product.getId();

    }

    @Override
    public void remove(Long id) {
        Product product = this.getEntity(id);

        // 만약 이벤트 상품이라면 삭제 불가
        if (product.getEvent() == ProductEvent.Y) {
            throw new IllegalStateException("이벤트 상품은 삭제할 수 없습니다.");
        }

        // 만약 주문된 이력이 있는 상품이라면 삭제 불가
        if (orderService.checkProductOrder(product)) {
            throw new IllegalStateException("주문된 상품은 삭제할 수 없습니다.");
        }

        // 파일 삭제
        fileService.deleteS3Files(product.getImageList().stream()
                .map(ProductImage::getImageName)
                .collect(Collectors.toList()));

        // 이미지 삭제
        product.clearImageList();
        // 해당 참조한 카테고리 삭제
        categoryBridgeRepository.deleteByProduct(product);
        // 해당 참조한 태그 삭제
        productTagRepository.deleteByProduct(product);
        // 해당 참조한 리뷰 삭제
        reviewService.deleteByProduct(product);

        productRepository.modifyDeleteFlag(product.getId());
    }

    @Override
    public void removeAll(List<Long> productIds) {
        List<Product> products = productRepository.findAllById(productIds);

        for (Product product : products) {
            this.remove(product.getId());
        }
    }


    @Override
    public void modifyProductBest(List<Long> modifyProductIdList) {
        List<Product> idList = productRepository.findByIdList(modifyProductIdList);
        idList.forEach(this::changeEnumBest);
    }


    @Override
    public void modifyProductIsNew(List<Long> modifyProductIdList) {
        List<Product> idList = productRepository.findByIdList(modifyProductIdList);
        idList.forEach(this::changeEnumIsNew);
    }

    /**
     * 상품 복사 - 지정된 상품을 복제하여 새로운 상품으로 등록
     * 
     * @param productId 복사할 상품 ID
     * @return 복사된 새 상품의 ID
     */
    @Override
    @Transactional
    public Long copyProduct(Long productId) {
        log.info("상품 복사 시작. 원본 상품 ID: {}", productId);
        
        // 원본 상품 조회
        Product originalProduct = this.getEntity(productId);
        
        // 상품 정보를 Request DTO로 변환
        ProductDTO.Request productRequest = entityToReqDTO(originalProduct);
        
        // 태그 정보 복사
        List<String> tagStrList = originalProduct.getProductTagList().stream()
                .map(productTag -> "#" + productTag.getTag().getName())
                .collect(Collectors.toList());
        productRequest.setTagStrList(tagStrList);
        
        // register 메소드를 활용하여 상품 등록
        Long newProductId = register(productRequest);
        
        log.info("상품 복사 완료. 새 상품 ID: {}", newProductId);
        return newProductId;
    }


    @Override
    public void approveProduct(List<Long> productIdList) {
        List<Product> idList = productRepository.findByIdList(productIdList);
        idList.forEach(this::changeApproval);

        // send notification
        notificationService.sendProductApprovalNotification(idList);
    }


    //enums 값 변경
    public void changeEnumBest(Product product) {
        product.changeBest(product.getBest().equals(Y) ? N : Y);
    }


    public void changeEnumIsNew(Product product) {
        product.changeIsNew(product.getIsNew().equals(ProductIsNew.Y) ? ProductIsNew.N : ProductIsNew.Y);
    }

    public void changeApproval(Product product) {
        product.changeApprovalStatus(product.getApprovalStatus().equals(ProductApproval.Y) ? ProductApproval.N : ProductApproval.Y);
    }

    /**
     * Entity 찾기
     *
     * @param id 엔티티 id
     * @return DTO
     */
    private Product getEntity(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 엔티티가 존재하지 않습니다. id: " + id));
    }

    /**
     * 상품 카테고리 찾기
     *
     * @param categoryId 카테고리 id
     * @return 카테고리
     */
    private Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("해당 카테고리가 존재하지 않습니다. id: " + categoryId));
    }

    private Shop getShop(Long shopId) {
        return shopRepository.findById(shopId)
                .orElseThrow(() -> new EntityNotFoundException("해당 seller 가 존재하지 않습니다 " + shopId));
    }


}
