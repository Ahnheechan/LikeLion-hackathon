package com.power.likelion.utils.s3;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.power.likelion.dto.ai_info.ImageResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Component
@Slf4j
public class S3Uploader {
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /** 이미지 업로드시 글 작성을 그만 두면 해당 이미지들을 bucket에서 삭제해야한다.
     *  따라서 이걸 구현하려면 아직 글작성을 하지 않았지만 이미지를 업로드 하려고 한 사람들의 이미지들은
     *  temp 파일에 두었다가 글작성이 완료되면서 글작성에 사용된 temp 파일의 이미지들을 다른 디렉토리에 옮겨주고
     *  temp에서 사용하지 않은 사진들은 모두 삭제되게 해야한다.
     *
     *  하지만 현재는 그냥 글작성 중단시 이미지가 남아있게 되는 문제가 발생하므로 이 부분을 나중에 수정해야 될 거 같다.
     *  */

    public void delete(String fileName,String dirName){
        try {
            String name=fileName.replace("https://ncp-bucket-user.kr.object.ncloudstorage.com/"+dirName+"/","");
            log.info("name: {}" ,name);
            amazonS3Client.deleteObject(new DeleteObjectRequest(bucket+"/"+dirName,name));

        } catch (AmazonS3Exception e) {
            e.printStackTrace();
        } catch(SdkClientException e) {
            e.printStackTrace();
        }
    }


    /** MultipartFile을 전달받아 File로 전환한 후 S3에 업로드  */
    public ImageResDto upload(MultipartFile multipartFile, String dirName) throws IOException,Exception {

        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File 전환 실패"));
        String url=uploads(uploadFile,dirName);
        ImageResDto imageResDto=ImageResDto.builder().imageUrl(url).build();

        log.info("url:{}",imageResDto.getImageUrl());
        return imageResDto;
    }


    private String uploads(File uploadFile, String dirName) {
        String fileName = dirName + "/" + uploadFile.getName();
        String uploadImageUrl = putS3(uploadFile, fileName);


        removeNewFile(uploadFile);  // 로컬에 생성된 File 삭제 (MultipartFile -> File 전환 하며 로컬에 파일 생성됨)


        return uploadImageUrl;      // 업로드된 파일의 S3 URL 주소 반환
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(
                new PutObjectRequest(bucket, fileName, uploadFile)
                        .withCannedAcl(CannedAccessControlList.PublicRead)	// PublicRead 권한으로 업로드 됨
        );
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    private void removeNewFile(File targetFile) {
        if(targetFile.delete()) {
            log.info("파일이 삭제되었습니다.");
        }else {
            log.info("파일이 삭제되지 못했습니다.");
        }
    }
    private Optional<File> convert(MultipartFile file) throws  IOException,Exception {
        String contentType = file.getContentType();
        String originalFileExtension;
        // 확장자명이 존재하지 않을 경우 처리 x
        if (ObjectUtils.isEmpty(contentType)) {
            throw new Exception("파일 확장자 명이 존재하지 않습니다."); //BadRequest
        }
        else {  // 확장자가 jpeg, png인 파일들만 받아서 처리
            if (contentType.contains("image/jpeg"))
                originalFileExtension = ".jpg";
            else if (contentType.contains("image/png"))
                originalFileExtension = ".png";
            else  // 다른 확장자일 경우 처리 x
                throw new Exception("해당 파일 확장자는 지원하지 않습니다.");
        }
        String new_file_name = System.nanoTime() + originalFileExtension;

        File convertFile = new File(new_file_name);
        if(convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }
}
