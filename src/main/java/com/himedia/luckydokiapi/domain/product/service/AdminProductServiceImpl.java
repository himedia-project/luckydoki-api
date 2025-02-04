package com.himedia.luckydokiapi.domain.product.service;

import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.product.entity.Category;
import com.himedia.luckydokiapi.domain.product.entity.Product;
import com.himedia.luckydokiapi.domain.product.entity.ProductTag;
import com.himedia.luckydokiapi.domain.product.entity.Tag;
import com.himedia.luckydokiapi.domain.product.repository.CategoryRepository;
import com.himedia.luckydokiapi.domain.product.repository.ProductRepository;
import com.himedia.luckydokiapi.domain.product.repository.ProductTagRepository;
import com.himedia.luckydokiapi.domain.product.repository.TagRepository;
import com.himedia.luckydokiapi.util.file.CustomFileUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class AdminProductServiceImpl implements AdminProductService {

    private final CustomFileUtil fileUtil;

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final ProductTagRepository productTagRepository;


    @Override
    public Long register(ProductDTO dto) {

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

        // 카테고리
        Category category = this.getCategory(dto.getCategoryId());

        // 실제 저장 처리
        Product result = productRepository.save(this.dtoToEntity(dto, category));
        log.info("product result: {}", result);

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
}
