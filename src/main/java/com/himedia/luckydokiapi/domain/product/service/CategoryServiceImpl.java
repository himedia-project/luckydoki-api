package com.himedia.luckydokiapi.domain.product.service;


import com.himedia.luckydokiapi.domain.likes.repository.ProductLikeRepository;
import com.himedia.luckydokiapi.domain.product.dto.AdminCategoriesDTO;
import com.himedia.luckydokiapi.domain.product.dto.CategoryDTO;
import com.himedia.luckydokiapi.domain.product.dto.ChildCategoryDTO;
import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.product.entity.Category;
import com.himedia.luckydokiapi.domain.product.entity.Product;
import com.himedia.luckydokiapi.domain.product.repository.CategoryRepository;
import com.himedia.luckydokiapi.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductServiceImpl productServiceImpl;
    private final ProductLikeRepository productLikeRepository;

    //파라미터 없이 최상위 카테고리들만 조회
    @Transactional(readOnly = true)
    @Override
    public List<CategoryDTO> getParentCategories() {
        List<Category> mainCategories = categoryRepository.findMainCategories();
        return mainCategories.stream().map(this::entityToCategoriesDTO).toList();
    }


    /**
     * 해당 카테고리의 하위 카테고리들을 조회
     * @param categoryId 해당 카테고리 아이디
     * @return 하위 카테고리 리스트
     */
    @Transactional(readOnly = true)
    @Override
    public List<ChildCategoryDTO> getChildCategories(Long categoryId) {
        return categoryRepository.findChildCategories(categoryId).stream()
                .map(this::entityToChildCategoryDTO)
                .toList();

    }


    //main -> sub
    @Transactional(readOnly = true)
    @Override
    public List<CategoryDTO> getSubCategoryList(Long mainCategoryId) {
        List<Category> subCategories = categoryRepository.findSubCategories(mainCategoryId);
        return subCategories.stream().map(this::entityToCategoriesDTO).toList();
    }

    //sub -> child
    @Transactional(readOnly = true)
    @Override
    public List<CategoryDTO> getChildCategoryList(Long subCategoryId) {
        List<Category> childCategories = categoryRepository.findChildCategories(subCategoryId);
        return childCategories.stream().map(this::entityToCategoriesDTO).toList();
    }


    //카테고리 아이디 조건식
    @Transactional(readOnly = true)
    @Override
    public List<ProductDTO.Response> getProductCategoryId(Long categoryId , String email) {
        List<Product> productList = productRepository.findByProductCategoryId(categoryId);
        return productList.stream().map(product -> productServiceImpl.entityToDTO(product,productLikeRepository.likes(email, product.getId()))).toList();
    } // 람다식을 사용하여 각각의 요소들을 전달하여 하나씩 dto 로 변환 후 list 로 수집


    //admin 용 카테고리 리스트
    @Transactional(readOnly = true)
    @Override
    public List<AdminCategoriesDTO> getCategory(Long categoryId) {
        List<Category> dtoLists = categoryRepository.findByCategoryId(categoryId);
        return dtoLists.stream()
                .map(this::entityToDTO) // Product를 ProductDTO로 변환
                .toList();
    }


//    @Override
//    public List<ProductDTO> getCategoryProducts(Long categoryId) {
//        List<Product> productList = categoryRepository.findListByCategory(categoryId);
//        if (productList.isEmpty()) {
//            throw new EntityNotFoundException("해당 카테고리에 대한 상품이 없습니다.");
//        }
//
//        List<ProductDTO> productDTOList = productList.stream().map(product ->
//                        adminProductService.entityToDTO(product))
//                .collect(Collectors.toList());
//
//        return productDTOList;
//    }

}
