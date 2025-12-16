package com.shopmind.framework.service.impl;

import com.shopmind.framework.exception.ShopmindException;
import com.shopmind.framework.id.IdGenerator;
import com.shopmind.framework.model.FileObject;
import com.shopmind.framework.model.FilePart;
import com.shopmind.framework.properties.StorageProperties;
import com.shopmind.framework.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description: RustFS 实现
 * Author: huangcy
 * Date: 2025-12-16
 */
@Slf4j
public class RustFSStorageImpl implements StorageService {

    private final S3Client s3Client;
    private final StorageProperties.RustFS rustFSProperties;
    private final IdGenerator idGenerator;

    public RustFSStorageImpl(S3Client s3Client, StorageProperties storageProperties, IdGenerator idGenerator) {
        this.s3Client = s3Client;
        this.rustFSProperties = storageProperties.getRustfs();
        this.idGenerator = idGenerator;
    }

    @Override
    public String uploadFile(MultipartFile file) {
        try {
            // 检查存储桶是否存在，不存在则创建
            if (!bucketExists(rustFSProperties.getBucketName())) {
                createBucket(rustFSProperties.getBucketName());
            }

            String fileName = generateFileName(file.getOriginalFilename());

            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(rustFSProperties.getBucketName())
                            .key(fileName)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromInputStream(
                            file.getInputStream(),
                            file.getSize()
                    )
            );
            // 构造文件的公共访问URL
            return buildFileUrl(fileName);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new ShopmindException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public byte[] downloadFile(String fileName) {
        try {
            ResponseInputStream<GetObjectResponse> response =
                    s3Client.getObject(
                            GetObjectRequest.builder()
                                    .bucket(rustFSProperties.getBucketName())
                                    .key(fileName)
                                    .build()
                    );
            return response.readAllBytes();
        } catch (Exception e) {
            log.error("文件下载失败", e);
            throw new ShopmindException("文件下载失败: " + e.getMessage());
        }
    }

    @Override
    public void deleteFile(String fileName) {
        try {
            s3Client.deleteObject(
                    DeleteObjectRequest.builder()
                            .bucket(rustFSProperties.getBucketName())
                            .key(fileName)
                            .build()
            );
        } catch (Exception e) {
            log.error("文件删除失败", e);
            throw new ShopmindException("文件删除失败: " + e.getMessage());
        }
    }

    @Override
    public List<FileObject> listFiles() {
        ListObjectsRequest request = ListObjectsRequest.builder()
                .bucket(rustFSProperties.getBucketName())
                .build();
        return getFileObjects(request);
    }


    @Override
    public List<FileObject> listFiles(String prefix) {
        ListObjectsRequest request = ListObjectsRequest.builder()
                .bucket(rustFSProperties.getBucketName())
                .prefix(prefix)
                .build();
        return getFileObjects(request);
    }

    @Override
    public String uploadLargeFile(MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();
            byte[] fileBytes = file.getBytes();

            // 设置每个分片的大小（MB）
            int chunkSize = rustFSProperties.getChunkSize() * 1024 * 1024;

            // 1. 初始化分片上传
            String uploadId = this.initiateMultipartUpload(fileName);

            // 2. 上传所有分片
            List<CompletedPart> completedParts = new ArrayList<>();
            int partNumber = 1;
            int offset = 0;

            while (offset < fileBytes.length) {
                int currentChunkSize = Math.min(chunkSize, fileBytes.length - offset);
                byte[] chunk = new byte[currentChunkSize];
                System.arraycopy(fileBytes, offset, chunk, 0, currentChunkSize);

                CompletedPart completedPart = this.uploadPart(
                        fileName,
                        uploadId,
                        partNumber,
                        new ByteArrayInputStream(chunk),
                        currentChunkSize
                );

                completedParts.add(completedPart);
                offset += currentChunkSize;
                partNumber++;
            }

            // 3. 完成分片上传
            s3Client.completeMultipartUpload(
                    CompleteMultipartUploadRequest.builder()
                            .bucket(rustFSProperties.getBucketName())
                            .key(fileName)
                            .uploadId(uploadId)
                            .multipartUpload(CompletedMultipartUpload.builder()
                                    .parts(completedParts)
                                    .build())
                            .build()
            );
            return buildFileUrl(fileName);
        } catch (Exception e) {
            log.error("上传文件失败：{}", e.getMessage(), e);
            throw new ShopmindException(e.getMessage());
        }
    }

    @Override
    public String initiateMultipartUpload(String fileName) {
        return s3Client.createMultipartUpload(
                CreateMultipartUploadRequest.builder()
                        .bucket(rustFSProperties.getBucketName())
                        .key(fileName)
                        .build()
        ).uploadId();
    }

    @Override
    public CompletedPart uploadPart(String fileName, String uploadId, int partNumber, InputStream inputStream, long size) {
        UploadPartResponse response = s3Client.uploadPart(
                UploadPartRequest.builder()
                        .bucket(rustFSProperties.getBucketName())
                        .key(fileName)
                        .uploadId(uploadId)
                        .partNumber(partNumber)
                        .build(),
                RequestBody.fromInputStream(inputStream, size)
        );

        return CompletedPart.builder()
                .partNumber(partNumber)
                .eTag(response.eTag())
                .build();
    }

    @Override
    public String completeMultipartUpload(String fileName, String uploadId, List<FilePart> parts) {
        List<CompletedPart> completedParts = parts.stream().map(part ->
                CompletedPart
                        .builder()
                        .partNumber(part.getPartNumber())
                        .eTag(part.getETag())
                        .build()
        ).collect(Collectors.toList());
        s3Client.completeMultipartUpload(
                CompleteMultipartUploadRequest.builder()
                        .bucket(rustFSProperties.getBucketName())
                        .key(fileName)
                        .uploadId(uploadId)
                        .multipartUpload(CompletedMultipartUpload.builder()
                                .parts(completedParts)
                                .build())
                        .build()
        );
        return buildFileUrl(fileName);
    }

    @Override
    public void cancelMultipartUpload(String fileName, String uploadId) {
        s3Client.abortMultipartUpload(
                AbortMultipartUploadRequest.builder()
                        .bucket(rustFSProperties.getBucketName())
                        .key(fileName)
                        .uploadId(uploadId)
                        .build()
        );
    }

    /**
     * 检查存储桶是否存在
     */
    private boolean bucketExists(String bucketName) {
        try {
            s3Client.headBucket(
                    HeadBucketRequest.builder()
                            .bucket(bucketName)
                            .build()
            );
            return true;
        } catch (NoSuchBucketException e) {
            return false;
        }
    }

    /**
     * 创建存储桶
     */
    private void createBucket(String bucketName) {
        s3Client.createBucket(
                CreateBucketRequest.builder()
                        .bucket(bucketName)
                        .build()
        );

        // 设置存储桶策略为公开可读
        setBucketPolicy(bucketName);
    }

    /**
     * 设置存储桶策略。下面的策略是一个标准的 公有读策略，谁都可以通过 get 请求访问桶下的资源
     *             {
     *                 "Version": "2012-10-17",  // 使用 IAM 哪个策略语言版本解析这个 policy。 "2012-10-17" 是当前标准语法版本，不要修改
     *                 "Statement": [
     *                     {
     *                         "Effect": "Allow",  // 允许匹配的操作
     *                         "Principal": {"AWS": ["*"]},  // 使用 AWS 身份的任何用户 (谁都可以访问)
     *                         "Action": ["s3:GetObject"],  // 只允许通过  HTTP GET 请求下载对象，不允许删除。 s3:DeleteObject 删除；s3:ListBucket（列出文件）；s3:PutObject（上传）
     *                         "Resource": ["arn:aws:s3:::%s/*"]  // 策略作用的资源范围：arn:aws:s3::: 是标准前缀，%s 会被 bucket name 替换，/* 表示bucket下的所有对象
     *                     }
     *                 ]
     *             }
     */
    private void setBucketPolicy(String bucketName) {
        String policy = """
            {
                "Version": "2012-10-17",
                "Statement": [
                    {
                        "Effect": "Allow",
                        "Principal": {"AWS": ["*"]},
                        "Action": ["s3:GetObject"],
                        "Resource": ["arn:aws:s3:::%s/*"]
                    }
                ]
            }
            """.formatted(bucketName);

        s3Client.putBucketPolicy(
                PutBucketPolicyRequest.builder()
                        .bucket(bucketName)
                        .policy(policy)
                        .build()
        );
    }

    /**
     * 生成唯一文件名
     */
    private String generateFileName(String originalFileName) {
        if (originalFileName == null || originalFileName.trim().isEmpty()) {
            // 如果没有原始文件名，使用默认前缀
            return "file_" + idGenerator.nextIdStr();
        }

        String baseName;
        String extension = "";

        int lastDotIndex = originalFileName.lastIndexOf(".");
        if (lastDotIndex > 0 && lastDotIndex < originalFileName.length() - 1) {
            baseName = originalFileName.substring(0, lastDotIndex);
            extension = originalFileName.substring(lastDotIndex); // 包含 "."
        } else {
            // 没有有效扩展名（如 ".gitignore" 或 "README"）
            baseName = originalFileName;
        }

        // 清理 baseName 中的非法字符（可选但推荐）
        baseName = sanitizeFileName(baseName);

        String uniqueId = idGenerator.nextIdStr();
        return baseName + "_" + uniqueId + extension;
    }


    /**
     * 清理文件名中的非法字符（防止路径遍历、特殊字符等问题）
     */
    private String sanitizeFileName(String fileName) {
        if (fileName == null) {
            return "unnamed";
        }
        // 移除或替换非法字符（根据操作系统限制）, 常见非法字符：\ / : * ? " < > | 空格开头/结尾等
        return fileName
                .replaceAll("[\\\\/:*?\"<>|]", "_")     // 替换 Windows 非法字符
                .replaceAll("^\\s+|\\s+$", "")         // 去首尾空格
                .replaceAll("\\s+", "_")               // 空格转下划线
                .replaceAll("[^\\w._-]", "_")          // 只保留字母、数字、下划线、点、连字符
                .replaceAll("_+", "_");                // 合并多个下划线
    }

    /**
     * 构造文件的公共访问URL
     */
    private String buildFileUrl(String fileName) {
        // 格式：http://{endpoint}/{bucket-name}/{file-name}
        String endpoint = rustFSProperties.getEndpoint();
        String bucketName = rustFSProperties.getBucketName();
        return String.format("%s/%s/%s", endpoint, bucketName, fileName);
    }

    private List<FileObject> getFileObjects(ListObjectsRequest request) {
        ListObjectsResponse listObjectsResponse = s3Client.listObjects(request);
        return listObjectsResponse.contents().stream().map(content -> {
            FileObject fo = new FileObject();
            fo.setFileName(content.key());
            fo.setFileUrl(buildFileUrl(content.key()));
            fo.setSize(formatFileSize(content.size()));
            fo.setLastModifiedTime(LocalDateTime.ofInstant(content.lastModified(), ZoneId.systemDefault()));
            return fo;
        }).collect(Collectors.toList());
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", bytes / (1024.0 * 1024));
        } else {
            return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
        }
    }
}
