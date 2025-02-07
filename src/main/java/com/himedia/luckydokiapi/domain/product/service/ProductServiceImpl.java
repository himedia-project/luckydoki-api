package com.himedia.luckydokiapi.domain.product.service;


import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.member.repository.MemberRepository;
import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.product.dto.ProductRequestDTO;
import com.himedia.luckydokiapi.domain.product.dto.ProductResponseDTO;
import com.himedia.luckydokiapi.domain.product.dto.TagDTO;
import com.himedia.luckydokiapi.domain.product.entity.*;
import com.himedia.luckydokiapi.domain.product.enums.ProductBest;
import com.himedia.luckydokiapi.domain.product.enums.ProductIsNew;
import com.himedia.luckydokiapi.domain.product.repository.CategoryRepository;
import com.himedia.luckydokiapi.domain.product.repository.ProductRepository;
import com.himedia.luckydokiapi.domain.product.repository.ProductTagRepository;
import com.himedia.luckydokiapi.domain.product.repository.TagRepository;
import com.himedia.luckydokiapi.domain.shop.entity.Shop;
import com.himedia.luckydokiapi.domain.shop.repository.ShopRepository;
import com.himedia.luckydokiapi.dto.PageResponseDTO;
import com.himedia.luckydokiapi.util.file.CustomFileUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.himedia.luckydokiapi.util.NumberGenerator.generateRandomNumber;


@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final AdminProductService adminProductService;
    private final ShopRepository shopRepository;
    private final CategoryRepository categoryRepository;
    private final AdminProductServiceImpl adminProductServiceImpl;
    private final CustomFileUtil customFileUtil;
    private final ProductTagRepository productTagRepository;
    private final TagRepository tagRepository;
    private final CustomFileUtil fileUtil;

    @Transactional(readOnly = true)
    @Override
    public ProductResponseDTO getProduct(Long id) {
        Product product = getEntity(id);
        return entityToDTO(product);
    }


    @Override
    public Product getEntity(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 상품이 존재하지 않습니다."));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ProductResponseDTO> list(ProductRequestDTO requestDTO) {
        return productRepository.findByDTO(requestDTO).stream()
                .map(this::entityToDTO).collect(Collectors.toList());
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

    //member 의 본인이 올린  상품 리스트 확인
    @Override
    public List<ProductResponseDTO> getListByMember(String email) {
        Member member = getMember(email);
        return productRepository.findProductByShopMemberEmail(member.getEmail()).stream()
                .map(this::entityToDTO).collect(Collectors.toList());
    }

    //member 의 상품 등록
    @Transactional
    @Override
    public void createProduct(String email, ProductDTO dto) {
        Member member = getMember(email);

        Shop shop = getShopMemberEmail(member.getEmail());

        Category category = getCategory(dto.getCategoryId());
        try {
            List<MultipartFile> files = dto.getFiles();
            List<String> uploadS3FilesNames = fileUtil.uploadS3Files(files);
            log.info("uploadS3FilesNames: {}", uploadS3FilesNames);
            //s3업로드
            dto.setUploadFileNames(uploadS3FilesNames);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
//업로드 리턴 값 저장
        Product newProduct = adminProductServiceImpl.dtoToEntity(dto, category, shop);
//빌더에 보내서 엔티티 저장
        List<String> uploadFileNames = dto.getUploadFileNames();
        if (uploadFileNames == null || uploadFileNames.isEmpty()) {
            return;
        }


        log.info("newProduct: {}", newProduct);
        Product result = productRepository.save(newProduct);
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
    }

    @Override
    public void updateProduct(String email, ProductDTO dto, Long productId) {
        Member member = getMember(email);
        Shop shop = getShopMemberEmail(member.getEmail());
        Product product = getProductById(productId);


        if (product.getId() != dto.getId()) {
            throw new EntityNotFoundException("상품 아이디가 일치 하지 않아 수정할 수 없습니댜");
        }
        ProductDTO oldDTO = adminProductService.entityToDTO(product);
//기존의 dto

// 파일 업로드 처리
        //기존 db에 저징된 이미지들
        List<String> oldFileNames = oldDTO.getUploadFileNames();

        //새로 업로드 해야 하는 파일들 dtp 에서 받은 멀티파트
        List<MultipartFile> files = dto.getFiles();

        List<String> currentUploadFileNames = fileUtil.uploadS3Files(files);
        //s3에 업로드하고 만들어진 새 파일 이름들

        //화면에서 변화 없이 계속 유지된 파일들
        List<String> uploadedFileNames = dto.getUploadFileNames();
        //해당 파일을 파일 응답값 리스트에 넣기

        dto.setUploadFileNames(uploadedFileNames);
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
                log.warn("파일 삭제 에러 : {}", e.getMessage());
            }//파일이 없어도 진행되게
        }


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
                productTagRepository.save(ProductTag.from(savedTag, product));
            });
        }


        product.changeCategory(this.getCategory(dto.getCategoryId()));
        product.changeName(dto.getName());
        product.changePrice(dto.getPrice());
        product.changeDiscountPrice(dto.getDiscountPrice() == null ? 0 : dto.getDiscountPrice());
        product.changeDescription(dto.getDescription());
        product.changeStockNumber(dto.getStockNumber());
        product.changeBest(dto.getBest() == null ? ProductBest.N : dto.getBest());
        product.changeIsNew(dto.getIsNew() == null ? ProductIsNew.N : dto.getIsNew());
        product.changeEvent(dto.getEvent());
        product.changeShop(shop);
        //연관관계 때문에 넣음

        product.clearImageList();
        //기존 엔티티 이미지 삭제

        List<String> uploadFileNames = dto.getUploadFileNames();
        if (uploadFileNames != null && !uploadFileNames.isEmpty()) {
            uploadFileNames.forEach(product::addImageString);
        }//업로드 이미지들 엔티티 저장

    }


    @Override
    public void deleteProductById(Long productId) {
        Product product = getProductById(productId);
        List<String> deleteImages = product.getImageList().stream().map(ProductImage::getImageName).collect(Collectors.toList());
        customFileUtil.deleteS3Files(deleteImages);
        productRepository.modifyDeleteFlag(productId);
//row 가 삭제되는게 아니라 deflag 가 바뀐다
    }

    private Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("해당 아이디를 가진 product 가 없습니다 " + id));
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
