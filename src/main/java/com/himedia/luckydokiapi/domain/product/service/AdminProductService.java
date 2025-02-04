package com.himedia.luckydokiapi.domain.product.service;


import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.product.entity.Category;
import com.himedia.luckydokiapi.domain.product.entity.Product;
import com.himedia.luckydokiapi.domain.product.entity.ProductImage;


import java.util.List;

// API 명세서 -> JDOC
public interface AdminProductService {


    Long register(ProductDTO productDTO);

    /**
     * Product -> ProductDTO 변환
     *
     * @param product Product
     * @return ProductDTO
     */
    default ProductDTO entityToDTO(Product product) {

        ProductDTO productDTO = ProductDTO.builder()
                .id(product.getId())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .name(product.getName())
                .price(product.getPrice())
                .discountPrice(product.getDiscountPrice())
                .description(product.getDescription())
                .mdPick(product.getMdPick())
                .createdAt(product.getCreatedAt())
                .modifiedAt(product.getModifiedAt())
                .build();

        List<ProductImage> imageList = product.getImageList();

        if (imageList == null || imageList.isEmpty()) {
            return productDTO;
        }

        List<String> fileNameList = imageList.stream().map(ProductImage::getImageName).toList();

        productDTO.setUploadFileNames(fileNameList);
        productDTO.setCategoryId(product.getCategory().getId());

        return productDTO;
    }

    default Product dtoToEntity(ProductDTO dto, Category category) {

        Product product = Product.builder()
                .id(dto.getId())
                .category(category)
                .name(dto.getName())
                .price(dto.getPrice())
                .discountPrice(dto.getDiscountPrice())
                .description(dto.getDescription())
                .mdPick(dto.getMdPick())
                .stockNumber(dto.getStockNumber())
                .delFlag(false)
                .build();

        //업로드 처리가 끝난 파일들의 이름 리스트
        List<String> uploadFileNames = dto.getUploadFileNames();

        if (uploadFileNames == null) {
            return product;
        }

        // 이미지 파일 업로드 처리
        uploadFileNames.forEach(product::addImageString);

        return product;
    }
}
