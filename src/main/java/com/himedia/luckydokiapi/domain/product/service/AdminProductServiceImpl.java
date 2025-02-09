package com.himedia.luckydokiapi.domain.product.service;

import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.product.dto.ProductSearchDTO;
import com.himedia.luckydokiapi.domain.product.entity.*;
import com.himedia.luckydokiapi.domain.product.repository.*;
import com.himedia.luckydokiapi.domain.shop.entity.Shop;
import com.himedia.luckydokiapi.domain.shop.repository.ShopRepository;
import com.himedia.luckydokiapi.dto.PageResponseDTO;
import com.himedia.luckydokiapi.util.file.CustomFileUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;



@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class AdminProductServiceImpl implements AdminProductService {

    private final CustomFileUtil fileUtil;

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryBridgeRepository categoryBridgeRepository;
    private final TagRepository tagRepository;
    private final ProductTagRepository productTagRepository;
    private final ShopRepository shopRepository;

    private final ProductService productService;


    @Transactional(readOnly = true)
    @Override
    public PageResponseDTO<ProductDTO.Response> list(ProductSearchDTO requestDTO) {
        log.info("ProductAdminService list...");

        Page<Product> result = productRepository.findListBy(requestDTO);

        return PageResponseDTO.<ProductDTO.Response>withAll()
                .dtoList(result.stream().map(this::entityToDTO).collect(Collectors.toList()))
                .totalCount(result.getTotalElements())
                .pageRequestDTO(requestDTO)
                .build();
    }

    //admin 프로덕트 확인용
    @Transactional(readOnly = true)
    @Override
    public ProductDTO.Response getOne(Long id) {
        Product product = this.getEntity(id);
        return this.entityToDTO(product);
    }

    @Override
    public Long register(ProductDTO.Request dto) {

        // 파일 업로드 처리
        if (dto.getFiles() != null || !dto.getFiles().isEmpty()) {
            List<MultipartFile> files = dto.getFiles();
            List<String> uploadFileNames = fileUtil.uploadS3Files(files);
            log.info("uploadFileNames: {}", uploadFileNames);
            dto.setUploadFileNames(uploadFileNames);
        }

        // excel 업로드 imagePathList 있을시 s3 업로드
        if (dto.getImagePathList() != null) {
            log.info("excel 업로드 이미지 파일 존재! dto.getImagePathList(): {}", dto.getImagePathList());
            dto.setUploadFileNames(fileUtil.uploadImagePathS3Files(dto.getImagePathList()));
        }


        Category category = this.getCategory(dto.getCategoryId());
        Shop shop = this.getShop(dto.getShopId());
        // 실제 저장 처리
        Product result = productRepository.save(productService.dtoToEntity(dto, category, shop));
        log.info("product result: {}", result);

        // 카테고리 처리
        if(dto.getCategoryId() != null) {
            categoryBridgeRepository.save(CategoryBridge.from(category, result));
        }

        // 태그 처리
        if (dto.getTagStrList() != null) {
            dto.getTagStrList().forEach(tag -> {
                String tagName = tag.trim();
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

        return result.getId();
    }


    @Override
    public Long modify(Long id, ProductDTO.Request dto) {

        Product product = this.getEntity(id);

        ProductDTO.Request request = this.entityToReqDTO(product);

        // 파일 업로드 처리
        //기존의 파일들 (데이터베이스에 존재하는 파일들 - 수정 과정에서 삭제되었을 수 있음)
        List<String> oldFileNames = request.getUploadFileNames();

        //새로 업로드 해야 하는 파일들
        List<MultipartFile> files = dto.getFiles();

        //새로 업로드되어서 만들어진 파일 이름들
        List<String> currentUploadFileNames = fileUtil.uploadS3Files(files);

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
            fileUtil.deleteS3Files(removeFiles);
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

        // 파일 삭제
        fileUtil.deleteS3Files(product.getImageList().stream()
                .map(ProductImage::getImageName)
                .collect(Collectors.toList()));

        // 파일 삭제
        product.clearImageList();
        // 해당 참조한 태그 삭제
        List<ProductTag> productTags = product.getProductTagList();
        log.info("productTags: {}", productTags);
        if(productTags != null && !productTags.isEmpty()) {
            // deleteAll && clear 모두 해야 삭제된다.
            productTagRepository.deleteAll(productTags);
            product.clearTagList();
        }

        productRepository.modifyDeleteFlag(product.getId());
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
