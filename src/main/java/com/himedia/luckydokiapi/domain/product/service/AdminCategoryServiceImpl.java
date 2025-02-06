package com.himedia.luckydokiapi.domain.product.service;


import com.himedia.luckydokiapi.domain.product.dto.CategoryDTO;
import com.himedia.luckydokiapi.domain.product.entity.Category;
import com.himedia.luckydokiapi.domain.product.repository.CategoryRepository;
import com.himedia.luckydokiapi.domain.product.repository.ProductRepository;
import com.himedia.luckydokiapi.util.file.CustomFileUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminCategoryServiceImpl implements AdminCategoryService {

    private final CategoryRepository categoryRepository;
    private final CustomFileUtil fileUtil;

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    @Override
    public List<CategoryDTO> list() {
        return categoryRepository.findAll().stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Long register(CategoryDTO dto) {
        // 파일 s3 업로드
        if (dto.getFile() != null || dto.getFile().isEmpty()) {
            MultipartFile file = dto.getFile();
            String uploadFileName = fileUtil.uploadS3File(file);
            dto.setFileName(uploadFileName);
        }
        Category result = categoryRepository.save(this.dtoToEntity(dto));
        return result.getId();
    }

    @Override
    public Long modify(Long categoryId, CategoryDTO categoryDTO) {
        Category category = this.getEntity(categoryId);

        // 파일 s3 업로드 & 기존 파일 삭제
        if (categoryDTO.getFile() != null || categoryDTO.getFile().isEmpty()) {

            if(category.getLogo() != null) {
                fileUtil.deleteS3File(category.getLogo());
            }

            MultipartFile file = categoryDTO.getFile();
            String uploadFileName = fileUtil.uploadS3File(file);
            category.changeLogo(uploadFileName);
        }

        return category.getId();
    }

    @Override
    public void remove(Long categoryId) {
        // 연관관계가 있는 카테고리라면 삭제 불가능
        if (productRepository.existsByCategoryId(categoryId)) {
            throw new IllegalStateException("연관된 상품이 있어 카테고리 삭제가 불가능합니다.");
        }

        categoryRepository.deleteById(categoryId);
        // 파일 삭제
        fileUtil.deleteS3File(this.getEntity(categoryId).getLogo());
    }


    /**
     * entity를 찾기
     * @param categoryId 카테고리 id
     * @return 카테고리 entity
     */
    private Category getEntity(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("해당 카테고리가 존재하지 않습니다. id: " + categoryId));
    }
}
