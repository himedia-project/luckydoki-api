package com.himedia.luckydokiapi.domain.product.service;


import com.himedia.luckydokiapi.domain.likes.repository.ProductLikeRepository;
import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.member.repository.MemberRepository;
import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.product.dto.ProductSearchDTO;
import com.himedia.luckydokiapi.domain.product.dto.TagDTO;
import com.himedia.luckydokiapi.domain.product.entity.*;
import com.himedia.luckydokiapi.domain.product.enums.ProductApproval;
import com.himedia.luckydokiapi.domain.product.repository.*;
import com.himedia.luckydokiapi.domain.shop.entity.Shop;
import com.himedia.luckydokiapi.domain.shop.repository.ShopRepository;
import com.himedia.luckydokiapi.exception.OutOfStockException;
import com.himedia.luckydokiapi.util.file.CustomFileUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.himedia.luckydokiapi.domain.product.entity.QProduct.product;


@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;

    private final ShopRepository shopRepository;
    private final CategoryRepository categoryRepository;

    private final CategoryBridgeRepository categoryBridgeRepository;
    private final ProductTagRepository productTagRepository;
    private final TagRepository tagRepository;
    private final CustomFileUtil fileUtil;
    private final ProductLikeRepository productLikeRepository;


    @Transactional(readOnly = true)
    @Override
    public ProductDTO.Response getProduct(Long id, String email) {
        Product product = getEntity(id);

        // 승인되지 않은 상품이면 예외 발생
        if (product.getApprovalStatus() != ProductApproval.Y) {
            throw new EntityNotFoundException("승인되지 않은 상품입니다. id: " + id);
        }

        boolean likes = (email != null) && productLikeRepository.likes(email, product.getId());
        return this.entityToDTO(product, likes);
    }


    @Transactional(readOnly = true)
    @Override
    public Product getEntity(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 상품이 존재하지 않습니다. id: " + id));

        // 승인되지 않은 상품이면 예외 발생
        if (product.getApprovalStatus() != ProductApproval.Y) {
            throw new EntityNotFoundException("승인되지 않은 상품입니다. id: " + id);
        }

        return product;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ProductDTO.Response> list(ProductSearchDTO requestDTO, String email) {
        List<ProductDTO.Response> productList = productRepository.findByDTO(requestDTO).stream()
                .filter(product -> product.getApprovalStatus() == ProductApproval.Y) // 승인된 상품만 필터링
                .map(product -> {
                    boolean likes = (email != null) && productLikeRepository.likes(email, product.getId());
                    return this.entityToDTO(product, likes);
                }).toList();

        return productList;
    }


    @Transactional(readOnly = true)
    @Override
    public List<TagDTO> tagList(Long id) {
        Product product = getEntity(id);
        return product.getProductTagList().stream()
                .map(productTag -> {
                    Tag tag = productTag.getTag();
                    return TagDTO.builder()
                            .id(tag.getId())
                            .name(tag.getName())
                            .build();
                }).collect(Collectors.toList());
    }

    //셀러가 올린 상품 리스트 확인
    @Transactional(readOnly = true)
    @Override
    public List<ProductDTO.Response> getListByMember(String email) {
        Member member = getMember(email);
        List<Product> productList = productRepository.findProductByShopMemberEmail(member.getEmail()).stream()
                .filter(product -> product.getApprovalStatus() == ProductApproval.Y) // 승인된 상품만 필터링
                .toList();

        return productList.stream()
                .map(product -> this.entityToDTO(
                        product,
                        productLikeRepository.likes(member.getEmail(), product.getId())
                ))
                .toList();
    }

    //seller 의 상품 등록
    @Override
    public Long createProduct(String email, ProductDTO.Request dto) {

        try {
            List<MultipartFile> files = dto.getFiles();
            List<String> uploadS3FilesNames = fileUtil.uploadS3Files(files);
            log.info("uploadS3FilesNames: {}", uploadS3FilesNames);
            //s3업로드
            dto.setUploadFileNames(uploadS3FilesNames);
        } catch (Exception e) {
            throw new RuntimeException("파일 업로드 에러");
        }

        //빌더에 보내서 엔티티 저장
        List<String> uploadFileNames = dto.getUploadFileNames();
        if (uploadFileNames == null || uploadFileNames.isEmpty()) {
            return null;
        }

        Member member = getMember(email);
        Category category = this.getCategory(dto.getCategoryId());
        Shop shop = this.getShopMemberEmail(member.getEmail());
        Product newProduct = this.dtoToEntity(dto, category, shop);

        newProduct.setApprovalStatus(ProductApproval.N);

        log.info("newProduct: {}", newProduct);

        //업로드 리턴 값 저장
        Product result = productRepository.save(newProduct);

        // 카테고리 처리
        if (dto.getCategoryId() != null) {
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
    public Long updateProduct(String email, ProductDTO.Request dto, Long productId) {
        Member member = getMember(email);
        Product product = this.getEntity(productId);

        //기존의 product 를 dto 로  변환
        ProductDTO.Response oldDTO = this.entityToDTO(product,productLikeRepository.likes(member.getEmail(), product.getId()));

        // 파일 업로드 처리
        //기존 db에 저징된 이미지들
        List<String> oldFileNames = oldDTO.getUploadFileNames();

        //새로 업로드 해야 하는 파일들 dtp 에서 받은 멀티파트
        List<MultipartFile> files = dto.getFiles();

        //s3에 업로드하고 만들어진 새 파일 이름들
        List<String> currentUploadFileNames = fileUtil.uploadS3Files(files);

        //화면에서 변화 없이 계속 유지된 파일들
        List<String> uploadedFileNames = dto.getUploadFileNames();

        //해당 파일을 파일 응답값 리스트에 넣기
//        dto.setUploadFileNames(uploadedFileNames);

        //유지되는 파일들  + 새로 업로드된 파일 이름들이 저장해야 하는 파일 목록이 됨
        if (currentUploadFileNames != null && !currentUploadFileNames.isEmpty()) {
            //s3에 새로 업데이트 한 파일이 있다면 ?
            uploadedFileNames.addAll(currentUploadFileNames);
            //화면 유지 이미지 리스트에 s3 이미지 저장 리턴값 추가
        }

        //기존 파일들 중에서 화면에서 삭제된 파일들을 제거
        if (oldFileNames != null && !oldFileNames.isEmpty()) {

            //지워야 하는 파일 목록 찾기
            //예전 파일들 중에서 지워져야 하는 파일이름들
            List<String> removeFiles = oldFileNames.stream()
                    .filter(fileName -> !uploadedFileNames.contains(fileName)).toList();
            try {
                //실제 파일 삭제
                fileUtil.deleteS3Files(removeFiles);
            } catch (Exception e) {
                log.error("파일 삭제 에러 : {}", e.getMessage());
                throw new RuntimeException("파일 삭제 에러");
            }
        }

        // 상품 수정
        product.changeCategory(this.getCategory(dto.getCategoryId()));
        product.changeName(dto.getName());
        product.changePrice(dto.getPrice());
        product.changeDiscountPrice(dto.getDiscountPrice());
        product.changeDescription(dto.getDescription());
        product.changeStockNumber(dto.getStockNumber());
        product.changeShop(this.getShopMemberEmail(member.getEmail()));
        //연관관계 때문에 넣음

        //기존 엔티티 이미지 삭제
        product.clearImageList();

        List<String> uploadFileNames = dto.getUploadFileNames();
        if (uploadFileNames != null && !uploadFileNames.isEmpty()) {
            uploadFileNames.forEach(product::addImageString);
        }//업로드 이미지들 엔티티 저장

        // 카테고리 처리
        // 만일, 카테고리 변경이 있다면, 카테고리 삭제 -> 등록
        if (!product.getCategory().getId().equals(dto.getCategoryId())) {
            Category category = this.getCategory(dto.getCategoryId());
            categoryBridgeRepository.deleteByProduct(product);
            categoryBridgeRepository.save(CategoryBridge.from(category, product));
        }
        // 만일, 카테고리 변경이 없다면 그대로 둚


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
                // 해당 태그, 해당 상품이 이미 존재하는지 확인
                if (productTagRepository.existsByTagAndProduct(savedTag, product)) {
                    log.info("이미 존재하는 해당태그, 해당 상품이 등록되었습니다. tagName: {}, productName", tagName, product.getName());
                } else {
                    // 존재하지 않는다면 저장
                    productTagRepository.save(ProductTag.from(savedTag, product));
                }
            });
        }

        return product.getId();
    }


    @Override
    public void deleteProductById(Long productId) {
        Product product = this.getEntity(productId);
        // s3 파일 삭제
        List<String> deleteImages = product.getImageList().stream().map(ProductImage::getImageName).collect(Collectors.toList());
        fileUtil.deleteS3Files(deleteImages);

        // 파일 삭제
        product.clearImageList();
        // 해당 참조한 카테고리 삭제
        categoryBridgeRepository.deleteByProduct(product);
        // 해당 참조한 태그 삭제
        productTagRepository.deleteByProduct(product);
        // 상품 delFlag true 로 변경
        productRepository.modifyDeleteFlag(productId);
//row 가 삭제되는게 아니라 deflag 가 바뀐다
    }
    @Transactional(readOnly = true)
    @Override
    public List<ProductDTO.Response> recommendList(ProductDTO.Request request, String email) {
        List<Product> productList = productRepository.findRecommendFirstExtractList(email).stream()
                .filter(product -> product.getApprovalStatus() == ProductApproval.Y) // 승인된 상품만 필터링
                .toList();

        return productList.stream()
                .map(product -> this.entityToDTO(product, productLikeRepository.likes(email, product.getId())))
                .toList();
    }



    @Override
    public void validateProductCount(Long id, Integer count) {
        Product product = this.getEntity(id);
        // 주문상품 count 개수가 상품 재고수량 보다 많으면 예외처리
        if (count > product.getStockNumber()) {
            throw new OutOfStockException("해당 상품은 재고수량 삭제할 수 없습니다. 상품 id: " + id);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Long> getRecentlyChangedProducts(LocalDateTime fromTime) {
        log.info("최근 변경된 상품 조회: {}", fromTime);
        return productRepository.getRecentlyChangedProducts(fromTime);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Long> getRecentlyAddedProducts(LocalDateTime fromTime) {
        log.info("최근 추가된 상품 조회: {}", fromTime);
        return productRepository.getRecentlyAddedProducts(fromTime);
    }


    private Member getMember(String email) {
        return memberRepository.getWithRoles(email).orElseThrow(() -> new EntityNotFoundException("회원 권한이 없습니다" + email));
    }

    private Shop getShopMemberEmail(String email) {
        return shopRepository.findByMemberEmail(email).orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 shop 이 없습니다" + email));
    }


    private Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new EntityNotFoundException("해당 아이디를 가진 카테고리가 없습니다" + categoryId));
    }

}
